package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.*;
import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesSixtyPicComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.SpecOutInnerColorComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SixtyPicListDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.hqpic.HqPicDataComponent;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicDataDto;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.app.cars.service.components.vr.SpecVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import com.autohome.app.cars.service.services.CarPhotoService;
import com.autohome.app.cars.service.services.dtos.ChejiSeriesDataDto;
import com.autohome.app.cars.service.services.dtos.piclist.GroupCountDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@DubboService
@RestController
public class PicListServiceGrpcImpl extends DubboPicListServiceTriple.PicListServiceImplBase {

    @Autowired
    CarPhotoService carPhotoService;

    @Autowired
    SeriesVrComponent seriesVrComponent;

    @Autowired
    SpecVrComponent specVrComponent;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    @Autowired
    SeriesSixtyPicComponent seriesSixtyPicComponent;

    @Autowired
    SpecOutInnerColorComponent specColorComponent;

    @Autowired
    private HqPicDataComponent hqPicDataComponent;

    @Value("${cheji_series_data_list:}")
    private String cheji_series_data_list;

    @Override
    @GetMapping(value = "/carbase/pic/getpiclist", produces = "application/json;charset=utf-8")
    public PicListResponse getPicList(PicListRequest request) {
        int specSaleStatus = 0;
        CompletableFuture<SeriesDetailDto> seriesDetailFuture = seriesDetailComponent.getAsync(request.getSeriesid());
        SpecDetailDto specDetail = request.getSpecid() > 0 ? specDetailComponent.getSync(request.getSpecid()) : null;
        SeriesDetailDto seriesDetail = seriesDetailFuture.join();

        if (seriesDetail == null) {
            return PicListResponse.newBuilder().setReturnCode(0).setReturnMsg("车系不存在").build();
        }

        if (specDetail != null) {
            specSaleStatus = specDetail.getState();
        }
        int seriesStatus = seriesDetail.getState();

        if(request.getCategoryid()==55){
            request.toBuilder().clearColorid().clearSpecid();
        }

        PicListResponse.Result.Builder resultBuilder = PicListResponse.Result.newBuilder();
        CompletableFuture<List<SixtyPicListDto>> seriesSixtyPicFuture = seriesSixtyPicComponent.get(request.getSeriesid());

        boolean hasHq = false;
        boolean hasTest = false;
        HqPicDataDto hqPicDataDto = null;
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.3")) {
            if (List.of("C1", "C2", "D1", "D2").contains(request.getMegaab())) {
                hasTest = true;
            }
        } else if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.5")) {
            if (List.of("C1").contains(request.getMegaab())) {
                hasTest = true;
            }
        }
        if (hasTest) {
            hqPicDataDto = hqPicDataComponent.get(seriesDetail.getId());
            if (Objects.nonNull(hqPicDataDto)) {
                resultBuilder.setMegastyle(1);
                hasHq = true;
            }
        }
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.3")) {
            hqPicDataDto = null == hqPicDataDto ? hqPicDataComponent.get(seriesDetail.getId()) : hqPicDataDto;
            if (Objects.nonNull(hqPicDataDto)) {
                resultBuilder.setIsmegatestseries(1);
            }
        }

        SpecOutInnerColorDto specColorDto = new SpecOutInnerColorDto();
        boolean hasOtherSpec = false;
        // 不与其他实验交叉
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")
                && "B".equals(request.getRcotherspec())
                && !((Arrays.asList("B1", "C1").contains(request.getMegaab()) || CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.3")) && Objects.nonNull(hqPicDataDto))
                && request.getSpecid() > 0 && Arrays.asList(1, 3, 10, 12).contains(request.getCategoryid())
                && !(Arrays.asList("B1", "C1").contains(request.getMegaab()) && Arrays.asList(6939, 7177).contains(request.getSeriesid()))
                && 0 == request.getReqpicgroup()) {
            specColorDto = specColorComponent.get(request.getSpecid(), CommonHelper.isInner(request.getCategoryid())).join();
            //没有车型颜色不走实验
            if (specColorDto != null && !CollectionUtils.isEmpty(specColorDto.getColoritems())) {
                hasOtherSpec = true;
            }
        }
        List<CompletableFuture> tasks = new ArrayList<>();

        if (request.getPageindex() == 1) {
            CompletableFuture<SeriesVr> seriesVrFuture = seriesVrComponent.get(request.getSeriesid());
            CompletableFuture<SpecVrInfoDto> specVrFuture = request.getSpecid() > 0 ? specVrComponent.get(request.getSpecid()) : CompletableFuture.completedFuture(null);

            CompletableFuture<List<SeriesVrExteriorResult.Color_List>> vrColorList = request.getCategoryid() != 1
                    ? CompletableFuture.completedFuture(new ArrayList<>())
                    : (request.getSpecid() > 0
                    ? specVrFuture.thenApply(x -> x == null || x.getVrMaterial() == null || x.getVrMaterial().getColor_list() == null ? new ArrayList<>() : x.getVrMaterial().getColor_list())
                    : seriesVrFuture.thenApply(x -> x == null || x.getVrMaterial() == null || x.getVrMaterial().getColor_list() == null ? new ArrayList<>() : x.getVrMaterial().getColor_list())
            );

            if (hasOtherSpec) {
                tasks.add(carPhotoService.getColorListWithOtherSpecColor(request, specSaleStatus, seriesStatus, vrColorList, specColorDto).thenAccept(resultBuilder::addAllColorlist));
            } else {
                //获取颜色列表
                tasks.add(carPhotoService.getColorList(request, specSaleStatus, seriesStatus, vrColorList, hqPicDataDto).thenAccept(resultBuilder::addAllColorlist));
            }
            if (hasHq) {
                //获取图片二级分类列表
                tasks.add(carPhotoService.getAllSecFilters(request, hqPicDataDto).thenAccept(secFilters->{
                    if (!CollectionUtils.isEmpty(secFilters)) {
                        resultBuilder.addAllSecfilters(secFilters);
                    }
                }));

                //获取视频合集
                tasks.add(carPhotoService.getVideoAlbum(request, specDetail, hqPicDataDto).thenAccept(videoAlbum -> {
                    if (Objects.nonNull(videoAlbum)) {
                        resultBuilder.setVideoalbum(videoAlbum);
                    }
                }));
            }

            if (request.getCategoryid() == 1) {
                //外观小屏vr，同时处理侧边按钮3D、改装
                tasks.add(carPhotoService.buildVrMaterial(specVrFuture, seriesVrFuture, seriesDetail, specDetail,hqPicDataDto, resultBuilder, request).thenApply(resultBuilder::setVrmaterial));
                //车视频
                resultBuilder.setVideo(carPhotoService.buildVideoInfo(request));
                if (hasHq) {
                    //互动视频入口
                    tasks.add(carPhotoService.buildActionVideoInfo(request, seriesDetail, specDetail, hqPicDataDto)
                            .thenAccept(actionVideoInfo -> {
                                if (Objects.nonNull(actionVideoInfo)) {
                                    resultBuilder.setActionvideoinfo(actionVideoInfo);
                                }
                            }));
                }
                //全景VR
                tasks.add(carPhotoService.buildVr(seriesVrFuture, request).thenApply(resultBuilder::setVr));
            } else if (request.getCategoryid() == 1111) {
                //改装vr
                tasks.add(carPhotoService.buildRefitVrMaterial(request).thenApply(resultBuilder::setVrmaterial));
            }

            //外观全屏、内饰半屏、内饰全屏
            if (request.getCategoryid() == 1 || request.getCategoryid() == 10) {
                tasks.add(carPhotoService.buildH5VrInfo(specVrFuture, seriesVrFuture,hqPicDataDto, request)
                        .thenAccept(x -> {
                            if (x == null) {
                                return;
                            }
                            resultBuilder.setH5Vrinfo(x);
                            //外观和中控增加车机展示入口
                            if (StringUtils.isNotEmpty(x.getShowurl())) {
                                List<ChejiSeriesDataDto> chejiDataList = JSON.parseArray(cheji_series_data_list, ChejiSeriesDataDto.class);
                                Optional<ChejiSeriesDataDto> any = chejiDataList.stream().filter(s -> s.getSeriesid() == request.getSeriesid()).findAny();
                                if (any.isPresent()) {
                                    ChejiSeriesDataDto chejiSeriesDataDto = any.get();
                                    String scheme = "autohome://insidebrowserwk?loadtype=1&url=";
                                    if (StringUtils.isNotEmpty(chejiSeriesDataDto.getBrowserparam())) {
                                        scheme = String.format("autohome://insidebrowserwk?loadtype=1&%s&url=", chejiSeriesDataDto.getBrowserparam());
                                    }
                                    scheme = scheme + UrlUtil.encode(chejiSeriesDataDto.getLinkurl());
                                    resultBuilder.setCartest(PicListResponse.Result.Cartest.newBuilder()
                                            .setIconurl(CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")
                                                    ? "http://nfiles3.autohome.com.cn/zrjcpk10/piclist_cartest_240821.webp"
                                                    : "https://files3.autoimg.cn/zrjcpk10/piclist_cartest_20231025.webp")
                                            .setLinkurl(scheme).build());
                                }
                            }
                        }));
            }
        }

        CompletableFuture<List<Integer>> sixtyPicSpecIdsTask = seriesSixtyPicFuture.thenApply(x->{
            if(x==null || x.size()==0){
                return new ArrayList<>();
            }
            List<Integer> ids = new ArrayList<>();
            for (SixtyPicListDto dto : x) {
                ids.addAll(dto.getSpecpic().stream().map(y->y.getSpecid()).collect(Collectors.toList()));
            }
            return ids.stream().filter(y->y!=null).distinct().collect(Collectors.toList());
        });

        if (hqPicDataDto==null && request.getReqpicgroup() == 1) {
            // 按车型分组获取图片列表
            GroupCountDto groupCount = new GroupCountDto();
            tasks.add(carPhotoService.getGroupPicList(request,sixtyPicSpecIdsTask,groupCount).thenAccept(picList -> {
                if (picList == null) {
                    return;
                }
                resultBuilder.addAllPicgrouplist(picList.getItems());
                resultBuilder.setRowcount(groupCount.getTotalCount());
                resultBuilder.setPagecount(picList.getCount() / request.getPagesize() + (picList.getCount() % request.getPagesize() == 0 ? 0 : 1));
                resultBuilder.setPageindex(request.getPageindex());
            }));
        } else if (request.getCategoryid() == 1111) {
            tasks.add(carPhotoService.getRefixPicList(request).thenAccept(picList -> {
                if (picList == null) {
                    return;
                }
                resultBuilder.addAllPiclist(picList.getItems());
                resultBuilder.setRowcount(picList.getCount());
                resultBuilder.setPagecount(picList.getCount() / request.getPagesize() + (picList.getCount() % request.getPagesize() == 0 ? 0 : 1));
                resultBuilder.setPageindex(picList.getPageIndex());
            }));
        }else if(request.getCategoryid() == 7008){
            tasks.add(carPhotoService.getGL60PicList(seriesSixtyPicFuture,request, resultBuilder).thenAccept(picList -> {
                if (picList == null) {
                    return;
                }
                resultBuilder.addAllPiclist(picList.getItems());
                resultBuilder.setRowcount(picList.getCount());
                resultBuilder.setPagecount(picList.getCount() / request.getPagesize() + (picList.getCount() % request.getPagesize() == 0 ? 0 : 1));
                resultBuilder.setPageindex(request.getPageindex());
            }));
        }else if(hasOtherSpec) {
            //获取图片列表
            tasks.add(carPhotoService.getPicListWithOtherSpec(request, specSaleStatus,sixtyPicSpecIdsTask, specColorDto).thenAccept(picList -> {
                if (picList == null) {
                    return;
                }
                resultBuilder.addAllPiclist(picList.getItems());
                resultBuilder.setRowcount(picList.getCount());
                resultBuilder.setPagecount(picList.getCount() / request.getPagesize() + (picList.getCount() % request.getPagesize() == 0 ? 0 : 1));
                resultBuilder.setPageindex(picList.getPageIndex());
            }).exceptionally(e -> {
                log.error("获取图片列表报错", e);
                return null;
            }));
        } else {
            //获取图片列表
            tasks.add(carPhotoService.getPicList(request, resultBuilder,sixtyPicSpecIdsTask, hqPicDataDto).thenAccept(picList -> {
                if (picList == null) {
                    return;
                }
                resultBuilder.addAllPiclist(picList.getItems());
                resultBuilder.setRowcount(picList.getCount());
                resultBuilder.setPagecount(picList.getCount() / request.getPagesize() + (picList.getCount() % request.getPagesize() == 0 ? 0 : 1));
                resultBuilder.setPageindex(picList.getPageIndex());
            }).exceptionally(e -> {
                log.error("获取图片列表报错", e);
                return null;
            }));
        }

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]))
                .thenApply(x -> {
                            //颜色和vr不匹配，不返回vr信息
                            if (request.getCategoryid() == 1 && request.getColorid() > 0) {
                                List<Integer> colorIds = resultBuilder.getVrmaterial().getColorListList().stream().map(PicListResponse.Result.Vrmaterial.ColorList::getColorId).toList();
                                List<Integer> remoteColorIds = resultBuilder.getVrmaterial().getColorListList().stream().map(PicListResponse.Result.Vrmaterial.ColorList::getRemoteColorId).toList();
                                if (!colorIds.contains(request.getColorid()) && !remoteColorIds.contains(request.getColorid())) {
                                    resultBuilder.setVrmaterial(PicListResponse.Result.Vrmaterial.newBuilder().build());
                                    resultBuilder.setH5Vrinfo(PicListResponse.Result.H5vrinfo.newBuilder().build());
                                }
                            }
//                            //命中互动视频且存在，不返回h5vr  TODO  客户端反馈pad的时候不会显示互动视频，@邓凯 check一下
//                            if ("A".equals(request.getVideovrabtest())) {
//                                if (StringUtils.isNotEmpty(resultBuilder.getActionvideoinfo().getVideourl())) {
//                                    resultBuilder.setH5Vrinfo(PicListResponse.Result.H5vrinfo.newBuilder().build());
//                                }
//                            }

                            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")) {
                                //根据图片id计算图片在该tab的索引
                                Optional<PicListResponse.Result.Piclist> pic = resultBuilder.getPiclistList().stream()
                                        .filter(e -> Objects.equals(e.getMixid(), request.getMixid()))
                                        .findFirst();
                                if (pic.isPresent()) {
                                    resultBuilder.setPicindex(resultBuilder.getPiclistList().indexOf(pic.get()) +
                                            (resultBuilder.getPageindex() - 1) * request.getPagesize());
                                } else {
                                    resultBuilder.setPicindex(0);
                                }
                            } else {
                                //根据图片id计算图片在该tab的索引
                                Optional<PicListResponse.Result.Piclist> pic = resultBuilder.getPiclistList().stream()
                                        .filter(e -> Objects.equals(e.getId(), request.getPicid()))
                                        .findFirst();
                                if (pic.isPresent()) {
                                    resultBuilder.setPicindex(resultBuilder.getPiclistList().indexOf(pic.get()) +
                                            (resultBuilder.getPageindex() - 1) * request.getPagesize());
                                } else {
                                    resultBuilder.setPicindex(0);
                                }
                            }

                            return PicListResponse.newBuilder().setReturnCode(0).setResult(resultBuilder).build();
                        }
                ).join();
    }

    @Override
    @GetMapping(value = "/carbase/pic/getPicList", produces = "application/json;charset=utf-8")
    @CrossOrigin
    public PicListResponse getPicListPc(PicListRequest request) {
        SeriesDetailDto seriesDetail = seriesDetailComponent.getAsync(request.getSeriesid()).join();
        if (seriesDetail == null) {
            return PicListResponse.newBuilder().setReturnCode(0).setReturnMsg("车系不存在").build();
        }

        PicListResponse.Result.Builder resultBuilder = PicListResponse.Result.newBuilder();
        List<CompletableFuture> tasks = new ArrayList<>();

        CompletableFuture<List<SixtyPicListDto>> seriesSixtyPicFuture = seriesSixtyPicComponent.get(request.getSeriesid());

        CompletableFuture<List<Integer>> sixtyPicSpecIdsTask = seriesSixtyPicFuture.thenApply(x->{
            if(x==null || x.size()==0){
                return new ArrayList<>();
            }
            List<Integer> ids = new ArrayList<>();
            for (SixtyPicListDto dto : x) {
                ids.addAll(dto.getSpecpic().stream().map(SixtyPicListDto.SpecPic::getSpecid).toList());
            }
            return ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        });

        //获取图片列表
        tasks.add(carPhotoService.getPicList(request, resultBuilder, sixtyPicSpecIdsTask, null).thenAccept(picList -> {
            if (picList == null) {
                return;
            }
            resultBuilder.addAllPiclist(picList.getItems());
            resultBuilder.setRowcount(picList.getCount());
            resultBuilder.setPagecount(picList.getCount() / request.getPagesize() + (picList.getCount() % request.getPagesize() == 0 ? 0 : 1));
            resultBuilder.setPageindex(request.getPageindex());
        }).exceptionally(e -> {
            log.error("获取图片列表报错", e);
            return null;
        }));

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                .thenApply(x -> PicListResponse.newBuilder().setReturnCode(0).setResult(resultBuilder).build()
                ).join();
    }

    @Override
    @GetMapping(value = "/carbase/pic/getPicListShare", produces = "application/json;charset=utf-8")
    public PicListShareResponse getPicListShare(PicListShareRequest request) {
        HqPicDataDto hqPicDataDto = hqPicDataComponent.get(request.getSeriesid());
        PicListShareResponse.PicListShareResult result = carPhotoService.getPicListShare(request,hqPicDataDto);
        return PicListShareResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("success")
                .setResult(result)
                .build();
    }
}