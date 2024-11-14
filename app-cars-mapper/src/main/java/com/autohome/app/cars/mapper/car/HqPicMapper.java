package com.autohome.app.cars.mapper.car;

import com.autohome.app.cars.mapper.car.entities.*;
import com.autohome.app.cars.mapper.car.providers.HqPicProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
@DS("car")
public interface HqPicMapper {

    @SelectProvider(value = HqPicProvider.class, method = "getSeriesSpec")
    List<HqOrderSeriesSpec> getSeriesSpec();

    @SelectProvider(value = HqPicProvider.class, method = "getHqPhoto")
    List<HqPhotoEntity> getHqPhoto(int seriesId, int specId);

    @SelectProvider(value = HqPicProvider.class, method = "getHqVideo")
    List<HqVideoEntity> getHqVideo(int seriesId, int specId);

    @SelectProvider(value = HqPicProvider.class, method = "getHqVideoType")
    List<HqVideoTypeEntity> getHqVideoType();

    @SelectProvider(value = HqPicProvider.class, method = "getHqVideoSubType")
    List<HqVideoSubTypeEntity> getHqVideoSubType();

    @SelectProvider(value = HqPicProvider.class, method = "getHqRotateVideo")
    List<HqRotateVideoEntity> getHqRotateVideo(int seriesId, int specId);

    @SelectProvider(value = HqPicProvider.class, method = "getSeriesIdList")
    List<Integer> getSeriesIdList();

    @SelectProvider(value = HqPicProvider.class, method = "getSeriesSpecBySeriesId")
    List<HqOrderSeriesSpec> getSeriesSpecBySeriesId(int seriesId);

    @SelectProvider(value = HqPicProvider.class, method = "getHqOuterColor")
    List<HqColorEntity> getHqOuterColor();

    @SelectProvider(value = HqPicProvider.class, method = "getHqPhotoSubType")
    List<HqPhotoSubTypeEntity> getHqPhotoSubType();

    @SelectProvider(value = HqPicProvider.class, method = "getHqInnerColor")
    List<HqColorEntity> getHqInnerColor();

    @SelectProvider(value = HqPicProvider.class, method = "getHqPhotoType")
    List<HqPhotoTypeEntity> getHqPhotoType();

    @Select("select id, point_id, url,modified_stime\n" +
            "from high_quality_photo\n" +
            "where order_Id = #{orderId} and publish_state = 10 and check_state = 1 and is_del = 0;")
    List<HqPhotoBaseEntity> getByOrderId(int orderId);

