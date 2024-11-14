package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carext.AttentionNewCarRankTrendResponse;
import autohome.rpc.car.app_cars.v1.carext.RankResultResponse;
import autohome.rpc.car.app_cars.v1.carext.RankSaleRefreshRequest;
import autohome.rpc.car.app_cars.v1.carext.RankSaleRefreshResponse;
import com.autohome.app.cars.apiclient.dingding.DingDingApiClient;
import com.autohome.app.cars.apiclient.dingding.dtos.DingDingApiResult;
import com.autohome.app.cars.apiclient.dingding.dtos.DingDingMessageParam;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.recrank.attention.AreaSeriesAttentionComponent;
import com.autohome.app.cars.service.components.recrank.attention.AttentionNewCarListComponent;
import com.autohome.app.cars.service.components.recrank.attention.AttentionNewCarTrendComponent;
import com.autohome.app.cars.service.components.recrank.attention.DtNewCarAttentionComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.discount.DiscountComponent;
import com.autohome.app.cars.service.components.recrank.dtos.AttentionNewCarTrendDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.hedge.HedgeComponent;
import com.autohome.app.cars.service.components.recrank.koubei.KoubeiRankComponent;
import com.autohome.app.cars.service.components.recrank.realtest.PowerConsumptionAndEnduranceComponent;
import com.autohome.app.cars.service.components.recrank.sale.*;
import com.autohome.app.cars.service.components.recrank.sale.history.RankHistoryComponent;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@Service
@Slf4j
public class RecRankService {

    @Autowired
    private AttentionNewCarTrendComponent attentionNewCarTrendComponent;

    @Autowired
    private AttentionNewCarListComponent attentionNewCarListComponent;

    @Autowired
    private DtNewCarAttentionComponent dtNewCarAttentionComponent;

    /**
     * 销量月榜
     */
    @Resource
    private RankSaleMonthComponent rankSaleMonthComponent;

    /**
     * 销量周榜
     */
    @Resource
    private RankSaleWeekComponent rankSaleWeekComponent;

    @Resource
    private RankSaleCityComponent rankSaleCityComponent;
    /**
     * 新能源-新势力热度榜
     */
    @Resource
    private RankNewEnergyNewPowerHotComponent rankNewEnergyNewPowerHotComponent;
    /**
     * 电耗榜&续航榜
     */
    @Resource
    private NewEnergyPowerConsumptionAndBatteryLifeComponent newEnergyPowerConsumptionAndBatteryLifeComponent;

    @Autowired
    private PowerConsumptionAndEnduranceComponent powerConsumptionAndEnduranceComponent;

    @Autowired
    private DiscountComponent discountComponent;

    @Autowired
    private HedgeComponent hedgeComponent;

    @Resource
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Resource
    private RankBrandMonthComponent brandMonthComponent;

    @Autowired
    private AreaSeriesAttentionComponent areaSeriesAttentionComponent;

    @Autowired
    private KoubeiRankComponent koubeiRankComponent;

    @Autowired
    private RankHistoryComponent rankHistoryComponent;

    @Resource
    private DingDingApiClient dingDingApiClient;

    /**
     * 销量月榜、周榜更新发送钉钉通知人
     */
    @Value("${rank_sale_refresh_dingding:}")
    private String rankSaleRefreshDingDing;

