package com.autohome.app.cars.service.components.newcar;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.car.CarApiClient;
import com.autohome.app.cars.apiclient.car.dtos.MonthRankDto;
import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.TopicContentResult;
import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.CmsArticleDataResult;
import com.autohome.app.cars.apiclient.cms.dtos.MarketCarResult;
import com.autohome.app.cars.apiclient.vr.PanoApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.popauto.CarPhotoViewMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SeriesTimeAxisMapper;
import com.autohome.app.cars.mapper.popauto.SpecParamMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecParamEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesAttentionComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesAttentionDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiTabComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKoubeiTabDto;
import com.autohome.app.cars.service.components.newcar.dtos.CarCalendarDto;
import com.autohome.app.cars.service.services.dtos.NewCarCalendarConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : zzli
 * @description : 新车日历-11.63.3版本的
 * https://doc.autohome.com.cn/docapi/page/share/share_w1Mtfghigy
 * @date : 2024/6/7 10:50
 */
@Component
@DBConfig(tableName = "series_calendar")
public class NewCarCalendarComponent extends BaseComponent<CarCalendarDto> {
    final static String seriesIdParamName = "seriesId";

    @Autowired
    SeriesTimeAxisMapper seriesTimeAxisMapper;
    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;
    @Autowired
    SeriesMapper seriesMapper;
    @Autowired
    CarApiClient carApiClient;
    @Autowired
    SpecParamMapper specParamMapper;
    @Autowired
    CmsApiClient cmsApiClient;
    @Autowired
    ClubApiClient clubApiClient;
    @Autowired
    PanoApiClient panoApiClient;
    @Autowired
    SeriesDetailComponent seriesDetailComponent;
    @Autowired
    private SeriesKouBeiTabComponent seriesKouBeiTabComponent;
    @Autowired
    private SeriesAttentionComponent seriesAttentionComponent;

    @Value("${newcar_calendar_config:}")
    private String NewCarCalendarConfigJson;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<CarCalendarDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    /**
     * @param xxlLog
     * @description 小改款、中期改款、车系换代、新增车款、全新车系,标签下的相关数据刷取
     * 当资讯标签和品库新车同时出现时，以资讯优先
     * 这三种车系
     * seriesDetailDto.getNewBrandTagId()>1
     * seriesDetailDto.getIsNewCar() || seriesDetailDto.getNewBrandTagId() == 1
     * 所有车型的发布时间距离当天小于90天
     * @author zzli
     */

