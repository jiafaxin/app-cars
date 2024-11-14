package com.autohome.app.cars.service.components.car;


import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.mapper.popauto.CarPhotoViewMapper;
import com.autohome.app.cars.mapper.popauto.SpecPicClassStatisticsMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecPicInfoDto;
import com.autohome.app.cars.service.components.vr.SpecVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/3/7
 */
@Component
@RedisConfig
@DBConfig(tableName = "spec_pic_info")
@Slf4j
public class SpecPicInfoComponent extends BaseComponent<SpecPicInfoDto> {

    @Autowired
    private SeriesAttentionComponent seriesAttentionComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    private SpecVrComponent specVrComponent;

    @Autowired
    private CarPhotoViewMapper carPhotoViewMapper;

    @Autowired
    SpecPicClassStatisticsMapper specPicClassStatisticsMapper;

    static String specIdParamName = "specId";

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecPicInfoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    /**
     * 拉取所有数据到redis&db
     */
    public void refreshAll(int totalMinutes, Consumer<String> log) {
        loopShardingSpec(totalMinutes, specId -> {
            try {
                SpecPicInfoDto specPicInfoDto = getSpecPicInfo(specId);
                if (Objects.nonNull(specPicInfoDto)) {
                    update(ParamBuilder.create(specIdParamName, specId).build(), specPicInfoDto);
                }
                log.accept(specId + "success");
            } catch (Exception e) {
                log.accept(specId + "fail:" + ExceptionUtil.getStackTrace(e));
            }
        }, log);
    }

    private SpecPicInfoDto getSpecPicInfo(int specId) {
        SpecPicInfoDto specPicInfoDto = new SpecPicInfoDto();
        try {
            // 获取车系车型基本信息
            SpecDetailDto specDetailDto = specDetailComponent.get(specId).join();
            if (specDetailDto == null) {
                return null;
            }

            // 获取车系vr信息
            SpecVrInfoDto specVrInfoDto = specVrComponent.get(specId).join();

            int seriesId = specDetailDto.getSeriesId();
            String seriesName = specDetailDto.getSeriesName();
            int hotSpecId = specId;

            // 获取热点车型
            SeriesAttentionDto seriesAttentionDto = seriesAttentionComponent.get(specDetailDto.getSeriesId()).join();
            if (seriesAttentionDto != null
                    && seriesAttentionDto.getSpecAttentions() != null
                    && !seriesAttentionDto.getSpecAttentions().isEmpty()) {
                hotSpecId = seriesAttentionDto.getSpecAttentions().get(0).getSpecid();
            }

            // 获取车型下不同分类的照片
            List<CarPicItem> carPicItemListBySpec = getCarPicItemListBySpec(seriesId, specId);

            // 获取车型的照片数量
            int specPicCount = getPicCount(specId, seriesId);
            specPicInfoDto.setSpecOrigPicCount(specPicCount);

            // 构建车型照片的picList
            specPicInfoDto.setPicIcon("http://nfiles3.autohome.com.cn/zrjcpk10/car_spec_piccount_icon@3x.png");
            if (specPicCount > 1) {
                specPicInfoDto.setPicCount(specPicCount + "张");
                specPicInfoDto.setLinkUrl(
                        String.format("autohome://car/specpicture?seriesid=%s&specid=%s&orgin=1", seriesId, specId));
                setPicListOfPicInfo(specPicInfoDto, specVrInfoDto, carPicItemListBySpec, seriesId, specId, seriesName, true);
            }

            // 当前车型无实拍则取该车系的
            if (specPicInfoDto.getPicList().isEmpty()) {
                specPicInfoDto.setTip("");
                specPicInfoDto.setPicCount("");
                specPicInfoDto.setLinkUrl("");
                SpecVrInfoDto newIsVrBySeriesId = null;
                if (hotSpecId != specId) {
                    newIsVrBySeriesId = specVrComponent.get(hotSpecId).join();
                }
                List<CarPicItem> carPicItemListBySeries = getCarPicItemListBySeries(seriesId);
                //车系图片列表对应的tab
                String url= String.format("autohome://car/seriespicture?seriesid=%s&orgin=1&seriesname=%s",
                        seriesId, UrlUtil.encode(seriesName).replace("+", "%20"));
                specPicInfoDto.setLinkUrl(url);
                setPicListOfPicInfo(specPicInfoDto, newIsVrBySeriesId, carPicItemListBySeries, seriesId, specId, seriesName, false);
                if (specPicInfoDto.getPicList().size() >= 3) {
                    specPicInfoDto.setTip("当前车型暂无实拍图，为您展示其他车型图片供参考");
                } else {
                    specPicInfoDto.setTip("");
                    specPicInfoDto.setPicCount("");
                    specPicInfoDto.setLinkUrl("");
                    specPicInfoDto.getPicList().clear();
                }
            }
        } catch (Exception e) {
            log.error("获取specPicInfo异常，车型specId:{}", specId, e);
            return null;
        }
        return specPicInfoDto;
    }

