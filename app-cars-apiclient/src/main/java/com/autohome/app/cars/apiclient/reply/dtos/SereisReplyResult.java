package com.autohome.app.cars.apiclient.reply.dtos;

import java.util.ArrayList;
import java.util.List;

public class SereisReplyResult {
    private Integer firstReplyId;
    private Boolean hasmore;
    private String hotScore;
    private Integer lastReplyId;
    private List<ListDTO> list = new ArrayList<>();
    private Integer next;

    public static class ListDTO {
        private String nlpSeries;
        private Integer replyId;
        private Integer rObjId;
        private String rReplyDate;
        private String rContent;
        private Integer rTargetReplyId;
        private Integer rTargetMemberId;
        private Integer rMemberId;
        private String rMemberName;
        private String rUserHeaderImage;
        private Integer rFloor;
        private Integer rMemberSex;
        private Integer rContentLength;
        private Integer createType;
        private String rJson;
        private String spType;
        private String replydate;
        private Object quote;
        private Integer duihua;
        private Integer rUp;
        private Integer imgId;
        private String imageUrl;
        private Integer rStatus;
        private Integer rMemberType;
        private Integer rReplyType;
        private Integer rSecObjId;
        private Integer rTag;
        private Integer chatIndex;
        private Integer chatCount;
        private Integer rDelete;
        private String mood;
        private Integer fReplyId;
        private Integer fReplyCount;
        private Object subQuoteList;
        private Boolean godReply;
        private Integer appId;
        private String location;

        public String getNlpSeries() {
            return nlpSeries;
        }

        public void setNlpSeries(String nlpSeries) {
            this.nlpSeries = nlpSeries;
        }

        public Integer getReplyId() {
            return replyId;
        }

        public void setReplyId(Integer replyId) {
            this.replyId = replyId;
        }

        public Integer getrObjId() {
            return rObjId;
        }

        public void setrObjId(Integer rObjId) {
            this.rObjId = rObjId;
        }

        public String getrReplyDate() {
            return rReplyDate;
        }

        public void setrReplyDate(String rReplyDate) {
            this.rReplyDate = rReplyDate;
        }

        public String getrContent() {
            return rContent;
        }

        public void setrContent(String rContent) {
            this.rContent = rContent;
        }

        public Integer getrTargetReplyId() {
            return rTargetReplyId;
        }

        public void setrTargetReplyId(Integer rTargetReplyId) {
            this.rTargetReplyId = rTargetReplyId;
        }

        public Integer getrTargetMemberId() {
            return rTargetMemberId;
        }

        public void setrTargetMemberId(Integer rTargetMemberId) {
            this.rTargetMemberId = rTargetMemberId;
        }

        public Integer getrMemberId() {
            return rMemberId;
        }

        public void setrMemberId(Integer rMemberId) {
            this.rMemberId = rMemberId;
        }

        public String getrMemberName() {
            return rMemberName;
        }

        public void setrMemberName(String rMemberName) {
            this.rMemberName = rMemberName;
        }

        public String getrUserHeaderImage() {
            return rUserHeaderImage;
        }

        public void setrUserHeaderImage(String rUserHeaderImage) {
            this.rUserHeaderImage = rUserHeaderImage;
        }

        public Integer getrFloor() {
            return rFloor;
        }

        public void setrFloor(Integer rFloor) {
            this.rFloor = rFloor;
        }

        public Integer getrMemberSex() {
            return rMemberSex;
        }

        public void setrMemberSex(Integer rMemberSex) {
            this.rMemberSex = rMemberSex;
        }

        public Integer getrContentLength() {
            return rContentLength;
        }

        public void setrContentLength(Integer rContentLength) {
            this.rContentLength = rContentLength;
        }

        public Integer getCreateType() {
            return createType;
        }

        public void setCreateType(Integer createType) {
            this.createType = createType;
        }

        public String getrJson() {
            return rJson;
        }

        public void setrJson(String rJson) {
            this.rJson = rJson;
        }

        public String getSpType() {
            return spType;
        }

        public void setSpType(String spType) {
            this.spType = spType;
        }

        public String getReplydate() {
            return replydate;
        }

        public void setReplydate(String replydate) {
            this.replydate = replydate;
        }

        public Object getQuote() {
            return quote;
        }

        public void setQuote(Object quote) {
            this.quote = quote;
        }

        public Integer getDuihua() {
            return duihua;
        }

        public void setDuihua(Integer duihua) {
            this.duihua = duihua;
        }

        public Integer getrUp() {
            return rUp;
        }

        public void setrUp(Integer rUp) {
            this.rUp = rUp;
        }

        public Integer getImgId() {
            return imgId;
        }

        public void setImgId(Integer imgId) {
            this.imgId = imgId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getrStatus() {
            return rStatus;
        }

        public void setrStatus(Integer rStatus) {
            this.rStatus = rStatus;
        }

        public Integer getrMemberType() {
            return rMemberType;
        }

        public void setrMemberType(Integer rMemberType) {
            this.rMemberType = rMemberType;
        }

        public Integer getrReplyType() {
            return rReplyType;
        }

        public void setrReplyType(Integer rReplyType) {
            this.rReplyType = rReplyType;
        }

        public Integer getrSecObjId() {
            return rSecObjId;
        }

        public void setrSecObjId(Integer rSecObjId) {
            this.rSecObjId = rSecObjId;
        }

        public Integer getrTag() {
            return rTag;
        }

        public void setrTag(Integer rTag) {
            this.rTag = rTag;
        }

        public Integer getChatIndex() {
            return chatIndex;
        }

        public void setChatIndex(Integer chatIndex) {
            this.chatIndex = chatIndex;
        }

        public Integer getChatCount() {
            return chatCount;
        }

        public void setChatCount(Integer chatCount) {
            this.chatCount = chatCount;
        }

        public Integer getrDelete() {
            return rDelete;
        }

        public void setrDelete(Integer rDelete) {
            this.rDelete = rDelete;
        }

        public String getMood() {
            return mood;
        }

        public void setMood(String mood) {
            this.mood = mood;
        }

        public Integer getfReplyId() {
            return fReplyId;
        }

        public void setfReplyId(Integer fReplyId) {
            this.fReplyId = fReplyId;
        }

        public Integer getfReplyCount() {
            return fReplyCount;
        }

        public void setfReplyCount(Integer fReplyCount) {
            this.fReplyCount = fReplyCount;
        }

        public Object getSubQuoteList() {
            return subQuoteList;
        }

        public void setSubQuoteList(Object subQuoteList) {
            this.subQuoteList = subQuoteList;
        }

        public Boolean getGodReply() {
            return godReply;
        }

        public void setGodReply(Boolean godReply) {
            this.godReply = godReply;
        }

        public Integer getAppId() {
            return appId;
        }

        public void setAppId(Integer appId) {
            this.appId = appId;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public Integer getFirstReplyId() {
        return firstReplyId;
    }

    public void setFirstReplyId(Integer firstReplyId) {
        this.firstReplyId = firstReplyId;
    }

    public Boolean getHasmore() {
        return hasmore;
    }

    public void setHasmore(Boolean hasmore) {
        this.hasmore = hasmore;
    }

    public String getHotScore() {
        return hotScore;
    }

    public void setHotScore(String hotScore) {
        this.hotScore = hotScore;
    }

    public Integer getLastReplyId() {
        return lastReplyId;
    }

    public void setLastReplyId(Integer lastReplyId) {
        this.lastReplyId = lastReplyId;
    }

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }
}
