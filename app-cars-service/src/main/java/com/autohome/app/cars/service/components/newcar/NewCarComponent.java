package com.autohome.app.cars.service.components.newcar;

import com.autohome.app.cars.apiclient.vr.PanoApiClient;
import com.autohome.app.cars.apiclient.vr.VrApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.apiclient.vr.dtos.VrSuperCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 新车上市 - 曹飞交寄给吕明了
 */
@Component
//@DBConfig(tableName = "series_newcar")
public class NewCarComponent extends BaseComponent<SeriesVr> {

    static String paramName = "seriesId";

    @Autowired
    SeriesMapper seriesMapper;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesVr> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> log) {
        
    }
}