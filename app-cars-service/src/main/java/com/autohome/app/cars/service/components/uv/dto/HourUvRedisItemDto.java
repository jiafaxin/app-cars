package com.autohome.app.cars.service.components.uv.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
public class HourUvRedisItemDto {
    private String date;
    private int hour;
    private List<SeriesUvItem> ranklist = new LinkedList<>();

    @Setter
    @Getter
    public static class SeriesUvItem {
        private int seriesid;
        private long hotv;
    }
}
