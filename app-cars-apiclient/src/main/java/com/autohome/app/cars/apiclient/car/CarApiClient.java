package com.autohome.app.cars.apiclient.car;

import com.autohome.app.cars.apiclient.car.dtos.MonthRankDto;
import com.autohome.app.cars.apiclient.car.dtos.WeekRankHistoryDto;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface CarApiClient {

    @AutoGet(
            dev = "http://`car.api.`autohome.com.cn/v2/CarPrice/Brand_GetBrandLogo.ashx?_appid=app&brandid=${brandId}"
    )
    CompletableFuture<BaseModel> brandGetBrandLogo(int brandId, @CookieParameter String cookieTest, @HeaderParameter String headerTest);

    //todo  周榜的数据现在在摩托车库里，成涛在11.62.5版本会迁移过来，这个版本暂时先走接口
    @AutoGet(
            dev = "http://carservice.autohome.com.cn/salecount/weekRankHistory?seriesid=${seriesId}",
            beta = "http://carservice.autohome.com.cn/salecount/weekRankHistory?seriesid=${seriesId}",
            online = "http://carservice.autohome.com.cn/salecount/weekRankHistory?seriesid=${seriesId}"
    )
    CompletableFuture<BaseModel<WeekRankHistoryDto>> getSeriesWeekRankHistory(int seriesId);

    @AutoGet(
            dev = "http://carservice.autohome.com.cn/salecount/weekRank?date=${date}&levels=&manutypes=&isNewenergy=&minPrice=0&maxPrice=90000000&brandid=",
            beta = "http://carservice.autohome.com.cn/salecount/weekRank?date=${date}&levels=&manutypes=&isNewenergy=&minPrice=0&maxPrice=90000000&brandid=",
            online = "http://carservice.autohome.com.cn/salecount/weekRank?date=${date}&levels=&manutypes=&isNewenergy=&minPrice=0&maxPrice=90000000&brandid="
    )
    @AutoCache(liveTime = 60 * 30, effectiveTime = 60 * 30)
    CompletableFuture<BaseModel<List<WeekRankHistoryDto.DataDTO>>> getSeriesWeekRank(String date);
    @AutoGet(
            dev = "http://carservice.autohome.com.cn/salecount/carseriesCLHSaleMixRank?maxPrice=9000000&minPrice=0&beginMonth=${date}&endMonth=${date}",
            beta = "http://carservice.autohome.com.cn/salecount/carseriesCLHSaleMixRank?maxPrice=9000000&minPrice=0&beginMonth=${date}&endMonth=${date}",
            online = "http://carservice.autohome.com.cn/salecount/carseriesCLHSaleMixRank?maxPrice=9000000&minPrice=0&beginMonth=${date}&endMonth=${date}"
    )
    @AutoCache(liveTime = 60 * 30, effectiveTime = 60 * 30)
    CompletableFuture<BaseModel<List<MonthRankDto>>> getSeriesMonthRank(String date);
}
