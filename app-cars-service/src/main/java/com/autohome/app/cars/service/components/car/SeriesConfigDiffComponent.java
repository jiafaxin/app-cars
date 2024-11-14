package com.autohome.app.cars.service.components.car;

import autohome.rpc.car.app_cars.v1.carcfg.GetSpecParamConfigInfoResponse;
import com.autohome.app.cars.common.carconfig.Level;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecHighlightMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesConfigDiffDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecGroupOfSeriesDto;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 车系差异配置
 */
@Component
@DBConfig(tableName = "series_config_diff")
public class SeriesConfigDiffComponent extends BaseComponent<SeriesConfigDiffDto> {
    final String seriesIdParamName = "seriesId";

    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private SpecMapper specMapper;

    @Autowired
    private SpecHighlightMapper specHighlightMapper;

    @Autowired
    SpecYearNewComponent specYearNewComponent;

    @Autowired
    SpecDetailComponent specDetailComponent;

    /**
     * 亮点配置-》20230713 产品新需求：所有亮点配置主项，包括其下子项 全为亮点
     */
    private final List<Integer> highLightConfig_configItem_v2 = Arrays.asList(1, 2, 3, 4, 6, 8, 9, 10, 12, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24, 26, 30, 34, 38, 39, 40, 42, 45, 47, 51, 54, 61, 62, 63, 64, 67, 68, 77, 78, 79, 80, 82, 83, 85, 86, 93, 95, 97, 100, 101, 102, 104, 105, 106, 107, 108, 109, 110, 111, 112, 115, 116, 117, 118, 119, 121, 127, 130, 131, 133, 134, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 217, 218, 219, 220, 221, 222, 223, 225, 226, 227, 228, 229, 230, 231, 232, 233, 247, 248, 249, 250, 251, 252, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 278, 279, 280, 281, 282, 283, 284, 285, 286, 288, 289, 290, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 335, 336, 337, 338, 339, 340, 341, 342);
    /**
     * 亮点配置-》 纯电车和氢能源车取值
     * 50	最大功率(kW)
     * 52	最大扭矩(N·m)
     * 55	能源类型
     * 62	电机类型
     * 63	电动机总功率(kW)
     * 72	驱动电机数
     * 73	电机布局
     * 74	电池类型
     * 76	电池能量(kWh)
     * 86	驱动方式
     * 87	四驱形式
     * 89	前悬架类型
     * 90	后悬架类型
     * 108	简称
     * 114	电动机(Ps)
     * 122	电芯品牌
     **/
    private final List<Integer> highLightconfig_PureElectricParamId = Arrays.asList(50, 52, 55, 62, 63, 72, 73, 74, 76, 86, 87, 89, 90, 108, 114, 122);
    /**
     * 亮点配置-》增程、插电取值,油电混
     * 43	气缸数(个)
     * 50	最大功率(kW)
     * 52	最大扭矩(N·m)
     * 55	能源类型
     * 62	电机类型
     * 63	电动机总功率(kW)
     * 72	驱动电机数
     * 73	电机布局
     * 74	电池类型
     * 76	电池能量(kWh)
     * 86	驱动方式
     * 87	四驱形式
     * 89	前悬架类型
     * 90	后悬架类型
     * 108	简称
     * 114	电动机(Ps)
     * 115	发动机
     * 122	电芯品牌
     **/
    private final List<Integer> highLightconfig_ZengChengChaDianParamId = Arrays.asList(43, 50, 52, 55, 62, 63, 72, 73, 74, 76, 86, 87, 89, 90, 108, 114, 115, 122);

