package com.autohome.app.cars.service.components.subsidy;

import com.autohome.app.cars.common.utils.EsPageUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.subsidy.dtos.SeriesCitySubsidyDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecCitySubsidyDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/4/25
 */
@Component
@DBConfig(tableName = "spec_city_subsidy")
@Slf4j
public class SpecCitySubsidyComponent extends BaseComponent<SpecCitySubsidyDto> {

    @SuppressWarnings("all")
    @Autowired
    @Qualifier("appesEsClient")
    private RestHighLevelClient highLevelClient;

    final static String specIdIdParamName = "specId";
    final static String cityParamName = "cityId";

    private static final String UID = "_id";

    @Value("${spring.elasticsearch.indexname:dealer-app-baojia-city_spec_benefits}")
    String indexName;

    private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
            5,
            5,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10),// 阻塞队列
            new ThreadPoolExecutor.CallerRunsPolicy());  // 控制任务提交速率


    TreeMap<String, Object> makeParam(int specId, int cityId) {
        return ParamBuilder.create(specIdIdParamName, specId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SpecCitySubsidyDto> get(int specId, int cityId) {
        return baseGetAsync(makeParam(specId, cityId));
    }

    public CompletableFuture<List<SpecCitySubsidyDto>> getList(List<Integer> specIdList, Integer cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SpecCitySubsidyDto> dtoList = Collections.synchronizedList(new ArrayList<>());
        specIdList.forEach(specId -> tasks.add(get(specId, cityId).thenApply(dtoList::add)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return CompletableFuture.completedFuture(dtoList);
    }

    public void refreshAll(Consumer<String> xxlLog) {

        ConcurrentMap<String, Integer> seriesCityDatas = new ConcurrentHashMap<>();

        // 城市id设置为0，用于不计城市查询全量车型的厂商补贴信息
        ConcurrentMap<TreeMap<String, Object>, SpecCitySubsidyDto> specDatas = new ConcurrentHashMap<>();

        // 本次新计算得到的key，用于删除历史数据
        ConcurrentHashSet<String> newKeys = new ConcurrentHashSet<>();

        // 根据索引创建查询请求
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 需要同步所有benefit_sum不为空的数据
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<QueryBuilder> must = boolQueryBuilder.must();
        must.add(QueryBuilders.boolQuery().must((QueryBuilders.existsQuery("benefit_sum"))));
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
//        System.out.println("searchSourceBuilder: " + searchSourceBuilder);

        AtomicInteger count = new AtomicInteger(0);
        AtomicBoolean exFlag = new AtomicBoolean(false);
        try {
            EsPageUtil.searchForHit(highLevelClient, UID, 500, searchRequest, sourceDataArray ->
                    CompletableFuture.runAsync(() -> {
                        Map<TreeMap<String, Object>, SpecCitySubsidyDto> datas = new HashMap<>();
                        Arrays.stream(sourceDataArray)
                                .map(e -> JsonUtil.toObject(e.getSourceAsString(), SpecCitySubsidyDto.class))
                                .filter(e -> Objects.nonNull(e.getBenefit_sum()))
                                .forEach(e -> {
                                    datas.put(makeParam(e.getSpec_id(), e.getCity_id()), e);
                                    updateBatch(datas);
                                    newKeys.addAll(datas.keySet().stream().map(this::getKey).collect(Collectors.toSet()));
                                    datas.clear();
                                    createSeriesData(seriesCityDatas, e);
                                    handleSpecDatas(specDatas, e);
                                });
                        count.addAndGet(sourceDataArray.length);
                        if (count.get() % 10000 == 0) {
                            xxlLog.accept("now:" + count.get());
                        }
                    }, threadPoolExecutor).exceptionally(e -> {
                        exFlag.set(true);
                        xxlLog.accept("exception:" + e);
                        return null;
                    }));
        } catch (Exception e) {
            exFlag.set(true);
            log.error(e.getMessage(), e);
        }
        // 处理车系数据
        saveSeries(seriesCityDatas);
        xxlLog.accept("now saveSeries done:" + seriesCityDatas.size());
        // 处理 车型+城市Id为0 数据
        updateBatch(specDatas);
        newKeys.addAll(specDatas.keySet().stream().map(this::getKey).collect(Collectors.toSet()));
        xxlLog.accept("now updateSpecCity0Datas done:" + specDatas.size());
        // 删除历史无效数据
        if(!exFlag.get()) {
            log.error("SpecCitySubsidyComponent 数据同步任务出现异常，不执行历史数据删除任务");
            deleteHistorys(new HashSet<>(newKeys), xxlLog);
        }
    }

    public void deleteHistorys(HashSet<String> newKeys, Consumer<String> xxlLog) {
        deleteHistory(newKeys, xxlLog);
    }

    /**
     * 城市id设置为0，用于不计城市查询全量车型的厂商补贴信息
     */
    private void handleSpecDatas(Map<TreeMap<String, Object>, SpecCitySubsidyDto> specDatas,
                                 SpecCitySubsidyDto specCitySubsidyDto) {
        if (specDatas.get(makeParam(specCitySubsidyDto.getSpec_id(), 0)) != null) {
            return;
        }
        if (!CollectionUtils.isEmpty(specCitySubsidyDto.getFactory_benefits())) {
            SpecCitySubsidyDto specCityFactorySubsidy = new SpecCitySubsidyDto();
            specCityFactorySubsidy.setSpec_id(specCitySubsidyDto.getSpec_id());
            specCityFactorySubsidy.setCity_id(0);
            specCityFactorySubsidy.setBrand_id(specCitySubsidyDto.getBrand_id());
            specCityFactorySubsidy.setChannel_id(specCitySubsidyDto.getChannel_id());
            specCityFactorySubsidy.setSeries_id(specCitySubsidyDto.getSeries_id());
            specCityFactorySubsidy.setFactory_benefits(specCitySubsidyDto.getFactory_benefits());
            specCityFactorySubsidy.setBenefit_sum(specCitySubsidyDto.getBenefit_sum());
            specDatas.put(makeParam(specCitySubsidyDto.getSpec_id(), 0), specCityFactorySubsidy);
        }
    }


    //处理车系逻辑
    synchronized void createSeriesData(ConcurrentMap<String, Integer> seriesCityDatas, SpecCitySubsidyDto specData) {
        int seriesId = specData.getSeries_id();
        int cityId = specData.getCity_id();
        if (specData.getBenefit_sum() == null) {
            return;
        }
        int price = getSeriesPrice(specData);
        if (price > 0) {
            String key = cityId + "_" + seriesId;
            seriesCityDatas.compute(key, (ok, ov) -> ov == null || ov < price ? price : ov);
        }
    }

    int getSeriesPrice(SpecCitySubsidyDto specData) {
        SpecCitySubsidyDto.BenefitSum b = specData.getBenefit_sum();
        int price = 0;
        if (b.getReplace_this_brand_sum() != null || b.getReplace_other_brand_sum() != null || b.getReplace_all_brand_sum() != null) {
            price = b.getReplace_this_brand_sum() != null && b.getReplace_this_brand_sum().getSum() > price ? b.getReplace_this_brand_sum().getSum() : price;
            price = b.getReplace_other_brand_sum() != null && b.getReplace_other_brand_sum().getSum() > price ? b.getReplace_other_brand_sum().getSum() : price;
            price = b.getReplace_all_brand_sum() != null && b.getReplace_all_brand_sum().getSum() > price ? b.getReplace_all_brand_sum().getSum() : price;
        } else if (b.getAdd_all_brand_sum() != null || b.getAdd_other_brand_sum() != null || b.getAdd_all_brand_sum() != null) {
            price = b.getAdd_all_brand_sum() != null && b.getAdd_all_brand_sum().getSum() > price ? b.getAdd_all_brand_sum().getSum() : price;
            price = b.getAdd_other_brand_sum() != null && b.getAdd_other_brand_sum().getSum() > price ? b.getAdd_other_brand_sum().getSum() : price;
            price = b.getAdd_all_brand_sum() != null && b.getAdd_all_brand_sum().getSum() > price ? b.getAdd_all_brand_sum().getSum() : price;
        } else if (b.getNew_car_sum() != null) {
            price = b.getNew_car_sum().getSum();
        }
        return price;
    }

    private TreeMap<String, Object> makeSeriesKey(int seriesId, int cityId) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("seriesId", seriesId);
        params.put("cityId", cityId);
        return params;
    }

    private void saveSeries(ConcurrentMap<String, Integer> seriesCityDatas) {
        seriesCityDatas.forEach((k, v) -> {
            try {
                String[] ids = k.split("_");
                SeriesCitySubsidyDto dto = new SeriesCitySubsidyDto();
                dto.setSeriesId(Integer.parseInt(ids[1]));
                dto.setCityId(Integer.parseInt(ids[0]));
                dto.setPrice(v);
                updateRedis(makeSeriesKey(dto.getSeriesId(), dto.getCityId()), JsonUtil.toString(dto), true, 93600);
            } catch (Exception e) {
                log.error("保存redis 报错");
            }
        });
    }

    public CompletableFuture<SeriesCitySubsidyDto> getSeriesCityData(int seriesId, int cityId) {
        String json = redisTemplate.opsForValue().get(getKey(makeSeriesKey(seriesId, cityId)));
        if (StringUtils.isBlank(json))
            return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> JsonUtil.toObject(json, SeriesCitySubsidyDto.class), ThreadPoolUtils.defaultThreadPoolExecutor);
    }
    
}
