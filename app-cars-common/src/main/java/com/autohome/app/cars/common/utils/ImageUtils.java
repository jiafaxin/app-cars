package com.autohome.app.cars.common.utils;




import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片处理方法公共类
 */
@Slf4j
public class ImageUtils {

    private final static String New_Image_Domain = "app2.autoimg.cn";

    /**
     * 转换图片尺寸
     *
     * @param url
     * @param imageSizeEnum 尺寸枚举
     * @return
     */
    public static String convertImage_Size(String url, ImageSizeEnum imageSizeEnum) {
        if (imageSizeEnum == null || StringUtils.isEmpty(url)) return "";
        String paramStr = "";
        String urlStr = url;
        //通用图片处理规则
        if (url.contains("autohomecar__") || url.contains("dfs/") || url.contains("autohomedealer__")) {
            if (url.contains("?")) {
                urlStr = url.split("[?]")[0];
                paramStr = url.split("[?]")[1];
            }
            if (url.contains("autohomecar__")) {
                urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + urlStr.substring(urlStr.indexOf("autohomecar__"));
            } else if (url.contains("autohomedealer__")) {
                urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + urlStr.substring(urlStr.indexOf("autohomedealer__"));
            } else {
                urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + "autohomecar__" + urlStr.substring(urlStr.lastIndexOf("/") + 1);
            }
            url = urlStr + (StringUtils.hasLength(paramStr) ? "?" + paramStr : "");
        } else if (url.contains("/travelplat/")) {
            if (url.contains("?")) {
                urlStr = url.split("[?]")[0];
                paramStr = url.split("[?]")[1];
            }
            urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + "autohomecar__" + urlStr.substring(urlStr.lastIndexOf("/") + 1);
            url = urlStr + (StringUtils.hasLength(paramStr) ? "?" + paramStr : "");
        } else if (imageSizeEnum == ImageSizeEnum.ImgSize_4x3_400x0 || imageSizeEnum == ImageSizeEnum.ImgSize_4x3_500x0) {
            // 论坛图片规则，走特殊处理
            url = convertImage_Size_ClubImg(url, imageSizeEnum);
        }
        return url;
    }

    /**
     * 转换图片尺寸
     *
     * @param url
     * @param imageSizeEnum 尺寸枚举
     * @return
     */
    public static String convertImage_SizeWebp(String url, ImageSizeEnum imageSizeEnum) {
        if (imageSizeEnum == null || StringUtils.isEmpty(url)) return "";
        String paramStr = "";
        String urlStr = url;
        if (urlStr.toLowerCase().startsWith("https://")) {
            urlStr = "http:/" + urlStr.substring(7);
        }
        //通用图片处理规则
        if (url.contains("autohomecar__") || url.contains("dfs/") || url.contains("autohomedealer__") || url.contains("panovr")) {
            if (url.contains("?")) {
                urlStr = url.split("[?]")[0];
                paramStr = url.split("[?]")[1];
            }
            if (url.contains("autohomecar__")) {
                urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + urlStr.substring(urlStr.indexOf("autohomecar__"));
            } else if (url.contains("autohomedealer__")) {
                urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + urlStr.substring(urlStr.indexOf("autohomedealer__"));
            } else {
                urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + "autohomecar__" + urlStr.substring(urlStr.lastIndexOf("/") + 1);
            }
            url = urlStr + (StringUtils.hasLength(paramStr) ? "?" + paramStr : "");
        } else if (url.contains("/travelplat/")) {
            if (url.contains("?")) {
                urlStr = url.split("[?]")[0];
                paramStr = url.split("[?]")[1];
            }
            urlStr = urlStr.substring(0, urlStr.lastIndexOf("/") + 1) + imageSizeEnum.getSize() + "autohomecar__" + urlStr.substring(urlStr.lastIndexOf("/") + 1);
            url = urlStr + (StringUtils.hasLength(paramStr) ? "?" + paramStr : "");
        } else if (imageSizeEnum == ImageSizeEnum.ImgSize_4x3_400x0 || imageSizeEnum == ImageSizeEnum.ImgSize_4x3_500x0) {
            // 论坛图片规则，走特殊处理
            url = convertImage_Size_ClubImg(url, imageSizeEnum);
        }
        url = url.toLowerCase().endsWith(".webp") ? url : url + ".webp";
        return url;
    }

