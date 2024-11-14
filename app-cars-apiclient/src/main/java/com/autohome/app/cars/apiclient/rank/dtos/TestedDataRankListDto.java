package com.autohome.app.cars.apiclient.rank.dtos;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestedDataRankListDto {

    private int id;
    private String alias;
    private Object name;
    private Object ranktags;
    private List<ListBean> list;

    @Setter
    @Getter
    public static class ListBean {

        private String name;
        private int rank;
        private int maxrank;
        private String unit;
        private String showvalue;
        private int brandid;
        private int seriesid;
        private int specid;
        private int itemid;
        private int fueltypedetail;
        private int dataid;
        private String createdstime;
        private String seriesname;
        private String seriespnglogo;
        private int seriesminprice;
        private int seriesmaxprice;
        private Object serieslevelid;
        private String specname;
        private Object firstval;
        private Object lastval;
        private Object middleval;
        private int level1itemid;
        private String level1itemname;
        private int level2itemid;
        private String level2itemname;
        private Object level3itemid;
        private Object level3itemname;


        //排序筛选字段
        private int serieslevelid2;
        private int fctid;
        private int minprice;
        private int maxprice;
        private int seriesstatus;
        private int fueltype;
        // 0不限，1燃油车，456新能源，4纯电, 5插电, 6增程
        private int energytype;
        private float value;

    }
}
