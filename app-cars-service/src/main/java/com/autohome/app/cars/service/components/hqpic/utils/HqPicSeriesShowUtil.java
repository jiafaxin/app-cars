package com.autohome.app.cars.service.components.hqpic.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author chengjincheng
 * @date 2024/8/18
 */
@Service
public class HqPicSeriesShowUtil {

    @Value("#{T(com.autohome.app.cars.service.components.hqpic.utils.SeriesShowConfig).decode('${series_show_config:}')}")
    SeriesShowConfig seriesShowConfig;

    public boolean seriesShow(int seriesId) {
        if (Objects.isNull(seriesShowConfig)) {
            return false;
        }
        if (seriesShowConfig.getShowType() == 0) {
            return false;
        } else if (seriesShowConfig.getShowType() == 1) {
            return seriesShowConfig.getSeriesIdList().contains(seriesId);
        } else {
            return true;
        }
    }

}
