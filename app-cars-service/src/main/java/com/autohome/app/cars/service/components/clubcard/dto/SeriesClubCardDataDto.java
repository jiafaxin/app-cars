package com.autohome.app.cars.service.components.clubcard.dto;

import java.util.ArrayList;
import java.util.List;

import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;

/**
 * @author wbs
 * @date 2024/6/6
 */
public class SeriesClubCardDataDto {

    private int club_topic_piccount;
    private String club_video_title;
    private int pv;
    private String club_bbs_type;
    private int club_refine;
    private int vote_id;
    private int club_is_class_quality;
    private String club_jingxuan_imgs;
    private int club_is_pool;
    private String publish_time;
    private String ip_province_name;
    private String ip_city_code;
    private String pc_url;
    private int club_topic_isPicture;
    private String club_jingxuan_publishtime;
    private int club_qa_ishigh;
    private String club_ip;
    private int club_topic_wordcount;
    private String club_video_explain;
    private int reward_coin;
    private String club_bbs_name;
    private long global_id;
    private int reply_count;
    private String m_url;
    private String club_jinghua_imgs169;
    private String ip_province_code;
    private int club_is_video;
    private String subtitle;
    private int club_is_poll;
    private String club_jinghua_summary;
    private int author_id;
    private double hot_score_temp;
    private String club_jinghua_imgs;
    private String imgList;
    private String author_name;
    private int club_is_solve;
    private double hot_score;
    private String club_liveCover;
    private String club_videoimgs;
    private String icon;
    private String title;
    private List<String> club_topicimgs;
    private int duration;
    private int club_is_jingxuan;
    private int topic_reply_count_all;
    private List<String> club_topic_tag_names;
    private String club_video_quality;
    private String topictype;
    private String video_source;
    private String club_video_publishtime;
    private String summary;
    private String app_url;
    private String main_data_type;
    private double hot_score_all_bbs;
    private String club_topic_lastPostDate;
    private List<String> club_topic_tag_ids;
    private int is_delete;
    private int is_close_comment;
    private int club_liveId;
    private int club_delete_flag;
    private List<String> multi_images;
    private int topic_reply_count;
    private int club_qa_confirmreplyid;
    private int club_ishtml;
    private int club_bbs_id;
    private String club_qa_summary;
    private int biz_id;
    private String ip_city_name;

    private int like_count;
    private String last_reply_date;
    private String club_videoimgs169;
    private int club_qa_pool;
    private List<Integer> series_ids;
    private int counter;

    private String username;
    private String headimg;
    private int authseriesid;
    private String authseriesname;
    private int authlevel;
    private List<UserAuthSeriesResult.AuthseriesResult> rzcList = new ArrayList<>();

    public List<UserAuthSeriesResult.AuthseriesResult> getRzcList() {
        return rzcList;
    }

    public void setRzcList(List<UserAuthSeriesResult.AuthseriesResult> rzcList) {
        this.rzcList = rzcList;
    }

    public int getClub_topic_piccount() {
        return club_topic_piccount;
    }

    public void setClub_topic_piccount(int club_topic_piccount) {
        this.club_topic_piccount = club_topic_piccount;
    }

    public String getClub_video_title() {
        return club_video_title;
    }