    /**
     * 论坛图片尺寸转换
     *
     * @param imgUrl
     * @param imageSizeEnum
     * @return
     */
    public static String convertImage_Size_ClubImg(String imgUrl, ImageSizeEnum imageSizeEnum) {
        String url = imgUrl;
        String size = imageSizeEnum.getSize();
        url = url.replaceAll("\\/\\d{3}_", "/" + size);
        url = StringUtils.replace(url, "/club0.", "/club2.");
        url = StringUtils.replace(url, "/club1.", "/club2.");
        int addWebp = 1;
        if (url.contains(size)) {

        } else if (url.contains("/userphotos/")) {
            url = url.substring(0, url.lastIndexOf('/') + 1) + size + url.substring(url.lastIndexOf('/') + 1);
        } else if (url.contains("")) {

        }
        if (url.toLowerCase().endsWith(".webp")) {
            addWebp = 0;
        }
        url = addWebp == 0 ? url : url + ".webp";
        return url;
    }

    public static String convertImage_Size_ClubImgBean(String imgUrl, ImageSizeEnum imageSizeEnum) {
        String url = convertImage_ToHttp(imgUrl);
        String size = imageSizeEnum.getSize();
        url = url.replaceAll("\\/\\d{3}_", "/");
        url = StringUtils.replace(url, "/club0.", "/club2.");
        url = StringUtils.replace(url, "/club1.", "/club2.");
        int addWebp = 1;
        if (url.contains(size)) {

        } else if (url.contains("/userphotos/")) {
            url = url.substring(0, url.lastIndexOf('/') + 1) + size + "autohomecar__" + url.substring(url.lastIndexOf('/') + 1);
        } else if (url.contains("")) {

        }
        if (url.toLowerCase().endsWith(".webp")) {
            addWebp = 0;
        }
        url = addWebp == 0 ? url : url + ".webp";
        return url;
    }


    /**
     * 图片转webp
     *
     * @param url
     * @return
     */
    public static String convertImage_ToWebp(String url) {
        if (url == null || url.trim().equals("")) {
            return "";
        }
        if(StringUtils.endsWithIgnoreCase(url,".gif")){
            return url;
        }
        String imgUrl = url;
        String param = "";
        if (!url.contains("userphotos") && ((!url.contains("qnwww") && url.contains("autohomecar__")) || url.contains("dfs/") || url.contains("club2.") || url.contains("club3."))) {
            if (imgUrl.contains("?")) {
                imgUrl = url.substring(0, url.indexOf("?"));
                param = url.substring(url.indexOf("?"));
            }
            imgUrl = imgUrl.replace(".png.png", ".png");
            imgUrl = imgUrl.toLowerCase().endsWith(".webp") ? imgUrl : imgUrl + ".webp";
        } else {
        }
        imgUrl = imgUrl + param;
        return imgUrl;
    }

    /**
     * 图片域名收敛,收敛为 app2.autoimg.cn
     *
     * @param url
     * @return
     */
    public static String convertImage_DomainUnit(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String imgUrl = url;

        if ((!url.contains("qnwww") && url.contains("autohomecar__")) || url.contains("dfs/")) {
            imgUrl = changeDomain(imgUrl, New_Image_Domain);
        } else if (url.contains("club2.") || url.contains("club3.")) {
            if (url.contains("/400_") || url.contains("/500_")) {
                imgUrl = changeDomain(url, New_Image_Domain);
            }
        }
        return imgUrl;
    }

