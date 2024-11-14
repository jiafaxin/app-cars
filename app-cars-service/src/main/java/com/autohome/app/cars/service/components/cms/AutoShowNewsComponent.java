package com.autohome.app.cars.service.components.cms;

import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowNewsDto;
import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 车展资讯相关 -- 沈巨明
 */
@Component
@DBConfig(tableName = "auto_show_news")
public class AutoShowNewsComponent extends BaseComponent<AutoShowNewsDto> {

    static String autoShowIdName = "autoShowId";

    static String seriesIdParamName = "seriesId";

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    CmsApiClient cmsApiClient;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;


    TreeMap<String, Object> makeParam(int autoShowId, int seriesId) {
        return ParamBuilder.create(autoShowIdName, autoShowId).add(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<AutoShowNewsDto> get(int autoShowId, int seriesId) {
        if (autoShowConfig.IsBetweenDate()) {
            return baseGetAsync(makeParam(autoShowId, seriesId));
        }
        return CompletableFuture.completedFuture(null);
    }

    public void refreshAll(int totalMinutes, Consumer<String> log) {
        log.accept("车展配置：" + JsonUtil.toString(autoShowConfig));
        if (!autoShowConfig.IsBetweenDate()) {
            log.accept("不在车展时间内");
            return;
        }
        if (autoShowConfig.IsBetweenDate()) {
            loopSeries(totalMinutes, (seriesId) -> {
                cmsApiClient.getAutoShowNews(autoShowConfig.getAutoshowid(), seriesId).thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0 || data.getResult() == null) {
                        return;
                    }
                    if (data.getResult().getItems().isEmpty() || data.getResult().getItems().size() < 3) {
                        delete(makeParam(autoShowConfig.getAutoshowid(), seriesId));
                    } else {
                        AutoShowNewsDto newsDto = new AutoShowNewsDto();
                        data.getResult().getItems().forEach(x -> {
                            AutoShowNewsDto.NewsItem item = new AutoShowNewsDto.NewsItem();
                            BeanUtils.copyProperties(x.getMainDataAll(), item);
                            newsDto.getNewsItems().add(item);
                        });

                        updateAutoShow(makeParam(autoShowConfig.getAutoshowid(), seriesId), newsDto);
                    }
                }).exceptionally(e -> {
                    log.accept("资讯车展数据失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join();
            }, log);
        }
        log.accept("刷新资讯车展数据成功");
    }
}
