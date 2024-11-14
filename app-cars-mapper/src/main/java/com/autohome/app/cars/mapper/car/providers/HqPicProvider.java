package com.autohome.app.cars.mapper.car.providers;

public class HqPicProvider {

    public String getSeriesSpec() {
        return "select series_id AS seriesId,\n" +
                "       spec_id   AS specId\n" +
                "from high_quality_order\n" +
                "group by series_id, spec_id; ";
    }

    // TODO chengjincheng 2024/8/8 还需要判断各级分类的is_del

    public String getHqPhotoAll() {
        String sql = "" +
                "select hqo.series_id         AS seriesId,\n" +
                "       hqo.series_name       AS seriesName,\n" +
                "       hqo.spec_id           AS specId,\n" +
                "       hqo.spec_name         AS specName,\n" +
                "       hqo.color_id          AS colorId,\n" +
                "       hqo.color_name        AS colorName,\n" +
                "       hqo.color_value       AS colorValue,\n" +
                "       hqo.inner_color_id    AS innerColorId,\n" +
                "       hqo.inner_color_name  AS innerColorName,\n" +
                "       hqo.inner_color_value AS innerColorValue,\n" +
                "       hqp.id                AS photoId,\n" +
                "       hqp.url               AS photoUrl,\n" +
                "       hqp.height            AS height,\n" +
                "       hqp.width             AS width,\n" +
                "       hqpt.id               AS typeId,\n" +
                "       hqpt.type_name        AS typeName,\n" +
                "       hqpt.sort_id          AS typeSortId,\n" +
                "       hqps.id               AS subTypeId,\n" +
                "       hqps.subtype_name     AS subTypeName,\n" +
                "       hqps.sort_id          AS subTypeSortId,\n" +
                "       hqpp.id               AS pointId,\n" +
                "       hqpp.point_name       AS pointName,\n" +
                "       hqpp.sort_id          AS pointSortId\n" +
                "from high_quality_photo AS hqp\n" +
                "         join high_quality_order hqo on hqp.order_Id = hqo.id\n" +
                "         join high_quality_photo_point hqpp on hqp.point_id = hqpp.id\n" +
                "         join high_quality_photo_subtype hqps on hqpp.sub_type_id = hqps.id\n" +
                "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
//                "";
                "where hqp.check_state = 1\n" +
                "  and hqp.publish_state = 10\n" +
                "  and hqp.is_del = 0 ";
        return sql;
    }

    public String getHqPhoto(int seriesId, int specId) {
        String sql = getHqPhotoAll() + "and hqo.series_id = #{seriesId} ";
        if (specId > 0) {
            sql = sql + "and hqo.spec_id = #{specId} ";
        }
        return sql;
    }

    public String getHqVideoAll() {
        String sql = "" +
                "select hqo.series_id         AS seriesId,\n" +
                "       hqo.series_name       AS seriesName,\n" +
                "       hqo.spec_id           AS specId,\n" +
                "       hqo.spec_name         AS specName,\n" +
                "       hqo.color_id          AS colorId,\n" +
                "       hqo.color_name        AS colorName,\n" +
                "       hqo.color_value       AS colorValue,\n" +
                "       hqo.inner_color_id    AS innerColorId,\n" +
                "       hqo.inner_color_name  AS innerColorName,\n" +
                "       hqo.inner_color_value AS innerColorValue,\n" +
                "       hqv.id                AS videoId,\n" +
                "       hqv.logo              AS videoLogoUrl,\n" +
                "       hqv.mid               AS videoMid,\n" +
                "       hqvt.id               AS typeId,\n" +
                "       hqvt.type_name        AS typeName,\n" +
                "       hqvt.sort             AS typeSortId,\n" +
                "       hqvs.id               AS subTypeId,\n" +
                "       hqvs.sub_type_name    AS subTypeName,\n" +
                "       hqvs.sort             AS subTypeSortId,\n" +
                "       hqvp.id               AS pointId,\n" +
                "       hqvp.point_name       AS pointName,\n" +
                "       hqvp.sort             AS pointSortId\n" +
                "from high_quality_video AS hqv\n" +
                "         join high_quality_order hqo on hqv.order_Id = hqo.id\n" +
                "         join high_quality_video_point hqvp on hqv.point_id = hqvp.id\n" +
                "         join high_quality_video_subtype hqvs on hqvp.sub_type_id = hqvs.id\n" +
                "         join high_quality_video_type hqvt on hqvs.type_id = hqvt.id\n" +
//                "";
                "where hqv.check_state = 1\n" +
                "  and hqv.publish_state = 10\n" +
                "  and hqv.is_del = 0 ";
        return sql;
    }

