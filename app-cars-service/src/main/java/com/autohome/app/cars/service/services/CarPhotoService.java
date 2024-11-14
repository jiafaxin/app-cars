package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.*;
import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.apiclient.baike.dtos.ConfigBaikeLinkDto;
import com.autohome.app.cars.apiclient.car.ConfigItemApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.ListCshDealerByCityResult;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.carconfig.SixtyPic.SixtyPic;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.popauto.entities.PicParamConfigEntity;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.common.PageOf;
import com.autohome.app.cars.service.components.baike.BaikelinkforconfigComponent;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.dealer.DealerComponent;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicDataDto;
import com.autohome.app.cars.service.components.hqpic.utils.TypeIdTranslateUtil;
import com.autohome.app.cars.service.components.remodel.SeriesRemodel3DComponent;
import com.autohome.app.cars.service.components.remodel.SeriesRemodelCoversComponent;
import com.autohome.app.cars.service.components.remodel.dtos.SeriesRemodel3DDto;
import com.autohome.app.cars.service.components.remodel.dtos.SeriesRemodelCoversDto;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.app.cars.service.components.vr.SeriesVrPointComponent;
import com.autohome.app.cars.service.components.vr.SpecVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVrPointDto;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import com.autohome.app.cars.service.services.dtos.CarVideoData;
import com.autohome.app.cars.service.services.dtos.VideoVRConfig;
import com.autohome.app.cars.service.services.dtos.piclist.GlTabPicConfig;
import com.autohome.app.cars.service.services.dtos.piclist.GroupCountDto;
import com.autohome.app.cars.service.services.enums.SpecStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarPhotoService {

    @Autowired
    ColorComponent colorComponent;

    @Autowired
    CarPhotoComponent carPhotoComponent;

    @Autowired
    HqPhotoComponent hqPhotoComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    @Autowired
    DealerComponent dealerComponent;

    @Value("${piclist_usebigsize:0}")
    private int usebigsize;
    @Autowired
    SeriesVrComponent seriesVrComponent;

    @Autowired
    SpecVrComponent specVrComponent;

    @Autowired
    SeriesVrPointComponent seriesVrPointComponent;

    @Autowired
    SeriesRemodel3DComponent seriesRemodel3DComponent;

    @Autowired
    SeriesRemodelCoversComponent seriesRemodelCoversComponent;

    @Autowired
    SeriesSixtyPicComponent seriesSixtyPicComponent;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.VideoVRConfig).decodeVideoVRConfig('${videovrconfig:}')}")
    List<VideoVRConfig> videoVRConfigList;

    @Value("${carVideos:}")
    private String carVideos;
    final static String shareUrlTemp = "https://comm.app.autohome.com.cn/comm_v1.0.0/cars/dutusharedcontent-pm%s-ss%s-sp%s-t%s-imgid%s.html";

    @Value("${carpic_gltab_config:}")
    private String carpicGltabConfig;

    @Autowired
    BaikelinkforconfigComponent baikelinkforconfigComponent;

    @Autowired
    PicParamConfigComponent picParamConfigComponent;

    @Value("${series_spec_pic_filter:}")
    private String series_spec_pic_filter;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    ConfigItemApiClient configItemApiClient;

    /**
     * 获取颜色列表
     *
     * @param specSaleStatus
     * @param seriesStatus
     * @param hqPicDataDto
     * @return
     */
    public CompletableFuture<List<PicListResponse.Result.Colorlist>> getColorList(
            PicListRequest request,
            int specSaleStatus, int seriesStatus,
            CompletableFuture<List<SeriesVrExteriorResult.Color_List>> vrColorList,
            HqPicDataDto hqPicDataDto) {
        String pluginversion = request.getPluginversion();
        String piccolor1 = request.getPiccolor1();//图库缺失图片补全实验1
        String piccolor2 = request.getPiccolor2();//图库缺失图片补全实验2
        String piccolor3 = request.getPiccolor3();//图库缺失图片补全实验3
        int categoryId = request.getCategoryid();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int categoryid = request.getCategoryid();
        Vector<PicListResponse.Result.Colorlist.Builder> onsaleList = new Vector<>();
        Vector<PicListResponse.Result.Colorlist.Builder> stopsaleList = new Vector<>();
        Vector<PicListResponse.Result.Colorlist.Builder> vrList = new Vector<>();

        List<Integer> classIds = Arrays.asList(categoryid);
        if (categoryid == 1) {
            classIds = new ArrayList<>();
        }

        AtomicBoolean isTest = new AtomicBoolean(false);
        List<Integer> filterSpecIds = new ArrayList<>();
        Map<Integer, List<Integer>> outColorMap = new HashMap<>();
        Map<Integer, List<Integer>> innerColorMap = new HashMap<>();
        if (CommonHelper.isTakeEffectVersion(pluginversion, "11.65.5") && Arrays.asList(1, 10, 3, 12).contains(categoryId)) {
            String json = "{\"piccolor3_a\":[{\"seriesId\":65,\"specInfos\":[{\"specId\":54011,\"outColors\":[3638],\"innerColors\":[]}]}],\"piccolor3_b\":[{\"seriesId\":65,\"specInfos\":[{\"specId\":64199,\"outColors\":[3241],\"innerColors\":[3914]}]}]}";
            json = series_spec_pic_filter;
            CarPicFilterInfoDto filterInfoDto = JsonUtil.toObject(json, CarPicFilterInfoDto.class);
            if (filterInfoDto != null) {
                CarPicFilterInfoDto.FilterBean filterBean = null;
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor1, "A") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor1_a())) {
                    filterBean = filterInfoDto.getPiccolor1_a().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor1, "B") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor1_b())) {
                    filterBean = filterInfoDto.getPiccolor1_b().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor2, "A") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor2_a())) {
                    filterBean = filterInfoDto.getPiccolor2_a().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor2, "B") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor2_b())) {
                    filterBean = filterInfoDto.getPiccolor2_b().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor3, "A") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor3_a())) {
                    filterBean = filterInfoDto.getPiccolor3_a().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor3, "B") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor3_b())) {
                    filterBean = filterInfoDto.getPiccolor3_b().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean != null) {
                    int finalSpecId = specId;
                    if (specId > 0) {
                        filterBean.getSpecInfos().stream().filter(spec -> spec.getSpecId() == finalSpecId).findFirst().ifPresent(spec -> {
                            filterSpecIds.add(spec.getSpecId());
                            if (ListUtil.isNotEmpty(spec.getOutColors()) && Arrays.asList(1, 12).contains(categoryId)) {
                                outColorMap.put(spec.getSpecId(), spec.getOutColors());
                            }
                            if (ListUtil.isNotEmpty(spec.getInnerColors()) && Arrays.asList(3, 10).contains(categoryId)) {
                                innerColorMap.put(spec.getSpecId(), spec.getInnerColors());
                            }
                        });
                    } else {
                        filterBean.getSpecInfos().forEach(spec -> {
                            filterSpecIds.add(spec.getSpecId());
                            if (ListUtil.isNotEmpty(spec.getOutColors()) && Arrays.asList(1, 12).contains(categoryId)) {
                                outColorMap.put(spec.getSpecId(), spec.getOutColors());
                            }
                            if (ListUtil.isNotEmpty(spec.getInnerColors()) && Arrays.asList(3, 10).contains(categoryId)) {
                                innerColorMap.put(spec.getSpecId(), spec.getInnerColors());
                            }
                        });
                    }
                    isTest.set(true);
                }
            }
        }

        CompletableFuture<List<ColorStatisticsDto>> colorFuture = null;
        if (isTest.get()) {
            colorFuture = colorComponent.getColors(request.getIsinner() == 1 ? 2 : 1, seriesId, specId, classIds, filterSpecIds, outColorMap, innerColorMap);
        } else {
            colorFuture = colorComponent.getColors(request.getIsinner() == 1 ? 2 : 1, seriesId, specId, classIds);
        }

        List<HqPicDataDto.ColorInfo> hqColorList = new ArrayList<>();
        if (Objects.nonNull(hqPicDataDto) && Objects.nonNull(hqPicDataDto.getColorInfoList())) {
            List<Integer> finalClassIds = classIds;
            List<HqPicDataDto.ColorInfo> hqFilterList = hqPicDataDto.getColorInfoList().stream().filter(x -> {
                if (request.getIsinner() == 0 && x.getHqTypeId() != 1) {
                    return false;
                }
                if (request.getIsinner() == 1 && x.getHqTypeId() == 1) {
                    return false;
                }
                if (specId > 0 && x.getSpecId() != specId) {
                    return false;
                }
                if (finalClassIds != null && finalClassIds.size() > 0 && !finalClassIds.contains(x.getCpkTypeId())) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            Map<Integer, List<HqPicDataDto.ColorInfo>> hqColorMap = hqFilterList.stream().collect(Collectors.groupingBy(x -> x.getColorId()));
            hqColorMap.forEach((k, v) -> {
                hqColorList.add(v.get(0));
            });
        }

        CompletableFuture pic = colorFuture.thenAccept(colorList -> {
            if (colorList == null || colorList.size() == 0) {
                return;
            }
            for (ColorStatisticsDto dto : colorList) {
                if (dto.getId() == 0 && StringUtils.isBlank(dto.getName()) && StringUtils.isBlank(dto.getValue())) {
                    continue;
                }
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(dto.getId())
                        .setName(dto.getName())
                        .setValue(dto.getValue());
                //删除高质量图重复颜色
                hqColorList.removeIf(x -> x.getColorId() == dto.getId());
                if (seriesStatus != 40 && (dto.getIsonsale() == 1 || (specId > 0 && specSaleStatus == 1))) {
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                        colorlist.setColortype(1);
                    }
                    onsaleList.add(colorlist);
                } else {
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                        colorlist.setColortype(4);
                    }
                    stopsaleList.add(colorlist);
                }
            }
        });


        CompletableFuture vr = vrColorList.thenAccept(vrDetail -> {
            if (vrDetail == null || vrDetail.size() == 0) {
                return;
            }
            for (SeriesVrExteriorResult.Color_List colorItem : vrDetail) {
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(colorItem.getColorId())
                        .setName(colorItem.getColorName())
                        .setValue("#" + colorItem.getColorValue());
                if (StringUtils.isNotBlank(colorItem.getColorNames())) {
                    colorlist.setValue("#" + colorItem.getColorValues().replace("/", "/#"));
                    colorlist.setName(colorItem.getColorNames());
                }
                vrList.add(colorlist);

            }
        });

        return CompletableFuture.allOf(pic, vr).thenApply(xx -> {
            List<PicListResponse.Result.Colorlist.Builder> result = new ArrayList<>();
            result.add(PicListResponse.Result.Colorlist.newBuilder().setName("全部颜色").setValue(""));
            //拼接HqColor
            for (HqPicDataDto.ColorInfo hqColor : hqColorList) {
                if (hqColor.getColorId() == 0 || StringUtils.isBlank(hqColor.getColorName()) || StringUtils.isBlank(hqColor.getColorValue())) {
                    continue;
                }
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(hqColor.getColorId())
                        .setName(hqColor.getColorName())
                        .setValue(hqColor.getColorValue());
                if (seriesStatus != 40 && (hqColor.getIsOnsale() == 1 || (specId > 0 && specSaleStatus == 1))) {
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                        colorlist.setColortype(1);
                    }
                    onsaleList.add(colorlist);
                } else {
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                        colorlist.setColortype(4);
                    }
                    stopsaleList.add(colorlist);
                }
            }
            Map<String, PicListResponse.Result.Colorlist.Builder> stopPicMap = stopsaleList.size() > 0 ? stopsaleList.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();
            Map<String, PicListResponse.Result.Colorlist.Builder> vrMap = vrList.size() > 0 ? vrList.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();
            if (seriesStatus != 40 && stopsaleList.size() > 0) {
                //移除VR颜色中的停售颜色
                vrList.removeIf(x -> stopPicMap.containsKey(x.getName()));
                //把VR的value设置为停售的value：这里主要是为了兼容VR，但是是不合理的
                stopsaleList.forEach(x -> {
                    if (!vrMap.containsKey(x.getName())) {
                        return;
                    }
                    x.setValue(vrMap.get(x.getName()).getValue());
                });
            }
            List<PicListResponse.Result.Colorlist.Builder> vrPicColors = new ArrayList<>();
            List<PicListResponse.Result.Colorlist.Builder> vrColors = new ArrayList<>();

            if (seriesStatus == 40 && onsaleList.size() == 0 && stopsaleList.size() > 0) {
                for (PicListResponse.Result.Colorlist.Builder vrItem : vrList) {
                    if (stopPicMap.containsKey(vrItem.getName())) {
                        PicListResponse.Result.Colorlist.Builder b = stopPicMap.get(vrItem.getName());
                        vrItem.setId(b.getId());
                        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                            vrItem.setColortype(1);
                        }
                        vrPicColors.add(vrItem);
                        stopsaleList.remove(b);
                    } else {
                        vrItem.setTag("仅VR");
                        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                            vrItem.setColortype(2);
                        }
                        vrColors.add(vrItem);
                    }
                }
                Collections.reverse(stopsaleList);
                result.add(0, PicListResponse.Result.Colorlist.newBuilder().setName("停售：").setValue("").setId(-2));
                result.addAll(vrPicColors);  //vr和图片都有的颜色
                result.addAll(vrColors);  //只有VR有的颜色
                result.addAll(stopsaleList);  //只有图片有的颜色
            } else {
                Map<String, PicListResponse.Result.Colorlist.Builder> picMap = onsaleList.stream().collect(Collectors.toMap(PicListResponse.Result.Colorlist.Builder::getName, x -> x, (o, n) -> o));
                for (PicListResponse.Result.Colorlist.Builder vrItem : vrList) {
                    if (picMap.containsKey(vrItem.getName())) {
                        PicListResponse.Result.Colorlist.Builder picItem = picMap.get(vrItem.getName());
                        vrItem.setId(picItem.getId());
                        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                            vrItem.setColortype(1);
                        }
                        vrPicColors.add(vrItem);
                        onsaleList.remove(picMap.get(vrItem.getName()));
                    } else {
                        vrItem.setTag("仅VR");
                        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                            vrItem.setColortype(2);
                        }
                        vrColors.add(vrItem);
                    }
                }
                result.addAll(vrPicColors);  //vr和图片都有的颜色
                result.addAll(vrColors);  //只有VR有的颜色
                result.addAll(onsaleList);  //只有图片有的颜色
                if (stopsaleList.size() > 0 && seriesStatus != 40) {
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.0")) {
                        result.add(
                                PicListResponse.Result.Colorlist.newBuilder()
                                        .setName("停售:")
                                        .setValue("")
                                        .setId(-2)
                        );
                        result.addAll(stopsaleList);
                    } else {
                        result.add(
                                PicListResponse.Result.Colorlist.newBuilder()
                                        .setName("停售颜色")
                                        .setValue("")
                                        .setId(-1)
                                        .addAllSublist(stopsaleList.stream().map(x -> x.build()).collect(Collectors.toList()))
                        );
                    }
                }
            }

            return result.stream().map(x -> x.build()).collect(Collectors.toList());
        }).exceptionally(e -> {
            log.error("getColorList error", e);
            return new ArrayList<>();
        });
    }

    /**
     * 获取颜色列表, 其他车型颜色补全实验
     *
     * @param specSaleStatus
     * @param seriesStatus
     * @return
     */
    public CompletableFuture<List<PicListResponse.Result.Colorlist>> getColorListWithOtherSpecColor(
            PicListRequest request,
            int specSaleStatus, int seriesStatus,
            CompletableFuture<List<SeriesVrExteriorResult.Color_List>> vrColorListSpec,
            SpecOutInnerColorDto colors
    ) {
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int categoryid = request.getCategoryid();

        List<PicListResponse.Result.Colorlist.Builder> onsaleListSpecHasPic = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> onsaleSpecColorList = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> stopsaleListSpecHasPic = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> stopsaleSpecColorList = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> onsaleListSeriesHasPic = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> onsaleListSeriesNoPic = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> stopsaleListSeriesHasPic = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> stopsaleListSeriesNoPic = new Vector<>();
        List<PicListResponse.Result.Colorlist.Builder> vrListSpec = new Vector<>();

        List<Integer> classIds = Arrays.asList(categoryid);
        if (categoryid == 1) {
            classIds = new ArrayList<>();
        }

        List<Integer> mixClassIds = new ArrayList<>();
        if (1 == request.getOutermixdetialpic() && CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.3")) {
            mixClassIds = Arrays.asList(1, 12);
        } else {
            mixClassIds = classIds;
        }

        // 本车型的颜色
        CompletableFuture picSpec = colorComponent.getColorsWithOtherSpec(CommonHelper.isInner(request.getCategoryid()) ? 2 : 1, seriesId, specId, classIds).thenAccept(colorList -> {
            if (colorList == null || colorList.size() == 0) {
                return;
            }
            for (ColorStatisticsDto dto : colorList) {
                if (dto.getId() == 0 && StringUtils.isBlank(dto.getName()) && StringUtils.isBlank(dto.getValue())) {
                    continue;
                }
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(dto.getId())
                        .setName(dto.getName())
                        .setValue(dto.getValue());
                if (seriesStatus != 40 && (dto.getIsonsale() == 1 || (specId > 0 && specSaleStatus == 1))) {
                    onsaleListSpecHasPic.add(colorlist);
                }else {
                    stopsaleListSpecHasPic.add(colorlist);
                }

            }
        });
        // 该车型所有的颜色中找缺失图片的颜色
        for (SpecOutInnerColorDto.ColorItem dto : colors.getColoritems()) {
            PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                    .setId(dto.getId())
                    .setName(dto.getName())
                    .setValue(dto.getValue());
            if (specSaleStatus != 40) {
                onsaleSpecColorList.add(colorlist);
            } else {
                stopsaleSpecColorList.add(colorlist);
            }
        }

        //本车系停售颜色但是有图片的
        CompletableFuture picSeriesNoSale = carPhotoComponent.getChannelCount(seriesId, specId, CommonHelper.isInner(categoryid) ? 1 : 0, mixClassIds.stream().map(e -> String.valueOf(e)).collect(Collectors.joining(",")), 0, null).thenAccept(channelCountList -> {
            if (channelCountList == null || channelCountList.size() == 0) {
                return;
            }

            for (CarPhotoCountDto dto : channelCountList) {
                if (StringUtils.isBlank(dto.getColorName()) && StringUtils.isBlank(dto.getColorValue())) {
                    continue;
                }
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(dto.getColorId())
                        .setName(dto.getColorName())
                        .setValue(dto.getColorValue());
                if (dto.getCount() == 0) {
                    stopsaleListSeriesNoPic.add(colorlist);
                }
            }
        });


        // 所有车型的颜色
        CompletableFuture picSeries = colorComponent.getColorsWithOtherSpec(CommonHelper.isInner(categoryid) ? 2 : 1, seriesId, 0, mixClassIds).thenAccept(colorList -> {
            if (colorList == null || colorList.size() == 0) {
                return;
            }
            for (ColorStatisticsDto dto : colorList) {
                if (dto.getId() == 0 && StringUtils.isBlank(dto.getName()) && StringUtils.isBlank(dto.getValue())) {
                    continue;
                }
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(dto.getId())
                        .setName(dto.getName())
                        .setValue(dto.getValue());
                if (seriesStatus != 40 && (dto.getIsonsale() == 1 || (specId > 0 && specSaleStatus == 1))) {
                    onsaleListSeriesHasPic.add(colorlist);
                }else {
                    stopsaleListSeriesHasPic.add(colorlist);
                }
            }
        });

        CompletableFuture vrSpec = vrColorListSpec.thenAccept(vrDetail -> {
            if (vrDetail == null || vrDetail.size() == 0) {
                return;
            }
            for (SeriesVrExteriorResult.Color_List colorItem : vrDetail) {
                PicListResponse.Result.Colorlist.Builder colorlist = PicListResponse.Result.Colorlist.newBuilder()
                        .setId(colorItem.getColorId())
                        .setName(colorItem.getColorName())
                        .setValue("#" + colorItem.getColorValue());
                if (StringUtils.isNotBlank(colorItem.getColorNames())) {
                    colorlist.setValue("#" + colorItem.getColorValues().replace("/", "/#"));
                    colorlist.setName(colorItem.getColorNames());
                }
                vrListSpec.add(colorlist);
            }
        });

        return CompletableFuture.allOf(picSpec, picSeriesNoSale, picSeries, vrSpec).thenApply(xx -> {
            List<PicListResponse.Result.Colorlist.Builder> result = new ArrayList<>();
            result.add(PicListResponse.Result.Colorlist.newBuilder().setName("全部颜色").setValue(""));

            Map<String, PicListResponse.Result.Colorlist.Builder> salePicMapSeries = onsaleListSeriesHasPic.size() > 0 ? onsaleListSeriesHasPic.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();
            Map<String, PicListResponse.Result.Colorlist.Builder> stopNoPicMapSeries = stopsaleListSeriesNoPic.size() > 0 ? stopsaleListSeriesNoPic.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();
            Map<String, PicListResponse.Result.Colorlist.Builder> salePicMapSpec = onsaleListSpecHasPic.size() > 0 ? onsaleListSpecHasPic.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();
            Map<String, PicListResponse.Result.Colorlist.Builder> stopPicMapSpec = stopsaleListSpecHasPic.size() > 0 ? stopsaleListSpecHasPic.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();
            Map<String, PicListResponse.Result.Colorlist.Builder> stopMapSpec = stopsaleSpecColorList.size() > 0 ? stopsaleSpecColorList.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();

            Map<String, PicListResponse.Result.Colorlist.Builder> vrMapSpec = vrListSpec.size() > 0 ? vrListSpec.stream().collect(Collectors.toMap(x -> x.getName(), x -> x, (o, n) -> o)) : new HashMap<>();

            // 本车型的在产在售、停产在售颜色、未售颜色、即将销售颜色（图片和VR都有）
            List<PicListResponse.Result.Colorlist.Builder> listSort1 = onsaleListSpecHasPic.stream().filter(e -> vrMapSpec.containsKey(e.getName())).toList();

            // 本车型的在产在售、停产在售颜色、未售颜色、即将销售颜色（仅有图片）
            List<PicListResponse.Result.Colorlist.Builder> listSort2 = onsaleListSpecHasPic.stream().filter(e -> !vrMapSpec.containsKey(e.getName())).toList();

            // 本车型的在产在售、停产在售颜色、未售颜色、即将销售颜色（仅VR）
            List<PicListResponse.Result.Colorlist.Builder> listSort3 = vrListSpec.stream().filter(e -> !salePicMapSpec.containsKey(e.getName()) && !stopMapSpec.containsKey(e.getName())).toList();

            // 其他车型的在产在售、停产在售颜色、未售颜色、即将销售颜色（仅图片）
            List<PicListResponse.Result.Colorlist.Builder> listSort4 = onsaleSpecColorList.stream().filter(e -> !salePicMapSpec.containsKey(e.getName()) && salePicMapSeries.containsKey(e.getName()) && !vrMapSpec.containsKey(e.getName())).toList();

            // 本车型的停售颜色（图片和VR都有）
            List<PicListResponse.Result.Colorlist.Builder> listSort5 = stopsaleSpecColorList.stream().filter(e -> !stopNoPicMapSeries.containsKey(e.getName()) && vrMapSpec.containsKey(e.getName())).toList();

            // 本车型的停售颜色（仅有图片）
            List<PicListResponse.Result.Colorlist.Builder> listSort6 = stopsaleSpecColorList.stream().filter(e -> !stopNoPicMapSeries.containsKey(e.getName()) && !vrMapSpec.containsKey(e.getName())).toList();

            // 本车型的停售颜色 (仅VR)
            List<PicListResponse.Result.Colorlist.Builder> listSort7 = stopsaleSpecColorList.stream().filter(e -> stopNoPicMapSeries.containsKey(e.getName()) && vrMapSpec.containsKey(e.getName())).toList();

            listSort1.forEach(e -> e.setColortype(1));
            result.addAll(listSort1);
            listSort2.forEach(e -> e.setColortype(1));
            result.addAll(listSort2);
            listSort3.forEach(e -> e.setTag("仅VR").setColortype(2));
            result.addAll(listSort3);
            listSort4.forEach(e -> e.setTag("其它车型").setColortype(3));
            result.addAll(listSort4);

            List<PicListResponse.Result.Colorlist.Builder> stopsaleList = new ArrayList<>();
            listSort5.forEach(e -> e.setColortype(4));
            stopsaleList.addAll(listSort5);
            listSort6.forEach(e -> e.setColortype(4));
            stopsaleList.addAll(listSort6);
            listSort7.forEach(e -> e.setTag("仅VR").setColortype(4));
            stopsaleList.addAll(listSort7);

            if (stopsaleList.size() > 0) {
                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.0")) {
                    result.add(
                            PicListResponse.Result.Colorlist.newBuilder()
                                    .setName("停售:")
                                    .setValue("")
                                    .setId(-2)
                    );
                    result.addAll(stopsaleList);
                } else {
                    result.add(
                            PicListResponse.Result.Colorlist.newBuilder()
                                    .setName("停售颜色")
                                    .setValue("")
                                    .setId(-1)
                                    .addAllSublist(stopsaleList.stream().map(x -> x.build()).collect(Collectors.toList()))
                    );
                }
            }

            return result.stream().map(x -> x.build()).collect(Collectors.toList());
        }).exceptionally(e -> {
            log.error("getColorList error", e);
            return new ArrayList<>();
        });
    }

    /**
     * 分组获取图片列表
     *
     * @param request
     * @return
     */
    public CompletableFuture<PageOf<PicListResponse.Result.PicGroupItem>> getGroupPicList(PicListRequest request, CompletableFuture<List<Integer>> seriesSixtyPicSpecIdsFuture, GroupCountDto groupCount) {
        int seriesId = request.getSeriesid();
        int colorId = request.getColorid();
        int categoryId = request.getCategoryid();
        int page = request.getPageindex();
        int size = request.getPagesize();
        int pm = request.getPm();
        int isinner = request.getIsinner();
        int outermixdetialpic = request.getOutermixdetialpic();  //外观下，命中实验，外观填充细节图片
        if (categoryId == 54 || categoryId == 55) {
            colorId = 0;
        }

        List<Integer> classIds = outermixdetialpic == 1
                ? Arrays.asList(1, 12)
                : categoryId > 0 ? Arrays.asList(categoryId) : new ArrayList<>();

        int outColor = isinner == 1 ? 0 : colorId;
        int innerColorId = isinner == 1 ? colorId : 0;

        CompletableFuture<Map<String, ConfigBaikeLinkDto>> baikeList = baikelinkforconfigComponent.getMap();
        CompletableFuture<Map<Integer, List<PicParamConfigEntity>>> picParamConfig = picParamConfigComponent.getMap(seriesId);
        CompletableFuture<List<ColorStatisticsDto>> colorFuture = colorComponent.getColors(Arrays.asList(3, 10).contains(request.getCategoryid()) ? 2 : 1, seriesId, 0, classIds);

        //获取图片列表
        return carPhotoComponent.getGroup(seriesId, classIds, outColor, innerColorId, page, size, 12, groupCount).thenCompose(groups -> {
            PageOf<PicListResponse.Result.PicGroupItem> result = new PageOf<>();
            if (groups == null || groups.getCount() == 0 || groups.getItems() == null || groups.getItems().size() == 0) {
                return CompletableFuture.completedFuture(result);
            }
            List<Integer> specIds = groups.getItems().stream().map(x -> x.getSpecId()).collect(Collectors.toList());
            List<Integer> dealerIds = new ArrayList<>();
            for (CarPhotoGroup item : groups.getItems()) {
                dealerIds.addAll(item.getItems().stream().map(x -> x.getDealerId()).collect(Collectors.toList()));
            }
            dealerIds = dealerIds.stream().distinct().collect(Collectors.toList());
            dealerIds.removeIf(x -> x == 0);
            //获取车型&经销商信息
            return specDetailComponent.getList(specIds).thenCombine(dealerComponent.getList(dealerIds), (specInfos, dealers) -> {
                Map<Integer, SpecDetailDto> specMap = specInfos == null || specInfos.size() == 0 ? new HashMap<>() : specInfos.stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x, (v1, v2) -> v2));
                Map<Integer, ListCshDealerByCityResult> dealerMap = dealers == null || dealers.size() == 0 ? new HashMap<>() : dealers.stream().collect(Collectors.toMap(x -> x.getDealerId(), x -> x));
                List<Integer> seriesSixtyPicSpecIds = seriesSixtyPicSpecIdsFuture.join();
                Map<String, ConfigBaikeLinkDto> baikemap = baikeList.join();
                Map<Integer, List<PicParamConfigEntity>> picParamConfigMap = picParamConfig.join();
                List<ColorStatisticsDto> colorList = colorFuture.join();
                for (CarPhotoGroup item : groups.getItems()) {
                    SpecDetailDto spec = specMap.containsKey(item.getSpecId()) ? specMap.get(item.getSpecId()) : null;
                    if (spec == null) {
                        continue;
                    }

                    PicListResponse.Result.PicGroupItem.Builder itemBuilder = PicListResponse.Result.PicGroupItem.newBuilder();
                    itemBuilder.setAutoshowid(item.getShowId());
                    itemBuilder.setSpecid(item.getSpecId());
                    itemBuilder.setSpecname(spec.getSpecName());
                    itemBuilder.setSalestate(spec.getState());
                    itemBuilder.setSalestatetip(SpecStateEnum.getNameByValueForPic(spec.getState()));
                    itemBuilder.setPiccount(item.getCount());
                    itemBuilder.setAllcolorcounttext(this.enableGetColorCount(item.getColorSet()) ? String.format("共%d个颜色",item.getColorSet().size()) : "");
                    itemBuilder.addAllImagelist(buildPicList(request.getPluginversion(), pm, seriesId, request.getSpecid(), categoryId, item.getItems(), specMap, dealerMap, seriesSixtyPicSpecIds, baikemap, picParamConfigMap, colorList));
                    result.getItems().add(itemBuilder.build());
                }
                result.setCount(groups.getCount());
                return result;
            });
        }).exceptionally(e -> {
            log.error("getGroupPicList error", e);
            return null;
        });
    }

    private boolean enableGetColorCount(Set<Integer> colorSet){
        if(CollectionUtils.isEmpty(colorSet)){
            return false;
        }
        //如果id集合中存在任意一个 id为0（id = 0 代表有图片未和颜色进行关联，那么不能在页面上显示 共多少颜色，以防止给用户提供错误信息）
        for(Integer colorId : colorSet){
            if(0 == colorId){
                return false;
            }
        }
        return true;
    }


    /**
     * 获取图片列表
     *
     * @return
     */
    public CompletableFuture<PageOf<PicListResponse.Result.Piclist>> getPicList(PicListRequest request,
                                                                                PicListResponse.Result.Builder resultBuilder,
                                                                                CompletableFuture<List<Integer>> seriesSixtyPicFuture,
                                                                                HqPicDataDto hqPicDataDto) {
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int colorId = request.getColorid();
        int categoryId = request.getCategoryid();
        int page = request.getPageindex();
        int size = request.getPagesize();
        int pm = request.getPm();
        int isinner = request.getIsinner();
        int outermixdetialpic = request.getOutermixdetialpic();  //外观下，命中实验，外观填充细节图片
        int sectab = request.getSectab();  //二级分类
        String pluginversion = request.getPluginversion();
        String piccolor1 = request.getPiccolor1();//图库缺失图片补全实验1
        String piccolor2 = request.getPiccolor2();//图库缺失图片补全实验2
        String piccolor3 = request.getPiccolor3();//图库缺失图片补全实验3

        if (categoryId == 54) {
            colorId = 0;
        }
        if (categoryId == 55) {
            colorId = 0;
            specId = 0;
        }

        List<Integer> classIds = (outermixdetialpic == 1 && null == hqPicDataDto) || (outermixdetialpic == 1 && !CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.3"))
                ? Arrays.asList(1, 12)
                : categoryId > 0 ? Arrays.asList(categoryId) : new ArrayList<>();

        int outColor = isinner == 1 ? 0 : colorId;
        int innerColorId = isinner == 1 ? colorId : 0;

        CompletableFuture<Map<String, ConfigBaikeLinkDto>> baikeList = baikelinkforconfigComponent.getMap();
        CompletableFuture<Map<Integer, List<PicParamConfigEntity>>> picParamConfig = picParamConfigComponent.getMap(seriesId);
        CompletableFuture<List<ColorStatisticsDto>> colorFuture = colorComponent.getColors(Arrays.asList(3, 10).contains(request.getCategoryid()) ? 2 : 1, seriesId, 0, classIds);

        if(request.getReqpicgroup() == 1){  //命中group实验的，需要修正客户端传过来的size
            size = 60;
        }

        int ctype = 0, cid = 0;
        if (StringUtils.isNotBlank(request.getMixid())) {
            try {
                String[] ctypeidstr = request.getMixid().split("_");
                if (ctypeidstr.length == 2) {
                    ctype = Integer.parseInt(ctypeidstr[0]);
                    cid = Integer.parseInt(ctypeidstr[1]);
                }
            } catch (Exception e) {
                log.error("拆分pic错误", e);
            }
        }

        //走高质图片实验
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")
                && Arrays.asList(1, 10, 3, 4).contains(request.getCategoryid())  //外观、中控、座椅、细节
                && Arrays.asList("C1", "C2", "D1", "D2").contains(request.getMegaab())
                && Objects.nonNull(hqPicDataDto)) {


            int type = request.getCategoryid();
            switch (categoryId) {
                case 10:
                    type = 2;
                    break;
            }

            return hqPhotoComponent.get(seriesId, specId, outColor, innerColorId, type, sectab, page, size, ctype, cid).thenComposeAsync(list -> {
                if (list == null) {
                    return CompletableFuture.completedFuture(new PageOf<PicListResponse.Result.Piclist>());
                }
                List<Integer> specIds = list.getItems().stream().map(x -> x.getSpecId()).distinct().collect(Collectors.toList());
                List<Integer> dealerIds = list.getItems().stream().map(x -> x.getDealerId()).distinct().collect(Collectors.toList());

                return specDetailComponent.getList(specIds).thenCombine(dealerComponent.getList(dealerIds), (specInfos, dealers) -> {
                    Map<Integer, SpecDetailDto> specMap = specInfos == null || specInfos.size() == 0 ? new HashMap<>() : specInfos.stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x));
                    Map<Integer, ListCshDealerByCityResult> dealerMap = dealers == null || dealers.size() == 0 ? new HashMap<>() : dealers.stream().collect(Collectors.toMap(x -> x.getDealerId(), x -> x));

                    List<PicListResponse.Result.Piclist> picList = buildHqPicList(pluginversion, pm, seriesId, request.getSpecid(), categoryId, list.getItems(), specMap, dealerMap, seriesSixtyPicFuture.join(), baikeList.join(), picParamConfig.join(), hqPicDataDto, colorFuture.join());
                    PageOf<PicListResponse.Result.Piclist> result = new PageOf<>();
                    result.setItems(picList);
                    result.setCount(list.getCount());
                    result.setPageIndex(list.getPageIndex());
                    return result;
                });
            }).exceptionally(e -> {
                log.error("hqPhotoComponent getPicList error", e);
                return null;
            });
        }


        AtomicBoolean isTest = new AtomicBoolean(false);
        List<Integer> filterSpecIds = new ArrayList<>();
        Map<Integer, List<Integer>> outColorMap = new HashMap<>();
        Map<Integer, List<Integer>> innerColorMap = new HashMap<>();
        if (CommonHelper.isTakeEffectVersion(pluginversion, "11.65.5") && Arrays.asList(1, 10, 3, 12).contains(categoryId)) {
            String json = "{\"piccolor3_a\":[{\"seriesId\":65,\"specInfos\":[{\"specId\":54011,\"outColors\":[3638],\"innerColors\":[]}]}],\"piccolor3_b\":[{\"seriesId\":65,\"specInfos\":[{\"specId\":64199,\"outColors\":[3241],\"innerColors\":[3914]}]}]}";
            json = series_spec_pic_filter;
            CarPicFilterInfoDto filterInfoDto = JsonUtil.toObject(json, CarPicFilterInfoDto.class);
            if (filterInfoDto != null) {
                CarPicFilterInfoDto.FilterBean filterBean = null;
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor1, "A") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor1_a())) {
                    filterBean = filterInfoDto.getPiccolor1_a().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor1, "B") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor1_b())) {
                    filterBean = filterInfoDto.getPiccolor1_b().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor2, "A") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor2_a())) {
                    filterBean = filterInfoDto.getPiccolor2_a().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor2, "B") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor2_b())) {
                    filterBean = filterInfoDto.getPiccolor2_b().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor3, "A") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor3_a())) {
                    filterBean = filterInfoDto.getPiccolor3_a().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean == null && StringUtils.equalsIgnoreCase(piccolor3, "B") && ListUtil.isNotEmpty(filterInfoDto.getPiccolor3_b())) {
                    filterBean = filterInfoDto.getPiccolor3_b().stream().filter(x -> x.getSeriesId() == seriesId).findFirst().orElse(null);
                }
                if (filterBean != null) {
                    int finalSpecId = specId;
                    if (specId > 0) {
                        filterBean.getSpecInfos().stream().filter(spec -> spec.getSpecId() == finalSpecId).findFirst().ifPresent(spec -> {
                            filterSpecIds.add(spec.getSpecId());
                            if (ListUtil.isNotEmpty(spec.getOutColors()) && Arrays.asList(1, 12).contains(categoryId)) {
                                outColorMap.put(spec.getSpecId(), spec.getOutColors());
                            }
                            if (ListUtil.isNotEmpty(spec.getInnerColors()) && Arrays.asList(3, 10).contains(categoryId)) {
                                innerColorMap.put(spec.getSpecId(), spec.getInnerColors());
                            }
                        });
                    } else {
                        filterBean.getSpecInfos().forEach(spec -> {
                            filterSpecIds.add(spec.getSpecId());
                            if (ListUtil.isNotEmpty(spec.getOutColors()) && Arrays.asList(1, 12).contains(categoryId)) {
                                outColorMap.put(spec.getSpecId(), spec.getOutColors());
                            }
                            if (ListUtil.isNotEmpty(spec.getInnerColors()) && Arrays.asList(3, 10).contains(categoryId)) {
                                innerColorMap.put(spec.getSpecId(), spec.getInnerColors());
                            }
                        });
                    }
                    isTest.set(true);
                }
            }
        }

        CompletableFuture<PageOf<CarPhotoDto>> carPhotoFuture = null;
        if (isTest.get()) {
            carPhotoFuture = carPhotoComponent.get(seriesId, specId, outColor, innerColorId, classIds, page, size, filterSpecIds, outColorMap, innerColorMap, ctype, cid);
        } else {
            carPhotoFuture = carPhotoComponent.get(seriesId, specId, outColor, innerColorId, classIds, page, size, ctype, cid);
        }


        return carPhotoFuture.thenComposeAsync(list -> {

            if (list == null) {
                return CompletableFuture.completedFuture(new PageOf<PicListResponse.Result.Piclist>());
            }
            List<Integer> specIds = list.getItems().stream().map(x -> x.getSpecId()).distinct().collect(Collectors.toList());
            List<Integer> dealerIds = list.getItems().stream().map(x -> x.getDealerId()).distinct().collect(Collectors.toList());

            return specDetailComponent.getList(specIds).thenCombine(dealerComponent.getList(dealerIds), (specInfos, dealers) -> {
                Map<Integer, SpecDetailDto> specMap = specInfos == null || specInfos.size() == 0 ? new HashMap<>() : specInfos.stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x));
                Map<Integer, ListCshDealerByCityResult> dealerMap = dealers == null || dealers.size() == 0 ? new HashMap<>() : dealers.stream().collect(Collectors.toMap(x -> x.getDealerId(), x -> x));

                List<PicListResponse.Result.Piclist> picList = buildPicList(pluginversion, pm, seriesId, request.getSpecid(), categoryId, list.getItems(), specMap, dealerMap, seriesSixtyPicFuture.join(), baikeList.join(), picParamConfig.join(), colorFuture.join());
                PageOf<PicListResponse.Result.Piclist> result = new PageOf<>();
                result.setItems(picList);
                result.setCount(list.getCount());
                result.setPageIndex(list.getPageIndex());
                return result;
            });
        }).exceptionally(e -> {
            log.error("getPicList error", e);
            return null;
        });
    }

    public CompletableFuture<PageOf<PicListResponse.Result.Piclist>> getPicListWithOtherSpec(PicListRequest request,  int specSaleStatus, CompletableFuture<List<Integer>> seriesSixtyPicFuture, SpecOutInnerColorDto specColorDto) {
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        int colorId = request.getColorid();
        int categoryId = request.getCategoryid();
        int page = request.getPageindex();
        int size = request.getPagesize();
        int pm = request.getPm();
        int isinner = CommonHelper.isInner(categoryId) ? 1 : 0;
        int outermixdetialpic = request.getOutermixdetialpic();  //外观下，命中实验，外观填充细节图片
        if (categoryId == 54) {
            colorId = 0;
        }
        if(categoryId==55){
            colorId = 0;
            specId = 0;
        }

        List<Integer> classIds = outermixdetialpic == 1
                ? Arrays.asList(1, 12)
                : categoryId > 0 ? Arrays.asList(categoryId) : new ArrayList<>();

        int outColor = isinner == 1 ? 0 : colorId;
        int innerColorId = isinner == 1 ? colorId : 0;

        List<Integer> otherColors = new ArrayList<>();
        otherColors.addAll(specColorDto.getColoritems().stream().map(SpecOutInnerColorDto.ColorItem::getId).toList());
        if (colorId > 0) {
            int finalColorId = colorId;
            otherColors.removeIf(e ->  e == finalColorId);
        }

        int ctype = 0, cid = 0;
        if (StringUtils.isNotBlank(request.getMixid())) {
            try {
                String[] ctypeidstr = request.getMixid().split("_");
                if (ctypeidstr.length == 2) {
                    ctype = Integer.parseInt(ctypeidstr[0]);
                    cid = Integer.parseInt(ctypeidstr[1]);
                }
            } catch (Exception e) {
                log.error("拆分pic错误", e);
            }
        }

        CompletableFuture<Map<String,ConfigBaikeLinkDto>> baikeList = baikelinkforconfigComponent.getMap();
        CompletableFuture<Map<Integer,List<PicParamConfigEntity>>> picParamConfig = picParamConfigComponent.getMap(seriesId);
        CompletableFuture<List<ColorStatisticsDto>> colorFuture = colorComponent.getColors(Arrays.asList(3, 10).contains(request.getCategoryid()) ? 2 : 1, seriesId, 0, classIds);


        return carPhotoComponent.getWithOtherSpec(seriesId, specId, specSaleStatus, outColor, innerColorId, isinner, otherColors, classIds, page, size, ctype, cid).thenComposeAsync(list -> {

            if (list == null) {
                return CompletableFuture.completedFuture(new PageOf<PicListResponse.Result.Piclist>());
            }
            List<Integer> specIds = list.getItems().stream().map(x -> x.getSpecId()).distinct().collect(Collectors.toList());
            List<Integer> dealerIds = list.getItems().stream().map(x -> x.getDealerId()).distinct().collect(Collectors.toList());

            return specDetailComponent.getList(specIds).thenCombine(dealerComponent.getList(dealerIds), (specInfos, dealers) -> {
                Map<Integer, SpecDetailDto> specMap = specInfos == null || specInfos.size() == 0 ? new HashMap<>() : specInfos.stream().collect(Collectors.toMap(x -> x.getSpecId(), x -> x));
                Map<Integer, ListCshDealerByCityResult> dealerMap = dealers == null || dealers.size() == 0 ? new HashMap<>() : dealers.stream().collect(Collectors.toMap(x -> x.getDealerId(), x -> x));

                List<PicListResponse.Result.Piclist> picList = buildPicList(request.getPluginversion(), pm, seriesId, request.getSpecid(), categoryId, list.getItems(), specMap, dealerMap, seriesSixtyPicFuture.join(),baikeList.join(),picParamConfig.join(), colorFuture.join());
                List<PicListResponse.Result.Piclist> targetPicList = new ArrayList<>();
                picList.forEach( e -> targetPicList.add(e.toBuilder().setRecommendbycolor(String.valueOf(request.getSpecid()).equals(e.getSpecid()) ? 0 : 1).build()));

                PageOf<PicListResponse.Result.Piclist> result = new PageOf<>();
                result.setItems(targetPicList);
                result.setCount(list.getCount());
                result.setPageIndex(list.getPageIndex());
                return result;
            });
        }).exceptionally(e -> {
            log.error("getPicList error", e);
            return null;
        });
    }

    List<PicListResponse.Result.Piclist> buildPicList(
            String version, int pm, int seriesId, int specId, int categoryId, List<CarPhotoDto> list,
            Map<Integer, SpecDetailDto> specMap,
            Map<Integer, ListCshDealerByCityResult> dealerMap,
            List<Integer> sixtyPicSpecIds,
            Map<String, ConfigBaikeLinkDto> baikeLinks,
            Map<Integer, List<PicParamConfigEntity>> picParamConfig,
            List<ColorStatisticsDto> colorList

    ) {
        Map<Integer, String> colorMap = new HashMap<>();
        if (CommonHelper.isTakeEffectVersion(version, "11.67.0") && !CollectionUtils.isEmpty(colorList)) {
            colorMap = colorList.stream().collect(Collectors.toMap(ColorStatisticsDto::getId, ColorStatisticsDto::getName,(k1,k2)->k1));
        }

        List<PicListResponse.Result.Piclist> result = new ArrayList<>();
        for (CarPhotoDto sourcePic : list) {
            if (!specMap.containsKey(sourcePic.getSpecId())) {
                continue;
            }
            String path = ImageUtils.getFullImagePathWithoutReplace(sourcePic.getFilepath());
            SpecDetailDto spec = specMap.get(sourcePic.getSpecId());
            ListCshDealerByCityResult dealer = dealerMap.containsKey(sourcePic.getDealerId()) ? dealerMap.get(sourcePic.getDealerId()) : null;
            PicListResponse.Result.Piclist.Builder newItem = PicListResponse.Result.Piclist.newBuilder();
            newItem.setDifconfiglinkurl("");
            newItem.setId(sourcePic.getPicId() + "");
            newItem.setMixid(1 + "_" + sourcePic.getPicId());
            newItem.setSpecid(sourcePic.getSpecId() + "");
            newItem.setSpecname(spec == null ? "" : spec.getSpecName());
            newItem.setLinkurl("autohome://car/specmain?specid=" + sourcePic.getSpecId());
            newItem.setTypeid(sourcePic.getPicClass());
            newItem.setNowebppic(HttpUtils.ToHttps(ChangeLogoSeriesImageSize(path, 14)));
            newItem.setShareurl(HttpUtils.ToHttps(String.format(shareUrlTemp, pm, seriesId, sourcePic.getSpecId(), categoryId, newItem.getId())));
            newItem.setBigpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 4))));
            newItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 16))));
            if (1 == usebigsize) {
                newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 15))));
            } else {
                newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 14))));
            }
            newItem.setOriginalpic("");
            if (!HttpUtils.ToHttp(path).equals(newItem.getHighpic().replace(".webp", ""))) {
                newItem.setOriginalpic(HttpUtils.ToHttp(path));
            }
            newItem.setHeight(sourcePic.getHeight());
            newItem.setWidth(sourcePic.getWidth());
            newItem.setDealerid(sourcePic.getDealerId());
            newItem.setDealername(dealer == null ? "" : dealer.getDealerName());
            newItem.setIswallpaper(sourcePic.getIsWallPaper());
            newItem.setOptiontips(sourcePic.getOptional() == 1 ? "此配置为选装，请以实车为准" : "");
            newItem.setAutoshowtitle(sourcePic.getShowName());
            int state = spec == null ? sourcePic.getSpecState() : spec.getState();
            newItem.setSalestate(state);
            newItem.setSalestatetip(SpecStateEnum.getNameByValueForPic(state));
            newItem.setCategoryid(sourcePic.getPicClass());
            if (categoryId == 54) {
                newItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 54))));
                newItem.setBigpic(path);
                newItem.setHighpic(path);
                newItem.setNowebppic(path);
                newItem.setOriginalpic("");
            }
            if (sixtyPicSpecIds.contains(sourcePic.getSpecId()) && sourcePic.getPointlocatinid() > 0) {
                newItem.setDifconfiglinkurl("autohome://rninsidebrowser?url=" + UrlUtil.encode("rn://Car_SeriesSummary/PictureContrast?seriesid=" + seriesId + "&panValid=0&specid=" + sourcePic.getSpecId() + "&locationid=" + SixtyPic.get(sourcePic.getPointlocatinid(), 0) + "&typeid=1&fromtype=2&isfirst=1"));
            }
            newItem.addAllConfiglist(getConfigItems(sourcePic.getPicId(), baikeLinks, picParamConfig));

            if (CommonHelper.isTakeEffectVersion(version, "11.67.0")) {
                newItem.setColorid(Arrays.asList(1, 12).contains(sourcePic.getPicClass()) ? sourcePic.getPicColorId() : sourcePic.getInnerColorId());
                newItem.setColorname(null != colorMap.get(newItem.getColorid()) ? colorMap.get(newItem.getColorid()) : "");
                if (specId > 0) {
                    newItem.setColortype(40 == sourcePic.getSpecState() ? 4 : (sourcePic.getSpecId() == specId ? 1 : 3));
                }else {
                    newItem.setColortype(40 == sourcePic.getSpecState() ? 4 : 1);
                }
            }
            result.add(newItem.build());
        }
        return result;
    }

    List<PicListResponse.Result.Piclist> buildHqPicList(
            String version, int pm, int seriesId, int specId, int categoryId, List<HqPhotoDto> list,
            Map<Integer, SpecDetailDto> specMap,
            Map<Integer, ListCshDealerByCityResult> dealerMap,
            List<Integer> sixtyPicSpecIds,
            Map<String, ConfigBaikeLinkDto> baikeLinks,
            Map<Integer, List<PicParamConfigEntity>> picParamConfig,
            HqPicDataDto hqPicDataDto,
            List<ColorStatisticsDto> colorList
    ) {
        Map<Integer, String> colorMap = new HashMap<>();
        if (CommonHelper.isTakeEffectVersion(version, "11.67.0") && !CollectionUtils.isEmpty(colorList)) {
            colorMap = colorList.stream().collect(Collectors.toMap(ColorStatisticsDto::getId, ColorStatisticsDto::getName,(k1,k2)->k1));
        }

        List<PicListResponse.Result.Piclist> result = new ArrayList<>();
        for (HqPhotoDto sourcePic : list) {
            if (!specMap.containsKey(sourcePic.getSpecId())) {
                continue;
            }
            SpecDetailDto spec = specMap.get(sourcePic.getSpecId());
            if (spec == null) {
                continue;
            }
            String path = ImageUtils.getFullImagePathWithoutReplace(sourcePic.getUrl());
            ListCshDealerByCityResult dealer = dealerMap.containsKey(sourcePic.getDealerId()) ? dealerMap.get(sourcePic.getDealerId()) : null;
            PicListResponse.Result.Piclist.Builder newItem = PicListResponse.Result.Piclist.newBuilder();
            newItem.setDifconfiglinkurl("");
            newItem.setId(sourcePic.getId() + "");
            newItem.setMixid(sourcePic.getType() + "_" + sourcePic.getId());
            newItem.setSpecid(sourcePic.getSpecId() + "");
            newItem.setSpecname(spec.getSpecName());
            newItem.setLinkurl("autohome://car/specmain?specid=" + sourcePic.getSpecId());
            newItem.setTypeid(sourcePic.getTypeId());
            newItem.setNowebppic(HttpUtils.ToHttps(ChangeLogoSeriesImageSize(path, 14)));
            newItem.setShareurl(HttpUtils.ToHttps(String.format(shareUrlTemp, pm, seriesId, sourcePic.getSpecId(), categoryId, newItem.getId())));
            newItem.setBigpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 4))));
            newItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 16))));
            if (1 == usebigsize) {
                newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 15))));
            } else {
                newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 14))));
            }
            newItem.setOriginalpic("");
            if (!HttpUtils.ToHttp(path).equals(newItem.getHighpic().replace(".webp", ""))) {
                newItem.setOriginalpic(HttpUtils.ToHttp(path));
            }
            newItem.setHeight(sourcePic.getHeight());
            newItem.setWidth(sourcePic.getWidth());
            newItem.setDealerid(sourcePic.getDealerId());
            newItem.setDealername(dealer == null ? "" : dealer.getDealerName());
            newItem.setIswallpaper(sourcePic.getIsWallPaper());
            newItem.setOptiontips(sourcePic.getOptional() == 1 ? "此配置为选装，请以实车为准" : "");
            newItem.setAutoshowtitle(sourcePic.getShowName());
            int state = spec.getState();
            newItem.setSalestate(state);
            newItem.setSalestatetip(SpecStateEnum.getNameByValueForPic(state));
            newItem.setCategoryid(sourcePic.getTypeId());
            newItem.setIshqpic(sourcePic.getIshqpic());
            newItem.setPointname(Objects.nonNull(sourcePic.getPointName()) ? sourcePic.getPointName() : "");
            if (categoryId == 54) {
                newItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 54))));
                newItem.setBigpic(path);
                newItem.setHighpic(path);
                newItem.setNowebppic(path);
                newItem.setOriginalpic("");
            }
            if (sixtyPicSpecIds.contains(sourcePic.getSpecId()) && sourcePic.getPointlocatinid() > 0) {
                newItem.setDifconfiglinkurl("autohome://rninsidebrowser?url=" + UrlUtil.encode("rn://Car_SeriesSummary/PictureContrast?seriesid=" + seriesId + "&panValid=0&specid=" + sourcePic.getSpecId() + "&locationid=" + SixtyPic.get(sourcePic.getPointlocatinid(), 0) + "&typeid=1&fromtype=2&isfirst=1"));
            }
            newItem.addAllConfiglist(getConfigItems(sourcePic.getId(), baikeLinks, picParamConfig));

            switch (sourcePic.getType()) {
                case 1:  //新图
                    newItem.setMediatype(0);
                    break;
                case 10: //老图
                    newItem.setMediatype(0);
                    break;
                case 2:  //视频
                    newItem.setMediatype(1);
                    newItem.setVid(sourcePic.getMid());
                    break;
            }

            newItem.setSecondtabid(sourcePic.getSubTypeId());
            if (sourcePic.getSubTypeId() != 0 && !CollectionUtils.isEmpty(hqPicDataDto.getPhotoSubTypeMap())) {
                List<HqPicDataDto.PhotoSubTypeDto> subTypeDtoList = hqPicDataDto.getPhotoSubTypeMap()
                        .get(sourcePic.getTypeId());
                if (!CollectionUtils.isEmpty(subTypeDtoList)) {
                    subTypeDtoList.stream()
                            .filter(e -> e.getSubTypeId() == sourcePic.getSubTypeId())
                            .findFirst().ifPresent(photoSubTypeDto -> newItem.setSecondtabname(photoSubTypeDto.getSubTypeName()));
                }
            } else {
                newItem.setSecondtabname("");
            }

            if (CommonHelper.isTakeEffectVersion(version, "11.67.0")) {
                newItem.setColorid(Arrays.asList(1, 12).contains(sourcePic.getTypeId()) ? sourcePic.getOutColor() : sourcePic.getInnerColor());
                newItem.setColorname(null != colorMap.get(newItem.getColorid()) ? colorMap.get(newItem.getColorid()) : "");
                if (specId > 0) {
                    newItem.setColortype(40 == sourcePic.getSpecState() ? 4 : (sourcePic.getSpecId() == specId ? 1 : 3));
                }else{
                    newItem.setColortype(40 == sourcePic.getSpecState() ? 4 : 1);
                }
            }

            result.add(newItem.build());
        }
        return result;
    }


    public List<PicListResponse.Result.Piclist.ConfigItem> getConfigItems(
            int picId,
            Map<String, ConfigBaikeLinkDto> baikeLinks,
            Map<Integer, List<PicParamConfigEntity>> picParamConfig
    ) {
        if (!picParamConfig.containsKey(picId)) {
            return new ArrayList<>();
        }
        List<PicListResponse.Result.Piclist.ConfigItem> list = new ArrayList<>();
        List<PicParamConfigEntity> d1L = picParamConfig.get(picId).stream().filter(x -> x.getDataType() == 1 && StringUtils.isNotBlank(x.getValue())).collect(Collectors.toList());
        for (PicParamConfigEntity config : d1L) {
            PicListResponse.Result.Piclist.ConfigItem.Builder b = PicListResponse.Result.Piclist.ConfigItem.newBuilder();
            b.setName(config.getName());
            b.setValue(config.getValue());
            b.setTypeid(1);
            b.setTitle(b.getName() + "：" + b.getValue());
            if (baikeLinks.containsKey(b.getName())) {
                ConfigBaikeLinkDto linkDto = baikeLinks.get(b.getName());
                if (linkDto != null) {
                    b.setDesc(linkDto.getFirstpartcnt());
                    b.setScheme(CommonHelper.getInsideBrowerSchemeWK("https://cars.app.autohome.com.cn/carcfg/view/carconfiginfo?id=" + linkDto.getId() + "&pm=1&pluginversion=11.34.0"));
                    if (StringUtils.isNotBlank(linkDto.getVideocover())) {
                        b.setImg(linkDto.getVideocover());
                    } else if (StringUtils.isNotBlank(linkDto.getRepImg())) {
                        b.setImg(linkDto.getRepImg());
                    }
                }
            }
            list.add(b.build());
        }

        Map<String, List<PicParamConfigEntity>> d2L = picParamConfig.get(picId).stream().filter(x -> x.getDataType() == 2 && StringUtils.isNotBlank(x.getName())).collect(Collectors.groupingBy(x -> x.getName()));
        d2L.forEach((k, v) -> {
            PicListResponse.Result.Piclist.ConfigItem.Builder b = PicListResponse.Result.Piclist.ConfigItem.newBuilder();
            b.setName(k);
            b.setTypeid(2);

            List<String> vvs = v == null || v.size() == 0 ? new ArrayList<>() : v.stream().filter(x -> StringUtils.isNotBlank(x.getSubName())).map(x -> x.getSubName()).distinct().collect(Collectors.toList());
            if (vvs.size() == 0) {
                b.setValue("标配");
            } else {
                b.setValue(String.join("/", vvs));
            }
            b.setTitle(b.getName() + "：" + b.getValue());
            if (baikeLinks.containsKey(b.getName())) {
                ConfigBaikeLinkDto linkDto = baikeLinks.get(b.getName());
                if (linkDto != null) {
                    b.setDesc(linkDto.getFirstpartcnt());
                    b.setScheme(CommonHelper.getInsideBrowerSchemeWK("https://cars.app.autohome.com.cn/carcfg/view/carconfiginfo?id=" + linkDto.getId() + "&pm=1&pluginversion=11.34.0"));
                    if (StringUtils.isNotBlank(linkDto.getVideocover())) {
                        b.setImg(linkDto.getVideocover());
                    } else if (StringUtils.isNotBlank(linkDto.getRepImg())) {
                        b.setImg(linkDto.getRepImg());
                    }
                }
            }
            list.add(b.build());
        });
        return list;
    }


    private String ChangeLogoSeriesImageSize(String imageUrl, int imageIndex) {
        if (StringUtils.isBlank(imageUrl) || StringUtils.endsWithIgnoreCase(imageUrl, ".webp") || StringUtils.endsWithIgnoreCase(imageUrl, ".gif")) {
            return imageUrl;
        }
        int index = imageUrl.lastIndexOf("/");
        if (imageUrl.indexOf("autohomecar") > -1) {
            return imageUrl.substring(0, index + 1) + getCarLogoSizePrefix().get(imageIndex) + imageUrl.substring(index + 1);
        } else if (imageIndex == 16) {
            return imageUrl.substring(0, index + 1) + "400x300_q100_autohomecar__" + imageUrl.substring(index + 1);
        }
        return imageUrl;
    }

    static Map<Integer, String> CarLogoSizePrefix;

    private Map<Integer, String> getCarLogoSizePrefix() {
        if (CarLogoSizePrefix == null) {
            initCarLogoSizePrefix();
        }
        return CarLogoSizePrefix;
    }

    synchronized void initCarLogoSizePrefix() {
        if (CarLogoSizePrefix != null) {
            return;
        }
        CarLogoSizePrefix = new HashMap<>();
        CarLogoSizePrefix.put(0, "");
        CarLogoSizePrefix.put(1, "u_");
        CarLogoSizePrefix.put(2, "ys_");
        CarLogoSizePrefix.put(3, "cw_");
        CarLogoSizePrefix.put(4, "500x0_1_");
        CarLogoSizePrefix.put(5, "k_");
        CarLogoSizePrefix.put(6, "cp_");
        CarLogoSizePrefix.put(7, "tp_");
        CarLogoSizePrefix.put(8, "240x180_");
        CarLogoSizePrefix.put(9, "m_");
        CarLogoSizePrefix.put(10, "s_");
        CarLogoSizePrefix.put(11, "l_");
        CarLogoSizePrefix.put(12, "400x300_");
        CarLogoSizePrefix.put(13, "600x450_");
        CarLogoSizePrefix.put(14, "1400x1050_");
        CarLogoSizePrefix.put(15, "2000x1500_");
        CarLogoSizePrefix.put(16, "400x300_q100_");
        CarLogoSizePrefix.put(54, "240x0_0_q87_");
    }


    /**
     * 外观全屏、内饰半屏、内饰全屏
     *
     * @param specVrFuture
     * @param seriesVrFuture
     * @param hqPicDataDto
     * @param request
     * @return
     */
    public CompletableFuture<PicListResponse.Result.H5vrinfo.Builder> buildH5VrInfo(CompletableFuture<SpecVrInfoDto> specVrFuture,
                                                                                    CompletableFuture<SeriesVr> seriesVrFuture,
                                                                                    HqPicDataDto hqPicDataDto, PicListRequest request) {
        PicListResponse.Result.H5vrinfo.Builder h5VrInfoBuilder = PicListResponse.Result.H5vrinfo.newBuilder();

        return CompletableFuture.allOf(specVrFuture, seriesVrFuture).thenApply(x -> {
            SeriesVr.VrH5Info h5VrInfo = new SeriesVr.VrH5Info();
            if (request.getSpecid() > 0) {
                SpecVrInfoDto specVr = specVrFuture.join();
                if ((specVr == null) || (!specVr.isHasExterior() && specVr.getExtInfo().isEmpty() && specVr.getIntInfo().isEmpty())) {
                    return h5VrInfoBuilder;
                }
                if (!specVr.getIntInfo().isEmpty()) {
                    h5VrInfo.setHasInterior(StringUtils.isNotEmpty(specVr.getIntInfo().get(0).getShowUrl()));
                    h5VrInfo.getIntInfo().setSpecId(request.getSpecid());
                    h5VrInfo.getIntInfo().setCoverUrl(specVr.getIntInfo().get(0).getCoverUrl());
                    h5VrInfo.getIntInfo().setShowUrl(specVr.getIntInfo().get(0).getShowUrl());
                    h5VrInfo.getIntInfo().setIs_show(specVr.getIntInfo().get(0).getIs_show());
                    h5VrInfo.getIntInfo().setNarration(specVr.getIntInfo().get(0).getNarration());
                }
                if (!specVr.getExtInfo().isEmpty()) {
                    h5VrInfo.setHasExterior(specVr.isHasExterior());
                    h5VrInfo.getExtInfo().setSpecId(request.getSpecid());
                    h5VrInfo.getExtInfo().setCoverUrl(specVr.getExtInfo().get(0).getCoverUrl());
                    h5VrInfo.getExtInfo().setIs_show(specVr.getExtInfo().get(0).getIs_show());
                    h5VrInfo.getExtInfo().setNarration(specVr.getExtInfo().get(0).getNarration());
                }
            } else {
                SeriesVr seriesVr = seriesVrFuture.join();
                if (seriesVr == null) {
                    return h5VrInfoBuilder;
                }
                h5VrInfo = seriesVr.getH5Vr();
            }

            String isVoice = "";
            String vrsource = request.getSpecid() > 0 ? "&pagesrc=spec_img" : "&pagesrc=series_img";
            String colorStr = "";
            if (request.getColorid() > 0) {
                colorStr = "&colorid=" + request.getColorid();
            }

            String firstImg = "";
            //外观VR
            if (request.getCategoryid() == 1 && h5VrInfo != null && h5VrInfo.isHasExterior() && h5VrInfo.getExtInfo().getIs_show()) {
                int specId = h5VrInfo.getExtInfo().getSpecId();
                if (h5VrInfo.getExtInfo().getNarration() != null && h5VrInfo.getExtInfo().getNarration() == 1) {
                    isVoice = "&guide=1";
                }
                String extUrl = String.format("https://pano.autohome.com.cn/car/ext/%s?_ahrotate=%s&clicktype=1&noshare=1%s&appversion=%s%s&ipadtile=1%s&landscapefullscreen=1", specId, request.getPm(), colorStr, request.getPluginversion(), vrsource, isVoice);
                String encodeextUrl = UrlUtil.encode(extUrl);
                h5VrInfoBuilder.setSchemashowurl("autohome://insidebrowserwk?navigationbarstyle=2&url=" + encodeextUrl);
                h5VrInfoBuilder.setLiteurl("https://pano.autohome.com.cn/car/extseries/" + specId
                        + "?spin=1&progress=0&bg=1&pagesrc=series_img&noswitch=1&clicktype=1" + colorStr
                        + "&appversion=" + request.getPluginversion());// &color="+colorvalue
                h5VrInfoBuilder.setShowurl(extUrl);
                firstImg = h5VrInfo.getExtInfo().getCoverUrl();
            }
            //内饰VR
            else if (request.getCategoryid() == 10 && h5VrInfo != null && h5VrInfo.isHasInterior() && StringUtils.isNotEmpty(h5VrInfo.getIntInfo().getShowUrl()) && h5VrInfo.getIntInfo().getIs_show()) {
                int vrInfoSpecId = h5VrInfo.getIntInfo().getSpecId();
                if (h5VrInfo.getIntInfo().getNarration() != null && h5VrInfo.getIntInfo().getNarration() == 1) {
                    isVoice = "&guide=1";
                }
                String prefix = "https://pano.autohome.com.cn/car/panoseries/";
                String suffix = "?spin=1&progress=0&noswitch=1&bg=1&pagesrc=series_img&clicktype=1" + colorStr + vrsource + "&appversion=" + request.getPluginversion();
                String liteUrl = prefix + vrInfoSpecId + suffix;

                String showUrl = h5VrInfo.getIntInfo().getShowUrl();
                if (!showUrl.contains("?")) {
                    showUrl += "?_ahrotate=1";
                }
                h5VrInfoBuilder.setSchemashowurl("autohome://insidebrowserwk?navigationbarstyle=2&url="
                        + UrlUtil.encode(showUrl
                        + colorStr + vrsource + "&ipadtile=1"));
                h5VrInfoBuilder.setLiteurl(liteUrl);//
                h5VrInfoBuilder.setShowurl(showUrl + "&clicktype=1&noshare=1"
                        + colorStr + vrsource + "&appversion=" + request.getPluginversion() + isVoice);
                firstImg = h5VrInfo.getIntInfo().getCoverUrl();
            }

            //高质量图车系 赋值 megainfo
            if (Objects.nonNull(hqPicDataDto) && StringUtils.isNotEmpty(firstImg)) {
                try {
                    if (StringUtils.isNotEmpty(firstImg)) {
                        PicListResponse.Result.MegaInfo.Builder megaInfo = PicListResponse.Result.MegaInfo.newBuilder();
                        megaInfo.setName("VR");
                        megaInfo.setImgurl(firstImg);
                        megaInfo.setPvitem(Pvitem.newBuilder()
                                .putArgvs("botton_name", "VR")
                                .putArgvs("objectid", request.getCategoryid()+"")
                                .putArgvs("seriesid", hqPicDataDto.getSeriesId() + "")
                                .putArgvs("specid", h5VrInfo.getExtInfo().getSpecId() + "")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));
                        h5VrInfoBuilder.setMegainfo(megaInfo);
                    }
                } catch (Exception e) {
                    log.warn("高质量图设置 mega信息异常", e);
                }
            }

            return h5VrInfoBuilder;
        }).exceptionally(e -> {
            log.error("buildH5VrInfo error", e);
            return h5VrInfoBuilder;
        });
    }

    /**
     * 外观小屏vr
     *
     * @param specVrFuture
     * @param seriesVrFuture
     * @param hqPicDataDto
     * @param request
     * @return
     */
    public CompletableFuture<PicListResponse.Result.Vrmaterial.Builder> buildVrMaterial(CompletableFuture<SpecVrInfoDto> specVrFuture,
                                                                                        CompletableFuture<SeriesVr> seriesVrFuture,
                                                                                        SeriesDetailDto seriesDetailDto,
                                                                                        SpecDetailDto specDetail,
                                                                                        HqPicDataDto hqPicDataDto,
                                                                                        PicListResponse.Result.Builder resultBuilder,
                                                                                        PicListRequest request) {
        CompletableFuture<List<SeriesVrPointDto>> seriesVrPointFuture = seriesVrPointComponent.get(request.getSeriesid());
        CompletableFuture<SeriesRemodel3DDto> seriesRemodel3DFuture = seriesRemodel3DComponent.get(request.getSeriesid());
        AtomicReference<PicListResponse.Result.Vrmaterial.Builder> vrMaterialBuilder = new AtomicReference<>(PicListResponse.Result.Vrmaterial.newBuilder());

        return CompletableFuture.allOf(specVrFuture, seriesVrFuture, seriesVrPointFuture, seriesRemodel3DFuture).thenApply(x -> {
            SeriesVr seriesVr = new SeriesVr();
            List<SeriesVrPointDto> vrPointList = new ArrayList<>();
            if (request.getSpecid() > 0) {
                SpecVrInfoDto specVr = specVrFuture.join();
                if (specVr != null) {
                    seriesVr.setVrMaterial(specVr.getVrMaterial());
                }
            } else {
                seriesVr = seriesVrFuture.join();
                vrPointList = seriesVrPointFuture.join();
            }

            PicListResponse.Result.Vrmaterial.Builder vrMaterial = getVrMaterial(request,seriesVr, vrPointList, seriesDetailDto, hqPicDataDto, request.getPm(), request.getPluginversion());
            vrMaterialBuilder.set(vrMaterial);

            //车型状态设置
            SpecDetailDto specDetailDto;
            if (request.getSpecid() == vrMaterial.getSpecId()) {
                specDetailDto = specDetail;
            } else {
                specDetailDto = specDetailComponent.get(vrMaterial.getSpecId()).join();
            }
            if (specDetailDto != null) {
                vrMaterial.setSalestate(specDetailDto.getState());
                vrMaterial.setSalestatetip(SpecStateEnum.getNameByValueForPic(specDetailDto.getState()));
                vrMaterial.setSpecname(specDetailDto.getSpecName());
            }

            SeriesRemodel3DDto seriesRemodel3DDto = seriesRemodel3DFuture.join();
            if (!vrMaterial.getColorListList().isEmpty() && seriesRemodel3DDto != null && StringUtils.isNotEmpty(seriesRemodel3DDto.getJumpPaintingUrl())) {
                resultBuilder.setRefitinfo(PicListResponse.Result.Refitinfo.newBuilder()
                        .setIconurl(CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")
                                ? "http://nfiles3.autohome.com.cn/zrjcpk10/piclist_refit_240820.webp"
                                : "https://files3.autoimg.cn/zrjcpk10/piclist_refit_20231025.webp")
                        .setLinkurl(seriesRemodel3DDto.getJumpPaintingUrl().replace("6854956", "6855858")));
            }
            if (vrMaterial.getIs3Dpk() == 1) {
                resultBuilder.setTdcarinfo(PicListResponse.Result.Tdcarinfo.newBuilder()
                        .setIconurl(CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")
                                ? "http://nfiles3.autohome.com.cn/zrjcpk10/piclist_3dt_240821.webp"
                                : "https://files3.autoimg.cn/zrjcpk10/piclist_3dt_20231025.webp")
                        .setLinkurl(vrMaterial.getJumpUrl()));
            }
            return vrMaterialBuilder.get();
        }).exceptionally(e -> {
            log.error("buildVrMaterial error", e);
            return vrMaterialBuilder.get();
        });
    }

    /**
     * 改装vr
     *
     * @param request
     * @return
     */
    public CompletableFuture<PicListResponse.Result.Vrmaterial.Builder> buildRefitVrMaterial(PicListRequest request) {
        PicListResponse.Result.Vrmaterial.Builder vrMaterialBuilder = PicListResponse.Result.Vrmaterial.newBuilder();

        return seriesRemodel3DComponent.get(request.getSeriesid()).thenApply(x -> {
            if (x == null) {
                return vrMaterialBuilder;
            }
            vrMaterialBuilder.setSuperspeclinkurl(x.getJumpProjectUrl());
            vrMaterialBuilder.setTaglabel("3D改装模拟");
            vrMaterialBuilder.setVrinfoBackgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/car_series_refit_240726.png.webp");

            PicListResponse.Result.Vrmaterial.ColorList.Hori.Builder hori = PicListResponse.Result.Vrmaterial.ColorList.Hori.newBuilder();
            AtomicReference<Integer> normalIndex = new AtomicReference<>(0);
            AtomicReference<Integer> previewIndex = new AtomicReference<>(0);
            x.getImages().forEach(img -> {
                PicListResponse.Result.Vrmaterial.ColorList.Normal.Builder normal = PicListResponse.Result.Vrmaterial.ColorList.Normal.newBuilder();
                normal.setSeq(normalIndex.getAndSet(normalIndex.get() + 1));
                if (request.getPm() == 2) {
                    normal.setUrl(ImageUtils.convertImage_Size(img, ImageSizeEnum.ImgSizeVR_4x3_640x0));
                } else {
                    normal.setUrl(img);
                }
                hori.addNormal(normal.build());
            });
            x.getSmallImages().forEach(img -> {
                PicListResponse.Result.Vrmaterial.ColorList.Preview.Builder preview = PicListResponse.Result.Vrmaterial.ColorList.Preview.newBuilder();
                preview.setSeq(previewIndex.getAndSet(previewIndex.get() + 1));
                preview.setUrl(img);
                hori.addPreview(preview.build());
            });
            vrMaterialBuilder.addColorList(PicListResponse.Result.Vrmaterial.ColorList.newBuilder().setId(888888).setHori(hori).build());
            return vrMaterialBuilder;
        }).exceptionally(e -> {
            log.error("buildRefitVrMaterial error", e);
            return null;
        });
    }

    /**
     * 全景VR
     *
     * @param seriesVrFuture
     * @param request
     * @return
     */
    public CompletableFuture<PicListResponse.Result.Vr.Builder> buildVr(CompletableFuture<SeriesVr> seriesVrFuture,
                                                                        PicListRequest request) {
        PicListResponse.Result.Vr.Builder vrBuilder = PicListResponse.Result.Vr.newBuilder();
        return seriesVrFuture.thenApply(seriesVr -> {
            if (seriesVr == null || seriesVr.getRealScene() == null) {
                return vrBuilder;
            }

            vrBuilder.setCover(seriesVr.getRealScene().getCover_url());
            vrBuilder.setScheme("autohome://insidebrowserwk?navigationbarstyle=2&url=" + UrlUtil.encode(seriesVr.getRealScene().getShow_url() + "?_ahrotate=0&landscapefullscreen=1&fullScreen=true") + "&disable_back=1");
            vrBuilder.setImage(seriesVr.getRealScene().getScene_url());
            vrBuilder.setGuideicon("http://nfiles3.autohome.com.cn/zrjcpk10/panorama.json");
            vrBuilder.setPvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "").putArgvs("specid", request.getSpecid() + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_vr_click").build())
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_vr_show").build()).build());

            return vrBuilder;
        }).exceptionally(e -> {
            log.error("get series vr error", e);
            return null;
        });
    }

    private PicListResponse.Result.Vrmaterial.Builder getVrMaterial(PicListRequest request, SeriesVr seriesVr, List<SeriesVrPointDto> vrPointList, SeriesDetailDto seriesDetailDto, HqPicDataDto hqPicDataDto, int pm, String pluginversion) {
        //先取VR外观，在判断是否有超级车型库、3dpk数据，存在更新vr外观的链接和显示标识
        //优先级：超级车型库》3kpk》vr外观

        PicListResponse.Result.Vrmaterial.Builder vrMaterial = PicListResponse.Result.Vrmaterial.newBuilder();
        if (seriesVr == null) {
            return vrMaterial;
        }
        SeriesVrExteriorResult vrInfo = seriesVr.getVrMaterial();
        //vr外观有值，加工处理字段
        if (vrInfo != null && vrInfo.getColor_list() != null && vrInfo.getColor_list().size() > 0) {
            if (pm == 1 && StringUtils.isNotEmpty(vrInfo.getAdurl_ios())) {
                vrInfo.setSuperspeclinkurl("autohome://insidebrowserwk?navigationbarstyle=2&url="
                        + URLEncoder.encode(vrInfo.getAdurl_ios() + "&ipadtile=1"));
            } else if (pm == 2 && StringUtils.isNotEmpty(vrInfo.getAdurl_android())) {
                vrInfo.setSuperspeclinkurl("autohome://insidebrowserwk?navigationbarstyle=2&url="
                        + URLEncoder.encode(vrInfo.getAdurl_android() + "&ipadtile=1"));
            }
            vrInfo.setAdurl_ios(null);
            vrInfo.setAdurl_android(null);

            if (StringUtils.isNotEmpty(vrInfo.getJump_url())) {
                vrInfo.setJump_url(vrInfo.getJump_url()
                        + "?clicktype=1&noext=1&btnoffsety=15&appversion=" + pluginversion);
            }

            vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/vrbgimg_default_0324.png.webp");

            //图片大小处理：区分端、http前缀
            try {
                vrInfo.getColor_list().forEach(i -> {
                    i.getHori().getNormal().forEach(j -> {
                        j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                        if (pm == 1) {
                            j.setUrl(StringUtils.replace(j.getUrl(), "/900x0_autohomecar", "/1500x0_autohomecar"));
                        }
                        if (pm == 2) {
                            j.setUrl(StringUtils.replace(j.getUrl(), "/900x0_autohomecar", "/900x600_k1_autohomecar"));
                        }
                    });
                    i.getHori().getPreview().forEach(j -> {
                        j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                    });
                    i.getOver().getNormal().forEach(j -> {
                        j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                    });
                    i.getOver().getPreview().forEach(j -> {
                        j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                    });
                });
            } catch (Exception e) {
                log.error("vr 切图异常", e);
            }
        } else {
            vrInfo = new SeriesVrExteriorResult();
        }

        AtomicInteger ifSuperGarageCar = new AtomicInteger(1);
        AtomicReference<String> vr_orgiurl = new AtomicReference<>("");
        //超级车型库：ifSuperGarageCar 取值
        //  1：品牌展馆
        //  2：超级车型库1.0
        //  3：超级车型库2.0
        if (vrInfo.getColor_list() != null && vrInfo.getColor_list().size() > 0) {
            List<SeriesVr.VrSuperCar> seriesIndexVrList = seriesVr.getSuperCarList().stream().filter(p -> p.getPosition().equalsIgnoreCase("series_index") && p.getTerminal() == pm).toList();
            seriesIndexVrList.forEach(p -> {
                vr_orgiurl.set(p.getUrl());
                if (p.getExhibitionType() == 6) {
                    ifSuperGarageCar.set(2);
                } else if (p.getExhibitionType() == 8 || p.getExhibitionType() == 22) {
                    ifSuperGarageCar.set(3);
                }
            });
        }

        //标记是否有效，超级车型库
        if (StringUtils.isNotEmpty(vr_orgiurl.get())) {
            vrInfo.setIssuperspeclinkurl(true);
            vrInfo.setSuperspeclinkurl(vr_orgiurl.get());
        }

        if (ifSuperGarageCar.get() == 2) {
            vrInfo.setShowtype(2);
            vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/vrbgimg_super_v1_0324.png.webp");
            vrInfo.setIscloud(0);
        } else if (ifSuperGarageCar.get() == 3) {
            vrInfo.setIscloud(0);
            vrInfo.setShowtype(3);
            vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/vrbgimg_super_v2_0324.png");
        } else {
            //是否有3Dpk数据
            Optional<SeriesVr.VrSuperCar> series3dpk = seriesVr.getSuperCarList().stream().filter(p -> p.getTerminal() == pm && p.getPosition().equalsIgnoreCase("series_3dpk")).findFirst();
            if (series3dpk.isPresent()) {
                vrInfo.setIs3dpk(1);
                vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/vrbgimg_super_v2_0324.png.webp");
                vrInfo.setJump_url(CommonHelper.getInsideBrowerSchemeWK(UrlUtil.getUrlParamValue(series3dpk.get().getUrl(), "url") + "&navigationbarstyle=2&disable_back=1&ipadtile=1"));
            }

            if (vrInfo.getShowtype() == 1) {
                vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/vrbgimg_default_0324.png.webp");
            }
        }

        //20、30车系才返回
        if (seriesDetailDto.getState() == 20 || seriesDetailDto.getState() == 30) {
            SeriesVrExteriorResult finalVrInfo = vrInfo;
            if (vrPointList != null && finalVrInfo.getColor_list() != null) {
                vrPointList.forEach(vrPoint -> {
                    finalVrInfo.getColor_list().forEach(color -> {
                        int index = Integer.parseInt(vrPoint.getFrameids());
                        if (index >= color.getHori().getNormal().size()) {
                            return;
                        }
                        SeriesVrExteriorResult.PicMaterial picMaterial = color.getHori().getNormal().get(index);
                        SeriesVrExteriorResult.PointInfo pointInfo = new SeriesVrExteriorResult.PointInfo();
                        pointInfo.setLocationid(vrPoint.getClassid());
                        pointInfo.setName(vrPoint.getName());
                        String rnurl = "rn://Car_SeriesSummary/PictureContrast?seriesid=" + seriesDetailDto.getId() + "&locationid=" + vrPoint.getClassid() + "&panValid=0&specid=0&typeid=1&fromtype=1&isfirst=1&seriesname=" + UrlUtil.encode(seriesDetailDto.getName());
                        pointInfo.setLinkurl("autohome://rninsidebrowser?url=" + UrlUtil.encode(rnurl));
                        picMaterial.getPointinfo().add(pointInfo);
                    });
                });
            }
        }

        //组件对象转换外展对象
        vrMaterial.setSpecId(vrInfo.getSpecId())
                .setShowtype(vrInfo.getShowtype())
                .setIscloud(vrInfo.getIscloud())
                .setIssuperspeclinkurl(vrInfo.isIssuperspeclinkurl())
                .setIs3Dpk(vrInfo.getIs3dpk())
                .setJumpUrl(vrInfo.getJump_url())
                .setNarration(vrInfo.getNarration())
                .setSpecState(vrInfo.getSpecState())
                .setVrspecstate(vrInfo.getVrspecstate())
                .setSuperspeclinkurl(StringUtils.isNotEmpty(vrInfo.getSuperspeclinkurl()) ? vrInfo.getSuperspeclinkurl() : "")
                .setVrinfoBackgroudImg(vrInfo.getVrinfo_backgroudImg());
        if (vrInfo.getColor_list() != null) {
            vrInfo.getColor_list().forEach(color -> {
                PicListResponse.Result.Vrmaterial.ColorList.Builder colorItem = PicListResponse.Result.Vrmaterial.ColorList.newBuilder();
                colorItem.setId(color.getId())
                        .setColorId(color.getColorId())
                        .setBaseColorName(color.getBaseColorName())
                        .setColorName(color.getColorName())
                        .setColorNames(color.getColorNames())
                        .setColorValue(color.getColorValue())
                        .setColorValues(color.getColorValues())
                        .setRemoteColorId(color.getRemoteColorId());

                PicListResponse.Result.Vrmaterial.ColorList.Over.Builder overItem = PicListResponse.Result.Vrmaterial.ColorList.Over.newBuilder();
                PicListResponse.Result.Vrmaterial.ColorList.Hori.Builder horiItem = PicListResponse.Result.Vrmaterial.ColorList.Hori.newBuilder();
                color.getOver().getNormal().forEach(normal -> overItem.addNormal(PicListResponse.Result.Vrmaterial.ColorList.Normal.newBuilder().setSeq(normal.getSeq()).setUrl(normal.getUrl())));
                color.getOver().getPreview().forEach(preview -> overItem.addPreview(PicListResponse.Result.Vrmaterial.ColorList.Preview.newBuilder().setSeq(preview.getSeq()).setUrl(preview.getUrl())));
                color.getHori().getNormal().forEach(normal -> {
                    PicListResponse.Result.Vrmaterial.ColorList.Normal.Builder horiNormal = PicListResponse.Result.Vrmaterial.ColorList.Normal.newBuilder();
                    horiNormal.setSeq(normal.getSeq()).setUrl(normal.getUrl());
                    normal.getPointinfo().forEach(pointInfo -> {
                        PicListResponse.Result.Vrmaterial.ColorList.Pointinfo.Builder pointInfoItem = PicListResponse.Result.Vrmaterial.ColorList.Pointinfo.newBuilder();
                        pointInfoItem.setLocationid(pointInfo.getLocationid()).setName(pointInfo.getName()).setLinkurl(pointInfo.getLinkurl());
                        horiNormal.addPointinfo(pointInfoItem);
                    });
                    horiItem.addNormal(horiNormal);
                });
                color.getHori().getPreview().forEach(preview -> horiItem.addPreview(PicListResponse.Result.Vrmaterial.ColorList.Preview.newBuilder().setSeq(preview.getSeq()).setUrl(preview.getUrl())));

                colorItem.setHori(horiItem).setOver(overItem);
                vrMaterial.addColorList(colorItem);
            });
            //高质量图车系 赋值 megainfo
            if (Objects.nonNull(hqPicDataDto) && !vrInfo.getColor_list().isEmpty()) {
                try {
                    String firstImg = vrInfo.getColor_list().get(0).getHori().getPreview().get(0).getUrl();
                    if (StringUtils.isNotEmpty(firstImg)) {
                        PicListResponse.Result.MegaInfo.Builder megaInfo = PicListResponse.Result.MegaInfo.newBuilder();
                        megaInfo.setName("VR");
                        megaInfo.setImgurl(firstImg);
                        megaInfo.setPvitem(Pvitem.newBuilder()
                                .putArgvs("botton_name", "VR")
                                .putArgvs("objectid", request.getCategoryid()+"")
                                .putArgvs("seriesid", hqPicDataDto.getSeriesId() + "")
                                .putArgvs("specid", vrInfo.getSpecId() + "")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));
                        vrMaterial.setMegainfo(megaInfo);
                    }
                } catch (Exception e) {
                    log.warn("高质量图设置 mega信息异常", e);
                }
            }
        }

        return vrMaterial;
    }

    /**
     * 改装图片列表
     *
     * @param request
     * @return
     */
    public CompletableFuture<PageOf<PicListResponse.Result.Piclist>> getRefixPicList(PicListRequest request) {

        int ctype = 0, cid = 0;
        if (StringUtils.isNotBlank(request.getMixid())) {
            try {
                String[] ctypeidstr = request.getMixid().split("_");
                if (ctypeidstr.length == 2) {
                    ctype = Integer.parseInt(ctypeidstr[0]);
                    cid = Integer.parseInt(ctypeidstr[1]);
                }
            } catch (Exception e) {
                log.error("拆分pic错误", e);
            }
        }

        return seriesRemodelCoversComponent.get(request.getSeriesid(), request.getSpecid(), request.getPageindex(), request.getPagesize(), ctype, cid).thenApply(x -> {
            List<PicListResponse.Result.Piclist> picList = new ArrayList<>();
            if (x != null && !x.getList().isEmpty()) {

                List<Integer> specIds = x.getList().stream().map(SeriesRemodelCoversDto.ListDTO::getSpec_id).distinct().toList();
                Map<Integer, Integer> specMap = specDetailComponent.getList(specIds).join().stream().collect(Collectors.toMap(SpecDetailDto::getSpecId, SpecDetailDto::getState));

                x.getList().forEach(pic -> {
                    PicListResponse.Result.Piclist.Builder newItem = PicListResponse.Result.Piclist.newBuilder();
                    newItem.setNowebppic(HttpUtils.ToHttps(ChangeLogoSeriesImageSize(pic.getImg_url(), 14)));
                    newItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(pic.getImg_url(), 16))));
                    newItem.setBigpic(HttpUtils.ToHttp(ImageUtils.convertImage_ToWebp(ChangeLogoSeriesImageSize(pic.getImg_url(), 4))));
                    newItem.setHighpic(HttpUtils.ToHttp(ImageUtils.convertImage_ToWebp(ChangeLogoSeriesImageSize(pic.getImg_url(), 14))));
                    newItem.setOriginalpic("");
                    if (!HttpUtils.ToHttp(pic.getImg_url()).equals(newItem.getHighpic().replace(".webp", ""))) {
                        newItem.setOriginalpic(HttpUtils.ToHttp(pic.getImg_url()));
                    }
                    newItem.setWidth(0);
                    newItem.setHeight(0);
                    newItem.setId(pic.getBiz_id() + "");
                    newItem.setMixid(1 + "_" + pic.getBiz_id());
                    newItem.setSpecid(pic.getSpec_id() + "");
                    newItem.setSpecname(pic.getSpec_name());
                    newItem.setTypeid(1111);
                    newItem.setDifconfiglinkurl("");
                    newItem.setWidth(pic.getCommunity_width());
                    newItem.setHeight(pic.getCommunity_height());
                    newItem.setDealername("");
                    newItem.setAutoshowtitle("");
                    newItem.setOptiontips("");
                    newItem.setShareurl("");
                    newItem.setLinkurl("autohome://car/specmain?specid=" + pic.getSpec_id());
                    newItem.setShareurl(HttpUtils.ToHttps(String.format("https://comm.app.autohome.com.cn/comm_v1.0.0/cars/dutusharedcontent-pm%s-ss%s-sp%s-t%s-imgid%s.html",
                            request.getPm(), request.getSeriesid(), pic.getSpec_id(), request.getCategoryid(), newItem.getId())));
                    newItem.setPicsource("查看图片来源");
                    newItem.setPicsourcelinkurl(pic.getApp_url());
                    if (specMap.containsKey(pic.getSpec_id())) {
                        newItem.setSalestate(specMap.get(pic.getSpec_id()));
                        newItem.setSalestatetip(SpecStateEnum.getNameByValueForPic(specMap.get(pic.getSpec_id())));
                    }
                    picList.add(newItem.build());
                });
            }

            PageOf<PicListResponse.Result.Piclist> pageOf = new PageOf<>();
            pageOf.setItems(picList);
            pageOf.setCount(x != null ? x.getTotal() : 0);
            pageOf.setPageIndex(x != null ? x.getPageIndex() : 1);

            return pageOf;
        }).exceptionally(e -> {
            log.error("get series refix pic error", e);
            return null;
        });
    }

    /**
     * 图片60对比图-概览
     *
     * @param request
     * @return
     */
    public CompletableFuture<PageOf<PicListResponse.Result.Piclist>> getGL60PicList(CompletableFuture<List<SixtyPicListDto>> seriesSixtyPicComponent, PicListRequest request, PicListResponse.Result.Builder resultBuilder) {

        List<PicListResponse.Result.GlTablist> glTabList = new ArrayList<>();
        List<PicListResponse.Result.Piclist> picList = new ArrayList<>();

        return seriesSixtyPicComponent.thenApply(data -> {
            if (data == null || data.isEmpty()) {
                return null;
            }

            List<Integer> specids = new ArrayList<>();
            data.forEach(e -> e.getSpecpic().forEach(s -> specids.add(s.getSpecid())));
            Map<Integer, Long> map = specids.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
            List<Map.Entry<Integer, Long>> specidlist = map.entrySet().stream().filter(m -> m.getValue().intValue() >= 50).toList();
            if (specidlist.isEmpty() || StringUtils.isEmpty(carpicGltabConfig)) {
                return null;
            }
            int maxSpecid = specidlist.stream().mapToInt(Map.Entry::getKey).max().getAsInt();

            List<GlTabPicConfig> glTabPicConfigs = JsonUtil.toObjectList(carpicGltabConfig, GlTabPicConfig.class);
            for (GlTabPicConfig cfg : glTabPicConfigs) {
                PicListResponse.Result.GlTablist.Builder glTabItem = PicListResponse.Result.GlTablist.newBuilder();
                glTabItem.setTabid(cfg.getTabId());
                glTabItem.setTabname(cfg.getTabName());

                List<SixtyPicListDto> picDtoList = data.stream().filter(e -> cfg.getTabPicList().contains(e.getSixtypicsortid()) && e.getSpecpic().stream().map(SixtyPicListDto.SpecPic::getSpecid).toList().contains(maxSpecid)).toList();
                if (!picDtoList.isEmpty()) {
                    glTabItem.setCount(picDtoList.size());
                    glTabList.add(glTabItem.build());
                    for (int id : cfg.getTabPicList()) {
                        List<SixtyPicListDto> pics = picDtoList.stream().filter(e -> id == e.getSixtypicsortid()).toList();
                        for (SixtyPicListDto pic : pics) {
                            List<SixtyPicListDto.SpecPic> dwPics = pic.getSpecpic().stream().filter(e -> maxSpecid == e.getSpecid()).toList();
                            for (SixtyPicListDto.SpecPic dwpic : dwPics) {
                                PicListResponse.Result.Piclist.Builder pictureItem = PicListResponse.Result.Piclist.newBuilder();
                                PicListResponse.Result.Piclist.GlPointInfo.Builder glPoint = PicListResponse.Result.Piclist.GlPointInfo.newBuilder();
                                glPoint.setGltabid(cfg.getTabId());
                                glPoint.setGlpointid(pic.getSixtypicsortid());
                                glPoint.setGltabname(cfg.getTabName());
                                glPoint.setGlpointname(pic.getName());
                                pictureItem.setGlpointinfo(glPoint);
                                pictureItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(dwpic.getPic(), 16))));
                                pictureItem.setBigpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(dwpic.getPic(), 4))));
                                pictureItem.setNowebppic(HttpUtils.ToHttps(ChangeLogoSeriesImageSize(dwpic.getPic(), 14)));
                                pictureItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(dwpic.getPic(), 14))));
                                pictureItem.setOriginalpic(HttpUtils.ToHttp(dwpic.getPic()));
                                pictureItem.setSpecid(String.valueOf(dwpic.getSpecid()));
                                pictureItem.setId(dwpic.getPicid() + "");
                                pictureItem.setMixid(1 + "_" + dwpic.getPicid());
                                pictureItem.setWidth(3840);
                                pictureItem.setHeight(2880);
                                pictureItem.setLinkurl("autohome://car/specmain?specid=" + dwpic.getSpecid());
                                picList.add(pictureItem.build());
                            }
                        }
                    }
                }
            }

            PageOf<PicListResponse.Result.Piclist> pageOf = new PageOf<>();
            pageOf.setItems(picList);
            pageOf.setCount(picList.size());

            resultBuilder.addAllGltablist(glTabList);

            return pageOf;
        }).exceptionally(e -> {
            log.error("get series 60 pic error", e);
            return null;
        });
    }

    /**
     * 互动视频
     *
     * @param request
     * @return
     */
    public CompletableFuture<PicListResponse.Result.Actionvideoinfo.Builder> buildActionVideoInfo(PicListRequest request,
                                                                                                  SeriesDetailDto seriesDetail,
                                                                                                  SpecDetailDto specDetail,
                                                                                                  HqPicDataDto hqPicDataDto) {
        return CompletableFuture.supplyAsync(() -> {
            // TODO chengjincheng 2024/8/14 增加实验判断

            if (Objects.isNull(hqPicDataDto)
                    || CollectionUtils.isEmpty(hqPicDataDto.getRotateVideoAlbumList())) {
                return null;
            }

            // 根据车型和颜色进行筛选
            HqPicDataDto.RotateVideoAlbum rotateVideoAlbum;
            if (Objects.isNull(specDetail) && request.getColorid() == 0) {
                rotateVideoAlbum = hqPicDataDto.getRotateVideoAlbumList().stream().findFirst().orElse(null);
            } else {
                rotateVideoAlbum = hqPicDataDto.getRotateVideoAlbumList().stream()
                        .filter(e -> {
                            boolean flag = true;
                            if (Objects.nonNull(specDetail)) {
                                flag = e.getSpecId() == specDetail.getSpecId();
                            }
                            if (request.getColorid() > 0) {
                                flag = flag && e.getColorId() == request.getColorid();
                            }
                            return flag;
                        })
                        .findFirst()
                        .orElse(null);
            }

            if (Objects.isNull(rotateVideoAlbum)) {
                return null;
            }

            SpecDetailDto specInRotateVideo = specDetailComponent.getSync(rotateVideoAlbum.getSpecId());

            PicListResponse.Result.Actionvideoinfo.Builder actionVideoInfo =
                    PicListResponse.Result.Actionvideoinfo.newBuilder();
            Map<Integer, HqPicDataDto.PointRotateVideo> pointMap = rotateVideoAlbum.getPointRotateVideoList().stream()
                    .collect(Collectors.toMap(HqPicDataDto.PointRotateVideo::getPointId, e -> e, (k1, k2) -> k2));
            if (Objects.nonNull(pointMap.get(1))) {
                if (Objects.nonNull(pointMap.get(1).getMiniVideo())) {
                    actionVideoInfo.setVideobytesize(pointMap.get(1).getMiniVideo().getByteSize());
                    actionVideoInfo.setVideourl(pointMap.get(1).getMiniVideo().getVideoUrl());
                    actionVideoInfo.setVideoimage(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(
                            ImageUtils.convertImage_Size(pointMap.get(1).getMiniVideo().getImgUrl(),
                                    ImageSizeEnum.ImgSize_WxH_300x0_))));
                    actionVideoInfo.setScheme("autohome://car/actionvideo?sourceid=0&specid=" + rotateVideoAlbum.getSpecId());
                    actionVideoInfo.setSalestate(specInRotateVideo.getState());
                    actionVideoInfo.setSalestatetip(SpecStateEnum.getNameByValueForPic(specInRotateVideo.getState()));
                    actionVideoInfo.setName(specInRotateVideo.getSpecName());

                    PicListResponse.Result.MegaInfo.Builder megaInfo = PicListResponse.Result.MegaInfo.newBuilder();
                    megaInfo.setName("真车")
                            .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(
                                    ImageUtils.convertImage_Size(pointMap.get(1).getMiniVideo().getImgUrl(),
                                            ImageSizeEnum.ImgSize_WxH_300x0_))))
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("botton_name", "真车")
                                    .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                                    .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                    .putArgvs("specid", String.valueOf(request.getSpecid()))
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));
                    actionVideoInfo.setMegainfo(megaInfo);
                }
                if (Objects.nonNull(pointMap.get(1).getOriginVideo())) {
                    PicListResponse.Result.Actionvideoinfo.FullVideoinfo.Builder fullVideoInfo =
                            PicListResponse.Result.Actionvideoinfo.FullVideoinfo.newBuilder();
                    fullVideoInfo.setVideourl(pointMap.get(1).getOriginVideo().getVideoUrl());
                    fullVideoInfo.setVideobytesize(pointMap.get(1).getOriginVideo().getByteSize());
                    fullVideoInfo.setVideoimage(pointMap.get(1).getOriginVideo().getImgUrl());
                    actionVideoInfo.setFullvideoinfo(fullVideoInfo);
                }
            }
            if (Objects.nonNull(pointMap.get(2))) {
                if (Objects.nonNull(pointMap.get(2).getMiniVideo())) {
                    PicListResponse.Result.Actionvideoinfo.Doorinfo.Builder doorInfo =
                            PicListResponse.Result.Actionvideoinfo.Doorinfo.newBuilder();
                    doorInfo.setVideourl(pointMap.get(2).getMiniVideo().getVideoUrl());
                    doorInfo.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/car_piclist_top_door_0_240723@3x.png");
                    doorInfo.setSelicon("http://nfiles3.autohome.com.cn/zrjcpk10/car_piclist_top_door_1_240723@3x.png");
                    doorInfo.setVideoimage(pointMap.get(2).getMiniVideo().getImgUrl());
                    doorInfo.setPvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_pic_detail_list_open_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_pic_detail_list_open_show").build()).build());
                    actionVideoInfo.setDoorinfo(doorInfo);
                }
                if (Objects.nonNull(pointMap.get(2).getOriginVideo())) {
                    PicListResponse.Result.Actionvideoinfo.FullVideoinfo.Builder fullDoorVideoInfo =
                            PicListResponse.Result.Actionvideoinfo.FullVideoinfo.newBuilder();
                    fullDoorVideoInfo.setVideourl(pointMap.get(2).getOriginVideo().getVideoUrl());
                    fullDoorVideoInfo.setVideobytesize(pointMap.get(2).getOriginVideo().getByteSize());
                    fullDoorVideoInfo.setVideoimage(pointMap.get(2).getOriginVideo().getImgUrl());
                    actionVideoInfo.setFulldoorvideoinfo(fullDoorVideoInfo);
                }
            }

            actionVideoInfo.setPvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "").putArgvs("specid", "59602")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_interactive_entrance_click").build())
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_interactive_entrance_show").build()).build());

            return actionVideoInfo;
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> null);
    }

    /**
     * 互动视频
     *
     * @param request
     * @return
     */
    @Deprecated
    public CompletableFuture<PicListResponse.Result.Actionvideoinfo.Builder> buildActionVideoInfoOld(PicListRequest request,
                                                                                                     SpecDetailDto specDetail) {

        return CompletableFuture.supplyAsync(() -> {
            PicListResponse.Result.Actionvideoinfo.Builder actionVideoInfo = PicListResponse.Result.Actionvideoinfo.newBuilder();

            VideoVRConfig videoVRConfig = getVideoVRConfig(request);
            if (videoVRConfig != null) {
                SpecDetailDto specDetailDto;
                if (request.getSpecid() == videoVRConfig.getSpecid()) {
                    specDetailDto = specDetail;
                } else {
                    specDetailDto = specDetailComponent.get(videoVRConfig.getSpecid()).join();
                }

                actionVideoInfo.setVideobytesize(videoVRConfig.getActionvideoinfo().getVideobytesize());
                actionVideoInfo.setVideourl(videoVRConfig.getActionvideoinfo().getVideourl());
                actionVideoInfo.setVideoimage(videoVRConfig.getActionvideoinfo().getVideoimage());
                actionVideoInfo.setScheme("autohome://car/actionvideo?sourceid=0&id=" + videoVRConfig.getId());
                actionVideoInfo.setPvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "").putArgvs("specid", videoVRConfig.getSpecid() + "")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_interactive_entrance_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_interactive_entrance_show").build()).build());
                actionVideoInfo.setFullvideoinfo(PicListResponse.Result.Actionvideoinfo.FullVideoinfo.newBuilder()
                        .setVideourl(videoVRConfig.getActionvideoinfo().getFullvideoinfo().getVideourl())
                        .setVideobytesize(videoVRConfig.getActionvideoinfo().getFullvideoinfo().getVideobytesize())
                        .setVideoimage(videoVRConfig.getActionvideoinfo().getFullvideoinfo().getVideoimage()).build());
                actionVideoInfo.setVerticalfullvideoinfo(PicListResponse.Result.Actionvideoinfo.FullVideoinfo.newBuilder()
                        .setVideourl(videoVRConfig.getActionvideoinfo().getVerticalfullvideoinfo().getVideourl())
                        .setVideobytesize(videoVRConfig.getActionvideoinfo().getVerticalfullvideoinfo().getVideobytesize())
                        .setVideoimage(videoVRConfig.getActionvideoinfo().getVerticalfullvideoinfo().getVideoimage()).build());

                if (specDetailDto != null) {
                    actionVideoInfo.setSalestate(specDetailDto.getState());
                    actionVideoInfo.setSalestatetip(SpecStateEnum.getNameByValueForPic(specDetailDto.getState()));
                    actionVideoInfo.setName(specDetailDto.getSpecName());
                }
            }

            return actionVideoInfo;
        });
    }

    private VideoVRConfig getVideoVRConfig(PicListRequest request) {
        if (videoVRConfigList != null && !videoVRConfigList.isEmpty()) {
            return this.videoVRConfigList.stream().filter(model -> model.isEnable()
                    && model.getSeriesid() == request.getSeriesid()
                    && (request.getSpecid() == 0 || model.getSpecid() == request.getSpecid())
                    && (request.getColorid() == 0 || model.getRemotecolorid() == request.getColorid())).findFirst().orElse(null);
        }

        return null;
    }

    /**
     * 车视频
     *
     * @param request
     * @return
     */
    public PicListResponse.Result.VideoInfo.Builder buildVideoInfo(PicListRequest request) {
        PicListResponse.Result.VideoInfo.Builder video = PicListResponse.Result.VideoInfo.newBuilder();
        if (StringUtils.isNotEmpty(carVideos)) {
            List<CarVideoData> carVideoDataList = JSON.parseArray(carVideos, CarVideoData.class);
            Map<Integer, CarVideoData> carVideoDataMap = carVideoDataList.stream().collect(Collectors.toMap(CarVideoData::getSeriesid, c -> c));
            CarVideoData data = carVideoDataMap.get(request.getSeriesid());
            if (data != null) {
                video.setVideoid(data.getVideoid());
                video.setCover(data.getCover());
                video.setExptitle("全车讲解视频");
                video.setSameleveltitle("同级车视频");
                video.setExpscheme("autohome://article/slidevideodetail?newsid=" + data.getFullvideoid());
                video.setScheme("autohome://article/slidevideodetail?newsid=" + data.getShortvideoid());
                List<PicListResponse.Result.VideoInfo.VideoCar> samelevelcars = new ArrayList<>();
                carVideoDataList.forEach(sameLevelCar -> {
                    if (request.getSeriesid() != sameLevelCar.getSeriesid()) {
                        PicListResponse.Result.VideoInfo.VideoCar.Builder car = PicListResponse.Result.VideoInfo.VideoCar.newBuilder();
                        car.setName(sameLevelCar.getName());
                        car.setScheme("autohome://article/slidevideodetail?newsid=" + sameLevelCar.getShortvideoid());
                        car.setPvitem(Pvitem.newBuilder().putArgvs("seriesid", sameLevelCar.getSeriesid() + "")
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_video_samecar_click").build())
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_video_samecar_show").build()).build());
                        samelevelcars.add(car.build());
                    }
                });
                video.addAllSamelevelcars(samelevelcars);

                video.setVideopvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_video_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_video_show").build()).build());
                video.setDespvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_videodes_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_videodes_show").build()).build());
                video.setSamecarpvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_video_samecar_fold_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_video_samecar_fold_show").build()).build());
            }
        }

        return video;
    }

    public void buildPicBaseInfo(PicListRequest request, PicListResponse.Result.Builder resultBuilder) {
        if ((request.getCategoryid() == 1 || request.getCategoryid() == 10)) {

        }
    }

    /**
     * 获取图片二级分类，仅高质图片后台上传的图片有二级分类
     *
     * @param request      PicListRequest
     * @param hqPicDataDto HqPicDataDto
     * @return CompletableFuture<List < PicListResponse.Result.SecFilter>>
     */
    public CompletableFuture<List<PicListResponse.Result.SecFilter>> getAllSecFilters(PicListRequest request,
                                                                                      HqPicDataDto hqPicDataDto) {
        return CompletableFuture.supplyAsync(() -> {
            if (Objects.nonNull(hqPicDataDto) && !CollectionUtils.isEmpty(hqPicDataDto.getPhotoSubTypeMap())) {
                List<HqPicDataDto.PhotoSubTypeDto> hqPhotoSubTypeDtoList =
                        hqPicDataDto.getPhotoSubTypeMap().get(TypeIdTranslateUtil.getTypeId2HqPic(request.getCategoryid()));
                hqPhotoSubTypeDtoList = hqPhotoSubTypeDtoList.stream()
                        .sorted(Comparator.comparing(HqPicDataDto.PhotoSubTypeDto::getSubTypeSortId))
                        .toList();

                List<HqPicDataDto.PicCount> picCountList = hqPicDataDto.getPicCountList().stream()
                        .filter(e -> {
                                    boolean isOne = true;
                                    if (request.getSpecid() > 0) {
                                        isOne = e.getSpecId() == request.getSpecid();
                                    }
                                    if (request.getCategoryid() > 0) {
                                        isOne = isOne && e.getCpkTypeId() == request.getCategoryid();
                                    }
                                    if (request.getColorid() > 0) {
                                        isOne = isOne && (request.getIsinner() == 0 && e.getColorId() == request.getColorid()) || (request.getIsinner() == 1 && e.getInnerColorId() == request.getColorid());
                                    }
                                    return isOne;
                                }
                        ).toList();

                if (request.getCategoryid() == 4 && hqPhotoSubTypeDtoList.size() == 1) {
                    hqPhotoSubTypeDtoList = new ArrayList<>();
                }
                if (!CollectionUtils.isEmpty(hqPhotoSubTypeDtoList)) {
                    int page = request.getSpecid() > 0 ? 1 : 0;
                    List<PicListResponse.Result.SecFilter> secFilterList = new ArrayList<>();
                    PicListResponse.Result.SecFilter.Builder wholeSecFilter = PicListResponse.Result.SecFilter.newBuilder();
                    wholeSecFilter.setName("全部");
                    wholeSecFilter.setFilterid(0);
                    wholeSecFilter.setPvitem(Pvitem.newBuilder()
                            .putArgvs("botton_name", "全部")
                            .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                            .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                            .putArgvs("page", String.valueOf(page))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_filtrate_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_filtrate_show").build())
                            .build());
                    secFilterList.add(wholeSecFilter.build());

                    hqPhotoSubTypeDtoList.stream()
                            .sorted(Comparator.comparing(HqPicDataDto.PhotoSubTypeDto::getSubTypeSortId))
                            .forEach(e -> {
                                if (picCountList.stream().anyMatch(picCount -> picCount.getSubTypeId() == e.getSubTypeId())) {
                                    PicListResponse.Result.SecFilter.Builder secFilter = PicListResponse.Result.SecFilter.newBuilder();
                                    secFilter.setName(e.getSubTypeName());
                                    secFilter.setFilterid(e.getSubTypeId());
                                    secFilter.setPvitem(Pvitem.newBuilder()
                                            .putArgvs("botton_name", e.getSubTypeName())
                                            .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                            .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                                            .putArgvs("page", String.valueOf(page))
                                            .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_filtrate_click").build())
                                            .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_filtrate_show").build())
                                            .build());

                                    secFilterList.add(secFilter.build());
                                }
                            });
                    if (secFilterList.size() == 1) {
                        secFilterList.clear();
                    }
                    return secFilterList;
                }
            }
            return null;
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> null);
    }

    /**
     * 获取图片视频合集，仅高质图片后台上传的车型会有视频合集
     *
     * @param request      PicListRequest
     * @param specDetail   SpecDetailDto
     * @param hqPicDataDto HqPicDataDto
     * @return CompletableFuture<PicListResponse.Result.Videoalbum>
     */
    public CompletableFuture<PicListResponse.Result.Videoalbum> getVideoAlbum(PicListRequest request,
                                                                              SpecDetailDto specDetail,
                                                                              HqPicDataDto hqPicDataDto) {
        return CompletableFuture.supplyAsync(() -> {
            if (Objects.isNull(hqPicDataDto)
                    || CollectionUtils.isEmpty(hqPicDataDto.getVideoAlbumList())) {
                return null;
            }

            // 根据车型和颜色进行筛选
            HqPicDataDto.VideoAlbum videoAlbum;
            if (Objects.isNull(specDetail) && request.getColorid() == 0) {
                videoAlbum = hqPicDataDto.getVideoAlbumList().stream().findFirst().orElse(null);
            } else {
                videoAlbum = hqPicDataDto.getVideoAlbumList().stream()
                        .filter(e -> {
                            boolean flag = true;
                            if (Objects.nonNull(specDetail)) {
                                flag = e.getSpecId() == specDetail.getSpecId();
                            }
                            if (request.getColorid() > 0) {
                                if (request.getCategoryid() == 1) {
                                    flag = flag && e.getColorId() == request.getColorid();
                                } else {
                                    flag = flag && e.getInnerColorId() == request.getColorid();
                                }
                            }
                            return flag;
                        })
                        .findFirst()
                        .orElse(null);
            }
            if (Objects.isNull(videoAlbum)) {
                return null;
            }

            // 补充func
            PicListResponse.Result.Videoalbum.Builder videoBuilder = PicListResponse.Result.Videoalbum.newBuilder();
            PicListResponse.Result.Videoalbum.Func.Builder fb = PicListResponse.Result.Videoalbum.Func.newBuilder();
            fb.setType(1)
                    .setName("静音")
                    .setImgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_piclist_mega_mute_240719@3x.webp")
                    .setSelimgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_piclist_mega_unmute_240719@3x.webp")
                    .setPvitem(Pvitem.newBuilder()
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_sound_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_sound_show").build()));
            videoBuilder.addFunc(fb.build());

            // 过滤出一级分类下的视频合集列表
            HqPicDataDto.TypeAlbum typeAlbum = videoAlbum.getTypeAlbumList().stream()
                    .filter(e -> e.getTypeId() == TypeIdTranslateUtil.getTypeId2HqPic(request.getCategoryid()))
                    .findFirst()
                    .orElse(null);
            if (Objects.isNull(typeAlbum)) {
                return null;
            }

            List<HqPicDataDto.SubTypeAlbum> subTypeAlbumList = typeAlbum.getSubTypeAlbumList().stream()
                    .sorted(Comparator.comparing(HqPicDataDto.SubTypeAlbum::getSubTypeSortId))
                    .toList();

            // 视频合集resp构建
            for (HqPicDataDto.SubTypeAlbum album : subTypeAlbumList) {
                PicListResponse.Result.Videoalbum.Album.Builder albumBuilder =
                        PicListResponse.Result.Videoalbum.Album.newBuilder();
                PicListResponse.Result.Videoalbum.Album.Info.Builder infoBuilder =
                        PicListResponse.Result.Videoalbum.Album.Info.newBuilder();

                List<HqPicDataDto.Video> videoList = album.getVideoList().stream()
                        .sorted(Comparator.comparing(HqPicDataDto.Video::getPointSortId))
                        .toList();

                // 构建视频合集的info
                String albumName = album.getAlbumName() + "(" + videoList.size() + ")";
                infoBuilder.setName(albumName)
                        .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ImageUtils.convertImage_Size(
                                videoList.get(0).getImgUrl(), ImageSizeEnum.ImgSize_WxH_300x0_))))
                        .setPvitem(Pvitem.newBuilder()
                                .putArgvs("botton_name", albumName)
                                .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                                .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                .putArgvs("specid", String.valueOf(request.getSpecid()))
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));
                albumBuilder.setInfo(infoBuilder.build());

                // 构建视频合集的视频信息
                for (HqPicDataDto.Video video : videoList) {
                    PicListResponse.Result.Videoalbum.Album.Video.Builder vBuilder =
                            PicListResponse.Result.Videoalbum.Album.Video.newBuilder();
                    String videoName = getVideoName(video, videoList);
                    vBuilder.setName(videoName)
                            .setVid(video.getVid())
                            .setNamenoindex(video.getName())
                            .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(
                                    ImageUtils.convertImage_Size(video.getImgUrl(), ImageSizeEnum.ImgSize_WxH_800x0_))))
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("botton_name", albumName)
                                    .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                    .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                                    .putArgvs("video_name", videoName)
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_multivideo_click").build())
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_multivideo_show").build()));
                    albumBuilder.addVideos(vBuilder.build());
                }
                videoBuilder.addAlbum(albumBuilder.build());
            }
            return videoBuilder.build();

        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> null);
    }

    private String getVideoName(HqPicDataDto.Video video, List<HqPicDataDto.Video> videoList) {
        return video.getName()
                + "(" + (videoList.indexOf(video) + 1)
                + "/" + videoList.size() + ")";
    }

    public PicListShareResponse.PicListShareResult getPicListShare(PicListShareRequest request,HqPicDataDto hqPicDataDto) {
        String wxurl = UrlUtil.encode("/car-package/pages/pic/list/index");
        String sence = "auto_open_from=autohome_qrcode_picturelist&seriesid=%s&specid=%s&classId=%s";
        sence = String.format(sence, request.getSeriesid(),request.getSpecid() > 0 ? request.getSpecid() : "", Arrays.asList(1111,55,4).contains(request.getClassid()) ? 1 : request.getClassid());
        CompletableFuture<BaseModel> codeFuture = configItemApiClient.getQrcode(wxurl, UrlUtil.encode(sence));
        CompletableFuture<SeriesDetailDto> seriesDetailFuture = seriesDetailComponent.getAsync(request.getSeriesid());
        CompletableFuture<SeriesVr> seriesVrFuture = seriesVrComponent.get(request.getSeriesid());
        CompletableFuture<SpecDetailDto> specDetailFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<PageOf<HqPhotoDto>> HqPhotoFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<PageOf<CarPhotoDto>> photoFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<SeriesRemodelCoversDto> refixFuture = CompletableFuture.completedFuture(null);
        if (request.getClassid() == 1111){
            //改装的数据取自不同组件
            refixFuture = seriesRemodelCoversComponent.get(request.getSeriesid(), request.getSpecid(), 1, 4, 0, 0);
        }
        if (request.getSpecid() > 0){
            //存在不传车型的情况
            specDetailFuture = specDetailComponent.get(request.getSpecid());
        }
        //当前车系有高质图，并且是外观、座椅、夜景、中控这四个分类就使用高质图
        if (Objects.nonNull(hqPicDataDto) && Arrays.asList("1","3","4","10").contains(request.getClassid()+"")){
            //是座椅分类时要把分类id改成2
            int classId = request.getClassid() == 10 ? 2 : request.getClassid();
            HqPhotoFuture = hqPhotoComponent.getWithoutVideo(request.getSeriesid(), request.getSpecid(), 0, 0, classId, 0, 1, 4, 0, 0);
        }else{
            //非高质图
            photoFuture = carPhotoComponent.get(request.getSeriesid(), request.getSpecid(), 0, 0, List.of(request.getClassid()), 1, 4, 0, 0);
        }
        CompletableFuture<SpecDetailDto> final_specDetailFuture = specDetailFuture;
        CompletableFuture<PageOf<HqPhotoDto>> final_HqPhotoFuture = HqPhotoFuture;
        CompletableFuture<PageOf<CarPhotoDto>> final_photoFuture = photoFuture;
        CompletableFuture<SeriesRemodelCoversDto> final_refixFuture = refixFuture;
        return CompletableFuture.allOf(specDetailFuture,seriesDetailFuture,HqPhotoFuture,photoFuture,codeFuture,refixFuture,seriesVrFuture).thenApply(x -> {
            SpecDetailDto specDetailInfo = final_specDetailFuture.join();//车型详情
            PageOf<HqPhotoDto> HqPhotoInfo = final_HqPhotoFuture.join();//高质图
            PageOf<CarPhotoDto> photoInfo = final_photoFuture.join();//普通图
            SeriesRemodelCoversDto refixInfo = final_refixFuture.join();//改装图
            SeriesDetailDto seriesDetailInfo = seriesDetailFuture.join();//车系详情
            SeriesVr seriesVrInfo = seriesVrFuture.join();//车系vr
            BaseModel codeInfo = codeFuture.join();//二维码
            boolean hasHqPhoto = HqPhotoInfo != null ;
            String first_image = "";//首图，取自图片列表的第一张图
            List<String> imageList = new ArrayList<>();//图片列表
            String path = "/car-package/pages/pic/list/index?auto_open_from=autohome_picturelist&seriesid=%s&specid=%s&classId=%s";
            String shareUrl = request.getSpecid() > 0 ? "https://car.m.autohome.com.cn/pic/series/%s-%s-1-0-i0.html" : "https://car.m.autohome.com.cn/pic/series/%s.html";//兜底跳转链接
            int vrState = 0;//vrState为0，当前车系无vr；vrState为1，当前车系有vr
            if (seriesVrInfo != null && seriesVrInfo.getVrMaterial() != null && StringUtils.isNotEmpty(seriesVrInfo.getVrMaterial().getJump_url())){
                vrState = 1;
            }
            if (request.getClassid() == 1111 && refixInfo != null && !refixInfo.getList().isEmpty()){
                //改装图的数据来自其他组件，需要特殊处理
                String specName = "";
                for (int i = 0; i < refixInfo.getList().size(); i++) {
                    SeriesRemodelCoversDto.ListDTO refixDto = refixInfo.getList().get(i);
                    if (i == 0){
                        first_image = ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(refixDto.getImg_url()), true, false, false, ImageSizeEnum.ImgSize_4x3_800x600);
                        if (request.getSpecid() <= 0){
                            specName = refixDto.getSpec_name();
                        }
                    }
                    imageList.add(ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(refixDto.getImg_url()), true, false, false, ImageSizeEnum.ImgSize_4x3_800x600));
                }
                if (request.getSpecid() > 0){
                    specName = specDetailInfo != null ? specDetailInfo.getSpecName() : "";
                }
                return PicListShareResponse.PicListShareResult.newBuilder()
                        .setBanner("http://nfiles3.autohome.com.cn/zrjcpk10/car_share_photo_applet_banner@3x.png")
                        .setVrstate(vrState)
                        .setQrcode(codeInfo.getResult().toString())
                        .setPath(String.format(path, request.getSeriesid(), request.getSpecid() > 0 ? request.getSpecid() : "", "1", first_image))
                        .setSpecname(specName)
                        .setWeixintitle(seriesDetailInfo != null ? seriesDetailInfo.getName() + " " + "高清大图，帮看看这车好么" : "")
                        .setWeixinlogo(first_image)
                        .setShareurl(request.getSpecid() > 0 ? String.format(shareUrl,request.getSeriesid(),request.getSpecid()) : String.format(shareUrl, request.getSeriesid()))
                        .addAllImagelist(imageList)
                        .setSharetip(request.getSpecid() > 0 ? "您当前分享的是本车型全部图片" : "您当前分享的是本车系全部图片")
                        .build();
            }
            AtomicReference<String> specNameWithoutSpecId = new AtomicReference<>("");//当接口入参无车型id时，取对应分类(夜景、车展、官图等)下第一张图对应车型的名称
            if (hasHqPhoto){
                //当前车系含有高质图
                for (int i = 0; i < HqPhotoInfo.getItems().size(); i++) {
                    HqPhotoDto hqPhotoDto = HqPhotoInfo.getItems().get(i);
                    if (i == 0){
                        first_image = ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(hqPhotoDto.getUrl()), true, false, false, ImageSizeEnum.ImgSize_4x3_800x600);
                        if (request.getSpecid() <= 0){
                            //接口入参未传车型id，就取第一张图片对应的车型名称、车型vr状态、车型id
                            SpecDetailDto specInfo = specDetailComponent.getSync(hqPhotoDto.getSpecId());
                            specNameWithoutSpecId.set(specInfo != null ? specInfo.getSpecName() : "");
                        }
                    }
                    imageList.add(ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(hqPhotoDto.getUrl()), true, false, false, ImageSizeEnum.ImgSize_4x3_800x600));
                }
            }else{
                //当前车系无高质图
                for (int i = 0; i < photoInfo.getItems().size(); i++) {
                    CarPhotoDto carPhotoDto = photoInfo.getItems().get(i);
                    if (i == 0){
                        first_image = ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(carPhotoDto.getFilepath()), true, false, false, ImageSizeEnum.ImgSize_4x3_800x600);
                        if (request.getSpecid() <= 0){
                            //接口入参未传车型id，就取第一张图片对应的车型名称、车型vr状态、车型id
                            SpecDetailDto specInfo = specDetailComponent.getSync(carPhotoDto.getSpecId());
                            specNameWithoutSpecId.set(specInfo != null ? specInfo.getSpecName() : "");
                        }
                    }
                    imageList.add(ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(carPhotoDto.getFilepath()), true, false, false, ImageSizeEnum.ImgSize_4x3_800x600));
                }
            }
            if (request.getSpecid() > 0){
                return PicListShareResponse.PicListShareResult.newBuilder()
                        .setBanner("http://nfiles3.autohome.com.cn/zrjcpk10/car_share_photo_applet_banner@3x.png")
                        .setVrstate(vrState)
                        .setQrcode(codeInfo != null && codeInfo.getResult() != null ? codeInfo.getResult().toString() : "")
                        .setPath(String.format(path, request.getSeriesid(), request.getSpecid(), Arrays.asList(55,4).contains(request.getClassid()) ? 1 : request.getClassid(), first_image))
                        .setSpecname(specDetailInfo != null ? specDetailInfo.getSpecName() : "")
                        .setWeixintitle(specDetailInfo != null ? specDetailInfo.getSeriesName() + " " + "高清大图，帮看看这车好么" : "")
                        .setWeixinlogo(first_image)
                        .setShareurl(String.format(shareUrl,request.getSeriesid(),request.getSpecid()))
                        .addAllImagelist(imageList)
                        .setSharetip("您当前分享的是本车型全部图片")
                        .build();
            }else{
                //这里取车系图
                return PicListShareResponse.PicListShareResult.newBuilder()
                        .setBanner("http://nfiles3.autohome.com.cn/zrjcpk10/car_share_photo_applet_banner@3x.png")
                        .setVrstate(vrState)
                        .setQrcode(codeInfo != null && codeInfo.getResult() != null ? codeInfo.getResult().toString() : "")
                        .setPath(String.format(path, request.getSeriesid(), "", Arrays.asList(55,4).contains(request.getClassid()) ? 1 :request.getClassid(), first_image))
                        .setSpecname(specNameWithoutSpecId.get())
                        .setWeixintitle(seriesDetailInfo != null ? seriesDetailInfo.getName() + " " + "高清大图，帮看看这车好么" : "")
                        .setWeixinlogo(first_image)
                        .setShareurl(String.format(shareUrl, request.getSeriesid()))
                        .addAllImagelist(imageList)
                        .setSharetip("您当前分享的是本车系全部图片")
                        .build();
            }
        }).exceptionally(ex -> {
            log.error("图片列表-分享接口错误",ex);
            return null;
        }).join();
    }
}
