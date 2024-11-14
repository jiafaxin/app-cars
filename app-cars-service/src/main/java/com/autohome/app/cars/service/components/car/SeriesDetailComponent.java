package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.CmsNewCarResult;
import com.autohome.app.cars.common.carconfig.Level;
import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.utils.CarSettings;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * TODO check ??? 如果redis不存在，应该怎么处理？
 */
@Component
@DBConfig(tableName = "series_detail")
@RedisConfig
public class SeriesDetailComponent extends BaseComponent<SeriesDetailDto> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SpecMapper specMapper;
    @Autowired
    CmsApiClient cmsApiClient;
    @Autowired
    SeriesAttentionComponent seriesAttentionComponent;

    static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public SeriesDetailDto get(int seriesId) {
        return baseGet(makeParam(seriesId));
    }

    public CompletableFuture<SeriesDetailDto> getAsync(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public CompletableFuture<List<SeriesDetailDto>> getList(List<Integer> seriesId) {
        return baseGetListAsync(seriesId.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    public List<SeriesDetailDto> getListSync(List<Integer> seriesId) {
        return baseGetList(seriesId.stream().map(this::makeParam).toList());
    }

    /**
     * 从库里拉取所有数据到redis&db
     */
    public void refreshAll(Consumer<String> log) {
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        List<SeriesFuelTypeEntity> seriesFuelTypes = seriesMapper.getSeriesFuelTypeDetailAll();
        List<SeriesHasBookSpecEntity> seriesHasBooks = seriesMapper.getSeriesHasBookSpecAll();
        List<Integer> seriesIdsParamIsShow = seriesMapper.getSeriesParamIsShowAll();
        List<Integer> allStopSeriesIsImageSpec = seriesMapper.getAllStopSeriesIsImageSpec();
        List<SpecEntity> specAll = specMapper.getSpecAll();
        specAll.addAll(specMapper.getCvSpecAll());
        List<VideoOrderModel> videoOrderAll = seriesMapper.getVideoOrderBySeriesId(-1);

        Map<Integer, List<SeriesFuelTypeEntity>> seriesFuelTypeMap = seriesFuelTypes.stream().collect(Collectors.groupingBy(SeriesFuelTypeEntity::getSeriesId));
        Map<Integer, Integer> seriesHasBooksMap = seriesHasBooks.stream().collect(Collectors.toMap(SeriesHasBookSpecEntity::getSereisId, SeriesHasBookSpecEntity::getBooked));
        Map<Integer, List<VideoOrderModel>> videoOrderMap = videoOrderAll.stream().collect(Collectors.groupingBy(VideoOrderModel::getSeriesId));

        allSeries.forEach(series -> {
            List<Integer> fuelTypes = new ArrayList<>();
            if (seriesFuelTypeMap.containsKey(series.getId())) {
                fuelTypes = seriesFuelTypeMap.get(series.getId()).stream().map(fuelType -> Spec.getFueltypeDetailId(fuelType.getSubParamName())).collect(Collectors.toList());
            }
            update(ParamBuilder.create(seriesIdParamName, series.getId()).build(),
                    builder(
                            series,
                            fuelTypes,
                            seriesHasBooksMap.get(series.getId()),
                            seriesIdsParamIsShow,
                            allStopSeriesIsImageSpec,
                            specAll.stream().filter(p -> p.getSeriesId() == series.getId()).collect(Collectors.toList()),
                            videoOrderMap.get(series.getId()),
                            getSeriesOnTime(series.getId()))
            );
            log.accept(series.getId() + " success");
            ThreadUtil.sleep(20);
        });
    }

    public void refresh(int seriesId) {
        try {
            SeriesEntity entity = seriesMapper.getSeries(seriesId);
            SeriesHasBookSpecEntity seriesHasBookSpec = seriesMapper.getSeriesHasBookSpec(entity.getId());
            List<SeriesFuelTypeEntity> fuelTypes = seriesMapper.getSeriesFuelTypeDetail(entity.getId());
            List<Integer> seriesParamIsShow = seriesMapper.getSeriesParamIsShow(entity.getId());
            List<Integer> stopSeriesIsImageSpec = seriesMapper.getStopSeriesIsImageSpec(entity.getId());
            List<SpecEntity> specAll = Level.isCVLevel(entity.getLevelId()) ? specMapper.getCvSpecBySeriesId(entity.getId()) : specMapper.getSpecBySeriesId(entity.getId());
            List<VideoOrderModel> videoOrderAll = seriesMapper.getVideoOrderBySeriesId(seriesId);
            CmsNewCarResult seriesOnTime = getSeriesOnTime(seriesId);
            SeriesDetailDto series = builder(entity,
                    fuelTypes.stream().map(fuelType -> Spec.getFueltypeDetailId(fuelType.getSubParamName())).collect(Collectors.toList()),
                    seriesHasBookSpec != null ? seriesHasBookSpec.getBooked() : 0,
                    seriesParamIsShow,
                    stopSeriesIsImageSpec,
                    specAll,
                    videoOrderAll,
                    seriesOnTime);
            update(makeParam(seriesId), series);
        } catch (Exception e) {
            System.out.println("error:" + e.getMessage());
        }
    }

    private SeriesDetailDto builder(SeriesEntity entity, List<Integer> fuelTypes, Integer seriesHasBookSpec, List<Integer> seriesIdsParamIsShow, List<Integer> allStopSeriesIsImageSpec, List<SpecEntity> specAll, List<VideoOrderModel> videoOrderList,CmsNewCarResult seriesOnTime) {
        SeriesDetailDto dto = new SeriesDetailDto();
        dto.setId(entity.getId());
        dto.setName(StringEscapeUtils.unescapeHtml4(entity.getName()));
        dto.setLogo(CarSettings.getInstance().GetFullImagePath(entity.getImg()));

        dto.setPngLogo(CarSettings.getInstance().GetFullImagePath(entity.getNobgImg()));
        dto.setEnergytype(entity.getIsNewEnergy());
        dto.setRank(entity.getSeriesNewRank());
        dto.setBrandId(entity.getBrandId());
        dto.setBrandLogo(CarSettings.getInstance().GetFullImagePath(entity.getBrandLogo()));
        dto.setBrandName(entity.getBrandName());
        dto.setBrandCodeName(StringEscapeUtils.unescapeHtml4(entity.getBrandName()));
        dto.setFctId(entity.getManufactoryId());
        dto.setFctName(entity.getManufactoryName());
        dto.setFctCodeName(StringEscapeUtils.unescapeHtml4(entity.getManufactoryName()));
        dto.setLevelId(entity.getLevelId());
        dto.setLevelName(entity.getLevelName());
        dto.setPlace(entity.getPlace());
        dto.setContainBookedSpec(seriesHasBookSpec == null ? 0 : seriesHasBookSpec);
        dto.setFueltypes(fuelTypes.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")));

        //车系状态，视图表不及时，使用车型状态判断替换
        dto.setState(entity.getState());
        //状态优先级 ： 20>30>10>0>40
        //车系下所有车型范围,有一个车型是20 车系状态为20，如没有20状态车型，有任一状态为30的车型，车系状态为30，依此类推车系状态。
        List<Integer> statePriorities = Arrays.asList(20, 30, 10, 0, 40);
        statePriorities.stream().filter(state -> specAll.stream().anyMatch(spec -> spec.getState() == state)).findFirst().ifPresent(dto::setState);

        //有图片的车型
        List<SpecEntity> hasImgSpecAll = specAll.stream().filter(p -> p.getPicNumber() > 0).toList();
        dto.setDisplacementItems(getDisplacement(hasImgSpecAll, dto.getState()));
        dto.setSellSpecNum((int) hasImgSpecAll.stream().filter(p -> p.getState() >= 20 && p.getState() <= 30).count());
        dto.setWaitSpecNum((int) hasImgSpecAll.stream().filter(p -> p.getState() == 10).count());
        dto.setStopSpecNum((int) hasImgSpecAll.stream().filter(p -> p.getState() == 40).count());

        //车系价格，视图表不及时，使用车型价格判断替换
        dto.setMaxPrice(entity.getSeriesPriceMax());
        dto.setMinPrice(entity.getSeriesPriceMin());
        //车系最大和最小价格，依据车系当前状态取对应状态的车型价格区间计算最大和最小价格,
        //注意一：车系状态为20或30的，价格区间要涵盖状态为20和30的车型价格
        //注意二：车系状态为0 如果车系下有停售的车型，取停售车型价格区间来填充车系价格区间 （乘用车条件）
        List<SpecEntity> tempSpecList = specAll.stream().filter(spec -> {
            if (spec.getMinPrice() <= 0 || spec.getMaxPrice() <= 0) {
                return false;
            }
            if (dto.getState() == 20 || dto.getState() == 30) {
                return spec.getState() == 20 || spec.getState() == 30;
            } else if (dto.getState() == 0 && dto.getStopSpecNum() > 0 && !Level.isCVLevel(dto.getLevelId())) {
                return spec.getState() == 40;
            } else if (dto.getState() == 10) {
                return spec.getState() == 10;
            } else {
                return false;
            }
        }).toList();
        tempSpecList.stream().min(Comparator.comparing(SpecEntity::getMinPrice)).ifPresent(minSpec -> {
            dto.setMinPrice(minSpec.getMinPrice());
            dto.setMinPriceSpecId(minSpec.getId());
        });
        tempSpecList.stream().max(Comparator.comparing(SpecEntity::getMaxPrice)).ifPresent(spec -> dto.setMaxPrice(spec.getMaxPrice()));

        int paramIsShow = 0;
        if (dto.getState() <= 30) {
            paramIsShow = seriesIdsParamIsShow.contains(dto.getId()) ? 1 : 0;
        } else if (dto.getState() == 40) {
            paramIsShow = !allStopSeriesIsImageSpec.contains(dto.getId()) ? 1 : 0;
        }
        dto.setParamIsShow(paramIsShow);

        //判断是否是新车车系--车系状态是即将销售且所有车型都是即将销售或无车型，过滤掉图片车型
        if (dto.getState()==10) {
            List<SpecEntity> entityList = specAll.stream().filter(p -> p.getIsImageSpec() != null && p.getIsImageSpec() == 0).collect(Collectors.toList());
            dto.setNewCar(!entityList.stream().anyMatch(x -> x.getState() != 10));
        }

        //热门车型
        SeriesAttentionDto seriesAttentionDto = seriesAttentionComponent.get(entity.getId()).join();
        if (seriesAttentionDto != null && seriesAttentionDto.getSpecAttentions() != null && seriesAttentionDto.getSpecAttentions().size() > 0) {
            dto.setHotSpecId(seriesAttentionDto.getSpecAttentions().get(0).getSpecid());
            dto.setHotSpecName(seriesAttentionDto.getSpecAttentions().get(0).getSpecname());
        }
        //智能驾驶xx项功能
        if (videoOrderList != null && videoOrderList.size() > 0) {
            dto.setIntelligentDrivingNum(videoOrderList.get(0).getItemCount());
        }
        //车系上市时间
        if (seriesOnTime!=null) {
            dto.setOnLineTime(seriesOnTime.getOnDate());
            dto.setNewBrandTagId(NumberUtils.toInt(String.valueOf(seriesOnTime.getTagId())));
        }
        dto.setPriceDescription(entity.getPriceDescription());
        //标识直播：新车车系 或者 在售的车系页下的在售车型发布时间等于今天
        //https://doc.autohome.com.cn/docapi/page/share/share_uqbyNr9aFc
        if (dto.getIsNewCar()) {
            dto.setLiveStatus(1);
        } else if ((dto.getState() == 20 && specAll.stream().noneMatch(spec -> spec.getState() == 40))) {
            boolean liveStatus = specAll.stream().allMatch(spec -> spec.getTimeMarket() != null && DateUtil.format(DateUtil.parse(spec.getTimeMarket(), "yyyy-MM-dd"), "yyyy-MM-dd").equals(DateUtil.format(new Date(), "yyyy-MM-dd")));
            dto.setLiveStatus(liveStatus ? 1 : 0);
        }
        return dto;
    }

    private static List<String> getDisplacement(List<SpecEntity> allSpecs, int state) {
        List<SpecEntity> onSellSpecs = allSpecs.stream().filter(x -> state == 20 ? x.getState() >= 20 && x.getState() <= 30 : x.getState() == state).collect(Collectors.toList());

        return onSellSpecs.stream()
                .filter(x -> x != null && x.getDisplacement() != null && x.getFlowMode() > 0 && x.getDisplacement().compareTo(new BigDecimal(0)) > 0)
                .sorted(Comparator.comparing(SpecEntity::getDisplacement).thenComparing(SpecEntity::getFlowMode))
                .map(x -> {
                    String v = new DecimalFormat("#0.0").format(x.getDisplacement().setScale(1, BigDecimal.ROUND_HALF_UP));
                    if (x.getFlowMode() <= 1) {
                        return v.concat("L");
                    } else {
                        return v.concat("T");
                    }
                }).distinct().collect(Collectors.toList());
    }

    CmsNewCarResult getSeriesOnTime(int seriesId) {
        return cmsApiClient.getCmsNewCar(seriesId).thenApply(x -> {
            if (x != null && x.getResult() != null && x.getResult().size() > 0) {
                return x.getResult().get(0);
            }
            return null;
        }).exceptionally(e -> {
            return null;
        }).join();
    }
}
