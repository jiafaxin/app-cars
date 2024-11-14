package com.autohome.app.cars.common.utils;

import org.apache.commons.lang3.StringUtils;

public class SchemeUtils {
    public SchemeUtils() {
    }

    public static String getScheme(String url, Boolean isWK) {
        if (StringUtils.isEmpty(url)) {
            return url;
        } else {
            String scheme = url;
            if (!url.startsWith("autohome://") && (url.startsWith("http://") || url.startsWith("https://"))) {
                scheme = String.format(isWK ? "autohome://insidebrowserwk?url=%s" : "autohome://insidebrowser?url=%s", UrlUtil.encode(UrlUtil.toHttps(url)));
            }

            return scheme;
        }
    }

    public static String getScheme(String url, Boolean isWK, Integer navigationBarsTyle) {
        if (StringUtils.isEmpty(url)) {
            return url;
        } else {
            String scheme = url;
            if (!url.startsWith("autohome://") && (url.startsWith("http://") || url.startsWith("https://"))) {
                scheme = String.format(isWK ? "autohome://insidebrowserwk?navigationbarstyle=%s&url=%s" : "autohome://insidebrowser?navigationbarstyle=%s&url=%s", navigationBarsTyle, UrlUtil.encode(UrlUtil.toHttps(url)));
            }

            return scheme;
        }
    }

    public static String getNewsShortVideoListScheme(int source, int bsId, String bsName, int id, String url, int loadmodel, int order, int topicId, String seriesId, int isContinue, int notShowRecord, int notShowPublish, int showMore, int notFeedback, Integer specId, int businessLine, String pvData) {
        StringBuffer schemeSb = new StringBuffer("autohome://article/shortvideolist");
        schemeSb.append("?source=").append(source).append("&bsid=").append(bsId).append("&bsname=").append(StringUtils.isNotEmpty(bsName) ? UrlUtil.encode(bsName) : "").append("&id=").append(id).append("&url=").append(StringUtils.isNotEmpty(url) ? UrlUtil.encode(url) : "").append("&loadmodel=").append(loadmodel).append("&order=").append(order).append("&topicid=").append(topicId).append("&seriesid=").append(seriesId).append("&specid=").append(specId).append("&iscontinue=").append(isContinue).append("&notshowrecord=").append(notShowRecord).append("&notshowpublish=").append(notShowPublish).append("&showmore=").append(showMore).append("&notfeedback=").append(notFeedback).append("&businessline=").append(businessLine).append("&pvdata=").append(StringUtils.isNotEmpty(pvData) ? pvData : "");
        return schemeSb.toString();
    }

    public static String getNewsShortVideoListScheme(int source, int bsId, String bsName, int id, String url, int loadmodel, int order, int topicId, String seriesId, int isContinue, int notShowRecord, int notShowPublish, int showMore, int notFeedback, Integer specId) {
        return getNewsShortVideoListScheme(source, bsId, bsName, id, url, loadmodel, order, topicId, seriesId, isContinue, notShowRecord, notShowPublish, showMore, notFeedback, specId, 0, "");
    }

    public static String getAH100Scheme(Object id) {
        return String.format("autohome://article/articledetail?newsid=%s&newstype=64&articlefromtype=0&shieldpublish=0&scrolltocomment=0", id);
    }

    public static String getArticleDetailScheme(Object newsId, int articleFrom, boolean isScrollToReply, String lastUpdateTime, String pvid) {
        return String.format("autohome://article/articledetail?newsid=%s&newstype=0&articlefromtype=%s&scrolltocomment=%s&lastupdatetime=%s&pvid=%s", newsId, articleFrom, isScrollToReply ? 1 : 0, lastUpdateTime, pvid);
    }

    public static String getNewsTopicScheme(Object newsId, int articleFrom, Boolean isShieldReply, boolean isScrollToReply, String lastUpdateTime, String pvid) {
        return String.format("autohome://article/articledetail?newsid=%s&newstype=26&articlefromtype=%s&shieldpublish=%s&scrolltocomment=%s&lastupdatetime=%s&pvid=%s", newsId, articleFrom, isShieldReply ? 1 : 0, isScrollToReply ? 1 : 0, lastUpdateTime, pvid);
    }

    public static String getTuWenScheme(Object objId, String pcis, String pvid) {
        return String.format("autohome://article/pictextdetail?newsid=%s&indexdetail=%s&pvid=%s", objId, UrlUtil.encode(pcis), pvid);
    }

    public static String getYC_CheDanScheme(Object objId, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/chedan?newsid=%s&fromsource=%s&pvid=%s", objId, fromSource, pvid);
    }

    public static String getLiveScheme(Object objId, Boolean isVerticalScreen, String pvid, Integer sourceId) {
        return getLiveScheme_Source(objId, isVerticalScreen, pvid, sourceId);
    }

    public static String getLiveScheme(Object objId, Boolean isVerticalScreen, String pvid) {
        return getLiveScheme_Source(objId, isVerticalScreen, pvid, 0);
    }

    public static String getLiveScheme(Object objId) {
        return getLiveScheme_Source(objId, false, "", 0);
    }

    public static String getLiveScheme(Object objId, String pvid) {
        return getLiveScheme_Source(objId, false, pvid, 0);
    }

    private static String getLiveScheme_Source(Object objId, Boolean isVerticalScreen, String pvid, Integer sourceId) {
        return isVerticalScreen ? String.format("autohome://liveshow/liveshowvertical?roomid=%s&pvid=%s&sourceid=%s", objId, pvid, sourceId) : String.format("autohome://liveshow/liveshowdetail?roomid=%s&pvid=%s&sourceid=%s", objId, pvid, sourceId);
    }

