package com.autohome.app.cars.mapper.appcars.providers;

import java.util.List;

public class SeriesRecommendSpecProvider {

    public String getListBySeriesIdList(Integer cityId, List<Integer> seriesIdList) {
        List<String> seriesIdStrList = seriesIdList.stream().map(Object::toString).toList();

        return "SELECT * FROM series_recommend_spec WHERE cityId = " + cityId + "AND seriesId IN (" + String.join(",", seriesIdStrList) + ") AND is_del = 0";
    }

    public String getListBySeriesId(Integer seriesId) {
        return "SELECT * FROM series_recommend_spec WHERE seriesId=" + seriesId + " AND is_del = 0";
    }

}
