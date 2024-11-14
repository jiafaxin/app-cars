package com.autohome.app.cars.apiclient.rank;

import com.autohome.app.cars.apiclient.rank.dtos.TestedDataRankListDto;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhangchengtao
 * @date 2024/9/24 16:54
 */
@AutoHttpClient
public interface RealTestRankClient {

    /**
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=wVHbzSun0S
     * @param standardId
     * @param rankId
     * @param fuelType
     * @return
     */
    @AutoGet(
            dev = "http://carservice.autohome.com.cn/teststandard/getTestedDataRankListByConditions?standardId=${standardId}&rankId=${rankId}&fueltype=${fuelType}",
            beta = "http://carservice.autohome.com.cn/teststandard/getTestedDataRankListByConditions?standardId=${standardId}&rankId=${rankId}&fueltype=${fuelType}",
            online = "http://carservice.autohome.com.cn/teststandard/getTestedDataRankListByConditions?standardId=${standardId}&rankId=${rankId}&fueltype=${fuelType}",
            timeout = 750
    )
    CompletableFuture<BaseModel<TestedDataRankListDto>> getRealTestData(int standardId, int rankId, String fuelType);
}
