package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.mapper.popauto.entities.CarPhotoEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class CarPhotoDto  {
    public CarPhotoDto(){

    }

    public CarPhotoDto(CarPhotoEntity entity){
       setPicId(entity.getPicId());  //0
       setFilepath(entity.getPicFilePath());  //1
       setSpecId(entity.getSpecId());  //2
       setSeriesId(entity.getSeriesId()); //3
       setPicClass(entity.getPicClass()); //4
       setPicColorId(entity.getPicColorId());
       setSpecState(entity.getSpecState());
       setSyear(entity.getSyear());
       setSyearId(entity.getSyearId());
       setSpecPicNumber(entity.getSpecPicNumber());
       setInnerColorId(entity.getInnerColorId());
       setIsclassic(entity.getIsclassic());
       setDealerPicOrder(entity.getDealerPicOrder());
       setSpecPicUploadTimeOrder(entity.getSpecPicUploadTimeOrder());
       setWidth(entity.getWidth());
       setHeight(entity.getHeight());
       setPointlocatinid(entity.getPointlocatinid());
       setIsWallPaper(entity.getIsWallPaper());
       setOptional(entity.getOptional());
       setShowId(entity.getShowId());
       setDealerId(entity.getDealerid());
    }

    int specId;
    int seriesId;
    int picClass;
    String filepath;
    int picId;
    int picColorId;
    int specState;
    int syearId;
    int syear;
    int specPicNumber;
    int innerColorId;
    int isclassic;
    int dealerPicOrder;
    int specPicUploadTimeOrder;
    int width;
    int height;
    int pointlocatinid;
    int isWallPaper;
    int optional;
    int showId;
    int specMaxPicId;
    int stateOrder;
    int sourceTypeOrder;
    int classOrder;
    int dealerId;

    public String getShowName() {
        if(StringUtils.isBlank(showName)){
            return "";
        }
        return showName;
    }

    String showName;


    public static List<int[]> toArray(List<CarPhotoEntity> list) {
        Map<Integer, Integer> specMaxPicId = new HashMap<>();
        for (CarPhotoEntity entity : list) {
            specMaxPicId.compute(entity.getSpecId(), (k, v) -> v == null || v < entity.getPicId() ? entity.getPicId() : v);
        }
        return list.stream().map(x -> {
            return new int[]{
                    x.getStateOrder(),
                    x.getIsclassic(),
                    x.getSourceTypeOrder(),
                    x.getDealerPicOrder(),
                    x.getSpecPicUploadTimeOrder(),
                    x.getPicId(),
                    x.getShowId(),
                    x.getClassOrder(),
                    x.getSpecId(),
                    x.getPicClass(),
                    x.getPicColorId(),
                    x.getSpecPicNumber(),
                    x.getInnerColorId(),
                    specMaxPicId.get(x.getSpecId()),
                    x.getInnerColorId(),
                    x.getSyear(),
                    x.getSpecState(),
            };
        }).collect(Collectors.toList());
    }

    public static List<CarPhotoDto> toDtos(List<int[]> datas){
        return datas.stream().map(x->toDto(x)).collect(Collectors.toList());
    }

    public static CarPhotoDto toDto(int[] data){
        CarPhotoDto dto = new CarPhotoDto();
        dto.setStateOrder(data[0]);
        dto.setIsclassic(data[1]);
        dto.setSourceTypeOrder(data[2]);
        dto.setDealerPicOrder(data[3]);
        dto.setSpecPicUploadTimeOrder(data[4]);
        dto.setPicId(data[5]);
        dto.setShowId(data[6]);
        dto.setClassOrder(data[7]);
        dto.setSpecId(data[8]);
        dto.setPicClass(data[9]);
        dto.setPicColorId(data[10]);
        dto.setSpecPicNumber(data[11]);
        dto.setInnerColorId(data[12]);
        dto.setSpecMaxPicId(data[13]);
        dto.setInnerColorId(data[14]);
        dto.setSyear(data[15]);
        if (data.length > 16) {
            dto.setSpecState(data[16]);
        }
        return dto;
    }


}
