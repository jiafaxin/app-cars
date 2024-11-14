package com.autohome.app.cars.service.components.recrank.sale;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.rank.NewEnergyDataClient;
import com.autohome.app.cars.apiclient.rank.dtos.NewEnergyRankDto;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecViewEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.NewEnergyPowerConsumptionAndBatteryLifeResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 新能源榜-电耗榜&续航榜
 */
@Component
@Slf4j
public class NewEnergyPowerConsumptionAndBatteryLifeComponent extends RankBaseComponent<NewEnergyPowerConsumptionAndBatteryLifeResultDto> {

    @Resource
    private NewEnergyDataClient newEnergyDataClient;

    @Resource
    private SeriesMapper seriesMapper;
    @Resource
    private SpecMapper specMapper;

    @Resource
    private RankCommonComponent rankCommonComponent;


    private static final String dateParamName = "newEnergyData";

    TreeMap<String, Object> makeParam() {
        return ParamBuilder.create(dateParamName, "redisKey").build();
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    /**
     * 获取所有数据
     *
     * @param param 榜单参数
     * @return 结果
     */
    public RankResultDto getAllData(RankParam param) {
        RankResultDto rankResultDto = new RankResultDto();
        List<RankResultDto.ListDTO> resultList = new ArrayList<>();
        NewEnergyPowerConsumptionAndBatteryLifeResultDto resultDto = getFromRedis(makeParam());
        if (Objects.isNull(resultDto)) {
            // 重新执行更新
            rankResultDto.setMessage("");
            rankResultDto.setReturncode(101);
            log.warn("从Redis中获取数据失败!");
            return rankResultDto;
        }
        List<NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto> dataList = resultDto.getDataList();
        // 是否为续航榜
        boolean isEnduranceRank = param.getSubranktypeid() == 1209;
        rankResultDto.getResult().setSaleranktip(isEnduranceRank ? "* 数据源于官方参数以及之家实测" : "汽车之家实测电耗（kWh/100km）");
        if (Objects.nonNull(dataList) && !dataList.isEmpty()) {
            resortDataList(dataList, param.getSubranktypeid());
            dataList.forEach(data -> resultList.add(transToResult(data, param)));
            rankResultDto.getResult().setList(resultList);
        }
        return rankResultDto;
    }


    public RankResultDto getResultListByCondition(RankParam param) {
        RankResultDto resultDto = getAllData(param);
        // 通过条件筛选
        rankCommonComponent.filterByParam(resultDto, param);
        if (Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().getList().isEmpty()) {
            // 处理当月销量排名
            processRankNum(resultDto);
            rankCommonComponent.pagination(resultDto, param);
            if (CommonHelper.isTakeEffectVersion(param.getPluginversion(), "11.56.0")) {
                // 分享按钮
                rankCommonComponent.addShareBtn(param, resultDto);
            }
        }
        return resultDto;
    }

    private void processRankNum(RankResultDto resultDto) {
        List<RankResultDto.ListDTO> list = resultDto.getResult().getList();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRn(i + 1);
            list.get(i).setRankNum(i + 1);
        }

    }

    /**
     * 根据榜单类型重新拍寻
     *
     * @param dataList      数据List
     * @param subRankTypeId 子榜单类型
     */
    private void resortDataList(List<NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto> dataList, Integer subRankTypeId) {
        switch (subRankTypeId) {
            // 续航榜 续航值倒序
            case 1209 ->
                    dataList.sort(Comparator.comparing(NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto::getEndurance).reversed());
            // 电耗榜 电耗值正序
            case 1210 ->
                    dataList.sort(Comparator.comparing(NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto::getPowerConsumption));
        }
    }

