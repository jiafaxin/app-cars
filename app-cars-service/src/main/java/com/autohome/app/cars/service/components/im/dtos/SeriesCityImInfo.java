package com.autohome.app.cars.service.components.im.dtos;

import lombok.Data;

@Data
public class SeriesCityImInfo {
    int seriesId;
    int cityId;
    int targetId;
    int targetType;
    int memberCount;
    String ryRoomId;
}
