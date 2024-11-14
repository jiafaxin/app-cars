package com.autohome.app.cars.apiclient.club;

import com.autohome.app.cars.apiclient.club.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.annotation.AutoGet;
import com.autohome.app.cars.common.httpclient.annotation.AutoHttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AutoHttpClient
public interface ClubApiClient {
    @AutoGet(
            dev = "http://clubapi.in.autohome.com.cn/japi/club/getClubLatelyScoreForCar?_appid=app&seriesId=${seriesId}",
            beta = "http://clubapi.in.autohome.com.cn/japi/club/getClubLatelyScoreForCar?_appid=app&seriesId=${seriesId}",
            online = "http://clubapi.in.autohome.com.cn/japi/club/getClubLatelyScoreForCar?_appid=app&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<ClubLatelyScoreForCarResult>> getClubLatelyScoreForCar(int seriesId);

    @AutoGet(
            dev = "http://clubapi.in.autohome.com.cn/japi/qa/getSeriesQaInfoForCar?_appid=app&seriesId=${seriesId}",
            beta = "http://clubapi.in.autohome.com.cn/japi/qa/getSeriesQaInfoForCar?_appid=app&seriesId=${seriesId}",
            online = "http://clubapi.in.autohome.com.cn/japi/qa/getSeriesQaInfoForCar?_appid=app&seriesId=${seriesId}"
    )
    CompletableFuture<BaseModel<ClubLatelyScoreForCarResult>> getSeriesQaInfoForCar(int seriesId);

    @AutoGet(
            dev = "http://clubapi.in.autohome.com.cn/japi/qa/bbs/topics/_query?_appid=app&bbsid=${seriesId}&page=1&size=1&orderby=topicid-",
            beta = "http://clubapi.in.autohome.com.cn/japi/qa/bbs/topics/_query?_appid=app&bbsid=${seriesId}&page=1&size=1&orderby=topicid-",
            online = "http://clubapi.in.autohome.com.cn/japi/qa/bbs/topics/_query?_appid=app&bbsid=${seriesId}&page=1&size=1&orderby=topicid-"
    )
    CompletableFuture<BaseModel<ClubWendaResult>> getSeriesClubWendaResult(int seriesId);

    @AutoGet(
            dev = "http://clubapi.in.autohome.com.cn/japi/club/getInterestGroupList?_appid=app&bbsId=${seriesId}",
            beta = "http://clubapi.in.autohome.com.cn/japi/club/getInterestGroupList?_appid=app&bbsId=${seriesId}",
            online = "http://clubapi.in.autohome.com.cn/japi/club/getInterestGroupList?_appid=app&bbsId=${seriesId}"
    )
    CompletableFuture<BaseModel<ClubGroupResult>> getClubGroups(int seriesId);

    /**
     * 帖子列表(内容分类)
     * http://wiki.corpautohome.com/pages/viewpage.action?pageId=92209426
     */
    @AutoGet(
            dev = "http://clubapi.in.autohome.com.cn/api/topic/GetTopicListByContentType?pagesize=${pagesize}&pageindex=${pageindex}&tagInfoId=${tagInfoId}&bbsid=${seriesId}&_appid=app&fields=bbs,bbsid,bbsname,topicid,title,post_memberid,post_membername,lastreplydate,lasteditdate,postdate,isrefine,isvideo,videoid,replycount,allreplycount,viewcount,ispic,ispoll,piccount,isjingxuan,jximgs,issolve,imgs,summary,videoinfo,url,liveid,livecover,isdelete",
            beta = "http://clubapi.in.autohome.com.cn/api/topic/GetTopicListByContentType?pagesize=${pagesize}&pageindex=${pageindex}&tagInfoId=${tagInfoId}&bbsid=${seriesId}&_appid=app&fields=bbs,bbsid,bbsname,topicid,title,post_memberid,post_membername,lastreplydate,lasteditdate,postdate,isrefine,isvideo,videoid,replycount,allreplycount,viewcount,ispic,ispoll,piccount,isjingxuan,jximgs,issolve,imgs,summary,videoinfo,url,liveid,livecover,isdelete",
            online = "http://clubapi.in.autohome.com.cn/api/topic/GetTopicListByContentType?pagesize=${pagesize}&pageindex=${pageindex}&tagInfoId=${tagInfoId}&bbsid=${seriesId}&_appid=app&fields=bbs,bbsid,bbsname,topicid,title,post_memberid,post_membername,lastreplydate,lasteditdate,postdate,isrefine,isvideo,videoid,replycount,allreplycount,viewcount,ispic,ispoll,piccount,isjingxuan,jximgs,issolve,imgs,summary,videoinfo,url,liveid,livecover,isdelete"
    )
    CompletableFuture<BaseModel<TopicContentResult>> GetTopicListByContentType(int seriesId, int tagInfoId,int pagesize,int pageindex);


    /**
     * 根据车型获取帖子列表
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=xysm9XvPlI
     * @param specId 车型ID
     * @return 帖子列表
     */
    @AutoGet(
            dev = "http://clubapi-in.thallo.corpautohome.com/japi/topic/getTopicListByCarSpec?_appid=app&specId=${specId}",
            beta = "http://clubapi.in.autohome.com.cn/japi/topic/getTopicListByCarSpec?_appid=app&specId=${specId}",
            online = "http://clubapi.in.autohome.com.cn/japi/topic/getTopicListByCarSpec?_appid=app&specId=${specId}",
            timeout = 1000
    )
    CompletableFuture<BaseModel<List<SpecClubTopicResult>>> getTopicListByCarSpec(int specId);


    /**
     * 查询多条论坛信息
     * https://zhishi.autohome.com.cn/home/teamplace/file?targetId=to1aL43TMm
     * @param seriesIds 车系ID列表
     * @return SeriesBbsResult
     */
    @AutoGet(dev = "http://clubapi.in.autohome.com.cn/japi/club/getbbsbyids?_appid=app&bbsids=${seriesIds}",
            beta = "http://clubapi.in.autohome.com.cn/japi/club/getbbsbyids?_appid=app&bbsids=${seriesIds}",
            online = "http://clubapi.in.autohome.com.cn/japi/club/getbbsbyids?_appid=app&bbsids=${seriesIds}"
    )
    CompletableFuture<BaseModel<List<SeriesBbsResult>>> getBbsBySeriesIds(int seriesIds);


    /**
     * 车系页糖豆-帖子列表
     * http://la.corpautohome.com/doc/detail?laFlowId=1827
     * @param seriesId
     * @param datatype
     * @param profile
     * @param sort
     * @param size
     * @return
     */
    @AutoGet(dev = "http://la.corpautohome.com/club/club_topics_by_profile_for_series_app?_appid=app&seriesid=${seriesId}&datatype=${datatype}&profile=${profile}&sort=${sort}&size=${size}",
            beta = "http://la.corpautohome.com/club/club_topics_by_profile_for_series_app?_appid=app&seriesid=${seriesId}&datatype=${datatype}&profile=${profile}&sort=${sort}&size=${size}",
            online = "http://la.corpautohome.com/club/club_topics_by_profile_for_series_app?_appid=app&seriesid=${seriesId}&datatype=${datatype}&profile=${profile}&sort=${sort}&size=${size}",
            timeout = 3000
    )
    CompletableFuture<BaseModel<SeriesClubPostResult>> getSeriesSugarPostList(long seriesId,long datatype,String profile,long sort,long size);
}
