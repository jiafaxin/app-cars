package com.autohome.app.cars.service.components.hqpic;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.car.HqPicMapper;
import com.autohome.app.cars.mapper.car.entities.*;
import com.autohome.app.cars.mapper.popauto.CarPhotoMapper;
import com.autohome.app.cars.mapper.popauto.entities.PicCountEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicDataDto;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicPrepareData;
import com.autohome.app.cars.service.components.hqpic.utils.HqPicSeriesShowUtil;
import com.autohome.app.cars.service.components.hqpic.utils.TypeIdTranslateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/8/8
 */
@Slf4j
@Component
@DBConfig(tableName = "series_hq_pic_data")
@RedisConfig(keyVersion = "v2")
public class HqPicDataComponent extends BaseComponent<HqPicDataDto> {

    @Autowired
    private HqPicMapper hqPicMapper;

    @Autowired
    private CarPhotoMapper carPhotoMapper;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    HqPicSeriesShowUtil hqPicSeriesShowUtil;

    final static List<Integer> outerColorTypeIds = List.of(1);
    final static List<Integer> innerColorTypeIds = List.of(2, 3);

    final static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public String get(TreeMap<String, Object> params) {
        HqPicDataDto dto = get((int) params.get("seriesId"));
        return JsonUtil.toString(dto);
    }

    public HqPicDataDto get(int seriesId) {
        if (hqPicSeriesShowUtil.seriesShow(seriesId)) {
            return baseGet(makeParam(seriesId));
        } else {
            return null;
        }
    }

