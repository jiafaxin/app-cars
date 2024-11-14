package com.autohome.app.cars.service.components.hqpic.dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengjincheng
 * @date 2024/8/14
 */
@Data
public class HqPicPrepareData {

    /**
     * 外观颜色 id-值
     */
    Map<Integer, String> colorValueMap = new HashMap<>();

    /**
     * 外观颜色 id-名称
     */
    Map<Integer, String> colorNameMap = new HashMap<>();

    /**
     * 内饰颜色 id-值
     */
    Map<Integer, String> innerColorValueMap = new HashMap<>();

    /**
     * 内饰颜色 id-名称
     */
    Map<Integer, String> innerColorNameMap = new HashMap<>();

    /**
     * 一级分类 id-名称
     * <p>
     * 图片&视频一级分类名称和id保证一致
     * </p>
     */
    Map<Integer, String> typeNameMap = new HashMap<>();

    /**
     * 视频 二级分类 id-名称
     */
    Map<Integer, String> videoSubtypeMap = new HashMap<>();

    /**
     * 视频 一级分类 id-排序
     */
    Map<Integer, Integer> videoTypeSortMap = new HashMap<>();

    /**
     * 视频 二级分类 id-排序
     */
    Map<Integer, Integer> videoSubTypeSortMap = new HashMap<>();

    /**
     * 图片 一级分类对应的二级分类
     */
    Map<Integer, List<HqPicDataDto.PhotoSubTypeDto>> photoSubTypeMap = new HashMap<>();

}
