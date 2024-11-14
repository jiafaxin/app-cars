package com.autohome.app.cars.service.components.car.dtos;

import lombok.Data;

@Data
public class MustSeeItemDto {

    private String itemtype;        //参配分类名称
    private String paramitemname;   //参配项名称

    public MustSeeItemDto(String itemtype, String paramitemname){
        this.itemtype = itemtype;
        this.paramitemname = paramitemname;
    }

}
