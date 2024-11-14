package com.autohome.app.cars.service.components.clubcard.dto;

/**
 * @author wbs
 * @date 2024/6/12
 */
public class ReplyAndLikeDto {
    private int bizId;
    private int replyCount;
    private int likeCount;

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
