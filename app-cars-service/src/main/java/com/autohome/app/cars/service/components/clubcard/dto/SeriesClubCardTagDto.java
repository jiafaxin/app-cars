package com.autohome.app.cars.service.components.clubcard.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbs
 * @date 2024/5/31
 */
public class SeriesClubCardTagDto {

    List<ClubTab> tablist = new ArrayList<>();

    public List<ClubTab> getTablist() {
        return tablist;
    }

    public void setTablist(List<ClubTab> tablist) {
        this.tablist = tablist;
    }

    public static class ClubTab {
        private String linkurl;
        private String name;
        private int tabid;

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTabid() {
            return tabid;
        }

        public void setTabid(int tabid) {
            this.tabid = tabid;
        }
    }

    public static class Button {

        private String btnurl;
        private String text;

        public String getBtnurl() {
            return btnurl;
        }

        public void setBtnurl(String btnurl) {
            this.btnurl = btnurl;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
