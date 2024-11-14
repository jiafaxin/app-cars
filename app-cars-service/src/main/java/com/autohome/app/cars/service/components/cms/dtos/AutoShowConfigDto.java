package com.autohome.app.cars.service.components.cms.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class AutoShowConfigDto {


    private List<CpkSeriesPic> cpkSeriesPics = new ArrayList<>();

    private List<NewsCarItem> newsCarItems = new ArrayList<>();

    @Data
    public static class CpkSeriesPic {
        private int brandId;

        private int seriesId;

        private int picCount;
    }

    @Data
    public static class NewsCarItem {
        private int seriesId;

        private int brandId;

        private int carAction;

        private String tagIds;
    }

    /**
     * 车展动作，-1说明没有车展动作，0说明有车展图，大于0说明资讯后台数据
     *
     * @param seriesId
     * @return
     */
    public int getCarAction(int seriesId) {
        if (getCpkSeriesPics() == null && getNewsCarItems() == null)
            return -1;
        if (getNewsCarItems() != null && !getNewsCarItems().isEmpty()) {
            Optional<NewsCarItem> carAction = getNewsCarItems().stream().filter(x -> x.getSeriesId() == seriesId).findFirst();
            if (carAction.isPresent()) {
                return carAction.get().getCarAction();
            }
        }
        if (getCpkSeriesPics() != null && !getCpkSeriesPics().isEmpty()) {
            Optional<CpkSeriesPic> first = getCpkSeriesPics().stream().filter(x -> x.getSeriesId() == seriesId).findFirst();
            if (first.isPresent()) {
                return 0;
            }
        }
        return -1;
    }
}