    public CompletableFuture<HqPicDataDto> getAsync(int seriesId) {
        if (hqPicSeriesShowUtil.seriesShow(seriesId)) {
            return baseGetAsync(makeParam(seriesId));
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<Integer> seriesIdList = hqPicMapper.getSeriesIdList();
        HqPicPrepareData hqPicPrepareData = buildHqPicPrepareData();
        seriesIdList.forEach(seriesId -> {
            try {
                refresh(seriesId, hqPicPrepareData);
                xxlLog.accept("series:" + seriesId + "success");
            } catch (Exception e) {
                xxlLog.accept("series:" + seriesId + "fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    private HqPicPrepareData buildHqPicPrepareData() {
        // 外观颜色 id对应色值 map
        Map<Integer, String> colorValueMap = hqPicMapper.getHqOuterColor().stream()
                .collect(Collectors.toMap(HqColorEntity::getColorId,
                        HqColorEntity::getColorValue, (k1, k2) -> k2));
        // 外观颜色 id对应名称 map
        Map<Integer, String> colorNameMap = hqPicMapper.getHqOuterColor().stream()
                .collect(Collectors.toMap(HqColorEntity::getColorId,
                        HqColorEntity::getColorName, (k1, k2) -> k2));
        // 内饰颜色 id对应色值 map
        Map<Integer, String> innerColorValueMap = hqPicMapper.getHqInnerColor().stream()
                .collect(Collectors.toMap(HqColorEntity::getColorId,
                        HqColorEntity::getColorValue, (k1, k2) -> k2));
        // 内饰颜色 id对应名称 map
        Map<Integer, String> innerColorNameMap = hqPicMapper.getHqInnerColor().stream()
                .collect(Collectors.toMap(HqColorEntity::getColorId,
                        HqColorEntity::getColorName, (k1, k2) -> k2));
        // type id对应名称 map
        Map<Integer, String> typeNameMap = hqPicMapper.getHqPhotoType().stream()
                .collect(Collectors.toMap(HqPhotoTypeEntity::getTypeId,
                        HqPhotoTypeEntity::getTypeName, (k1, k2) -> k2));
        // video subType id对应名称 map
        Map<Integer, String> videoSubtypeMap = hqPicMapper.getHqVideoSubType().stream()
                .collect(Collectors.toMap(HqVideoSubTypeEntity::getVideoSubTypeId,
                        HqVideoSubTypeEntity::getVideoSubTypeName, (k1, k2) -> k2));
        // video type id对应排序 map
        Map<Integer, Integer> videoTypeSortMap = hqPicMapper.getHqVideoType().stream()
                .collect(Collectors.toMap(HqVideoTypeEntity::getVideoTypeId,
                        HqVideoTypeEntity::getVideoTypeSortId, (k1, k2) -> k2));
        // video subType id对应排序 map
        Map<Integer, Integer> videoSubTypeSortMap = hqPicMapper.getHqVideoSubType().stream()
                .collect(Collectors.toMap(HqVideoSubTypeEntity::getVideoSubTypeId,
                        HqVideoSubTypeEntity::getVideoSubTypeSortId, (k1, k2) -> k2));

        HqPicPrepareData hqPicPrepareData = new HqPicPrepareData();
        hqPicPrepareData.setColorValueMap(colorValueMap);
        hqPicPrepareData.setColorNameMap(colorNameMap);
        hqPicPrepareData.setInnerColorValueMap(innerColorValueMap);
        hqPicPrepareData.setInnerColorNameMap(innerColorNameMap);
        hqPicPrepareData.setTypeNameMap(typeNameMap);
        hqPicPrepareData.setVideoSubtypeMap(videoSubtypeMap);
        hqPicPrepareData.setVideoTypeSortMap(videoTypeSortMap);
        hqPicPrepareData.setVideoSubTypeSortMap(videoSubTypeSortMap);
        hqPicPrepareData.setPhotoSubTypeMap(getPhotoSubTypeMap());
        return hqPicPrepareData;
    }

    public void refresh(int seriesId, HqPicPrepareData hqPicPrepareData) {
        if (Objects.isNull(hqPicPrepareData)) {
            hqPicPrepareData = buildHqPicPrepareData();
        }
        HqPicDataDto hqPicChannelDto = build(seriesId, hqPicPrepareData);
        if (Objects.nonNull(hqPicChannelDto)) {
            update(makeParam(seriesId), hqPicChannelDto);
        } else {
            delete(makeParam(seriesId));
        }
    }

    public HqPicDataDto build(int seriesId, HqPicPrepareData hqPicPrepareData) {
        HqPicDataDto hqPicDataDto = new HqPicDataDto();
        hqPicDataDto.setSeriesId(seriesId);
        hqPicDataDto.setVideoAlbumList(buildVideoAlbumList(seriesId, hqPicPrepareData));
        hqPicDataDto.setRotateVideoAlbumList(buildRotateVideoInfoList(seriesId, hqPicPrepareData));
        List<HqPicDataDto.ColorInfo> colorInfoList = getColorInfoList(seriesId, hqPicPrepareData);
        hqPicDataDto.setColorInfoList(getColorInfoList(seriesId, hqPicPrepareData));
        hqPicDataDto.setSpecIdList(colorInfoList.stream().map(HqPicDataDto.ColorInfo::getSpecId).distinct().toList());
        hqPicDataDto.setPicCountList(getMixPicCountList(seriesId));
        hqPicDataDto.setPhotoSubTypeMap(hqPicPrepareData.getPhotoSubTypeMap());
        if (CollectionUtils.isEmpty(hqPicDataDto.getVideoAlbumList())
                && CollectionUtils.isEmpty(hqPicDataDto.getRotateVideoAlbumList())
                && CollectionUtils.isEmpty(hqPicDataDto.getColorInfoList())) {
            return null;
        }
        return hqPicDataDto;
    }

    private List<HqPicDataDto.RotateVideoAlbum> buildRotateVideoInfoList(int seriesId,
                                                                         HqPicPrepareData hqPicPrepareData) {
        List<HqPicDataDto.RotateVideoAlbum> rotateVideoAlbumList = new ArrayList<>();
        List<HqOrderSeriesSpec> seriesSpecList = hqPicMapper.getSeriesSpecBySeriesId(seriesId);
        seriesSpecList.forEach(spec -> {
            List<HqRotateVideoEntity> hqRotateVideoEntityList = hqPicMapper.getHqRotateVideo(seriesId, spec.getSpecId());
            if (!CollectionUtils.isEmpty(hqRotateVideoEntityList)) {
                // 按照color进行分组
                Map<Integer, List<HqRotateVideoEntity>> colorVideosMap = hqRotateVideoEntityList.stream()
                        .collect(Collectors.groupingBy(HqRotateVideoEntity::getColorId));
                List<HqPicDataDto.PointRotateVideo> pointRotateVideoList = new ArrayList<>();
                colorVideosMap.forEach((color, colorVideos) -> {
                    colorVideos.forEach(e -> {
                        // 对每一个点位构建dto
                        HqPicDataDto.PointRotateVideo pointRotateVideo = new HqPicDataDto.PointRotateVideo();
                        pointRotateVideo.setPointId(e.getPointId());

                        HqPicDataDto.RotateVideo rotateVideo = new HqPicDataDto.RotateVideo();
                        rotateVideo.setVideoUrl(e.getVideoUrl());
                        rotateVideo.setVid(e.getVideoMid());
                        rotateVideo.setImgUrl(e.getVideoLogoUrl());
                        rotateVideo.setByteSize(e.getVideoSize());
                        pointRotateVideo.setOriginVideo(rotateVideo);

                        if (List.of(1, 2).contains(e.getPointId())) {
                            HqPicDataDto.RotateVideo miniRotateVideo = new HqPicDataDto.RotateVideo();
                            miniRotateVideo.setVideoUrl(e.getMiniVideoUrl());
                            miniRotateVideo.setVid(e.getMiniVideoMid());
                            miniRotateVideo.setImgUrl(e.getVideoLogoUrl());
                            miniRotateVideo.setByteSize(e.getMiniVideoSize());
                            pointRotateVideo.setMiniVideo(miniRotateVideo);
                        }
                        pointRotateVideoList.add(pointRotateVideo);
                    });

                    HqPicDataDto.RotateVideoAlbum rotateVideoAlbum = new HqPicDataDto.RotateVideoAlbum();
                    rotateVideoAlbum.setSpecId(spec.getSpecId());
                    rotateVideoAlbum.setColorId(color);
                    rotateVideoAlbum.setColorValue(hqPicPrepareData.getColorValueMap().get(color));
                    rotateVideoAlbum.setColorName(hqPicPrepareData.getColorNameMap().get(color));
                    rotateVideoAlbum.setPointRotateVideoList(pointRotateVideoList);
                    rotateVideoAlbumList.add(rotateVideoAlbum);
                });
            }
        });
        return rotateVideoAlbumList;
    }

    private List<HqPicDataDto.VideoAlbum> buildVideoAlbumList(int seriesId,
                                                              HqPicPrepareData hqPicPrepareData) {
        List<HqPicDataDto.VideoAlbum> videoAlbumList = new ArrayList<>();
        List<HqOrderSeriesSpec> seriesSpecList = hqPicMapper.getSeriesSpecBySeriesId(seriesId);
        seriesSpecList.forEach(spec -> {
            List<HqVideoEntity> hqVideoEntityList = hqPicMapper.getHqVideo(seriesId, spec.getSpecId());
            if (!CollectionUtils.isEmpty(hqVideoEntityList)) {
                // 按照color进行分组
                Map<ColorKey, List<HqVideoEntity>> colorVideosMap = hqVideoEntityList.stream()
                        .collect(Collectors.groupingBy(e -> new ColorKey(e.getColorId(), e.getInnerColorId())));
                colorVideosMap.forEach((color, colorVideos) -> {
                    // 按照一级分类分组
                    Map<Integer, List<HqVideoEntity>> typeVideosMap = colorVideos.stream()
                            .collect(Collectors.groupingBy(HqVideoEntity::getTypeId));
                    List<HqPicDataDto.TypeAlbum> typeAlbumList = new ArrayList<>();
                    typeVideosMap.forEach((typeId, typeVideos) -> {
                        // 按照二级分类分组
                        Map<Integer, List<HqVideoEntity>> subTypeVideosMap = typeVideos.stream()
                                .collect(Collectors.groupingBy(HqVideoEntity::getSubTypeId));
                        List<HqPicDataDto.SubTypeAlbum> subTypeAlbumList = new ArrayList<>();
                        List<HqPicDataDto.SubTypeAlbum> finalSubTypeAlbumList = subTypeAlbumList;
                        subTypeVideosMap.forEach((subTypeId, subTypeVideos) -> {
                            // 对二级分类下的视频合集构建dto
                            HqPicDataDto.SubTypeAlbum subTypeAlbum = new HqPicDataDto.SubTypeAlbum();
                            subTypeAlbum.setSubTypeId(subTypeId);
                            subTypeAlbum.setSubTypeSortId(hqPicPrepareData.getVideoSubTypeSortMap().get(subTypeId));
                            subTypeAlbum.setAlbumName(hqPicPrepareData.getVideoSubtypeMap().get(subTypeId));
                            subTypeAlbum.setVideoList(subTypeVideos.stream()
                                    .sorted(Comparator.comparing(HqVideoEntity::getPointSortId))
                                    .map(e -> {
                                        HqPicDataDto.Video video = new HqPicDataDto.Video();
                                        video.setName(e.getPointName());
                                        video.setVid(e.getVideoMid());
                                        video.setImgUrl(e.getVideoLogoUrl());
                                        video.setPointSortId(e.getPointSortId() != 0 ? e.getPointSortId() : Integer.MAX_VALUE);
                                        return video;
                                    })
                                    .toList());
                            finalSubTypeAlbumList.add(subTypeAlbum);
                        });

                        subTypeAlbumList = subTypeAlbumList.stream()
                                .sorted(Comparator.comparing(HqPicDataDto.SubTypeAlbum::getSubTypeSortId)).toList();
                        HqPicDataDto.TypeAlbum typeAlbum = new HqPicDataDto.TypeAlbum();
                        typeAlbum.setTypeId(typeId);
                        typeAlbum.setTypeSortId(hqPicPrepareData.getVideoTypeSortMap().get(typeId));
                        typeAlbum.setSubTypeAlbumList(subTypeAlbumList);
                        typeAlbumList.add(typeAlbum);
                    });

                    HqPicDataDto.VideoAlbum videoAlbum = new HqPicDataDto.VideoAlbum();
                    videoAlbum.setSpecId(spec.getSpecId());
                    videoAlbum.setColorId(color.getColorId());
                    videoAlbum.setInnerColorId(color.getInnerColorId());
                    videoAlbum.setTypeAlbumList(typeAlbumList);
                    videoAlbumList.add(videoAlbum);
                });
            }
        });
        return videoAlbumList;
    }


    private List<HqPicDataDto.ColorInfo> getColorInfoList(int seriesId, HqPicPrepareData hqPicPrepareData) {
        List<HqPhotoEntity> hqPhotoEntityList = hqPicMapper.getHqPhoto(seriesId, 0);
        List<HqVideoEntity> hqVideoEntityList = hqPicMapper.getHqVideo(seriesId, 0);
        Set<Integer> specIds = new HashSet<>();
        List<PicDto> picList = new ArrayList<>();
        hqPhotoEntityList.forEach(p -> {
            PicDto picDto = new PicDto();
            picDto.setSpecId(p.getSpecId());
            picDto.setTypeId(p.getTypeId());
            picDto.setPicId("p" + p.getPhotoId());
            picDto.setColorId(p.getColorId());
            picDto.setInnerColorId(p.getInnerColorId());
            specIds.add(p.getSpecId());
            picList.add(picDto);
        });
        hqVideoEntityList.forEach(v -> {
            PicDto picDto = new PicDto();
            picDto.setSpecId(v.getSpecId());
            picDto.setTypeId(v.getTypeId());
            picDto.setPicId("v" + v.getVideoId());
            picDto.setColorId(v.getColorId());
            picDto.setInnerColorId(v.getInnerColorId());
            specIds.add(v.getSpecId());
            picList.add(picDto);
        });
        Map<Integer, SpecDetailDto> specDetailDtoMap = new HashMap<>();
        List<SpecDetailDto> specDetailDtos = specDetailComponent.mGet(specIds.stream().toList());
        if (Objects.nonNull(specDetailDtos)) {
            specDetailDtoMap.putAll(specDetailDtos.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecDetailDto::getSpecId, Function.identity(), (k1, k2) -> k2)));
        }
        Map<PicKey, List<PicDto>> outerPicKeyMap = picList.stream()
                .filter(e -> outerColorTypeIds.contains(e.getTypeId()))
                .collect(Collectors.groupingBy(e -> {
                    PicKey key = new PicKey();
                    key.setSpecId(e.getSpecId());
                    key.setTypeId(e.getTypeId());
                    key.setColorId(e.getColorId());
                    if (specDetailDtoMap.containsKey(e.getSpecId())) {
                        key.setIsOnsale(Arrays.asList(10, 20, 30).contains(specDetailDtoMap.get(e.getSpecId()).getState()) ? 1 : 0);
                    }
                    return key;
                }));
        Map<PicKey, List<PicDto>> innerPicKeyMap = picList.stream()
                .filter(e -> innerColorTypeIds.contains(e.getTypeId()))
                .collect(Collectors.groupingBy(e -> {
                    PicKey key = new PicKey();
                    key.setSpecId(e.getSpecId());
                    key.setTypeId(e.getTypeId());
                    key.setColorId(e.getInnerColorId());
                    if (specDetailDtoMap.containsKey(e.getSpecId())) {
                        key.setIsOnsale(Arrays.asList(10, 20, 30).contains(specDetailDtoMap.get(e.getSpecId()).getState()) ? 1 : 0);
                    }
                    return key;
                }));
        Map<PicKey, List<PicDto>> nightPicKeyMap = picList.stream()
                .filter(e -> Objects.equals(4, e.getTypeId()))
                .collect(Collectors.groupingBy(e -> {
                    PicKey key = new PicKey();
                    key.setSpecId(e.getSpecId());
                    key.setTypeId(e.getTypeId());
                    key.setColorId(e.getColorId());
                    if (specDetailDtoMap.containsKey(e.getSpecId())) {
                        key.setIsOnsale(Arrays.asList(10, 20, 30).contains(specDetailDtoMap.get(e.getSpecId()).getState()) ? 1 : 0);
                    }
                    return key;
                }));

        List<HqPicDataDto.ColorInfo> colorInfoList = new ArrayList<>();
        outerPicKeyMap.forEach((key, dtoList) -> colorInfoList.add(buildColorInfo(key, dtoList, hqPicPrepareData)));
        innerPicKeyMap.forEach((key, dtoList) -> colorInfoList.add(buildColorInfo(key, dtoList, hqPicPrepareData)));
        nightPicKeyMap.forEach((key, dtoList) -> colorInfoList.add(buildColorInfo(key, dtoList, hqPicPrepareData)));

        return colorInfoList;
    }

    private HqPicDataDto.ColorInfo buildColorInfo(PicKey picKey,
                                                  List<PicDto> picDtoList,
                                                  HqPicPrepareData hqPicPrepareData) {
        HqPicDataDto.ColorInfo colorInfo = new HqPicDataDto.ColorInfo();
        colorInfo.setSpecId(picKey.getSpecId());
        colorInfo.setIsOnsale(picKey.getIsOnsale());
        colorInfo.setHqTypeId(picKey.getTypeId());
        colorInfo.setCpkTypeId(TypeIdTranslateUtil.getTypeId2Cpk(picKey.getTypeId()));
        colorInfo.setTypeName(hqPicPrepareData.getTypeNameMap().get(picKey.getTypeId()));
        colorInfo.setColorId(picKey.getColorId());
        colorInfo.setColorValue(outerColorTypeIds.contains(picKey.getTypeId())
                ? hqPicPrepareData.getColorValueMap().get(picKey.getColorId())
                : hqPicPrepareData.getInnerColorValueMap().get(picKey.getColorId()));
        colorInfo.setColorName(outerColorTypeIds.contains(picKey.getTypeId())
                ? hqPicPrepareData.getColorNameMap().get(picKey.getColorId())
                : hqPicPrepareData.getInnerColorNameMap().get(picKey.getColorId()));
        colorInfo.setPicCount(picDtoList.size());
        return colorInfo;
    }

    private List<HqPicDataDto.PicCount> getMixPicCountList(int seriesId) {
        // 获取所有老图数量
        List<PicCountEntity> seriesOuterPicCountList = carPhotoMapper.getSeriesOuterPicCount(seriesId);
        List<PicCountEntity> specOuterPicCountList = carPhotoMapper.getSpecOuterPicCount(seriesId);
        List<PicCountEntity> seriesInnerPicCountList = carPhotoMapper.getSeriesInnerPicCount(seriesId);
        List<PicCountEntity> specInnerPicCountList = carPhotoMapper.getSpecInnerPicCount(seriesId);
        List<PicCountEntity> allOldPicCountList = new ArrayList<>();
        allOldPicCountList.addAll(seriesOuterPicCountList);
        allOldPicCountList.addAll(specOuterPicCountList);
        allOldPicCountList.addAll(seriesInnerPicCountList);
        allOldPicCountList.addAll(specInnerPicCountList);
        List<HqPicDataDto.PicCount> allPicCountList = new ArrayList<>(allOldPicCountList.stream().map(e -> {
            HqPicDataDto.PicCount picCountDto = new HqPicDataDto.PicCount();
            picCountDto.setSpecId(e.getSpecId());
            picCountDto.setHqTypeId(TypeIdTranslateUtil.getTypeId2HqPic(e.getTypeId()));
            picCountDto.setCpkTypeId(e.getTypeId());
            picCountDto.setColorId(e.getColorId());
            picCountDto.setInnerColorId(e.getInnerColorId());
            picCountDto.setSubTypeId(0);
            picCountDto.setPicCount(e.getPicCount());
            return picCountDto;
        }).toList());

        // 获取所有新图库中图片的count
        List<HqPicCountEntity> allHqPicCountList = new ArrayList<>();
        allHqPicCountList.addAll(hqPicMapper.getSeriesOuterPhotoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSpecOuterPhotoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSeriesInnerPhotoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSpecInnerPhotoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSeriesOuterVideoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSpecOuterVideoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSeriesInnerVideoCountList(seriesId));
        allHqPicCountList.addAll(hqPicMapper.getSpecInnerVideoCountList(seriesId));

        Map<PicCountSubTypeKey, List<Integer>> picCountKeyListMap = allHqPicCountList.stream()
                .collect(Collectors.groupingBy(e -> {
                    PicCountSubTypeKey picCountSubTypeKey = new PicCountSubTypeKey();
                    picCountSubTypeKey.setSpecId(e.getSpecId());
                    picCountSubTypeKey.setTypeId(e.getTypeId());
                    picCountSubTypeKey.setColorId(e.getColorId());
                    picCountSubTypeKey.setInnerColorId(e.getInnerColorId());
                    picCountSubTypeKey.setSubTypeId(e.getSubTypeId());
                    return picCountSubTypeKey;
                }, Collectors.mapping(HqPicCountEntity::getPicCount, Collectors.toList())));
        Map<PicCountSubTypeKey, Integer> picCountSubTypeMap = new HashMap<>();
        picCountKeyListMap.forEach((k, v) ->
                picCountSubTypeMap.put(k, v.stream().mapToInt(Integer::intValue).sum()));

        List<HqPicDataDto.PicCount> hqPicCountList = new ArrayList<>(picCountSubTypeMap.entrySet().stream().map(e -> {
            HqPicDataDto.PicCount picCountDto = new HqPicDataDto.PicCount();
            picCountDto.setSpecId(e.getKey().getSpecId());
            picCountDto.setHqTypeId(e.getKey().getTypeId());
            picCountDto.setCpkTypeId(TypeIdTranslateUtil.getTypeId2Cpk(e.getKey().getTypeId()));
            picCountDto.setColorId(e.getKey().getColorId());
            picCountDto.setInnerColorId(e.getKey().getInnerColorId());
            picCountDto.setSubTypeId(e.getKey().getSubTypeId());
            picCountDto.setPicCount(e.getValue());
            return picCountDto;
        }).toList());
        allPicCountList.addAll(hqPicCountList);

        Map<PicCountKey, List<Integer>> picCountMap = hqPicCountList.stream()
                .collect(Collectors.groupingBy(e -> {
                    PicCountKey picCountKey = new PicCountKey();
                    picCountKey.setSpecId(e.getSpecId());
                    picCountKey.setHqTypeId(e.getHqTypeId());
                    picCountKey.setCpkTypeId(e.getCpkTypeId());
                    picCountKey.setColorId(e.getColorId());
                    picCountKey.setInnerColorId(e.getInnerColorId());
                    return picCountKey;
                }, Collectors.mapping(HqPicDataDto.PicCount::getPicCount, Collectors.toList())));
        Map<PicCountKey, Integer> picCountKeyCount = new HashMap<>();
        picCountMap.forEach((k, v) ->
                picCountKeyCount.put(k, v.stream().mapToInt(Integer::intValue).sum()));

        Set<PicCountKey> usedPicCountKeySet = new HashSet<>();
        allPicCountList.forEach(e -> {
            if (e.getSubTypeId() == 0) {
                PicCountKey picCountKey = new PicCountKey();
                picCountKey.setSpecId(e.getSpecId());
                picCountKey.setHqTypeId(e.getHqTypeId());
                picCountKey.setCpkTypeId(e.getCpkTypeId());
                picCountKey.setColorId(e.getColorId());
                picCountKey.setInnerColorId(e.getInnerColorId());
                Integer picCount = picCountKeyCount.get(picCountKey);
                if (Objects.nonNull(picCount)) {
                    e.setPicCount(e.getPicCount() + picCount);
                    usedPicCountKeySet.add(picCountKey);
                }
            }
        });

        picCountKeyCount.forEach((k, v) -> {
            if (!usedPicCountKeySet.contains(k)) {
                if (k.getHqTypeId() != 4) {
                    HqPicDataDto.PicCount picCountDto = new HqPicDataDto.PicCount();
                    picCountDto.setSpecId(k.getSpecId());
                    picCountDto.setHqTypeId(k.getHqTypeId());
                    picCountDto.setCpkTypeId(k.getCpkTypeId());
                    picCountDto.setColorId(k.getColorId());
                    picCountDto.setInnerColorId(k.getInnerColorId());
                    picCountDto.setSubTypeId(0);
                    picCountDto.setPicCount(v);
                    allPicCountList.add(picCountDto);
                }
            }
        });

        return allPicCountList;
    }

    private Map<Integer, List<HqPicDataDto.PhotoSubTypeDto>> getPhotoSubTypeMap() {
        Map<Integer, List<HqPicDataDto.PhotoSubTypeDto>> photoSubTypeMap = new HashMap<>();
        List<HqPhotoSubTypeEntity> subTypeEntities = hqPicMapper.getHqPhotoSubType();
        Map<Integer, List<HqPhotoSubTypeEntity>> typeSubTypeMap = subTypeEntities.stream()
                .collect(Collectors.groupingBy(HqPhotoSubTypeEntity::getPhotoTypeId));
        typeSubTypeMap.forEach((key, subTypes) -> {
            List<HqPicDataDto.PhotoSubTypeDto> subTypeDtoList = subTypes.stream().map(e -> {
                        HqPicDataDto.PhotoSubTypeDto hqPhotoSubTypeDto = new HqPicDataDto.PhotoSubTypeDto();
                        hqPhotoSubTypeDto.setSubTypeId(e.getPhotoSubTypeId());
                        hqPhotoSubTypeDto.setSubTypeName(e.getPhotoSubTypeName());
                        hqPhotoSubTypeDto.setSubTypeSortId(e.getPhotoSubTypeSortId());
                        return hqPhotoSubTypeDto;
                    })
                    .toList();
            photoSubTypeMap.put(key, subTypeDtoList);
        });
        return photoSubTypeMap;
    }

    @Data
    public static class PicDto {
        private int specId;
        private int typeId;
        private int colorId;
        private int innerColorId;
        private String picId;
    }

    @Data
    public static class PicKey {
        private int specId;
        private int typeId;
        private int colorId;
        private int isOnsale;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorKey {
        private int colorId;
        private int innerColorId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PicCountKey {
        private int specId;
        private int hqTypeId;
        private int cpkTypeId;
        private int colorId;
        private int innerColorId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PicCountSubTypeKey {
        private int specId;
        private int typeId;
        private int colorId;
        private int innerColorId;
        private int subTypeId;
    }
}
