package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dx on 2024/6/4
 * 降价榜数据组件实体对象
 */
@Data
public class DiscountRankDto {
    private int cityid;
    private List<Object[]> list;

    @Data
    public static class Item {
        private int ranknum;//排名
        private int seriesid;//车系id
        private float praisescore = 0;//口碑分数
        private int specid;//车型id
        private int specdealerprice;//车型最低经销商价格
        private int levelid;//车系级别id
        private String place = "";//厂商属性 合资、国产(自主)、进口、独资
        private int isnewenergy;//是否新能源 0-非新能源、1-新能源
        private String fueltypes = "";//能源类型值 4纯电, 5插电, 6增程
        private int minprice;//车系最低指导价
        private int maxprice;//车系最高指导价
        private int specminprice;//车型最低指导价
        private int specmaxprice;//车型最高指导价
        private int brandid;//品牌id
    }

    /**
     * 将对象转换为数组
     *
     * @param list 对象结合
     * @return
     */
    public static List<Object[]> toArray(List<DiscountRankDto.Item> list) {
        return list.stream().map(x -> {
            return new Object[]{
                    x.getSeriesid(),
                    x.getPraisescore(),
                    x.getSpecid(),
                    x.getSpecdealerprice(),
                    x.getLevelid(),
                    x.getPlace(),
                    x.getIsnewenergy(),
                    x.getFueltypes(),
                    x.getMinprice(),
                    x.getMaxprice(),
                    x.getSpecminprice(),
                    x.getSpecmaxprice(),
                    x.getBrandid()
            };
        }).collect(Collectors.toList());
    }

    /**
     * 将数组结构转换成对象
     *
     * @param datas 数组集合
     * @return
     */
    public static List<DiscountRankDto.Item> toDtos(List<Object[]> datas) {
        return datas.stream().map(x -> toDto(x)).collect(Collectors.toList());
    }

    public static DiscountRankDto.Item toDto(Object[] data) {
        DiscountRankDto.Item dto = new DiscountRankDto.Item();
        dto.setSeriesid(Integer.parseInt(data[0].toString()));
        dto.setPraisescore(Float.parseFloat(data[1].toString()));
        dto.setSpecid(Integer.parseInt(data[2].toString()));
        dto.setSpecdealerprice(Integer.parseInt(data[3].toString()));
        dto.setLevelid(Integer.parseInt(data[4].toString()));
        dto.setPlace(data[5].toString());
        dto.setIsnewenergy(Integer.parseInt(data[6].toString()));
        dto.setFueltypes(data[7].toString());
        dto.setMinprice(Integer.parseInt(data[8].toString()));
        dto.setMaxprice(Integer.parseInt(data[9].toString()));
        dto.setSpecminprice(Integer.parseInt(data[10].toString()));
        dto.setSpecmaxprice(Integer.parseInt(data[11].toString()));
        dto.setBrandid(Integer.parseInt(data[12].toString()));
        return dto;
    }
}
