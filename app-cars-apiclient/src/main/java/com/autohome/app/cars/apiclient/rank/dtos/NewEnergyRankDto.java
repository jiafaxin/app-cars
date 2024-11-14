package com.autohome.app.cars.apiclient.rank.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 新能源-续航榜&电耗榜源接口Dto
 */
@Getter
@Setter
public class NewEnergyRankDto {

    /**
     * returncode : 0
     * message : ok
     * result : [{"seriesid":5232,"specid":38721,"specname":null,"levelid":0,"editmemo":null,"automodelscore":61,"automodeltotalscore":0,"automodelscore2":0,"relationarticleid":0,"cartype":0,"automodelevaluateid":14,"ranknumber":0,"evaluatesummary":null,"accel":0,"brake":0,"oilwear":0,"batterylife":0,"fulloil":0,"batchtype":0,"accelerationtime":null,"modulecontents":[],"evaluateitems":[{"categoryitemid":108380,"categoryid":577,"categoryname":"0-100km/h加速时间","evaluateid":0,"criteriaid":7,"parentid":576,"levelid":4,"data":4.88,"evaluaterate":0,"score":22,"totalscore":25,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":333489,"categoryid":659,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":658,"levelid":3,"data":0,"evaluaterate":0,"score":17,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":108749,"categoryid":660,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":659,"levelid":4,"data":41.56,"evaluaterate":0,"score":17,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":5481,"categoryid":890,"categoryname":"城市续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":385,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":5482,"categoryid":895,"categoryname":"高速续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":309,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0}],"articleid":0,"title":null,"publishtime":"/Date(-62135596800000)/","murl":null,"pcurl":null,"editorid":0,"editorname":null,"autohomeuserid":0,"pcimage":null,"appimage":null,"pageid":null,"replycount":0,"viewcount":0,"class1":0,"memberpic":null,"position":null,"picturesquare":null},{"seriesid":4974,"specid":38634,"specname":null,"levelid":0,"editmemo":null,"automodelscore":71,"automodeltotalscore":0,"automodelscore2":0,"relationarticleid":0,"cartype":0,"automodelevaluateid":124,"ranknumber":0,"evaluatesummary":null,"accel":0,"brake":0,"oilwear":0,"batterylife":0,"fulloil":0,"batchtype":0,"accelerationtime":null,"modulecontents":[],"evaluateitems":[{"categoryitemid":123407,"categoryid":577,"categoryname":"0-100km/h加速时间","evaluateid":0,"criteriaid":7,"parentid":576,"levelid":4,"data":6.36,"evaluaterate":0,"score":15,"totalscore":25,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":261297,"categoryid":659,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":658,"levelid":3,"data":0,"evaluaterate":0,"score":34,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":123431,"categoryid":660,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":659,"levelid":4,"data":38.24,"evaluaterate":0,"score":34,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":261363,"categoryid":902,"categoryname":"PHEV油耗","evaluateid":0,"criteriaid":7,"parentid":896,"levelid":4,"data":8.3,"evaluaterate":0,"score":12,"totalscore":25,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0}],"articleid":0,"title":null,"publishtime":"/Date(-62135596800000)/","murl":null,"pcurl":null,"editorid":0,"editorname":null,"autohomeuserid":0,"pcimage":null,"appimage":null,"pageid":null,"replycount":0,"viewcount":0,"class1":0,"memberpic":null,"position":null,"picturesquare":null},{"seriesid":5529,"specid":45119,"specname":null,"levelid":0,"editmemo":null,"automodelscore":38,"automodeltotalscore":0,"automodelscore2":0,"relationarticleid":0,"cartype":0,"automodelevaluateid":63,"ranknumber":0,"evaluatesummary":null,"accel":0,"brake":0,"oilwear":0,"batterylife":0,"fulloil":0,"batchtype":0,"accelerationtime":null,"modulecontents":[],"evaluateitems":[{"categoryitemid":85177,"categoryid":577,"categoryname":"0-100km/h加速时间","evaluateid":0,"criteriaid":7,"parentid":576,"levelid":4,"data":9.33,"evaluaterate":0,"score":0,"totalscore":25,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":85297,"categoryid":659,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":658,"levelid":3,"data":0,"evaluaterate":0,"score":18,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":85220,"categoryid":660,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":659,"levelid":4,"data":41.33,"evaluaterate":0,"score":18,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":85245,"categoryid":890,"categoryname":"城市续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":523,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":85246,"categoryid":895,"categoryname":"高速续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":680,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0}],"articleid":0,"title":null,"publishtime":"/Date(-62135596800000)/","murl":null,"pcurl":null,"editorid":0,"editorname":null,"autohomeuserid":0,"pcimage":null,"appimage":null,"pageid":null,"replycount":0,"viewcount":0,"class1":0,"memberpic":null,"position":null,"picturesquare":null},{"seriesid":5323,"specid":42904,"specname":null,"levelid":0,"editmemo":null,"automodelscore":63,"automodeltotalscore":0,"automodelscore2":0,"relationarticleid":0,"cartype":0,"automodelevaluateid":56,"ranknumber":0,"evaluatesummary":null,"accel":0,"brake":0,"oilwear":0,"batterylife":0,"fulloil":0,"batchtype":0,"accelerationtime":null,"modulecontents":[],"evaluateitems":[{"categoryitemid":73513,"categoryid":577,"categoryname":"0-100km/h加速时间","evaluateid":0,"criteriaid":7,"parentid":576,"levelid":4,"data":4.67,"evaluaterate":0,"score":23,"totalscore":25,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":336564,"categoryid":659,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":658,"levelid":3,"data":0,"evaluaterate":0,"score":28,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":73559,"categoryid":660,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":659,"levelid":4,"data":39.39,"evaluaterate":0,"score":28,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":73571,"categoryid":890,"categoryname":"城市续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":387,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":73572,"categoryid":895,"categoryname":"高速续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":346,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0}],"articleid":0,"title":null,"publishtime":"/Date(-62135596800000)/","murl":null,"pcurl":null,"editorid":0,"editorname":null,"autohomeuserid":0,"pcimage":null,"appimage":null,"pageid":null,"replycount":0,"viewcount":0,"class1":0,"memberpic":null,"position":null,"picturesquare":null}]
     */

