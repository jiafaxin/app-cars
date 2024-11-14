package com.autohome.app.cars.service.components.subsidy;

import com.autohome.app.cars.service.components.car.SeriesAttentionComponent;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecCitySubsidyDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecSubsidyCountDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/5/27
 */
@Slf4j
@Component
public class SubsidyPolicyCommonComponent {

    @Autowired
    private SpecCitySubsidyComponent specCitySubsidyComponent;

    @Autowired
    private SeriesSpecComponent seriesSpecComponent;

    @Autowired
    private SeriesAttentionComponent seriesAttentionComponent;

    public List<SpecSubsidyCountDto> getSpecBySeries(int seriesId, int cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        // 获取车系下所有的车型
        AtomicReference<SeriesSpecDto> seriesSpecDtoRef = new AtomicReference<>();
        tasks.add(seriesSpecComponent.getAsync(seriesId)
                .thenAccept(seriesSpecDtoRef::set)
                .exceptionally(ex -> null));
        // 获取车系下所有车型关注度
        AtomicReference<SeriesAttentionDto> seriesAttentionDtoRef = new AtomicReference<>();
        tasks.add(seriesAttentionComponent.get(seriesId)
                .thenAccept(seriesAttentionDtoRef::set)
                .exceptionally(ex -> null));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        // 车型关注度Map
        Map<Integer, Integer> specAttMap = Objects.nonNull(seriesAttentionDtoRef.get())
                ? seriesAttentionDtoRef.get().getSpecAttentions().stream()
                .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                        SeriesAttentionDto.SpecAttention::getAttention))
                : new HashMap<>();
        // 获取所有在售车型对应的补贴信息
        List<Integer> onSaleSpecIdList = Objects.nonNull(seriesSpecDtoRef.get())
                ? seriesSpecDtoRef.get().getItems().stream()
                .filter(e -> e.getState() == 20 || e.getState() == 30)
                .map(SeriesSpecDto.Item::getId)
                .distinct()
                .toList()
                : new ArrayList<>();
        List<SpecCitySubsidyDto> specCitySubsidyDtoList =
                specCitySubsidyComponent.getList(onSaleSpecIdList, cityId).join();

        // 获取有品牌/地区补贴活动的车型List
        List<SpecSubsidyCountDto> specSubsidyCountList = new ArrayList<>();
        List<SpecSubsidyCountDto> finalSpecSubsidyCountList = specSubsidyCountList;
        specCitySubsidyDtoList.stream()
                .filter(Objects::nonNull)
                .filter(e -> {
                    if ((Objects.nonNull(e.getBenefit_sum().getNew_car_sum())
                            && e.getBenefit_sum().getNew_car_sum().getSum() > 0)
                            || (Objects.nonNull(e.getBenefit_sum().getReplace_this_brand_sum())
                            && e.getBenefit_sum().getReplace_this_brand_sum().getSum() > 0)
                            || (Objects.nonNull(e.getBenefit_sum().getReplace_other_brand_sum())
                            && e.getBenefit_sum().getReplace_other_brand_sum().getSum() > 0)
                            || (Objects.nonNull(e.getBenefit_sum().getReplace_all_brand_sum())
                            && e.getBenefit_sum().getReplace_all_brand_sum().getSum() > 0)
                            || (Objects.nonNull(e.getBenefit_sum().getAdd_this_brand_sum())
                            && e.getBenefit_sum().getAdd_this_brand_sum().getSum() > 0)
                            || (Objects.nonNull(e.getBenefit_sum().getAdd_other_brand_sum())
                            && e.getBenefit_sum().getAdd_other_brand_sum().getSum() > 0)
                            || (Objects.nonNull(e.getBenefit_sum().getAdd_all_brand_sum())
                            && e.getBenefit_sum().getAdd_all_brand_sum().getSum() > 0)) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .forEach(subsidyDto -> {
                    int localBenefitsCount = 0;
                    int factoryBenefitsCount = 0;

                    if (!CollectionUtils.isEmpty(subsidyDto.getLocal_benefits())) {
                        List<SpecCitySubsidyDto.LocalBenefit> localBenefits =
                                subsidyDto.getLocal_benefits().stream()
                                        .filter(e -> !StringUtils.equals("报废买新", e.getSubsidy_type_name()))
                                        .toList();
                        localBenefitsCount = localBenefits.size();
                    }
                    if (!CollectionUtils.isEmpty(subsidyDto.getFactory_benefits())) {
                        factoryBenefitsCount = subsidyDto.getFactory_benefits().size();
                    }
                    SpecSubsidyCountDto specSubsidyCountDto = new SpecSubsidyCountDto();
                    specSubsidyCountDto.setSpecId(subsidyDto.getSpec_id());
                    specSubsidyCountDto.setSubsidyCount(localBenefitsCount + factoryBenefitsCount);
                    specSubsidyCountDto.setHasSubsidy(true);
                    specSubsidyCountDto.setHasFactoryBenefits(factoryBenefitsCount > 0);
                    finalSpecSubsidyCountList.add(specSubsidyCountDto);
                });

        // 取有【厂商活动】或【政策活动】且【amount≠0】
        // 按照amount≠0活动的活动数量倒序展示（多的在左边），对应数量一致的则从关注度高到关注度低展示
        specSubsidyCountList = specSubsidyCountList.stream()
                .sorted(Comparator.comparing(SpecSubsidyCountDto::getSpecId).reversed()).toList();
        if (!CollectionUtils.isEmpty(specSubsidyCountList)) {
            return specSubsidyCountList.stream()
                    .sorted((o1, o2) -> {
                        if (o1.getSubsidyCount() < o2.getSubsidyCount()) {
                            return 1;
                        } else if (o1.getSubsidyCount() == o2.getSubsidyCount()) {
                            if (Objects.nonNull(specAttMap.get(o1.getSpecId())) && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                                return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                            }
                            return 1;
                        } else {
                            return -1;
                        }
                    })
                    .toList();
        } else {
            return Collections.emptyList();
        }

    }


    public List<SpecSubsidyCountDto> getAllSpecBySeries(int seriesId, int cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        // 获取车系下所有的车型
        AtomicReference<SeriesSpecDto> seriesSpecDtoRef = new AtomicReference<>();
        tasks.add(seriesSpecComponent.getAsync(seriesId)
                .thenAccept(seriesSpecDtoRef::set)
                .exceptionally(ex -> null));
        // 获取车系下所有车型关注度
        AtomicReference<SeriesAttentionDto> seriesAttentionDtoRef = new AtomicReference<>();
        tasks.add(seriesAttentionComponent.get(seriesId)
                .thenAccept(seriesAttentionDtoRef::set)
                .exceptionally(ex -> null));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        // 车型关注度Map
        Map<Integer, Integer> specAttMap = Objects.nonNull(seriesAttentionDtoRef.get())
                ? seriesAttentionDtoRef.get().getSpecAttentions().stream()
                .collect(Collectors.toMap(SeriesAttentionDto.SpecAttention::getSpecid,
                        SeriesAttentionDto.SpecAttention::getAttention))
                : new HashMap<>();
        // 获取所有在售车型对应的补贴信息
        List<Integer> onSaleSpecIdList = Objects.nonNull(seriesSpecDtoRef.get())
                ? seriesSpecDtoRef.get().getItems().stream()
                .filter(e -> e.getState() == 20 || e.getState() == 30)
                .map(SeriesSpecDto.Item::getId)
                .distinct()
                .toList()
                : new ArrayList<>();
        List<SpecCitySubsidyDto> specCitySubsidyDtoList =
                specCitySubsidyComponent.getList(onSaleSpecIdList, cityId).join();

        // 获取有品牌/地区补贴活动的车型List
        List<SpecSubsidyCountDto> specSubsidyCountList = new ArrayList<>();
        List<SpecSubsidyCountDto> finalSpecSubsidyCountList = specSubsidyCountList;
        specCitySubsidyDtoList.stream()
                .filter(Objects::nonNull)
                .forEach(subsidyDto -> {
                    int localBenefitsCount = 0;
                    int factoryBenefitsCount = 0;

                    if (!CollectionUtils.isEmpty(subsidyDto.getLocal_benefits())) {
                        List<SpecCitySubsidyDto.LocalBenefit> localBenefits =
                                subsidyDto.getLocal_benefits().stream()
                                        .filter(e -> !StringUtils.equals("报废买新", e.getSubsidy_type_name()))
                                        .toList();
                        localBenefitsCount = localBenefits.size();
                    }
                    if (!CollectionUtils.isEmpty(subsidyDto.getFactory_benefits())) {
                        factoryBenefitsCount = subsidyDto.getFactory_benefits().size();
                    }
                    SpecSubsidyCountDto specSubsidyCountDto = new SpecSubsidyCountDto();
                    specSubsidyCountDto.setSpecId(subsidyDto.getSpec_id());
                    specSubsidyCountDto.setSubsidyCount(localBenefitsCount + factoryBenefitsCount);
                    specSubsidyCountDto.setHasSubsidy(true);
                    specSubsidyCountDto.setHasFactoryBenefits(factoryBenefitsCount > 0);
                    finalSpecSubsidyCountList.add(specSubsidyCountDto);
                });

        // 按照活动数量倒序展示（多的在左边），对应数量一致的则从关注度高到关注度低展示
        specSubsidyCountList = specSubsidyCountList.stream()
                .sorted(Comparator.comparing(SpecSubsidyCountDto::getSpecId).reversed()).toList();

        List<SpecSubsidyCountDto> result;
        if (!CollectionUtils.isEmpty(specSubsidyCountList)) {
            result = new ArrayList<>(specSubsidyCountList.stream()
                    .sorted((o1, o2) -> {
                        if (o1.getSubsidyCount() < o2.getSubsidyCount()) {
                            return 1;
                        } else if (o1.getSubsidyCount() == o2.getSubsidyCount()) {
                            if (Objects.nonNull(specAttMap.get(o1.getSpecId())) && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                                return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                            }
                            return 1;
                        } else {
                            return -1;
                        }
                    })
                    .toList());
        } else {
            result = new ArrayList<>();
        }

        // 再将所有在售车型都加到列表后面
        List<Integer> allOnSaleSpecIdList = new ArrayList<>(onSaleSpecIdList);
        allOnSaleSpecIdList.removeAll(specSubsidyCountList.stream().map(SpecSubsidyCountDto::getSpecId).toList());
        List<SpecSubsidyCountDto> onlyOnSaleSpecIdList = allOnSaleSpecIdList.stream()
                .map(e -> new SpecSubsidyCountDto(e, 0, false, false))
                .sorted((o1, o2) -> {
                    if (Objects.nonNull(specAttMap.get(o1.getSpecId()))
                            && Objects.nonNull(specAttMap.get(o2.getSpecId()))) {
                        return specAttMap.get(o2.getSpecId()) - specAttMap.get(o1.getSpecId());
                    }
                    return 1;
                })
                .toList();
        result.addAll(onlyOnSaleSpecIdList);

        return result;
    }
}
