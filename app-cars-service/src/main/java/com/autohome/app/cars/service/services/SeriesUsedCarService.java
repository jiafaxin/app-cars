package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.*;
import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.ApiAutoAppShClient;
import com.autohome.app.cars.apiclient.che168.dtos.CitySeriesCarsWithPic;
import com.autohome.app.cars.apiclient.che168.dtos.UsedCarDetailResult;
import com.autohome.app.cars.apiclient.che168.dtos.UsedCarSearchResult;
import com.autohome.app.cars.common.utils.ClientSignUtil;
import com.autohome.app.cars.common.utils.PriceUtil;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.SpecYearNewComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecGroupOfSeriesDto;
import com.autohome.app.cars.service.components.che168.SeriesKeepValueComponent;
import com.autohome.app.cars.service.components.che168.SeriesYearCityPriceComponent;
import com.autohome.app.cars.service.components.che168.dtos.KeepValueSeriesInfo;
import com.autohome.app.cars.service.components.che168.dtos.PriceRangeInfo;
import com.autohome.app.cars.service.components.che168.dtos.SeriesYearCityPriceInfo;
import com.autohome.app.cars.service.services.dtos.UsedCarFilterConfig;
import com.autohome.app.cars.service.services.dtos.UsedCarInfoDto;
import com.google.protobuf.GeneratedMessageV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @author zhangchengtao
 * @date 2024/10/28 16:35
 */
@Service
@Slf4j
public class SeriesUsedCarService {

    @Autowired
    private ApiAutoAppShClient apiAutoAppShClient;

    @Autowired
    private Api2scautork2Client api2scautork2Client;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SpecYearNewComponent specYearNewComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    private SeriesKeepValueComponent seriesKeepValueComponent;

    @Autowired
    private SeriesYearCityPriceComponent seriesYearCityPriceComponent;

    /**
     * 二手车顶部筛选项
     */
    @Value("#{T(com.autohome.app.cars.service.services.dtos.UsedCarFilterConfig).format('${used_car_filter_config:[]}')}")
    private List<UsedCarFilterConfig> usedCarFilterConfigList;


    private static final String APP_KEY = "3J4a2P1Q8W9K7L5T6M0N2V1U4B8Z6Y7X0";

    private static final String DEFAULT_BG_COLOR = "#F5F6FA";

    private static final String MARK_TEMPLATE = "%s/%s万公里%s";

    private static final String DEFAULT_TXT_COLOR = "#828CA0";


    /**
     * 查询二手车车源列表
     *
     * @param request 请求参数
     * @return UsedCarInfoDto
     */
    public UsedCarInfoDto getUsedCarListInfo(SeriesUsedCarTabRequest request) {
        int provinceId = request.getCityid() / 10000 * 10000;
        SeriesDetailDto detailDto = seriesDetailComponent.get(request.getSeriesid());

        List<UsedCarSearchResult.CarDTO> carList = new ArrayList<>();
        List<UsedCarSearchResult.YearDTO> yearList = new ArrayList<>();
        Map<Integer, Integer> specYearMinGuidePriceMap = new HashMap<>();
        AtomicReference<String> priceInfo = new AtomicReference<>("— —");
        AtomicInteger totalPageCount = new AtomicInteger(0);

        String appId = request.getPm() == 1 ? "main2sc.ios" : "main2sc.android";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("_appid", appId);
        paramMap.put("pageindex", String.valueOf(request.getPageindex()));
        paramMap.put("pagesize", String.valueOf(request.getPagesize()));
        paramMap.put("cid", String.valueOf(request.getCityid()));
        paramMap.put("pid", String.valueOf(provinceId));
        paramMap.put("brandid", String.valueOf(detailDto.getBrandId()));
        paramMap.put("seriesid", String.valueOf(request.getSeriesid()));
        paramMap.put("seriesyearid", request.getSeriesyearid());
        paramMap.put("price", request.getPrice());
        paramMap.put("agerange", request.getAge());
        paramMap.put("mileage", request.getMileage());
        paramMap.put("deviceid", request.getDeviceid());
        paramMap.put("sort", String.valueOf(request.getSort()));
        List<CompletableFuture<?>> taskList = new ArrayList<>();


        String sign = ClientSignUtil.createSign(paramMap, APP_KEY);

        if (request.getPageindex() == 1) {
            taskList.add(getSpecYearMinGuidePrice(request.getSeriesid()).thenAccept(specYearMinGuidePriceMap::putAll));
            taskList.add(getPrice(request.getCityid(), request.getSeriesid()).thenAccept(priceInfo::set));
        }
        taskList.add(apiAutoAppShClient.search(appId, request.getPageindex(), request.getPagesize(), request.getCityid(), provinceId, detailDto.getBrandId(), request.getSeriesid(), request.getSeriesyearid(), request.getPrice(), request.getAge(), request.getMileage(), request.getDeviceid(), request.getSort(), sign)
                .thenAccept(searchResult -> {
                    if (Objects.nonNull(searchResult) && Objects.nonNull(searchResult.getResult())) {
                        // 由于search接口 totalcount 和 pagecount 不准确, 固当search接口返回数据量不足10条时, 认为已到达最后一页, 拼接推荐车源, 若大于10条, 则将推荐车源放到下一页
                        totalPageCount.set(searchResult.getResult().getPagecount() + (searchResult.getResult().getCarlist().size() < 10 ? 0 : 1));
                        if (request.getPageindex() > totalPageCount.get()) {
                            return;
                        }
                        carList.addAll(searchResult.getResult().getCarlist());
                        // 年代款
                        if (!CollectionUtils.isEmpty(searchResult.getResult().getSylist())) {
                            yearList.addAll(searchResult.getResult().getSylist());
                        }
                    }
                })
                .exceptionally(e -> {
                    log.error("search error", e);
                    return null;
                }));
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        if (carList.size() < 10) {
            totalPageCount.set(request.getPageindex());
            // 获取周边推荐
            CompletableFuture<List<UsedCarSearchResult.CarDTO>> peripheryFuture = getPeriphery(request, provinceId, appId, detailDto);
            carList.addAll(peripheryFuture.join());
        }

        return new UsedCarInfoDto(totalPageCount.get(), priceInfo.get(), carList, yearList, specYearMinGuidePriceMap);
    }


