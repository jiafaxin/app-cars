package com.autohome.app.cars.apiclient.clubcard.dtos;

import java.util.List;

/**
 * @author wbs
 * @date 2024/5/31
 */
public class SeriesClubCardTagResult {

    private int id;
    private int isNewEnergy;
    private List<TagInfo> tagInfoList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsNewEnergy() {
        return isNewEnergy;
    }

    public void setIsNewEnergy(int isNewEnergy) {
        this.isNewEnergy = isNewEnergy;
    }

    public List<TagInfo> getTagInfoList() {
        return tagInfoList;
    }

    public void setTagInfoList(List<TagInfo> tagInfoList) {
        this.tagInfoList = tagInfoList;
    }

    public static class TagInfo {

        private int tagId;
        private String tagName;
        private List<Integer> clubTagIds;
        private List<Integer> qaTagIds;
        private int containsKouBei;

        public int getTagId() {
            return tagId;
        }

        public void setTagId(int tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<Integer> getClubTagIds() {
            return clubTagIds;
        }

        public void setClubTagIds(List<Integer> clubTagIds) {
            this.clubTagIds = clubTagIds;
        }

        public List<Integer> getQaTagIds() {
            return qaTagIds;
        }

        public void setQaTagIds(List<Integer> qaTagIds) {
            this.qaTagIds = qaTagIds;
        }

        public int getContainsKouBei() {
            return containsKouBei;
        }

        public void setContainsKouBei(int containsKouBei) {
            this.containsKouBei = containsKouBei;
        }
    }

}
