package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.CarPriceChangeMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.mapper.popauto.entities.PriceChangeEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.CarPriceChangeDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 车型降价
 * @date : 2024/10/24 10:43
 */
@Component
@DBConfig(tableName = "car_price_change")
@RedisConfig
public class CarPriceChangeComponent extends BaseComponent<CarPriceChangeDto> {
    @Autowired
    BrandMapper brandMapper;

    @Autowired
    CarPriceChangeMapper carPriceChangeMapper;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    static String brandIdParamName = "brandId";

    TreeMap<String, Object> makeParam(int brandId) {
        return ParamBuilder.create(brandIdParamName, brandId).build();
    }

    /**
     * 获取品牌下的车型（在售或停产在售）降价列表
     */
    public List<CarPriceChangeDto.CutPriceListDTO> getByBrandId(int brandId) {
        CarPriceChangeDto dto = baseGet(makeParam(brandId));
        if (dto == null || dto.getCutPriceList() == null) {
            return new ArrayList<>();
        }
        return dto.getCutPriceList().stream()
                .filter(x -> isCurrentTimeBetween(x.getStartTime(), x.getEndTime()))
                .map(x -> {
                    if (x.getChangeType() != 50) {
                        x.setEndTime(null);
                    }
                    return x;
                }).collect(Collectors.toList());
    }

    /**
     * 车系下的
     */
    public List<CarPriceChangeDto.CutPriceListDTO> getBySeriesId(int seriesId) {
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
        if (seriesDetailDto == null || seriesDetailDto.getId() == 0) {
            return new ArrayList<>();
        }
        return getByBrandId(seriesDetailDto.getBrandId()).stream().filter(x -> x.getSeriesId() == seriesId).collect(Collectors.toList());
    }


    /**
     * 车型的
     */
    public CarPriceChangeDto.CutPriceListDTO getBySpecId(int specId) {
        SpecDetailDto specDetailDto = specDetailComponent.getSync(specId);
        if (specDetailDto == null) {
            return null;
        }
        return getByBrandId(specDetailDto.getBrandId()).stream().filter(x -> x.getSpecId() == specId).findFirst().orElse(null);
    }


    public void refreshAll(Consumer<String> xxlLog) {
        List<PriceChangeEntity> priceChangeEntities = carPriceChangeMapper.GetAllList();
        Map<Integer, List<PriceChangeEntity>> priceChangeEntityMap = priceChangeEntities.stream()
                .collect(Collectors.groupingBy(PriceChangeEntity::getBrandId));
        for (BrandEntity brand : brandMapper.getAllBrands()) {
            try {
                int brandId = brand.getId();
                if (priceChangeEntityMap.containsKey(brandId)) {
                    List<PriceChangeEntity> list = priceChangeEntityMap.get(brandId);
                    CarPriceChangeDto dto = new CarPriceChangeDto();
                    list.forEach(x -> {
                        CarPriceChangeDto.CutPriceListDTO item = new CarPriceChangeDto.CutPriceListDTO();
                        item.setChangeType(x.getChangeType());
                        item.setSeriesId(x.getSeriesId());
                        item.setSpecId(x.getSpecId());
                        item.setCutPrice(x.getPriceGap());
                        item.setTargetPrice(x.getTargetPrice());
                        item.setStartTime(x.getStartTime());
                        item.setEndTime(x.getEndTime());
                        item.setH5Url(UrlUtil.getInsideBrowerSchemeWK(x.getArticleUrl()));
                        item.setDescription(x.getDescription());
                        long articleId = NumberUtils.toLong(x.getArticleId());
                        if (articleId > 0) {
                            item.setRnUrl(String.format("autohome://article/articledetailcolumn?newsid=%d&newstype=0&mediatype=0&frompage=180003&lastupdatetime=20241029113736", articleId));
                        }
                        item.setCreatedStime(x.getCreatedStime());
                        dto.getCutPriceList().add(item);
                    });
                    update(makeParam(brandId), dto);
                    xxlLog.accept("品牌:" + brandId + "成功");
                } else {
                    delete(makeParam(brandId));
                }
            } catch (Exception e) {
                xxlLog.accept("error:" + e.getMessage());
            }
        }
    }

    boolean isCurrentTimeBetween(Date startDate, Date endDate) {
        Date now = new Date();
        if (startDate != null && endDate != null) {
            return !now.before(startDate) && !now.after(endDate);
        } else if (startDate != null && endDate == null) {
            return !now.before(startDate);
        }
        return false;
    }
}
