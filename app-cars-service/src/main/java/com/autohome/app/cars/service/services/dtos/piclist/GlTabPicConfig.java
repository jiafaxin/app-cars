package com.autohome.app.cars.service.services.dtos.piclist;

import java.util.List;

public class GlTabPicConfig {
    private int tabId;

    private String tabName;

    private List<Integer> tabPicList;

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public List<Integer> getTabPicList() {
        return tabPicList;
    }

    public void setTabPicList(List<Integer> tabPicList) {
        this.tabPicList = tabPicList;
    }
}
