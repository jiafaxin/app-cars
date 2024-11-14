package com.autohome.app.cars.apiclient.vr.dtos;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class SeriesVrExteriorResult {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int specId;
    
    @JSONField(name = "isShow")
    private boolean is_show;

    @JSONField(name = "jumpUrl")
    private String jump_url = "";
    private int narration;
    @JSONField(name = "adurlandroid")
    private String adurl_android;
    @JSONField(name = "adurlios")
    private String adurl_ios;
    @JSONField(name = "colorList")
    private List<Color_List> color_list;
    @JSONField(name = "vrinfo_backgroudImg")
    private String vrinfo_backgroudImg="";
    private String taglabel;
    private String superspeclinkurl = "";
    private int showtype;// 1 - 正常全景 2- 超级车型库,默认是1；
    private int iscloud;
    private String specname;
    private int is3dpk;

    private int specState;
    private int vrspecstate;

    private boolean issuperspeclinkurl;

    /**
     * bool类型主动声明get set
     *
     * @return
     */
    public boolean isIs_show() {
        return is_show;
    }

    public void setIs_show(boolean is_show) {
        this.is_show = is_show;
    }

    public static class Color_List implements Serializable {

        @JSONField(name = "id")
        private int Id;
        @JSONField(name = "remoteColorId")
        private int RemoteColorId;
        @JSONField(name = "baseColorName")
        private String BaseColorName;
        private String ColorName;
        private String ColorValue;
        private String ColorNames;
        private String ColorValues;
        private int ColorId;
        private Hori Hori;
        private Over Over;

        public String getColorNames() {
            return ColorNames;
        }

        public void setColorNames(String colorNames) {
            ColorNames = colorNames;
        }

        public String getColorValues() {
            return ColorValues;
        }

        public void setColorValues(String colorValues) {
            ColorValues = colorValues;
        }

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getBaseColorName() {
            return BaseColorName;
        }

        public void setBaseColorName(String baseColorName) {
            BaseColorName = baseColorName;
        }

        public String getColorName() {
            return ColorName;
        }

        public void setColorName(String colorName) {
            ColorName = colorName;
        }

        public String getColorValue() {
            return ColorValue;
        }

        public void setColorValue(String colorValue) {
            ColorValue = colorValue;
        }

        public int getColorId() {
            return ColorId;
        }

        public void setColorId(int colorId) {
            ColorId = colorId;
        }

        public Hori getHori() {
            return Hori;
        }

        public void setHori(Hori hori) {
            Hori = hori;
        }

        public Over getOver() {
            return Over;
        }

        public void setOver(Over over) {
            Over = over;
        }

        public int getRemoteColorId() {
            return RemoteColorId;
        }

        public void setRemoteColorId(int remoteColorId) {
            RemoteColorId = remoteColorId;
        }
    }

    public static class Hori implements Serializable {
        private List<PicMaterial> Normal;
        private List<PicMaterial> Preview;

        public List<PicMaterial> getNormal() {
            return Normal;
        }

        public void setNormal(List<PicMaterial> normal) {
            Normal = normal;
        }

        public List<PicMaterial> getPreview() {
            return Preview;
        }

        public void setPreview(List<PicMaterial> preview) {
            Preview = preview;
        }
    }

    public static class Over implements Serializable {
        private List<PicMaterial> Normal;
        private List<PicMaterial> Preview;

        public List<PicMaterial> getNormal() {
            return Normal;
        }

        public void setNormal(List<PicMaterial> normal) {
            Normal = normal;
        }

        public List<PicMaterial> getPreview() {
            return Preview;
        }

        public void setPreview(List<PicMaterial> preview) {
            Preview = preview;
        }
    }

    public static class PicMaterial implements Serializable {
        private int Seq;
        private String Url;
        List<PointInfo> pointinfo = new ArrayList<>();

        public List<PointInfo> getPointinfo() {
            return pointinfo;
        }

        public void setPointinfo(List<PointInfo> pointinfo) {
            this.pointinfo = pointinfo;
        }

        public int getSeq() {
            return Seq;
        }

        public void setSeq(int seq) {
            Seq = seq;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }
    }

    public static class PointInfo {
        private int locationid;

        private String name;

        private String linkurl;

        public int getLocationid() {
            return locationid;
        }

        public void setLocationid(int locationid) {
            this.locationid = locationid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }
    }

}