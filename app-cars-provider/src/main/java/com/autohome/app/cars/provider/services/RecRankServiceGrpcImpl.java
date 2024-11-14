package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carext.*;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.enums.RankLanTypeEnum;
import com.autohome.app.cars.provider.config.SimplifyJson;
import com.autohome.app.cars.service.components.recrank.dtos.RankHistoryResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.newenergy.NewEnergyComponent;
import com.autohome.app.cars.service.components.recrank.realtest.PowerConsumptionAndEnduranceComponent;
import com.autohome.app.cars.service.components.recrank.sale.history.RankHistoryComponent;
import com.autohome.app.cars.service.services.RecRankService;
import com.autohome.app.cars.service.services.rank.LanApiRankService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@DubboService(timeout = -1)
@RestController
@Slf4j
public class RecRankServiceGrpcImpl extends DubboRecRankServiceTriple.RecRankServiceImplBase {

    @Autowired
    private RecRankService recRankService;
    @Autowired
    private NewEnergyComponent newEnergyComponent;

    @Resource
    private RankHistoryComponent historyComponent;
    @Autowired
    private LanApiRankService lanApiRankService;

    /**
     * 询价按钮开关
     */
    @Value("${rank_list_ask_show_switch:}")
    private String showAskBtnSwitch;

    @GetMapping(value = "/carext/recrank/attention/newcar/trend", produces = "application/json;charset=utf-8")
    @Override
    public AttentionNewCarRankTrendResponse getAttentionNewCarRankTrend(AttentionNewCarRankTrendRequest request) {
        AttentionNewCarRankTrendResponse.Result.Builder result =
                recRankService.getAttentionNewCarTrend(request.getSeriesid());
        if (result == null) {
            return AttentionNewCarRankTrendResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("新车关注度排名趋势无数据")
                    .build();
        }
        return AttentionNewCarRankTrendResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("success")
                .setResult(result)
                .build();
    }

//    @GetMapping(value = "/carext/recrank/newenergy/driverangecost", produces = "application/json;charset=utf-8")
//    public String getDriverangecost() {
//        final CompletableFuture<DriveRangeCostListDto> list = newEnergyComponent.getList("0-9000", 1, "4", 0, 1, 110100, 1, 20, "", 1);
//
//
//        return JsonUtil.toString(list.join());
//    }


    @Override
    @GetMapping(value = "/carext/recrank/all/getrecranklistpageresult2_v2", produces = "application/json;charset=utf-8")
    public RankResultResponse getRankListResult(RankResultRequest request) {
        RankParam param = RankParam.getInstance(request);
        checkShowAskPriceBtn(param, "rec");
        RankResultResponse.Result.Builder resultBuilder = RankResultResponse.Result.newBuilder();
        RankResultDto resultDto = null;
        switch (request.getTypeid()) {
            case 1 -> {
                switch (param.getSubranktypeid()) {
                    // 月销榜
                    case 1 ->  resultDto = recRankService.getMonthSaleRankResult(param);
                    // 周销榜
                    case 2 -> resultDto = recRankService.getWeekSaleRankResult(param);
                    // 品牌月榜
                    case 3 -> resultDto = recRankService.getBrandMonthRank(param);
                    // 城市榜
                    case 4 -> resultDto = recRankService.getCitySaleRankResult(param);
                    // 品牌周榜
                    case 5 -> resultDto = recRankService.getBrandWeekRank(param);
                }
                return genSaleRankResponse(resultDto, param, resultBuilder);
            }
            case 2 -> {
                // 关注榜
                switch (param.getSubranktypeid()) {
                    // 全部关注榜
                    case 2001 -> resultDto = recRankService.getAreaSeriesAttRankResult(param);
                    // 新车关注榜
                    case 2002 -> resultDto = recRankService.getAttentionRankNewCarList(param);
                }
                return genSaleRankResponse(resultDto, param, resultBuilder);
            }
            case 9 -> {
                switch (param.getSubranktypeid()) {
                    //续航榜 , 电耗榜
                    case 1209, 1210 -> {
                        return recRankService.getNewEnergyPowerConsumptionAndEnduranceComponentRank(param);
                    }
                        // 新势力热度榜
                    case 1211-> {
                        resultDto = recRankService.getNewPowerHotRank(param);
                        return genSaleRankResponse(resultDto, param, resultBuilder);
                    }
                    // 新能源月榜
                    case 2305-> {
                        resultDto = recRankService.getMonthSaleRankResult(param);
                        return genSaleRankResponse(resultDto, param, resultBuilder);
                    }
                    // 新能源周榜
                    case 2306-> {
                        resultDto = recRankService.getWeekSaleRankResult(param);
                        return genSaleRankResponse(resultDto, param, resultBuilder);
                    }
                    // 品牌月榜
                    case 2307-> {
                        resultDto = recRankService.getBrandMonthRank(param);
                        return genSaleRankResponse(resultDto, param, resultBuilder);
                    }
                    // 品牌周榜
                    case 2308 -> {
                        resultDto = recRankService.getBrandWeekRank(param);
                        return genSaleRankResponse(resultDto, param, resultBuilder);
                    }
                }
            }
            case 3 -> {
                //降价榜
                resultDto = recRankService.getDiscountRank(param);
                return genSaleRankResponse(resultDto, param, resultBuilder);
            }
            case 6 -> {
                //保值榜
                resultDto = recRankService.getHedgeRank(param);
                return genSaleRankResponse(resultDto, param, resultBuilder);
            }
            case 4 -> {
                //口碑榜
                resultDto = recRankService.getKoubeiRankList(param);
                return genSaleRankResponse(resultDto, param, resultBuilder);
            }
        }
        return RankResultResponse.newBuilder().build();
    }

