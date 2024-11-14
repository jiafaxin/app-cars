package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import autohome.rpc.car.app_cars.v1.carbase.SeriesSpecListBaseInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.SeriesSpecListBaseInfoResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSeriesCanAskPriceResult;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecCanAskPriceNewApiResult;
import com.autohome.app.cars.apiclient.dealer.dtos.SListAreaButtonResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.EnergyTypesEnum;
import com.autohome.app.cars.common.enums.SeriesStateEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.components.car.SeriesAttentionComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.SpecYearNewComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecGroupOfSeriesDto;
import com.autohome.app.cars.service.components.che168.SeriesCityHotUsedCarComponent;
import com.autohome.app.cars.service.components.che168.SeriesCityUsedCarSpecYearComponent;
import com.autohome.app.cars.service.components.che168.SpecCityUsedCarPriceComponent;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityHotUsedCarDto;
import com.autohome.app.cars.service.components.che168.dtos.SeriesCityUsedCarSpecYearDto;
import com.autohome.app.cars.service.components.che168.dtos.SpecCityUsedCarDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.SeriesDriveComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.uv.EsSeriesUvComponent;
import com.autohome.app.cars.service.components.uv.dto.EsSeriesUvItemDto;
import com.autohome.app.cars.service.services.dtos.SeriesConsultConfigDto;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.autohome.app.cars.common.utils.CommonHelper.df02;

@Service
public class SeriesSpecListService {

    private static final Logger log = LoggerFactory.getLogger(SeriesSpecListService.class);

    @Autowired
    SeriesDriveComponent seriesDriveComponent;

    @Autowired
    SeriesCityHotUsedCarComponent seriesCityHotUsedCarComponent;

    @Autowired
    private SeriesAttentionComponent seriesAttentionComponent;

    @Autowired
    private SpecCityUsedCarPriceComponent specUsedCarPriceComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SeriesCityUsedCarSpecYearComponent seriesCityUsedCarSpecYearComponent;

    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    @Autowired
    DealerApiClient dealerApiClient;
    @Autowired
    SpecYearNewComponent specYearComponent;
    @Autowired
    EsSeriesUvComponent seriesUvComponent;

    @Value("${askprice_tesla:}")
    private String teslaConfig;

