package com.autohome.app.cars.service.components.car;

import autohome.rpc.car.app_cars.v1.carbase.SeriesListBaseInfoResponse;
import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.popauto.CarFctColorMapper;
import com.autohome.app.cars.mapper.popauto.InnerFctColorMapper;
import com.autohome.app.cars.mapper.popauto.SpecColorMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.ColorInfoEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecColorListEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecParamConfigDto;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RedisConfig
@Slf4j
@DBConfig(tableName = "spec_out_inner_color")
public class SpecOutInnerColorComponent extends BaseComponent<SpecOutInnerColorDto> {

    private static String specIdParamName = "specId";
    private static String innerColorParamName = "innerColor";

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private SpecColorMapper specColorMapper;
    @Autowired
    private InnerFctColorMapper innerFctColorMapper;
    @Autowired
    private CarFctColorMapper carFctColorMapper;
    @Autowired
    private SeriesVrComponent seriesVrComponent;

    TreeMap<String, Object> makeParam(int specId, boolean inner) {
        return ParamBuilder.create(specIdParamName, specId).add(innerColorParamName, inner ? 1 : 0).build();
    }

    public CompletableFuture<SpecOutInnerColorDto> get(int specId, boolean inner) {
        return baseGetAsync(makeParam(specId, inner));
    }