    /**
     * 图片域名收敛
     *
     * @param url        图片地址
     * @param descDomain 收敛后的域名
     * @return
     */
    public static String convertImage_DomainUnit(String url, String descDomain) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String imgUrl = url;

        if ((!url.contains("qnwww") && url.contains("autohomecar__")) || url.contains("dfs/")) {
            imgUrl = changeDomain(imgUrl, descDomain);
        } else if (url.contains("club2.") || url.contains("club3.")) {
            if (url.contains("/400_") || url.contains("/500_")) {
                imgUrl = changeDomain(url, descDomain);
            }
        }
        return imgUrl;
    }

    private static String changeDomain(String url, String descDomain) {
        String newUrl = url;
        try {
            String domain = new URL(url).getHost();
            newUrl = url.replace(domain, descDomain);
        } catch (Exception ex) {

        }
        return newUrl;
    }

    /**
     * 图片地址转 HTTPS
     *
     * @param url
     * @return
     */
    public static String convertImage_ToHttps(String url) {
        if (StringUtils.isEmpty(url)) return "";
        if (url.toLowerCase().startsWith("http://")) {
            url = "https:/" + url.substring(6);
        }
        return url;
    }

    /**
     * 图片地址转 HTTP
     *
     * @param url
     * @return
     */
    public static String convertImage_ToHttp(String url) {
        if (StringUtils.isEmpty(url)) return "";
        if (url.toLowerCase().startsWith("https://")) {
            url = "http:/" + url.substring(7);
        }
        return url;
    }

    public static String convertImage_NoHttp(String url) {
        if (!StringUtils.isEmpty(url)) {
            if (url.startsWith("//")) {
                url = "http:" + url;
            }
        }
        return url;
    }

    /**
     * 图片转换（支持转Webp、https/http、域名收敛）
     *
     * @param url        图片地址
     * @param toWebp     是否转为 webp 格式
     * @param toHttps    是否转Https，false时，协议会统一转为http
     * @param domainUnit 是否域名收敛，true时，默认域名为 app2.autoimg.cn
     * @return
     */
    public static String convertImageUrl(String url, boolean toWebp, boolean toHttps, boolean domainUnit) {
        if (StringUtils.isEmpty(url)) return "";
        if (toWebp) {
            url = convertImage_ToWebp(url);
        }
        if (toHttps) {
            url = convertImage_ToHttps(url);
        } else {
            url = convertImage_ToHttp(url);
        }
        if (domainUnit) {
            url = convertImage_DomainUnit(url);
        }
        return url;
    }

    /**
     * 图片转换（支持转Webp、https/http、域名收敛）
     *
     * @param url        图片地址
     * @param toWebp     是否转为 webp 格式
     * @param toHttps    是否转Https，false时，协议会统一转为http
     * @param domainUnit 是否域名收敛，true时，默认域名为 app2.autoimg.cn
     * @return
     */
    public static String convertImageUrl(String url, boolean toWebp, boolean toHttps, boolean domainUnit, ImageSizeEnum imageSizeEnum) {
        if (StringUtils.isEmpty(url)) return "";
        if (imageSizeEnum != null) {
            url = convertImage_Size(url, imageSizeEnum);
        }
        if (toWebp) {
            url = convertImage_ToWebp(url);
        }
        if (toHttps) {
            url = convertImage_ToHttps(url);
        } else {
            url = convertImage_ToHttp(url);
        }
        if (domainUnit) {
            url = convertImage_DomainUnit(url);
        }
        return url;
    }


    /**
     * 转换成 k1 格式的图片路径
     * http://panovr.autoimg.cn/pano/g28/M04/F7/5A/900x600_k1_autohomecar__ChxkmmUyHIyATZDRABQaiEcmwEY342.png.webp
     *
     * @param url           URL
     * @param toWebp        是否转为webp
     * @param toHttps       是否https
     * @param domainUnit    是否域名收敛
     * @param imageSizeEnum 图片尺寸
     * @param isK1          是否添加 k1 到url
     * @return 处理后的URL
     */
    public static String convertK1ImageUrl(String url, boolean toWebp, boolean toHttps, boolean domainUnit, ImageSizeEnum imageSizeEnum, boolean isK1) {
        if (StringUtils.isEmpty(url)) return "";
        if (imageSizeEnum != null) {
            url = convertImage_Size(url, imageSizeEnum);
        }
        if (toWebp) {
            url = convertImage_ToWebp(url);
        }
        if (toHttps) {
            url = convertImage_ToHttps(url);
        } else {
            url = convertImage_ToHttp(url);
        }
        if (domainUnit) {
            url = convertImage_DomainUnit(url);
        }
        if (isK1) {
            url = addK1ToPath(url);
        }
        return url;
    }

    /**
     * 图片转换（支持转Webp、https/http、域名收敛，指定收敛域名）
     *
     * @param url        图片地址
     * @param toWebp     是否转为 .webp
     * @param toHttps    是否转Https，false时，协议会统一转为http
     * @param domainUnit 是否域名收敛
     * @param descDomain 收敛后的目标域名
     * @return
     */
    public static String convertImageUrl(String url, boolean toWebp, boolean toHttps, boolean domainUnit, String descDomain) {
        if (StringUtils.isEmpty(url)) return "";
        if (toWebp) {
            url = convertImage_ToWebp(url);
        }
        if (toHttps) {
            url = convertImage_ToHttps(url);
        } else {
            url = convertImage_ToHttp(url);
        }
        if (domainUnit) {
            url = StringUtils.hasLength(descDomain) ? convertImage_DomainUnit(url, descDomain) : convertImage_DomainUnit(url);
        }
        return url;
    }

    /**
     * 图片转换（支持转Webp、https/http、域名收敛，指定收敛域名）
     *
     * @param url        图片地址
     * @param toWebp     是否转为 .webp
     * @param toHttps    是否转Https，false时，协议会统一转为http
     * @param domainUnit 是否域名收敛
     * @param descDomain 收敛后的目标域名
     * @return
     */
    public static String convertImageUrl(String url, boolean toWebp, boolean toHttps, boolean domainUnit, String descDomain, ImageSizeEnum imageSizeEnum) {
        if (StringUtils.isEmpty(url)) return "";
        if (imageSizeEnum != null) {
            url = convertImage_Size(url, imageSizeEnum);
        }
        if (toWebp) {
            url = convertImage_ToWebp(url);
        }
        if (toHttps) {
            url = convertImage_ToHttps(url);
        } else {
            url = convertImage_ToHttp(url);
        }
        if (domainUnit) {
            url = StringUtils.hasLength(descDomain) ? convertImage_DomainUnit(url, descDomain) : convertImage_DomainUnit(url);
        }
        return url;
    }


    /**
     * 图片转换（支持转Webp、https/http、域名收敛）
     *
     * @param url          图片地址
     * @param toWebp       是否转为 webp 格式
     * @param toHttps      是否转Https，false时，协议会统一转为http
     * @param domainUnit   是否域名收敛，true时，默认域名为 app2.autoimg.cn
     * @param delQuality   是否删除质量参数
     * @param delCrop      是否删除裁剪参数
     * @param delWatermark 是否删除水印参数
     * @return 处理完成后的url
     */

    public static String convertImageUrl(String url, boolean toWebp, boolean toHttps, boolean domainUnit, ImageSizeEnum imageSizeEnum, boolean delQuality, boolean delCrop, boolean delWatermark) {
        if (StringUtils.isEmpty(url)) return "";
        if (imageSizeEnum != null) {
            url = convertImage_Size(url, imageSizeEnum);
        }
        if (toWebp) {
            url = convertImage_ToWebp(url);
        }
        if (toHttps) {
            url = convertImage_ToHttps(url);
        } else {
            url = convertImage_ToHttp(url);
        }
        if (domainUnit) {
            url = convertImage_DomainUnit(url);
        }
        url = delOpt(url, delQuality, delCrop, delWatermark);
        return url;
    }

    /**
     * 删除url中的特定参数
     *
     * @param url          原始地址URL
     * @param delQuality   是否删除质量参数
     * @param delCrop      是否删除裁剪参数
     * @param delWatermark 是否删除水印参数
     * @return 处理完成后的url
     */
    public static String delOpt(String url, boolean delQuality, boolean delCrop, boolean delWatermark) {
        if (!StringUtils.hasLength(url) || (!delCrop && !delQuality && !delWatermark)) {
            return url;
        }
        int imgIndex = url.lastIndexOf(StrPool.SLASH);
        String prefix = url.substring(0, imgIndex);
        String suffix = url.substring(imgIndex);
        int endIndex = suffix.indexOf("_autohomecar__");
        // 类似下放URL的地址不存在需要处理的参数, 故直接返回
        // http://www2.autoimg.cn/newsdfs/g27/M08/BC/84/autohomecar__CjIFVWSn95uAA5vsAAB9IPh_CuI306.jpg.webp
        if (endIndex == -1) {
            return url;
        }
        String substring = suffix.substring(0, endIndex);
        String strSuffix = suffix.substring(endIndex);
        String[] optSplit = substring.split(StrPool.UNDERLINE);
        List<String> prefixList = new ArrayList<>(optSplit.length);
        for (String str : optSplit) {
            boolean canAdd = !delQuality || !str.startsWith("q");
            if (delCrop && str.startsWith("c")) {
                canAdd = false;
            }
            if (delWatermark && str.equals("0")) {
                canAdd = false;
            }
            if (canAdd) {
                prefixList.add(str);
            }
        }
        String collect = String.join(StrPool.UNDERLINE, prefixList);
        return prefix + collect + strSuffix;
    }

