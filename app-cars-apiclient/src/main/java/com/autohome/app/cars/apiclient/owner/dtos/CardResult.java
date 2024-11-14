package com.autohome.app.cars.apiclient.owner.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CardResult {

    private List<Item> cards;

    @Data
    public static class Item{
        String title;
        String code;
        String subtitle;
        String data;
        String imgUrl;
        String appHref;
    }
}
