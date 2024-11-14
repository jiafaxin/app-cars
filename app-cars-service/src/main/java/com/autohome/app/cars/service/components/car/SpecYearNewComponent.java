package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.common.carconfig.Level;
import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecConfigMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecConfigItemRelaPic;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecGroupOfSeriesDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@RedisConfig(keyVersion = "v2")
@DBConfig(tableName = "spec_year")
public class SpecYearNewComponent extends BaseComponent<List<SpecGroupOfSeriesDto>> {


    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private SpecMapper specMapper;
    @Autowired
    SpecParamMapper specParamMapper;
    @Autowired
    SpecConfigMapper specConfigMapper;


    @Autowired
    SpecDetailComponent specDetailComponent;

    static String seriesIdParamName = "seriesId";

    //启用压缩
    @Override
    protected boolean gzip() {
        return true;
    }

    /**
     * /getComponentValueByMethod?component=specYearNew&seriesId=18&method=get1
     */
    public String get1(TreeMap<String, Object> params) {
        return JsonUtil.toString(get((int) params.get("seriesId")));
    }

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public List<SpecGroupOfSeriesDto> get(int seriesId) {
        return baseGet(makeParam(seriesId));
    }

    public CompletableFuture<List<SpecGroupOfSeriesDto>> getAsync(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> log) {
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());
        List<Integer> hideSeriesDiffConfig = seriesMapper.getHideSeriesDiffConfig();
        //车系map
        Map<Integer, List<SpecEntity>> seriesMap = specAll.stream()
                .filter(x -> x.getState() == 0 || (x.getState() != 0 && x.getIsImageSpec() == 0))
                .filter(x -> !(x.getState() == 40 && x.getMinPrice() == 0 && x.getMaxPrice() == 0))
                .collect(Collectors.groupingBy(SpecEntity::getSeriesId));