    private RankResultDto.ListDTO transToResult(NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto dataDto, RankParam param) {
        RankResultDto.ListDTO result = new RankResultDto.ListDTO();
        String seriesId = dataDto.getSeriesId().toString();
        // 设置车系详细数据
        result.setSeriesid(seriesId);
        // 获取车系信息
        result.setSeriesimage(dataDto.getSeriesImage());
        result.setSeriesname(dataDto.getSeriesName());
        result.setCardtype(1);
        result.setState(dataDto.getState());
        result.setMinPrice(dataDto.getMinPrice());
        result.setMaxPrice(dataDto.getMaxPrice());
        result.setPriceinfo(CommonHelper.priceForamtV2(dataDto.getMinPrice(), dataDto.getMaxPrice()));
        result.setLevelId(String.valueOf(dataDto.getLevelId()));
        result.setBrandid(dataDto.getBrandId());
        result.setEnergytype(dataDto.getEnergyType());
        result.setManuType(dataDto.getManuType());
        result.setFuelTypes(dataDto.getFuelTypes());
        result.setShowenergyicon(dataDto.getEnergyType());

        result.setIsshowscorevalue(0);
        // result.setSpecname(item.getSpecName());
        result.setRcmdesc(dataDto.getSpecName());
        result.setRcmtext("实测车型");

        result.setRighttextone(StrPool.EMPTY);
        result.setRighttexttwo(StrPool.EMPTY);
        result.setRighttexttwolinkurl(StrPool.EMPTY);
        result.setShowrankchange(0);
//        result.setRankchange(0);
        // 设置PV
        Map<String, String> pvArgs = new HashMap<>();
        pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
        pvArgs.put("rank", StrPool.EMPTY);
        pvArgs.put("typeid", String.valueOf(param.getTypeid()));
        pvArgs.put("seriesid", seriesId);
        result.setRcmlinkurl(String.format("autohome://car/specmain?specid=%d", dataDto.getSpecId()));
        result.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107", seriesId));
        result.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null, "car_rec_main_rank_series_show", null));
        result.setRightinfo(genRankRightInfo(pvArgs, param, dataDto));

        return result;
    }

    private RankResultDto.RightinfoDTO genRankRightInfo(Map<String, String> pvArgs, RankParam param, NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto item) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();
        Integer subRankTypeId = param.getSubranktypeid();
        // 是否为续航榜
        boolean isEnduranceRank = subRankTypeId == 1209;
        String value = isEnduranceRank ? Math.round(item.getEndurance()) + "km": String.format("%.2f", item.getPowerConsumption()) + "kWh";
        rightInfo.setRighttextone(value);
        PvItem pvItem = PvItem.getInstance(pvArgs, "car_rec_main_rank_history_click", null, "", null);
        rightInfo.setPvitem(pvItem);
        return rightInfo;
    }


    public void refreshAll(Consumer<String> log) {
        updateDate(log);
        log.accept("NewEnergyPowerConsumptionAndBatteryLifeComponent refreshAll finished");
    }

    public void updateDate(Consumer<String> log) {
        List<Integer> seriesIdList = seriesMapper.getAllNewEnergySeriesIds();
        Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(seriesIdList);
        NewEnergyPowerConsumptionAndBatteryLifeResultDto resultDto = new NewEnergyPowerConsumptionAndBatteryLifeResultDto();
        List<NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto> dataDtoList = new ArrayList<>(seriesIdList.size());
        loopNewEnergySeries(5, seriesIdList, seriesId -> {
            SeriesDetailDto detail = seriesDetailMap.get(seriesId.toString());
            NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto dataDto = new NewEnergyPowerConsumptionAndBatteryLifeResultDto.RankDataDto();
            dataDto.setSeriesId(seriesId);
            dataDto.setSeriesName(detail.getName());
            dataDto.setSeriesImage(RankUtil.resizeSeriesImage(detail.getPngLogo()));
            dataDto.setLevelId(String.valueOf(detail.getLevelId()));
            dataDto.setState(detail.getState());
            dataDto.setMinPrice(detail.getMinPrice());
            dataDto.setMaxPrice(detail.getMaxPrice());
            CompletableFuture<NewEnergyRankDto> rankNewEnergyInfoFuture = newEnergyDataClient.getRankNewEnergyInfo(seriesId.toString()).thenApply(newEnergyRankDto -> {
                if (Objects.nonNull(newEnergyRankDto) && Objects.nonNull(newEnergyRankDto.getResult()) && !newEnergyRankDto.getResult().isEmpty()) {
                    double endurance;
                    Optional<NewEnergyRankDto.ResultNewEnergyRankBean.EvaluateItemsBean> optional890 = newEnergyRankDto.getResult().get(0).getEvaluateitems().stream().filter(e -> e.getCategoryid() == 890).findFirst();
                    Optional<NewEnergyRankDto.ResultNewEnergyRankBean.EvaluateItemsBean> optional895 = newEnergyRankDto.getResult().get(0).getEvaluateitems().stream().filter(e -> e.getCategoryid() == 895).findFirst();
                    if (optional890.isPresent() && optional895.isPresent()) {
                        endurance = optional890.get().getData() * 0.65 + optional895.get().getData() * 0.35;
                        dataDto.setEndurance(endurance);
                        int specId = newEnergyRankDto.getResult().get(0).getSpecid();
                        // 车型电池容量
                        SpecViewEntity specView = specMapper.getSpecViewBatteryCapacity(specId);
                        if (Objects.nonNull(specView)) {
                            double batteryCapacity = specView.getBatteryCapacity();
                            // 计算电耗
                            dataDto.setSpecId(specView.getSpecId());
                            dataDto.setSpecName(specView.getSpecName());
                            dataDto.setPowerConsumption((batteryCapacity / endurance) * 100);
                            dataDtoList.add(dataDto);
                        }
                    }
                }
                return newEnergyRankDto;
            }).exceptionally(e -> {
                log.accept("查询新能源电耗榜&续航榜数据异常");
                return null;
            });
            // 完成任务
            rankNewEnergyInfoFuture.join();
        }, log);
        log.accept("更新新能源电耗榜&续航榜数据完成,数据量: " + dataDtoList.size());
        resultDto.setDataList(dataDtoList);
        updateRedis(makeParam(), JSONObject.toJSONString(resultDto), true);
    }

}
