package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.owner.dtos.SeriesOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 车系用车相关 -- 李李佳
 *
 * 接口1：车系-视频按钮-链接逻辑 - 【用车说明书】车系页入口接口，固定sourceid=1,app_version=11.59.0
 * http://wiki.corpautohome.com/pages/viewpage.action?pageId=198150369
 * ownermp.corpautohome.com/ownerserviceapi/instructions/getentry?seriesid=5769&sourceid=1&app_version=11.59.0
 */
@Component
@DBConfig(tableName = "series_owner")
public class SeriesOwnerComponent extends BaseComponent<SeriesOwner> {

    static String paramName = "seriesId";

    @Autowired
    OwnerApiClient ownerApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesOwner> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(10, seriesId -> {
            ownerApiClient.getSeriesVideoInstruction(seriesId).thenAccept(data -> {
                //为null或者returncode!=0的时候，说明原接口异常，这时候不处理这条数据
                if (data == null || data.getReturncode()!=0) {
                    return;
                }
                //没有result的时候，删掉数据
                if (data.getResult() == null) {
                    delete(makeParam(seriesId));
                    return;
                }
                SeriesOwner dto = new SeriesOwner();
                dto.setVideoShowEntry(data.getResult().getShowEntry());
                dto.setVideoShowEntryScheme(data.getResult().getScheme());
                update(makeParam(seriesId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }
}
