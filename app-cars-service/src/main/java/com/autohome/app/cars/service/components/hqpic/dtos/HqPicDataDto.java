package com.autohome.app.cars.service.components.hqpic.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HqPicDataDto {

    private int seriesId;

    private List<Integer> specIdList;

    private List<VideoAlbum> videoAlbumList;

    private List<RotateVideoAlbum> rotateVideoAlbumList;

    private List<ColorInfo> colorInfoList;

    private List<PicCount> picCountList;

    private Map<Integer, List<PhotoSubTypeDto>> photoSubTypeMap;

    @Data
    public static class VideoAlbum {
        private int specId;
        private int colorId;
        private int innerColorId;
        private List<TypeAlbum> typeAlbumList;
    }

    @Data
    public static class TypeAlbum {
        private int typeId;
        private int typeSortId;
        private List<SubTypeAlbum> subTypeAlbumList;
    }

    @Data
    public static class SubTypeAlbum {
        private int subTypeId;
        private int subTypeSortId;
        private String albumName;
        private List<Video> videoList;
    }

    @Data
    public static class Video {
        private String name;
        private String vid;
        private String imgUrl;
        private int pointSortId;
    }

    @Data
    public static class RotateVideoAlbum {
        private int specId;
        private int colorId;
        private String colorValue;
        private String colorName;
        private List<PointRotateVideo> pointRotateVideoList;
    }

    /**
     * specId-typeId-colorId 数据唯一
     * 当type为外观（即hqTypeId=1）时，颜色取外观颜色
     * 当type为内饰/座椅（即hqTypeId=2/3）时，颜色取内饰颜色
     */
    @Data
    public static class ColorInfo {
        private int specId;
        private int hqTypeId;
        private int cpkTypeId;
        private String typeName;
        private int colorId;
        private String colorValue;
        private String colorName;
        private int picCount;
        private int isOnsale;
    }

    @Data
    public static class PointRotateVideo {
        //1, "关灯-关门"
        //2, "关灯-开门"
        //3, "开灯-关门"
        //4, "开灯-开门"
        private int pointId;
        private RotateVideo originVideo;
        private RotateVideo miniVideo;
    }

    @Data
    public static class RotateVideo {
        private String videoUrl;
        private String vid;
        private String imgUrl;
        private int byteSize;
    }

    /**
     * 统计新老图所有的图片数量
     * 当type为外观（即hqTypeId=1）时，取新图外观+老图外观+老图细节
     * 当type为内饰/座椅（即hqTypeId=2/3）时，取老图+新图
     */
    @Data
    public static class PicCount {
        private int specId;
        private int hqTypeId;
        private int cpkTypeId;
        private int colorId;
        private int innerColorId;
        private int subTypeId;
        private int picCount;
    }


    @Data
    public static class PhotoSubTypeDto {
        private int subTypeId;
        private String subTypeName;
        private int subTypeSortId;
    }
}
