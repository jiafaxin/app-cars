package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carext.*;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.che168.AssessReportClient;
import com.autohome.app.cars.apiclient.che168.dtos.GetSeriesAssessPriceResult;
import com.autohome.app.cars.apiclient.maindata.MainDataApiClient;
import com.autohome.app.cars.apiclient.maindata.dtos.HotDataResult;
import com.autohome.app.cars.apiclient.subscribe.SubscribeClient;
import com.autohome.app.cars.apiclient.subscribe.dtos.SubscribedSeriesDto;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.SeriesSubscribeNewsEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.SeriesSubscribeNewsMapper;
import com.autohome.app.cars.mapper.appcars.SpecCityPriceHistoryMapper;
import com.autohome.app.cars.mapper.appcars.entities.SeriesSubscribeNewsEntity;
import com.autohome.app.cars.mapper.appcars.entities.SpecCityPriceHistoryEntity;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHistoryDto;
import com.autohome.app.cars.service.components.newcar.dtos.SeriesSubscribeNewsDto;
import com.autohome.app.cars.service.components.recrank.attention.DtNewCarAttentionComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.DtNewCarAttentionDto;
import com.autohome.app.cars.service.components.recrank.dtos.MonthRankDataResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseSaleRankDataDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthComponent;
import com.autohome.app.cars.service.services.dtos.SubscribeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 车系动态频道
 * @date : 2024/9/4 19:39
 */
@Service
@Slf4j
public class SubscribeService {

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    private SeriesSubscribeNewsMapper seriesSubscribeNewsMapper;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Resource
    private MainDataApiClient mainDataApiClient;

    @Resource
    private SubscribeClient subscribeClient;

    @Autowired
    private DtNewCarAttentionComponent newCarAttentionComponent;

    @Autowired
    private RankSaleMonthComponent rankSaleMonthComponent;

    @Autowired
    private SpecMapper specMapper;

    @Autowired
    private SpecCityPriceHistoryMapper specCityPriceHistoryMapper;

