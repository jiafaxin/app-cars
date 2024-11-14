package com.autohome.app.cars.service.components.subsidy;

import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecCitySubsidyDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecSubsidyCountDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SubsidyPolicyEntryDto;
import com.autohome.app.cars.service.components.subsidy.enums.SubsidyPolicyEntryItemTypeEnum;
import com.autohome.app.cars.service.components.subsidy.enums.SubsidySumTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/5/27
 */
@Slf4j
@Component
public class SubsidyPolicyEntryComponent extends BaseComponent<SubsidyPolicyEntryDto> {

    @Autowired
    private SpecCitySubsidyComponent specCitySubsidyComponent;

    @Autowired
    private SubsidyPolicyCommonComponent subsidyPolicyCommonComponent;

    public String get(TreeMap<String, Object> params) {
        SubsidyPolicyEntryDto dto = get((int) params.get("seriesId"),
                (int) params.get("specId"),
                (int) params.get("cityId"))
                .join();
        return JsonUtil.toString(dto);
    }

    public CompletableFuture<SubsidyPolicyEntryDto> get(int seriesId, int specId, int cityId) {
        // 车型id为0时，根据车系找到在售车型，并取最多活动且amount不为0的车型
        if (specId == 0) {
            List<SpecSubsidyCountDto> specSubsidyList = subsidyPolicyCommonComponent.getSpecBySeries(seriesId, cityId);
            if (CollectionUtils.isEmpty(specSubsidyList)) {
                return CompletableFuture.completedFuture(null);
            } else {
                specId = specSubsidyList.get(0).getSpecId();
            }
        }

        SpecCitySubsidyDto specCitySubsidyDto = specCitySubsidyComponent.get(specId, cityId).join();
        if (Objects.isNull(specCitySubsidyDto) || Objects.isNull(specCitySubsidyDto.getBenefit_sum())) {
            // 该车型城市没有补贴信息，返回null不展示入口
            return CompletableFuture.completedFuture(null);
        }

        // 取加和中的最大值
        SubsidySumDto subsidySumDto = buildSubsidySumDto(specCitySubsidyDto);

        // 组装补贴政策入口数据
        SubsidyPolicyEntryDto subsidyPolicyEntryDto = buildSubsidyPolicyEntryDto(specCitySubsidyDto,
                subsidySumDto, cityId);
        return CompletableFuture.completedFuture(subsidyPolicyEntryDto);
    }

    private SubsidySumDto buildSubsidySumDto(SpecCitySubsidyDto specCitySubsidyDto) {
        // 取该车型、城市的sum中，除<报废买新加和><最高补贴加和值>外的最大sum值。
        // 有相同的值时，取值顺序为：新购加和>本品牌（渠道）置换加和>非本品牌（渠道）置换加和>不限品牌置换加和>本品牌（渠道）增购加和>非本品牌（渠道）增购加和>不限品牌（渠道）增购加和
        SubsidySumDto subsidySumDto = new SubsidySumDto();
        SpecCitySubsidyDto.BenefitSum benefitSum = specCitySubsidyDto.getBenefit_sum();
        if (Objects.nonNull(benefitSum.getNew_car_sum())) {
            subsidySumDto.setType(SubsidySumTypeEnum.NEW_CAR_SUM);
            subsidySumDto.setSum(benefitSum.getNew_car_sum());
        }
        if (Objects.nonNull(benefitSum.getReplace_this_brand_sum())) {
            if (Objects.isNull(subsidySumDto.getSum())
                    || benefitSum.getReplace_this_brand_sum().getSum() > subsidySumDto.getSum().getSum()) {
                subsidySumDto.setType(SubsidySumTypeEnum.REPLACE_THIS_BRAND_SUM);
                subsidySumDto.setSum(benefitSum.getReplace_this_brand_sum());
            }
        }
        if (Objects.nonNull(benefitSum.getReplace_other_brand_sum())) {
            if (Objects.isNull(subsidySumDto.getSum())
                    || benefitSum.getReplace_other_brand_sum().getSum() > subsidySumDto.getSum().getSum()) {
                subsidySumDto.setType(SubsidySumTypeEnum.REPLACE_OTHER_BRAND_SUM);
                subsidySumDto.setSum(benefitSum.getReplace_other_brand_sum());
            }
        }
        if (Objects.nonNull(benefitSum.getReplace_all_brand_sum())) {
            if (Objects.isNull(subsidySumDto.getSum())
                    || benefitSum.getReplace_all_brand_sum().getSum() > subsidySumDto.getSum().getSum()) {
                subsidySumDto.setType(SubsidySumTypeEnum.REPLACE_ALL_BRAND_SUM);
                subsidySumDto.setSum(benefitSum.getReplace_all_brand_sum());
            }
        }
        if (Objects.nonNull(benefitSum.getAdd_this_brand_sum())) {
            if (Objects.isNull(subsidySumDto.getSum())
                    || benefitSum.getAdd_this_brand_sum().getSum() > subsidySumDto.getSum().getSum()) {
                subsidySumDto.setType(SubsidySumTypeEnum.ADD_THIS_BRAND_SUM);
                subsidySumDto.setSum(benefitSum.getAdd_this_brand_sum());
            }
        }
        if (Objects.nonNull(benefitSum.getAdd_other_brand_sum())) {
            if (Objects.isNull(subsidySumDto.getSum())
                    || benefitSum.getAdd_other_brand_sum().getSum() > subsidySumDto.getSum().getSum()) {
                subsidySumDto.setType(SubsidySumTypeEnum.ADD_OTHER_BRAND_SUM);
                subsidySumDto.setSum(benefitSum.getAdd_other_brand_sum());
            }
        }
        if (Objects.nonNull(benefitSum.getAdd_all_brand_sum())) {
            if (Objects.isNull(subsidySumDto.getSum())
                    || benefitSum.getAdd_all_brand_sum().getSum() > subsidySumDto.getSum().getSum()) {
                subsidySumDto.setType(SubsidySumTypeEnum.ADD_ALL_BRAND_SUM);
                subsidySumDto.setSum(benefitSum.getAdd_all_brand_sum());
            }
        }
        return subsidySumDto;
    }