        allSeries.forEach(entity -> {
            try {
                //车系是否外显配置差异
                boolean dispDiffConfig = !(hideSeriesDiffConfig != null && hideSeriesDiffConfig.contains(entity.getId()));
                List<SpecGroupOfSeriesDto> builder = builder(entity, seriesMap, dispDiffConfig);
                update(makeParam(entity.getId()), builder);
                log.accept(entity.getId() + " success");
            } catch (Exception e) {
                log.accept("车系:" + entity.getId() + "-车型列表，刷新失败：" + e.getMessage());
            }
        });
    }

    public void refresh(int seriesId) {
        try {
            SeriesEntity entity = seriesMapper.getSeries(seriesId);
            List<SpecEntity> specAll = Level.isCVLevel(entity.getLevelId()) ? specMapper.getCvSpecBySeriesId(entity.getId()) : specMapper.getSpecBySeriesId(entity.getId());
            List<Integer> hideSeriesDiffConfig = seriesMapper.getHideSeriesDiffConfig();
            //车系map
            Map<Integer, List<SpecEntity>> seriesMap = specAll.stream()
                    .filter(x -> x.getState() == 0 || (x.getState() != 0 && x.getIsImageSpec() == 0))
                    .filter(x -> !(x.getState() == 40 && x.getMinPrice() == 0 && x.getMaxPrice() == 0))
                    .collect(Collectors.groupingBy(SpecEntity::getSeriesId));
            //车系是否外显配置差异
            boolean dispDiffConfig = !(hideSeriesDiffConfig != null && hideSeriesDiffConfig.contains(entity.getId()));

            List<SpecGroupOfSeriesDto> builder = builder(entity, seriesMap, dispDiffConfig);

            update(makeParam(entity.getId()), builder);

        } catch (Exception ex) {
            log.error("车系年代款刷新失败：" + seriesId,ex);
        }
    }

    private List<SpecGroupOfSeriesDto> builder(SeriesEntity series, Map<Integer, List<SpecEntity>> seriesMap, boolean dispDiffConfig) {
        List<SpecGroupOfSeriesDto> specYearDto = new ArrayList<>();

        //tab 排序 在售、即将销售、未售、纯电动、排量、座位数、年代款

        List<SpecEntity> specEntities = seriesMap.get(series.getId());
        if (specEntities == null || specEntities.isEmpty()) {
            return specYearDto;
        }
        List<String> bj40_2024_sortList = Arrays.asList("2.0升 涡轮增压 224马力 国VI", "2.0升 涡轮增压 245马力 国VI", "2.0升 涡轮增压 163马力 国VI", "2.3升 涡轮增压 231马力 国VI");
        AtomicInteger tabIndex = new AtomicInteger(1);
        //在售
        List<Integer> onSaleSpecsIds = specEntities.stream().filter(spec -> spec.getState() == 20 || spec.getState() == 30)
                .map(SpecEntity::getId).collect(Collectors.toList());
        List<SpecDetailDto> onSalespecDetailDtos = mGetSpec(onSaleSpecsIds);
        if (CollectionUtils.isNotEmpty(onSalespecDetailDtos)) {
            List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> specGroupList = new ArrayList<>();
            //年代款分组、排序 map
            Map<Integer, List<SpecDetailDto>> yearSpecListOnSellMap = onSalespecDetailDtos.stream()
                    .collect(Collectors.groupingBy(SpecDetailDto::getYearName, () -> new TreeMap<>(Comparator.reverseOrder()), Collectors.toList()));

            //遍历年代款map
            yearSpecListOnSellMap.forEach((k, v) -> {
                //参数是否外显分组
                Map<Integer, List<SpecDetailDto>> paramShowMap = v.stream().collect(Collectors.groupingBy(SpecDetailDto::getParamIsShow, Collectors.toList()));
                //在售参数外显分组
                if (paramShowMap.containsKey(1)) {
                    specGroupList.addAll(groupSpecList(paramShowMap.get(1), k + "款", dispDiffConfig));
                    //北京BJ40车系分组排序特殊处理
                    if (series.getId() == 623) {
                        if (k.equals(2024)) {
                            //2024款在售车型分组排序处理
                            if (CollectionUtils.isNotEmpty(specGroupList)) {
                                //subYlb.getSpecgourplist() 按sortlist排序
                                Collections.sort(specGroupList, Comparator.comparingInt(o -> bj40_2024_sortList.indexOf(o.getName())));
                            }
                        }
                    }
                }
                //参数配置未公布分组
                if (paramShowMap.containsKey(0)) {
                    SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup item_specGroup = new SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup(
                            "参数配置未公布", k + "款",
                            paramShowMap.get(0).stream().sorted(Comparator.comparing(SpecDetailDto::getOrders)).collect(Collectors.toList()));
                    specGroupList.add(item_specGroup);
                }

            });
            if (specGroupList.size() > 0) {
                specYearDto.add(new SpecGroupOfSeriesDto("在售", tabIndex.getAndAdd(1), specGroupList));
            }
        }


        //即将销售
        List<Integer> willSaleSpecsIds = specEntities.stream().filter(spec -> spec.getState() == 10).map(SpecEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(willSaleSpecsIds)) {
            List<SpecDetailDto> specDetailDtos = mGetSpec(willSaleSpecsIds);
            if (CollectionUtils.isNotEmpty(specDetailDtos)) {
                List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> specGroupList = new ArrayList<>();
                //年代款分组、排序 map
                Map<Integer, List<SpecDetailDto>> yearSpecListOnSellMap = specDetailDtos.stream().collect(Collectors.groupingBy(SpecDetailDto::getYearName, () -> new TreeMap<>(Comparator.reverseOrder()), Collectors.toList()));
                //遍历年代款map
                yearSpecListOnSellMap.forEach((k, v) -> {
                    Map<Boolean, List<SpecDetailDto>> waitSellMap = v.stream().filter(s -> s.getParamIsShow() == 1).collect(Collectors.groupingBy(s -> s.getEnginePower() == 0 || StringUtils.isBlank(s.getEmissionStandards()) || "-".equals(s.getEmissionStandards())));
                    if (waitSellMap.containsKey(true)) {
                        specGroupList.add(new SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup(
                                "即将销售", k + "款",
                                waitSellMap.get(true).stream().sorted(Comparator.comparing(SpecDetailDto::getOrders)).collect(Collectors.toList())));
                    }
                    if (waitSellMap.containsKey(false)) {
                        specGroupList.addAll(groupSpecList(waitSellMap.get(false), k + "款", dispDiffConfig));
                    }

                    List<SpecDetailDto> paramNotShowList = v.stream().filter(s -> s.getParamIsShow() == 0).collect(Collectors.toList());
                    if (paramNotShowList.size() > 0) {
                        specGroupList.add(new SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup(
                                "参数配置未公布", k + "款",
                                paramNotShowList.stream().sorted(Comparator.comparing(SpecDetailDto::getOrders)).collect(Collectors.toList())));
                        v.removeAll(paramNotShowList);
                    }
                });
                if (specGroupList.size() > 0) {
                    specYearDto.add(new SpecGroupOfSeriesDto("即将销售", tabIndex.getAndAdd(1), specGroupList));
                }
            }
        }

        //未售
        List<SpecEntity> notSaleSpecs = specEntities.stream().filter(spec -> spec.getState() == 0
                && spec.getId() < 1000000
                && spec.getParamIsShow() == 1
                && NumberUtils.toInt(spec.getIsForeignCar() + "", 0) == 1).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notSaleSpecs)) {
            List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> specGroupList = new ArrayList<>();
            specGroupList.add(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup.SpecGroupOfSeriesItem_SpecGroup2(
                    "", "",
                    notSaleSpecs.stream().sorted(Comparator.comparing(SpecEntity::getOrders)).collect(Collectors.toList())));
            specYearDto.add(new SpecGroupOfSeriesDto("未售", tabIndex.getAndAdd(1), specGroupList));
        }
        SpecGroupOfSeriesDto specGroupOfSeriesDto = specYearDto.stream().filter(x -> "在售".equals(x.getYearname())).findFirst().orElse(null);
        if (specGroupOfSeriesDto != null && specGroupOfSeriesDto.getYearspeclist() != null) {
            List<SpecGroupOfSeriesDto.Spec> specList = new ArrayList<>();
            specGroupOfSeriesDto.getYearspeclist().forEach(x -> {
                if (x.getSpeclist() != null) {
                    specList.addAll(x.getSpeclist());
                }
            });

            //燃料类型
            Map<Integer, List<SpecGroupOfSeriesDto.Spec>> fuelTypeMap = specList.stream().collect(Collectors.groupingBy(SpecGroupOfSeriesDto.Spec::getFuelType));
            if (fuelTypeMap != null && fuelTypeMap.size() > 1 && fuelTypeMap.containsKey(4)) {
                List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> fuelTypeGroupList = new ArrayList<>();
                fuelTypeGroupList.add(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup.SpecGroupOfSeriesItem_SpecGroup1("", "", fuelTypeMap.get(4)));
                specYearDto.add(new SpecGroupOfSeriesDto("纯电动", tabIndex.getAndAdd(1), fuelTypeGroupList));
            }

            //排量
            List<SpecGroupOfSeriesDto.Spec> displacementList = specList.stream().filter(x -> x.getDisplacement() != null && x.getDisplacement().compareTo(new BigDecimal(0)) > 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(displacementList)) {
                Map<String, List<SpecGroupOfSeriesDto.Spec>> displacementMap = displacementList.stream().collect(Collectors.groupingBy(xx -> xx.getDisplacement() + (xx.getFlowModeId() == 1 ? "L" : "T")));
                if (displacementMap.size() >= 2) {
                    TreeMap<String, List<SpecGroupOfSeriesDto.Spec>> sortedMap = new TreeMap<>(displacementMap);
                    sortedMap.forEach((displacement, displacementSpecs) -> {
                        List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> displacementGroupList = new ArrayList<>();
                        displacementGroupList.add(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup.SpecGroupOfSeriesItem_SpecGroup1("", "", displacementSpecs));
                        specYearDto.add(new SpecGroupOfSeriesDto(displacement, tabIndex.getAndAdd(1), displacementGroupList));
                    });
                }
            }

            //座位数
            List<SpecGroupOfSeriesDto.Spec> seatList = specList.stream().filter(x -> x.getSeatCount() > 0 && x.getSpecId() < 1000000).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(seatList)) {
                Map<Integer, List<SpecGroupOfSeriesDto.Spec>> seatMap = seatList.stream().collect(Collectors.groupingBy(SpecGroupOfSeriesDto.Spec::getSeatCount));
                if (seatMap.size() >= 2) {
                    TreeMap<Integer, List<SpecGroupOfSeriesDto.Spec>> sortedMap = new TreeMap<>(seatMap);
                    sortedMap.forEach((seat, seatSpecs) -> {
                        String groupName = seat + "座";
                        List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> seatGroupList = new ArrayList<>();
                        seatGroupList.add(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup.SpecGroupOfSeriesItem_SpecGroup1("", "", seatSpecs));
                        specYearDto.add(new SpecGroupOfSeriesDto(groupName, tabIndex.getAndAdd(1), seatGroupList));
                    });
                }
            }
        }

        //所有年代款，按照year顺序添加
        Map<Integer, List<SpecEntity>> yearGroup = specEntities.stream().filter(x -> x.getState() != 0).collect(Collectors.groupingBy(SpecEntity::getYearName, () -> new TreeMap<>(Collections.reverseOrder()), Collectors.toList()));
        //先加在售的
        yearGroup.forEach((year, yearSpecs) -> {
            Integer yearState = yearSpecs.stream().min(Comparator.comparing(SpecEntity::getState)).map(SpecEntity::getState).get();
            if (yearState != 40) {
                List<Integer> collect = yearSpecs.stream().map(SpecEntity::getId).collect(Collectors.toList());
                List<SpecDetailDto> specDetailDtos = mGetSpec(collect);
                if (CollectionUtils.isNotEmpty(specDetailDtos)) {
                    List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> seatGroupList = new ArrayList<>();
                    String groupName = year + "款";
                    List<SpecDetailDto> tempBeans = specDetailDtos.stream().filter(s -> (s.getState() == 10 && (s.getEnginePower() == 0 || StringUtils.isBlank(s.getEmissionStandards()) || "-".equals(s.getEmissionStandards())))).sorted(Comparator.comparing(SpecDetailDto::getOrders)).collect(Collectors.toList());
                    //即将销售 分组
                    if (tempBeans.size() > 0) {
                        seatGroupList.add(new SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup("即将销售", groupName, tempBeans));
                    }

                    // 在售+待售参数外显的
                    tempBeans = specDetailDtos.stream().filter(s -> s.getState() != 40 && !(s.getState() == 10 && (s.getEnginePower() == 0 || StringUtils.isBlank(s.getEmissionStandards()) || "-".equals(s.getEmissionStandards()))) && s.getParamIsShow() == 1).collect(Collectors.toList());
                    if (tempBeans.size() > 0) {
                        seatGroupList.addAll(groupSpecList(tempBeans, groupName, dispDiffConfig));
                        //BJ40 在售2024款特殊处理
                        if (series.getId() == 623 && year.equals(2024)) {
                            //2024款在售车型分组排序处理
                            if (CollectionUtils.isNotEmpty(seatGroupList)) {
                                //subYlb.getSpecgourplist() 按sortlist排序
                                Collections.sort(seatGroupList, Comparator.comparingInt(o -> bj40_2024_sortList.indexOf(o.getName())));
                            }
                        }
                    }
                    //在售参数未公布分组
                    tempBeans = specDetailDtos.stream().filter(s -> (s.getState() == 20 || s.getState() == 30) && s.getParamIsShow() == 0).sorted(Comparator.comparing(SpecDetailDto::getOrders)).collect(Collectors.toList());
                    if (tempBeans.size() > 0) {
                        seatGroupList.add(new SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup("参数配置未公布", groupName, tempBeans));
                    }
                    //停售车型分组
                    tempBeans = specDetailDtos.stream().filter(s -> (s.getState() == 40)).sorted(Comparator.comparing(SpecDetailDto::getOrders)).collect(Collectors.toList());
                    if (tempBeans.size() > 0) {
                        seatGroupList.add(new SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup(year + "款(停售)", groupName, tempBeans));
                    }

                    if (seatGroupList.size() > 0) {
                        specYearDto.add(new SpecGroupOfSeriesDto(groupName, year, seatGroupList, yearState));
                    }
                }
            }
        });
        yearGroup.forEach((year, yearSpecs) -> {
            if (CollectionUtils.isNotEmpty(yearSpecs)) {
                Integer yearState = yearSpecs.stream().min(Comparator.comparing(SpecEntity::getState)).map(SpecEntity::getState).get();
                if (yearState == 40) {
                    List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> seatGroupList = new ArrayList<>();
                    String groupName = year + "款";
                    seatGroupList.add(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup.SpecGroupOfSeriesItem_SpecGroup2(
                            groupName, groupName + "(停售)",
                            yearSpecs.stream().sorted(Comparator.comparing(SpecEntity::getMinPrice)).collect(Collectors.toList())));
                    if (seatGroupList.size() > 0) {
                        specYearDto.add(new SpecGroupOfSeriesDto(groupName, year, seatGroupList, yearState));
                    }
                }
            }
        });
        return specYearDto;
    }

    /**
     * 在售车型分组
     */
    private List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> groupSpecList(List<SpecDetailDto> specitemsBeans, String yearName, boolean dispDiffConfig) {
        List<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> tempList = new ArrayList<>();
        String ddPatern = "电动 %s马力";
        String oilPatern = "%s升 %s %s马力 %s";
        String qiyoudianquPatern = "%s升 %s %s马力 %s";   //"发动机排量+燃料形式+电动机马力"
        for (SpecDetailDto specitemsBean : specitemsBeans) {
            String subName;
            double displacement = specitemsBean.getDisplacement() != null ? specitemsBean.getDisplacement().doubleValue() : 0D;
            String emissionStandards = specitemsBean.getEmissionStandards() != null && !"-".equals(specitemsBean.getEmissionStandards()) ? specitemsBean.getEmissionStandards() : "";
//           能源类型为纯电动和排量为0的车型
            if ((specitemsBean.getFuelType() == 4 || specitemsBean.getFuelType() == 7) && displacement <= 0) {
                subName = String.format(ddPatern, specitemsBean.getEnginePower());
            } else if (specitemsBean.getFuelType() == 12) { //汽油电驱分组名
                subName = String.format(qiyoudianquPatern, displacement, "汽油电驱", (int) (specitemsBean.getElectricKw() * 1.36), emissionStandards);
            } else {
                if (displacement > 0 && specitemsBean.getEnginePower() > 0) {
                    subName = String.format(oilPatern, displacement, specitemsBean.getFlowModeName(), specitemsBean.getEnginePower(), emissionStandards);
                } else {
                    subName = "";
                }
            }
            //根据名称 创建组
            Optional<SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup> anyOptional = tempList.stream().filter(s -> s.getName().equals(subName)).findAny();
            SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup newSpecgourplistBean;

            if (!anyOptional.isPresent()) {
                //下列字段用于排序，

                newSpecgourplistBean = SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup.SpecGroupOfSeriesItem_SpecGroup1(subName, yearName, new ArrayList<>());
                newSpecgourplistBean.setDisplacement(displacement);
                newSpecgourplistBean.setEnginepower(specitemsBean.getEnginePower());
                newSpecgourplistBean.setIsclassic(specitemsBean.isClassic());
                newSpecgourplistBean.setFlowmodeid(specitemsBean.getFlowModeId());
                newSpecgourplistBean.setEmissionstandardsNum(emissionStandards.equals("国VI") ? 6 : 5);
                newSpecgourplistBean.setIsOil((specitemsBean.getFuelType() == 4 && displacement <= 0) ? 0 : 1);
                tempList.add(newSpecgourplistBean);
            } else {
                newSpecgourplistBean = anyOptional.get();
            }
            SpecGroupOfSeriesDto.Spec spec = new SpecGroupOfSeriesDto.Spec();
            BeanUtils.copyProperties(specitemsBean, spec);
            newSpecgourplistBean.getSpeclist().add(spec);
        }        // 组内按照优先级“在售非经典款>在售经典款>停产在售非经典款>停产在售经典款”，相同优先级的，按照车型列表Order值升序

        for (SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup specgourplistBean : tempList) {
            double average = specgourplistBean.getSpeclist().stream().mapToInt(m -> m.getState()).summaryStatistics().getAverage();
            specgourplistBean.setAverage(average);
            specgourplistBean.getSpeclist().sort(Comparator.comparing(SpecGroupOfSeriesDto.Spec::getState).thenComparing(SpecGroupOfSeriesDto.Spec::isClassic).thenComparing(SpecGroupOfSeriesDto.Spec::getOrders));

            /**
             * 设置同组内的差异化车型配置项字段
             */
            if (dispDiffConfig) {
                SetDiffConfigInfoField(average, specgourplistBean);
            }
        }
        //组与组之间排序按照优先级“马力（升序排列）>进气形式（涡轮增压>自然吸气）>排量（升序排列）>环保标准（降序排列）”
        tempList.sort(Comparator.comparing(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getAverage)
                .thenComparing(Comparator.comparing(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getIsOil).reversed())
                .thenComparing(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getEnginepower)
                .thenComparing(Comparator.comparing(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getFlowmodeid).reversed())
                .thenComparing(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getDisplacement)
                .thenComparing(Comparator.comparing(SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup::getEmissionstandardsNum).reversed()));

        return tempList;
    }

    /**
     * 补充车型信息
     */
    List<SpecDetailDto> mGetSpec(List<Integer> specIds) {
        if (specIds != null && specIds.size() > 0) {
            List<SpecDetailDto> specDetailDtos = specDetailComponent.mGet(specIds);
            return specDetailDtos.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 在售车型列表，当前车型与下一车型的配置差异
     */
    private void SetDiffConfigInfoField(double average, SpecGroupOfSeriesDto.SpecGroupOfSeriesItem_SpecGroup specgourplistBean) {
        try {
            if (average >= 20 && average <= 30 && specgourplistBean.getSpeclist().size() > 1) {
                List<SpecGroupOfSeriesDto.Spec> listSpecBean = specgourplistBean.getSpeclist();
                for (int i = 0, len = listSpecBean.size(); i < len; i++) {
                    if (i == len - 1) {
                        break;
                    }
                    SpecGroupOfSeriesDto.Spec currentSpec = listSpecBean.get(i);
                    currentSpec.setDiffconfigofhighlight(getHighLightDiffInfoByGroup(currentSpec, listSpecBean.get(i + 1)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 基于亮点配置的差异计算（B车多于A车的）
     */
    public SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight getHighLightDiffInfoByGroup(SpecGroupOfSeriesDto.Spec currentSpec, SpecGroupOfSeriesDto.Spec nextSpec) {

        if (!(currentSpec.getSpecId() > 0 && nextSpec.getSpecId() > 0 && currentSpec.getSpecId() < 1000000 && nextSpec.getSpecId() < 1000000)) {
            return null;
        }
        int diffPrice = currentSpec.getMinPrice() - nextSpec.getMinPrice();
        //只有加价才显示差异
        if (diffPrice >= 0) {
            return null;
        }
        SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight diffConfigInfo = new SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight(); //存储封装好的返回信息
        String specIds = String.format("%s,%s", currentSpec.getSpecId(), nextSpec.getSpecId());
        List<SpecConfigItemRelaPic> specStardConfig = specConfigMapper.getSpecStardConfig(specIds);
        List<SpecParamEntity> specHighLightParam = specParamMapper.getSpecHighLightParam(specIds);

        List<SpecConfigItemRelaPic> listSpecA = getHighLight_ParamAndConfig_BySpecV2(currentSpec, specStardConfig, specHighLightParam);
        List<SpecConfigItemRelaPic> listSpecB = getHighLight_ParamAndConfig_BySpecV2(nextSpec, specStardConfig, specHighLightParam);
        List<SpecConfigItemRelaPic> listDiffSpecConfig = getDifferentBThanA_v2(listSpecA, listSpecB);

        if (CollectionUtils.isNotEmpty(listDiffSpecConfig)) {
            //分组排序配置差异项
            Map<String, List<SpecConfigItemRelaPic>> mapGroupConfigList = listDiffSpecConfig.stream()
                    .collect(Collectors.groupingBy(a -> (a.getDataType() + "_" + a.getTypeid()), TreeMap::new, Collectors.toList())); //按配置项分组 + "_" + a.getItemorder() + "_" + a.getItemid()
            List<SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight.DiffConfigItemByGroup> listGroup = new ArrayList<>();
            //计算重名子项，如果有重名的外显的name加上主项的名字。
            List<String> listRepeatSubItemName = listDiffSpecConfig.stream().filter(x -> x.getSubitemid() > 0)
                    .collect(Collectors.toMap(SpecConfigItemRelaPic::getSubitemname, e -> 1, (a, b) -> a + b)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
                    .entrySet().stream() // Set<Entry>转换为Stream<Entry>
                    .filter(entry -> entry.getValue() > 1) // 过滤出元素出现次数大于 1 的 entry
                    .map(entry -> entry.getKey()) // 获得 entry 的键（重复元素）对应的 Stream
                    .collect(Collectors.toList());

            for (Map.Entry<String, List<SpecConfigItemRelaPic>> item : mapGroupConfigList.entrySet()) {
                List<SpecConfigItemRelaPic> itemRelaPicList = item.getValue();
                SpecConfigItemRelaPic specConfigItem = itemRelaPicList.get(0);
                Integer dataType = specConfigItem.getDataType();
                String gpName = dataType == 1 ? "性能/车身" : specConfigItem.getTypename();
                List<SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight.DiffConfigItemByGroup.DiffConfigItem> listDiffConfigItem = new ArrayList<>();
                for (SpecConfigItemRelaPic specConfigItemRelaPic : item.getValue()) {
                    SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight.DiffConfigItemByGroup.DiffConfigItem diffConfigItem = new SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight.DiffConfigItemByGroup.DiffConfigItem();
                    //不管主项还是子项都当作配置项返回业务端 直接输出结果用。
                    // 逻辑上只要子项id>0只算子项的id、name和图片信息（每个车型配置后台关联的图片，没有就取配置的默认logo）反之 子项id=0 输出主项的信息
                    if (specConfigItemRelaPic.getSubitemid() > 0) {
                        diffConfigItem.setId(specConfigItemRelaPic.getSubitemid());
                        //子项名重复处理方案：主项名+子项名
                        if (listRepeatSubItemName.size() > 0 && listRepeatSubItemName.contains(specConfigItemRelaPic.getSubitemname()) && specConfigItemRelaPic.getItemname() != null) {
                            diffConfigItem.setName(specConfigItemRelaPic.getItemname() + specConfigItemRelaPic.getSubitemname());
                        } else {
                            diffConfigItem.setName(specConfigItemRelaPic.getSubitemname());
                        }
                        diffConfigItem.setImage(specConfigItemRelaPic.getSubitemlogo());
                    } else {
                        diffConfigItem.setId(specConfigItemRelaPic.getItemid());
                        diffConfigItem.setName(specConfigItemRelaPic.getItemname());
                        diffConfigItem.setImage(specConfigItemRelaPic.getItemlogo());
                    }
                    listDiffConfigItem.add(diffConfigItem);
                }
                if (CollectionUtils.isNotEmpty(listDiffConfigItem)) {
                    SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight.DiffConfigItemByGroup diffConfigItemByGroup = new SpecGroupOfSeriesDto.Spec.DiffConfigInfoHighlight.DiffConfigItemByGroup();
                    diffConfigItemByGroup.setName(gpName);
                    diffConfigItemByGroup.setList(listDiffConfigItem);
                    listGroup.add(diffConfigItemByGroup);
                }
            }
            if (CollectionUtils.isNotEmpty(listGroup)) {
                AtomicReference<Integer> diffCount = new AtomicReference<>(0);
                listGroup.forEach(a -> {
                    if (CollectionUtils.isNotEmpty(a.getList())) {
                        diffCount.updateAndGet(v -> v + a.getList().size());
                    }
                });
                diffConfigInfo.setDiffcount(diffCount.get());
                diffConfigInfo.setPrice(diffPrice);
                diffConfigInfo.setGrouplist(listGroup);
            }
        }
        return diffConfigInfo;
    }

    /**
     * 亮点配置-》20230713 产品新需求：所有亮点配置主项，包括其下子项 全为亮点
     */
    private final List<Integer> highLightConfig_configItem_v2 = Arrays.asList(1, 2, 3, 4, 6, 8, 9, 10, 12, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24, 26, 30, 34, 38, 39, 40, 42, 45, 47, 51, 54, 61, 62, 63, 64, 67, 68, 77, 78, 79, 80, 82, 83, 85, 86, 93, 95, 97, 100, 101, 102, 104, 105, 106, 107, 108, 109, 110, 111, 112, 115, 116, 117, 118, 119, 121, 127, 130, 131, 133, 134, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 217, 218, 219, 220, 221, 222, 223, 225, 226, 227, 228, 229, 230, 231, 232, 233, 247, 248, 249, 250, 251, 252, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 278, 279, 280, 281, 282, 283, 284, 285, 286, 288, 289, 290, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 335, 336, 337, 338, 339, 340, 341, 342);
    private final List<Integer> highLightconfig_PureElectricParamId = Arrays.asList(55, 62, 72, 74, 86, 87, 89, 90);
    private final List<Integer> highLightconfig_ZengChengChaDianParamId = Arrays.asList(43, 55, 62, 72, 74, 86, 87, 89, 90);
    private final List<Integer> highLightconfig_OilCarParamId = Arrays.asList(43, 55, 86, 87, 89, 90);

    List<SpecConfigItemRelaPic> getHighLight_ParamAndConfig_BySpecV2(SpecGroupOfSeriesDto.Spec spec, List<SpecConfigItemRelaPic> specStardConfig, List<SpecParamEntity> specHighLightParam) {
        //车型标准配置
        List<SpecConfigItemRelaPic> specConfigItemRelaPicList = specStardConfig.stream()
                .filter(x -> x.getSpecid() == spec.getSpecId() && highLightConfig_configItem_v2.contains(x.getItemid()))
                .sorted(Comparator.comparing(SpecConfigItemRelaPic::getTypeSort)
                        .thenComparing(SpecConfigItemRelaPic::getItemorder))
                .collect(Collectors.toList());
        List<SpecConfigItemRelaPic> filter_Result = new ArrayList<>();
        if (specConfigItemRelaPicList != null && specConfigItemRelaPicList.size() > 0) {
            //添加参数部分
            filter_Result.addAll(builderParamBaseInfo(spec, specHighLightParam));
            //亮点配置项
            filter_Result.addAll(specConfigItemRelaPicList);
        }
        return filter_Result;
    }

    List<SpecConfigItemRelaPic> builderParamBaseInfo(SpecGroupOfSeriesDto.Spec spec, List<SpecParamEntity> specHighLightParam) {
        List<SpecConfigItemRelaPic> filter_Result = new ArrayList<>();
        List<SpecParamEntity> specParamEntityList = specHighLightParam.stream().filter(x -> x.getSpecId() == spec.getSpecId()).sorted(Comparator.comparing(SpecParamEntity::getParamSort)).collect(Collectors.toList());

        if (specParamEntityList != null && specParamEntityList.size() > 0) {
            int fuelType = spec.getFuelType();//能源类型
            //根据能源类型过滤参数集合，不同能源类型展示不同参数项
            List<SpecParamEntity> filteResult_byFueltype;
            if (fuelType == 4 || fuelType == 7) {
                filteResult_byFueltype = specParamEntityList.stream().filter(a -> highLightconfig_PureElectricParamId.contains(a.getParamId())).collect(toList());
            } else if (fuelType == 3 || fuelType == 5 || fuelType == 6) {
                filteResult_byFueltype = specParamEntityList.stream().filter(a -> highLightconfig_ZengChengChaDianParamId.contains(a.getParamId())).collect(toList());
            } else {
                filteResult_byFueltype = specParamEntityList.stream().filter(a -> highLightconfig_OilCarParamId.contains(a.getParamId())).collect(toList());
            }

            for (SpecParamEntity highLightParamItem : filteResult_byFueltype) {
                SpecConfigItemRelaPic itemRelaPic = new SpecConfigItemRelaPic();
                int paramId = highLightParamItem.getParamId();
                int subParamId = NumberUtils.toInt(highLightParamItem.getSubParamId());
                if (subParamId > 0) {
                    itemRelaPic.setItemname(highLightParamItem.getParamName());
                    itemRelaPic.setSubitemname(highLightParamItem.getSubParamName());
                    if (StringUtils.isNotEmpty(highLightParamItem.getLogo())) {
                        itemRelaPic.setSubitemlogo(CarSettings.getInstance().GetFullImagePath(highLightParamItem.getLogo()));
                    }
                } else {
                    if (StringUtils.isNotEmpty(highLightParamItem.getParamValue()) && highLightParamItem.getParamValue() != "-" && highLightParamItem.getParamValue() != "0" && highLightParamItem.getParamValue() != "0.0") {
                        itemRelaPic.setItemname(highLightParamItem.getParamId() == 108 ? "变速箱" : highLightParamItem.getParamName());
                        itemRelaPic.setSubitemname(highLightParamItem.getParamValue());
                        //主项logo
                        if (StringUtils.isNotEmpty(highLightParamItem.getLogo())) {
                            itemRelaPic.setItemlogo(CarSettings.getInstance().GetFullImagePath(highLightParamItem.getLogo()));
                        }
                    }
                }
                itemRelaPic.setTypeid(0);//组合数据用，参数部分默认typeid=0 （性能/车身）
                itemRelaPic.setItemvalue(itemRelaPic.getSubitemname());//补充为空串,必填。
                itemRelaPic.setSpecid(spec.getSpecId());
                itemRelaPic.setDataType(1);
                itemRelaPic.setItemid(paramId);
                itemRelaPic.setSubitemid(subParamId);
                filter_Result.add(itemRelaPic);
            }
        }
        return filter_Result;
    }


    public List<SpecConfigItemRelaPic> getDifferentBThanA_v2(List<SpecConfigItemRelaPic> listA, List<SpecConfigItemRelaPic> listB) {
        if (CollectionUtils.isEmpty(listA) || CollectionUtils.isEmpty(listB)) {
            return null;
        }
        List<SpecConfigItemRelaPic> diffList = new ArrayList<SpecConfigItemRelaPic>();
        Map<SpecConfigItemRelaPic, Integer> map = new HashMap<SpecConfigItemRelaPic, Integer>(listB.size()); //
        for (SpecConfigItemRelaPic scf : listB) {
            map.put(scf, 1);
        }
        for (SpecConfigItemRelaPic scf : listA) {
            if (map.get(scf) != null) {
                map.put(scf, 2);
            } else {
                map.put(scf, 3);
            }
        }
        //取HashMap里值=1 的就是B车型比A车型多的配置项(B有A没有的部分)
        for (Map.Entry<SpecConfigItemRelaPic, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diffList.add(entry.getKey());
            }
        }
        //车型配置关联过的图片
        if (diffList.size() > 0) {
            for (SpecConfigItemRelaPic scf : diffList) {
                String picUrl = scf.getPicurl();//车型配置关联的图片
                //补充没有关联车型配置图片的配置默认logo
                if (scf.getDataType() == 2) {
                    if (scf.getSubitemid() > 0) {
                        //配置子项logo 赋值
                        scf.setSubitemlogo(StringUtils.isNotEmpty(picUrl) ? picUrl : CarSettings.getInstance().GetFullImagePathByPrefix(scf.getSubitemlogo(), ""));
                        //赋值配置子项名称 优先展示别名，对用户提示友好。
                        scf.setSubitemname(StringUtils.isNotEmpty(scf.getSubitemalias()) ? scf.getSubitemalias() : scf.getSubitemname());
                    } else {
                        //配置主项logo 赋值
                        scf.setItemlogo(StringUtils.isNotEmpty(picUrl) ? picUrl : CarSettings.getInstance().GetFullImagePathByPrefix(scf.getItemlogo(), ""));
                    }
                }
            }
        }
        return diffList;
    }
}
