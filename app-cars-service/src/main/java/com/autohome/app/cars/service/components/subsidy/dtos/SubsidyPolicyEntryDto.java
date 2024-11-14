package com.autohome.app.cars.service.components.subsidy.dtos;

import com.autohome.app.cars.service.components.subsidy.enums.SubsidyPolicyEntryItemTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/5/27
 */
@Data
public class SubsidyPolicyEntryDto {

    private String subsidyTitle;

    private String subsidyPrice;

    private int subsidyPriceInt;

    private int specId;

    private int cityId;

    /**
     * scene 用于埋点
     * 1=Factory和local均大于0，有金融/车主权益补贴，
     * 2=Factory和local均大于0，无金融/车主权益补贴，
     * 3=Factory和local其中一项大于0，该项包含2个大于0的子项，有金融/车主权益补贴，
     * 4=Factory和local其中一项大于0，该项包含2个大于0的子项，无金融/车主权益补贴，
     * 5=Factory和local其中一项大于0，该项包含1个大于0的子项，有金融/车主权益补贴，
     * 6=Factory和local其中一项大于0，该项包含1个大于0的子项，无金融/车主权益补贴
     */
    private int scene;

    private String sumType;

    private List<TagItem> taglist;

    @Data
    public static class TagItem {

        private String title;

        /**
         * @see SubsidyPolicyEntryItemTypeEnum
         */
        private int type;

        private String price;

        private int priceInt;
    }
}
