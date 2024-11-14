package com.autohome.app.cars.mapper.appcars.entities;

import lombok.Data;

@Data
public class SeriesRecommendSpecEntity {
        private int id;
        private int seriesId;
        private int cityId;
        private int specId;
        private int isDel;
        private int createdStime;
        private int modifiedStime;
}
