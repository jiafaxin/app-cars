package com.autohome.app.cars.apiclient.baike.dtos;

import java.util.List;

public class BaikeInfoResult {

    private int returncode;
    private String message;
    private List<ResultBean> result;

    public int getReturncode() {
        return returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * sublist : []
         * id : 122
         * name : 多媒体/充电接口
         * link : https://car.autohome.com.cn/baike/detail_8_30_122.html
         * mlink : https://car.m.autohome.com.cn/baike/detail_8_30_122.html
         * firstpartcnt :       [汽车之家 百科]    多媒体/充电接口是指车内连接外部设备的接口，有的接口支持外接音源输入功能，通过连接外接设备，比如手机、MP3/MP4，U盘，移动硬盘等，即可在车载音响中播放外接设备的影音节目。另一种接口是充电接口，只能负责给外接设备进行充电，不支持音源输入。目前常用的多媒体/充电接口形式有：USB、AUX、SD、HDMI、Type-c等。
         * mid : null
         * repImg : https://car2.autoimg.cn/cardfs/newcarbig/g2/M07/6A/E4/autohomecar__ChcCRFt2n2KAUkDSAABy742a33g922.jpg
         * videocover : null
         */

        private int id;
        private String name;
        private String link;
        private String mlink;
        private String firstpartcnt;
        private Object mid;
        private String repImg;
        private Object videocover;
        private List<?> sublist;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getMlink() {
            return mlink;
        }

        public void setMlink(String mlink) {
            this.mlink = mlink;
        }

        public String getFirstpartcnt() {
            return firstpartcnt;
        }

        public void setFirstpartcnt(String firstpartcnt) {
            this.firstpartcnt = firstpartcnt;
        }

        public Object getMid() {
            return mid;
        }

        public void setMid(Object mid) {
            this.mid = mid;
        }

        public String getRepImg() {
            return repImg;
        }

        public void setRepImg(String repImg) {
            this.repImg = repImg;
        }

        public Object getVideocover() {
            return videocover;
        }

        public void setVideocover(Object videocover) {
            this.videocover = videocover;
        }

        public List<?> getSublist() {
            return sublist;
        }

        public void setSublist(List<?> sublist) {
            this.sublist = sublist;
        }
    }
}
