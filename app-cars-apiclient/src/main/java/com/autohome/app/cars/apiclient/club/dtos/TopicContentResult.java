package com.autohome.app.cars.apiclient.club.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author : zzli
 * @description : 帖子列表
 * @date : 2024/6/7 20:52
 */
@NoArgsConstructor
@Data
public class TopicContentResult {
    private Integer pageindex;
    private Integer pagecount;
    private Integer rowcount;
    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        private Integer topicid;
        private String title;
        private Integer post_memberid;
        private String post_membername;
        private String postdate;
        private String lastreplydate;
        private String lasteditdate;
        private Integer ispic;
        private Integer ispoll;
        private Integer isrefine;
        private Integer replycount;
        private Integer viewcount;
        private Integer bbsid;
        private String bbs;
        private String bbsname;
        private String url;
        private Integer issolve;
        private Integer isjingxuan;
        private String jximgs;
        private String imgs;
        private String summary;
        private Integer piccount;
        private Integer isvideo;
        private String videoid;
        private VideoinfoDTO videoinfo;

        @NoArgsConstructor
        @Data
        public static class VideoinfoDTO {
            private String videoid;
            private String videoimg;
            private Integer duration;
            private List<TaglistDTO> taglist;

            @NoArgsConstructor
            @Data
            public static class TaglistDTO {
                private Integer tagid;
                private String tagname;
            }
        }
    }
}
