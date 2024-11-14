package com.autohome.app.cars.mapper.popauto.entities;


import lombok.Data;


@Data
public class VideoOrderModel {
    /**
     * 订单任务id
     */
    private int orderId;

    /**
     * 关联任务id
     */
    private int taskId;

    /**
     * 品牌ID
     */
    private int brandId;

    /**
     * 车系ID
     */
    private int seriesId;

    /**
     * 车型ID
     */
    private int specId;

    private int itemCount;
}
