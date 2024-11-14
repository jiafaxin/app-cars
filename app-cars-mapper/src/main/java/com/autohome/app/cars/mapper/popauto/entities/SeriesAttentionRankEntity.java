package com.autohome.app.cars.mapper.popauto.entities;

import lombok.Data;

/**
 * Created by dx on 2024/6/20
 * 车系关注度db实体
 */
@Data
public class SeriesAttentionRankEntity {
    private int seriesId;
    private int seriesNewRank;
    private int seriesState;
}
