package com.autohome.app.cars.mapper.popauto;

import com.autohome.app.cars.mapper.popauto.entities.*;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
@DS("popauto")
public interface TestDataMapper {
    /**
     * 实测数据-实测项-无配置项关联表
     *
     * @param
     * @return
     */
    @Select("SELECT dataId, itemId, standardId\n" +
            "FROM TestDataItemNotConfig where dataId=#{dataId} and is_del =0")
    List<SpecTestDataItemEntity> getItemNotConfigList(@Param("dataId") Integer dataId);

    /**
     * 获取实测项集合
     */
    @Select("SELECT A.id, A.standardId, A.name, A.levelId, A.parentId, A.isShow, A.isRequire, A.isShowVideo, A.isFourDrive, A.isResultShow, A.contentType, \n" +
            "A.contentTypeUnit, A.remark, A.userId, A.userName, A.explainText, A.explainImg, A.zixunVideoId, A.sourceVideoId, A.ItemImg, A.isNotConfig, A.opinionLength, A.contentTypeLength, A.is_del, A.created_stime, A.modified_stime, A.sort,A.aliasName\n" +
            ",isnull(A.zixunVideoIdItem,0)as zixunVideoIdItem,A.sourceVideoIdItem,isnull(A.isRequireNecessary,1) as isRequireNecessary,isnull(A.isShowVideoNecessary,1) as isShowVideoNecessary \n" +
            "FROM  TestStandardItem as A with(Nolock) where  A.is_del =0 and A.isShow=1")
    List<TestStandardItem> getTestStandardItemList();

    /**
     * 车系获取几个单项实测数据
     *
     * @param seriesId
     * @return
     */
    @Select("select C.specId , A.dataId,A.standardId,A.itemid, B.name,A.resultShowValue,B.contentTypeUnit  from TestDataItem  as A inner join TestStandardItem  as B on A.itemId =B.id\n" +
            "inner join TestData as C with(Nolock) on A.dataId =C.id \n" +
            "where C.isPublish =1  and B.levelId =4  and A.is_del =0 and B.is_del =0 and C.is_del =0  and B.name in ('0-100km/h加速时间', '百公里油耗', '综合续航里程','刹车距离','120km/h','30%-80%充电时长') and A.resultShowValue!='' \n" +
            " and C.seriesId =#{seriesId} ")
    List<SpecTestDataItemEntity> getSpecSpeedOilwareTestDataBySeries(@Param("seriesId") Integer seriesId);

    /**
     * 获取所有已发布的实测、超测数据
     *
     * @return
     */
    @Select("select id, seriesId ,specId,standardId,isnull(fromId,0) as fromId, isnull(isGenerate,0) as isGenerate,isPublish,is_del from TestData with(nolock) ")
    List<TestDataEntity> getPublishedTestDataList();

    /**
     * 实测数据下所有的实测项素材列表
     *
     * @param dataId
     * @param
     * @return
     */
    @Select("select * from (\n" +
            "select  A.id,A.dataId, A.contentId,A.standardId, A.itemId, A.contentValue, A.sourceVideoId,\n" +
            " B.name,  B.isShow, B.isRequire, B.contentType, B.contentTypeUnit, ROW_NUMBER ()over(partition by A.dataId,A.itemid,A.contentId order by A.id ) as RN\n" +
            "from TestDataItemContent as A with(nolock) inner join TestStandardItemContent as B with(nolock) \n" +
            "on  A.contentId  = B.id  \n" +
            "where A.dataId =#{dataId} and A.is_del=0 and B.is_del =0 and B.isShow =1\n" +
            ") as T where RN =1")
    List<TestDataItemContent> getTestDataItemContent(@Param("dataId") Integer dataId);

    /**
     * 获取车型实测数据值
     *
     * @param
     * @param
     * @param dataId
     * @return
     */
    @Select("SELECT id, dataId, standardId, itemId, perspectiveValue, showVideoValue, showSourceVideoId, resultShowValue, resultSourceVideoId,itemJson,itemJson165,itemJson185,isThird\n" +
            "FROM TestDataItem with(Nolock) where is_del =0 and dataId=#{dataId}")
    List<TestDataItem> getSpecTestDataItem_PoList(@Param("dataId") Integer dataId);

    /**
     * 超测入口数据
     */
    @Select("select A.id as dataId,A.brandId,A.seriesId,A.specId,A.standardId,C.levelId,B.itemId,C.name AS itemName,C.contentTypeUnit as unit,B.resultShowValue as showValue,B.resultSourceVideoId as newValue \n" +
            "from TestData as A  with(nolock) inner join TestDataItem as B  with(nolock) on A.id =B.dataId\n" +
            "inner join TestStandardItem  as C with(nolock) on B.itemId  = C.id\n" +
            "where A.standardId = #{standardId} and C.levelid=4 and A.fromId =0 and A.isGenerate=0  and A.is_del = 0  and B.resultShowValue != '' AND  A.isPublish = 1 \n" +
            "and C.name in('续航里程','100-0km/h制动距离','0-100km/h加速时间','30%-80%充电时长','雪地极速','振动隔绝')")
    List<TestDataSpecBasePo> getRankListData_2023WinterByItemName(@Param("standardId")Integer standardId);

    @Select("SELECT A.id, A.dataId, A.standardId, A.contentId,B.name as contentname,B.contentType , A.contentValue, A.sourceVideoId       \n" +
            "FROM TestDataContent as A  with(nolock) inner join TestStandardContent as B with(nolock)\n" +
            "on A.contentId =B.id and A.standardId =B.standardId  where B.name = '${contentname}'  and A.standardId =#{standardId} and A.is_del =0 and B.is_del =0")
    List<TestDataItemContent> getTestDataContentByStandardId(@Param("standardId")Integer standardId,@Param("contentname")String contentname);
}
