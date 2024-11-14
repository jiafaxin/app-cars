package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
import com.autohome.app.cars.service.common.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@DBConfig(tableName = "brand_detail")
public class BrandDetailComponent extends BaseComponent<BrandDetailDto> {

    final String brandIdParamName = "brandId";

    @Autowired
    BrandMapper brandMapper;

    TreeMap<String, Object> makeParam(int brandId) {
        return ParamBuilder.create(brandIdParamName, brandId).build();
    }

    int getIdFromParam(Map<String, Object> param) {
        if (param == null || !param.containsKey(brandIdParamName))
            return 0;
        return (int)param.get(brandIdParamName);
    }

    public BrandDetailDto getById(int brandId){
        return baseGet(makeParam(brandId));
    }

    public CompletableFuture<BrandDetailDto> get(int brandId) {
        return baseGetAsync(makeParam(brandId));
    }

    public List<BrandDetailDto> mGet(List<Integer> brandId) {
        return baseGetList(brandId.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }


    @Override
    protected BrandDetailDto sourceData(TreeMap<String, Object> params) {
        return builder(brandMapper.getBrand(getIdFromParam(params)));
    }

    @Override
    protected Map<TreeMap<String, Object>, BrandDetailDto> sourceDatas(List<TreeMap<String, Object>> params) {
        List<Integer> ids = params.stream().map(x -> getIdFromParam(x)).collect(Collectors.toList());
        Map<TreeMap<String, Object>, BrandDetailDto> result = new LinkedHashMap<>();
        List<BrandEntity> list = brandMapper.getBrandList(ids);
        Map<Integer, BrandDetailDto> maps = list.stream().collect(Collectors.toMap(x -> x.getId(), y -> builder(y)));
        for (TreeMap<String, Object> param : params) {
            int id = getIdFromParam(param);
            BrandDetailDto item = maps.containsKey(id) ? maps.get(getIdFromParam(param)) : null;
            result.put(param, item);
        }
        return result;
    }

    public void refresh(BrandDetailDto dto) {
        update(ParamBuilder.create(brandIdParamName, dto.getId()).build(), dto);
    }

    public void refreshAll(Consumer<String> log) {
        List<BrandEntity> allBrands = brandMapper.getAllBrands();
        allBrands.forEach(series -> {
            refresh(builder(series));
            log.accept(series.getId() + "");
        });
    }

    BrandDetailDto builder(BrandEntity entity) {
        BrandDetailDto dto = new BrandDetailDto();
        if(null == entity){
            return null;
        }
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogo(CarSettings.getInstance().GetFullImagePath(entity.getImg()));
        return dto;
    }
}
