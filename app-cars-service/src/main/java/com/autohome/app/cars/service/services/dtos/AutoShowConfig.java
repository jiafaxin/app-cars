package com.autohome.app.cars.service.services.dtos;

import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AutoShowConfig {

    private static final Logger logger = LoggerFactory.getLogger(AutoShowConfig.class);

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoshowstart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoshowend;
    private int autoshowid;
    private String autoshowname;
    private String autoshowedgebtn;

    private String beanTitle;
    private String beanSubTitle;

    private String minusbgcolor;

    private List<Integer> allow_brandlist=new ArrayList<>();
    /**
     * -1:没有初始化；
     * 1：正确；
     * 0：错误；
     */
    private boolean _isBetweenDate = false;

    private BrandModel brand;
    private SeriesModel series;

    private BgModel bg;

    public boolean IsBetweenDate() {
        if (this.autoshowstart == null || this.autoshowend == null) {
            _isBetweenDate = false;
        } else {
            Date now = new Date();
            long nowTime = now.getTime();
            try {
                long startTime = this.autoshowstart.getTime();
                long endTime = this.autoshowend.getTime();
                if (nowTime >= startTime && nowTime <= endTime) {
                    _isBetweenDate = true;
                } else {
                    _isBetweenDate = false;
                }
            } catch (Exception e) {
                _isBetweenDate = false;
            }
        }
        return _isBetweenDate;
    }

    public AutoShowConfig() {
    }


    public static AutoShowConfig decodeAutoShowConfig(String json) {
        try {
            if (json == null || json.equals("")) {
                return new AutoShowConfig();
            }
            return JsonUtil.toObject(json,AutoShowConfig.class);
        } catch (Exception e) {
            logger.error("AutoShowConfig 序列化失败",e);
            return new AutoShowConfig();
        }
    }

    /**
     * 根据品牌-车车系列表的时候，出车展logo；
     */
    @Data
    public static class SeriesModel {
        /**
         * 51-新车上市
         */
        private String logo51;
        /**
         * 52-首发新车
         */
        private String logo52;
        /**
         * 新增53-抢先实测
         */
        private String logo53;

        private String imagewidth;
        private String imgheight;
    }

    @Data
    public static class BrandModel {
        private String imageurl;
        private String imagewidth;
        private String imgheight;
    }

    @Data
    public static class BgModel {
        private String aheadbg;
        private String bheadbg;
        private String boothbg;
    }

}
