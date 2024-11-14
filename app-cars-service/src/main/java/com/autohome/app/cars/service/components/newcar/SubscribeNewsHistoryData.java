package com.autohome.app.cars.service.components.newcar;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.TopicContentResult;
import com.autohome.app.cars.apiclient.user.UserApiClient;
import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;
import com.autohome.app.cars.apiclient.user.dtos.UserInfoResult;
import com.autohome.app.cars.apiclient.vr.PanoApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.SeriesSubscribeNewsEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.RankSaleWeekMapper;
import com.autohome.app.cars.mapper.appcars.SeriesSubscribeNewsMapper;
import com.autohome.app.cars.mapper.appcars.SpecCityPriceHistoryMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.SpecCityPriceHistoryEntity;
import com.autohome.app.cars.mapper.popauto.CarPhotoViewMapper;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarPhotoViewEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.dtos.SpecCityPriceHistoryDto;
import com.autohome.app.cars.service.components.newcar.dtos.SeriesSubscribeNewsDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthSourceComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleWeekSourceComponent;
import com.autohome.app.cars.service.services.dtos.SubscribeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 动态频道历史数据
 * @date : 2024/9/9 15:39
 */
@Component
@Slf4j
@DBConfig(tableName = "series_subscribe_news")
public class SubscribeNewsHistoryData extends BaseComponent<SeriesSubscribeNewsDto> {
    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private RankSaleMonthSourceComponent rankSaleMonthSourceComponent;

    @Autowired
    private RankSaleWeekSourceComponent rankSaleWeekSourceComponent;
    @Autowired
    CarPhotoViewMapper carPhotoViewMapper;
    @Resource
    PanoApiClient panoApiClient;


    @Resource
    ClubApiClient clubApiClient;


    @Resource
    UserApiClient userApiClient;

    @Autowired
    RankSaleWeekMapper rankSaleWeekMapper;


