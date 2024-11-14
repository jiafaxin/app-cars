package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

/**
 * @author : zzli
 * @description : 消费补贴政策查询
 * @date : 2024/10/15 19:09
 */

@Data
public class SubsidyInfoEntity {
    /**
     * 消费补贴文本
     */
    private String subsidytext;
    private String provinceid;
    private String cityid;
    /**
     * policytype
     * -1   全国报废补贴
     * 1     新购补贴
     * 2     置换补贴
     * 3     政府报废补贴
     */
    private String policytype;
}
