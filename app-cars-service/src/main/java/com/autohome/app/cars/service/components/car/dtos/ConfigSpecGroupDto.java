package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : yangchengwei
 * @description : 参配-车型列表
 * @date : 2024/6/26 10:34
 */
@Data
public class ConfigSpecGroupDto {
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
    private List<ConfigSpecGroupItem_SpecGroup> yearspeclist = new ArrayList<>();

    public ConfigSpecGroupDto() {
    }

    public ConfigSpecGroupDto(String yearname, int yearvalue, List<ConfigSpecGroupItem_SpecGroup> yearspeclist) {
        this.yearname = yearname;
        this.yearvalue = yearvalue;
        this.yearspeclist = yearspeclist;
    }

    public ConfigSpecGroupDto(String yearname, int yearvalue, List<ConfigSpecGroupItem_SpecGroup> yearspeclist, int yearstate) {
        this.yearname = yearname;
        this.yearvalue = yearvalue;
        this.yearspeclist = yearspeclist;
        this.yearstate = yearstate;
    }

    @Data
    public static class ConfigSpecGroupItem_SpecGroup {
        /**
         * 二组分组名称：2.4升 涡轮增压 140马力 国VI   、  电动 184马力；如果是年代款就是年代款名称
         */
        private String name;
        /**
         * 年代款名称除（纯电动、排量、座位数外）：2024款，纯电动、排量、座位数不在按年代款分组
         */
        private String yearname;
        private List<SpecDetailDto> speclist = new ArrayList<>();

        public ConfigSpecGroupItem_SpecGroup() {
        }

        public ConfigSpecGroupItem_SpecGroup(String name, String yearname, List<SpecDetailDto> speclist) {
            this.name = name;
            this.yearname = yearname;
            this.speclist = speclist;
        }
    }

}