    public CompletableFuture<Map<Integer, SpecOutInnerColorDto>> getMap(List<Integer> specIdList, boolean inner) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x,inner)).collect(Collectors.toList())).thenApply(result -> {
            if(Objects.isNull(result)){
                return new HashMap<>();
            }
            result.removeIf(x -> x == null);
            return result.stream().collect(Collectors.toMap(SpecOutInnerColorDto::getSpecid,item->item,(v1,v2)->v2));
        });
    }
    public CompletableFuture<List<SpecOutInnerColorDto>> get(List<Integer> specIdList, boolean inner) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x, inner)).collect(Collectors.toList()));
    }

    public String get(TreeMap<String, Object> params) {
        SpecOutInnerColorDto dto = get((int) params.get("specId"),
                Boolean.valueOf(params.get("inner").toString())).join();
        return JsonUtil.toString(dto);
    }


    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        List<ColorInfoEntity> allOuterColorInfos = carFctColorMapper.getAllColorInfo();
        List<ColorInfoEntity> allInnerColorInfos = innerFctColorMapper.getAllColorInfo();
        List<SpecEntity> allSpecEntities = specMapper.getSpecAll();
        allSpecEntities.addAll(specMapper.getCvSpecAll());
        allSpecEntities = allSpecEntities.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> seriesList = allSpecEntities.stream().map(item -> item.getSeriesId()).distinct().collect(Collectors.toList());
        Map<Integer, SpecEntity> specMap = allSpecEntities.stream().collect(Collectors.toMap(SpecEntity::getId, Function.identity(), (k1, k2) -> k1));
        ConcurrentHashMap<Integer, SeriesVr> seriesVrMap =new ConcurrentHashMap<>();
        List<CompletableFuture> tasks = new ArrayList<>();
        Lists.partition(seriesList,100).forEach(subList->{
            tasks.add(seriesVrComponent.getMap(subList).thenAccept(x->{
                seriesVrMap.putAll(x);
            }));
        });
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        loopSpec(totalMinutes, (specId) -> {
            try {
                SpecEntity spec = specMap.get(specId);
                SeriesVr seriesVr=null;
                if(Objects.nonNull(spec)){
                     seriesVr = seriesVrMap.get(spec.getSeriesId());
                }

                getSpecOuterColor(specId, allOuterColorInfos,seriesVr);
                xxlLog.accept("outer specId = " + specId + " success");
            } catch (Exception e) {
                xxlLog.accept("outer specId = " + specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
            try {
                getSpecInnerColor(specId, allInnerColorInfos);
                xxlLog.accept("inner specId = " + specId + " success");
            } catch (Exception e) {
                xxlLog.accept("inner specId = " + specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
            ThreadUtil.sleep(50);
        }, xxlLog);
    }

    public void refresh(int specId){
        try {
            SpecEntity spec;
            if (Spec.isCvSpec(specId)) {
                spec = specMapper.getCvSpec(specId);
            } else {
                spec = specMapper.getSpec(specId);
            }
            SeriesVr seriesVr=null;
            if(Objects.nonNull(spec)){
                seriesVr = seriesVrComponent.get(spec.getSeriesId()).join();
            }
            List<ColorInfoEntity> allOuterColorInfos = carFctColorMapper.getAllColorInfo();
            getSpecOuterColor(specId, allOuterColorInfos, seriesVr);
        } catch (Exception e) {
            log.error("刷新车型外观颜色异常-exception:{}", e);
        }
        try {
            List<ColorInfoEntity> allInnerColorInfos = innerFctColorMapper.getAllColorInfo();
            getSpecInnerColor(specId, allInnerColorInfos);
        } catch (Exception e) {
            log.error("刷新车型内饰颜色异常-exception:{}", e);
        }
    }

    private void getSpecInnerColor(int specId, List<ColorInfoEntity> allInnerColorInfos) {
        List<SpecColorListEntity> specColors = specColorMapper.getSpecInnerColorBySpecId(specId);
        specColors.removeIf(Objects::isNull);
        Map<Integer,SpecColorListEntity> colorMap = specColors.stream().collect(Collectors.toMap(x -> x.getColorId(), x -> x, (v1, v2) -> v2));
        if (!CollectionUtils.isEmpty(specColors)) {
            if (allInnerColorInfos == null) {
                allInnerColorInfos = innerFctColorMapper.getAllColorInfo();
            }
            if (!CollectionUtils.isEmpty(allInnerColorInfos)) {
                List<Integer> colorIds = specColors.stream().map(x -> x.getColorId()).collect(Collectors.toList());
                List<ColorInfoEntity> colorInfos = allInnerColorInfos.stream().filter(x -> colorIds.contains(x.getId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(colorInfos)) {
                    SpecOutInnerColorDto dto = new SpecOutInnerColorDto();
                    dto.setSpecid(specId);
                    dto.setColoritems(new ArrayList<>());
                    colorInfos.forEach(x -> {
                        SpecOutInnerColorDto.ColorItem item = new SpecOutInnerColorDto.ColorItem();
                        item.setId(x.getId());
                        item.setName(x.getName());
                        item.setValue(x.getValue());
                        if(colorMap.containsKey(x.getId())){
                            item.setPiccount(colorMap.get(x.getId()).getPicNumber());
                        }
                        specColors.stream().filter(y -> y.getColorId() == x.getId()).findFirst().ifPresent(y -> {
                            item.setPrice(y.getPrice());
                            item.setRemarks(y.getRemarks());
                        });
                        dto.getColoritems().add(item);
                    });
                    List<SpecOutInnerColorDto.ColorItem> colorList =  dto.getColoritems();
                    List<SpecOutInnerColorDto.ColorItem> sortList = colorList.stream().sorted(Comparator.comparing(SpecOutInnerColorDto.ColorItem::getPiccount).reversed()).collect(Collectors.toList());
                    dto.setColoritems(sortList);
                    update(makeParam(specId, true), dto);
                }
            }
        }
    }

    private void getSpecOuterColor(int specId, List<ColorInfoEntity> allColorInfos, SeriesVr seriesVr) {
        List<SpecColorListEntity> specColors = specColorMapper.getSpecOuterColorBySpecId(specId);
        specColors.removeIf(Objects::isNull);
        Map<Integer,SpecColorListEntity> colorMap = specColors.stream().collect(Collectors.toMap(x -> x.getColorId(), x -> x, (v1, v2) -> v2));
        if (!CollectionUtils.isEmpty(specColors)) {
            if (allColorInfos == null) {
                allColorInfos = carFctColorMapper.getAllColorInfo();
            }
            if (!CollectionUtils.isEmpty(allColorInfos)) {
                List<Integer> colorIds = specColors.stream().map(x -> x.getColorId()).collect(Collectors.toList());
                List<ColorInfoEntity> colorInfos = allColorInfos.stream().filter(x -> colorIds.contains(x.getId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(colorInfos)) {
                    SpecOutInnerColorDto dto = new SpecOutInnerColorDto();
                    dto.setSpecid(specId);
                    dto.setColoritems(new ArrayList<>());
                    colorInfos.forEach(x -> {
                        SpecOutInnerColorDto.ColorItem item = new SpecOutInnerColorDto.ColorItem();
                        item.setId(x.getId());
                        item.setName(x.getName());
                        item.setValue(x.getValue());
                        if(colorMap.containsKey(x.getId())){
                            item.setPiccount(colorMap.get(x.getId()).getPicNumber());
                        }
                        specColors.stream().filter(y -> y.getColorId() == x.getId()).findFirst().ifPresent(y -> {
                            item.setPrice(y.getPrice());
                            item.setRemarks(y.getRemarks());
                        });
                        item.setPicurl("http://app2.autoimg.cn/appdfs/g26/M07/04/3B/autohomecar__ChxkjmU6Wt6ALwxcAAAUavI2etQ450.png");
                        if (seriesVr != null && seriesVr.getVrMaterial() != null && ListUtil.isNotEmpty(seriesVr.getVrMaterial().getColor_list())) {
                            seriesVr.getVrMaterial().getColor_list().stream().filter(p -> p.getColorName().equals(item.getName())).findFirst().ifPresent(p -> {
                                if(Objects.nonNull(p.getHori())&& Objects.nonNull(p.getHori().getPreview()) && p.getHori().getPreview().size()>0){
                                    item.setPicurl(ImageUtils.convertImageUrl(p.getHori().getPreview().get(0).getUrl(), false, false, false, ImageSizeEnum.ImgSize_WxH_400x0));
                                }
                            });
                        }
                        dto.getColoritems().add(item);
                    });
                    List<SpecOutInnerColorDto.ColorItem> colorList =  dto.getColoritems();
                    List<SpecOutInnerColorDto.ColorItem> sortList = colorList.stream().sorted(Comparator.comparing(SpecOutInnerColorDto.ColorItem::getPiccount).reversed()).collect(Collectors.toList());
                    dto.setColoritems(sortList);
                    update(makeParam(specId, false), dto);
                }
            }
        }
    }

}