    @Autowired
    private SpecCityPriceHistoryMapper specCityPriceHistoryMapper;

    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    @Autowired
    private SeriesSubscribeNewsMapper seriesSubscribeNewsMapper;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.SubscribeConfig).createFromJson('${series_subscribe_config:}')}")
    private SubscribeConfig seriesSubscribeConfig;

    private final String seriesIdParamName = "series_id";
    private final String specIdParamName = "spec_id";
    private final String cityIdParamName = "city_id";
    private final String typeIdParamName = "biz_type";
    private final String isShowParamName = "is_show";
    private final String displayTimeParamName = "display_time";
    @Autowired
    private SpecMapper specMapper;


    public TreeMap<String, Object> makeParam(int seriesId, int specId, int cityId, int isShow, int typeId, Timestamp displayTime) {
        return ParamBuilder.create(seriesIdParamName, seriesId)
                .add(typeIdParamName, typeId)
                .add(specIdParamName, specId)
                .add(cityIdParamName, cityId)
                .add(isShowParamName, isShow)
                .add(displayTimeParamName, displayTime)
                .build();
    }

    public void refreshAll(Consumer<String> xxlLog) {
        // 180天内
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -180);
        Date oneEightyDaysAgo = calendar.getTime();
        try {
            int i = seriesSubscribeNewsMapper.cleanPicAndCarWork();
        } catch (Exception e) {
            log.warn("");
        }
        List<SeriesEntity> seriesList = seriesMapper.getAllSeries();
        seriesList.sort((s1, s2) -> Integer.compare(s2.getId(), s1.getId()));

        seriesList.stream().filter(x -> Arrays.asList(10, 20, 30).contains(x.getState())).forEach(seriesDto -> {
            try {
                //刷新车图
                refreshSeriesPic(seriesDto, oneEightyDaysAgo);

                //提车作业
                refreshSeriesFriendShare(seriesDto, oneEightyDaysAgo);

            } catch (Exception e) {
                log.error("动态频道历史数据error", e);
            }
            xxlLog.accept(seriesDto.getId() + " success");
        });
    }

    /**
     * 周
     */
    public void refreshWeekRank(Consumer<String> xxlLog) {
        List<String> weekList = rankSaleWeekMapper.getWeekList("2024-04-01");
        List<Map<String, RankSaleWeekSourceEntity>> data = new ArrayList<>();

        weekList.forEach(x -> {
            data.add(getRankWeekMap(x));
        });
        List<SeriesEntity> seriesList = seriesMapper.getAllSeries();
        seriesList.sort((s1, s2) -> Integer.compare(s2.getId(), s1.getId()));

        seriesList.stream().filter(x -> Arrays.asList(10, 20, 30).contains(x.getState())).forEach(seriesDto -> {
            try {
                for (int i = 0; i < data.size(); i++) {
                    if (i < data.size() - 2) {
                        refreshWeekRankList(seriesDto, data.get(i), data.get(i + 1));
                    }
                }
            } catch (Exception e) {
                log.error("刷新错误", e);
            }
            xxlLog.accept(seriesDto.getId() + " success");
        });
    }

    /**
     * 月销量
     */
    public void refreshMonthRank(Consumer<String> xxlLog) {
        Map<String, RankSaleMonthSourceEntity> monthRank8 = getRankMonthMap("2024-08");
        Map<String, RankSaleMonthSourceEntity> monthRank7 = getRankMonthMap("2024-07");
        Map<String, RankSaleMonthSourceEntity> monthRank6 = getRankMonthMap("2024-06");
        Map<String, RankSaleMonthSourceEntity> monthRank5 = getRankMonthMap("2024-05");
        Map<String, RankSaleMonthSourceEntity> monthRank4 = getRankMonthMap("2024-04");
        Map<String, RankSaleMonthSourceEntity> monthRank3 = getRankMonthMap("2024-03");

        List<SeriesEntity> seriesList = seriesMapper.getAllSeries();
        seriesList.sort((s1, s2) -> Integer.compare(s2.getId(), s1.getId()));

        seriesList.stream().filter(x -> Arrays.asList(10, 20, 30).contains(x.getState())).forEach(seriesDto -> {
            try {
                refreshRankList(seriesDto, monthRank8, monthRank7);
            } catch (Exception e) {
                log.error("2024-08刷新错误", e);
            }
            try {
                refreshRankList(seriesDto, monthRank7, monthRank6);
            } catch (Exception e) {
                log.error("2024-07刷新错误", e);
            }
            try {
                refreshRankList(seriesDto, monthRank6, monthRank5);
            } catch (Exception e) {
                log.error("2024-06刷新错误", e);
            }

            try {
                refreshRankList(seriesDto, monthRank5, monthRank4);
            } catch (Exception e) {
                log.error("2024-05刷新错误", e);
            }
            try {
                refreshRankList(seriesDto, monthRank4, monthRank3);
            } catch (Exception e) {
                log.error("2024-04刷新错误", e);
            }

            xxlLog.accept(seriesDto.getId() + " success");
        });
    }

    /**
     * 销量趋势协议模板
     */
    private final static String RANK_HISTORY_SCHEME_TEMPLATE = "rn://Car_SeriesSummary/SaleHistory?panValid=0&seriesid=%d&seriesname=%s&typeid=%s&subranktypeid=%s&date=%s&energytype=%d";

    private void refreshRankList(SeriesEntity seriesDetailDto,
                                 Map<String, RankSaleMonthSourceEntity> rankMap,
                                 Map<String, RankSaleMonthSourceEntity> rankMap2) {
        String seriesid = seriesDetailDto.getId() + "";
        if (rankMap.containsKey(seriesid)) {
            RankSaleMonthSourceEntity monthRank = rankMap.get(seriesid);

            SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = new SeriesSubscribeNewsDto.RankInfoDto();
            LocalDate month = LocalDate.parse(monthRank.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER);
            rankInfoDto.setDate(month.getMonthValue() + "月");

            RankSaleMonthSourceEntity rankSaleMonthSourceEntity = rankMap2.get(seriesid);
            if (rankSaleMonthSourceEntity != null) {
                rankInfoDto.setLastSaleCount(rankSaleMonthSourceEntity.getSalecnt());
            } else {
                rankInfoDto.setLastSaleCount(0);
            }
            rankInfoDto.setCurrentSaleCount(monthRank.getSalecnt());
            rankInfoDto.setScheme(String.format(RANK_HISTORY_SCHEME_TEMPLATE, seriesDetailDto.getId(), CommonHelper.encodeUrl(seriesDetailDto.getName()), 1, 1, monthRank.getMonth(), 0));
            rankInfoDto.setRnnum(monthRank.getRnnum());
            rankInfoDto.setDateValue(monthRank.getMonth());
            Date date = DateUtil.parse(monthRank.getMonth() + "-10 11:00:00", "yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            long time = calendar.getTimeInMillis() + monthRank.getRnnum() * 1000;
            updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.RANK_MONTH.getType(), new Timestamp(time)), JSONObject.toJSONString(rankInfoDto));
        }
    }

    public Map<String, RankSaleMonthSourceEntity> getRankMonthMap(String lastMonth) {
        List<RankSaleMonthSourceEntity> monthRankList = rankSaleMonthSourceComponent.getSaleCountByCondition(lastMonth, lastMonth, 1000);

        for (int i = 0; i < monthRankList.size(); i++) {
            RankSaleMonthSourceEntity rankSaleMonthSourceEntity = monthRankList.get(i);
            rankSaleMonthSourceEntity.setRnnum(i + 1);
        }
        return monthRankList.stream().collect(Collectors.toMap(RankSaleMonthSourceEntity::getSeriesid, x -> x));
    }

    private void refreshWeekRankList(SeriesEntity seriesDetailDto,
                                     Map<String, RankSaleWeekSourceEntity> rankMap,
                                     Map<String, RankSaleWeekSourceEntity> rankMap2) {
        String seriesid = seriesDetailDto.getId() + "";
        if (rankMap.containsKey(seriesid)) {
            RankSaleWeekSourceEntity weekRank = rankMap.get(seriesid);
            LocalDate week = LocalDate.parse(weekRank.getWeek_day(), RankConstant.LOCAL_WEEK_FORMATTER);
            LocalDate weekStart = week.minusDays(1);
            LocalDate weekEnd = week.plusDays(5);

            SeriesSubscribeNewsDto.RankInfoDto rankInfoDto = new SeriesSubscribeNewsDto.RankInfoDto();
            rankInfoDto.setDate(weekStart.format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER) + "-" + weekEnd.format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER));
            RankSaleWeekSourceEntity lastWeekRank = rankMap2.get(seriesid);
            if (lastWeekRank != null) {
                rankInfoDto.setLastSaleCount(lastWeekRank.getSalecnt());
            } else {
                rankInfoDto.setLastSaleCount(0);
            }
            rankInfoDto.setCurrentSaleCount(weekRank.getSalecnt());
            rankInfoDto.setRnnum(weekRank.getRnnum());
            rankInfoDto.setDateValue(weekRank.getWeek_day());
            rankInfoDto.setScheme(String.format(RANK_HISTORY_SCHEME_TEMPLATE, seriesDetailDto.getId(), CommonHelper.encodeUrl(seriesDetailDto.getName()), 1, 2, weekRank.getWeek_day(), 0));


            Date date = DateUtil.parse(weekRank.getWeek_day() + " 11:00:00", "yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // 给Calendar对象添加7天
            calendar.add(Calendar.DATE, 7);
            long time = calendar.getTimeInMillis() + weekRank.getRnnum() * 1000;

            updateDB(makeParam(seriesDetailDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.RANK_WEEK.getType(), new Timestamp(time)), JSONObject.toJSONString(rankInfoDto));
        }
    }

    public Map<String, RankSaleWeekSourceEntity> getRankWeekMap(String lastWeek) {
        List<RankSaleWeekSourceEntity> weekRankList = rankSaleWeekSourceComponent.getListByWeek(lastWeek, lastWeek, 1000);

        for (int i = 0; i < weekRankList.size(); i++) {
            RankSaleWeekSourceEntity rankSaleMonthSourceEntity = weekRankList.get(i);
            rankSaleMonthSourceEntity.setRnnum(i + 1);
        }
        return weekRankList.stream().collect(Collectors.toMap(RankSaleWeekSourceEntity::getSeriesid, x -> x));
    }

    /**
     * 图片更新
     */
    void refreshSeriesPic(SeriesEntity seriesDto, Date oneEightyDaysAgo) {
        List<CarPhotoViewEntity> carPhotoViewEntityList = carPhotoViewMapper.getPhotoViewBySeriesId(seriesDto.getId(), "");
        if (CollectionUtils.isEmpty(carPhotoViewEntityList)) {
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dates = new ArrayList<>();


        for (CarPhotoViewEntity entity : carPhotoViewEntityList) {
            Date uploadDate = entity.getPicUploadTime();
            if (uploadDate.after(oneEightyDaysAgo)) {
                String formattedDate = formatter.format(uploadDate);
                if (!dates.contains(formattedDate)) {
                    dates.add(formattedDate);
                }
            }
        }


        BaseModel<SeriesVrExteriorResult> vr = panoApiClient.getSeriesExterior(seriesDto.getId()).join();
        for (String date : dates) {
            List<CarPhotoViewEntity> collect = carPhotoViewEntityList.stream().filter(x -> date.equals(formatter.format(x.getPicUploadTime()))).collect(Collectors.toList());
            SeriesSubscribeNewsDto.PicDTO picDTO = new SeriesSubscribeNewsDto.PicDTO();

            collect = collect.stream().sorted(Comparator.comparing(CarPhotoViewEntity::getClassOrder)
                    .thenComparing(CarPhotoViewEntity::getShowId, Comparator.reverseOrder())
                    .thenComparing(CarPhotoViewEntity::getSourceTypeOrder)
                    .thenComparing(CarPhotoViewEntity::getDealerPicOrder)
                    .thenComparing(CarPhotoViewEntity::getSpecPicUploadTimeOrder, Comparator.reverseOrder())
                    .thenComparing(CarPhotoViewEntity::getPicId, Comparator.reverseOrder())
            ).collect(Collectors.toList());

            picDTO.setPublishTime(collect.stream()
                    .sorted(Comparator.comparing(CarPhotoViewEntity::getPicUploadTime).reversed()).findFirst().get().getPicUploadTime());

            picDTO.setPicCount(collect.size());
            picDTO.setLinkurl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName())));

            //外观下是否有颜色,且颜色数量>1
            Map<Integer, List<CarPhotoViewEntity>> outPicMap = collect.stream()
                    .filter(x -> x.getPicClass() == 1 && x.getPicColorId() != 0)
                    .collect(Collectors.groupingBy(CarPhotoViewEntity::getPicColorId));
            if (!outPicMap.isEmpty()) {

                for (Map.Entry<Integer, List<CarPhotoViewEntity>> carPhotoViewMap : outPicMap.entrySet()) {
                    //如果只有一种，将一种颜色的外观图放出四张
                    if (outPicMap.size() == 1) {
                        carPhotoViewMap.getValue().stream().limit(4).forEach(x -> {
                            SeriesSubscribeNewsDto.PicDTO.picItemsDTO item = new SeriesSubscribeNewsDto.PicDTO.picItemsDTO();
                            item.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                            item.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName()), x.getPicClass()));
                            picDTO.getPicItems().add(item);
                        });
                    } else {
                        SeriesSubscribeNewsDto.PicDTO.picItemsDTO item = new SeriesSubscribeNewsDto.PicDTO.picItemsDTO();
                        item.setColorName(carPhotoViewMap.getValue().get(0).getColorname());
                        item.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(carPhotoViewMap.getValue().get(0).getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                        String url = String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=1&colorid=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName()), carPhotoViewMap.getValue().get(0).getPicColorId());
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
                        picDTO.getPicItems().add(item);
                    }
                }
            }
            if (picDTO.getPicItems().size() == 0) {
                collect.stream().limit(4).forEach(x -> {
                    SeriesSubscribeNewsDto.PicDTO.picItemsDTO item = new SeriesSubscribeNewsDto.PicDTO.picItemsDTO();
                    item.setPic(ImageUtils.convertImage_SizeWebp(ImageUtils.getFullImagePathWithoutReplace(x.getPicFilePath()), ImageSizeEnum.ImgSize_4x3_400x300));
                    item.setUrl(String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s&categoryid=%s", seriesDto.getId(), UrlUtil.encode(seriesDto.getName()), x.getPicClass()));
                    picDTO.getPicItems().add(item);
                });
            }
            updateDB(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.IMAGE.getType(), new Timestamp(picDTO.getPublishTime().getTime())), JSONObject.toJSONString(picDTO));
        }
    }

    /**
     * 提车作业分享（仅限全新车系）
     */
    void refreshSeriesFriendShare(SeriesEntity seriesDto, Date oneEightyDaysAgo) {
        clubApiClient.GetTopicListByContentType(seriesDto.getId(), 1076, 50, 1).thenAccept(clubTopicResult -> {
            if (clubTopicResult != null && clubTopicResult.getResult() != null && clubTopicResult.getResult().getList() != null && clubTopicResult.getResult().getList().size() > 0) {
                for (TopicContentResult.ListDTO listDTO : clubTopicResult.getResult().getList()) {
                    Date publishTime = DateUtil.parse(listDTO.getPostdate(), "yyyy-MM-dd HH:mm:ss");

                    if (publishTime.after(oneEightyDaysAgo)) {
                        SeriesSubscribeNewsDto.friendShareDTO friendShareDTO = new SeriesSubscribeNewsDto.friendShareDTO();
                        friendShareDTO.setPublishTime(DateUtil.parse(listDTO.getPostdate(), "yyyy-MM-dd HH:mm:ss"));
                        BaseModel<List<UserInfoResult>> userInfo = userApiClient.getUserInfoList(listDTO.getPost_memberid()).join();
                        if (null != userInfo && null != userInfo.getResult() && 0 == userInfo.getReturncode() && !CollectionUtils.isEmpty(userInfo.getResult())) {
                            friendShareDTO.setAuthorName(userInfo.getResult().get(0).getNewnickname());
                        } else {
                            friendShareDTO.setAuthorName(listDTO.getPost_membername());
                        }
                        friendShareDTO.setTitle(listDTO.getTitle());
                        friendShareDTO.setScheme(String.format("autohome://club/topicdetail?topicid=%d&bbsid=%d&from=10", listDTO.getTopicid(), seriesDto.getId()));
                        friendShareDTO.setTopicid(listDTO.getTopicid());

                        if (listDTO.getVideoinfo() != null) {
                            friendShareDTO.setIsvideo(true);
                            friendShareDTO.setDuration(listDTO.getVideoinfo().getDuration());
                            friendShareDTO.getImgUrlList().add(ImageUtils.convertImageUrl(listDTO.getVideoinfo().getVideoimg(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                        } else {
                            if (StringUtils.isNotEmpty(listDTO.getImgs())) {
                                String[] arrImgs = listDTO.getImgs().split(",");
                                if (arrImgs != null && arrImgs.length > 0) {
                                    for (String imgurl : arrImgs) {
                                        if (friendShareDTO.getImgUrlList().size() < 3) {
                                            friendShareDTO.getImgUrlList().add(ImageUtils.convertImageUrl(imgurl, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                                        }
                                    }
                                }
                            }
                        }


                        BaseModel<List<UserAuthSeriesResult>> authCar = userApiClient.getUserAuthseries(listDTO.getPost_memberid()).join();
                        boolean hasAuthCar = null != authCar && null != authCar.getResult() && 0 == authCar.getReturncode() && !CollectionUtils.isEmpty(authCar.getResult());
                        if (hasAuthCar && authCar.getResult().get(0).getList() != null) {
                            UserAuthSeriesResult.AuthseriesResult authseriesResult = authCar.getResult().get(0).getList().stream().filter(x -> seriesDto.getId() == x.getSeriesId()).findFirst().orElse(null);
                            friendShareDTO.setCarowner(authseriesResult != null);
                        }
                        updateDB(makeParam(seriesDto.getId(), 0, 0, 1, SeriesSubscribeNewsEnum.CAR_WORK.getType(), new Timestamp(friendShareDTO.getPublishTime().getTime())), JSONObject.toJSONString(friendShareDTO));
                    }
                }
            }
        }).exceptionally(e -> null).join();
    }


    // region 刷新价格
    public void refreshPriceDown(Consumer<String> xxlLog) {
        try {
            int i = seriesSubscribeNewsMapper.cleanPriceDown();
            if (i > 0) {
                xxlLog.accept("cleanPriceDown success");
            }
        } catch (Exception e) {
            log.error("cleanPriceDown error", e);
        }
        List<SeriesEntity> seriesList = seriesMapper.getAllSeries();
        seriesList.sort(Comparator.comparingInt(SeriesEntity::getId).reversed());
        List<SpecEntity> specList = specMapper.getSpecAll();
        List<Integer> cityIdList = CityUtil.getAllCityIds();
        Map<Integer, List<SpecEntity>> specMap = specList.stream().collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        LocalDate nowDate = LocalDate.now();
        LocalDate beginDate = nowDate.minusDays(60);
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate cur = LocalDate.from(beginDate);
        while (!cur.isAfter(nowDate)) {
            dateList.add(cur);
            cur = cur.plusDays(1);
        }
        seriesList.stream().filter(x -> Arrays.asList(10, 20, 30).contains(x.getState())).forEach(seriesDto -> {
            try {
                List<SpecEntity> curSpecList = specMap.get(seriesDto.getId());
                refreshGuidePriceDown(seriesDto, curSpecList);
                refreshDealerPriceDown(seriesDto, curSpecList, cityIdList, dateList);
                xxlLog.accept("当前车系ID:" + seriesDto.getId());
            } catch (Exception e) {
                log.error("动态频道刷入历史降价数据error", e);
            }
            xxlLog.accept(seriesDto.getId() + " success");
        });
    }

    private void refreshDealerPriceDown(SeriesEntity seriesDto, List<SpecEntity> specList, List<Integer> cityIdList, List<LocalDate> dateList) {
        LocalDate nowDate = LocalDate.now();
        LocalDate beginDate = nowDate.minusDays(60);
        int seriesId = seriesDto.getId();
        List<Integer> specIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(specList)) {
            specIdList.addAll(specList.stream().map(SpecEntity::getId).toList());
        }
        List<LocalDate> localDateList = dateList.stream().filter(beginDate::isBefore).toList();
        for (Integer cityId : cityIdList) {
            Map<TreeMap<String, Object>, String> map = new HashMap<>();
            List<SpecCityPriceHistoryDto> dealerPriceList = getByCityAndSpecIdList(cityId, specIdList);
            Map<Integer, Map<LocalDate, Integer>> specDatePriceMap = new HashMap<>();
            // 城市-车型 价格历史 List
            dealerPriceList.forEach(specDto -> {
                Map<LocalDate, Integer> dateMap = new LinkedHashMap<>();
                List<SpecCityPriceHistoryDto.SpecCityPriceItemDto> dtoList = specDto.getList();
                AtomicInteger prevInteger = new AtomicInteger();
                SpecCityPriceHistoryDto.SpecCityPriceItemDto firstItem = dtoList.get(0);
                for (SpecCityPriceHistoryDto.SpecCityPriceItemDto priceItemDto : dtoList) {
                    if (!priceItemDto.getDate().isAfter(beginDate)) {
                        firstItem = priceItemDto;
                    }
                }
                Map<LocalDate, Integer> priceMap = dtoList.stream().collect(Collectors.toMap(SpecCityPriceHistoryDto.SpecCityPriceItemDto::getDate, SpecCityPriceHistoryDto.SpecCityPriceItemDto::getNewsPrice));
                for (LocalDate date : dateList) {
                    if (prevInteger.get() == 0 && !date.isBefore(firstItem.getDate())) {
                        prevInteger.set(firstItem.getNewsPrice());
                    }
                    int curPrice = priceMap.getOrDefault(date, prevInteger.get());
                    if (curPrice != 0) {
                        prevInteger.set(curPrice);
                    }
                    dateMap.put(date, curPrice);

                }
                specDatePriceMap.put(specDto.getSpecId(), dateMap);
            });
            Map<Integer, Integer> specPrevPriceMap = new HashMap<>();
            SeriesSubscribeNewsDto.SeriesPriceDownDto lastPriceDownDto = null;
            TreeMap<String, Object> lastPriceKey = null;
            if (!specDatePriceMap.isEmpty()) {
                // 降价前最低价格
                int prevMinPrice = Integer.MAX_VALUE;
                // 降价前最高价格
                int prevMaxPrice = Integer.MIN_VALUE;
                for (LocalDate localDate : localDateList) {
                    LocalDateTime localDateTime = localDate.atStartOfDay();
                    // 最大降价车型ID
                    int maxPriceDownSpecId = 0;
                    // 最大降价车型名称
                    String maxPriceDownSpecName = StrPool.EMPTY;

                    // 最大降价差额
                    double maxPriceGapPct = 0;
                    // 降价车型个数
                    int priceDownSpecCount = 0;
                    // 当前最低价格

                    boolean isSeriesPriceDown = false;
                    int curMinPrice = Integer.MAX_VALUE;
                    // 当前最高价格
                    int curMaxPrice = Integer.MIN_VALUE;
                    int minDownPrice = Integer.MAX_VALUE;
                    int maxDownPrice = Integer.MIN_VALUE;
                    for (SpecEntity spec : specList) {
                        int specId = spec.getId();
                        if (specDatePriceMap.containsKey(specId)) {
                            Map<LocalDate, Integer> todayPriceMap = specDatePriceMap.get(specId);
                            int price = todayPriceMap.get(localDate);
                            if (price == 0) {
                                continue;
                            }
                            int prevPrice = specPrevPriceMap.getOrDefault(specId, 0);
                            curMinPrice = Math.min(curMinPrice, price);
                            curMaxPrice = Math.max(curMaxPrice, price);
                            if (localDate.isAfter(beginDate) && prevPrice != price) {
                                int priceGap = prevPrice - price;
                                // 降价百分比
                                double downPricePercent = (double) priceGap / prevPrice;
                                if (priceGap > 0) {
                                    if (downPricePercent >= maxPriceGapPct) {
                                        maxPriceGapPct = downPricePercent;
                                        maxPriceDownSpecId = specId;
                                        maxPriceDownSpecName = spec.getName();
                                    }
                                    priceDownSpecCount++;
                                }

                                // 降价百分比大于阈值
                                boolean biggerThanThreshold = downPricePercent > seriesSubscribeConfig.getPriceDownThreshold().getDealerThreshold();
                                if (biggerThanThreshold) {
                                    minDownPrice = Math.min(minDownPrice, priceGap);
                                    maxDownPrice = Math.max(maxDownPrice, priceGap);
                                    isSeriesPriceDown = true;
                                }
                                SeriesSubscribeNewsDto.SpecPriceDownDto priceDownDto = new SeriesSubscribeNewsDto.SpecPriceDownDto();
                                priceDownDto.setCurPrice(price);
                                priceDownDto.setPrevPrice(prevPrice);
                                priceDownDto.setSpecName(spec.getName());

                                localDateTime = localDateTime.plusSeconds(1);
                                map.put(makeParam(seriesId, specId, cityId, biggerThanThreshold ? 1 : 0, SeriesSubscribeNewsEnum.SPEC_DEALER_PRICE.getType(), Timestamp.valueOf(localDateTime)), JSONObject.toJSONString(priceDownDto));
                            }
                            // 未存储过或价格有更新时 更新上次价格
                            if (!specPrevPriceMap.containsKey(specId) || price != specPrevPriceMap.get(specId)) {
                                specPrevPriceMap.put(specId, price);
                            }
                        }
                    }
                    if (isSeriesPriceDown) {
                        // 最新价格使用车系页价格
                        SeriesSubscribeNewsDto.SeriesPriceDownDto priceDownDto = new SeriesSubscribeNewsDto.SeriesPriceDownDto();
                        lastPriceDownDto = priceDownDto;
                        priceDownDto.setCurMinPrice(curMinPrice);
                        priceDownDto.setCurMaxPrice(curMaxPrice);
                        priceDownDto.setPrevMinPrice(prevMinPrice);
                        priceDownDto.setPrevMaxPrice(prevMaxPrice);
                        priceDownDto.setMinPriceDown(minDownPrice);
                        priceDownDto.setMaxPriceDown(maxDownPrice);
                        priceDownDto.setCount(priceDownSpecCount);
                        priceDownDto.setSpecName(maxPriceDownSpecName);
                        priceDownDto.setSpecId(maxPriceDownSpecId);
                        localDateTime = localDateTime.plusSeconds(1);
                        TreeMap<String, Object> keyTreeMap = makeParam(seriesId, 0, cityId, 1, SeriesSubscribeNewsEnum.SERIES_DEALER_PRICE.getType(), Timestamp.valueOf(localDateTime));
                        lastPriceKey = keyTreeMap;
                        map.put(keyTreeMap, JSONObject.toJSONString(priceDownDto));
                    }
                    prevMinPrice = curMinPrice;
                    prevMaxPrice = curMaxPrice;
                }
            }
            // 更新最后一次降价时的车系报价, 使用车系经销商价格
            if (Objects.nonNull(lastPriceDownDto) && Objects.nonNull(lastPriceKey)) {
                CompletableFuture<SeriesCityAskPriceDto> seriesCityAskPriceFuture = seriesCityAskPriceNewComponent.get(seriesId, cityId);
                SeriesCityAskPriceDto seriesDealerPrice = seriesCityAskPriceFuture.join();
                lastPriceDownDto.setCurMinPrice(seriesDealerPrice.getMinPrice());
                lastPriceDownDto.setCurMaxPrice(seriesDealerPrice.getMaxPrice());
                map.put(lastPriceKey, JSONObject.toJSONString(lastPriceDownDto));
            }
            if (!map.isEmpty()) {
                updateDBBatch(map);
            }
        }
    }

    /**
     * 更新指导价
     *
     * @param seriesDto 车系dto
     * @param specList  车型Dto List
     */
    private void refreshGuidePriceDown(SeriesEntity seriesDto, List<SpecEntity> specList) {
        if (specList.isEmpty()) {
            return;
        }
        int seriesId = seriesDto.getId();
        Map<TreeMap<String, Object>, String> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        for (SpecEntity specEntity : specList) {
            SeriesSubscribeNewsDto.SpecPriceDownDto specPriceDownDto = new SeriesSubscribeNewsDto.SpecPriceDownDto();
            specPriceDownDto.setCurPrice(specEntity.getMinPrice());
            specPriceDownDto.setSpecName(specEntity.getName());
            specPriceDownDto.setPrevPrice(0);
            map.put(makeParam(seriesId, specEntity.getId(), 0, 0, SeriesSubscribeNewsEnum.SPEC_GUIDE_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(specPriceDownDto));
            now = now.plusSeconds(1);
        }

        // 新增车系
        SeriesSubscribeNewsDto.SeriesPriceDownDto seriesPriceDownDto = new SeriesSubscribeNewsDto.SeriesPriceDownDto();
        seriesPriceDownDto.setCurMinPrice(seriesDto.getSeriesPriceMin());
        seriesPriceDownDto.setCurMaxPrice(seriesDto.getSeriesPriceMax());
        seriesPriceDownDto.setSpecName(StrPool.EMPTY);
        seriesPriceDownDto.setMinPriceDown(0);
        seriesPriceDownDto.setMaxPriceDown(0);
        seriesPriceDownDto.setPrevMinPrice(0);
        seriesPriceDownDto.setPrevMaxPrice(0);
        seriesPriceDownDto.setCount(0);
        now = now.plusSeconds(1);
        map.put(makeParam(seriesId, 0, 0, 0, SeriesSubscribeNewsEnum.SERIES_GUIDE_PRICE.getType(), Timestamp.valueOf(now)), JSONObject.toJSONString(seriesPriceDownDto));
        updateDBBatch(map);
    }

    public List<SpecCityPriceHistoryDto> getByCityAndSpecIdList(int cityId, List<Integer> specIdList) {
        if (!CollectionUtils.isEmpty(specIdList) && cityId > 0) {
            String specIds = specIdList.stream().map(Objects::toString).collect(Collectors.joining(StrPool.COMMA));
            List<SpecCityPriceHistoryEntity> resultList = specCityPriceHistoryMapper.selectByCityIdAndSpecIdIn(cityId, specIds);
            return resultList.stream().map(x -> {
                SpecCityPriceHistoryDto dto = new SpecCityPriceHistoryDto();
                dto.setCityId(x.getCityId());
                dto.setSpecId(x.getSpecId());

                dto.setList(JSONArray.parseArray(x.getData(), SpecCityPriceHistoryDto.SpecCityPriceItemDto.class));
                return dto;
            }).toList();
        }
        return Collections.emptyList();
    }

    public int cleanData(int type) {
        return seriesSubscribeNewsMapper.cleanDataByType(type);
    }
}
