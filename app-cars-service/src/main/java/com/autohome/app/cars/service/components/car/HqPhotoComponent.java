package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.car.*;
import com.autohome.app.cars.mapper.car.entities.HqOrderEntity;
import com.autohome.app.cars.mapper.car.entities.HqPhotoBaseEntity;
import com.autohome.app.cars.mapper.car.entities.HqPointEntity;
import com.autohome.app.cars.mapper.car.entities.HqVideoBaseEntity;
import com.autohome.app.cars.mapper.popauto.CarPhotoMapper;
import com.autohome.app.cars.mapper.popauto.ShowMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoEntity;
import com.autohome.app.cars.mapper.popauto.entities.ShowEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.PageOf;
import com.autohome.app.cars.service.components.car.dtos.CarPhotoDto;
import com.autohome.app.cars.service.components.car.dtos.CarPhotoGroup;
import com.autohome.app.cars.service.components.car.dtos.HqPhotoDto;
import com.autohome.app.cars.service.services.dtos.piclist.GroupCountDto;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HqPhotoComponent extends BaseComponent<List<int[]>> {
    final static String paramName = "seriesId";

    @Autowired
    CarPhotoMapper carPhotoMapper;
    @Autowired
    ShowMapper showMapper;
    @Autowired
    ColorComponent colorComponent;
    @Autowired
    HqPhotoPointMapper photoPointMapper;
    @Autowired
    HqVideoPointMapper videoPointMapper;
    @Autowired
    HqOrderMapper hqOrderMapper;
    @Autowired
    SpecMapper specMapper;
    @Autowired
    HqPicMapper picMapper;
    @Autowired
    HqVideoMapper videoMapper;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
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
     * @param page
     * @param size
     * @return
     */
    public CompletableFuture<PageOf<HqPhotoDto>> get(int seriesId, int specId, int colorId, int innerColorId, int typeId,int subTypeId, int page, int size, int currentType, int currentId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<HqPhotoDto> entities = HqPhotoDto.toDtos(datas);
            List<HqPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, typeId, subTypeId);
            int totalCount = dataList.size();
            sort(dataList, typeId);

            int newPage = page;
            if(currentId>0 && currentType>0) { //如果传了图片id，需要根据图片id重新计算翻页
                int index = getIndex(dataList, currentType, currentId);
                if (index > 0) {
                    newPage = index / size + 1;
                }
            }

            List<String> picIds = dataList.stream().skip((newPage - 1) * size).limit(size).map(x -> x.getType() + "_" + x.getId()).collect(Collectors.toList());
            PageOf<HqPhotoDto> result = new PageOf<>();
            result.setCount(totalCount);
            result.setItems(getPicList(seriesId, picIds));
            result.setPageIndex(newPage);
            return result;
        });
    }

    /**
     * 过滤掉视频内容
     * @param seriesId
     * @param specId
     * @param colorId
     * @param page
     * @param size
     * @return
     */
    public CompletableFuture<PageOf<HqPhotoDto>> getWithoutVideo(int seriesId, int specId, int colorId, int innerColorId, int typeId,int subTypeId, int page, int size, int currentType, int currentId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(datas -> {
            if (datas == null || datas.size() == 0) {
                return null;
            }
            List<HqPhotoDto> entities = HqPhotoDto.toDtos(datas);
            List<HqPhotoDto> dataList = filter(entities, specId, colorId, innerColorId, typeId, subTypeId);
            int totalCount = dataList.size();
            sort(dataList, typeId);

            int newPage = page;
            if(currentId>0 && currentType>0) { //如果传了图片id，需要根据图片id重新计算翻页
                int index = getIndex(dataList, currentType, currentId);
                if (index > 0) {
                    newPage = index / size + 1;
                }
            }

            dataList.removeIf(x -> x.getType() == 2);//过滤掉视频

            List<String> picIds = dataList.stream().skip((newPage - 1) * size).limit(size).map(x -> x.getType() + "_" + x.getId()).collect(Collectors.toList());
            PageOf<HqPhotoDto> result = new PageOf<>();
            result.setCount(totalCount);
            result.setItems(getPicList(seriesId, picIds));
            result.setPageIndex(newPage);
            return result;
        });
    }


    public void refreshAll(Consumer<String> xxlLog) {
        List<HqPointEntity> photoPoints = photoPointMapper.getPoints();
        List<HqPointEntity> videoPoints = videoPointMapper.getPoints();

        Map<Integer, HqPointEntity> photoPointMap = photoPoints.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (v1, v2) -> v2));
        Map<Integer, HqPointEntity> videoPointMap = videoPoints.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (v1, v2) -> v2));
        for (Integer seriesId : hqOrderMapper.getAllSeriesIds()) {
            refreshOne(seriesId, photoPointMap, videoPointMap);
            xxlLog.accept(seriesId + " success");
        }
    }


    public void refreshOne(int seriesId) {
        List<HqPointEntity> photoPoints = photoPointMapper.getPoints();
        List<HqPointEntity> videoPoints = videoPointMapper.getPoints();
        Map<Integer, HqPointEntity> photoPointMap = photoPoints.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (v1, v2) -> v2));
        Map<Integer, HqPointEntity> videoPointMap = videoPoints.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (v1, v2) -> v2));
        refreshOne(seriesId, photoPointMap, videoPointMap);
    }

    public void refreshOne(int seriesId,Map<Integer, HqPointEntity> photoPoints,Map<Integer, HqPointEntity> videoPoints) {
        List<HqOrderEntity> orders = hqOrderMapper.getBySeriesId(seriesId);
        List<HqPhotoDto> list = new ArrayList<>();
        for (HqOrderEntity order : orders) {
            list.addAll(getOrderList(order,photoPoints,videoPoints));
        }
        list.addAll(getOldList(seriesId));

        //给所有车型赋值typeStateOrder：用来查询车型是否有在售高质图片
        Map<Integer,Integer> typeStateOrders = new HashMap<>();
        for (HqPhotoDto dto : list) {
            typeStateOrders.compute(dto.getSpecId(), (oldKey, oldValue) -> {
                if (oldValue!=null && oldValue == 1) {
                    return 1;
                }
                if ((dto.getType() == 1 || dto.getType() == 2 )&& dto.getSpecState() <= 30) {
                    return 1;
                }
                return 2;
            });
        }
        for (HqPhotoDto dto : list) {
            dto.setTypeStateOrder(typeStateOrders.containsKey(dto.getSpecId()) ? typeStateOrders.get(dto.getSpecId()) : 2);
        }

        List<int[]> datas = HqPhotoDto.toArray(list);
        update(makeParam(seriesId), datas);
        savePicList(seriesId, list);
        System.out.println(seriesId + " 共执行 "+list.size()+" 个图片");
    }


    List<HqPhotoDto> getOrderList(HqOrderEntity order,Map<Integer, HqPointEntity> photoPoints,Map<Integer, HqPointEntity> videoPoints) {
        List<HqPhotoDto> result = new ArrayList<>();

        SpecEntity spec = specMapper.getSpec(order.getSpec_id());
        Date lastUpdateTime = null;

        List<HqPhotoBaseEntity> photos = picMapper.getByOrderId(order.getId());
        if (photos != null && photos.size() > 0) {
            for (HqPhotoBaseEntity photo : photos) {
                if (!photoPoints.containsKey(photo.getPoint_id())) {
                    continue;
                }
                HqPointEntity point = photoPoints.get(photo.getPoint_id());
                HqPhotoDto dto = new HqPhotoDto();
                dto.setId(photo.getId());
                dto.setSpecId(order.getSpec_id());
                dto.setInnerColor(order.getInner_color_id());
                dto.setOutColor(order.getColor_id());
                dto.setUrl(photo.getUrl());
                dto.setType(1);
                dto.setSort(point.getSort());
                dto.setTypeId(point.getFId());
                dto.setSubTypeId(point.getSId());
                dto.setYear(spec.getYearName());
                dto.setSpecState(spec.getState());
                dto.setIshqpic(1);
                dto.setPointName(point.getName());
                result.add(dto);
                if(lastUpdateTime==null || lastUpdateTime.before(photo.getModified_stime())){
                    lastUpdateTime = photo.getModified_stime();
                }
            }
        }

        List<HqVideoBaseEntity> videos = videoMapper.getBySeriesId(order.getId());
        if (videos != null && videos.size() > 0) {
            for (HqVideoBaseEntity photo : videos) {
                if (!videoPoints.containsKey(photo.getPoint_id())) {
                    continue;
                }
                HqPointEntity point = videoPoints.get(photo.getPoint_id());
                HqPhotoDto dto = new HqPhotoDto();
                dto.setId(photo.getId());
                dto.setSpecId(order.getSpec_id());
                dto.setInnerColor(order.getInner_color_id());
                dto.setOutColor(order.getColor_id());
                dto.setUrl(photo.getLogo());
                dto.setMid(photo.getMid());
                dto.setPointName(point.getName());
                dto.setType(2);
                dto.setSort(point.getSort());
                dto.setTypeId(point.getFId());
                dto.setSubTypeId(point.getSId());
                dto.setYear(spec.getYearName());
                dto.setSpecState(spec.getState());
                dto.setIshqpic(1);
                result.add(dto);
                if(lastUpdateTime==null || lastUpdateTime.before(photo.getModified_stime())){
                    lastUpdateTime = photo.getModified_stime();
                }
            }
        }

        for (HqPhotoDto photoDto : result) {
            photoDto.setOutColorLastUpdateTime(lastUpdateTime);
            photoDto.setInColorLastUpdateTime(lastUpdateTime);
        }

        return result;
    }

    /**
     * //StateOrder --在售车型在前面 specstate<=30?0:1  车型维度
     * //isclassic  --是否经典车型 对应spec_new 表的是isclassic 字段   车型维度
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

    @Data
    public static class SpecSort{
        int specId;
        int maxPicId;
        int stateOrder;
        int isclassic;
        int dealerPicOrder;
        int year;
    }

    List<HqPhotoDto> getOldList(int seriesId) {
        List<CarPhotoEntity> photos = carPhotoMapper.getAllPhotosBySeriesId(seriesId);
        List<HqPhotoDto> result = new ArrayList<>();
        if (photos == null || photos.size() == 0) {
            return result;
        }

        Map<String, Integer> ocMaxPicId = new HashMap<>();
        Map<String, Integer> icMaxPicId = new HashMap<>();
        Map<Integer, SpecSort> specsSort = new HashMap<>();

        for (CarPhotoEntity entity : photos) {
            if (!specsSort.containsKey(entity.getSpecId())) {
                specsSort.put(entity.getSpecId(),
                        new SpecSort() {{
                            setSpecId(entity.getSpecId());
                            setYear(entity.getSyear());
                            setStateOrder(entity.getStateOrder());
                            setIsclassic(entity.getIsclassic());
                            setDealerPicOrder(entity.getDealerPicOrder());
                        }});
            }

            if (entity.getPicClass() == 3 || entity.getPicClass() == 10) {
                String key = entity.getSpecId() + "_" + entity.getInnerColorId();
                icMaxPicId.compute(key, (ok, ov) -> ov == null || ov < entity.getPicId() ? entity.getPicId() : ov);
            } else if (entity.getPicClass() == 1 || entity.getPicClass() == 12) {
                String key = entity.getSpecId() + "_" + entity.getPicColorId();
                ocMaxPicId.compute(key, (ok, ov) -> ov == null || ov < entity.getPicId() ? entity.getPicId() : ov);
            }
        }

        List<SpecSort> specsSortNew = specsSort.values().stream().sorted(
                Comparator.comparing(SpecSort::getYear, Comparator.reverseOrder())
                        .thenComparing(SpecSort::getStateOrder)
                        .thenComparing(SpecSort::getIsclassic)
                        .thenComparing(SpecSort::getDealerPicOrder)
//                        .thenComparing(SpecSort::getMaxPicId, Comparator.reverseOrder())
        ).collect(Collectors.toList());


        Map<Integer, Integer> specSortIndex = new HashMap<>();
        for (int i = 0; i < specsSortNew.size(); i++) {
            SpecSort spec = specsSortNew.get(i);
            specSortIndex.put(spec.getSpecId(), i);
        }

        for (CarPhotoEntity entity : photos) {
            //老图只4大类融合到新图里
            if (entity.getPicClass() != 1 && entity.getPicClass() != 3 && entity.getPicClass() != 10 && entity.getPicClass() != 12) {
                continue;
            }

            try {
                HqPhotoDto dto = new HqPhotoDto();
                dto.setType(10);
                dto.setId(entity.getPicId());
                dto.setUrl(entity.getPicFilePath());
                dto.setSpecId(entity.getSpecId());
                dto.setTypeId(entity.getPicClass());
                dto.setOutColor(entity.getPicColorId());
                dto.setSpecPicUploadTimeOrder(entity.getSpecPicUploadTimeOrder());
                dto.setYear(entity.getSyear());
                dto.setInnerColor(entity.getInnerColorId());
                dto.setWidth(entity.getWidth());
                dto.setHeight(entity.getHeight());
                dto.setPointlocatinid(entity.getPointlocatinid());
                dto.setIsWallPaper(entity.getIsWallPaper());
                dto.setOptional(entity.getOptional());
                dto.setShowId(entity.getShowId());
                dto.setDealerId(entity.getDealerid());
                dto.setYear(entity.getSyear());
                dto.setSpecState(entity.getSpecState());

                String okey = entity.getSpecId() + "_" + entity.getPicColorId();
                String iKey = entity.getSpecId() + "_" + entity.getInnerColorId();

                if (specSortIndex.containsKey(entity.getSpecId())) {
                    dto.setSpecSort(specSortIndex.get(entity.getSpecId()));
                }
                if (ocMaxPicId.containsKey(okey)) {
                    dto.setOutColorSort(ocMaxPicId.get(okey));
                }
                if (icMaxPicId.containsKey(iKey)) {
                    dto.setInColorSort(icMaxPicId.get(iKey));
                }
                result.add(dto);
            } catch (Exception e) {
                log.error("报错了", e);
            }
        }
        return result;
    }

    /**
     * 过滤符合条件的图片
     *
     * @param datas
     * @param specId
     * @param colorId
     * @return
     */
    List<HqPhotoDto> filter(List<HqPhotoDto> datas, int specId, int colorId, int innerColorId, int typeId,int subTypeId) {
        return datas.stream().filter(x -> {
            if (specId > 0 && x.getSpecId() != specId) {
                return false;
            }
            if (typeId > 0) {
                if((x.getType()==1 || x.getType()==2) && x.getTypeId() != typeId) {  //新图完全匹配
                    return false;
                }
                if(x.getType() == 10){  //老图
                    if(typeId == 1 && x.getTypeId() !=1 && x.getTypeId() !=12){  //外观对外观和细节
                        return false;
                    }
                    if(typeId == 2 && x.getTypeId() !=10) {  //内饰 对 中控
                        return false;
                    }
                    if(typeId == 3 && x.getTypeId() != 3){  //座椅对座椅
                        return false;
                    }
                    if(typeId == 4){
                        return false;
                    }
                }
            }
            if (subTypeId > 0 && x.getSubTypeId() != subTypeId) {
                return false;
            }
            if(typeId !=4 && colorId > 0 && x.getOutColor() != colorId){
                return false;
            }
            if(typeId !=4 && innerColorId > 0 && x.getInnerColor() != innerColorId){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public int getIndex(List<HqPhotoDto> datas,int type,int id) {
        int i = 0;
        for (HqPhotoDto data : datas) {
            if (data.getType() == type && data.getId() == id) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void sort(List<HqPhotoDto> datas,int typeId) {
        if (typeId == 1 || typeId == 4) {
            datas.sort(
                    Comparator.comparing(HqPhotoDto::getTypeStateOrder)  //高质在售在前
                            .thenComparing(HqPhotoDto::getYear, Comparator.reverseOrder()) //最新年代款放前面
                            .thenComparing(HqPhotoDto::getSpecStateOrder) //在售在前
                            .thenComparing(HqPhotoDto::getTypeOrder) // 高质图在前
                            .thenComparing(HqPhotoDto::getSpecSort)
                            .thenComparing(HqPhotoDto::getOutColorSort, Comparator.reverseOrder())  //再根据车型+外观颜色排序
                            .thenComparing(HqPhotoDto::getSort) //新图根据点位排序
                            .thenComparing(HqPhotoDto::getTypeId)
                            .thenComparing(HqPhotoDto::getId, Comparator.reverseOrder())  //老图根据图片Id倒叙
            );
        } else {
            datas.sort(
                    Comparator.comparing(HqPhotoDto::getTypeStateOrder)  //高质在售在前
                            .thenComparing(HqPhotoDto::getYear, Comparator.reverseOrder()) //最新年代款放前面
                            .thenComparing(HqPhotoDto::getSpecStateOrder) //在售在前
                            .thenComparing(HqPhotoDto::getTypeOrder) // 高质图在前
                            .thenComparing(HqPhotoDto::getSpecSort)
                            .thenComparing(HqPhotoDto::getInColorSort, Comparator.reverseOrder())  //再根据车型+外观颜色排序
                            .thenComparing(HqPhotoDto::getSort) //新图根据点位排序
                            .thenComparing(HqPhotoDto::getTypeId)
                            .thenComparing(HqPhotoDto::getId, Comparator.reverseOrder())  //老图根据图片Id倒叙
            );
        }
    }

    /**
     * 图片详情
     *
     * @param seriesId
     * @return
     */
    public String key_pic_detail(int seriesId) {
        return getBaseKey() + ":hqphotos:hash:" + seriesId;
    }

    public List<HqPhotoDto> getPicList(int seriesId, List<String> picIds) {
        List<Object> jsonResult = redisTemplate.opsForHash().multiGet(key_pic_detail(seriesId), picIds.stream().collect(Collectors.toList()));
        return jsonResult.stream().filter(x -> x != null).map(x -> JsonUtil.toObject(x.toString(), HqPhotoDto.class)).collect(Collectors.toList());
    }

    public void savePicList(int seriesId, List<HqPhotoDto> datas) {
        String key = key_pic_detail(seriesId);

        for (List<HqPhotoDto> list : Lists.partition(datas, 500)) {
            Map<String, String> vs = new HashMap<>();
            for (HqPhotoDto dto : list) {
                vs.put(dto.getType() + "_" + dto.getId(), JsonUtil.toString(dto));
            }
            redis_hash_putall(key, vs);
        }

    }
}