    @Value("${series_consult_config:}")
    private String seriesConsultConfig;
    @Value("${spec_bottom_title_config:}")
    private String specbottomtitleConfig;
    /**
     * 车型列表
     */
    public CompletableFuture<SeriesSpecListBaseInfoResponse.Result.SpecInfo> getSpecInfo(SeriesDetailDto seriesDetailDto,
                                                                                         SeriesSpecListBaseInfoRequest request) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.Builder specInfo = SeriesSpecListBaseInfoResponse.Result.SpecInfo.newBuilder();
        int cityId = request.getCityid();
        int seriesId = seriesDetailDto.getId();
        int seriesState = seriesDetailDto.getState();
        int tagId = request.getTagid();
        String tagName = request.getTagname();
        String pluginversion = request.getPluginversion();
        //品库车型列表
        CompletableFuture<List<SpecGroupOfSeriesDto>> specYearFuture = specYearComponent.getAsync(request.getSeriesid());
        //热门二手车
        CompletableFuture<SeriesCityHotUsedCarDto> scSeries = seriesState == 40 ? seriesCityHotUsedCarComponent.get(seriesId, cityId).thenApply(x -> x).exceptionally(e -> null) : CompletableFuture.completedFuture(null);
        //CompletableFuture<SeriesCityUsedCarDto> scMoreUrlFuture = seriesState == 40 ? seriesCityUsedCarComponent.get(seriesDetailDto.getId(), cityId) : CompletableFuture.completedFuture(null);
        // 新老打通—车系页 https://doc.autohome.com.cn/docapi/page/share/share_zaMppkmoxk
        int usedCarSpecId = CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8") ? request.getUsedcarspecid() : 0;
        //各二级tab下的数据处理
        return CompletableFuture.allOf(specYearFuture, scSeries).thenApply(x -> {
            //排序列表
            List<String> orderList = Arrays.asList("默认排序", "价格从低到高", "价格从高到低", "关注度从高到低");
            for (int i = 0; i < orderList.size(); i++) {
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.OrderList.Builder sortSpec = SeriesSpecListBaseInfoResponse.Result.SpecInfo.OrderList.newBuilder();
                sortSpec.setIndex(i);
                sortSpec.setName(orderList.get(i));
                specInfo.addOrderlist(sortSpec);
            }
            SeriesCityHotUsedCarDto scSeriesList = scSeries.join();
            //添加热门二手车tab
            if (scSeriesList != null) {
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.YearList.Builder yearSpec =
                        SeriesSpecListBaseInfoResponse.Result.SpecInfo.YearList.newBuilder();
                yearSpec.setYearname("热门二手车");
                yearSpec.setYearvalue(1);
                specInfo.addYearlist(0, yearSpec);
            }
            List<SpecGroupOfSeriesDto> specYearDto = specYearFuture.join();
            //添加年代款列表
            if (specYearDto != null) {
                specYearDto.stream().forEach(year -> {
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.YearList.Builder yearSpec = SeriesSpecListBaseInfoResponse.Result.SpecInfo.YearList.newBuilder();
                    yearSpec.setYearname(year.getYearname());
                    yearSpec.setYearvalue(specInfo.getYearlistList().size() == 0 ? 1 : year.getYearvalue());
                    specInfo.addYearlist(yearSpec);
                });
            }
            if (specInfo.getYearlistList() == null || specInfo.getYearlistList().size() == 0) {
                return specInfo.build();
            }
            //挑选中的tab
            SeriesSpecListBaseInfoResponse.Result.SpecInfo.YearList selectTab;
            //默认第一个tab时，端上不传tagname,兼容下取第一个
            if (tagId == 1 && StringUtils.isEmpty(tagName)) {
                selectTab = specInfo.getYearlistList().stream().findFirst().get();
            } else {
                selectTab = specInfo.getYearlistList().stream().filter(yearList -> yearList.getYearname().equals(tagName)).findFirst().orElse(null);
            }
            //还没有选中的tab就直接返回空了
            if (selectTab == null) {
                return specInfo.build();
            }
            //处理热门二手车
            if ("热门二手车".equals(selectTab.getYearname())) {
                specInfo.addSpeclist(init2scData(seriesDetailDto, scSeriesList, tagId, cityId, request.getUsedcarbtnstyleab(),request.getPluginversion()));
            } else {
                Optional<SpecGroupOfSeriesDto> first = specYearDto.stream().filter(year -> year.getYearname().equals(selectTab.getYearname())).findFirst();
                if (first.isPresent()) {
                    SpecGroupOfSeriesDto specGroupOfSeriesDto = first.get();
                    if (Arrays.asList("在售", "即将销售").contains(specGroupOfSeriesDto.getYearname()) ||
                            (specGroupOfSeriesDto.getYearname().contains("款") && Arrays.asList(10, 20, 30).contains(specGroupOfSeriesDto.getYearstate()))) {//在售
                        specInfo.addSpeclist(onSaleSpecData(specGroupOfSeriesDto, seriesDetailDto, tagId, usedCarSpecId));
                    } else if ("未售".equals(specGroupOfSeriesDto.getYearname())) {//未售
                        specInfo.addSpeclist(initNotSaleSpecListV2(specGroupOfSeriesDto, seriesDetailDto, tagId));
                    } else if (specGroupOfSeriesDto.getYearname().contains("款") && (specGroupOfSeriesDto.getYearstate() == 40)) {//停售年代款
                        specInfo.addSpeclist(initStopYearData(specGroupOfSeriesDto, seriesDetailDto, tagId, pluginversion, usedCarSpecId));
                    } else {//排量、座位
                        specInfo.addSpeclist(initPaiLiangAndSeatData(specGroupOfSeriesDto, seriesDetailDto, tagId ));
                    }
                }
            }
            // 这之后的逻辑主要是为车型列表赋值非产品库的数据
            if (!CollectionUtils.isEmpty(specInfo.getSpeclistBuilderList())
                    && !CollectionUtils.isEmpty(specInfo.getSpeclistBuilderList().get(0).getYearspeclistBuilderList())
                    && !CollectionUtils.isEmpty(specInfo.getSpeclistBuilderList().get(0).getYearspeclistBuilderList().get(0).getSpeclistBuilderList())) {
                buildSpecInfoOtherParams(specInfo, seriesDetailDto, specYearDto, selectTab.getYearname(), cityId, request.getPm(), request.getPluginversion(), request.getZixunabtest(), request.getAttentionabtest(), request.getDealer400Ab() ,request.getUsedcarbtnstyleab());
                initDefaultPvareaidValue(specInfo);
            }
            return specInfo.build();
        }).exceptionally(e -> {
            log.error("getSpecInfo error", e);
            return specInfo.build();
        });
    }

    private void buildSpecInfoOtherParams(SeriesSpecListBaseInfoResponse.Result.SpecInfo.Builder specInfo,
                                          SeriesDetailDto seriesDetailDto,
                                          List<SpecGroupOfSeriesDto> specYearDto,
                                          String tagName,
                                          int cityId,
                                          int pm,
                                          String pluginversion,
                                          String zixunabtest,
                                          String attentionabtest,
                                          String dealer400ab,
                                          String usedcarbtnstyleab) {
        int seriesId = seriesDetailDto.getId();
        int brandId = seriesDetailDto.getBrandId();
        int newEnergyType = seriesDetailDto.getEnergytype();
        // 车型列表的所有车型Id
        List<Integer> specIdList = specInfo.getSpeclistBuilderList().stream()
                .flatMap(e -> e.getYearspeclistBuilderList().stream())
                .flatMap(y -> y.getSpeclistBuilderList().stream())
                .map(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder::getId)
                .toList();
        // 将需要用到的组件数据提前准备
        List<CompletableFuture> preparationTasks = new ArrayList<>();
        AtomicReference<Map<Integer, SpecCityUsedCarDto>> specUsedCarPriceInfoMapRef = new AtomicReference<>();
        AtomicReference<SeriesAttentionDto> seriesAttentionDtoRef = new AtomicReference<>();
        AtomicReference<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceDtoMapRef = new AtomicReference<>();
        AtomicReference<SeriesCityAskPriceDto> seriesCityAskPriceDtoRef = new AtomicReference<>();
        List<SListAreaButtonResult.ButtonListDTO> areaButtonList = Collections.synchronizedList(new ArrayList<>());
        AtomicReference<Map<Integer, DealerSpecCanAskPriceNewApiResult.Specs>> dealerSpecCanAskPriceNewMapRef =
                new AtomicReference<>();
        preparationTasks.add(specUsedCarPriceComponent.getList(specIdList, cityId)
                .thenAccept(e -> {
                            if (!CollectionUtils.isEmpty(e)) {
                                specUsedCarPriceInfoMapRef.set(e.stream().filter(Objects::nonNull).collect(Collectors.toMap(
                                        SpecCityUsedCarDto::getSpecid, Function.identity(), (k1, k2) -> k2)));
                            }
                        }
                ).exceptionally(e -> null));
        preparationTasks.add(seriesAttentionComponent.get(seriesId)
                .thenAccept(seriesAttentionDtoRef::set).exceptionally(e -> null));
        preparationTasks.add(specCityAskPriceComponent.getListAsync(specIdList, cityId)
                .thenAccept(e -> {
                            if (!CollectionUtils.isEmpty(e)) {
                                specCityAskPriceDtoMapRef.set(e.stream().filter(Objects::nonNull).collect(Collectors.toMap(
                                        SpecCityAskPriceDto::getSpecId, Function.identity(), (k1, k2) -> k2)));
                            }
                        }
                ).exceptionally(e -> null));
        preparationTasks.add(seriesCityAskPriceNewComponent.get(seriesId, cityId)
                .thenAccept(seriesCityAskPriceDtoRef::set).exceptionally(e -> null));
        int pageSize = 20;
        int totalPage = (specIdList.size() + (pageSize - 1)) / pageSize;
        String deviceType = "ios";
        if (pm == 1) {
            deviceType = "ios";
        } else if (pm == 2) {
            deviceType = "android";
        } else if (pm == 3) {
            deviceType = "harmony";
        }
        for (int i = 0; i < totalPage; i++) {
            int startIdx = pageSize * i;
            int toIdx = Math.min(pageSize * (i + 1), specIdList.size());
            preparationTasks.add(
                    dealerApiClient.getListSmartAreaButton(cityId, deviceType, 1,
                            StringUtils.join(specIdList.subList(startIdx, toIdx), ","),
                            pluginversion, "92f6e950_5616_4589_a7b2_0702fdb77432", "", seriesId,
                            UUID.randomUUID().toString()).thenAccept(x -> {
                        if (x != null && x.getResult() != null) {
                            x.getResult().forEach(item -> {
                                if (item.getButtonList() != null) {
                                    areaButtonList.addAll(item.getButtonList());
                                }
                            });
                        }
                    }).exceptionally(e -> {
                        log.error("listSmartAreaButton", e);
                        return null;
                    }));
        }
        preparationTasks.add(dealerApiClient.getSeriesMinpriceWithSpecs(seriesId, cityId)
                .thenAccept(e -> {
                            if (Objects.nonNull(e) && !CollectionUtils.isEmpty(e.getResult())) {
                                dealerSpecCanAskPriceNewMapRef.set(e.getResult().stream()
                                        .map(DealerSpecCanAskPriceNewApiResult::getSpecs)
                                        .flatMap(Collection::stream)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toMap(DealerSpecCanAskPriceNewApiResult.Specs::getSpecId,
                                                Function.identity(), (k1, k2) -> k2)));
                            }
                        }
                ).exceptionally(e -> null));
        CompletableFuture.allOf(preparationTasks.toArray(new CompletableFuture[0])).join();


        int yearStatus;
        if (tagName.contains("款")
                && specYearDto != null
                && !CollectionUtils.isEmpty(specYearDto)) {
            Optional<SpecGroupOfSeriesDto> optional =
                    specYearDto.stream()
                            .filter(year -> year.getYearname().equals(tagName))
                            .findFirst();
            yearStatus = optional.map(SpecGroupOfSeriesDto::getYearstate).orElse(0);
        } else {
            yearStatus = 0;
        }

        // 设置默认值
        initDefaultValue(specInfo.getSpeclistBuilderList().get(0));

        // 设置停车车型的询价信息
        initStopSaleSpecList(specInfo.getSpeclistBuilderList().get(0),
                specUsedCarPriceInfoMapRef.get(),
                seriesDetailDto,
                specCityAskPriceDtoMapRef.get(),
                seriesCityAskPriceDtoRef.get(),
                dealerSpecCanAskPriceNewMapRef.get(),
                seriesId,
                seriesDetailDto.getState(),
                yearStatus,
                cityId,
                pm,
                tagName,
                usedcarbtnstyleab,
                pluginversion);
        // 设置关注信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pluginversion", pluginversion);
        paramMap.put("attentionabtest", attentionabtest);
        initOnSaleSpecList(specInfo.getSpeclistBuilderList().get(0),
                seriesAttentionDtoRef.get(), seriesId, paramMap);
        // 设置品牌询价弹层
        initApolloBrandAskPrice(specInfo.getSpeclistBuilderList().get(0),
                newEnergyType, seriesId, brandId);
        // 设置询价按钮title和scheme协议
        initButtonInfo(specInfo.getSpeclistBuilderList().get(0),
                pm, pluginversion, seriesId, areaButtonList, dealer400ab ,usedcarbtnstyleab,cityId);
        // 针对全新车系添加咨询按钮
        initZixunInfo(specInfo.getSpeclistBuilderList().get(0), zixunabtest,
                seriesDetailDto, pm, seriesId, cityId);
    }

    private void initDefaultValue(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList) {
        specList.getYearspeclistBuilderList().forEach(s1 ->
                s1.getSpeclistBuilderList().forEach(s2 -> s2.setMinpricename("报价:")));
    }

    private void initDefaultPvareaidValue(SeriesSpecListBaseInfoResponse.Result.SpecInfo.Builder specList) {
        specList.getSpeclistBuilderList().forEach(spec ->
                spec.getYearspeclistBuilderList().forEach(s1 ->
                        s1.getSpeclistBuilderList().forEach(s2 -> {
                                    s2.setPvareaid("109553");
//                                    if (!CollectionUtils.isEmpty(s2.getSscllistBuilderList())) {
//                                        s2.getSscllistBuilderList().forEach(s3 -> s3.setPvareaid("109553"));
//                                    }
                                }
                        )
                ));

    }

    /**
     * 停售车型询价信息
     *
     * @param specList
     * @param specUsedCarPriceInfoMap
     * @param seriesDetailDto
     * @param specCityAskPriceDtoMap
     * @param seriesCityAskPriceDto
     * @param dealerSpecCanAskPriceNewMapRef
     * @param seriesId
     * @param seriesStatus
     * @param yearStatus
     * @param cityId
     * @param pm
     * @param yearName
     */
    public void initStopSaleSpecList(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                     Map<Integer, SpecCityUsedCarDto> specUsedCarPriceInfoMap,
                                     SeriesDetailDto seriesDetailDto,
                                     Map<Integer, SpecCityAskPriceDto> specCityAskPriceDtoMap,
                                     SeriesCityAskPriceDto seriesCityAskPriceDto,
                                     Map<Integer, DealerSpecCanAskPriceNewApiResult.Specs> dealerSpecCanAskPriceNewMapRef,
                                     int seriesId,
                                     int seriesStatus,
                                     int yearStatus,
                                     int cityId,
                                     int pm,
                                     String yearName,
                                     String usedcarbtnstyleab,
                                     String pluginversion) {
        try {
            if (seriesStatus == 40 || seriesStatus == 0) {
                if (yearName.contains("款")) {
                    // 这里对车型列表的各项进行的处理
                    initStopSaleSpecInfoByNewApi(specList, specUsedCarPriceInfoMap, seriesDetailDto,
                            specCityAskPriceDtoMap, seriesId, cityId, pm, yearName, usedcarbtnstyleab,pluginversion);
                }
            } else if ((seriesStatus == 20 || seriesStatus == 30) && yearStatus == 40) {
                // 在售但年贷款停售
                // 这里对车型列表的各项进行的处理
                initStopSaleSpecButSeriesOnSale(specList, specUsedCarPriceInfoMap, specCityAskPriceDtoMap,
                        seriesCityAskPriceDto, cityId, pm, seriesId,usedcarbtnstyleab,pluginversion);
            } else {
                // 这里对车型列表的各项进行的处理
                initSeriesSpecAskPriceInfoByNewAskPriceApi(specList, dealerSpecCanAskPriceNewMapRef, cityId);
                // 设置车系车型经销商最低报价
                initSeriesMinPrice(specList, specCityAskPriceDtoMap, seriesCityAskPriceDto, cityId);
            }
        } catch (Exception e) {
            log.error("设置停售的车型列表异常", e);
        }
    }

    private void initStopSaleSpecInfoByNewApi(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                              Map<Integer, SpecCityUsedCarDto> specUsedCarPriceInfoMap,
                                              SeriesDetailDto seriesDetailDto,
                                              Map<Integer, SpecCityAskPriceDto> specCityAskPriceDtoMap,
                                              int seriesId,
                                              int cityId,
                                              int pm,
                                              String yearName,
                                              String usedcarbtnstyleab,
                                              String pluginversion) {
        specList.getYearspeclistBuilderList().forEach(s1 ->
                s1.getSpeclistBuilderList().forEach(s2 -> {
                    SpecCityUsedCarDto specUsedCarPriceInfo = specUsedCarPriceInfoMap.get(s2.getId());
                    if (Objects.nonNull(specUsedCarPriceInfo)
                            && (StringUtils.isNotEmpty(specUsedCarPriceInfo.getSpecurl()))) {
                        s2.setCanaskprice(4);
                        String price = specUsedCarPriceInfo.getMinprice() > 0
                                ? df02.format(specUsedCarPriceInfo.getMinprice()) + "万起"
                                : "";
                        if ("D".equalsIgnoreCase(usedcarbtnstyleab) && CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                            if(StringUtils.isEmpty(specbottomtitleConfig)){
                                s2.setSpecbottomtitle("同款二手车");
                            }else {
                                s2.setSpecbottomtitle(specbottomtitleConfig);
                            }
                        }else if ("B".equalsIgnoreCase(usedcarbtnstyleab) && CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                            s2.setSpecbottomtitle("同款二手车"+price);
                        }else if("C".equalsIgnoreCase(usedcarbtnstyleab) && CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                            SpecCityUsedCarDto country = specUsedCarPriceComponent.getCountry((s2.getId())).join();
                            if (Objects.nonNull(country) ){
                                if ( country.getCunt() > 0){
                                    s2.setSpecbottomtitle("全国"+country.getCunt()+"辆同款二手车");
                                }else {
                                    s2.setSpecbottomtitle("暂无二手车");
                                }
                            }
                        }else {
                            s2.setSpecbottomtitle("二手车");
                        }
                        s2.setSscprice(specUsedCarPriceInfo.getMinprice() > 0
                                ? df02.format(specUsedCarPriceInfo.getMinprice()) + "万起"
                                : "");
                        s2.setSscpricename(specUsedCarPriceInfo.getMinprice() > 0 ? "二手车 " : "");
                        s2.setSpecbottomurl(specUsedCarPriceInfo.getSpecurl() + "&seriesyearid%3D"+specUsedCarPriceInfo.getSpecyearid());
                        if (s2.getState() == 40 && ((!("B".equalsIgnoreCase(usedcarbtnstyleab)
                                ||"C".equalsIgnoreCase(usedcarbtnstyleab) ||"D".equalsIgnoreCase(usedcarbtnstyleab)) &&CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0"))
                                || !CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0"))) {
                            s2.setUsedcarpricebtn(getUserCarBtnData(seriesId, s2.getId(), cityId + ""));
                        }
                    }
                }));

        // TODO chengjincheng 2024/5/24 注意拉取数据后进行替换，没有拉取数据前就在此处调用接口获取数据
        int year = Integer.parseInt(yearName.replace("款", ""));
        SeriesCityUsedCarSpecYearDto seriesCityUsedCarSpecYearDto =
                seriesCityUsedCarSpecYearComponent.get(seriesId, cityId, year).join();
        if (Objects.nonNull(seriesCityUsedCarSpecYearDto) &&
                !CollectionUtils.isEmpty(seriesCityUsedCarSpecYearDto.getList())) {
            SeriesCityUsedCarSpecYearDto finalSeriesCityUsedCarSpecYearDto = seriesCityUsedCarSpecYearDto;
            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(s2 -> {
                        SeriesCityUsedCarSpecYearDto.SUsedCarSpecList_Spec usedCarSpecList_spec =
                                finalSeriesCityUsedCarSpecYearDto.getList().stream()
                                        .filter(e -> e.getSpecid() == s2.getId())
                                        .findAny()
                                        .orElse(null);
                        if (Objects.nonNull(usedCarSpecList_spec)) {
                            usedCarSpecList_spec.getList().forEach(usedCarSpec -> {
                                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.SscInfo.Builder sscDto =
                                        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.SscInfo.newBuilder();
                                sscDto.setImage(usedCarSpec.getImageurl());
                                sscDto.setLinkurl(usedCarSpec.getUrl());
                                sscDto.setName(usedCarSpec.getCarname());
                                sscDto.setPrice(usedCarSpec.getPrice() + "万");
                                sscDto.setPricename("售价");
                                sscDto.setPvareaid("109552");
                                sscDto.setSpecid(usedCarSpec.getSpecid());
                                sscDto.setCityname(usedCarSpec.getCityname());
                                sscDto.setTags(usedCarSpec.getMileage() + "万公里/"
                                        + usedCarSpec.getRegdate().replace("年", ""));
                                if (s2.getSscllistList().size() < 5) {
                                    s2.addSscllist(sscDto.build());
                                }
                            });
                            s2.setSsclinkurl("");
                            if (usedCarSpecList_spec.getList().size() > 5) {
                                String moreUrl = String.format("autohome://usedcar/buycarlist?pvareaid=%s&s_cid=%s&scene_no=%s&brand=%s", "112432", cityId, 102,
                                        UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}",
                                                seriesDetailDto.getBrandId(), seriesDetailDto.getBrandName(), seriesDetailDto.getId(), seriesDetailDto.getName())));
                                s2.setSsclinkurl(moreUrl);
                                Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                                        .putArgvs("specid", String.valueOf(s2.getId()))
                                        .putArgvs("seriesid", String.valueOf(seriesId))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_hot_used_car_more_click"));
                                s2.setSscpvitem(pvBuilder);
                            }
                        }
                    }));

        }

        if (pm != 3) {
            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(s2 -> {
                        SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceDtoMap.get(s2.getId());
                        if (Objects.nonNull(specCityAskPriceDto)) {
                            s2.setSpecbottomtitle("获取底价");
                            s2.setCalcprice(specCityAskPriceDto.getMinPrice());
                            if (specCityAskPriceDto.getMinPrice() > 0) {
                                s2.setCanaskprice(1);
                                s2.setMinprice(df02.format(specCityAskPriceDto.getMinPrice() / 10000.0) + "万起");
                                if (s2.getNoshowprice() > specCityAskPriceDto.getMinPrice()) {
                                    s2.setDownprice(df02.format(
                                            (s2.getNoshowprice() - specCityAskPriceDto.getMinPrice()) / 10000.0)
                                            + "万");
                                }
                            }
                        }
                    }));
        }
    }

    private void initStopSaleSpecButSeriesOnSale(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                                 Map<Integer, SpecCityUsedCarDto> specUsedCarPriceInfoMap,
                                                 Map<Integer, SpecCityAskPriceDto> specCityAskPriceDtoMap,
                                                 SeriesCityAskPriceDto seriesCityAskPriceDto,
                                                 int cityId,
                                                 int pm,
                                                 int seriesId,
                                                 String usedcarbtnstyleab,
                                                 String pluginversion) {
        Map<String, String> map = new HashMap<>();
        map.put("maxspecid", "0");
        map.put("maxspecname", "");
        map.put("maxspecprice", "");

        specList.getYearspeclistBuilderList().forEach(s1 ->
                s1.getSpeclistBuilderList().forEach(s2 -> {
                    SpecCityUsedCarDto specUsedCarPriceInfo = specUsedCarPriceInfoMap.get(s2.getId());
                    if (Objects.nonNull(specUsedCarPriceInfo) && StringUtils.isNotEmpty(specUsedCarPriceInfo.getSpecurl())) {
                        s2.setCanaskprice(4);
                        String price = specUsedCarPriceInfo.getMinprice() > 0
                                ? df02.format(specUsedCarPriceInfo.getMinprice()) + "万起"
                                : "";
                        if ("D".equalsIgnoreCase(usedcarbtnstyleab)&& CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                            if(StringUtils.isEmpty(specbottomtitleConfig)){
                                s2.setSpecbottomtitle("同款二手车");
                            }else {
                                s2.setSpecbottomtitle(specbottomtitleConfig);
                            }
                        }else if ("B".equalsIgnoreCase(usedcarbtnstyleab) && CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                            s2.setSpecbottomtitle("同款二手车"+price);
                        }else if("C".equalsIgnoreCase(usedcarbtnstyleab)&& CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                            SpecCityUsedCarDto country = specUsedCarPriceComponent.getCountry((s2.getId())).join();
                            if (Objects.nonNull(country) ){
                                if ( country.getCunt() > 0){
                                    s2.setSpecbottomtitle("全国"+country.getCunt()+"辆同款二手车");
                                }else {
                                    s2.setSpecbottomtitle("暂无二手车");
                                }
                            }
                        }else {
                            s2.setSpecbottomtitle("二手车");
                        }
                        s2.setSscprice(price);
                        s2.setSscpricename(specUsedCarPriceInfo.getMinprice() > 0
                                ? "二手车 "
                                : "");

                        s2.setSpecbottomurl(specUsedCarPriceInfo.getSpecurl()  + "&seriesyearid%3D"+specUsedCarPriceInfo.getSpecyearid());

                        if (s2.getState() == 40 && ((!("B".equalsIgnoreCase(usedcarbtnstyleab)
                                ||"C".equalsIgnoreCase(usedcarbtnstyleab) ||"D".equalsIgnoreCase(usedcarbtnstyleab)) &&CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0"))
                                || !CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0"))) {
                            s2.setUsedcarpricebtn(getUserCarBtnData(seriesId, s2.getId(), cityId + ""));
                        }
                    } else {
                        if (CommonHelper.isTakeEffectVersion(pluginversion, "11.68.5") &&
                                ("B".equalsIgnoreCase(usedcarbtnstyleab) || "C".equalsIgnoreCase(usedcarbtnstyleab) || "D".equalsIgnoreCase(usedcarbtnstyleab))){
                            s2.setCanaskprice(4);
                            s2.setSpecbottomtitle("暂无二手车");
                        }else{
                            s2.setCanaskprice(7);
                            s2.setSpecbottomtitle("我要卖车");
                            String url = "https://activitym.che168.com/2023/sellonestop/main/index?leadssources=2&sourcetwo=4&sourcethree=1753&sourcename=2sc&pvareaid=112719&isonestopab=B";
                            s2.setSpecbottomurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(url));
                        }
                    }
                }));
        if (pm != 3) {
            // 存在报价，但不是本市的经销商报价
            boolean existedLocalDealer = Objects.nonNull(seriesCityAskPriceDto)
                    && ((Objects.nonNull(seriesCityAskPriceDto.getPriceType())
                    && seriesCityAskPriceDto.getPriceType() == 0)
                    || (!CollectionUtils.isEmpty(seriesCityAskPriceDto.getSaleCityList())
                    && seriesCityAskPriceDto.getSaleCityList().contains(cityId)));
            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(s2 -> {
                        SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceDtoMap.get(s2.getId());
                        if (Objects.nonNull(specCityAskPriceDto)) {
                            s2.setSpecbottomtitle("获取底价");
                            s2.setCalcprice(specCityAskPriceDto.getMinPrice());
                            if (specCityAskPriceDto.getMinPrice() > 0) {
                                s2.setCanaskprice(1);
                                s2.setMinprice(df02.format(specCityAskPriceDto.getMinPrice() / 10000.0) + "万起");
                                if (s2.getNoshowprice() > specCityAskPriceDto.getMinPrice()) {
                                    s2.setDownprice(df02.format(
                                            (s2.getNoshowprice() - specCityAskPriceDto.getMinPrice()) / 10000.0)
                                            + "万");
                                }
                            }
                        }
                        if (Objects.nonNull(specCityAskPriceDto)) {
                            if (!existedLocalDealer) {
                                s2.setIsother(1);
                                specList.setNodealertip("当地无4S店报价，以下为周边城市4S店报价");
                            }
                        }
                    }));
        }
    }

    // 设置车系下车型列表询价
    private void initSeriesSpecAskPriceInfoByNewAskPriceApi(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                                            Map<Integer, DealerSpecCanAskPriceNewApiResult.Specs> dealerSpecCanAskPriceNewMapRef,
                                                            int cityId) {
        try {
            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(s2 -> {
                        s2.setSpecbottomtitle("获取底价");
                        s2.setSpecbottomurl("");
                        s2.setCanaskprice(0);
                        if (Objects.nonNull(dealerSpecCanAskPriceNewMapRef)) {
                            DealerSpecCanAskPriceNewApiResult.Specs specAskPrice =
                                    dealerSpecCanAskPriceNewMapRef.get(s2.getId());
                            if (Objects.nonNull(specAskPrice)) {
                                s2.setCanaskprice(1);
                            }
                        } else {
                            s2.setCanaskprice(1);
                        }
                    }));
        } catch (Exception ex) {
            String exceptionMsg = String.format("ErrorMessage:%s;RequestParm:%s; exceptionStack:%s", "车型设置cpl失败",
                    "strSpecIds" + "|" + cityId, ExceptionUtils.getStackTrace(ex));
            log.error(exceptionMsg);
        }
    }

    private void initSeriesMinPrice(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                    Map<Integer, SpecCityAskPriceDto> specCityAskPriceDtoMap,
                                    SeriesCityAskPriceDto seriesCityAskPriceDto,
                                    int cityId) {
        try {
            // 存在报价，但不是本市的经销商报价
            boolean existedLocalDealer = Objects.nonNull(seriesCityAskPriceDto)
                    && ((Objects.nonNull(seriesCityAskPriceDto.getPriceType())
                    && seriesCityAskPriceDto.getPriceType() == 0)
                    || (!CollectionUtils.isEmpty(seriesCityAskPriceDto.getSaleCityList())
                    && seriesCityAskPriceDto.getSaleCityList().contains(cityId)));
            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(s2 -> {
                                SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceDtoMap.get(s2.getId());
                                if (Objects.nonNull(specCityAskPriceDto)) {
                                    s2.setCalcprice(specCityAskPriceDto.getMinPrice());
                                    s2.setMinprice(
                                            df02.format(specCityAskPriceDto.getMinPrice() / 10000.0) + "万起");

                                    if (!existedLocalDealer) {
                                        s2.setIsother(1);
                                        specList.setNodealertip("当地无4S店报价，以下为周边城市4S店报价");
                                    }
                                    if (s2.getNoshowprice() > specCityAskPriceDto.getMinPrice()) {
                                        s2.setDownprice(df02.format(
                                                (s2.getNoshowprice() - specCityAskPriceDto.getMinPrice()) / 10000.0) + "万");
                                    }
                                }
                            }
                    ));
        } catch (Exception e) {
            log.error("车系综述-获取车系车型最低价specListDAL.getMinPrieBySeriesByApi" + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 关注信息
     *
     * @param specList
     * @param seriesAttentionDto
     * @param seriesId
     */
    public void initOnSaleSpecList(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                   SeriesAttentionDto seriesAttentionDto,
                                   int seriesId,
                                   Map<String, Object> paramMap) {
        String pluginversion = (String) paramMap.get("pluginversion");
        String attentionabtest = (String) paramMap.get("attentionabtest");
        try {
            if (seriesAttentionDto != null
                    && !CollectionUtils.isEmpty(seriesAttentionDto.getSpecAttentions())) {
                int attentionSum = 0;
                if (StringUtils.isNotEmpty(pluginversion)
                        && StringUtils.isNotEmpty(attentionabtest)
                        && CommonHelper.isTakeEffectVersion(pluginversion, "11.65.0")
                        && StringUtils.equalsAnyIgnoreCase(attentionabtest, "1")) {
                    attentionSum = seriesAttentionDto.getSpecAttentions().stream().mapToInt(SeriesAttentionDto.SpecAttention::getAttention).sum();
                }
                int max = seriesAttentionDto.getSpecAttentions().stream()
                        .mapToInt(SeriesAttentionDto.SpecAttention::getAttention)
                        .summaryStatistics().getMax();
                Map<Integer, SeriesAttentionDto.SpecAttention> specAttentionMap = seriesAttentionDto.getSpecAttentions()
                        .stream().collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                                Function.identity(), (k1, k2) -> k2));
                int finalAttentionSum = attentionSum;
                specList.getYearspeclistBuilderList().forEach(s1 ->
                        s1.getSpeclistBuilderList().forEach(s2 -> {
                            SeriesAttentionDto.SpecAttention specAttention = specAttentionMap.get(s2.getId());
                            if (Objects.nonNull(specAttention)) {
                                s2.setAttentionint(specAttention.getAttention());
                                BigDecimal b1 = new BigDecimal(specAttention.getAttention());
                                BigDecimal b2 = new BigDecimal(max);
                                if (finalAttentionSum > 0) {
                                    b2 = new BigDecimal(finalAttentionSum);
                                }
                                int attention = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP)
                                        .multiply(new BigDecimal(100)).intValue();
                                if (attention > 98) {
                                    s2.setAttention(98);
                                } else {
                                    s2.setAttention(Math.max(attention, 10));
                                }
                                s2.setAttentioninfo(s2.getAttention() + "%人关注");
                            }
                        }));
            }
        } catch (Exception e) {
            String exceptionMsg = String.format("ErrorMessage:%s;RequestParm:%s;  exceptionStack:%s;",
                    "init spec attention is error", seriesId, "data is empty");
            log.error(exceptionMsg, e);
        }
        // 设置排序值
        initOrderInfo(specList, pluginversion);
    }


    private void initOrderInfo(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList, String pluginversion) {
        if (specList != null
                && !CollectionUtils.isEmpty(specList.getYearspeclistBuilderList())) {
            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(s2 -> {
                        long orderOne = 0;
                        long orderTwo = 0;
                        long orderThree = 0;
                        try {
                            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5") && 40 == s2.getState()) {
                                if (s2.getNoshowprice() > 0) {
                                    orderOne = s2.getNoshowprice();
                                } else {
                                    orderOne = 10000 * 1000 * 100;
                                }
                            } else {
                                if (StringUtils.contains(s2.getSscpricename(), "二手车")) { //二手车
                                    orderOne = (long) ((Double.parseDouble(StringUtils.substringBefore(s2.getSscprice(),
                                            "万"))) * 100000L);
                                } else {
                                    if (StringUtils.isNotEmpty(s2.getMinprice())) {
                                        orderOne = s2.getCalcprice() * 100L;
                                    } else {
                                        orderOne = 10000 * 1000 * 100;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("initOrderInfo error. speclist={}", s2.toString(), e);
                        }
                        try {
                            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5") && 40 == s2.getState()) {
                                if (s2.getNoshowprice() > 0) {
                                    orderTwo = -1 * orderOne;
                                } else {
                                    orderOne = 10000 * 1000 * 100;
                                }
                            } else {
                                if (StringUtils.isNotEmpty(s2.getMinprice())
                                        || StringUtils.isNotEmpty(s2.getSscprice())) {
                                    orderTwo = -1 * orderOne;
                                } else {
                                    orderTwo = 10000 * 1000 * 100;
                                }
                            }
                        } catch (Exception e) {
                            log.error("initOrderInfo error. speclist={}", s2, e);
                        }
                        try {
                            orderThree = ((Integer.MAX_VALUE / 10) - s2.getAttentionint())
                                    + (2030 - s2.getYear())
                                    + s2.getState();
                        } catch (Exception e) {
                            log.error("initOrderInfo error. speclist={}", s2, e);
                        }
                        s2.addOrder(0);
                        s2.addOrder(orderOne);
                        s2.addOrder(orderTwo);
                        s2.addOrder(orderThree);
                    }));
        }
    }


    /**
     * 品牌询价弹层
     *
     * @param specList
     * @param seriesStatus
     * @param seriesId
     * @param brandId
     */
    public void initApolloBrandAskPrice(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                                        int seriesStatus,
                                        int seriesId,
                                        int brandId) {
        try {
            if (seriesStatus == 20 || seriesStatus == 30) {
                JSONObject config = JSONObject.parseObject(teslaConfig);
                if (config != null) {
                    int status = config.getIntValue("onlinestatus");
                    String brandids = config.getString("brandids");
                    if (status == 0 && Arrays.asList(brandids.split(",")).contains(brandId + "")) {
                        specList.getYearspeclistBuilderList().forEach(s1 ->
                                s1.getSpeclistBuilderList().forEach(specInfo -> {
                                    if ((specInfo.getState() == 20 || specInfo.getState() == 30)) {
                                        if (specInfo.getCanaskprice() == 1 || specInfo.getCanaskprice() == 21) {
                                            specInfo.setCanaskprice(22);
                                            specInfo.setSpecbottomurl("autohome://car/tslfloating?customshowanimationtype=2&seriesid="
                                                    + seriesId + "&specid=" + specInfo.getId() + "&fromtype=1");
                                        }
                                    }
                                }));
                    }
                }
            }
        } catch (Exception e) {
            log.error("品牌询价信息异常", e);
        }
    }

    /**
     * 询价按钮title和scheme协议
     *
     * @param specList
     * @param pm
     * @param seriesId
     */
    public void initButtonInfo(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                               int pm, String pluginversion,
                               int seriesId, List<SListAreaButtonResult.ButtonListDTO> areaButtonList, String dealer400ab ,String usedcarbtnstyleab ,Integer cityId) {
        //设置询价按钮title和scheme协议
        try {
            boolean flag = specList.getYearspeclistBuilderList().stream()
                    .flatMap(s -> s.getSpeclistBuilderList().stream())
                    .map(specInfo -> {
                        if (specInfo.getCanaskprice() == 0 && specInfo.getState() == 40) {
                            return 1;
                        } else {
                            return 0;
                        }
                    })
                    .mapToInt(Integer::intValue).sum() > 0;
            if (flag) {
                specList.getYearspeclistBuilderList().forEach(s1 ->
                        s1.getSpeclistBuilderList().forEach(specInfo -> {
                            if (specInfo.getCanaskprice() == 0 && specInfo.getState() == 40) {
                                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.68.5") &&
                                        ("B".equalsIgnoreCase(usedcarbtnstyleab) || "C".equalsIgnoreCase(usedcarbtnstyleab) || "D".equalsIgnoreCase(usedcarbtnstyleab))){
                                    specInfo.setCanaskprice(4);
                                    specInfo.setSpecbottomtitle("暂无二手车");
                                }else{
                                    specInfo.setCanaskprice(7);
                                    specInfo.setSpecbottomtitle("旧车回收");
                                    String url = "https://activitym.che168.com/2023/sellonestop/main/index?leadssources=2&sourcetwo=4&sourcethree=1753&sourcename=2sc&pvareaid=112719&isonestopab=B";
                                    specInfo.setSpecbottomurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(url));
                                }
                            }
                        }));
            }

            specList.getYearspeclistBuilderList().forEach(s1 ->
                    s1.getSpeclistBuilderList().forEach(specInfo -> {
                        String eid;
                        if (pm == 1) {
                            if (StringUtils.isEmpty(specList.getNodealertip())) {
                                eid = "3|1411001|48|33|200014|300000";
                            } else {
                                eid = "3|1411002|1373|0|204745|303106";
                            }
                        } else if (pm == 3) {
                            eid = "3|1474001|48|33|203054|306043";
                        } else {
                            if (StringUtils.isEmpty(specList.getNodealertip())) {
                                eid = "3|1412001|48|33|200014|300000";
                            } else {
                                eid = "3|1411002|1373|0|204745|303105";
                            }
                        }
                        if (pm == 3) {
                            if (areaButtonList != null && areaButtonList.size() > 0) {
                                Optional<SListAreaButtonResult.ButtonListDTO> first = areaButtonList.stream()
                                        .filter(x -> x.getSpecId() == specInfo.getId() && x.getBtnType() == 2)
                                        .findFirst();
                                if (first.isPresent()) {
                                    specInfo.setCanaskprice(1);
                                    specInfo.setSpecbottomtitle(first.get().getMainText());
                                    specInfo.setSpecbottomurl(first.get().getUrl());
                                }
                            }
                            if (StringUtils.isEmpty(specInfo.getSpecbottomurl()) && specInfo.getCanaskprice() == 1) {
                                specInfo.setCanaskprice(0);
                            }
                        } else if (specInfo.getCanaskprice() == 1 || specInfo.getCanaskprice() == 0) {
                            // 非鸿蒙的处理逻辑
                            if (!CollectionUtils.isEmpty(areaButtonList)) {
                                specInfo.setCanaskprice(1);
                                Optional<SListAreaButtonResult.ButtonListDTO> btnType2 = areaButtonList.stream()
                                        .filter(x -> x.getSpecId() == specInfo.getId() && x.getBtnType() == 2)
                                        .findFirst();
                                if (btnType2.isPresent()) {
                                    // btnType == 2 询价按钮处理
                                    SListAreaButtonResult.ButtonListDTO button = btnType2.get();
                                    // 询价按钮
                                    specInfo.setSpecbottomtitle(button.getMainText());
                                    if (StringUtils.isNotEmpty(button.getUrl())) {
                                        specInfo.setSpecbottomurl(button.getUrl());
                                    } else {
                                        String url;
                                        String askPriceSchemaTemp = "autohome://car/asklowprice?customshowanimationtype=2&eid=%s&seriesid=%s&specid=%s&inquirytype=2&price_show=%s&isother=%s&title=%s&ext=%s";
                                        if (button.getWindowType() == 14) {
                                            url = String.format("autohome://dealerconsult/dealerprice?seriesid=%s&specid=%s&eid=%s",
                                                    seriesId, specInfo.getId(), UrlUtil.encode(eid));
                                        } else {
                                            url = String.format(askPriceSchemaTemp, UrlUtil.encode(eid), seriesId, specInfo.getId(),
                                                    DealerCommHelp.getPriceShowFromWindowType(button.getWindowType()),
                                                    specInfo.getIsother(),
                                                    StringUtils.isNotBlank(button.getMainText())
                                                            ? UrlUtil.encode(button.getMainText()) : "",
                                                    UrlUtil.encode(specInfo.getExt()));
                                        }
                                        specInfo.setSpecbottomurl(url);
                                    }
                                    specInfo.setExt(String.format("{\"price_show\":%s}",
                                            DealerCommHelp.getPriceShowFromWindowType(button.getWindowType())));
                                    String bottomUrlNew = UrlUtil.removeParamInUrl(specInfo.getSpecbottomurl(), "ext");
                                    String extJsonNew = UrlUtil.addParamInExtJson(specInfo.getExt(), "link", bottomUrlNew);
                                    specInfo.setExt(extJsonNew);
                                } else {
                                    // 没有就走打底逻辑
                                    String rnUrl = String.format("rn://DealerPriceRn/ReverseAuctionDialog?seriesid=%s&specid=%s&siteid=21&gps=1&eid=%s",
                                            seriesId, specInfo.getId(), UrlUtil.encode(eid));
                                    specInfo.setSpecbottomtitle("查报价单");
                                    specInfo.setSpecbottomurl(String.format("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s",
                                            UrlUtil.encode(rnUrl)));
                                    specInfo.setExt("{\"price_show\":36}");
                                    String bottomUrlNew = UrlUtil.removeParamInUrl(specInfo.getSpecbottomurl(), "ext");
                                    String extJsonNew = UrlUtil.addParamInExtJson(specInfo.getExt(), "link", bottomUrlNew);
                                    specInfo.setExt(extJsonNew);
                                }
                                Optional<SListAreaButtonResult.ButtonListDTO> btnType3 = areaButtonList.stream()
                                        .filter(x -> x.getSpecId() == specInfo.getId() && x.getBtnType() == 3)
                                        .findFirst();
                                if (btnType3.isPresent()) {
                                    // btnType == 3 im按钮处理
                                    specInfo.setImtype(1);
                                    String linkUrl = btnType3.get().getImSchema();
                                    String specId = "specid=" + specInfo.getId();
                                    linkUrl = linkUrl.replace("specid=0", specId);
                                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Iminfo.Builder iminfoBuilder =
                                            SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Iminfo.newBuilder();
                                    iminfoBuilder.setImtitle(btnType3.get().getMainText());
                                    iminfoBuilder.setSubline(1);
                                    linkUrl = StringUtils.replace(linkUrl, "sourceid=51", "sourceid=69");
                                    String imEid = pm == 1 ? "3|1411002|1373|0|205310|304223" : "3|1412002|1373|0|205310|304222";
                                    iminfoBuilder.setImlinkurl(linkUrl + "&eid=" + UrlUtil.encode(imEid));
                                    specInfo.setIminfo(iminfoBuilder);
                                    if (specInfo.getUsedcarpricebtn() != null) {
                                        specInfo.getUsedcarpricebtnBuilder().clear();
                                    }
                                }
                                Optional<SListAreaButtonResult.ButtonListDTO> btnType1 = areaButtonList.stream()
                                        .filter(x -> x.getSpecId() == specInfo.getId() && x.getBtnType() == 1)
                                        .findFirst();
                                if (btnType1.isPresent()) {
                                    // btnType == 1 400按钮处理，理论上目前不会有这个类型的值返回
                                    try {
                                        if (specInfo.getCanaskprice() == 1 && specInfo.getState() != 10) {
                                            SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Telinfo.Builder telBuilder =
                                                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Telinfo.newBuilder();
                                            telBuilder.setTitle(Objects.requireNonNullElse(btnType1.get().getMainText(),""));
                                            telBuilder.setSubtitle(Objects.requireNonNullElse(btnType1.get().getSubText(),""));
                                            telBuilder.setTel(Objects.requireNonNullElse(btnType1.get().getTelNO(),""));
                                            specInfo.setTelinfo(telBuilder);
                                        }
                                    } catch (Exception e) {
                                        log.error("btnType1 telInfo error",e);
                                    }
                                }
                                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5") && StringUtils.equalsIgnoreCase(dealer400ab, "A") && btnType1.isPresent()) {
                                    // btnType == 1 400按钮处理
                                    try {
                                        SListAreaButtonResult.ButtonListDTO buttonDTO = btnType1.get();
                                        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Dealer400.Builder dealer400Builder = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Dealer400.newBuilder();
                                        dealer400Builder.setBtntype(200);//客户端要求写死200
                                        dealer400Builder.setTitle(StringUtils.isNotEmpty(buttonDTO.getMainText()) ? buttonDTO.getMainText() : "");
                                        dealer400Builder.setTel(StringUtils.isNotEmpty(buttonDTO.getTelNO()) ? buttonDTO.getTelNO() : "");
                                        dealer400Builder.setLinkurl(StringUtils.isNotEmpty(buttonDTO.getUrl()) ? buttonDTO.getUrl() : "");
                                        dealer400Builder.setExt(StringUtils.isNotEmpty(buttonDTO.getExt()) ? buttonDTO.getExt() : "");
                                        Pvitem.Builder pvitemBuilder = Pvitem.newBuilder();
                                        pvitemBuilder.putArgvs("eid", "3|1411002|1373|0|211848|306928");
                                        pvitemBuilder.getShowBuilder().setEventid("auto_dlr_ics_common_xj_show");
                                        pvitemBuilder.getClickBuilder().setEventid("auto_dlr_ics_common_xj_click");
                                        dealer400Builder.setPvitem(pvitemBuilder);
                                        specInfo.setDealer400(dealer400Builder);
                                    } catch (Exception e) {
                                        log.error("btnType1 Dealer400 error");
                                    }
                                }
                            } else {
                                // 没有就走打底逻辑
                                String rnUrl = String.format("rn://DealerPriceRn/ReverseAuctionDialog?seriesid=%s&specid=%s&siteid=21&gps=1&eid=%s",
                                        seriesId, specInfo.getId(), UrlUtil.encode(eid));
                                specInfo.setSpecbottomtitle("查报价单");
                                specInfo.setSpecbottomurl(String.format("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s",
                                        UrlUtil.encode(rnUrl)));
                                specInfo.setExt("{\"price_show\":36}");
                                String bottomUrlNew = UrlUtil.removeParamInUrl(specInfo.getSpecbottomurl(), "ext");
                                String extJsonNew = UrlUtil.addParamInExtJson(specInfo.getExt(), "link", bottomUrlNew);
                                specInfo.setExt(extJsonNew);
                            }
                        }
                    }));
        } catch (Exception e) {
            log.error("车型列表询价按钮title和scheme协议数据异常：{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 全新车系添加咨询按钮
     *
     * @param specList
     * @param zixunAbTest
     * @param seriesDetailDto
     * @param pm
     * @param seriesId
     */
    public void initZixunInfo(SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specList,
                              String zixunAbTest,
                              SeriesDetailDto seriesDetailDto,
                              int pm,
                              int seriesId,
                              int cityId) {
        try {
            //针对全新车系添加咨询按钮
            if (StringUtils.equalsAnyIgnoreCase(zixunAbTest, "B", "C", "D")) {
                SeriesConsultConfigDto seriesConsultConfigDto = JSON.parseObject(seriesConsultConfig, new TypeReference<SeriesConsultConfigDto>() {
                });
                boolean isNotBlack = !CollectionUtils.isEmpty(seriesConsultConfigDto.getSeriesblacklist())
                        && !seriesConsultConfigDto.getSeriesblacklist().contains(seriesId);
                // TODO chengjincheng 2024/6/28 后续新车咨询按钮也由经销商接口返回，就无需再进行这样的判断
                BaseModel<List<DealerSeriesCanAskPriceResult>> seriesCanAskPriceResult =
                        dealerApiClient.getDealerSeriesCanAskPrice(cityId, seriesId).join();
                boolean canAskPrice = Objects.nonNull(seriesCanAskPriceResult)
                        && Objects.nonNull(seriesCanAskPriceResult.getResult())
                        && !CollectionUtils.isEmpty(seriesCanAskPriceResult.getResult());
                //车系是否在黑名单中，如果在，就不添加咨询入口
                if (isNotBlack && canAskPrice) {
                    //是否全新车系（state=10&&全部车型都是即将销售）
                    boolean isAllNewSeries = seriesDetailDto.getIsNewCar();
                    if (isAllNewSeries) {
                        List<SeriesConsultConfigDto.AbtestlistBean> abtestlist = null;
                        //通过车系id取阿波罗上配置的数据
                        if (!CollectionUtils.isEmpty(seriesConsultConfigDto.getSeriesabtestlist())) {
                            SeriesConsultConfigDto.SeriesAbtestlistBean seriesAbtestlistBean =
                                    seriesConsultConfigDto.getSeriesabtestlist().stream()
                                            .filter(x1 -> x1.getSeriesid() == seriesId)
                                            .findFirst()
                                            .orElse(null);
                            if (seriesAbtestlistBean != null
                                    && !CollectionUtils.isEmpty(seriesAbtestlistBean.getAbtestlist())) {
                                abtestlist = seriesAbtestlistBean.getAbtestlist();
                            }
                        }
                        //通过车系id没有取到值，使用阿波罗配置的默认值
                        if (abtestlist == null) {
                            abtestlist = seriesConsultConfigDto.getAbtestlist();
                        }
                        if (!CollectionUtils.isEmpty(abtestlist)) {
                            SeriesConsultConfigDto.AbtestlistBean configDto = abtestlist.stream()
                                    .filter(dto -> StringUtils.equalsIgnoreCase(dto.getAbtest(), zixunAbTest))
                                    .findFirst()
                                    .orElse(null);
                            if (configDto == null) {
                                //如果没有取到，就默认取第0个打底
                                configDto = abtestlist.get(0);
                            }

                            String zixuneid = pm == 1
                                    ? "3|1411002|1373|0|206323|305897"
                                    : "3|1412002|1373|0|206323|305897";
                            String title = configDto.getTitle();
                            String linkurl = "autohome://car/zixunpoppage?customshowanimationtype=2&seriesid=%s&specid=%s&eid=%s&ext=%s&ordertype=0&entrystyle=%s";
                            JSONObject extObj = new JSONObject();
                            extObj.put("texttype", configDto.getId());

                            SeriesConsultConfigDto.AbtestlistBean finalConfigDto = configDto;
                            specList.getYearspeclistBuilderList().forEach(s1 ->
                                    s1.getSpeclistBuilderList().forEach(specInfo -> {
                                        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.ZixunInfo.Builder zixunInfo =
                                                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.ZixunInfo.newBuilder();
                                        if (specInfo.getImtype() == 0) {
                                            // 若没有im的【联系销售】，才展示咨询按钮
                                            zixunInfo.setTitle(title);
                                            zixunInfo.setLinkurl(String.format(linkurl, seriesId, specInfo.getId(),
                                                    UrlUtil.encode(zixuneid), UrlUtil.encode(extObj.toJSONString()),
                                                    finalConfigDto.getId()));
                                            zixunInfo.setExt(extObj.toJSONString());
                                            zixunInfo.setEid(zixuneid);
                                            zixunInfo.setBtntype(7);
                                            specInfo.setZixuninfo(zixunInfo);
                                        }
                                    }));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("车型列表咨询入口数据异常：{}", ExceptionUtils.getStackTrace(e));
        }
    }


    private SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.UsedCarPriceBtn.Builder
    getUserCarBtnData(int seriesId, int specId, String cityId ) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.UsedCarPriceBtn.Builder btnBuilder =
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.UsedCarPriceBtn.newBuilder();
        btnBuilder.setTitle("二手车价");
        btnBuilder.setLinkurl("autohome://car/askusedcarprice?customshowanimationtype=2&animation_type=2&flutterPresentType=2&seriesid="
                + seriesId + "&askspecid=" + specId + "&eid=101&successclose=1&title=" + UrlUtil.encode("获取二手车报价"));
        btnBuilder.setPvitem(getPvItem(seriesId, specId, "car_series_usedcar_price_button_click", "car_series_usedcar_price_button_show", "108434", cityId));
        return btnBuilder;
    }

    public CompletableFuture<SeriesSpecListBaseInfoResponse.Result.SpecBottomList.Builder> getSpecBottomList(SeriesDetailDto series, int cityId, int pm) {
        return seriesDriveComponent.get(series.getId()).thenApply(x -> {
            if (x == null || x.getHomeTestDriveCitys() == null || !x.getHomeTestDriveCitys().contains(cityId)) {
                return null;
            }
            SeriesSpecListBaseInfoResponse.Result.SpecBottomList.Data.Builder driveData = SeriesSpecListBaseInfoResponse.Result.SpecBottomList.Data.newBuilder();
            String eid = pm == 1 ? "3|1411002|1373|0|205893|305178" : "3|1412002|1373|0|205893|305177";
            driveData.setSeriesid(series.getId());
            driveData.addAllTags(Arrays.asList("足不出户", "现车体验", "全程免费"));
            driveData.setTipimg("http://nfiles3.autohome.com.cn/zrjcpk10/car_sereis_shijia_text_icon.webp");
            driveData.setLefticon("http://nfiles3.autohome.com.cn/zrjcpk10/car_shijia_left_icon.webp");
            driveData.setBgimg("");
            driveData.setImg(ImageUtils.convertImageUrl(series.getPngLogo(), true, false, false, ImageSizeEnum.ImgSize_4x3_320x240));
            driveData.setSeriesname(series.getName());
            driveData.setSubtitle(getSeriesDayUv(series.getId()));
            driveData.setBtntitle("预约试驾");
            driveData.setBtnlinkurl(String.format("autohome://rninsidebrowser?url=%s",
                    UrlUtil.encode(String.format("rn://CarAskpriceRN/ApplyTestDrivePage?seriesid=%s&specid=%s&sourceid=1&eid=%s&cityid=%s&cityname=%s",
                            series.getId(), "", UrlUtil.encode(eid), cityId, UrlUtil.encode(CityUtil.getCityName(cityId))))));
            driveData.setRatio("6.5625");
            driveData.addAllHeaderlist(UserPhotoUtils.getRandomByCount(3));

            Pvitem.Builder pvitem = Pvitem.newBuilder();
            pvitem.putArgvs("seriesid", series.getId() + "");
            pvitem.putArgvs("eid", eid);
            pvitem.putArgvs("cityid", cityId + "");
            pvitem.setClick(Pvitem.Click.newBuilder().setEventid("auto_dlr_ics_common_xj_click").build());
            pvitem.setShow(Pvitem.Show.newBuilder().setEventid("auto_dlr_ics_common_xj_show").build());
            driveData.setPvitem(pvitem.build());
            return SeriesSpecListBaseInfoResponse.Result.SpecBottomList.newBuilder().setType(11088).setData(driveData);
        }).exceptionally(e -> {
            log.error("getSpecBottomList error", e);
            return null;
        });
    }

    String getSeriesDayUv(int seriesId) {
        String uvStr = StringUtils.EMPTY;
        EsSeriesUvItemDto seriesDayUv = seriesUvComponent.getSeriesDayUv(new Date(), seriesId);
        if (seriesDayUv != null) {
            int totalCount = (int) (seriesDayUv.getCount() * 1.5) + 333;
            totalCount = totalCount / 10 * 10;
            if (totalCount > 10000) {
                DecimalFormat df = new DecimalFormat("0.00");
                uvStr = df.format(totalCount / 10000.0) + "万人正在关注";
            } else {
                uvStr = totalCount + "人正在关注";
            }
        }
        return uvStr;
    }

    /**
     * 我要卖车
     *
     * @return
     */
    public SeriesSpecListBaseInfoResponse.Result.SaleCarInfo.Builder getSaleCarInfo() {
        SeriesSpecListBaseInfoResponse.Result.SaleCarInfo.Builder saleCarInfo = SeriesSpecListBaseInfoResponse.Result.SaleCarInfo.newBuilder();

        saleCarInfo.setBtnname("我要卖车");
        saleCarInfo.setImage("https://files3.autoimg.cn/zrjcpk10/car_seriesusedcarsale@3x.png");
        saleCarInfo.setLinkurl("");
        saleCarInfo.setText("请输入您的手机号");
        saleCarInfo.setIcon("https://files3.autoimg.cn/zrjcpk10/car_series_usedcarsafe.png01.png");
        saleCarInfo.setTipinfo("10,115,157");
        saleCarInfo.setSubtip("位用户已申请服务");
        saleCarInfo.setBtnbottomone("稍后将由之家合作平台和商户为您提供服务，接受服务前请阅读并同意");
        saleCarInfo.setBtnbottomtwo("《隐私政策-二手车买卖咨询服务功能》");
        saleCarInfo.setConceallinkurl("autohome://insidebrowserwk?url=https%3a%2f%2fm.che168.com%2fhtml%2fdeclaration202210.html");

        return saleCarInfo;
    }

    /**
     * 在售系列的
     */
    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder onSaleSpecData(SpecGroupOfSeriesDto specGroupOfSeriesDto, SeriesDetailDto seriesDetailDto, int tagId, int usedCarSpecId) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specListBuilder = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.newBuilder();
        Map<String, List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup>> yearNameMap = specGroupOfSeriesDto.getYearspeclist().stream().collect(Collectors.groupingBy(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getYearname, Collectors.toList()));
        if (yearNameMap.size() == 1) {
            specListBuilder.setYearname(specGroupOfSeriesDto.getYearspeclist().get(0).getYearname());
        } else {
            specListBuilder.setYearname(StringUtils.EMPTY);
        }
        specListBuilder.setYearvalue(tagId);

        AtomicReference<SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder> groupBuilder = new AtomicReference<>();
        specGroupOfSeriesDto.getYearspeclist().forEach(specgourplistBean -> {
            SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder specGroup = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.newBuilder();
            specGroup.setName(specgourplistBean.getName());

            Map<Integer, SpecDetailDto> specMap = mGetSpecMap(specgourplistBean, seriesDetailDto, specGroupOfSeriesDto.getYearname());
            if (specgourplistBean.getSpeclist().size() > 0) {
                SpecDetailDto specDetailDto = specMap.get(specgourplistBean.getSpeclist().get(0).getSpecId());
                if (specDetailDto != null && specDetailDto.getFuelType() == 3) {
                    String elecPowerStr = StringUtils.EMPTY;
                    if (specDetailDto.getElectricKw() > 0) {
                        String elecPower = Long.toString(Math.round(((int) specDetailDto.getElectricKw()) * 1.36));
                        elecPowerStr = String.format(" 电动机：%s马力", elecPower);
                    }
                    String groupName =
                            String.format("%s升 %s 发动机：%s马力 %s%s", specDetailDto.getDisplacement(), specDetailDto.getFlowModeName(), specDetailDto.getEnginePower(), specDetailDto.getEmissionStandards(), elecPowerStr);
                    specGroup.setName(groupName);
                }
            }
            if (StringUtils.isNotEmpty(specgourplistBean.getYearname())) {
                specGroup.setYearname(specgourplistBean.getYearname());
            } else {
                specGroup.setYearname(specGroupOfSeriesDto.getYearname());
            }
            for (int i = 0; i < specgourplistBean.getSpeclist().size(); i++) {
                SpecGroupOfSeriesDto.Spec spec = specgourplistBean.getSpeclist().get(i);
                SpecDetailDto s3 = specMap.get(spec.getSpecId());
                if (s3 == null) {
                    continue;
                }
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder dto = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.newBuilder();
                dto.setId(s3.getSpecId());
                dto.setName(s3.getSpecName());
                dto.setPricename(s3.getState() == SeriesStateEnum.coming_soon.getValue() ? (s3.isBooked() ? "订金:" : "预售价:") : "指导价:");
                dto.setPrice(PriceUtil.GetPriceStringDetail(s3.getMinPrice(), s3.getMaxPrice(), 0));
                dto.setParamisshow(String.valueOf(s3.getParamIsShow()));
                dto.setState(s3.getState());
                //dto.setFueltypeid(s3.getFuelType());
                dto.setFueltypedetailid(s3.getFuelType());
                String energyType = EnergyTypesEnum.getTypeByValue(s3.getFuelType());
                if (Arrays.asList(4, 5, 6).contains(s3.getFuelType()) && StringUtils.isNotBlank(energyType) && s3.getEnduranceMileage() > 0) {
                    dto.setFueltypedetail(energyType);
                    dto.setEndurancetext("官方续航：");
                    dto.setEndurancemileage(s3.getEnduranceMileage() + "KM");
                }
                dto.setNoshowprice(s3.getMinPrice());
                dto.setMinprice(StringUtils.EMPTY);
                dto.setCalcprice(s3.getMinPrice());
                dto.setLabletype(0);
                dto.setFctpricetipinfo(StringUtils.defaultString(s3.getPriceDescription()));
                dto.setSeatcount(s3.getSeatCount());
                dto.setIstaxexemption(s3.isTaxExemption() ? 1 : 0);
                dto.setMali(s3.getEnginePower());
                dto.setYear(s3.getYearName());
                dto.setSpecbottomtitle(StringUtils.EMPTY);
                dto.setSpecbottomurl(StringUtils.EMPTY);
                dto.setAttention(0);
                dto.setAttentionint(0);
                dto.setDownprice(StringUtils.EMPTY);
                if (s3.getState() == 30) {
                    dto.setLabletype(2);
                } else if (s3.isIsnewcar()) {
                    dto.setLabletype(1);
                }
                dto.setCanaskprice(1);
                dto.setElectriccarname(StringUtils.EMPTY);
                dto.setElectriccarval(StringUtils.EMPTY);
                dto.setSscprice(StringUtils.EMPTY);
                //todo 设置下一车型配置差异信息
                if (spec.getDiffconfigofhighlight() != null && spec.getDiffconfigofhighlight().getPrice() != 0 && (i + 1) < specgourplistBean.getSpeclist().size()) {
                    dto.setDiffconfiginfo(setSpecNextDiffInfo(spec, specgourplistBean.getSpeclist().get(i + 1), specMap));
                }
                dto.setSpecbottomtitle("获取底价");
                if (usedCarSpecId == spec.getSpecId()) {
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder cloneGroup = specGroup.clone();
                    cloneGroup.setYearname("二手车同款车型");
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder cloneSpec = dto.clone();
                    cloneSpec.setLabelstr("同款车型");
                    cloneSpec.setLabletype(3);
                    cloneGroup.addSpeclist(cloneSpec);
                    groupBuilder.set(cloneGroup);
                }
                specGroup.addSpeclist(dto);
            }
            if (groupBuilder.get() != null) {
                specListBuilder.addYearspeclist(groupBuilder.get());
            }
            specListBuilder.addYearspeclist(specGroup);
        });
        return specListBuilder;
    }

    /**
     * 未售
     */
    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder initNotSaleSpecListV2(SpecGroupOfSeriesDto specGroupOfSeriesDto, SeriesDetailDto seriesDetailDto, int tagId) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specListBuilder = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.newBuilder();
        specListBuilder.setYearname(StringUtils.EMPTY);
        specListBuilder.setYearvalue(tagId);
        specGroupOfSeriesDto.getYearspeclist().forEach(specgourplistBean -> {
            SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder specGroup = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.newBuilder();
            specGroup.setName(StringUtils.EMPTY);
            specGroup.setYearname(StringUtils.EMPTY);
            Map<Integer, SpecDetailDto> specMap = mGetSpecMap(specgourplistBean, seriesDetailDto, specGroupOfSeriesDto.getYearname());
            for (int i = 0; i < specgourplistBean.getSpeclist().size(); i++) {
                SpecDetailDto s1 = specMap.get(specgourplistBean.getSpeclist().get(i).getSpecId());
                if (s1 == null) {
                    continue;
                }
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder dto = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.newBuilder();
                dto.setId(s1.getSpecId());
                dto.setName(s1.getSpecName());
                dto.setPricename("预售价 ");
                dto.setPrice(PriceUtil.GetPriceStringDetail(s1.getMinPrice(), s1.getMaxPrice(), s1.getState()));
                dto.setParamisshow(String.valueOf(s1.getParamIsShow()));
                dto.setState(0);
                //dto.setFueltypeid(0);
                dto.setFueltypedetailid(0);
                dto.setNoshowprice(s1.getMinPrice());
                dto.setLabletype(0);
                dto.setYear(202106);
                dto.setMali(0);
                dto.setCanaskprice(0);
                dto.setAttention(0);
                dto.setFctpricetipinfo(StringUtils.EMPTY);
                specGroup.addSpeclist(dto);
            }
            specListBuilder.addYearspeclist(specGroup);
        });
        return specListBuilder;
    }

    /**
     * 停售  initStopYearData
     */
    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder initStopYearData(SpecGroupOfSeriesDto specGroupOfSeriesDto, SeriesDetailDto seriesDetailDto, int tagId, String pluginversion, int usedCarSpecId) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specListBuilder = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.newBuilder();
        specListBuilder.setYearname(StringUtils.EMPTY);
        specListBuilder.setYearvalue(tagId);
        AtomicReference<SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder> groupBuilder = new AtomicReference<>();
        specGroupOfSeriesDto.getYearspeclist().forEach(specgourplistBean -> {
            SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder specGroup = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.newBuilder();
            specGroup.setName(StringUtils.EMPTY);
            specGroup.setYearname(specgourplistBean.getYearname());
            Map<Integer, SpecDetailDto> specMap = mGetSpecMap(specgourplistBean, seriesDetailDto, specGroupOfSeriesDto.getYearname());
            for (int i = 0; i < specgourplistBean.getSpeclist().size(); i++) {
                SpecDetailDto s1 = specMap.get(specgourplistBean.getSpeclist().get(i).getSpecId());
                if (s1 == null) {
                    continue;
                }
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder dto = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.newBuilder();
                dto.setId(s1.getSpecId());
                dto.setName(s1.getSpecName());
                dto.setPricename("厂商指导价 ");
                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5")) {
                    dto.setPricename("指导价:");
                }

                dto.setPrice(PriceUtil.GetPriceStringDetail(s1.getMinPrice(), s1.getMaxPrice(), s1.getState()));
                dto.setParamisshow(String.valueOf(s1.getParamIsShow()));
                dto.setState(s1.getState());
                //dto.setFueltypeid(s1.getFuelType());
                dto.setFueltypedetailid(s1.getFuelType());
                dto.setNoshowprice(s1.getMinPrice());
                dto.setLabletype(0);
                dto.setYear(s1.getYearName());
                dto.setMali(s1.getEnginePower());
                dto.setCanaskprice(0);
                dto.setAttention(0);
                dto.setFctpricetipinfo(StringUtils.EMPTY);
                if (usedCarSpecId != 0 && usedCarSpecId == s1.getSpecId()) {
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder cloneGroup = specGroup.clone();
                    cloneGroup.setYearname("二手车同款车型");
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder cloneSpec = dto.clone();
                    cloneSpec.setLabelstr("同款车型");
                    cloneSpec.setLabletype(3);
                    List<Long> orderList = Arrays.asList(0L, 1L, -111111111189900L, 1L);
                    cloneSpec.addAllOrder(orderList);
                    cloneGroup.addSpeclist(cloneSpec);
                    groupBuilder.set(cloneGroup);
                }
                specGroup.addSpeclist(dto);
            }
            if (groupBuilder.get() != null) {
                specListBuilder.addYearspeclist(groupBuilder.get());
            }
            specListBuilder.addYearspeclist(specGroup);
        });

        return specListBuilder;
    }

    /**
     * 座位、排量、新能源
     */
    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder initPaiLiangAndSeatData(SpecGroupOfSeriesDto specGroupOfSeriesDto, SeriesDetailDto seriesDetailDto, int tagId) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specListBuilder = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.newBuilder();
        specListBuilder.setYearname(StringUtils.EMPTY);
        specListBuilder.setYearvalue(tagId);

        specGroupOfSeriesDto.getYearspeclist().forEach(specgourplistBean -> {
            List<SpecDetailDto> specDetailDtos = mGetSpecList(specgourplistBean, seriesDetailDto, specGroupOfSeriesDto.getYearname());
            Map<Integer, List<SpecDetailDto>> groupSpec = specDetailDtos.stream().collect(Collectors.groupingBy(SpecDetailDto::getYearName, LinkedHashMap::new, Collectors.toList()));
            Comparator byYear = Comparator.comparing(SpecDetailDto::getYearName);
            Comparator byPrice = Comparator.comparing(SpecDetailDto::getMinPrice);
            for (Integer key : groupSpec.keySet()) {
                SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder specGroup = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.newBuilder();
                specGroup.setName(StringUtils.EMPTY);
                specGroup.setYearname(StringUtils.EMPTY);

                List<SpecDetailDto> specList = groupSpec.get(key);
                Collections.sort(specList, byYear.thenComparing(byPrice));

                for (int i = 0; i < specList.size(); i++) {
                    SpecDetailDto s1 = specList.get(i);
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder dto = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.newBuilder();
                    dto.setId(s1.getSpecId());
                    dto.setName(s1.getSpecName());
                    dto.setPricename("指导价:");

                    dto.setPrice(PriceUtil.GetPriceStringDetail(s1.getMinPrice(), s1.getMaxPrice(), s1.getState()));
                    dto.setParamisshow(String.valueOf(s1.getParamIsShow()));
                    dto.setState(s1.getState());
                    //dto.setFueltypeid(s1.getFuelType());
                    dto.setFueltypedetailid(s1.getFuelType());
                    String energyType = EnergyTypesEnum.getTypeByValue(s1.getFuelType());
                    if (Arrays.asList(4, 5, 6).contains(s1.getFuelType()) && StringUtils.isNotBlank(energyType) && s1.getEnduranceMileage() > 0) {
                        dto.setFueltypedetail(energyType);
                        dto.setEndurancetext("纯电续航：");
                        dto.setEndurancemileage(s1.getEnduranceMileage() + "KM");
                    }
                    dto.setFctpricetipinfo(StringUtils.defaultString(s1.getPriceDescription()));
                    dto.setNoshowprice(s1.getMinPrice());
                    dto.setMinprice(StringUtils.EMPTY);
                    dto.setCalcprice(s1.getMinPrice());
                    dto.setLabletype(0);
                    if (s1.getState() == 30) {
                        dto.setLabletype(2);
                    } else if (s1.isIsnewcar()) {
                        dto.setLabletype(1);
                    }
                    dto.setYear(s1.getYearName());
                    dto.setMali(s1.getEnginePower());
                    if (s1.getState() == 20 || s1.getState() == 30) {
                        dto.setCanaskprice(1);
                    }
                    dto.setCalcprice(s1.getMinPrice());
                    dto.setAttention(0);
                    specGroup.addSpeclist(dto);
                }
                specListBuilder.addYearspeclist(specGroup);
            }
        });
        return specListBuilder;
    }

    /**
     * 热门二手车
     */
    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder init2scData(SeriesDetailDto seriesDetailDto,
                                                                                SeriesCityHotUsedCarDto hotUsedCarDto,
                                                                                int tagId, int cityId,
                                                                                String usedcarbtnstyleab,
                                                                                String pluginversion) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Builder specListBuilder = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.newBuilder();
        specListBuilder.setYearname(StringUtils.EMPTY);
        specListBuilder.setYearvalue(tagId);
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Builder specGroup = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.newBuilder();
        specGroup.setName(StringUtils.EMPTY);
        specGroup.setYearname(StringUtils.EMPTY);
        try {
            if (hotUsedCarDto != null && hotUsedCarDto.getList() != null) {
                hotUsedCarDto.getList().forEach(item -> {
                    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Builder dto = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.newBuilder();
                    dto.setId(item.getSpecid());
                    dto.setName(item.getSpecname());
                    dto.setPrice(item.getDynamicprice() + "万");
                    dto.setPricename("指导价 ");
                    dto.setSscpricename("二手车 ");
                    dto.setSscprice(item.getPrice() + "万");
                    dto.setSpecbottomurl(item.getMoreurl());

                    String price ="";
                    String cunt ="";
                    if ("D".equalsIgnoreCase(usedcarbtnstyleab) && CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                        if(StringUtils.isEmpty(specbottomtitleConfig)){
                            dto.setSpecbottomtitle("同款二手车");
                        }else {
                            dto.setSpecbottomtitle(specbottomtitleConfig);
                        }
                    }else if ("B".equalsIgnoreCase(usedcarbtnstyleab)&& CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                        SpecCityUsedCarDto specCityUsedCarDto = specUsedCarPriceComponent.get((item.getSpecid()), cityId).join();
                        if (Objects.nonNull(specCityUsedCarDto)){
                            price = specCityUsedCarDto.getMinprice() > 0
                                    ? df02.format(specCityUsedCarDto.getMinprice()) + "万起"
                                    : "";
                        }
                        dto.setSpecbottomtitle("同款二手车"+price);
                    }else if("C".equalsIgnoreCase(usedcarbtnstyleab)&& CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")){
                        SpecCityUsedCarDto country = specUsedCarPriceComponent.getCountry((item.getSpecid())).join();
                        if (Objects.nonNull(country)){
                            if (country.getCunt()>0){
                                dto.setSpecbottomtitle("全国"+country.getCunt()+"辆同款二手车");
                            }else {
                                dto.setSpecbottomtitle("暂无二手车");
                            }
                        }

                    }else {
                        dto.setSpecbottomtitle("二手车");
                    }
                    dto.setParamisshow("1");
                    dto.setState(40);
                    //dto.setFueltypeid(0);
                    dto.setNoshowprice(0);
                    dto.setLabletype(0);
                    dto.setYear(5000);
                    dto.setMali(0);
                    dto.setCanaskprice(4);
                    if (CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0")) {
                        if (!("B".equalsIgnoreCase(usedcarbtnstyleab) || "C".equalsIgnoreCase(usedcarbtnstyleab)
                        || "D".equalsIgnoreCase(usedcarbtnstyleab))){
                            dto.setUsedcarpricebtn(getUserCarBtnData(seriesDetailDto.getId(), item.getSpecid(), cityId + ""));
                        }
                    }else{
                        dto.setUsedcarpricebtn(getUserCarBtnData(seriesDetailDto.getId(), item.getSpecid(), cityId + ""));
                    }
                    dto.setSsclinkurl("");
                    dto.setAttention(0);
                    if (item.getList() != null && item.getList().size() > 0) {
                        for (SeriesCityHotUsedCarDto.HotSpecDealerItem dealerItem : item.getList()) {
                            SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.SscInfo.Builder dealerDto = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.SscInfo.newBuilder();
                            dealerDto.setImage(dealerItem.getImageurl());
                            dealerDto.setImage(ImageUtils.convertImage_ToWebp(ImageUtils.convertImage_ToHttp(dealerDto.getImage())).replace("_q87_c42", "_c42"));
                            dealerDto.setLinkurl(dealerItem.getUrl());
                            dealerDto.setName(dealerItem.getCarname());
                            dealerDto.setPrice(dealerItem.getPrice() + "万");
                            dealerDto.setPricename("售价");
                            dealerDto.setPvareaid("109552");
                            dealerDto.setSpecid(dealerItem.getSpecid());
                            dealerDto.setCityname(dealerItem.getCityname());
                            dealerDto.setTags(dealerItem.getMileage() + "万公里/" + dealerItem.getRegdate().replace("年", ""));
                            if (dto.getSscllistCount() < 5) {
                                dto.addSscllist(dealerDto);
                            }
                        }
                        if (item.getList().size() > 5) {
                            String moreUrl = String.format("autohome://usedcar/buycarlist?pvareaid=%s&s_cid=%s&scene_no=%s&brand=%s", "112432", cityId, 102,
                                    UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}",
                                            seriesDetailDto.getBrandId(), seriesDetailDto.getBrandName(), seriesDetailDto.getId(), seriesDetailDto.getName())));
                            dto.setSsclinkurl(moreUrl);
                            dto.setSscpvitem(getPvItem(seriesDetailDto.getId(), item.getSpecid(), "car_series_hot_used_car_more_click", ""));
                        }
                    }
                    specGroup.addSpeclist(dto);
                });
            }
            specListBuilder.addYearspeclist(specGroup);
        } catch (Exception e) {
            log.error("init2scData error", e);
        }
        return specListBuilder;
    }

    Pvitem.Builder getPvItem(int seriesId, int specid, String clickEventId, String showEventId) {
        Pvitem.Builder pvitem = Pvitem.newBuilder();
        pvitem.putArgvs("seriesid", String.valueOf(seriesId));
        pvitem.putArgvs("specid", String.valueOf(specid));
        pvitem.setClick(Pvitem.Click.newBuilder().setEventid(clickEventId));
        pvitem.setShow(Pvitem.Show.newBuilder().setEventid(showEventId));
        return pvitem;
    }

    Pvitem.Builder getPvItem(int seriesId, int specid, String clickEventId, String showEventId, String eid, String cityid) {
        Pvitem.Builder pvitem = Pvitem.newBuilder();
        pvitem.putArgvs("seriesid", String.valueOf(seriesId));
        pvitem.putArgvs("specid", String.valueOf(specid));
        pvitem.putArgvs("sourceid", "429");
        pvitem.putArgvs("cityid",cityid );
        pvitem.setClick(Pvitem.Click.newBuilder().setEventid(clickEventId));
        pvitem.setShow(Pvitem.Show.newBuilder().setEventid(showEventId));
        return pvitem;
    }

    /**
     * 车型配置差异
     */
    SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Diffconfiginfo.Builder setSpecNextDiffInfo(SpecGroupOfSeriesDto.Spec currentSpec, SpecGroupOfSeriesDto.Spec nextSpec, Map<Integer, SpecDetailDto> specMap) {
        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Diffconfiginfo.Builder specDiff = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Diffconfiginfo.newBuilder();

        try {
            int diffPirce = Math.abs(currentSpec.getDiffconfigofhighlight().getPrice());
            String title = "<font color=\"#FF6600\">加" + CommonHelper.getPriceInfoForDiffConfig(diffPirce) + "</font>" + "升级为下一款，增加<font color=\"#FF6600\">" + currentSpec.getDiffconfigofhighlight().getDiffcount() + "项</font>配置";
            specDiff.setTitle(title);
            currentSpec.getDiffconfigofhighlight().getGrouplist().forEach(x -> {
                if (x.getList() != null) {
                    x.getList().forEach(p -> {
                        SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Diffconfiginfo.Configlist.Builder config = SeriesSpecListBaseInfoResponse.Result.SpecInfo.SpecList.Yearspeclist.Spec.Diffconfiginfo.Configlist.newBuilder();
                        config.setTitle(StringEscapeUtils.unescapeHtml4(p.getName()));
                        config.setImage(p.getImage() == null ? "" : ImageUtils.convertImageUrl(p.getImage(), true, false, false, ImageSizeEnum.ImgSize_4x3_320x240));
                        config.setPlaceimgurl("");
                        specDiff.addConfiglist(config);
                    });
                }
            });

            //当前车型id和下已车型id
            String specids = nextSpec.getSpecId() + "," + currentSpec.getSpecId();
            SpecDetailDto currentSpecDetailDto = specMap.get(currentSpec.getSpecId());
            SpecDetailDto nextSpecDetailDto = specMap.get(nextSpec.getSpecId());

            String specNameone = nextSpecDetailDto.getSpecName();
            String specNametwo = currentSpecDetailDto.getSpecName();

            if (nextSpecDetailDto.getMinPrice() < currentSpec.getMinPrice()) {
                specNameone = currentSpecDetailDto.getSpecName();
                specNametwo = nextSpecDetailDto.getSpecName();
                specids = currentSpec.getSpecId() + "," + nextSpec.getSpecId();
            }
            String url = "autohome://car/summaryconfigdif?seriesid=" + currentSpecDetailDto.getSeriesId() + "&specids=" + UrlUtil.encode(specids) +
                    "&specnameone=" + UrlUtil.encode(specNameone).replace("+", "%20") +
                    "&specnametwo=" + UrlUtil.encode(specNametwo).replace("+", "%20");
            specDiff.setLinkurl(url);
        } catch (Exception e) {
            log.error("diffconfiginfo error", e);
        }
        return specDiff;
    }

    Map<Integer, SpecDetailDto> mGetSpecMap(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup specGroup, SeriesDetailDto seriesDetailDto, String groupname) {
        if (specGroup != null && specGroup.getSpeclist() != null && specGroup.getSpeclist().size() > 0) {
            List<SpecDetailDto> specDetailDtos = specDetailComponent.mGet(specGroup.getSpeclist().stream().map(SpecGroupOfSeriesDto.Spec::getSpecId).collect(Collectors.toList()));
            Map<Integer, SpecDetailDto> collect = specDetailDtos.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecDetailDto::getSpecId, Function.identity(), (k1, k2) -> k2));
            if (specGroup.getSpeclist().size() != collect.size()) {
                log.error(String.format("mGetSpecList less,seriesid:%s groupname:%s", seriesDetailDto.getId(), groupname));
            }
            return collect;
        }
        return new HashMap<>();
    }

    List<SpecDetailDto> mGetSpecList(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup specGroup, SeriesDetailDto seriesDetailDto, String groupname) {
        if (specGroup != null && specGroup.getSpeclist() != null && specGroup.getSpeclist().size() > 0) {
            List<SpecDetailDto> specDetailDtos = specDetailComponent.mGet(specGroup.getSpeclist().stream().map(SpecGroupOfSeriesDto.Spec::getSpecId).collect(Collectors.toList()));
            List<SpecDetailDto> collect = specDetailDtos.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (specGroup.getSpeclist().size() != collect.size()) {
                log.error(String.format("mGetSpecList less,seriesid:%s groupname:%s", seriesDetailDto.getId(), groupname));
            }
            return collect;
        }
        return new ArrayList<>();
    }
}

