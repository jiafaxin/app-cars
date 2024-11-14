package com.autohome.app.cars.apiclient.maindata.dtos;

import lombok.Data;

@Data
public class HotDataResult {
    private String main_data_type;
    private String hot_data_type;
    private int biz_id;
    private int count;

    public int getCount() {
        return Math.max(count, 0);
    }
}
