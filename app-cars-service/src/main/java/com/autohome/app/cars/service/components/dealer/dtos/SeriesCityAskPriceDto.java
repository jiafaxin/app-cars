package com.autohome.app.cars.service.components.dealer.dtos;

import com.autohome.app.cars.common.utils.CityUtil;
import lombok.Data;

import java.util.HashSet;
import java.util.List;

@Data
public class SeriesCityAskPriceDto {
    int seriesId;
    int cityId;
    //所有车车型最低售价经销商ID
    int minPriceDealer;
    //所有车型最低售价
    int minPrice;
    //所有车型最高售价经销商ID
    int maxPriceDealer;
    //所有车型最高售价
    int maxPrice;
    //车型数量
    int specCount;
    //在售车型最低价格
    int minPriceOnSale;
    //在售车型最高价格
    int maxPriceOnSale;

    HashSet<Integer> localCity;

    boolean cityLocal;

    // TODO chengjincheng 2024/8/19 8.19为修复线上问题增加该字段，修复的测试只保证本市情况的正确性，另外两种类型，使用前需要验证
    /**
     * 当dto为某市的报价信息时，该报价是否为售本市的经销商
     * 0-本市 1-本省 2-全国
     */
    Integer priceType;

    /**
     * 当dto为某省的报价信息时，对于该车系哪些城市有本地售卖的经销商
     */
    List<Integer> saleCityList;



    public boolean isLocalPrice(int cityId){
        //本市价格
        if(cityId==getCityId()){
            return isCityLocal();
        }
        //本省
        if(CityUtil.getProvinceId(cityId)==cityId){
            return getLocalCity()!=null && getLocalCity().contains(cityId);
        }
        //全国
        if(cityId==0){
            return getLocalCity()!=null && getLocalCity().contains(cityId);
        }
        return false;
    }

}
