package com.autohome.app.cars.service.components.misc.dtos;

import java.util.Date;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/7/19
 */
public class SeriesCityHotNewsDto {

    private String icon;

    private int pageCardDataId;

    private String linkUrl;

    private String sort;

    private int position;

    private String title;

    private int seriesId;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getPageCardDataId() {
        return pageCardDataId;
    }

    public void setPageCardDataId(int pageCardDataId) {
        this.pageCardDataId = pageCardDataId;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }
}
