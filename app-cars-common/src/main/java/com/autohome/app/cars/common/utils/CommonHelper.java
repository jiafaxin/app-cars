package com.autohome.app.cars.common.utils;

import com.autohome.app.cars.common.enums.CarSellTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommonHelper {

    private final static Logger logger = LoggerFactory.getLogger(CommonHelper.class);

    public static DecimalFormat df02 = new DecimalFormat("0.00");

    public static boolean isTakeEffectVersion(String pluginversion, String effectversion) {

        boolean flag = false;
        try {
            String[] pvArr = pluginversion.split("\\.");
            String[] evArr = effectversion.split("\\.");

            return ((Integer.parseInt(pvArr[0]) < Integer.parseInt(evArr[0]))
                    || (Integer.parseInt(pvArr[0]) == Integer.parseInt(evArr[0])
                    && Integer.parseInt(pvArr[1]) < Integer.parseInt(evArr[1]))
                    || (Integer.parseInt(pvArr[0]) == Integer.parseInt(evArr[0])
                    && Integer.parseInt(pvArr[1]) == Integer.parseInt(evArr[1])
                    && Integer.parseInt(pvArr[2]) < Integer.parseInt(evArr[2]))) == false;

        } catch (Exception e) {
            logger.error("版本判断错误;exceptionStack:" + ExceptionUtils.getStackTrace(e));
        }
        return flag;
    }

    public static boolean isInner(int categoryId) {
        return Arrays.asList(3, 10).contains(categoryId);
    }

    public static boolean isStopOrUnsold(int seriesState) {
        return seriesState == 0 || seriesState == 40;
    }

    public static String getInsideBrowerSchemeWK(String httpScheme) {
        if (StringUtils.isBlank(httpScheme)) {
            return "";
        }
        if (!StringUtils.startsWith(httpScheme, "http://") && !StringUtils.startsWith(httpScheme, "https://")) {
            return httpScheme;
        }
        try {
            return String.format("autohome://insidebrowserwk?url=%s",
                    URLEncoder.encode(ToHttps(httpScheme), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpScheme;
    }

    public static String getInsideBrowerSchemeWK_Goback(String httpScheme) {
        if (StringUtils.isBlank(httpScheme)) {
            return "";
        }
        try {
            return String.format("autohome://insidebrowserwk?cangoback=1&url=%s",
                    URLEncoder.encode(ToHttps(httpScheme), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpScheme;
    }

    public static String ToHttps(String url) {
        String uString = url;
        if (!StringUtils.isNotEmpty(url)) {
            return "";
        } else {
            if (url.toLowerCase().startsWith("http://")) {
                uString = "https://" + url.substring(7);
            }
            return uString;
        }
    }

    public static String ToHttp(String url) {
        String uString = url;
        if (!StringUtils.isNotEmpty(url)) {
            return "";
        } else {
            if (url.toLowerCase().startsWith("https://")) {
                uString = "http://" + url.substring(8);
            }
            return uString;
        }
    }

    public static String getMoney(int price, String unit) {
        String priceInfo = "";
        try {
            if (price > 9999) {
                priceInfo = String.format("%.2f", Double.valueOf(price / 10000.0)).toString() + "万" + unit;
            } else {
                priceInfo = price + unit;
            }
        } catch (Exception e) {
        }
        return priceInfo;
    }


    public static String priceFormat(Double minPrice, Double maxPrice, CarSellTypeEnum carSellTypeEnum, String splitChar) {
        String priceStr = "";
        if (carSellTypeEnum == CarSellTypeEnum.Selling) {
            priceStr = "暂无报价";
        } else if (carSellTypeEnum == CarSellTypeEnum.StopSell) {
            priceStr = "停售";
        } else if (carSellTypeEnum == CarSellTypeEnum.WillSell) {
            priceStr = "即将销售";
        }

        if (!(minPrice <= 0.0)) {
            priceStr = df02.format(minPrice / 10000.0);
            if (!(maxPrice <= 0.0) && minPrice != maxPrice) {
                priceStr = priceStr + splitChar + df02.format(maxPrice / 10000.0);
            }

            priceStr = priceStr + "万";
        }

        return priceStr;
    }

    public static String priceForamtV2(int minPrice, int maxPrice) {
        String priceInfo = "暂无报价";
        if (minPrice > 0) {
            priceInfo = getPriceInfo(minPrice);
        }
        if (maxPrice > 0 && maxPrice != minPrice) {
            if (minPrice == 0) {
                priceInfo = getPriceInfo(maxPrice);
            } else {
                priceInfo = priceInfo.replace("万", "") + "-" + getPriceInfo(maxPrice);
            }
        }
        return priceInfo;
    }

    public static String getPriceInfo(int price) {
        String priceInfo = "暂无报价";
        try {
            if (price > 0) {
                priceInfo = String.format("%.2f", Double.valueOf(price / 10000.0)).toString() + "万";
            } else {
                priceInfo = "暂无报价";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return priceInfo;
    }

    public static String getPriceDetailInfo(int price) {
        String priceInfo = "暂无报价";
        try {
            if (price > 0) {
                priceInfo = String.format("%.4f", Double.valueOf(price / 10000.0)).toString();
                String priceInt = String.format("%.2f", Double.valueOf(price / 10000.0)).toString();
                if (Float.parseFloat(priceInfo) == Float.parseFloat(priceInt)) {
                    priceInfo = priceInt + "万";
                } else {
                    priceInfo = Float.parseFloat(priceInfo) + "万";
                }
            } else {
                priceInfo = "暂无报价";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceInfo;
    }
    public static String getPriceInfoForDiffConfig(int price) {
        String priceInfo = "暂无数据";

        try {
            if (price > 0) {
                if (price >= 10000) {
                    priceInfo = String.format("%.1f", (double) price / 10000.0D).toString() + "万";
                } else {
                    priceInfo = price + "元";
                }
            } else {
                priceInfo = "暂无数据";
            }
        } catch (Exception var3) {
        }

        return priceInfo;
    }

    /**
     * 编码URL
     * @param url 未编码的字符串
     * @return 编码后的字符串
     */
    public static String encodeUrl(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public static String priceForamt(int minPrice, int maxPrice) {
        String priceInfo = "";
        if (minPrice > 0) {
            priceInfo = getPriceInfo(minPrice);
        }
        if (maxPrice > 0 && maxPrice != minPrice) {
            priceInfo = priceInfo.replace("万", "") + "-" + getPriceInfo(maxPrice);
        }
        return priceInfo;
    }

    // 裁剪车系列表图标尺寸
    public static String ChangeLogoSize(int imageSizeIndex, String imageUrl) {
        String result = "";

        if (imageSizeIndex == 16) {
            int index = imageUrl.lastIndexOf("/");
            result = imageUrl.substring(0, index + 1) + "900x600_0_q50_autohomecar__"
                    + imageUrl.substring(index + 1, imageUrl.length());
            return result;
        }

        if (null != imageUrl && !"".equals(imageUrl) && imageUrl.indexOf("autohomecar") > -1) {
            int index = imageUrl.lastIndexOf("/");
            result = imageUrl.substring(0, index + 1) + getCarLogoSizePrefix().get(imageSizeIndex)
                    + imageUrl.substring(index + 1, imageUrl.length());
        } else if (imageSizeIndex == 14) {
            int index = imageUrl.lastIndexOf("/");
            result = imageUrl.substring(0, index + 1) + "1400x1050_0_q95_autohomecar__"
                    + imageUrl.substring(index + 1, imageUrl.length());
        } else if (imageSizeIndex == 8) {
            int index = imageUrl.lastIndexOf("/");
            result = imageUrl.substring(0, index + 1) + "240x180_0_q95_autohomecar__"
                    + imageUrl.substring(index + 1, imageUrl.length());
        } else {
            result = imageUrl;
        }
        return result;
    }

    public static Map<Integer, String> getCarLogoSizePrefix() {
        Map<Integer, String> result = new HashMap<Integer, String>();
        result.put(0, "");
        result.put(1, "u_");
        result.put(2, "ys_");
        result.put(3, "cw_");
        result.put(4, "500x0_1_");//w_
        result.put(5, "k_");
        result.put(6, "cp_");
        result.put(7, "tp_");
        result.put(8, "240x180_");//t_
        result.put(9, "m_");
        result.put(10, "s_");
        result.put(11, "l_");
        result.put(12, "400x300_");
        result.put(13, "600x450_");
        return result;
    }

    public static String getRandInfo(String priceOne, String priceTwo) {
        String price = "";
        try {
            BigDecimal bg = new BigDecimal(Double.parseDouble(priceOne) - Double.parseDouble(priceTwo)).setScale(2,
                    RoundingMode.HALF_UP);
            if (Double.parseDouble(bg + "") > 0) {
                price = bg + "";
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return price;
    }

    public static String getPercentInfo(String newPrice, String oldPrice) {
        String percent = "0.0%";
        try {
            // BigDecimal bg = new BigDecimal(price / 10000.0).setScale(2,
            // RoundingMode.HALF_UP);
            BigDecimal bg = new BigDecimal(
                    ((Double.parseDouble(oldPrice) - Double.parseDouble(newPrice)) / Double.parseDouble(oldPrice))
                            * 100).setScale(1, RoundingMode.HALF_UP);
            percent = bg + "%";
        } catch (Exception e) {
            // TODO: handle exception
        }
        return percent;
    }

}
