package com.autohome.app.cars.apiclient.che168;

import com.autohome.app.cars.apiclient.che168.dtos.CitySeriesCarsWithPic;
import com.autohome.app.cars.apiclient.che168.dtos.UsedCarDetailResult;
import com.autohome.app.cars.apiclient.che168.dtos.UsedCarSearchResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoCache;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhangchengtao
 * @date 2024/9/30 19:15
 */
@AutoHttpClient
public interface ApiAutoAppShClient {

    /**
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=zNGA3AaAkK
     *
     * @param seriesId   车系id
     * @param cityId     城市id
     * @param provinceId 省份id
     * @return UsedCarDetailResult
     */
    @AutoGet(dev = "http://apiautoappsh.che168.com/gc/seriesdetail?_appid=${appId}&cid=${cityId}&pid=${provinceId}&seriesid=${seriesId}&_sign=${sign}",
            beta = "http://apiautoappsh.che168.com/gc/seriesdetail?_appid=${appId}&cid=${cityId}&pid=${provinceId}&seriesid=${seriesId}&_sign=${sign}",
            online = "http://apiautoappsh.che168.com/gc/seriesdetail?_appid=${appId}&cid=${cityId}&pid=${provinceId}&seriesid=${seriesId}&_sign=${sign}",
            timeout = 750)
    CompletableFuture<BaseModel<UsedCarDetailResult>> seriesDetail(String appId, int seriesId, int cityId, int provinceId, String sign);


    /**
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=zN4HlfENsG
     * @param cityId
     * @param provinceId
     * @param brandId
     * @param seriesId
     * @param seriesYearId
     * @param price
     * @param ageRange
     * @param mileage
     * @param deviceId
     * @param sort
     * @return
     */
    @AutoGet(dev = "http://apiautoappsh.che168.com/gc/search?_appid=${appId}&pageindex=${pageIndex}&pagesize=${pageSize}&cid=${cityId}&pid=${provinceId}&brandid=${brandId}&seriesid=${seriesId}&seriesyearid=${seriesYearId}&price=${price}&agerange=${ageRange}&mileage=${mileage}&deviceid=${deviceId}&sort=${sort}&_sign=${sign}",
            beta = "http://apiautoappsh.che168.com/gc/search?_appid=${appId}&pageindex=${pageIndex}&pagesize=${pageSize}&cid=${cityId}&pid=${provinceId}&brandid=${brandId}&seriesid=${seriesId}&seriesyearid=${seriesYearId}&price=${price}&agerange=${ageRange}&mileage=${mileage}&deviceid=${deviceId}&sort=${sort}&_sign=${sign}",
            online = "http://apiautoappsh.che168.com/gc/search?_appid=${appId}&pageindex=${pageIndex}&pagesize=${pageSize}&cid=${cityId}&pid=${provinceId}&brandid=${brandId}&seriesid=${seriesId}&seriesyearid=${seriesYearId}&price=${price}&agerange=${ageRange}&mileage=${mileage}&deviceid=${deviceId}&sort=${sort}&_sign=${sign}",
            timeout = 750)
    CompletableFuture<BaseModel<UsedCarSearchResult>> search(String appId, int pageIndex, int pageSize, int cityId, int provinceId, int brandId, int seriesId, String seriesYearId, String price, String ageRange, String mileage, String deviceId, int sort, String sign);


    /**
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=zN4OHFhj3Q
     * @param pageIndex
     * @param pageSize
     * @param cityId
     * @param provinceId
     * @param brandId
     * @param seriesId
     * @param seriesYearId
     * @param deviceId
     * @param price
     * @param ageRange
     * @param mileage
     * @return
     */
    @AutoGet(dev = "http://apiautoappsh.che168.com/gc/getperiphery?_appid=${appId}&pageindex=${pageIndex}&pagesize=${pageSize}&cid=${cityId}&pid=${provinceId}&brandid=${brandId}&seriesid=${seriesId}&seriesyearid=${seriesYearId}&deviceid=${deviceId}&price=${price}&agerange=${ageRange}&mileage=${mileage}&_sign=${sign}",
            beta = "http://apiautoappsh.che168.com/gc/getperiphery?_appid=${appId}&pageindex=${pageIndex}&pagesize=${pageSize}&cid=${cityId}&pid=${provinceId}&brandid=${brandId}&seriesid=${seriesId}&seriesyearid=${seriesYearId}&deviceid=${deviceId}&price=${price}&agerange=${ageRange}&mileage=${mileage}&_sign=${sign}",
            online = "http://apiautoappsh.che168.com/gc/getperiphery?_appid=${appId}&pageindex=${pageIndex}&pagesize=${pageSize}&cid=${cityId}&pid=${provinceId}&brandid=${brandId}&seriesid=${seriesId}&seriesyearid=${seriesYearId}&deviceid=${deviceId}&price=${price}&agerange=${ageRange}&mileage=${mileage}&_sign=${sign}",
            timeout = 750)
    CompletableFuture<BaseModel<UsedCarSearchResult>> getPeriphery(String appId, int pageIndex, int pageSize, int cityId, int provinceId, int brandId, int seriesId, String seriesYearId, String deviceId, String price, String ageRange, String mileage, String sign);


    /**
     * <a href="https://zhishi.autohome.com.cn/home/teamplace/file?targetId=zz0vlm6l8a">wiki</a>
     * @param cityId 城市ID
     * @param seriesId 车系ID
     * @param specId 车型ID
     * @param pageIndex 页码
     * @param pageSize 每页数量
     * @return 二手车带图列表
     */
    @AutoGet(dev = "http://api2scautork.lf.corpautohome.com/auto/getcityseriescars?_appid=app&cityid=${cityId}&seriesid=${seriesId}&specid=${specId}&pageindex=${pageIndex}&pagesize=${pageSize}",
            beta = "http://api2scautork.lf.corpautohome.com/auto/getcityseriescars?_appid=app&cityid=${cityId}&seriesid=${seriesId}&specid=${specId}&pageindex=${pageIndex}&pagesize=${pageSize}",
            online = "http://api2scautork.lf.corpautohome.com/auto/getcityseriescars?_appid=app&cityid=${cityId}&seriesid=${seriesId}&specid=${specId}&pageindex=${pageIndex}&pagesize=${pageSize}",
            timeout = 200)
    @AutoCache(liveTime = 1800)
    CompletableFuture<CitySeriesCarsWithPic> getCitySeriesCarsWithPic(int cityId, int seriesId, int specId, int pageIndex, int pageSize);
}
