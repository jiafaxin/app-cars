package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.PicListRequest;
import autohome.rpc.car.app_cars.v1.carbase.PicListResponse;
import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import com.autohome.app.cars.apiclient.baike.dtos.ConfigBaikeLinkDto;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.HttpUtils;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.StringUtils;
import com.autohome.app.cars.mapper.popauto.entities.PicParamConfigEntity;
import com.autohome.app.cars.service.components.baike.BaikelinkforconfigComponent;
import com.autohome.app.cars.service.components.car.PicParamConfigComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.file.MegaDataComponent;
import com.autohome.app.cars.service.services.dtos.MegaDataDto;
import com.autohome.app.cars.service.services.enums.SpecStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MegaService {


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    BaikelinkforconfigComponent baikelinkforconfigComponent;

    @Autowired
    PicParamConfigComponent picParamConfigComponent;

    @Autowired
    MegaDataComponent fileComponent;
    @Autowired
    SpecDetailComponent specDetailComponent;

    @Value("${piclist_usebigsize:0}")
    private int usebigsize;

    final static String shareUrlTemp = "https://comm.app.autohome.com.cn/comm_v1.0.0/cars/dutusharedcontent-pm%s-ss%s-sp%s-t%s-imgid%s.html";

    public void addMegaData(PicListResponse.Result.Builder builder, PicListRequest request) {
        MegaDataDto megaData = fileComponent.getMegaPicData(request.getSeriesid());

        if (null == megaData) {
            return;
        }

        builder.setMegastyle(1);
        //添加二级分类 secfilters
        List<MegaDataDto.Secfilters> secfilters = new ArrayList<>();
        Optional<MegaDataDto.Subtab> opt = megaData.getTab().stream().filter(e -> {

                    if (request.getCategoryid() > 0) {
                        return request.getCategoryid() == e.getTabId();
                    }
                    return true;
                }
        ).findFirst();


        if (opt.isPresent()) {
            secfilters = opt.get().getSecfilters().stream().filter(e ->
                    {
                        Optional<MegaDataDto.Pic> pic = megaData.getPiclist().stream().filter(d -> {
                            boolean isOne = true;
                            if (e.getFilterid() > 0) {
                                isOne = isOne && d.getSubtabId() == e.getFilterid();
                            }
                            if (request.getColorid() > 0) {
                                isOne = isOne && d.getColorId() == request.getColorid();
                            }
                            return isOne;
                        }).findAny();
                        return pic.isPresent();
                    }
            ).collect(Collectors.toList());
        }
        buildSecFilter(builder, secfilters, request);

        boolean fiterSpec =(megaData.getActionvideoinfo().getSpecid()>0 && request.getSpecid() >0)?request.getSpecid() ==megaData.getActionvideoinfo().getSpecid():true ;
       if(fiterSpec){
           //添加视频合集 videoalbum
           MegaDataDto.Videoalbum videoalbum = megaData.getVideoalbum();
           buildVideoalbum(builder, videoalbum, request);

           //互动视频增加megainfo
           if (1 == request.getCategoryid()) {
               addMegainfo2actionvideoinfo(builder, megaData.getActionvideoinfo(), request);
           }
       }

        //vrmaterial 补充 megainfo
        if (1 == request.getCategoryid()) {
            MegaDataDto.VRMegaInfo vrmaterial = megaData.getVrmaterial().get(0);
            addMegainfo2vrmaterial(builder, vrmaterial.getMegainfo(), request);
        }

        //h5vrinfo 补充 megainfo
        if (Arrays.asList(1, 10).contains(request.getCategoryid())) {
            MegaDataDto.VRMegaInfo h5vrinfo = megaData.getH5vrinfo().get(0);
            addMegainfo2h5vrinfo(builder, h5vrinfo.getMegainfo(), request);
        }

        //piclist
        List<MegaDataDto.Pic> picList = megaData.getPiclist().stream().filter(e -> {
            boolean isOne = true;
            if (request.getSpecid() > 0) {
                isOne = isOne && e.getSpecId() == request.getSpecid();
            }
            if (request.getCategoryid() > 0) {
                isOne = isOne && e.getTypeId() == request.getCategoryid();
            }
            if (request.getSectab() > 0) {
                isOne = isOne && e.getSubtabId() == request.getSectab();
            }
            if (request.getColorid() > 0) {
                isOne = isOne && e.getColorId() == request.getColorid();
            }
            return isOne;
        }).collect(Collectors.toList());

        if (!CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0")) {
            picList.removeIf(e -> 1 == e.getMediatype());
        }

//        if (picList.size() > 0) {
//            List<Integer> picColors = picList.stream().map(MegaDataDto.Pic::getColorId).distinct().collect(Collectors.toList());
//            List<MegaDataDto.Color> colors = megaData.getColorList().stream().filter(e -> picColors.contains(e.getId())).collect(Collectors.toList());
//            addColorinfo(builder, colors, request);
//        }


        int pageIndex = request.getPageindex();
        int rowcount = picList.size();
        int pageSize = 60;
        int pCount = rowcount % pageSize > 0 ? rowcount / pageSize + 1 : rowcount / pageSize;
        builder.setPagecount(pCount).setRowcount(rowcount).setPageindex(request.getPageindex());

        //根据图片id计算图片在该tab的索引
        Optional<MegaDataDto.Pic> pic = picList.stream()
                .filter(e -> Objects.equals(String.valueOf(e.getId()), request.getPicid()))
                .findFirst();
        if (pic.isPresent()) {
            builder.setPicindex(picList.indexOf(pic.get()));
        } else {
            builder.setPicindex(0);
        }

        List<MegaDataDto.Pic> pageList = picList.stream().skip((pageIndex - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        Map<Integer, String> specMap = new HashMap<>();
        specMap.put(59602, "2024款 Ultra");
        specMap.put(61882, "2023款 领航版");
        specMap.put(61883, "2023款 云辇旗舰版");
        specMap.put(68034, "2024款 云辇豪华版");
        specMap.put(65046, "2023款 探索版");

        CompletableFuture<Map<String, ConfigBaikeLinkDto>> baikeList = baikelinkforconfigComponent.getMap();
        CompletableFuture<Map<Integer, List<PicParamConfigEntity>>> picParamConfig = picParamConfigComponent.getMap(request.getSeriesid());

        List<PicListResponse.Result.Piclist> piclists = buildPicList(request, pageList, specMap, baikeList.join(), picParamConfig.join(), secfilters);

        builder.addAllPiclist(piclists);


    }

    public List<PicListResponse.Result.Piclist> buildPicList(PicListRequest request, List<MegaDataDto.Pic> list,
                                                             Map<Integer, String> specMap,
                                                             Map<String, ConfigBaikeLinkDto> baikeLinks,
                                                             Map<Integer, List<PicParamConfigEntity>> picParamConfig,
                                                             List<MegaDataDto.Secfilters> secfilters) {

        List<PicListResponse.Result.Piclist> result = new ArrayList<>();
        Map<Integer, String> secfilterMap = secfilters.stream()
                .collect(Collectors.toMap(MegaDataDto.Secfilters::getFilterid, MegaDataDto.Secfilters::getName,
                        (k1, k2) -> k2));

        for (MegaDataDto.Pic sourcePic : list) {
            String path = sourcePic.getPicurl();

            PicListResponse.Result.Piclist.Builder newItem = PicListResponse.Result.Piclist.newBuilder();
            newItem.setDifconfiglinkurl("");
            newItem.setId(sourcePic.getId() + "");
            int mixType = sourcePic.getMediatype() + 1;
            newItem.setMixid(mixType + "_" + sourcePic.getId());
            newItem.setSpecid(sourcePic.getSpecId() + "");
            newItem.setSpecname(specMap.get(sourcePic.getSpecId()));
            newItem.setLinkurl("autohome://car/specmain?specid=" + sourcePic.getSpecId());
            newItem.setTypeid(request.getCategoryid());
            newItem.setShareurl(HttpUtils.ToHttps(String.format(shareUrlTemp, request.getPm(), request.getSeriesid(), sourcePic.getSpecId(), request.getCategoryid(), newItem.getId())));
            newItem.setNowebppic(HttpUtils.ToHttps(ChangeLogoSeriesImageSize(path, 14)));
            newItem.setBigpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 4))));
            newItem.setSmallpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 16))));
            if (1 == usebigsize) {
                newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 15))));
            } else {
                newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(path, 14))));
            }
            if (1 == sourcePic.getMediatype()) {
                newItem.setNowebppic(HttpUtils.ToHttps(ChangeVideoImageSize(path, 14)));
                newItem.setBigpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeVideoImageSize(path, 4))));
                newItem.setSmallpic(HttpUtils.ToHttp(path));
                if (1 == usebigsize) {
                    newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeVideoImageSize(path, 15))));
                } else {
                    newItem.setHighpic(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeVideoImageSize(path, 14))));
                }
            }
            newItem.setOriginalpic("");
            if (!HttpUtils.ToHttp(path).equals(newItem.getHighpic().replace(".webp", ""))) {
                newItem.setOriginalpic(HttpUtils.ToHttp(path));
            }
            newItem.setHeight(sourcePic.getHeight());
            newItem.setWidth(sourcePic.getWidth());
            newItem.setDealerid(0);
            newItem.setDealername("");
            newItem.setIswallpaper(0);
            newItem.setOptiontips("");
            newItem.setAutoshowtitle("");
            int state = 20;
            newItem.setSalestate(state);
            newItem.setSalestatetip(SpecStateEnum.getNameByValueForPic(state));
            newItem.setCategoryid(request.getCategoryid());
            newItem.addAllConfiglist(getConfigItems(sourcePic.getId(), baikeLinks, picParamConfig));

            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0")) {
                newItem.setMediatype(sourcePic.getMediatype());
                newItem.setVid(StringUtils.isEmpty(sourcePic.getVid()) ? "" : sourcePic.getVid());
                newItem.setPointname(StringUtils.isEmpty(sourcePic.getPointname()) ? "" : sourcePic.getPointname());
            }

            newItem.setSecondtabid(sourcePic.getSubtabId());
            if (sourcePic.getSubtabId() != 0) {
                newItem.setSecondtabname(secfilterMap.get(sourcePic.getSubtabId()));
            } else {
                newItem.setSecondtabname("");
            }
            result.add(newItem.build());

        }
        return result;

    }

    private String ChangeLogoSeriesImageSize(String imageUrl, int imageIndex) {
        if (org.apache.commons.lang3.StringUtils.isBlank(imageUrl) || org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(imageUrl, ".webp") || org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(imageUrl, ".gif")) {
            return imageUrl;
        }
        int index = imageUrl.lastIndexOf("/");
        if (imageUrl.indexOf("autohomecar") > -1) {
            return imageUrl.substring(0, index + 1) + getCarLogoSizePrefix().get(imageIndex) + imageUrl.substring(index + 1);
        } else if (imageIndex == 16) {
            return imageUrl.substring(0, index + 1) + "400x300_q100_autohomecar__" + imageUrl.substring(index + 1);
        } else if (imageIndex == 55) {
            return imageUrl.substring(0, index + 1) + "300x0_autohomecar__" + imageUrl.substring(index + 1);
        } else if (imageIndex == 56) {
            return imageUrl.substring(0, index + 1) + "800x0_autohomecar__" + imageUrl.substring(index + 1);
        }
        return imageUrl;
    }

    private String ChangeVideoImageSize(String imageUrl, int imageIndex) {
        if (org.apache.commons.lang3.StringUtils.isBlank(imageUrl) || org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(imageUrl, ".webp") || org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(imageUrl, ".gif")) {
            return imageUrl;
        }
        int index = imageUrl.lastIndexOf("/");
        return imageUrl.substring(0, index + 1) + getCarLogoSizePrefix().get(imageIndex) + "autohomecar__" + imageUrl.substring(index + 1);
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
        CarLogoSizePrefix.put(55, "300x0_q87_");
        CarLogoSizePrefix.put(56, "800x0_q87_");
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
        List<PicParamConfigEntity> d1L = picParamConfig.get(picId).stream().filter(x -> x.getDataType() == 1 && org.apache.commons.lang3.StringUtils.isNotBlank(x.getValue())).collect(Collectors.toList());
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
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(linkDto.getVideocover())) {
                        b.setImg(linkDto.getVideocover());
                    } else if (org.apache.commons.lang3.StringUtils.isNotBlank(linkDto.getRepImg())) {
                        b.setImg(linkDto.getRepImg());
                    }
                }
            }
            list.add(b.build());
        }

        Map<String, List<PicParamConfigEntity>> d2L = picParamConfig.get(picId).stream().filter(x -> x.getDataType() == 2 && org.apache.commons.lang3.StringUtils.isNotBlank(x.getName())).collect(Collectors.groupingBy(x -> x.getName()));
        d2L.forEach((k, v) -> {
            PicListResponse.Result.Piclist.ConfigItem.Builder b = PicListResponse.Result.Piclist.ConfigItem.newBuilder();
            b.setName(k);
            b.setTypeid(2);

            List<String> vvs = v == null || v.size() == 0 ? new ArrayList<>() : v.stream().filter(x -> org.apache.commons.lang3.StringUtils.isNotBlank(x.getSubName())).map(x -> x.getSubName()).distinct().collect(Collectors.toList());
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
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(linkDto.getVideocover())) {
                        b.setImg(linkDto.getVideocover());
                    } else if (org.apache.commons.lang3.StringUtils.isNotBlank(linkDto.getRepImg())) {
                        b.setImg(linkDto.getRepImg());
                    }
                }
            }
            list.add(b.build());
        });
        return list;
    }

    public void buildSecFilter(PicListResponse.Result.Builder builder, List<MegaDataDto.Secfilters> secfiltersList, PicListRequest request) {

        int page = request.getSpecid() > 0 ? 1 : 0;

        for (MegaDataDto.Secfilters item : secfiltersList) {
            PicListResponse.Result.SecFilter.Builder secfilter = PicListResponse.Result.SecFilter.newBuilder();
            secfilter.setName(item.getName())
                    .setFilterid(item.getFilterid())
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("botton_name", item.getName())
                            .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                            .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                            .putArgvs("page", String.valueOf(page))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_filtrate_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_filtrate_show").build())
                            .build());
            builder.addSecfilters(secfilter.build());

        }
    }

    public void buildVideoalbum(PicListResponse.Result.Builder builder, MegaDataDto.Videoalbum videoalbum, PicListRequest request) {

        PicListResponse.Result.Videoalbum.Builder video = PicListResponse.Result.Videoalbum.newBuilder();

        for (MegaDataDto.Func func : videoalbum.getFunc()) {
            PicListResponse.Result.Videoalbum.Func.Builder fb = PicListResponse.Result.Videoalbum.Func.newBuilder();
            fb.setType(func.getType())
                    .setName(func.getName())
                    .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(func.getImgurl(), 55))))
                    .setSelimgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(func.getSelimgurl(), 55))));

            if (0 == func.getType()) {
                fb.setPvitem(Pvitem.newBuilder()
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_full_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_full_show").build()));
            }
            if (1 == func.getType()) {
                fb.setPvitem(Pvitem.newBuilder()
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_sound_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_sound_show").build()));
            }

            video.addFunc(fb.build());
        }

        List<MegaDataDto.Album> list = videoalbum.getAlbum().stream().filter(e -> {
            boolean isOne = true;
            if (request.getCategoryid() > 0) {
                isOne = isOne && e.getTabId() == request.getCategoryid();
            }
            if (request.getColorid() > 0) {
                isOne = isOne && e.getColorId() == request.getColorid();
            }
            return isOne;
        }).collect(Collectors.toList());

        for (MegaDataDto.Album album : list) {
            PicListResponse.Result.Videoalbum.Album.Builder albumBuilder = PicListResponse.Result.Videoalbum.Album.newBuilder();
            PicListResponse.Result.Videoalbum.Album.Info.Builder info = PicListResponse.Result.Videoalbum.Album.Info.newBuilder();
            info.setName(album.getInfo().getName())
                    .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(album.getInfo().getImgurl(), 55))))
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("botton_name", album.getInfo().getName())
                            .putArgvs("objectid", String.valueOf(album.getTabId()))
                            .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                            .putArgvs("specid", String.valueOf(request.getSpecid()))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));

            albumBuilder.setInfo(info.build());

            for (MegaDataDto.Videos videos : album.getVideos()) {
                PicListResponse.Result.Videoalbum.Album.Video.Builder v = PicListResponse.Result.Videoalbum.Album.Video.newBuilder();
                v.setName(videos.getName())
                        .setVid(videos.getVid())
                        .setNamenoindex(videos.getNamenoindex())
                        .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(ChangeLogoSeriesImageSize(videos.getImgurl(), 56))))
                        .setPvitem(Pvitem.newBuilder()
                                .putArgvs("botton_name", album.getInfo().getName())
                                .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                .putArgvs("objectid", String.valueOf(album.getTabId()))
                                .putArgvs("video_name", videos.getName())
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_piclist_multivideo_click").build())
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_piclist_multivideo_show").build()));


                albumBuilder.addVideos(v.build());
            }
            video.addAlbum(albumBuilder.build());
        }
        builder.setVideoalbum(video.build());

    }

    public void addMegainfo2actionvideoinfo(PicListResponse.Result.Builder builder, MegaDataDto.ActionVideoInfo data, PicListRequest request) {

        if (request.getColorid() > 0 && request.getColorid() != data.getColor()) {
            return;
        }

        PicListResponse.Result.MegaInfo.Builder megaInfo = PicListResponse.Result.MegaInfo.newBuilder();
        megaInfo.setName(data.getMegainfo().getName())
                .setImgurl(data.getMegainfo().getImgurl())
                .setPvitem(Pvitem.newBuilder()
                        .putArgvs("botton_name", data.getMegainfo().getName())
                        .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                        .putArgvs("specid", String.valueOf(request.getSpecid()))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));

        PicListResponse.Result.Actionvideoinfo.Builder actionVideoInfo = builder.getActionvideoinfo().toBuilder().setMegainfo(megaInfo.build());

        actionVideoInfo.setVideobytesize(data.getVideobytesize());
        actionVideoInfo.setVideourl(data.getVideourl());
        actionVideoInfo.setVideoimage(data.getVideoimage());
        actionVideoInfo.setScheme(data.getScheme());
        actionVideoInfo.setPvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "").putArgvs("specid", "59602")
                .setClick(Pvitem.Click.newBuilder().setEventid("car_interactive_entrance_click").build())
                .setShow(Pvitem.Show.newBuilder().setEventid("car_interactive_entrance_show").build()).build());
        actionVideoInfo.setFullvideoinfo(PicListResponse.Result.Actionvideoinfo.FullVideoinfo.newBuilder()
                .setVideourl(data.getFullvideoinfo().getVideourl())
                .setVideobytesize(data.getFullvideoinfo().getVideobytesize())
                .setVideoimage(data.getFullvideoinfo().getVideoimage()).build());

        actionVideoInfo.setSalestate(data.getSalestate());
        actionVideoInfo.setSalestatetip(data.getSalestatetip());
        actionVideoInfo.setName(data.getName());

        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0")) {
            PicListResponse.Result.Actionvideoinfo.Doorinfo.Builder doorinfo = PicListResponse.Result.Actionvideoinfo.Doorinfo.newBuilder();
            doorinfo.setVideourl(data.getDoorinfo().getVideourl())
                    .setVideoimage(data.getDoorinfo().getVideoimage())
                    .setIcon(data.getDoorinfo().getIcon())
                    .setSelicon(data.getDoorinfo().getSelicon())
                    .setPvitem(Pvitem.newBuilder().putArgvs("seriesid", request.getSeriesid() + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_pic_detail_list_open_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_pic_detail_list_open_show").build()).build());
            actionVideoInfo.setDoorinfo(doorinfo.build());

            actionVideoInfo.setFulldoorvideoinfo(PicListResponse.Result.Actionvideoinfo.FullVideoinfo.newBuilder()
                    .setVideourl(data.getFulldoorvideoinfo().getVideourl())
                    .setVideobytesize(data.getFulldoorvideoinfo().getVideobytesize())
                    .setVideoimage(data.getFulldoorvideoinfo().getVideoimage()).build());

        }

        builder.setActionvideoinfo(actionVideoInfo.build());

    }

    public void addMegainfo2vrmaterial(PicListResponse.Result.Builder builder, MegaDataDto.Megainfo megainfo, PicListRequest request) {

        PicListResponse.Result.MegaInfo.Builder megaInfo = PicListResponse.Result.MegaInfo.newBuilder();
        megaInfo.setName(megainfo.getName())
                .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(megainfo.getImgurl())))
                .setPvitem(Pvitem.newBuilder()
                        .putArgvs("botton_name", megaInfo.getName())
                        .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                        .putArgvs("specid", String.valueOf(request.getSpecid()))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));

        PicListResponse.Result.Vrmaterial.Builder vrbuilder = builder.getVrmaterial().toBuilder().setMegainfo(megaInfo.build());
        builder.setVrmaterial(vrbuilder.build());

    }

    public void addMegainfo2h5vrinfo(PicListResponse.Result.Builder builder, MegaDataDto.Megainfo megainfo, PicListRequest request) {

        PicListResponse.Result.MegaInfo.Builder megaInfo = PicListResponse.Result.MegaInfo.newBuilder();
        megaInfo.setName(megainfo.getName())
                .setImgurl(ImageUtils.convertImage_ToWebp(HttpUtils.ToHttp(megainfo.getImgurl())))
                .setPvitem(Pvitem.newBuilder()
                        .putArgvs("botton_name", megaInfo.getName())
                        .putArgvs("objectid", String.valueOf(request.getCategoryid()))
                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                        .putArgvs("specid", String.valueOf(request.getSpecid()))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_vr_entrance_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_vr_entrance_show").build()));

        PicListResponse.Result.H5vrinfo.Builder h5builder = builder.getH5Vrinfo().toBuilder().setMegainfo(megaInfo.build()).setLiteurl(builder.getH5Vrinfo().getLiteurl() + "&nocolor=1");
        builder.setH5Vrinfo(h5builder.build());


    }

    public void addColorinfo(PicListResponse.Result.Builder builder, List<MegaDataDto.Color> colors, PicListRequest request) {

        List<Integer> color = builder.getColorlistList().stream().map(e -> e.getId()).collect(Collectors.toList());

        List<MegaDataDto.Color> need = colors.stream().filter(e -> !color.contains(e.getId())).collect(Collectors.toList());

        for (MegaDataDto.Color c : need) {
            PicListResponse.Result.Colorlist.Builder b = PicListResponse.Result.Colorlist.newBuilder();
            b.setId(c.getId()).setName(c.getName()).setTag("").setValue(c.getValue());
            builder.addColorlist(b.build());
        }

    }

}
