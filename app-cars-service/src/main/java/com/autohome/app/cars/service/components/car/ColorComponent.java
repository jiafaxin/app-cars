package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.HttpClientAsync;
import com.autohome.app.cars.common.utils.HttpClientUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.ColorMapper;
import com.autohome.app.cars.mapper.popauto.entities.PicColorEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.ColorDto;
import com.autohome.app.cars.service.components.car.dtos.ColorStatisticsDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SpecConfigBagDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ColorComponent  extends BaseComponent<ColorDto> {
    @Autowired
    ColorMapper colorMapper;

    final static String paramName = "seriesId";
    final static String typeParamName = "type";

    TreeMap<String, Object> makeParam(int seriesId,int type) {
        return ParamBuilder.create(paramName, seriesId).add(typeParamName,type).build();
    }

    public String get(TreeMap<String, Object> params) {
        int seriesId = (int)params.get("seriesId");
        int type = (int)params.get("type");
        ColorDto dto = baseGet(makeParam(seriesId, type));
        return JsonUtil.toString(dto);
    }

    /**
     * 获取外观或内饰的颜色列表
     * @param type 外观：1 ，内饰：2
     * @param seriesId
     * @param specId
     * @param classIds
     * @return
     */
    public CompletableFuture<List<ColorStatisticsDto>> getColors(int type,int seriesId,int specId,List<Integer> classIds) {
        return getColors(type, seriesId, specId, classIds, Arrays.asList(10, 20, 30));
    }

    private CompletableFuture<List<ColorStatisticsDto>> getColors(int type,int seriesId,int specId,List<Integer> classIds, List<Integer> stateList) {
        //        return HttpClientUtil.get("http://car-app2.mesh-thallo.autohome.com.cn/test/getcolors?type=1&seriesId=163&specId=0&classIds=1,12", new TypeReference<List<ColorStatisticsDto>>() {
//        },null,1000,"utf-8");

        return baseGetAsync(makeParam(seriesId,type)).thenApply(allColors -> {
            if (allColors == null || allColors.getColors() == null || allColors.getColors().size() == 0) {
                return new ArrayList<>();
            }
            List<PicColorEntity> colors = allColors.getColors().stream().filter(x -> {
                if (specId > 0 && x.getSpecId() != specId) {
                    return false;
                }
                if (classIds!=null && classIds.size()>0 && !classIds.contains(x.getPicClass())) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            Map<Integer, List<PicColorEntity>> map = colors.stream().collect(Collectors.groupingBy(x -> x.getColorId()));
            List<ColorStatisticsDto> result = new ArrayList<>();
            map.forEach((k, v) -> {
                result.add(new ColorStatisticsDto() {{
                    setId(k);
                    setClubpiccount(v.stream().mapToInt(x -> x.getClubPicNumber()).sum());
                    setIsonsale(v.stream().anyMatch(s -> stateList.contains(s.getSpecState())) ? 1 : 0);
                    setName(v.get(0).getColorName());
                    setPicCount(v.stream().mapToInt(x ->  x.getPicNumber()).sum());
                    setValue(v.get(0).getColorValue());
                }});
            });
            result.sort(Comparator.comparing(ColorStatisticsDto::getPicCount, Comparator.reverseOrder()));
            return result;
        });
    }

    /**
     * 得到在产在售、停产在售颜色、即将销售颜色、未售颜色0(含)
     * @param type
     * @param seriesId
     * @param specId
     * @param classIds
     * @return
     */
    public CompletableFuture<List<ColorStatisticsDto>> getColorsWithOtherSpec(int type, int seriesId, int specId, List<Integer> classIds) {
//        return getColors(type, seriesId, specId, classIds, Arrays.asList(0, 10, 20, 30));
        return getColors(type, seriesId, specId, classIds, Arrays.asList(10, 20, 30));
    }

    public CompletableFuture<List<ColorStatisticsDto>> getColors(int type,int seriesId,int specId,List<Integer> classIds, List<Integer> specIds, Map<Integer, List<Integer>> outColorMap, Map<Integer, List<Integer>> innerColorMap) {

//        return HttpClientUtil.get("http://car-app2.mesh-thallo.autohome.com.cn/test/getcolors?type=1&seriesId=163&specId=0&classIds=1,12", new TypeReference<List<ColorStatisticsDto>>() {
//        },null,1000,"utf-8");

        return baseGetAsync(makeParam(seriesId,type)).thenApply(allColors -> {
            if (allColors == null || allColors.getColors() == null || allColors.getColors().size() == 0) {
                return new ArrayList<>();
            }
            List<PicColorEntity> colors = allColors.getColors().stream().filter(x -> {
                if (specId > 0 && x.getSpecId() != specId) {
                    return false;
                }
                if (classIds!=null && classIds.size()>0 && !classIds.contains(x.getPicClass())) {
                    return false;
                }
                if (x.getSpecState() == 20 && specIds != null && specIds.size() > 0 && !specIds.contains(x.getSpecId())) {
                    return false;
                }
                if (x.getSpecState() == 20 && type == 1 && outColorMap != null && outColorMap.size() > 0 && (!outColorMap.containsKey(x.getSpecId()) || !outColorMap.get(x.getSpecId()).contains(x.getColorId()))) {
                    return false;
                }
                if (x.getSpecState() == 20 && type == 2 && innerColorMap != null && innerColorMap.size() > 0 && (!innerColorMap.containsKey(x.getSpecId()) || !innerColorMap.get(x.getSpecId()).contains(x.getColorId()))) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            Map<Integer, List<PicColorEntity>> map = colors.stream().collect(Collectors.groupingBy(x -> x.getColorId()));
            List<ColorStatisticsDto> result = new ArrayList<>();
            map.forEach((k, v) -> {
                result.add(new ColorStatisticsDto() {{
                    setId(k);
                    setClubpiccount(v.stream().mapToInt(x -> x.getClubPicNumber()).sum());
                    setIsonsale(v.stream().anyMatch(s -> s.getSpecState() == 10 || s.getSpecState() == 20 || s.getSpecState() == 30) ? 1 : 0);
                    setName(v.get(0).getColorName());
                    setPicCount(v.stream().mapToInt(x ->  x.getPicNumber()).sum());
                    setValue(v.get(0).getColorValue());
                }});
            });
            result.sort(Comparator.comparing(ColorStatisticsDto::getPicCount, Comparator.reverseOrder()));
            return result;
        });
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<PicColorEntity> colors = colorMapper.getAllColors();
        Map<Integer, List<PicColorEntity>> seriesColors = colors.stream().collect(Collectors.groupingBy(x -> x.getSeriesId()));
        seriesColors.forEach((series, items) -> {
            ColorDto dto = new ColorDto();
            dto.setSeriesId(series);
            dto.setColors(items);
            update(makeParam(series,1), dto);
        });

        List<PicColorEntity> innerColors = colorMapper.getAllInnerColors();
        Map<Integer, List<PicColorEntity>> seriesInnerColors = innerColors.stream().collect(Collectors.groupingBy(x -> x.getSeriesId()));
        seriesInnerColors.forEach((series, items) -> {
            ColorDto dto = new ColorDto();
            dto.setSeriesId(series);
            dto.setColors(items);
            update(makeParam(series,2), dto);
        });
    }

}
