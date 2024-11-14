package com.autohome.app.cars.common.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Formatter;

public class PriceUtil {
    public static String GetPriceStringDetail(int minPrice, int maxPrice, int specState) {
        String priceInfo = "";
        switch (specState) {
            case 10:
                priceInfo = priceDetailForamt(minPrice, maxPrice);
                if ("".equals(priceInfo)) {
                    priceInfo = "即将销售";
                }
                break;
            case 0:
            case 20:
            case 40:
            case 30:
                priceInfo = priceDetailForamt(minPrice, maxPrice);
                if ("".equals(priceInfo)) {
                    priceInfo = "暂无报价";
                }
                break;
            default:
                priceInfo = "暂无报价";
                break;
        }

        return priceInfo;
    }

    public static String priceDetailForamt(int minPrice, int maxPrice) {
        String priceInfo = "";
        if (minPrice > 0) {
            priceInfo = getPriceDetailInfo(minPrice);
        }
        if (maxPrice > 0 && maxPrice != minPrice) {
            priceInfo = priceInfo.replace("万", "") + "-" + getPriceDetailInfo(maxPrice);
        }
        return priceInfo;
    }

    public static String getPriceDetailInfo(int price) {
        String priceInfo = "暂无报价";
        try {
            if (price > 0) {
                priceInfo = String.format("%.4f", Double.valueOf(price / 10000.0));
                String priceInt = String.format("%.2f", Double.valueOf(price / 10000.0));
                if (Float.parseFloat(priceInfo) == Float.parseFloat(priceInt)) {
                    priceInfo = priceInt + "万";
                } else {
                    priceInfo = Float.parseFloat(priceInfo) + "万";
                }
            } else {
                priceInfo = "暂无报价";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return priceInfo;
    }

    public static String getStrPrice(int minPrice, int maxPrice) {
        if (minPrice == 0 || maxPrice == 0 )
        {
            return "暂无报价";
        }
        if(minPrice==maxPrice){
            Double d = minPrice / 10000.0;
            if(d>1000) {
                return new DecimalFormat("#.00").format(d) + "万";
            }else{
                return new DecimalFormat("#.00##").format(d) + "万";
            }
        }else{
            String min = getBaseStr(minPrice);
            String max = getBaseStr(maxPrice);
            return min+"-"+max+"万";
        }
    }

    /**
     * 价格除10000后的字符串：
     * > 1000 保留两位小数
     * 小数点后至少保留两位，至多保留4位
     * @param price
     * @return
     */
    static String getBaseStr(Integer price){
        Double d = price / 10000.0;
        String str = d + "";
        if(d > 1000){
            return  new DecimalFormat("#.##").format(d);
        }
        if(str.indexOf(".") < 0)
            return new Formatter().format("%.2f", d).toString();
        int sIndex = str.length() - str.indexOf(".") - 1;
        if(sIndex < 2){
            return new Formatter().format("%.2f", d).toString();
        }else if(sIndex > 4){
            return new Formatter().format("%.4f", d).toString();
        }else {
            return str;
        }

    }

    public static String getPriceInfoNoDefult(int price) {
        String priceInfo = "";
        // BigDecimal bg = new BigDecimal(price / 10000.0).setScale(2,
        // RoundingMode.HALF_UP);
        try {
            if (price > 10000) {
                priceInfo = String.format("%.2f", Double.valueOf(price / 10000.0)).toString() + "万元";
            } else {
                priceInfo = price == 0 ? "" : price + "元";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return priceInfo;
    }

    public static String getSpecPrice(Integer minPrice) {
        String price = "暂无报价";

        if (minPrice != null && minPrice > 10000) {
            price = String.format("%.2f", minPrice / 10000.0).toString() + "万";
        } else if (minPrice != null && minPrice > 0) {
            price = minPrice + "";
        }

        return price;
    }
    public static  String SeriesSubscribeFormatPrice(int price) {
        try {
            return price < 5000 ? price + "元" : String.format("%.2f", price / 10000.0) + "万";
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }


    public static String toWanStr(Double d){
        String price = "暂无";
        try {
            if(ObjectUtils.isEmpty(d) || d <= 0){
                return price;
            }
            BigDecimal decimal = new BigDecimal(d);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedNumber = decimalFormat.format(decimal);
            return formattedNumber+"万";
        } catch (Exception e) {
        }
        return d+"";
    }

    public static void main(String[] args) {
        System.out.println(toWanStr(14D));
    }

}
