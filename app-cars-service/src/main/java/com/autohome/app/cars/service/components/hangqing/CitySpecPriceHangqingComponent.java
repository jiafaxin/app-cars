package com.autohome.app.cars.service.components.hangqing;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.dtos.CitySortHangqingDto;
import com.autohome.app.cars.service.components.hangqing.dtos.CitySpecPriceHangqingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chengjincheng
 * @date 2024/6/20
 */
@Slf4j
@Component
public class CitySpecPriceHangqingComponent extends BaseComponent<CitySpecPriceHangqingDto> {
    final static String cityParamName = "cityId";

    final static String specIdParamName = "specId";

    final static String seriesIdParamName = "seriesId";

    @Autowired
    private CitySortHangqingComponent citySortHangqingComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    TreeMap<String, Object> makeParam(int cityId, int specId, int seriesId) {
        return ParamBuilder.create(cityParamName, cityId)
                .add(specIdParamName, specId)
                .add(seriesIdParamName, seriesId)
                .build();
    }

    public String get(TreeMap<String, Object> params) {
        CitySpecPriceHangqingDto dto = get((int) params.get("cityId"),
                (int) params.get("specId"),
                (int) params.get("seriesId"));
        return JsonUtil.toString(dto);
    }

    public CitySpecPriceHangqingDto get(int cityId, int specId, int seriesId) {
        AtomicReference<CitySortHangqingDto> priceOffRecentlyHangqingDto = new AtomicReference<>();
        AtomicReference<CitySortHangqingDto> priceHisHangqingDto = new AtomicReference<>();
        AtomicReference<SpecCityAskPriceDto> specCityAskPriceDto = new AtomicReference<>();
        AtomicReference<SpecDetailDto> specDetailDto = new AtomicReference<>();
        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(citySortHangqingComponent.getAsync(cityId, 5).thenAccept(priceOffRecentlyHangqingDto::set).exceptionally(e -> null));
        tasks.add(citySortHangqingComponent.getAsync(cityId, 6).thenAccept(priceHisHangqingDto::set).exceptionally(e -> null));
        tasks.add(specCityAskPriceComponent.get(specId, cityId).thenAccept(specCityAskPriceDto::set).exceptionally(e -> null));
        tasks.add(specDetailComponent.get(specId).thenAccept(specDetailDto::set).exceptionally(e -> null));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

        CitySpecPriceHangqingDto citySpecPriceHangqingDto = new CitySpecPriceHangqingDto();
        citySpecPriceHangqingDto.setSpecId(specId);
        if (Objects.nonNull(specDetailDto.get())) {
            citySpecPriceHangqingDto.setSpecMinPrice(specDetailDto.get().getMinPrice());
        }
        if (Objects.nonNull(specCityAskPriceDto.get())) {
            citySpecPriceHangqingDto.setDealerMinPrice(specCityAskPriceDto.get().getMinPrice());
        }

        if (Objects.nonNull(priceOffRecentlyHangqingDto.get()) &&
                !CollectionUtils.isEmpty(priceOffRecentlyHangqingDto.get().getDtoList())) {
            CitySortHangqingDto.HangqingDto hangqingDto = priceOffRecentlyHangqingDto.get().getDtoList().stream()
                    .filter(e -> e.getSeriesId() == seriesId && Objects.nonNull(e.getPriceOffInfo()))
                    .findFirst().orElse(null);
            if (Objects.nonNull(hangqingDto)) {
                CitySortHangqingDto.SpecPriceOffInfo specPriceOffInfo =
                        hangqingDto.getPriceOffInfo().getSpecPriceOffInfoList().stream()
                                .filter(e -> e.getSpecId() == specId)
                                .findFirst().orElse(null);
                if (Objects.nonNull(specPriceOffInfo)) {
                    CitySpecPriceHangqingDto.PriceOffInfo priceOffInfo = new CitySpecPriceHangqingDto.PriceOffInfo();
                    priceOffInfo.setDt(specPriceOffInfo.getDt());
                    priceOffInfo.setPriceOff(specPriceOffInfo.getPriceOff());
                    citySpecPriceHangqingDto.setPriceOffInfo(priceOffInfo);
                }
            }
        }
        if (Objects.nonNull(priceHisHangqingDto.get()) &&
                !CollectionUtils.isEmpty(priceHisHangqingDto.get().getDtoList())) {
            CitySortHangqingDto.HangqingDto hangqingDto = priceHisHangqingDto.get().getDtoList().stream()
                    .filter(e -> e.getSeriesId() == seriesId && Objects.nonNull(e.getPriceHisInfo()))
                    .findFirst().orElse(null);
            if (Objects.nonNull(hangqingDto)) {
                CitySortHangqingDto.SpecPriceHisInfo specPriceHisInfo =
                        hangqingDto.getPriceHisInfo().getSpecPriceHisInfoList().stream()
                                .filter(e -> e.getSpecId() == specId)
                                .findFirst().orElse(null);
                if (Objects.nonNull(specPriceHisInfo)) {
                    CitySpecPriceHangqingDto.PriceHisInfo priceHisInfo = new CitySpecPriceHangqingDto.PriceHisInfo();
                    priceHisInfo.setLastDt(specPriceHisInfo.getLastDt());
                    priceHisInfo.setPriceHisTag(specPriceHisInfo.getPriceHisTag());
                    citySpecPriceHangqingDto.setPriceHisInfo(priceHisInfo);
                }
            }
        }
        return citySpecPriceHangqingDto;
    }

}


