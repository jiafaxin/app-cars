package com.autohome.car;

import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClientScan;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.HttpClientUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.car.tools.CompareJson;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com.autohome.app.cars"})
@AutoHttpClientScan("com.autohome.app.cars.apiclient")
public class SeriesPrice {

     static SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    static SeriesCityAskPriceComponent seriesCityAskPriceComponent;

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(SeriesPrice.class, args);
        SeriesMapper bean = ac.getBean(SeriesMapper.class);
        seriesCityAskPriceNewComponent = ac.getBean(SeriesCityAskPriceNewComponent.class);
        seriesCityAskPriceComponent = ac.getBean(SeriesCityAskPriceComponent.class);

        List<Integer> seriesIds = bean.getAllSeriesIds();
        seriesIds = seriesIds.stream().sorted().collect(Collectors.toList());
        List<Integer> cityIds = CityUtil.getAllCityIds().stream().sorted().collect(Collectors.toList());

        for (Integer cityId : cityIds) {
            for (Integer seriesId : seriesIds) {
                CompletableFuture<BaseModel<SeriesCityAskPriceDto>> newTask = HttpClientUtil.get("http://cars-app-http2.mesh-thallo.autohome.com.cn/getComponentValue?component=seriesCityAskPriceNew&seriesId="+seriesId+"&cityId="+cityId, new TypeReference<BaseModel<SeriesCityAskPriceDto>>() {
                },null,2000,"utf-8");
                newTask.thenCombine(CompletableFuture.supplyAsync(()->seriesCityAskPriceComponent.test4(seriesId, cityId)), (aStr, b) -> {
                    var a = aStr==null||aStr.getReturncode()!=0 && aStr.getResult()==null?null:aStr.getResult();
                    if (a == null && b == null) {

                    } else if (a == null && b != null) {
                        if(b.getMinPrice()==0 && b.getMaxPrice()==0){
                            return null;
                        }
                        System.out.println(seriesId + " - " +cityId+ " a null,b not null");
                    } else if (a != null && b == null) {
                        System.out.println(seriesId + " - " +cityId+ " a not null ,b null");
                    } else {
                        if (a.getMinPrice() != b.getMinPrice() || a.getMaxPrice() != b.getMaxPrice()) {
                            System.out.println(seriesId + " - " +cityId+ " a,b 不一致: min(" + a.getMinPrice() + " - " + b.getMinPrice() + "),max(" + a.getMaxPrice() + " - " + b.getMaxPrice() + ")");
                        }
                    }

                    return null;
                }).exceptionally(e -> {
                    System.out.println(ExceptionUtil.getStackTrace(e));
                    return null;
                }).join();
            }
        }

        System.out.println("=== success =================================");
    }
}
