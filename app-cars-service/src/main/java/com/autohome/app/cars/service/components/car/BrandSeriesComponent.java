package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.mapper.popauto.entities.BrandFctSeriesEntity;
import com.autohome.app.cars.service.components.car.dtos.BrandSeriesDto;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 根据品牌Id取到所有的车系Id
 * {厂商id:[车系ids]}
 */
@Component
@DBConfig(tableName = "brand_series")
public class BrandSeriesComponent extends BaseComponent<BrandSeriesDto> {

    @Autowired
    BrandMapper brandMapper;

    final String brandIdParamName = "brandId";

    public CompletableFuture<BrandSeriesDto> get(int brandId) {
        return baseGetAsync(makeParam(brandId));
    }

    public BrandSeriesDto getByBrandId(int brandId){
        return baseGet(makeParam(brandId));
    }

    @Override
    protected BrandSeriesDto sourceData(TreeMap<String, Object> params) {
        int brandId = getIdFromParam(params);
        return buildDto(brandId, brandMapper.getBrandSeriesIds(brandId));
    }

    public void refreshAll(Consumer<String> log) {
        for (BrandEntity brand : brandMapper.getAllBrands()) {
            update(makeParam(brand.getId()), buildDto(brand.getId(), brandMapper.getBrandSeriesIds(brand.getId())));
            log.accept("success:" + brand.getId());
        }
    }

    BrandSeriesDto buildDto(int brandId,List<BrandFctSeriesEntity> entities) {
        BrandSeriesDto result = new BrandSeriesDto();
        result.setBrandId(brandId);

        Map<Integer, BrandSeriesDto.FactoryItem> map = new LinkedHashMap<>();
        for (BrandFctSeriesEntity entity : entities) {
            BrandSeriesDto.FactoryItem factoryItem;
            if (!map.containsKey(entity.getFctid())) {
                factoryItem = new BrandSeriesDto.FactoryItem();
                factoryItem.setId(entity.getFctid());
                factoryItem.setPy(entity.getFctPy());
                result.getFctoryList().add(factoryItem);
                map.put(entity.getFctid(), factoryItem);
            } else {
                factoryItem = map.get(entity.getFctid());
            }
            factoryItem.getSeriesList().add(new BrandSeriesDto.SeriesItem() {{
                setId(entity.getSeriesId());
                setState(entity.getSeriesState());
            }});
        }
        return result;
    }

    TreeMap<String, Object> makeParam(int brandId) {
        return ParamBuilder.create(brandIdParamName, brandId).build();
    }

    int getIdFromParam(TreeMap<String, Object> param) {
        if (param == null || !param.containsKey(brandIdParamName))
            return 0;
        return (int)param.get(brandIdParamName);
    }

}
