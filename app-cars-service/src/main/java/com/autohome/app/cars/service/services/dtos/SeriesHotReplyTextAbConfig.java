package com.autohome.app.cars.service.services.dtos;

/**
 * Created by dx on 2024/5/14
 * 车系热评文案配置实体
 */
public class SeriesHotReplyTextAbConfig {
    /**
     * [{
     * "abversion": "A",
     * "firsttabname": "热评",
     * "secondtabname": "车系热评",
     * "secondtabnameicon": "http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/serieshot.webp",
     * "thirdbuttontext": "条热评"
     * },
     * {
     * "abversion": "B",
     * "firsttabname": "评论",
     * "secondtabname": "车系评论",
     * "secondtabnameicon": "http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/cxpl_0514.webp",
     * "thirdbuttontext": "条回复"
     * }
     * ]
     */
    private String abversion;
    private String firsttabname;
    private String secondtabname;
    private String secondtabnameicon;
    private String thirdbuttontext;

    public String getAbversion() {
        return abversion;
    }

    public void setAbversion(String abversion) {
        this.abversion = abversion;
    }

    public String getFirsttabname() {
        return firsttabname;
    }

    public void setFirsttabname(String firsttabname) {
        this.firsttabname = firsttabname;
    }

    public String getSecondtabname() {
        return secondtabname;
    }

    public void setSecondtabname(String secondtabname) {
        this.secondtabname = secondtabname;
    }

    public String getSecondtabnameicon() {
        return secondtabnameicon;
    }

    public void setSecondtabnameicon(String secondtabnameicon) {
        this.secondtabnameicon = secondtabnameicon;
    }

    public String getThirdbuttontext() {
        return thirdbuttontext;
    }

    public void setThirdbuttontext(String thirdbuttontext) {
        this.thirdbuttontext = thirdbuttontext;
    }
}
