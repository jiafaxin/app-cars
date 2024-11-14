package com.autohome.app.cars.service.components.recrank.dtos.configdtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEnergyCountDataDto extends BaseSaleRankDataDto {
    /**
     * 是否有多种能源类型
     */
    private boolean hasManyEnergy;
    /**
     * 新能源销量
     */
    private long newEnergy;
    /**
     * 燃油车销量
     */
    private long ofv;
    /**
     * 纯电销量
     */
    private long ev;
    /**
     * 插电混动销量
     */
    private long phev;
    /**
     * 增程式销量
     */
    private long reev;
}