    /**
     * 填充列表数据
     *
     * @param carList       车源列表
     * @param resultBuilder Builder
     * @param fromType      来源标记:  1: 车系页二手车Tab 2: 保值率页面二手车列表
     */
    public void buildUsedCarTab(List<UsedCarSearchResult.CarDTO> carList, SeriesUsedCarTabResponse.Result.Builder resultBuilder, int fromType) {
        if (!carList.isEmpty()) {
            carList.forEach(carDto -> {
                SeriesUsedCarTabResponse.Result.List.Builder listBuilder = SeriesUsedCarTabResponse.Result.List.newBuilder();
                SeriesUsedCarTabResponse.Result.Data.Builder dataBuilder = SeriesUsedCarTabResponse.Result.Data.newBuilder();
                dataBuilder.setRdpvinfo(SeriesUsedCarTabResponse.Result.Data.RdPvInfo.newBuilder().build());
                dataBuilder.setHasvideo(StringUtils.hasLength(carDto.getVideourl()) ? 1 : 0);
                dataBuilder.setInfoid(carDto.getInfoid());
                dataBuilder.setIsrecommend(carDto.getIsrecommend());
                // 推荐周边车源添加城市名称
                if (fromType == 1 && carDto.getIsrecommend() == 1) {
                    dataBuilder.setLocation(carDto.getCname());
                }
                dataBuilder.setLinkurl(carDto.getUrl());
                dataBuilder.setSpecname(carDto.getCarname());
                dataBuilder.setSpecimage(carDto.getImageurl());
                // 图片标签
                SeriesUsedCarTabResponse.Result.ImagePicTag.Builder tagBuilder = SeriesUsedCarTabResponse.Result.ImagePicTag.newBuilder();
                UsedCarSearchResult.CarDTO.CartagsDTO carTags = carDto.getCartags();
                // 严选车
                if (Objects.nonNull(carDto.getConsignment()) && carDto.getConsignment().getIsconsignment() == 1) {
                    tagBuilder.setImgurl("https://dx.autoimg.cn/2sc/2025rnw_sdcard_images/autohome_icon_2024_0307.png?format=webp");
                    tagBuilder.setImagewidth(21);
                    tagBuilder.setImageheight(21);
                    dataBuilder.setImagepictag(tagBuilder);
                }

                if (Objects.nonNull(carTags)) {
                    // 左上角标签
                    if (!carDto.getCartags().getP1().isEmpty()) {
                        UsedCarSearchResult.CarDTO.CartagsDTO.TagDTO tagDTO = carDto.getCartags().getP1().get(0);
                        dataBuilder.setImagetxttag(SeriesUsedCarTabResponse.Result.TagInfo.newBuilder()
                                .setName(tagDTO.getTitle()).setStyle(
                                        SeriesUsedCarTabResponse.Result.Style.newBuilder()
                                                .setBgcolor(getTagColor(tagDTO.getBg_color(), tagDTO.getBg_color_end()))
                                                .setTxtcolor(Objects.nonNull(tagDTO.getFont_color()) ? tagDTO.getFont_color() : DEFAULT_TXT_COLOR)));
                    }
                    // 车源名称左边标签
                    if (!carDto.getCartags().getP2().isEmpty()) {
                        UsedCarSearchResult.CarDTO.CartagsDTO.TagDTO tagDTO = carDto.getCartags().getP2().get(0);
                        dataBuilder.setSpecnametag(SeriesUsedCarTabResponse.Result.TagInfo.newBuilder()
                                .setName(tagDTO.getTitle()).setStyle(
                                        SeriesUsedCarTabResponse.Result.Style.newBuilder()
                                                .setBgcolor(getTagColor(tagDTO.getBg_color(), tagDTO.getBg_color_end()))
                                                .setTxtcolor(Objects.nonNull(tagDTO.getFont_color()) ? tagDTO.getFont_color() : DEFAULT_TXT_COLOR)));
                    }

                    // 设置价格格式
                    if (StringUtils.hasLength(carDto.getAct_discount())) {
                        // 券后价
                        dataBuilder.setPrice(carDto.getAct_discount() + "万");
                        dataBuilder.setSavetitlepre("券后");
                        dataBuilder.setSavetitleline(1);
                        dataBuilder.setSavetitle(carDto.getPrice() + "万");
                    } else if (StringUtils.hasLength(carDto.getDownpayment())) {
                        // 首付
                        dataBuilder.setPrice(carDto.getPrice() + "万");
                        dataBuilder.setSavetitle(carDto.getDownpayment() + "万首付");
                    } else if (StringUtils.hasLength(carDto.getSaveprice())) {
                        dataBuilder.setPrice(carDto.getPrice() + "万");
                        dataBuilder.setSavetitle("比新车省");
                        dataBuilder.setSaveprice(carDto.getSaveprice().replace("已降", StrPool.EMPTY));
                    } else {
                        dataBuilder.setPrice(carDto.getPrice() + "万");
                    }

                    // 普通标签位置
                    if (!carDto.getCartags().getP4().isEmpty()) {
                        carDto.getCartags().getP4().stream().limit(4).forEach(tagDTO -> {
                            // 特殊处理蓝底蓝字标签颜色
                            if ("#0088FF".equals(tagDTO.getFont_color()) && "#E5F3FF".equals(tagDTO.getBg_color()) && "#E5F3FF".equals(tagDTO.getBg_color_end())) {
                                tagDTO.setFont_color("#111E36");
                                tagDTO.setBg_color("#E5F3FF");
                                tagDTO.setBg_color_end("#E5F3FF");
                            }
                            dataBuilder.addColorsubmarks(SeriesUsedCarTabResponse.Result.TagInfo.newBuilder()
                                    .setName(tagDTO.getTitle())
                                    .setStyle(SeriesUsedCarTabResponse.Result.Style.newBuilder()
                                            .setTxtcolor(Objects.nonNull(tagDTO.getFont_color()) ? tagDTO.getFont_color() : DEFAULT_TXT_COLOR)
                                            .setBgcolor(getTagColor(tagDTO.getBg_color(), tagDTO.getBg_color_end()))));
                        });
                    }

                }

                HashMap<String, String> args = new HashMap<>(4);
                // 设置标签 列表数据: "xxxx年/xx.xx万公里/xx人咨询" 推荐车源: "xxxx年/xx.xx万公里/河北"
                if (fromType == 1) {
                    dataBuilder.setMark(String.format(MARK_TEMPLATE,
                            carDto.getFirstregyear(),
                            carDto.getMileage(),
                            carDto.getIsrecommend() == 1 ?
                                    (StringUtils.hasLength(carDto.getCname()) ? StrPool.SLASH + carDto.getCname() : StrPool.EMPTY)
                                    : carDto.getLeads() > 0 ? StrPool.SLASH + carDto.getLeads() + "人咨询" : StrPool.EMPTY));
                    args.put("seriesid", carDto.getSeriesid().toString());
                    args.put("specid", carDto.getSpecid().toString());
                    args.put("cityid", carDto.getCityid().toString());
                    args.put("pvid", "112846");

                    dataBuilder.setPvitem(Pvitem.newBuilder()
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_card_show"))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_card_click"))
                            .putAllArgvs(args)
                    );
                } else if (fromType == 2) {
                    dataBuilder.setMark(String.format(MARK_TEMPLATE,
                            carDto.getFirstregyear(),
                            carDto.getMileage(),
                            StringUtils.hasLength(carDto.getCname()) ? StrPool.SLASH + carDto.getCname() : StrPool.EMPTY));
                    args.put("seriesid", carDto.getSeriesid().toString());
                    args.put("cityid", carDto.getCityid().toString());
                    args.put("infoid", carDto.getInfoid().toString());
                    args.put("isrecommend", String.valueOf(carDto.getIsrecommend()));

                    dataBuilder.setPvitem(Pvitem.newBuilder()
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_valuerate_carcard_show"))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_valuerate_carcard_click"))
                            .putAllArgvs(args)
                    );
                }
                listBuilder.setType(11032);
                listBuilder.setData(dataBuilder);
                resultBuilder.addList(listBuilder);
            });
        }
    }


    public void getHedgeCharts(SeriesUsedCarTabRequest request, HedgeRatioChartResponse.Result.Builder resultBuilder) {
        List<CompletableFuture<Void>> tasks = new ArrayList<>(3);
        // 车系基础信息
        AtomicReference<SeriesDetailDto> seriesDetailDtoRef = new AtomicReference<>();
        // 保值率数据
        AtomicReference<KeepValueSeriesInfo> keepValueSeriesInfoRef = new AtomicReference<>();
        // 年款销量&车源数量
        AtomicReference<SeriesYearCityPriceInfo> seriesYearCityPriceInfoRef = new AtomicReference<>();
        tasks.add(seriesDetailComponent.getAsync(request.getSeriesid())
                .thenAccept(seriesDetailDtoRef::set)
                .exceptionally(e -> {
                    log.warn("查询车系详情失败", e);
                    return null;
                }));
        tasks.add(seriesKeepValueComponent.get(request.getSeriesid())
                .thenAccept(keepValueSeriesInfoRef::set)
                .exceptionally(e -> {
                    log.warn("查询保值率数据失败", e);
                    return null;
                }));
        tasks.add(seriesYearCityPriceComponent.getByCity(request.getSeriesid(), request.getCityid())
                .thenAccept(seriesYearCityPriceInfoRef::set)
                .exceptionally(e -> {
                    log.warn("查询年款价格数据失败", e);
                    return null;
                }));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        SeriesDetailDto seriesDetailDto = seriesDetailDtoRef.get();
        KeepValueSeriesInfo keepValueSeriesInfo = keepValueSeriesInfoRef.get();
        SeriesYearCityPriceInfo seriesYearCityPriceInfo = seriesYearCityPriceInfoRef.get();
        // 基础信息
        if (Objects.nonNull(seriesDetailDto)) {
            HedgeRatioChartResponse.BaseInfo.Builder baseInfoBuilder = HedgeRatioChartResponse.BaseInfo.newBuilder();
            baseInfoBuilder.setSeriesname(seriesDetailDto.getName());
            baseInfoBuilder.setPrice("指导价：" + PriceUtil.GetPriceStringDetail(seriesDetailDto.getMinPrice(), seriesDetailDto.getMaxPrice(), seriesDetailDto.getState()));
            if (seriesDetailDto.getLevelId() == 14 || seriesDetailDto.getLevelId() == 15) {
                baseInfoBuilder.setLevel("皮卡");
            } else {
                baseInfoBuilder.setLevel(seriesDetailDto.getLevelName());
            }
            baseInfoBuilder.setImage(seriesDetailDto.getPngLogo());
            baseInfoBuilder.setScheme(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107", seriesDetailDto.getId()));
            baseInfoBuilder.setBgimage("http://nfiles3.autohome.com.cn/zrjcpk10/car_bzl_bg_116800@3x.png");
            resultBuilder.setBaseinfo(baseInfoBuilder);
        }
        HedgeRatioChartResponse.HedgeRatioCard.Builder bzlCardBuilder = HedgeRatioChartResponse.HedgeRatioCard.newBuilder();
        HedgeRatioChartResponse.BzlData.Builder bzlDataBuilder = HedgeRatioChartResponse.BzlData.newBuilder();
        // 保值率曲线
        if (Objects.nonNull(keepValueSeriesInfo)) {
            Set<Integer> yearSet = new HashSet<>();
            // 车系保值率
            List<KeepValueSeriesInfo.KeepRateInfo> seriesKeepRateList = keepValueSeriesInfo.getSerieskeeprate();
            List<KeepValueSeriesInfo.KeepRateInfo> levelKeepRateList = keepValueSeriesInfo.getLevelkeeprate();
            if (!CollectionUtils.isEmpty(seriesKeepRateList) || !CollectionUtils.isEmpty(levelKeepRateList)) {
                HedgeRatioChartResponse.ChartInfo.Builder chartInfoBuilder = HedgeRatioChartResponse.ChartInfo.newBuilder();
                bzlCardBuilder.setType(30801);

                bzlDataBuilder.setTitle("保值率走势");
                bzlDataBuilder.setDetail("汽车保值率又称残值，指车辆在使用一段时间后的交易价格和原始裸车价的比值，保值率越高、该车的二手车在市场越值钱。数据定期更新，仅供参考。");
                bzlDataBuilder.setBzltitle("3年保值率");
                BigDecimal seriesMaxRate = BigDecimal.ZERO;
                BigDecimal levelMaxRate = BigDecimal.ZERO;
                boolean isSeriesListNotEmpty = !CollectionUtils.isEmpty(seriesKeepRateList);
                boolean isLevelListNotEmpty = !CollectionUtils.isEmpty(levelKeepRateList);
                // 年款List
                if (isSeriesListNotEmpty) {
                    yearSet.addAll(seriesKeepRateList.stream().map(KeepValueSeriesInfo.KeepRateInfo::getYear).toList());
                }
                if (isLevelListNotEmpty) {
                    yearSet.addAll(levelKeepRateList.stream().map(KeepValueSeriesInfo.KeepRateInfo::getYear).toList());
                }
                List<Integer> yearList = yearSet.stream().filter(x -> x <= 10).sorted(Comparator.comparing(x -> x)).toList();
                if (isSeriesListNotEmpty) {
                    // 三年保值率
                    Optional<KeepValueSeriesInfo.KeepRateInfo> threeYearsHedgeRatio = seriesKeepRateList.stream().filter(x -> x.getYear() == 3).findFirst();
                    if (threeYearsHedgeRatio.isEmpty()) {
                        return;
                    }
                    bzlDataBuilder.setBzlvalue(threeYearsHedgeRatio.get().getKeeprate().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%");
                    // 当前车系保值率曲线
                    HedgeRatioChartResponse.Lines.Builder seriesLineBuilder = HedgeRatioChartResponse.Lines.newBuilder();
                    List<HedgeRatioChartResponse.Values> seriesValuesBuilderList = new ArrayList<>(seriesKeepRateList.size());
                    seriesMaxRate = processLineValue(seriesKeepRateList, seriesValuesBuilderList, yearList);
                    seriesLineBuilder.setColor("#FF6600");
                    seriesLineBuilder.setName(seriesDetailDto.getName());
                    seriesLineBuilder.addAllValues(seriesValuesBuilderList);
                    chartInfoBuilder.addLines(seriesLineBuilder);
                }

                if (isLevelListNotEmpty) {
                    // 同级车保值率曲线
                    HedgeRatioChartResponse.Lines.Builder levelLineBuilder = HedgeRatioChartResponse.Lines.newBuilder();
                    List<HedgeRatioChartResponse.Values> levelValuesBuilderList = new ArrayList<>(levelKeepRateList.size());
                    levelMaxRate = processLineValue(levelKeepRateList, levelValuesBuilderList, yearList);
                    levelLineBuilder.setColor("#99CFFF");
                    levelLineBuilder.setName("同级车均值");
                    levelLineBuilder.addAllValues(levelValuesBuilderList);
                    chartInfoBuilder.addLines(levelLineBuilder);
                }
                // 填充X轴数据
                yearList.forEach(year -> chartInfoBuilder.addXlist(HedgeRatioChartResponse.XList.newBuilder().setName(String.format("%d年", year)).build()));
                // 最大保值率
                BigDecimal maxRate = seriesMaxRate.max(levelMaxRate);
                if (!maxRate.equals(BigDecimal.ZERO)) {
                    double rate = maxRate.doubleValue();
                    // y轴最大值
                    int yAxis = (int) (rate / 5 + (rate % 5 == 0 ? 0 : 1)) * 5;
                    int yStep = yAxis / 5;
                    for (int i = 0; i <= 5; i++) {
                        // 填充y轴数据
                        chartInfoBuilder.addYlist(HedgeRatioChartResponse.YList.newBuilder()
                                .setValue(yStep * i)
                                .setName((yStep * i) + "%")
                                .build());
                    }
                }
                bzlDataBuilder.setChartinfo(chartInfoBuilder);
            } else {
                bzlDataBuilder.setChartinfo(HedgeRatioChartResponse.ChartInfo.newBuilder());
            }
        }
        bzlCardBuilder.setData(bzlDataBuilder);
        resultBuilder.setBzlcard(bzlCardBuilder);
        // 年款曲线 保证全国和本地不同时为空
        if (Objects.nonNull(seriesYearCityPriceInfo) && (Objects.nonNull(seriesYearCityPriceInfo.getCityInfo()) || Objects.nonNull(seriesYearCityPriceInfo.getAll()))) {
            HedgeRatioChartResponse.UsedCarCard.Builder usedCarCardBuilder = HedgeRatioChartResponse.UsedCarCard.newBuilder();
            usedCarCardBuilder.setType(30802);
            HedgeRatioChartResponse.CarData.Builder carDataBuilder = HedgeRatioChartResponse.CarData.newBuilder();
            carDataBuilder.setTitle("在售车源分布");
            carDataBuilder.setDetail("数据来源于之家每日采集的车源和报价，因二手车市波动较大，数据可能延迟，页面信息仅供参考，具体交易请联系卖方，感谢理解。");
            // 本地曲线
            HedgeRatioChartResponse.ChartList.Builder localChartListBuilder = HedgeRatioChartResponse.ChartList.newBuilder();
            localChartListBuilder.setAreaid("1");
            localChartListBuilder.setArea("本地");
            if (Objects.nonNull(seriesYearCityPriceInfo.getCityInfo())) {
                // 本地曲线
                PriceRangeInfo cityInfo = seriesYearCityPriceInfo.getCityInfo();
                buildUsedCarCardBuilder(cityInfo, localChartListBuilder);
            }
            carDataBuilder.addChartlist(localChartListBuilder);

            HedgeRatioChartResponse.ChartList.Builder globalChartListBuilder = HedgeRatioChartResponse.ChartList.newBuilder();
            globalChartListBuilder.setAreaid("0");
            globalChartListBuilder.setArea("全国");
            if (Objects.nonNull(seriesYearCityPriceInfo.getAll())) {
                // 全国
                PriceRangeInfo all = seriesYearCityPriceInfo.getAll();
                // 全国曲线
                buildUsedCarCardBuilder(all, globalChartListBuilder);
            }
            carDataBuilder.addChartlist(globalChartListBuilder);

            usedCarCardBuilder.setData(carDataBuilder);
            resultBuilder.setUsedcarcard(usedCarCardBuilder);
        }
    }


    public PicUsedCarResponse getPicUsedCarList(PicUsedCarRequest request) {
        PicUsedCarResponse.Builder responseBuilder = PicUsedCarResponse.newBuilder();
        PicUsedCarResponse.Result.Builder resultBuilder = PicUsedCarResponse.Result.newBuilder();
        List<PicUsedCarResponse.List> listDtoList = new ArrayList<>();
        AtomicBoolean hasMore = new AtomicBoolean(false);
        AtomicReference<String> hasMoreScheme = new AtomicReference<>(StrPool.EMPTY);
        List<CompletableFuture<Void>> tasks = new ArrayList<>(2);
        tasks.add(api2scautork2Client.getSpecUsedCarsJumpInfoV2(request.getSeriesid(), request.getCityid(), 3).thenAccept(data -> {
            if (Objects.nonNull(data) && Objects.nonNull(data.getResult()) && StringUtils.hasLength(data.getResult().getJumpurl())) {
                hasMoreScheme.set(data.getResult().getJumpurl().replace("pvareaid=112200", "pvareaid=113045"));
            }
        }).exceptionally(e -> {
            log.warn("getSpecUsedCarsJumpInfoV2 error", e);
            return null;
        }));

        tasks.add(apiAutoAppShClient.getCitySeriesCarsWithPic(request.getCityid(), request.getSeriesid(), request.getSpecid(), 1, 40).thenAccept(data -> {
            if (Objects.nonNull(data) && !CollectionUtils.isEmpty(data.getList())) {
                if (data.getTotalcount() > 40) {
                    hasMore.set(true);
                }
                data.getList().forEach(item -> {
                    PicUsedCarResponse.List.Builder listBuilder = PicUsedCarResponse.List.newBuilder();
                    listBuilder.setSeriesid(request.getSeriesid())
                            .setSpecid(item.getSpecid()).setSpecname(item.getSpecname());
                    item.getCars().forEach(car -> {
                        PicUsedCarResponse.Cars.Builder carBuilder = PicUsedCarResponse.Cars.newBuilder();
                        carBuilder.addAllTag(car.getTag().subList(0, Math.min(3, car.getTag().size())))
                                .setInfoid(car.getInfoid())
                                .setPicCount(car.getPics().size())
                                .setCarInfo(String.format("%s/%s/%s", car.getRegdate(), car.getMileage(), car.getCityname()))
                                .setFirstPrice(StringUtils.hasLength(car.getFirprice()) ? car.getFirprice() + "万首付" : StrPool.EMPTY)
                                .setPrice(car.getPrice() + "万")
                                .setCarDetailTitle("车源详情")
                                .setTitleName("二手车价")
                                .setCarDetailScheme(car.getDetailurl());
                        List<CitySeriesCarsWithPic.ListDTO.CarsDTO.PicsDTO> pics = car.getPics();
                        pics.forEach(pic -> carBuilder.addPicList(PicUsedCarResponse.Piclist.newBuilder().setPicUrl(pic.getUrl()).setScheme(pic.getPiclisturl())));
                        listBuilder.addCars(carBuilder);
                    });
                    listDtoList.add(listBuilder.build());
                });
            } else {
                responseBuilder.setReturnCode(101);
            }
            resultBuilder.addAllList(listDtoList);
        }).exceptionally(e -> {
            log.warn("getPicUsedCarList error", e);
            responseBuilder.setReturnCode(101);
            return null;
        }));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        if (hasMore.get()) {
            resultBuilder.setHasmore(hasMore.get());
            resultBuilder.setHasmorescheme(hasMoreScheme.get());
        }
        return responseBuilder.setResult(resultBuilder).build();
    }

    private void buildUsedCarCardBuilder(PriceRangeInfo info, HedgeRatioChartResponse.ChartList.Builder listBuilder) {

        HedgeRatioChartResponse.UsedCarLines.Builder lineBuilder = HedgeRatioChartResponse.UsedCarLines.newBuilder();
        lineBuilder.setColor("#99CFFF");
        lineBuilder.addValues(HedgeRatioChartResponse.UsedCarValues.newBuilder()
                .setCount(info.getCunt())
                .setValue(BigDecimal.valueOf(info.getAvgprice()).multiply(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .setShowvalue("均价" + BigDecimal.valueOf(info.getAvgprice()).setScale(2, RoundingMode.HALF_UP) + "万")
                .setAllyear(1)
                .setMinvalue(BigDecimal.valueOf(info.getMinprice() * 10000).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .setMaxvalue(BigDecimal.valueOf(info.getMaxprice() * 10000).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .setShowminvalue(BigDecimal.valueOf(info.getMinprice()).setScale(2, RoundingMode.HALF_UP) + "万")
                .setShowmaxvalue(BigDecimal.valueOf(info.getMaxprice()).setScale(2, RoundingMode.HALF_UP) + "万")
                .build());
        BigDecimal maxPrice = BigDecimal.valueOf(info.getMaxprice());
        BigDecimal minPrice = BigDecimal.valueOf(info.getMinprice());
        listBuilder.addXlist(HedgeRatioChartResponse.UsedCarXList.newBuilder()
                .setCount(String.valueOf(info.getCunt()))
                .setName("不限年款")
                .setYear(""));
        List<PriceRangeInfo.SeriesYears> seriesYearsList = info.getSeriesyears().stream()
                .filter(x -> x.getCunt() > 0 && !x.getYearname().isEmpty())
                .sorted(Comparator.comparing(x -> Integer.parseInt(StringUtils.hasLength(x.getYearname()) ? x.getYearname() : "0")))
                .collect(Collectors.toList());
        Collections.reverse(seriesYearsList);
        for (PriceRangeInfo.SeriesYears seriesYear : seriesYearsList) {
            lineBuilder.addValues(HedgeRatioChartResponse.UsedCarValues.newBuilder()
                    .setValue(seriesYear.getAvgprice().multiply(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .setShowvalue("均价" + seriesYear.getAvgprice().setScale(2, RoundingMode.HALF_UP) + "万")
                    .setAllyear(0)
                    .setCount(seriesYear.getCunt())
                    .setMinvalue(seriesYear.getMinprice().multiply(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .setMaxvalue(seriesYear.getMaxprice().multiply(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .setShowminvalue(seriesYear.getMinprice().setScale(2, RoundingMode.HALF_UP) + "万")
                    .setShowmaxvalue(seriesYear.getMaxprice().setScale(2, RoundingMode.HALF_UP) + "万")
                    .build());
            listBuilder.addXlist(HedgeRatioChartResponse.UsedCarXList.newBuilder()
                    .setYear(seriesYear.getSeriesyearid().toString())
                    .setCount(String.valueOf(seriesYear.getCunt()))
                    .setName(seriesYear.getYearname() + "款"));
            maxPrice = maxPrice.max(seriesYear.getMaxprice()).setScale(2, RoundingMode.HALF_UP);
            minPrice = minPrice.min(seriesYear.getMinprice()).setScale(2, RoundingMode.HALF_UP);
        }
        listBuilder.addLines(lineBuilder);
        listBuilder
                .addYlist(HedgeRatioChartResponse.YList.newBuilder().setName(minPrice + "万").setValue(minPrice.multiply(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).doubleValue()))
                .addYlist(HedgeRatioChartResponse.YList.newBuilder().setName(maxPrice + "万").setValue(maxPrice.multiply(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP).doubleValue()));
    }

    /**
     * 填充列表数据
     *
     * @param cityId        城市ID
     * @param carList       车源列表
     * @param resultBuilder Builder
     */
    public void buildHedgeUsedCarList(int cityId, int pageIndex, List<UsedCarSearchResult.CarDTO> carList, SeriesHedgeRatioResponse.Result.Builder resultBuilder) {
        if (!carList.isEmpty()) {
            AtomicBoolean isRecommend = new AtomicBoolean(false);
            boolean hasNoRecommend = carList.stream().anyMatch(x -> x.getIsrecommend() == 0);
            carList.forEach(carDto -> {
                SeriesHedgeRatioResponse.Result.List.Builder listBuilder = SeriesHedgeRatioResponse.Result.List.newBuilder();
                SeriesHedgeRatioResponse.Result.Data.Builder dataBuilder = SeriesHedgeRatioResponse.Result.Data.newBuilder();
                dataBuilder.setHasvideo(StringUtils.hasLength(carDto.getVideourl()) ? 1 : 0);
                if (!isRecommend.get() && carDto.getIsrecommend() == 1) {
                    isRecommend.set(true);
                    dataBuilder.setTip("为您推荐更多车源");
                    // 当没有未推荐车源，且是第一页，则提示当前城市暂无在售车源
                    if (cityId != 0 && !hasNoRecommend && pageIndex == 1) {
                        dataBuilder.setCitytip("抱歉，当前城市暂无在售车源");
                    }
                }
                dataBuilder.setLinkurl(carDto.getUrl().replace("112862", "112902"));
                dataBuilder.setSpecname(carDto.getCarname());
                dataBuilder.setSpecimage(carDto.getImageurl());
                // 图片标签
                UsedCarSearchResult.CarDTO.CartagsDTO carTags = carDto.getCartags();
                // 严选车 图片标签
                if (Objects.nonNull(carDto.getConsignment()) && carDto.getConsignment().getIsconsignment() == 1) {
                    dataBuilder.setCornermarkinfo(SeriesHedgeRatioResponse.Result.Cornermarkinfo.newBuilder()
                            .setType(2)
                            .setName("https://dx.autoimg.cn/2sc/2025rnw_sdcard_images/autohome_icon_2024_0307.png?format=webp"));
                } else {
                    // 左上角标签
                    if (!carTags.getP1().isEmpty()) {
                        UsedCarSearchResult.CarDTO.CartagsDTO.TagDTO tagDTO = carDto.getCartags().getP1().get(0);
                        dataBuilder.setCornermarkinfo(SeriesHedgeRatioResponse.Result.Cornermarkinfo.newBuilder()
                                .setType(1)
                                .setName(tagDTO.getTitle())
                                .setStyle(SeriesHedgeRatioResponse.Result.Style.newBuilder()
                                        .setBgcolor(getTagColor(tagDTO.getBg_color(), tagDTO.getBg_color_end()))
                                        .setTxtcolor(Objects.nonNull(tagDTO.getFont_color()) ? tagDTO.getFont_color() : DEFAULT_TXT_COLOR)));
                    }
                }
                if (Objects.nonNull(carTags)) {
                    // 车源名称左边标签
                    if (!carTags.getP2().isEmpty()) {
                        UsedCarSearchResult.CarDTO.CartagsDTO.TagDTO tagDTO = carTags.getP2().get(0);
                        dataBuilder.setSpecnametag(SeriesHedgeRatioResponse.Result.Specnametag.newBuilder()
                                .setName(tagDTO.getTitle()).setStyle(
                                        SeriesHedgeRatioResponse.Result.Style.newBuilder()
                                                .setBgcolor(getTagColor(tagDTO.getBg_color(), tagDTO.getBg_color_end()))
                                                .setTxtcolor(Objects.nonNull(tagDTO.getFont_color()) ? tagDTO.getFont_color() : DEFAULT_TXT_COLOR)));
                    }

                    // 设置价格格式
                    if (StringUtils.hasLength(carDto.getAct_discount())) {
                        // 券后价
                        dataBuilder.setPrice(carDto.getAct_discount() + "万");
                        dataBuilder.setSavetitlepre("券后");
                        dataBuilder.setSavetitleline(1);
                        dataBuilder.setSavetitle(carDto.getPrice() + "万");
                    } else if (StringUtils.hasLength(carDto.getDownpayment())) {
                        // 首付
                        dataBuilder.setPrice(carDto.getPrice() + "万");
                        dataBuilder.setSavetitle(carDto.getDownpayment() + "万首付");
                    } else if (StringUtils.hasLength(carDto.getSaveprice())) {
                        dataBuilder.setPrice(carDto.getPrice() + "万");
                        dataBuilder.setSavetitle("比新车省");
                        dataBuilder.setSaveprice(carDto.getSaveprice().replace("已降", StrPool.EMPTY));
                    } else {
                        dataBuilder.setPrice(carDto.getPrice() + "万");
                    }

                    // 普通标签位置
                    if (!carDto.getCartags().getP4().isEmpty()) {
                        carDto.getCartags().getP4().stream().limit(4).forEach(tagDTO -> {
                            // 特殊处理蓝底蓝字标签颜色
                            if ("#0088FF".equals(tagDTO.getFont_color()) && "#E5F3FF".equals(tagDTO.getBg_color()) && "#E5F3FF".equals(tagDTO.getBg_color_end())) {
                                tagDTO.setFont_color("#111E36");
                                tagDTO.setBg_color("#E5F3FF");
                                tagDTO.setBg_color_end("#E5F3FF");
                            }
                            dataBuilder.addColorsubmarks(SeriesHedgeRatioResponse.Result.Colorsubmarks.newBuilder()
                                    .setName(tagDTO.getTitle())
                                    .setStyle(SeriesHedgeRatioResponse.Result.Style.newBuilder()
                                            .setTxtcolor(Objects.nonNull(tagDTO.getFont_color()) ? tagDTO.getFont_color() : DEFAULT_TXT_COLOR)
                                            .setBgcolor(getTagColor(tagDTO.getBg_color(), tagDTO.getBg_color_end()))));
                        });
                    }

                }

                HashMap<String, String> args = new HashMap<>(6);
                // 设置标签 列表数据: "xxxx年/xx.xx万公里/xx人咨询" 推荐车源: "xxxx年/xx.xx万公里/河北"

                dataBuilder.setMark(String.format(MARK_TEMPLATE,
                        carDto.getFirstregyear(),
                        carDto.getMileage(),
                        StringUtils.hasLength(carDto.getCname()) ? StrPool.SLASH + carDto.getCname() : StrPool.EMPTY));
                args.put("seriesid", carDto.getSeriesid().toString());
                args.put("specid", carDto.getSpecid().toString());
                args.put("cityid", carDto.getCityid().toString());
                args.put("infoid", carDto.getInfoid().toString());
                args.put("isSubspace", String.valueOf(carDto.getIsrecommend()));
                args.put("pvareaid", "112902");

                dataBuilder.setPvitem(Pvitem.newBuilder()
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_valuerate_carcard_show"))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_valuerate_carcard_click"))
                        .putAllArgvs(args)
                );

                listBuilder.setType(30803);
                listBuilder.setData(dataBuilder);
                resultBuilder.addList(listBuilder);
            });
        }
    }


    /**
     * 添加二手车筛选项
     *
     * @param builder response
     */
    public void addUsedCarFilter(SeriesUsedCarTabResponse.Result.Builder builder) {

        usedCarFilterConfigList.forEach(filter -> {
            SeriesUsedCarTabResponse.Result.UsedCarFilter.Builder filterBuilder = SeriesUsedCarTabResponse.Result.UsedCarFilter.newBuilder()
                    .setParameter(filter.getParameter())
                    .setName(filter.getName());
            filter.getList().forEach(filterItem -> {
                SeriesUsedCarTabResponse.Result.UsedCarFilterItem.Builder filterItemBuilder = SeriesUsedCarTabResponse.Result.UsedCarFilterItem.newBuilder()
                        .setName(filterItem.getName())
                        .setTypeid(filterItem.getTypeid())
                        .setSelected(filterItem.getSelected())
                        .setValue(filterItem.getValue());
                filterBuilder.addList(filterItemBuilder.build());
            });
            builder.addFilter(filterBuilder);
        });
    }


    /**
     * 获取周边推荐
     *
     * @param request    request
     * @param provinceId 省份ID
     * @param appId      appid
     * @param detailDto  车系详情
     */
    private CompletableFuture<List<UsedCarSearchResult.CarDTO>> getPeriphery(SeriesUsedCarTabRequest request, int provinceId, String appId, SeriesDetailDto detailDto) {
        List<UsedCarSearchResult.CarDTO> carList = new ArrayList<>();
        Map<String, String> peripheryParamMap = new HashMap<>();
        peripheryParamMap.put("_appid", appId);
        peripheryParamMap.put("cid", String.valueOf(request.getCityid()));
        peripheryParamMap.put("pid", String.valueOf(provinceId));
        peripheryParamMap.put("brandid", String.valueOf(detailDto.getBrandId()));
        peripheryParamMap.put("seriesid", String.valueOf(request.getSeriesid()));
        peripheryParamMap.put("seriesyearid", request.getSeriesyearid());
        peripheryParamMap.put("price", request.getPrice());
        peripheryParamMap.put("agerange", request.getAge());
        peripheryParamMap.put("mileage", request.getMileage());
        peripheryParamMap.put("deviceid", request.getDeviceid());
        peripheryParamMap.put("pagesize", "100");
        peripheryParamMap.put("pageindex", "1");
        return apiAutoAppShClient.getPeriphery(appId, 1, 100, request.getCityid(), provinceId, detailDto.getBrandId(), request.getSeriesid(), request.getSeriesyearid(), request.getDeviceid(), request.getPrice(), request.getAge(), request.getMileage(), ClientSignUtil.createSign(peripheryParamMap, APP_KEY))
                .thenApply(peripheryResult -> {
                    if (Objects.nonNull(peripheryResult) && Objects.nonNull(peripheryResult.getResult()) && !CollectionUtils.isEmpty(peripheryResult.getResult().getCarlist())) {
                        peripheryResult.getResult().getCarlist().forEach(carListDTO -> carListDTO.setIsrecommend(1));
                        carList.addAll(peripheryResult.getResult().getCarlist());
                    }
                    return carList;
                })
                .exceptionally(e -> {
                    log.error("get periphery error", e);
                    return null;
                });
    }

    /**
     * 获取车系下各个年代款的最低指导价
     *
     * @param seriesId 车系ID
     * @return Map<Integer, Integer>
     */
    private CompletableFuture<Map<Integer, Integer>> getSpecYearMinGuidePrice(int seriesId) {
        return specYearNewComponent.getAsync(seriesId).thenApply(specGroupOfSeriesDtoList -> {
            List<Integer> specIdList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(specGroupOfSeriesDtoList)) {
                for (SpecGroupOfSeriesDto specGroupOfSeriesDto : specGroupOfSeriesDtoList) {
                    List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> yearSpecList = specGroupOfSeriesDto.getYearspeclist();
                    yearSpecList.forEach(specGroup -> {
                        if (Objects.nonNull(specGroup) && !CollectionUtils.isEmpty(specGroup.getSpeclist())) {
                            specIdList.addAll(specGroup.getSpeclist().stream().map(SpecGroupOfSeriesDto.Spec::getSpecId).toList());
                        }
                    });
                }
            }
            return specIdList;
        }).thenApply(specIdList -> specDetailComponent.getList(specIdList).thenApply(specDetailDtoList -> {
            Map<Integer, Integer> specYearMinPriceMap = new HashMap<>();
            Map<Integer, List<SpecDetailDto>> specListGrpByYearIdMap = specDetailDtoList.stream().collect(Collectors.groupingBy(SpecDetailDto::getYearId));
            AtomicInteger allYearMinPrice = new AtomicInteger(Integer.MAX_VALUE);
            specListGrpByYearIdMap.forEach((yearId, specDetailList) -> {
                int min = specDetailList.stream().mapToInt(SpecDetailDto::getMinPrice).min().orElse(0);
                if (min > 0) {
                    allYearMinPrice.set(Math.min(allYearMinPrice.get(), min));
                }
                specYearMinPriceMap.put(yearId, min);
            });
            // 全部车型的最低指导价
            specYearMinPriceMap.put(-1, allYearMinPrice.get());
            return specYearMinPriceMap;
        }).join());
    }


    private String getTagColor(String beginColor, String endColor) {
        if (StringUtils.hasLength(beginColor) && StringUtils.hasLength(endColor)) {
            return String.format("%s/%s", beginColor, endColor);
        } else if (StringUtils.hasLength(beginColor) || StringUtils.hasLength(endColor)) {
            return StringUtils.hasLength(beginColor) ? beginColor : endColor;
        } else {
            return DEFAULT_BG_COLOR;
        }
    }


    private CompletableFuture<String> getPrice(int cityId, int seriesId) {
        return seriesYearCityPriceComponent.getByCity(seriesId, cityId)
                .thenApply(result -> {
                    String price = "— —";
                    if (Objects.nonNull(result.getCityInfo())) {
                        if (Objects.nonNull(result.getCityInfo().getMinprice()) && result.getCityInfo().getMinprice() > 0) {
                            price = BigDecimal.valueOf(result.getCityInfo().getMinprice()).setScale(2, RoundingMode.HALF_UP) + "万起";
                        } else if (Objects.nonNull(result.getAll()) && result.getAll().getMinprice() > 0) {
                            price = BigDecimal.valueOf(result.getAll().getMinprice()).setScale(2, RoundingMode.HALF_UP) + "万起";
                        }
                    }
                    return price;
                })
                .exceptionally(e -> {
                    log.warn("查询年款价格数据失败", e);
                    return null;
                });
    }


    private CompletableFuture<UsedCarDetailResult> getUsedCarDetail(String appId, int seriesId, int cityId, int provinceId, String sign) {
        return apiAutoAppShClient.seriesDetail(appId, seriesId, cityId, provinceId, sign).thenApply(detailResultModel -> {
            if (Objects.nonNull(detailResultModel) && Objects.nonNull(detailResultModel.getResult())) {
                return detailResultModel.getResult();
            }
            return null;
        }).exceptionally(e -> {
            log.warn("getMainTabBuilder error", e);
            return null;
        });
    }

    private BigDecimal processLineValue(List<KeepValueSeriesInfo.KeepRateInfo> keepRateList, List<HedgeRatioChartResponse.Values> list, List<Integer> yearList) {
        BigDecimal maxBzlValue = BigDecimal.ZERO;
        // 最多只取10年的保值率
        Map<Integer, BigDecimal> yearRateMap = keepRateList.stream().filter(x -> x.getYear() <= 10).collect(Collectors.toMap(KeepValueSeriesInfo.KeepRateInfo::getYear, KeepValueSeriesInfo.KeepRateInfo::getKeeprate));
        for (int year : yearList) {
            if (yearRateMap.containsKey(year)) {
                // 计算百分比
                BigDecimal rate = yearRateMap.get(year).multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)).setScale(2, RoundingMode.HALF_UP);
                list.add(HedgeRatioChartResponse.Values.newBuilder()
                        .setValue(rate.doubleValue())
                        .setShowvalue(rate + "%")
                        .build());
                maxBzlValue = maxBzlValue.max(rate);
            } else {
                // 有年款,但没有保值率
                list.add(HedgeRatioChartResponse.Values.newBuilder().setValue(0).setShowvalue("").build());
            }

        }
        return maxBzlValue;
    }
}
