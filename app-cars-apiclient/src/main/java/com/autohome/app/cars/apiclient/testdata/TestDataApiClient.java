package com.autohome.app.cars.apiclient.testdata;

import com.autohome.app.cars.apiclient.testdata.dtos.TestDataResult;
import com.autohome.app.cars.apiclient.testdata.dtos.TestStandardResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoCache;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface TestDataApiClient {

    @AutoGet(
            dev = "http://carservice.autohome.com.cn/teststandard/getTestedDataIdBySpecIds?speclist=${speclist}",
            beta = "http://carservice.autohome.com.cn/teststandard/getTestedDataIdBySpecIds?speclist=${speclist}",
            online = "http://carservice.autohome.com.cn/teststandard/getTestedDataIdBySpecIds?speclist=${speclist}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<List<TestDataResult>>> getTestedDataIdBySpecIds(String speclist);

    @AutoGet(
            dev = "http://carservice.autohome.com.cn/teststandard/getTestStandardItemList?_appid=app&dataId=${dataId}&test=${test}",
            beta = "http://carservice.autohome.com.cn/teststandard/getTestStandardItemList?_appid=app&dataId=${dataId}&test=${test}",
            online = "http://carservice.autohome.com.cn/teststandard/getTestStandardItemList?_appid=app&dataId=${dataId}&test=${test}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<TestStandardResult>> getTestStandardItemList(int dataId, int test);

}