    private SubsidyPolicyEntryDto buildSubsidyPolicyEntryDto(SpecCitySubsidyDto specCitySubsidyDto,
                                                             SubsidySumDto subsidySumDto,
                                                             int cityId) {
        // 根据最大的加和来处理补贴信息的构成项
        // 厂商补贴Map
        Map<String, SpecCitySubsidyDto.FactoryBenefit> factoryBenefitMap =
                CollectionUtils.isEmpty(specCitySubsidyDto.getFactory_benefits())
                        ? new HashMap<>()
                        : specCitySubsidyDto.getFactory_benefits().stream().collect(
                        Collectors.toMap(SpecCitySubsidyDto.FactoryBenefit::getBenefit_id, Function.identity(),
                                (k1, k2) -> k2));
        // 地方补贴Map
        Map<String, SpecCitySubsidyDto.LocalBenefit> localBenefitMap =
                CollectionUtils.isEmpty(specCitySubsidyDto.getLocal_benefits())
                        ? new HashMap<>()
                        : specCitySubsidyDto.getLocal_benefits().stream().collect(
                        Collectors.toMap(SpecCitySubsidyDto.LocalBenefit::getBenefit_id, Function.identity(),
                                (k1, k2) -> k2));
        // 其他补贴总数
        int otherSubsidyCount = Objects.isNull(specCitySubsidyDto.getFactory_benefits())
                ? 0
                : (int) specCitySubsidyDto.getFactory_benefits().stream()
                .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "金融")
                        || StringUtils.equals(e.getSubsidy_type_name(), "车主权益"))
                .count();
        List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitList = new ArrayList<>();
        List<SpecCitySubsidyDto.LocalBenefit> localBenefitList = new ArrayList<>();
        // 厂商补贴or地方补贴，都只取金额大于0的补贴数据
        if(Objects.nonNull(subsidySumDto.getSum())) {
            for (String benefitId : subsidySumDto.getSum().getBenefit_id_list()) {
                if (Objects.nonNull(factoryBenefitMap.get(benefitId))
                        && factoryBenefitMap.get(benefitId).getAmount() > 0) {
                    factoryBenefitList.add(factoryBenefitMap.get(benefitId));
                } else if (Objects.nonNull(localBenefitMap.get(benefitId))
                        && localBenefitMap.get(benefitId).getAmount() > 0) {
                    localBenefitList.add(localBenefitMap.get(benefitId));
                }
            }
        }

        if (factoryBenefitList.isEmpty() && localBenefitList.isEmpty()) {
            // 如果厂商补贴和地方补贴均为空，则表示没有补贴信息，返回null不展示入口
            return null;
        }

        // 补贴政策信息的展示规则
        // 参考：https://doc.autohome.com.cn/docapi/page/share/share_vT2HAYRUPo
        SubsidyPolicyEntryDto subsidyPolicyEntryDto = new SubsidyPolicyEntryDto();
        subsidyPolicyEntryDto.setSumType(subsidySumDto.getType().name());
        subsidyPolicyEntryDto.setSpecId(specCitySubsidyDto.getSpec_id());
        subsidyPolicyEntryDto.setCityId(cityId);
        subsidyPolicyEntryDto.setTaglist(new ArrayList<>());
        if (!factoryBenefitList.isEmpty() && !localBenefitList.isEmpty()) {
            subsidyPolicyEntryDto.setSubsidyTitle("购车有机会享受最高补贴");
            subsidyPolicyEntryDto.setSubsidyPrice(subsidySumDto.getSum().getSum() + "元");
            subsidyPolicyEntryDto.setSubsidyPriceInt(subsidySumDto.getSum().getSum());
            SubsidyPolicyEntryDto.TagItem tagItem1 = new SubsidyPolicyEntryDto.TagItem();
            tagItem1.setTitle("品牌补贴");
            tagItem1.setType(SubsidyPolicyEntryItemTypeEnum.BRAND.getValue());
            int priceInt1 = factoryBenefitList.stream()
                    .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum();
            tagItem1.setPrice(priceInt1 + "元");
            tagItem1.setPriceInt(priceInt1);
            subsidyPolicyEntryDto.getTaglist().add(tagItem1);

            SubsidyPolicyEntryDto.TagItem tagItem2 = new SubsidyPolicyEntryDto.TagItem();
            tagItem2.setTitle(CityUtil.getCityName(cityId) + "政策补贴");
            tagItem2.setType(SubsidyPolicyEntryItemTypeEnum.CITY.getValue());
            int priceInt2 = localBenefitList.stream()
                    .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum();
            tagItem2.setPrice(priceInt2 + "元");
            tagItem2.setPriceInt(priceInt2);
            subsidyPolicyEntryDto.getTaglist().add(tagItem2);

            if (otherSubsidyCount > 0) {
                SubsidyPolicyEntryDto.TagItem tagItem3 = new SubsidyPolicyEntryDto.TagItem();
                tagItem3.setTitle("其他补贴");
                tagItem3.setType(SubsidyPolicyEntryItemTypeEnum.OTHER.getValue());
                tagItem3.setPrice(otherSubsidyCount + "项");
                tagItem3.setPriceInt(0);
                subsidyPolicyEntryDto.getTaglist().add(tagItem3);
                subsidyPolicyEntryDto.setScene(1);
            } else {
                subsidyPolicyEntryDto.setScene(2);
            }

        } else if (factoryBenefitList.size() == 2 || factoryBenefitList.size() == 1) {
            List<SpecCitySubsidyDto.FactoryBenefit> replaceFactoryBenefit = factoryBenefitList.stream()
                    .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "置换")).toList();
            if (!CollectionUtils.isEmpty(replaceFactoryBenefit)) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle("品牌置换补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.BRAND_REPLACE.getValue());
                tagItem.setPrice(replaceFactoryBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum() + "元");
                tagItem.setPriceInt(replaceFactoryBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum());
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            List<SpecCitySubsidyDto.FactoryBenefit> addFactoryBenefit = factoryBenefitList.stream()
                    .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "增购")).toList();
            if (!CollectionUtils.isEmpty(addFactoryBenefit)) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle("品牌增购补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.BRAND_ADD.getValue());
                tagItem.setPrice(addFactoryBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum() + "元");
                tagItem.setPriceInt(addFactoryBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum());
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            List<SpecCitySubsidyDto.FactoryBenefit> newFactoryBenefit = factoryBenefitList.stream()
                    .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "新购"))
                    .toList();
            if (!CollectionUtils.isEmpty(newFactoryBenefit)) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle("品牌新购补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.BRAND_NEW.getValue());
                tagItem.setPrice(newFactoryBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum() + "元");
                tagItem.setPriceInt(newFactoryBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount).sum());
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            if (otherSubsidyCount > 0) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle("其他补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.OTHER.getValue());
                tagItem.setPrice(otherSubsidyCount + "项");
                tagItem.setPriceInt(0);
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            if (subsidyPolicyEntryDto.getTaglist().size() == 1) {
                subsidyPolicyEntryDto.setSubsidyTitle("购车有机会享受 "
                        + subsidyPolicyEntryDto.getTaglist().get(0).getTitle());
            } else {
                subsidyPolicyEntryDto.setSubsidyTitle("购车有机会享受最高补贴");
            }

            subsidyPolicyEntryDto.setSubsidyPrice(subsidySumDto.getSum().getSum() + "元");
            subsidyPolicyEntryDto.setSubsidyPriceInt(subsidySumDto.getSum().getSum());

            if (subsidyPolicyEntryDto.getTaglist().size() == 3) {
                subsidyPolicyEntryDto.setScene(3);
            } else if (subsidyPolicyEntryDto.getTaglist().size() == 2) {
                if (otherSubsidyCount > 0) {
                    subsidyPolicyEntryDto.setScene(5);
                } else {
                    subsidyPolicyEntryDto.setScene(4);
                }
            } else {
                subsidyPolicyEntryDto.setScene(6);
            }

        } else if (localBenefitList.size() == 2 || localBenefitList.size() == 1) {
            List<SpecCitySubsidyDto.LocalBenefit> replaceLocalBenefit = localBenefitList.stream()
                    .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "置换"))
                    .toList();
            if (!CollectionUtils.isEmpty(replaceLocalBenefit)) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle(CityUtil.getCityName(cityId) + "置换补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.CITY_REPLACE.getValue());
                tagItem.setPrice(replaceLocalBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum() + "元");
                tagItem.setPriceInt(replaceLocalBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum());
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            List<SpecCitySubsidyDto.LocalBenefit> addLocalBenefit = localBenefitList.stream()
                    .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "增购"))
                    .toList();
            if (!CollectionUtils.isEmpty(addLocalBenefit)) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle(CityUtil.getCityName(cityId) + "增购补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.CITY_ADD.getValue());
                tagItem.setPrice(addLocalBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum() + "元");
                tagItem.setPriceInt(addLocalBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum());
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            List<SpecCitySubsidyDto.LocalBenefit> newLocalBenefit = localBenefitList.stream()
                    .filter(e -> StringUtils.equals(e.getSubsidy_type_name(), "新购"))
                    .toList();
            if (!CollectionUtils.isEmpty(newLocalBenefit)) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle(CityUtil.getCityName(cityId) + "新购补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.CITY_NEW.getValue());
                tagItem.setPrice(newLocalBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum() + "元");
                tagItem.setPriceInt(newLocalBenefit.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount).sum());
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            if (otherSubsidyCount > 0) {
                SubsidyPolicyEntryDto.TagItem tagItem = new SubsidyPolicyEntryDto.TagItem();
                tagItem.setTitle("其他补贴");
                tagItem.setType(SubsidyPolicyEntryItemTypeEnum.OTHER.getValue());
                tagItem.setPrice(otherSubsidyCount + "项");
                tagItem.setPriceInt(0);
                subsidyPolicyEntryDto.getTaglist().add(tagItem);
            }

            if (subsidyPolicyEntryDto.getTaglist().size() == 1) {
                subsidyPolicyEntryDto.setSubsidyTitle("购车有机会享受 "
                        + subsidyPolicyEntryDto.getTaglist().get(0).getTitle());
            } else {
                subsidyPolicyEntryDto.setSubsidyTitle("购车有机会享受最高补贴");
            }

            subsidyPolicyEntryDto.setSubsidyPrice(subsidySumDto.getSum().getSum() + "元");
            subsidyPolicyEntryDto.setSubsidyPriceInt(subsidySumDto.getSum().getSum());

            if (subsidyPolicyEntryDto.getTaglist().size() == 3) {
                subsidyPolicyEntryDto.setScene(3);
            } else if (subsidyPolicyEntryDto.getTaglist().size() == 2) {
                if (otherSubsidyCount > 0) {
                    subsidyPolicyEntryDto.setScene(5);
                } else {
                    subsidyPolicyEntryDto.setScene(4);
                }
            } else {
                subsidyPolicyEntryDto.setScene(6);
            }

        } else {
            return null;
        }
        return subsidyPolicyEntryDto;
    }

    @Data
    private static class SubsidySumDto {

        private SubsidySumTypeEnum type;

        private SpecCitySubsidyDto.Sum sum;
    }

}
