package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesPicEntity;
import com.autohome.app.cars.mapper.popauto.providers.SeriesPicProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("popauto")
public interface SeriesPicMapper {

    @SelectProvider(value = SeriesPicProvider.class, method = "getSeriesPicAll")
    List<SeriesPicEntity> getSeriesPicAll();

    @SelectProvider(value = SeriesPicProvider.class, method = "getSeriesAutoShowPicCountAll")
    List<SeriesPicEntity> getSeriesAutoShowPicCountAll(Integer autoShowId);

    @SelectProvider(value = SeriesPicProvider.class, method = "getSeriesPicTop5BySeriesId")
    List<CarPhotoViewEntity> getSeriesPicTop5BySeriesId(int seriesId);
}
