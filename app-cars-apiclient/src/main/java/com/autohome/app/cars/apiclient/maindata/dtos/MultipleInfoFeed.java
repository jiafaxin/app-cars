package com.autohome.app.cars.apiclient.maindata.dtos;

import lombok.Data;

@Data
public class MultipleInfoFeed extends MainDataFeedBase{
    private String[] multi_images;
}
