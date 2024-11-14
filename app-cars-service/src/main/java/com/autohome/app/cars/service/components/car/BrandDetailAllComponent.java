package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.BrandInfoDto;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class BrandDetailAllComponent extends BaseComponent<List<BrandInfoDto>> {


    @Autowired
    private BrandMapper brandMapper;

    TreeMap<String, Object> makeParam() {
        return ParamBuilder.create().build();
    }

    public CompletableFuture<List<BrandInfoDto>> getAsync() {
        return baseGetAsync(makeParam());
    }

    public List<BrandInfoDto> get() {
        return baseGet(makeParam());
    }


    @Override
    protected List<BrandInfoDto> sourceData(TreeMap<String, Object> params) {
        return builder(brandMapper.getAllBrandSort());
    }


    public void refreshAll() {
        List<BrandEntity> allBrands = brandMapper.getAllBrandSort();
        update(ParamBuilder.create().build(), builder(allBrands));
    }

    List<BrandInfoDto> builder(List<BrandEntity> entityList) {
        List<BrandInfoDto> brandInfoDtoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(entityList)){
            for(BrandEntity brandEntity : entityList){
                if(brandEntity.getId() == 266) {
                    continue;
                }
                BrandInfoDto brandInfoDto = new BrandInfoDto();
                brandInfoDto.setId(brandEntity.getId());
                brandInfoDto.setName(StringEscapeUtils.unescapeHtml4(brandEntity.getName()));
                brandInfoDto.setLogo(brandEntity.getImg());
                brandInfoDto.setLetter(brandEntity.getFirstLetter());
                brandInfoDto.setSort(brandEntity.getOrdercls());
                brandInfoDto.setIsNewEnergy(brandEntity.getIsNewEnergy());
                brandInfoDto.setState(brandEntity.getState());
                brandInfoDtoList.add(brandInfoDto);

            }
        }
        return brandInfoDtoList;
    }
}
