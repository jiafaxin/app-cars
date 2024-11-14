package com.autohome.app.cars.apiclient.video.dtos;

import lombok.Data;

import java.util.List;

@Data
public class MainDataInfoResult {

    /**
     * result : [{"like_count":12,"reply_count":2,"biz_id":17257145,"pv":126299,"main_data_type":"chejiahao"},{"like_count":280,"reply_count":9,"biz_id":2247609,"pv":14009,"main_data_type":"video"}]
     * message : ok
     * returncode : 0
     */

    private String message;
    private int returncode;
    private List<ResultBean> result;

    @Data
    public static class ResultBean {
        /**
         * like_count : 12
         * reply_count : 2
         * biz_id : 17257145
         * pv : 126299
         * main_data_type : chejiahao
         */

        private int like_count;
        private int reply_count;
        private int biz_id;
        private int pv;
        private String main_data_type;
        private String author_name;
    }
}
