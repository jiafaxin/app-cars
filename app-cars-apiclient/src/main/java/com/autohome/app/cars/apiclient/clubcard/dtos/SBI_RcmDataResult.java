package com.autohome.app.cars.apiclient.clubcard.dtos;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public class SBI_RcmDataResult {
    private String bsdata;
    private Integer number;
    private String requestid;
    private SBI_RcmData_Body result;

    public String getBsdata() {
        return bsdata;
    }

    public void setBsdata(String bsdata) {
        this.bsdata = bsdata;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public SBI_RcmData_Body getResult() {
        return result;
    }

    public void setResult(SBI_RcmData_Body result) {
        this.result = result;
    }

    public static class SBI_RcmData_Body{
        private Boolean isbreake;
        private List<SBI_RcmData_Item> toplist;
        private List<SBI_RcmData_Item> itemlist;

        public Boolean getIsbreake() {
            return isbreake;
        }

        public void setIsbreake(Boolean isbreake) {
            this.isbreake = isbreake;
        }

        public List<SBI_RcmData_Item> getItemlist() {
            return itemlist;
        }

        public void setItemlist(List<SBI_RcmData_Item> itemlist) {
            this.itemlist = itemlist;
        }

        public List<SBI_RcmData_Item> getToplist() {
            return toplist;
        }

        public void setToplist(List<SBI_RcmData_Item> toplist) {
            this.toplist = toplist;
        }
    }

    public static class SBI_RcmData_Item {
        //自定义字段，非源接口字段
        private Integer pm;
        private String appversion;
        private String pvid;
        private Integer devicetype;
        private boolean top;
        /** 关注关系标识 */
        private String focus_tag;
        private JSONObject extJsonObject;

        private SBI_RcmData_Item_ResourceObj resourceobj;
        private SBI_RcmData_Item_PvObj pvobj;
        private String itemext;
        private SBI_RcmDataResult_ItemExt itemextmodel;

        public SBI_RcmData_Item_ResourceObj getResourceobj() {
            return resourceobj;
        }

        public void setResourceobj(SBI_RcmData_Item_ResourceObj resourceobj) {
            this.resourceobj = resourceobj;
        }

        public SBI_RcmData_Item_PvObj getPvobj() {
            return pvobj;
        }

        public void setPvobj(SBI_RcmData_Item_PvObj pvobj) {
            this.pvobj = pvobj;
        }

        public String getItemext() {
            return itemext;
        }

        public void setItemext(String itemext) {
            this.itemext = itemext;
        }

        public Integer getPm() {
            return pm;
        }

        public void setPm(Integer pm) {
            this.pm = pm;
        }

        public String getAppversion() {
            return appversion;
        }

        public void setAppversion(String appversion) {
            this.appversion = appversion;
        }

        public String getPvid() {
            return pvid;
        }

        public void setPvid(String pvid) {
            this.pvid = pvid;
        }

        public void setItemextmodel(SBI_RcmDataResult_ItemExt itemextmodel) {
            this.itemextmodel = itemextmodel;
        }

        public SBI_RcmDataResult_ItemExt getItemextmodel() {
            return this.itemextmodel;
        }

        public Integer getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(Integer devicetype) {
            this.devicetype = devicetype;
        }

        public boolean isTop() {
            return top;
        }

        public void setTop(boolean top) {
            this.top = top;
        }

        public String getFocus_tag() {
            return focus_tag;
        }

        public void setFocus_tag(String focus_tag) {
            this.focus_tag = focus_tag;
        }

        public JSONObject getExtJsonObject() {
            return extJsonObject;
        }

        public void setExtJsonObject(JSONObject extJsonObject) {
            this.extJsonObject = extJsonObject;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj{
        private Long biz_id;
        private Integer biz_type;
        private SBI_RcmData_Item_ResourceObj_Hot hot;
        private SBI_RcmData_Item_ResourceObj_Show show;
        private SBI_RcmData_Item_ResourceObj_Other other;
        private SBI_RcmData_Item_ResourceObj_Base base;

        private SBI_RcmData_Item_ResourceObj_Big big;
        private SBI_RcmData_Item_ResourceObj_Nlp nlp;

        public Long getBiz_id() {
            return biz_id;
        }

        public void setBiz_id(Long biz_id) {
            this.biz_id = biz_id;
        }

        public Integer getBiz_type() {
            return biz_type;
        }

        public void setBiz_type(Integer biz_type) {
            this.biz_type = biz_type;
        }

        public SBI_RcmData_Item_ResourceObj_Hot getHot() {
            return hot;
        }

        public void setHot(SBI_RcmData_Item_ResourceObj_Hot hot) {
            this.hot = hot;
        }

        public SBI_RcmData_Item_ResourceObj_Show getShow() {
            return show;
        }

        public void setShow(SBI_RcmData_Item_ResourceObj_Show show) {
            this.show = show;
        }

        public SBI_RcmData_Item_ResourceObj_Other getOther() {
            return other;
        }

        public void setOther(SBI_RcmData_Item_ResourceObj_Other other) {
            this.other = other;
        }

        public SBI_RcmData_Item_ResourceObj_Base getBase() {
            return base;
        }

        public void setBase(SBI_RcmData_Item_ResourceObj_Base base) {
            this.base = base;
        }

        public SBI_RcmData_Item_ResourceObj_Big getBig() {
            return big;
        }

        public void setBig(SBI_RcmData_Item_ResourceObj_Big big) {
            this.big = big;
        }

        public SBI_RcmData_Item_ResourceObj_Nlp getNlp() {
            return nlp;
        }

        public void setNlp(SBI_RcmData_Item_ResourceObj_Nlp nlp) {
            this.nlp = nlp;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj_Hot{
        private String item_key;
        private Integer view;
        private Integer play;
        private Integer reply;
        private Integer like;
        private Integer topic_reply;

        public String getItem_key() {
            return item_key;
        }

        public void setItem_key(String item_key) {
            this.item_key = item_key;
        }

        public Integer getView() {
            return view;
        }

        public void setView(Integer view) {
            this.view = view;
        }

        public Integer getPlay() {
            return play;
        }

        public void setPlay(Integer play) {
            this.play = play;
        }

        public Integer getReply() {
            return reply;
        }

        public void setReply(Integer reply) {
            this.reply = reply;
        }

        public Integer getLike() {
            return like;
        }

        public void setLike(Integer like) {
            this.like = like;
        }

        public Integer getTopic_reply() {
            return topic_reply;
        }

        public void setTopic_reply(Integer topic_reply) {
            this.topic_reply = topic_reply;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj_Show{
        private String title;
        private String author;
        private String author_id;
        private String author_icon;
        private List<String> cms_tags;
        private String img_url;
        private String img_url2;
        private String img_url3;
        private String img_url4;
        private String jump_url;
        private String publish_time;
        private String item_key;
        private String index_detail;
        private List<String> graphic_img_list;
        private List<String> graphic_img_list2;
        private List<String> graphic_img_list3;
        private String answerer;

        private String video_url;

        private String merge_jump;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getAuthor_icon() {
            return author_icon;
        }

        public void setAuthor_icon(String author_icon) {
            this.author_icon = author_icon;
        }

        public List<String> getCms_tags() {
            return cms_tags;
        }

        public void setCms_tags(List<String> cms_tags) {
            this.cms_tags = cms_tags;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getImg_url2() {
            return img_url2;
        }

        public void setImg_url2(String img_url2) {
            this.img_url2 = img_url2;
        }

        public String getImg_url3() {
            return img_url3;
        }

        public void setImg_url3(String img_url3) {
            this.img_url3 = img_url3;
        }

        public String getImg_url4() {
            return img_url4;
        }

        public void setImg_url4(String img_url4) {
            this.img_url4 = img_url4;
        }

        public String getJump_url() {
            return jump_url;
        }

        public void setJump_url(String jump_url) {
            this.jump_url = jump_url;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getItem_key() {
            return item_key;
        }

        public void setItem_key(String item_key) {
            this.item_key = item_key;
        }

        public String getIndex_detail() {
            return index_detail;
        }

        public void setIndex_detail(String index_detail) {
            this.index_detail = index_detail;
        }

        public List<String> getGraphic_img_list() {
            return graphic_img_list;
        }

        public void setGraphic_img_list(List<String> graphic_img_list) {
            this.graphic_img_list = graphic_img_list;
        }

        public List<String> getGraphic_img_list2() {
            return graphic_img_list2;
        }

        public void setGraphic_img_list2(List<String> graphic_img_list2) {
            this.graphic_img_list2 = graphic_img_list2;
        }

        public List<String> getGraphic_img_list3() {
            return graphic_img_list3;
        }

        public void setGraphic_img_list3(List<String> graphic_img_list3) {
            this.graphic_img_list3 = graphic_img_list3;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public String getMerge_jump() {
            return merge_jump;
        }

        public void setMerge_jump(String merge_jump) {
            this.merge_jump = merge_jump;
        }

        public String getAnswerer() {
            return answerer;
        }

        public void setAnswerer(String answerer) {
            this.answerer = answerer;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj_Other{
        private String duration;
        private String object_uid;
        private String index_detail;
        private Integer kind;
        private Integer subject_id;
        private Integer refine;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getObject_uid() {
            return object_uid;
        }

        public void setObject_uid(String object_uid) {
            this.object_uid = object_uid;
        }

        public String getIndex_detail() {
            return index_detail;
        }

        public void setIndex_detail(String index_detail) {
            this.index_detail = index_detail;
        }

        public Integer getKind() {
            return kind;
        }

        public void setKind(Integer kind) {
            this.kind = kind;
        }

        public Integer getSubject_id() {
            return subject_id;
        }

        public void setSubject_id(Integer subject_id) {
            this.subject_id = subject_id;
        }

        public Integer getRefine() {
            return refine;
        }

        public void setRefine(Integer refine) {
            this.refine = refine;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj_Base{
        private String recommend_time;
        private String update_at;
        private String create_time;
        private String modify_time;
        private List<String> cms_series_ids;
        private List<String> car_brand_ids;
        private List<String> cms_spec_ids;
        private String cms_content_class;
        private Integer page_index;
        private String show_big_img;
        private String item_key;
        private String is_close_comment;
        private Integer live_line;
        private Integer modetype;
        private Integer is_yingxiao;
        private String media_type;

        private String v_id;
        private String author_role;
        private String video_width;
        private String video_height;

        private String summary;

        /** 论坛用户ID。注：原创作者的之家用户ID，貌似存这个字段。。。。 */
        private Integer club_member_id;

        public String getUpdate_at() {
            return update_at;
        }

        public void setUpdate_at(String update_at) {
            this.update_at = update_at;
        }

        public String getRecommend_time() {
            return recommend_time;
        }

        public void setRecommend_time(String recommend_time) {
            this.recommend_time = recommend_time;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getModify_time() {
            return modify_time;
        }

        public void setModify_time(String modify_time) {
            this.modify_time = modify_time;
        }

        public List<String> getCms_series_ids() {
            return cms_series_ids;
        }

        public void setCms_series_ids(List<String> cms_series_ids) {
            this.cms_series_ids = cms_series_ids;
        }

        public List<String> getCar_brand_ids() {
            return car_brand_ids;
        }

        public void setCar_brand_ids(List<String> car_brand_ids) {
            this.car_brand_ids = car_brand_ids;
        }

        public List<String> getCms_spec_ids() {
            return cms_spec_ids;
        }

        public void setCms_spec_ids(List<String> cms_spec_ids) {
            this.cms_spec_ids = cms_spec_ids;
        }

        public String getCms_content_class() {
            return cms_content_class;
        }

        public void setCms_content_class(String cms_content_class) {
            this.cms_content_class = cms_content_class;
        }

        public Integer getPage_index() {
            return page_index;
        }

        public void setPage_index(Integer page_index) {
            this.page_index = page_index;
        }

        public String getShow_big_img() {
            return show_big_img;
        }

        public void setShow_big_img(String show_big_img) {
            this.show_big_img = show_big_img;
        }

        public String getItem_key() {
            return item_key;
        }

        public void setItem_key(String item_key) {
            this.item_key = item_key;
        }

        public String getIs_close_comment() {
            return is_close_comment;
        }

        public void setIs_close_comment(String is_close_comment) {
            this.is_close_comment = is_close_comment;
        }

        public Integer getLive_line() {
            return live_line;
        }

        public void setLive_line(Integer live_line) {
            this.live_line = live_line;
        }

        public Integer getModetype() {
            return modetype;
        }

        public void setModetype(Integer modetype) {
            this.modetype = modetype;
        }

        public Integer getIs_yingxiao() {
            return is_yingxiao;
        }

        public void setIs_yingxiao(Integer is_yingxiao) {
            this.is_yingxiao = is_yingxiao;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getV_id() {
            return v_id;
        }

        public void setV_id(String v_id) {
            this.v_id = v_id;
        }

        public String getAuthor_role() {
            return author_role;
        }

        public void setAuthor_role(String author_role) {
            this.author_role = author_role;
        }

        public String getVideo_width() {
            return video_width;
        }

        public void setVideo_width(String video_width) {
            this.video_width = video_width;
        }

        public String getVideo_height() {
            return video_height;
        }

        public void setVideo_height(String video_height) {
            this.video_height = video_height;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public Integer getClub_member_id() {
            return club_member_id;
        }

        public void setClub_member_id(Integer club_member_id) {
            this.club_member_id = club_member_id;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj_Big {

        private String ext_json;
        private String content;

        public String getExt_json() {
            return ext_json;
        }

        public void setExt_json(String ext_json) {
            this.ext_json = ext_json;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class SBI_RcmData_Item_PvObj{
        private String canditypestring;

        private String pvinfo;

        private String ext;

        public String getCanditypestring() {
            return canditypestring;
        }

        public void setCanditypestring(String canditypestring) {
            this.canditypestring = canditypestring;
        }

        public String getPvinfo() {
            return pvinfo;
        }

        public void setPvinfo(String pvinfo) {
            this.pvinfo = pvinfo;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }
    }

    public static class SBI_RcmData_Item_ResourceObj_Nlp {

        private String uniq_series_id;

        private String uniq_series_name;

        public String getUniq_series_id() {
            return uniq_series_id;
        }

        public void setUniq_series_id(String uniq_series_id) {
            this.uniq_series_id = uniq_series_id;
        }

        public String getUniq_series_name() {
            return uniq_series_name;
        }

        public void setUniq_series_name(String uniq_series_name) {
            this.uniq_series_name = uniq_series_name;
        }
    }

    public static class SBI_RcmDataResult_ItemExt {
        private Integer video_height;
        private Integer video_width;
        private String video_id;
        private List<Integer> author_roles;
        private String imgurl5;
        private String share_url;
        private String summary;
        private String live_albumname;
        private String info_obj;
        private String geo_point;
        private String series_names;
        private Integer repacket;
        private String repacketjason;
        private String groupvsid;
        private String groupvideo;
        private Long mdbid;
        private String query;
        private Integer jumptype;

        private BigimgModeItem bigimgmode;

        private List<SBI_RcmDataResult_ItemExt_Pics> imagelist;

        private String rn_url;

        public Integer getVideo_height() {
            return video_height;
        }

        public void setVideo_height(Integer video_height) {
            this.video_height = video_height;
        }

        public Integer getVideo_width() {
            return video_width;
        }

        public void setVideo_width(Integer video_width) {
            this.video_width = video_width;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public List<Integer> getAuthor_roles() {
            return author_roles;
        }

        public void setAuthor_roles(List<Integer> author_roles) {
            this.author_roles = author_roles;
        }

        public String getImgurl5() {
            return imgurl5;
        }

        public void setImgurl5(String imgurl5) {
            this.imgurl5 = imgurl5;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getLive_albumname() {
            return live_albumname;
        }

        public void setLive_albumname(String live_albumname) {
            this.live_albumname = live_albumname;
        }

        public String getInfo_obj() {
            return info_obj;
        }

        public void setInfo_obj(String info_obj) {
            this.info_obj = info_obj;
        }

        public String getGeo_point() {
            return geo_point;
        }

        public void setGeo_point(String geo_point) {
            this.geo_point = geo_point;
        }

        public String getSeries_names() {
            return series_names;
        }

        public void setSeries_names(String series_names) {
            this.series_names = series_names;
        }

        public Integer getRepacket() {
            return repacket;
        }

        public void setRepacket(Integer repacket) {
            this.repacket = repacket;
        }

        public String getRepacketjason() {
            return repacketjason;
        }

        public void setRepacketjason(String repacketjason) {
            this.repacketjason = repacketjason;
        }

        public String getGroupvsid() {
            return groupvsid;
        }

        public void setGroupvsid(String groupvsid) {
            this.groupvsid = groupvsid;
        }

        public String getGroupvideo() {
            return groupvideo;
        }

        public void setGroupvideo(String groupvideo) {
            this.groupvideo = groupvideo;
        }

        public Long getMdbid() {
            return mdbid;
        }

        public void setMdbid(Long mdbid) {
            this.mdbid = mdbid;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public BigimgModeItem getBigimgmode() {
            return bigimgmode;
        }

        public void setBigimgmode(BigimgModeItem bigimgmode) {
            this.bigimgmode = bigimgmode;
        }

        public List<SBI_RcmDataResult_ItemExt_Pics> getImagelist() {
            return imagelist;
        }

        public void setImagelist(List<SBI_RcmDataResult_ItemExt_Pics> imagelist) {
            this.imagelist = imagelist;
        }

        public Integer getJumptype() {
            return jumptype;
        }

        public void setJumptype(Integer jumptype) {
            this.jumptype = jumptype;
        }

        public String getRn_url() {
            return rn_url;
        }

        public void setRn_url(String rn_url) {
            this.rn_url = rn_url;
        }

        public static class BigimgModeItem{
            private Integer cardtype;
            private String cardextendattr;
            private String cardextendimg;

            public Integer getCardtype() {
                return cardtype;
            }

            public void setCardtype(Integer cardtype) {
                this.cardtype = cardtype;
            }

            public String getCardextendattr() {
                return cardextendattr;
            }

            public void setCardextendattr(String cardextendattr) {
                this.cardextendattr = cardextendattr;
            }

            public String getCardextendimg() {
                return cardextendimg;
            }

            public void setCardextendimg(String cardextendimg) {
                this.cardextendimg = cardextendimg;
            }
        }

        public static class SBI_RcmDataResult_ItemExt_Pics {
            private int seriesid;
            private int specid;
            private int picid;
            private String imgurl;
            private int pictypeid;

            public int getSeriesid() {
                return seriesid;
            }

            public void setSeriesid(int seriesid) {
                this.seriesid = seriesid;
            }

            public int getSpecid() {
                return specid;
            }

            public void setSpecid(int specid) {
                this.specid = specid;
            }

            public int getPicid() {
                return picid;
            }

            public void setPicid(int picid) {
                this.picid = picid;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }

            public int getPictypeid() {
                return pictypeid;
            }

            public void setPictypeid(int pictypeid) {
                this.pictypeid = pictypeid;
            }
        }
    }
}
