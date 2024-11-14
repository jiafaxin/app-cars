package com.autohome.app.cars.service.services.dtos;

import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import com.autohome.app.cars.service.components.subsidy.enums.SubsidyGroupTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/5/28
 */
@Data
public class Card30104Data {

    private SubsidyGroupTypeEnum groupType;

    private int priceInt;

    private String local_benefit_id;

    private String filterid;
    private String acttitle;
    private String acttime;
    private ActInfo actinfo;
    private TagInfo taginfo;
    private String righttext;
    private String righttextvague;
    private List<Item> list;
    private Pvitem pvitem;

    @Data
    public static class ActInfo {
        private String title;
        private List<ListTip> list;
    }

    @Data
    public static class ListTip {
        private String title;
        private List<String> subtitle;
        // 补贴活动id 上报埋点使用
        private String benefit_id;
    }

    @Data
    public static class TagInfo {
        private String text;
        private String backcolor;
    }

    @Data
    public static class Item {
        private String title;
        private String value;
        // 补贴活动id 上报埋点使用
        private String benefit_id;
    }

}
