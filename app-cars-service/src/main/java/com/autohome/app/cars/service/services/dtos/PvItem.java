package com.autohome.app.cars.service.services.dtos;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class PvItem {
    private Map<String,String> argvs =new HashMap<>();
    private PvObj click =new PvObj();
    private PvObj show =new PvObj();

    @Data
    public static class PvObj {
        private String eventid;
        private Map<String,String> argvs =new HashMap();

        public static PvObj getInstance(String eventId, Map<String, String> args) {
            PvObj pvObj = new PvObj();
            pvObj.setEventid(eventId);
            if (Objects.nonNull(args)) {
                pvObj.setArgvs(args);
            }
            return pvObj;
        }
    }

    public static PvItem getInstance(Map<String, String> args, String clickEventId, Map<String, String> clickArgs, String showEventId, Map<String, String> showArgs) {
        PvItem pvItem = new PvItem();
        if (StringUtils.isNotBlank(clickEventId)) {
            pvItem.setClick(PvObj.getInstance(clickEventId, clickArgs));
        }
        if (StringUtils.isNotBlank(showEventId)) {
            pvItem.setShow(PvObj.getInstance(showEventId, showArgs));
        }
        if (Objects.nonNull(args) && !args.isEmpty()) {
            pvItem.setArgvs(args);
        }
        return pvItem;
    }

    public static PvItem getInstance(String clickEventId, String showEventId) {
        return getInstance(Collections.emptyMap(), clickEventId, Collections.emptyMap(), showEventId, Collections.emptyMap());
    }
}

