package com.autohome.app.cars.apiclient.video;

import com.autohome.app.cars.apiclient.video.dtos.MainDataInfoResult;
import com.autohome.app.cars.apiclient.video.dtos.XuanGouVideoResult;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface XuanGouVideoApiClient {

    @AutoGet(
            dev = "http://la.corpautohome.com/video/cms_auto_subject_base_data_list?_appid=app&subject_id=10090&module_id=${module_id}",
            beta = "http://la.corpautohome.com/video/cms_auto_subject_base_data_list?_appid=app&subject_id=10090&module_id=${module_id}",
            online = "http://la.corpautohome.com/video/cms_auto_subject_base_data_list?_appid=app&subject_id=10090&module_id=${module_id}",
            timeout = 200
    )
    CompletableFuture<XuanGouVideoResult> getXuanGouVideoResult(String module_id);

    @AutoGet(
            dev = "http://maindata.api.autohome.com.cn/data/more/maindata_get_multiple_infos?_appid=maindata&mainDataIds=${mainDataIds}&fields=pv,like_count,reply_count,author_name&search_after=",
            beta = "http://maindata.api.autohome.com.cn/data/more/maindata_get_multiple_infos?_appid=maindata&mainDataIds=${mainDataIds}&fields=pv,like_count,reply_count,author_name&search_after=",
            online = "http://maindata.api.autohome.com.cn/data/more/maindata_get_multiple_infos?_appid=maindata&mainDataIds=${mainDataIds}&fields=pv,like_count,reply_count,author_name&search_after=",
            timeout = 200
    )
    CompletableFuture<MainDataInfoResult> getMainDataInfoResult(String mainDataIds);

}
