package com.autohome.app.cars.apiclient.che168;

import com.autohome.app.cars.apiclient.che168.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface Api2scautork2Client {



    /**
     * 何峰
     * 确认可以刷，设备id可以固定
     * <p>
     * 2024年6月更新：有个加密上报字段用到deviceid返回不同的数据，目前这个接口值判断tab是否有不影响，但tab里的数据不能直接使用
     *
     * @param seriesId
     * @param cityId
     * @param deviceId
     * @return
     */
    @AutoGet(
            //dsh
            dev = "http://api2scautork2.lf.corpautohome.com/autohome/GetRecommendCars.ashx?_appid=2sc&cid=${cityId}&seriesid=${seriesId}&deviceid=${deviceId}&pvareaid=${pvareaid}&carpvareaid=${carpvareaid}&bpvareaid=${bpvareaid}",
            beta = "http://api2scautork2.lf.corpautohome.com/autohome/GetRecommendCars.ashx?_appid=2sc&cid=${cityId}&seriesid=${seriesId}&deviceid=${deviceId}&pvareaid=${pvareaid}&carpvareaid=${carpvareaid}&bpvareaid=${bpvareaid}",
            online = "http://api2scautork2.lf.corpautohome.com/autohome/GetRecommendCars.ashx?_appid=2sc&cid=${cityId}&seriesid=${seriesId}&deviceid=${deviceId}&pvareaid=${pvareaid}&carpvareaid=${carpvareaid}&bpvareaid=${bpvareaid}"
    )
    CompletableFuture<BaseModel<GetRecommendCarResult>> getRecommendCars2(int seriesId, int cityId, String deviceId, String pvareaid,String carpvareaid,String bpvareaid);

    /**
     * 何峰
     * cityid 与何峰确认过，原接口已经不用这个参数了
     *
     * @param seriesId
     * @return
     */
    @AutoGet(
            dev = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&cid=110100&seriesid=${seriesId}&sourceid=2",
            beta = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&cid=110100&seriesid=${seriesId}&sourceid=2",
            online = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&cid=110100&seriesid=${seriesId}&sourceid=2"
    )
    CompletableFuture<BaseModel<GetUsedCarsJumpInfoResult>> getUsedCarsJumpInfo(int seriesId);
    @AutoGet(
            dev = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&cid=110100&seriesid=${seriesId}&sourceid=2&pvareaid=${pvareaid}",
            beta = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&cid=110100&seriesid=${seriesId}&sourceid=2&pvareaid=${pvareaid}",
            online = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&cid=110100&seriesid=${seriesId}&sourceid=2&pvareaid=${pvareaid}"
    )
    CompletableFuture<BaseModel<GetUsedCarsJumpInfoResult>> getUsedCarsJumpInfo2(int seriesId,String pvareaid);

    /**
     * 何峰
     * 确认可以刷，设备id可以固定
     * <p>
     * 2024年6月更新：有个加密上报字段用到deviceid返回不同的数据，目前这个接口值判断tab是否有不影响，但tab里的数据不能直接使用
     *
     * @param seriesId
     * @param cityId
     * @param deviceId
     * @return
     */
    @AutoGet(
            dev = "http://api2scautork2.lf.corpautohome.com/autohome/GetRecommendCars.ashx?_appid=2sc&cid=${cityId}&seriesid=${seriesId}&deviceid=${deviceId}",
            beta = "http://api2scautork2.lf.corpautohome.com/autohome/GetRecommendCars.ashx?_appid=2sc&cid=${cityId}&seriesid=${seriesId}&deviceid=${deviceId}",
            online = "http://api2scautork2.lf.corpautohome.com/autohome/GetRecommendCars.ashx?_appid=2sc&cid=${cityId}&seriesid=${seriesId}&deviceid=${deviceId}"
    )
    CompletableFuture<BaseModel<GetRecommendCarResult>> getRecommendCars(int seriesId, int cityId, String deviceId);

    @AutoGet(
            dev = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetCarCount.ashx?_appid=app&cid=${cityId}&seriesid=${seriesId}",
            beta = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetCarCount.ashx?_appid=app&cid=${cityId}&seriesid=${seriesId}",
            online = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetCarCount.ashx?_appid=app&cid=${cityId}&seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<GetCarCountResult>> getCarCount(int seriesId, int cityId);


    @AutoGet(
            dev = "http://api2scautork.lf.corpautohome.com/m/CarSpecPirceRange.ashx?_app=autoapp&_callback=&cid=${cityId}&specid=${specId}&appversion=v11.60.0",
            beta = "http://api2scautork.lf.corpautohome.com/m/CarSpecPirceRange.ashx?_app=autoapp&_callback=&cid=${cityId}&specid=${specId}&appversion=v11.60.0",
            online = "http://api2scautork.lf.corpautohome.com/m/CarSpecPirceRange.ashx?_app=autoapp&_callback=&cid=${cityId}&specid=${specId}&appversion=v11.60.0"
    )
    CompletableFuture<BaseModel<List<GetCarSpecPirceRangeResult>>> getCarSpecPirceRange(int specId, int cityId);

    @AutoGet(
            dev = "http://api2scautork.cupid.autohome.com.cn/auto/spec/getcityprice?_appid=app&specid=${specId}",
            beta = "http://api2scautork.cupid.autohome.com.cn/auto/spec/getcityprice?_appid=app&specid=${specId}",
            online = "http://api2scautork.lf.corpautohome.com/auto/spec/getcityprice?_appid=app&specid=${specId}",
            timeout = 10000
    )
    CompletableFuture<BaseModel<GetCarCityPriceListResult>> getCarCityPriceList(int specId);

    /**
     * @param seriesId
     * @param specId   http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetHotSpec.ashx?_appid=2sc&cid=110100&seriesid=18
     * @return
     */
    @AutoGet(
            dev = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&seriesid=${seriesId}&specid=${specId}&sourceid=3",
            beta = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&seriesid=${seriesId}&specid=${specId}&sourceid=3",
            online = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=app.iphone&seriesid=${seriesId}&specid=${specId}&sourceid=3"
    )
    CompletableFuture<BaseModel<GetSpecUsedCarsJumpInfoResult>> getSpecUsedCarsJumpInfo(int seriesId, int specId);


    /**
     * 车系车型列表-热门二手车
     *
     * @param seriesId
     * @param cityId
     * @return
     */
    @AutoGet(
            dev = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetHotSpec.ashx?_appid=app.iphone&cid=${cityId}&seriesid=${seriesId}",
            beta = "http://api2scrn.lf.cupid.autohome.com.cn/AutoApp/Product/GetHotSpec.ashx?_appid=app.iphone&cid=${cityId}&seriesid=${seriesId}",
            online = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetHotSpec.ashx?_appid=app.iphone&cid=${cityId}&seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<GetSeriesHotSpecsResult>> GetSeriesHotSpecs(int seriesId, int cityId);


    // TODO chengjincheng 2024/5/14 该接口需要替换为新接口，此组件仅暂时用于代码开发

    /**
     * 获取二手车年代款车源数据
     *
     * @param seriesId
     * @param cityId
     * @return
     */
    @AutoGet(
            dev = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetSpecByYear.ashx?_appid=app.iphone&cid=${cityId}&seriesid=${seriesId}&year=${year}",
            beta = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetSpecByYear.ashx?_appid=app.iphone&cid=${cityId}&seriesid=${seriesId}&year=${year}",
            online = "http://api2scrn.lf.corpautohome.com/AutoApp/Product/GetSpecByYear.ashx?_appid=app.iphone&cid=${cityId}&seriesid=${seriesId}&year=${year}"
    )
    CompletableFuture<BaseModel<GetUsedCarSpecYearList>> GetUsedCarSpecYearList(int seriesId, int cityId, int year);


    /**
     * 获取二手车置换信息
     *
     * @param seriesId
     * @param cityId
     * @return
     */
    @AutoGet(
            dev = "http://yccacheapigo.che168.com/api/auto/forautomexchangecarurl?_appid=auto_app&cid=${cityId}&pid=&specid=0&bid=${brandId}&sid=${seriesId}&leadssources=25&sourcetwo=4&sourcethree=189&pos=1",
            beta = "http://yccacheapigo.che168.com/api/auto/forautomexchangecarurl?_appid=auto_app&cid=${cityId}&pid=&specid=0&bid=${brandId}&sid=${seriesId}&leadssources=25&sourcetwo=4&sourcethree=189&pos=1",
            online = "http://yccacheapigo.che168.com/api/auto/forautomexchangecarurl?_appid=auto_app&cid=${cityId}&pid=&specid=0&bid=${brandId}&sid=${seriesId}&leadssources=25&sourcetwo=4&sourcethree=189&pos=1"
    )
    CompletableFuture<BaseModel<GetUsedCarExchangeInfoResult>> getUsedCarExchangeInfo(int seriesId,
                                                                                      int cityId,
                                                                                      int brandId);

    /**
     * 获取二手车保值率
     *
     * @param seriesId
     * @return
     */
    @AutoGet(
            dev = "http://pinguapi.lq.che168.com/v1/auto/keeprateofsid.ashx?_appid=app.iphone&seriesid=${seriesId}",
            beta = "http://pinguapi.lq.che168.com/v1/auto/keeprateofsid.ashx?_appid=app.iphone&seriesid=${seriesId}",
            online = "http://pinguapi.lq.che168.com/v1/auto/keeprateofsid.ashx?_appid=app.iphone&seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<UsedCarKeepRateResult>> getUsedCarKeepRate(int seriesId);


    /**
     * 主软车系页二手车推荐模块接口V2
     * !!!!!!!!!!注意！！！！！！！
     * 源接口在sort=1 且 pageindex>1 时 不跟据页码返回对应页数, 而是根据设备号请求此时返回第 n 页的数据: 例如:
     * pageindex=1   ==> 第一页固定返回第一页数据
     * pageindex=2  ==> 第1次请求, 返回第二页
     * pageindex=2  ==> 第2次请求, 返回第三页
     * pageindex=10 ==> 第3次请求, 返回第四页
     * pageindex=1 ==> 第4次请求, pageindex = 1 重新计数
     * pageindex=2 ==> 第5次请求, 返回第2页
     * pageindex=2  ==> 第2次请求, 返回第三页
     * pageindex=3 ==> 第3次请求, 返回第四页
     * ........
     * <a href="https://zhishi.autohome.com.cn/home/teamplace/file?targetId=xvDteaR2ga">wiki</a>
     * @param seriesId  车系ID
     * @param deviceId 设备号
     * @param cityId 城市ID
     * @param pageIndex 页码
     * @param pageSize 每页个数
     * @param price 价格
     * @param sort 排序  默认排序:0; 价格最低:2; 价格最高:1; 发布最车龄最短:6; 里程最少:5;
     * @param mileage 里程  0-10
     * @param age 车龄 0-10
     * @return 二手车推荐
     */
    @AutoGet(dev = "http://api2scautork2.lf.cupid.autohome.com.cn/v2/autohome/GetRecommendCars.ashx?_appid=autoapp&seriesid=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&deviceid=${deviceId}&sort=${sort}&cid=${cityId}&mileage=${mileage}&price=${price}&age=${age}",
            beta = "http://api2scautork2.lf.cupid.autohome.com.cn/v2/autohome/GetRecommendCars.ashx?_appid=autoapp&seriesid=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&deviceid=${deviceId}&sort=${sort}&cid=${cityId}&mileage=${mileage}&price=${price}&age=${age}",
            online = "http://api2scautork2.lf.corpautohome.com/v2/autohome/GetRecommendCars.ashx?_appid=autoapp&seriesid=${seriesId}&pageindex=${pageIndex}&pagesize=${pageSize}&deviceid=${deviceId}&sort=${sort}&cid=${cityId}&mileage=${mileage}&price=${price}&age=${age}")
    CompletableFuture<BaseModel<GetRecommendCarResult>> getRecommendCarsV2(int seriesId, int cityId, int pageIndex, int pageSize, int sort, String mileage, String price, String deviceId, String age);


    /**
     * 获取车系 各城市二手车价格数据
     * 接口维护人：何峰
     * 接口文档：https://zhishi.autohome.com.cn/home/teamplace/file?targetId=zkY8yX3128
     * @param seriesId
     * @return
     */
    @AutoGet(
            dev = "http://api2scautork.lf.corpautohome.com/auto/spec/getseriesyearcityprice?_appid=app&seriesid=${seriesId}",
            beta = "http://api2scautork.lf.corpautohome.com/auto/spec/getseriesyearcityprice?_appid=app&seriesid=${seriesId}",
            online = "http://api2scautork.lf.corpautohome.com/auto/spec/getseriesyearcityprice?_appid=app&seriesid=${seriesId}",
            timeout = 2000
    )
    CompletableFuture<BaseModel<SeriesYearCityPriceResult>> getSeriesYearCityPrice(int seriesId);

    /**
     * @param seriesId 车系ID
     * @param cityId  城市ID
     * @param sourceId 来源ID
     * @return
     */
    @AutoGet(
            dev = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=2sc&seriesid=${seriesId}&cid=${cityId}&sourceid=${sourceId}",
            beta = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=2sc&seriesid=${seriesId}&cid=${cityId}&sourceid=${sourceId}",
            online = "http://api2scautork2.lf.corpautohome.com/autohome/GetUsedCarsJumpInfo.ashx?_appid=2sc&seriesid=${seriesId}&cid=${cityId}&sourceid=${sourceId}"
    )
    CompletableFuture<BaseModel<GetSpecUsedCarsJumpInfoResult>> getSpecUsedCarsJumpInfoV2(int seriesId, int cityId, int sourceId);
}
