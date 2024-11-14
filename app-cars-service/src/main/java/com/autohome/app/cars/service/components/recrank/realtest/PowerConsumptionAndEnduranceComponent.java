package com.autohome.app.cars.service.components.recrank.realtest;

import autohome.rpc.car.app_cars.v1.carext.RankResultResponse;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.rank.RealTestRankClient;
import com.autohome.app.cars.apiclient.rank.dtos.TestedDataRankListDto;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.recrank.dtos.PowerConsumptionAndEnduranceResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RealTestTypeEnum;
import com.autohome.app.cars.service.services.dtos.PvItem;
import com.autohome.app.cars.service.services.rank.RankCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 实测榜数据
 *
 * @author zhangchengtao
 * @date 2024/9/24 16:52
 */
@Slf4j
@Component
public class PowerConsumptionAndEnduranceComponent extends BaseComponent<PowerConsumptionAndEnduranceResultDto> {

    @Resource
    private RealTestRankClient realTestRankClient;


    @Autowired
    private RankCommonService commonService;

    @Autowired
    private SpecDetailComponent specDetailComponent;


    private static final RealTestTypeEnum[] SYNC_RANK_TYPE_ID_LIST = RealTestTypeEnum.values();

    private static final String SERIES_SCHEME = "autohome://car/seriesmain?seriesid=%d&fromtype=107";

    private static final String SPEC_SCHEME = "autohome://car/specmain?specid=%d";

    private static final String TYPE_ID = "typeId";

    TreeMap<String, Object> makeParam(int typeId) {
        return ParamBuilder.create(TYPE_ID, typeId).build();
    }


    public String get(TreeMap<String, Object> params) {
        // 获取UrlEncode后的参数JSON
        String encodedSrt = params.get("param").toString();
        // 进行Decode
        String decode = URLDecoder.decode(encodedSrt, StandardCharsets.UTF_8);
        // 将JSON转为RankParam对象
        RankParam param = JSONObject.parseObject(decode, RankParam.class);
        RankParam.getInstance(param);
        // 调用组件的查询方法
        RankResultDto resultDto = getResultListByCondition(param);
        return JsonUtil.toString(resultDto);
    }

    public List<PowerConsumptionAndEnduranceResultDto.RankDataDto> getDataList(RealTestTypeEnum typeEnum) {
        refreshAll(log::info);
        if (Objects.isNull(typeEnum)) {
            return Collections.emptyList();
        }
        PowerConsumptionAndEnduranceResultDto dataDto = getFromRedis(makeParam(typeEnum.getRankId()));
        if (Objects.nonNull(dataDto) && Objects.nonNull(dataDto.getDataList()) && !dataDto.getDataList().isEmpty()) {
            return dataDto.getDataList();
        } else {
            // 回源数据
            refreshAll(log::info);
        }
        return Collections.emptyList();
    }


    public RankResultDto getResultListByCondition(RankParam param) {
        RealTestTypeEnum typeEnum = RealTestTypeEnum.getByTypeId(param.getSubranktypeid());
        RankResultDto resultDto = new RankResultDto();
        List<PowerConsumptionAndEnduranceResultDto.RankDataDto> dataList = getDataList(typeEnum);

        dataList = filterByParam(dataList, param);
        int skipCount = (param.getPageindex() - 1) * param.getPagesize();
        RankResultDto.ResultDTO result = new RankResultDto.ResultDTO();
        result.setSaleranktip(RealTestTypeEnum.ENDURANCE.equals(typeEnum) ? "* 数据源于官方参数以及之家实测" : "汽车之家实测电耗（kWh/100km）");
        if (param.getPageindex() > 1 && skipCount >= dataList.size()) {
            resultDto.setReturncode(101);
            resultDto.setMessage("超出最大页数");
            resultDto.setResult(new RankResultDto.ResultDTO());
            return resultDto;
        }
        result.setPageindex(param.getPageindex());
        result.setPagesize(param.getPagesize());
        AtomicInteger rank = new AtomicInteger(skipCount + 1);
        dataList.stream().skip(skipCount).limit(param.getPagesize()).forEach(item -> {
            int rankNum = rank.getAndIncrement();
            RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
            dto.setCardtype(1);
            dto.setSeriesid(item.getSeriesId().toString());
            dto.setRankNum(rankNum);
            dto.setEnergytype(0);
            dto.setRighttexttwo("");
            dto.setRighttextone("");
            dto.setRighttexttwolinkurl("");
            dto.setRank(commonService.getRankStr(rankNum));
            dto.setLinkurl(String.format(SERIES_SCHEME, item.getSeriesId()));
            dto.setPriceinfo(CommonHelper.priceForamtV2(item.getMinPrice(), item.getMaxPrice()));
            dto.setSeriesimage(RankUtil.resizeSeriesImage(item.getSeriesImage()));
            dto.setRcmtext("实测车型");
            dto.setRcmdesc(item.getTestSpecName());
            dto.setSeriesname(item.getSeriesName());
            dto.setRcmlinkurl(String.format(SPEC_SCHEME, item.getTestSpecId()));
            Map<String, String> pvArgs = new HashMap<>();
            pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
            pvArgs.put("rank", dto.getRank());
            pvArgs.put("typeid", String.valueOf(param.getTypeid()));
            pvArgs.put("seriesid", item.getSeriesId().toString());
            dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", Collections.emptyMap(), "car_rec_main_rank_series_show", Collections.emptyMap()));
            dto.setRightinfo(genRightInfo(pvArgs, item));
            result.getList().add(dto);
        });
        result.setPagecount(RankUtil.calcPageCount(dataList.size(), param.getPagesize()));
        resultDto.setResult(result);
        commonService.addShareBtn(param, resultDto);
        return resultDto;
    }


