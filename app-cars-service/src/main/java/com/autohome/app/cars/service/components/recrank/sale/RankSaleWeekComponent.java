package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.DateRankResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.WeekRankDataResultDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author zhangchengtao
 * @date 2024/5/6 19:10
 */
@Component
@Slf4j
public class RankSaleWeekComponent extends RankBaseComponent<WeekRankDataResultDto> {

    @Resource
    private RankSaleWeekSourceComponent rankSaleWeekSourceComponent;

    @Resource
    private RankCommonComponent rankCommonComponent;

    final static String WEEK_PARAM_NAME = "week";

    public TreeMap<String, Object> makeParam(String week) {
        return ParamBuilder.create(WEEK_PARAM_NAME, week).build();
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    /**
     * 获取周榜数据List
     *
     * @param beginWeek 开始日期
     * @param endWeek   结束日期
     * @return result
     */
    public List<WeekRankDataResultDto.RankDataDto> getDataList(String beginWeek, String endWeek) {
        TreeMap<String, Object> makeParam = makeParam(RankUtil.getDataRange(beginWeek, endWeek));
        // 先从Redis中获取
        WeekRankDataResultDto baseDataResult = new WeekRankDataResultDto();
        try {
            WeekRankDataResultDto fromRedis = getFromRedis(makeParam);
            if (Objects.nonNull(fromRedis)) {
                baseDataResult = fromRedis;
            }
        } catch (Exception e) {
            log.warn("从Redis中获取周榜销量失败!", e);
            deleteRedis(makeParam);
        }
        List<WeekRankDataResultDto.RankDataDto> dataList = Collections.emptyList();
        if (Objects.nonNull(baseDataResult.getDataList()) && !baseDataResult.getDataList().isEmpty()) {
            dataList = baseDataResult.getDataList();
        } else {
            // Redis中不存在则从数据库中查询
            WeekRankDataResultDto resultFromDB = getDataFromDB(beginWeek, endWeek, 10000);
            if (Objects.nonNull(resultFromDB.getDataList()) && !resultFromDB.getDataList().isEmpty()) {
                dataList = resultFromDB.getDataList();
                baseDataResult.setDataList(dataList);
                try {
                    String cacheStr = RankUtil.serializeObject(dataList, WeekRankDataResultDto.RankDataDto.class);
                    updateRedis(makeParam, cacheStr, true);
                } catch (Exception e) {
                    log.warn("销量月榜-写入Redis失败", e);
                }
            }
        }
        return dataList;
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
        // 计算上个周的日期
        String preBeginWeek = rankCommonComponent.getDateOffset(param.getWeek(), RankConstant.WEEK_DATE_FORMAT, Calendar.DAY_OF_YEAR, -7);
        // 获取时间范围内的销量数据
        CompletableFuture<RankResultDto> curRankFuture = CompletableFuture.supplyAsync(() -> {
            RankResultDto resultDto = getAllListOnlyByWeek(param.getWeek(), param.getEndWeek(), param);
            // 通过条件筛选
            rankCommonComponent.filterByParam(resultDto, param);
            if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().getList().isEmpty()) {
                // 处理当月销量排名
                rankCommonComponent.processRankNum(resultDto);
            }
            return resultDto;
        }).exceptionally(e -> {
            log.warn("查询当周销量数据出错!", e);
            return null;
        });
        CompletableFuture<RankResultDto> preRankFuture = CompletableFuture.supplyAsync(() -> {
            /*
             * 当前此处计算上月销量的方式为: 如当前查询销量范围为 2024/02-2014-04 上月排名取值的范围是 2024-01 月份的排名
             * 若后期改为同等时间段排名重新计算 perEndMonth即可
             */
            RankResultDto preResultDto = getAllListOnlyByWeek(preBeginWeek, preBeginWeek, param);
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
            rankCommonComponent.calcPreRankNum(result, preRankFuture.join());
            rankCommonComponent.processOtherInfo(result, param);
        }
        refreshDetailInfo(result);
        return result;
    }

    public RankResultDto getAllListOnlyByWeek(String beginWeek, String endWeek, RankParam param) {
        RankResultDto result = new RankResultDto();
        List<RankResultDto.ListDTO> resultList = new ArrayList<>();
        List<WeekRankDataResultDto.RankDataDto> dataList = getDataList(beginWeek, endWeek);
        if (!dataList.isEmpty()) {
            // 重新排序
            rankCommonComponent.reSort(param.getEnergytype(), dataList);
            // 处理数据Dto转为resultDto
            dataList.forEach(data -> resultList.add(transToResult(data, param)));
        }
        result.getResult().setSaleranktip("* 数据源于终端销量及之家大数据整理");
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        result.getResult().setPageindex(param.getPageindex());
        result.getResult().setPagesize(param.getPagesize());
        result.getResult().setList(resultList);


        // 更新后写入Redis
        return result;
    }

    private WeekRankDataResultDto getDataFromDB(String beginWeek, String endWeek, int size) {
        List<RankSaleWeekSourceEntity> sourceEntityList = rankSaleWeekSourceComponent.getListByWeek(beginWeek, endWeek, size);
        WeekRankDataResultDto result = new WeekRankDataResultDto();
        if (Objects.nonNull(sourceEntityList) && !sourceEntityList.isEmpty()) {
            List<WeekRankDataResultDto.RankDataDto> resultList = new ArrayList<>(sourceEntityList.size());
            List<Integer> seriesIdList = sourceEntityList.stream().map(x -> Integer.parseInt(x.getSeriesid())).collect(Collectors.toList());
            // 获取车系信息Map
            Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(seriesIdList);
            // 获取口碑分Map
            Map<String, String> kouBeiScoreMap = rankCommonComponent.getKouBeiScoreMap(seriesIdList);
            for (RankSaleWeekSourceEntity item : sourceEntityList) {
                WeekRankDataResultDto.RankDataDto dataDto = new WeekRankDataResultDto.RankDataDto();
                String seriesId = item.getSeriesid();
                dataDto.setSeriesId(Integer.parseInt(seriesId));
                dataDto.setSaleCount(item.getSalecnt());
                // 填充各个能源类型销量
                rankCommonComponent.fillEnergyTypeSaleCount(dataDto, item.getEnergy_sale_count());
                if (seriesDetailMap.containsKey(seriesId)) {
                    // 获取车系信息
                    SeriesDetailDto detail = seriesDetailMap.get(seriesId);
                    dataDto.setSeriesImage(RankUtil.resizeSeriesImage(detail.getPngLogo()));
                    dataDto.setSeriesName(detail.getName());
                    dataDto.setMinPrice(detail.getMinPrice());
                    dataDto.setMaxPrice(detail.getMaxPrice());
                    dataDto.setLevelId(String.valueOf(detail.getLevelId()));
                    dataDto.setBrandId(detail.getBrandId());
                    dataDto.setEnergyType(detail.getEnergytype());
                    dataDto.setManuType(item.getManu_type());
                    dataDto.setFuelTypes(detail.getFueltypes());
                    dataDto.setWeekDay(item.getWeek_day());
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


    private RankResultDto.ListDTO transToResult(WeekRankDataResultDto.RankDataDto item, RankParam param) {
        RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
        String seriesId = item.getSeriesId().toString();
        // 设置车系详细数据
        dto.setSeriesid(seriesId);
        dto.setSalecount(item.getSaleCount());
        // 设置各个能源类型销量
        rankCommonComponent.setEnergyTypeSaleDetail(param.getEnergytype(), item, dto);
        // 获取车系信息
        dto.setSeriesimage(item.getSeriesImage());
        dto.setSeriesname(item.getSeriesName());
        dto.setMinPrice(item.getMinPrice());
        dto.setMaxPrice(item.getMaxPrice());
        dto.setPriceinfo(CommonHelper.priceForamtV2(item.getMinPrice(), item.getMaxPrice()));
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
        dto.setRightinfo(genWeekSaleRankRightInfo(pvArgs, item.getSaleCount(), param, item));
        return dto;
    }

    private RankResultDto.RightinfoDTO genWeekSaleRankRightInfo(Map<String, String> argsMap, Long saleCount, RankParam param, WeekRankDataResultDto.RankDataDto dto) {
        String rankEid = "";
        if (param.getTypeid() == 1) {
            rankEid = getWeekRankEid(param.getChannel(), param.getPm());
        } else if (param.getTypeid() == 9) {
            rankEid = rankCommonComponent.getNewEnergyMonthAndWeekRankEid(param.getChannel(), param.getPm());
        }
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();

        String rightTextTwoUrlScheme = "autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s";
        String rightTwoUrlScheme = "rn://Car_SeriesSummary/SaleHistory?panValid=0&seriesid=%s&seriesname=%s&typeid=%s&subranktypeid=%s&date=%s&energytype=%d";
        String rightUrlFormat = String.format(rightTextTwoUrlScheme, CommonHelper.encodeUrl(String.format(rightTwoUrlScheme, dto.getSeriesId(), CommonHelper.encodeUrl(dto.getSeriesName()), param.getTypeid(), param.getSubranktypeid(), dto.getWeekDay(), param.getEnergytype())));
        rightInfo.setRighttexttwolinkurl(rightUrlFormat);
        rightInfo.setRighttextone(saleCount.toString());
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

    private String getWeekRankEid(int channel, int pm) {
        if (pm == 1) {
            if (channel == 0) {
                return "3|1411002|572|25528|205883|305153";
            } else if (channel == 1) {
                return "3|1411002|572|25529|205884|305153";
            }
        } else {
            if (channel == 0) {
                return "3|1412002|572|25528|205883|305153";
            } else if (channel == 1) {
                return "3|1412002|572|25529|205884|305153";
            }
        }
        return StrPool.EMPTY;
    }

    @Override
    public WeekRankDataResultDto getFromRedis(TreeMap<String, Object> params) {
        String key = getKey(params);
        String json = redisTemplate.opsForValue().get(key);
        WeekRankDataResultDto resultDto = new WeekRankDataResultDto();
        if (StringUtils.hasLength(json)) {
            List<WeekRankDataResultDto.RankDataDto> dataDtoList = RankUtil.deserializeObject(json, WeekRankDataResultDto.RankDataDto.class);
            resultDto.setDataList(dataDtoList);
            return resultDto;
        }
        return null;
    }

    /**
     * 获取几周的销量数据（以库中最新周为基准，向前查询weekCount周）
     *
     * @param weekCount
     * @return
     */
    public List<DateRankResultDto> getAllListByLastWeek(int weekCount) {
        List<DateRankResultDto> resultList = new ArrayList<>();
        try {
            List<CompletableFuture> tasks = new ArrayList<>();
            String lastWeekDay = rankSaleWeekSourceComponent.getLastWeek();
            if (Objects.nonNull(lastWeekDay) && StringUtils.hasLength(lastWeekDay) && weekCount > 0) {
                for (int i = 0; i < weekCount; i++) {
                    int finalI = i;
                    tasks.add(CompletableFuture.supplyAsync(() -> {
                        String[] dates = getBeginEndWeekDates(lastWeekDay, finalI);
                        String beginDate = dates[0];
                        String endDate = dates[1];
                        RankParam param = new RankParam();
                        param.setPageindex(1);
                        param.setPagesize(9999);
                        RankResultDto result = getAllListOnlyByWeek(beginDate, endDate, param);
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

    /**
     * 获取某一周的"周一和周日"的日期
     *
     * @param week 向前或向后推的周数
     * @return
     */
    private String[] getBeginEndWeekDates(String date, int week) {
        String beginDate = "";
        String endDate = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(date, "yyyy-MM-dd"));
        // 将日期减去7天，以便进入上一周
        calendar.add(Calendar.DAY_OF_MONTH, -7 * week);
        // 设置当前日期为本周的周日
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        beginDate = DateUtil.format(calendar.getTime(), "yyyy-MM-dd");
        // 将日期加上6天，得到本周的周日
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        endDate = DateUtil.format(calendar.getTime(), "yyyy-MM-dd");
        return new String[]{beginDate, endDate};
    }


    private void refreshDetailInfo(RankResultDto resultDto) {
        Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(resultDto.getResult().getList().stream().map(x -> Integer.parseInt(x.getSeriesid())).toList());
        resultDto.getResult().getList().forEach(dto -> {
            if (seriesDetailMap.containsKey(dto.getSeriesid())) {
                SeriesDetailDto detailDto = seriesDetailMap.get(dto.getSeriesid());
                dto.setSeriesimage(RankUtil.resizeSeriesImage(detailDto.getPngLogo()));
                dto.setPriceinfo(CommonHelper.priceForamtV2(detailDto.getMinPrice(), detailDto.getMaxPrice()));
                dto.setSeriesname(detailDto.getName());
            }
        });
    }

    /**
     * 删除周榜对应缓存  week格式：2024-06-25
     *
     * @param week
     */
    public void delRedisByWeek(String week) {
        TreeMap<String, Object> makeParam = makeParam(RankUtil.getDataRange(week, week));
        deleteRedis(makeParam);
    }
}
