package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.Level;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.CarFctColorMapper;
import com.autohome.app.cars.mapper.popauto.InnerFctColorMapper;
import com.autohome.app.cars.mapper.popauto.SpecColorMapper;
import com.autohome.app.cars.mapper.popauto.SpecInnerColorStatisticsMapper;
import com.autohome.app.cars.mapper.popauto.SpecPicColorStatisticsMapper;
import com.autohome.app.cars.mapper.popauto.entities.ColorInfoEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecColorListBaseInfoEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecColorListEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecPicColorStatisticsEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecColorDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.ColorBaseInfoDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecColorListItemsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_out_in_color")
public class SpecColorComponent extends BaseComponent<SpecColorDto> {

    private static String specIdParamName = "specId";
    private static String innerColorParamName = "innerColor";

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecColorMapper specColorMapper;
    @Autowired
    private SpecInnerColorStatisticsMapper specInnerColorStatisticsMapper;
    @Autowired
    private SpecPicColorStatisticsMapper specPicColorStatisticsMapper;
    @Autowired
    private InnerFctColorMapper innerFctColorMapper;
    @Autowired
    private CarFctColorMapper carFctColorMapper;

    TreeMap<String, Object> makeParam(int specId, boolean inner) {
        return BaseComponent.ParamBuilder.create(specIdParamName, specId).add(innerColorParamName, inner ? 1 : 0).build();
    }

    TreeMap<String, Object> makeParam2(int specId, boolean inner) {
        return BaseComponent.ParamBuilder.create(specIdParamName, specId).add(innerColorParamName, inner).build();
    }

    public CompletableFuture<SpecColorDto> get(int specId, boolean inner) {
        return baseGetAsync(makeParam(specId, inner));
    }