    /**
     * 销量趋势接口
     * @param request 请求参数
     * @return RankHistoryResponse
     */
    @Override
    @GetMapping(value = "/carext/searchcar/getseriessalehistory", produces = "application/json;charset=utf-8")
    public RankHistoryResponse getRankHistory(RankHistoryRequest request) {
        RankHistoryResultDto rankHistory = historyComponent.get(request.getTypeid(), request.getSubranktypeid(), request.getSeriesid(), request.getEnergytype(), request.getDate(),request.getPluginversion());
        RankHistoryResponse.Builder builder = RankHistoryResponse.newBuilder();
        if (rankHistory != null && !rankHistory.getResult().getMonthsale().isEmpty()) {
            try {
                JsonFormat.parser().ignoringUnknownFields().merge(JSONObject.toJSONString(rankHistory), builder);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }
    /**
     * 检查是否需要展示询价按钮
     *
     * @param param 参数
     * @param key   key
     */
    public void checkShowAskPriceBtn(RankParam param, String key) {
        boolean showAskPriceBtn = false;
        //{"startup": true, "rec": true}
        try {
            if (StringUtils.hasLength(showAskBtnSwitch)) {
                JSONObject switchJson = JSONObject.parseObject(showAskBtnSwitch);
                if ((switchJson.containsKey(key) && switchJson.getBoolean(key)) || (param.getTypeid() == 18 && switchJson.containsKey("supertest") && switchJson.getBoolean("supertest"))) {
                    showAskPriceBtn = true;
                }
            }
        } catch (Exception e) {
            log.error("checkShowAskPriceBtn error", e);
        }

        param.setShowAskPriceBtn(showAskPriceBtn);
    }

    private RankResultResponse genErrorResponse() {
        return RankResultResponse.newBuilder()
                .setReturnMsg("没有数据")
                .setReturnCode(101).build();
    }

    private RankResultResponse genSaleRankResponse(RankResultDto resultDto, RankParam param, RankResultResponse.Result.Builder resultBuilder) {
        if (Objects.isNull(resultDto)) {
            return genErrorResponse();
        }
        RankResultDto.ResultDTO result = resultDto.getResult();
        resultBuilder.setPagecount(result.getPagecount());
        resultBuilder.setPageindex(result.getPageindex());
        resultBuilder.setPagesize(result.getPagesize());
        if (StringUtils.hasLength(result.getMorescheme())) {
            resultBuilder.setMorescheme(result.getMorescheme());
        }
        if (StringUtils.hasLength(result.getSaleranktip())) {
            resultBuilder.setSaleranktip(result.getSaleranktip());
        }
        resultBuilder.setScenesubtitle(result.getScenesubtitle());
        resultBuilder.setScenetitle(result.getScenetitle());
        RankResultResponse.Result.Shareinfo.Builder shareInfoBuilder = RankResultResponse.Result.Shareinfo.newBuilder();
        if (Objects.nonNull(result.getShareinfo())) {
            try {
                JsonFormat.parser().merge(JSONObject.toJSONString(result.getShareinfo()), shareInfoBuilder);
                resultBuilder.setShareinfo(shareInfoBuilder);
            } catch (InvalidProtocolBufferException e) {
                log.warn("销量榜Message转换错误", e);
            }
        }
        List<RankResultDto.ListDTO> list = resultDto.getResult().getList();

        for (RankResultDto.ListDTO item : list) {
            RankResultResponse.Result.RankItemList.Builder itemBuilder = RankResultResponse.Result.RankItemList.newBuilder();
            try {
                JsonFormat.parser().ignoringUnknownFields().merge(JSONObject.toJSONString(item), itemBuilder);
            } catch (InvalidProtocolBufferException e) {
                log.warn("销量榜Message转换错误");
            }
            resultBuilder.addList(itemBuilder);
        }
        return RankResultResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("SUCCESS")
                .setCacheable(1)
                .setCdncachesecond(0)
                .setResult(resultBuilder).build();
    }

    /**
     * 刷新销量榜月、周销量数据-只支持全量刷新
     *
     * @param request
     * @return
     */
    @Override
    @GetMapping(value = "/carext/recrank/refreshranksalemonthandweek", produces = "application/json;charset=utf-8")
    public RankSaleRefreshResponse refreshMonthAndWeekRank(RankSaleRefreshRequest request) {
        return recRankService.refreshMonthAndWeekRank(request);
    }


    /**
     * <a href="https://doc.autohome.com.cn/docapi/page/share/share_yZ3VvXSEAC">需求地址</a>
     * <a href="https://zhishi.autohome.com.cn/home/teamplace/file?targetId=yd2jSHgMfw">接口文档</a>
     * @param request 请求
     * @return 对应榜单响应
     */
    @Override
    @GetMapping(value = "/lanapi/rank/getranklist", produces = "application/json;charset=utf-8")
    @SimplifyJson
    public RankLanApiResponse getRankLanResult(RankLanApiRequest request) {
        RankLanTypeEnum lanTypeEnum = RankLanTypeEnum.getByTypeId(request.getTypeid());
        if (Objects.nonNull(lanTypeEnum)) {
            return lanApiRankService.getRankLanResult(request, lanTypeEnum);
        }
        return RankLanApiResponse.newBuilder()
                .setReturnCode(101)
                .setReturnMsg("非法参数")
                .setResult(RankLanApiResponse.RankLanApiResult.newBuilder().build())
                .build();
    }
}