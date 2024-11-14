package com.autohome.app.cars.service.services.dtos;

import java.util.List;

/**
 * @author wbs
 * @date 2024/7/12
 */
public class MegaDataDto {

    private int seriesId;

    private List<Color> colorList;

    private List<Subtab> tab;

    private Videoalbum videoalbum;

    private ActionVideoInfo actionvideoinfo;

    private List<VRMegaInfo> vrmaterial;

    private List<VRMegaInfo> h5vrinfo;

    private List<Pic> piclist;

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
    }

    public List<Subtab> getTab() {
        return tab;
    }

    public void setTab(List<Subtab> tab) {
        this.tab = tab;
    }

    public Videoalbum getVideoalbum() {
        return videoalbum;
    }

    public void setVideoalbum(Videoalbum videoalbum) {
        this.videoalbum = videoalbum;
    }

    public ActionVideoInfo getActionvideoinfo() {
        return actionvideoinfo;
    }

    public void setActionvideoinfo(ActionVideoInfo actionvideoinfo) {
        this.actionvideoinfo = actionvideoinfo;
    }

    public List<VRMegaInfo> getVrmaterial() {
        return vrmaterial;
    }

    public void setVrmaterial(List<VRMegaInfo> vrmaterial) {
        this.vrmaterial = vrmaterial;
    }

    public List<VRMegaInfo> getH5vrinfo() {
        return h5vrinfo;
    }

    public void setH5vrinfo(List<VRMegaInfo> h5vrinfo) {
        this.h5vrinfo = h5vrinfo;
    }

    public List<Pic> getPiclist() {
        return piclist;
    }

    public void setPiclist(List<Pic> piclist) {
        this.piclist = piclist;
    }

    public List<Color> getColorList() {
        return colorList;
    }

    public void setColorList(List<Color> colorList) {
        this.colorList = colorList;
    }

    public static class Doorinfo {

        private List<String> anchor;
        private String videourl;
        private String icon;
        private String selicon;
        private String videoimage;

        public void setAnchor(List<String> anchor) {
            this.anchor = anchor;
        }
        public List<String> getAnchor() {
            return anchor;
        }

        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }
        public String getVideourl() {
            return videourl;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setSelicon(String selicon) {
            this.selicon = selicon;
        }
        public String getSelicon() {
            return selicon;
        }

        public void setVideoimage(String videoimage) {
            this.videoimage = videoimage;
        }
        public String getVideoimage() {
            return videoimage;
        }

    }

    public static class Fulldoorvideoinfo {

        private String videourl;
        private int videobytesize;
        private String videoimage;
        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }
        public String getVideourl() {
            return videourl;
        }

        public int getVideobytesize() {
            return videobytesize;
        }

        public void setVideobytesize(int videobytesize) {
            this.videobytesize = videobytesize;
        }

        public void setVideoimage(String videoimage) {
            this.videoimage = videoimage;
        }
        public String getVideoimage() {
            return videoimage;
        }

    }

    public static class ActionVideoInfo {
        private Doorinfo doorinfo;
        private Fulldoorvideoinfo fulldoorvideoinfo;
        private int color;
        private int videobytesize;
        private String videourl;
        private String videoimage;
        private String scheme;
        private int salestate;
        private String salestatetip;
        private String name;
        private Fullvideoinfo fullvideoinfo;
        private Megainfo megainfo;

        private int specid;

        public int getSpecid() {
            return specid;
        }

        public void setSpecid(int specid) {
            this.specid = specid;
        }

        public Doorinfo getDoorinfo() {
            return doorinfo;
        }

        public void setDoorinfo(Doorinfo doorinfo) {
            this.doorinfo = doorinfo;
        }

        public Fulldoorvideoinfo getFulldoorvideoinfo() {
            return fulldoorvideoinfo;
        }

        public void setFulldoorvideoinfo(Fulldoorvideoinfo fulldoorvideoinfo) {
            this.fulldoorvideoinfo = fulldoorvideoinfo;
        }

        public int getVideobytesize() {
            return videobytesize;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setVideobytesize(int videobytesize) {
            this.videobytesize = videobytesize;
        }

        public String getVideourl() {
            return videourl;
        }

        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }

        public String getVideoimage() {
            return videoimage;
        }

        public void setVideoimage(String videoimage) {
            this.videoimage = videoimage;
        }

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public int getSalestate() {
            return salestate;
        }

        public void setSalestate(int salestate) {
            this.salestate = salestate;
        }

        public String getSalestatetip() {
            return salestatetip;
        }

        public void setSalestatetip(String salestatetip) {
            this.salestatetip = salestatetip;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Fullvideoinfo getFullvideoinfo() {
            return fullvideoinfo;
        }

        public void setFullvideoinfo(Fullvideoinfo fullvideoinfo) {
            this.fullvideoinfo = fullvideoinfo;
        }

        public Megainfo getMegainfo() {
            return megainfo;
        }

        public void setMegainfo(Megainfo megainfo) {
            this.megainfo = megainfo;
        }
    }

    //"id":10324,"name":"银色金属漆","value":"#C7C7C7","vrvalue":"#C7C7C7","type":"1"
    public static class Color {
        private int id;
        private String name;
        private String value;
        private String vrvalue;
        private String type;

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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getVrvalue() {
            return vrvalue;
        }

        public void setVrvalue(String vrvalue) {
            this.vrvalue = vrvalue;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Fullvideoinfo {
        private String videourl;
        private int videobytesize;
        private String videoimage;

        public String getVideourl() {
            return videourl;
        }

        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }

        public int getVideobytesize() {
            return videobytesize;
        }

        public void setVideobytesize(int videobytesize) {
            this.videobytesize = videobytesize;
        }

        public String getVideoimage() {
            return videoimage;
        }

        public void setVideoimage(String videoimage) {
            this.videoimage = videoimage;
        }
    }

    public static class Pic {
        private int specId;
        private int typeId;
        private int subtabId;
        private int id;
        private int height;
        private int width;
        private int colorId;
        private String picurl;
        private int mediatype;
        private String vid;
        private String pointname;

        public int getMediatype() {
            return mediatype;
        }

        public void setMediatype(int mediatype) {
            this.mediatype = mediatype;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getPointname() {
            return pointname;
        }

        public void setPointname(String pointname) {
            this.pointname = pointname;
        }

        public int getSpecId() {
            return specId;
        }

        public void setSpecId(int specId) {
            this.specId = specId;
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public int getSubtabId() {
            return subtabId;
        }

        public void setSubtabId(int subtabId) {
            this.subtabId = subtabId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getColorId() {
            return colorId;
        }

        public void setColorId(int colorId) {
            this.colorId = colorId;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }

    public static class Subtab {

        private int tabId;
        private int colorId;
        private List<Secfilters> secfilters;

        public void setTabId(int tabId) {
            this.tabId = tabId;
        }

        public int getTabId() {
            return tabId;
        }

        public int getColorId() {
            return colorId;
        }

        public void setColorId(int colorId) {
            this.colorId = colorId;
        }

        public void setSecfilters(List<Secfilters> secfilters) {
            this.secfilters = secfilters;
        }

        public List<Secfilters> getSecfilters() {
            return secfilters;
        }

    }

    public static class Secfilters {

        private String name;
        private int filterid;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setFilterid(int filterid) {
            this.filterid = filterid;
        }

        public int getFilterid() {
            return filterid;
        }

    }

    public static class Info {

        private String name;
        private String imgurl;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getImgurl() {
            return imgurl;
        }

    }

    public static class Func {

        private int type;
        private String name;
        private String selname;
        private String imgurl;
        private String selimgurl;

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setSelname(String selname) {
            this.selname = selname;
        }

        public String getSelname() {
            return selname;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setSelimgurl(String selimgurl) {
            this.selimgurl = selimgurl;
        }

        public String getSelimgurl() {
            return selimgurl;
        }

    }

    public static class Videos {

        private String name;
        private String namenoindex;
        private String vid;
        private String imgurl;

        public String getNamenoindex() {
            return namenoindex;
        }

        public void setNamenoindex(String namenoindex) {
            this.namenoindex = namenoindex;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getVid() {
            return vid;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getImgurl() {
            return imgurl;
        }

    }

    public static class Videoalbum {

        private List<Func> func;
        private List<Album> album;

        public void setFunc(List<Func> func) {
            this.func = func;
        }

        public List<Func> getFunc() {
            return func;
        }

        public void setAlbum(List<Album> album) {
            this.album = album;
        }

        public List<Album> getAlbum() {
            return album;
        }

    }

    public static class Album {

        private int tabId;
        private int colorId;
        private Info info;
        private List<Videos> videos;

        public int getTabId() {
            return tabId;
        }

        public void setTabId(int tabId) {
            this.tabId = tabId;
        }

        public int getColorId() {
            return colorId;
        }

        public void setColorId(int colorId) {
            this.colorId = colorId;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public Info getInfo() {
            return info;
        }

        public void setVideos(List<Videos> videos) {
            this.videos = videos;
        }

        public List<Videos> getVideos() {
            return videos;
        }

    }

    public static class VRMegaInfo {

        private int colorId;
        private Megainfo megainfo;

        public void setMegainfo(Megainfo megainfo) {
            this.megainfo = megainfo;
        }

        public Megainfo getMegainfo() {
            return megainfo;
        }

        public int getColorId() {
            return colorId;
        }

        public void setColorId(int colorId) {
            this.colorId = colorId;
        }

    }

    public static class Megainfo {

        private String name;
        private String imgurl;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getImgurl() {
            return imgurl;
        }

    }
}
