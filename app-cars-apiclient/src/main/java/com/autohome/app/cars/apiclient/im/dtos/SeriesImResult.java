package com.autohome.app.cars.apiclient.im.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SeriesImResult {

    private Item generic;
    private List<Item> list;

    @Data
    public static class Item{
        int targetId;
        int targetType;
        String targetName;
        int memberCount;
        int cityId;
        int chatId;
        String ryRoomId;
    }
}
