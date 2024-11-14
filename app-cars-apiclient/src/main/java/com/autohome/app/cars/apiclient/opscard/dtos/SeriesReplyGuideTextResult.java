package com.autohome.app.cars.apiclient.opscard.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class SeriesReplyGuideTextResult {

    private List<CardsDTO> cards = new ArrayList<>();

    @NoArgsConstructor
    @Data
    public static class CardsDTO {
        private String cardtag;
        private Integer cardposition;
        private CardstyleDTO cardstyle;
        private CarddataDTO carddata;
        private List<CellsDTO> cells = new ArrayList<>();

        @NoArgsConstructor
        @Data
        public static class CardstyleDTO {
        }

        @NoArgsConstructor
        @Data
        public static class CarddataDTO {
        }

        @NoArgsConstructor
        @Data
        public static class CellsDTO {
            private String posttext;
            private String guidetext;
            private String guidescheme;
            private String opsendtime;
            private String redtext;
            private List<?> oppositionids;
            private Integer pagecarddataid;
            private Integer position;
            private String opsstarttime;
            private String seriesids;
            private String prompttext;
            private List<?> aw_oppositionids;
        }
    }
}
