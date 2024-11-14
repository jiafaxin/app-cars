package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.common.BasePageModel;
import com.autohome.app.cars.common.enums.RankRedisKeyEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.DateRankResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.MonthRankDataResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.common.RankResultNewDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhangchengtao
 * @date 2024/4/23 10:46
 */
@Component
@Slf4j
public class RankSaleMonthComponent extends RankBaseComponent<MonthRankDataResultDto> {


    @Resource
    private RankSaleMonthSourceComponent monthSourceComponent;

    @Resource
    private RankCommonComponent rankCommonComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    final static String dateParamName = "date";
    //月榜最新月份

    final static String LastMonth_RedisKey = "LastMonth_RedisKey";

    public TreeMap<String, Object> makeParam(String date) {
        return ParamBuilder.create(dateParamName, date).build();
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }


    /**
     * 获取月份时间
     * @param beginMonth 开始月份
     * @param endMonth 结束月份
     * @param size 1000
     * @return result
     */
    public List<MonthRankDataResultDto.RankDataDto> getDataList(String beginMonth, String endMonth, int size) {
        TreeMap<String, Object> makeParam = makeParam(RankUtil.getDataRange(beginMonth, endMonth));
        // 先从Redis中获取
        MonthRankDataResultDto baseDataResult = new MonthRankDataResultDto();
        try {
            MonthRankDataResultDto fromRedis = getFromRedis(makeParam);
            if (Objects.nonNull(fromRedis)) {
                baseDataResult = fromRedis;
            }
        } catch (Exception e) {
            log.warn("从Redis中获取月榜销量失败!", e);
            // 如果序列化失败, 则重建缓存
            deleteRedis(makeParam);
        }

        List<MonthRankDataResultDto.RankDataDto> dataList = Collections.emptyList();
        if (Objects.nonNull(baseDataResult.getDataList()) && !baseDataResult.getDataList().isEmpty()) {
            dataList = baseDataResult.getDataList();
        } else {
            // Redis中不存在则从数据库中查询
            MonthRankDataResultDto resultFromDB = getDataFromDB(beginMonth, endMonth, size);
            if (Objects.nonNull(resultFromDB.getDataList()) && !resultFromDB.getDataList().isEmpty()) {
                dataList = resultFromDB.getDataList();
                baseDataResult.setDataList(dataList);
                try {
                    String cacheStr = RankUtil.serializeObject(dataList, MonthRankDataResultDto.RankDataDto.class);
                    updateRedis(makeParam, cacheStr, true);
                } catch (Exception e) {
                    log.warn("销量月榜-写入Redis失败", e);
                }
            }
        }
        return dataList;

    }

    /**
     * 获取时间段内未经过滤条件过滤的销量数据
     *
     * @param beginMonth 开始月份
     * @param endMonth   结束月份
     * @param param      查询参数
     * @return 销量数据
     */
    public RankResultDto getAllListOnlyByMonth(String beginMonth, String endMonth, RankParam param) {
        RankResultDto result = new RankResultDto();
        List<RankResultDto.ListDTO> resultList = new ArrayList<>();
        List<MonthRankDataResultDto.RankDataDto> dataList = getDataList(beginMonth, endMonth, 1000);
        if (!dataList.isEmpty()) {
            rankCommonComponent.reSort(param.getEnergytype(), dataList);
            // 处理数据Dto转为resultDto
            dataList.forEach(data -> resultList.add(transToResult(data, param)));
        }
        result.getResult().setSaleranktip("* 数据源于行业综合销量，每月10日左右更新");
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        result.getResult().setShareinfo(rankCommonComponent.getShareInfo());
        result.getResult().setPageindex(param.getPageindex());
        result.getResult().setPagesize(param.getPagesize());
        result.getResult().setList(resultList);
        // 更新后写入Redis
        return result;

    }


