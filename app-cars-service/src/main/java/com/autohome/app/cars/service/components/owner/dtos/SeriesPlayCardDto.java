package com.autohome.app.cars.service.components.owner.dtos;

import lombok.Data;

@Data
public class SeriesPlayCardDto {
    /**
     * 视频说明书是否有入口
     */
    int videoShowEntry;

    /**
     * 视频说明书链接
     */
    String videoShowEntryScheme;
}
