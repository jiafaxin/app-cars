package com.autohome.app.cars.mapper.popauto.entities;

import java.util.List;

/**
 * @projectName: microservice
 * @package: com.autohome.maintain.model.diffconfig.v2
 * @className: DiffItem
 * @author: lvming
 * @description:
 * @date: 2022/11/17 14:38
 */
public class DiffItem {

    /**
     * 大分类名称(对应显示左上角标题)
     */
    private String title;

    /**
     *
     */
    private List<DiffItemDetail> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DiffItemDetail> getItems() {
        return items;
    }

    public void setItems(List<DiffItemDetail> items) {
        this.items = items;
    }
}
