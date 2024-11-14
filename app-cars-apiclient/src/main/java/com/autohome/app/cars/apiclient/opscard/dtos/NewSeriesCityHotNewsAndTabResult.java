package com.autohome.app.cars.apiclient.opscard.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/8/22 14:11
 */
@Data
@NoArgsConstructor
public class NewSeriesCityHotNewsAndTabResult {

    private List<Card> cards;
    @Data
    public static class Card {

        private String cardtag;

        private int cardposition;

        private List<Card.Cell> cells;

        @NoArgsConstructor
        @Data
        public static class Cell {
            private String opsendtime;
            private String icon;
            private String sort;
            private String opsstarttime;
            private String title;
            private String type;
            private String seriesids;
            private List<String> awOppositionids;
            private String subtitle;
            private List<String> oppositionids;
            private Integer pagecarddataid;
            private String linkurl;
            private Integer position;
            private String reddot;
        }

    }
}