    /**
     * 获取车型下不同分类的照片
     * 参考接口逻辑：http://car.api.autohome.com.cn/v1/carpic/spec_classpicturebyspecId.ashx?_appid=app&specid={specid}
     *
     * @param seriesId 车系Id
     * @param specId   车型Id
     * @return List<CarPicItem>
     */
    private List<CarPicItem> getCarPicItemListBySpec(int seriesId, int specId) {
        // 根据车系获取照片信息
        List<CarPhotoViewEntity> carPhotoViewEntityList = carPhotoViewMapper.getPhotoViewBySeries(seriesId);
        if (CollectionUtils.isEmpty(carPhotoViewEntityList)) {
            return Collections.emptyList();
        }
        // 过滤出车型并排序
        carPhotoViewEntityList = filterAndSortCarPhotoBySpec(carPhotoViewEntityList, specId, false);
        // 按照type类型进行分组
        return buildCarPicItemList(carPhotoViewEntityList);
    }

    /**
     * 获取车型真实的图片数量
     *
     * @param specId
     * @param seriesId
     * @return
     */
    private int getPicCount(int specId, int seriesId) {
        List<SpecPicColorStatisticsEntity> list = Spec.isCvSpec(specId)
                ? specPicClassStatisticsMapper.getCVSpecPicClassStatisticsBySeriesId(seriesId)
                : specPicClassStatisticsMapper.getSpecPicClassStatisticsBySeriesId(seriesId);
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.stream()
                .filter(e -> e.getSpecId() == specId)
                .filter(e -> e.getPicClass() != 13 && e.getPicClass() != 51 && e.getPicClass() != 15)
                .map(SpecPicColorStatisticsEntity::getPicNumber).mapToInt(Integer::intValue).sum();
    }


