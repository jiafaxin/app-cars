package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.CarPhotoMapper;
import com.autohome.app.cars.mapper.popauto.ColorMapper;
import com.autohome.app.cars.mapper.popauto.ShowMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoEntity;
import com.autohome.app.cars.mapper.popauto.entities.ShowEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.PageOf;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecColorListItemsDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.vr.SpecVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import com.autohome.app.cars.service.services.dtos.piclist.GroupCountDto;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * //StateOrder --在售车型在前面 specstate<=30?0:1  车型维度
 * //isclassic  --是否经典车型 对应spec_new 表的是isclassic 字段   车型维度
 * //CASE IsClubPhoto when 2 then 5 when 1 then 10  when 3 then 0 else IsClubPhoto end as sourceTypeOrder -- 图片来源排序 为把经销商图片审核通过的图片排到编辑的图片后面，排序值0 编辑上传、排序值5 是经销商推图、排序值10 是网友传图。对应
 * //IsClubPhoto 字段对应表：car_spec_photo,cv_photo 原始值 0（网页传图后台）和3（来自预约后台）为编辑上传,1是早年网友上传,2经销商传图。
 * //dealerPicOrder --车系25图代表车型排前面，对应brands表的delegate25SpecId字段,名字和实际意义不一致
 * //SpecPicUploadTimeOrder --上传排序字段 最新上传的一批图片排前
 * //CASE A.PicClass WHEN 10 THEN 2  when 54 then 12.2 when 53 then 12.5 when 51 then 15  when 15 then 16   ELSE A.PicClass END AS ClassOrder  --图片类别排序
 * //对以下分类调整排序：
 * //10	中控方向盘
 * //54	影致
 * //53	官图
 * //51	改装
 * //15	活动
 */
@Component
@Slf4j
public class CarPhotoComponent extends BaseComponent<List<int[]>> {

    final static String paramName = "seriesId";

    @Autowired
    CarPhotoMapper carPhotoMapper;

    @Autowired
    ShowMapper showMapper;

    @Autowired
    ColorComponent colorComponent;
    @Autowired
    SpecOutInnerColorComponent specColorComponent;

