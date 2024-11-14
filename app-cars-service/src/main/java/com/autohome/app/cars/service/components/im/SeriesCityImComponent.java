package com.autohome.app.cars.service.components.im;

import com.autohome.app.cars.apiclient.im.ChatApiClient;
import com.autohome.app.cars.apiclient.im.dtos.SeriesImResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.im.dtos.SeriesCityImInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

//TODO 和道辉一起看看数据问题

/**
 * 获取城市下车系的聊天室，如果cityid的不存在，就直接取cityId=0的数据，cityId=0的数据一般是有的
 */
@Component
@DBConfig(tableName = "series_city_im")
public class SeriesCityImComponent extends BaseComponent<SeriesCityImInfo> {

    @Autowired
    ChatApiClient chatApiClient;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName,cityId).build();
    }

    /**
     * 根据车系id+城市id获取热聊入口信息，如果不存在则返回车系默认的信息
     * @param seriesId
     * @param cityId
     * @return
     */
    public CompletableFuture<SeriesCityImInfo> get(int seriesId, int cityId) {
        List<TreeMap<String, Object>> params = new ArrayList<>();
        params.add(makeParam(seriesId, cityId));
        params.add(makeParam(seriesId, 0));
        return baseGetListAsync(params).thenApply(list -> {
            if (list == null || list.size() == 0)
                return null;
            for (SeriesCityImInfo info : list) {
                if(info==null)
                    continue;
                if (info.getCityId() == cityId)
                    return info;
            }
            return list.get(0);
        });
    }

    public void refreshAll(Consumer<String> log) {
        loopSeries(30,seriesId->{
            chatApiClient.getSeriesCitys(seriesId).thenAccept(data -> {
                if(data==null||data.getReturncode()!=0||data.getResult()==null){
                    return;
                }

                if(data.getResult().getGeneric()!=null) {
                    updateItem(seriesId, data.getResult().getGeneric());
                }

                if(data.getResult().getList()!=null && data.getResult().getList().size()>0) {
                    for (SeriesImResult.Item item : data.getResult().getList()) {
                        updateItem(seriesId, item);
                    }
                }
            }).exceptionally(e -> {
                log.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        },log);
    }

    void updateItem(int seriesId, SeriesImResult.Item item){
        SeriesCityImInfo info = new SeriesCityImInfo();
        info.setSeriesId(seriesId);
        info.setCityId(item.getCityId());
        info.setMemberCount(item.getMemberCount());
        info.setRyRoomId(item.getRyRoomId());
        info.setTargetId(item.getTargetId());
        info.setTargetType(item.getTargetType());
        update(makeParam(seriesId, item.getCityId()), info);
    }

}
