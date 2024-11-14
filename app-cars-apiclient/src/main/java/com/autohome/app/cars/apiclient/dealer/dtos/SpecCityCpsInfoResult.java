package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

@Data
public class SpecCityCpsInfoResult {

    private int productId;
    private int productType;
    private Object specId;
    private Object specName;
    private int seriesId;
    private String seriesName;
    private String seriesImg;
    private String facIconTxt;
    private Object dealerIconTxt;
    private String facBtnMainTitle;
    private String dealerBtnMainTitle;
    private String facShortTitle;
    private Object dealerShortTitle;
    private String facMediumTitle;
    private Object dealerMediumTitle;
    private String facLongTitle;
    private Object dealerLongTitle;
    private String title;
    private String originalJumpUrl;
    private Object price;
    private int depositAmount;
    private int rebateAmount;
    private int originalPrice;
    private int discount;
    private Object totalDiscount;
    private Object stock;
    private int showBuyNum;
    private Object competitionTitle;
    private Object appJumpUrl;

}
