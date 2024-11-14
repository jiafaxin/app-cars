package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/4/26
 */
@Data
public class SpecChannelReq {

    /**
     * 渠道id
     */
    private Integer channelId;

    /**
     * 渠道id的集合 ,Long
     */
    private List<Integer> channelIdList;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 业务员工号id的集合 ,String
     */
    private List<String> clerkIdList;

    /**
     * 页码
     */
    private Integer pageIndex;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;
}
