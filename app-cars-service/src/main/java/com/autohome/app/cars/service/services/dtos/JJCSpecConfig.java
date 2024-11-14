package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzli
 * @description : 降价朝配置数据2023
 * @date : 2024/3/14 19:58
 */
@Data
public class JJCSpecConfig {
    private static final Logger logger = LoggerFactory.getLogger(JJCSpecConfig.class);

    private Integer isopen;

    private List<SpecList> speclist = new ArrayList<>();

    public static JJCSpecConfig createFromJson(String json) {
        if (StringUtils.isBlank(json))
            return null;
        try {
            return JsonUtil.toObject(json, JJCSpecConfig.class);
        } catch (Exception e) {
            logger.error("JJCSpecConfig 序列化失败", e);
            return null;
        }
    }

    @Data
    public static class SpecList {
        private Integer brandid;

        private Integer butiePrice;

        private String endtime;

        private String pname;

        private Integer price;

        private Integer seriesid;

        private String seriesname;

        private Integer specid;

        private String specname;
    }
}
