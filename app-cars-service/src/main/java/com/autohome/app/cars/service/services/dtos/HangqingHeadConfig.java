package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

/**
 * Created by dx on 2024/6/27
 * 车辆行情头部信息配置项实体
 */
@Data
public class HangqingHeadConfig {
    private String bgpic;//头部的背景图片
    private String bgcolor;//头部图片为空，背景色值
    private String titlepic;//文案图片
    private String subtitle;//副标题
}