    public void refreshAll(Consumer<String> xxlLog) {
        Date now = new Date();
        //新车日历相关配置
        NewCarCalendarConfig newCarCalendarConfig = JsonUtil.toObject(NewCarCalendarConfigJson, NewCarCalendarConfig.class);
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        List<SpecEntity> specAll = seriesTimeAxisMapper.getSpecAll();
        Map<Integer, List<SpecEntity>> specMap = specAll.stream().collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        //月销量
        BaseModel<List<MonthRankDto>> finalSeriesMonthRank = getSeriesMonthRank();
        //车系首发时间-发布时间
        Map<Integer, List<MarketCarResult.ItemsDTO>> marketCarMap = getMarketCar();
        for (SeriesEntity series : allSeries) {
            if (series == null) {
                xxlLog.accept("===========");
                continue;
            }
            SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(series.getId());
            if (seriesDetailDto == null) {
                xxlLog.accept("===========");
                continue;
            }

            //油车\纯电\非纯电
            String energyName = seriesDetailDto.getEnergytype() == 1 ? ("4".equals(seriesDetailDto.getFueltypes()) ? "纯电" : "非纯电") : "油车";
            CarCalendarDto carCalendarDto = new CarCalendarDto();
            //车系首发时间-发布时间
            if (marketCarMap != null && marketCarMap.containsKey(series.getId()) && !marketCarMap.get(series.getId()).isEmpty()) {
                getSeriesPublishDate(marketCarMap.get(series.getId()), carCalendarDto);
                xxlLog.accept("发布时间:" + JSONObject.toJSONString(carCalendarDto.getPublishdate()));
            }
            boolean newcar = seriesDetailDto.getIsNewCar() || seriesDetailDto.getNewBrandTagId() == 1;
            int afterdaynum = 0;
            if (seriesDetailDto.getNewBrandTagId() == 1) {//资讯的上市时间 90天以后不显示
                if (DateUtil.getDistanceOfTwoDate(seriesDetailDto.getOnLineTime(), now) > newCarCalendarConfig.getShow_limit()) {
                    xxlLog.accept("===========");
                    xxlLog.accept("上市时间：" + DateUtil.format(seriesDetailDto.getOnLineTime(), "yyyy-MM-dd"));
                    xxlLog.accept("资讯的新车，上市时间 90天以后不显示,Show_limit:" + newCarCalendarConfig.getShow_limit());
                    delete(makeParam(series.getId()));
                    continue;
                }
            } else if (seriesDetailDto.getNewBrandTagId() > 1) {            //带资讯标签的车系，判断显示的时间窗口
                //xxx天，配置优先级：车系>车系级别>能源类型
                NewCarCalendarConfig.EntranceDayNum entranceDayNum = newCarCalendarConfig.getNonnewcarentrance().stream().filter(x -> x.getType().contains(String.valueOf(series.getId()))).findFirst().orElse(null);
                if (entranceDayNum == null && StringUtils.isNotEmpty(carCalendarDto.getEventLevel())) {
                    entranceDayNum = newCarCalendarConfig.getNonnewcarentrance().stream().filter(x -> x.getType().contains(carCalendarDto.getEventLevel())).findFirst().orElse(null);
                }
                if (entranceDayNum == null) {
                    String finalEnergyName = energyName;
                    entranceDayNum = newCarCalendarConfig.getNonnewcarentrance().stream().filter(x -> x.getType().contains(finalEnergyName)).findFirst().orElse(null);
                }
                if (entranceDayNum == null) {
                    xxlLog.accept("===========");
                    delete(makeParam(series.getId()));
                    continue;
                }
                afterdaynum = entranceDayNum.getAfterdaynum();
                Date date = DateUtil.addDays(seriesDetailDto.getOnLineTime(), -entranceDayNum.getBeforedaynum());
                Date date1 = DateUtil.addDays(seriesDetailDto.getOnLineTime(), afterdaynum);
                xxlLog.accept("上市时间：" + DateUtil.format(seriesDetailDto.getOnLineTime(), "yyyy-MM-dd"));

                if (now.before(date) || now.after(date1)) {
                    xxlLog.accept("===========");
                    xxlLog.accept("非新车不在显示范围：" + series.getId() + ",上市时间：" + DateUtil.format(seriesDetailDto.getOnLineTime(), "yyyy-MM-dd"));
                    delete(makeParam(series.getId()));
                    continue;
                }
            }


            //车系打标签车型
            List<SpecEntity> specTagList = new ArrayList<>();
            //车系下所有车型
            List<SpecEntity> specEntityList = new ArrayList<>();
            if (specMap.containsKey(series.getId())) {
                specEntityList = specMap.get(series.getId());
                //最早发布的车型
                SpecEntity specEntity1 = specEntityList.stream().filter(x -> x.getTimeMarket() != null)
                        .sorted(Comparator.comparing(specEntity -> DateUtil.parse(specEntity.getTimeMarket(), "yyyy-MM-dd HH:mm:ss")))
                        .findFirst().orElse(null);
                if (specEntity1 != null) {
                    carCalendarDto.setSpecpublishtime(DateUtil.parse(specEntity1.getTimeMarket(), "yyyy-MM-dd HH:mm:ss"));
                }
                if (seriesDetailDto.getNewBrandTagId() > 1) {
                    //根据产品定义，获取标签下(小改款等)相关车型
                    //新增车型是即将销售或上市时间-当天<=8天
                    int finalAfterdaynum = afterdaynum;
                    specTagList = specEntityList.stream()
                            .filter(x -> x.getState() == 10 || (x.getTimeMarket() != null && DateUtil.getDistanceOfTwoDate(DateUtil.parse(x.getTimeMarket(), "yyyy-MM-dd"), now) <= finalAfterdaynum))
                            //.sorted(Comparator.comparing(specEntity -> DateUtil.parse(((SpecEntity) specEntity).getTimeMarket(), "yyyy-MM-dd HH:mm:ss")).reversed())
                            .collect(Collectors.toList());
                }
            }
            if (!newcar && seriesDetailDto.getNewBrandTagId() <= 0) {
                if (carCalendarDto.getSpecpublishtime() == null || DateUtil.getDistanceOfTwoDate(carCalendarDto.getSpecpublishtime(), now) > newCarCalendarConfig.getShow_limit()) {
                    xxlLog.accept("===========");
                    xxlLog.accept("非新、非标：" + series.getId());
                    delete(makeParam(series.getId()));
                    continue;
                }
            }
            xxlLog.accept("===========");
            if (seriesDetailDto.getNewBrandTagId() > 1) {
                carCalendarDto.setParam(getSeriesParam(series, specTagList, energyName, false, specEntityList, seriesDetailDto.getNewBrandTagId()));
                carCalendarDto.setPic(getSeriesPic(series, specTagList, false));
            } else {
                carCalendarDto.setParam(getSeriesParam(series, specEntityList.stream()
                        .filter(x -> x.getParamIsShow() == 1 && x.getIsImageSpec() == 0 && x.getDtime() != null && x.getState() != 40)
                        .sorted(Comparator.comparing(specEntity -> DateUtil.parse(((SpecEntity) specEntity).getDtime(), "yyyy-MM-dd HH:mm:ss")).reversed())
                        .collect(Collectors.toList()), energyName, true, specEntityList, 0));
                carCalendarDto.setPic(getSeriesPic(series, specTagList, true));
            }

            carCalendarDto.setPrice(getSeriesPriceTime(series));


            carCalendarDto.setCarsales(getSeriesSales(series.getId(), finalSeriesMonthRank));


            carCalendarDto.setCarfriendshare(getSeriesFriendShare(series, specTagList));

            CarCalendarDto.PriceDTO seriesEvaluating = getSeriesEvaluating(series);
            carCalendarDto.setEvaluating(seriesEvaluating);
            carCalendarDto.setCarpreview(getNewCarPreview(series));
            if (carCalendarDto.getCarfriendshare() != null) {
                xxlLog.accept("车友提车分享：" + series.getId());
            }
            if (carCalendarDto.getCarpreview() != null) {
                xxlLog.accept("新车预告：" + series.getId());
            }
            if (carCalendarDto.getEvaluating() != null) {
                xxlLog.accept("新车评测：" + series.getId());
            }
            carCalendarDto.setTestdrive(getSeriesTestDrive(series, seriesEvaluating));
            if (carCalendarDto.getTestdrive() != null) {
                xxlLog.accept("新车首试：" + series.getId());
            }
            if (carCalendarDto.checkAtLeastOneObjectNotNull()) {
                update(makeParam(series.getId()), carCalendarDto);

                xxlLog.accept(series.getId() + " " + series.getName() + " success," + (newcar ? (!seriesDetailDto.getIsNewCar() ? "资讯-新车" : "品库新车") : "非新车") + "," + (seriesDetailDto.getEnergytype() == 1 ? "新能源" : "非新能源车"));
            }
            ThreadUtil.sleep(50);
        }
    }

