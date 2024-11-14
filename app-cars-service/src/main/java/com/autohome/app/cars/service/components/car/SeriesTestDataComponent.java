package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.enums.EnergyTypesNewEnum;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.TestDataMapper;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.service.components.car.dtos.SeriesTestDataDto;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zzli
 * @description 车系实测、超测入口数据相关
 * https://doc.autohome.com.cn/docapi/page/share/share_sZZ0LMUifI
 */

@Component
@DBConfig(tableName = "series_testdata")
public class SeriesTestDataComponent extends BaseComponent<SeriesTestDataDto> {
    final static String seriesIdParamName = "seriesId";

    final static int STANDARDID = 119;//实测标准
    final static int WINTERSTANDARDID = 120;//冬测标准

    @Autowired
    TestDataMapper testDataMapper;

    @Autowired
    SpecMapper specMapper;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    /***
     *
     * @description 根据车系id获取数据
     * @author zzli
     * @param seriesId 车系id
     * @return 车系实测，超测入口数据
     */
    public CompletableFuture<SeriesTestDataDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> log) {
        List<TestDataEntity> publishedTestDataList = testDataMapper.getPublishedTestDataList();

        if (CollectionUtils.isEmpty(publishedTestDataList))
            return;
        List<TestStandardItem> StandardItemList = testDataMapper.getTestStandardItemList();

        if (CollectionUtils.isEmpty(StandardItemList))
            return;

        //超测入口数据,提前查出来
        //超测的配置项
        List<TestStandardItem> testWinterStandardItemList = StandardItemList.stream()
                .filter(x -> x.getStandardId().equals(WINTERSTANDARDID))
                .sorted(Comparator.comparing(TestStandardItem::getSort))
                .collect(Collectors.toList());
        List<TestDataSpecBasePo> testWinterItemDataByItemName = testDataMapper.getRankListData_2023WinterByItemName(WINTERSTANDARDID);
        List<TestDataItemContent> testDataContentByStandardId = testDataMapper.getTestDataContentByStandardId(WINTERSTANDARDID, "单车视频");

        //实测的配置项
        List<TestStandardItem> testStandardItemList = StandardItemList.stream()
                .filter(x -> x.getStandardId().equals(STANDARDID))
                .sorted(Comparator.comparing(TestStandardItem::getSort))
                .collect(Collectors.toList());
        //已删除的
        List<Integer> delTestSeriesIds = publishedTestDataList.stream()
                .filter(x -> x.getIsPublish() == 0 || x.getIs_del() == 1)
                .map(TestDataEntity::getSeriesId)
                .distinct()
                .collect(Collectors.toList());
        for (Integer seriesid : delTestSeriesIds) {
            delete(makeParam(seriesid));
            log.accept("del,车系id:" + seriesid);
        }

        //按车系逐个更新
        List<Integer> TestSeriesIds = publishedTestDataList.stream()
                .filter(x -> x.getIsPublish() == 1 && x.getIs_del() == 0)
                .map(TestDataEntity::getSeriesId)
                .distinct()
                .collect(Collectors.toList());

        for (Integer seriesid : TestSeriesIds) {
            SeriesTestDataDto seriesTestDataDto = new SeriesTestDataDto();
            //实测车系
            List<TestDataEntity> testSeriesList = publishedTestDataList.stream()
                    .filter(a -> a.getStandardId() == STANDARDID && a.getSeriesId().equals(seriesid))
                    .filter(x -> x.getIsPublish() == 1 && x.getIs_del() == 0)
                    .sorted(Comparator.comparing(TestDataEntity::getIsGenerate))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(testSeriesList)) {
                seriesTestDataDto.setTestData(getSeriesTestDataSpecList(testSeriesList, seriesid, testStandardItemList));
            }
            //超测车系
            if (publishedTestDataList.stream().anyMatch(a -> a.getStandardId() == WINTERSTANDARDID && a.getSeriesId().equals(seriesid) && a.getIsPublish() == 1 && a.getIs_del() == 0)) {
                seriesTestDataDto.setTestWinterData(getSeriesTestDataSpecList_2023winter(seriesid, testWinterStandardItemList, testWinterItemDataByItemName, testDataContentByStandardId));
            }
            if (!seriesTestDataDto.getTestData().isEmpty() || !seriesTestDataDto.getTestWinterData().isEmpty()) {
                update(makeParam(seriesid), seriesTestDataDto);
                log.accept("success,车系id:" + seriesid);
            } else {
                delete(makeParam(seriesid));
                log.accept("del,车系id:" + seriesid);
            }
        }
    }

    // 定义常量-实测车系入口要显示的配置项数据。产品指定的,分新能源和油车
    private static final List<String> NEW_ENERGY_FILTER_LIST = Arrays.asList("0-100km/h加速时间", "综合续航里程", "30%-80%充电时长", "刹车距离", "120km/h");
    private static final List<String> OIL_CAR_FILTER_LIST = Arrays.asList("0-100km/h加速时间", "百公里油耗", "刹车距离", "120km/h");

    /**
     * 逻辑等价carservice接口，获取车系下车型实测数据,车系入口相关
     * https://carservice.autohome.com.cn/teststandard/getTestSpecInfoBySeriesIdSpecId?_appid=app&seriesid=6072
     */
    List<SeriesTestDataDto.SeriesTestData119Dto> getSeriesTestDataSpecList(List<TestDataEntity> TestSeriesList, int seriesId, List<TestStandardItem> allStandardItemList) {
        List<SeriesTestDataDto.SeriesTestData119Dto> result = new ArrayList<>();
        if (TestSeriesList != null && TestSeriesList.size() > 0) {
            //取固定几项，车系下所有的实测项数据,按车系一次性全查出来所有车型的
            Map<Integer, List<SpecTestDataItemEntity>> mapDataId = Optional.ofNullable(testDataMapper.getSpecSpeedOilwareTestDataBySeries(seriesId))
                    .filter(l -> !l.isEmpty())
                    .map(l -> l.stream().collect(Collectors.groupingBy(SpecTestDataItemEntity::getDataId)))
                    .orElse(Collections.emptyMap());

            for (TestDataEntity vo : TestSeriesList) {
                Integer tempSpecId = vo.getSpecId();

                //TODO 后期可走组件缓存
                int fueltype = 0;
                SpecEntity spec = specMapper.getSpec(tempSpecId);
                if (spec != null) {
                    fueltype = spec.getFuelType();
                }

                SeriesTestDataDto.SeriesTestData119Dto specVo = new SeriesTestDataDto.SeriesTestData119Dto();
                specVo.setSpecId(tempSpecId);
                specVo.setStandardId(vo.getStandardId());
                specVo.setDataId(vo.getId());
                specVo.setFueltypedetail(fueltype);
                specVo.setIsGenerate(vo.getIsGenerate()); //此数据是否是程序生成的数据

                //车型 无配置项
                List<SpecTestDataItemEntity> itemNotConfigList = testDataMapper.getItemNotConfigList(vo.getId());

                //智能驾驶  智能座舱,要计算这两一级配置项，下面测了多少项数据,排到无配置项的
                List<SeriesTestDataDto.TestItemSummary> summaryList = getSummaryList_FixedRule(vo.getId(), allStandardItemList, itemNotConfigList, seriesId);
                if (summaryList != null && summaryList.size() > 0) {
                    specVo.getTestItemlist().addAll(summaryList);
                }

                List<SeriesTestDataDto.TestItemSummary> testItemSummaryList_bySeres = new ArrayList<>();
                if (mapDataId.get(vo.getId()) != null) {
                    //无配置项id集合
                    List<Integer> noConfigItemIds = getNoConfigAll4LevelItems(allStandardItemList, itemNotConfigList);
                    for (SpecTestDataItemEntity Vo : mapDataId.get(vo.getId())) {
                        if (noConfigItemIds.contains(Vo.getItemId())) {
                            continue;
                        }
                        SeriesTestDataDto.TestItemSummary summary = new SeriesTestDataDto.TestItemSummary();
                        summary.setSpecId(Vo.getSpecId());
                        summary.setDataId(Vo.getDataId());
                        summary.setItemId(Vo.getItemId());
                        summary.setName(Vo.getName());
                        summary.setShowValue(Vo.getResultShowValue());
                        summary.setUnit(Vo.getContentTypeUnit());
                        testItemSummaryList_bySeres.add(summary);
                    }
                }
                if (CollectionUtils.isNotEmpty(testItemSummaryList_bySeres)) {
                    if (StringUtils.isNotEmpty(EnergyTypesNewEnum.getTypeByValue(fueltype))) {
                        //新能源
                        specVo.getTestItemlist().addAll(testItemSummaryList_bySeres.stream().filter(a -> NEW_ENERGY_FILTER_LIST.contains(a.getName())).collect(Collectors.toList()));
                    } else {
                        //油车的处理
                        specVo.getTestItemlist().addAll(testItemSummaryList_bySeres.stream().filter(a -> OIL_CAR_FILTER_LIST.contains(a.getName())).collect(Collectors.toList()));
                    }
                }
                result.add(specVo);
            }
        }
        return result;
    }

    /**
     * 实测排行榜用：根据实测项id 获取实测数据下所有第四级无参配置项
     *
     * @return
     */
    List<Integer> getNoConfigAll4LevelItems(List<TestStandardItem> allStandardItemList, List<SpecTestDataItemEntity> itemNotConfigList) {
        List<Integer> allReturnItemId = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allStandardItemList) && CollectionUtils.isNotEmpty(itemNotConfigList)) {
            for (SpecTestDataItemEntity notConfigItem : itemNotConfigList) {
                List<TestStandardItem> filter_result = allStandardItemList.stream().filter(a -> a.getParentId().equals(notConfigItem.getItemId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(filter_result)) {
                    getAllNoConfigSubItems(allStandardItemList, filter_result, allReturnItemId);
                } else {
                    allReturnItemId.add(notConfigItem.getItemId());
                }
            }
        }
        return allReturnItemId;
    }

    /**
     * 实测排行榜用：递归方法 遍历实测数据下所有第四级无参配置项
     *
     * @param allStandardItemList
     * @param filter_result
     * @param allReturnItemId
     */
    void getAllNoConfigSubItems(List<TestStandardItem> allStandardItemList, List<TestStandardItem> filter_result, List<Integer> allReturnItemId) {
        Iterator<TestStandardItem> iterator = filter_result.listIterator();
        while (iterator.hasNext()) {
            TestStandardItem item = iterator.next();
            List<TestStandardItem> fiterItem = allStandardItemList.stream().filter(a -> a.getParentId().equals(item.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fiterItem)) {
                getAllNoConfigSubItems(allStandardItemList, fiterItem, allReturnItemId);
            } else {
                allReturnItemId.add(item.getId());
            }
        }
    }

    /**
     * 写死业务逻辑 取相关参数
     * 1. 智能驾驶一级分类下，所有4级别分类统计实测项 通过多少项，共多少项
     * 20230912改
     * 1. 智能驾驶一级分类下，所有4级别分类统计实测项 共多少项
     * 2. 智能座舱一级分类下，所有4级别分类统计实测项 共多少项
     *
     * @param
     * @param dataId
     * @return
     */
    List<SeriesTestDataDto.TestItemSummary> getSummaryList_FixedRule(Integer dataId, List<TestStandardItem> allStandardItemList, List<SpecTestDataItemEntity> itemNotConfigList, int seriesid) {
        List<SeriesTestDataDto.TestItemSummary> summaryListResult = new ArrayList<>();
        //先查一下，有没有这两项的数据，避免后面做无效查询
        List<TestStandardItem> dataItem = filterDataItem("智能驾驶", allStandardItemList, itemNotConfigList);
        List<TestStandardItem> dataItem1 = filterDataItem("智能座舱", allStandardItemList, itemNotConfigList);
        if (CollectionUtils.isEmpty(dataItem) && CollectionUtils.isEmpty(dataItem1)) {
            return summaryListResult;
        }
        //实测素材
        List<TestDataItemContent> testDataItemContentPoList = testDataMapper.getTestDataItemContent(dataId);
        //实测数据
        List<TestDataItem> testDataItemPoList = testDataMapper.getSpecTestDataItem_PoList(dataId);
        Map<Integer, List<TestDataItem>> mapDataItemPo = new HashMap<>();
        if (testDataItemPoList != null && testDataItemPoList.size() > 0) {
            mapDataItemPo = testDataItemPoList.stream().collect(Collectors.groupingBy(TestDataItem::getItemId));
        }

        if (CollectionUtils.isNotEmpty(dataItem)) {
            //智能驾驶
            List<TestStandardItem> result = filterDataItem2(dataItem, mapDataItemPo, testDataItemContentPoList);
            if (CollectionUtils.isNotEmpty(result)) {
                SeriesTestDataDto.TestItemSummary testItemSummary = new SeriesTestDataDto.TestItemSummary();
                testItemSummary.setName("智能驾驶");
                testItemSummary.setShowValue(String.format("%s项测试", result.size()));
                summaryListResult.add(testItemSummary);
            }
        }
        if (CollectionUtils.isNotEmpty(dataItem1)) {
            //智能座舱
            List<TestStandardItem> zuocang_testList = filterDataItem2(dataItem1, mapDataItemPo, testDataItemContentPoList);
            if (CollectionUtils.isNotEmpty(zuocang_testList)) {
                SeriesTestDataDto.TestItemSummary testItemSummary = new SeriesTestDataDto.TestItemSummary();
                testItemSummary.setName("智能座舱");
                testItemSummary.setShowValue(String.format("%s项测试", zuocang_testList.size()));
                summaryListResult.add(testItemSummary);
            }
        }
        return summaryListResult;
    }

    //递归取一级配置项下的所有四级配置项，过滤掉无配置项的
    List<TestStandardItem> filterDataItem(String rootNodeName, List<TestStandardItem> allStandardItemList, List<SpecTestDataItemEntity> itemNotConfigList) {
        List<TestStandardItem> result = new ArrayList<>();
        //无配置项id集合
        List<Integer> notConfigItemIds = CollectionUtils.isEmpty(itemNotConfigList) ? Collections.emptyList()
                : itemNotConfigList.stream()
                .map(SpecTestDataItemEntity::getItemId)
                .collect(Collectors.toList());
        Optional<TestStandardItem> optional_root = allStandardItemList.stream().filter(a -> a.getName().equals(rootNodeName)).findFirst();
        if (optional_root.isPresent()) {
            if (!notConfigItemIds.contains(optional_root.get().getId())) { //判断是否无参数配置项
                getChildrenList_ByCondition(allStandardItemList, optional_root.get(), result, 4, notConfigItemIds);
            }
        }
        return result;
    }

    //递归取一级配置项下的所有四级配置项，过滤掉无配置项的
    void getChildrenList_ByCondition(List<TestStandardItem> allSublist, TestStandardItem parentObj, List<TestStandardItem> returnList, int filterLevelId, List<Integer> noconfigIds) {
        List<TestStandardItem> filterResult = allSublist.stream().filter(a -> a.getParentId().equals(parentObj.getId())).collect(Collectors.toList());
        if (filterResult != null && filterResult.size() > 0) {
            Iterator<TestStandardItem> itSub = filterResult.listIterator();
            while (itSub.hasNext()) {
                TestStandardItem item = itSub.next();
                if (noconfigIds.contains(item.getId())) {
                    itSub.remove();
                    continue;
                }
                //收集符合层级的项
                if (item.getLevelId() == filterLevelId) {
                    returnList.add(item);
                }
                this.getChildrenList_ByCondition(allSublist, item, returnList, filterLevelId, noconfigIds);
            }
        }
    }

    /**
     * 智能驾驶四级通过项
     * 统计通过项过滤
     * 产品需求写死条件 （赖晓云）
     */
    static final List<String> SMARTDRIVE_All = Arrays.asList("通过", "未通过", "居中", "不居中", "未完成", "识别", "未识别", "激进", "正常", "迟钝");

    //取有值 的配置项
    List<TestStandardItem> filterDataItem2(List<TestStandardItem> dataItem, Map<Integer, List<TestDataItem>> mapDataItemPo, List<TestDataItemContent> testDataItemContentPoList) {
        List<TestStandardItem> list = new ArrayList<>();
        for (TestStandardItem po : dataItem) {
            if (mapDataItemPo != null && mapDataItemPo.containsKey(po.getId())) {
                Optional<TestDataItem> optional_dataItem = mapDataItemPo.get(po.getId()).stream().findFirst();
                if (optional_dataItem.isPresent()) {
                    TestDataItem testDataItem = optional_dataItem.get();
                    //只取单选项类型固定范围的测试项做统计。产品需求
                    if (!SMARTDRIVE_All.contains(testDataItem.getResultShowValue())) {
                        continue;
                    }
                    if (StringUtils.isNotEmpty(testDataItem.getPerspectiveValue()) || StringUtils.isNotEmpty(testDataItem.getResultShowValue())
                            || StringUtils.isNotEmpty(testDataItem.getShowSourceVideoId()) || StringUtils.isNotEmpty(testDataItem.getShowVideoValue())) {
                        list.add(po);
                    } else if (testDataItemContentPoList != null && testDataItemContentPoList.size() > 0) {
                        List<TestDataItemContent> itemContentList = testDataItemContentPoList.stream().filter(a -> a.getItemId().equals(testDataItem.getItemId())).collect(Collectors.toList());
                        if (itemContentList != null && itemContentList.size() > 0) {
                            for (TestDataItemContent contentItem : itemContentList) {
                                if (StringUtils.isNotEmpty(contentItem.getContentValue()) || StringUtils.isNotEmpty(contentItem.getSourceVideoId())) {
                                    list.add(po);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    //    屏蔽三个车型（原逻辑为屏蔽三个车系）
    //    品牌：埃安；车系：AION S MAX（7452）；2024款 80 星辰版 （65367）
    //    品牌：极氪；车系：极氪001 FR（6091）；2023款 FR版 100kWh （64158）
    //    品牌：小鹏；车系：小鹏X9（7067）；2024款 702超长续航Pro （66307）
    static final List<Integer> HIDE_WINTER2023_SPECIDS = Arrays.asList(65367, 64158, 66307);

    /**
     * 超测数据
     * http://carservice.yz.test.autohome.com.cn/teststandard/2023winter/getTestSpecInfoBySeriesIdSpecId?standardId=120&seriesId=6354
     */
    List<SeriesTestDataDto.SeriesTestDataWinter120Dto> getSeriesTestDataSpecList_2023winter(int seriesId,
                                                                                            List<TestStandardItem> testWinterStandardItemList,
                                                                                            List<TestDataSpecBasePo> testWinterItemDataByItemName,
                                                                                            List<TestDataItemContent> testDataContentVoList) {
        List<SeriesTestDataDto.SeriesTestDataWinter120Dto> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(testWinterItemDataByItemName) || CollectionUtils.isEmpty(testWinterStandardItemList)) {
            return result;
        }
        List<TestDataSpecBasePo> seriesDataSpecPo = testWinterItemDataByItemName.stream().filter(a -> a.getSeriesId() == seriesId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(seriesDataSpecPo)) {
            List<TestDataSpecBasePo> filterResult = new ArrayList<>();
            Map<Integer, TestStandardItem> standardItemMap = testWinterStandardItemList.stream()
                    .collect(Collectors.toMap(TestStandardItem::getId, Function.identity()));
            seriesDataSpecPo.forEach(a -> {
                TestStandardItem level1Item = getParentItemByLevel_recursion(standardItemMap, a.getItemId(), 1);
                TestStandardItem level2Item = getParentItemByLevel_recursion(standardItemMap, a.getItemId(), 2);
                TestStandardItem level3Item = getParentItemByLevel_recursion(standardItemMap, a.getItemId(), 3);
                if (level1Item != null) {
                    a.setLevel1ItemName(level1Item.getName());
                }
                if (level2Item != null) {
                    a.setLevel2ItemName(level2Item.getName());
                }
                if (level3Item != null) {
                    a.setLevel3ItemName(level3Item.getName());
                }
            });
            seriesDataSpecPo.forEach(a -> {
                if (a.getItemName().equals("续航里程") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("100-0km/h制动距离") && a.getLevel3ItemName().equals("极寒雪地制动") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("100-0km/h制动距离") && a.getLevel3ItemName().equals("极寒干地制动") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("0-100km/h加速时间") && a.getLevel3ItemName().equals("极寒雪地加速") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("0-100km/h加速时间") && a.getLevel3ItemName().equals("极寒干地加速") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("30%-80%充电时长") && a.getLevel2ItemName().equals("寒冷区-10℃/5℃") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("雪地极速") && a.getLevel3ItemName().equals("极寒雪地极速") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                } else if (a.getItemName().equals("振动隔绝") && a.getLevel3ItemName().equals("座椅舒适性") && !a.getShowValue().equals("")) {
                    filterResult.add(a);
                }
            });

            if (CollectionUtils.isNotEmpty(filterResult)) {
                Map<Integer, List<TestDataSpecBasePo>> mapSpecData = filterResult.stream().collect(Collectors.groupingBy(TestDataSpecBasePo::getSpecId));
                if (mapSpecData != null && mapSpecData.size() > 0) {
                    for (Map.Entry<Integer, List<TestDataSpecBasePo>> entry : mapSpecData.entrySet()) {

                        SeriesTestDataDto.SeriesTestDataWinter120Dto specVo = new SeriesTestDataDto.SeriesTestDataWinter120Dto();
                        Integer tempSpecId = entry.getKey();

                        //屏蔽固定3个车型id
                        if (HIDE_WINTER2023_SPECIDS.contains(tempSpecId)) {
                            continue;
                        }

                        specVo.setSpecId(tempSpecId);
                        specVo.setStandardId(WINTERSTANDARDID);
                        //TODO 后期可走组件缓存
                        int fueltype = 0;
                        SpecEntity spec = specMapper.getSpec(tempSpecId);
                        if (spec != null) {
                            fueltype = spec.getFuelType();
                        }
                        specVo.setFueltypedetail(fueltype);

                        if (CollectionUtils.isNotEmpty(entry.getValue())) {
                            Integer tempDataID = entry.getValue().get(0).getDataId();
                            //返回单车视频信息
                            if (CollectionUtils.isNotEmpty(testDataContentVoList)) {
                                Optional<TestDataItemContent> optional = testDataContentVoList.stream().filter(a -> a.getDataId().equals(tempDataID)).findFirst();
                                if (optional.isPresent()) {
                                    specVo.setZixunVideoId(optional.get().getContentValue());
                                    specVo.setVideoId(optional.get().getSourceVideoId());
                                }
                            }
                            specVo.setDataId(tempDataID);
                            List<SeriesTestDataDto.TestWinterItemSummary> summary2023WinterList = new ArrayList<>();
                            for (TestDataSpecBasePo dataSpecPo : entry.getValue()) {
                                SeriesTestDataDto.TestWinterItemSummary summary2023Winter = new SeriesTestDataDto.TestWinterItemSummary();
                                summary2023Winter.setLevel3name(dataSpecPo.getLevel3ItemName());
                                summary2023Winter.setLevel2name(dataSpecPo.getLevel2ItemName());
                                summary2023Winter.setLevel1name(dataSpecPo.getLevel1ItemName());
                                summary2023Winter.setShowValue(dataSpecPo.getShowValue());
                                summary2023Winter.setUnit(dataSpecPo.getUnit());
                                summary2023Winter.setName(dataSpecPo.getItemName());
                                summary2023WinterList.add(summary2023Winter);
                            }
                            specVo.setTestItemlist(summary2023WinterList);
                        }
                        result.add(specVo);
                    }
                    if (mapSpecData.size() > 1) {
                        //同车系下多个车型，最新发布的排第一处理
                        result.sort(Comparator.comparing(SeriesTestDataDto.SeriesTestDataWinter120Dto::getDataId).reversed());
                    }
                }
            }
        }
        return result;
    }

    //根据子项取父项
    TestStandardItem getParentItemByLevel_recursion(Map<Integer, TestStandardItem> standardItemMap, Integer itemId, int levelId) {
        TestStandardItem item = standardItemMap.get(itemId);
        if (item != null) {
            if (item.getLevelId() == levelId) {
                return item;
            } else if (item.getParentId() != null) {
                return getParentItemByLevel_recursion(standardItemMap, item.getParentId(), levelId);
            }
        }
        return null;
    }
}
