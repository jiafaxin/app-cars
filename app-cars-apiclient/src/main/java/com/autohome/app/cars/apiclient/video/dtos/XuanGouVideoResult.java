package com.autohome.app.cars.apiclient.video.dtos;

import lombok.Data;

import java.util.List;

@Data
public class XuanGouVideoResult {


    /**
     * returncode : 0
     * message :
     * result : {"havemore":false,"list":[{"id":1362637,"main_data_type":"chejiahao","obj_id":"17257145","subject_id":10090,"module_id":331,"publish_time":"2024/10/08 13:05:19","main_data_json":{},"title":"给豪华品牌上一课！仰望U8智驾体验，主打稳健又好用","biz_id":17257145,"biz_update_time":"2024/10/08 15:46:38","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M00/76/1C/autohomecar__Chtk2WcEu3WAUHpyAAQ4PzIdOtM41.jpeg","multi_images":[],"author_id":15781968,"video_type":0,"duration":496,"video_source":"84824EB5D2F4A50C6F15C4841F4F2CE2","parent_biz_id":3,"is_close_comment":false},{"id":1348265,"main_data_type":"video","obj_id":"2247609","subject_id":10090,"module_id":331,"publish_time":"2024/09/29 10:00:00","main_data_json":{},"title":"红旗汽车崛起之路·10月1日献礼国庆","biz_id":2247609,"biz_update_time":"2024/09/29 15:53:00","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M06/72/E2/autohomecar__ChxkPmb2Zk2Ae9CKAASwhlXGmb859.jpeg","multi_images":[],"author_id":268471927,"video_direction":1,"duration":55,"video_source":"8F5D0A269BB7F76F6F15C4841F4F2CE2","parent_biz_id":8,"is_close_comment":false},{"id":1344723,"main_data_type":"chejiahao","obj_id":"16984097","subject_id":10090,"module_id":331,"publish_time":"2024/09/21 17:59:38","main_data_json":{},"title":"全新岚图梦想家油耗测试，重2.6吨的车到底省油吗？","biz_id":16984097,"biz_update_time":"2024/09/21 18:08:26","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M05/DB/55/autohomecar__Chtk2WbumOKAakRPAAeKtd6LEmk20.jpeg","multi_images":[],"author_id":190416381,"video_type":0,"duration":188,"video_source":"FE1B93BA16ACB39B6F15C4841F4F2CE2","parent_biz_id":3,"is_close_comment":false},{"id":1344749,"main_data_type":"chejiahao","obj_id":"16393881","subject_id":10090,"module_id":331,"publish_time":"2024/08/19 11:37:57","main_data_json":{},"title":"冰箱彩电大沙发，还有三排座椅电动调节，静态体验新款岚图梦想家","biz_id":16393881,"biz_update_time":"2024/08/19 15:27:17","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M04/D0/0F/autohomecar__ChxkPWbCu-iACJP6AANouT1iR2U90.jpeg","multi_images":[],"author_id":13487686,"video_type":0,"duration":643,"video_source":"2B7FB15FBAA59ACB6F15C4841F4F2CE2","parent_biz_id":3,"is_close_comment":false}],"pageid":"2024/08/19 11:37:57,1344749"}
     */

    private int returncode;
    private String message;
    private ResultBean result;