    public Map<Integer, List<MarketCarResult.ItemsDTO>> getMarketCar() {
        Date now = new Date();
        String lastMonthFormat = DateUtil.format(DateUtil.addDays(now, -60), "yyyy-MM-dd");
        String beforeMonthFormat = DateUtil.format(DateUtil.addDays(now, 60), "yyyy-MM-dd");

        System.out.println(lastMonthFormat + "  " + beforeMonthFormat);
        Map<Integer, List<MarketCarResult.ItemsDTO>> marketCarMap = null;
        BaseModel<MarketCarResult> marketCarResult = cmsApiClient.getMarketCarList(0, lastMonthFormat, beforeMonthFormat).join();
        if (marketCarResult != null && marketCarResult.getResult() != null && marketCarResult.getResult().getItems() != null) {
            marketCarMap = marketCarResult.getResult().getItems().stream().collect(Collectors.groupingBy(MarketCarResult.ItemsDTO::getSeriesId));
        }
        return marketCarMap;
    }

    BaseModel<List<MonthRankDto>> getSeriesMonthRank() {
        //月销量：上一月
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String lastMonthFormat = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        BaseModel<List<MonthRankDto>> seriesMonthRank = carApiClient.getSeriesMonthRank(lastMonthFormat).join();
        if (seriesMonthRank == null || seriesMonthRank.getResult() == null || seriesMonthRank.getResult().size() == 0) {
            //上上一月
            YearMonth lastLastMonth = YearMonth.now().minusMonths(2);
            String lastLastMonthFormat = lastLastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            seriesMonthRank = carApiClient.getSeriesMonthRank(lastLastMonthFormat).join();
        }
        return seriesMonthRank;
    }

