package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.IMApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerIMResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityDealerIMInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author : zzli
 * @description : 经销商IM入口数据
 * @date : 2024/4/23 19:57
 */
@Component
@DBConfig(tableName = "series_city_dealer_im")
public class SeriesCityDealerIMComponent extends BaseComponent<SeriesCityDealerIMInfo> {
    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    @Autowired
    SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;
    @Autowired
    SeriesDetailComponent seriesDetailComponent;
    @Autowired
    IMApiClient imApiClient;

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityDealerIMInfo> get(int seriesId, int cityId, int brandId, int sourceId) {
        BaseModel<DealerIMResult> imResult = imApiClient.getImEntranceInfo(brandId, seriesId, 0, cityId, sourceId).join();
        if (imResult != null && imResult.getResult() != null) {
            SeriesCityDealerIMInfo imInfo = new SeriesCityDealerIMInfo();
            imInfo.setBtnname(imResult.getResult().getBtnname());
            imInfo.setLinkurl(imResult.getResult().getLinkurl());
            return CompletableFuture.completedFuture(imInfo);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

//    public CompletableFuture<SeriesCityDealerIMInfo> get(int seriesId, int cityId) {
//        return baseGetAsync(makeParam(seriesId, cityId));
//    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        HashSet<String> newKeys = new HashSet<>();

        loopSeriesCity(totalMinutes, (seriesId, cityId) -> seriesCityAskPriceNewComponent.get(seriesId, cityId).thenAccept(data -> {
            if (data != null) {
                int brandId = seriesDetailComponent.get(seriesId).getBrandId();
                // sourceId其实不能直接写死，有125和51之分
                BaseModel<DealerIMResult> imResult = imApiClient.getImEntranceInfo(brandId, seriesId, 0, cityId, 51).join();
                if (imResult != null && imResult.getResult() != null) {
                    SeriesCityDealerIMInfo imInfo = new SeriesCityDealerIMInfo();
                    imInfo.setBtnname(imResult.getResult().getBtnname());
                    imInfo.setLinkurl(imResult.getResult().getLinkurl());
                    update(makeParam(seriesId, cityId), imInfo);
                    newKeys.add(getKey(makeParam(seriesId, cityId)));
                }
            }
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "," + cityId + "error " + ExceptionUtil.getStackTrace(e));
            return null;
        }).join(), xxlLog);
        // TODO chengjincheng 2024/6/6 分片处理不能使用批量历史删除，会导致所有数据被删除
        deleteHistory(newKeys, xxlLog);
    }
}