    @Data
    public static class ResultBean {
        /**
         * havemore : false
         * list : [{"id":1362637,"main_data_type":"chejiahao","obj_id":"17257145","subject_id":10090,"module_id":331,"publish_time":"2024/10/08 13:05:19","main_data_json":{},"title":"给豪华品牌上一课！仰望U8智驾体验，主打稳健又好用","biz_id":17257145,"biz_update_time":"2024/10/08 15:46:38","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M00/76/1C/autohomecar__Chtk2WcEu3WAUHpyAAQ4PzIdOtM41.jpeg","multi_images":[],"author_id":15781968,"video_type":0,"duration":496,"video_source":"84824EB5D2F4A50C6F15C4841F4F2CE2","parent_biz_id":3,"is_close_comment":false},{"id":1348265,"main_data_type":"video","obj_id":"2247609","subject_id":10090,"module_id":331,"publish_time":"2024/09/29 10:00:00","main_data_json":{},"title":"红旗汽车崛起之路·10月1日献礼国庆","biz_id":2247609,"biz_update_time":"2024/09/29 15:53:00","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M06/72/E2/autohomecar__ChxkPmb2Zk2Ae9CKAASwhlXGmb859.jpeg","multi_images":[],"author_id":268471927,"video_direction":1,"duration":55,"video_source":"8F5D0A269BB7F76F6F15C4841F4F2CE2","parent_biz_id":8,"is_close_comment":false},{"id":1344723,"main_data_type":"chejiahao","obj_id":"16984097","subject_id":10090,"module_id":331,"publish_time":"2024/09/21 17:59:38","main_data_json":{},"title":"全新岚图梦想家油耗测试，重2.6吨的车到底省油吗？","biz_id":16984097,"biz_update_time":"2024/09/21 18:08:26","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M05/DB/55/autohomecar__Chtk2WbumOKAakRPAAeKtd6LEmk20.jpeg","multi_images":[],"author_id":190416381,"video_type":0,"duration":188,"video_source":"FE1B93BA16ACB39B6F15C4841F4F2CE2","parent_biz_id":3,"is_close_comment":false},{"id":1344749,"main_data_type":"chejiahao","obj_id":"16393881","subject_id":10090,"module_id":331,"publish_time":"2024/08/19 11:37:57","main_data_json":{},"title":"冰箱彩电大沙发，还有三排座椅电动调节，静态体验新款岚图梦想家","biz_id":16393881,"biz_update_time":"2024/08/19 15:27:17","img_url":"https://www2.autoimg.cn/chejiahaodfs/g32/M04/D0/0F/autohomecar__ChxkPWbCu-iACJP6AANouT1iR2U90.jpeg","multi_images":[],"author_id":13487686,"video_type":0,"duration":643,"video_source":"2B7FB15FBAA59ACB6F15C4841F4F2CE2","parent_biz_id":3,"is_close_comment":false}]
         * pageid : 2024/08/19 11:37:57,1344749
         */

        private boolean havemore;
        private String pageid;
        private List<ListBean> list;

        @Data
        public static class ListBean {
            /**
             * id : 1362637
             * main_data_type : chejiahao
             * obj_id : 17257145
             * subject_id : 10090
             * module_id : 331
             * publish_time : 2024/10/08 13:05:19
             * main_data_json : {}
             * title : 给豪华品牌上一课！仰望U8智驾体验，主打稳健又好用
             * biz_id : 17257145
             * biz_update_time : 2024/10/08 15:46:38
             * img_url : https://www2.autoimg.cn/chejiahaodfs/g32/M00/76/1C/autohomecar__Chtk2WcEu3WAUHpyAAQ4PzIdOtM41.jpeg
             * multi_images : []
             * author_id : 15781968
             * video_type : 0
             * duration : 496
             * video_source : 84824EB5D2F4A50C6F15C4841F4F2CE2
             * parent_biz_id : 3
             * is_close_comment : false
             * video_direction : 1
             */

            private int id;
            private String main_data_type;
            private String obj_id;
            private int subject_id;
            private int module_id;
            private String publish_time;
            private MainDataJsonBean main_data_json;
            private String title;
            private int biz_id;
            private String biz_update_time;
            private String img_url;
            private int author_id;
            private int video_type;
            private int duration;
            private String video_source;
            private int parent_biz_id;
            private boolean is_close_comment;
            private int video_direction;
            private List<?> multi_images;


            @Data
            public static class MainDataJsonBean {
            }
        }
    }
}