    CarCalendarDto.PicDTO getSeriesPic(SeriesEntity series, List<SpecEntity> specTagList, boolean newcar) {
        List<CarPhotoViewEntity> carPhotoViewEntityList = carPhotoViewMapper.getPhotoViewBySeriesId(series.getId(),"");
        if (CollectionUtils.isEmpty(carPhotoViewEntityList)) {
            return null;
        }
        carPhotoViewEntityList = carPhotoViewEntityList.stream().sorted(Comparator.comparing(CarPhotoViewEntity::getClassOrder)
                .thenComparing(CarPhotoViewEntity::getShowId, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewEntity::getSourceTypeOrder)
                .thenComparing(CarPhotoViewEntity::getDealerPicOrder)
                .thenComparing(CarPhotoViewEntity::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                .thenComparing(CarPhotoViewEntity::getPicId, Comparator.reverseOrder())
        ).collect(Collectors.toList());
        BaseModel<SeriesVrExteriorResult> vr = panoApiClient.getSeriesExterior(series.getId()).join();

        CarCalendarDto.PicDTO picDTO = new CarCalendarDto.PicDTO(101, "图片发布", new CarCalendarDto.PicDTO.CarddataDTO());
        picDTO.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s", series.getId(), UrlUtil.encode(series.getName())));

        if (!newcar) {
            if (specTagList.isEmpty()) {
                return null;
            }
            List<Integer> idList = specTagList.stream()
                    .map(SpecEntity::getId)
                    .collect(Collectors.toList());
            carPhotoViewEntityList = carPhotoViewEntityList.stream().filter(x -> idList.contains(x.getSpecId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(carPhotoViewEntityList)) {
                return null;
            }
            picDTO.setDate(carPhotoViewEntityList.stream()
                    .sorted(Comparator.comparing(CarPhotoViewEntity::getPicUploadTime).reversed()).findFirst().get().getPicUploadTime());
            Map<Integer, List<CarPhotoViewEntity>> specPicMap = carPhotoViewEntityList.stream().collect(Collectors.groupingBy(CarPhotoViewEntity::getSpecId));
            specTagList.forEach(spec -> {
                if (specPicMap.containsKey(spec.getId())) {
                    List<CarPhotoViewEntity> picList = specPicMap.get(spec.getId());
                    if (picList.size() > 2) {
                        CarCalendarDto.PicDTO.CarddataDTO.SpecPic specPic = new CarCalendarDto.PicDTO.CarddataDTO.SpecPic();
                        specPic.setSpecname(spec.getName());
                        //取外观图片
                        Map<Integer, List<CarPhotoViewEntity>> picColorMap = picList.stream().filter(x -> x.getPicClass() == 1 && x.getPicColorId() != 0).collect(Collectors.groupingBy(CarPhotoViewEntity::getPicColorId));
                        if (picColorMap.size() <= 1) {
                            picList.stream().limit(4).forEach(x -> {
                                CarCalendarDto.PicDTO.CarddataDTO.SpecPicItemDto picitemsDTO = new CarCalendarDto.PicDTO.CarddataDTO.SpecPicItemDto();
                                picitemsDTO.setImage(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                                picitemsDTO.setLinkurl(String.format("autohome://car/specpicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s&specid=%s", series.getId(), UrlUtil.encode(series.getName()), x.getPicClass(), spec.getId()));
                                specPic.getList().add(picitemsDTO);
                            });
                        } else {
                            for (Map.Entry<Integer, List<CarPhotoViewEntity>> entry : picColorMap.entrySet()) {
                                CarCalendarDto.PicDTO.CarddataDTO.SpecPicItemDto picitemsDTO = new CarCalendarDto.PicDTO.CarddataDTO.SpecPicItemDto();
                                List<CarPhotoViewEntity> value = entry.getValue();
                                picitemsDTO.setColortext(value.get(0).getColorname());
                                picitemsDTO.setImage(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(value.get(0).getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                                String url = String.format("autohome://car/specpicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=1&colorid=%s&specid=%s", series.getId(), UrlUtil.encode(series.getName()), value.get(0).getPicColorId(), spec.getId());
                                //vr外观颜色
                                if (vr != null && vr.getResult() != null && vr.getResult().getColor_list() != null && vr.getResult().getColor_list().size() > 0) {
                                    Optional<SeriesVrExteriorResult.Color_List> first1 = vr.getResult().getColor_list().stream().filter(x -> x.getRemoteColorId() == entry.getKey()).findFirst();
                                    if (first1.isPresent()) {
                                        if (first1.get().getColorValues() != null && !"".equals(first1.get().getColorValues())
                                                && first1.get().getColorValues().length() > 0) {
                                            url = url + "&vrecolor=" + UrlUtil.encode(first1.get().getColorValues());
                                        } else {
                                            url = url + "&vrecolor=" + first1.get().getColorValue();
                                        }
                                    }
                                }
                                picitemsDTO.setLinkurl(url);
                                specPic.getList().add(picitemsDTO);
                            }
                        }
                        picDTO.getCarddata().getSpecpicList().add(specPic);
                    }
                }
            });
            if (picDTO.getCarddata().getSpecpicList().size() == 0) {
                return null;
            }
        } else {
            carPhotoViewEntityList.stream().limit(4).forEach(x -> {
                CarCalendarDto.PicDTO.CarddataDTO.picitemsDTO picitemsDTO = new CarCalendarDto.PicDTO.CarddataDTO.picitemsDTO();
                picitemsDTO.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                picitemsDTO.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s", series.getId(), UrlUtil.encode(series.getName()), x.getPicClass()));
                picDTO.getCarddata().getPicitems().add(picitemsDTO);
            });
            picDTO.setDate(carPhotoViewEntityList.stream()
                    .sorted(Comparator.comparing(CarPhotoViewEntity::getPicUploadTime).reversed()).findFirst().get().getPicUploadTime());
            //外观下是否有颜色
            List<CarPhotoViewEntity> outPicList = carPhotoViewEntityList.stream().filter(x -> x.getPicClass() == 1 && x.getPicColorId() != 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(outPicList)) {

                Optional<CarPhotoViewEntity> first = outPicList.stream()
                        .sorted(Comparator.comparing(CarPhotoViewEntity::getPicUploadTime).reversed()).findFirst();

                picDTO.setDate(first.get().getPicUploadTime());

                Map<Integer, List<CarPhotoViewEntity>> outPicMap = outPicList.stream().collect(Collectors.groupingBy(CarPhotoViewEntity::getPicColorId));

                for (Map.Entry<Integer, List<CarPhotoViewEntity>> carPhotoViewMap : outPicMap.entrySet()) {
                    //如果只有一种，将一种颜色的外观图放出四张
                    if (outPicMap.size() == 1) {
                        picDTO.getCarddata().getPicitems().clear();
                        carPhotoViewMap.getValue().stream().limit(4).forEach(x -> {
                            CarCalendarDto.PicDTO.CarddataDTO.picitemsDTO picitemsDTO = new CarCalendarDto.PicDTO.CarddataDTO.picitemsDTO();
                            picitemsDTO.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                            picitemsDTO.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s", series.getId(), UrlUtil.encode(series.getName()), x.getPicClass()));
                            picDTO.getCarddata().getPicitems().add(picitemsDTO);
                        });
                    } else {
                        CarCalendarDto.PicDTO.CarddataDTO.OutcoloritemsDTO item = new CarCalendarDto.PicDTO.CarddataDTO.OutcoloritemsDTO();
                        item.setColorname(carPhotoViewMap.getValue().get(0).getColorname());
                        item.setColorpic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(carPhotoViewMap.getValue().get(0).getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                        String url = String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=1&colorid=%s", series.getId(), UrlUtil.encode(series.getName()), carPhotoViewMap.getValue().get(0).getPicColorId());
                        //vr外观颜色
                        if (vr != null && vr.getResult() != null && vr.getResult().getColor_list() != null && vr.getResult().getColor_list().size() > 0) {
                            Optional<SeriesVrExteriorResult.Color_List> first1 = vr.getResult().getColor_list().stream().filter(x -> x.getRemoteColorId() == carPhotoViewMap.getKey()).findFirst();
                            if (first1.isPresent()) {
                                if (first1.get().getColorValues() != null && !"".equals(first1.get().getColorValues())
                                        && first1.get().getColorValues().length() > 0) {
                                    url = url + "&vrecolor=" + UrlUtil.encode(first1.get().getColorValues());
                                } else {
                                    url = url + "&vrecolor=" + first1.get().getColorValue();
                                }
                            }
                        }
                        item.setUrl(url);
                        picDTO.getCarddata().getOutcoloritems().add(item);
                    }
                }
            }
        }

        picDTO.getCarddata().setPiccount(carPhotoViewEntityList.size());
        return picDTO;
    }


    CarCalendarDto.ParamDTO getSeriesParam(SeriesEntity series, List<SpecEntity> specEntityList, String energyName, boolean newcar, List<SpecEntity> specAll, int tagId) {
        SeriesAttentionDto seriesAttentionDto = null;
        if (newcar == false) {
            specEntityList = specEntityList.stream()
                    .filter(x -> x.getParamIsShow() == 1 && x.getIsImageSpec() == 0 && x.getDtime() != null)
                    .sorted(Comparator.comparing(specEntity -> DateUtil.parse(((SpecEntity) specEntity).getDtime(), "yyyy-MM-dd HH:mm:ss")).reversed())
                    .collect(Collectors.toList());
            seriesAttentionDto = seriesAttentionComponent.get(series.getId()).join();
        }
        if (CollectionUtils.isEmpty(specEntityList)) {
            return null;
        }
        CarCalendarDto.ParamDTO paramDTO = new CarCalendarDto.ParamDTO(102,
                DateUtil.parse(specEntityList.get(0).getDtime(), "yyyy-MM-dd HH:mm:ss"),
                String.format("autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s", series.getId(), UrlUtil.encode(series.getName())),
                "参配发布",
                new CarCalendarDto.ParamDTO.CarddataDTO());
        paramDTO.getCarddata().setSpeccount(specEntityList.size());

        List<SpecParamEntity> specPartParam1BySeriesId = specParamMapper.getSpecPartParam1BySeriesId(series.getId(), series.getLevelId());
        Map<Integer, List<SpecParamEntity>> specParamMap = specPartParam1BySeriesId.stream().collect(Collectors.groupingBy(SpecParamEntity::getSpecId));
        SeriesAttentionDto finalSeriesAttentionDto = seriesAttentionDto;
        List<Integer> specIds = specEntityList.stream().map(SpecEntity::getId).collect(Collectors.toList());
        specEntityList.forEach(x -> {
            CarCalendarDto.ParamDTO.CarddataDTO.SpecconfigitemsDTO specconfigitemsDTO = new CarCalendarDto.ParamDTO.CarddataDTO.SpecconfigitemsDTO();
            specconfigitemsDTO.setSpecid(x.getId());
            specconfigitemsDTO.setSpecname(x.getName());
            specconfigitemsDTO.setUrl(String.format("autohome://car/specconfig?seriesid=%s&specid=%s&specname=%s", series.getId(), x.getId(), UrlUtil.encode(x.getName())));

            List<SpecParamEntity> paramEntities = specParamMap.get(x.getId());
            specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(1, paramEntities));
            if ("纯电".equals(energyName)) {
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(50, paramEntities));
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(17, paramEntities));
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(10000, paramEntities));
            } else if ("非纯电".equals(energyName)) {
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(102, paramEntities));
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(50, paramEntities));
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(17, paramEntities));
            } else {
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(102, paramEntities));
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(49, paramEntities));
                specconfigitemsDTO.getConfiglist().add(getSpecConfigItem(17, paramEntities));
            }