    /**
     * 亮点配置-》  油车取值
     * 43	气缸数(个)
     * 50	最大功率(kW)
     * 52	最大扭矩(N·m)
     * 55	能源类型
     * 86	驱动方式
     * 87	四驱形式
     * 89	前悬架类型
     * 90	后悬架类型
     * 108	简称
     * 115	发动机
     */
    private final List<Integer> highLightconfig_OilCarParamId = Arrays.asList(43, 50, 52, 55, 86, 87, 89, 90, 108, 115);

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesConfigDiffDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }


    public CompletableFuture<List<SeriesConfigDiffDto>> getList(List<Integer> seriesId) {
        return baseGetListAsync(seriesId.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public void refreshAll(int totalMinutes, Consumer<String> log) {
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());

        //车系map
        Map<Integer, List<SpecEntity>> seriesMap = specAll.stream().filter(x -> x.getIsImageSpec() == 0).collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        allSeries.forEach(entity -> {
            try {
                SeriesConfigDiffDto builder = builder(entity, seriesMap);
                if (builder != null) {
                    update(makeParam(entity.getId()), builder);
                    log.accept(entity.getId() + " success");
                }
            } catch (Exception e) {
                log.accept("车系:" + entity.getId() + "-车系差异配置，刷新失败：" + e.getMessage());
            }
        });
    }

    public void refresh(int seriesId) {

        SeriesEntity entity = seriesMapper.getSeries(seriesId);
        List<SpecEntity> specAll = Level.isCVLevel(entity.getLevelId()) ? specMapper.getCvSpecBySeriesId(entity.getId()) : specMapper.getSpecBySeriesId(entity.getId());
        //车系map
        Map<Integer, List<SpecEntity>> seriesMap = specAll.stream().filter(x -> x.getIsImageSpec() == 0).collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        try {
            SeriesConfigDiffDto builder = builder(entity, seriesMap);
            if (builder != null) {
                update(makeParam(entity.getId()), builder);
            }
        } catch (Exception e) {
            System.out.println("车系:" + entity.getId() + "-车系差异配置，刷新失败：" + e.getMessage());
        }
    }

    SeriesConfigDiffDto builder(SeriesEntity series, Map<Integer, List<SpecEntity>> seriesMap) {
        SeriesConfigDiffDto dto = new SeriesConfigDiffDto();
        List<SpecEntity> specEntities = seriesMap.get(series.getId());
        //获取两个车型差异项数量
        if (specEntities != null) {
            specEntities = specEntities.stream().filter(spec -> spec.getState() == 20 || spec.getState() == 30).collect(Collectors.toList());
            if (specEntities.size() > 1) {
                specEntities.sort(Comparator.comparing(SpecEntity::getMinPrice,Comparator.reverseOrder()).thenComparing(SpecEntity::getId,Comparator.reverseOrder()));
                SpecEntity specA = specEntities.get(0);
                SpecEntity specB = specEntities.get(specEntities.size() - 1);

                if (specA != null && specB != null) {
                    int diffcount = getSpecHighLightDiffCount(specA, specB);
                    if (diffcount > 0) {
                        SeriesConfigDiffDto.ConfigDiffDTO configDiffDTO = new SeriesConfigDiffDto.ConfigDiffDTO();
                        configDiffDTO.setDiffCount(diffcount);
                        configDiffDTO.getSpecIds().add(specA.getId());
                        configDiffDTO.getSpecIds().add(specB.getId());
                        dto.setConfigDiff(configDiffDTO);
                    }
                }
            }
        }
        //获取60点位图车型列表
        List<Integer> idList = specHighlightMapper.get60picSpecIdListBySeries(series.getId());
        if (idList != null && idList.size() > 1) {
            dto.getPicSpecIds().addAll(idList);
        }
        if (dto.getConfigDiff() == null && dto.getPicSpecIds().isEmpty()) {
            return null;
        }
        return dto;
    }

    public int getSpecHighLightDiffCount(SpecEntity specA, SpecEntity specB) {
        //车型标准配置
        LinkedList<SpecConfigItemRelaPic> listA = getHighLight_ParamAndConfig_BySpecV2(specA);
        LinkedList<SpecConfigItemRelaPic> listB = getHighLight_ParamAndConfig_BySpecV2(specB);
        if (listA.size() > listB.size()) {
            listA.removeAll(listB);
            return listA.size();
        } else {
            listB.removeAll(listA);
            return listB.size();
        }
    }


    public LinkedList<SpecConfigItemRelaPic> getHighLight_ParamAndConfig_BySpecV2(SpecEntity spec) {
        //车型标准配置
        List<SpecConfigItemRelaPic> configList_DB = getSpecStardConfig(spec.getId());

        LinkedList<SpecConfigItemRelaPic> filter_Result = new LinkedList<>();
        if (configList_DB != null && configList_DB.size() > 0) {
            //添加参数部分
            builderParamBaseInfo(spec, filter_Result);
            //过滤出亮点配置项
            Iterator<SpecConfigItemRelaPic> ite = configList_DB.listIterator();
            while (ite.hasNext()) {
                SpecConfigItemRelaPic item = ite.next();
                if (highLightConfig_configItem_v2.contains(item.getItemid())) {
                    filter_Result.add(item);
                }
            }
        }
        return filter_Result;
    }


    /**
     * 亮点配置构建基本参数部分
     *
     * @param configList
     */
    private void builderParamBaseInfo(SpecEntity spec, List<SpecConfigItemRelaPic> configList) {
        List<SpecConfigItemRelaPic> paramList = new ArrayList<>();
        List<HighLightParamItem> highLightParamItemList = this.getSpecHighLightParam(spec.getId());
        if (highLightParamItemList != null && highLightParamItemList.size() > 0) {
            int fueltypeDetail = spec.getFuelType(); //能源类型
            //根据能源类型过滤参数集合，不同能源类型展示不同参数项
            List<HighLightParamItem> filteResult_byFueltype = new ArrayList<>();
            if (fueltypeDetail == 4 || fueltypeDetail == 7) {
                filteResult_byFueltype = highLightParamItemList.stream().filter(a -> highLightconfig_PureElectricParamId.contains(a.getParamId())).collect(toList());
            } else if (fueltypeDetail == 3 || fueltypeDetail == 5 || fueltypeDetail == 6) {
                filteResult_byFueltype = highLightParamItemList.stream().filter(a -> highLightconfig_ZengChengChaDianParamId.contains(a.getParamId())).collect(toList());
            } else {
                filteResult_byFueltype = highLightParamItemList.stream().filter(a -> highLightconfig_OilCarParamId.contains(a.getParamId())).collect(toList());
            }

            for (HighLightParamItem highLightParamItem : filteResult_byFueltype) {
                SpecConfigItemRelaPic itemRelaPic = new SpecConfigItemRelaPic();
                int paramId = highLightParamItem.getParamId();
                int subParamId = highLightParamItem.getSubParamId();
                if (subParamId > 0) {
                    itemRelaPic.setItemname(highLightParamItem.getParamName());
                    itemRelaPic.setSubitemname(highLightParamItem.getSubParamName());
                } else {
                    if (StringUtils.isNotEmpty(highLightParamItem.getParamValue()) && highLightParamItem.getParamValue() != "-" && highLightParamItem.getParamValue() != "0" && highLightParamItem.getParamValue() != "0.0") {
                        if (highLightParamItem.getParamId().equals(108)) { //变速箱特殊处理
                            itemRelaPic.setItemname("变速箱");
                            itemRelaPic.setSubitemname(highLightParamItem.getParamValue());
                        } else {
                            itemRelaPic.setItemname(highLightParamItem.getParamName());
                            itemRelaPic.setSubitemname(highLightParamItem.getParamValue());
                        }
                    }
                }
                itemRelaPic.setTypeid(0);//组合数据用，参数部分默认typeid=0 （性能/车身）
                itemRelaPic.setItemvalue(itemRelaPic.getSubitemname());//补充为空串,必填。
                itemRelaPic.setSpecid(spec.getId());
                itemRelaPic.setDataType(1);
                itemRelaPic.setItemid(paramId);
                itemRelaPic.setSubitemid(subParamId);
                paramList.add(itemRelaPic);
            }
        }
        if (paramList != null && paramList.size() > 0) {
            configList.addAll(paramList);
        }
    }

    /**
     * 本地缓存车型亮点参数
     *
     * @param specId
     * @return
     */
    public List<HighLightParamItem> getSpecHighLightParam(Integer specId) {
        List<HighLightParamItem> pecHightLightPaamList = new ArrayList<>();
        if (specId < 1000000) {
            pecHightLightPaamList = specHighlightMapper.getSpecParamItemListOfHighLight(specId);
        } else {
            pecHightLightPaamList = specHighlightMapper.getSpecParamItemListOfHighLight_cv(specId);
        }

        return pecHightLightPaamList;
    }

    /**
     * 获取车型配置-标配
     *
     * @param specId
     * @return
     */
    public List<SpecConfigItemRelaPic> getSpecStardConfig(Integer specId) {
        List<SpecConfigItemRelaPic> specConfigList = specHighlightMapper.getSpecStandardConfigAndRelationPicItemList(specId);
        return specConfigList;
    }
}
