package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.mapper.popauto.entities.CarPhotoEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class HqPhotoDto {
    int type; //1图片，2视频，10 老图
    int id;
    String url;
    String mid;
    int specId;
    int outColor;
    int innerColor;
    int typeId;
    int subTypeId;
    int width;
    int height;
    int dealerId;
    int isWallPaper;
    int optional;
    int showId;
    String showName;
    int pointlocatinid;
    int year;
    int specPicUploadTimeOrder;
    int specState;
    int specSort;
    int outColorSort; //外观颜色的排序号，越大的越靠前：值为修改时间的秒数减去1704067200(2024年1月1日的System.currentTimeMillis()/1000)
    int inColorSort; //内饰颜色的排序号，越大的越靠前：值为修改时间的秒数减去1704067200(2024年1月1日的System.currentTimeMillis()/1000)
    int sort;  //点位序号，越小越在前 只在specid+outcolor+innercolor下才会生效
    int typeStateOrder;
    int ishqpic;
    String pointName;

    public int getTypeOrder() {
        if (type == 1 || type == 2) {  //图片和视频合并排序
            return 1;
        }
        return type;
    }

    public String getShowName() {
        if(StringUtils.isBlank(showName)){
            return "";
        }
        return showName;
    }

    public int getSpecStateOrder(){
        return specState<=30?0:1;
    }

    public void setOutColorLastUpdateTime(Date lastUpdateTime){
        setOutColorSort(Math.round(lastUpdateTime.getTime()/1000 - 1704067200));
    }

    public void setInColorLastUpdateTime(Date lastUpdateTime){
        setOutColorSort(Math.round(lastUpdateTime.getTime()/1000 - 1704067200));
    }

    public static List<int[]> toArray(List<HqPhotoDto> list) {
        return list.stream().map(x -> {
            return new int[]{
                    x.getType(),
                    x.getId(),
                    x.getSpecId(),
                    x.getInnerColor(),
                    x.getOutColor(),
                    x.getTypeId(),
                    x.getSubTypeId(),
                    x.getOutColorSort(),
                    x.getInColorSort(),
                    x.getSort(),
                    x.getSpecState(),
                    x.getTypeStateOrder(),
                    x.getSpecSort()
            };
        }).collect(Collectors.toList());

    }

    public static List<HqPhotoDto> toDtos(List<int[]> datas){
        return datas.stream().map(x->toDto(x)).collect(Collectors.toList());
    }

    public static HqPhotoDto toDto(int[] data){
        HqPhotoDto dto = new HqPhotoDto();
        dto.setType(data[0]);
        dto.setId(data[1]);
        dto.setSpecId(data[2]);
        dto.setInnerColor(data[3]);
        dto.setOutColor(data[4]);
        dto.setTypeId(data[5]);
        dto.setSubTypeId(data[6]);
        dto.setOutColorSort(data[7]);
        dto.setInColorSort(data[8]);
        dto.setSort(data[9]);
        dto.setSpecState(data[10]);
        dto.setTypeStateOrder(data[11]);
        dto.setSpecSort(data[12]);
        return dto;
    }


}
