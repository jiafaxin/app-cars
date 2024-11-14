package com.autohome.app.cars.apiclient.recommend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeriesSpecPair {
    private Integer series_id;
    private Integer spec_id;
}
