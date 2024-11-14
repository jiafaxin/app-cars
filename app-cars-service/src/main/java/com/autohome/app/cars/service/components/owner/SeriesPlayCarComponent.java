package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.apiclient.owner.dtos.PlayCarCardResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.owner.dtos.SeriesCityZhaodijiaInfo;
import com.autohome.app.cars.service.components.owner.dtos.SeriesOwner;
import com.autohome.app.cars.service.components.owner.dtos.SeriesPlayCardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 车系玩车Tab
 */
@Component
@DBConfig(tableName = "series_play_car")
public class SeriesPlayCarComponent extends BaseComponent<PlayCarCardResult> {

    static String paramName = "seriesId";

    final static String cityParamName = "cityId";

    @Autowired
    OwnerApiClient ownerApiClient;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(paramName, seriesId).add(cityParamName, cityId).build();
    }
    
     //启用压缩
     @Override
     protected boolean gzip() {
         return true;
     }

    public CompletableFuture<PlayCarCardResult> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshClearAll(Consumer<String> xxlLog) {
        loopSeriesCity(60, (seriesId, cityId) -> {
            delete(makeParam(seriesId, cityId));
        }, xxlLog);
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {

        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {
            refreshOne(xxlLog, seriesId, cityId);
        }, xxlLog);

//        loopSeries(totalMinutes, seriesId -> {
//            refreshOne(xxlLog, seriesId, 120100);
//        }, xxlLog);
    }

    public void refreshOne(Consumer<String> xxlLog, int seriesId, int cityId) {
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
        if (seriesDetailDto == null) {
            return;
        }
        ownerApiClient.getPlayCarCard(seriesId, seriesDetailDto.getLevelId(), cityId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                return;
            }
            if (data.getResult() == null || data.getResult().getList() == null || data.getResult().getList().isEmpty()) {
                delete(makeParam(seriesId, cityId));
                return;
            }
            PlayCarCardResult dto = data.getResult();

            dto.getList().forEach(card -> {
                card.getCarddata().getCardinfo().getImg().forEach(img -> {
                    boolean toWebp = true;
                    if (img.getUrl().contains("userphotos")) {
                        toWebp = false;
                    }
                    img.setUrl(ImageUtils.convertImageUrl(img.getUrl(), toWebp, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts, true, true, true));

                });
                card.getCarddata().getCardinfo().getTaginfo().stream().filter(x -> x.getPosition() == 1000).forEach(tag -> {
                    tag.setBgcolor("#150088FF");
                    tag.setFontcolor("#FF0088FF");
                });
            });

            update(makeParam(seriesId, cityId), dto);
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }).join();
    }
}
