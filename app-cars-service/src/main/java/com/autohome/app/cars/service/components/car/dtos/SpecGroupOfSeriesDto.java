package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 车系-车型列表
 * @date : 2024/5/10 16:34
 */
@Data
public class SpecGroupOfSeriesDto {
    /**
     * 大分组的分组名称：在售、即将销售、未售、纯电动、排量、座位数、2024款
     */
    private String yearname;
    /**
     * 大分组的编号，如果是年代款就是年代款id,其它是顺序编号
     */
    private int yearvalue;
    /**
     * 当分组是年代款时，年代款状态
     */

    private int yearstate;
    private List<SpecGroupOfSeriesItem_SpecGroup> yearspeclist = new ArrayList<>();

    public SpecGroupOfSeriesDto() {
    }

    public SpecGroupOfSeriesDto(String yearname, int yearvalue, List<SpecGroupOfSeriesItem_SpecGroup> yearspeclist) {
        this.yearname = yearname;
        this.yearvalue = yearvalue;
        this.yearspeclist = yearspeclist;
    }

    public SpecGroupOfSeriesDto(String yearname, int yearvalue, List<SpecGroupOfSeriesItem_SpecGroup> yearspeclist, int yearstate) {
        this.yearname = yearname;
        this.yearvalue = yearvalue;
        this.yearspeclist = yearspeclist;
        this.yearstate = yearstate;
    }

    @Data
    public static class SpecGroupOfSeriesItem_SpecGroup {
        /**
         * 二组分组名称：2.4升 涡轮增压 140马力 国VI   、  电动 184马力；如果是年代款就是年代款名称
         */
        private String name;
        /**
         * 年代款名称除（纯电动、排量、座位数外）：2024款，纯电动、排量、座位数不在按年代款分组
         */
        private String yearname;
        private List<Spec> speclist = new ArrayList<>();
        @JsonIgnore
        private double displacement;
        @JsonIgnore
        private boolean isclassic;
        @JsonIgnore
        private int enginepower;
        @JsonIgnore
        private int emissionstandardsNum;
        @JsonIgnore
        private int flowmodeid;
        @JsonIgnore
        private int isOil;
        @JsonIgnore
        private double average;

        public SpecGroupOfSeriesItem_SpecGroup() {
        }

        public static SpecGroupOfSeriesItem_SpecGroup SpecGroupOfSeriesItem_SpecGroup1(String name, String yearname, List<Spec> speclist) {
            SpecGroupOfSeriesItem_SpecGroup item_specGroup = new SpecGroupOfSeriesItem_SpecGroup();
            item_specGroup.setName(name);

            item_specGroup.setYearname(yearname);
            if (speclist != null && speclist.size() > 0) {
                item_specGroup.setSpeclist(speclist);
            }
            return item_specGroup;
        }

        public SpecGroupOfSeriesItem_SpecGroup(String name, String yearname, List<SpecDetailDto> speclist) {
            this.name = name;
            this.yearname = yearname;
            if (speclist != null && speclist.size() > 0) {
                List<Spec> list = new ArrayList<>();
                speclist.forEach(x -> {
                    SpecGroupOfSeriesDto.Spec spec = new SpecGroupOfSeriesDto.Spec();
                    BeanUtils.copyProperties(x, spec);
                    list.add(spec);
                });
                this.speclist = list;
            }
        }

        public static SpecGroupOfSeriesItem_SpecGroup SpecGroupOfSeriesItem_SpecGroup2(String name, String yearname, List<SpecEntity> speclist) {
            SpecGroupOfSeriesItem_SpecGroup item_specGroup = new SpecGroupOfSeriesItem_SpecGroup();
            item_specGroup.setName(name);
            item_specGroup.setYearname(yearname);
            if (speclist != null && speclist.size() > 0) {
                List<Spec> list = new ArrayList<>();
                speclist.forEach(x -> {
                    SpecGroupOfSeriesDto.Spec spec = new SpecGroupOfSeriesDto.Spec();
                    spec.setSpecId(x.getId());
                    spec.setFuelType(x.getFuelType());
                    spec.setDisplacement(x.getDisplacement());
                    spec.setFlowModeId(x.getFlowMode());
                    spec.setSeatCount(getSeatCount(x.getSeats()));
                    spec.setState(x.getState());
                    spec.setClassic(x.getIsclassic() != null && x.getIsclassic() == 1);
                    spec.setOrders(x.getOrders());
                    spec.setMinPrice(x.getMinPrice());
                    spec.setParamIsShow(x.getParamIsShow());
                    list.add(spec);
                });
                item_specGroup.setSpeclist(list);
            }
            return item_specGroup;
        }

        static int getSeatCount(String seatStr) {
            try {
                if (StringUtils.isNotEmpty(seatStr)) {
                    //对商用车座位数据特殊处理
                    String[] seatArray = new String[]{};
                    if (seatStr.indexOf("-") > -1) {
                        seatArray = seatStr.split("-");
                    } else if (seatStr.indexOf("/") > -1) {
                        seatArray = seatStr.split("/");
                    }
                    if (seatArray != null && seatArray.length > 0) {
                        return Integer.parseInt(seatArray[seatArray.length - 1]);
                    } else {
                        return Integer.parseInt(seatStr.replace("-", "0"));
                    }
                }
            } catch (NumberFormatException e) {
            }
            return 0;
        }
    }

    @Data
    public static class Spec {
        public Spec() {
        }

        private int specId;
        @JsonIgnore
        private int fuelType;
        @JsonIgnore
        private BigDecimal displacement;
        @JsonIgnore
        private int flowModeId;
        @JsonIgnore
        private int seatCount;
        //@JsonIgnore
        private int state;
        private int paramIsShow;
        @JsonIgnore
        private boolean isClassic;
        @JsonIgnore
        private int orders;
        @JsonIgnore
        private int minPrice;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private DiffConfigInfoHighlight diffconfigofhighlight;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private SpecDetailDto details;

        @Data
        public static class DiffConfigInfoHighlight {
            /**
             * 差异项数
             */
            private int diffcount;

            /**
             * 差价格
             */
            private int price;

            /**
             * 展示配置差异集合，分组
             */
            private List<DiffConfigItemByGroup> grouplist;

            @Data
            public static class DiffConfigItemByGroup {
                /**
                 * 分组名称
                 */
                private String name;

                /**
                 * 配置差集合
                 */
                private List<DiffConfigItem> list;

                @Data
                public static class DiffConfigItem {

                    //配置id或者是子项id
                    private Integer id;
                    //展示的配置名称或者是子项配置名
                    private String name;
                    //某车型配置项或子项对应的图片
                    private String image;
                }
            }
        }

    }
}
