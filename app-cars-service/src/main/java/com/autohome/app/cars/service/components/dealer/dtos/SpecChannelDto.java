package com.autohome.app.cars.service.components.dealer.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/3/6
 */
@Data
public class SpecChannelDto {

    /**
     * 渠道Id集合
     * <p>
     * 一般一个车型就一个渠道，一个车型被2个渠道售卖的少。比如比亚迪，长安才会存在这种情况
     * </p>
     */
    private List<Integer> channelIdList = new ArrayList<>();
}
