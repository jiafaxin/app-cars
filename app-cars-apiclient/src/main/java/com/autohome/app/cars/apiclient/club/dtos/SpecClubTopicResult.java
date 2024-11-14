package com.autohome.app.cars.apiclient.club.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchengtao
 * @date 2024/8/19 21:32
 */
@NoArgsConstructor
@Data
public class SpecClubTopicResult {

        private Integer topicid;
        private String title;
        private Integer postMemberid;
        private String postdate;
        private Integer ispoll;
        private Integer isdelete;
        private Integer bbsid;
        private String bbs;
        private String bbsname;
        private String summary;
        private String pubprovincename;
        private String subtitle;
        private Integer wordcount;
}
