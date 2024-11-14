package com.autohome.app.cars.apiclient.cms.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SpecEvaluateItemResult {

    private String specname;
    private int automodelevaluateid;
    private String title;
    private int seriesid;
    private int specid;
    private Object murl;
    private String publishtime;
    private int articleid;
    private int cartype;
    private int automodelscore;
    private int automodeltotalscore;
    private List<ModulecontentsBean> modulecontents;
    private List<EvaluateitemsBean> evaluateitems;
    private List<?> evaluatesecondtypes;

    @Data
    public static class ModulecontentsBean {

        private int id;
        private Object partarticletitle;
        private int moduleclassid;
        private int ordernumber;
        private Object mcontent;
        private int partarticleid;
        private String title;
        private double automodelitemscore;
        private int automodelitemtotalscore;

    }

    @Data
    public static class EvaluateitemsBean {

        private Object description;
        private double data;
        private int evaluateitemid;
        private String evaluateitemname;
        private int firsttypeid;
        private int secondtypeid;
        private int thirdtypeid;
        private Object itemmemo;
        private int score;
        private int seriesid;
        private int specid;

    }
}