    public String getHqVideo(int seriesId, int specId) {
        String sql = getHqVideoAll() + "and hqo.series_id = #{seriesId} ";
        if (specId > 0) {
            sql = sql + "and hqo.spec_id = #{specId} ";
        }
        return sql;
    }

    public String getHqVideoType() {
        return "select id        AS videoTypeId,\n" +
                "       type_name AS videoTypeName,\n" +
                "       sort      AS videoTypeSortId\n" +
                "from high_quality_video_type\n" +
                "where is_del = 0; \n";
    }

    public String getHqVideoSubType() {
        return "select id            AS videoSubTypeId,\n" +
                "       sub_type_name AS videoSubTypeName,\n" +
                "       sort          AS videoSubTypeSortId\n" +
                "from high_quality_video_subtype\n" +
                "where is_del = 0; \n";
    }

    public String getHqRotateVideoAll() {
        String sql = "" +
                "select hqo.series_id         AS seriesId,\n" +
                "       hqo.series_name       AS seriesName,\n" +
                "       hqo.spec_id           AS specId,\n" +
                "       hqo.spec_name         AS specName,\n" +
                "       hqo.color_id          AS colorId,\n" +
                "       hqo.color_name        AS colorName,\n" +
                "       hqo.color_value       AS colorValue,\n" +
                "       hqo.inner_color_id    AS innerColorId,\n" +
                "       hqo.inner_color_name  AS innerColorName,\n" +
                "       hqo.inner_color_value AS innerColorValue,\n" +
                "       hqrv.point_id         AS pointId,\n" +
                "       hqrv.mid              AS videoMid,\n" +
                "       hqrv.export_url       AS videoUrl,\n" +
                "       hqrv.logo             AS videoLogoUrl,\n" +
                "       hqrv.video_size       AS videoSize,\n" +
                "       hqrv.mid_s            AS miniVideoMid,\n" +
                "       hqrv.export_url_s     AS miniVideoUrl,\n" +
                "       hqrv.video_size_s     AS miniVideoSize\n" +
                "from high_quality_rotate_video AS hqrv\n" +
                "         join high_quality_order hqo on hqrv.order_Id = hqo.id\n" +
//                "";
                "where hqrv.check_state = 1\n" +
                "  and hqrv.publish_state = 10\n" +
                "  and hqrv.is_del = 0 ";
        return sql;
    }

    public String getHqRotateVideo(int seriesId, int specId) {
        String sql = getHqRotateVideoAll() + "and hqo.series_id = #{seriesId} ";
        if (specId > 0) {
            sql = sql + "and hqo.spec_id = #{specId} ";
        }
        return sql;
    }

    public String getSeriesIdList() {
        return "select distinct series_id AS seriesId from high_quality_order;";
    }

    public String getSeriesSpecBySeriesId(int seriesId) {
        return "select series_id AS seriesId,\n" +
                "       spec_id   AS specId\n" +
                "from high_quality_order\n" +
                "where series_id = " + "#{seriesId}\n" +
                "group by series_id, spec_id;";
    }

    public String getHqOuterColor() {
        return "select distinct color_id    AS colorId,\n" +
                "                color_name  AS colorName,\n" +
                "                color_value AS colorValue\n" +
                "from high_quality_order\n" +
                "where is_del = 0;";
    }

    public String getHqPhotoSubType() {
        return "select type_id      AS photoTypeId,\n" +
                "       id           AS photoSubTypeId,\n" +
                "       subtype_name AS photoSubTypeName,\n" +
                "       sort_id      AS photoSubTypeSortId\n" +
                "from high_quality_photo_subtype\n" +
                "where is_del = 0; \n";
    }

    public String getHqInnerColor() {
        return "select distinct inner_color_id    AS colorId,\n" +
                "                inner_color_name  AS colorName,\n" +
                "                inner_color_value AS colorValue\n" +
                "from high_quality_order\n" +
                "where is_del = 0;";
    }

    public String getHqPhotoType() {
        return "select id        AS typeId,\n" +
                "       type_name AS typeName,\n" +
                "       sort_id   AS typeSortId\n" +
                "from high_quality_photo_type\n" +
                "where is_del = 0; \n";
    }
}
