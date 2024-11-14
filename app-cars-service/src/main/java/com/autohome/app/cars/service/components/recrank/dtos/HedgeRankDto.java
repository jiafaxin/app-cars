package com.autohome.app.cars.service.components.recrank.dtos;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class HedgeRankDto {
    private List<Object[]> list;

    @Data
    public static class Item {
        private int seriesid;//车系id
        private int levelid;//车系级别id
        private String place = "";//厂商属性 合资、国产(自主)、进口、独资
        private String fueltypes = "";//能源类型值 4纯电, 5插电, 6增程
        private float praisescore = 0;//口碑分数
        private float ratevalue = 0;//保值率
        private int isnewenergy;//是否新能源 0-非新能源、1-新能源
        private int minprice;//车系最低价
        private int maxprice;//车系最高价
        private int ranknum;//排名
    }

    /**
     * 将对象转换为数组
     *
     * @param list 对象结合
     * @return
     */
    public static List<Object[]> toArray(List<HedgeRankDto.Item> list) {
        return list.stream().map(x -> {
            return new Object[]{
                    x.getSeriesid(),
                    x.getLevelid(),
                    x.getPlace(),
                    x.getFueltypes(),
                    x.getPraisescore(),
                    x.getRatevalue(),
                    x.getIsnewenergy(),
                    x.getMinprice(),
                    x.getMaxprice()
            };
        }).collect(Collectors.toList());
    }

    /**
     * 将数组结构转换成对象
     *
     * @param datas 数组集合
     * @return
     */
    public static List<HedgeRankDto.Item> toDtos(List<Object[]> datas) {
        return datas.stream().map(x -> toDto(x)).collect(Collectors.toList());
    }

    public static HedgeRankDto.Item toDto(Object[] data) {
        HedgeRankDto.Item dto = new HedgeRankDto.Item();
        dto.setSeriesid(Integer.parseInt(data[0].toString()));
        dto.setLevelid(Integer.parseInt(data[1].toString()));
        dto.setPlace(data[2].toString());
        dto.setFueltypes(data[3].toString());
        dto.setPraisescore(Float.parseFloat(data[4].toString()));
        dto.setRatevalue(Float.parseFloat(data[5].toString()));
        dto.setIsnewenergy(Integer.parseInt(data[6].toString()));
        dto.setMinprice(Integer.parseInt(data[7].toString()));
        dto.setMaxprice(Integer.parseInt(data[8].toString()));
        return dto;
    }
}
