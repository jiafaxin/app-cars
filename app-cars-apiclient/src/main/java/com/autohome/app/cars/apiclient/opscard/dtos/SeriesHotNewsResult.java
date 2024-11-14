package com.autohome.app.cars.apiclient.opscard.dtos;

import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/7/19
 */
public class SeriesHotNewsResult {

    private List<Card> cards;

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public static class Card {

        private String cardtag;

        private int cardposition;

        private List<Cell> cells;

        public void setCardtag(String cardtag) {
            this.cardtag = cardtag;
        }

        public String getCardtag() {
            return cardtag;
        }

        public void setCardposition(int cardposition) {
            this.cardposition = cardposition;
        }

        public int getCardposition() {
            return cardposition;
        }

        public void setCells(List<Cell> cells) {
            this.cells = cells;
        }

        public List<Cell> getCells() {
            return cells;
        }

        public static class Cell {

            private String opsendtime;

            private String icon;

            private List<String> oppositionids;

            private int pagecarddataid;

            private String linkurl;

            private String sort;

            private int position;

            private String opsstarttime;

            private String title;

            private String seriesids;

            private List<String> aw_oppositionids;

            public void setOpsendtime(String opsendtime) {
                this.opsendtime = opsendtime;
            }

            public String getOpsendtime() {
                return opsendtime;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getIcon() {
                return icon;
            }

            public void setOppositionids(List<String> oppositionids) {
                this.oppositionids = oppositionids;
            }

            public List<String> getOppositionids() {
                return oppositionids;
            }

            public void setPagecarddataid(int pagecarddataid) {
                this.pagecarddataid = pagecarddataid;
            }

            public int getPagecarddataid() {
                return pagecarddataid;
            }

            public void setLinkurl(String linkurl) {
                this.linkurl = linkurl;
            }

            public String getLinkurl() {
                return linkurl;
            }

            public void setSort(String sort) {
                this.sort = sort;
            }

            public String getSort() {
                return sort;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            public int getPosition() {
                return position;
            }

            public void setOpsstarttime(String opsstarttime) {
                this.opsstarttime = opsstarttime;
            }

            public String getOpsstarttime() {
                return opsstarttime;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            public void setSeriesids(String seriesids) {
                this.seriesids = seriesids;
            }

            public String getSeriesids() {
                return seriesids;
            }

            public void setAw_oppositionids(List<String> aw_oppositionids) {
                this.aw_oppositionids = aw_oppositionids;
            }

            public List<String> getAw_oppositionids() {
                return aw_oppositionids;
            }

        }

    }
}
