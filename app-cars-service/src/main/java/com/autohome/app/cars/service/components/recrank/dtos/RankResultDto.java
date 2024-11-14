package com.autohome.app.cars.service.components.recrank.dtos;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.service.components.recrank.enums.RankError;
import com.autohome.app.cars.service.services.dtos.PvItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class RankResultDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 7879619347205510070L;

    private int cacheable = 1;
    private int cdncachesecond = 0;
    private int returncode = 0;
    private String message = "SUCCESS";
    private ResultDTO result = new ResultDTO();

    @Data
    public static class ResultDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 8485940833542762709L;
        private int pageindex;
        private int pagecount;
        private int pagesize;
        private String saleranktip;
        private String morescheme;
        private String scenetitle = "";
        private String scenesubtitle = "";
        private ShareinfoDTO shareinfo;
        /**
         * 榜单异常信息
         */
        private JSONObject error;
        private List<ListDTO> list = new ArrayList<>();

        private String emptyreason;


        public void setError(RankError error) {
            JSONObject errorObj = new JSONObject();
            errorObj.put("error_code", error.error_code);
            errorObj.put("reason", error.reason);
            this.error = errorObj;
        }

        @Data
        public static class ShareinfoDTO implements Serializable {
            @Serial
            private static final long serialVersionUID = 8672366906689404395L;
            private String title;
            private String subtitle;
            private String url;
            private String logo;
        }


    }

    @Data
    public static class ListDTO implements Serializable {


        @Serial
        private static final long serialVersionUID = -6334660456280570051L;
        private int cardtype;
        private String seriesid;
        private String rank;
        private String levelId;
        private int rankNum;
        private int rankchange;
        private int showrankchange;
        private String seriesimage;
        private String seriesname;
        private String scorevalue;
        private String scoretip;
        private String priceinfo;
        private String linkurl;
        private Long salecount;
        private String historylinkname;
        private String historylinkurl;
        private String rightpricetitle;
        private String pricelinkurl;
        private String rcmtext;
        private String rcmdesc;

        private String rcmlinkurl;

        private String rcmrighttitle;
        private String specname;
        private String righttextone;
        private String righttexttwo;
        private String righttexttwolinkurl;
        private int isshowscorevalue;
        private RightinfoDTO rightinfo;

        private String routecycle;//路线周期
        private String routemileage;//路线里程
        private String recommendreason;//推荐理由
        private PvItem pvitem;
        private List<String> saferesultlist;

        private ArticleInfo articleinfo;

        private TechnologyInfo technologyinfo;
        private Integer hasbusinesscar;

        private String tag;

        private Integer brandid;
        private String brandname;
        private String brandimage;
        private int showenergyicon;

        private Integer energytype;

        private Integer ishiderank;
        private Integer rnbtn;

        private String seriestag;

        private int rcmtype;

        private String rcmpic;

        private PvItem rcmpvitem;

        private int minPrice;
        private int maxPrice;
        /**
         * 厂商类型
         */
        private String manuType;

        /**
         * 能源类型
         */
        private String fuelTypes;
        /**
         * 本月排名 销量相同会重复
         */
        private int rn;
        /**
         * 本月排名 销量相同不会重复
         */
        private int rnNum;
        /**
         * 上月排名
         */
        private int preRankNum;

        /**
         * 车系销售状态
         */
        private int state;

        private String energySaleCount;

        /**
         * 存在销量的能源类型
         */
        private String energyTypes = "";

        @JsonProperty("rcmlist")
        private List<RcmlistDTO> rcmlist = new ArrayList<>();
        @JsonProperty("safeitemlist")
        private List<SafeitemlistDTO> safeitemlist = new ArrayList<>();
        private RankShareParamInfo shareinfo = new RankShareParamInfo();
    }

    @Data
    public static class RightinfoDTO {

        private String righttextone;
        private String righttexttwo;
        private String righttexttwolinkurl;
        private String rightpricetitle = "";
        private String rightpriceurl = "";

        private String rightdefurl;
        private String rightpriceeid = "";
        private PvItem pvitem;
        private String ext = "";

        private PriceInfoDto priceinfo = new PriceInfoDto();

        @Data
        public static class PriceInfoDto {

            /**
             * title : 查成交价
             * eid : 3|1411002|572|25528|205883|305153
             * linkurl : autohome://car/asklowprice?customshowanimationtype=2&eid=3%7C1411001%7C48%7C33%7C200014%7C300000&seriesid=526&specid=63170&objectid=63170&inquirytype=2&price_show=6&isother=0&title=%E8%8E%B7%E5%8F%96%E5%BA%95%E4%BB%B7
             * ext : {"ab_test":"","algo":"landing_page_inquirylist_ee,landing_page_inquirybar_ctnet","exp":"","final_score":0,"price_show":6,"price_text":"","pvid":"sact_9baa745584074b0aba4f87ae2de09c91","series_id":526,"sid":"3211960df6ac37e4ae4c3d396f2be689","spec_id":63170,"subtitle":"","target_id":"1","title":"获取底价","trace_type":101,"link":"autohome://car/asklowprice?customshowanimationtype=2&eid=3%7C1411001%7C48%7C33%7C200014%7C300000&seriesid=526&specid=63170&objectid=63170&inquirytype=2&price_show=6&isother=0&title=%E8%8E%B7%E5%8F%96%E5%BA%95%E4%BB%B7"}
             */

            private String title;
            private String eid;
            private String linkurl;
            private String ext;
        }
    }


    @Data
    public static class TechnologyInfo {
        private Integer technologyid = 0;
        private String title = "";
        private String subtitle = "";
        private String linkurl = "";
        private PvItem pvitem = new PvItem();
    }

    @Data
    public static class ArticleInfo {
        private String title = "";
        private String linkurl = "";
        private PvItem pvitem = new PvItem();

    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class RcmlistDTO implements Serializable {
        private String text;
        private String desc;
        private String righttitle = "";
        private String linkurl = "";

        public static RcmlistDTO getInstance(String text, String desc, String righttitle, String linkurl) {
            return new RcmlistDTO(text, desc, righttitle, linkurl);
        }
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class SafeitemlistDTO {
        private String text;
        private String desc;
        private String desccolor;

        public static SafeitemlistDTO getInstance(String text, String desc, String descColor) {
            return new SafeitemlistDTO(text, desc, descColor);
        }
    }
}
