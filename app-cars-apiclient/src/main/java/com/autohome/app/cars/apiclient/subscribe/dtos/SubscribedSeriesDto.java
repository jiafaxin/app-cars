package com.autohome.app.cars.apiclient.subscribe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribedSeriesDto {
    private int seriesId;
    private int specId;

    public static SubscribedSeriesDto getInstance(int seriesId, int specId) {
        return new SubscribedSeriesDto(seriesId, specId);
    }
}