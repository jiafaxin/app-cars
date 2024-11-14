package com.autohome.app.cars.mapper.popauto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeriesMapperTest {

    @Autowired
    SeriesMapper seriesMapper;

    @Test
    void getAllSeries() {
        Assertions.assertTrue(seriesMapper.getAllSeries().size() > 1);
    }

    @Test
    void getSeries() {
        Assertions.assertTrue(seriesMapper.getSeries(18).getId() == 18);
    }

    @Test
    void getSeriesList() {
        Assertions.assertTrue(seriesMapper.getSeriesList(Arrays.asList(18, 60)).size() == 2);
    }

}