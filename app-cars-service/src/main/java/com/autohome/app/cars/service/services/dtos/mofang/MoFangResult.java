package com.autohome.app.cars.service.services.dtos.mofang;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/16 19:52
 */
@Data
public class MoFangResult {
    private List<? extends MoFangCardModel> result;
}
