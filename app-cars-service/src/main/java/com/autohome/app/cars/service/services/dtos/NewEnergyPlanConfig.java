package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Slf4j
public class NewEnergyPlanConfig {

    private static final Logger logger = LoggerFactory.getLogger(NewEnergyPlanConfig.class);

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end;
    private String content;
    private String linkurl;
    private String iconurl;
    private String bgurl;
    private List<Integer> allow_brandlist = new ArrayList<>();

    public static NewEnergyPlanConfig decodeNewEnergyPlanConfig(String json) {
        try {
            if (json == null || json.equals("")) {
                return null;
            }
            return JsonUtil.toObject(json, NewEnergyPlanConfig.class);
        } catch (Exception e) {
            logger.error("NewEnergyPlanConfig 序列化失败",e);
            return null;
        }
    }

    /**
     * -1:没有初始化；
     * 1：正确；
     * 0：错误；
     */
    private boolean _isBetweenDate = false;

    public boolean IsBetweenDate() {
        if (this.start == null || this.end == null) {
            return false;
        }
        Date now = new Date();
        return now.after(start) && now.before(end);
    }

}

