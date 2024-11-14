package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.ApiPcMuscClient;
import com.autohome.app.cars.apiclient.che168.dtos.KeepValueSeriesResult;
import com.autohome.app.cars.apiclient.che168.dtos.SeriesYearCityPriceResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.KeepValueSeriesInfo;
import com.autohome.app.cars.service.components.che168.dtos.PriceRangeInfo;
import com.autohome.app.cars.service.components.che168.dtos.SeriesYearCityPriceInfo;
import com.autohome.app.cars.service.components.che168.dtos.SeriesYearPriceInfo;
import com.autohome.autolog4j.common.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "series_year_ershou_price")
public class SeriesYearCityPriceComponent extends BaseComponent<SeriesYearPriceInfo> {

    @SuppressWarnings("all")
    @Autowired
    private Api2scautork2Client api2scautork2Client;

    final static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    /**
     * 根据车系id 获取各个城市报价区间和全国报价数据
     *
     * @param seriesId
     * @return
     */
    public CompletableFuture<SeriesYearCityPriceInfo> getByCity(int seriesId, int city) {
        return get(seriesId).thenApply(seriesYearPriceInfo -> {
            SeriesYearCityPriceInfo priceInfo = new SeriesYearCityPriceInfo();
            if (ObjectUtils.isEmpty(seriesYearPriceInfo)) {
                return priceInfo;
            }
            //车系id
            priceInfo.setSeriesId(seriesYearPriceInfo.getSeriesId());
            //获取全国价格区间信息
            priceInfo.setAll(CollectionUtils.isEmpty(seriesYearPriceInfo.getAllInfoList()) ? null : seriesYearPriceInfo.getAllInfoList().get(0));
            //获取各城市价格区间信息
            if (!CollectionUtils.isEmpty(seriesYearPriceInfo.getCityInfoList())) {
                seriesYearPriceInfo.getCityInfoList().forEach(cityInfo -> {
                    if (city == cityInfo.getCityid()) {
                        priceInfo.setCityInfo(cityInfo);
                    }
                });
            }
            return priceInfo;
        });
    }

    /**
     * 根据车系id 获取各城市报价区间和全国报价区间数据
     * 数据说明：
     * 1.存在一些冷门车系没有数据情况
     * 1.各个城市报价区间数据 下年款会有差异（年款数据不连贯）, 比如：一些冷门车系，在北京正在售卖的只有2012、2015款,所以返回的数据只有2012、2015两个年款价格区间信息
     * 2.全国报价区间数据 同上也有类型情况
     *
     * @param seriesId
     * @return
     */
    public CompletableFuture<SeriesYearPriceInfo> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> {
            refreshOne(xxlLog, seriesId);
        }, xxlLog);
    }

    public void refreshOne(Consumer<String> xxlLog, Integer seriesId) {
        api2scautork2Client.getSeriesYearCityPrice(seriesId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                xxlLog.accept(String.format("seriesId:%d returnCode !=0 meg:%s", seriesId, data.getMessage()));
                return;
            }
            SeriesYearCityPriceResult result = data.getResult();
            if (ObjectUtils.isEmpty(result)) {
                xxlLog.accept(seriesId + "无数据！");
                return;
            }

            SeriesYearPriceInfo priceInfo = new SeriesYearPriceInfo();
            priceInfo.setSeriesId(seriesId);
            priceInfo.setCityInfoList(JacksonUtil.deserialize(JacksonUtil.serialize(result.getCityinfo()),
                    new TypeReference<List<PriceRangeInfo>>() {
                    }));
            priceInfo.setAllInfoList(JacksonUtil.deserialize(JacksonUtil.serialize(result.getOtherinfo()),
                    new TypeReference<List<PriceRangeInfo>>() {
                    }));
            update(makeParam(seriesId), priceInfo);
            xxlLog.accept("seriesId:" + seriesId + "成功");
        }).exceptionally(e -> {
            xxlLog.accept("seriesId:" + seriesId + ",失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        });
    }
}
