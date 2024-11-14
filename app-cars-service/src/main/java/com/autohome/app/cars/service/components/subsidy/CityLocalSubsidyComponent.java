package com.autohome.app.cars.service.components.subsidy;

import com.autohome.app.cars.apiclient.subsidy.SubsidyApiClient;
import com.autohome.app.cars.apiclient.subsidy.dtos.LocalPolicyResult;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SubsidyInfoMapper;
import com.autohome.app.cars.mapper.popauto.entities.SubsidyInfoEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.subsidy.dtos.CityLocalSubsidyDto;
import com.autohome.app.cars.service.components.subsidy.enums.PolicyTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * 地方消费补贴政策组件
 * @date 2024/9/25
 */
@Component
@DBConfig(tableName = "city_local_subsidy")
@Slf4j
public class CityLocalSubsidyComponent extends BaseComponent<List<CityLocalSubsidyDto>> {

    static String paramName = "cityId";

    @SuppressWarnings("all")
    @Autowired
    SubsidyApiClient subsidyApiClient;
    @Autowired
    SubsidyInfoMapper subsidyInfoMapper;

    TreeMap<String, Object> makeParam(int cityId) {
        return ParamBuilder.create(paramName, cityId).build();
    }

    public CompletableFuture<List<CityLocalSubsidyDto>> getAsync(int cityId) {
        return baseGetAsync(makeParam(cityId));
    }

    public List<CityLocalSubsidyDto> get(int cityId) {
        return baseGet(makeParam(cityId));
    }

    public String get(TreeMap<String, Object> params) {
        List<CityLocalSubsidyDto> dto = get((int) params.get("cityId"));
        return JsonUtil.toString(dto);
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<SubsidyInfoEntity> subsidyList = subsidyInfoMapper.getSubsidyList();

        CityUtil.getAllCityIds().forEach(cityId -> {
                    subsidyApiClient.getCityLocalPolicy(cityId)
                            .thenAcceptAsync(data -> {
                                if (data == null || data.getReturncode() != 0) {
                                    return;
                                }
                                if (data.getResult() == null
                                        || CollectionUtils.isEmpty(data.getResult())) {
                                    delete(makeParam(cityId));
                                    return;
                                }

                                List<LocalPolicyResult> resultList = data.getResult();
                                List<CityLocalSubsidyDto> dtoList = new ArrayList<>();
                                resultList.forEach(r -> {
                                    PolicyTypeEnum byType = PolicyTypeEnum.getByType(r.getTitle());
                                    if (Objects.nonNull(byType)) {
                                        CityLocalSubsidyDto dto = new CityLocalSubsidyDto();
                                        dto.setTitle(byType.getAlias());
                                        dto.setSubsidyTypeId(r.getSubsidyTypeId());
                                        dto.setSubsidySubTypeId(byType.getId());

                                        if (!CollectionUtils.isEmpty(r.getContents())) {
                                            List<String> entrancePath = r.getContents().stream()
                                                    .filter(subList -> subList.stream().anyMatch(s -> s.contains("领取路径"))).findFirst().orElse(null);

                                            if (entrancePath != null) {
                                                dto.getReceivePath().add(entrancePath.get(1));
                                            }
                                        }
                                        dto.getReceivePath().add("上传购车材料");
                                        dto.getReceivePath().add("等待政府审核");
                                        dto.getReceivePath().add("审核通过，补贴发放至指定账户");
                                        dto.setStartTime(r.getStartTime());
                                        dto.setEndTime(r.getEndTime());
                                        dto.setOrder(r.getOrder());
                                        if (!CollectionUtils.isEmpty(r.getPriceList())) {
                                            dto.setPriceList(r.getPriceList().stream().map(e -> {
                                                CityLocalSubsidyDto.PriceSubsidyDto priceSubsidyDto =
                                                        new CityLocalSubsidyDto.PriceSubsidyDto();
                                                priceSubsidyDto.setMinPrice(e.getMinPrice());
                                                priceSubsidyDto.setMinPriceInclude(e.getMinPriceInclude());
                                                priceSubsidyDto.setMaxPrice(e.getMaxPrice());
                                                priceSubsidyDto.setMaxPriceInclude(e.getMaxPriceInclude());
                                                priceSubsidyDto.setAmount(e.getAmount());
                                                return priceSubsidyDto;
                                            }).toList());
                                        }
                                        dto.setCarLevelIdSet(r.getCarLevelIdSet());
                                        dto.setFuelTypeSet(r.getFuelTypeSet());

                                        //补贴政策
                                        int provinceId = CityUtil.getProvinceId(cityId);
                                        SubsidyInfoEntity subsidyInfoEntity = subsidyList.stream().filter(x -> NumberUtils.toInt(x.getCityid()) == cityId && NumberUtils.toInt(x.getPolicytype()) == r.getSubsidyTypeId()).findFirst().orElse(null);

                                        if (Objects.isNull(subsidyInfoEntity)) {
                                            xxlLog.accept("省份：" + provinceId + ",类型：" + r.getSubsidyTypeId());
                                            subsidyInfoEntity = subsidyList.stream().filter(x -> NumberUtils.toInt(x.getCityid()) == 0 && NumberUtils.toInt(x.getProvinceid()) == provinceId && NumberUtils.toInt(x.getPolicytype()) == r.getSubsidyTypeId()).findFirst().orElse(null);
                                        } else {
                                            xxlLog.accept("城市：" + cityId + ",类型：" + r.getSubsidyTypeId());
                                        }

                                        if (subsidyInfoEntity != null) {
                                            xxlLog.accept("补贴政策：" + subsidyInfoEntity.getSubsidytext());
                                            dto.setSubsidyPolicy(subsidyInfoEntity.getSubsidytext());
                                        }
                                        dtoList.add(dto);
                                    } else {
                                        xxlLog.accept("业务方新增了类型未匹配到！！！！！" + r.getTitle());
                                    }
                                });

                                update(makeParam(cityId), dtoList);
                                xxlLog.accept("城市" + cityId + "成功");
                            }).exceptionally(e -> {
                                xxlLog.accept(cityId + "失败:" + ExceptionUtil.getStackTrace(e));
                                return null;
                            }).join();
                    ThreadUtil.sleep(500);
                }
        );
    }
}
