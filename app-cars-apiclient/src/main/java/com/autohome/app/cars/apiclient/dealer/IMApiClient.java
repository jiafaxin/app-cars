package com.autohome.app.cars.apiclient.dealer;

import com.autohome.app.cars.apiclient.dealer.dtos.DealerIMResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author : zzli
 * @description : 经销商IM
 * @date : 2024/4/23 20:12
 */
@AutoHttpClient
public interface IMApiClient {
    @AutoGet(
            dev = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?sourceId=${sourceId}&appid=app&cityid=${cityId}&brandid=${brandId}&specid=${specId}&seriesid=${seriesId}&appVerison=11.61.5",
            beta = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?sourceId=${sourceId}&appid=app&cityid=${cityId}&brandid=${brandId}&specid=${specId}&seriesid=${seriesId}&appVerison=11.61.5",
            online = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?sourceId=${sourceId}&appid=app&cityid=${cityId}&brandid=${brandId}&specid=${specId}&seriesid=${seriesId}&appVerison=11.61.5",
            timeout = 1000
    )
    CompletableFuture<BaseModel<DealerIMResult>> getImEntranceInfo(int brandId, int seriesId, int specId,int cityId, int sourceId);

    @AutoGet(
            dev = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?sourceId=${sourceId}&appid=app&cityid=${cityId}&brandid=${brandId}&specid=${specId}&seriesid=${seriesId}&appVerison=11.61.5",
            beta = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?sourceId=${sourceId}&appid=app&cityid=${cityId}&brandid=${brandId}&specid=${specId}&seriesid=${seriesId}&appVerison=11.61.5",
            online = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?sourceId=${sourceId}&appid=app&cityid=${cityId}&brandid=${brandId}&specid=${specId}&seriesid=${seriesId}&appVerison=11.61.5",
            timeout = 180
    )
    CompletableFuture<BaseModel<DealerIMResult>> getCfgImEntranceInfo(int brandId, int seriesId, int specId,int cityId, int sourceId);

    /**
     * 获取电商mvp入口信息
     * 接口wiki：http://10.168.66.18:8082/project/362/interface/api/9590
     *
     * @param seriesId   车系id
     * @param specId     车型id
     * @param brandId    品牌id
     * @param cityId     城市id
     * @param sourceId   入口标识id
     * @param appVerison app版本号
     * @param sendText   首次创建会话后自动发送的文本（可包含链接），注意为encode之后的文本
     * @return
     */
    @AutoGet(
            dev = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?seriesid=${seriesId}&specid=${specId}&brandid=${brandId}&cityid=${cityId}&appid=app&sourceId=${sourceId}&appVerison=${appVerison}&sendText=${sendText}",
            beta = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?seriesid=${seriesId}&specid=${specId}&brandid=${brandId}&cityid=${cityId}&appid=app&sourceId=${sourceId}&appVerison=${appVerison}&sendText=${sendText}",
            online = "http://imuserapi.in.autohome.com.cn/user/im/getImEntranceParam?seriesid=${seriesId}&specid=${specId}&brandid=${brandId}&cityid=${cityId}&appid=app&sourceId=${sourceId}&appVerison=${appVerison}&sendText=${sendText}",
            timeout = 200
    )
    CompletableFuture<BaseModel<DealerIMResult>> getBusinessMvpImEntranceInfo(int seriesId, int specId, int brandId, int cityId, int sourceId, String appVerison, String sendText);
}