    public AttentionNewCarRankTrendResponse.Result.Builder getAttentionNewCarTrend(int seriesId) {
        try {
            AttentionNewCarTrendDto trendDto = attentionNewCarTrendComponent.get(seriesId).join();
            if (trendDto == null) {
                log.error("新车关注度排名趋势无法取到数据");
                return null;
            }
            AttentionNewCarRankTrendResponse.Result.Builder trendRespBuilder =
                    AttentionNewCarRankTrendResponse.Result.newBuilder();
            trendRespBuilder.setSeriesid(trendDto.getSeriesId());
            trendRespBuilder.setSeriesname(trendDto.getSeriesName());
            trendRespBuilder.setTitle(trendDto.getTitle());
            trendRespBuilder.setMax(trendDto.getMax());
            trendRespBuilder.setChartcolnum(trendDto.getChartColNum());
            List<AttentionNewCarRankTrendResponse.Result.TrendDto> trendDtoList = new ArrayList<>();
            trendDto.getTrendList().forEach(e -> {
                AttentionNewCarRankTrendResponse.Result.TrendDto.Builder trendDtoBuilder =
                        AttentionNewCarRankTrendResponse.Result.TrendDto.newBuilder();
                trendDtoBuilder.setRank(e.getRank());
                trendDtoBuilder.setRanknum(e.getRankNum());
                trendDtoBuilder.setAttnum(e.getAttNum());
                trendDtoList.add(trendDtoBuilder.build());
            });
            trendRespBuilder.addAllTrendlist(trendDtoList);
            List<AttentionNewCarRankTrendResponse.Result.MonthDayDto> monthDayDtoList = new ArrayList<>();
            trendDto.getMonth().forEach(e -> {
                AttentionNewCarRankTrendResponse.Result.MonthDayDto.Builder monthDayDtoBuilder =
                        AttentionNewCarRankTrendResponse.Result.MonthDayDto.newBuilder();
                monthDayDtoBuilder.setDate(e.getDate().toString());
                monthDayDtoBuilder.setDateformatstr(e.getDateFormatStr());
                monthDayDtoList.add(monthDayDtoBuilder.build());
            });
            trendRespBuilder.addAllMonth(monthDayDtoList);
            return trendRespBuilder;
        } catch (Exception e) {
            log.error("新车关注度排名趋势组件构建resultBuilder异常, seriesId={}", seriesId, e);
        }
        return null;
    }

    public void updateAttentionNewCarData(Consumer<String> logInfo, boolean async) {
        attentionNewCarListComponent.updateAttentionNewCarDataFlag();
        CompletableFuture<Void> refreshFutureOld = CompletableFuture.runAsync(
                () -> attentionNewCarTrendComponent.refreshAll(logInfo), ThreadPoolUtils.defaultThreadPoolExecutor);
        CompletableFuture<Void> refreshFutureNew = CompletableFuture.runAsync(
                () -> dtNewCarAttentionComponent.refreshAll(logInfo), ThreadPoolUtils.defaultThreadPoolExecutor);
        if (!async) {
            CompletableFuture.allOf(refreshFutureOld, refreshFutureNew).join();
        }
    }


    /**
     * 销量月榜/新能源月榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getMonthSaleRankResult(RankParam param) {
        return rankSaleMonthComponent.getResultListByCondition(param);
    }


    /**
     * 销量周榜/新能源周榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getWeekSaleRankResult(RankParam param) {
        return rankSaleWeekComponent.getResultListByCondition(param);
    }


    /**
     * 城市榜
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getCitySaleRankResult(RankParam param) {
        return rankSaleCityComponent.getResultListByCondition(param);
    }

    /**
     * 关注榜-全部关注榜
     *
     * @param param 查询条件
     * @return 排行榜结果
     */
    public RankResultDto getAreaSeriesAttRankResult(RankParam param) {
        return areaSeriesAttentionComponent.getResultListByCondition(param);
    }

    /**
     * 关注榜-新车榜
     *
     * @param param 查询条件
     * @return 排行榜结果
     */
    public RankResultDto getAttentionRankNewCarList(RankParam param) {
        return dtNewCarAttentionComponent.getResultListByCondition(param);
    }

    /**
     * 新能源榜-新势力热度榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getNewPowerHotRank(RankParam param) {
        return rankNewEnergyNewPowerHotComponent.getResultListByCondition(param);
    }


    /**
     * 新能源榜-电耗榜&续航榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getNewEnergyPowerConsumptionAndBatteryLifeRank(RankParam param) {
        return newEnergyPowerConsumptionAndBatteryLifeComponent.getResultListByCondition(param);
    }



    /**
     * 新能源榜-电耗榜&续航榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultResponse getNewEnergyPowerConsumptionAndEnduranceComponentRank(RankParam param) {
        return powerConsumptionAndEnduranceComponent.getByCondition(param);
    }

    /**
     * 品牌月榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getBrandMonthRank(RankParam param) {
        return brandMonthComponent.getAllList(param);
    }

    /**
     * 品牌周榜
     *
     * @param param 查询条件
     * @return result
     */
    public RankResultDto getBrandWeekRank(RankParam param) {
        return new RankResultDto();
    }

