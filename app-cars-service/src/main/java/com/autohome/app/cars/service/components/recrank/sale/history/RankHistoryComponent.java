package com.autohome.app.cars.service.components.recrank.sale.history;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.rank.RankHistoryClient;
import com.autohome.app.cars.apiclient.rank.dtos.SeriesRankHistoryResult;
import com.autohome.app.cars.common.enums.RankRedisKeyEnum;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.RankSaleMonthMapper;
import com.autohome.app.cars.mapper.appcars.RankSaleRefreshVersionMapper;
import com.autohome.app.cars.mapper.appcars.RankSaleWeekMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleRefreshVersionEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankHistoryResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankHistorySaleCountItemDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleWeekComponent;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RankHistoryComponent extends BaseComponent<RankHistoryResultDto> {

    @Resource
    private RankSaleMonthMapper rankSaleMonthMapper;

    @Resource
    private RankSaleWeekMapper rankSaleWeekMapper;

    @Resource
    private RankHistoryClient rankHistoryClient;

    @Resource
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private SeriesSaleWeekRankByEnergyTypeComponent seriesSaleWeekRankByEnergyTypeComponent;

    @Autowired
    private SeriesSaleMonthRankByEnergyTypeComponent seriesSaleMonthRankByEnergyTypeComponent;

    @Autowired
    private RankSaleRefreshVersionMapper rankSaleRefreshVersionMapper;

    @Autowired
    private RankSaleMonthComponent rankSaleMonthComponent;

    @Autowired
    private RankSaleWeekComponent rankSaleWeekComponent;

    //销量月榜、周榜更新版本缓存key
    static final String MonthAndWeekRankRefresh_RedisKey = "rank_sale_refresh_version";

    static final String typeIdName = "typeId";
    static final String subRankTypeIdName = "subRankTypeId";
    static final String seriesIdName = "seriesId";
    static final String energyTypeName = "energyType";
    static final String dateName = "date";
    static final String pluginversionName = "pluginversion";
    static final String vName = "v";
    static final String redisVersion = "v5.6";

    TreeMap<String, Object> makeParam(int typeId, int subRankTypeId, int seriesId, int energyType, String date, String pluginversion, int redisVersion) {
        return ParamBuilder
                .create(typeIdName, typeId)
                .add(subRankTypeIdName, subRankTypeId)
                .add(seriesIdName, seriesId)
                .add(energyTypeName, energyType)
                .add(dateName, date)
                .add(pluginversionName, pluginversion)
                .add(vName, redisVersion)
                .build();
    }

    public String getHistory(TreeMap<String, Object> param) {
        RankHistoryResultDto rankHistoryResultDto = get((Integer) param.get(typeIdName), (Integer) param.get(subRankTypeIdName), (Integer) param.get(seriesIdName), (Integer) param.get(energyTypeName), param.get(dateName).toString(), param.containsKey("pluginversion") ? param.get("pluginversion").toString() : "11.64.5");
        if (Objects.nonNull(rankHistoryResultDto)) {
            return JsonUtil.toString(rankHistoryResultDto);
        }
        return JsonUtil.toString(new RankHistoryResultDto());
    }

    public RankHistoryResultDto get(int typeId, int subRankTypeId, int seriesId, int energyType, String date, String pluginversion) {
        int v = getMonthAndWeekRankRefreshVesion();//刷新版本
        TreeMap<String, Object> makeParam = makeParam(typeId, subRankTypeId, seriesId, energyType, date, pluginversion, v);
        RankHistoryResultDto result = null;
        if (v > 0) {
            result = getFromRedis(makeParam);
        }
        if (Objects.isNull(result)) {
            result = getRankHistory(typeId, subRankTypeId, seriesId, energyType, pluginversion);
            if (v > 0) {
                updateRedis(makeParam, JsonUtil.toString(result), true);
            }
        }
        return result;
    }

    @Override
    public RankHistoryResultDto getFromRedis(TreeMap<String, Object> params) {
        String json = redisTemplate.opsForValue().get(getKey(params));
        if (StringUtils.hasLength(json)) {
            return JSONObject.parseObject(json, RankHistoryResultDto.class);
        }
        return null;
    }


    public RankHistoryResultDto getRankHistory(int typeId, int subRankTypeId, int seriesId, int energyType, String pluginversion) {
        RankHistoryResultDto result = new RankHistoryResultDto();
        switch (typeId) {
            // 销量榜
            case 1 -> {
                switch (subRankTypeId) {
                    // 月榜销量趋势
                    case 1 -> result.setResult(getMonthHistory(seriesId, energyType, pluginversion));
                    // 周榜销量趋势
                    case 2 -> result.setResult(getWeekHistory(seriesId, energyType, pluginversion));
                }
            }
            case 9 -> {
                switch (subRankTypeId) {
                    // 新能源-月榜销量趋势
                    case 2305 -> result.setResult(getMonthHistory(seriesId, energyType, pluginversion));
                    // 新能源-周榜销量趋势
                    case 2306 -> result.setResult(getWeekHistory(seriesId, energyType, pluginversion));
                }
            }
        }
        return result;
    }

    public RankHistoryResultDto.SaleSeriesRankHistoryResult getMonthHistory(int seriesId, int energyType, String pluginversion) {
        RankHistoryResultDto.SaleSeriesRankHistoryResult resultDto = new RankHistoryResultDto.SaleSeriesRankHistoryResult();
        resultDto.setSeriesid(seriesId);
        List<RankHistorySaleCountItemDto> dataList = new ArrayList<>();
        if (energyType <= 1 || energyType == 456) {
            CompletableFuture<SeriesRankHistoryResult> future = rankHistoryClient.getRankHistory(seriesId);
            SeriesRankHistoryResult seriesRankHistoryResult = future.join();
            if (seriesRankHistoryResult == null) {
                return resultDto;
            }
            LocalDate beginData = LocalDate.of(2021, 10, 1);
            List<SeriesRankHistoryResult.ItemDto> data = seriesRankHistoryResult.getResult().getData().stream().filter(x -> x.getSalecnt() > 0 && LocalDate.parse(x.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER).isBefore(beginData)).toList();
            if (!data.isEmpty()) {
                dataList.addAll(data.stream().map(x -> RankHistorySaleCountItemDto.getInstance(0, LocalDate.parse(x.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER), x.getSalecnt())).toList());
            }

        }
        String energyTypeStr = "0";
        if (energyType != 0) {
            String[] split = String.valueOf(energyType).split("");
            energyTypeStr = String.join(",", split);
        }
        List<RankSaleMonthSourceEntity> historyList = rankSaleMonthMapper.getSeriesSaleHistory(seriesId, energyTypeStr);
        if (historyList != null && !historyList.isEmpty()) {
            dataList.addAll(historyList.stream().map(x -> RankHistorySaleCountItemDto.getInstance(1, LocalDate.parse(x.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER), x.getSalecnt())).toList());
        }
        if (!dataList.isEmpty()) {
            dataList.sort(Comparator.comparing(RankHistorySaleCountItemDto::getMonth));
            boolean isInVersion = CommonHelper.isTakeEffectVersion(pluginversion, "11.64.5");
            boolean isHideRankTextVersion = CommonHelper.isTakeEffectVersion(pluginversion, "11.65.0");
            // 中汽协文案标识
            boolean zqxFlag = true;
            // 乘联会文案标识
            boolean clhFlag = true;
            int maxSaleCount = -1;
            // 倒序dataList
            Collections.reverse(dataList);
            //获取排名
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            Map<String, Integer> ranknumMap = new HashMap<>();
            //乘联会数据
            List<RankHistorySaleCountItemDto> dataCLH = new ArrayList<>();
            if (isInVersion) {
                dataCLH = dataList.stream().filter(p -> p.getType() == 1).collect(Collectors.toList());
                List<String> monthList = dataCLH.stream().map(RankHistorySaleCountItemDto::getMonth).map(month -> month.format(formatter)).collect(Collectors.toList());
                Map<String, Integer> ranknumMapTemp = new HashMap<>();
                Lists.partition(monthList, 100).forEach(sublist -> {
                    ranknumMapTemp.putAll(seriesSaleMonthRankByEnergyTypeComponent.getRankByMonthList(sublist, seriesId, energyType));
                });
                ranknumMap.putAll(ranknumMapTemp);
            }
            for (RankHistorySaleCountItemDto dto : dataList) {
                LocalDate month = dto.getMonth();
                String date = String.format("%d年%02d月", month.getYear(), month.getMonthValue());
                maxSaleCount = Math.max(maxSaleCount, dto.getSaleCount());
                // 添加中汽协标识文案
                if (dto.getType() == 0 && zqxFlag) {
                    String zqxTitle = isInVersion && !isHideRankTextVersion ? "以下为中汽协批发量数据，暂无排名" : "以下为中汽协批发量数据";
                    resultDto.getMonthsale().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem.getInstance(2, zqxTitle, "", (isInVersion ? "-" : null)));
                    zqxFlag = false;
                }
                // 添加乘联会标识文案
                if (dto.getType() == 1 && clhFlag) {
                    resultDto.getMonthsale().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem.getInstance(2, "以下为行业销量综合数据", "", (isInVersion ? "-" : null)));
                    clhFlag = false;
                }
                String ranktext = isInVersion ? "-" : null;
                Integer rankNum = ranknumMap.get(month.format(formatter));
                if (rankNum != null && !rankNum.equals(-1)) {
                    ranktext = "第" + rankNum + "名";
                }
                int type = isHideRankTextVersion && dto.getType() == 0 ? 3 : 1;
                resultDto.getMonthsale().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem.getInstance(type, date, dto.getSaleCount() + "辆", ranktext));
                resultDto.getMonth().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthItem.getInstance(month.getMonthValue() + "月", date));
                resultDto.getSalecount().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.getInstance(dto.getSaleCount(), null));
            }
            if (isInVersion && dataCLH.size() > 0)
                getMonthSaleCountInfo(resultDto, dataCLH);

            Collections.reverse(resultDto.getMonth());
            Collections.reverse(resultDto.getSalecount());
            resultDto.setMax(rankCommonComponent.getSaleHistoryMax(maxSaleCount));
        }

        resultDto.setChartcolnum(6);
        return resultDto;
    }


    public RankHistoryResultDto.SaleSeriesRankHistoryResult getWeekHistory(int seriesId, int energyType, String pluginversion) {
        RankHistoryResultDto.SaleSeriesRankHistoryResult resultDto = new RankHistoryResultDto.SaleSeriesRankHistoryResult();
        resultDto.setSeriesid(seriesId);
        List<RankHistorySaleCountItemDto> dataList = new ArrayList<>();
        String energyTypeStr = "0";
        if (energyType != 0) {
            String[] split = String.valueOf(energyType).split("");
            energyTypeStr = String.join(",", split);
        }
        List<RankSaleWeekSourceEntity> historyList = rankSaleWeekMapper.getSeriesSaleHistory(seriesId, energyTypeStr);
        if (historyList != null && !historyList.isEmpty()) {
            dataList = historyList.stream().map(x -> RankHistorySaleCountItemDto.getInstance(0, LocalDate.parse(x.getWeek_day(), RankConstant.LOCAL_WEEK_FORMATTER), x.getSalecnt().intValue())).toList();
        }
        if (!dataList.isEmpty()) {
            int maxSaleCount = -1;
            boolean isInVersion = CommonHelper.isTakeEffectVersion(pluginversion, "11.64.5");
            // 倒序dataList
            resultDto.getMonthsale().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem.getInstance(2, "终端销量及之家大数据整理", "", (isInVersion ? "-" : null)));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            Map<String, Integer> ranknumMap = new HashMap<>();
            if (isInVersion) {
                List<String> weekDayList = dataList.stream().map(RankHistorySaleCountItemDto::getMonth).map(month -> month.format(formatter)).collect(Collectors.toList());
                ranknumMap = seriesSaleWeekRankByEnergyTypeComponent.getRankByWeekDayList(weekDayList, seriesId, energyType);
            }
            for (RankHistorySaleCountItemDto dto : dataList) {
                LocalDate week = dto.getMonth();
                String weekBegin_month = week.minusDays(1).format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER);
                String weekBegin_Year = week.minusDays(1).format(formatter1);
                String weekEnd = week.plusDays(5).format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER);
                String date_year = weekBegin_Year + "-" + weekEnd;
                String date_month = weekBegin_month + "-" + weekEnd;
                String ranktext = isInVersion ? "-" : null;
                Integer rankNum = ranknumMap.get(week.format(formatter));
                if (rankNum != null && !rankNum.equals(-1)) {
                    ranktext = "第" + rankNum + "名";
                }
                maxSaleCount = Math.max(maxSaleCount, dto.getSaleCount());
                resultDto.getMonthsale().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem.getInstance(1, isInVersion ? date_year : date_month, dto.getSaleCount() + "辆", ranktext));
                resultDto.getMonth().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthItem.getInstance(date_month, date_month));
                resultDto.getSalecount().add(RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.getInstance(dto.getSaleCount(), null));
            }
            //处理较上周数据
            if (isInVersion)
                getWeekSaleCountInfo(resultDto);

            Collections.reverse(resultDto.getSalecount());
            Collections.reverse(resultDto.getMonth());
            resultDto.setMax(maxSaleCount);
        }
        resultDto.setChartcolnum(7);
        return resultDto;
    }

    /**
     * 较上周数据处理
     *
     * @param resultDto 返回result对象
     */
    private void getWeekSaleCountInfo(RankHistoryResultDto.SaleSeriesRankHistoryResult resultDto) {
        try {
            if (resultDto.getSalecount() != null && resultDto.getSalecount().size() > 0) {
                for (int i = 0; i < resultDto.getSalecount().size(); i++) {
                    if (i == resultDto.getSalecount().size() - 1) {
                        //当前是最后一个元素或者总共只有一条数据怎么处理？
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info info = new RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info();
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem current = resultDto.getSalecount().get(i);
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem monthSaleItem = resultDto.getMonthsale().get(i + 1);
                        info.setTitle(monthSaleItem.getName() + "·终端销量");
                        info.setText("较上周");
                        info.setCount(current.getCount());
                        info.setComparenum(0);
                        info.setComparetype(-1);
                        String rank = monthSaleItem.getRanktext();
                        if (!Strings.isNullOrEmpty(rank))
                            info.setRank(rank.replace("第", "").replace("名", ""));
                        else
                            info.setRank("-");
                        current.setInfo(info);
                    } else {
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info info = new RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info();
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem current = resultDto.getSalecount().get(i);
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem next = resultDto.getSalecount().get(i + 1);
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.MonthSaleItem monthSaleItem = resultDto.getMonthsale().get(i + 1);
                        int saleDiffCount = current.getCount() - next.getCount();
                        info.setTitle(monthSaleItem.getName() + "·终端销量");
                        info.setText("较上周");
                        info.setCount(current.getCount());
                        info.setComparenum(0);
                        // 上升1，下降-1，持平0
                        if (saleDiffCount > 0) {
                            info.setComparetype(1);
                        } else if (saleDiffCount == 0) {
                            info.setComparetype(-1);//与上周持平默认下降0
                        } else {
                            info.setComparetype(-1);
                        }
                        if (saleDiffCount != 0)
                            info.setComparenum(Math.abs(saleDiffCount));
                        String rank = monthSaleItem.getRanktext();
                        if (!Strings.isNullOrEmpty(rank))
                            info.setRank(rank.replace("第", "").replace("名", ""));
                        else
                            info.setRank("-");
                        current.setInfo(info);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("处理较上周数据异常-ex:{}", ex);
        }
    }


    /**
     * 较上月数据处理
     *
     * @param resultDto 返回result
     * @param list      乘联会数据集合
     */
    private void getMonthSaleCountInfo(RankHistoryResultDto.SaleSeriesRankHistoryResult resultDto, List<RankHistorySaleCountItemDto> list) {
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == list.size() - 1) {
                        //当前是最后一个元素或者总共只有一条数据怎么处理
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info info = new RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info();
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem current = resultDto.getSalecount().get(i);
                        info.setTitle(resultDto.getMonth().get(i).getSubname() + "·零售销量");
                        info.setText("较上月");
                        info.setCount(current.getCount());
                        info.setComparenum(0);
                        info.setComparetype(-1);
                        String rank = resultDto.getMonthsale().get(i + 1).getRanktext();
                        if (!Strings.isNullOrEmpty(rank))
                            info.setRank(rank.replace("第", "").replace("名", ""));
                        else
                            info.setRank("-");
                        current.setInfo(info);
                        break;
                    } else {
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info info = new RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem.Info();
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem current = resultDto.getSalecount().get(i);
                        RankHistoryResultDto.SaleSeriesRankHistoryResult.SaleCountItem next = resultDto.getSalecount().get(i + 1);
                        int saleDiffCount = current.getCount() - next.getCount();
                        info.setTitle(resultDto.getMonth().get(i).getSubname() + "·零售销量");
                        info.setText("较上月");
                        info.setCount(current.getCount());
                        info.setComparenum(0);
                        // 上升1，下降-1，持平0
                        if (saleDiffCount > 0) {
                            info.setComparetype(1);
                        } else if (saleDiffCount == 0) {
                            info.setComparetype(-1);//与上月持平默认下降0
                        } else {
                            info.setComparetype(-1);
                        }
                        if (saleDiffCount != 0)
                            info.setComparenum(Math.abs(saleDiffCount));
                        String rank = resultDto.getMonthsale().get(i + 1).getRanktext();
                        if (!Strings.isNullOrEmpty(rank))
                            info.setRank(rank.replace("第", "").replace("名", ""));
                        else
                            info.setRank("-");
                        current.setInfo(info);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("处理较上月数据异常-ex:{}", ex);
        }
    }

    /**
     * 月榜、周榜销量趋势数据刷新
     */
    public boolean refreshMonthAndWeekRank(int operatetype, String month, String week) {
        try {
            boolean isSuccess = false;
            //刷新月销量列表和趋势
            if (operatetype == 1 && !Strings.isNullOrEmpty(month)) {
                isSuccess = seriesSaleMonthRankByEnergyTypeComponent.modifyRefreshByMonth(month);
                if (isSuccess){
                    //删除月榜列表当前月缓存
                    rankSaleMonthComponent.delRedisByMonth(month,month);
                    //获取表中当前最新月份
                    RankSaleMonthSourceEntity lastMonth = rankSaleMonthMapper.getLastMonth();
                    if (lastMonth!=null&&!Strings.isNullOrEmpty(lastMonth.getMonth())){
                        updateRedisByKey(RankRedisKeyEnum.MONTH_LAST_PASSIVE_UPDATE_TIME.getKey(), lastMonth.getMonth(), true, 20, TimeUnit.DAYS);
                        Date lastMontDate = DateUtils.parseDate(lastMonth.getMonth(), Locale.CHINA, "yyyy-MM");
                        String lastMontDateStr = DateFormatUtils.format(lastMontDate, "yyyy-MM");
                        String last3DateStr = DateFormatUtils.format(DateUtils.addMonths(lastMontDate, -2), "yyyy-MM");
                        String last6DateStr = DateFormatUtils.format(DateUtils.addMonths(lastMontDate, -5), "yyyy-MM");
                        //近三个月
                        rankSaleMonthComponent.delRedisByMonth(last3DateStr, lastMontDateStr);
                        //近半年
                        rankSaleMonthComponent.delRedisByMonth(last6DateStr, lastMontDateStr);
                    }
                }
            }
            //刷新周销量列表和趋势
            if (operatetype == 2 && !Strings.isNullOrEmpty(week)) {
                isSuccess = seriesSaleWeekRankByEnergyTypeComponent.modifyRefresh();
                if (isSuccess){
                    //删除周榜列表当前周缓存
                    rankSaleWeekComponent.delRedisByWeek(week);
                    RankSaleWeekSourceEntity lastWeek = rankSaleWeekMapper.getLastWeek();
                    if (lastWeek!=null&&!Strings.isNullOrEmpty(lastWeek.getWeek_day())) {
                        updateRedisByKey(RankRedisKeyEnum.WEEK_LAST_PASSIVE_UPDATE_TIME.getKey(), lastWeek.getWeek_day(), true, 20, TimeUnit.DAYS);
                    }
                }
            }
            //更改缓存版本号
            if (isSuccess) {
                RankSaleRefreshVersionEntity dbInfo = rankSaleRefreshVersionMapper.getRankSaleRefreshVersion();
                if (dbInfo != null && dbInfo.getId() > 0) {
                    int old_current = dbInfo.getCurrentversion();
                    int current = old_current + 1;
                    boolean isTrue = rankSaleRefreshVersionMapper.updateRankSaleRefreshVersion(current, old_current) > 0;
                    if (isTrue) {
                        updateRedisByKey(MonthAndWeekRankRefresh_RedisKey, String.valueOf(dbInfo.getCurrentversion()), true, 365, TimeUnit.DAYS);
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("refreshMonthAndWeekRank异常-ex:{}", ex);
        }
        return false;
    }

    /**
     * 获取月销、周销的缓存刷新版本
     *
     * @return
     */
    private int getMonthAndWeekRankRefreshVesion() {
        try {
            String json = redisTemplate.opsForValue().get(MonthAndWeekRankRefresh_RedisKey);
            if (Strings.isNullOrEmpty(json)) {
                RankSaleRefreshVersionEntity dbInfo = rankSaleRefreshVersionMapper.getRankSaleRefreshVersion();
                if (dbInfo != null && dbInfo.getId() > 0) {
                    updateRedisByKey(MonthAndWeekRankRefresh_RedisKey, String.valueOf(dbInfo.getCurrentversion()), true, 365, TimeUnit.DAYS);
                    return dbInfo.getCurrentversion();
                }
            } else {
                return Integer.parseInt(json);
            }
        } catch (Exception ex) {
            log.error("getMonthAndWeekRankRefreshVesion异常-ex:{}", ex);
        }
        return 0;
    }
}
