package com.autohome.app.cars.apiclient.sou;

import com.autohome.app.cars.apiclient.sou.dtos.SouGoodsAndNewCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@AutoHttpClient
public interface CarSearchApiClient {
    /**
     * 车品搜索列表接口
     */
    @AutoGet(
            dev = "http://sou.api.autohome.com.cn/v3/carsearch/goodsAndNewCar?q=&biztype=610112&spec=${specId}&offset=0&size=10",
            beta = "http://sou.api.autohome.com.cn/v3/carsearch/goodsAndNewCar?q=&biztype=610112&spec=${specId}&offset=0&size=10",
            online = "http://sou.api.autohome.com.cn/v3/carsearch/goodsAndNewCar?q=&biztype=610112&spec=${specId}&offset=0&size=10"
    )
    CompletableFuture<BaseModel<SouGoodsAndNewCarResult>> getSouGoodsAndNewCar(int specId);
}
