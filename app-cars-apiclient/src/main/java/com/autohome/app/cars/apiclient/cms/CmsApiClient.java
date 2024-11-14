package com.autohome.app.cars.apiclient.cms;

import com.autohome.app.cars.apiclient.cms.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface CmsApiClient {

    @AutoGet(
            dev = "https://cms.api.autohome.com.cn/Wcf/AutoShowSerivce.svc/GetAutoManageRecommendInfos?_appId=app&bizType=${bizType}&bizRegion=${bizRegion}",
            beta = "https://cms.api.autohome.com.cn/Wcf/AutoShowSerivce.svc/GetAutoManageRecommendInfos?_appId=app&bizType=${bizType}&bizRegion=${bizRegion}",
            online = "https://cms.api.autohome.com.cn/Wcf/AutoShowSerivce.svc/GetAutoManageRecommendInfos?_appId=app&bizType=${bizType}&bizRegion=${bizRegion}"
    )
    CompletableFuture<BaseModel<List<AutoShowConfigResult>>> getAutoShowConfig(int bizType, int bizRegion);

    @AutoGet(
            dev = "https://cms.api.autohome.com.cn/CmsJava/Wcf/AutoShowSerivce.svc/GetAutoShowSeries?_appid=car&auto_show_id=${autoShowId}",
            beta = "https://cms.api.autohome.com.cn/CmsJava/Wcf/AutoShowSerivce.svc/GetAutoShowSeries?_appid=car&auto_show_id=${autoShowId}",
            online = "https://cms.api.autohome.com.cn/CmsJava/Wcf/AutoShowSerivce.svc/GetAutoShowSeries?_appid=car&auto_show_id=${autoShowId}",
            timeout = 5000
    )
    CompletableFuture<BaseModel<AutoShowConfigNewResult>> getAutoShowConfig(int autoShowId);

    @AutoGet(
            dev = "https://cms.api.autohome.com.cn/CmsJava/SubjectData/GetSubjectDataMixedList?_appid=app&auto_show_id=${autoShowId}&series_id=${seriesId}&page_size=10",
            beta = "https://cms.api.autohome.com.cn/CmsJava/SubjectData/GetSubjectDataMixedList?_appid=app&auto_show_id=${autoShowId}&series_id=${seriesId}&page_size=10",
            online = "https://cms.api.autohome.com.cn/CmsJava/SubjectData/GetSubjectDataMixedList?_appid=app&auto_show_id=${autoShowId}&series_id=${seriesId}&page_size=4",
            timeout = 5000
    )
    CompletableFuture<BaseModel<AutoShowNewsResult>> getAutoShowNews(int autoShowId, int seriesId);


    @AutoGet(
            dev = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetAHEvaluateItemsWithPointView?_appid=app&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<CmsTestEvalItemsResult>> getAHEvaluateItemsWithPointView(int seriesId);

    //http://la.corpautohome.com/doc/detail?laFlowId=828
    @AutoGet(
            dev = "http://la.corpautohome.com/cms/cms_newbrand_seriesid?_appid=app&series_id=${seriesId}",
            beta = "http://la.corpautohome.com/cms/cms_newbrand_seriesid?_appid=app&series_id=${seriesId}",
            online = "http://la.corpautohome.com/cms/cms_newbrand_seriesid?_appid=app&series_id=${seriesId}"
    )
    CompletableFuture<BaseModel<List<CmsNewCarResult>>> getCmsNewCar(int seriesId);


    //http://la.corpautohome.com/doc/detail?laFlowId=828
    @AutoGet(
            dev = "http://cms.api.autohome.com.cn/Wcf/ArticleSerivce.svc/GetNewsContentRecommendBySerisId?_appid=app&seriesId=${seriesId}",
            beta = "http://cms.api.autohome.com.cn/Wcf/ArticleSerivce.svc/GetNewsContentRecommendBySerisId?_appid=app&seriesId=${seriesId}",
            online = "http://cms.api.autohome.com.cn/Wcf/ArticleSerivce.svc/GetNewsContentRecommendBySerisId?_appid=app&seriesId=${seriesId}",
            timeout = 3000
    )
    CompletableFuture<BaseModel<STopSeriesNewsResult>> getTopSeriesNews(int seriesId);

    @AutoGet(
            dev = "https://cms.api.autohome.com.cn/CmsJava/SubjectData/GetPageList?_appid=car&subject_id=${subjectId}&tag_id=${seriesId}&page_size=1",
            beta = "https://cms.api.autohome.com.cn/CmsJava/SubjectData/GetPageList?_appid=car&subject_id=${subjectId}&tag_id=${seriesId}&page_size=1",
            online = "https://cms.api.autohome.com.cn/CmsJava/SubjectData/GetPageList?_appid=car&subject_id=${subjectId}&tag_id=${seriesId}&page_size=1"
    )
    CompletableFuture<BaseModel<CmsArticleDataResult>> getCmsArticleData(int seriesId, int subjectId);

    /**
     * 获取标签下的文章内容:100002试驾内容\100003 就是评测
     * https://at.corpautohome.com/document?shareId=e52feb48-e437-4f6f-8245-ef191cda7386#/setting
     */
    @AutoGet(
            dev = "http://cms.api.autohome.com.cn/CmsJava/SubjectData/GetAscPageList?_appid=cms&subject_id=${subjectId}&tag_id=${seriesId}&page_size=1",
            beta = "http://cms.api.autohome.com.cn/CmsJava/SubjectData/GetAscPageList?_appid=cms&subject_id=${subjectId}&tag_id=${seriesId}&page_size=1",
            online = "http://cms.api.autohome.com.cn/CmsJava/SubjectData/GetAscPageList?_appid=cms&subject_id=${subjectId}&tag_id=${seriesId}&page_size=1"
    )
    CompletableFuture<BaseModel<CmsArticleDataResult>> GetAscPageList(int seriesId, int subjectId);

    //源接口对接人：沈巨明
    @AutoGet(
            dev = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetSpecsEvaluateItems?_appid=app&specIds=${specIds}&withModule=0",
            beta = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetSpecsEvaluateItems?_appid=app&specIds=${specIds}&withModule=0",
            online = "http://cms.api.autohome.com.cn/Wcf/ModuleArticleService.svc/GetSpecsEvaluateItems?_appid=app&specIds=${specIds}&withModule=0",
            timeout = 180
    )
    CompletableFuture<BaseModel<List<SpecEvaluateItemResult>>> getSpecEvaluateItems(String specIds);

    /**
     * https://doc.autohome.com.cn/docapi/page/share/share_x4YlU00E3E
     * 上市车管理v2.0查询接口
     */
    @AutoGet(
            dev = "http://carref-manager.corpautohome.com/carref-api/public/marketcar/list?_appid=app&marketType=${marketType}&startTime=${startTime}&endTime=${endTime}",
            beta = "http://carref-manager.corpautohome.com/carref-api/public/marketcar/list?_appid=app&marketType=${marketType}&startTime=${startTime}&endTime=${endTime}",
            online = "http://carref-manager.corpautohome.com/carref-api/public/marketcar/list?_appid=app&marketType=${marketType}&startTime=${startTime}&endTime=${endTime}",
            timeout = 3000
    )
    CompletableFuture<BaseModel<MarketCarResult>> getMarketCarList(int marketType, String startTime, String endTime);

}
