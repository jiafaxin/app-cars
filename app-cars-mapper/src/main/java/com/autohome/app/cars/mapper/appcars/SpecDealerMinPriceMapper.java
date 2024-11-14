package com.autohome.app.cars.mapper.appcars;

import com.autohome.app.cars.mapper.appcars.entities.SpecDealerMinPriceEntity;
import com.autohome.app.cars.mapper.appcars.providers.SpecDealerMinPriceProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
@DS("appcars")
public interface SpecDealerMinPriceMapper {

    @UpdateProvider(value = SpecDealerMinPriceProvider.class,method = "insertBeatch")
    void insertBeatch(List<SpecDealerMinPriceEntity> infos);

}