            //新老款差异对比（仅非全新车系）
            if (!newcar && specAll.size() > 2) {
                //对比车型
                SpecEntity specEntity1 = specAll.stream().filter(spec -> spec.getYearName() != x.getYearName()
                                && spec.getName().contains(x.getName().replace(x.getYearName() + "款", "")))
                        .findFirst().orElse(null);
                if (specEntity1 == null) {
                    if (finalSeriesAttentionDto != null
                            && !org.springframework.util.CollectionUtils.isEmpty(finalSeriesAttentionDto.getSpecAttentions())) {
                        Stream<SeriesAttentionDto.SpecAttention> specAttentionStream = finalSeriesAttentionDto.getSpecAttentions().stream()
                                .filter(attention -> !specIds.contains(attention.getSpecid()) && attention.getParamIsShow() == 1);
                        //除了新增车型标签，其余的标签取之前年代款的关注度
                        if (tagId != 4) {
                            specAttentionStream = specAttentionStream.filter(attention -> attention.getYearName() < x.getYearName());
                        }
                        SeriesAttentionDto.SpecAttention specAttention = specAttentionStream.findFirst().orElse(null);
                        if (specAttention != null) {
                            specEntity1 = new SpecEntity();
                            specEntity1.setId(specAttention.getSpecid());
                            specEntity1.setName(specAttention.getSpecname());
                        }
                    }
                }
                if (Objects.nonNull(specEntity1)) {
                    String url = "autohome://carcompare/comprehensivecontrast?specids=" + x.getId() + "," + specEntity1.getId() + "&defaultindex=13&fromtype=24";
                    specconfigitemsDTO.setSummaryconfigdif(url);
                }
            }
            paramDTO.getCarddata().getSpecconfigitems().add(specconfigitemsDTO);
        });

        return paramDTO;
    }

    CarCalendarDto.ParamDTO.CarddataDTO.SpecconfigitemsDTO.ConfiglistDTO getSpecConfigItem(int paramId, List<SpecParamEntity> paramEntities) {
        CarCalendarDto.ParamDTO.CarddataDTO.SpecconfigitemsDTO.ConfiglistDTO item = new CarCalendarDto.ParamDTO.CarddataDTO.SpecconfigitemsDTO.ConfiglistDTO();
        item.setParamvalue("-");
        switch (paramId) {
            case 1:
                item.setParamname("最高车速(km/h)");
                break;
            case 50:
                item.setParamname("最大功率(kW)");
                break;
            case 17:
                item.setParamname("轴距(mm)");
                break;
            case 102:
                item.setParamname("油耗(L/100km)");
                break;
            case 49:
                item.setParamname("最大马力(Ps)");
                break;
            case 10000:
                item.setParamname("座位数");
                break;
        }
        if (CollectionUtils.isNotEmpty(paramEntities)) {
            if (paramId == 10000) {
                item.setParamvalue(paramEntities.get(0).getSeats());
            } else {
                Optional<SpecParamEntity> first = paramEntities.stream().filter(x -> x.getParamId() == paramId && !"-".equals(x.getParamValue())).findFirst();
                if (first.isPresent()) {
                    item.setParamvalue(first.get().getParamValue());
                }
            }
        }
        return item;
    }

    CarCalendarDto.PriceDTO getSeriesPriceTime(SeriesEntity series) {
        return cmsApiClient.getCmsNewCar(series.getId()).thenCombineAsync(cmsApiClient.getCmsArticleData(series.getId(), 300001), (result1, cmsArticle) -> {
            if (result1 != null && result1.getResult() != null && result1.getResult().size() > 0) {
                Date onDate = result1.getResult().get(0).getOnDate();
                CarCalendarDto.PriceDTO priceDTO = new CarCalendarDto.PriceDTO(103, onDate, "价格公布");
                if (cmsArticle != null && cmsArticle.getResult() != null && cmsArticle.getResult().getItems() != null && cmsArticle.getResult().getItems().size() > 0) {
                    CarCalendarDto.PriceDTO.CarddataDTO carddataDTO = new CarCalendarDto.PriceDTO.CarddataDTO();
                    CmsArticleDataResult.ItemsDTO itemsDTO = cmsArticle.getResult().getItems().get(0);
                    priceDTO.setUrl("autohome://article/articledetail?newsid=" + itemsDTO.getBizId() + "&newstype=0");
                    if (compareDate(onDate,itemsDTO.getMainDataAll().getPublish_time())){
                        carddataDTO.setTitle(StringUtils.isNotEmpty(itemsDTO.getMainDataAll().getSmall_title()) ? itemsDTO.getMainDataAll().getSmall_title() : itemsDTO.getMainDataAll().getTitle());
                        carddataDTO.setImg(itemsDTO.getMainDataAll().getImg_url());
                        priceDTO.setCarddata(carddataDTO);
                    }
                }
                return priceDTO;
            }
            return null;
        }).exceptionally(e -> null).join();
    }

    /**
     * 对比新车上市时间和资讯文章发布时间是否一致
     * @param onDate
     * @param articlePubTime
     * @return
     */
    private boolean compareDate(Date onDate, String articlePubTime) {
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDate time = LocalDate.parse(articlePubTime, formatter);
        LocalDate localDate = onDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return time.equals(localDate);
    }

    void getSeriesPublishDate(List<MarketCarResult.ItemsDTO> marketCarList, CarCalendarDto carCalendarDto) {
        if (marketCarList != null && marketCarList.size() > 0) {
            MarketCarResult.ItemsDTO itemsDTO = marketCarList.stream().filter(x -> x.getMarketType() == 2).findFirst().orElse(null);
            if (itemsDTO != null) {
                carCalendarDto.setPublishdate(new CarCalendarDto.PriceDTO(0, DateUtil.parse(itemsDTO.getActiveBeginTime(), "yyyy-MM-dd HH:mm:ss"), "发布时间"));
            }
            String eventLevelName = "";
            switch (marketCarList.get(0).getEventLevel()) {
                case 0:
                    eventLevelName = "未评级";
                    break;
                case 1:
                    eventLevelName = "S";
                    break;
                case 2:
                    eventLevelName = "A";
                    break;
                case 3:
                    eventLevelName = "B";
                    break;
                case 4:
                    eventLevelName = "C";
                    break;
            }
            carCalendarDto.setEventLevel(eventLevelName);
        }
    }

    CarCalendarDto.CarsalesDTO getSeriesSales(int seriesId, BaseModel<List<MonthRankDto>> seriesMonthRank) {
        if (seriesMonthRank != null && seriesMonthRank.getResult() != null && seriesMonthRank.getResult().size() > 0) {
            MonthRankDto monthRankDto = seriesMonthRank.getResult().stream().filter(x -> String.valueOf(seriesId).equals(x.getSeriesid())).findFirst().orElse(null);
            if (monthRankDto != null) {
                Date parse = DateUtil.parse(monthRankDto.getMonth() + "-10", "yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parse);
                calendar.add(Calendar.MONTH, 1); // 添加一个月

                int month = parse.getMonth();

                CarCalendarDto.CarsalesDTO carsalesDTO = new CarCalendarDto.CarsalesDTO(107, calendar.getTime(), "月销量公布", new CarCalendarDto.CarsalesDTO.CarddataDTO());
                carsalesDTO.getCarddata().setCurrentmonth((month + 1) + "月");
                carsalesDTO.getCarddata().setCurrentmonthsales(monthRankDto.getSalecnt());
                if (monthRankDto.getRnnum() != null && monthRankDto.getRnnum() <= 100) {
                    carsalesDTO.getCarddata().setRn("销量排名第" + monthRankDto.getRnnum() + "名");
                    carsalesDTO.setUrl("autohome://car/recmainrank?from=1&typeid=1&subranktypeid=1&rank=" + monthRankDto.getRnnum());
                }

                carsalesDTO.getCarddata().setLastmonth(month + "月");
                carsalesDTO.getCarddata().setLastmonthsales(monthRankDto.getPre_salecnt());
                return carsalesDTO;
            }
        }

        return null;
    }

    CarCalendarDto.CarfriendshareDTO getSeriesFriendShare(SeriesEntity series, List<SpecEntity> specTagList) {
        if (!specTagList.isEmpty()) {
            return seriesKouBeiTabComponent.get(series.getId()).thenApply(seriesKouBeiTabDto -> {
                if (seriesKouBeiTabDto != null && !seriesKouBeiTabDto.getEvaluations().isEmpty()) {
                    Map<Integer, List<SeriesKoubeiTabDto.Evaluation>> collect = seriesKouBeiTabDto.getEvaluations().stream().filter(x -> x.getKoubeitype() == 1 && StringUtils.isNotEmpty(x.getFeeling_summary()) && StringUtils.isNotEmpty(x.getPosttime())).collect(Collectors.groupingBy(SeriesKoubeiTabDto.Evaluation::getSpecid));
                    for (SpecEntity spec : specTagList) {
                        if (collect.containsKey(spec.getId())) {
                            SeriesKoubeiTabDto.Evaluation evaluations = collect.get(spec.getId()).get(0);
                            CarCalendarDto.CarfriendshareDTO carfriendshareDTO = new CarCalendarDto.CarfriendshareDTO(801, DateUtil.parse(evaluations.getPosttime(), "yyyy-MM-dd"), evaluations.getLinkurl(), "车友提车分享", new CarCalendarDto.CarfriendshareDTO.CarddataDTO());
                            carfriendshareDTO.getCarddata().setTitle(evaluations.getFeeling_summary());
                            if (!evaluations.getPiclist().isEmpty()) {
                                carfriendshareDTO.getCarddata().setImg(evaluations.getPiclist().get(0));
                            }
                            return carfriendshareDTO;
                        }
                    }
                }
                return null;
            }).exceptionally(e -> null).join();
        } else {
            return clubApiClient.GetTopicListByContentType(series.getId(), 1076,1,1).thenApply(clubTopicResult -> {
                if (clubTopicResult != null && clubTopicResult.getResult() != null && clubTopicResult.getResult().getList() != null && clubTopicResult.getResult().getList().size() > 0) {
                    TopicContentResult.ListDTO listDTO = clubTopicResult.getResult().getList().get(0);
                    CarCalendarDto.CarfriendshareDTO carfriendshareDTO = new CarCalendarDto.CarfriendshareDTO(801, DateUtil.parse(listDTO.getPostdate(), "yyyy-MM-dd HH:mm:ss"), String.format("autohome://club/topicdetail?topicid=%d&bbsid=%d&from=10", listDTO.getTopicid(), series.getId()), "车友提车分享", new CarCalendarDto.CarfriendshareDTO.CarddataDTO());
                    carfriendshareDTO.getCarddata().setTitle(listDTO.getTitle());
                    if (StringUtils.isNotEmpty(listDTO.getImgs())) {
                        String[] split = listDTO.getImgs().split(",");
                        if (split != null && split.length > 0) {
                            carfriendshareDTO.getCarddata().setImg(ImageUtils.convertImageUrl(split[0], true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                        }
                    }
                    if (listDTO.getVideoinfo() != null) {
                        carfriendshareDTO.getCarddata().setIsvideo(true);
                        carfriendshareDTO.getCarddata().setDuration(listDTO.getVideoinfo().getDuration());
                        carfriendshareDTO.getCarddata().setImg(ImageUtils.convertImageUrl(listDTO.getVideoinfo().getVideoimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                    }
                    return carfriendshareDTO;
                }
                return null;
            }).exceptionally(e -> null).join();
        }
    }

    /**
     * 首测公布(新车评测)
     */
    CarCalendarDto.PriceDTO getSeriesEvaluating(SeriesEntity series) {
        return cmsApiClient.getCmsArticleData(series.getId(), 100003).thenApply(x -> {
            if (x != null && x.getResult() != null && x.getResult().getItems() != null && x.getResult().getItems().size() > 0) {
                CmsArticleDataResult.ItemsDTO itemsDTO = x.getResult().getItems().get(0);
                CarCalendarDto.PriceDTO priceDTO = new CarCalendarDto.PriceDTO(802, DateUtil.parse(itemsDTO.getMainDataAll().getPublish_time(), "yyyy/MM/dd HH:mm:ss"), "新车评测");
                CarCalendarDto.PriceDTO.CarddataDTO carddataDTO = new CarCalendarDto.PriceDTO.CarddataDTO();
                carddataDTO.setTitle(StringUtils.isNotEmpty(itemsDTO.getMainDataAll().getVideo_app_title()) ? itemsDTO.getMainDataAll().getVideo_app_title() : itemsDTO.getMainDataAll().getTitle());
                carddataDTO.setImg(itemsDTO.getMainDataAll().getImg_url_4x3());
                carddataDTO.setDuration(NumberUtils.toInt(String.valueOf(itemsDTO.getMainDataAll().getDuration()), 0));
                carddataDTO.setIsvideo(true);
                priceDTO.setUrl(itemsDTO.getMainDataAll().getApp_url());
                priceDTO.setCarddata(carddataDTO);
                return priceDTO;
            }
            return null;
        }).exceptionally(e -> null).join();
    }

    /**
     * 新车首试
     */
    CarCalendarDto.PriceDTO getSeriesTestDrive(SeriesEntity series, CarCalendarDto.PriceDTO evaluating) {
        return cmsApiClient.getCmsArticleData(series.getId(), 100002).thenApply(x -> {
            if (x != null && x.getResult() != null && x.getResult().getItems() != null && x.getResult().getItems().size() > 0) {
                CmsArticleDataResult.ItemsDTO itemsDTO = x.getResult().getItems().get(0);
                if (evaluating != null && itemsDTO.getMainDataAll().getApp_url().equals(evaluating.getUrl())) {
                    return null;
                }
                CarCalendarDto.PriceDTO priceDTO = new CarCalendarDto.PriceDTO(803, DateUtil.parse(itemsDTO.getMainDataAll().getPublish_time(), "yyyy/MM/dd HH:mm:ss"), "新车首试");
                CarCalendarDto.PriceDTO.CarddataDTO carddataDTO = new CarCalendarDto.PriceDTO.CarddataDTO();
                carddataDTO.setTitle(StringUtils.isNotEmpty(itemsDTO.getMainDataAll().getVideo_app_title()) ? itemsDTO.getMainDataAll().getVideo_app_title() : itemsDTO.getMainDataAll().getTitle());
                carddataDTO.setImg(itemsDTO.getMainDataAll().getImg_url_4x3());
                carddataDTO.setDuration(NumberUtils.toInt(String.valueOf(itemsDTO.getMainDataAll().getDuration()), 0));
                carddataDTO.setIsvideo(true);
                priceDTO.setUrl(itemsDTO.getMainDataAll().getApp_url());
                priceDTO.setCarddata(carddataDTO);
                return priceDTO;
            }
            return null;
        }).exceptionally(e -> null).join();
    }

    /**
     * 新车预告 804
     */
    CarCalendarDto.PriceDTO getNewCarPreview(SeriesEntity series) {
        return cmsApiClient.getCmsArticleData(series.getId(), 300003).thenApply(cmsArticle -> {
            if (cmsArticle != null && cmsArticle.getResult() != null && cmsArticle.getResult().getItems() != null && cmsArticle.getResult().getItems().size() > 0) {
                CmsArticleDataResult.ItemsDTO itemsDTO = cmsArticle.getResult().getItems().get(0);
                CarCalendarDto.PriceDTO priceDTO = new CarCalendarDto.PriceDTO(804, DateUtil.parse(itemsDTO.getMainDataAll().getPublish_time(), "yyyy/MM/dd HH:mm:ss"), "新车预告");
                CarCalendarDto.PriceDTO.CarddataDTO carddataDTO = new CarCalendarDto.PriceDTO.CarddataDTO();
                carddataDTO.setTitle(StringUtils.isNotEmpty(itemsDTO.getMainDataAll().getSmall_title()) ? itemsDTO.getMainDataAll().getSmall_title() : itemsDTO.getMainDataAll().getTitle());
                carddataDTO.setImg(itemsDTO.getMainDataAll().getImg_url());
                priceDTO.setUrl("autohome://article/articledetail?newsid=" + itemsDTO.getBizId() + "&newstype=0");
                priceDTO.setCarddata(carddataDTO);
                return priceDTO;
            }
            return null;
        }).exceptionally(e -> null).join();
    }
}