    @Autowired
    private AssessReportClient assessReportClient;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.SubscribeConfig).createFromJson('${series_subscribe_config:}')}")
    private SubscribeConfig seriesSubscribeConfig;

    private final static int fiveMinutesInMillis = 5 * 60 * 1000; // 5分钟转换为毫秒
    /**
     * 消息列表
     */

    private static final String rnPrefixScheme = "autohome://rninsidebrowser?translucentdialog=1&customshowanimationtype=0&animation_type=0&noneanimation=1&animationtype=1&statusbarstyle=3&bgtransparent=1&conttransparent=1&contmargintop=0.1&screenOrientation=0&url=%s";
    private static final String rnUrlScheme = "rn://CarSeriesTestRN/SingleNewsPage?seriesid=%d&title=%s&panValid=0";
    private static final String assessRnUrlScheme = rnUrlScheme + "&mycarseriesid=%d&mycarspecid=%d&assessab=%s";
    private static final String recommendScheme = "全新车系关注度第%d名";
    private static final String seriesMainScheme = "autohome://car/seriesmain?seriesid=%d";

    private static final String rnSeriesNewsPage = "rn://CarSeriesTestRN/SeriesNewsPage?seriesid=%s";
    /**
     * 商用车级别
     */
    private static final List<Integer> COMMERCIAL_LEVEL_ID_LIST = Arrays.asList(11, 12, 13, 14, 25);

    public SeriesSubscribePageGetResponse pageGetList(SeriesSubscribePageGetRequest request) {
        int pagesize = request.getPagesize() == 0 || request.getPagesize() > 50 ? 20 : request.getPagesize();
        //订阅的车系
        List<Integer> seriesIds = new ArrayList<>();
        //订阅的车型
        List<Integer> specIds = new ArrayList<>();

        //如果传入了我的车系信息那么加入查询
        if(request.getMycarseriesid() > 0){
            seriesIds.add(request.getMycarseriesid());
        }
        if(request.getMycarspecid() > 0){
            specIds.add(request.getMycarspecid());
        }
        if (request.getSeriesid() == 0 && StringUtils.isEmpty(request.getSpecids())) {
            //1、获取用户订阅的车系、车型
            BaseModel<List<SubscribedSeriesDto>> subscribedSeries = subscribeClient.getSubscribeSeriesAndSpecList(request.getDeviceid()).join();
            if (subscribedSeries != null && CollectionUtils.isNotEmpty(subscribedSeries.getResult())) {
                seriesIds.addAll(subscribedSeries.getResult().stream().limit(50).map(SubscribedSeriesDto::getSeriesId).collect(Collectors.toList()));
                specIds.addAll(subscribedSeries.getResult().stream().map(SubscribedSeriesDto::getSpecId).filter(specId -> specId != 0).collect(Collectors.toList()));
            }
        } else {
            seriesIds.add(request.getSeriesid());
            List<Integer> ids = com.autohome.app.cars.common.utils.StringUtils.splitToInt(request.getSpecids());
            if (ids != null && ids.size() > 0) {
                specIds.addAll(ids);
            }
        }
        if(org.springframework.util.CollectionUtils.isEmpty(seriesIds)){
            return SeriesSubscribePageGetResponse.newBuilder()
                    .setReturnCode(0)
                    .setResult(SeriesSubscribePageGetResponse.Result.newBuilder())
                    .setReturnMsg("无订阅车系")
                    .build();
        }

        List<CompletableFuture> tasks = new ArrayList<>();
        //获取截至上次更新数据的条数
        AtomicReference<Integer> updateCount = new AtomicReference<>(0);
        //是否还有更多
        AtomicBoolean hasMore = new AtomicBoolean(false);
        AtomicLong searchAfter = new AtomicLong();

        String timestamp = DateUtil.convertTimestamp(request.getLastclicktime(), "yyyy-MM-dd HH:mm:ss");
        List<Integer> finalSeriesIds = seriesIds;
        List<Integer> finalSpecIds = specIds;
        //上次点击时间不为空，且在5分钟之前
        if (StringUtils.isNotEmpty(timestamp) && (new Date().getTime() - DateUtil.parse(timestamp, "yyyy-MM-dd HH:mm:ss").getTime()) > fiveMinutesInMillis) {
            tasks.add(CompletableFuture.supplyAsync(() ->
                            seriesSubscribeNewsMapper.getUpdatedCountByTimeRange(request.getCityid(), finalSeriesIds, finalSpecIds, timestamp), ThreadPoolUtils.defaultThreadPoolExecutor)
                    .thenAccept(num -> {
                        if (num != null) {
                            updateCount.set(num);
                        }
                    }).exceptionally(e -> {
                        log.error("getUpdatedCountByTimeRange-error", e);
                        return null;
                    }));
        }

        CompletableFuture<List<SeriesSubscribePageGetResponse.Result.Newslist>> pageListFuture = CompletableFuture.supplyAsync(() -> {
                    String searchAfterRequest = DateUtil.convertTimestamp(request.getSearchafter(), "yyyy-MM-dd HH:mm:ss.SSS");
                    //1.获取订阅List信息
                    List<SeriesSubscribeNewsEntity> list = seriesSubscribeNewsMapper.pageGetList(request.getCityid(), finalSeriesIds, finalSpecIds, pagesize + 1, searchAfterRequest);

                    //2.尝试填充我的爱车估值报告信息,并且是第一位
                    if(request.getMycarseriesid() > 0 && StringUtils.isBlank(request.getSearchafter())){
                        //谨慎起见，判断个空
                        if(org.springframework.util.CollectionUtils.isEmpty(list)){
                            list = new ArrayList<>();
                        }
                        list.add(0,this.tryFillMyCar(request));
                    }
                    return list;
                }, ThreadPoolUtils.defaultThreadPoolExecutor)
                .thenCombine(seriesDetailComponent.getList(seriesIds), (list, seriesDetails) -> {
                    List<SeriesSubscribePageGetResponse.Result.Newslist.Builder> newslists = new ArrayList<>();
                    //卡片内容解析
                    if (list != null && !list.isEmpty()) {
                        if (list.size() > pagesize) {
                            hasMore.set(true);
                            list.remove(list.size() - 1);
                        }
                        List<String> maindataids = new ArrayList<>();
                        AtomicInteger index = new AtomicInteger();
                        list.forEach(x -> {
                            SeriesSubscribeNewsEnum subscribeNewsEnum = SeriesSubscribeNewsEnum.getByType(x.getBiz_type());
                            SeriesDetailDto seriesDetailDto = seriesDetails.stream().filter(dto -> dto.getId() == x.getSeries_id()).findFirst().orElse(null);
                            SubscribeConfig.NodelistDTO nodeConfig = seriesSubscribeConfig.getNodelist().stream().filter(config -> config.getCode() == subscribeNewsEnum.getType()).findFirst().orElse(null);
                            //不显示车系名的情况:单车系，非第一条数据不显示车系名;
                            boolean hideSeriesName = request.getSeriesid() > 0 && (StringUtils.isNotEmpty(request.getSearchafter()) || (StringUtils.isEmpty(request.getSearchafter()) && index.getAndIncrement() != 0));
                            switch (subscribeNewsEnum) {
                                case IMAGE -> {
                                    newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                            .setType(subscribeNewsEnum.getCardtype())
                                            .setData(getImageCardData(x, subscribeNewsEnum, seriesDetailDto, nodeConfig, request, hideSeriesName)));
                                }
                                case CONFIG -> {
                                    newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                            .setType(subscribeNewsEnum.getCardtype())
                                            .setData(getConfigCardData(x, subscribeNewsEnum, seriesDetailDto, nodeConfig, request, hideSeriesName)));
                                }
                                case MARKET_PRICE -> {
                                    SeriesSubscribeNewsDto.PriceDto priceDto = JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.PriceDto.class);
                                    if (StringUtils.isNotEmpty(priceDto.getTitle())) {
                                        maindataids.add(priceDto.getMainDataType() + "-pv-" + priceDto.getBizId());
                                    }
                                    newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                            .setType(subscribeNewsEnum.getCardtype())
                                            .setData(getMarketPriceCardData(x, subscribeNewsEnum, priceDto, seriesDetailDto, nodeConfig, request, hideSeriesName)));
                                }
                                case CAR_WORK -> {
                                    SeriesSubscribeNewsDto.friendShareDTO friendShareDTO = JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.friendShareDTO.class);
                                    maindataids.add("club-pv-" + friendShareDTO.getTopicid());
                                    newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                            .setType(subscribeNewsEnum.getCardtype())
                                            .setData(getCarWorkCardData(x, subscribeNewsEnum, friendShareDTO, seriesDetailDto, nodeConfig, request, hideSeriesName)));
                                }
                                case RANK_WEEK, RANK_MONTH -> {
                                    newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                            .setType(subscribeNewsEnum.getCardtype())
                                            .setData(getCarRankCardData(x, subscribeNewsEnum, seriesDetailDto, nodeConfig, request, hideSeriesName)));
                                }
                                case SERIES_GUIDE_PRICE, SERIES_DEALER_PRICE -> {
                                    SeriesSubscribePageGetResponse.Result.Data.Builder seriesCutPriceCardData = getSeriesCutPriceCardData(x, subscribeNewsEnum, seriesDetailDto, nodeConfig, request, hideSeriesName);
                                    if (seriesCutPriceCardData != null) {
                                        newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                                .setType(subscribeNewsEnum.getCardtype())
                                                .setData(seriesCutPriceCardData));
                                    }
                                }
                                case SPEC_GUIDE_PRICE, SPEC_DEALER_PRICE -> {
                                    SeriesSubscribePageGetResponse.Result.Data.Builder specCutPriceCardData = getSpecCutPriceCardData(x, subscribeNewsEnum, seriesDetailDto, nodeConfig, request, hideSeriesName);
                                    if (specCutPriceCardData != null) {
                                        newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                                .setType(subscribeNewsEnum.getCardtype())
                                                .setData(specCutPriceCardData));
                                    }
                                }
                                case CMS_NEWS, VIDEO_NEWS -> {
                                    SeriesSubscribeNewsDto.CmsNewsDto cmsNewsDto = JSONObject.parseObject(x.getData(), SeriesSubscribeNewsDto.CmsNewsDto.class);
                                    boolean isVideo = "video".equals(cmsNewsDto.getMainDataType());
                                    if (isVideo) {
                                        maindataids.add(cmsNewsDto.getMainDataType() + "-vv-" + cmsNewsDto.getBizId());
                                    } else {
                                        maindataids.add(cmsNewsDto.getMainDataType() + "-pv-" + cmsNewsDto.getBizId());
                                    }
                                    newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                            .setType(subscribeNewsEnum.getCardtype())
                                            .setData(getCmsNewCardData(x, subscribeNewsEnum, cmsNewsDto, seriesDetailDto, nodeConfig, request, hideSeriesName)));
                                }
                                case ASSESS_REPORT -> {
                                    //根据实验决定是否添加爱车卡片
                                    if(List.of("E","F").contains(request.getAssessab())){
                                        newslists.add(SeriesSubscribePageGetResponse.Result.Newslist.newBuilder()
                                                .setType(subscribeNewsEnum.getCardtype())
                                                .setData(getAssessReportData(x,subscribeNewsEnum, request,seriesDetailDto)));
                                    }
                                }
                            }
                            searchAfter.set(x.getDisplay_time().getTime());
                        });
                        if (!maindataids.isEmpty()) {
                            mainDataApiClient.getHotData(String.join(",", maindataids)).thenAccept(hotDataResult -> {
                                if (hotDataResult != null && hotDataResult.getResult() != null) {
                                    newslists.forEach(x -> {
                                        if (x.getData() != null) {
                                            HotDataResult hotDataResult1 = hotDataResult.getResult().stream().filter(p -> p.getBiz_id() == x.getData().getMaindataid()).findFirst().orElse(null);
                                            if (hotDataResult1 != null) {
                                                x.setData(x.getData().toBuilder().setBrowsecount(SafeParamUtil.convertToWan(hotDataResult1.getCount()) + ("vv".equals(hotDataResult1.getHot_data_type()) ? "播放" : "浏览")));
                                            }
                                        }
                                    });
                                }
                            }).exceptionally(e -> {
                                log.error("getHotData-error", e);
                                return null;
                            }).join();
                        }
                    }
                    return newslists.stream().map(item -> item.build()).collect(Collectors.toList());
                }).exceptionally(e -> {
                    log.error("getUpdatedCountByTimeRange-error", e);
                    return new ArrayList<>();
                });
        tasks.add(pageListFuture);

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {
            SeriesSubscribePageGetResponse.Result.Builder result = SeriesSubscribePageGetResponse.Result.newBuilder()
                    .setHasmore(hasMore.get() ? 1 : 0)
                    .setToasttext(updateCount.get() > 0 ? "为您更新了" + updateCount.get() + "条动态" : "")
                    .setToastnum(updateCount.get())
                    .setSearchafter(searchAfter.toString())
                    .addAllNewslist(pageListFuture.join());

            return SeriesSubscribePageGetResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("ok")
                    .setResult(result)
                    .build();
        }).exceptionally(e -> {
            log.error("pageGetList error", e);
            return SeriesSubscribePageGetResponse.newBuilder()
                    .setReturnCode(101)
                    .setResult(SeriesSubscribePageGetResponse.Result.newBuilder())
                    .setReturnMsg("fail")
                    .build();
        }).join();
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getAssessReportData(SeriesSubscribeNewsEntity item,SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                         SeriesSubscribePageGetRequest request, SeriesDetailDto seriesDetailDto) {
        SpecDetailDto specDetailDto = specDetailComponent.get(item.getSpec_id()).join();
        if(null == specDetailDto){
            specDetailDto = new SpecDetailDto();
        }
        String seriesName = seriesDetailDto.getName();
        String scheme = "";
        if("E".equals(request.getAssessab())){
            String url = "rn://UsedCar/Valuation?panValid=false&pvareaid=113038&leadssources=3" +
                    "&sourcetwo=4&sourcethree=1837&animationtype=0&bgtransparent=0&conttransparent=1" +
                    "&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0";
            scheme = "autohome://rninsidebrowser?url="+URLEncoder.encode(url);
        }
        if("F".equals(request.getAssessab())){
            //对数据进行URL encode
            String data = URLEncoder.encode(String.format("{\"seriesid\":\"%d\",\"specid\":\"%d\",\"cid\":\"%d\"}",item.getSeries_id(),item.getSpec_id(),request.getCityid()));
            String url = "rn://UsedCar/ValuationPro?&panValid=false&pvareaid=113039&leadssources=3&sourcetwo=4&sourcethree=1838&data="
                    +data+"&animationtype=0&bgtransparent=0&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0";
            scheme = "autohome://rninsidebrowser?url=" + URLEncoder.encode(url);
        }

        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(seriesDetailDto.getName())
                .setTitle("爱车估值更新")
                .setDatetext(DateUtil.formatDisplayTime(new Timestamp(new Date().getTime())))
                .setSubtitle(DateUtil.serialize(LocalDate.now(),"MM") + "月车辆价值报告发布")
                .setLiketext("我的爱车：")
                .setLikespecname(StringUtils.isNotBlank(specDetailDto.getSpecName()) ? specDetailDto.getSpecName() : "")
                .setLinkurl(scheme)
                .setCardpvitem(Pvitem.newBuilder()
                        .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                        .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                        .putArgvs("scene", String.valueOf(request.getSource()))
                        .putArgvs("click", "101")
                        .putArgvs("ismycar","1")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));
        //半浮层协议
        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme,
                    CommonHelper.encodeUrl(String.format(assessRnUrlScheme, item.getSeries_id(),
                            CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态"),
                            item.getSeries_id(),specDetailDto.getSpecId(),request.getAssessab()))));
        }

        //添加爱车本月行情
        String valuationPriceStr = this.getValuationPriceStr(request,item);
        SeriesSubscribePageGetResponse.Result.List.Builder priceInfo = SeriesSubscribePageGetResponse.Result.List.newBuilder();
        priceInfo.setValue(valuationPriceStr).setValuecolor("#FF6600").setName("本月行情");
        builder.addList(priceInfo);
        //添加爱车已使用年限
        SeriesSubscribePageGetResponse.Result.List.Builder carYearInfo = SeriesSubscribePageGetResponse.Result.List.newBuilder();
        carYearInfo.setValue(this.getYearStr(specDetailDto)).setValuecolor("#111E36").setName("车龄");
        builder.addList(carYearInfo);

        //添加点击报告
        SeriesSubscribePageGetResponse.Result.List.Builder clickInfo = SeriesSubscribePageGetResponse.Result.List.newBuilder();
        clickInfo.setValue("查看报告").setValuecolor("#464E64").setName("").setScheme(scheme)
                .setPvitem(Pvitem.newBuilder()
                        .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                        .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                        .putArgvs("scene", String.valueOf(request.getSource()))
                        .putArgvs("click", "102")
                .putArgvs("ismycar","1")
                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                .setShow(Pvitem.Show.newBuilder().setEventid("")))
        ;
        builder.addList(clickInfo);

        return builder;
    }

    public String getValuationPriceStr(SeriesSubscribePageGetRequest request,SeriesSubscribeNewsEntity item){
        CompletableFuture<BaseModel<GetSeriesAssessPriceResult>> result = assessReportClient.getAssessPrice(request.getCityid(),
                item.getSeries_id(),item.getSpec_id(),request.getDeviceid());
        BaseModel<GetSeriesAssessPriceResult> priceResult = result.join();
        String valuationPriceStr = "暂无";
        if(ObjectUtils.isNotEmpty(priceResult) && priceResult.getReturncode() == 0 && ObjectUtils.isNotEmpty(priceResult.getResult())){
            valuationPriceStr = PriceUtil.toWanStr(priceResult.getResult().getValuationprice());
        }
        return valuationPriceStr;
    }

    public String getYearStr(SpecDetailDto specDetailDto){
        //如果没拿到车型年款，那么返回暂无
        if(specDetailDto.getYearName() <= 0){
            return "暂无";
        }
        //当前车龄 = 当前时间（年） - 年款
        int year = IntUtil.tryParseInt(DateUtil.serialize(LocalDate.now(),"YYYY"),0) - specDetailDto.getYearName();
        //如果计算后小于等0，返回不足一年
        if(year <= 0){
            return "不足1年";
        }
        return year + "年";
    }


    public SeriesSubscribeNewsEntity tryFillMyCar(SeriesSubscribePageGetRequest request){
        SeriesSubscribeNewsEntity entity = new SeriesSubscribeNewsEntity();
        entity.setSeries_id(request.getMycarseriesid());
        entity.setSpec_id(request.getMycarspecid());
        entity.setBiz_type(SeriesSubscribeNewsEnum.ASSESS_REPORT.getType());
        entity.setDisplay_time(new Timestamp(new Date().getTime()));
        return entity;
    }

    /**
     * 动态频道头部车系tab条
     *
     * @param request 请求
     * @return 响应
     */
    public SeriesSubscribeTagInfoResponse tagInfoList(SeriesSubscribeTagInfoRequest request) {
        SeriesSubscribeTagInfoResponse.Builder resultBuilder = SeriesSubscribeTagInfoResponse.newBuilder();
        SeriesSubscribeTagInfoResponse.Result.Builder tabListBuilder = SeriesSubscribeTagInfoResponse.Result.newBuilder();
        tabListBuilder.setTitle("动态");
        // 查询订阅的最新50条数据
        subscribeClient.getSubscribeSeriesAndSpecList(request.getDeviceid()).thenAccept(resultDto -> {
            //只要带入了车系，resultDto为空也可以进行后续操作
            boolean hasSubscribeSeries = Objects.nonNull(resultDto) && Objects.nonNull(resultDto.getResult()) && !resultDto.getResult().isEmpty();
            if ( hasSubscribeSeries|| 0 != request.getSeriesid()) {
                List<SubscribedSeriesDto> list;
                // 处理新用户无定阅车系 但从车系页带入的场景
                if (hasSubscribeSeries) {
                    list = resultDto.getResult();
                } else {
                    list = new ArrayList<>();
                }

                List<Integer> allSeriesId = list.stream().map(SubscribedSeriesDto::getSeriesId).distinct().collect(Collectors.toList());
                List<Integer> seriesIdList = allSeriesId.subList(0, Math.min(50, allSeriesId.size()));
                //车系下订阅的车型
                Map<Integer, List<SubscribedSeriesDto>> dtoSpecMap = list.stream()
                        .filter(x -> x.getSpecId() != 0 && x.getSeriesId() != 0)
                        .collect(Collectors.groupingBy(SubscribedSeriesDto::getSeriesId));

                Map<Integer, SeriesDetailDto> detailDtoMap = new HashMap<>(seriesIdList.size());
                //已订阅车系数量>=2 才展示 最新
                if (seriesIdList.size() >= 2) {
                    // 添加最新标签
                    tabListBuilder.addTablist(SeriesSubscribeTagInfoResponse.Result.Tablist.newBuilder().setTabid(1).setTabname("最新").build());
                }

                //新带入车系id
                AtomicInteger newSeriesId = new AtomicInteger(request.getSeriesid());
                //tab框里是否显示㊉订阅，true显示，false不显示
                AtomicBoolean subscribeFlag = new AtomicBoolean(true);
                //带入了车系
                if (0 != newSeriesId.get()) {
                    //新带入的车系不在已订阅车系列表中
                    if (!seriesIdList.contains(newSeriesId.get())) {
                        if (allSeriesId.contains(newSeriesId.get())) {
                            // 不需要显示㊉
                            subscribeFlag.set(false);
                        }
                        seriesIdList.add(newSeriesId.get());
                    } else {
                        // 不需要显示㊉
                        subscribeFlag.set(false);
                    }
                }
                int mycarseriesid = request.getMycarseriesid();
                String mycarspecid = request.getMycarspecid();
                if(0 != mycarseriesid && !seriesIdList.contains(mycarseriesid)){
                    seriesIdList.add(mycarseriesid);
                }

                // 添加订阅车系标签
                seriesDetailComponent.getList(seriesIdList).thenAccept(detailList -> {
                    if (CollectionUtils.isNotEmpty(detailList)) {
                        detailDtoMap.putAll(detailList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity())));
                        List<Integer> specIdList = list.stream().map(SubscribedSeriesDto::getSpecId).filter(specId -> specId > 0).toList();
                        List<SeriesSubscribeNewsEntity> subscribeSeriesList = seriesSubscribeNewsMapper.getSubscribeSeriesList(seriesIdList, specIdList, request.getCityid());
                        List<Integer> usedSeriesIdList = new ArrayList<>();
                        //如果存在要展示我的爱车,那么就在 “最新” 这个tab之后展示
                        if(request.getMycarseriesid() > 0){
                            SeriesDetailDto myCarSeriesDetailDto = detailDtoMap.get(mycarseriesid);
                            tabListBuilder.addTablist(SeriesSubscribeTagInfoResponse.Result.Tablist.newBuilder()
                                    .setBrandlogo(myCarSeriesDetailDto.getBrandLogo())
                                    .setSeriesid(mycarseriesid)
                                    .setTabid(0)
                                    .setLastupdatetime(0)
                                    .setTabname(myCarSeriesDetailDto.getName())
                                    .setTagname("爱车")
                                    .setIsmycar(1)
                                    .setSubsstatus(0)
                                    .setScheme(String.format(seriesMainScheme, mycarseriesid))
                                    .setSpecids(mycarspecid)
                                    .build());
                            usedSeriesIdList.add(mycarseriesid);
                        }

                        //如果新带入了车系
                        if (0 != newSeriesId.get() && detailDtoMap.containsKey(newSeriesId.get()) && !usedSeriesIdList.contains(newSeriesId.get())) {
                            //新传入的车系id对应的SeriesSubscribeNewsEntity
                            SeriesSubscribeNewsEntity newSeriesEntity = subscribeSeriesList.stream().filter(e -> e.getSeries_id() == newSeriesId.get()).findFirst().orElse(null);
                            SeriesDetailDto newSeriesDetailDto = detailDtoMap.get(newSeriesId.get());
                            String specIds = StringUtils.EMPTY;
                            if (dtoSpecMap.containsKey(newSeriesId.get())) {
                                List<Integer> list1 = dtoSpecMap.get(newSeriesId.get()).stream().map(SubscribedSeriesDto::getSpecId).collect(Collectors.toList());
                                specIds = String.join(",", list1.stream().map(String::valueOf).collect(Collectors.toList()));
                            }
                            tabListBuilder.addTablist(SeriesSubscribeTagInfoResponse.Result.Tablist.newBuilder()
                                    .setBrandlogo(newSeriesDetailDto.getBrandLogo())
                                    .setSeriesid(newSeriesId.get())
                                    .setTabid(0)
                                    .setLastupdatetime(Objects.nonNull(newSeriesEntity) ? newSeriesEntity.getDisplay_time().getTime() : 0)
                                    .setTabname(newSeriesDetailDto.getName())
                                    //2代表新带入的未订阅
                                    .setSubsstatus(subscribeFlag.get() ? 2 : 0)
                                    .setScheme(String.format(seriesMainScheme, newSeriesId.get()))
                                    .setSpecids(specIds)
                                    .build());
                            usedSeriesIdList.add(newSeriesId.get());
                        }

                        Map<Integer, SeriesSubscribeNewsEntity> entityMap = subscribeSeriesList.stream().collect(Collectors.toMap(SeriesSubscribeNewsEntity::getSeries_id, x -> x));
                        seriesIdList.forEach(seriesId -> {
                            //遍历到了新带入车系，暂时不加入tabListBuilder，前面已经处理,去重
                            if(usedSeriesIdList.contains(seriesId)){
                                return;
                            }
                            if (detailDtoMap.containsKey(seriesId)) {
                                SeriesSubscribeNewsEntity entity = entityMap.get(seriesId);
                                SeriesDetailDto seriesDetailDto = detailDtoMap.get(seriesId);
                                String specIds = StringUtils.EMPTY;
                                if (dtoSpecMap.containsKey(seriesId)) {
                                    List<Integer> list1 = dtoSpecMap.get(seriesId).stream().map(SubscribedSeriesDto::getSpecId).toList();
                                    specIds = list1.stream().map(String::valueOf).collect(Collectors.joining(","));
                                }
                                tabListBuilder.addTablist(SeriesSubscribeTagInfoResponse.Result.Tablist.newBuilder()
                                        .setBrandlogo(seriesDetailDto.getBrandLogo())
                                        .setSeriesid(seriesId)
                                        .setTabid(0)
                                        .setLastupdatetime(Objects.nonNull(entity) ? entity.getDisplay_time().getTime() : 0)
                                        .setTabname(seriesDetailDto.getName())
                                        .setSubsstatus(0)
                                        .setScheme(String.format(seriesMainScheme, seriesId))
                                        .setSpecids(specIds)
                                        .build());
                            }
                        });
                    }
                }).join();
                // 添加订阅车系标签
                tabListBuilder.addTablist(SeriesSubscribeTagInfoResponse.Result.Tablist.newBuilder().setTabid(999).setTabname("+订阅车系").build());
            }
        }).exceptionally(e -> {
            log.warn("getSubscribeSeriesAndSpecList error", e);
            return null;
        }).join();

        resultBuilder.setResult(tabListBuilder);
        return resultBuilder.build();
    }


    SeriesSubscribePageGetResponse.Result.Data.Builder getImageCardData(SeriesSubscribeNewsEntity item,
                                                                        SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                        SeriesDetailDto seriesDetailDto,
                                                                        SubscribeConfig.NodelistDTO nodeConfig,
                                                                        SeriesSubscribePageGetRequest request,
                                                                        boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "图片发布")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()));

        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }

        SeriesSubscribeNewsDto.PicDTO picDTO = JSONObject.parseObject(item.getData(), SeriesSubscribeNewsDto.PicDTO.class);

        String subTitle = "发布图片共" + picDTO.getPicCount() + "张";

        AtomicInteger colorCount = new AtomicInteger();
        if (picDTO.getPicItems() != null) {
            picDTO.getPicItems().forEach(x -> {
                SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
                if (StringUtils.isNotEmpty(x.getColorName())) {
                    colorCount.getAndIncrement();
                    listItemBuilder.setColortext(x.getColorName());
                }
                listItemBuilder.setImageurl(x.getPic())
                        .setLinkurl(x.getUrl())
                        .setPvitem(Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", StringUtils.isNotEmpty(x.getColorName()) ? "13" : "12")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("")));
                builder.addList(listItemBuilder);
            });

            if (colorCount.get() > 0) {
                subTitle += "，" + colorCount.get() + "款颜色";
            }
        }
        builder.setSubtitle(subTitle).setRighttext("全部图片")
                .setLinkurl(picDTO.getLinkurl())
                .setRightlinkurl(picDTO.getLinkurl())
                .setRightpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "11")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("")))
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", colorCount.get() > 1 ? "13" : "12")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));


        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getConfigCardData(SeriesSubscribeNewsEntity item,
                                                                         SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                         SeriesDetailDto seriesDetailDto,
                                                                         SubscribeConfig.NodelistDTO nodeConfig,
                                                                         SeriesSubscribePageGetRequest request,
                                                                         boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribeNewsDto.CarParamDTO carParamDTO = JSONObject.parseObject(item.getData(), SeriesSubscribeNewsDto.CarParamDTO.class);
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "参配发布")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                .setSubtitle("发布" + carParamDTO.getSpeccount() + "款车型参配信息")
                .setLinkurl(carParamDTO.getUrl())
                .setRightlinkurl(carParamDTO.getUrl())
                .setRighttext("参配详情")
                .setRightpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "21")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("")))
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "22")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));

        carParamDTO.getSpecconfigitems().forEach(x -> {
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder
                    .setSpecname(x.getSpecname())
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                            .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                            .putArgvs("scene", String.valueOf(request.getSource()))
                            .putArgvs("click", "23")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));

            if (request.getPm()==1) {
                listItemBuilder.setLinkurl(String.format("autohome://carcompare/specsummaryconfig?seriesid=%s&specid=%s&specname=%s", item.getSeries_id(), x.getSpecid(), UrlUtil.encode(x.getSpecname())));
            }else {
                listItemBuilder.setLinkurl(String.format("autohome://car/specconfig?seriesid=%s&specid=%s&specname=%s", item.getSeries_id(), x.getSpecid(), UrlUtil.encode(x.getSpecname())));
            }
            if (!x.getConfiglist().isEmpty()) {
                x.getConfiglist().forEach(configListDTO -> {
                    listItemBuilder.addConfiglist(SeriesSubscribePageGetResponse.Result.Configlist.newBuilder()
                            .setName(configListDTO.getParamname())
                            .setValue(configListDTO.getParamvalue()));
                });
            }
            builder.addList(listItemBuilder);
        });
        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getMarketPriceCardData(SeriesSubscribeNewsEntity item,
                                                                              SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                              SeriesSubscribeNewsDto.PriceDto priceDto,
                                                                              SeriesDetailDto seriesDetailDto,
                                                                              SubscribeConfig.NodelistDTO nodeConfig,
                                                                              SeriesSubscribePageGetRequest request,
                                                                              boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "价格公布")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                .setSubtitle("厂商指导价：" + seriesDetailDto.getPrice())
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "31")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));

        if (StringUtils.isNotEmpty(priceDto.getTitle())) {
            builder.setAutotext(StringUtils.isNotEmpty(seriesSubscribeConfig.getCms_source()) ? seriesSubscribeConfig.getCms_source() : "之家原创")
                    .setAuthor(StringUtils.isNotEmpty(priceDto.getAuthorName()) ? priceDto.getAuthorName() : "")
                    .setIsvideo(0)
                    .setMaindataid(priceDto.getBizId())
                    .setLinkurl(priceDto.getScheme())
                    .setDescribe(priceDto.getTitle())
                    .addAllImglist(priceDto.getImgUrlList());
        }

        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getCarWorkCardData(SeriesSubscribeNewsEntity item,
                                                                          SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                          SeriesSubscribeNewsDto.friendShareDTO friendShareDTO,
                                                                          SeriesDetailDto seriesDetailDto,
                                                                          SubscribeConfig.NodelistDTO nodeConfig,
                                                                          SeriesSubscribePageGetRequest request,
                                                                          boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "提车分享")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                .setAuthor(StringUtils.isNotEmpty(friendShareDTO.getAuthorName()) ? friendShareDTO.getAuthorName() : "")
                .addAllImglist(friendShareDTO.getImgUrlList())
                .setLinkurl(friendShareDTO.getScheme())
                .setSubtitle(friendShareDTO.getTitle())
                .setMaindataid(friendShareDTO.getTopicid())
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "41")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));
        if (friendShareDTO.isCarowner()) {
            builder.setCarimage(seriesDetailDto.getBrandLogo())
                    .setOwnername(seriesDetailDto.getName() + "车主");
        }

        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getCarRankCardData(SeriesSubscribeNewsEntity item,
                                                                          SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                          SeriesDetailDto seriesDetailDto,
                                                                          SubscribeConfig.NodelistDTO nodeConfig,
                                                                          SeriesSubscribePageGetRequest request,
                                                                          boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = JSONObject.parseObject(item.getData(), SeriesSubscribeNewsDto.RankInfoDto.class);
        String linkUrl = "autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=" + CommonHelper.encodeUrl(rankInfoDto.getScheme());
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "销量更新")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                //.setLinkurl(linkUrl)
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "51")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));

        if (subscribeNewsEnum == SeriesSubscribeNewsEnum.RANK_MONTH) {
            Date date = DateUtil.parse(rankInfoDto.getDateValue(), "yyyy-MM");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -6);
            Date halfYearAgo = calendar.getTime();
            // 判断日期是否在近7个月内
            if (date.after(halfYearAgo)) {
                builder.setLinkurl("autohome://car/recmainrank?from=1&typeid=1&subranktypeid=1&date=" + rankInfoDto.getDateValue() + (rankInfoDto.getRnnum() > 0 && rankInfoDto.getRnnum() <= 100 ? "&rank=" + rankInfoDto.getRnnum() : ""));
            } else {
                builder.setLinkurl("autohome://car/recmainrank?from=1&typeid=1&subranktypeid=1");
            }
            builder.setSubtitle(rankInfoDto.getDate() + "销量更新");
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setMonth(rankInfoDto.getDate()).setValue(rankInfoDto.getCurrentSaleCount() + "辆");
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setMonth("比上月");
            long l = rankInfoDto.getCurrentSaleCount() - rankInfoDto.getLastSaleCount();
            if (rankInfoDto.getCurrentSaleCount() == 0 || rankInfoDto.getLastSaleCount() == 0 || l == 0) {
                listItemBuilder.setValue("-");
            } else {
                listItemBuilder.setValue((l > 0 ? "+" : "") + l + "辆").setValuecolor(l < 0 ? "#1CCD99" : "#FF6600");
            }
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setValue("月销量趋势")
                    .setValuecolor("#464E64")
                    .setScheme(linkUrl)
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                            .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                            .putArgvs("scene", String.valueOf(request.getSource()))
                            .putArgvs("click", "52")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));
            builder.addList(listItemBuilder);
        } else {
            Date date = DateUtil.parse(rankInfoDto.getDateValue(), "yyyy-MM");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -3);
            Date halfAgo = calendar.getTime();
            if (date.after(halfAgo)) {
                builder.setLinkurl("autohome://car/recmainrank?from=1&typeid=1&subranktypeid=2&week=" + rankInfoDto.getDateValue() + (rankInfoDto.getRnnum() > 0 && rankInfoDto.getRnnum() <= 100 ? "&rank=" + rankInfoDto.getRnnum() : ""));
            } else {
                builder.setLinkurl("autohome://car/recmainrank?from=1&typeid=1&subranktypeid=2");
            }
            builder.setSubtitle(rankInfoDto.getDate() + "周销量更新");
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setMonth(rankInfoDto.getDate()).setValue(rankInfoDto.getCurrentSaleCount() + "辆");
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setMonth("比上周");
            long l = rankInfoDto.getCurrentSaleCount() - rankInfoDto.getLastSaleCount();
            if (rankInfoDto.getCurrentSaleCount() == 0 || rankInfoDto.getLastSaleCount() == 0 || l == 0) {
                listItemBuilder.setValue("-");
            } else {
                listItemBuilder.setValue((l > 0 ? "+" : "") + l + "辆").setValuecolor(l < 0 ? "#1CCD99" : "#FF6600");
            }
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setValue("周销量趋势")
                    .setValuecolor("#464E64")
                    .setScheme(linkUrl)
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                            .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                            .putArgvs("scene", String.valueOf(request.getSource()))
                            .putArgvs("click", "52")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));
            builder.addList(listItemBuilder);
        }
        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getSeriesCutPriceCardData(SeriesSubscribeNewsEntity item,
                                                                                 SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                                 SeriesDetailDto seriesDetailDto,
                                                                                 SubscribeConfig.NodelistDTO nodeConfig,
                                                                                 SeriesSubscribePageGetRequest request,
                                                                                 boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribeNewsDto.SeriesPriceDownDto seriesPriceDownDto = JSONObject.parseObject(item.getData(), SeriesSubscribeNewsDto.SeriesPriceDownDto.class);



        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();

        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        String linkUrl = "autohome://car/pricelibrary?brandid=" + seriesDetailDto.getBrandId() + "&seriesid=" + item.getSeries_id() + "&specid=" + seriesPriceDownDto.getSpecId() + "&seriesname=" + UrlUtil.encode(seriesDetailDto.getName()).replace("+", "%20") + "&tabindex=1&fromtype=1&tabtype=1&sourceid=11&tabpricename=" + UrlUtil.encode("本地报价");
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "降价提醒")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                .setLinkurl(linkUrl)
                .setPricespec("降价车型")
                .setPricespecvalue("共" + seriesPriceDownDto.getCount() + "款车型")
                .setMaxname("最大降幅")
                .setMaxvalue(seriesPriceDownDto.getSpecName())
                .setRighttext("查看详情")
                .setRightlinkurl(linkUrl)
                .setRightpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "62")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("")))
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "61")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));

        String priceInfo = CommonHelper.priceForamtV2(seriesPriceDownDto.getMinPriceDown(), seriesPriceDownDto.getMaxPriceDown());
        if (subscribeNewsEnum == SeriesSubscribeNewsEnum.SERIES_GUIDE_PRICE) {
            builder.setSubtitle("指导价最高降" + CommonHelper.getPriceInfo(seriesPriceDownDto.getMaxPriceDown()));
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setName("降价幅度").setPrice(priceInfo).setPricecolor("#1CCD99").setIspricedown(1);
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setName("降价后指导价").setPrice(CommonHelper.priceForamtV2(seriesPriceDownDto.getCurMinPrice(), seriesPriceDownDto.getCurMaxPrice())).setPricecolor("#FF6600").setIspricedown(0);
            builder.addList(listItemBuilder);
        } else {
            String cityName = CityUtil.getCityName(request.getCityid());
            builder.setSubtitle(String.format("%s本地经销商报价最高降%s", cityName, PriceUtil.SeriesSubscribeFormatPrice(seriesPriceDownDto.getMaxPriceDown())));
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setName("降价幅度").setPrice(priceInfo).setPricecolor("#1CCD99").setIspricedown(1);
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setName("降价后报价").setPrice(CommonHelper.priceForamtV2(seriesPriceDownDto.getCurMinPrice(), seriesPriceDownDto.getCurMaxPrice())).setPricecolor("#FF6600").setIspricedown(0);
            builder.addList(listItemBuilder);
        }
        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getSpecCutPriceCardData(SeriesSubscribeNewsEntity item,
                                                                               SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                               SeriesDetailDto seriesDetailDto,
                                                                               SubscribeConfig.NodelistDTO nodeConfig,
                                                                               SeriesSubscribePageGetRequest request,
                                                                               boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        SeriesSubscribeNewsDto.SpecPriceDownDto specPriceDownDto = JSONObject.parseObject(item.getData(), SeriesSubscribeNewsDto.SpecPriceDownDto.class);
        int curPrice = Math.abs(specPriceDownDto.getPrevPrice() - specPriceDownDto.getCurPrice());
        if (curPrice == 0) {
            return null;
        }
        String linkUrl = "autohome://car/pricelibrary?brandid=" + seriesDetailDto.getBrandId() + "&seriesid=" + item.getSeries_id() + "&specid=" + item.getSpec_id() + "&seriesname=" + UrlUtil.encode(seriesDetailDto.getName()).replace("+", "%20") + "&tabindex=1&fromtype=1&tabtype=1&sourceid=11&tabpricename=" + UrlUtil.encode("本地报价");
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();

        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "降价提醒")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                .setLinkurl(linkUrl)
                .setRighttext("查看详情")
                .setRightlinkurl(linkUrl)
                .setRightpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "62")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("")))
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "61")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));

        if (subscribeNewsEnum == SeriesSubscribeNewsEnum.SPEC_GUIDE_PRICE) {
            builder.setSubtitle(String.format("%s 指导价降%s", specPriceDownDto.getSpecName(), PriceUtil.SeriesSubscribeFormatPrice(curPrice)));
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setName("降价幅度").setPrice(CommonHelper.getPriceInfo(curPrice)).setPricecolor("#1CCD99").setIspricedown(1);
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            listItemBuilder.setName("降价后指导价").setPrice(CommonHelper.getPriceInfo(specPriceDownDto.getCurPrice())).setPricecolor("#FF6600").setIspricedown(0);
            builder.addList(listItemBuilder);
        } else {
            String cityName = CityUtil.getCityName(request.getCityid());
            builder.setSubtitle(String.format("%s %s本地经销商报价最高降%s", specPriceDownDto.getSpecName(), cityName, PriceUtil.SeriesSubscribeFormatPrice(curPrice)));
            SeriesSubscribePageGetResponse.Result.List.Builder listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            String priceInfo = CommonHelper.getPriceInfo(curPrice);
            listItemBuilder.setName("降价幅度").setPrice("暂无报价".equals(priceInfo) ? "-" : priceInfo + "起").setPricecolor("#1CCD99").setIspricedown(1);
            builder.addList(listItemBuilder);
            listItemBuilder = SeriesSubscribePageGetResponse.Result.List.newBuilder();
            String priceInfo1 = CommonHelper.getPriceInfo(specPriceDownDto.getCurPrice());
            listItemBuilder.setName("降价后报价").setPrice("暂无报价".equals(priceInfo1) ? "-" : priceInfo1 + "起").setPricecolor("#FF6600").setIspricedown(0);
            builder.addList(listItemBuilder);
        }
        return builder;
    }

    SeriesSubscribePageGetResponse.Result.Data.Builder getCmsNewCardData(SeriesSubscribeNewsEntity item,
                                                                         SeriesSubscribeNewsEnum subscribeNewsEnum,
                                                                         SeriesSubscribeNewsDto.CmsNewsDto cmsNewsDto,
                                                                         SeriesDetailDto seriesDetailDto,
                                                                         SubscribeConfig.NodelistDTO nodeConfig,
                                                                         SeriesSubscribePageGetRequest request,
                                                                         boolean hideSeriesName) {
        String seriesName = seriesDetailDto != null ? seriesDetailDto.getName() : "";
        boolean isVideo = "video".equals(cmsNewsDto.getMainDataType());
        SeriesSubscribePageGetResponse.Result.Data.Builder builder = SeriesSubscribePageGetResponse.Result.Data.newBuilder();
        if (request.getSeriesid() <= 0) {
            builder.setNewslinkurl(String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, item.getSeries_id(), CommonHelper.encodeUrl(seriesName.replace("+", "%20") + "动态")))));
        }
        builder.setBiztype(subscribeNewsEnum.getBiztype())
                .setSeriesid(item.getSeries_id())
                .setSeriesname(!hideSeriesName ? seriesName : "")
                .setTitle(nodeConfig != null ? nodeConfig.getTitle() : "资讯动态")
                .setDatetext(DateUtil.formatDisplayTime(item.getDisplay_time()))
                .setAutotext(StringUtils.isNotEmpty(seriesSubscribeConfig.getCms_source()) ? seriesSubscribeConfig.getCms_source() : "之家原创")
                .setAuthor(StringUtils.isNotEmpty(cmsNewsDto.getAuthorName()) ? cmsNewsDto.getAuthorName() : "")
                .setIsvideo(isVideo ? 1 : 0)
                .setLinkurl(cmsNewsDto.getScheme())
                .setSubtitle(cmsNewsDto.getTitle())
                .setMaindataid(cmsNewsDto.getBizId())
                .setCardpvitem(
                        Pvitem.newBuilder()
                                .putArgvs("type", String.valueOf(subscribeNewsEnum.getPvtype()))
                                .putArgvs("seriesid", String.valueOf(item.getSeries_id()))
                                .putArgvs("scene", String.valueOf(request.getSource()))
                                .putArgvs("click", "7")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscard_click"))
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscard_show")));
        if (isVideo && !cmsNewsDto.getImgUrlList().isEmpty()) {
            builder.setVideoimage(cmsNewsDto.getImgUrlList().get(0));
        } else {
            builder.addAllImglist(cmsNewsDto.getImgUrlList());
        }
        return builder;
    }

    public SeriesSubscribeRecommendedCarResponse getRecommendedCarList(SeriesSubscribeRecommendedCarRequest request) {
        List<DtNewCarAttentionDto> rankList = newCarAttentionComponent.getRankList();
        rankList = rankList.stream().filter(x -> x.getSeriesTagId() == 1).toList();
        for (int i = 0; i < rankList.size(); i++) {
            rankList.get(i).setRankNum(i + 1);
        }
        // 使用 HashSet 去重
        Set<Integer> allSeriesIdSet = new HashSet<>(34);
        Map<Integer, Integer> seriesIdSourceMap = new HashMap<>(34);
        String seriesIds = request.getSeriesids();
        List<Integer> allHistoryIdList;
        if (StringUtils.isNotBlank(seriesIds)) {
            String[] seriesIdArr = seriesIds.split(StrPool.COMMA);
            // 过滤榜单已经存在的车系
            allHistoryIdList = Arrays.stream(seriesIdArr).map(Integer::parseInt).toList();
        } else {
            allHistoryIdList = Collections.emptyList();
        }


        // 如果排行榜超过24个，只取前24个
        if (!rankList.isEmpty() && rankList.size() > 24) {
            rankList = rankList.subList(0, 24);
        }
        List<Integer> rankIdList = rankList.stream().map(DtNewCarAttentionDto::getSeriesId).toList();
        // 新车关注榜Map
        Map<Integer, DtNewCarAttentionDto> attrNewCarRankMap = rankList.stream().collect(Collectors.toMap(DtNewCarAttentionDto::getSeriesId, x -> x));

        allSeriesIdSet.addAll(rankIdList);
        allSeriesIdSet.addAll(allHistoryIdList);

        List<Integer> allSeriesIdList = new ArrayList<>(allSeriesIdSet);
        List<SeriesDetailDto> seriesDetailList = seriesDetailComponent.getListSync(allSeriesIdList);
        // 过滤掉未售和停售且已上市 和 去掉 商用车的车系
        Map<Integer, SeriesDetailDto> onSaleSeriesDetailMap = seriesDetailList.stream().filter(x -> x.getState() != 0 && x.getState() != 40 && !COMMERCIAL_LEVEL_ID_LIST.contains(x.getLevelId())).collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));
        // source value=1 为 新车关注榜车系
        List<Integer> onSaleRankIdList = rankIdList.stream().filter(onSaleSeriesDetailMap::containsKey).toList();
        seriesIdSourceMap.putAll(onSaleRankIdList.stream().collect(Collectors.toMap(x -> x, x -> 1)));
        // 排行榜前4个
        List<Integer> firstRankIdList = onSaleRankIdList.subList(0, 4);
        // 取历史浏览车系前8个
        List<Integer> historyIdList = allHistoryIdList.stream().distinct().filter(x -> onSaleSeriesDetailMap.containsKey(x) && !firstRankIdList.contains(x)).limit(8).collect(Collectors.toList());
        // 历史数据标记为2
        seriesIdSourceMap.putAll(historyIdList.stream().collect(Collectors.toMap(x -> x, x -> 2)));

        List<Integer> seriesIdList = new ArrayList<>(firstRankIdList);
        // 移除排行榜前4个
        historyIdList.removeIf(firstRankIdList::contains);
        seriesIdList.addAll(historyIdList.subList(0, Math.min(8, historyIdList.size())));
        if (seriesIdList.size() < 6) {
            // 排行榜候补List, 如果总数未超过6个, 则从这个list中补足6个
            List<Integer> rankReplenishIdList = onSaleRankIdList.stream().filter(x -> !seriesIdList.contains(x)).limit(6 - seriesIdList.size()).toList();
            seriesIdList.addAll(rankReplenishIdList);
        }


        // 移除停售和未上市的车系
        seriesIdList.removeIf(x -> !onSaleSeriesDetailMap.containsKey(x));
        // 查询车系下车型List
        List<SpecEntity> specEntityList = specMapper.getSpecBySeriesIds(seriesIdList);
        Map<Integer, List<SpecEntity>> specsGrpSeriesIdMap = specEntityList.stream().collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        List<Integer> specIdList = specEntityList.stream().map(SpecEntity::getId).toList();
        Map<Integer, Long> rankSaleCountMap = new HashMap<>();
        Map<Integer, Integer> specGuidePriceMap = specEntityList.stream().collect(Collectors.toMap(SpecEntity::getId, SpecEntity::getMaxPrice));
        List<SpecCityPriceHistoryDto> specCityPriceHisList = new ArrayList<>();
        // 查询车型价格
        CompletableFuture<Void> specCityPriceFuture = CompletableFuture.runAsync(() -> specCityPriceHisList.addAll(getCurrentPriceByCityAndSpecIdList(request.getCityid(), specIdList)))
                .exceptionally(e -> {
                    log.warn("查询车型价格错误", e);
                    return null;
                });
        CompletableFuture<String> lastMonthFuture = CompletableFuture.supplyAsync(() -> {
            // 排行榜 车系--销量
            String lastMonthStr = rankSaleMonthComponent.getLastMonth();
            List<MonthRankDataResultDto.RankDataDto> dataList = rankSaleMonthComponent.getDataList(lastMonthStr, lastMonthStr, 1000);
            // 榜单销量
            rankSaleCountMap.putAll(dataList.stream().collect(Collectors.toMap(MonthRankDataResultDto.RankDataDto::getSeriesId, BaseSaleRankDataDto::getSaleCount)));
            return lastMonthStr;
        }).exceptionally(e -> {
            log.warn("查询排行榜错误", e);
            return null;
        });
        List<SeriesSubscribeNewsEntity> subscribeSeriesList = new ArrayList<>();
        CompletableFuture<Void> subscribeSeriesListFuture = CompletableFuture
                .runAsync(() -> subscribeSeriesList.addAll(seriesSubscribeNewsMapper.getSubscribeSeriesList(seriesIdList, Collections.emptyList(), request.getCityid())))
                .exceptionally(e -> {
                    log.warn("查询动态信息错误");
                    return null;
                });

        CompletableFuture.allOf(specCityPriceFuture, lastMonthFuture, subscribeSeriesListFuture).join();
        // 车型经销商价Map
        Map<Integer, Integer> specCurDealerPriceMap = specCityPriceHisList.stream().collect(Collectors.toMap(SpecCityPriceHistoryDto::getSpecId, x -> x.getList().get(0).getNewsPrice()));

        LocalDate lastMonthDate = LocalDate.parse(lastMonthFuture.join(), RankConstant.LOCAL_MONTH_FORMATTER);
        int lastMonthValue = lastMonthDate.getMonthValue();
        List<SeriesSubscribeRecommendedCarResponse.Result.Serieslist> itemList = new ArrayList<>(seriesIdList.size());

        Map<Integer, Integer> hasNewsSeriesMap;
        if (CollectionUtils.isNotEmpty(subscribeSeriesList)) {
            hasNewsSeriesMap = subscribeSeriesList.stream().collect(Collectors.toMap(SeriesSubscribeNewsEntity::getSeries_id, x -> 1));
        } else {
            hasNewsSeriesMap = Collections.emptyMap();
        }
        seriesIdList.forEach(seriesId -> {
            SeriesSubscribeRecommendedCarResponse.Result.Serieslist.Builder itemBuilder = SeriesSubscribeRecommendedCarResponse.Result.Serieslist.newBuilder();
            List<SpecEntity> specEntities = specsGrpSeriesIdMap.get(seriesId);
            int maxPriceGap = 0;

            for (SpecEntity specEntity : specEntities) {
                Integer specId = specEntity.getId();
                int specGuidePrice = specGuidePriceMap.getOrDefault(specId, 0);
                int specDealerPrice = specCurDealerPriceMap.getOrDefault(specId, 0);
                if (specGuidePrice > 0 && specDealerPrice > 0) {
                    maxPriceGap = Math.max(maxPriceGap, specGuidePrice - specDealerPrice);
                }
            }
            Integer source = seriesIdSourceMap.get(seriesId);
            String subtitle = "";
            String newsText = "";
            SeriesSubscribeRecommendedCarResponse.Result.Typeinfo.Builder typeInfoBuilder = SeriesSubscribeRecommendedCarResponse.Result.Typeinfo.newBuilder();
            SeriesDetailDto detailDto = onSaleSeriesDetailMap.get(seriesId);
            itemBuilder.setSourceid(source.toString());
            itemBuilder.setSeriesid(seriesId.toString());
            itemBuilder.setSeriesname(detailDto.getName());
            itemBuilder.setSerieslogo(detailDto.getPngLogo());
            itemBuilder.setBtntitle("立即订阅");
            boolean hasNews = hasNewsSeriesMap.containsKey(seriesId);
            itemBuilder.setNewsname(hasNews ? "看动态" : StrPool.EMPTY);
            itemBuilder.setNewslinkurl(hasNews ? String.format(rnPrefixScheme, CommonHelper.encodeUrl(String.format(rnUrlScheme, seriesId, CommonHelper.encodeUrl(detailDto.getName().replace("+", "%20") + "动态")))) : StrPool.EMPTY);
            itemBuilder.setStatus(0);
            itemBuilder.setScheme(String.format(seriesMainScheme, seriesId));
            Map<String, String> pvArgs = new HashMap<>(2);
            pvArgs.put("seriesid", seriesId.toString());
            pvArgs.put("source", source.toString());
            itemBuilder.setSeriespvitem(Pvitem.newBuilder()
                    .putAllArgvs(pvArgs)
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscardrecommendseries_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("")));
            itemBuilder.setBtnpvitem(Pvitem.newBuilder()
                    .putAllArgvs(pvArgs)
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_trendscardrecommendsubscribe_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("")));
            itemBuilder.setCardpvitem(Pvitem.newBuilder()
                    .putAllArgvs(pvArgs)
                    .setClick(Pvitem.Click.newBuilder().setEventid(""))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_trendscardrecommend_show")));
            switch (source) {
                case 1 -> {
                    DtNewCarAttentionDto attentionDto = attrNewCarRankMap.get(seriesId);
                    subtitle = String.format(recommendScheme, attentionDto.getRankNum());
                    typeInfoBuilder.setType(1);
                    typeInfoBuilder.setTagname(attentionDto.getSeriesTag());
                    if (detailDto.getMinPrice() > 0) {
                        newsText = rankSaleCountMap.containsKey(seriesId)
                                ? String.format("%d月销量%d辆", lastMonthValue, rankSaleCountMap.get(seriesId))
                                : "销量未公布";
                    } else {
                        newsText = "上市价格未公布";
                    }
                }
                case 2 -> {
                    subtitle = "最近浏览过";
                    if (rankSaleCountMap.containsKey(seriesId)) {
                        newsText = "下月销量如何？";
                        typeInfoBuilder.setType(2);
                        typeInfoBuilder.setSaletext(String.format("%d月销量", lastMonthValue));
                        typeInfoBuilder.setSalevalue(String.format("%d辆", rankSaleCountMap.get(seriesId)));
                    } else if (maxPriceGap > 0) {
                        typeInfoBuilder.setType(3);
                        typeInfoBuilder.setPricedowntext("最高降幅");
                        typeInfoBuilder.setPricedownvalue(maxPriceGap < 5000 ? maxPriceGap + "元" : CommonHelper.getPriceInfo(maxPriceGap));
                        newsText = "后续价格走势？";
                    } else {
                        typeInfoBuilder.setType(2);
                        typeInfoBuilder.setSaletext("销量未公布");
                        newsText = "下月销量如何？";
                    }
                }
            }
            itemBuilder.setTypeinfo(typeInfoBuilder);
            itemBuilder.setSubtitle(subtitle);
            itemBuilder.setNewstext(newsText);
            itemList.add(itemBuilder.build());
        });
        SubscribeConfig.NoScribeInfo noScribeInfo = seriesSubscribeConfig.getNoScribeInfo();
        return SeriesSubscribeRecommendedCarResponse.newBuilder()
                .setResult(SeriesSubscribeRecommendedCarResponse.Result.newBuilder()
                        .setTitle(noScribeInfo.getTitle())
                        .setSubtitle(noScribeInfo.getSubtitle())
                        .setDescribe(noScribeInfo.getDescribe())
                        .addAllSerieslist(itemList)
                        .setBottombtninfo(
                                SeriesSubscribeRecommendedCarResponse.Result.Bottombtninfo.newBuilder().setBtntitle("查看已订阅动态")
                                        .setPvitem(Pvitem.newBuilder()
                                                .setShow(Pvitem.Show.newBuilder().setEventid("car_trends_view_show"))
                                                .setClick(Pvitem.Click.newBuilder().setEventid("car_trends_view_click"))
                                        )
                        )).build();
    }


    public List<SpecCityPriceHistoryDto> getCurrentPriceByCityAndSpecIdList(int cityId, List<Integer> specIdList) {
        if (!org.springframework.util.CollectionUtils.isEmpty(specIdList) && cityId > 0) {
            String specIds = specIdList.stream().map(Objects::toString).collect(Collectors.joining(StrPool.COMMA));
            List<SpecCityPriceHistoryEntity> resultList = specCityPriceHistoryMapper.selectByCityIdAndSpecIdIn(cityId, specIds);
            return resultList.stream().map(x -> {
                SpecCityPriceHistoryDto dto = new SpecCityPriceHistoryDto();
                dto.setCityId(x.getCityId());
                dto.setSpecId(x.getSpecId());
                List<SpecCityPriceHistoryDto.SpecCityPriceItemDto> specCityPriceHisDtos = JSONArray.parseArray(x.getData(), SpecCityPriceHistoryDto.SpecCityPriceItemDto.class);
                dto.setList(Collections.singletonList(specCityPriceHisDtos.get(specCityPriceHisDtos.size() - 1)));
                return dto;
            }).toList();
        }
        return Collections.emptyList();
    }
}
