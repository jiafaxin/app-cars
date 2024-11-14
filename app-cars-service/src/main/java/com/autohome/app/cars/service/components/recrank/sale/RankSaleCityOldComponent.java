package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleCitySourceEntity;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.CityRankDataResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 城市榜组件
 */
@Component
@Slf4j
public class RankSaleCityOldComponent extends RankBaseComponent<CityRankDataResultDto> {

    @Resource
    private RankCommonComponent rankCommonComponent;

    @Resource
    private RankSaleCityOldSourceComponent sourceComponent;

    final static String dateParamName = "date";
    final static String cityParamName = "cityId";


    public TreeMap<String, Object> makeParam(String date, Integer cityId) {
        return ParamBuilder.create(dateParamName, date).add(cityParamName, cityId).build();
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    /**
     * 获取城市榜数据List
     *
     * @param beginMonth 开始月份
     * @param endMonth   结束月份
     * @param cityId     城市ID
     * @return 销量数据
     */
    public List<CityRankDataResultDto.RankDataDto> getDataList(String beginMonth, String endMonth, int cityId) {
        TreeMap<String, Object> makeParam = makeParam(RankUtil.getDataRange(beginMonth, endMonth), cityId);
        // 先从Redis中获取
        CityRankDataResultDto baseDataResult = new CityRankDataResultDto();
        try {
            CityRankDataResultDto fromRedis = getFromRedis(makeParam);
            if (Objects.nonNull(fromRedis)) {
                baseDataResult = fromRedis;
            }
        } catch (Exception e) {
            log.warn("从Redis中获取城市销量榜失败!", e);
            deleteRedis(makeParam);
        }

        List<CityRankDataResultDto.RankDataDto> dataList = Collections.emptyList();
        if (Objects.nonNull(baseDataResult.getDataList()) && !baseDataResult.getDataList().isEmpty()) {
            dataList = baseDataResult.getDataList();
        } else {
            // Redis中不存在则从数据库中查询
            CityRankDataResultDto resultFromDB = getDataFromDB(beginMonth, endMonth, cityId, 10000);
            if (Objects.nonNull(resultFromDB.getDataList()) && !resultFromDB.getDataList().isEmpty()) {
                dataList = resultFromDB.getDataList();
                baseDataResult.setDataList(dataList);
                try {
                    String cacheStr = RankUtil.serializeObject(dataList, CityRankDataResultDto.RankDataDto.class);
                    updateRedis(makeParam, cacheStr, true);
                } catch (Exception e) {
                    log.warn("城市榜-写入Redis失败", e);
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
        // 计算上个月的日期
        String preBeginMonth = rankCommonComponent.getDateOffset(param.getBeginMonth(), RankConstant.MONTH_DATE_FORMAT, Calendar.MONTH, -1);
        // 获取时间范围内的销量数据
        CompletableFuture<RankResultDto> curRankFuture = CompletableFuture.supplyAsync(() -> {
            RankResultDto resultDto = getAllListByMonthAndCity(param.getBeginMonth(), param.getEndMonth(), param);
            // 通过条件筛选
            rankCommonComponent.filterByParam(resultDto, param);
            if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().getList().isEmpty()) {
                // 处理当月销量排名
                rankCommonComponent.processRankNum(resultDto);
            }
            return resultDto;
        }).exceptionally(e -> {
            log.warn("查询城市销量数据出错!", e);
            return null;
        });
        CompletableFuture<RankResultDto> preRankFuture = CompletableFuture.supplyAsync(() -> {
            /*
             * 当前此处计算上月销量的方式为: 如当前查询销量范围为 2024/02-2014-04 上月排名取值的范围是 2024-01 月份的排名
             * 若后期改为同等时间段排名重新计算 perEndMonth即可
             */
            RankResultDto preResultDto = getAllListByMonthAndCity(preBeginMonth, preBeginMonth, param);
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
        return result;
    }

    /**
     * 获取时间段内未经过滤条件过滤的销量数据
     *
     * @param beginMonth 开始月份
     * @param endMonth   结束月份
     * @param param      查询参数
     * @return 销量数据
     */
    public RankResultDto getAllListByMonthAndCity(String beginMonth, String endMonth, RankParam param) {
        RankResultDto result = new RankResultDto();
        List<RankResultDto.ListDTO> resultList = new ArrayList<>();
        List<CityRankDataResultDto.RankDataDto> dataList = getDataList(beginMonth, endMonth, param.getCityid());
        if (!dataList.isEmpty()) {
            // 重新排序
            // rankCommonComponent.reSort(param.getEnergytype(), dataList);
            // 处理数据Dto转为resultDto
            dataList.forEach(data -> resultList.add(transToResult(data, param)));
        }
        result.getResult().setSaleranktip("数据源于终端量，每月20日左右更新上月数据");
        result.getResult().setScenetitle("全车型销量总榜");
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        //result.getResult().setShareinfo(rankCommonComponent.getShareInfo());
        result.getResult().setScenesubtitle(rankCommonComponent.genSceneSubTitle(param));
        result.getResult().setPageindex(param.getPageindex());
        result.getResult().setPagesize(param.getPagesize());
        result.getResult().setList(resultList);
        // 更新后写入Redis
        return result;

    }

    private RankResultDto.ListDTO transToResult(CityRankDataResultDto.RankDataDto data, RankParam param) {
        RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
        String seriesId = data.getSeriesId().toString();
        // 设置车系详细数据
        dto.setSeriesid(seriesId);
        // rankCommonComponent.setEnergyTypeSaleDetail(param.getEnergytype(), data, dto);
        dto.setSalecount(data.getSaleCount());
        // 获取车系信息
        dto.setSeriesimage(RankUtil.resizeSeriesImage(data.getSeriesImage()));
        dto.setSeriesname(data.getSeriesName());
        dto.setMinPrice(data.getMinPrice());
        dto.setMaxPrice(data.getMaxPrice());
        if (data.getMinPrice() >= 0 && data.getMaxPrice() > data.getMinPrice()) {
            dto.setPriceinfo(CommonHelper.priceForamtV2(data.getMinPrice(), data.getMaxPrice()));
        } else {
            dto.setPriceinfo("暂无价格");
        }
        dto.setLevelId(String.valueOf(data.getLevelId()));
        dto.setBrandid(data.getBrandId());
        dto.setEnergytype(data.getEnergyType());
        dto.setManuType(data.getManuType());
        dto.setFuelTypes(data.getFuelTypes());
        dto.setShowenergyicon(data.getEnergyType());
        // 获取口碑分
        if (StringUtils.hasLength(data.getScoreValue())) {
            dto.setScorevalue(data.getScoreValue());
        } else {
            dto.setScorevalue("暂无");
        }
        dto.setScoretip("分");
        dto.setCardtype(2);
        dto.setIsshowscorevalue(1);
        dto.setSpecname(null);
        dto.setRcmtext(null);
        dto.setRighttextone(null);
        dto.setRighttexttwo(null);
        dto.setRighttexttwolinkurl(null);

        // 筛选第三行能源类型时, 因为在本地做了过滤, 所以不展示排名变化
        dto.setShowrankchange(1);
        dto.setRankchange(data.getPreRankNum() - data.getRn());
        // 设置PV
        Map<String, String> pvArgs = new HashMap<>(3);
        // pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
        pvArgs.put("rank", StrPool.EMPTY);
        pvArgs.put("typeid", String.valueOf(param.getTypeid()));
        pvArgs.put("seriesid", seriesId);

        dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107", seriesId));
        dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null, "car_rec_main_rank_series_show", null));
        dto.setRightinfo(genCityRankRightInfo(data));
        return dto;
    }


    private RankResultDto.RightinfoDTO genCityRankRightInfo(CityRankDataResultDto.RankDataDto data) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();

        rightInfo.setRighttextone(String.valueOf(data.getSaleCount()));
        rightInfo.setRighttexttwo("本地销量");
        rightInfo.setExt("");
        rightInfo.setRightpricetitle(StrPool.EMPTY);
        rightInfo.setRightpriceurl(StrPool.EMPTY);
        RankResultDto.RightinfoDTO.PriceInfoDto priceInfo = new RankResultDto.RightinfoDTO.PriceInfoDto();
        priceInfo.setExt(StrPool.EMPTY);
        priceInfo.setEid(StrPool.EMPTY);
        priceInfo.setTitle(StrPool.EMPTY);
        priceInfo.setLinkurl(StrPool.EMPTY);
        rightInfo.setPriceinfo(priceInfo);
        return rightInfo;
    }

    private CityRankDataResultDto getDataFromDB(String beginMonth, String endMonth, Integer cityId, int size) {
        List<RankSaleCitySourceEntity> dataList = sourceComponent.getListByCondition(beginMonth, endMonth, cityId, size);
        CityRankDataResultDto result = new CityRankDataResultDto();
        if (Objects.nonNull(dataList) && !dataList.isEmpty()) {
            List<CityRankDataResultDto.RankDataDto> resultList = new ArrayList<>(dataList.size());
            List<Integer> seriesIdList = dataList.stream().map(x -> Integer.parseInt(x.getSeriesid())).collect(Collectors.toList());
            // 获取车系信息Map
            Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(seriesIdList);
            // 获取口碑分Map
            Map<String, String> kouBeiScoreMap = rankCommonComponent.getKouBeiScoreMap(seriesIdList);

            for (RankSaleCitySourceEntity item : dataList) {
                CityRankDataResultDto.RankDataDto dataDto = new CityRankDataResultDto.RankDataDto();
                String seriesId = item.getSeriesid();
                dataDto.setSeriesId(Integer.parseInt(seriesId));
                dataDto.setSaleCount(item.getSalecnt());
                // rankCommonComponent.fillEnergyTypeSaleCount(dataDto, item.getEnergy_sale_count());
                if (seriesDetailMap.containsKey(seriesId)) {
                    // 获取车系信息
                    SeriesDetailDto detail = seriesDetailMap.get(seriesId);
                    dataDto.setSeriesImage(detail.getPngLogo());
                    dataDto.setSeriesName(detail.getName());
                    dataDto.setMinPrice(Math.toIntExact(item.getMin_guidance_price()));
                    dataDto.setMaxPrice(Math.toIntExact(item.getMax_guidance_price()));
                    dataDto.setLevelId(String.valueOf(detail.getLevelId()));
                    dataDto.setBrandId(detail.getBrandId());
                    dataDto.setEnergyType(detail.getEnergytype());
                    dataDto.setManuType(item.getManu_type());
                    dataDto.setFuelTypes(detail.getFueltypes());
                    dataDto.setMonth(item.getMonth());
                    dataDto.setCityId(item.getCityid());
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

    public List<RankSaleCitySourceEntity> getAllCityByMonth(String month, int isNewEnergy) {
        return sourceComponent.getAllCityByMonth(month, isNewEnergy);
    }

    @Override
    public CityRankDataResultDto getFromRedis(TreeMap<String, Object> params) {
        String key = getKey(params);
        String cacheStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(cacheStr)) {
            CityRankDataResultDto result = new CityRankDataResultDto();
            List<CityRankDataResultDto.RankDataDto> dataDtoList = RankUtil.deserializeObject(cacheStr, CityRankDataResultDto.RankDataDto.class);
            if (!dataDtoList.isEmpty()) {
                result.setDataList(dataDtoList);
                return result;
            }
        }
        return null;
    }

    public void refreshCache(int totalMinutes, List<String[]> dateList) {
        List<Integer> allCityIds = CityUtil.getAllCityIds();
        int duration = totalMinutes * 60000 / (allCityIds.size() * dateList.size());
        allCityIds.forEach(cityId -> dateList.forEach(dates -> {
            CompletableFuture.runAsync(() -> {
                CityRankDataResultDto dataFromDB = getDataFromDB(dates[0], dates[1], cityId, 1000);
                if (Objects.nonNull(dataFromDB.getDataList()) && !dataFromDB.getDataList().isEmpty()) {
                    List<CityRankDataResultDto.RankDataDto> dataList = dataFromDB.getDataList();
                    try {
                        String cacheStr = RankUtil.serializeObject(dataList, CityRankDataResultDto.RankDataDto.class);
                        TreeMap<String, Object> makeParam = makeParam(RankUtil.getDataRange(dates[0], dates[1]), cityId);
                        updateRedis(makeParam, cacheStr, true);
                    } catch (Exception e) {
                        log.warn("[城市榜]-写入Redis失败", e);
                    }
                }
            });
            ThreadUtil.sleep(duration);
        }));
    }

}
