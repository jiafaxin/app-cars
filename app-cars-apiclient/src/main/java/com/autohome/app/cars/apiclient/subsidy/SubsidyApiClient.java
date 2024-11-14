package com.autohome.app.cars.apiclient.subsidy;

import com.autohome.app.cars.apiclient.subsidy.dtos.LocalPolicyResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author chengjincheng
 * @date 2024/9/25
 */
@AutoHttpClient
public interface SubsidyApiClient {

    /**
     * 本地政府补贴
     * http://doc.phonebus.corpautohome.com/project/1531/interface/api/79582
     * http://wiki.corpautohome.com/pages/viewpage.action?pageId=337942886
     */
    @AutoGet(
            dev = "http://baojia-benefit-api.mesh-mulan.corpautohome.com/localpolicy/getLocalPolicy?cityId=${cityId}",
            beta = "http://baojia.benefit.api.corpautohome.com/localpolicy/getLocalPolicy?cityId=${cityId}",
            online = "http://baojia.benefit.api.corpautohome.com/localpolicy/getLocalPolicy?cityId=${cityId}"
    )
    CompletableFuture<BaseModel<List<LocalPolicyResult>>> getCityLocalPolicy(int cityId);
}
