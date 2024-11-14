package com.autohome.app.cars.service.components.cms.dtos;

import lombok.Data;

@Data
public class AHEvaluateDto {

    Item miludata;
    Item jiasu0100;


    @Data
    public static class Item{
        String name;
        Double data;
    }

}
