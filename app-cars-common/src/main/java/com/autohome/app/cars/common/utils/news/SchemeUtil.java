package com.autohome.app.cars.common.utils.news;

import com.autohome.app.cars.common.utils.StringUtils;

public class SchemeUtil {

    /**
     * 产品库-车型图片列表页
     */
    private static final String CAR_SPEC_PICTURE_SCHEME = "autohome://car/specpicture";

    /**
     * 产品库-车系综述页
     */
    private static final String CAR_SERIES_MAIN_SCHEME = "autohome://car/seriesmain?seriesid=%s&fromtype=%d";

    /**
     * 产品库-资讯/车展图片读图页协议
     */
    private static final String CAR_PICTURE_PAGE_SCHEME = "autohome://car/newpicturepage?picssid=%s&picspid=%s&picimgid=%s&sourcetype=%s&autoshowid=%s";

    /**
     * 产品库-车系图片列表页协议
     */
    private static final String CAR_SERIES_PICTURE_SCHEME = "autohome://car/seriespicture?seriesid=%s&seriesname=%s&categoryid=%s&orgin=%s";

    /**
     * 产品库-车系详解
     */
    private static final String CAR_SERIES_ANNOTATION_SCHEME = "autohome://car/seriesannotation?seriesid=%d&seriesname=%s&fromtype=%d&id=%d";

    /**
     * 产品库-车系列表
     */
    private static final String CAR_SERIES_BRAND_SCHEME = "autohome://car/seriesbrand?brandid=%d&brandname=%s";

    /**
     * 产品库-车系配置协议
     */
    private static final String CAR_COMPARE_PARAMCONTRAST = "autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s";

    /**
     * 资讯-原创编辑主页
     */
    private static final String NEWS_AUTHOR_DETAIL_SCHEME = "autohome://article/authordetail?uid=%d&authoruserid=%d&fromtype=%d";

    /**
     * 跳转标签列表页原生加壳
     */
    private static final String NEWS_LABELS_DETAIL_SCHEME = "autohome://article/labelsdetail?wordids=%s&keyword=%s&bgurl=%s";


    /**
     * 资讯-快讯详情页协议
     */
    private static final String NEWS_FASTNEWS_DETAIL_SCHEME = "autohome://article/newsflash?id=%s&from=%s";

    /**
     * 资讯-原创视频栏目
     */
    private static final String NEWS_ORIGINAL_COLUMN_SCHEME = "autohome://article/originalcolumn?id=%d";

    /**
     * 资讯-标签视频列表页
     */
    private static final String NEWS_TAGVIDEO_LIST_SCHEME = "autohome://article/immersivepagelist?fromtype=%d&tagid=%s";

    /**
     * 资讯-视频最终页
     */
    private static final String NEWS_VIDEO_DETAIL_SCHEME = "autohome://article/videodetail";

    /**
     * 资讯-新版视频最终页
     */
    private static final String NEWS_VIDEO_FINAL_PAGE_SCHEME = "autohome://article/slidevideodetail";

    /**
     * 热点榜-热点榜页协议
     */
    private static final String NEWS_RCMHOTRANK_HOME_SCHEME = "autohome://article/hotchannelrank";

    /**
     * 热点榜-热点落地页协议
     */
    private static final String NEWS_RCMHOTRANK_DETAIL_SCHEME = "https://fs.autohome.com.cn/app_spa/hotart/index.html#?id=%s";

    /**
     * 资讯-视频卡片点赞
     */
    private static final String NEWS_VIDEO_PRAISE_SCHEME = "autohome://article/videopraise?schemetype=%d&idd=%s&mediatype=%d";

    /**
     * 论坛-帖子最终页
     */
    private static final String CLUB_TOPIC_DETAIL_SCHEME = "autohome://club/topicdetail";

    /**
     * 论坛-帖子列表页
     */
    private static final String CLUB_TOPICLIST_SCHEME = "autohome://club/topiclist?bbsid=%d&bbstype=%s&bbsname=%s";

    /**
     * 论坛-帖子点赞
     */
    private static final String CLUB_TOPIC_PRAISE_SCHEME = "autohome://club/articleparise?busimediatype=5&schemetype=%d&topicid=%d";

    // /** 新车专题页 */
    // private static final String NEWS_NEWCAR_SCHEME = "https://fs.autohome.com.cn/app_views/2024_beijing_newcar#/detail?id=%d";
    // private static final String NEWS_NEWCAR_SCHEME = "autohome://car/seriesmain?seriesid=%s"; // 专题页未上线，暂用车系页代替

    /**
     * 品库-之家实测-落地页协议
     */
    private static final String CARS_AUTOTEST_DETTAIL_SCHEME = "autohome://car/ahtest";

