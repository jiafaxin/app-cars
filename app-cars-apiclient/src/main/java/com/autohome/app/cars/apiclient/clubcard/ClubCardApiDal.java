package com.autohome.app.cars.apiclient.clubcard;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmExtParamModel;
import autohome.rpc.car.app_cars.v1.carext.SeriesTabRequest;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.autohome.app.cars.apiclient.clubcard.dtos.KeyValueModel;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.HttpClientUtil;
import com.autohome.app.cars.common.utils.SafeParamUtil;
import com.autohome.app.cars.common.utils.SecretKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ClubCardApiDal {
    private static final String DATA_RELATIVE_RECOMMEND_API = "data.in.corpautohome.com/oneapi/v2";


    public CompletableFuture<SBI_RcmDataResult> getRecommendNews(SeriesTabRequest request) {

        String url = DATA_RELATIVE_RECOMMEND_API;

        StringBuffer sb = new StringBuffer();
        SBI_RcmExtParamModel paramModel = new SBI_RcmExtParamModel();
        paramModel.setSeriesids(String.valueOf(request.getSeriesid()));
        paramModel.setFacilitytype(request.getPm());

        paramModel.setAppexp("100102-B1");
        sb.append("http://").append(url)
                .append("?pid=").append("90100193")
                .append("&uuid=").append(request.getDeviceid())
                .append("&version=1")
                .append("&devicetype=").append(request.getPm() == 1 ? "ios" : "android")
                .append("&source=app")
                .append("&operation=").append(0)
                .append("&appversion=").append(request.getPluginversion())
                .append("&requestid=").append(request.getDeviceid() + "," + System.currentTimeMillis())
                .append("&number=").append(3)
                .append("&perscont=").append(0)
                .append("&ext=").append(urlEncode(JSONObject.toJSONString(paramModel)));

        Map<String,String> headerMap = SecretKeyUtils.getSecretKey("oneapi_v2");

        CompletableFuture<SBI_RcmDataResult> smodel = HttpClientUtil.get(sb.toString(), new com.fasterxml.jackson.core.type.TypeReference<SBI_RcmDataResult>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        }, headerMap, 300, "UTF-8").exceptionally(ex -> {
           log.error("调用推荐接口超时",ex);
           return null;
        });
        return smodel;
    }

    public String urlEncode(String part) {
        String result = "";
        try {
            result =  URLEncoder.encode(part, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            log.error("url解码错误:",var2);
        }
        return result;
    }
}
