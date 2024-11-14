package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoResponse;
import autohome.rpc.car.app_cars.v1.carcfg.*;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.autohome.app.cars.apiclient.car.ConfigItemApiClient;
import com.autohome.app.cars.apiclient.car.dtos.KouBeiInfoDto;
import com.autohome.app.cars.apiclient.car.dtos.SNewEnaryConfigResult;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.SListAreaButtonResult;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecCityCpsInfoResult;
import com.autohome.app.cars.apiclient.video.dtos.SpecShiCeSmallVideoResult;
import com.autohome.app.cars.apiclient.video.dtos.SpecSmallVideoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.carconfig.SpecElectric;
import com.autohome.app.cars.common.enums.CarSellTypeEnum;
import com.autohome.app.cars.common.enums.TestDataConfigItemEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.components.baike.dtos.ConfigInfoDto;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigPicInfoDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecSpecificConfigDto;
import com.autohome.app.cars.service.components.che168.SpecCityUsedCarPriceComponent;
import com.autohome.app.cars.service.components.che168.dtos.SpecCityUsedCarDto;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowConfigDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.video.SpecAiVideoComponent;
import com.autohome.app.cars.service.components.video.SpecConfigSmallVideoComponent;
import com.autohome.app.cars.service.components.video.SpecShiCeSmallVideoComponent;
import com.autohome.app.cars.service.components.video.dtos.SpecAiVideoDto;
import com.autohome.app.cars.service.components.video.dtos.SpecConfigSmallVideoDto;
import com.autohome.app.cars.service.components.video.dtos.SpecShiCeVideoDto;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.JsonUtils;
import org.apache.dubbo.common.utils.LogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StopWatch;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParamConfigService {

    static List<MustSeeItemDto> mustSeeLevel = new ArrayList<>();

    static List<MustSeeItemDto> mustSeeLevel_7 = new ArrayList<>();
    static List<MustSeeItemDto> mustSeeLevel_16_20 = new ArrayList<>();
    static List<MustSeeItemDto> mustSeeLevel_14_20 = new ArrayList<>();
    static List<MustSeeItemDto> mustSeeLevel_21_24 = new ArrayList<>();
    static List<MustSeeItemDto> mustSeeEnergytype1 = new ArrayList<>();
    static List<MustSeeItemDto> mustSeeEnergytype = new ArrayList<>();
    static HashMap<String, String> eidMap = new HashMap<>();

    static {
        initMustSeeLevel();
        initMustSeeEnergytype();
        initMustSeeLevelOther();
        initEidmap();
    }

    @Autowired
    SpecParamConfigTempComponent tempComponent;

    @Autowired
    private SpecShiCeSmallVideoComponent specShiCeSmallVideoComponent;

    @Autowired
    private SpecTestDataComponent specTestDataComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SpecParamInfoNewComponent specParamInfoNewComponent;

    @Autowired
    private SpecConfigInfoNewComponent specConfigInfoNewComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    private SpecYearNewComponent specYearNewComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SeriesConfigDiffComponent seriesConfigDiffComponent;

    @Autowired
    private DealerApiClient dealerApiClient;

    @Autowired
    private SeriesVrComponent seriesVrComponent;

    @Autowired
    private SpecConfigSmallVideoComponent specConfigSmallVideoComponent;

    @Autowired
    private SpecAiVideoComponent specAiVideoComponent;

    @Autowired
    private SpecParamConfigPicInfoComponent specParamConfigPicInfoComponent;

    @Autowired
    private SpecSpecialConfigComponent specSpecificConfigComponent;

    @Autowired
    private SpecOutInnerColorComponent specOutInnerColorComponent;

    @Autowired
    private SpecConfigBagComponent specConfigBagComponent;

    @Autowired
    private SpecCityUsedCarPriceComponent specUsedCarPriceComponent;

    @Autowired
    SeriesCityAskPriceNewComponent seriesCityAskPriceComponent;

    @Autowired
    ConfigItemApiClient configItemApiClient;

    @Autowired
    SeriesEnergyInfoComponent seriesEnergyInfoComponent;

    @Autowired
    private RedisTemplate redisTemplate;


    //新能源的fueltype
    public static final List<Integer> newEnergyFueltypeList = ImmutableList.of(4, 5, 6, 7);

    //油车的fueltype
    public static final List<Integer> noElectricMotorFuelTypeList = ImmutableList.of(1, 2, 8, 9, 10, 11);
    /**
     * 电动机分类：油车不显示这些分类
     */
    public static final List<String> electricTypeList = ImmutableList.of("电动机", "电池/续航", "充/放电");
    /**
     * 新能源基本参数项，只有新能源车型才显示，非新能源车型隐藏这些参数:
     * "快充电量百分比", "工信部纯电续驶里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)"
     */
    public static List<String> listNewEnergyParam = ImmutableList.of("快充电量百分比", "纯电续航里程(km)", "NEDC纯电续航里程(km)", "WLTP纯电续航里程(km)", "工信部纯电续航里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)");
    /**
     * 纯电动车型不显示:
     * "充电桩价格", "系统综合功率(kW)", "系统综合扭矩(N·m)"
     */
    public static List<String> listNotDisPlayOfPEVCarParam = ImmutableList.of("油箱容积(L)", "NEDC综合油耗(L/100km)", "WLTC综合油耗(L/100km)", "四驱形式", "充电桩价格", "发动机", "变速箱", "系统综合功率(kW)", "系统综合扭矩(N·m)", "工信部综合油耗(L/100km)", "实测油耗(L/100km)", "环保标准");


    //全部新能源的时候，这些type显示三电系统
    public static final List<String> threeElectricType = ImmutableList.of("发动机", "电动机", "电池/续航", "充/放电", "变速箱");
    @Value("${baike_info_config:}")
    private String baiKeInFoConfig;


    public CompletableFuture<NewSpecCompareResponse> getSpecCompare(NewSpecCompareRequest request) {
        int siteId = request.getSite();
        int seriesId = request.getSeriesid();
        int year = request.getYear();
        int pm = request.getPm();
        int cityId = request.getCityid();
        String specIds = request.getSpecids();
        long s = System.currentTimeMillis();
        Map<String,Long> ts = new LinkedHashMap<>();
        NewSpecCompareResponse.Builder response = NewSpecCompareResponse.newBuilder();
        NewSpecCompareResponse.Result.Builder resultBuilder = NewSpecCompareResponse.Result.newBuilder();

        ConcurrentMap<Integer, SpecDetailDto> specDetails = new ConcurrentHashMap<>();

        List<CompletableFuture> tasks = new ArrayList<>();

        CompletableFuture<SeriesDetailDto> seriesDetail = null;
        if (siteId == 1) {

            seriesDetail = seriesDetailComponent.getAsync(seriesId);
            ts.put("1",System.currentTimeMillis() -s);

        } else if (siteId == 2) {  //车型
            if(!NumberUtils.isCreatable(specIds)){
                return CompletableFuture.completedFuture(response.setReturnMsg("参数错误").setReturnCode(-1).build());
            }
            int specId = Integer.valueOf(specIds);
            //CPS
            tasks.add(buildCpsinfo(specId, cityId).thenAccept(x -> {
                ts.put("2",System.currentTimeMillis() -s);
                if (x == null) {
                    return;
                }
                resultBuilder.setCpsinfo(x);
            }));
            SpecDetailDto specDetail = specDetailComponent.getSync(specId);
            if (specDetail == null) {
                return CompletableFuture.completedFuture(response.setReturnMsg("车型不存在").build());
            }
            specDetails.put(specDetail.getSpecId(), specDetail);
            seriesId = specDetail.getSeriesId();
            seriesDetail = seriesDetailComponent.getAsync(seriesId);
            ts.put("3",System.currentTimeMillis() -s);
        }
        if (siteId == 1 || siteId == 2) {
            //关注度最高车型
            tasks.add(buildAttentionSpecInfo(seriesDetail).thenAccept(x -> {
                if (x == null) {
                    return;
                }
                resultBuilder.setAttentionspecinfo(x);
                ts.put("4",System.currentTimeMillis() -s);
            }));
        }
        CompletableFuture<SpecParamConfigTempDto> temp = tempComponent.baseGetAsync(null);
        ts.put("5",System.currentTimeMillis() -s);
        Vector<String> deleteSpecIds = new Vector<>();
        Vector<String> deleteSpecName = new Vector<>();

        List<SpecGroupOfSeriesDto> seriesYears = new ArrayList<>();
        CompletableFuture<List<Integer>> specIdFuture = getSpecIds(request,specIds, siteId, year, seriesId, specDetails, deleteSpecIds, deleteSpecName, seriesYears).exceptionally(e -> logCompletableFutureError(e, "获取车型id错误", new ArrayList<>()));

        if (siteId == 1) {
            CompletableFuture<SeriesDetailDto> finalSeriesDetail = seriesDetail;
            int finalSeriesId = seriesId;
            tasks.add(specIdFuture.thenCompose(specIdList -> {
                        ts.put("6",System.currentTimeMillis() -s);
                        if (specIdList == null || specIdList.size() == 0) {
                            return CompletableFuture.completedFuture(null);
                        }
                        return buildToolboxEntry(finalSeriesId, pm, finalSeriesDetail, specIdList).thenAccept(x -> {
                            ts.put("7",System.currentTimeMillis() -s);
                            if (x == null) {
                                return;
                            }
                            resultBuilder.setToolboxentry(x);
                        });
                    }
            ));
            tasks.add(specIdFuture.thenCompose(specIdList -> {
                        if (specIdList == null || specIdList.size() == 0) {
                            return CompletableFuture.completedFuture(null);
                        }
                        return buildMustSeeList(finalSeriesDetail).thenAccept(resultBuilder::addAllMustseelist);
                    }
            ));
        }

        CompletableFuture main = specIdFuture.thenCompose(specIdList -> {
            if (siteId == 2 || siteId == 3) {
                if (deleteSpecIds.size() > 0) {
                    resultBuilder.setDeletespecids(String.join(",", deleteSpecIds));
                    resultBuilder.setDeletetip(String.join(",", deleteSpecName));
                }
            }

            if (specIdList == null || specIdList.size() == 0) {
                return CompletableFuture.completedFuture(null);
            }

            List<Integer> otherSpecs = specIdList.stream().filter(x -> !specDetails.containsKey(x)).collect(Collectors.toList());
            CompletableFuture othersSpecsFuture = specDetailComponent.getList(otherSpecs).thenAccept(oss -> {
                ts.put("8",System.currentTimeMillis() -s);
                if (Objects.nonNull(oss)  && oss.size() > 0) {
                    oss.forEach(os -> specDetails.put(os.getSpecId(), os));
                }
                resultBuilder.setSeriesids(String.join(",", specDetails.values().stream().map(x -> x.getSeriesId() + "").distinct().collect(Collectors.toList())));
            });

            //实测小视频
            CompletableFuture<Map<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>>> shiceSmallVideoDtoMap = specShiCeSmallVideoComponent.get(specIdList).thenApply(result -> {
                Map<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>> map = new HashMap<>();
                for (SpecShiCeVideoDto item : result) {
                    Map<Long, SpecShiCeSmallVideoResult.ResultBean> sm = map.get(item.getSpecId());
                    if (Objects.isNull(sm)) {
                        sm = new HashMap<>();
                    }
                    for (SpecShiCeSmallVideoResult.ResultBean video : item.getVideoInfoList()) {
                        sm.put(video.getTag_id(), video);
                    }
                    map.put(item.getSpecId(), sm);
                }
                ts.put("9",System.currentTimeMillis() -s);
                return map;
            });

            //之家实测
//            specIdList.forEach(i -> {
//                specTestDataComponent.getData(i);
//            });
            CompletableFuture<Map<Integer, SpecTestDataDto>> specTestDataMap = specTestDataComponent.get(specIdList).thenApply(result -> {
                ts.put("10",System.currentTimeMillis() -s);
                result.removeIf(x -> x == null);
                return result.stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x, (x1, x2) -> x1));
            });

            //是否可询价
            CompletableFuture<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceMap = specCityAskPriceComponent.get(specIdList, cityId).thenApply(result -> {
                ts.put("11",System.currentTimeMillis() -s);
                result.removeIf(Objects::isNull);
                return result.stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x, (v1, v2) -> v2));
            });
            //询价按钮程序化
            CompletableFuture<Map<Integer, SListAreaButtonResult>> smartAreaButtonMap = getSmartAreaButton(pm, siteId, specIdList, cityId, request.getPluginversion());

            //获取城市二手车价格
            CompletableFuture<List<SpecCityUsedCarDto>> specCityUsedCarDtoFuture = getSpecCityUsedCarDtoFuture(request, specIdList);
            //车型列表：必须先获取所有车型，才能进行下一步
            Map<String, List<String>> specConMap = new HashMap<>();
            return othersSpecsFuture.thenCompose(x -> {
                ts.put("12",System.currentTimeMillis() -s);
                if(request.getSite()==1){
                    resultBuilder.addAllConditionlist(buildSeriesConditionlist(request,specConMap, seriesYears, specDetails.values().stream().toList()));
                }else if (request.getSite() == 2) {
                    resultBuilder.addAllConditionlist(buildSpecConditionlist(request, seriesYears, specDetails.values().stream().toList()));
                }

                return CompletableFuture.allOf(
                        buildSpecItems(request,pm, siteId, specIdList, specDetails, specCityAskPriceMap,smartAreaButtonMap, specConMap, specCityUsedCarDtoFuture).thenAccept(specinfo->{
                            ts.put("buildSpecItems",System.currentTimeMillis() -s);
                            resultBuilder.setSpecinfo(specinfo);
                        }),
                        buildParamItems(request, siteId,specIdList, temp, shiceSmallVideoDtoMap, specTestDataMap, specCityAskPriceMap, specDetails, specCityUsedCarDtoFuture).thenAccept(pitem->{
                            ts.put("buildParamItems",System.currentTimeMillis() -s);
                            resultBuilder.addAllParamitems(pitem);
                        }),
                        buildConfigItems(request,siteId,specIdList, temp, shiceSmallVideoDtoMap, specTestDataMap, specDetails).thenAccept(citem->{
                            ts.put("buildConfigItems",System.currentTimeMillis() -s);
                            resultBuilder.addAllConfigitems(citem);
                        }),
                        buildSpecFootaskpriceinfo(request,specDetails,specCityAskPriceMap,smartAreaButtonMap, specCityUsedCarDtoFuture).thenAccept(foot->{
                            ts.put("buildSpecFootaskpriceinfo",System.currentTimeMillis() -s);
                            resultBuilder.setFootaskpriceinfo(foot);
                        }));

            });
        });
        tasks.add(main);

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenApply(x -> {
            ts.put("end",System.currentTimeMillis() -s);
            if(System.currentTimeMillis() -s > 250) {
                StringBuilder sb = new StringBuilder();
                ts.forEach((k, v) -> {
                    sb.append("，" + k + ":" + v);
                });
                log.warn("参数配置耗时："+sb.toString());
            }
            if(resultBuilder.getParamitemsList().isEmpty()){
                return response.setResult(NewSpecCompareResponse.Result.newBuilder()).setReturnMsg("无参配外显车型").build();
            }else{
                return response.setResult(resultBuilder).setReturnMsg("success").build();
            }

        }).exceptionally(e -> {
            log.error("参数配置接口异常", ExceptionUtils.getStackTrace(e));
            return response.setReturnCode(-1).setReturnMsg("服务器异常").build();
        });
    }


    //取二手车价格
    CompletableFuture<List<SpecCityUsedCarDto>> getSpecCityUsedCarDtoFuture(NewSpecCompareRequest request, List<Integer> specIdList) {
        CompletableFuture<List<SpecCityUsedCarDto>> specCityUsedCarDtoFuture = CompletableFuture.completedFuture(null);
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8") && StringUtils.equalsIgnoreCase(request.getUsedcarabtest(), "B")) {
            specCityUsedCarDtoFuture = specUsedCarPriceComponent.getList(specIdList, request.getCityid());
        }
        return specCityUsedCarDtoFuture;
    }

    CompletableFuture<List<Integer>> getSpecIds(NewSpecCompareRequest request, String specIdStr, int site, int year, int seriesId,
                                                ConcurrentMap<Integer, SpecDetailDto> specs,
                                                Vector<String> deleteSpecIds,
                                                Vector<String> deleteSpecName,
                                                List<SpecGroupOfSeriesDto> seriesYears
    ) {

        //车型不传年代款时，只显示当前车型的信息
        if ( site == 3) {
            return getFilterSpecIds(specIdStr,specs,deleteSpecIds,deleteSpecName);
        }

        if (site == 1 || site == 2) {
            return specYearNewComponent.getAsync(seriesId).thenCompose(specYearList -> {
                List<Integer> specIds = new ArrayList<>();
                List<Integer> stopSellSpecIds = new ArrayList<>();
                if (specYearList == null || specYearList.size() == 0) {
                    return CompletableFuture.completedFuture(specIds);
                }
                if(site == 1){
                    seriesYears.addAll(specYearList);
                }
                if(site == 2){
                    seriesYears.addAll(specYearList);
                    if(year == 0){
                        return getFilterSpecIds(specIdStr,specs,deleteSpecIds,deleteSpecName);
                    }
                }
                return CompletableFuture.supplyAsync(()->{
                    int onSellYearCount = 0;
                    int addStopSell = 0;
                    SpecGroupOfSeriesDto yearDto = specYearList.stream().filter(group -> group.getYearvalue() == year).findFirst().orElse(null);
                    if (site == 1 && request.getSourceid() == 101 && year > 0) {
                        if (Objects.nonNull(yearDto) && yearDto.getYearstate() != 40 && yearDto.getYearspeclist().stream().anyMatch(x -> x.getSpeclist().size() > 0 && x.getSpeclist().get(0).getState() == 40)) {
                            yearDto.getYearspeclist().forEach(x -> specIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1).map(z -> z.getSpecId()).collect(Collectors.toList())));
                            return specIds;
                        }
                    }

                    for (SpecGroupOfSeriesDto yearItem : specYearList) {
                        if (site == 1) {
                            if ((Objects.nonNull(yearDto) && yearDto.getYearstate() != 40 && request.getSourceid() == 101) || year == 0) {
                                if (StringUtils.equalsAny(yearItem.getYearname(), "在售", "即将销售")) {
                                    yearItem.getYearspeclist().forEach(x -> specIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1).map(z -> z.getSpecId()).collect(Collectors.toList())));
                                    if (onSellYearCount++ >= 2 && specIds.size() > 0) {
                                        return specIds;
                                    }
                                } else if (specIds.size() == 0 && addStopSell == 0) {
                                    yearItem.getYearspeclist().forEach(x -> stopSellSpecIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1).map(z -> z.getSpecId()).collect(Collectors.toList())));
                                    if (!stopSellSpecIds.isEmpty() && CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.0")) {
                                        addStopSell = 1;
                                    }
                                }
                            } else {
                                //车系参配来源 如果year > 0，就只返回当前年代款的
                                if (year > 0 && yearItem.getYearvalue() == year) {
                                    yearItem.getYearspeclist().forEach(x -> specIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1 && (site != 1 || y.getState() == 40)).map(z -> z.getSpecId()).collect(Collectors.toList())));
                                }
                            }
                        } else if (site == 2) {
                            //车型参配来源 如果year > 0，就只返回当前年代款的
                            if (year > 0 && yearItem.getYearvalue() == year) {
                                yearItem.getYearspeclist().forEach(x -> specIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1 && (site != 1 || y.getState() == 40)).map(z -> z.getSpecId()).collect(Collectors.toList())));
                            }
                        }
                    }
                    return specIds.size() > 0 ? specIds : stopSellSpecIds;
                });
            });
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }


    public CompletableFuture<List<Integer>> getFilterSpecIds(String specIdStr,ConcurrentMap<Integer, SpecDetailDto> specs,
                                                             Vector<String> deleteSpecIds,
                                                             Vector<String> deleteSpecName){
        List<Integer> specIds = Arrays.stream(specIdStr.trim().split(",")).map(x -> Integer.valueOf(x)).distinct().collect(Collectors.toList());
        return specDetailComponent.getList(specIds).thenApply(list -> {
            List<Integer> ids = new ArrayList<>();
            for (SpecDetailDto spec : list) {
                if (spec.getParamIsShow() == 1) {
                    ids.add(spec.getSpecId());
                } else {
                    deleteSpecIds.add(spec.getSpecId() + "");
                    deleteSpecName.add(spec.getSpecName());
                }
                specs.put(spec.getSpecId(), spec);
            }
            return ids;
        });
    }

    CompletableFuture<NewSpecCompareResponse.Result.Specinfo> buildSpecItems(
            NewSpecCompareRequest request, int pm, int site, List<Integer> specIdList,
            Map<Integer, SpecDetailDto> specs,
            CompletableFuture<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceMapFuture,
            CompletableFuture<Map<Integer, SListAreaButtonResult>> smartAreaButtonMapFuture, Map<String, List<String>> specConMap,
            CompletableFuture<List<SpecCityUsedCarDto>> specCityUsedCarDtoFuture
    ) {
        StopWatch stopWatch =new StopWatch("buildSpecItems");
        stopWatch.start("CompletableFuture.allOf");
        return CompletableFuture.allOf(specCityAskPriceMapFuture, smartAreaButtonMapFuture, specCityUsedCarDtoFuture).thenApply(x -> {
            stopWatch.stop();
            stopWatch.start("data");
            Map<Integer, SpecCityAskPriceDto> specCityAskPriceMap = specCityAskPriceMapFuture.join();
            Map<Integer, SListAreaButtonResult> specButtons = smartAreaButtonMapFuture.join();
            Map<Integer, SpecCityUsedCarDto> specCityUsedCarDtoMap = new HashMap<>();
            List<SpecCityUsedCarDto> specCityUsedCarDtos = specCityUsedCarDtoFuture.join();
            if(ListUtil.isNotEmpty(specCityUsedCarDtos)){
                specCityUsedCarDtos.removeIf(Objects::isNull);
                specCityUsedCarDtoMap = specCityUsedCarDtos.stream().collect(Collectors.toMap(SpecCityUsedCarDto::getSpecid, user -> user));
            }
            List<Specitem> result = new ArrayList<>();
            AtomicInteger onsaleorder = new AtomicInteger(1);
            for (Integer specId : specIdList) {
                SpecDetailDto specInfo = specs.get(specId);
                if (specInfo == null) {
                    continue;
                }
                String dynamicprice = "";
                if (specInfo.getState() == 10) {
                    dynamicprice = (specInfo.isBooked() ? "订金:" : "预售价:") + PriceUtil.getStrPrice(specInfo.getMinPrice(), specInfo.getMaxPrice());
                } else {
                    dynamicprice = "指导价:" + PriceUtil.getStrPrice(specInfo.getMinPrice(), specInfo.getMaxPrice());
                }

                Specitem.Builder specitem = Specitem.newBuilder();
                specitem.setYear(specInfo.getYearName());
                specitem.setBrandid(specInfo.getBrandId());
                specitem.setSpecid(specInfo.getSpecId());
                specitem.setCount(0);
                specitem.setParamisshow(specInfo.getParamIsShow());
                specitem.setSeriesid(specInfo.getSeriesId());
                specitem.setSeriesname(specInfo.getSeriesName());
                specitem.addAllPicitems(new ArrayList());
                specitem.setCanaskprice(0);  //询价逻辑由经销商接口来判断，默认不可询价
                specitem.setPresell(specInfo.getState() == 10 ? 1 : 0);
                specitem.setMinprice(com.autohome.app.cars.common.utils.StringUtils.subAfter(dynamicprice, ":", true));
                specitem.setSpecname(specInfo.getSpecName());
                specitem.setNoshowprice(specInfo.getMinPrice());
                specitem.setDownprice("");
                specitem.setSpecstatus(specInfo.getState());
                if(specInfo.getState()==20){
                    specitem.setOnsaleOrder(onsaleorder.getAndIncrement());
                }
                Specitem.Moresendinfo.Builder moresendinfo = Specitem.Moresendinfo.newBuilder();
                specitem.setMoresendinfo(moresendinfo.build());
                if (specInfo.getMinPrice() > 0) {
                    specitem.setDealerprice(String.format("%.2f", specInfo.getMinPrice() / 10000.0).toString() + "万");
                } else {
                    specitem.setDealerprice("--");
                }
                if (specInfo.getState() == 10 && specInfo.isBooked()) {
                    specitem.setPresell(0);
                    specitem.setMinprice(dynamicprice);
                    specitem.setDealerprice(dynamicprice);
                }
                specitem.setDynamicprice(dynamicprice);
                if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(dynamicprice) && dynamicprice.contains(":")) {
                    specitem.setPricetitle(com.autohome.app.cars.common.utils.StringUtils.subBefore(dynamicprice, ":", true) + "：");
                }
                specitem.setSpecisbooked(specInfo.isBooked() ? 1 : 0);
                specitem.setDealerpricetip("厂商指导价：");
                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.66.0")){
                    specitem.setReferpricetitle("参考价：");
                    specitem.setReferprice("暂无报价");
                }
                SpecCityUsedCarDto cityUsedCarDto = specCityUsedCarDtoMap.get(specId);
                SpecCityAskPriceDto askPrice = specCityAskPriceMap.get(specId);
                if (askPrice != null) {
                    specitem.setDealerprice("暂无报价");
                    specitem.setNoshowprice(askPrice.getMinPrice());
                    if (askPrice.getMinPrice() > 0) {
                        specitem.setCanaskprice(1);
                        specitem.setDealerprice(CommonHelper.df02.format(askPrice.getMinPrice() / 10000.0) + "万起");
                        specitem.setDealerpricetip("");
                    } else {
                        specitem.setDealerprice("--");
                        specitem.setDealerpricetip("");
                    }
                    if (specitem.getNoshowprice() > askPrice.getMinPrice()) {
                        specitem.setDownprice(CommonHelper.df02.format((specitem.getNoshowprice() - askPrice.getMinPrice()) / 10000.0) + "万");
                    }
                    //订金显示优化
                    if (specitem.getSpecisbooked() == 1) {
                        specitem.setDealerprice(specitem.getDynamicprice());
                    }
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.66.0")){
                        int minPrice = askPrice.getMinPrice();//获取参考价
                        specitem.setReferprice(minPrice <= 0 ? "暂无报价" : CommonHelper.df02.format(minPrice / 10000.0) + "万起");
                        specitem.setReferpricetitle("参考价：");
                    }
                } else {
                    //停售且不可询价的才走本期逻辑
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8")
                            && StringUtils.equalsIgnoreCase(request.getUsedcarabtest(), "B")
                            && specInfo.getState() == 40) {
                        double minPrice = 0;
                        if (cityUsedCarDto != null) {
                            minPrice = cityUsedCarDto.getMinprice();//二手价
                        }
                        specitem.setReferprice(minPrice <= 0 ? "暂无报价" : String.format("%.2f", minPrice) + "万起");
                        specitem.setReferpricetitle("二手价：");
                    }
                }

                SListAreaButtonResult dealerIMInfo = specButtons.get(specId);
                Specitem.Askpriceinfo.Builder askpriceinfo = buildSpecInfoAskPriceInfo(request, site, pm, specInfo.getSeriesId(), specId, askPrice, dealerIMInfo, cityUsedCarDto, specInfo);
                if (askpriceinfo != null) {
                    specitem.setAskpriceinfo(askpriceinfo);
                }

                Specitem.Iminfo.Builder iminfo = buildIminfo(request, pm, specId, askPrice, dealerIMInfo, cityUsedCarDto, specInfo);
                if (iminfo != null) {
                    specitem.setIminfo(iminfo);
                }

                //设置筛选项
                if (specConMap.containsKey(specId + "")) {
                    specitem.addAllCondition(specConMap.get(specId + ""));
                }
                result.add(specitem.build());
            }
            stopWatch.stop();
            printCost(stopWatch);
            return NewSpecCompareResponse.Result.Specinfo.newBuilder().addAllSpecitems(result).build();
        }).exceptionally(e -> logCompletableFutureError(e, "bindspecItems error", NewSpecCompareResponse.Result.Specinfo.newBuilder().build()));
    }


    private Specitem.Iminfo.Builder buildIminfo(NewSpecCompareRequest request, int pm, int specId, SpecCityAskPriceDto specCityAskPriceDto, SListAreaButtonResult dealerIMInfo, SpecCityUsedCarDto cityUsedCarDto, SpecDetailDto specInfo) {
        Specitem.Iminfo.Builder iminfo = Specitem.Iminfo.newBuilder();
        try {
            if (pm != 3
                    && Objects.isNull(specCityAskPriceDto)
                    && CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8")
                    && StringUtils.equalsIgnoreCase(request.getUsedcarabtest(), "B")
                    && specInfo != null
                    && specInfo.getState() == 40
                    && cityUsedCarDto != null
                    && cityUsedCarDto.getMinprice() > 0) {
                String scheme = "autohome://usedcar/buycarlist?pvareaid=112894&brand=";
                scheme += UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}", specInfo.getBrandId(), specInfo.getBrandName(), specInfo.getSeriesId(), specInfo.getSeriesName()));
                iminfo.setEntertype(1);
                iminfo.setImtitle("低价车源");
                iminfo.setImlinkurl(scheme);
                return iminfo;
            }
            if (pm == 3 || Objects.isNull(specCityAskPriceDto) || dealerIMInfo == null || dealerIMInfo.getButtonList() == null || dealerIMInfo.getButtonList().size() == 0) {
                return iminfo;
            }
            Optional<SListAreaButtonResult.ButtonListDTO> btnType3 = dealerIMInfo.getButtonList().stream().filter(x -> x.getSpecId() == specId && x.getBtnType() == 3).findFirst();
            if (btnType3.isPresent()) {
                // btnType == 3 im按钮处理
                String eid = com.autohome.app.cars.common.utils.StringUtils.format("&eid={0}", UrlUtil.encode(pm == 1 ? "3|1411002|572|3285|205313|304229" : "3|1412002|572|3285|205313|304228"));
                iminfo.setEntertype(0);
                iminfo.setImtitle(btnType3.get().getMainText());
                iminfo.setImlinkurl(btnType3.get().getImSchema() + eid);
                iminfo.setImiconurl("");
            }
            return iminfo;
        } catch (Exception e) {
            log.error("车型参配接口异常-buildIminfo error: {}", ExceptionUtils.getStackTrace(e));
            return iminfo;
        }
    }

    private Specitem.Askpriceinfo.Builder buildSpecInfoAskPriceInfo(
            NewSpecCompareRequest request,
            int site, int pm, int seriesId, int specId,
            SpecCityAskPriceDto specCityAskPriceDto,
            SListAreaButtonResult buttonList,
            SpecCityUsedCarDto cityUsedCarDto,
            SpecDetailDto specInfo) {
        try {
            Specitem.Askpriceinfo.Builder askpriceinfo = Specitem.Askpriceinfo.newBuilder();
            askpriceinfo.setAskpricesubtitle("");
            askpriceinfo.setAskpricetitle("询底价");
            askpriceinfo.setAskpriceurl("");
            askpriceinfo.setCopa("");
            askpriceinfo.setType(0);
            if (Objects.nonNull(specCityAskPriceDto)) {
                askpriceinfo.setCanaskprice(specCityAskPriceDto.getMinPrice() > 0 ? 1 : 0);
                if (askpriceinfo.getCanaskprice() == 1 && buttonList != null) {
                    String eid = eidMap.get(site + "_" + pm);
                    if (buttonList == null || buttonList.getButtonList() == null || buttonList.getButtonList().size() == 0) {
                        if (pm != 3) {  //不要鸿蒙
                            // 没有就走打底逻辑
                            String rnUrl = String.format("rn://DealerPriceRn/ReverseAuctionDialog?seriesid=%s&specid=%s&siteid=21&gps=1&eid=%s",
                                    seriesId, specId, UrlUtil.encode(eid));
                            askpriceinfo.setScheme(String.format("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s", UrlUtil.encode(rnUrl)));
                            askpriceinfo.setAskpricetitle("查报价单");
                            askpriceinfo.setAskpricesubtitle("");
                            askpriceinfo.setExt("{\"price_show\":36}");
                        }
                    } else {
                        SListAreaButtonResult.ButtonListDTO first = buttonList.getButtonList().stream().filter(x -> x.getSpecId() == specId && x.getBtnType() == 2).findFirst().orElse(null);
                        if (first != null) {
                            String scheme = first.getUrl();
                            if (pm != 3 && StringUtils.isBlank(scheme)) {
                                if (first.getWindowType() == 14) {
                                    scheme = String.format("autohome://dealerconsult/dealerprice?seriesid=%s&specid=%s&eid=%s", seriesId, specId, eid);
                                } else {
                                    String askPriceSchemaTemp = "autohome://car/asklowprice?customshowanimationtype=2&eid=%s&seriesid=%s&specid=%s&inquirytype=2&price_show=%s&title=%s&ext=%s";
                                    scheme = String.format(askPriceSchemaTemp, eid, seriesId, specId, DealerCommHelp.getPriceShowFromWindowType(first.getWindowType()), StringUtils.isNotBlank(first.getMainText()) ? UrlUtil.encode(first.getMainText()) : "", UrlUtil.encode(first.getExt()));
                                }
                            }
                            askpriceinfo.setScheme(scheme);
                            askpriceinfo.setAskpricetitle(first.getMainText());
                            askpriceinfo.setAskpricesubtitle("");
                            askpriceinfo.setExt(first.getExt()!=null?first.getExt():"{}");
                        }
                    }
                }
            } else {
                //查二手车价
                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8")
                        && StringUtils.equalsIgnoreCase(request.getUsedcarabtest(), "B")
                        && specInfo != null && specInfo.getState() == 40) {
                    double minPrice = 0;
                    if (cityUsedCarDto != null) {
                        minPrice = cityUsedCarDto.getMinprice();
                    }
                    String eid = "3|1411002|572|3285|211921|306976";
                    String scheme = "autohome://car/askusedcarprice?customshowanimationtype=2&animation_type=2&flutterPresentType=2&seriesid="
                            + seriesId + "&askspecid=" + specId + "&eid=" + UrlUtil.encode(eid) + "&successclose=1&title=" + UrlUtil.encode("获取二手车报价");
                    askpriceinfo.setCanaskprice(4);
                    askpriceinfo.setScheme(minPrice > 0 ? scheme : "");
                    askpriceinfo.setAskpricetitle("查二手车价");
                }
            }
            return askpriceinfo;
        } catch (Exception e) {
            log.error("车型参配接口异常-buildSpecInfoAskPriceInfo error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }


    /**
     * 构建参配筛选项列表 车系
     *
     * @param request
     * @param specYearList
     * @param specDetailDtos
     * @return
     */
    private List<Conditionlist> buildSeriesConditionlist(NewSpecCompareRequest request, Map<String, List<String>> specConMap, List<SpecGroupOfSeriesDto> specYearList, List<SpecDetailDto> specDetailDtos) {
        if (Objects.isNull(specYearList) && specYearList.size() == 0) {
            return new ArrayList<>();
        }

        //年款
        Map<String, String> unsale_yearMap = new HashMap<>();
        Map<String, String> onsale_yearMap = new HashMap<>();
        Map<String, String> stopsale_yearMap = new LinkedHashMap<>();
        //排量
        Map<String, String> disMap = new HashMap<>();
        //变速箱
        Map<String, String> goxMap = new HashMap<>();
        //环保标准
        Map<String, String> hbbzMap = new LinkedHashMap<>();
        //车身结构
        Map<String, String> structMap = new HashMap<>();
        //驱动形式
        Map<String, String> driveTypeMap = new HashMap<>();
        //座位数
        Map<String, String> seatMap = new HashMap<>();
        List<SpecDetailDto> specList = specDetailDtos;
        // 分组处理在售 和停售
        for (SpecGroupOfSeriesDto configSpecGroupDto : specYearList) {
            String yearname = configSpecGroupDto.getYearname();
            int yearstate = configSpecGroupDto.getYearstate();
            String yearvalue = configSpecGroupDto.getYearvalue() + "";
            if (Arrays.asList(10, 20, 30).contains(yearstate)) {
                configSpecGroupDto.getYearspeclist().forEach(year -> {
                    List<SpecGroupOfSeriesDto.Spec> speclist = year.getSpeclist();
                    boolean hasShow = speclist.stream().anyMatch(spec -> spec.getParamIsShow() == 1);
                    if (hasShow) {
                        if (yearstate == 10) {
                            unsale_yearMap.put(yearvalue, yearname);
                            boolean hasOnSale = speclist.stream().anyMatch(spec -> spec.getState() == 20);
                            if (hasOnSale) {
                                onsale_yearMap.put(yearvalue, yearname);
                            }
                        } else {
                            onsale_yearMap.put(yearvalue, yearname);
                        }
                    }
                    if (speclist != null) {
                        boolean hasStop = speclist.stream().anyMatch(spec -> spec.getState() == 40);
                        if (hasStop) {
                            stopsale_yearMap.put(configSpecGroupDto.getYearvalue() + "", configSpecGroupDto.getYearname());
                        }
                    }
                });
            } else if (yearstate == 40) {
                stopsale_yearMap.put(configSpecGroupDto.getYearvalue() + "", configSpecGroupDto.getYearname());
            }
        }

        specList.forEach(item -> {
            List<String> conList = new ArrayList<>();
            if (item.getYearName() > 0) {
                conList.add(item.getYearName() + "");
            }else{
                conList.add("-");
            }
            int isEc = 0;
            if (item.getFuelType() == 4) {
                if (Objects.nonNull(item.getDisplacement()) && item.getDisplacement().doubleValue() > 0d) {
                    String disStr = item.getDisplacement().doubleValue() + (item.getFlowModeId() == 1 ? "L" : "T");
                    disMap.put(disStr, disStr);
                    conList.add(disStr);
                } else {
                    isEc = 1;
                    disMap.put("新能源", "新能源");
                    conList.add("新能源");
                }
            } else {
                if (Objects.nonNull(item.getDisplacement()) && item.getDisplacement().doubleValue() > 0) {
                    String disStr = item.getDisplacement().doubleValue() + (item.getFlowModeId() == 1 ? "L" : "T");
                    disMap.put(disStr, disStr);
                    conList.add(disStr);
                }else{
                    conList.add("-");
                }
            }

            if (StringUtils.isNotEmpty(item.getGearbox())) {
                goxMap.put(item.getGearbox(), item.getGearbox());
                conList.add(item.getGearbox());
            }else{
                conList.add("-");
            }

            if (isEc == 1) {
                hbbzMap.put("纯电", "纯电");
                conList.add("纯电");
            } else if (StringUtils.isNotEmpty(item.getEmissionStandards())) {
                hbbzMap.put(item.getEmissionStandards(), item.getEmissionStandards());
                conList.add(item.getEmissionStandards());
            }else{
                conList.add("-");
            }
            if (StringUtils.isNotEmpty(item.getStructtype())) {
                structMap.put(item.getStructtype(), item.getStructtype());
                conList.add(item.getStructtype());
            }else{
                conList.add("-");
            }
            String newModeName = convertDrivemodeName(item.getDrivingModeName());
            if (StringUtils.isNotEmpty(newModeName)) {
                driveTypeMap.put(newModeName, newModeName);
                conList.add(newModeName);
            }else{
                conList.add("-");
            }
            if (StringUtils.isNotEmpty(item.getSeats())) {
                seatMap.put(item.getSeats(), item.getSeats() + "座");
                conList.add(item.getSeats());
            }else{
                conList.add("-");
            }

            specConMap.put(item.getSpecId() + "", conList);
        });
        int index = 0;
        //设置年款筛选项
        Conditionlist.Builder yearCon = Conditionlist.newBuilder();
        yearCon.setIndex(index);
        yearCon.setIsselectmore(1);
        yearCon.setTypeid(index);
        yearCon.setName("年款");
        yearCon.setTypevalue("year");
        List<Conditionlist.List.Builder> itemList = new ArrayList<>();
        Map<String, String> sale_yearMap = new HashMap<>();
        sale_yearMap.putAll(unsale_yearMap);
        sale_yearMap.putAll(onsale_yearMap);
        yearCon.setGrouptext(sale_yearMap.isEmpty()?"更多":"停售");
        for (String key : sale_yearMap.keySet()) {
            if (key == null || "0".equals(key)) {
                continue;
            }
            Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
            dtoItem.setId(key);
            dtoItem.setLazyload(0);
            dtoItem.setName(sale_yearMap.get(key));
            itemList.add(dtoItem);
        }
        int stopIndex = 0;
        for (String key : stopsale_yearMap.keySet()) {
            if (key == null || "0".equals(key)) {
                continue;
            }
            Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
            dtoItem.setId(key);
//            无在售年款时 lazyload=0

            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.0")) {
                int lazyload = 0;
                if (sale_yearMap.isEmpty()) {
                    lazyload = stopIndex == 0 ? 0 : 1;
                } else {
                    lazyload = 1;
                }
                dtoItem.setLazyload(lazyload);
            } else {
                dtoItem.setLazyload(sale_yearMap.isEmpty() ? 0 : 1);
            }
            dtoItem.setName(stopsale_yearMap.get(key));
            itemList.add(dtoItem);
            stopIndex++;
        }

        itemList.sort(Comparator.comparing(Conditionlist.List.Builder::getLazyload).thenComparing(Conditionlist.List.Builder::getName, Comparator.reverseOrder()));
        if (onsale_yearMap.size() > 1) {
            Conditionlist.List.Builder onsaleCon = Conditionlist.List.newBuilder();
            onsaleCon.setId("onsale");
            onsaleCon.setLazyload(0);
            onsaleCon.setName("在售");
            itemList.add(0, onsaleCon);
        }
        itemList.forEach(i -> {
            yearCon.addList(i);
        });
        List<Conditionlist> cl = new ArrayList<>();
        cl.add(yearCon.build());
        index++;
        Map<String, String> finalDisMap = new LinkedHashMap<>();
        if (!disMap.containsKey("新能源")) {
            disMap.keySet().stream().filter(i -> org.apache.commons.lang3.StringUtils.contains(i, "L")).forEach(i -> finalDisMap.put(i, disMap.get(i)));
            disMap.keySet().stream().filter(i -> org.apache.commons.lang3.StringUtils.contains(i, "T")).forEach(i -> finalDisMap.put(i, disMap.get(i)));
        } else {
            finalDisMap.putAll(disMap);
        }
        cl.add(getMapCondition(finalDisMap, "排量", 1, index++, "displacement", 0));
        cl.add(getMapCondition(goxMap, "变速箱", 1, index++, "gearbox", 0));
        if (!hbbzMap.isEmpty()) {
            cl.add(getMapCondition(hbbzMap, "环保标准", 1, index++, "standards", 0));
        }
        cl.add(getMapCondition(structMap, "车身结构", 1, index++, "cartype", 0));
        cl.add(getMapCondition(driveTypeMap, "驱动形式", 1, index++, "drivemode", 0));
        cl.add(getMapCondition(seatMap, "座位数", 1, index++, "seatcount", 0));
        return cl;
    }

    /**
     * 构建参配筛选项列表 车型
     *
     * @param specYearList
     * @param specDetailDtos
     * @return
     */
    private List<Conditionlist> buildSpecConditionlist(NewSpecCompareRequest request, List<SpecGroupOfSeriesDto> specYearList, List<SpecDetailDto> specDetailDtos) {
        List<Conditionlist> conList = new ArrayList<>();
        //年款
        Map<String, String> yearMap = new HashMap<>();
        if(Objects.nonNull(specYearList) && !specYearList.isEmpty()){
            for (SpecGroupOfSeriesDto configSpecGroupDto : specYearList) {
                if (configSpecGroupDto.getYearvalue() > 1000) {
                    if (Objects.nonNull(configSpecGroupDto.getYearspeclist())) {
                        configSpecGroupDto.getYearspeclist().forEach(year -> {
                            boolean hasShow = year.getSpeclist().stream().anyMatch(spec -> spec.getParamIsShow() == 1);
                            if (hasShow) {
                                yearMap.put(configSpecGroupDto.getYearvalue() + "", configSpecGroupDto.getYearname());
                            }
                        });
                    }
                }
            }
            int index = 0;
            //设置年款筛选项
            Conditionlist.Builder yearCon =Conditionlist.newBuilder();
            yearCon.setIndex(index);
            yearCon.setIsselectmore(1);
            yearCon.setTypeid(index);
            yearCon.setName("年款");
            yearCon.setTypevalue("year");
            List<Conditionlist.List.Builder> itemList =new ArrayList<>();
            for (String key : yearMap.keySet()) {
                Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
                if (key == null || "0".equals(key) ) {
                    continue;
                }
                dtoItem.setId(key);
                dtoItem.setLazyload(0);
                dtoItem.setName(yearMap.get(key));
                itemList.add(dtoItem);
            }

            itemList.sort(Comparator.comparing(Conditionlist.List.Builder::getName).reversed());

            if(yearMap.size()>0){
                Conditionlist.List.Builder onsaleCon =Conditionlist.List.newBuilder();
                onsaleCon.setId("currentspec");
                onsaleCon.setLazyload(0);
                onsaleCon.setName("当前车款");
                itemList.add(0,onsaleCon);
            }
            itemList.forEach(i->{
                yearCon.addList(i);
            });
            conList.add(yearCon.build());

        }
        return conList;
    }

    private String convertDrivemodeName(String drivemodeName) {
        if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(drivemodeName)) {
            if ("双电机后驱/前置前驱/前置后驱/中置后驱/后置后驱".contains(drivemodeName)) {
                return "两驱";
            } else if ("三电机四驱/前置四驱/中置四驱/后置四驱/双电机四驱/四电机四驱/电子适时四驱".contains(drivemodeName)) {
                return "四驱";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private Conditionlist getMapCondition(Map<String, String> map, String name, int isSelectMore, int index, String typekey, int lazyload) {
        Conditionlist.Builder builder = Conditionlist.newBuilder();
        builder.setIndex(index);
        builder.setIsselectmore(isSelectMore);
        builder.setTypeid(index);
        builder.setName(name);
        builder.setTypevalue(typekey);
        List<Conditionlist.List> itemList = new ArrayList<>();
        for (String key : map.keySet()) {
            Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
            if (key == null || "0".equals(key)) {
                continue;
            }
            dtoItem.setId(key);
            dtoItem.setLazyload(lazyload);
            dtoItem.setName(map.get(key));
            itemList.add(dtoItem.build());
        }
        builder.addAllList(itemList);
        return builder.build();
    }


    CompletableFuture<Map<Integer, SListAreaButtonResult>> getSmartAreaButton(int pm, int site, List<Integer> specIdList, int cityId, String pluginversion) {
        //询价按钮程序化 三端 deviceType
        String deviceType;
        if (pm == 1) {
            deviceType = "ios";
        } else if (pm == 2) {
            deviceType = "android";
        } else if (pm == 3) {
            deviceType = "harmony";
        } else {
            deviceType = "ios";
        }
        //询价按钮程序化 车系、车型、pk areaid
        int areaid;//车系
        if (site == 2) {
            areaid = 501;//车型
        } else if (site == 3) {
            areaid = 502;//pk
        } else {
            areaid = 500;
        }
        List<CompletableFuture> tasks = new ArrayList<>();
        ConcurrentMap<Integer, SListAreaButtonResult> results = new ConcurrentHashMap<>();
        Lists.partition(specIdList, 20).forEach(sublist -> {
            tasks.add(dealerApiClient.getListSmartAreaButton(cityId, deviceType, areaid, StringUtils.join(sublist, ","), pluginversion, "92f6e950_5616_4589_a7b2_0702fdb77432", "", 0, UUID.randomUUID().toString()).thenAccept(result -> {
                if (result != null && result.getResult() != null && result.getResult().size() > 0) {
                    results.putAll(result.getResult().stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x, (v1, v2) -> v2)));
                }
            }).exceptionally(e -> logCompletableFutureError(e, "getListSmartAreaButton", null)));
        });

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenApply(x -> results);
    }


    //TODO 承伟： 实测等项的值，刷到参配里面。
    CompletableFuture<List<Paramitem>> buildParamItems(
            NewSpecCompareRequest request,
            int siteId, List<Integer> specIds,
            CompletableFuture<SpecParamConfigTempDto> tempFuture,
            CompletableFuture<Map<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>>> shiceSmallVideoDtoMapFuture,
            CompletableFuture<Map<Integer, SpecTestDataDto>> specTestDataMap,
            CompletableFuture<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceMapFuture,
            Map<Integer, SpecDetailDto> specs,
            CompletableFuture<List<SpecCityUsedCarDto>> specCityUsedCarDtoFuture) {
//        specIds.forEach(i->{
//            specParamInfoNewComponent.refresh(i);
//        });
        StopWatch stopWatch =new StopWatch("buildParamItems");
        List<CompletableFuture> tasks = new ArrayList<>();
        //获取所有车型的参配信息
        CompletableFuture<Map<Integer, SpecParamConfigDto>> paramInfoListFuture = specParamInfoNewComponent.getMap(specIds);
        tasks.add(paramInfoListFuture);
        tasks.add(tempFuture);
        tasks.add(shiceSmallVideoDtoMapFuture);
        tasks.add(specTestDataMap);
        tasks.add(specCityAskPriceMapFuture);
        tasks.add(specCityUsedCarDtoFuture);
        stopWatch.start("CompletableFuture.allOf");
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenApply(x -> {
            stopWatch.stop();
            stopWatch.start("data");
            Map<Integer, SpecParamConfigDto> paramInfoList = paramInfoListFuture.join();
            Map<Integer, SpecCityAskPriceDto> askPriceMap = specCityAskPriceMapFuture.join();
            Map<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>> specShiceVideoMap = shiceSmallVideoDtoMapFuture.join();
            Map<Integer, SpecCityUsedCarDto> specCityUsedCarDtoMap = new HashMap<>();
            List<SpecCityUsedCarDto> specCityUsedCarDtos = specCityUsedCarDtoFuture.join();
            if(ListUtil.isNotEmpty(specCityUsedCarDtos)){
                specCityUsedCarDtos.removeIf(Objects::isNull);
                specCityUsedCarDtoMap = specCityUsedCarDtos.stream().collect(Collectors.toMap(SpecCityUsedCarDto::getSpecid, user -> user));
            }
            SpecParamConfigTempDto paramTemp = tempFuture.join();
            int specCount = specs.size();
            int newEnergyCount = 0;
            int evCount = 0;
            int oilCount = 0;
            for (SpecDetailDto spec : specs.values()) {
                int fuelType = spec.getFuelType();
                if (newEnergyFueltypeList.contains(fuelType)) {
                    newEnergyCount++;
                }
                if (fuelType == 4 || fuelType == 7) {
                    evCount++;
                }
                if (noElectricMotorFuelTypeList.contains(fuelType)) {
                    oilCount++;
                }
            }
            boolean allSpecIsPEV = (evCount == specCount);

            List<Paramitem> paramitems = new ArrayList<>();
            boolean has100003 = false;//有参考价时，优惠信息拼接在参考价下，无参考价时 优惠信息拼接在厂商指导价下

            for (SpecParamConfigTempDto.ParamTemp.ParamType paramType : paramTemp.getParam().getParamTypes(specCount, evCount, oilCount)) {
                //纯电不显发动机大分类
                if (allSpecIsPEV && paramType.getName().equals("发动机")) {
                    continue;
                }
                //油车不显示'电动机','电池/续航','充/放电'大分类
                if (oilCount == specCount && electricTypeList.contains(paramType.getName())) {
                    continue;
                }


                Paramitem.Builder paramTypeBuilder = Paramitem.newBuilder();
                for (SpecParamConfigTempDto.ParamTemp.ParamItem paramItem : paramType.getParamItems(specCount, newEnergyCount, evCount, oilCount)) {
                    if (paramType.getId() == 1) {  //基本参数大分类隐藏项
                        //全部是非新能源车型，隐藏“基本参数”的部分新能源参数
                        if (newEnergyCount == 0 && listNewEnergyParam.contains(paramItem.getName())) {
                            continue;
                        }
                        //纯电动不显示内容。
                        if (allSpecIsPEV && listNotDisPlayOfPEVCarParam.contains(paramItem.getName())) {
                            continue;
                        }
                        //燃料形式全是油的车型，不显电动机的基本参数项
                        if (oilCount == specCount && StringUtils.equalsAny(paramItem.getName(),"电动机(Ps)","电动机总功率(kW)","电动机总扭矩(N·m)")) {
                            continue;
                        }

                    }

                    List<Item.Modelexcessid> models = new ArrayList<>();
                    boolean anyItemHasValue = false;
                    for (Integer specId : specIds) {
                        SpecParamConfigDto specParams = paramInfoList.get(specId);
                        Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();

                        modelexcessid.setId(specId);
                        modelexcessid.setValue("-");
                        modelexcessid.setPriceinfo("-1");
                        modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder().setType(0).setTitle("").build());

                        SpecDetailDto specDetailDto = specs.get(specId);
                        SpecCityUsedCarDto specCityUsedCarDto = specCityUsedCarDtoMap.get(specId);
                        if (paramItem.getId() == 100003) {
                            //参考价 id=100003
                            modelexcessid.setPriceinfo("");
                            SpecCityAskPriceDto specCityAskPriceDto = askPriceMap.get(specId);
                            if (Objects.nonNull(specCityAskPriceDto) && specCityAskPriceDto.getMinPrice()>0) {
                                anyItemHasValue = true;
                                has100003 = true;
                                modelexcessid.setValue(specCityAskPriceDto.getMinPrice() > 0 ? CommonHelper.df02.format(specCityAskPriceDto.getMinPrice() / 10000.0) + "万起" : "暂无报价");
                            } else {
                                modelexcessid.setValue("暂无报价");
                                //二手价
                                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8")
                                        && StringUtils.equalsIgnoreCase(request.getUsedcarabtest(), "B")
                                        && specDetailDto.getState() == 40) {
                                    anyItemHasValue = true;
                                    has100003 = true;
                                    double minPrice = 0;
                                    if(specCityUsedCarDto != null){
                                        minPrice = specCityUsedCarDto.getMinprice();
                                    }
                                    modelexcessid.setValue(minPrice > 0 ? String.format("%.2f", minPrice) + "万起(二手价)" : "暂无报价(二手价)");
                                }
                            }
                        } else if (paramItem.getId() == 100005) {
                            if(has100003){
                                anyItemHasValue = true;
                            }
                            //优惠信息 id=100005
                            modelexcessid.setPriceinfo("");
                            modelexcessid.setValue("暂无");
                        } else if (paramItem.getId() == 100006) {
                            if(!has100003){
                                paramItem.setId(100005);
                                anyItemHasValue = true;
                            }
                            //优惠信息 id=100005
                            modelexcessid.setPriceinfo("");
                            modelexcessid.setValue("暂无");
                        } else {
                            if (Objects.nonNull(specParams)) {
                                String key = paramType.getId() + "_" + paramItem.getId();
                                //实测添加项 取ContentId
                                if (paramItem.getDataType() == 1000) {
                                    key = key + "_" + paramItem.getContentId();
                                    modelexcessid.setValue("-");
                                }
                                SpecParamConfigDto.Item sourceItem = specParams.getItemMap().get(key);
                                if (sourceItem != null) {
                                    if(!StringUtils.equalsAny(sourceItem.getValue(),"","-")){
                                        anyItemHasValue = true;
                                        modelexcessid.setValue(sourceItem.getValue() == null ? "" : sourceItem.getValue());
                                    }
                                    if (paramItem.getDataType() == 1000) {
                                        modelexcessid.setValue("-");
                                    }
                                    modelexcessid.setSubvalue(sourceItem.getSubvalue() == null ? "" : sourceItem.getSubvalue());
                                    modelexcessid.setCornertype(sourceItem.getCornertype());
                                    modelexcessid.setCornerscheme(sourceItem.getCornerscheme() == null ? "" : sourceItem.getCornerscheme()+(siteId+6));
                                    if (sourceItem.getSublist() != null && sourceItem.getSublist().size() > 0) {
                                        anyItemHasValue = true;
                                        modelexcessid.setValue("");
                                        for (SpecParamConfigDto.SubItem subItem : sourceItem.getSublist()) {
                                            Item.Modelexcessid.Sublist.Builder subList = Item.Modelexcessid.Sublist.newBuilder();
                                            subList.setName(subItem.getName()).setValue(subItem.getValue()!=null?subItem.getValue():"");
                                            if (Objects.nonNull(subItem.getPriceinfo())) {
                                                subList.setPriceinfo(subItem.getPriceinfo());
                                            }
                                            modelexcessid.addSublist(subList);
                                        }
                                    }
                                }
                            }
                            // 处理小视频;产品说小视频和视频不会冲突;
                            Map<Long, SpecShiCeSmallVideoResult.ResultBean> shiceSmallVideoDtoMap = specShiceVideoMap.get(specId);
                            if (shiceSmallVideoDtoMap != null) {
                                if ("加速性能(s)".equalsIgnoreCase(paramItem.getName()) && shiceSmallVideoDtoMap.containsKey(30050011018L)) {
                                    modelexcessid.setCornertype(2);
                                    modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011018L).getApp_jump() + "&loadmodel=0&source=66");
                                }
                                if ("刹车性能(m)".equalsIgnoreCase(paramItem.getName()) && shiceSmallVideoDtoMap.containsKey(30050011019L)) {
                                    modelexcessid.setCornertype(2);
                                    modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011019L).getApp_jump() + "&loadmodel=0&source=66");
                                }
                            }
                        }

                        models.add(modelexcessid.build());
                    }
                    if (anyItemHasValue || paramItem.getDynamicShow() != 1) {  //动态外显且没有车型有值的，就不显示了
                        Item.Builder item = Item.newBuilder()
                                .addAllModelexcessids(models)
                                .setId(paramItem.getBaikeId()>0?paramItem.getBaikeId():-1)
                                .setParamitemid(paramItem.getId())
                                .setName(paramItem.getName());
                        //参考价、优惠信息 特殊处理
                        if (Arrays.asList(100003, 100005).contains(paramItem.getId())) {
                            item.setSubid(paramItem.getId());
                            item.setParamitemid(0);
                        }
                        if (paramItem.getContentId() != null && paramItem.getDataType() != 1000) {
                            item.setContentid(paramItem.getContentId());
                            item.setPlaystarttime(paramItem.getPlayStartTime());
                        }
                        if(paramItem.getDataType() == 1000){
                            item.setParamitemid(0);
                        }
                        paramTypeBuilder.addItems(item);
                    }
                }
                if (paramTypeBuilder.getItemsCount() > 0) {
//                    paramTypeBuilder.setGroupname(newEnergyCount == specCount && threeElectricType.contains(paramType.getName()) ? "三电系统" : "参数信息");
                    paramTypeBuilder.setGroupname("参数信息");
                    paramTypeBuilder.setItemtype(paramType.getName());
                    paramTypeBuilder.setShowtips(false);
                    paramitems.add(paramTypeBuilder.build());
                }
            }
            stopWatch.stop();
            printCost(stopWatch);
            return paramitems;
        }).exceptionally(e -> logCompletableFutureError(e, "bindparamerror", new ArrayList<>()));
    }

    CompletableFuture<List<Configitem>> buildConfigItems(
            NewSpecCompareRequest request, int siteId, List<Integer> specIds,
            CompletableFuture<SpecParamConfigTempDto> tempFuture,
            CompletableFuture<Map<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>>> shiceSmallVideoDtoMapFuture,
            CompletableFuture<Map<Integer, SpecTestDataDto>> specTestDataMap,
            Map<Integer, SpecDetailDto> specs
    ) {
        List<Integer> seriesIds = specs.values().stream().map(x -> x.getSeriesId()).distinct().collect(Collectors.toList());
//        specIds.forEach(i -> {
//            specConfigSmallVideoComponent.refresh(i);
//            specParamConfigPicInfoComponent.refresh(i);
//            specOutInnerColorComponent.refresh(i);
//            specConfigBagComponent.refresh(i);
//            specSpecificConfigComponent.refresh(i);
//            specConfigInfoNewComponent.refresh(i);
//        });
        int specCount = specs.size();
        int newEnergyCount = 0;
        int evCount = 0;
        int oilCount = 0;

        for (SpecDetailDto spec : specs.values()) {
            int fuelType = spec.getFuelType();
            if (newEnergyFueltypeList.contains(fuelType)) {
                newEnergyCount++;
            }
            if (fuelType == 4 || fuelType == 7) {
                evCount++;
            }
            if (noElectricMotorFuelTypeList.contains(fuelType)) {
                oilCount++;
            }
        }
        boolean allSpecIsPEV = (evCount == specCount);
        StopWatch stopWatch =new StopWatch("buildConfigItems");
        stopWatch.start("CompletableFuture.allOf");
        CompletableFuture<Map<Integer, SeriesVr>> seriesVrsFuture = seriesVrComponent.getMap(seriesIds);
        CompletableFuture<Map<Integer, SpecConfigSmallVideoDto>> smallVideosFuture = specConfigSmallVideoComponent.getMap(specIds);
        CompletableFuture<Map<Integer, SpecAiVideoDto>> aiVideoFuture = specAiVideoComponent.getMap(specIds);
        CompletableFuture<List<SpecParamConfigPicInfoDto>> paramConfigPicInfoFuture = specParamConfigPicInfoComponent.get(specIds);
        //获取需要展示的参配百科数据
        List<ConfigInfoDto> configInfos = JSONObject.parseObject(baiKeInFoConfig, new TypeReference<List<ConfigInfoDto>>() {
        });
        Map<String, Integer> configMap = new HashMap<>();
        configInfos.forEach(infos -> configMap.put(infos.getConfigName(), infos.getConfigId()));
        //特色配置
        CompletableFuture<Configitem> specificConfigFuture = getSpecIficConfig(specIds);
        //选配颜色
        CompletableFuture<Configitem> specColorConfigFuture = getSpecColorConfig(specIds);
        //选装包
        CompletableFuture<Configitem> configBagFuture = getSpecConfigBag(specIds);
        //获取所有车型的参配信息
        CompletableFuture<Map<Integer, SpecParamConfigDto>> configInfoListFuture = specConfigInfoNewComponent.getMap(specIds);
        List<Configitem> configitems = new ArrayList<>();
        return CompletableFuture.allOf(tempFuture, seriesVrsFuture, smallVideosFuture,
                aiVideoFuture, shiceSmallVideoDtoMapFuture, paramConfigPicInfoFuture,
                specificConfigFuture,specColorConfigFuture, configBagFuture, specTestDataMap).thenApply(y -> {
            stopWatch.stop();

            Map<Integer, SpecParamConfigDto> specConfigMap = configInfoListFuture.join();
            Map<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>> specShiceVideoMap = shiceSmallVideoDtoMapFuture.join();
            Map<Integer, SpecAiVideoDto> aiVideoMap = aiVideoFuture.join();
            Map<Integer, SpecConfigSmallVideoDto> smallVideoMap = smallVideosFuture.join();
            List<SpecParamConfigPicInfoDto> configPicInfoList = paramConfigPicInfoFuture.join();
            stopWatch.start("configPicMap");
            ConcurrentHashMap<Integer, List<Integer>> configPicMap = new ConcurrentHashMap<>();//配置项图片
            if (configPicInfoList != null) {
                configPicInfoList.removeIf(Objects::isNull);
                configPicInfoList.stream().forEach(haspicdata -> {
                    if (ListUtil.isNotEmpty(haspicdata.getList())) {
                        haspicdata.getList().forEach(picitemdata -> {
                            if (picitemdata.getDatatype() == 2) {
                                if (configPicMap.containsKey(haspicdata.getSpecId())) {
                                    configPicMap.get(haspicdata.getSpecId()).add(picitemdata.getItemid());
                                } else {
                                    List<Integer> list = new ArrayList();
                                    list.add(picitemdata.getItemid());
                                    configPicMap.put(haspicdata.getSpecId(), list);
                                }
                            }
                        });
                    }
                });
            }
            stopWatch.stop();
            stopWatch.start("data");
            //处理配置项数据
            for (SpecParamConfigTempDto.ConfigTemp.ConfigType configType : tempFuture.join().getConfig().getParamTypes()) {
                Configitem.Builder configTypeBuilder = Configitem.newBuilder();
                for (SpecParamConfigTempDto.ConfigTemp.ConfigItem paramItem : configType.getParamItems()) {
                    if (allSpecIsPEV && SpecElectric.dicExcludePEVCarConfig.containsKey(paramItem.getId())){
                        continue;
                    }
                    List<Item.Modelexcessid> models = new ArrayList<>();
                    boolean anyItemHasValue = false;
                    Item.Builder item = Item.newBuilder()
                            .setId(paramItem.getBaikeId()>0?paramItem.getBaikeId():-1)
                            .setSubid(paramItem.getId())
                            .setParamitemid(paramItem.getId())
                            .setName(paramItem.getName());
                    for (Integer specId : specIds) {
                        SpecParamConfigDto specParams = specConfigMap.get(specId);
                        SpecDetailDto specDetailDto = specs.get(specId);
                        Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();

                        modelexcessid.setId(specId);
                        modelexcessid.setValue("-");
                        modelexcessid.setPriceinfo("-1");
                        modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder().setType(0).setTitle("").build());
                        if (Objects.nonNull(specParams) && Objects.nonNull(specDetailDto)) {
                            String key = configType.getId() + "_" + paramItem.getId();
                            //实测添加项 取ContentId
                            if (paramItem.getDataType() == 1000) {
                                key = paramItem.getId() + "_"+ TestDataConfigItemEnum.getValueByName(paramItem.getName());
                            }
                            SpecParamConfigDto.Item sourceItem = specParams.getItemMap().get(key);
                            if (sourceItem != null) {
                                modelexcessid.setValue(sourceItem.getValue() == null ? "" : sourceItem.getValue().replace("&nbsp;", " ").replace("&amp;", "&").replace("/", " / "));
                                modelexcessid.setSubvalue(sourceItem.getSubvalue() == null ? "" : sourceItem.getSubvalue());
                                if(StringUtils.isNotEmpty(sourceItem.getPriceinfo())){
                                    modelexcessid.setPriceinfo(sourceItem.getPriceinfo());
                                }
                                if(!"-".equals(modelexcessid.getValue())){
                                    anyItemHasValue = true;
                                }
                                if (paramItem.getDataType() == 1000 && StringUtils.isNotEmpty(modelexcessid.getSubvalue())) {
                                    anyItemHasValue = true;
                                }
                                modelexcessid.setCornertype(sourceItem.getCornertype());
                                modelexcessid.setCornerscheme(sourceItem.getCornerscheme() == null ? "" : sourceItem.getCornerscheme()+ ( siteId+ 6));
                                if (sourceItem.getSublist() != null && sourceItem.getSublist().size() > 0) {
                                    anyItemHasValue = true;
                                    modelexcessid.setValue("");
                                    for (SpecParamConfigDto.SubItem subItem : sourceItem.getSublist()) {
                                        Item.Modelexcessid.Sublist.Builder subList = Item.Modelexcessid.Sublist.newBuilder();
                                        subList.setName(subItem.getName()).setValue(subItem.getValue()).setId(subItem.getSubitemid());
                                        if (Objects.nonNull(subItem.getPriceinfo())) {
                                            subList.setPriceinfo(subItem.getPriceinfo());
                                        }
                                        modelexcessid.addSublist(subList);
                                    }
                                }
                            }
                        }
                        //有视频数据，跳转到“autohome://car/videomotor”页面播放
                        String videourl = "";
                        String videoid = "";
                        String small_videoId = "";
                        if (smallVideoMap.containsKey(specId)) {
                            SpecSmallVideoResult.ResultBean.VideolistBean videoInfo = smallVideoMap.get(specId).getVideoInfoMap().get(paramItem.getId());
                            if (Objects.nonNull(videoInfo)) {
                                modelexcessid.setPlayurl(videoInfo.getPlayurl());
                                videourl = videoInfo.getPlayurl();
                                small_videoId = videoInfo.getMediaid();
                            }
                        }
                        if (aiVideoMap.containsKey(specId)) {
                            SpecAiVideoDto.SpecAiVideoResult videoInfo = aiVideoMap.get(specId).getVideoInfoMap().get(paramItem.getId());
                            if (Objects.nonNull(videoInfo)) {
                                modelexcessid.setVideoid(videoInfo.getVideoid());
                                videoid = videoInfo.getVideoid();
                            }
                        }
                        if ((StringUtils.isNotEmpty(modelexcessid.getValue()) && !modelexcessid.getValue().equals("-")) || modelexcessid.getSublistCount() > 0) {
                            if (StringUtils.isNotEmpty(videoid) || StringUtils.isNotEmpty(videourl)) {
                                String url = String.format("autohome://car/videomotor?vid=%s&iconurl=%s&videourl=%s&type=1", videoid, "", UrlUtil.encode(videourl));
                                modelexcessid.setCornertype(1);
                                modelexcessid.setCornerscheme(url);
                                modelexcessid.setLinkurl(url);
                            }
                        }
                        // 处理小视频;产品说小视频和视频不会冲突;
                        Map<Long, SpecShiCeSmallVideoResult.ResultBean> shiceSmallVideoDtoMap = specShiceVideoMap.get(specId);
                        if (shiceSmallVideoDtoMap != null) {
                            if ("加速性能(s)".equalsIgnoreCase(paramItem.getName()) && shiceSmallVideoDtoMap.containsKey(30050011018L)) {
                                modelexcessid.setCornertype(2);
                                modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011018L).getApp_jump() + "&loadmodel=0&source=66");
                            }
                            if ("刹车性能(m)".equalsIgnoreCase(paramItem.getName()) && shiceSmallVideoDtoMap.containsKey(30050011019L)) {
                                modelexcessid.setCornertype(2);
                                modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011019L).getApp_jump() + "&loadmodel=0&source=66");
                            }
                        }

                        // 处理图片
                        List<Integer> configPicInfoDtoList = configPicMap.get(specId);
                        if (Objects.nonNull(configPicInfoDtoList) && configPicInfoDtoList.contains(paramItem.getId())) {
                            modelexcessid.setHaspic(1);
                            modelexcessid.setCornertype(3);
                            modelexcessid.setCornerscheme("");
                            item.setDatatype(2);
                        }
                        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0") && "B".equals(request.getBaikeabtest())) {
                            if (StringUtils.isNotEmpty(paramItem.getName()) && configMap.get(paramItem.getName()) != null && ((StringUtils.isNotEmpty(modelexcessid.getValue()) && !"-".equals(modelexcessid.getValue())) || !modelexcessid.getSublistList().isEmpty())) {
                                modelexcessid.setCornertype(6);
                                String subitemid ="";
                                if(Objects.nonNull(modelexcessid.getSublistList()) && !modelexcessid.getSublistList().isEmpty()){
                                    subitemid =  modelexcessid.getSublistList().get(0).getId()+"";
                                }
                                String scheme = "autohome://carcompare/configjiedu?customshowanimationtype=2&title=%s&configid=%d&seriesid=%d&specid=%d&typeid=%d&videoid=%s&itemid=%d&year=%d&subitemid=%s";
                                modelexcessid.setCornerscheme(String.format(scheme, UrlUtil.encode(paramItem.getName()), configMap.get(paramItem.getName()), specDetailDto.getSeriesId(), specDetailDto.getSpecId(),request.getSite(),small_videoId,paramItem.getId(),specDetailDto.getYearName(),subitemid));
                            }else{
                                //命中实验但不展示百科的参配，隐藏图片、视频标签
                                modelexcessid.setCornertype(0);
                                modelexcessid.setHaspic(0);
                                modelexcessid.setCornerscheme("");
                                modelexcessid.setPlayurl("");
                                modelexcessid.setLinkurl("");
                                modelexcessid.setVideoid("");
                            }
                        }
                        models.add(modelexcessid.build());
                    }
                    if (anyItemHasValue || paramItem.getDynamicShow() != 1) {  //动态外显且没有车型有值的，就不显示了
                        item.addAllModelexcessids(models);
                        //参考价、优惠信息 特殊处理
                        if (Arrays.asList(100003, 100005).contains(paramItem.getId())) {
                            item.setSubid(paramItem.getId());
                            item.setParamitemid(0);
                        }
                        if (paramItem.getContentId() != null) {
                            item.setContentid(paramItem.getContentId());
                            item.setPlaystarttime(paramItem.getPlayStartTime());
                        }
                        if (paramItem.getDataType() == 1000){
                            item.setParamitemid(0);
                            item.setContentid("");
                        }
                        configTypeBuilder.addItems(item);
                    }
                }
                if (configTypeBuilder.getItemsCount() > 0) {
                    configTypeBuilder.setGroupname(configType.getGroupname());
                    configTypeBuilder.setItemtype(configType.getName());
                    configTypeBuilder.setShowtips(true);
                    configitems.add(configTypeBuilder.build());
                }
            }
            stopWatch.stop();
            stopWatch.start("specIficConfig");
            //处理个性化 特色配置
            Configitem specIficConfig = specificConfigFuture.join();
            if (Objects.nonNull(specIficConfig)) {
                configitems.add(specIficConfig);
            }
            stopWatch.stop();
            stopWatch.start("specColorConfig");
            //处理个性化 颜色
            Configitem specColorConfig = specColorConfigFuture.join();
            if (Objects.nonNull(specColorConfig)) {
                configitems.add(specColorConfig);
            }
            stopWatch.stop();
            stopWatch.start("configBagItem");
            //处理个性化 选装包
            Configitem configBagItem = configBagFuture.join();
            if (Objects.nonNull(configBagItem)) {
                configitems.add(configBagItem);
            }
            stopWatch.stop();
            printCost(stopWatch);
            return configitems;
        }).exceptionally(e -> logCompletableFutureError(e, "bindconfigerror", new ArrayList<>()));
    }

    public void printCost(StopWatch stopWatch){
//        StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();
//        StringBuilder sb = new StringBuilder();
//        sb.append("\n");
//        sb.append(String.format("%s cost:%s \n",stopWatch.getId(),stopWatch.getTotalTimeMillis()));
//        for (StopWatch.TaskInfo tak:taskInfo){
//            sb.append(String.format("%s:%s",tak.getTaskName(),tak.getTimeMillis())+"\n") ;
//        }
//        log.warn(sb.toString());
    }

    private CompletableFuture<Configitem> getSpecColorConfig(List<Integer> specIds) {
        return specOutInnerColorComponent.getMap(specIds, false).thenCombine(specOutInnerColorComponent.getMap(specIds, true),(outColorDtoMap,innerColorMap)->{
            try {
                List<Item> items = new ArrayList<>();

                boolean anyOuterHasValue = false;
                boolean anyInnerHasValue = false;
                //外观颜色
                Item.Builder outItem = Item.newBuilder();
                outItem.setName("外观颜色");
                outItem.setParamitemid(0);
                outItem.setSubid(20210610);
                outItem.setId(-1);
                //内饰颜色
                Item.Builder innerItem = Item.newBuilder();
                innerItem.setName("内饰颜色");
                innerItem.setParamitemid(0);
                innerItem.setSubid(20210610);
                innerItem.setId(-1);
                for (Integer specId : specIds) {
                    Item.Modelexcessid.Builder outer_meid = Item.Modelexcessid.newBuilder();
                    outer_meid.setId(specId);
                    outer_meid.setValue("-");
                    SpecOutInnerColorDto outerColorDto = outColorDtoMap.get(specId);
                    if (Objects.nonNull(outerColorDto) && !outerColorDto.getColoritems().isEmpty()) {
                        Item.Modelexcessid.Colorinfo colorinfo = getConfigColorInfo(1, outerColorDto);
                        if (colorinfo != null) {
                            anyOuterHasValue = true;
                            outer_meid.setValue("");
                            outer_meid.setColorinfo(colorinfo);
                        }
                    }
                    outItem.addModelexcessids(outer_meid);

                    Item.Modelexcessid.Builder inner_meid = Item.Modelexcessid.newBuilder();
                    inner_meid.setId(specId);
                    inner_meid.setValue("-");
                    SpecOutInnerColorDto innerColorDto = innerColorMap.get(specId);
                    if (Objects.nonNull(innerColorDto) && !innerColorDto.getColoritems().isEmpty()) {
                        Item.Modelexcessid.Colorinfo colorinfo = getConfigColorInfo(2, innerColorDto);
                        if (colorinfo != null) {
                            anyInnerHasValue = true;
                            inner_meid.setValue("");
                            inner_meid.setColorinfo(colorinfo);
                        }
                    }
                    innerItem.addModelexcessids(inner_meid);
                }

                items.add(outItem.build());

                items.add(innerItem.build());

                //有配置项才返回
                if (ListUtil.isNotEmpty(items)) {
                    Configitem.Builder configitem = Configitem.newBuilder();
                    configitem.setGroupname("个性化");
                    configitem.setItemtype("颜色");
                    configitem.setShowtips(true);
                    configitem.addAllItems(items);
                    return configitem.build();
                }
            } catch (Exception e) {
                log.error("车型参配接口异常-buildSpecColorConfig error", e);
            }
            return null;
        }).exceptionally(e -> {
            log.warn("getSpecColorConfig error",e);
            return null;
        });

    }

    private CompletableFuture<Configitem> getSpecConfigBag(List<Integer> specIds) {
        return specConfigBagComponent.get(specIds).thenApply(list->{
            if(Objects.isNull(list)){
                return null;
            }
            list.removeIf(Objects::isNull);
            List<SpecConfigBagDto.ConfigBagValue> allConfigBagList = new ArrayList<>();
            list.forEach(item -> allConfigBagList.addAll(item.getConfigbags()));
            LinkedHashMap<String, Map<Integer, SpecConfigBagDto.ConfigBagValue>> allConfigBagMap = allConfigBagList.stream().collect(Collectors.groupingBy(config ->
                    config.getName(), LinkedHashMap::new, Collectors.toMap(SpecConfigBagDto.ConfigBagValue::getSpecid, Function.identity(), (k1, k2) -> k2)));
            List<Item> configitems = new ArrayList<>();
            for (String bagname : allConfigBagMap.keySet()) {
                Item.Builder item = Item.newBuilder();
                item.setName(bagname);
                item.setParamitemid(0);
                item.setSubid(20231019);
                item.setId(-1);
                item.setVideoid("");
                Map<Integer, SpecConfigBagDto.ConfigBagValue> configBagValueMap = allConfigBagMap.get(bagname);
                for (Integer specId : specIds) {
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specId);
                    modelexcessid.setPriceinfo("");
                    modelexcessid.setValue("-");
                    SpecConfigBagDto.ConfigBagValue configBagValue = configBagValueMap.get(specId);
                    if (configBagValue != null) {
                        //截取部分字符串，这里一个汉字的长度认为是2
                        if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(configBagValue.getDescription())) {
                            String str = subPreGbk(configBagValue.getDescription().replace("&#8304;", "度").replace("&#8482;", "™").replace("&#174", "®"), 600, "...");
                            modelexcessid.setValue(str != null ? str : "");
                        }
                        Item.Modelexcessid.Sublist.Builder sublist = Item.Modelexcessid.Sublist.newBuilder();
                        sublist.setName("选配");
                        sublist.setPriceinfo(configBagValue.getPricedesc());
                        sublist.setValue("○");
                        modelexcessid.addSublist(sublist);
                    }
                    item.addModelexcessids(modelexcessid);
                }

                configitems.add(item.build());
            }

            //组装
            if (ListUtil.isNotEmpty(configitems)) {
                Configitem.Builder configitem = Configitem.newBuilder();
                configitem.setGroupname("个性化");
                configitem.setItemtype("选装包");
                configitem.setShowtips(true);
                configitem.addAllItems(configitems);
                return configitem.build();
            }
            return null;
        }).exceptionally(e -> {
            log.warn("getSpecConfigBag error",e);
            return null;
        });

    }

    private CompletableFuture<Configitem> getSpecIficConfig(List<Integer> specIds) {
        return specSpecificConfigComponent.get(specIds).thenApply(specificList->{
            if(Objects.isNull(specificList)){
                return null;
            }
            specificList.removeIf(Objects::isNull);
            List<SpecSpecificConfigDto.ConfigItem> allConfigItemList = new ArrayList<>();
            specificList.forEach(item -> allConfigItemList.addAll(item.getConfigitems()));
            LinkedHashMap<String, Map<Integer, SpecSpecificConfigDto.ConfigItem>> allConfigItemMap = allConfigItemList.stream().collect(Collectors.groupingBy(config ->
                    config.getName(), LinkedHashMap::new, Collectors.toMap(SpecSpecificConfigDto.ConfigItem::getSpecid, Function.identity(), (k1, k2) -> k2)));
            List<Item> configitems = new ArrayList<>();
            for (String ificname : allConfigItemMap.keySet()) {
                Item.Builder item = Item.newBuilder();
                item.setName(ificname);
                Map<Integer, SpecSpecificConfigDto.ConfigItem> ificValueMap = allConfigItemMap.get(ificname);
                for (Integer specId : specIds) {
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specId);
                    modelexcessid.setValue("-");
                    modelexcessid.setPriceinfo("-1");
                    SpecSpecificConfigDto.ConfigItem ificValue = ificValueMap.get(specId);
                    if (ificValue != null) {
                        item.setId(ificValue.getBaikeid() > 0 ? ificValue.getBaikeid() : -1);
                        modelexcessid.setValue(ificValue.getValue());
                        if (StringUtils.isNotEmpty(ificValue.getPrice())) {
                            int price = (int) Double.parseDouble(ificValue.getPrice());
                            if (price > 0) {
                                modelexcessid.setPriceinfo(PriceUtil.getSpecPrice(price) + "元");
                            }
                        }
                    }
                    item.addModelexcessids(modelexcessid);
                }

                configitems.add(item.build());
            }

            //组装
            if (ListUtil.isNotEmpty(configitems)) {
                Configitem.Builder configitem = Configitem.newBuilder();
                configitem.setGroupname("个性化");
                configitem.setItemtype("特色配置");
                configitem.setShowtips(true);
                configitem.addAllItems(configitems);
                return configitem.build();
            }
            return null;
        }).exceptionally(e -> {
            log.warn("getSpecIficConfig error",e);
            return null;
        });

    }

    private Item.Modelexcessid.Colorinfo getConfigColorInfo(int type, SpecOutInnerColorDto specColorDto) {
        try {
            Item.Modelexcessid.Colorinfo.Builder colorInfo = Item.Modelexcessid.Colorinfo.newBuilder();
            colorInfo.setType(type);
            colorInfo.setTitle("共" + specColorDto.getColoritems().size() + "色");
            specColorDto.getColoritems().forEach(item -> {
                Item.Modelexcessid.Colorinfo.List.Builder listItemBuilder = Item.Modelexcessid.Colorinfo.List.newBuilder();
                listItemBuilder.setName(item.getName() != null ? item.getName() : "");
                listItemBuilder.setValue(item.getValue() != null ? item.getValue() : "");
                listItemBuilder.setIsaddprice(item.getPrice() > 0);
                listItemBuilder.setAddpricetext(item.getPrice() > 0 ? "+¥" + item.getPrice() : item.getPrice() < 0 ? "-¥" + item.getPrice() : "价格已包含");
                listItemBuilder.setRemark(item.getRemarks() != null ? item.getRemarks() : "");
                if (type == 1 && StringUtils.isNotEmpty(item.getPicurl())) {
                    listItemBuilder.setPicurl(item.getPicurl());
                }
                colorInfo.addList(listItemBuilder.build());
            });
            return colorInfo.build();
        } catch (Exception e) {
            log.error("车型参配接口异常-getConfigColorInfo error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    static void initMustSeeLevel() {
        mustSeeLevel.add(new MustSeeItemDto("基本参数", "长*宽*高(mm)"));//长*宽*高(mm)-参数项
        mustSeeLevel.add(new MustSeeItemDto("车身", "轴距(mm)"));//轴距(mm)-参数项
        mustSeeLevel.add(new MustSeeItemDto("基本参数", "能源类型"));//能源类型-参数项，注意：产品强制将“能源类型”归类到“基本参数”下
        mustSeeLevel.add(new MustSeeItemDto("基本参数", "官方0-100km/h加速(s)"));//官方0-100km/h加速(s)-参数项
        mustSeeLevel.add(new MustSeeItemDto("车身", "车身结构"));//车身结构-参数项
        mustSeeLevel.add(new MustSeeItemDto("基本参数", "整车质保"));//整车质保-参数项
        mustSeeLevel.add(new MustSeeItemDto("被动安全", "主/副驾驶座安全气囊"));//主/副驾驶座安全气囊-配置项
        mustSeeLevel.add(new MustSeeItemDto("被动安全", "前/后排侧气囊"));//前/后排侧气囊-配置项
        mustSeeLevel.add(new MustSeeItemDto("被动安全", "前/后排头部气囊(气帘)"));//前/后排头部气囊(气帘)-配置项
        mustSeeLevel.add(new MustSeeItemDto("被动安全", "膝部气囊"));//膝部气囊-配置项
        mustSeeLevel.add(new MustSeeItemDto("被动安全", "车身稳定控制(ESC/ESP/DSC等)"));//车身稳定控制(ESC/ESP/DSC等)-配置项
        mustSeeLevel.add(new MustSeeItemDto("主动安全", "车道偏离预警系统"));//车道偏离预警系统-配置项
        mustSeeLevel.add(new MustSeeItemDto("主动安全", "主动刹车/主动安全系统"));//主动刹车/主动安全系统-配置项
        mustSeeLevel.add(new MustSeeItemDto("主动安全", "疲劳驾驶提示"));//疲劳驾驶提示-配置项
        mustSeeLevel.add(new MustSeeItemDto("主动安全", "前方碰撞预警"));//前方碰撞预警-配置项
        mustSeeLevel.add(new MustSeeItemDto("主动安全", "后方碰撞预警"));//后方碰撞预警-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶操控", "驾驶模式切换"));//驾驶模式切换-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶操控", "自动驻车"));//自动驻车-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶操控", "空气悬架"));//空气悬架-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶硬件", "前/后驻车雷达"));//前/后驻车雷达-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶硬件", "驾驶辅助影像"));//驾驶辅助影像-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶功能", "巡航系统"));//巡航系统-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶功能", "辅助驾驶系统"));//辅助驾驶系统-配置项
        mustSeeLevel.add(new MustSeeItemDto("驾驶功能", "辅助驾驶等级"));//辅助驾驶等级-配置项
        mustSeeLevel.add(new MustSeeItemDto("外观/防盗", "电动后备厢"));//电动后备厢-配置项
        mustSeeLevel.add(new MustSeeItemDto("外观/防盗", "钥匙类型"));//钥匙类型-配置项
        mustSeeLevel.add(new MustSeeItemDto("外观/防盗", "无钥匙进入功能"));//无钥匙进入功能-配置项
        mustSeeLevel.add(new MustSeeItemDto("车外灯光", "近光灯光源"));//近光灯光源-配置项
        mustSeeLevel.add(new MustSeeItemDto("车外灯光", "远光灯光源"));//远光灯光源-配置项
        mustSeeLevel.add(new MustSeeItemDto("车外灯光", "自动头灯"));//自动头灯-配置项
        mustSeeLevel.add(new MustSeeItemDto("天窗/玻璃", "天窗类型"));//天窗类型-配置项
        mustSeeLevel.add(new MustSeeItemDto("天窗/玻璃", "车内化妆镜"));//车内化妆镜-配置项
        mustSeeLevel.add(new MustSeeItemDto("外后视镜", "外后视镜功能"));//外后视镜功能-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "中控屏幕尺寸"));//中控屏幕尺寸-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "副驾娱乐屏"));//副驾娱乐屏-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "手机互联/映射"));//手机互联/映射-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "语音识别控制系统"));//语音识别控制系统-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "车载智能系统"));//车载智能系统-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "车机智能芯片"));//车机智能芯片-配置项
        mustSeeLevel.add(new MustSeeItemDto("屏幕/系统", "后排液晶屏幕尺寸"));//后排液晶屏幕尺寸-配置项
        mustSeeLevel.add(new MustSeeItemDto("智能化配置", "4G/5G网络"));//4G/5G网络-配置项
        mustSeeLevel.add(new MustSeeItemDto("智能化配置", "OTA升级"));//OTA升级-配置项
        mustSeeLevel.add(new MustSeeItemDto("智能化配置", "手机APP远程功能"));//手机APP远程功能-配置项
        mustSeeLevel.add(new MustSeeItemDto("智能化配置", "主动降噪"));//主动降噪-配置项
        mustSeeLevel.add(new MustSeeItemDto("方向盘/内后视镜", "方向盘材质"));//方向盘材质-配置项
        mustSeeLevel.add(new MustSeeItemDto("方向盘/内后视镜", "方向盘位置调节"));//方向盘位置调节-配置项
        mustSeeLevel.add(new MustSeeItemDto("方向盘/内后视镜", "液晶仪表尺寸"));//液晶仪表尺寸-配置项
        mustSeeLevel.add(new MustSeeItemDto("方向盘/内后视镜", "HUD抬头数字显示"));//HUD抬头数字显示-配置项
        mustSeeLevel.add(new MustSeeItemDto("方向盘/内后视镜", "内后视镜功能"));//内后视镜功能-配置项
        mustSeeLevel.add(new MustSeeItemDto("车内充电", "USB/Type-C接口数量"));//USB/Type-C接口数量-配置项
        mustSeeLevel.add(new MustSeeItemDto("车内充电", "手机无线充电功能"));//手机无线充电功能-配置项
        mustSeeLevel.add(new MustSeeItemDto("座椅配置", "座椅材质"));//座椅材质-配置项
        mustSeeLevel.add(new MustSeeItemDto("座椅配置", "主座椅调节方式"));//主座椅调节方式-配置项
        mustSeeLevel.add(new MustSeeItemDto("座椅配置", "副座椅调节方式"));//副座椅调节方式-配置项
        mustSeeLevel.add(new MustSeeItemDto("座椅配置", "主/副驾驶座电动调节"));//主/副驾驶座电动调节-配置项
        mustSeeLevel.add(new MustSeeItemDto("座椅配置", "前排座椅功能"));//前排座椅功能-配置项
        mustSeeLevel.add(new MustSeeItemDto("座椅配置", "后排座椅放倒形式"));//后排座椅放倒形式-配置项
        mustSeeLevel.add(new MustSeeItemDto("音响/车内灯光", "扬声器品牌名称"));//扬声器品牌名称-配置项
        mustSeeLevel.add(new MustSeeItemDto("音响/车内灯光", "扬声器数量"));//扬声器数量-配置项
        mustSeeLevel.add(new MustSeeItemDto("音响/车内灯光", "车内环境氛围灯"));//车内环境氛围灯-配置项
        mustSeeLevel.add(new MustSeeItemDto("空调/冰箱", "空调温度控制方式"));//空调温度控制方式-配置项
        mustSeeLevel.add(new MustSeeItemDto("空调/冰箱", "后排独立空调"));//后排独立空调-配置项
        mustSeeLevel.add(new MustSeeItemDto("空调/冰箱", "车载空气净化器"));//车载空气净化器-配置项
        mustSeeLevel.add(new MustSeeItemDto("空调/冰箱", "车载冰箱"));//车载冰箱-配置项
    }

    static void initMustSeeLevelOther() {
        mustSeeLevel_7.add(new MustSeeItemDto("座椅配置", "头颈暖风系统"));//头颈暖风系统-配置项

        mustSeeLevel_16_20.add(new MustSeeItemDto("驾驶操控", "陡坡缓降"));//陡坡缓降-配置项

        mustSeeLevel_14_20.add(new MustSeeItemDto("四驱/越野", "中央差速器锁止功能"));//中央差速器锁止功能-配置项
        mustSeeLevel_14_20.add(new MustSeeItemDto("四驱/越野", "限滑差速器/差速锁"));//限滑差速器/差速锁-配置项
        mustSeeLevel_14_20.add(new MustSeeItemDto("四驱/越野", "涉水感应系统"));//涉水感应系统-配置项
        mustSeeLevel_14_20.add(new MustSeeItemDto("四驱/越野", "低速四驱"));//低速四驱-配置项


        mustSeeLevel_21_24.add(new MustSeeItemDto("外观/防盗", "侧滑门形式"));//侧滑门形式-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第二排座椅调节"));//第二排座椅调节-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第二排座椅电动调节"));//第二排座椅电动调节-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第二排座椅功能"));//第二排座椅功能-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "福祉座椅"));//福祉座椅-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第二排独立座椅"));//第二排独立座椅-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第三排座椅调节"));//第三排座椅调节-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第三排座椅电动调节"));//第三排座椅电动调节-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "第三排座椅功能"));//第三排座椅功能-配置项
        mustSeeLevel_21_24.add(new MustSeeItemDto("座椅配置", "座椅布局"));//座椅布局-配置项
    }

    static void initMustSeeEnergytype() {
        mustSeeEnergytype1.add(new MustSeeItemDto("电动机", "CLTC纯电续航里程(km)"));//CLTC纯电续航里程(km)-参数项
        mustSeeEnergytype1.add(new MustSeeItemDto("电动机", "WLTC纯电续航里程(km)"));//WLTC纯电续航里程(km)-参数项
        mustSeeEnergytype1.add(new MustSeeItemDto("电动机", "电池快充时间(小时)"));//电池快充时间(小时)-参数项
        mustSeeEnergytype1.add(new MustSeeItemDto("基本参数", "电动机(Ps)"));//电动机(Ps)-参数项
        mustSeeEnergytype1.add(new MustSeeItemDto("驾驶操控", "单踏板模式"));//单踏板模式-配置项
        mustSeeEnergytype1.add(new MustSeeItemDto("驾驶操控", "能量回收系统"));//能量回收系统-配置项
        mustSeeEnergytype1.add(new MustSeeItemDto("驾驶硬件", "辅助驾驶芯片"));//辅助驾驶芯片-配置项
        mustSeeEnergytype1.add(new MustSeeItemDto("驾驶硬件", "芯片总算力"));//芯片总算力-配置项
        mustSeeEnergytype1.add(new MustSeeItemDto("外观/防盗", "电池预加热"));//电池预加热-配置项
        mustSeeEnergytype1.add(new MustSeeItemDto("外观/防盗", "对外放电"));//对外放电-配置项
        mustSeeEnergytype1.add(new MustSeeItemDto("空调/冰箱", "热泵空调"));//热泵空调-配置项


        mustSeeEnergytype.add(new MustSeeItemDto("基本参数", "发动机"));//发动机-参数项
        mustSeeEnergytype.add(new MustSeeItemDto("变速箱", "简称"));//简称-参数项
        mustSeeEnergytype.add(new MustSeeItemDto("驾驶操控", "发动机启停技术"));//发动机启停技术-配置项
    }

    static void initEidmap() {
        eidMap.put("1_1", "3|1411002|572|3285|203989|302210");
        eidMap.put("1_2", "3|1412002|572|3285|203987|302209");
        eidMap.put("1_3", "3|1474001|48|35|204434|306043");
        eidMap.put("2_1", "3|1411002|572|3286|200072|300000");
        eidMap.put("2_2", "3|1412002|572|3286|200072|300000");
        eidMap.put("2_3", "3|1474001|108|219|204436|306043");
        eidMap.put("3_1", "3|1411112|1197|11411|203703|301601");
        eidMap.put("3_2", "3|1412112|1197|11411|203703|301602");
        eidMap.put("3_3", "3|1474002|20|0|206412|306043");
    }

    ;

    private String subPreGbk(String str, int len, String suffix) {
        try {
            if (com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(str)) {
                int counterOfDoubleByte = 0;
                byte[] b = str.toString().getBytes(Charset.forName("GBK"));
                if (b.length <= len) {
                    return str.toString();
                } else {
                    for (int i = 0; i < len; ++i) {
                        if (b[i] < 0) {
                            ++counterOfDoubleByte;
                        }
                    }

                    if (counterOfDoubleByte % 2 != 0) {
                        ++len;
                    }
                    return new String(b, 0, len, Charset.forName("GBK")) + suffix;
                }
            }
        } catch (Exception e) {
            log.error("车型参配接口异常-subPreGbk error: {}", ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    /**
     * 构建关注度最高车型信息
     *
     * @return
     */
    private CompletableFuture<Attentionspecinfo> buildAttentionSpecInfo(CompletableFuture<SeriesDetailDto> seriesDetailFuture) {

        return seriesDetailFuture.thenCompose(seriesDetailDto -> {
            if (seriesDetailDto == null || seriesDetailDto.getHotSpecId() == 0) {
                return CompletableFuture.completedFuture(null);
            }
            return specDetailComponent.get(seriesDetailDto.getHotSpecId()).thenApply(specInfo -> {
                Attentionspecinfo.Builder attentionSpecInfobuilder = Attentionspecinfo.newBuilder();
                if (Objects.nonNull(specInfo) && specInfo.getParamIsShow() == 1) {
                    attentionSpecInfobuilder.setParamisshow(specInfo.getParamIsShow());
                    attentionSpecInfobuilder.setParamisshow(specInfo.getParamIsShow());
                    String priceInfo = PriceUtil.getSpecPrice(specInfo.getMinPrice());
                    if (specInfo.isBooked() && specInfo.getState() == 10) {
                        priceInfo = getDynamicPrice(specInfo);
                    }
                    attentionSpecInfobuilder.setPriceinfo(priceInfo);
                    attentionSpecInfobuilder.setSeriesid(specInfo.getSeriesId());
                    attentionSpecInfobuilder.setSeriesname(specInfo.getSeriesName());
                    attentionSpecInfobuilder.setSpecid(specInfo.getSpecId());
                    attentionSpecInfobuilder.setSpecname(specInfo.getSpecName());
                }
                return attentionSpecInfobuilder.build();
            });
        });
    }

    private String getDynamicPrice(SpecDetailDto specDetail) {
        String dynamicprice = "";
        if (specDetail != null) {
            dynamicprice = specDetail.getState() == 10
                    ? (specDetail.isBooked() ? "订金:" + PriceUtil.getStrPrice(specDetail.getMinPrice(), specDetail.getMaxPrice()) : "预售价:" + PriceUtil.getStrPrice(specDetail.getMinPrice(), specDetail.getMaxPrice()))
                    : "指导价:" + PriceUtil.getStrPrice(specDetail.getMinPrice(), specDetail.getMaxPrice());
        }
        return dynamicprice;
    }


    /**
     * 构建车系参配浮窗工具箱
     *
     * @return
     */
    private CompletableFuture<Toolboxentry> buildToolboxEntry(int seriesId, int pm, CompletableFuture<SeriesDetailDto> seriesDetailFuture, List<Integer> allSpecIds) {  //, List<SpecDetailDto> specDetailList
        return seriesConfigDiffComponent.get(seriesId).thenCombine(seriesDetailFuture, (diff, seriesDetail) -> {
            Toolboxentry.Builder toolboxbuilder = Toolboxentry.newBuilder();
            toolboxbuilder.setEntrypvdata(Pvitem.newBuilder()
                    .putArgvs("seriesid", seriesId + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_entry_click").build()));
            //图片差异
            if (diff != null && diff.getPicSpecIds() != null) {
                Optional<Integer> first = allSpecIds.stream().filter(spec -> diff.getPicSpecIds().contains(spec)).findFirst();
                if (first.isPresent()) {
                    Toolboxentry.List.Builder picItem = Toolboxentry.List.newBuilder();
                    picItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cfg_img_0323.png");
                    picItem.setTitle("图片差异");
                    picItem.setTypeid(1);
                    String link = String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode(String.format("rn://Car_SeriesSummary/PictureContrast?seriesid=%s&panValid=0&specid=%s&typeid=1&fromtype=2&isfirst=1&locationid=0", seriesId, first.get())));
                    picItem.setLinkurl(link);
                    picItem.setPvdata(Pvitem.newBuilder()
                            .putArgvs("seriesid", seriesId + "")
                            .putArgvs("typeid", "1")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_list_click").build()));
                    toolboxbuilder.addList(picItem);
                }
            }
            //配置差异
            if (diff != null && diff.getConfigDiff() != null) {
                Toolboxentry.List.Builder cfgItem = Toolboxentry.List.newBuilder();
                cfgItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cfg_cydb_0323.png");
                cfgItem.setTitle(String.format("配置差异"));
                cfgItem.setTypeid(2);
                List<Integer> specIds = diff.getConfigDiff().getSpecIds();
                String link = String.format("autohome://car/summaryconfigdif?seriesid=%s&specids=%s", seriesId, UrlUtil.encode(String.format("%s,%s", specIds.get(0), specIds.get(1))));
                cfgItem.setLinkurl(link);
                cfgItem.setPvdata(Pvitem.newBuilder()
                        .putArgvs("seriesid", seriesId + "")
                        .putArgvs("typeid", "2")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_list_click").build()));
                toolboxbuilder.addList(cfgItem);
            }
            //参配纠错
            Toolboxentry.List.Builder jcItem = Toolboxentry.List.newBuilder();
            jcItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cfg_wtfk_0323.png");
            jcItem.setTitle("参配纠错");
            jcItem.setTypeid(4);
            String link = "";
            if (pm == 1) {
                link = "autohome://carcompare/specconfigissuereport?seriesid=" + seriesId;
            } else {
                link = "autohome://car/specconfigissuereport?seriesid=" + seriesId;
            }
            if (allSpecIds != null && allSpecIds.size() > 0) {
                link = link + "&seriesname=" + UrlUtil.encode(seriesDetail.getName()).replace("+", "%20");
            }
            jcItem.setLinkurl(link);
            jcItem.setPvdata(Pvitem.newBuilder()
                    .putArgvs("seriesid", seriesId + "")
                    .putArgvs("typeid", "4")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_list_click").build()));

            toolboxbuilder.addList(jcItem);
            return toolboxbuilder.build();
        });
    }


    /**
     * 构建必参配项列表
     *
     * @return
     */
    private CompletableFuture<List<Mustseelist>> buildMustSeeList(CompletableFuture<SeriesDetailDto> seriesDetail) {
        return seriesDetail.thenApply(seriesInfo -> {
            List<Mustseelist> mustSeeList = new ArrayList<>();
            if (seriesInfo != null) {
                List<MustSeeItemDto> mustSeeItemDtos = new ArrayList<>();
                //级别判断
                if (Arrays.asList(1, 2, 3, 4, 5, 6, 7, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24).contains(seriesInfo.getLevelId())) {
                    mustSeeItemDtos.addAll(mustSeeLevel);

                    //能源类型判断
                    if (seriesInfo.getEnergytype() == 1) {
                        mustSeeItemDtos.addAll(mustSeeEnergytype1);
                    } else {
                        mustSeeItemDtos.addAll(mustSeeEnergytype);
                    }
                }
                //级别判断
                if (Arrays.asList(16, 17, 18, 19, 20).contains(seriesInfo.getLevelId())) {
                    //SUV
                    mustSeeItemDtos.addAll(mustSeeLevel_16_20);
                }
                if (Arrays.asList(16, 17, 18, 19, 20).contains(seriesInfo.getLevelId()) || Arrays.asList(14, 15).contains(seriesInfo.getLevelId())) {
                    //SUV/皮卡
                    mustSeeItemDtos.addAll(mustSeeLevel_14_20);
                }
                if (Arrays.asList(21, 22, 23, 24).contains(seriesInfo.getLevelId())) {
                    //MPV
                    mustSeeItemDtos.addAll(mustSeeLevel_21_24);
                }
                if (Arrays.asList(7).contains(seriesInfo.getLevelId())) {
                    //跑车
                    mustSeeItemDtos.addAll(mustSeeLevel_7);
                }

                for (MustSeeItemDto item : mustSeeItemDtos) {
                    Mustseelist.Builder builder = Mustseelist.newBuilder();
                    builder.setItemtype(item.getItemtype());
                    builder.setParamitemname(item.getParamitemname());
                    mustSeeList.add(builder.build());
                }
            }
            return mustSeeList;
        });
    }


    /**
     * CPS信息
     *
     * @return
     */
    private CompletableFuture<Cpsinfo> buildCpsinfo(int specId, int cityId) {
        return dealerApiClient.getSpecCityCpsInfo(cityId, String.valueOf(specId)).thenApply(result -> {
            Cpsinfo.Builder cpsinfoBuilder = Cpsinfo.newBuilder();
            if (result != null && result.getResult() != null && result.getResult().size() > 0) {
                SpecCityCpsInfoResult source = result.getResult().get(0);
                String flbnb = "";
                if (source.getProductType() == 2) {
                    flbnb = "6837595";
                    cpsinfoBuilder.setTypeid(6837595);
                    cpsinfoBuilder.setTitle(source.getFacBtnMainTitle());
                }
                String linkurl = UrlUtil.getInsideBrowerSchemeWK(source.getOriginalJumpUrl() + "&flbnb=" + flbnb);
                cpsinfoBuilder.setLinkurl(linkurl);
                return cpsinfoBuilder.build();
            }
            return cpsinfoBuilder.build();
        });
    }


    /**
     * SpecFootaskpriceinfo
     *
     * @return
     */
    private CompletableFuture<Footaskpriceinfo> buildSpecFootaskpriceinfo(
            NewSpecCompareRequest request,
            ConcurrentMap<Integer, SpecDetailDto> specDetailDtos,
            CompletableFuture<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceMapFuture,
            CompletableFuture<Map<Integer, SListAreaButtonResult>> smartAreaButtonMapFuture,
            CompletableFuture<List<SpecCityUsedCarDto>> specCityUsedCarDtoFuture){
        return CompletableFuture.allOf(specCityAskPriceMapFuture, smartAreaButtonMapFuture, specCityUsedCarDtoFuture).thenApply(xt -> {
            Map<Integer, SpecCityAskPriceDto> specCityAskPriceMap = specCityAskPriceMapFuture.join();
            Map<Integer, SListAreaButtonResult> smartAreaButtonMap = smartAreaButtonMapFuture.join();
            Map<Integer, SpecCityUsedCarDto> specCityUsedCarDtoMap = new HashMap<>();
            List<SpecCityUsedCarDto> specCityUsedCarDtos = specCityUsedCarDtoFuture.join();
            if(ListUtil.isNotEmpty(specCityUsedCarDtos)){
                specCityUsedCarDtos.removeIf(Objects::isNull);
                specCityUsedCarDtoMap = specCityUsedCarDtos.stream().collect(Collectors.toMap(SpecCityUsedCarDto::getSpecid, user -> user));
            }
            Map<Integer, SpecCityUsedCarDto> finalSpecCityUsedCarDtoMap = specCityUsedCarDtoMap;
            Footaskpriceinfo.Builder footaskpriceinfo = Footaskpriceinfo.newBuilder();
            if (request.getSite()==2 && !specDetailDtos.isEmpty()) {
                specDetailDtos.entrySet().forEach(entry -> {
                    SpecDetailDto specDetail = entry.getValue();
                    if(specDetail != null){
                        SpecCityAskPriceDto askPriceDto = specCityAskPriceMap.get(specDetail.getSpecId());
                        SpecCityUsedCarDto cityUsedCarDto = finalSpecCityUsedCarDtoMap.get(specDetail.getSpecId());
                        boolean canaskprice = askPriceDto != null && askPriceDto.getMinPrice() > 0;
                        if (canaskprice) {
                            Integer key = specDetail.getSpecId();
                            if (smartAreaButtonMap != null && smartAreaButtonMap.containsKey(key)) {
                                SListAreaButtonResult sListAreaButtonResult = smartAreaButtonMap.get(key);
                                Map<String, String> dataMap = getAskpriceSchema(request, specDetail, sListAreaButtonResult.getButtonList());
                                if (dataMap != null) {
                                    footaskpriceinfo.setCanaskprice(1);
                                    footaskpriceinfo.setAskpricescheme(dataMap.get("scheme"));
                                    footaskpriceinfo.setAskpricetitle(dataMap.get("mainText"));
                                    footaskpriceinfo.setAskpricesubtitle(dataMap.get("subtitle"));
                                }
                                int pm = request.getPm();
                                List<SListAreaButtonResult.ButtonListDTO> buttonList = sListAreaButtonResult.getButtonList();
                                if (pm == 3) {
                                    //鸿蒙不处理im
                                } else {
                                    // 非鸿蒙的处理逻辑
                                    if (!CollectionUtils.isEmpty(buttonList)) {
                                        Optional<SListAreaButtonResult.ButtonListDTO> btnType3 = buttonList.stream()
                                                .filter(x -> x.getSpecId() == specDetail.getSpecId() && x.getBtnType() == 3)
                                                .findFirst();
                                        if (btnType3.isPresent()) {
                                            // btnType == 3 im按钮处理
                                            String eid = com.autohome.app.cars.common.utils.StringUtils.format("&eid={0}", UrlUtil.encode(request.getPm() == 1 ? "3|1411002|572|3285|205313|304229" : "3|1412002|572|3285|205313|304228"));
                                            footaskpriceinfo.setEntertype(0);
                                            footaskpriceinfo.setImtitle(btnType3.get().getMainText());
                                            footaskpriceinfo.setImsubtitle(btnType3.get().getSubText());
                                            footaskpriceinfo.setImlinkurl(btnType3.get().getImSchema() + eid);
                                            footaskpriceinfo.setImiconurl("");
                                        }
                                    }
                                }
                            }
                        } else {
                            //查二手车价
                            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.8")
                                    && StringUtils.equalsIgnoreCase(request.getUsedcarabtest(), "B")
                                    && specDetail.getState() == 40
                                    && cityUsedCarDto != null
                                    && cityUsedCarDto.getMinprice() > 0) {
                                String eid = "3|1411002|572|3285|211921|306976";
                                String scheme = "autohome://car/askusedcarprice?customshowanimationtype=2&animation_type=2&flutterPresentType=2&seriesid="
                                        + specDetail.getSeriesId() + "&askspecid=" + specDetail.getSpecId() + "&eid=" + UrlUtil.encode(eid) + "&successclose=1&title=" + UrlUtil.encode("获取二手车报价");
                                footaskpriceinfo.setCanaskprice(4);
                                footaskpriceinfo.setAskpricetitle("查二手车价");
                                footaskpriceinfo.setAskpricescheme(scheme);

                                String imscheme = "autohome://usedcar/buycarlist?pvareaid=112894&brand=";
                                imscheme += UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}", specDetail.getBrandId(), specDetail.getBrandName(), specDetail.getSeriesId(), specDetail.getSeriesName()));
                                footaskpriceinfo.setEntertype(1);
                                footaskpriceinfo.setImtitle("低价车源");
                                footaskpriceinfo.setImsubtitle("");
                                footaskpriceinfo.setImlinkurl(imscheme);
                            }
                        }
                    }
                });
            }
            return footaskpriceinfo.build();
        }).exceptionally(e -> logCompletableFutureError(e, "bindFootaskpriceinfo error", Footaskpriceinfo.newBuilder().build()));

    }

    private Map<String, String> getAskpriceSchema(NewSpecCompareRequest  request, SpecDetailDto specDetail,List<SListAreaButtonResult.ButtonListDTO> buttonList){
        Map<String, String> dataMap = new HashMap<>();
        int site = request.getSite();
        int pm = request.getPm();
        int seriesId = specDetail.getSeriesId();
        HashMap<String,String> eidMap = new HashMap<>(); //key规则： site_pm 页面来源_客户端类型
        eidMap.put("1_1","3|1411002|572|3285|203989|302210" );
        eidMap.put("1_2","3|1412002|572|3285|203987|302209" );
        eidMap.put("1_3","3|1474001|48|35|204434|306043" );
        eidMap.put("2_1","3|1411002|572|3286|200072|300000" );
        eidMap.put("2_2","3|1412002|572|3286|200072|300000" );
        eidMap.put("2_3","3|1474001|108|219|204436|306043" );
        eidMap.put("3_1","3|1411112|1197|11411|203703|301601" );
        eidMap.put("3_2","3|1412112|1197|11411|203703|301602" );
        eidMap.put("3_3","3|1474002|20|0|206412|306043" );
        String eid = eidMap.get(site+"_"+pm);
        if (pm == 3) {
            if (buttonList != null && buttonList.size() > 0) {
                Optional<SListAreaButtonResult.ButtonListDTO> first = buttonList.stream()
                        .filter(x -> x.getSpecId() == specDetail.getSpecId() && x.getBtnType() == 2)
                        .findFirst();
                if (first.isPresent()) {
                    dataMap.put("scheme", first.get().getUrl());
                    dataMap.put("mainText", first.get().getMainText());
                    dataMap.put("subtitle", "");
                    dataMap.put("ext", first.get().getExt());
                }
            }else{
                return null;
            }
        } else {
            // 非鸿蒙的处理逻辑
            if (!CollectionUtils.isEmpty(buttonList)) {
                Optional<SListAreaButtonResult.ButtonListDTO> btnType2 = buttonList.stream()
                        .filter(x -> x.getSpecId() == specDetail.getSpecId() && x.getBtnType() == 2)
                        .findFirst();
                if (btnType2.isPresent()) {
                    // btnType == 2 询价按钮处理
                    SListAreaButtonResult.ButtonListDTO button = btnType2.get();
                    // 询价按钮
                    String scheme="";
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(button.getUrl())) {
                        scheme = button.getUrl();
                    } else {
                        String askPriceSchemaTemp = "autohome://car/asklowprice?customshowanimationtype=2&eid=%s&seriesid=%s&specid=%s&inquirytype=2&price_show=%s&title=%s&ext=%s";
                        if (button.getWindowType() == 14) {
                            scheme = String.format("autohome://dealerconsult/dealerprice?seriesid=%s&specid=%s&eid=%s",
                                    seriesId, specDetail.getSpecId(), eid);
                        } else {
                            scheme = String.format(askPriceSchemaTemp, eid, seriesId, specDetail.getSpecId(),
                                    DealerCommHelp.getPriceShowFromWindowType(button.getWindowType()),
                                    org.apache.commons.lang3.StringUtils.isNotBlank(button.getMainText())
                                            ? UrlUtil.encode(button.getMainText()) : "",
                                    UrlUtil.encode(button.getExt()));
                        }

                    }
                    dataMap.put("scheme", scheme);
                    dataMap.put("mainText", button.getMainText());
                    dataMap.put("subtitle", "");
                    dataMap.put("ext", button.getExt());
                }


            } else {
                // 没有就走打底逻辑
                String rnUrl = String.format("rn://DealerPriceRn/ReverseAuctionDialog?seriesid=%s&specid=%s&siteid=21&gps=1&eid=%s",
                        seriesId, specDetail.getSpecId(), UrlUtil.encode(eid));
                dataMap.put("scheme", String.format("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s",
                        UrlUtil.encode(rnUrl)));
                dataMap.put("mainText", "查报价单");
                dataMap.put("subtitle", "");
                dataMap.put("ext", "{\"price_show\":36}");
            }
        }

        return dataMap;
    }

    <T> T logCompletableFutureError(Throwable e, String title, T returnValue) {
        log.error(title, e);
        return returnValue;
    }

    public CompletableFuture<GetConfigShareResponse> getConfigShare(GetConfigShareRequest request) {
        int siteId = request.getSite();
        int seriesId = request.getSeriesid();
        int year = request.getYear();
        int cityId = request.getCityid();
        String specIds = request.getSpecids();

        GetConfigShareResponse.Builder response = GetConfigShareResponse.newBuilder();
        GetConfigShareResponse.Result.Builder result = GetConfigShareResponse.Result.newBuilder();

        result.setUrl("https://m.autohome.com.cn/config/series/"+seriesId+".html");

        ConcurrentMap<Integer, SpecDetailDto> specDetails = new ConcurrentHashMap<>();
        Vector<String> deleteSpecIds = new Vector<>();
        Vector<String> deleteSpecName = new Vector<>();
        List<SpecGroupOfSeriesDto> seriesYears = new ArrayList<>();

        if (siteId == 2) {  //车型
            if (!NumberUtils.isCreatable(specIds)) {
                return CompletableFuture.completedFuture(
                        response.setReturnMsg("参数错误").setReturnCode(-1).build()
                );
            }
            int specId = Integer.parseInt(specIds);
            SpecDetailDto specDetail = specDetailComponent.getSync(specId);
            if (specDetail == null) {
                return CompletableFuture.completedFuture(
                        response.setReturnMsg("车型不存在").build()
                );
            }
            specDetails.put(specDetail.getSpecId(), specDetail);
        }

        // 获取车型id
        CompletableFuture<List<Integer>> specIdFuture = getSpecIds2(
                request.getPluginversion(), specIds, siteId, year, seriesId, specDetails, deleteSpecIds, deleteSpecName, seriesYears
        ).exceptionally(e -> logCompletableFutureError(e, "获取车型id错误", new ArrayList<>()));

        // 车系详情
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesid());
        result.setWeixintitle("点击查看"+seriesDetailDto.getName()+"参数配置，不同车型参配差异一目了然");
        result.setBgimgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_share_1168002.webp");

        CompletableFuture<Void> main = specIdFuture.thenCompose(specIdList -> {
            if (specIdList == null || specIdList.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }

            List<Integer> otherSpecs = specIdList.stream()
                    .filter(x -> !specDetails.containsKey(x))
                    .collect(Collectors.toList());

            CompletableFuture<Void> othersSpecsFuture = specDetailComponent.getList(otherSpecs).thenAccept(oss -> {
                if (Objects.nonNull(oss) && !oss.isEmpty()) {
                    oss.forEach(os -> specDetails.put(os.getSpecId(), os));
                }
            });

            // 是否可询价
            CompletableFuture<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceMap = specCityAskPriceComponent
                    .get(specIdList, cityId)
                    .thenApply(result1 -> {
                        result1.removeIf(Objects::isNull);
                        return result1.stream()
                                .collect(Collectors.toMap(x -> x.getSpecId(), x -> x, (v1, v2) -> v2));
                    });

            return othersSpecsFuture.thenCompose(x -> {
                return CompletableFuture.allOf(
                        buildSpecItems2(request, specIdList, specDetails, specCityAskPriceMap)
                                .thenAccept(specinfo -> {
                                    String wxurl = UrlUtil.encode("car-package/pages/series-config/index");
                                    String sence =  "auto_open_from=autohome_qrcode_config&seriesid=" + seriesId;
                                    String path = "/car-package/pages/series-config/index?auto_open_from=autohome_config&seriesid=" + seriesId;
                                    String specid = "";
                                    if (specinfo != null && !CollectionUtils.isEmpty(specinfo.getSpecitemsList())) {
                                        specid = "&specid=" + specinfo.getSpecitemsList().stream()
                                                .map(s -> s.getSpecid() + "")
                                                .collect(Collectors.joining(","));
                                    }
                                    sence =UrlUtil.encode(sence+specid);
                                    BaseModel qrcode = configItemApiClient.getQrcode(wxurl, sence).join();
                                    if (qrcode != null && qrcode.getReturncode()==0){
                                        result.setQrcode((String) qrcode.getResult());
                                    }
                                    result.setPath(path + specid);
                                    result.addAllSpecList(specinfo.getSpecitemsList());
                                }),
                        getSeriesBaseInfoBuilder(seriesDetailDto, request, specIdList)
                                .thenAccept(seriesInfo -> {
                                    result.setSeriesinfo(seriesInfo);
                                })
                );
            });
        });

        // 返回完整的 response
        return main.thenApply(v -> {
            response.setResult(result);
            response.setReturnCode(0); // 成功
            response.setReturnMsg("成功");
            return response.build();
        }).exceptionally(e -> {
            log.error("getConfigShare error", e);
            return response.setReturnCode(-1).setReturnMsg("系统错误").build();
        });
    }

    CompletableFuture<GetConfigShareResponse.SpecList> buildSpecItems2(
            GetConfigShareRequest request, List<Integer> specIdList,
            Map<Integer, SpecDetailDto> specs,
            CompletableFuture<Map<Integer, SpecCityAskPriceDto>> specCityAskPriceMapFuture
    ) {
        StopWatch stopWatch =new StopWatch("buildSpecItems");
        stopWatch.start("CompletableFuture.allOf");
        return CompletableFuture.allOf(specCityAskPriceMapFuture).thenApply(x -> {
            stopWatch.stop();
            stopWatch.start("data");
            Map<Integer, SpecCityAskPriceDto> specCityAskPriceMap = specCityAskPriceMapFuture.join();
            List<GetConfigShareResponse.Specitem> result = new ArrayList<>();
            for (Integer specId : specIdList) {
                SpecDetailDto specInfo = specs.get(specId);
                if (specInfo == null) {
                    continue;
                }
                String dynamicprice = "";
                if (specInfo.getState() == 10) {
                    dynamicprice = PriceUtil.getStrPrice(specInfo.getMinPrice(), specInfo.getMaxPrice());
                } else {
                    dynamicprice = PriceUtil.getStrPrice(specInfo.getMinPrice(), specInfo.getMaxPrice());
                }

                GetConfigShareResponse.Specitem.Builder specitem = GetConfigShareResponse.Specitem.newBuilder();
                specitem.setSeriesid(specInfo.getSeriesId());
                specitem.setSeriesname(specInfo.getSeriesName());
                specitem.setSpecid(specInfo.getSpecId());
                specitem.setSpecname(specInfo.getSpecName());
                specitem.setGuidingprice(dynamicprice);

                SpecCityAskPriceDto askPrice = specCityAskPriceMap.get(specId);
                if (askPrice != null) {
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.66.0")){
                        int minPrice = askPrice.getMinPrice();//获取参考价
                        specitem.setReferenceprice(minPrice <= 0 ? "暂无报价" : CommonHelper.df02.format(minPrice / 10000.0) + "万起");
                        //specitem.setReferpricetitle("参考价：");
                    }
                }else{
                    specitem.setReferenceprice("暂无报价");
                }
                result.add(specitem.build());
                if (result.size() == 2) {
                    break;
                }
            }
            stopWatch.stop();
            printCost(stopWatch);
            return GetConfigShareResponse.SpecList.newBuilder().addAllSpecitems(result).build();
        }).exceptionally(e -> logCompletableFutureError(e, "bindspecItems error", GetConfigShareResponse.SpecList.newBuilder().build()));
    }

    public CompletableFuture<GetConfigShareResponse.SeriesInfo> getSeriesBaseInfoBuilder(
            SeriesDetailDto series,
            GetConfigShareRequest request,
            List<Integer> specIds
    ) {
        /*
         车系停售页改版  101915 https://doc.autohome.com.cn/docapi/page/share/share_z1K5BbhRVw
         保留 https://doc.autohome.com.cn/docapi/page/share/share_xonpSFZvhQ 需求中 tab以上的变更
         */
        try {
            if (series == null) {
                return CompletableFuture.completedFuture(null);
            }
            CompletableFuture<GetConfigShareResponse.SeriesInfo.Builder> getParamInfo = CompletableFuture.supplyAsync(() -> {
                GetConfigShareResponse.SeriesInfo.Builder seriesBuilder = GetConfigShareResponse.SeriesInfo.newBuilder();


                seriesBuilder.setSeriesid(series.getId());
                seriesBuilder.setSeriesname(series.getName());
                seriesBuilder.setBrandname(StringEscapeUtils.unescapeHtml4(series.getBrandName()));
                seriesBuilder.setLogo(ImageUtils.convertImageUrl(series.getBrandLogo(), true, false, false, ImageSizeEnum.ImgSize_1x1_100x100_NO_OPT));
                seriesBuilder.setIsnewenergy(series.getEnergytype());
                //seriesBuilder.setLevelid(series.getLevelId());

                String levelName = series.getLevelName();
                if (series.getLevelId() == 14 || series.getLevelId() == 15) {
                    levelName = "皮卡";
                }
                //seriesBuilder.setLevelname(levelName);
                seriesBuilder.setSeriestype(levelName);
                String guidingpricerange = PriceUtil.GetPriceStringDetail(series.getMinPrice(), series.getMaxPrice(), series.getState());
                seriesBuilder.setGuidingpricerange("即将销售".equals(guidingpricerange)? "暂无报价" : guidingpricerange);

                //车系图片处理，兼容老图
                String limg = ImageUtils.convertImage_ToHttp(series.getPngLogo());
                String logo = "";
                if (StringUtils.isNotEmpty(limg)) {
                    int index = limg.lastIndexOf("/") + 1;
                    logo = !"gp_default.gif".equals(limg.substring(index)) ? limg.substring(0, index) + "u_" + limg.substring(index) : limg;
                }
                String img = ImageUtils.convertImage_ToWebp(ImageUtils.convertImage_Size(logo, ImageSizeEnum.ImgSize_4x3_400x300));
                seriesBuilder.setBigpic(img);

                //车型参配信息
                if (specIds != null && specIds.size() > 0) {
                    CompletableFuture<Map<Integer, SpecParamConfigDto>> paramInfoListFuture = specParamInfoNewComponent.getMap(List.of(specIds.get(0))).exceptionally(e -> logCompletableFutureError(e, "get param info error", new HashMap<>()));
                    paramInfoListFuture.thenAccept(specParamConfigDtoMap -> {
                        SpecParamConfigDto specParamConfigDto = specParamConfigDtoMap.get(specIds.get(0));
                        if (specParamConfigDto != null) {
                            Map<String, SpecParamConfigDto.Item> itemMap = specParamConfigDto.getItemMap();
                            if (seriesBuilder.getIsnewenergy() == 0){
                                seriesBuilder.setBgcarimgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_icon_share_oil_bg.webp");
                                //厂商
                                SpecParamConfigDto.Item item2 = itemMap.get("1_111");
                                if (item2!= null && !"".equals(item2.getValue()) &&!"-".equals(item2.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item2.getName());
                                    config.setCfgvalue(item2.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //发动机
                                SpecParamConfigDto.Item item5 = itemMap.get("1_115");
                                if (item5 != null&& !"".equals(item5.getValue()) &&!"-".equals(item5.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item5.getName());
                                    config.setCfgvalue(item5.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //环保标准
                                SpecParamConfigDto.Item item1 = itemMap.get("1_61");
                                if (item1 != null&& !"".equals(item1.getValue()) &&!"-".equals(item1.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item1.getName());
                                    config.setCfgvalue(item1.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //最大功率
                                SpecParamConfigDto.Item item4 = itemMap.get("1_50");
                                if (item4 != null&& !"".equals(item4.getValue()) &&!"-".equals(item4.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item4.getName());
                                    config.setCfgvalue(item4.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //能源类型
                                SpecParamConfigDto.Item item = itemMap.get("1_55");
                                if (item != null&& !"".equals(item.getValue()) &&!"-".equals(item.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item.getName());
                                    config.setCfgvalue(item.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //变速箱
                                SpecParamConfigDto.Item item3 = itemMap.get("1_108");
                                if (item3 != null&& !"".equals(item3.getValue()) &&!"-".equals(item3.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item3.getName());
                                    config.setCfgvalue(item3.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                            }else {
                                seriesBuilder.setBgcarimgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_icon_share_newenergy_bg.webp");
                                //厂商
                                SpecParamConfigDto.Item item2 = itemMap.get("1_111");
                                if (item2!= null&& !"".equals(item2.getValue()) &&!"-".equals(item2.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item2.getName());
                                    config.setCfgvalue(item2.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //0-100加速
                                SpecParamConfigDto.Item item3 = itemMap.get("1_2");
                                if (item3!= null&& !"".equals(item3.getValue()) &&!"-".equals(item3.getValue())){
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname("百公里加速");
                                    config.setCfgvalue(item3.getValue()+"s");
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //环保标准
                                SpecParamConfigDto.Item item1 = itemMap.get("1_61");
                                if (item1 != null&& !"".equals(item1.getValue()) &&!"-".equals(item1.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item1.getName());
                                    config.setCfgvalue(item1.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //电机
                                SpecParamConfigDto.Item item4 = itemMap.get("1_114");
                                if (item4!= null&& !"".equals(item4.getValue()) &&!"-".equals(item4.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item4.getName());
                                    config.setCfgvalue(item4.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //能源类型
                                SpecParamConfigDto.Item item = itemMap.get("1_55");
                                if (item != null&& !"".equals(item.getValue()) &&!"-".equals(item.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item.getName());
                                    config.setCfgvalue(item.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }
                                //上市时间
                                SpecParamConfigDto.Item item9 = itemMap.get("1_109");
                                if (item9!= null&& !"".equals(item9.getValue()) &&!"-".equals(item9.getValue())) {
                                    GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                                    config.setCfgname(item9.getName());
                                    config.setCfgvalue(item9.getValue());
                                    seriesBuilder.addParamsconfig(config);
                                }

                            }
                        }
                        if(seriesBuilder.getParamsconfigCount() < 2){
                            seriesBuilder.clearParamsconfig();
                        }
                        if (seriesBuilder.getParamsconfigCount()==0){
                            //打底文案
                            GetConfigShareResponse.Paramsconfig.Builder config = GetConfigShareResponse.Paramsconfig.newBuilder();
                            config.setCfgname("");
                            config.setCfgvalue("去汽车之家查看参数配置详情");
                            seriesBuilder.addParamsconfig(config);
                        }
                    }).join();
                }
                return seriesBuilder;
            });
            //经销商报价
            CompletableFuture<String> canKaoprice = getCanKaoprice(request.getSeriesid(), request.getCityid(), request.getPluginversion());
            //新能源参数
            CompletableFuture<List<GetConfigShareResponse.Newenergy>> newEnConfigList = getNewEnConfigList(request.getSeriesid(), 0);


//            Map<Integer, SpecParamConfigDto> join = specParamInfoNewComponent.getMap(List.of(specId)).join();

            return CompletableFuture.allOf(canKaoprice, getParamInfo ,newEnConfigList)
                    .thenApply(v -> {
                        GetConfigShareResponse.SeriesInfo.Builder seriesInfoBuilder = getParamInfo.join();
                        if (seriesInfoBuilder.getIsnewenergy() ==1){
                            List<GetConfigShareResponse.Newenergy> newenergyList = newEnConfigList.join();
                            seriesInfoBuilder.addAllNewenergy(newenergyList);
                        }
                        seriesInfoBuilder.setReferencepricerange(canKaoprice.join());
                        return seriesInfoBuilder.build();
                    })
                    .exceptionally(ex -> {
                        log.error("createSeriesBaseInfoBuilder error", ex);
                        return null;
                    });

        } catch (Exception e) {
            log.error("createSeriesBaseInfoBuilder error", e);
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * 经销商价格区间
     */
    public CompletableFuture<String> getCanKaoprice(
            int seriesId,int cityId,String pluginversion
    ){
        CompletableFuture<SeriesCityAskPriceDto> seriesCityAskPriceFuture = seriesCityAskPriceComponent.get(seriesId, cityId);
        return seriesCityAskPriceFuture.thenApply(p -> {
            String cankaoprice="暂无报价";
            if (p != null) {
                int minPrice = p.getMinPrice();
                int maxPrice = p.getMaxPrice();
                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5") && p.getMinPriceOnSale() > 0 && p.getMaxPriceOnSale() > 0) {
                    minPrice = p.getMinPriceOnSale();
                    maxPrice = p.getMaxPriceOnSale();
                }
                if (minPrice > 0) {
                    if (minPrice == maxPrice) {
                        cankaoprice=(CommonHelper.getMoney(minPrice, "起"));
                    } else {
                        cankaoprice=(CommonHelper.priceFormat(Double.parseDouble(minPrice + ""), Double.parseDouble(maxPrice + ""), CarSellTypeEnum.Selling, "-"));
                    }
                }
            }
            return cankaoprice;
        });
    }
    CompletableFuture<List<Integer>> getSpecIds2(String pluginversion, String specIdStr, int site, int year, int seriesId,
                                                 ConcurrentMap<Integer, SpecDetailDto> specs,
                                                 Vector<String> deleteSpecIds,
                                                 Vector<String> deleteSpecName,
                                                 List<SpecGroupOfSeriesDto> seriesYears
    ) {

        //车型不传年代款时，只显示当前车型的信息
        if ( site == 3) {
            return getFilterSpecIds(specIdStr,specs,deleteSpecIds,deleteSpecName);
        }

        if (site == 1 || site == 2) {
            return specYearNewComponent.getAsync(seriesId).thenCompose(specYearList -> {
                List<Integer> specIds = new ArrayList<>();
                List<Integer> stopSellSpecIds = new ArrayList<>();
                if (specYearList == null || specYearList.size() == 0) {
                    return CompletableFuture.completedFuture(specIds);
                }
                if(site == 1){
                    seriesYears.addAll(specYearList);
                }
                if(site == 2&&year==0){
                    seriesYears.addAll(specYearList);
                    return getFilterSpecIds(specIdStr,specs,deleteSpecIds,deleteSpecName);
                }
                return CompletableFuture.supplyAsync(()->{
                    int onSellYearCount = 0;
                    int addStopSell = 0;
                    for (SpecGroupOfSeriesDto yearItem : specYearList) {
                        if (year > 0) {  //如果year > 0，就只返回当前年代款的
                            if (yearItem.getYearvalue() == year) {
                                yearItem.getYearspeclist().forEach(x -> specIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1 && (site != 1 || y.getState() == 40)).map(z -> z.getSpecId()).collect(Collectors.toList())));
                            }
                        } else if (site == 1) {
                            if (StringUtils.equalsAny(yearItem.getYearname(), "在售", "即将销售")) {
                                yearItem.getYearspeclist().forEach(x -> specIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1).map(z -> z.getSpecId()).collect(Collectors.toList())));
                                if (onSellYearCount++ >= 2 && specIds.size() > 0) {
                                    return specIds;
                                }
                            } else if (specIds.size() == 0 && addStopSell== 0) {
                                yearItem.getYearspeclist().forEach(x -> stopSellSpecIds.addAll(x.getSpeclist().stream().filter(y -> y.getParamIsShow() == 1).map(z -> z.getSpecId()).collect(Collectors.toList())));
                                if(!stopSellSpecIds.isEmpty() && CommonHelper.isTakeEffectVersion(pluginversion,"11.67.0")){
                                    addStopSell =1;
                                }
                            }
                        }
                    }
                    return specIds.size() > 0 ? specIds : stopSellSpecIds;
                });
            });
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    private CompletableFuture<List<GetConfigShareResponse.Newenergy>> getNewEnConfigList(int seriesid, int specid) {
        List<GetConfigShareResponse.Newenergy> energyconfiglist = new ArrayList();
        try {
            CompletableFuture<SNewEnaryConfigResult> newEnantyConfigInfo = configItemApiClient.getNewEnantyConfigInfo(seriesid);
            return newEnantyConfigInfo.thenApply(source -> {
                if (source != null && source.getResult() != null && source.getResult().getSpecitems() != null && !source.getResult().getSpecitems().isEmpty()) {
                    List<String> specState = getSpecState(source);
                    if (specState != null && specState.size() > 0) {
                        String noData = "暂无";
                        List<SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems> newEnSpecItems = source.getResult().getSpecitems();

                        GetConfigShareResponse.Newenergy.Builder dto = GetConfigShareResponse.Newenergy.newBuilder();
                        dto.setSubtitle("容量");
                        dto.setLogo("http://nfiles3.autohome.com.cn/zrjcpk10/car_config_share_icon_rongliang.webp");
                        if (source.getResult().getSpecitems().stream()
                                .filter(specItem -> specItem.getBatterycapacity() > 0
                                        && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1) != null
                                && source.getResult().getSpecitems().stream()
                                .filter(specItem -> specItem.getBatterycapacity() > 0
                                        && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1)
                                .count() > 0) {
                            SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems maxSpecItem = source.getResult().getSpecitems().stream()
                                    .filter(specItem -> specItem.getBatterycapacity() > 0
                                            && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1)
                                    .sorted(Comparator.comparing(SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems::getBatterycapacity).reversed())
                                    .findFirst().get();
                            dto.setTitle(maxSpecItem.getBatterycapacity() + "kWh");
                        } else {
                            dto.setTitle(noData);
                        }
                        energyconfiglist.add(dto.build());


                        GetConfigShareResponse.Newenergy.Builder XDto = GetConfigShareResponse.Newenergy.newBuilder();
                        XDto.setSubtitle("官方续航");
                        XDto.setLogo("http://nfiles3.autohome.com.cn/zrjcpk10/car_config_share_icon_xuhang.webp");
                        if (source.getResult().getSpecitems().stream()
                                .filter(specItem -> specItem.getMileage() > 0
                                        && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1) != null
                                && source.getResult().getSpecitems().stream()
                                .filter(specItem -> specItem.getMileage() > 0
                                        && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1)
                                .count() > 0) {
                            SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems maxSpecItem = source.getResult().getSpecitems().stream()
                                    .filter(specItem -> specItem.getMileage() > 0
                                            && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1)
                                    .sorted(Comparator.comparing(SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems::getMileage).reversed()).findFirst()
                                    .get();
                            XDto.setTitle(maxSpecItem.getMileage() + "KM");
                        } else {
                            XDto.setTitle(noData);
                        }
                        energyconfiglist.add(XDto.build());

                        GetConfigShareResponse.Newenergy.Builder KDto = GetConfigShareResponse.Newenergy.newBuilder();
                        KDto.setSubtitle("快充");
                        // KDto.setLogob("http://nfiles3.autohome.com.cn/zrjcpk10/car_kuaichong_20201210@3x.png");
                        KDto.setLogo("http://nfiles3.autohome.com.cn/zrjcpk10/car_config_share_icon_fast.webp");
                        Optional<SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems> minSpecItemOpt;
                        if (specid > 0) {
                            minSpecItemOpt = newEnSpecItems.stream()
                                    .filter(specItem -> specItem.getSpecid() == specid && specItem.getOfficialfastchargetime() > 0 && (specState.get(0).contains(specItem.getSpecstate() + "")))
                                    .min(Comparator.comparing(SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems::getOfficialfastchargetime));
                        } else {
                            minSpecItemOpt = newEnSpecItems.stream()
                                    .filter(specItem -> specItem.getOfficialfastchargetime() > 0 && (specState.get(0).contains(specItem.getSpecstate() + "")))
                                    .min(Comparator.comparing(SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems::getOfficialfastchargetime));
                        }
                        if (minSpecItemOpt != null && minSpecItemOpt.isPresent()) {
                            SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems minSpecItem = minSpecItemOpt.get();
                            KDto.setTitle(minSpecItem.getOfficialfastchargetime() + "小时");
                        } else {
                            KDto.setTitle(noData);
                        }
                        energyconfiglist.add(KDto.build());

                        GetConfigShareResponse.Newenergy.Builder MDto = GetConfigShareResponse.Newenergy.newBuilder();
                        MDto.setSubtitle("慢充");
                        //MDto.setLogob("http://nfiles3.autohome.com.cn/zrjcpk10/car_manchong_20201210@3x.png");
                        MDto.setLogo("http://nfiles3.autohome.com.cn/zrjcpk10/car_config_share_icon_slow.webp");
                        if (specid > 0) {
                            minSpecItemOpt = newEnSpecItems.stream()
                                    .filter(specItem -> specItem.getSpecid() == specid && specItem.getOfficialslowchargetime() > 0 && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1)
                                    .min(Comparator.comparing(SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems::getOfficialslowchargetime));
                        } else {
                            minSpecItemOpt = newEnSpecItems.stream()
                                    .filter(specItem -> specItem.getOfficialslowchargetime() > 0 && specState.get(0).indexOf(specItem.getSpecstate() + "") > -1)
                                    .min(Comparator.comparing(SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems::getOfficialslowchargetime));
                        }
                        if (minSpecItemOpt != null && minSpecItemOpt.isPresent()) {
                            SNewEnaryConfigResult.SNewEnaryConfigResult_Result.NewEnergySpecitems minSpecItem = minSpecItemOpt.get();
                            MDto.setTitle(minSpecItem.getOfficialslowchargetime() + "小时");
                        } else {
                            MDto.setTitle(noData);
                        }
                        energyconfiglist.add(MDto.build());
                    }
                }
                return energyconfiglist;
            });

        } catch (Exception e) {
            String exceptionMsg = String.format("ErrorMessage:%s;RequestParm:seriesid=%s;  exceptionStack:%s;",
                    "海报分享卡片获取新能源配置异常", seriesid, ExceptionUtils.getStackTrace(e));
            return CompletableFuture.completedFuture(energyconfiglist);
        }
    }
    private List<String> getSpecState(SNewEnaryConfigResult source) {
        List<String> specState = new ArrayList<>();
        if (source != null && source.getResult() != null && source.getResult().getSpecitems().size() > 0) {
            if (source.getResult().getSpecitems().stream().anyMatch(specItem -> specItem.getSpecstate() == 20 || specItem.getSpecstate() == 30)) {
                specState.add("20,30");
            } else if (source.getResult().getSpecitems().stream().anyMatch(specItem -> specItem.getSpecstate() == 40)) {
                specState.add("40");
            }
        }
        return specState;
    }
}