    /**
     * 按照type类型进行分组
     *
     * @return List<CarPicItem>
     */
    private List<CarPicItem> buildCarPicItemList(List<CarPhotoViewEntity> carPhotoViewEntityList) {
        Map<Integer, List<CarPhotoViewEntity>> picClass2EntityMap = carPhotoViewEntityList.stream()
                .collect(Collectors.groupingBy(CarPhotoViewEntity::getPicClass,
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)));
        List<CarPicItem> typeItems = new ArrayList<>();
        for (Map.Entry<Integer, List<CarPhotoViewEntity>> carPhotoViewMap : picClass2EntityMap.entrySet()) {
            // 按照分类取第一张照片
            CarPicItem carPicItem = new CarPicItem();
            carPicItem.setTypeId(carPhotoViewMap.getKey());
            carPicItem.setFilepath(ImageUtils.getFullImagePathWithoutReplace(carPhotoViewMap.getValue().get(0).getPicFilePath()));
            carPicItem.setPicCount(carPhotoViewMap.getValue().size());
            carPicItem.setPicItems(carPhotoViewMap.getValue().stream().map(x ->
                    new SpecPicInfoDto.PicItem(x.getPicId(), ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), x.getSpecId())).collect(Collectors.toList()));
            typeItems.add(carPicItem);
        }
        return typeItems;
    }

    public List<CarPhotoViewEntity> filterAndSortCarPhotoBySpec(List<CarPhotoViewEntity> list, int specId, boolean hasClub) {
        return list.stream().filter(
                item -> item.getSpecId() == (specId) &&
                        (hasClub || item.getIsClubPhoto() == (0))
        ).sorted(Comparator.comparing(CarPhotoViewEntity::getClassOrder)
                .thenComparing(CarPhotoViewEntity::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewEntity::getSourceTypeOrder)
                .thenComparing(CarPhotoViewEntity::getDealerPicOrder)
                .thenComparing(CarPhotoViewEntity::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewEntity::getPicId, Comparator.reverseOrder())
        ).collect(Collectors.toList());
    }

    /**
     * 获取车系下不同分类的照片
     * 参考接口逻辑：http://car.api.autohome.com.cn/v1/carpic/series_classpicturebyseriesId.ashx?_appid=app&seriesid={seriesid}
     *
     * @param seriesId 车系Id
     * @return List<CarPicItem>
     */
    private List<CarPicItem> getCarPicItemListBySeries(int seriesId) {
        // 根据车系获取照片信息
        List<CarPhotoViewEntity> carPhotoViewEntityList = carPhotoViewMapper.getPhotoViewClassPicTop10BySeriesId(seriesId);
        if (CollectionUtils.isEmpty(carPhotoViewEntityList)) {
            return Collections.emptyList();
        }
        // 排序
        carPhotoViewEntityList = sortCarPhotoBySeries(carPhotoViewEntityList);
        // 按照type类型进行分组
        return buildCarPicItemList(carPhotoViewEntityList);
    }

    public List<CarPhotoViewEntity> sortCarPhotoBySeries(List<CarPhotoViewEntity> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list = list.stream().filter(s -> s.getSpecPicNumber() > 2)
                    .sorted(Comparator.comparing(CarPhotoViewEntity::getClassOrder)
                            .thenComparing(CarPhotoViewEntity::getShowId, Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewEntity::getStateOrder)
                            .thenComparing(CarPhotoViewEntity::getIsclassic)
                            .thenComparing(CarPhotoViewEntity::getSourceTypeOrder)
                            .thenComparing(CarPhotoViewEntity::getDealerPicOrder)
                            .thenComparing(CarPhotoViewEntity::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                            .thenComparing(CarPhotoViewEntity::getPicId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        return list;
    }


    private void setPicListOfPicInfo(SpecPicInfoDto specPicInfoDto,
                                     SpecVrInfoDto specVrInfoDto,
                                     List<CarPicItem> carPicItemList,
                                     int seriesId,
                                     int specId,
                                     String seriesName,
                                     boolean picBySpec) {
        //vr
        if (specVrInfoDto != null) {
            // 外观;
            if (specVrInfoDto.isHasExterior()
                    && specVrInfoDto.getExtInfo() != null
                    && !specVrInfoDto.getExtInfo().isEmpty()
                    && specVrInfoDto.getExtInfo().get(0).getIs_show()) {
                SpecPicInfoDto.PicListDto vrExt = new SpecPicInfoDto.PicListDto();
                vrExt.setIcon(ImageUtils.convertImage_Size_VRImg(specVrInfoDto.getExtInfo().get(0).getCoverUrl(),
                        ImageSizeEnum.ImgSize_4x3_400x300));
                vrExt.setTag("VR外观");
                vrExt.setIsOutVr(1);
                vrExt.setLinkUrl(buildLinkUrl(seriesId, specId, seriesName, picBySpec, 1));
                specPicInfoDto.getPicList().add(vrExt);
            }
            // 内饰;
            if (specVrInfoDto.getIntInfo() != null
                    && !specVrInfoDto.getIntInfo().isEmpty()) {
                SpecPicInfoDto.PicListDto vrInt = new SpecPicInfoDto.PicListDto();
                vrInt.setIcon(ImageUtils.convertImage_Size_VRImg(specVrInfoDto.getIntInfo().get(0).getCoverUrl(),
                        ImageSizeEnum.ImgSize_4x3_400x300));
                vrInt.setTag("VR内饰");
                vrInt.setIsOutVr(0);
                vrInt.setLinkUrl(buildLinkUrl(seriesId, specId, seriesName, picBySpec, 10));
                specPicInfoDto.getPicList().add(vrInt);
            }
        }
        //车图
        if (CollectionUtils.isNotEmpty(carPicItemList)) {
            carPicItemList.forEach(x -> {
                if (x.getTypeId() == 1 || x.getTypeId() == 10 || x.getTypeId() == 3 || x.getTypeId() == 12) {
                    if (StringUtils.isNotEmpty(x.getFilepath())) {
                        SpecPicInfoDto.PicListDto picInt = new SpecPicInfoDto.PicListDto();
                        picInt.setIcon(ImageUtils.convertImage_SizeWebp(x.getFilepath(),
                                ImageSizeEnum.ImgSize_4x3_400x300));
                        switch (x.getTypeId()) {
                            case 1:
                                picInt.setTag("外观");
                                break;
                            case 10:
                                picInt.setTag("中控");
                                break;
                            case 3:
                                picInt.setTag("座椅");
                                break;
                            case 12:
                                picInt.setTag("细节");
                                break;
                            default:
                                picInt.setTag("");
                                break;
                        }
                        picInt.setIsOutVr(0);
                        picInt.setLinkUrl(buildLinkUrl(seriesId, specId, seriesName, picBySpec, x.typeId));
                        picInt.setPicCount(x.getPicCount());
                        // 只取前5张
                        picInt.setPicItems(x.getPicItems().stream().limit(5).collect(Collectors.toList()));
                        specPicInfoDto.getPicList().add(picInt);
                    }
                }
            });
        }
    }

    private String buildLinkUrl(int seriesId, int specId, String seriesName, boolean picBySpec, int typeId) {
        if (picBySpec) {
            return String.format("autohome://car/specpicture?seriesid=%s&specid=%s&orgin=1&categoryid=%s",
                    seriesId, specId, typeId);
        } else {
            return String.format("autohome://car/seriespicture?seriesid=%s&orgin=1&seriesname=%s&categoryid=%s",
                    seriesId, UrlUtil.encode(seriesName).replace("+", "%20"), typeId);
        }
    }

    @Data
    public static class CarPicItem {
        private int typeId;
        private String filepath;
        private int picCount;
        private List<SpecPicInfoDto.PicItem> picItems = new ArrayList<>();
    }
}
