package com.autohome.app.cars.apiclient.maindata.dtos;

import lombok.Data;

@Data
public class SFastNewsParagraphItem {

    /**
     * 段落中图片url
     */
    private String PImage;

    /**
     * 段落中文本
     */
    private String PText;

    /**
     * 段落html
     */
    private String PHtml;

    /**
     * 段落类型  1 文本   2  图片
     */
    private Integer PType;
}
