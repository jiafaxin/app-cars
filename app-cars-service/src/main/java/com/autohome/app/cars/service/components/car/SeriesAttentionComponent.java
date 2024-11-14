package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.openApi.LnglatApiClient;
import com.autohome.app.cars.apiclient.openApi.dtos.SeriesAttentionResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 *
 * 原接口维护人： 王晓龙
 * 原接口数据每天凌晨5点更新一次
 * 接口数据 一个小时更新一次吧
 */

@Component
@DBConfig(tableName = "series_attention")
public class SeriesAttentionComponent extends BaseComponent<SeriesAttentionDto> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecMapper specMapper;

    @Autowired
    LnglatApiClient lnglatApiClient;

    static final String paramName = "seriesId";

    public CompletableFuture<SeriesAttentionDto> get(int seriesId){
        return baseGetAsync(makeParam(seriesId));
    }

    public CompletableFuture<List<SeriesAttentionDto>> getList(List<Integer> seriesIds) {
        return baseGetListAsync(seriesIds.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    public CompletableFuture<Integer> getHotSpecId(int seriesId){
        return get(seriesId).thenApply(x->{
            if(x==null||x.getSpecAttentions()==null|| x.getSpecAttentions().size()==0)
                return 0;
            return x.getSpecAttentions().get(0).getSpecid();
        });
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());
        loopSeries(30,seriesId->{
            lnglatApiClient.getSeriesAtention(seriesId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0 || data.getResult() == null || data.getResult().getSpeclist() == null || data.getResult().getSpeclist().size() == 0)
                    return;
                SeriesAttentionDto dto = new SeriesAttentionDto();
                dto.setSeriesId(seriesId);
                for (SeriesAttentionResult.SpecItem item : data.getResult().getSpeclist()) {
                    if (item == null)
                        continue;

                    SpecEntity specEntity = specAll.stream().filter(p -> p.getSeriesId() == seriesId && p.getId() == item.getSpecid()).findFirst().orElse(null);
                    dto.getSpecAttentions().add(new SeriesAttentionDto.SpecAttention() {{
                        setSpecid(item.getSpecid());
                        if (specEntity!=null) {
                            setYearName(specEntity.getYearName());
                            setParamIsShow(specEntity.getParamIsShow());
                            setSpecname(specEntity != null ? specEntity.getName() : "");
                        }
                        setAttention(item.getAttention());
                    }});
                }
                update(makeParam(seriesId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });
        },xxlLog);
    }

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }



}
