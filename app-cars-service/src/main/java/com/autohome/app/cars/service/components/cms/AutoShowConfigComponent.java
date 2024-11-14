package com.autohome.app.cars.service.components.cms;

import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SeriesPicMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesPicEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowConfigDto;
import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 车展相关 -- 沈巨明
 */
@Component
@DBConfig(tableName = "auto_show_config")
public class AutoShowConfigComponent extends BaseComponent<AutoShowConfigDto> {

    static String paramName = "autoShowId";

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesPicMapper seriesPicMapper;

    @Autowired
    CmsApiClient cmsApiClient;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;

    TreeMap<String, Object> makeParam(int autoShowId) {
        return ParamBuilder.create(paramName, autoShowId).build();
    }

    public CompletableFuture<AutoShowConfigDto> get(int autoShowId) {
        if (autoShowConfig.IsBetweenDate()) {
            return baseGetAsync(makeParam(autoShowId));
        }
        return CompletableFuture.completedFuture(null);
    }

    public void refreshAll(Consumer<String> log) {

        log.accept("车展配置：" + JsonUtil.toString(autoShowConfig));
        if (!autoShowConfig.IsBetweenDate()) {
            log.accept("不在车展时间内");
            return;
        }
        AutoShowConfigDto configDto = new AutoShowConfigDto();
        if (autoShowConfig.IsBetweenDate()) {
            List<SeriesPicEntity> autoShowPicList = seriesPicMapper.getSeriesAutoShowPicCountAll(autoShowConfig.getAutoshowid());

            cmsApiClient.getAutoShowConfig(autoShowConfig.getAutoshowid()).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0 || data.getResult() == null || data.getResult().getItems().isEmpty()) {
                    return;
                }

                data.getResult().getItems().forEach(x -> {
                    AutoShowConfigDto.NewsCarItem newsCarItem = new AutoShowConfigDto.NewsCarItem();
                    newsCarItem.setBrandId(x.getBrandId());
                    newsCarItem.setSeriesId(x.getSeriesId());
                    newsCarItem.setCarAction(x.getCarAction());
                    newsCarItem.setTagIds(x.getTagIds());
                    configDto.getNewsCarItems().add(newsCarItem);
                });
            }).exceptionally(e -> {
                log.accept("资讯车展数据失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();

            autoShowPicList.forEach(x -> {
                AutoShowConfigDto.CpkSeriesPic cpkSeriesPic = new AutoShowConfigDto.CpkSeriesPic();
                cpkSeriesPic.setBrandId(x.getBrandId());
                cpkSeriesPic.setSeriesId(x.getSeriesId());
                cpkSeriesPic.setPicCount(x.getPicCount());
                configDto.getCpkSeriesPics().add(cpkSeriesPic);
            });

            update(makeParam(autoShowConfig.getAutoshowid()), configDto);
        }
        log.accept("刷新车展配置成功");
    }
}