    public MonthRankDataResultDto getDataFromDB(String beginMonth, String endMonth, int size) {
        List<RankSaleMonthSourceEntity> sourceEntityList = monthSourceComponent.getSaleCountByCondition(beginMonth, endMonth, size);
        MonthRankDataResultDto result = new MonthRankDataResultDto();
        if (Objects.nonNull(sourceEntityList) && !sourceEntityList.isEmpty()) {
            List<MonthRankDataResultDto.RankDataDto> resultList = new ArrayList<>(sourceEntityList.size());
            List<Integer> seriesIdList = sourceEntityList.stream().map(x -> Integer.parseInt(x.getSeriesid())).collect(Collectors.toList());
            // 获取车系信息Map
            Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(seriesIdList);
            // 获取口碑分Map
            Map<String, String> kouBeiScoreMap = rankCommonComponent.getKouBeiScoreMap(seriesIdList);

            for (RankSaleMonthSourceEntity item : sourceEntityList) {
                MonthRankDataResultDto.RankDataDto dataDto = new MonthRankDataResultDto.RankDataDto();
                String seriesId = item.getSeriesid();
                dataDto.setSeriesId(Integer.parseInt(seriesId));
                dataDto.setSaleCount(item.getSalecnt());

                // 填充各个能源类型销量
                rankCommonComponent.fillEnergyTypeSaleCount(dataDto, item.getEnergy_sale_count());
                if (seriesDetailMap.containsKey(seriesId)) {
                    // 获取车系信息
                    SeriesDetailDto detail = seriesDetailMap.get(seriesId);
//                    dataDto.setSeriesImage(RankUtil.resizeSeriesImage(detail.getPngLogo()));
                    dataDto.setHostSpecId(detail.getHotSpecId());
                    dataDto.setSeriesName(detail.getName());
                    dataDto.setMinPrice(detail.getMinPrice());
                    dataDto.setMaxPrice(detail.getMaxPrice());
                    dataDto.setLevelId(String.valueOf(detail.getLevelId()));
                    dataDto.setBrandId(detail.getBrandId());
                    dataDto.setEnergyType(detail.getEnergytype());
                    dataDto.setManuType(item.getManu_type());
                    dataDto.setFuelTypes(detail.getFueltypes());
                    dataDto.setMonth(item.getMonth());
                    dataDto.setState(detail.getState());
                    // 获取口碑分
                    String kouBeiScore = kouBeiScoreMap.get(seriesId);
                    if (StringUtils.hasLength(kouBeiScore)) {
                        dataDto.setScoreValue(kouBeiScore);
                    }
                    resultList.add(dataDto);
                } else {
                    log.warn("未查询到车系详情, 当前车系ID:{}", item.getSeriesid());
                }
            }
            result.setDataList(resultList);
        }
        return result;
    }