    public void setClub_video_title(String club_video_title) {
        this.club_video_title = club_video_title;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public String getClub_bbs_type() {
        return club_bbs_type;
    }

    public void setClub_bbs_type(String club_bbs_type) {
        this.club_bbs_type = club_bbs_type;
    }

    public int getClub_refine() {
        return club_refine;
    }

    public void setClub_refine(int club_refine) {
        this.club_refine = club_refine;
    }

    public int getVote_id() {
        return vote_id;
    }

    public void setVote_id(int vote_id) {
        this.vote_id = vote_id;
    }

    public int getClub_is_class_quality() {
        return club_is_class_quality;
    }

    public void setClub_is_class_quality(int club_is_class_quality) {
        this.club_is_class_quality = club_is_class_quality;
    }

    public String getClub_jingxuan_imgs() {
        return club_jingxuan_imgs;
    }

    public void setClub_jingxuan_imgs(String club_jingxuan_imgs) {
        this.club_jingxuan_imgs = club_jingxuan_imgs;
    }

    public int getClub_is_pool() {
        return club_is_pool;
    }

    public void setClub_is_pool(int club_is_pool) {
        this.club_is_pool = club_is_pool;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getIp_province_name() {
        return ip_province_name;
    }

    public void setIp_province_name(String ip_province_name) {
        this.ip_province_name = ip_province_name;
    }

    public String getIp_city_code() {
        return ip_city_code;
    }

    public void setIp_city_code(String ip_city_code) {
        this.ip_city_code = ip_city_code;
    }

    public String getPc_url() {
        return pc_url;
    }

    public void setPc_url(String pc_url) {
        this.pc_url = pc_url;
    }

    public int getClub_topic_isPicture() {
        return club_topic_isPicture;
    }

    public void setClub_topic_isPicture(int club_topic_isPicture) {
        this.club_topic_isPicture = club_topic_isPicture;
    }

    public String getClub_jingxuan_publishtime() {
        return club_jingxuan_publishtime;
    }

    public void setClub_jingxuan_publishtime(String club_jingxuan_publishtime) {
        this.club_jingxuan_publishtime = club_jingxuan_publishtime;
    }

    public int getClub_qa_ishigh() {
        return club_qa_ishigh;
    }

    public void setClub_qa_ishigh(int club_qa_ishigh) {
        this.club_qa_ishigh = club_qa_ishigh;
    }

    public String getClub_ip() {
        return club_ip;
    }

    public void setClub_ip(String club_ip) {
        this.club_ip = club_ip;
    }

    public int getClub_topic_wordcount() {
        return club_topic_wordcount;
    }

    public void setClub_topic_wordcount(int club_topic_wordcount) {
        this.club_topic_wordcount = club_topic_wordcount;
    }

    public String getClub_video_explain() {
        return club_video_explain;
    }

    public void setClub_video_explain(String club_video_explain) {
        this.club_video_explain = club_video_explain;
    }

    public int getReward_coin() {
        return reward_coin;
    }

    public void setReward_coin(int reward_coin) {
        this.reward_coin = reward_coin;
    }

    public String getClub_bbs_name() {
        return club_bbs_name;
    }

    public void setClub_bbs_name(String club_bbs_name) {
        this.club_bbs_name = club_bbs_name;
    }

    public long getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(long global_id) {
        this.global_id = global_id;
    }

    public int getReply_count() {
        return reply_count;
    }

    public void setReply_count(int reply_count) {
        this.reply_count = reply_count;
    }

    public String getM_url() {
        return m_url;
    }

    public void setM_url(String m_url) {
        this.m_url = m_url;
    }

    public String getClub_jinghua_imgs169() {
        return club_jinghua_imgs169;
    }

    public void setClub_jinghua_imgs169(String club_jinghua_imgs169) {
        this.club_jinghua_imgs169 = club_jinghua_imgs169;
    }

    public String getIp_province_code() {
        return ip_province_code;
    }

    public void setIp_province_code(String ip_province_code) {
        this.ip_province_code = ip_province_code;
    }

    public int getClub_is_video() {
        return club_is_video;
    }

    public void setClub_is_video(int club_is_video) {
        this.club_is_video = club_is_video;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getClub_is_poll() {
        return club_is_poll;
    }

    public void setClub_is_poll(int club_is_poll) {
        this.club_is_poll = club_is_poll;
    }

    public String getClub_jinghua_summary() {
        return club_jinghua_summary;
    }

    public void setClub_jinghua_summary(String club_jinghua_summary) {
        this.club_jinghua_summary = club_jinghua_summary;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public double getHot_score_temp() {
        return hot_score_temp;
    }

    public void setHot_score_temp(double hot_score_temp) {
        this.hot_score_temp = hot_score_temp;
    }

    public String getClub_jinghua_imgs() {
        return club_jinghua_imgs;
    }

    public void setClub_jinghua_imgs(String club_jinghua_imgs) {
        this.club_jinghua_imgs = club_jinghua_imgs;
    }

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public int getClub_is_solve() {
        return club_is_solve;
    }

    public void setClub_is_solve(int club_is_solve) {
        this.club_is_solve = club_is_solve;
    }

    public double getHot_score() {
        return hot_score;
    }

    public void setHot_score(double hot_score) {
        this.hot_score = hot_score;
    }

    public String getClub_liveCover() {
        return club_liveCover;
    }

    public void setClub_liveCover(String club_liveCover) {
        this.club_liveCover = club_liveCover;
    }

    public String getClub_videoimgs() {
        return club_videoimgs;
    }

    public void setClub_videoimgs(String club_videoimgs) {
        this.club_videoimgs = club_videoimgs;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getClub_topicimgs() {
        return club_topicimgs;
    }

    public void setClub_topicimgs(List<String> club_topicimgs) {
        this.club_topicimgs = club_topicimgs;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getClub_is_jingxuan() {
        return club_is_jingxuan;
    }

    public void setClub_is_jingxuan(int club_is_jingxuan) {
        this.club_is_jingxuan = club_is_jingxuan;
    }

    public int getTopic_reply_count_all() {
        return topic_reply_count_all;
    }

    public void setTopic_reply_count_all(int topic_reply_count_all) {
        this.topic_reply_count_all = topic_reply_count_all;
    }

    public List<String> getClub_topic_tag_names() {
        return club_topic_tag_names;
    }

    public void setClub_topic_tag_names(List<String> club_topic_tag_names) {
        this.club_topic_tag_names = club_topic_tag_names;
    }

    public String getClub_video_quality() {
        return club_video_quality;
    }

    public void setClub_video_quality(String club_video_quality) {
        this.club_video_quality = club_video_quality;
    }

    public String getTopictype() {
        return topictype;
    }

    public void setTopictype(String topictype) {
        this.topictype = topictype;
    }

    public String getVideo_source() {
        return video_source;
    }

    public void setVideo_source(String video_source) {
        this.video_source = video_source;
    }

    public String getClub_video_publishtime() {
        return club_video_publishtime;
    }

    public void setClub_video_publishtime(String club_video_publishtime) {
        this.club_video_publishtime = club_video_publishtime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public String getMain_data_type() {
        return main_data_type;
    }

    public void setMain_data_type(String main_data_type) {
        this.main_data_type = main_data_type;
    }

    public double getHot_score_all_bbs() {
        return hot_score_all_bbs;
    }

    public void setHot_score_all_bbs(double hot_score_all_bbs) {
        this.hot_score_all_bbs = hot_score_all_bbs;
    }

    public String getClub_topic_lastPostDate() {
        return club_topic_lastPostDate;
    }

    public void setClub_topic_lastPostDate(String club_topic_lastPostDate) {
        this.club_topic_lastPostDate = club_topic_lastPostDate;
    }

    public List<String> getClub_topic_tag_ids() {
        return club_topic_tag_ids;
    }

    public void setClub_topic_tag_ids(List<String> club_topic_tag_ids) {
        this.club_topic_tag_ids = club_topic_tag_ids;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public int getIs_close_comment() {
        return is_close_comment;
    }

    public void setIs_close_comment(int is_close_comment) {
        this.is_close_comment = is_close_comment;
    }

    public int getClub_liveId() {
        return club_liveId;
    }

    public void setClub_liveId(int club_liveId) {
        this.club_liveId = club_liveId;
    }

    public int getClub_delete_flag() {
        return club_delete_flag;
    }

    public void setClub_delete_flag(int club_delete_flag) {
        this.club_delete_flag = club_delete_flag;
    }

    public List<String> getMulti_images() {
        return multi_images;
    }

    public void setMulti_images(List<String> multi_images) {
        this.multi_images = multi_images;
    }

    public int getTopic_reply_count() {
        return topic_reply_count;
    }

    public void setTopic_reply_count(int topic_reply_count) {
        this.topic_reply_count = topic_reply_count;
    }

    public int getClub_qa_confirmreplyid() {
        return club_qa_confirmreplyid;
    }

    public void setClub_qa_confirmreplyid(int club_qa_confirmreplyid) {
        this.club_qa_confirmreplyid = club_qa_confirmreplyid;
    }

    public int getClub_ishtml() {
        return club_ishtml;
    }

    public void setClub_ishtml(int club_ishtml) {
        this.club_ishtml = club_ishtml;
    }

    public int getClub_bbs_id() {
        return club_bbs_id;
    }

    public void setClub_bbs_id(int club_bbs_id) {
        this.club_bbs_id = club_bbs_id;
    }

    public String getClub_qa_summary() {
        return club_qa_summary;
    }

    public void setClub_qa_summary(String club_qa_summary) {
        this.club_qa_summary = club_qa_summary;
    }

    public int getBiz_id() {
        return biz_id;
    }

    public void setBiz_id(int biz_id) {
        this.biz_id = biz_id;
    }

    public String getIp_city_name() {
        return ip_city_name;
    }

    public void setIp_city_name(String ip_city_name) {
        this.ip_city_name = ip_city_name;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getLast_reply_date() {
        return last_reply_date;
    }

    public void setLast_reply_date(String last_reply_date) {
        this.last_reply_date = last_reply_date;
    }

    public String getClub_videoimgs169() {
        return club_videoimgs169;
    }

    public void setClub_videoimgs169(String club_videoimgs169) {
        this.club_videoimgs169 = club_videoimgs169;
    }

    public int getClub_qa_pool() {
        return club_qa_pool;
    }

    public void setClub_qa_pool(int club_qa_pool) {
        this.club_qa_pool = club_qa_pool;
    }

    public List<Integer> getSeries_ids() {
        return series_ids;
    }

    public void setSeries_ids(List<Integer> series_ids) {
        this.series_ids = series_ids;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public int getAuthseriesid() {
        return authseriesid;
    }

    public void setAuthseriesid(int authseriesid) {
        this.authseriesid = authseriesid;
    }

    public String getAuthseriesname() {
        return authseriesname;
    }

    public void setAuthseriesname(String authseriesname) {
        this.authseriesname = authseriesname;
    }

    public int getAuthlevel() {
        return authlevel;
    }

    public void setAuthlevel(int authlevel) {
        this.authlevel = authlevel;
    }
}
