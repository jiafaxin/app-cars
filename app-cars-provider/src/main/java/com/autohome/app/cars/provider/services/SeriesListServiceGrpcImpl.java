package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.messages.SeriesDetailMessage;
import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.apiclient.bfai.dtos.SSeriesSortListResult;
import com.autohome.app.cars.common.enums.EnergyTypesNewEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.provider.basic.car.FactoryVo;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.BrandSeriesComponent;
import com.autohome.app.cars.service.components.car.ProtobufSeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
import com.autohome.app.cars.service.components.car.dtos.BrandSeriesDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.services.SeriesListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.app_cars.v1.carbase.*;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@DubboService
@RestController
@Slf4j
public class SeriesListServiceGrpcImpl extends DubboSeriesListServiceTriple.SeriesListServiceImplBase {

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    BrandSeriesComponent brandSeriesComponent;
//
//    @Autowired
//    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    ProtobufSeriesDetailComponent seriesDetailComponent;

    @Autowired
    SeriesListService seriesListService;


    @Override
    @GetMapping(value = "/carbase/selectcarportal/serieslistbaseinfocard", produces = "application/json;charset=utf-8")
    public SeriesListBaseInfoResponse seriesListBaseInfo(SeriesListBaseInfoRequest request) {

        SeriesListBaseInfoResponse.Result.Builder result = SeriesListBaseInfoResponse.Result.newBuilder();
        int brandid = request.getBrandid();
        boolean isUpdate = brandid == 609;
        if (isUpdate) {
            brandid = 509;
        }
        //获取品牌信息
        CompletableFuture<BrandDetailDto> brandDetailTask = brandDetailComponent.get(brandid);

        //根据品牌Id获取车系列表
        int finalBrandid = brandid;
        var mainTask = brandSeriesComponent.get(brandid).thenComposeAsync(brandSeries -> {
            List<Integer> seriesIds = brandSeries.getFctoryList().stream()
                    .flatMap(item -> item.getSeriesList().stream())
                    .map(BrandSeriesDto.SeriesItem::getId)
                    .collect(Collectors.toList());

            if (seriesIds.size() == 0) {
                return CompletableFuture.completedFuture(null);
            }

            //根据车系id获取车系详情
            return seriesDetailComponent.getList(seriesIds).thenCombineAsync(brandDetailTask, (seriesList, brandDetail) -> {
                result.setBrandname(brandDetail.getName());
                result.setBrandicon(ImageUtils.convertImage_SizeWebp(brandDetail.getLogo(), ImageSizeEnum.ImgSize_100x100));

                //级别和厂商列表
                createLevelFactory(result, seriesList, brandSeries.getFctoryList());

                //获取推荐中车系的顺序
                Map<Integer, Integer> seriesMap = seriesList.stream().collect(Collectors.toMap(SeriesDetailMessage::getId, SeriesDetailMessage::getState));

                //郑州日产173车系4691，需要copy到东风日产92下
                if (brandDetail.getId() == 63) {
                    Optional<SeriesDetailMessage> first = seriesList.stream().filter(p -> p.getId() == 4691).findFirst();
                    if (first.isPresent()) {
                        seriesList.add(0, first.get().toBuilder().setFctId(92).build());
                    }
                }

                //推荐车系排序
                CompletableFuture<SSeriesSortListResult> seriesSortList = seriesListService.getSeriesSortList(request.getQueryid(), request.getCityid(), request.getPm(), request.getDeviceid(), finalBrandid, brandDetail.getName(), request.getSource(), request.getRecommendswitch(), seriesMap);

                List<SeriesDetailMessage> finalSeriesList = seriesList;
                seriesSortList.thenAccept(sseriesSortListResult -> {

                    String pvid = "";
                    String pv_ext = "";

                    Map<Integer, SSeriesSortListResult.CarColorItem> colorMap = new HashMap<>();
                    if (sseriesSortListResult != null && sseriesSortListResult.getResult() != null && sseriesSortListResult.getResult().getData() != null && sseriesSortListResult.getResult().getData().size() > 0) {

                        //用推荐返回的车系id，对车系列表进行排序
                        List<Integer> ids = sseriesSortListResult.getResult().getData().stream().distinct().toList();
                        finalSeriesList.sort(Comparator.comparingInt(o -> ids.indexOf(o.getId())));

                        pvid = sseriesSortListResult.getResult().getPvid();
                        pv_ext = sseriesSortListResult.getResult().getExt();
                        colorMap = sseriesSortListResult.getResult().getData1().stream().filter(Objects::nonNull).collect(Collectors.toMap(SSeriesSortListResult.CarColorItem::getSeries_id, Function.identity(), (key1, key2) -> key2));
                    } else {
                        //默认排序：状态》级别》价格》车系名
                        //下面车系所在级别索引+1000/2000/3000为了状态优先情况下再排级别
                        List<SeriesListBaseInfoResponse.Result.Alllevelinfo> sortLevels = result.getAlllevelinfoList().stream().filter(level -> level.getLevelid() != 0 && level.getLevelid() < 2021512).toList();
                        finalSeriesList.sort(Comparator.comparingInt((SeriesDetailMessage series) -> {
                            Optional<Integer> indexOptional = sortLevels.stream()
                                    .filter(level -> level.getLevelid() == series.getLevelId())
                                    .findFirst()
                                    .map(sortLevels::indexOf);

                            if (series.getState() == 10 || series.getState() == 20 || series.getState() == 30) {
                                return indexOptional.orElse(-1) + 1000;
                            } else if (series.getState() == 0) {
                                return indexOptional.orElse(-1) + 2000;
                            } else {
                                return indexOptional.orElse(-1) + 3000;
                            }
                        }).thenComparingInt(x->getMiniprice(x)).thenComparing(SeriesDetailMessage::getName, String.CASE_INSENSITIVE_ORDER));
                    }

                    //组装对象
                    int index = 0;
                    for (SeriesDetailMessage series : finalSeriesList) {
                        SeriesListBaseInfoResponse.Result.Serieslist.Builder seriesListBuilder = SeriesListBaseInfoResponse.Result.Serieslist.newBuilder();

                        //把seriesDto转成builder对象
                        SeriesListBaseInfoResponse.Result.Serieslist.Data.Builder seriesDataBuilder = createSeriesDataBuilder(series);

                        String carColorExt = "";
                        if (colorMap.containsKey(series.getId())) {
                            SSeriesSortListResult.CarColorItem carColorItem = colorMap.get(series.getId());
                            //如果推荐接口有返回图片，则替换为搜索过来的图片
                            if (carColorItem != null && StringUtils.isNotBlank(carColorItem.getImg_url())) {
                                seriesDataBuilder.setImgurl(ImageUtils.convertImageUrl(carColorItem.getImg_url(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300, true, true, true));
                                carColorExt = carColorItem.getExt();
                            }
                        }

                        //设置埋点信息
                        seriesDataBuilder.setPvdata(getPvData(finalBrandid, series.getId(), index++, pvid, pv_ext, carColorExt));

                        seriesListBuilder.setData(seriesDataBuilder);
                        seriesListBuilder.setType(17001);
                        result.addSerieslist(seriesListBuilder);
                    }
                }).exceptionally(e -> {
                    log.error("循环车系报错", e);
                    return null;
                }).join();

                return null;
            }).exceptionally(e -> {
                log.error("获取品牌车系异常", e);
                return null;
            });
        });

        //设置排序方式
        result.addOrderinfolist(
                SeriesListBaseInfoResponse.Result.Orderinfolist.newBuilder().setName("智能排序").setType("0")
        ).addOrderinfolist(
                SeriesListBaseInfoResponse.Result.Orderinfolist.newBuilder().setName("价格低").setType("1")
        ).addOrderinfolist(
                SeriesListBaseInfoResponse.Result.Orderinfolist.newBuilder().setName("价格高").setType("2")
        );

        //等待任务
        mainTask.join();
        result.setSerieslistrcmpvid(result.getSerieslistList().size() > 0 ? result.getSerieslist(0).getData().getPvdata().getPvid() : "");
        result.setRightname("品牌介绍");
        result.setRightlink("autohome://rninsidebrowser?url=" + UrlUtil.encode("rn://Car_SeriesSummary/BrandIntroduce?title=" + UrlUtil.encode(result.getBrandname()) + "&brandid=" + brandid));
        result.setPvdata(getAllPvData(result.getSerieslistList()));
        processSpecialBrand(isUpdate, result);
        return SeriesListBaseInfoResponse.newBuilder()
                .setReturnCode(0)
                .setReturnMsg("success")
                .setResult(result)
                .build();
    }

    /**
     * 当品牌id = 609
     */
    private void processSpecialBrand(boolean isFilter, SeriesListBaseInfoResponse.Result.Builder result) {
        try {
            if (!isFilter) {
                return;
            }
            if (!CollectionUtils.isEmpty(result.getSerieslistList())) {
                List<SeriesListBaseInfoResponse.Result.Serieslist> collect = result.getSerieslistList().stream().filter(e -> e.getData().getFctid() == 774).toList();
                result.clearSerieslist();
                result.addAllSerieslist(collect);
            }
            if (!CollectionUtils.isEmpty(result.getAllfctinfoList())) {
                List<SeriesListBaseInfoResponse.Result.Allfctinfo> allfctinfos = result.getAllfctinfoList().stream().filter(e -> e.getFctid() == 774 || e.getFctid() == 0).toList();
                result.clearAllfctinfo();
                result.addAllAllfctinfo(allfctinfos);
            }
            if (!CollectionUtils.isEmpty(result.getAlllevelinfoList())) {
                List<SeriesListBaseInfoResponse.Result.Alllevelinfo> alllevelinfos = result.getAlllevelinfoList().stream().filter(e -> e.getLevelid() == 2 || e.getLevelid() == 0).toList();
                result.clearAlllevelinfo();
                result.addAllAlllevelinfo(alllevelinfos);
            }
            result.setRightname("");
            result.setRightlink("");
            result.setBrandname("AITO");
            result.setBrandicon(ImageUtils.convertImage_SizeWebp("http://car2.autoimg.cn/cardfs/series/g31/M0B/EE/71/autohomecar__ChxoHWX85h-AGwuEAABla0Ui0JE217.png", ImageSizeEnum.ImgSize_100x100));

        } catch (Exception e) {
            log.error("处理车系异常：", e);
        }
    }

    private void createLevelFactory(SeriesListBaseInfoResponse.Result.Builder result, List<SeriesDetailMessage> seriesList, List<BrandSeriesDto.FactoryItem> fctoryList) {
        try {
            HashMap<Integer, SeriesListBaseInfoResponse.Result.Alllevelinfo> mapLevels = new HashMap<>();
            HashMap<Integer, FactoryVo> mapFactories = new HashMap<>();
            int flagEnergytype = 0;
            for (SeriesDetailMessage series : seriesList) {
                String[] levelArr = CarLevelUtil.getLevelName(series.getLevelId()).split(",");
                int levelId = Integer.parseInt(levelArr[0]);
                int fctId = series.getFctId();
                String levelName = levelArr[1];
                if (!mapLevels.containsKey(levelId)) {
                    SeriesListBaseInfoResponse.Result.Alllevelinfo.Builder levelBuilder = SeriesListBaseInfoResponse.Result.Alllevelinfo.newBuilder();
                    levelBuilder.setLevelid(levelId);
                    levelBuilder.setLevelname(levelName);
                    levelBuilder.setSort(levelId);
                    mapLevels.put(levelBuilder.getLevelid(), levelBuilder.build());
                }

                if (series.getEnergytype() == 1) {
                    flagEnergytype = 1;
                }

                if (series.getState() == 0 && !mapLevels.containsKey(20210809)) {
                    SeriesListBaseInfoResponse.Result.Alllevelinfo.Builder levelUnSoldBuilder = SeriesListBaseInfoResponse.Result.Alllevelinfo.newBuilder();
                    levelUnSoldBuilder.setLevelid(20210809);
                    levelUnSoldBuilder.setLevelname("未售");
                    levelUnSoldBuilder.setSort(2021809);
                    mapLevels.put(levelUnSoldBuilder.getLevelid(), levelUnSoldBuilder.build());
                } else if (series.getState() == 40 && !mapLevels.containsKey(2021512)) {
                    SeriesListBaseInfoResponse.Result.Alllevelinfo.Builder levelStopBuilder = SeriesListBaseInfoResponse.Result.Alllevelinfo.newBuilder();
                    levelStopBuilder.setLevelid(2021512);
                    levelStopBuilder.setLevelname("停售");
                    levelStopBuilder.setSort(2021512);
                    mapLevels.put(levelStopBuilder.getLevelid(), levelStopBuilder.build());
                }

                if (!mapFactories.containsKey(series.getFctId())) {
                    FactoryVo factoryVo = new FactoryVo();
                    factoryVo.setId(series.getFctId());
                    factoryVo.setPy(fctoryList.stream().filter(p -> p.getId() == fctId).findFirst().orElse(new BrandSeriesDto.FactoryItem()).getPy());
                    factoryVo.setName(series.getFctName());
                    factoryVo.setPlace(series.getPlace());

                    //车系厂商状态排序，优先在售》停售》==== (未售的厂商不显示)
                    List<SeriesDetailMessage> fctSeriesList = seriesList.stream().filter(p -> p.getFctId() == fctId).toList();
                    if (fctSeriesList.stream().anyMatch(p -> p.getState() >= 10 && p.getState() <= 30)) {
                        factoryVo.setStateSort(1);
                        mapFactories.put(series.getFctId(), factoryVo);
                    } else if (fctSeriesList.stream().anyMatch(p -> p.getState() == 40)) {
                        factoryVo.setStateSort(2);
                        mapFactories.put(series.getFctId(), factoryVo);
                    }
                }
            }

            if (mapLevels.size() > 1) {
                SeriesListBaseInfoResponse.Result.Alllevelinfo.Builder levelBuilder = SeriesListBaseInfoResponse.Result.Alllevelinfo.newBuilder();
                levelBuilder.setLevelid(0);
                levelBuilder.setLevelname("全部级别");
                levelBuilder.setSort(0);
                mapLevels.put(0, levelBuilder.build());
            }

            if (!mapFactories.isEmpty()) {
                FactoryVo factoryVo = new FactoryVo();
                factoryVo.setName("全部厂商");
                factoryVo.setPy("");
                factoryVo.setPlace("");
                mapFactories.put(0, factoryVo);
            }

            List<SeriesListBaseInfoResponse.Result.Alllevelinfo> levelList = mapLevels.values().stream().sorted(Comparator.comparing(SeriesListBaseInfoResponse.Result.Alllevelinfo::getSort)).collect(Collectors.toList());
            List<SeriesListBaseInfoResponse.Result.Allfctinfo> factoryList = mapFactories.values().stream().sorted(Comparator.comparing(FactoryVo::getStateSort).thenComparing(FactoryVo::getPlaceSort).thenComparing(FactoryVo::getPy)).map(p -> {
                SeriesListBaseInfoResponse.Result.Allfctinfo.Builder factoryBuilder = SeriesListBaseInfoResponse.Result.Allfctinfo.newBuilder();
                factoryBuilder.setLevelname(p.getName());
                if (!p.getName().equals("全部厂商")) {
                    factoryBuilder.setFctid(p.getId());
                    factoryBuilder.setLevelid(2021926);
                    factoryBuilder.setSort(2021926);
                }
                return factoryBuilder.build();
            }).toList();

            //新能源放到第二位
            if (flagEnergytype == 1) {
                levelList.add(1, SeriesListBaseInfoResponse.Result.Alllevelinfo.newBuilder().setLevelid(202104).setLevelname("新能源").setSort(202104).build());
            }

            result.addAllAlllevelinfo(levelList);
            result.addAllAllfctinfo(factoryList);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    SeriesListBaseInfoResponse.Result.Serieslist.Data.Builder createSeriesDataBuilder(SeriesDetailMessage series) {
        SeriesListBaseInfoResponse.Result.Serieslist.Data.Builder seriesDataBuilder = SeriesListBaseInfoResponse.Result.Serieslist.Data.newBuilder();
        String[] levelArr = CarLevelUtil.getLevelName(series.getLevelId()).split(",");
        int levelId = Integer.parseInt(levelArr[0]);
        seriesDataBuilder.setId(series.getId());
        seriesDataBuilder.setName(series.getName());
        seriesDataBuilder.setLevelid(levelId);
        seriesDataBuilder.setLevelname(series.getLevelName());
        seriesDataBuilder.setFctid(series.getFctId());
        seriesDataBuilder.setEnergytype(series.getEnergytype());
        //新能源返回fueltypes
        if (series.getEnergytype() == 1 && StringUtils.isNotEmpty(series.getFueltypes())) {
            List<String> typelist = new ArrayList<>();
            Arrays.asList(series.getFueltypes().split(",")).forEach(type -> {
                if (Integer.parseInt(type) != 0) {
                    String typename = EnergyTypesNewEnum.getTypeByValue(Integer.parseInt(type));
                    typelist.add(typename);
                }
            });
            seriesDataBuilder.setFueltypes(String.join("/", typelist));
        }
        seriesDataBuilder.setMiniprice(getMiniprice(series));
        seriesDataBuilder.setSort(series.getRank());
        if (StringUtils.isNotEmpty(series.getLogo())) {
            seriesDataBuilder.setImgurl(ImageUtils.convertImageUrl(series.getPngLogo(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300, true, true, true));
        } else {
            seriesDataBuilder.setImgurl("http://nfiles3.autohome.com.cn/zrjcpk10/series_def_220420.png.webp");
        }
        seriesDataBuilder.setDrivetype(series.getEnergytype() == 1 ? 202104 : 0);
        seriesDataBuilder.setPrice(getPrice(series));
        seriesDataBuilder.setNewenergypricetip(getNewenergypricetip(series));
        seriesDataBuilder.setLinkurl("autohome://car/seriesmain?seriesid=" + series.getId() + "&from=102");
        seriesDataBuilder.setState(series.getState());
        seriesDataBuilder.addAllOrder(Arrays.asList(0, series.getMinPrice(), -series.getMinPrice(), series.getId()));

        return seriesDataBuilder;
    }


    private SeriesListBaseInfoResponse.Result.Serieslist.Data.Pvdata.Builder getPvData(int brandId, int seriesId, int index, String pvid, String pv_ext, String carColorExt) {
        //设置埋点信息
        Map<String, Object> stra = new HashMap<>();
        stra.put("brandid", brandId);
        if (StringUtils.isNotEmpty(pv_ext)) {
            stra.putAll(new JSONObject(pv_ext).toMap());
        }
        if (StringUtils.isNotBlank(carColorExt)) {
            stra.putAll(new JSONObject(carColorExt).toMap());
        }

        SeriesListBaseInfoResponse.Result.Serieslist.Data.Pvdata.Builder spv = SeriesListBaseInfoResponse.Result.Serieslist.Data.Pvdata.newBuilder();
        spv.setT("0");
        spv.setP(index + 1 + "");
        spv.setPvid(pvid);
        spv.setStra(JsonUtil.toString(stra));
        spv.setObjectId(seriesId + "");
        return spv;
    }

    private SeriesListBaseInfoResponse.Result.Pvdata.Builder getAllPvData(List<SeriesListBaseInfoResponse.Result.Serieslist> serieslistList) {
        List<SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg> pvList = new ArrayList<>();
        pvList.add(SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.newBuilder().setArgkey("recm_id").setArgvalue("90100203").build());
        pvList.add(SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.newBuilder().setArgkey("pv_event_id").setArgvalue("car_brand_list_click_display_series_list_pv").build());
        pvList.add(SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.newBuilder().setArgkey("user_id").setArgvalue("0").build());
        pvList.add(SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.newBuilder().setArgkey("refreshtype").setArgvalue("0").build());
        pvList.add(SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.newBuilder().setArgkey("itemcount").setArgvalue(serieslistList.size() + "").build());

        SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.Builder subPv = SeriesListBaseInfoResponse.Result.Pvdata.Requestpvarg.newBuilder();
        subPv.setArgkey("itemlist");
        List<SeriesListBaseInfoResponse.Result.Serieslist.Data.Pvdata> seriesPvDatas = serieslistList.stream().map(p -> p.getData().getPvdata()).collect(Collectors.toList());
        subPv.setArgvalue(JSON.toJSONString(seriesPvDatas));

        pvList.add(subPv.build());
        return SeriesListBaseInfoResponse.Result.Pvdata.newBuilder().addAllRequestpvargs(pvList);
    }


    public String getNewenergypricetip(SeriesDetailMessage message) {
        if (message.getFctId() == 92 && message.getId() == 4691) {
            return "郑州日产生产";
        }
        return "";
    }

    public String getPrice(SeriesDetailMessage message) {
        if (message.getContainBookedSpec() > 0) {
            return "接受预订";
        }
        return PriceUtil.GetPriceStringDetail(message.getMinPrice(), message.getMaxPrice(), message.getState());
    }

    public int getMiniprice(SeriesDetailMessage message) {
        String price = getPrice(message);
        if (price.equals("暂无报价") || price.equals("即将销售") || price.equals("接受预订")) {
            return 999999999;
        }
        return message.getMinPrice();
    }
}