package com.autohome.app.cars.service.components.recrank.dtos.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重复线索提交信息
 *
 */
@Data
@NoArgsConstructor
public class ClueRepeatResult {
    private String linurl;
    private String ext;
    private int seriesid;
    private int specid;
}