    public CompletableFuture<List<SpecColorDto>> get(List<Integer> specIdList, boolean inner) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x, inner)).collect(Collectors.toList()));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        seriesIds.forEach(seriesId -> {
            try {
                //内饰颜色
                getData(seriesId, true);
                //外观颜色
                getData(seriesId, false);
                ThreadUtil.sleep(50);
                xxlLog.accept("inner seriesId = " + seriesId + " success");
            } catch (Exception e) {
                xxlLog.accept("inner seriesId = " + seriesId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    public void refresh(int specId) {
        try {
            SpecDetailDto specDetail = specDetailComponent.get(specId).join();
            if(specDetail != null){
                //内饰颜色
                getData(specDetail.getSeriesId(), true);
                //外观颜色
                getData(specDetail.getSeriesId(), false);
            }
        } catch (Exception e) {
            log.error("刷新车型外观内饰颜色异常-exception:{}", e);
        }
    }

    private void getData(int seriesId, boolean inner) {
        SpecColorListItemsDto specColorListItems = getSpecColorListBySeriesId(seriesId, inner);
        if (specColorListItems != null && !CollectionUtils.isEmpty(specColorListItems.getSpecitems())) {
            specColorListItems.getSpecitems().forEach(item -> {
                delete(makeParam2(item.getSpecid(), inner));
                if (!CollectionUtils.isEmpty(item.getColoritems())) {
                    SpecColorDto dto = new SpecColorDto();
                    dto.setSpecid(item.getSpecid());
                    dto.setColoritems(item.getColoritems());
                    update(makeParam(item.getSpecid(), inner), dto);
                }
            });
        }
    }

    private SpecColorListItemsDto getSpecColorListBySeriesId(int seriesId, boolean inner) {
        if (seriesId == 0) {
            return null;
        }
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
        if (Objects.isNull(seriesDetailDto)) {
            return null;
        }
        //商用车无颜色
        if (Level.isCVLevel(seriesDetailDto.getLevelId())) {
            return null;
        }
        List<SpecColorListBaseInfoEntity> specColors = inner ? getInnerColorData(seriesId) : getOutColorData(seriesId);
        List<SpecPicColorStatisticsEntity> specColorEntities = inner ? getInnerPicColorData(seriesId) : getOutPicColorData(seriesId);
        if (CollectionUtils.isEmpty(specColors)) {
            return null;
        }
        for (SpecColorListBaseInfoEntity specColor : specColors) {
            List<SpecPicColorStatisticsEntity> specPicColorStatisticsItem;
            if (!CollectionUtils.isEmpty(specColorEntities)) {
                specPicColorStatisticsItem = specColorEntities.stream()
                        .filter(s -> specColor.getSpecId() == s.getSpecId())
                        .filter(s -> specColor.getColorId() == s.getColorId())
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(specPicColorStatisticsItem)) {
                    specColor.setPicNumber(specPicColorStatisticsItem.stream().mapToInt(SpecPicColorStatisticsEntity::getPicNumber).sum());
                    specColor.setClubPicNumber(specPicColorStatisticsItem.stream().mapToInt(SpecPicColorStatisticsEntity::getClubPicNumber).sum());
                }
            }
        }

        specColors.sort(Comparator.comparing(SpecColorListBaseInfoEntity::getSpecId)
                .thenComparing(Comparator.comparing(SpecColorListBaseInfoEntity::getPicNumber).reversed()));

        List<Integer> colorIds = specColors.stream().map(SpecColorListBaseInfoEntity::getColorId).collect(Collectors.toList());
        Map<Integer, ColorBaseInfoDto> colorBaseInfoMap = getColorMap(colorIds, inner);

        Map<Integer, List<SpecColorListItemsDto.ColorItem>> colorItemMap = specColors.stream()
                .collect(Collectors.groupingBy(SpecColorListBaseInfoEntity::getSpecId, Collectors.mapping(specInnerColor -> {
                    ColorBaseInfoDto colorBaseInfo = CollectionUtils.isEmpty(colorBaseInfoMap) ? null : colorBaseInfoMap.get(specInnerColor.getColorId());
                    SpecColorListItemsDto.ColorItem colorItem = new SpecColorListItemsDto.ColorItem();
                    colorItem.setId(specInnerColor.getColorId());
                    colorItem.setName(colorBaseInfo != null ? colorBaseInfo.getName() : "");
                    colorItem.setValue(colorBaseInfo != null ? colorBaseInfo.getValue() : "");
                    colorItem.setPicnum(specInnerColor.getPicNumber());
                    colorItem.setClubpicnum(specInnerColor.getClubPicNumber());
                    colorItem.setPrice(specInnerColor.getPrice());
                    colorItem.setRemark(specInnerColor.getRemarks());
                    return colorItem;
                }, Collectors.toList())));

        List<SpecColorListItemsDto.SpecItem> specItems = specColors.stream().map(specInnerColor -> {
                    List<SpecColorListItemsDto.ColorItem> colorItems = colorItemMap.get(specInnerColor.getSpecId());
                    if (!CollectionUtils.isEmpty(colorItems)) {
                        SpecColorListItemsDto.SpecItem specItem = new SpecColorListItemsDto.SpecItem();
                        specItem.setSpecid(specInnerColor.getSpecId());
                        specItem.setColoritems(colorItems);
                        return specItem;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        SpecColorListItemsDto result = new SpecColorListItemsDto();
        result.setTotal(specItems.size());
        result.setSpecitems(specItems);

        return result;
    }

    protected List<SpecColorListBaseInfoEntity> getInnerColorData(int seriesId) {
        List<SpecColorListEntity> infos = specColorMapper.getOnSoldSpecInnerColorList(seriesId);
        return infos.stream().map(x -> new SpecColorListBaseInfoEntity() {{
            setSeriesId(x.getSeriesId());
            setSpecId(x.getSpecId());
            setColorId(x.getColorId());
            setPicNumber(x.getPicNumber());
            setClubPicNumber(x.getClubPicNumber());
        }}).collect(Collectors.toList());
    }

    protected List<SpecColorListBaseInfoEntity> getOutColorData(int seriesId) {
        List<SpecColorListEntity> infos = specColorMapper.getOnSoldSpecSpecColorList(seriesId);
        return infos.stream().map(x -> new SpecColorListBaseInfoEntity() {{
            setSeriesId(x.getSeriesId());
            setSpecId(x.getSpecId());
            setColorId(x.getColorId());
            setPicNumber(x.getPicNumber());
            setClubPicNumber(x.getClubPicNumber());
            setPrice(x.getPrice());
            setRemarks(x.getRemarks());
        }}).collect(Collectors.toList());
    }

    protected List<SpecPicColorStatisticsEntity> getInnerPicColorData(int seriesId) {
        List<SpecPicColorStatisticsEntity> list = specInnerColorStatisticsMapper.getSpecInnerColorStatisticsBySeriesId(seriesId);
        return list;
    }

    protected List<SpecPicColorStatisticsEntity> getOutPicColorData(int seriesid) {
        List<SpecPicColorStatisticsEntity> list = specPicColorStatisticsMapper.GetSpecPicColorStatisticsBySeriesId(seriesid);
        return list;
    }

    protected Map<Integer, ColorBaseInfoDto> getColorMap(List<Integer> colorIds, boolean inner) {
        Map<Integer, ColorBaseInfoDto> map = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(colorIds)) {
            return map;
        }
        List<ColorBaseInfoDto> colorList = getColorList(colorIds, inner);
        if (CollectionUtils.isEmpty(colorList)) {
            return map;
        }
        for (ColorBaseInfoDto colorBaseInfo : colorList) {
            map.put(colorBaseInfo.getId(), colorBaseInfo);
        }
        return map;
    }

    protected List<ColorBaseInfoDto> getColorList(List<Integer> colorIds, boolean inner) {
        List<ColorInfoEntity> infos = inner ? innerFctColorMapper.getAllColorInfo() : carFctColorMapper.getAllColorInfo();
        if (ListUtil.isEmpty(infos)) {
            return Collections.emptyList();
        }
        List<ColorBaseInfoDto> colorList = infos.stream().map(x -> {
            return new ColorBaseInfoDto() {{
                setId(x.getId());
                setName(x.getName());
                setValue(x.getValue());
            }};
        }).collect(Collectors.toList());
        colorList = colorList.stream().filter(x -> colorIds.contains(x.getId())).collect(Collectors.toList());
        return colorList;
    }

}