    private RankResultDto.ListDTO transToResult(MonthRankDataResultDto.RankDataDto item, RankParam param) {
        RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
        String seriesId = item.getSeriesId().toString();
        // 设置车系详细数据
        dto.setSeriesid(seriesId);
        // 设置各个能源类型销量
        rankCommonComponent.setEnergyTypeSaleDetail(param.getEnergytype(), item, dto);
        // 获取车系信息
//        dto.setSeriesimage(item.getSeriesImage());
//        dto.setSeriesname(item.getSeriesName());
        dto.setState(item.getState());
        dto.setMinPrice(item.getMinPrice());
        dto.setMaxPrice(item.getMaxPrice());
//        dto.setPriceinfo(CommonHelper.priceForamtV2(item.getMinPrice(), item.getMaxPrice()));
        dto.setLevelId(String.valueOf(item.getLevelId()));
        dto.setBrandid(item.getBrandId());
        dto.setEnergytype(item.getEnergyType());
        dto.setManuType(item.getManuType());
        dto.setFuelTypes(item.getFuelTypes());
        dto.setShowenergyicon(item.getEnergyType());
        // 获取口碑分
        if (StringUtils.hasLength(item.getScoreValue())) {
            dto.setScorevalue(item.getScoreValue());
        } else {
            dto.setScorevalue("暂无");
        }
        dto.setScoretip("分");
        dto.setCardtype(2);
        dto.setIsshowscorevalue(1);
        dto.setSpecname(StrPool.EMPTY);
        // dto.setRcmdesc(StrPool.EMPTY);
        dto.setRcmtext(StrPool.EMPTY);
        dto.setRighttextone(StrPool.EMPTY);
        dto.setRighttexttwo(StrPool.EMPTY);
        dto.setRighttexttwolinkurl(StrPool.EMPTY);


        // 筛选第三行能源类型时, 因为在本地做了过滤, 所以不展示排名变化
        dto.setShowrankchange(1);
        dto.setRankchange(item.getPreRankNum() - item.getRn());
        // 设置PV
        Map<String, String> pvArgs = new HashMap<>();
        pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
        pvArgs.put("rank", StrPool.EMPTY);
        pvArgs.put("typeid", String.valueOf(param.getTypeid()));
        pvArgs.put("seriesid", seriesId);

        dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107", seriesId));
        dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null, "car_rec_main_rank_series_show", null));
        dto.setRightinfo(genMonthSaleRankRightInfo(pvArgs, param, item));
        return dto;
    }


    /**
     * 生成销量月榜rightInfo
     *
     * @param dto     数据dto
     * @param argsMap 参数
     * @param param   RecRankParam
     * @return RankResultDto.RightinfoDTO
     */
    private RankResultDto.RightinfoDTO genMonthSaleRankRightInfo(Map<String, String> argsMap, RankParam param, MonthRankDataResultDto.RankDataDto dto) {
        String rankEid = "";
        if (param.getTypeid() == 1) {
            rankEid = getRankEid(param.getChannel(), param.getPm());
        } else if (param.getTypeid() == 9) {
            rankEid = rankCommonComponent.getNewEnergyMonthAndWeekRankEid(param.getChannel(), param.getPm());
        }
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();

        String rightTextTwoUrlScheme = "autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s";
        String rightTwoUrlScheme = "rn://Car_SeriesSummary/SaleHistory?panValid=0&seriesid=%s&seriesname=%s&typeid=%s&subranktypeid=%s&date=%s&energytype=%d";
        String rightUrlFormat = String.format(rightTextTwoUrlScheme, CommonHelper.encodeUrl(String.format(rightTwoUrlScheme, dto.getSeriesId(), CommonHelper.encodeUrl(dto.getSeriesName()), param.getTypeid(), param.getSubranktypeid(), dto.getMonth(), param.getEnergytype())));
        rightInfo.setRighttexttwolinkurl(rightUrlFormat);
        rightInfo.setRighttextone(String.valueOf(dto.getSaleCount()));
        rightInfo.setRighttexttwo("销量趋势");
        rightInfo.setExt("");
        RankResultDto.RightinfoDTO.PriceInfoDto priceInfo = new RankResultDto.RightinfoDTO.PriceInfoDto();
        String linkURLScheme = "autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s";
        String urlScheme = "rn://MallService/AskPrice?panValid=0&pvareaid=6849804&seriesid=%s&eid=%s";
        String linkURL = String.format(linkURLScheme, CommonHelper.encodeUrl(String.format(urlScheme, dto.getSeriesId(), CommonHelper.encodeUrl(rankEid))));
        rightInfo.setRightpricetitle("查成交价");
        rightInfo.setRightpriceurl(linkURL);
        rightInfo.setRightpriceeid(rankEid);
        rightInfo.setExt("");
        rightInfo.setPriceinfo(priceInfo);
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.55.0") && "rankcxhtest".equals(param.getAskpricecxhab())) {
            rankCommonComponent.setProgrammaticPriceInfo(rightInfo, param, dto.getSeriesId(), dto.getHostSpecId(), 1000, "查成交价");
        }
        if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.0") && "avgprice".equals(param.getAskpricecxhab())) {
            rankCommonComponent.setProgrammaticPriceInfo(rightInfo, param, dto.getSeriesId(), dto.getHostSpecId(), 17, "查报价单");
        }
        PvItem pvItem = PvItem.getInstance(argsMap, "car_rec_main_rank_history_click", null, "", null);
        rightInfo.setPvitem(pvItem);
        return rightInfo;
    }

    /**
     * 获取榜单迁移后的EID
     *
     * @param channel 场景榜/总榜
     * @param pm      平台 安卓/iOS
     * @return Eid
     */
    private String getRankEid(int channel, int pm) {
        String eid;
        if (channel == 0) {
            eid = pm == 1 ? "3|1411002|572|25528|205415|304431" : "3|1412002|572|25528|205415|304430";
        } else {
            eid = pm == 1 ? "3|1411002|572|25529|205414|304429" : "3|1412002|572|25529|205414|304428";
        }
        return eid;
    }


    /**
     * 获取时间段内的销量数据
     *
     * @param param data
     * @return 销量数据
     */
    @Override
    public RankResultDto getResultListByCondition(RankParam param) {
        RankResultDto result = new RankResultDto();
        if (param.getEnergytype() < 0) {
            fillInfo(param, result);
            return result;
        }
        // 月榜无需筛选是否在售,如果传入该参数, 则忽略, 设置为0
        if (param.getIssale() == 1) {
            param.setIssale(0);
        }
        if (param.getBeginMonth().isEmpty()) {
            String lastMonth = redisTemplate.opsForValue().get(RankRedisKeyEnum.MONTH_LAST_PASSIVE_UPDATE_TIME.getKey());
            param.setBeginMonth(lastMonth);
            param.setEndMonth(lastMonth);
        }
        // 计算上个月的日期
        String preBeginMonth = rankCommonComponent.getDateOffset(param.getBeginMonth(), RankConstant.MONTH_DATE_FORMAT, Calendar.MONTH, -1);
        // 获取时间范围内的销量数据
        CompletableFuture<RankResultDto> curRankFuture = CompletableFuture.supplyAsync(() -> {
            RankResultDto resultDto = getAllListOnlyByMonth(param.getBeginMonth(), param.getEndMonth(), param);
            // 通过条件筛选
            rankCommonComponent.filterByParam(resultDto, param);
            if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().getList().isEmpty()) {
                // 处理当月销量排名
                rankCommonComponent.processRankNum(resultDto);
            }
            return resultDto;
        }).exceptionally(e -> {
            log.warn("查询当月销量数据出错!", e);
            return null;
        });
        CompletableFuture<RankResultDto> preRankFuture = CompletableFuture.supplyAsync(() -> {
            /*
             * 当前此处计算上月销量的方式为: 如当前查询销量范围为 2024/02-2014-04 上月排名取值的范围是 2024-01 月份的排名
             * 若后期改为同等时间段排名重新计算 perEndMonth即可
             */
            RankResultDto preResultDto = getAllListOnlyByMonth(preBeginMonth, preBeginMonth, param);
            if (Objects.nonNull(preResultDto) && Objects.nonNull(preResultDto.getResult()) && !preResultDto.getResult().getList().isEmpty()) {
                // 通过条件过滤上月数据
                rankCommonComponent.filterByParam(preResultDto, param);
                // 处理上月数据的排名信息
                rankCommonComponent.processRankNum(preResultDto);
            }
            return preResultDto;
        }).exceptionally(e -> {
            log.warn("查询当月销量数据出错!", e);
            return null;
        });
        // 计算上月排名
        if (Objects.nonNull(curRankFuture) && Objects.nonNull(preRankFuture)) {
            result = curRankFuture.join();
            RankResultDto preResult = preRankFuture.join();
            if (Objects.nonNull(preResult)) {
                rankCommonComponent.calcPreRankNum(result, preResult);
                rankCommonComponent.processOtherInfo(result, param);
            }
        }
        refreshDetailInfo(result);
        return result;
    }


    @Override
    public MonthRankDataResultDto getFromRedis(TreeMap<String, Object> params) {
        String key = getKey(params);
        String cacheStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(cacheStr)) {
            MonthRankDataResultDto result = new MonthRankDataResultDto();
            List<MonthRankDataResultDto.RankDataDto> dataDtoList = RankUtil.deserializeObject(cacheStr, MonthRankDataResultDto.RankDataDto.class);
            if (!dataDtoList.isEmpty()) {
                result.setDataList(dataDtoList);
                return result;
            }
        }
        return null;
    }

    public void fillInfo(RankParam param, RankResultDto result) {
        result.getResult().setSaleranktip("* 数据源于行业综合销量，每月10日左右更新");
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        result.getResult().setShareinfo(rankCommonComponent.getShareInfo());
        result.getResult().setPageindex(param.getPageindex());
        result.getResult().setPagesize(param.getPagesize());
        rankCommonComponent.fillInfo(param, result);
    }

    /**
     * 获取几周的销量数据（以库中最新周为基准，向前查询weekCount周）
     *
     * @param monthCount
     * @return
     */
    public List<DateRankResultDto> getAllListByLastMonth(int monthCount) {
        List<DateRankResultDto> resultList = new ArrayList<>();
        try {
            List<CompletableFuture> tasks = new ArrayList<>();
            String lastMonth = monthSourceComponent.getLastMonth();
            if (Objects.nonNull(lastMonth) && StringUtils.hasLength(lastMonth) && monthCount > 0) {
                for (int i = 0; i < monthCount; i++) {
                    int finalI = i;
                    tasks.add(CompletableFuture.supplyAsync(() -> {
                        String[] dates = getBeginEndMonthDates(lastMonth, finalI);
                        String beginDate = dates[0];
                        String endDate = dates[1];
                        RankParam param = new RankParam();
                        param.setPageindex(1);
                        param.setPagesize(9999);
                        RankResultDto result = getAllListOnlyByMonth(beginDate, endDate, param);
                        // 通过条件筛选
                        rankCommonComponent.filterByParam(result, param);
                        if (Objects.nonNull(result) && Objects.nonNull(result.getResult()) && !result.getResult().getList().isEmpty()) {
                            // 处理当月销量排名
                            rankCommonComponent.processRankNum(result);
                        }
                        DateRankResultDto dto = new DateRankResultDto();
                        dto.setDate(beginDate + "=" + endDate);
                        dto.setRankResultDto(result);
                        return dto;
                    }).thenAccept(dto -> {
                        resultList.add(dto);
                    }).exceptionally(e -> {
                        log.error("getAllListByLastWeek error:", ExceptionUtils.getStackTrace(e));
                        return null;
                    }));
                }
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
            resultList.sort(Comparator.comparing(DateRankResultDto::getDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private String[] getBeginEndMonthDates(String date, int month) {
        String beginDate = "";
        String endDate = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(date, "yyyy-MM"));
        calendar.add(Calendar.MONTH, -month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        beginDate = DateUtil.format(calendar.getTime(), "yyyy-MM");
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = DateUtil.format(calendar.getTime(), "yyyy-MM");
        return new String[]{beginDate, endDate};
    }


    private void refreshDetailInfo(RankResultDto resultDto) {
        List<Integer> list = resultDto.getResult().getList().stream().map(x -> Integer.parseInt(x.getSeriesid())).toList();
        Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(list);
        resultDto.getResult().getList().forEach(dto -> {
            if (seriesDetailMap.containsKey(dto.getSeriesid())) {
                SeriesDetailDto detailDto = seriesDetailMap.get(dto.getSeriesid());
                dto.setSeriesimage(RankUtil.resizeSeriesImage(detailDto.getPngLogo()));
                dto.setPriceinfo(CommonHelper.priceForamtV2(detailDto.getMinPrice(), detailDto.getMaxPrice()));
                dto.setSeriesname(detailDto.getName());
            }
        });
    }

    public String getLastMonth() {
        String lastMonth = redisTemplate.opsForValue().get(LastMonth_RedisKey);
        if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(lastMonth)) {
            return lastMonth;
        }
        lastMonth=monthSourceComponent.getLastMonth();
        updateRedisByKey(LastMonth_RedisKey, lastMonth, true, 1, TimeUnit.HOURS);
        return lastMonth;
    }

    /**
     * 删除月榜对应缓存  month格式：2024-06
     * @param beginMonth 开始
     * @param endMonth 结束
     */
    public void delRedisByMonth(String beginMonth,String endMonth) {
        TreeMap<String, Object> makeParam = makeParam(RankUtil.getDataRange(beginMonth, endMonth));
        deleteRedis(makeParam);
    }

    /**
     * @param pageIndex      第几页
     * @param pageSize       每页多少条
     * @param filterSeriesId 要过滤的车系id
     * @param carType 0全部 1乘用车 2商用车
     * @description 获取最新月榜车系列表数据
     * @author zzli
     */
    public BasePageModel<MonthRankDataResultDto.RankDataDto> getLatestMonthDataList(int pageIndex, int pageSize, List<Integer> filterSeriesId,int carType) {

        try {
            String lastMonth = getLastMonth();
            List<MonthRankDataResultDto.RankDataDto> dataList = getDataList(lastMonth, lastMonth, 1000);
            // 车系过滤
            if (!CollectionUtils.isEmpty(filterSeriesId)) {
                dataList.removeIf(x -> filterSeriesId.contains(x.getSeriesId()));
            }
            if (carType > 0) {
                dataList = dataList.stream().filter(x -> {
                    int levelId = NumberUtils.toInt(x.getLevelId());
                    if (carType == 1) {
                        return !Level.isCVLevel(levelId);
                    } else if (carType == 2) {
                        return Level.isCVLevel(levelId);
                    }
                    return true;
                }).collect(Collectors.toList());
            }
            return new BasePageModel<>(pageIndex, pageSize, dataList);
        } catch (Exception e) {
            log.error("获取最新月榜车系列表数据出错!", e);
            return new BasePageModel<>();
        }
    }
    public String getMonthRank(TreeMap<String, Object> params) {
        RankParam param = super.getParams(params);

        if (com.autohome.app.cars.common.utils.StringUtils.isEmpty(param.getBeginMonth())) {
            String lastMonth = getLastMonth();
            param.setDate(lastMonth);
            RankParam.transMonth(param);
        }
        // 获取时间范围内的销量数据
        CompletableFuture<RankResultDto> curRankFuture = CompletableFuture.supplyAsync(() -> {
            RankResultDto resultDto = getAllListOnlyByMonth(param.getBeginMonth(), param.getEndMonth(), param);
            // 通过条件筛选
            rankCommonComponent.filterByParam(resultDto, param);
            if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().getList().isEmpty()) {
                // 处理当月销量排名
                rankCommonComponent.processRankNum(resultDto);
            }
            return resultDto;
        }).exceptionally(e -> {
            log.warn("查询当月销量数据出错!", e);
            return null;
        });
        CompletableFuture<RankResultDto> preRankFuture = CompletableFuture.supplyAsync(() -> {
            // 计算上个月的日期
            String preBeginMonth = rankCommonComponent.getDateOffset(param.getBeginMonth(), RankConstant.MONTH_DATE_FORMAT, Calendar.MONTH, -1);
            /*
             * 当前此处计算上月销量的方式为: 如当前查询销量范围为 2024/02-2014-04 上月排名取值的范围是 2024-01 月份的排名
             * 若后期改为同等时间段排名重新计算 perEndMonth即可
             */
            RankResultDto preResultDto = getAllListOnlyByMonth(preBeginMonth, preBeginMonth, param);
            if (Objects.nonNull(preResultDto) && Objects.nonNull(preResultDto.getResult()) && !preResultDto.getResult().getList().isEmpty()) {
                // 通过条件过滤上月数据
                rankCommonComponent.filterByParam(preResultDto, param);
                // 处理上月数据的排名信息
                rankCommonComponent.processRankNum(preResultDto);
            }
            return preResultDto;
        }).exceptionally(e -> {
            log.warn("查询上月销量数据出错!", e);
            return null;
        });

        RankResultDto resultDto = CompletableFuture.allOf(curRankFuture, preRankFuture).thenApply(p -> {
            RankResultDto result = curRankFuture.join();
            if (Objects.isNull(result) || result.getResult() == null || result.getResult().getList() == null || result.getResult().getList().isEmpty()) {
                return null;
            }

            RankResultDto preResult = preRankFuture.join();
            if (Objects.nonNull(preResult)) {
                rankCommonComponent.calcPreRankNum(result, preResult);
            }
            rankCommonComponent.pagination(result, param);

            return result;
        }).join();

        RankResultNewDto result = new RankResultNewDto();

        if (resultDto != null && resultDto.getResult() != null && resultDto.getResult().getList() != null) {
            result.setPagecount(resultDto.getResult().getPagecount());
            result.setPageindex(resultDto.getResult().getPageindex());
            result.setPagesize(resultDto.getResult().getPagesize());


            //填充车系信息
            List<Integer> seriesIds = resultDto.getResult().getList().stream().map(x -> Integer.parseInt(x.getSeriesid())).toList();
            Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(seriesIds);


            resultDto.getResult().getList().forEach(x -> {
                RankResultNewDto.RankListItemDto dto = new RankResultNewDto.RankListItemDto();
                BeanUtils.copyProperties(x, dto);

                if (seriesDetailMap.containsKey(dto.getSeriesid())) {
                    SeriesDetailDto detailDto = seriesDetailMap.get(dto.getSeriesid());
                    dto.setSeriesimage(RankUtil.resizeSeriesImage(detailDto.getPngLogo()));
                    dto.setPriceinfo(CommonHelper.priceForamtV2(detailDto.getMinPrice(), detailDto.getMaxPrice()));
                    dto.setSeriesname(detailDto.getName());
                    dto.setBrandid(detailDto.getBrandId());
                    dto.setBrandname(detailDto.getBrandName());
                    dto.setBrandimage(detailDto.getBrandLogo());
                    dto.setState(detailDto.getState());
                    dto.setMinpricespecid(detailDto.getMinPriceSpecId());
                    if (detailDto.getMinPriceSpecId()>0) {
                        SpecDetailDto spec = specDetailComponent.getSync(detailDto.getMinPriceSpecId());
                        dto.setMinpricespecname(spec.getSpecName());
                    }
                }
                result.getList().add(dto);
            });

            // 筛选在售状态
            if (param.getIssale() == 1) {
                result.getList().removeIf(x->!RankConstant.ON_SALE_STATE_LIST.contains(x.getState()));
            }
        }
        return JsonUtil.toString(result);
    }
}
