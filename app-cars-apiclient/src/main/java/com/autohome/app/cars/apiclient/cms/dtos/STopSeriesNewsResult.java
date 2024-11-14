package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;

@Data
public class STopSeriesNewsResult{

        private int id;

        private String title;

        private String title2;

        private String publishtime;

        private String img;

        private int replycount;

        private int clickcount;

        private String lastupdatetime;

        private boolean isclosecomment;

        private int kind;

        private int type;

        private String duration;

        private String videoid;

        private String firstcoverimg;

        private String secondcoverimg;

        private String thirdcoverimg;

        private String firstappcoverimg;

        private String secondappcoverimg;

        private String thirdappcoverimg;

        private String editorname;

        private String seriesids;
}