//    /**
//     * 获取webp图片
//     * apollo配置并且图片的域名是club2|club3
//     */
//    public static String getWebpImgs(String imgs, String version) {
//        String webpImgs = "";
//        if (StringUtils.hasLength(imgs)) {
//            if (imgs.contains(",")) {
//                String imgArr[] = imgs.split(",");
//                for (String img : imgArr) {
//                    String webpImg = "";
//                    if (img.contains("userphotos/")) {
//                        String dd = img.split("userphotos/")[1];
//                        if (dd.length() > 0 && dd.contains("/")) {
//                            String d = dd.substring(0, dd.indexOf("/"));
//                            try {
//                                if (Integer.valueOf(d) > 2017) {
//                                    webpImg = getWebpImg(img, version);
//                                } else {
//                                    webpImg = img;
//                                }
//                            } catch (Exception e) {
//                                webpImg = img;
//                            }
//
//                        }
//                    }
//                    webpImgs = webpImgs + webpImg + ",";
//                }
//                return webpImgs.length() > 0 ? webpImgs.substring(0, webpImgs.length() - 1) : "";
//            } else {
//                return getWebpImg(imgs, version);
//            }
//        } else {
//            return imgs;
//        }
//    }

    /**
     * 获取webp图片
     * apollo配置并且图片的域名是club2|club3
     */
