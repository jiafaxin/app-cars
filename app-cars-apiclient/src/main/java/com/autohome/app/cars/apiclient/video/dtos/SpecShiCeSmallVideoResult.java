package com.autohome.app.cars.apiclient.video.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SpecShiCeSmallVideoResult {

    /**
     * message : 请求成功
     * result : [{"mjump":"https://v.m.autohome.com.cn/small/v-14725038.html","vs_id":14725038,"title":"今天测试这台高功率版奥迪A4L的加速性能，看看它香不香？","video_img":"https://img2.autoimg.cn/svcovers/g24/M01/F6/24/ChwFjl7NK_KAJvB7AAFcVi3dwt8090.jpg","series_id":692,"spec_id":44088,"tag_id":30050011018,"app_jump":"autohome://article/shortvideolist?source=78&loadmodel=0&bsid=17&bsname=ZiXunYuanChuang&id=14725038","m_jump":"https://v.m.autohome.com.cn/small/v-14725038.html"},{"mjump":"https://v.m.autohome.com.cn/small/v-14762706.html","vs_id":14762706,"title":"这台顶配的奥迪A4L居然有跑车级操控？ #奥迪a4l #麋鹿测试 ","video_img":"https://img2.autoimg.cn/svcovers/g24/M0A/A2/B1/ChwFjl7PtoCADuIhAAFIbtmdXYg894.jpg","series_id":692,"spec_id":44088,"tag_id":30050011020,"app_jump":"autohome://article/shortvideolist?source=78&loadmodel=0&bsid=17&bsname=ZiXunYuanChuang&id=14762706","m_jump":"https://v.m.autohome.com.cn/small/v-14762706.html"}]
     * returncode : 0
     */

    private String message;
    private int returncode;
    private List<ResultBean> result;

    @Data
    public static class ResultBean {
        private int spec_id;
        private long tag_id;
        private String app_jump;
    }
}
