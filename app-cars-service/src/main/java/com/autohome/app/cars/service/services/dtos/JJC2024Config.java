package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Data
public class JJC2024Config {

    private static final Logger logger = LoggerFactory.getLogger(JJC2024Config.class);
    private String title;
    private List<Integer> serieslist;
    private String linkurl;
    private String icon;
    private Integer isopen;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end;

    public boolean isOpen(int seriesId){
        if(getIsopen()!=1)
            return false;
        if(start.after(new Date()) || end.before(new Date())){
            return false;
        }
        if(serieslist==null||serieslist.size()==0) {
            return false;
        }
        return serieslist.contains(seriesId);
    }

    public static JJC2024Config createFromJson(String json){
        if(StringUtils.isBlank(json))
            return null;
        try {
            return JsonUtil.toObject(json, JJC2024Config.class);
        }catch (Exception e){
            logger.error("Jjc2024Config 序列化失败",e);
            return null;
        }

    }
}
