package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.DiscountRankResult;
import com.autohome.app.cars.apiclient.rank.dtos.HedgeRankResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * Created by dx on 2024/6/3
 * 降价榜、保值榜源接口
 */
@AutoHttpClient
public interface LeaderboardClient {
    /**
     * 获取降价数据
     * 源接口wiki：https://zhishi.autohome.com.cn/home/teamplace/file?targetId=j18KRTc9uC
     *
     * @param pageindex 当前页码
     * @param pagesize  每页条数
     * @param cityid    城市id
     * @return
     */
    @AutoGet(
            dev = "http://advert.leaderboard.corpautohome.com/landing/page/scene/unite?pageindex=${pageindex}&pagesize=${pagesize}&pm=1&typeid=3&model=1&cityid=${cityid}&noCache=noCache",
            beta = "http://advert.leaderboard.corpautohome.com/landing/page/scene/unite?pageindex=${pageindex}&pagesize=${pagesize}&pm=1&typeid=3&model=1&cityid=${cityid}&noCache=noCache",
            online = "http://advert.leaderboard.corpautohome.com/landing/page/scene/unite?pageindex=${pageindex}&pagesize=${pagesize}&pm=1&typeid=3&model=1&cityid=${cityid}&noCache=noCache"
    )
    CompletableFuture<DiscountRankResult> getDiscountRankList(int pageindex, int pagesize, int cityid);


    /**
     * 获取保值榜数据
     * 源接口wiki：https://zhishi.autohome.com.cn/home/teamplace/file?targetId=j18KRTc9uC
     *
     * @param pageindex 当前页码
     * @param pagesize  每页条数
     * @param levelid   级别 新能源201908
     * @return
     */
    @AutoGet(
            dev = "http://test.leaderboard.corpautohome.com/landing/page/scene/unite?pageindex=${pageindex}&pagesize=${pagesize}&levelid=${levelid}&pm=1&typeid=6&pluginversion=11.63.0",
            beta = "http://advert.leaderboard.corpautohome.com/landing/page/scene/unite?pageindex=${pageindex}&pagesize=${pagesize}&levelid=${levelid}&pm=1&typeid=6&pluginversion=11.63.0",
            online = "http://advert.leaderboard.corpautohome.com/landing/page/scene/unite?pageindex=${pageindex}&pagesize=${pagesize}&levelid=${levelid}&pm=1&typeid=6&pluginversion=11.63.0"
    )
    CompletableFuture<BaseModel<HedgeRankResult>> getHedgeRankList(int pageindex, int pagesize, String levelid);
}