//    public static String getWebpImg(String img, String version) {
//        //webp开启标识【1：开启，0：未开启】
//        String WEBP_SWITCH = ConfigService.getAppConfig().getProperty("webp_switch", "0");
//        //webp客户端开启版本【默认990开启】
//        String WEBP_START_APPVERSION = ConfigService.getAppConfig().getProperty("webp_start_appversion", "9.8.5");
//        //webp正则
//        String WEBP_REX = ConfigService.getAppConfig().getProperty("webp_rex", "");
//        //webp域名
//        String WEBP_DOMAIN = ConfigService.getAppConfig().getProperty("webp_domain", "");
//
//        if (
//                org.apache.commons.lang3.StringUtils.isNoneBlank(img)
//                        &&
//                        WEBP_SWITCH.equals("1")
//                        &&
//                        Pattern.matches(WEBP_DOMAIN, img)
//                        &&
//                        !Pattern.matches(WEBP_REX, img)
//
//        ) {
//            return img + ".webp";
//        } else {
//            return img;
//        }
//    }

//    public static String getImgFileSizeStr(long size) {
//        String str = "";
//        if (size <= 0) {
//            return str;
//        }
//        if (size >= 1024 * 1024 * 1024) {
//            str = NumberUtil.div(size + "", 1073741824 + "", 1).doubleValue() + "G";
//        } else if (size >= 1024 * 1024) {
//            str = NumberUtil.div(size + "", 1048576 + "", 1).doubleValue() + "M";
//        } else if (size >= 1024) {
//            str = NumberUtil.div(size + "", 1024 + "", 1).doubleValue() + "K";
//        } else {
//            str = size + "B";
//        }
//        return str;
//    }

    /**
     * 在路径中添加 k1 规则
     *
     * @param url 图片URL
     * @return 处理后的图片
     */
    public static String addK1ToPath(String url) {
        if (url.contains("autohomecar__") && !url.contains("k1_autohomecar__")) {
            return url.replace("autohomecar__", "k1_autohomecar__");
        }
        return url;
    }

    public static String getFullImagePathWithoutReplace(String path) {
        if (org.apache.commons.lang3.StringUtils.isBlank(path)) {
            return path;
        }
        if(path.startsWith("http://") || path.startsWith("https://")){
            return path;
        }
        return String.format("%s%s", getImageDomain(path), path.replace("~", ""));
    }

    /**
     * 通过图片路径获取图片域名
     * @param path
     * @return
     */
    private static String getImageDomain(String path) {
        if (org.apache.commons.lang3.StringUtils.isBlank(path)) {
            return path;
        }
        int r = 0, b = 0;
        while ((r += 4) < path.length()) { b ^= path.charAt(r); }
        b %= 2;
        return String.format("https://car%s.autoimg.cn", path.contains("/cardfs/") ? String.valueOf(b + 2): "0");
    }

    /**
     * 论坛图片尺寸转换
     *
     * @param imgUrl
     * @param imageSizeEnum
     * @return
     */
    public static String convertImage_Size_VRImg(String imgUrl, ImageSizeEnum imageSizeEnum) {
        String url = "";
        //"640x0_autohomecar__"
        if (!StringUtils.isEmpty(imgUrl)) {
            String prefix = org.apache.commons.lang3.StringUtils.substringBeforeLast(imgUrl, "/");
            String after = org.apache.commons.lang3.StringUtils.substringAfterLast(imgUrl, "/");
            url = String.format("%s/%sautohomecar__%s.webp", prefix, imageSizeEnum.getSize(), after);
        }
        return url;
    }

    public static String getFullImagePath(String path) {
        if (org.apache.commons.lang3.StringUtils.isBlank(path)) {
            return "";
        }
        path = path.replace("~", "");
        return String.format("%s%s", getImageDomain(path), path);
    }

    /**
     * 获取当前图片url的宽高
     * @param url 图片地址
     * @return int[]
     */
    public static int[] getWidthAndHeight(String url) {
        int[] result = new int[2];
        try {
            int firstIndex = url.lastIndexOf("/") + 1;
            int lastIndex = url.indexOf("autohomecar__");
            String substring = url.substring(firstIndex, lastIndex);
            substring = substring.substring(0, substring.indexOf("_"));
            if (StringUtils.hasLength(substring) && substring.contains("x")) {
                String[] split = substring.split("x");
                if (split.length == 2) {
                    int width = Integer.parseInt(split[0]);
                    int height = Integer.parseInt(split[1]);
                    result[0] = width;
                    result[1] = height;
                }
            }
        } catch (Exception e) {
            log.warn("获取图片宽高出错");
        }
        return result;
    }
}
