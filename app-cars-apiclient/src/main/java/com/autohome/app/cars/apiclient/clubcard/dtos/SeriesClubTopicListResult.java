package com.autohome.app.cars.apiclient.clubcard.dtos;

import java.util.List;

/**
 * @author wbs
 * @date 2024/5/31
 */
public class SeriesClubTopicListResult {


    private long total;
    private List<Items> items;
    private int seriesid;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public int getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
    }

    public static class Items {

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

        public void setClub_topic_piccount(int club_topic_piccount) {
            this.club_topic_piccount = club_topic_piccount;
        }

        public int getClub_topic_piccount() {
            return club_topic_piccount;
        }

        public void setClub_video_title(String club_video_title) {
            this.club_video_title = club_video_title;
        }

        public String getClub_video_title() {
            return club_video_title;
        }

        public void setPv(int pv) {
            this.pv = pv;
        }

        public int getPv() {
            return pv;
        }

        public void setClub_bbs_type(String club_bbs_type) {
            this.club_bbs_type = club_bbs_type;
        }

        public String getClub_bbs_type() {
            return club_bbs_type;
        }

        public void setClub_refine(int club_refine) {
            this.club_refine = club_refine;
        }

        public int getClub_refine() {
            return club_refine;
        }

        public void setVote_id(int vote_id) {
            this.vote_id = vote_id;
        }

        public int getVote_id() {
            return vote_id;
        }

        public void setClub_is_class_quality(int club_is_class_quality) {
            this.club_is_class_quality = club_is_class_quality;
        }

        public int getClub_is_class_quality() {
            return club_is_class_quality;
        }

        public void setClub_jingxuan_imgs(String club_jingxuan_imgs) {
            this.club_jingxuan_imgs = club_jingxuan_imgs;
        }

        public String getClub_jingxuan_imgs() {
            return club_jingxuan_imgs;
        }

        public void setClub_is_pool(int club_is_pool) {
            this.club_is_pool = club_is_pool;
        }

        public int getClub_is_pool() {
            return club_is_pool;
        }


        public void setIp_province_name(String ip_province_name) {
            this.ip_province_name = ip_province_name;
        }

        public String getIp_province_name() {
            return ip_province_name;
        }

        public void setIp_city_code(String ip_city_code) {
            this.ip_city_code = ip_city_code;
        }

        public String getIp_city_code() {
            return ip_city_code;
        }

        public void setPc_url(String pc_url) {
            this.pc_url = pc_url;
        }

        public String getPc_url() {
            return pc_url;
        }

        public void setClub_topic_isPicture(int club_topic_isPicture) {
            this.club_topic_isPicture = club_topic_isPicture;
        }

        public int getClub_topic_isPicture() {
            return club_topic_isPicture;
        }


        public void setClub_qa_ishigh(int club_qa_ishigh) {
            this.club_qa_ishigh = club_qa_ishigh;
        }

        public int getClub_qa_ishigh() {
            return club_qa_ishigh;
        }

        public void setClub_ip(String club_ip) {
            this.club_ip = club_ip;
        }

        public String getClub_ip() {
            return club_ip;
        }

        public void setClub_topic_wordcount(int club_topic_wordcount) {
            this.club_topic_wordcount = club_topic_wordcount;
        }

        public int getClub_topic_wordcount() {
            return club_topic_wordcount;
        }

        public void setClub_video_explain(String club_video_explain) {
            this.club_video_explain = club_video_explain;
        }

        public String getClub_video_explain() {
            return club_video_explain;
        }

        public void setReward_coin(int reward_coin) {
            this.reward_coin = reward_coin;
        }

        public int getReward_coin() {
            return reward_coin;
        }

        public void setClub_bbs_name(String club_bbs_name) {
            this.club_bbs_name = club_bbs_name;
        }

        public String getClub_bbs_name() {
            return club_bbs_name;
        }

        public void setGlobal_id(long global_id) {
            this.global_id = global_id;
        }

        public long getGlobal_id() {
            return global_id;
        }

        public void setReply_count(int reply_count) {
            this.reply_count = reply_count;
        }

        public int getReply_count() {
            return reply_count;
        }

        public void setM_url(String m_url) {
            this.m_url = m_url;
        }

        public String getM_url() {
            return m_url;
        }

        public void setClub_jinghua_imgs169(String club_jinghua_imgs169) {
            this.club_jinghua_imgs169 = club_jinghua_imgs169;
        }

        public String getClub_jinghua_imgs169() {
            return club_jinghua_imgs169;
        }

        public void setIp_province_code(String ip_province_code) {
            this.ip_province_code = ip_province_code;
        }

        public String getIp_province_code() {
            return ip_province_code;
        }

        public void setClub_is_video(int club_is_video) {
            this.club_is_video = club_is_video;
        }

        public int getClub_is_video() {
            return club_is_video;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setClub_is_poll(int club_is_poll) {
            this.club_is_poll = club_is_poll;
        }

        public int getClub_is_poll() {
            return club_is_poll;
        }

        public void setClub_jinghua_summary(String club_jinghua_summary) {
            this.club_jinghua_summary = club_jinghua_summary;
        }

        public String getClub_jinghua_summary() {
            return club_jinghua_summary;
        }

        public int getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(int author_id) {
            this.author_id = author_id;
        }

        public void setHot_score_temp(double hot_score_temp) {
            this.hot_score_temp = hot_score_temp;
        }

        public double getHot_score_temp() {
            return hot_score_temp;
        }

        public void setClub_jinghua_imgs(String club_jinghua_imgs) {
            this.club_jinghua_imgs = club_jinghua_imgs;
        }

        public String getClub_jinghua_imgs() {
            return club_jinghua_imgs;
        }

        public void setImgList(String imgList) {
            this.imgList = imgList;
        }

        public String getImgList() {
            return imgList;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setClub_is_solve(int club_is_solve) {
            this.club_is_solve = club_is_solve;
        }

        public int getClub_is_solve() {
            return club_is_solve;
        }

        public void setHot_score(double hot_score) {
            this.hot_score = hot_score;
        }

        public double getHot_score() {
            return hot_score;
        }

        public void setClub_liveCover(String club_liveCover) {
            this.club_liveCover = club_liveCover;
        }

        public String getClub_liveCover() {
            return club_liveCover;
        }

        public void setClub_videoimgs(String club_videoimgs) {
            this.club_videoimgs = club_videoimgs;
        }

        public String getClub_videoimgs() {
            return club_videoimgs;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setClub_topicimgs(List<String> club_topicimgs) {
            this.club_topicimgs = club_topicimgs;
        }

        public List<String> getClub_topicimgs() {
            return club_topicimgs;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public void setClub_is_jingxuan(int club_is_jingxuan) {
            this.club_is_jingxuan = club_is_jingxuan;
        }

        public int getClub_is_jingxuan() {
            return club_is_jingxuan;
        }

        public void setTopic_reply_count_all(int topic_reply_count_all) {
            this.topic_reply_count_all = topic_reply_count_all;
        }

        public int getTopic_reply_count_all() {
            return topic_reply_count_all;
        }

        public void setClub_topic_tag_names(List<String> club_topic_tag_names) {
            this.club_topic_tag_names = club_topic_tag_names;
        }

        public List<String> getClub_topic_tag_names() {
            return club_topic_tag_names;
        }

        public void setClub_video_quality(String club_video_quality) {
            this.club_video_quality = club_video_quality;
        }

        public String getClub_video_quality() {
            return club_video_quality;
        }

        public void setTopictype(String topictype) {
            this.topictype = topictype;
        }

        public String getTopictype() {
            return topictype;
        }

        public void setVideo_source(String video_source) {
            this.video_source = video_source;
        }

        public String getVideo_source() {
            return video_source;
        }


        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getSummary() {
            return summary;
        }

        public void setApp_url(String app_url) {
            this.app_url = app_url;
        }

        public String getApp_url() {
            return app_url;
        }

        public void setMain_data_type(String main_data_type) {
            this.main_data_type = main_data_type;
        }

        public String getMain_data_type() {
            return main_data_type;
        }

        public void setHot_score_all_bbs(double hot_score_all_bbs) {
            this.hot_score_all_bbs = hot_score_all_bbs;
        }

        public double getHot_score_all_bbs() {
            return hot_score_all_bbs;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getClub_jingxuan_publishtime() {
            return club_jingxuan_publishtime;
        }

        public void setClub_jingxuan_publishtime(String club_jingxuan_publishtime) {
            this.club_jingxuan_publishtime = club_jingxuan_publishtime;
        }

        public String getClub_video_publishtime() {
            return club_video_publishtime;
        }

        public void setClub_video_publishtime(String club_video_publishtime) {
            this.club_video_publishtime = club_video_publishtime;
        }

        public String getClub_topic_lastPostDate() {
            return club_topic_lastPostDate;
        }

        public void setClub_topic_lastPostDate(String club_topic_lastPostDate) {
            this.club_topic_lastPostDate = club_topic_lastPostDate;
        }

        public void setClub_topic_tag_ids(List<String> club_topic_tag_ids) {
            this.club_topic_tag_ids = club_topic_tag_ids;
        }

        public List<String> getClub_topic_tag_ids() {
            return club_topic_tag_ids;
        }

        public void setIs_delete(int is_delete) {
            this.is_delete = is_delete;
        }

        public int getIs_delete() {
            return is_delete;
        }

        public void setIs_close_comment(int is_close_comment) {
            this.is_close_comment = is_close_comment;
        }

        public int getIs_close_comment() {
            return is_close_comment;
        }

        public void setClub_liveId(int club_liveId) {
            this.club_liveId = club_liveId;
        }

        public int getClub_liveId() {
            return club_liveId;
        }

        public void setClub_delete_flag(int club_delete_flag) {
            this.club_delete_flag = club_delete_flag;
        }

        public int getClub_delete_flag() {
            return club_delete_flag;
        }

        public void setMulti_images(List<String> multi_images) {
            this.multi_images = multi_images;
        }

        public List<String> getMulti_images() {
            return multi_images;
        }

        public void setTopic_reply_count(int topic_reply_count) {
            this.topic_reply_count = topic_reply_count;
        }

        public int getTopic_reply_count() {
            return topic_reply_count;
        }

        public void setClub_qa_confirmreplyid(int club_qa_confirmreplyid) {
            this.club_qa_confirmreplyid = club_qa_confirmreplyid;
        }

        public int getClub_qa_confirmreplyid() {
            return club_qa_confirmreplyid;
        }

        public void setClub_ishtml(int club_ishtml) {
            this.club_ishtml = club_ishtml;
        }

        public int getClub_ishtml() {
            return club_ishtml;
        }

        public void setClub_bbs_id(int club_bbs_id) {
            this.club_bbs_id = club_bbs_id;
        }

        public int getClub_bbs_id() {
            return club_bbs_id;
        }

        public void setClub_qa_summary(String club_qa_summary) {
            this.club_qa_summary = club_qa_summary;
        }

        public String getClub_qa_summary() {
            return club_qa_summary;
        }

        public int getBiz_id() {
            return biz_id;
        }

        public void setBiz_id(int biz_id) {
            this.biz_id = biz_id;
        }

        public void setIp_city_name(String ip_city_name) {
            this.ip_city_name = ip_city_name;
        }

        public String getIp_city_name() {
            return ip_city_name;
        }

    }

}