    public static String getTopicDetailScheme(Object objId, String title, int bbsId, String bbsType, Boolean isask, String pvid) {
        return String.format("autohome://club/topicdetail?pageid=%s&title=%s&bbsid=%s&bbstype=%s&isask=%s&pvid=%s", objId, UrlUtil.encode(title), bbsId, bbsType, isask ? 1 : 0, pvid);
    }

    public static String getVideoDetailScheme(Object objId, boolean isScrollToReply, String seriesIds, String pvid, String videoId, Integer from, Integer playCount) {
        return SafeParamUtil.toSafeInt(playCount) > 0 ? String.format("autohome://article/videodetail?newsid=%s&scrolltocomment=%s&mediatype=3&seriesids=%s&pvid=%s&vid=%s&from=%s&playcount=%s", objId, isScrollToReply ? 1 : 0, seriesIds, pvid, videoId, from, UrlUtil.encode(SafeParamUtil.convertToWan(playCount) + "播放")) : String.format("autohome://article/videodetail?newsid=%s&scrolltocomment=%s&mediatype=3&seriesids=%s&pvid=%s&vid=%s&from=%s", objId, isScrollToReply ? 1 : 0, seriesIds, pvid, videoId, from);
    }

    public static String getVideoDetailScheme(Object objId, boolean isScrollToReply, String seriesIds, String pvid, String videoId, Integer from) {
        return getVideoDetailScheme(objId, isScrollToReply, seriesIds, pvid, videoId, from, 0);
    }

    public static String getClubVideoDetailScheme(Object objId, boolean isScrollToReply, String seriesIds, String pvid, String videoId, Integer from, Integer playCount) {
        return getClubVideoDetailScheme(objId, isScrollToReply, 66, seriesIds, pvid, videoId, from, playCount);
    }

    public static String getClubVideoDetailScheme(Object objId, boolean isScrollToReply, Integer mediaType, String seriesIds, String pvid, String videoId, Integer from, Integer playCount) {
        return String.format("autohome://article/videodetail?newsid=%s&scrolltocomment=%s&mediatype=%s&seriesids=%s&pvid=%s&vid=%s&from=%s&playcount=%s", objId, isScrollToReply ? 1 : 0, mediaType, seriesIds, pvid, videoId, from, UrlUtil.encode(SafeParamUtil.convertToWan(playCount) + "播放"));
    }

    public static String getYC_ChangWenScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/long?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    public static String getYC_DuanWenScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/short?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    public static String getYC_VideoScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/video?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s&continueType=0&isquiet=0", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    public static String getYC_RadioScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/audio?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    public static String getConcern_TopicPageScheme(String topicName) {
        return String.format("autohome://attention/topicpager?name=%s", UrlUtil.encode(topicName));
    }

    public static String getNews_PicArticlePageScheme(Object id, String picStr) {
        return getNews_PicArticlePageScheme(id, picStr, "");
    }

    public static String getNews_PicArticlePageScheme(Object id, String picStr, String pvid) {
        return String.format("autohome://article/picarticledetail?newsid=%s&indexdetail=%s&pvid=%s", id, UrlUtil.encode(picStr), pvid);
    }

    public static String getKouBeiScheme(Integer kouBeiId, Integer seriesId, String seriesName, Integer specId, String specName, String userName, String reportDate, String pvid, Integer from) {
        return String.format("autohome://reputation/reputationdetail?koubeiid=%s&seriesid=%s&seriesname=%s&specid=%s&specname=%s&username=%s&reportdate=%s&pvid=%s&from=%s",
                kouBeiId, seriesId, StringUtils.isNotEmpty(seriesName) ? UrlUtil.encode(seriesName) : "",
                specId, StringUtils.isNotEmpty(specName) ? UrlUtil.encode(specName) : "",
                StringUtils.isNotEmpty(userName) ? UrlUtil.encode(userName) : "", reportDate, pvid, from);
    }

    public static String getKouBeiListScheme(Integer seriesId, String seriesName,
                                             Integer specId, String specName, Integer categoryId, Integer koubeiFromKey) {
        return String.format("autohome://reputation/reputationlist?brandid=&seriesid=%d&seriesname=%s&specid=%s&specname=%s&categoryid=%d&summarykey=&koubeifromkey=%d",
                seriesId, StringUtils.isNotEmpty(seriesName) ? UrlUtil.encode(seriesName) : "",
                null==specId?"":specId+"", StringUtils.isNotEmpty(specName) ? UrlUtil.encode(specName) : "", categoryId, koubeiFromKey);
    }

    public static String getSmallVideos_SeriesTheme(Integer seriesId) {
        return String.format("autohome://article/acsmvcarseriesinfodetail?seriesid=%s&pt=%s", seriesId, 1);
    }

    public static String getSmallVideos_MusicTheme(String musicId) {
        return String.format("autohome://article/shortmusicactivity?musicid=%s", musicId);
    }

    public static String getSmallVideos_TopicTheme(Integer topicId, Integer source) {
        return String.format("autohome://article/shortvideoactivity?topicid=%s&source=%s", topicId, source);
    }

    public static String getUserPageScheme_CheJiaHao(Integer userId, Integer selectTagId, Integer fromSource) {
        return String.format("autohome://articleplatform/userinfo?userid=%s&selecttagid=%s&fromsource=%s", userId, selectTagId, fromSource);
    }

    public static String getUserPageScheme_AutoUser(Integer userId, Integer authorId, Integer from, String userName) {
        return String.format("autohome://user/hiscenter?userid=%s&authorid=%s&from=%s&username=%s", userId, authorId, from, UrlUtil.encode(userName));
    }

    public static String getNewsFastnewsDetailScheme(Object id, int from) {
        return String.format("autohome://article/newsflash?id=%s&from=%s", id, from);
    }
}