    /**
     * 口碑榜
     *
     * @param param 查询条件
     * @return 排行榜结果
     */
    public RankResultDto getKoubeiRankList(RankParam param) {
        return koubeiRankComponent.getResultListByCondition(param);
    }



    /**
     * 数仓更新月榜/周榜回调接口
     * @param type 1:月榜 2:周榜
     * @param dateStr 月榜格式日期格式为: yyyy-MM, 周榜格式日期格式为: yyyy-MM-dd
     */
    public void updateSaleRank(Integer type, String dateStr) {
        // TODO 迁移回调任务
    }


    /**
     * 降价榜
     * @param param
     * @return
     */
    public RankResultDto getDiscountRank(RankParam param) {
        return discountComponent.getResultListByCondition(param);
    }

    /**
     * 保值榜
     * @param param
     * @return
     */
    public RankResultDto getHedgeRank(RankParam param) {
        return hedgeComponent.getResultListByCondition(param);
    }

    /**
     * 刷新销量榜单数据-目前只支持全量刷新
     *
     * @param request
     * @return
     */
    public RankSaleRefreshResponse refreshMonthAndWeekRank(RankSaleRefreshRequest request) {
        if (Strings.isNullOrEmpty(request.getSecretkey()) || !request.getSecretkey().equals("cCknc7")) {
            return RankSaleRefreshResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("无权限")
                    .build();
        }
        if (request.getOperatetype() <= 0 || request.getOperatetype() > 2) {
            return RankSaleRefreshResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("缺少必要参数")
                    .build();
        }
        if ((request.getOperatetype() == 1 && Strings.isNullOrEmpty(request.getMonth())) || (request.getOperatetype() == 2 && Strings.isNullOrEmpty(request.getWeek()))) {
            return RankSaleRefreshResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("缺少必要参数")
                    .build();
        }
        if (!Strings.isNullOrEmpty(request.getMonth())) {
            String dateRegex = "^\\d{4}-\\d{2}$";//2024-09
            Pattern datePattern = Pattern.compile(dateRegex);
            if (!datePattern.matcher(request.getMonth()).matches()) {
                return RankSaleRefreshResponse.newBuilder()
                        .setReturnCode(-1)
                        .setReturnMsg("参数格式错误")
                        .build();
            }
        }
        if (!Strings.isNullOrEmpty(request.getWeek())) {
            String timeRegex = "^\\d{4}-\\d{2}-\\d{2}$";//2024-09-03
            Pattern timePattern = Pattern.compile(timeRegex);
            if (!timePattern.matcher(request.getWeek()).matches()) {
                return RankSaleRefreshResponse.newBuilder()
                        .setReturnCode(-1)
                        .setReturnMsg("参数格式错误")
                        .build();
            }
            //判断是否为每周的周二
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dt = LocalDate.parse(request.getWeek(), formatter);
            if (dt.getDayOfWeek() != DayOfWeek.TUESDAY) {
                return RankSaleRefreshResponse.newBuilder()
                        .setReturnCode(-1)
                        .setReturnMsg("周榜日期错误")
                        .build();
            }
        }
        CompletableFuture.runAsync(() -> {
            boolean isTrue = rankHistoryComponent.refreshMonthAndWeekRank(request.getOperatetype(), request.getMonth(), request.getWeek());
            if (!Strings.isNullOrEmpty(rankSaleRefreshDingDing)) {
                //发送钉钉消息
                DingDingMessageParam param = new DingDingMessageParam();
                param.setSendWho(rankSaleRefreshDingDing);
                param.setType("ding");
                param.setTitle("销量榜单更新通知");
                String msg = "排行榜数据更新缓存刷新" + (isTrue ? "成功" : "失败");
                if (!Strings.isNullOrEmpty(request.getMonth())) {
                    msg += "\r\n" + "月榜：" + request.getMonth();
                }
                if (!Strings.isNullOrEmpty(request.getWeek())) {
                    msg += "\r\n" + "周榜：" + request.getWeek();
                }
                param.setMessage(msg);
                DingDingApiResult dingApiResult = dingDingApiClient.sendDingDingMsg(param).join();
            }
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("refreshMonthAndWeekRank-error:{}", e);
            return null;
        });
        return RankSaleRefreshResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("成功")
                .build();
    }
}