    public RankResultResponse getByCondition(RankParam param) {
        RealTestTypeEnum typeEnum = RealTestTypeEnum.getByTypeId(param.getSubranktypeid());
        RankResultResponse.Builder responseBuilder = RankResultResponse.newBuilder();
        RankResultResponse.Result.Builder resultBuilder = RankResultResponse.Result.newBuilder();
        resultBuilder.setSaleranktip(RealTestTypeEnum.ENDURANCE.equals(typeEnum) ? "* 数据源于官方参数以及之家实测" : "汽车之家实测电耗（kWh/100km）");
        List<PowerConsumptionAndEnduranceResultDto.RankDataDto> dataList = getDataList(typeEnum);

        dataList = filterByParam(dataList, param);
        int skipCount = (param.getPageindex() - 1) * param.getPagesize();
        if (param.getPageindex() > 1 && skipCount >= dataList.size()) {
            responseBuilder.setReturnCode(101);
            responseBuilder.setReturnMsg("超出最大页数");
            resultBuilder.addAllList(new ArrayList<>());
            responseBuilder.setResult(resultBuilder);
            return responseBuilder.build();
        }
        resultBuilder.setPageindex(param.getPageindex());
        resultBuilder.setPagesize(param.getPagesize());
        AtomicInteger rank = new AtomicInteger(skipCount + 1);
        dataList.stream().skip(skipCount).limit(param.getPagesize()).forEach(item -> {
            RankResultResponse.Result.RankItemList.Builder builder = RankResultResponse.Result.RankItemList.newBuilder();
            int rankNum = rank.getAndIncrement();
            builder.setCardtype(1);
            builder.setSeriesid(item.getSeriesId().toString());
            builder.setRankNum(rankNum);
            builder.setRank(commonService.getRankStr(rankNum));
            builder.setLinkurl(String.format(SERIES_SCHEME, item.getSeriesId()));
            builder.setPriceinfo(CommonHelper.priceForamtV2(item.getMinPrice(), item.getMaxPrice()));
            builder.setSeriesimage(RankUtil.resizeSeriesImage(item.getSeriesImage()));
            builder.setRcmtext("实测车型");
            builder.setRcmdesc(item.getTestSpecName());
            builder.setSeriesname(item.getSeriesName());
            builder.setRcmlinkurl(String.format(SPEC_SCHEME, item.getTestSpecId()));
            Map<String, String> pvArgs = new HashMap<>();
            pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
            pvArgs.put("rank", builder.getRank());
            pvArgs.put("typeid", String.valueOf(param.getTypeid()));
            pvArgs.put("seriesid", item.getSeriesId().toString());
            builder.setPvitem(RankUtil.genPvItem(pvArgs, "car_rec_main_rank_series_click", "car_rec_main_rank_series_show"));
            builder.setRightinfo(genRightInfoBuilder(pvArgs, item));
            commonService.addShareBtn(param, builder);
            resultBuilder.addList(builder);
        });
        resultBuilder.setPagecount(RankUtil.calcPageCount(dataList.size(), param.getPagesize()));
        responseBuilder.setResult(resultBuilder);
        return responseBuilder.build();
    }


    /**
     * 生成销量月榜rightInfo
     *
     * @param dto     数据dto
     * @param argsMap 参数
     * @return RankResultDto.RightinfoDTO
     */
    private RankResultResponse.Result.RankItemList.Rightinfo.Builder genRightInfoBuilder(Map<String, String> argsMap, PowerConsumptionAndEnduranceResultDto.RankDataDto dto) {
        RankResultResponse.Result.RankItemList.Rightinfo.Builder rightInfoBuilder = RankResultResponse.Result.RankItemList.Rightinfo.newBuilder();
        rightInfoBuilder.setRighttextone(dto.getShowValue() + dto.getUnit());
        rightInfoBuilder.setPvitem(RankUtil.genPvItem(argsMap, "car_rec_main_rank_history_click", ""));
        return rightInfoBuilder;
    }


    /**
     * 生成销量月榜rightInfo
     *
     * @param dto     数据dto
     * @param argsMap 参数
     * @return RankResultDto.RightinfoDTO
     */
    private RankResultDto.RightinfoDTO genRightInfo(Map<String, String> argsMap, PowerConsumptionAndEnduranceResultDto.RankDataDto dto) {
        RankResultDto.RightinfoDTO rightinfoDTO = new RankResultDto.RightinfoDTO();
        rightinfoDTO.setRighttextone(dto.getShowValue() + dto.getUnit());
        rightinfoDTO.setPvitem(PvItem.getInstance(argsMap, "car_rec_main_rank_history_click", Collections.emptyMap(), StrPool.EMPTY, Collections.emptyMap()));
        return rightinfoDTO;
    }


