package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.che168.dtos.SeriesUsedCarInfo;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 和何峰确认了，城市id参数没用了
 */
@Component
@DBConfig(tableName = "series_usedcar")
public class SeriesUsedCarComponent extends BaseComponent<SeriesUsedCarInfo> {

    @Autowired
    Api2scautork2Client api2scautork2Client;
    @Autowired
    SeriesDetailComponent seriesDetailComponent;
    static final String SeriesIdParamName = "seriesId";


    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(SeriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesUsedCarInfo> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, (seriesId) -> {
            SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
//            int seriesState = seriesDetailDto != null ? -1 : seriesDetailDto.getState();
//            String pvareaid = seriesState == 40 ? "112877" : "111397";
            api2scautork2Client.getUsedCarsJumpInfo(seriesId).thenCombine(api2scautork2Client.getUsedCarKeepRate(seriesId), (data, keepData) -> {
                if (data == null || data.getReturncode() != 0) {
                    return null;
                }

                if (data.getResult() == null || "暂无报价".equals(data.getResult().getSubtitle())) {
                    delete(makeParam(seriesId));
                    return null;
                }
                SeriesUsedCarInfo dto = new SeriesUsedCarInfo();
                dto.setSeriesId(seriesId);
                dto.setJumpurl(data.getResult().getJumpurl());
                dto.setJumpurl_base(StringUtils.replaceOnce(data.getResult().getJumpurl(), "&?pvareaid=\\d+", "&pvareaid=109551").replace("&?service=\\d+", ""));
                dto.setSubTitle(data.getResult().getSubtitle());
                dto.setTitle(data.getResult().getTitle());
                //dto.setPvareaid(pvareaid);
                if (keepData != null && keepData.getResult() != null && keepData.getReturncode() == 0) {
                    dto.setRate(keepData.getResult().getRate());
                }

                update(makeParam(seriesId), dto);

                return null;
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }

}
