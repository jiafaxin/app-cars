package com.autohome.app.cars.service.components.uv;

import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.uv.dto.EsSeriesUvItemDto;
import com.autohome.app.cars.service.components.uv.dto.HourUvRedisItemDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EsSeriesUvComponent extends BaseComponent<EsSeriesUvItemDto> {
    @Resource
    @Qualifier("seriesUvEsClient")
    private RestHighLevelClient highLevelClient;

    private static final String SERIES_HOT_RANK_PARAM_NAME = "SERIES_HOT_RANK";
    private static final String SERIES_HOUR_UV_PARAM_NAME = "SERIES_HOUR_UV";
    private static final String SERIES_DAY_UV_PARAM_NAME = "SERIES_DAY_UV";
    private static final String SERIES_TOP100_PARAM_NAME = "SERIES_TOP100";

    private static final int TWO_DAY_SECOND = 60 * 60 * 24 * 2;
    public TreeMap<String, Object> makeParam(String name, String key) {
        return ParamBuilder.create(name, key).build();
    }

    /**
     * 获取某天的车系uv
     */
    public EsSeriesUvItemDto getSeriesDayUv(Date date,int seriesId) {
        String key = String.format("SERIES_DAYUV_%s_%d", DateUtil.format(date, "yyyy-MM-dd"), seriesId);
        return baseGet(makeParam(SERIES_DAY_UV_PARAM_NAME, key));
    }

    public EsSeriesUvItemDto getSeriesDayUvByDateStr(String dateStr,int seriesId) {
        String key = String.format("SERIES_DAYUV_%s_%d", dateStr, seriesId);
        return baseGet(makeParam(SERIES_DAY_UV_PARAM_NAME, key));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        // 组装查询参数
        LocalDateTime now = LocalDateTime.now();
        SearchRequest searchRequest = getRequest(now.minusDays(2), now, "car-series-uv-data-");
        SearchRequest seriesRequest = getRequest(now.minusDays(1), now, "car-series-dayuv-data-");
        // 查询


        // 保存车系
        CompletableFuture<Void> saveSeriesTopRedisFuture = CompletableFuture.runAsync(() -> {
            List<EsSeriesUvItemDto> seriesDataList = querySeries(seriesRequest);
            if (!seriesDataList.isEmpty()) {
                saveSeriesTopRedis(seriesDataList);
            } else {
                log.warn("没有查询到车系UV");
            }
        }).exceptionally(e -> {
            log.error("saveSeriesTopRedis error", e);
            return null;
        });
        CompletableFuture<Void> saveHourUvRedisFuture = CompletableFuture.runAsync(() -> {
            List<EsSeriesUvItemDto> dataList = query(searchRequest);
            // 查询分小时数据
            if (!dataList.isEmpty()) {
                List<HourUvRedisItemDto> hourRedisList = getHourList(dataList);
                hourRedisList.sort(Comparator.comparing(HourUvRedisItemDto::getDate).reversed().thenComparing(HourUvRedisItemDto::getHour));
                if (!hourRedisList.isEmpty()) {
                    // 保存到redis
                    saveHourUvRedis(hourRedisList);
                }
            }
        }).exceptionally(e -> {
            log.error("saveHourUvRedis error", e);
            return null;
        });
        CompletableFuture.allOf(saveSeriesTopRedisFuture, saveHourUvRedisFuture).join();
        xxlLog.accept("从ES查询车系UV成功, 新势力榜缓存更新完成");

    }

    private void saveHourUvRedis(List<HourUvRedisItemDto> hourRedisList) {
        for (HourUvRedisItemDto dto : hourRedisList) {
            String seriesKey = String.format("SERIES_HOTRANK_%s_%d", dto.getDate(), dto.getHour());
            String redisStr = JSON.toJSONString(dto);
//            redisTemplate.opsForValue().set(seriesKey, redisStr, 2, TimeUnit.DAYS);
//            redisTemplate.opsForHash().put("seriesUvHour", seriesKey, redisStr);
            updateRedis(makeParam(SERIES_HOT_RANK_PARAM_NAME, seriesKey), redisStr, true, TWO_DAY_SECOND);
            updateRedis(makeParam(SERIES_HOUR_UV_PARAM_NAME, seriesKey), redisStr, true);
//            Object seriesUvHour = redisTemplate.opsForHash().get("seriesUvHour", seriesKey);
        }


    }


    public HourUvRedisItemDto getHourUvFromRedis(String name, String key) {
        String redisStr = redisTemplate.opsForValue().get(getKey(makeParam(name, key)));
        if (StringUtils.isNotBlank(redisStr)) {
            return JsonUtil.toObject(redisStr, HourUvRedisItemDto.class);
        }
        return null;
    }

    private void saveSeriesTopRedis(List<EsSeriesUvItemDto> dataList) {
        dataList.forEach(i -> {
            String redisStr = JSON.toJSONString(i);
            String key = String.format("SERIES_DAYUV_%s_%d", i.getDate(), i.getSeriesId());
//            redisTemplate.opsForValue().set(key, redisStr, 2, TimeUnit.DAYS);
            updateRedis(makeParam(SERIES_DAY_UV_PARAM_NAME, key), redisStr, true, TWO_DAY_SECOND);
        });
        log.info("### 车系每日UV计算 ##，车系总数：{}", dataList.size());
        //计算每日热车top 100
        dataList = dataList.stream().sorted(Comparator.comparingLong(EsSeriesUvItemDto::getCount).reversed()).limit(100).toList();
        if (!dataList.isEmpty()) {
            EsSeriesUvItemDto uv = dataList.get(0);
            String top100Str = JSON.toJSONString(dataList);
            String key = String.format("SERIES_TOP100_%s", uv.getDate());
//            redisTemplate.opsForValue().set(key, top100Str, 2, TimeUnit.DAYS);
            updateRedis(makeParam(SERIES_TOP100_PARAM_NAME, key), top100Str, true, TWO_DAY_SECOND);
            log.info("### 车系每日UVTOP 100 计算 ##，车系总数：{}", dataList.size());
        }

    }

    /**
     * 获取分小时数据
     *
     * @param dataList es查询结果
     * @return 分小时数据
     */
    private List<HourUvRedisItemDto> getHourList(List<EsSeriesUvItemDto> dataList) {
        List<HourUvRedisItemDto> hourRedisList = new ArrayList<>();
        if (!dataList.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            String today = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String yesterday = now.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            String beforeYesterday = now.minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE);
            Map<Integer, List<EsSeriesUvItemDto>> hourGroupMap = dataList.stream().collect(Collectors.groupingBy(EsSeriesUvItemDto::getHour));
            Map<Integer, List<EsSeriesUvItemDto>> sortHourMap = new LinkedHashMap<>();
            hourGroupMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sortHourMap.put(x.getKey(), x.getValue()));

            //计算今天
            List<String> todayList = Arrays.asList(today, yesterday);
            List<String> yestedayList = Arrays.asList(yesterday, beforeYesterday);
            sortHourMap.forEach((hour, hourList) -> {
                hourRedisList.add(generateHourItem(hourList, hour, todayList));
                if (now.getHour() > 1) {
                    hourRedisList.add(generateHourItem(hourList, hour, yestedayList));
                }
            });

        }
        return hourRedisList;
    }

    private HourUvRedisItemDto generateHourItem(List<EsSeriesUvItemDto> hourList, int hour, List<String> dateList) {
        HourUvRedisItemDto todayHourItem = new HourUvRedisItemDto();
        todayHourItem.setDate(dateList.get(0));
        todayHourItem.setHour(hour);
        Map<Integer, List<EsSeriesUvItemDto>> hourSeriesGroupMap = hourList.stream().collect(Collectors.groupingBy(EsSeriesUvItemDto::getSeriesId));
        List<HourUvRedisItemDto.SeriesUvItem> todaySuvList = new ArrayList<>();
        hourSeriesGroupMap.forEach((seriesId, dayList) -> {
            // 过滤昨天和今天数据
            Map<String, EsSeriesUvItemDto> dataMap = dayList.stream().filter(dayItem -> dateList.contains(dayItem.getDate())).collect(Collectors.toMap(EsSeriesUvItemDto::getDate, x -> x, (oldItem, newItem) -> oldItem));
            // 如果今天数据都有
            if (dataMap.containsKey(dateList.get(0)) && dataMap.containsKey(dateList.get(1))) {
                fillSuvItem(dataMap, dateList, seriesId, todaySuvList);
            }

        });
        todayHourItem.getRanklist().addAll(todaySuvList.stream().sorted(Comparator.comparingLong(HourUvRedisItemDto.SeriesUvItem::getHotv).reversed().thenComparing(HourUvRedisItemDto.SeriesUvItem::getSeriesid)).limit(500).toList());
        return todayHourItem;
    }


    private void fillSuvItem(Map<String, EsSeriesUvItemDto> dataMap, List<String> dateList, int seriesId, List<HourUvRedisItemDto.SeriesUvItem> suvList) {
        String firstDay = dateList.get(0);
        String secondDay = dateList.get(1);
        if (dataMap.containsKey(firstDay) && dataMap.containsKey(secondDay)) {
            long firstDayCount = dataMap.get(firstDay).getCount();
            long secondDayCount = dataMap.get(secondDay).getCount();
            long ads = firstDayCount - secondDayCount;
            if (ads > 10) {
                long hotV = ads * 333L;
                HourUvRedisItemDto.SeriesUvItem suvItem = new HourUvRedisItemDto.SeriesUvItem();
                suvItem.setSeriesid(seriesId);
                suvItem.setHotv(hotV);
                suvList.add(suvItem);
            }
        }
    }


    private SearchSourceBuilder getSearchSourceBuilder(String prefix) {

        AggregationBuilder dtAgg = AggregationBuilders.terms("dt").field("dt");
        AggregationBuilder hourAgg = AggregationBuilders.terms("hour").field("hour").size(10000);
        AggregationBuilder seriesAgg = AggregationBuilders.terms("seriesid").field("seriesid").size(10000);
        ValueCountAggregationBuilder countAgg = AggregationBuilders.count("count").field("deviceid");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        switch (prefix) {
            case "car-series-uv-data-" -> {
                seriesAgg.subAggregation(countAgg);
                dtAgg.subAggregation(hourAgg);
                hourAgg.subAggregation(seriesAgg);
                sourceBuilder.aggregation(dtAgg);
            }
            case "car-series-dayuv-data-" -> {
                seriesAgg.subAggregation(countAgg);
                dtAgg.subAggregation(seriesAgg);
                sourceBuilder.aggregation(dtAgg);
            }
            default -> {

            }
        }
        return sourceBuilder;
    }


    public SearchRequest getRequest(LocalDateTime begin, LocalDateTime end, String prefix) {
        List<String> indices = getEsIndex(begin, end, prefix);
        SearchSourceBuilder sourceBuilder = getSearchSourceBuilder(prefix);
        SearchRequest searchRequest = new SearchRequest(indices.toArray(new String[0]), sourceBuilder);
        log.info("ES查询DSL语句:\nGET  {}\n{}", String.format("/%s/_search", searchRequest.indices()[0]), sourceBuilder);
        return searchRequest;
    }


    public List<EsSeriesUvItemDto> query(SearchRequest searchRequest) {
        List<EsSeriesUvItemDto> dataList = new ArrayList<>();
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (response != null && response.getAggregations() != null && response.getAggregations().get("dt") != null) {
                ParsedStringTerms dtTerms = response.getAggregations().get("dt");
                if (dtTerms != null) {
                    List<? extends Terms.Bucket> buckets = dtTerms.getBuckets();
                    if (buckets != null && !buckets.isEmpty()) {
                        for (Terms.Bucket dtBucket : buckets) {
                            String dt = dtBucket.getKeyAsString();
                            ParsedStringTerms hourTerms = dtBucket.getAggregations().get("hour");
                            if (hourTerms != null && hourTerms.getBuckets() != null) {
                                List<? extends Terms.Bucket> hour_buckets = hourTerms.getBuckets();
                                for (Terms.Bucket hourBucket : hour_buckets) {
                                    String hour = hourBucket.getKeyAsString();
                                    ParsedStringTerms seriesTerms = hourBucket.getAggregations().get("seriesid");
                                    if (seriesTerms != null && seriesTerms.getBuckets() != null) {
                                        List<? extends Terms.Bucket> seriesBuckets = seriesTerms.getBuckets();
                                        for (Terms.Bucket seriesBucket : seriesBuckets) {
                                            String seriesId = seriesBucket.getKeyAsString();
                                            long count = ((ParsedValueCount) seriesBucket.getAggregations().get("count")).getValue();
                                            EsSeriesUvItemDto uvItem = new EsSeriesUvItemDto();
                                            uvItem.setDate(dt);
                                            uvItem.setHour(Integer.parseInt(hour));
                                            uvItem.setDateHour(dt + "_" + hour);
                                            uvItem.setSeriesId(Integer.parseInt(seriesId));
                                            uvItem.setCount(count);
                                            dataList.add(uvItem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("EsSeriesUvComponent.refreshAll error", e);
        }
        return dataList;
    }

    public List<EsSeriesUvItemDto> querySeries(SearchRequest searchRequest) {
        List<EsSeriesUvItemDto> dataList = new ArrayList<>();
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            getDateList(dataList, response);
        } catch (Exception e) {
            log.error("EsSeriesUvComponent.refreshAll error", e);
        }
        return dataList;
    }

    private void getDateList(List<EsSeriesUvItemDto> dataList, SearchResponse response) {
        if (response != null && response.getAggregations() != null && response.getAggregations().get("dt") != null) {
            ParsedStringTerms dtTerms = response.getAggregations().get("dt");
            if (dtTerms != null) {
                List<? extends Terms.Bucket> buckets = dtTerms.getBuckets();
                if (buckets != null && !buckets.isEmpty()) {
                    for (Terms.Bucket dtBucket : buckets) {
                        String dt = dtBucket.getKeyAsString();
                        ParsedStringTerms seriesTerms = dtBucket.getAggregations().get("seriesid");
                        if (seriesTerms != null && seriesTerms.getBuckets() != null) {
                            List<? extends Terms.Bucket> seriesBuckets = seriesTerms.getBuckets();
                            for (Terms.Bucket seriesBucket : seriesBuckets) {
                                String seriesId = seriesBucket.getKeyAsString();
                                long count = ((ParsedValueCount) seriesBucket.getAggregations().get("count")).getValue();
                                EsSeriesUvItemDto uvItem = new EsSeriesUvItemDto();
                                uvItem.setDate(dt);
                                uvItem.setSeriesId(Integer.parseInt(seriesId));
                                uvItem.setCount(count);
                                dataList.add(uvItem);
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<String> getEsIndex(LocalDateTime begin, LocalDateTime end, String prefix) {
        List<LocalDateTime> dateList = rangeToList(begin, end, ChronoUnit.DAYS);
        List<String> indexList = new ArrayList<>();
        for (LocalDateTime time : dateList) {
            String indexStr = prefix + time.format(DateTimeFormatter.ISO_LOCAL_DATE);
            indexList.add(indexStr);
        }
        return indexList;
    }

    public static List<LocalDateTime> rangeToList(LocalDateTime begin, LocalDateTime end, ChronoUnit unit) {
        List<LocalDateTime> dateList = new ArrayList<>();
        LocalDateTime beginDate = LocalDateTime.from(begin);
        while (!beginDate.isAfter(end)) {
            dateList.add(beginDate);
            beginDate = beginDate.plus(1, unit);
        }
        return dateList;
    }


    public String syncSeriesLastDayUvTask() {
        LocalDateTime now = LocalDateTime.now();
        List<String> indices = getEsIndex(now.minusDays(1), now, "car-series-dayuv-data-");
        AggregationBuilder dtAgg = AggregationBuilders.terms("dt").field("dt");
        AggregationBuilder seriesAgg = AggregationBuilders.terms("seriesid").field("seriesid").size(10000);
        seriesAgg.subAggregation(AggregationBuilders.count("count").field("deviceid"));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        dtAgg.subAggregation(seriesAgg);
        sourceBuilder.aggregation(dtAgg);
        SearchRequest searchRequest = new SearchRequest(indices.toArray(new String[0]), sourceBuilder);
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<EsSeriesUvItemDto> dataList = new ArrayList<>();
            if (response != null && response.getAggregations() != null && response.getAggregations().get("dt") != null) {
                ParsedStringTerms dt_Terms = response.getAggregations().get("dt");
                if (dt_Terms != null) {
                    List<? extends Terms.Bucket> buckets = dt_Terms.getBuckets();
                    if (buckets != null && !buckets.isEmpty()) {
                        for (Terms.Bucket dt_bucket : buckets) {
                            String dt = dt_bucket.getKeyAsString();
                            ParsedStringTerms series_Terms = dt_bucket.getAggregations().get("seriesid");
                            if (series_Terms != null && series_Terms.getBuckets() != null) {
                                List<? extends Terms.Bucket> series_buckets = series_Terms.getBuckets();
                                for (Terms.Bucket series_bucket : series_buckets) {
                                    String seriesid = series_bucket.getKeyAsString();
                                    long count = ((ParsedValueCount) series_bucket.getAggregations().get("count")).getValue();
                                    EsSeriesUvItemDto uvItem = new EsSeriesUvItemDto();
                                    uvItem.setDate(dt);
                                    uvItem.setSeriesId(Integer.parseInt(seriesid));
                                    uvItem.setCount(count);
                                    dataList.add(uvItem);
                                }
                            }

                            //errorDataMap.put(bucket.getKeyAsString(),bucket.getDocCount());
                        }
                    }
                }
            }

            dataList.forEach(i -> {
                String redisStr = JSON.toJSONString(i);
                String key = String.format("SERIES_DAYUV_%s_%s", i.getDate(), i.getSeriesId());
                redisTemplate.opsForValue().set(key, redisStr, 2, TimeUnit.DAYS);

            });
            log.info("### 车系昨日UV计算 ##，车系总数：{}", dataList.size());

        } catch (Exception e) {
            log.error("车系昨日UV计算 fail", e);
        }
        return null;

    }
}