    @Autowired
    SpecVrComponent specVrComponent;
    @Autowired
    private ColorMapper colorMapper;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public String get(TreeMap<String, Object> params) {
        int seriesId = (int) params.get("seriesId");
        int classId = (int) params.get("classId");
        int type = (int) params.get("type");
        List<int[]> datas = baseGet(makeParam(seriesId));
        if (datas == null || datas.size() == 0) {
            return null;
        }
        List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
        if (entities != null) {
            entities = entities.stream().filter(x -> {
                if (x == null) {
                    return false;
                }
                if (classId > 0 && classId != x.getPicClass()) {
                    return false;
                }
                if (type == 1 && x.getPicColorId() <= 0) {
                    return false;
                }
                if (type == 2 && x.getInnerColorId() <= 0) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }
        return JsonUtil.toString(entities);
    }

    //启用压缩
    @Override
    protected boolean gzip() {
        return true;
    }

    /**
     * 获取外观
     *
     * @param seriesId
     * @param specId
     * @param colorId
     * @param classId
     * @param page
     * @param size
     * @return
     */
    public CompletableFuture<PageOf<CarPhotoDto>> get(int seriesId, int specId, int colorId, int innerColorId, List<Integer> classId, int page, int size, int currentType, int currentId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
            List<CarPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, classId);
            int totalCount = dataList.size();
            sort(dataList, specId, classId, colorId,innerColorId);

            int newPage = page;
            if (currentId > 0 && currentType > 0) { //如果传了图片id，需要根据图片id重新计算翻页
                int index = getIndex(dataList, currentType, currentId);
                if (index > 0) {
                    newPage = index / size + 1;
                }
            }

            List<Integer> picIds = dataList.stream().skip((newPage - 1) * size).limit(size).map(x -> x.getPicId()).collect(Collectors.toList());
            PageOf<CarPhotoDto> result = new PageOf<>();
            result.setCount(totalCount);
            result.setItems(getPicList(seriesId, picIds));
            result.setPageIndex(newPage);
            return result;
        });
    }

    public CompletableFuture<PageOf<CarPhotoDto>> getWithOtherSpec(int seriesId, int specId, int specSaleStatus, int colorId, int innerColorId, int isinner, List<Integer> otherColors, List<Integer> classId, int page, int size, int currentType, int currentId) {
        return baseGetAsync(makeParam(seriesId)).thenCombine(specVrComponent.get(specId), (datas, vrinfo) -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);

            List<CarPhotoDto> targetList = getPicListWithOtherSpec(entities, seriesId, specId, specSaleStatus, colorId, innerColorId, isinner, otherColors, classId, vrinfo);

            //去掉非本车型无颜色的图片，否则picount对不上
            if (specId > 0) {
                targetList.removeIf(e -> e.getSpecId() != specId && e.getInnerColorId() == 0 && e.getPicColorId() == 0);
            }

            int newPage = page;
            if (currentId > 0 && currentType > 0) { //如果传了图片id，需要根据图片id重新计算翻页
                int index = getIndex(targetList, currentType, currentId);
                if (index > 0) {
                    newPage = index / size + 1;
                }
            }

            List<Integer> picIds = targetList.stream().skip((newPage - 1) * size).limit(size).map(x -> x.getPicId()).collect(Collectors.toList());
            PageOf<CarPhotoDto> result = new PageOf<>();
            result.setCount(targetList.size());
            result.setItems(getPicList(seriesId, picIds));
            result.setPageIndex(newPage);
            return result;
        });
    }

    public String getListCount(TreeMap<String, Object> params) {
        int seriesId = null == params.get("seriesId") ? 0 : (int) params.get("seriesId");
        int specId = null == params.get("specId") ? 0 : (int) params.get("specId");
        int colorId = null == params.get("colorId") ? 0 : (int) params.get("colorId");
        int innerColorId = null == params.get("innerColorId") ? 0 : (int) params.get("innerColorId");
        String classIds = null == params.get("classIds") ? "" : String.valueOf(params.get("classIds"));
        List<Integer> classId = StringUtils.isBlank(classIds) ? new ArrayList<>() : Arrays.stream(classIds.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        List<CarPhotoTypeCountDto> list = getTotoalCount(seriesId, specId, colorId, innerColorId, classId).join();
        return JsonUtil.toString(list);
    }

    public CompletableFuture<List<CarPhotoTypeCountDto>> getTotoalCount(int seriesId, int specId, int colorId, int innerColorId, List<Integer> classId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
            List<CarPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, classId);
            List<CarPhotoTypeCountDto> result = new ArrayList<>();
            for (int typeId : classId) {
                CarPhotoTypeCountDto countDto = new CarPhotoTypeCountDto();
                countDto.setClassId(typeId);
                countDto.setCount(Long.valueOf(dataList.stream().filter(e -> e.getPicClass() == typeId).count()).intValue());
                result.add(countDto);
            }
            return result;
        });
    }


    //提供给cars getPicListBySpec
    public String getPiclistBySpec(TreeMap<String, Object> params) {
        int seriesId = null == params.get("seriesId") ? 0 : (int) params.get("seriesId");
        int specId = null == params.get("specId") ? 0 : (int) params.get("specId");
        int colorId = null == params.get("colorId") ? 0 : (int) params.get("colorId");
        int innerColorId = null == params.get("innerColorId") ? 0 : (int) params.get("innerColorId");
        String classIds = null == params.get("classIds") ? "" : String.valueOf(params.get("classIds"));
        List<Integer> classId = StringUtils.isBlank(classIds) ? new ArrayList<>() : Arrays.stream(classIds.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        List<CarPhotoDto> list = getAll(seriesId, specId, colorId, innerColorId, classId).join();
        return JsonUtil.toString(list);
    }

    public CompletableFuture<List<CarPhotoDto>> getAll(int seriesId, int specId, int colorId, int innerColorId, List<Integer> classId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
            List<CarPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, classId);
            sort(dataList, specId, classId, colorId, innerColorId);
            List<Integer> picIds = dataList.stream().map(x -> x.getPicId()).collect(Collectors.toList());
            return getPicList(seriesId, picIds);
        });
    }

    private List<CarPhotoDto> getPicListWithOtherSpec(List<CarPhotoDto> entities, int seriesId, int specId, int specSaleStatus, int colorId, int innerColorId, int isinner, List<Integer> otherColors, List<Integer> classId, SpecVrInfoDto vrInfo) {
        List<CarPhotoDto> dataList = filter(entities, 0, colorId, innerColorId, classId);
        sort(dataList, 0, classId, colorId, innerColorId);
        //该车型的图片，屏蔽从车系找不足3个的情况
        List<CarPhotoDto> specPicList = filter(entities, specId, colorId, innerColorId, classId);
        sort(specPicList, specId, classId, colorId, innerColorId);

        dataList.removeIf(e -> e.getSpecId() == specId);
        List<CarPhotoDto> targetList = new ArrayList();
        //本车型的图片
        targetList.addAll(specPicList);

        List<Integer> colors = new ArrayList<>();
        colors.addAll(otherColors);

        List<SeriesVrExteriorResult.Color_List> vrColor = vrInfo == null || vrInfo.getVrMaterial() == null || vrInfo.getVrMaterial().getColor_list() == null ? new ArrayList<>() : vrInfo.getVrMaterial().getColor_list();
        List<Integer> vrColorIds = vrColor.stream().map(x -> x.getRemoteColorId()).collect(Collectors.toList());

        //如果size为0，则说明传进来的colorid是推荐颜色，需要补充进去
        if (specPicList.size() == 0) {
            colors.add(1 == isinner ? innerColorId : colorId);
        } else {
            List<Integer> hascolors = 1 == isinner ? specPicList.stream().map(CarPhotoDto::getInnerColorId).toList() : specPicList.stream().map(CarPhotoDto::getPicColorId).toList();
            colors.removeIf(e -> hascolors.contains(e));
        }
        if (!CollectionUtils.isEmpty(vrColorIds)) {
            //vr有的颜色不用补足
            colors.removeIf(e -> vrColorIds.contains(e));
        }

        //本车型的其他颜色需要补充
        if (1 == isinner) {
            List<CarPhotoDto> otherList = new ArrayList<>();
            otherList.addAll(dataList.stream().filter(e -> colors.contains(e.getInnerColorId())).toList());
            if (!CollectionUtils.isEmpty(otherList)) {
                if (40 == specSaleStatus) {
                    otherList.removeIf(e -> 40 != e.getSpecState());
                } else {
                    otherList.removeIf(e -> 40 == e.getSpecState());
                }
                targetList.addAll(otherList);
            }
        } else {
            List<CarPhotoDto> otherList = new ArrayList<>();
            otherList.addAll(dataList.stream().filter(e -> colors.contains(e.getPicColorId())).toList());
            if (!CollectionUtils.isEmpty(otherList)) {
                if (40 == specSaleStatus) {
                    otherList.removeIf(e -> 40 != e.getSpecState());
                } else {
                    otherList.removeIf(e -> 40 == e.getSpecState());
                }
                targetList.addAll(otherList);
            }
        }
        return targetList;
    }

    public String getChannelCount(TreeMap<String, Object> params) {
        int seriesId = null == params.get("seriesId") ? 0 : (int) params.get("seriesId");
        int specId = null == params.get("specId") ? 0 : (int) params.get("specId");
        int colorIdSelected = null == params.get("colorIdSelected") ? 0 : (int) params.get("colorIdSelected");
        int isinner = null == params.get("isinner") ? 0 : (int) params.get("isinner");
        String classIds = null == params.get("classIds") ? "" : String.valueOf(params.get("classIds"));
        CompletableFuture<SpecVrInfoDto> specVrFuture = specId > 0 ? specVrComponent.get(specId) : CompletableFuture.completedFuture(null);

        List<CarPhotoCountDto> list = getChannelCount(seriesId, specId, isinner, classIds, colorIdSelected, specVrFuture.join()).join();
        return JsonUtil.toString(list);
    }

    public CompletableFuture<List<CarPhotoCountDto>> getChannelCount(int seriesId, int specId, int isinner, String classIds, int colorIdSelected, SpecVrInfoDto vrInfo) {

        return baseGetAsync(makeParam(seriesId)).thenCombine(specColorComponent.get(specId, 1 == isinner), (datas, colors) -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            if (colors == null || CollectionUtils.isEmpty(colors.getColoritems())) {
                return null;
            }
            List<Integer> classId = StringUtils.isBlank(classIds) ? new ArrayList<>() : Arrays.stream(classIds.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);

            Optional<CarPhotoDto> carPhotoDto = entities.stream().filter(e -> e.getSpecId() == specId).findAny();
            if (!carPhotoDto.isPresent()) {
                return null;
            }
            int specSaleStatus = carPhotoDto.get().getSpecState();

            List<Integer> allColors = colors.getColoritems().stream().map(SpecOutInnerColorDto.ColorItem::getId).toList();

            //如果不筛选颜色，需要含图库中没有颜色的图片
            int noColorCount = 0;
            if (0 == colorIdSelected) {
                List<CarPhotoDto> dataList = filter(entities, specId, 0, 0, classId);
                noColorCount = dataList.stream().filter(e -> 0 == e.getInnerColorId() && 0 == e.getPicColorId()).toList().size();
            }

            List<CarPhotoCountDto> result = new ArrayList<>();
            //补充本车型无颜色信息图片信息
            if (noColorCount > 0) {
                CarPhotoCountDto dto = new CarPhotoCountDto();
                dto.setSpecId(specId);
                dto.setSeriesId(seriesId);
                dto.setColorType(1);
                dto.setColorId(0);
                dto.setCount(noColorCount);
                result.add(dto);
            }
            for (SpecOutInnerColorDto.ColorItem item : colors.getColoritems()) {
                CarPhotoCountDto dto = new CarPhotoCountDto();
                int colorId = item.getId();
                dto.setColorId(colorId);
                dto.setSeriesId(seriesId);
                dto.setSpecId(specId);
                dto.setColorValue(item.getValue());
                dto.setColorName(item.getName());

                int outColor = isinner == 1 ? 0 : colorId;
                int innerColorId = isinner == 1 ? colorId : 0;
                List<CarPhotoDto> dataList = filter(entities, 0, outColor, innerColorId, classId);
                //本车型图片数量
                int specCount = dataList.stream().filter(e -> e.getSpecId() == specId).toList().size();
                if (specCount > 0) {
                    dto.setColorType(1);
                    dto.setCount(specCount);
                    result.add(dto);
                } else {
                    List<Integer> otherColors = allColors.stream().filter(e -> e != colorId).toList();
                    List<CarPhotoDto> targetList = getPicListWithOtherSpec(entities, seriesId, specId, specSaleStatus, outColor, innerColorId, isinner, otherColors, classId, vrInfo);
                    dto.setColorType(3);
                    dto.setCount(targetList.size());
                    result.add(dto);
                }
            }
            return result;
        });
    }
    // todo 这个方法放在组件里不合适，拆到Service层里面
    public CompletableFuture<PageOf<CarPhotoDto>> get(int seriesId, int specId, int colorId, int innerColorId, List<Integer> classId, int page, int size, List<Integer> specIds, Map<Integer, List<Integer>> outColorMap, Map<Integer, List<Integer>> innerColorMap, int currentType, int currentId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
            List<CarPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, classId, specIds, outColorMap, innerColorMap);
            int totalCount = dataList.size();
            sort(dataList, specId, classId, colorId,innerColorId);

            int newPage = page;
            if(currentId>0 && currentType>0) { //如果传了图片id，需要根据图片id重新计算翻页
                int index = getIndex(dataList, currentType, currentId);
                if (index > 0) {
                    newPage = index / size + 1;
                }
            }

            List<Integer> picIds = dataList.stream().skip((newPage - 1) * size).limit(size).map(x -> x.getPicId()).collect(Collectors.toList());
            PageOf<CarPhotoDto> result = new PageOf<>();
            result.setCount(totalCount);
            result.setItems(getPicList(seriesId, picIds));
            result.setPageIndex(newPage);
            return result;
        });
    }

    public int getIndex(List<CarPhotoDto> datas, int type, int id) {
        int i = 0;
        for (CarPhotoDto data : datas) {
            if (data.getPicId() == id) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * 按 showid + specid 分组
     *
     * @param seriesId
     * @param classIds     支持多个分类合并【外观和细节应该合并，外观需要传 1外观 12 细节 2个】
     * @param colorId
     * @param innerColorId
     * @param page
     * @param size         显示多少个车型
     * @param groupSize    每个车型显示多少个图片
     * @return
     */
    public CompletableFuture<PageOf<CarPhotoGroup>> getGroup(int seriesId, List<Integer> classIds, int colorId, int innerColorId, int page, int size, int groupSize, GroupCountDto groupCount) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
            List<CarPhotoDto> dataList = filter(entities, 0, colorId, innerColorId, classIds);

            groupCount.setTotalCount(dataList.size());

            groupSort(dataList,seriesId, classIds,colorId,innerColorId);

            LinkedHashMap<String, List<CarPhotoDto>> groups = new LinkedHashMap<>();
            for (CarPhotoDto item : dataList) {
                String key = item.getShowId() + "-" + item.getSpecId();
                if (!groups.containsKey(key)) {
                    groups.put(key, new ArrayList<>());
                }
                groups.get(key).add(item);
            }

            //获取每个车型对应颜色种类
            Map<Integer,Set<Integer>> colorCounts = getColorsGroupSpec(dataList);

            List<Map.Entry<String, List<CarPhotoDto>>> groupsEntrySet = groups.size() > 1 ? groups.entrySet().stream().skip((page - 1) * size).limit(size).collect(Collectors.toList()) : groups.entrySet().stream().collect(Collectors.toList());

            List<CarPhotoGroup> specGroups = new ArrayList<>();
            List<Integer> picIds = new ArrayList<>();
            for (Map.Entry<String, List<CarPhotoDto>> listEntry : groupsEntrySet) {
                String[] ss = listEntry.getKey().split("-");
                CarPhotoGroup sg = new CarPhotoGroup();
                sg.setCount(listEntry.getValue().size());
                sg.setShowId(Integer.parseInt(ss[0]));
                sg.setSpecId(Integer.parseInt(ss[1]));
                sg.setColorSet(colorCounts.getOrDefault(Integer.parseInt(ss[1]),new HashSet<>()));

                List<CarPhotoDto> items = listEntry.getValue();
                if (items.size() > groupSize && groups.size() > 1) {
                    items = items.subList(0, groupSize);
                }
                picIds.addAll(items.stream().map(x -> x.getPicId()).collect(Collectors.toList()));
                sg.setItems(items);
                specGroups.add(sg);
            }
            List<CarPhotoDto> pics = getPicList(seriesId, picIds);
            Map<Integer, CarPhotoDto> picMap = pics.stream().collect(Collectors.toMap(CarPhotoDto::getPicId, x -> x));
            specGroups.forEach(group -> {
                group.setItems(group.getItems().stream().map(x -> {
                    if (!picMap.containsKey(x.getPicId())) {
                        return null;
                    }
                    return picMap.get(x.getPicId());
                }).filter(x -> x != null).collect(Collectors.toList()));
            });
            PageOf<CarPhotoGroup> result = new PageOf<>();
            result.setCount(groups.size());
            result.setItems(specGroups);
            return result;
        });
    }

    public Map<Integer,Set<Integer>> getColorsGroupSpec(List<CarPhotoDto> dataList){
        Map<Integer,Set<Integer>> colorCounter = new HashMap<>();
        if(CollectionUtils.isEmpty(dataList)){
            return colorCounter;
        }
        //计算每个车型对应的颜色种类
        for(CarPhotoDto dto : dataList){
            Set<Integer> colors = colorCounter.get(dto.getSpecId());
            if(CollectionUtils.isEmpty(colors)){
                colors = new HashSet<>();
                colorCounter.put(dto.getSpecId(),colors);
            }
            if(Arrays.asList(1,12).contains(dto.getPicClass())){
                //1-外观,12-细节 取picColorId
                colors.add(dto.getPicColorId());
            }
            if(Arrays.asList(3,10).contains(dto.getPicClass())){
                //3-座椅,10-中控 取InnerColorId
                colors.add(dto.getInnerColorId());
            }
        }
        return colorCounter;
    }

    public CompletableFuture<CarPhotoGroup> getGroupMore(int seriesId, int specId, int showId, List<Integer> classIds, int colorId, int innerColorId, int page, int size, int groupSize) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<CarPhotoDto> entities = CarPhotoDto.toDtos(datas);
            List<CarPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, classIds);
            //groupSort(dataList, classIds);
            //groupSort(dataList,seriesId, classIds,colorId,innerColorId);

            CarPhotoGroup sg = new CarPhotoGroup();
            sg.setCount(dataList.size());
            sg.setShowId(showId);
            sg.setSpecId(specId);
            sg.setItems(dataList.subList(0, groupSize));
            return sg;
        });
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        long s = System.currentTimeMillis();
        AtomicInteger c = new AtomicInteger(0);
        seriesMapper.getAllSeriesIds().forEach(seriesId -> {
            refreshOne(seriesId);
            if (c.incrementAndGet() % 100 == 0) {
                xxlLog.accept((System.currentTimeMillis() - s) + " : " + c.get());
            }
        });
    }

    public void refreshNew(Consumer<String> xxlLog){
        List<Integer> ids = carPhotoMapper.getUpdateSeriesIds();
        if(ids==null||ids.size()==0){
            return;
        }
        for (Integer id : ids) {
            refreshOne(id);
            xxlLog.accept("车系："+id+" 更新完成");
        }
        colorComponent.refreshAll(xxlLog);
    }

    public void refreshOne(int seriesId) {
        try {
            List<CarPhotoEntity> list = carPhotoMapper.getAllPhotosBySeriesId(seriesId);
            List<int[]> datas = CarPhotoDto.toArray(list);
            update(makeParam(seriesId), datas);
            savePicList(seriesId, list.stream().map(x -> new CarPhotoDto(x)).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("同步车系图片报错" + seriesId, e);
        }
    }

    /**
     * 过滤符合条件的图片
     *
     * @param datas
     * @param specId
     * @param colorId
     * @param classId
     * @return
     */
    List<CarPhotoDto> filter(List<CarPhotoDto> datas, int specId, int colorId, int innerColorId, List<Integer> classId) {
        return datas.stream().filter(x -> {
            if (specId <= 0) {
                if (x.getSpecPicNumber() <= 2) {
                    return false;
                }
            }
            if (specId > 0 && x.getSpecId() != specId) {
                return false;
            }
            if (classId == null || classId.size() == 0 || !classId.contains(x.getPicClass())) {
                return false;
            }
            if (colorId > 0 && x.getPicColorId() != colorId) {
                return false;
            }
            if (innerColorId > 0 && x.getInnerColorId() != innerColorId) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    List<CarPhotoDto> filter(List<CarPhotoDto> datas, int specId, int colorId, int innerColorId, List<Integer> classId, List<Integer> specIds, Map<Integer, List<Integer>> outColorMap, Map<Integer, List<Integer>> innerColorMap) {
        List<CarPhotoDto> data = datas.stream().filter(x->x.getSpecId() == specId).collect(Collectors.toList());
        return datas.stream().filter(x -> {
            if (specId <= 0) {
                if (x.getSpecPicNumber() <= 2) {
                    return false;
                }
            }
            if (specId > 0 && x.getSpecId() != specId) {
                return false;
            }
            if (classId == null || classId.size() == 0 || !classId.contains(x.getPicClass())) {
                return false;
            }
            if (colorId > 0 && x.getPicColorId() != colorId) {
                return false;
            }
            if (innerColorId > 0 && x.getInnerColorId() != innerColorId) {
                return false;
            }
            if (x.getSpecState() == 20 && specIds != null && specIds.size() > 0 && !specIds.contains(x.getSpecId())) {
                return false;
            }
            if (x.getSpecState() == 20 && outColorMap != null && outColorMap.size() > 0 && (!outColorMap.containsKey(x.getSpecId()) || !outColorMap.get(x.getSpecId()).contains(x.getPicColorId()))) {
                return false;
            }
            if (x.getSpecState() == 20 && innerColorMap != null && innerColorMap.size() > 0 && (!innerColorMap.containsKey(x.getSpecId()) || !innerColorMap.get(x.getSpecId()).contains(x.getInnerColorId()))) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public void sort(List<CarPhotoDto> datas, int specId, List<Integer> classId, int colorId, int innerColorId) {
        boolean flag = oldSort(datas, 0, specId, classId, colorId, innerColorId);
        if(flag){
            return;
        }
        if(specId<=0 || (specId > 0 && classId.contains(1) && classId.contains(12))){
            if (colorId > 0) {
                datas.sort(Comparator.comparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getStateOrder)
                        .thenComparing(CarPhotoDto::getIsclassic)
                        .thenComparing(CarPhotoDto::getSourceTypeOrder)
                        .thenComparing(CarPhotoDto::getDealerPicOrder)
                        .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getPicClass)
                        .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
            } else if (innerColorId > 0) {
                datas.sort(Comparator.comparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getStateOrder)
                        .thenComparing(CarPhotoDto::getIsclassic)
                        .thenComparing(CarPhotoDto::getSourceTypeOrder)
                        .thenComparing(CarPhotoDto::getDealerPicOrder)
                        .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getPicClass)
                        .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
            } else {
                if (classId.contains(1) || classId.contains(12)) {
                    datas.sort(Comparator.comparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                            .thenComparing(CarPhotoDto::getStateOrder)
                            .thenComparing(CarPhotoDto::getIsclassic)
                            .thenComparing(CarPhotoDto::getSourceTypeOrder)
                            .thenComparing(CarPhotoDto::getDealerPicOrder)
                            .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                            .thenComparing(CarPhotoDto::getPicColorId, Comparator.reverseOrder())
                            .thenComparing(CarPhotoDto::getPicClass)
                            .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
                } else {
                    //内饰颜色,需要按照颜色排序
                    if (classId.contains(3) || classId.contains(10)) {
                        datas.sort(Comparator.comparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getStateOrder)
                                .thenComparing(CarPhotoDto::getIsclassic)
                                .thenComparing(CarPhotoDto::getSourceTypeOrder)
                                .thenComparing(CarPhotoDto::getDealerPicOrder)
                                .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getInnerColorId, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
                    } else {//车展55 需要按照showId进行排序，其他的showId为0，不影响其他的排序
                        datas.sort(Comparator.comparing(CarPhotoDto::getShowId, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getStateOrder)
                                .thenComparing(CarPhotoDto::getIsclassic)
                                .thenComparing(CarPhotoDto::getSourceTypeOrder)
                                .thenComparing(CarPhotoDto::getDealerPicOrder)
                                .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
                    }
                }
            }
        }else{
            if (classId != null && classId.size() > 0 && colorId > 0) {
                datas.sort(
                        Comparator.comparing(CarPhotoDto::getSourceTypeOrder)
                                .thenComparing(CarPhotoDto::getDealerPicOrder)
                                .thenComparing(CarPhotoDto::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder())
                );
            } else if (classId != null && classId.size() > 0 && colorId == 0) {
                datas.sort(
                        Comparator.comparing(CarPhotoDto::getSourceTypeOrder)
                                .thenComparing(CarPhotoDto::getShowId, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getDealerPicOrder)
                                .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder())
                );
            } else if (colorId > 0 && (classId == null || classId.size() == 0)) {
                datas.sort(
                        Comparator.comparing(CarPhotoDto::getClassOrder)
                                .thenComparing(CarPhotoDto::getSourceTypeOrder)
                                .thenComparing(CarPhotoDto::getDealerPicOrder)
                                .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder())
                );
            } else {
                datas.sort(
                        Comparator.comparing(CarPhotoDto::getClassOrder)
                                .thenComparing(CarPhotoDto::getShowId, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getSourceTypeOrder)
                                .thenComparing(CarPhotoDto::getDealerPicOrder)
                                .thenComparing(CarPhotoDto::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                                .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder())
                );
            }
        }
    }

    public void groupSort(List<CarPhotoDto> datas, int seriesId, List<Integer> classIds, int colorId, int innerColorId) {
        boolean flag = oldSort(datas, seriesId, 0, classIds, colorId, innerColorId);
        if(flag){
            return;
        }
        if (classIds.contains(1) || classIds.contains(12)) {
            datas.sort(Comparator.comparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                    .thenComparing(CarPhotoDto::getStateOrder)
                    .thenComparing(CarPhotoDto::getIsclassic)
                    .thenComparing(CarPhotoDto::getSourceTypeOrder)
                    .thenComparing(CarPhotoDto::getDealerPicOrder)
                    .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                    .thenComparing(CarPhotoDto::getPicColorId, Comparator.reverseOrder())
                    .thenComparing(CarPhotoDto::getPicClass)
                    .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
        } else if (classIds.contains(3) || classIds.contains(10)) {
            datas.sort(Comparator.comparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                    .thenComparing(CarPhotoDto::getStateOrder)
                    .thenComparing(CarPhotoDto::getIsclassic)
                    .thenComparing(CarPhotoDto::getSourceTypeOrder)
                    .thenComparing(CarPhotoDto::getDealerPicOrder)
                    .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                    .thenComparing(CarPhotoDto::getInnerColorId, Comparator.reverseOrder())
                    .thenComparing(CarPhotoDto::getPicClass)
                    .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
        } else {
            datas.sort(
                    Comparator.comparing(CarPhotoDto::getShowId, Comparator.reverseOrder())
                            .thenComparing(CarPhotoDto::getSyear, Comparator.reverseOrder())
                            .thenComparing(CarPhotoDto::getStateOrder)
                            .thenComparing(CarPhotoDto::getIsclassic)
                            .thenComparing(CarPhotoDto::getSourceTypeOrder)
                            .thenComparing(CarPhotoDto::getDealerPicOrder)
                            .thenComparing(CarPhotoDto::getSpecMaxPicId, Comparator.reverseOrder())
                            .thenComparing(CarPhotoDto::getPicClass)
                            .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
        }
    }

    public boolean oldSort(List<CarPhotoDto> datas,int seriesId, int specId, List<Integer> classId, int colorId, int innerColorId) {
        //处理车系维度，老逻辑
        boolean flag = false;
        if((specId <= 0 || seriesId > 0) && classId.size() <= 1){
            if(classId.size() > 0 && (colorId > 0 || innerColorId > 0)){
                datas.sort(Comparator.comparing(CarPhotoDto::getStateOrder)
                        .thenComparing(CarPhotoDto::getIsclassic)
                        .thenComparing(CarPhotoDto::getSourceTypeOrder)
                        .thenComparing(CarPhotoDto::getDealerPicOrder)
                        .thenComparing(CarPhotoDto::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
            }else if(classId.size() > 0 && (colorId == 0 || innerColorId == 0)){
                datas.sort(Comparator.comparing(CarPhotoDto::getStateOrder)
                        .thenComparing(CarPhotoDto::getShowId, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getIsclassic)
                        .thenComparing(CarPhotoDto::getSourceTypeOrder)
                        .thenComparing(CarPhotoDto::getDealerPicOrder)
                        .thenComparing(CarPhotoDto::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
            }else if(classId.size() == 0 && (colorId > 0 || innerColorId > 0)){
                datas.sort(Comparator.comparing(CarPhotoDto::getClassOrder)
                        .thenComparing(CarPhotoDto::getStateOrder)
                        .thenComparing(CarPhotoDto::getIsclassic)
                        .thenComparing(CarPhotoDto::getSourceTypeOrder)
                        .thenComparing(CarPhotoDto::getDealerPicOrder)
                        .thenComparing(CarPhotoDto::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
            }else{
                datas.sort(Comparator.comparing(CarPhotoDto::getClassOrder)
                        .thenComparing(CarPhotoDto::getShowId, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getStateOrder)
                        .thenComparing(CarPhotoDto::getIsclassic)
                        .thenComparing(CarPhotoDto::getSourceTypeOrder)
                        .thenComparing(CarPhotoDto::getDealerPicOrder)
                        .thenComparing(CarPhotoDto::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                        .thenComparing(CarPhotoDto::getPicId, Comparator.reverseOrder()));
            }
            flag = true;
        }
        return flag;
    }

    /**
     * 图片详情
     *
     * @param seriesId
     * @return
     */
    public String key_pic_detail(int seriesId) {
        return getBaseKey() + ":picdetail:hash:" + seriesId;
    }

    public List<CarPhotoDto> getPicList(int seriesId, List<Integer> picIds) {
        List<Object> jsonResult = redisTemplate.opsForHash().multiGet(key_pic_detail(seriesId), picIds.stream().map(x -> x.toString()).collect(Collectors.toList()));
        return jsonResult.stream().filter(x -> x != null).map(x -> JsonUtil.toObject(x.toString(), CarPhotoDto.class)).collect(Collectors.toList());
    }

    public void savePicList(int seriesId, List<CarPhotoDto> datas) {
        String key = key_pic_detail(seriesId);

        List<ShowEntity> shows = showMapper.getShowNames();
        Map<Integer, String> showNames = shows.stream().filter(x -> StringUtils.isNotBlank(x.getName())).collect(Collectors.toMap(x -> x.getId(), x -> x.getName()));
        datas.forEach(x -> {
            if (!showNames.containsKey(x.getShowId())) {
                return;
            }
            x.setShowName(showNames.get(x.getShowId()));
        });

        for (List<CarPhotoDto> list : Lists.partition(datas, 500)) {
            Map<String, String> vs = new HashMap<>();
            for (CarPhotoDto dto : list) {
                vs.put(dto.getPicId() + "", JsonUtil.toString(dto));
            }
            redis_hash_putall(key, vs);
        }

    }


    /**
     * spec25Photo调用此方法
     */
    public CompletableFuture<List<CarPhotoDto>> getCarPhotoBySeriesId(int seriesId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(data -> {
            if (data == null || data.size() == 0) {
                return null;
            }
           return CarPhotoDto.toDtos(data);

        });
    }

    public List<CarPhotoDto> getData(List<CarPhotoDto> entities, int specId, List<Integer> classId) {
        List<CarPhotoDto> dataList = filter(entities, specId, 0, 0, classId);
        sort(dataList, specId, classId, 0,0);
        return dataList;
    }

}
