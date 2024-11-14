package com.autohome.app.cars.service.components.subsidy.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 置换补贴相关字段释义，见下方wiki
 * http://wiki.corpautohome.com/pages/viewpage.action?pageId=337942886
 *
 * @author chengjincheng
 * @date 2024/4/28
 */
@Data
public class SpecCitySubsidyDto {

    private int city_id;

    private int spec_id;

    private int series_id;

    private int brand_id;

    private List<Integer> channel_id;

    private List<LocalBenefit> local_benefits;

    private List<FactoryBenefit> factory_benefits;

    private BenefitSum benefit_sum;

    @Data
    public static class LocalBenefit {

        private Date start_time;

        private Date end_time;

        private int amount;

        private String subsidy_type_name;

        private int subsidy_type_id;

        private String subsidy_form_name;

        private int subsidy_form_id;

        private String entrance_path;

        private String policy_text;

        private String benefit_id;

    }

    @Data
    public static class FactoryBenefit {

        private Date start_time;

        private Date end_time;

        private int amount;

        private String subsidy_type_name;

        private int subsidy_type_id;

        private String sub_type_name;

        private int sub_type_id;

        private String subsidy_form_name;

        private String benefit_title;

        private String benefit_content;

        private String benefit_short_content;

        private int sort_key;

        private String benefit_id;

    }

    @Data
    public static class Sum {

        private int sum;

        private List<String> benefit_id_list;

    }

    @Data
    public static class BenefitSum {

        private Sum new_car_sum;

        private Sum replace_this_brand_sum;

        private Sum replace_other_brand_sum;

        private Sum replace_all_brand_sum;

        private Sum add_this_brand_sum;

        private Sum add_other_brand_sum;

        private Sum add_all_brand_sum;

        private Sum recycle_then_new_car_sum;

        private Sum max_sum;

    }
}
