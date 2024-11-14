package com.autohome.app.cars.mapper.popauto;


import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
public class BrandMapperTest {

    @Autowired
    BrandMapper brandMapper;

    @Test
    public void getAllBrands() {
        BrandEntity brand = brandMapper.getBrand(1);
        Assertions.assertTrue(brand.getId() == 1);
        List<BrandEntity> brands = brandMapper.getAllBrands();
        Assertions.assertTrue(brands.size() > 1);
    }
}