    @Select("select 0            AS specId,\n" +
            "       hqo.color_id AS colorId,\n" +
            "       hqpt.id      AS typeId,\n" +
            "       hqps.id      AS subTypeId,\n" +
            "       count(1)     AS picCount\n" +
            "from high_quality_photo AS hqp\n" +
            "         join high_quality_order hqo on hqp.order_Id = hqo.id\n" +
            "         join high_quality_photo_point hqpp on hqp.point_id = hqpp.id\n" +
            "         join high_quality_photo_subtype hqps on hqpp.sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqp.check_state = 1\n" +
            "  and hqp.publish_state = 10\n" +
            "  and hqp.is_del = 0\n" +
            "  and hqpt.id in (1, 4)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSeriesOuterPhotoCountList(int seriesId);

    @Select("select hqo.spec_id  AS specId,\n" +
            "       hqo.color_id AS colorId,\n" +
            "       hqpt.id      AS typeId,\n" +
            "       hqps.id      AS subTypeId,\n" +
            "       count(1)     AS picCount\n" +
            "from high_quality_photo AS hqp\n" +
            "         join high_quality_order hqo on hqp.order_Id = hqo.id\n" +
            "         join high_quality_photo_point hqpp on hqp.point_id = hqpp.id\n" +
            "         join high_quality_photo_subtype hqps on hqpp.sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqp.check_state = 1\n" +
            "  and hqp.publish_state = 10\n" +
            "  and hqp.is_del = 0\n" +
            "  and hqpt.id in (1, 4)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.spec_id, hqo.color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSpecOuterPhotoCountList(int seriesId);


    @Select("select 0                  AS specId,\n" +
            "       hqo.inner_color_id AS innerColorId,\n" +
            "       hqpt.id            AS typeId,\n" +
            "       hqps.id            AS subTypeId,\n" +
            "       count(1)           AS picCount\n" +
            "from high_quality_photo AS hqp\n" +
            "         join high_quality_order hqo on hqp.order_Id = hqo.id\n" +
            "         join high_quality_photo_point hqpp on hqp.point_id = hqpp.id\n" +
            "         join high_quality_photo_subtype hqps on hqpp.sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqp.check_state = 1\n" +
            "  and hqp.publish_state = 10\n" +
            "  and hqp.is_del = 0\n" +
            "  and hqpt.id in (2, 3)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.inner_color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSeriesInnerPhotoCountList(int seriesId);

    @Select("select hqo.spec_id        AS specId,\n" +
            "       hqo.inner_color_id AS innerColorId,\n" +
            "       hqpt.id            AS typeId,\n" +
            "       hqps.id            AS subTypeId,\n" +
            "       count(1)           AS picCount\n" +
            "from high_quality_photo AS hqp\n" +
            "         join high_quality_order hqo on hqp.order_Id = hqo.id\n" +
            "         join high_quality_photo_point hqpp on hqp.point_id = hqpp.id\n" +
            "         join high_quality_photo_subtype hqps on hqpp.sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqp.check_state = 1\n" +
            "  and hqp.publish_state = 10\n" +
            "  and hqp.is_del = 0\n" +
            "  and hqpt.id in (2, 3)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.spec_id, hqo.inner_color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSpecInnerPhotoCountList(int seriesId);

    @Select("select 0            AS specId,\n" +
            "       hqo.color_id AS colorId,\n" +
            "       hqpt.id      AS typeId,\n" +
            "       hqps.id      AS subTypeId,\n" +
            "       count(1)     AS picCount\n" +
            "from high_quality_video AS hqv\n" +
            "         join high_quality_order hqo on hqv.order_Id = hqo.id\n" +
            "         join high_quality_video_point hqvp on hqv.point_id = hqvp.id\n" +
            "         join high_quality_photo_subtype hqps on hqvp.photo_sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqv.check_state = 1\n" +
            "  and hqv.publish_state = 10\n" +
            "  and hqv.is_del = 0\n" +
            "  and hqvp.photo_sub_type_id > 0\n" +
            "  and hqpt.id in (1, 4)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSeriesOuterVideoCountList(int seriesId);

    @Select("select hqo.spec_id  AS specId,\n" +
            "       hqo.color_id AS colorId,\n" +
            "       hqpt.id      AS typeId,\n" +
            "       hqps.id      AS subTypeId,\n" +
            "       count(1)     AS picCount\n" +
            "from high_quality_video AS hqv\n" +
            "         join high_quality_order hqo on hqv.order_Id = hqo.id\n" +
            "         join high_quality_video_point hqvp on hqv.point_id = hqvp.id\n" +
            "         join high_quality_photo_subtype hqps on hqvp.photo_sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqv.check_state = 1\n" +
            "  and hqv.publish_state = 10\n" +
            "  and hqv.is_del = 0\n" +
            "  and hqvp.photo_sub_type_id > 0\n" +
            "  and hqpt.id in (1, 4)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.spec_id, hqo.color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSpecOuterVideoCountList(int seriesId);

    @Select("select 0            AS specId,\n" +
            "       hqo.inner_color_id AS innerColorId,\n" +
            "       hqpt.id      AS typeId,\n" +
            "       hqps.id      AS subTypeId,\n" +
            "       count(1)     AS picCount\n" +
            "from high_quality_video AS hqv\n" +
            "         join high_quality_order hqo on hqv.order_Id = hqo.id\n" +
            "         join high_quality_video_point hqvp on hqv.point_id = hqvp.id\n" +
            "         join high_quality_photo_subtype hqps on hqvp.photo_sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqv.check_state = 1\n" +
            "  and hqv.publish_state = 10\n" +
            "  and hqv.is_del = 0\n" +
            "  and hqvp.photo_sub_type_id > 0\n" +
            "  and hqpt.id in (2, 3)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.inner_color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSeriesInnerVideoCountList(int seriesId);

    @Select("select hqo.spec_id  AS specId,\n" +
            "       hqo.inner_color_id AS innerColorId,\n" +
            "       hqpt.id      AS typeId,\n" +
            "       hqps.id      AS subTypeId,\n" +
            "       count(1)     AS picCount\n" +
            "from high_quality_video AS hqv\n" +
            "         join high_quality_order hqo on hqv.order_Id = hqo.id\n" +
            "         join high_quality_video_point hqvp on hqv.point_id = hqvp.id\n" +
            "         join high_quality_photo_subtype hqps on hqvp.photo_sub_type_id = hqps.id\n" +
            "         join high_quality_photo_type hqpt on hqps.type_id = hqpt.id\n" +
            "where hqv.check_state = 1\n" +
            "  and hqv.publish_state = 10\n" +
            "  and hqv.is_del = 0\n" +
            "  and hqvp.photo_sub_type_id > 0\n" +
            "  and hqpt.id in (2, 3)\n" +
            "  and hqo.series_id = #{seriesId}\n" +
            "group by hqo.spec_id, hqo.inner_color_id, hqpt.id, hqps.id; ")
    List<HqPicCountEntity> getSpecInnerVideoCountList(int seriesId);

}