    /**
     * 品库-之家实测-视频落地页协议
     */
    private static final String CARS_AUTOTEST_VIDEODETTAIL_SCHEME = "autohome://car/immersionvideo";

    /**
     * H5地址转协议
     *
     * @param url
     * @return
     */
    public static String getScheme(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }

        String scheme = url;
        if (url.startsWith("autohome://")) {

        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            scheme = String.format("autohome://insidebrowserwk?url=%s", EncodesUtils.urlEncode(URLUtils.convertHttpToHttps(url)));
        }
        return scheme;
    }


    /**
     * H5地址转协议
     *
     * @param url
     * @param isWK               是否使用 WK 协议
     * @param navigationBarsTyle
     * @return
     */
    public static String getScheme(String url, Boolean isWK, Integer navigationBarsTyle) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        String scheme = url;
        if (url.startsWith("autohome://")) {

        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            scheme = String.format((isWK ? "autohome://insidebrowserwk?navigationbarstyle=%s&url=%s" : "autohome://insidebrowser?navigationbarstyle=%s&url=%s"), navigationBarsTyle, EncodesUtils.urlEncode(URLUtils.convertHttpToHttps(url)));
        }
        return scheme;
    }

    /**
     * 资讯-获取小视频播放页协议
     *
     * @param source         入口来源
     * @param bsId           业务来源
     * @param bsName         业务简称 （需编码）
     * @param id             小视频id
     * @param url            小视频url。iscontinue=1时，url必须传。
     * @param loadmodel      加载模式
     * @param order          排序规则：1-按发布时间倒序（默认），2-按发布时间正序，3-按热度倒序，4-按热度正序
     * @param topicId        小视频话题ID
     * @param seriesId       相关车系ID
     * @param isContinue     续播标识：1-续播，0-不续播，默认0
     * @param notShowRecord  不显示“录制”按钮：0-显示，1-不显示，默认0
     * @param notShowPublish 不显示“我发布的”按钮：0-显示，1-不显示，默认0
     * @param showMore       显示“更多”按钮：0-不显示，1-显示，默认0。注意：showmore如果显示时，notshowpublish会强制不显示。
     * @param notFeedback    不支持负反馈。1-不支持，0-支持（默认）。注意：如果showmore=1时，notfeedback会强制为1。
     * @param businessLine   描述指定业务线类型，接口根据此参数区分请求哪个接口。
     * @param pvData         用于上报透传的pv内容
     * @return
     */
    public static String getNewsShortVideoListScheme(int source, int bsId, String bsName, int id, String url, int loadmodel, int order,
                                                     int topicId, String seriesId, int isContinue, int notShowRecord, int notShowPublish,
                                                     int showMore, int notFeedback, Integer specId, int businessLine, String pvData) {
        StringBuffer schemeSb = new StringBuffer("autohome://article/shortvideolist");
        schemeSb.append("?source=").append(source)
                .append("&bsid=").append(bsId)
                .append("&bsname=").append(StringUtils.isNotEmpty(bsName) ? EncodesUtils.urlEncode(bsName) : "")
                .append("&id=").append(id)
                .append("&url=").append(StringUtils.isNotEmpty(url) ? EncodesUtils.urlEncode(url) : "")
                .append("&loadmodel=").append(loadmodel)
                .append("&order=").append(order)
                .append("&topicid=").append(topicId)
                .append("&seriesid=").append(seriesId)
                .append("&specid=").append(specId)
                .append("&iscontinue=").append(isContinue)
                .append("&notshowrecord=").append(notShowRecord)
                .append("&notshowpublish=").append(notShowPublish)
                .append("&showmore=").append(showMore)
                .append("&notfeedback=").append(notFeedback)
                .append("&businessline=").append(businessLine)
                .append("&pvdata=").append(StringUtils.isNotEmpty(pvData) ? pvData : "");
        return schemeSb.toString();
    }

    /**
     * 资讯-获取小视频播放页协议
     *
     * @param source         入口来源
     * @param bsId           业务来源
     * @param bsName         业务简称 （需编码）
     * @param id             小视频id
     * @param url            小视频url。iscontinue=1时，url必须传。
     * @param loadmodel      加载模式
     * @param order          排序规则：1-按发布时间倒序（默认），2-按发布时间正序，3-按热度倒序，4-按热度正序
     * @param topicId        小视频话题ID
     * @param seriesId       相关车系ID
     * @param isContinue     续播标识：1-续播，0-不续播，默认0
     * @param notShowRecord  不显示“录制”按钮：0-显示，1-不显示，默认0
     * @param notShowPublish 不显示“我发布的”按钮：0-显示，1-不显示，默认0
     * @param showMore       显示“更多”按钮：0-不显示，1-显示，默认0。注意：showmore如果显示时，notshowpublish会强制不显示。
     * @param notFeedback    不支持负反馈。1-不支持，0-支持（默认）。注意：如果showmore=1时，notfeedback会强制为1。
     * @return
     */
    public static String getNewsShortVideoListScheme(int source, int bsId, String bsName, int id, String url, int loadmodel, int order,
                                                     int topicId, String seriesId, int isContinue, int notShowRecord, int notShowPublish,
                                                     int showMore, int notFeedback, Integer specId) {
        return getNewsShortVideoListScheme(source, bsId, bsName, id, url, loadmodel, order, topicId, seriesId, isContinue, notShowRecord, notShowPublish, showMore, notFeedback, specId, 0, "");
    }

    /**
     * AH-100落地页跳转协议
     *
     * @param id 文章ID
     * @return
     */
    public static String getAH100Scheme(Object id) {
        return String.format("autohome://article/articledetail?newsid=%s&newstype=64&articlefromtype=0&shieldpublish=0&scrolltocomment=0", id);
    }

    /**
     * 资讯-获取文章最终页协议
     *
     * @param newsId          文章ID
     * @param articleFrom     入口来源 0-普通文章；1-焦点图文章；2-首页Feed流文章；3-行业列表
     * @param isScrollToReply 是否滑动到评论列表 true-是；falst-否
     * @param lastUpdateTime  文章最后更新时间的时间戳（用于读取预加载的缓存）
     * @param pvid            pvid , 智能流入口数据需要
     * @return
     */
    public static String getArticleDetailScheme(Object newsId, int articleFrom, boolean isScrollToReply, String lastUpdateTime, String pvid) {
        return String.format("autohome://article/articledetail?newsid=%s&newstype=0&articlefromtype=%s&scrolltocomment=%s&lastupdatetime=%s&pvid=%s",
                newsId, articleFrom, isScrollToReply ? 1 : 0, lastUpdateTime, pvid);
    }

    /**
     * 资讯-获取原创话题最终页协议
     *
     * @param newsId          话题ID
     * @param articleFrom     入口来源 0-普通文章；1-焦点图文章；2-首页Feed流文章；3-行业列表
     * @param isShieldReply   是否屏蔽评论 true-是；false-否
     * @param isScrollToReply 是否滑动到评论列表 true-是；falst-否
     * @param lastUpdateTime  文章最后更新时间的时间戳（用于读取预加载的缓存）
     * @param pvid            pvid , 智能流入口数据需要
     * @return
     */
    public static String getNewsTopicScheme(Object newsId, int articleFrom, Boolean isShieldReply, boolean isScrollToReply, String lastUpdateTime, String pvid) {
        return String.format("autohome://article/articledetail?newsid=%s&newstype=26&articlefromtype=%s&shieldpublish=%s&scrolltocomment=%s&lastupdatetime=%s&pvid=%s",
                newsId, articleFrom, isShieldReply ? 1 : 0, isScrollToReply ? 1 : 0, lastUpdateTime, pvid);
    }

    /**
     * 资讯-获取图文最终页协议
     *
     * @param objId 图文ID
     * @param pcis  三图图片地址，㊣ 分隔
     * @param pvid  统计pv
     * @return
     */
    public static String getTuWenScheme(Object objId, String pcis, String pvid) {
        return String.format("autohome://article/pictextdetail?newsid=%s&indexdetail=%s&pvid=%s", objId, EncodesUtils.urlEncode(pcis), pvid);
    }

    /**
     * 车家号-获取车家号-车单最终页协议
     *
     * @param objId      车单ID
     * @param fromSource 入口来源
     * @param pvid       pvid , 智能流入口数据需要
     * @return
     */
    public static String getYC_CheDanScheme(Object objId, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/chedan?newsid=%s&fromsource=%s&pvid=%s", objId, fromSource, pvid);
    }

    /**
     * 车家号-长文最终页协议
     *
     * @param objId           车单ID
     * @param isScrollToReply
     * @param fromSource      入口来源
     * @param pvid            pvid , 智能流入口数据需要
     * @param scrollToComment 是否进入页面锚点至评论
     * @return
     */
    public static String getCheJiaHaoChangWenScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid, boolean scrollToComment) {
        return String.format("autohome://articleplatform/detail/long?newsid=%s&autoscrolltocomment=%d&fromsource=%d&pvid=%s&scrolltocomment=%d", objId, isScrollToReply ? 1 : 0, fromSource, pvid, scrollToComment ? 1 : 0);
    }

    /**
     * 直播-获取直播落地页协议
     *
     * @param objId            直播房间号
     * @param isVerticalScreen 是否是 竖屏
     * @param pvid
     * @param sourceId         来源ID
     * @return
     */
    public static String getLiveScheme(Object objId, Boolean isVerticalScreen, String pvid, Integer sourceId) {
        return getLiveScheme_Source(objId, isVerticalScreen, pvid, sourceId);
    }

    /**
     * 直播-获取直播落地页协议
     *
     * @param objId            直播房间号
     * @param isVerticalScreen 是否是 竖屏
     * @param pvid
     * @return
     */
    public static String getLiveScheme(Object objId, Boolean isVerticalScreen, String pvid) {
        return getLiveScheme_Source(objId, isVerticalScreen, pvid, 0);
    }

    /**
     * 直播-获取直播落地页协议(横屏）
     *
     * @param objId 直播房间号
     * @return
     */
    public static String getLiveScheme(Object objId) {
        return getLiveScheme_Source(objId, false, "", 0);
    }

    /**
     * 直播-获取直播落地页协议(横屏）
     *
     * @param objId 直播房间号
     * @param pvid
     * @return
     */
    public static String getLiveScheme(Object objId, String pvid) {
        return getLiveScheme_Source(objId, false, pvid, 0);
    }

    /**
     * 直播-获取直播落地页协议
     *
     * @param objId            直播房间号
     * @param isVerticalScreen 是否是 竖屏
     * @param pvid
     * @return
     */
    private static String getLiveScheme_Source(Object objId, Boolean isVerticalScreen, String pvid, Integer sourceId) {
        if (isVerticalScreen) {
            return String.format("autohome://liveshow/liveshowvertical?roomid=%s&pvid=%s&sourceid=%s", objId, pvid, sourceId);
        } else {
            return String.format("autohome://liveshow/liveshowdetail?roomid=%s&pvid=%s&sourceid=%s", objId, pvid, sourceId);
        }

    }

    /**
     * 论坛-获取帖子最终页协议
     *
     * @param objId   帖子ID
     * @param title   帖子标题（未编码的）
     * @param bbsId   帖子所属论坛ID
     * @param bbsType 帖子所属论坛类型
     * @param isask   是否是问答帖
     * @param pvid    统计pvid
     * @return
     */
    public static String getTopicDetailScheme(Object objId, String title, int bbsId, String bbsType, Boolean isask, String pvid) {
        String topicTitle = StringUtils.isNotEmpty(title) ? EncodesUtils.urlEncode(title).replaceAll("\\+", "%20") : "";
        return String.format("autohome://club/topicdetail?pageid=%s&title=%s&bbsid=%s&bbstype=%s&isask=%s&pvid=%s",
                objId, topicTitle, bbsId, bbsType, isask ? 1 : 0, pvid);
    }

    /**
     * 论坛-获取帖子最终页协议
     *
     * @param topicId 帖子id
     * @param bbsId   论坛id
     * @param title   帖子标题
     * @param bbsType 论坛类型 c（车系） a（地区） o（主题）
     * @param pvId    智能推荐需传此参数
     * @param from    页面来源
     * @param isReply 是否跳转到回复区 1直接定位回复区， 0 不定位回复区
     * @return String
     */
    public static String getTopicDetailScheme(Long topicId, Integer bbsId, String title, String bbsType, String pvId, int from, int isReply) {
        StringBuilder schemeSb = new StringBuilder(CLUB_TOPIC_DETAIL_SCHEME);
        schemeSb.append("?topicid=").append(topicId);
        if (bbsId != null) {
            schemeSb.append("&bbsid=").append(bbsId.intValue());
        }
        if (StringUtils.isNotEmpty(title)) {
            schemeSb.append("&title=").append(EncodesUtils.urlEncode(title));
        }
        if (StringUtils.isNotEmpty(bbsType)) {
            schemeSb.append("&bbstype=").append(bbsType);
        }
        schemeSb.append("&pvid=").append(pvId)
                .append("&from=").append(from)
                .append("&isreply=").append(isReply);
        return schemeSb.toString();
    }


    /**
     * 车家号-获取车家号-长文最终页协议
     *
     * @param objId      车单ID
     * @param fromSource 入口来源
     * @param pvid       pvid , 智能流入口数据需要
     * @return
     */
    public static String getYC_ChangWenScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/long?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    /**
     * 车家号-获取车家号-短文最终页协议
     *
     * @param objId      车单ID
     * @param fromSource 入口来源
     * @param pvid       pvid , 智能流入口数据需要
     * @return
     */
    public static String getYC_DuanWenScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/short?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    /**
     * 车家号-短文最终页协议
     *
     * @param objId           车单ID
     * @param isScrollToReply
     * @param fromSource      入口来源
     * @param pvid            pvid , 智能流入口数据需要
     * @param scrollToComment 是否进入页面锚点至评论
     * @return
     */
    public static String getCheJiaHaoDuanWenScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid, boolean scrollToComment) {
        return String.format("autohome://articleplatform/detail/short?newsid=%s&autoscrolltocomment=%d&fromsource=%d&pvid=%s&scrolltocomment=%d", objId, isScrollToReply ? 1 : 0, fromSource, pvid, scrollToComment ? 1 : 0);
    }

    /**
     * 车家号-获取车家号-视频最终页协议
     *
     * @param objId      车单ID
     * @param fromSource 入口来源
     * @param pvid       pvid , 智能流入口数据需要
     * @return
     */
    public static String getYC_VideoScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/video?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s&continueType=0&isquiet=0", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    /**
     * 车家号-获取车家号-音频最终页协议
     *
     * @param objId      车单ID
     * @param fromSource 入口来源
     * @param pvid       pvid , 智能流入口数据需要
     * @return
     */
    public static String getYC_RadioScheme(Object objId, boolean isScrollToReply, int fromSource, String pvid) {
        return String.format("autohome://articleplatform/detail/audio?newsid=%s&autoscrolltocomment=%s&fromsource=%s&pvid=%s", objId, isScrollToReply ? 1 : 0, fromSource, pvid);
    }

    /**
     * 关注-关注话题落地页协议
     *
     * @param topicName 话题名(not urlEncode)
     * @return
     */
    public static String getConcern_TopicPageScheme(String topicName) {
        return String.format("autohome://attention/topicpager?name=%s", EncodesUtils.urlEncode(topicName));
    }

    /**
     * 资讯-图说落地页
     *
     * @param id     文章ID
     * @param picStr 三图图片地址，㊣ 分隔
     * @return
     */
    public static String getNews_PicArticlePageScheme(Object id, String picStr) {
        return getNews_PicArticlePageScheme(id, picStr, "");
    }

    /**
     * 资讯-图说落地页
     *
     * @param id     文章ID
     * @param picStr 三图图片地址，㊣ 分隔
     * @param pvid
     * @return
     */
    public static String getNews_PicArticlePageScheme(Object id, String picStr, String pvid) {
        return String.format("autohome://article/picarticledetail?newsid=%s&indexdetail=%s&pvid=%s", id, EncodesUtils.urlEncode(picStr), pvid);
    }

    /**
     * 口碑落地页跳转协议
     *
     * @param kouBeiId
     * @param seriesId
     * @param seriesName
     * @param specId
     * @param specName
     * @param userName
     * @param reportData
     * @param pvid
     * @param from
     * @return
     */
    public static String getKouBeiScheme(Integer kouBeiId, Integer seriesId, String seriesName, Integer specId, String specName, String userName, String reportData, String pvid, Integer from) {
        return String.format("autohome://reputation/reputationdetail?koubeiid=%s&seriesid=%s&seriesname=%s&specid=%s&specname=%s&username=%s&reportdata=%s&pvid=%s&from=%s",
                kouBeiId, seriesId, StringUtils.isNotEmpty(seriesName) ? EncodesUtils.urlEncode(seriesName) : "", specId, StringUtils.isNotEmpty(specName) ? EncodesUtils.urlEncode(specName) : "",
                StringUtils.isNotEmpty(userName) ? EncodesUtils.urlEncode(userName) : "", reportData, pvid, from);
    }

    /**
     * 车系小视频聚合页协议
     *
     * @param seriesId
     * @return
     */
    public static String getSmallVideos_SeriesTheme(Integer seriesId) {
        return String.format("autohome://article/acsmvcarseriesinfodetail?seriesid=%s&pt=%s", seriesId, 1);
    }

    /**
     * 小视频音乐聚合页
     *
     * @param musicId 音乐ID
     * @return
     */
    public static String getSmallVideos_MusicTheme(String musicId) {
        return String.format("autohome://article/shortmusicactivity?musicid=%s", musicId);
    }

    /**
     * 小视频活动聚合页
     *
     * @param topicId 活动ID
     * @return
     */
    public static String getSmallVideos_TopicTheme(Integer topicId, Integer source) {
        return String.format("autohome://article/shortvideoactivity?topicid=%s&source=%s", topicId, source);
    }

    /**
     * 车家号作者 客页
     *
     * @param userId
     * @param selectTagId 选择的标签
     * @param fromSource  来源位置
     * @return
     */
    public static String getUserPageScheme_CheJiaHao(Integer userId, Integer selectTagId, Integer fromSource) {
        return String.format("autohome://articleplatform/userinfo?userid=%s&selecttagid=%s&fromsource=%s", userId, selectTagId, fromSource);
    }

    /**
     * 之家用户 客页
     *
     * @param userId
     * @param authorId
     * @param from
     * @param userName
     * @return
     */
    public static String getUserPageScheme_AutoUser(Integer userId, Integer authorId, Integer from, String userName) {
        return String.format("autohome://user/hiscenter?userid=%s&authorid=%s&from=%s&username=%s", userId, authorId, from, EncodesUtils.urlEncode(userName));
    }

    /**
     * 获取用户个人主页
     *
     * @param userId 用户id
     * @return
     */
    public static String getUserPageScheme(int userId) {
        return String.format("autohome://user/hiscenter?userid=%d", userId);
    }


    /**
     * 产品库-获取资讯/车展图片读图协议
     *
     * @param seriesId   车系id
     * @param specId     车型id
     * @param pictureId  图片id
     * @param sourceType 来源 201资讯 202 VR车展
     * @param autoShowId 车展id （sourcetype为202车展时必须传）
     * @return
     */
    public static String getCarPicturePageScheme(int seriesId, int specId, int pictureId, int sourceType, int autoShowId) {
        return String.format(CAR_PICTURE_PAGE_SCHEME, seriesId, specId, pictureId, sourceType, autoShowId);
    }

    /**
     * 资讯-关键词文章列表页协议
     *
     * @return
     */
    public static String getNewsKeyWordArticleListScheme(String wordIds, String keyword, String backgroundImage) {
        keyword = StringUtils.isNotEmpty(keyword) ? EncodesUtils.urlEncode(keyword) : "";
        return String.format(NEWS_LABELS_DETAIL_SCHEME, wordIds, keyword, backgroundImage);
    }

    /**
     * 资讯-原创编辑主页
     *
     * @param authorId     编辑id
     * @param authorUserId 用户id
     * @param fromType     0:普通编辑账号  1:视频编辑账号
     * @return
     */
    public static String getNewsAuthorDetailScheme(int authorId, int authorUserId, int fromType) {
        return String.format(NEWS_AUTHOR_DETAIL_SCHEME, authorId, authorUserId, fromType);
    }

    /**
     * 产品库-获取车系综述页协议
     *
     * @param seriesId 车系id
     * @return
     */
    public static String getCarSeriesMainScheme(int seriesId) {
        return String.format(CAR_SERIES_MAIN_SCHEME, seriesId, 0);
    }

    /**
     * 产品库-获取车系综述页协议
     *
     * @param seriesId 车系id
     * @param fromType 来源
     * @return
     */
    public static String getCarSeriesMainScheme(int seriesId, int fromType) {
        return String.format(CAR_SERIES_MAIN_SCHEME, seriesId, fromType);
    }

    /**
     * 产品库-获取车系图片列表页协议
     *
     * @param seriesId   车系id
     * @param seriesName 车系名称-广告接口使用
     * @param categoryId 分类id，用于定位到对应分类tab页面 【不传默认定位外观】1.外观 10.中控 3.座椅 12.细节 14.特点 701.视频
     * @param orgin      来源：0.车系 1.车型 【ios使用】
     * @return
     */
    public static String getCarSeriesPictureScheme(int seriesId, String seriesName, int categoryId, int orgin) {
        return String.format(CAR_SERIES_PICTURE_SCHEME, seriesId, seriesName, categoryId, orgin);
    }


    /**
     * 处理导航url
     *
     * @param pm
     * @param url
     * @return
     */
    public static String parseNavigationUrl(int pm, String url) {
        if (pm == 2 && StringUtils.isNotEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
            String insertStr = "?aaplp=1&";
            String spilChar = "";
            if (url.contains("?")) {
                spilChar = "[?]";
            } else if (url.contains("#")) {
                spilChar = "#";
            } else {

            }
            if (StringUtils.isNotEmpty(spilChar)) {
                String[] urlArr = url.split(spilChar);
                url = urlArr[0] + insertStr + urlArr[1];
            } else {
                url += insertStr;
            }
        }
        return url;
    }

    /**
     * 资讯-获取快讯详情页协议
     *
     * @param id
     * @param from
     * @return
     */
    public static String getNewsFastnewsDetailScheme(Object id, int from) {
        return String.format(NEWS_FASTNEWS_DETAIL_SCHEME, id, from);
    }

    /**
     * 资讯-获取快讯详情页协议
     *
     * @param id              快讯id
     * @param from            来源【int】预留字段
     * @param scrollToComment 是否进入页面锚点至评论
     * @return
     */
    public static String getNewsFastnewsDetailScheme(Object id, int from, boolean scrollToComment) {
        String scheme = String.format(NEWS_FASTNEWS_DETAIL_SCHEME, id, from, scrollToComment ? 1 : 0);

        int isScrollToComment = scrollToComment ? 1 : 0;
        scheme = scheme + "&scrolltocomment=" + isScrollToComment;

        return scheme;
    }

    /**
     * 资讯-获取原创视频栏目主页
     *
     * @param id
     * @return
     */
    public static String getNewsOriginalColumnScheme(int id) {
        return String.format(NEWS_ORIGINAL_COLUMN_SCHEME, id);
    }

    /**
     * 产品库-车系详解
     *
     * @param seriesId   车系id
     * @param seriesName 车系名称
     * @param fromType   0 默认有导航 1 车系综述页嵌入 无导航
     * @param id         对应的节点id---定位使用
     * @return
     */
    public static String getCarSeriesAnnotationScheme(int seriesId, String seriesName, int fromType, int id) {
        return String.format(CAR_SERIES_ANNOTATION_SCHEME, seriesId, EncodesUtils.urlEncode(seriesName), fromType, id);
    }

    /**
     * 资讯-标签视频列表页协议
     *
     * @param fromType
     * @param tagId
     * @return
     */
    public static String getNewsTagvideoListScheme(int fromType, String tagId) {
        return String.format(NEWS_TAGVIDEO_LIST_SCHEME, fromType, tagId);
    }

    /**
     * 获取用户勋章页面协议
     *
     * @param userId 用户id
     * @return
     */
    public static String getUserBadgePageScheme(int userId) {
        return "autohome://rninsidebrowser?url=" + EncodesUtils.urlEncode(String.format("rn://attainment_system/AttainmentPage?tid=%s", userId));
    }

    /**
     * 资讯-新版视频最终页
     *
     * @param newsId          视频id
     * @param from            视频最终页来源:  首页推荐列表-2
     * @param mediaType       视频类型：3-资讯原创视频，66-论坛视频，14-车家号。默认值：3
     * @param seriesIds       车系Id
     * @param vid             视频源id 续播需要传该参数
     * @param scrollToComment 定位到评论列表，0-不定位到评论列表，1-定位到评论列表，默认值：0
     * @param isContinue      是否需要续播  0:不续播 1:续播(仅针对oputype不等与0时生效)
     * @param fromPage        新版视频最终页来源   0:未知来源  ； 1:首页推荐列表 ；100031：推送消息；
     * @param comext          页面pv，click，show等事件中的透传参数，值为jsonstring格式
     * @return
     */
    public static String getNewsVideoFinalPageScheme(int newsId, int from, int mediaType, String seriesIds, String vid, int scrollToComment,
                                                     int isContinue, int fromPage, String comext) {
        StringBuffer sb = new StringBuffer(NEWS_VIDEO_FINAL_PAGE_SCHEME);
        sb.append("?newsid=").append(newsId)
                .append("&from=").append(from)
                .append("&mediatype=").append(mediaType)
                .append("&seriesids=").append(seriesIds)
                .append("&vid=").append(vid)
                .append("&scrolltocomment=").append(scrollToComment)
                .append("&iscontinue=").append(isContinue)
                .append("&frompage=").append(fromPage);
        if (StringUtils.isNotEmpty(comext)) {
            sb.append("&comext=").append(EncodesUtils.urlEncode(comext));
        }
        return sb.toString();
    }

    /**
     * 产品库-条件选车
     *
     * @param type
     * @param from
     * @param price
     * @param autoTag
     * @param brandId
     * @param levelId
     * @param mileAge
     * @return
     */
    public static String getCarSelectCarScheme(int type, int from, String price, String autoTag, String brandId, String levelId, String mileAge) {
        StringBuffer sb = new StringBuffer("rn://Car_SelectCar/Main");
        sb.append("?type=").append(type).append("&from=").append(from);
        if (StringUtils.isNotEmpty(price)) {
            sb.append("&price=").append(price);
        }
        if (StringUtils.isNotEmpty(autoTag)) {
            sb.append("&autotag=").append(autoTag);
        }
        if (StringUtils.isNotEmpty(brandId)) {
            sb.append("&brandid=").append(brandId);
        }
        if (StringUtils.isNotEmpty(levelId)) {
            sb.append("&levelid=").append(levelId);
        }
        if (StringUtils.isNotEmpty(mileAge)) {
            sb.append("&mileage=").append(mileAge);
        }
        return "autohome://rninsidebrowser?url=" + EncodesUtils.urlEncode(sb.toString());
    }


    public static String getCarSeriesBrandScheme(int brandId, String brandName) {
        brandName = StringUtils.isNotEmpty(brandName) ? EncodesUtils.urlEncode(brandName).replaceAll("\\+", "%20") : "";
        return String.format(CAR_SERIES_BRAND_SCHEME, brandId, brandName);
    }

    /**
     * 产品库-获取车系配置协议
     *
     * @param seriesId   车系id
     * @param seriesName 车系名称
     * @return
     */
    public static String getCarCompareParamcontrastScheme(int seriesId, String seriesName) {
        return String.format(CAR_COMPARE_PARAMCONTRAST, seriesId, EncodesUtils.urlEncode(seriesName));
    }

    /**
     * 热点榜-热点榜单首页协议（榜单页）
     *
     * @return
     */
    public static String getRcmHotRankHomeScheme() {
        return SchemeUtil.NEWS_RCMHOTRANK_HOME_SCHEME;
    }

    /**
     * 热点榜-热点频道页协议
     *
     * @return
     */
    public static String getRcmHotRankChannelScheme() {
        return SchemeUtil.NEWS_RCMHOTRANK_HOME_SCHEME;
    }

    /**
     * 热点榜-热点落地页协议
     *
     * @return
     */
    public static String getRcmHotRankDetailScheme(String id) {
        String scheme = String.format(SchemeUtil.NEWS_RCMHOTRANK_DETAIL_SCHEME, id);
        return SchemeUtil.getScheme(scheme) + "&loadtype=1";
    }

    /**
     * 资讯-视频卡片点赞
     *
     * @param schemeType 是否是数据协议 1-数据协议，0-非数据协议
     * @param idd        落地页id
     * @param mediaType  业务类型：3-资讯原创视频，66-论坛视频，14-车家号，等等
     * @return String
     */
    public static String getNewsVideoPraiseScheme(int schemeType, String idd, int mediaType) {
        return String.format(NEWS_VIDEO_PRAISE_SCHEME, schemeType, idd, mediaType);
    }

    /**
     * 论坛-帖子列表页
     *
     * @param bbsId   论坛id
     * @param bbsType 论坛类型 c（车系） o（主题） a（地区）
     * @param bbsName 论坛名称
     * @return String
     */
    public static String getClubTopicListScheme(int bbsId, String bbsType, String bbsName) {
        return String.format(CLUB_TOPICLIST_SCHEME, bbsId, bbsType, EncodesUtils.urlEncode(bbsName));
    }

    /**
     * 论坛-帖子点赞
     *
     * @param schemeType schemeType
     * @param topicId    topicId
     * @return String
     */
    public static String getClubTopicPraiseScheme(int schemeType, int topicId) {
        return String.format(CLUB_TOPIC_PRAISE_SCHEME, schemeType, topicId);
    }

    /**
     * 品库-之家实测-落地页协议
     *
     * @param seriesId 车系ID
     * @param specId   车型ID
     * @param dataId   数据ID
     * @return
     */
    public static String getCarsAutoTestDetailScheme(Integer seriesId, Integer specId, Integer dataId) {
        StringBuffer sb = new StringBuffer(CARS_AUTOTEST_DETTAIL_SCHEME);
        sb.append("?sourceid=").append(3); // 固定值，品库提供。目前就1个入口，后续有需要抽到入参

        // 车系ID
        String seriesIdParam = "";
        if (seriesId != null) {
            seriesIdParam = seriesId + "";
        }
        sb.append("&seriesid=").append(seriesIdParam);

        // 车型ID
        String specIdParam = "";
        if (specId != null) {
            specIdParam = specId + "";
        }
        sb.append("&specid=").append(specIdParam);

        // 数据ID
        String dataIdParam = "";
        if (dataId != null) {
            dataIdParam = dataId + "";
        }
        sb.append("&dataid=").append(dataIdParam);

        return sb.toString();
    }

    /**
     * 品库-之家实测-视频落地页协议
     *
     * @param seriesId 车系ID
     * @param specId   车型ID
     * @param dataId   数据ID
     * @return
     */
    public static String getCarsAutoTestVideoDetailScheme(Integer seriesId, Integer specId, Integer dataId, String vid) {
        StringBuffer sb = new StringBuffer(CARS_AUTOTEST_VIDEODETTAIL_SCHEME);
        sb.append("?sourceid=").append(3); // 固定值，品库提供。目前就1个入口，后续有需要抽到入参

        // 车系ID
        String seriesIdParam = "";
        if (seriesId != null) {
            seriesIdParam = seriesId + "";
        }
        sb.append("&seriesid=").append(seriesIdParam);

        // 车型ID
        String specIdParam = "";
        if (specId != null) {
            specIdParam = specId + "";
        }
        sb.append("&specid=").append(specIdParam);

        // 数据ID
        String dataIdParam = "";
        if (dataId != null) {
            dataIdParam = dataId + "";
        }
        sb.append("&dataid=").append(dataIdParam);

        // 视频ID
        String vidParam = "";
        if (vid != null) {
            vidParam = vid + "";
        }
        sb.append("&vid=").append(vidParam);

        return sb.toString();
    }

    public static String getNewShortArticleScheme(Long objId, Integer mediaType, Integer scrollToComment, Integer fromPage) {
        return String.format("autohome://article/detail/newshort?newsid=%d&frompage=%d&mediatype=%d&autoscrolltocomment=%d",
                objId, fromPage, mediaType, scrollToComment);
    }

}