    private int returncode;
    private String message;
    private List<ResultNewEnergyRankBean> result;

    @Setter
    @Getter
    public static class ResultNewEnergyRankBean {
        /**
         * seriesid : 5232
         * specid : 38721
         * specname : null
         * levelid : 0
         * editmemo : null
         * automodelscore : 61
         * automodeltotalscore : 0
         * automodelscore2 : 0
         * relationarticleid : 0
         * cartype : 0
         * automodelevaluateid : 14
         * ranknumber : 0
         * evaluatesummary : null
         * accel : 0
         * brake : 0
         * oilwear : 0
         * batterylife : 0
         * fulloil : 0
         * batchtype : 0
         * accelerationtime : null
         * modulecontents : []
         * evaluateitems : [{"categoryitemid":108380,"categoryid":577,"categoryname":"0-100km/h加速时间","evaluateid":0,"criteriaid":7,"parentid":576,"levelid":4,"data":4.88,"evaluaterate":0,"score":22,"totalscore":25,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":333489,"categoryid":659,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":658,"levelid":3,"data":0,"evaluaterate":0,"score":17,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":108749,"categoryid":660,"categoryname":"100-0km/h制动距离","evaluateid":0,"criteriaid":7,"parentid":659,"levelid":4,"data":41.56,"evaluaterate":0,"score":17,"totalscore":45,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":5481,"categoryid":890,"categoryname":"城市续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":385,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0},{"categoryitemid":5482,"categoryid":895,"categoryname":"高速续航","evaluateid":0,"criteriaid":7,"parentid":687,"levelid":4,"data":309,"evaluaterate":0,"score":0,"totalscore":0,"description":"","configids":"","seriesid":0,"specid":0,"seriesname":null,"specname":null,"scoringconfigs":[],"pageid":null,"rankhighnumber":0,"ranklownumber":0}]
         * articleid : 0
         * title : null
         * publishtime : /Date(-62135596800000)/
         * murl : null
         * pcurl : null
         * editorid : 0
         * editorname : null
         * autohomeuserid : 0
         * pcimage : null
         * appimage : null
         * pageid : null
         * replycount : 0
         * viewcount : 0
         * class1 : 0
         * memberpic : null
         * position : null
         * picturesquare : null
         */

        private int seriesid;
        private int specid;
        private String specname;
        private int levelid;
        private Object editmemo;
        private int automodelscore;
        private int automodeltotalscore;
        private int automodelscore2;
        private int relationarticleid;
        private int cartype;
        private int automodelevaluateid;
        private int ranknumber;
        private Object evaluatesummary;
        private int accel;
        private int brake;
        private int oilwear;
        private int batterylife;
        private int fulloil;
        private int batchtype;
        private Object accelerationtime;
        private int articleid;
        private String title;
        private String publishtime;
        private String murl;
        private String pcurl;
        private int editorid;
        private String editorname;
        private int autohomeuserid;
        private String pcimage;
        private String appimage;
        private String pageid;
        private int replycount;
        private int viewcount;
        private int class1;
        private Object memberpic;
        private Object position;
        private Object picturesquare;

        private List<EvaluateItemsBean> evaluateitems;

        @Setter
        @Getter
        public static class EvaluateItemsBean {
            /**
             * categoryitemid : 108380
             * categoryid : 577
             * categoryname : 0-100km/h加速时间
             * evaluateid : 0
             * criteriaid : 7
             * parentid : 576
             * levelid : 4
             * data : 4.88
             * evaluaterate : 0
             * score : 22
             * totalscore : 25
             * description :
             * configids :
             * seriesid : 0
             * specid : 0
             * seriesname : null
             * specname : null
             * scoringconfigs : []
             * pageid : null
             * rankhighnumber : 0
             * ranklownumber : 0
             */

            private int categoryitemid;
            private int categoryid;
            private String categoryname;
            private int evaluateid;
            private int criteriaid;
            private int parentid;
            private int levelid;
            private double data;
            private int evaluaterate;
            private int score;
            private int totalscore;
            private String description;
            private String configids;
            private int seriesid;
            private int specid;
            private Object seriesname;
            private Object specname;
            private Object pageid;
            private int rankhighnumber;
            private int ranklownumber;


        }
    }
}
