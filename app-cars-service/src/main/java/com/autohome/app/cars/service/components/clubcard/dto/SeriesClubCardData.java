package com.autohome.app.cars.service.components.clubcard.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubCardDataResult;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubTopicListResult;

/**
 * @author wbs
 * @date 2024/6/6
 */
public class SeriesClubCardData {

    private List<SeriesClubCardDataDto> clubHotList = new ArrayList<>();

    private Map<Integer, List<SeriesClubCardDataDto>> SeriesClubCardDataMap = new ConcurrentHashMap<>();

    public List<SeriesClubCardDataDto> getClubHotList() {
        return clubHotList;
    }

    public void setClubHotList(List<SeriesClubCardDataDto> clubHotList) {
        this.clubHotList = clubHotList;
    }

    public Map<Integer, List<SeriesClubCardDataDto>> getSeriesClubCardDataMap() {
        return SeriesClubCardDataMap;
    }

    public void setSeriesClubCardDataMap(Map<Integer, List<SeriesClubCardDataDto>> seriesClubCardDataMap) {
        SeriesClubCardDataMap = seriesClubCardDataMap;
    }
}