    public void refreshAll(Consumer<String> logInfo) {

        for (RealTestTypeEnum typeEnum : SYNC_RANK_TYPE_ID_LIST) {
            PowerConsumptionAndEnduranceResultDto resultDto = new PowerConsumptionAndEnduranceResultDto();
            realTestRankClient.getRealTestData(119, typeEnum.getRankId(), "4,5,6").thenAccept(result -> {
                if (Objects.nonNull(result) && Objects.nonNull(result.getResult()) && Objects.nonNull(result.getResult().getList()) && !result.getResult().getList().isEmpty()) {
                    List<TestedDataRankListDto.ListBean> list = result.getResult().getList();
                    List<Integer> specIdList = list.stream().map(TestedDataRankListDto.ListBean::getSpecid).toList();
                    CompletableFuture<List<SpecDetailDto>> specDetailListFuture = specDetailComponent.getList(specIdList);
                    List<SpecDetailDto> specDetailList = specDetailListFuture.join();
                    // 过滤停售车型
                    Map<Integer, Integer> stopSaleSpecMap = specDetailList.stream().filter(x -> x.getState() == 40).collect(Collectors.toMap(SpecDetailDto::getSpecId, SpecDetailDto::getState));

                    List<Integer> seriesIdList = list.stream().filter(x -> !stopSaleSpecMap.containsKey(x.getSpecid())).map(TestedDataRankListDto.ListBean::getSeriesid).toList();
                    if (typeEnum == RealTestTypeEnum.ENDURANCE) {
                        list.forEach(item -> item.setValue(NumberUtils.toFloat(item.getShowvalue(), 0.0f)));
                        Collections.reverse(list);
                        list.sort(Comparator.comparingDouble(TestedDataRankListDto.ListBean::getValue).reversed());
                    }
                    Map<String, SeriesDetailDto> seriesDetailMap = commonService.getSeriesDetailMap(seriesIdList);
                    list.forEach(item -> {
                        SeriesDetailDto detailDto = seriesDetailMap.get(String.valueOf(item.getSeriesid()));
                        if (Objects.nonNull(detailDto) && (detailDto.getState() == 20 || detailDto.getState() == 30)) {
                            PowerConsumptionAndEnduranceResultDto.RankDataDto rankDataDto = new PowerConsumptionAndEnduranceResultDto.RankDataDto();
                            rankDataDto.setSeriesId(item.getSeriesid());
                            rankDataDto.setSeriesName(item.getSeriesname());
                            rankDataDto.setSeriesImage(item.getSeriespnglogo());
                            rankDataDto.setMinPrice(item.getSeriesminprice());
                            rankDataDto.setMaxPrice(item.getSeriesmaxprice());
                            rankDataDto.setRn(item.getRank());
                            rankDataDto.setRnNum(item.getRank());
                            rankDataDto.setBrandId(item.getBrandid());
                            rankDataDto.setLevelId(String.valueOf(detailDto.getLevelId()));
                            rankDataDto.setState(detailDto.getState());
                            rankDataDto.setShowValue(item.getShowvalue());
                            rankDataDto.setUnit(item.getUnit());
                            rankDataDto.setTestSpecName(item.getSpecname());
                            rankDataDto.setTestSpecId(item.getSpecid());
                            resultDto.getDataList().add(rankDataDto);
                        }
                    });
                    updateRedis(makeParam(typeEnum.getRankId()), JSONObject.toJSONString(resultDto), true);
                    logInfo.accept(typeEnum.getRankName() + "数据刷新完成");
                }
            }).exceptionally(e -> {
                logInfo.accept("查询实测榜-失败" + e.getMessage());
                log.warn("查询实测榜-失败", e);
                return null;
            }).join();

        }
    }


    /**
     * 按条件筛选
     *
     * @param list  结果集
     * @param param 查询条件
     */
    public List<PowerConsumptionAndEnduranceResultDto.RankDataDto> filterByParam(List<PowerConsumptionAndEnduranceResultDto.RankDataDto> list, RankParam param) {
        if (!list.isEmpty()) {
            // 通过过滤条件刷新数据
            List<String> levelIdList;
            if (StringUtils.hasLength(param.getLevelid())) {
                String[] levelIdArr = param.getLevelid().split(StrPool.COMMA);
                levelIdList = Arrays.asList(levelIdArr);
            } else {
                levelIdList = Collections.emptyList();
            }
            return list.stream().filter(x -> {
                if (StringUtils.hasLength(param.getLevelid()) && !levelIdList.contains(x.getLevelId())) {
                    return false;
                }
                // 筛选在售状态
                return param.getIssale() != 1 || RankConstant.ON_SALE_STATE_LIST.contains(x.getState());
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
