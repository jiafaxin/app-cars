package com.autohome.app.cars.service.components.che168;

import com.autohome.app.cars.apiclient.che168.ApiPcMuscClient;
import com.autohome.app.cars.apiclient.che168.dtos.KeepValueSeriesResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.che168.dtos.KeepValueSeriesInfo;
import com.autohome.autolog4j.common.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "series_keep_value")
public class SeriesKeepValueComponent extends BaseComponent<KeepValueSeriesInfo> {

    @SuppressWarnings("all")
    @Autowired
    private ApiPcMuscClient apiPcMuscClient;

    final static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    /**
     * 数据说明：
     * 该接口同时返回 该车系每年保值率，和该车系同级保值率 这两个重要对象信息
     * 1.存在 车系每年保值率 = null，车系同级别保值率 ！=null 的情况，例如：刚上市的小米SU7
     * 2.存在 车系每年保值率 ！= null，车系同级别保值率 =null 的情况，例如：一些冷门大型MPV级别车系
     * 3.存在 车系每年保值率 = null，车系同级别保值率 =null 的情况，例如：一些轻卡级别车
     *
     * @param seriesId
     * @return
     */
    public CompletableFuture<KeepValueSeriesInfo> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> {
            apiPcMuscClient.getKeepValueBySeriesId(seriesId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    xxlLog.accept(String.format("seriesId:%d returnCode !=0 meg:%s", seriesId, data.getMessage()));
                    return;
                }
                KeepValueSeriesResult result = data.getResult();
                if (ObjectUtils.isEmpty(result)) {
                    xxlLog.accept(seriesId + "无数据！");
                    return;
                }

                KeepValueSeriesInfo keepValueInfo = new KeepValueSeriesInfo();
                keepValueInfo.setSeriesId(seriesId);
                keepValueInfo.setFollowrank(result.getFollowrank());
                keepValueInfo.setSerieskeeprate(JacksonUtil.deserialize(JacksonUtil.serialize(result.getSerieskeeprate()),
                        new TypeReference<List<KeepValueSeriesInfo.KeepRateInfo>>() {
                        }));
                keepValueInfo.setLevelkeeprate(JacksonUtil.deserialize(JacksonUtil.serialize(result.getLevelkeeprate()),
                        new TypeReference<List<KeepValueSeriesInfo.KeepRateInfo>>() {
                        }));
                update(makeParam(seriesId), keepValueInfo);
                xxlLog.accept(seriesId + "成功");
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        }, xxlLog);
    }

}
