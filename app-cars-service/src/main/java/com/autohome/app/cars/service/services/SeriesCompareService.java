package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.Cardlist;
import autohome.rpc.car.app_cars.v1.carbase.GetBuyCarDiscountInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.GetBuyCarDiscountInfoResponse;
import autohome.rpc.car.app_cars.v1.carbase.GetSeriesDiscountInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.GetSeriesDiscountInfoResponse;
import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import autohome.rpc.car.app_cars.v1.carbase.SeriesCompareRequest;
import autohome.rpc.car.app_cars.v1.carbase.SeriesCompareResponse;
import com.autohome.app.cars.apiclient.share.ShareInfoApiClient;
import com.autohome.app.cars.apiclient.share.dtos.ShareInfoResult;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesSpecComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesSpecDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.dtos.DateRankResultDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleWeekComponent;
import com.autohome.app.cars.service.components.subsidy.SpecCitySubsidyComponent;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecCitySubsidyDto;
import com.autohome.app.cars.service.services.dtos.Ah100TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 车系对比（销量、排名、购车优惠）
 */
@Service
@Slf4j
public class SeriesCompareService {

    private final Logger logger = LoggerFactory.getLogger(Ah100TestConfig.class);

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;
    @Autowired
    private RankSaleMonthComponent rankSaleMonthComponent;
    @Autowired
    private RankSaleWeekComponent rankSaleWeekComponent;
    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;
    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;
    @Autowired
    private SeriesSpecComponent seriesSpecComponent;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecCitySubsidyComponent specCitySubsidyComponent;
    @Autowired
    private ShareInfoApiClient shareInfoApiClient;

    /**
     * 获取车系对比数据
     *
     * @param request
     * @return
     */
    public SeriesCompareResponse getSeriesCompare(SeriesCompareRequest request) {
        try {
            int cityId = request.getCityid();
            String seriesIdStr = request.getSeriesids();
            if (StringUtils.isEmpty(seriesIdStr)) {
                return SeriesCompareResponse.newBuilder().setReturnCode(-1).setReturnMsg("参数错误").build();
            }
            String[] ids = seriesIdStr.split(",");
            List<Integer> seriesIds = Arrays.asList(ids).stream().map(x -> NumberUtils.toInt(x)).filter(x -> Objects.nonNull(x) && x > 0).toList();
            if (CollectionUtils.isEmpty(seriesIds)) {
                return SeriesCompareResponse.newBuilder().setReturnCode(-1).setReturnMsg("参数错误").build();
            }

            List<CompletableFuture> tasks = new ArrayList<>();
            List<CompletableFuture> tasks2 = new ArrayList<>();
            List<DateRankResultDto> monthRankDtos = new ArrayList<>();
            List<DateRankResultDto> weekRankDtos = new ArrayList<>();
            List<SeriesDetailDto> seriesDetails = new ArrayList<>();
            List<SeriesSpecDto> seriesSpecDtos = new ArrayList<>();
            List<SeriesCityAskPriceDto> seriesAskPriceDtos = new ArrayList<>();
            List<SpecCityAskPriceDto> specAskPriceDtos = new ArrayList<>();
            List<SpecCitySubsidyDto> specSubsidyDtos = new ArrayList<>();
            AtomicReference<ShareInfoResult> shareInfoResult = new AtomicReference<>();

            //取车系数据
            tasks.add(seriesDetailComponent.getList(seriesIds).thenAccept(result -> {
                if (result != null && result.size() > 0) {
                    result.removeIf(Objects::isNull);
                    seriesDetails.addAll(result);
                }
            }).exceptionally(e -> {
                log.error("seriesDetailComponent.getList error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            //取经销商车系价格数据
            tasks.add(seriesCityAskPriceNewComponent.get(seriesIds, cityId).thenAccept(result -> {
                if (result != null) {
                    result.removeIf(Objects::isNull);
                    seriesAskPriceDtos.addAll(result);
                }
            }).exceptionally(e -> {
                log.error("seriesCityAskPriceNewComponent.get error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            //取车系的车型数据
            tasks.add(seriesSpecComponent.get(seriesIds).thenAccept(result -> {
                if (result != null && result.size() > 0) {
                    result.removeIf(Objects::isNull);
                    result.forEach(series -> {
                        series.getItems().removeIf(item -> item.getState() != 20 && item.getState() != 30);
                        List<Integer> specids = series.getItems().stream().map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(specids)) {
                            tasks2.add(specCityAskPriceComponent.get(specids, cityId).thenAccept(result2 -> {
                                if (result2 != null && result2.size() > 0) {
                                    result2.removeIf(Objects::isNull);
                                    specAskPriceDtos.addAll(result2);
                                }
                            }));
                            tasks2.add(specCitySubsidyComponent.getList(specids, cityId).thenAccept(result2 -> {
                                if (result2 != null && result2.size() > 0) {
                                    result2.removeIf(Objects::isNull);
                                    specSubsidyDtos.addAll(result2);
                                }
                            }));
                        }
                    });
                    seriesSpecDtos.addAll(result);
                    CompletableFuture.allOf(tasks2.toArray(new CompletableFuture[tasks2.size()])).join();
                }
            }).exceptionally(e -> {
                log.error("seriesSpecComponent.get error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            //按月取销量和排名数据
            tasks.add(CompletableFuture.supplyAsync(() -> rankSaleMonthComponent.getAllListByLastMonth(13)).thenAccept(result -> {
                if (result != null && result.size() > 0) {
                    result.removeIf(Objects::isNull);
                    monthRankDtos.addAll(result);
                }
            }).exceptionally(e -> {
                log.error("getAllListByLastMonth error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            //按周取销量和排名数据
            tasks.add(CompletableFuture.supplyAsync(() -> rankSaleWeekComponent.getAllListByLastWeek(13)).thenAccept(result -> {
                if (result != null && result.size() > 0) {
                    result.removeIf(Objects::isNull);
                    weekRankDtos.addAll(result);
                }
            }).exceptionally(e -> {
                log.error("getAllListByLastWeek error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            String url1 = String.format("rn://CarCPKPageRN/SeriesComparePage?seriesids=%s", UrlUtil.encode(seriesIdStr));
            String scheme = String.format("autohome://car/rnbrowser?url=%s", UrlUtil.encode(url1));
            tasks.add(shareInfoApiClient.getShareInfo("Y10", scheme).thenAccept(result -> {
                if (result != null && result.getResult() != null) {
                    shareInfoResult.set(result.getResult());
                }
            }).exceptionally(e -> {
                log.error("shareInfoApiClient.getShareInfo error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

            if (CollectionUtils.isEmpty(seriesDetails)) {
                return SeriesCompareResponse.newBuilder().setReturnCode(-1).setReturnMsg("车系数据获取失败").build();
            }

            SeriesCompareResponse.Result.Builder resultBuilder = SeriesCompareResponse.Result.newBuilder();
            Cardlist.Builder saleCardList = Cardlist.newBuilder();
            saleCardList.setType(30201);
            saleCardList.setData(getDataForSale(seriesDetails, monthRankDtos, weekRankDtos));
            resultBuilder.addCardlist(saleCardList);

            Cardlist.Builder discountCardList = Cardlist.newBuilder();
            discountCardList.setType(30202);
            discountCardList.setData(getDataForDiscount(0, seriesDetails, seriesAskPriceDtos, specAskPriceDtos, seriesSpecDtos, specSubsidyDtos));
            resultBuilder.addCardlist(discountCardList);

            //分享信息
            SeriesCompareResponse.Result.Shareinfo.Builder shareinfo = getShareInfo(shareInfoResult.get());
            if (shareinfo != null) {
                resultBuilder.setShareinfo(shareinfo);
            }

            return SeriesCompareResponse
                    .newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("success")
                    .setResult(resultBuilder).build();
        } catch (Exception e) {
            log.error("getSeriesCompare error:{}", ExceptionUtils.getStackTrace(e));
            return SeriesCompareResponse.newBuilder().setReturnCode(-1).setReturnMsg("服务端异常").build();
        }
    }

    private SeriesCompareResponse.Result.Shareinfo.Builder getShareInfo(ShareInfoResult shareInfoResult) {
        if (shareInfoResult != null) {
            SeriesCompareResponse.Result.Shareinfo.Builder shareinfo = SeriesCompareResponse.Result.Shareinfo.newBuilder();
            shareinfo.setPosition(170);
            shareinfo.setSharetype(170);
            shareinfo.setText("上汽车之家查看完整对比内容");
            shareinfo.setUrl(shareInfoResult.getShort_url());
            return shareinfo;
        }
        return null;
    }

    /**
     * 购车优惠
     *
     * @param seriesDetails
     * @return
     */
    private Cardlist.Data.Builder getDataForDiscount(int fromtype, List<SeriesDetailDto> seriesDetails, List<SeriesCityAskPriceDto> seriesAskPriceDtos, List<SpecCityAskPriceDto> specAskPriceDtos, List<SeriesSpecDto> seriesSpecDtos, List<SpecCitySubsidyDto> specSubsidyDtos) {
        Cardlist.Data.Builder data = Cardlist.Data.newBuilder();
        data.setBigtitle("购车优惠");
        data.setSubtitle("全面解析价格信息，为您快速了解购车优惠");
        data.setTipstitle("说明");
        data.setTipsdescribe("数据来源于之家采集的当前城市售卖该车系的经销商最低报价，并将车系下全部车型的最低价格整合为价格区间。如所在城市无销售该款车系的经销商，自动为您匹配附近经销商的报价。报价仅供参考，最终售价请以经销商线下售价为准。");
        data.addAllTablelist(getTablelist(fromtype, seriesDetails, seriesAskPriceDtos, specAskPriceDtos, seriesSpecDtos, specSubsidyDtos));
        data.setPvitem(Pvitem.newBuilder()
                .putArgvs("typeid", "1")
                .setClick(Pvitem.Click.newBuilder().build())
                .setShow(Pvitem.Show.newBuilder().setEventid("car_seriespk_content_show").build()));
        return data;
    }

    /**
     * 销量趋势
     *
     * @param seriesDetails
     * @param monthDatas
     * @param weekDatas
     * @return
     */
    private Cardlist.Data.Builder getDataForSale(List<SeriesDetailDto> seriesDetails, List<DateRankResultDto> monthDatas, List<DateRankResultDto> weekDatas) {
        Cardlist.Data.Builder data = Cardlist.Data.newBuilder();
        data.setBigtitle("销量趋势");
        data.setSubtitle("深度分析销量长期走势，帮你选好车");
        data.setTipstitle("说明");
        data.setTipsdescribe("基于乘联会数据，展示了对比车系的销量及排名变化，同时呈现了销售趋势图，助您全面了解市场变化。");
        data.addTablist(getTablist(1, seriesDetails, monthDatas, weekDatas));
        data.addTablist(getTablist(2, seriesDetails, monthDatas, weekDatas));
        data.setPvitem(Pvitem.newBuilder()
                .putArgvs("typeid", "0")
                .setClick(Pvitem.Click.newBuilder().build())
                .setShow(Pvitem.Show.newBuilder().setEventid("car_seriespk_content_show").build()));
        return data;
    }

    private List<Cardlist.Data.Tablelist> getTablelist(int fromtype, List<SeriesDetailDto> seriesDetails, List<SeriesCityAskPriceDto> seriesAskPriceDtos, List<SpecCityAskPriceDto> specAskPriceDtos, List<SeriesSpecDto> seriesSpecDtos, List<SpecCitySubsidyDto> specSubsidyDtos) {
        List<Cardlist.Data.Tablelist.Builder> tablists = new ArrayList<>();

        Cardlist.Data.Tablelist.Builder tablisttitle = Cardlist.Data.Tablelist.newBuilder();
        tablisttitle.setTitle("对比车系");
        tablisttitle.setTitlecolor("#828CA0");
        tablisttitle.setSubtitle("");
        Cardlist.Data.Tablelist.List.Builder list = Cardlist.Data.Tablelist.List.newBuilder();
        list.setName("车型价格");
        list.setBtnname("");
        list.setBtntype(0);
        list.setLinkurl("");
        tablisttitle.addList(list);
        list = Cardlist.Data.Tablelist.List.newBuilder();
        list.setName("优惠金额");
        list.setBtnname("");
        list.setBtntype(0);
        list.setLinkurl("");
        tablisttitle.addList(list);
        list = Cardlist.Data.Tablelist.List.newBuilder();
        list.setName("置换补贴");
        list.setBtnname("");
        list.setBtntype(0);
        list.setLinkurl("");
        tablisttitle.addList(list);
        tablists.add(tablisttitle);

        seriesDetails.forEach(seriesDetail -> {
            Cardlist.Data.Tablelist.Builder tablistdata = Cardlist.Data.Tablelist.newBuilder();
            tablistdata.setSeriesid(seriesDetail.getId());
            tablistdata.setTitle(seriesDetail.getName());
            tablistdata.setTitlecolor("#111E36");
            tablistdata.setSubtitle("暂无报价");
            seriesAskPriceDtos.stream().filter(x -> x.getSeriesId() == seriesDetail.getId()).findFirst().ifPresent(x -> {
                tablistdata.setSubtitle(priceDetailForamt(x.getMinPrice(), x.getMaxPrice()));
            });

            Cardlist.Data.Tablelist.List.Builder list1 = Cardlist.Data.Tablelist.List.newBuilder();
            list1.setName("暂无");
            list1.setBtnname("");
            list1.setBtntype(0);//0是默认值，1就是黑色，2，就是绿色
            list1.setLinkurl("");
            list1.setPvposition("6");
            seriesSpecDtos.stream().filter(x -> x.getSeriesId() == seriesDetail.getId()).findFirst().ifPresent(x -> {
                List<SeriesSpecDto.Item> items = x.getItems().stream().filter(y -> y.getState() == 20 || y.getState() == 30).collect(Collectors.toList());
                if (items != null && items.size() > 0) {
                    List<String> priceInfoList = new ArrayList<>();
                    items.forEach(item -> {
                        String priceInfo = "暂无报价";
                        SpecCityAskPriceDto specAskPriceDto = specAskPriceDtos.stream().filter(askPriceDto -> askPriceDto.getSpecId() == item.getId()).findFirst().orElse(null);
                        if (specAskPriceDto != null) {
                            priceInfo = String.valueOf(specAskPriceDto.getMinPrice());
                        }
                        if (!priceInfoList.contains(priceInfo)) {
                            priceInfoList.add(priceInfo);
                        }
                    });
                    list1.setName(String.format("共%s款车型", items.size()));
                    list1.setBtnname(String.format("%s类价格", priceInfoList.size()));
                    list1.setBtntype(1);
                    list1.setLinkurl(getSchemeForSpecDiscountPage(seriesDetail.getId(), 2, fromtype));
                }
            });
            tablistdata.addList(list1);

            Cardlist.Data.Tablelist.List.Builder list2 = Cardlist.Data.Tablelist.List.newBuilder();
            list2.setName("暂无");
            list2.setBtnname("");
            list2.setBtntype(0);
            list2.setLinkurl("");
            list2.setPvposition("7");
            seriesSpecDtos.stream().filter(x -> x.getSeriesId() == seriesDetail.getId()).findFirst().ifPresent(x -> {
                AtomicInteger count = new AtomicInteger(0);
                List<Integer> prices = new ArrayList<>();
                List<SeriesSpecDto.Item> items = x.getItems().stream().filter(y -> y.getState() == 20 || y.getState() == 30).collect(Collectors.toList());
                items.forEach(item -> {
                    specAskPriceDtos.stream().filter(askPriceDto -> askPriceDto.getSpecId() == item.getId()).findFirst().ifPresent(askPriceDto -> {
                        if (askPriceDto.getMinPrice() < item.getMinPrice()) {
                            count.incrementAndGet();
                            prices.add(item.getMinPrice() - askPriceDto.getMinPrice());
                        }
                    });
                });
                if (count.get() > 0) {
                    list2.setName(String.format("%s款有优惠", count.get()));
                    list2.setBtnname(String.format("%s", priceDetailForamt(Collections.min(prices), Collections.max(prices))));
                    list2.setBtntype(2);
                    list2.setLinkurl(getSchemeForSpecDiscountPage(seriesDetail.getId(), 3, fromtype));
                }
            });
            tablistdata.addList(list2);

            Cardlist.Data.Tablelist.List.Builder list3 = Cardlist.Data.Tablelist.List.newBuilder();
            list3.setName("暂无");
            list3.setBtnname("");
            list3.setBtntype(0);
            list3.setLinkurl("");
            list3.setPvposition("8");
            seriesSpecDtos.stream().filter(x -> x.getSeriesId() == seriesDetail.getId()).findFirst().ifPresent(x -> {
                AtomicInteger count = new AtomicInteger(0);
                List<Integer> prices = new ArrayList<>();
                List<SeriesSpecDto.Item> items = x.getItems().stream().filter(y -> y.getState() == 20 || y.getState() == 30).collect(Collectors.toList());
                items.forEach(item -> {
                    specSubsidyDtos.stream().filter(subsidyDto -> subsidyDto.getSpec_id() == item.getId()).findFirst().ifPresent(subsidyDto -> {
                        if (subsidyDto.getBenefit_sum() != null) {
                            int subsidy = 0;
                            SpecCitySubsidyDto.BenefitSum benefit_sum = subsidyDto.getBenefit_sum();
                            if (benefit_sum.getReplace_this_brand_sum() != null) {
                                subsidy = Math.max(subsidy, benefit_sum.getReplace_this_brand_sum().getSum());
                            }
                            if (benefit_sum.getReplace_other_brand_sum() != null) {
                                subsidy = Math.max(subsidy, benefit_sum.getReplace_other_brand_sum().getSum());
                            }
                            if (benefit_sum.getReplace_all_brand_sum() != null) {
                                subsidy = Math.max(subsidy, benefit_sum.getReplace_all_brand_sum().getSum());
                            }
                            if(subsidy > 0){
                                count.incrementAndGet();
                                prices.add(subsidy);
                            }
                        }
                    });
                });
                if (count.get() > 0) {
                    list3.setName(String.format("%s款有补贴", count.get()));
                    list3.setBtnname(String.format("%s", priceDetailForamt(Collections.min(prices), Collections.max(prices))));
                    list3.setBtntype(2);
                    list3.setLinkurl(getSchemeForSpecDiscountPage(seriesDetail.getId(), 4, fromtype));
                }
            });
            tablistdata.addList(list3);

            tablists.add(tablistdata);
        });

        return tablists.stream().map(x -> x.build()).toList();
    }

    /**
     * 跳转地址
     *
     * @param seriesid 车系id
     * @param anchor   跳转后的锚点
     * @param origin 0:车系对比，1:新车对比（新车PK）。
     * @return
     */
    private String getSchemeForSpecDiscountPage(int seriesid, int anchor, int origin) {
        String scheme = "";
        try {
            String url1 = String.format("rn://CarCPKPageRN/SpecDiscountInfo?panValid=0&seriesid=%s&anchor=%s&origin=%s", seriesid, anchor, origin);
            scheme = String.format("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s", UrlUtil.encode(url1));
        } catch (Exception e) {
            log.error("getSchemeForSpecDiscountPage error:{}", ExceptionUtils.getStackTrace(e));
        }
        return scheme;
    }

    /**
     * @param type          1：销量，2:排名
     * @param seriesDetails
     * @param monthDatas
     * @param weekDatas
     * @return
     */
    private Cardlist.Data.Tablist.Builder getTablist(int type, List<SeriesDetailDto> seriesDetails, List<DateRankResultDto> monthDatas, List<DateRankResultDto> weekDatas) {
        Cardlist.Data.Tablist.Builder tablist = Cardlist.Data.Tablist.newBuilder();
        tablist.setTabid(type);
        tablist.setTabtitle(type == 1 ? "销量" : type == 2 ? "排名" : "");
        tablist.addTimelist(getTimelistMonth(type, seriesDetails, monthDatas));
        tablist.addTimelist(getTimelistWeek(type, seriesDetails, weekDatas));
        return tablist;
    }

    private Cardlist.Data.Tablist.Timelist.Builder getTimelistMonth(int type, List<SeriesDetailDto> seriesDetails, List<DateRankResultDto> monthDatas) {
        Cardlist.Data.Tablist.Timelist.Builder timelist = Cardlist.Data.Tablist.Timelist.newBuilder();
        AtomicInteger index = new AtomicInteger();
        AtomicInteger maxNum = new AtomicInteger(0);
        monthDatas.stream().forEach(monthData -> {
            String date = monthData.getDate();
            RankResultDto value = monthData.getRankResultDto();
            String[] dates = date.split("=");
            String beginDate = dates[0];
            String endDate = dates[1];
            String month = formatDate2(beginDate);
            Cardlist.Data.Tablist.Timelist.Xlist.Builder xlist = Cardlist.Data.Tablist.Timelist.Xlist.newBuilder();
            xlist.setTitle(month + "月");
            xlist.setIsshow(index.get() > 0 && index.get() % 2 == 0 ? 1 : 0);
            timelist.addXlist(xlist);
            index.getAndIncrement();
        });

        List<String> colors = Arrays.asList("#25C9FF", "#0088FF", "#FF6600");
        AtomicInteger specIndex = new AtomicInteger(0);
        seriesDetails.forEach(seriesDetail -> {
            Cardlist.Data.Tablist.Timelist.Pklist.Builder pklist = Cardlist.Data.Tablist.Timelist.Pklist.newBuilder();
            pklist.setSeriesname(seriesDetail.getName());
            pklist.setThemecolor(specIndex.get() < colors.size() ? colors.get(specIndex.get()) : "#FF6600");
            pklist.setComparename("对比车系");

            AtomicReference<String> lastmonth = new AtomicReference<>("");
            AtomicInteger lastNum = new AtomicInteger(0);
            monthDatas.stream().forEach(monthData -> {
                String date = monthData.getDate();
                RankResultDto value = monthData.getRankResultDto();
                String[] dates = date.split("=");
                String beginDate = dates[0];
                String endDate = dates[1];
                String month = formatDate2(beginDate);

                //本月 销量/排名
                AtomicInteger currNum = new AtomicInteger(0);
                if (value != null && value.getResult() != null && value.getResult().getList() != null) {
                    value.getResult().getList().stream().filter(x -> NumberUtils.toInt(x.getSeriesid()) == seriesDetail.getId()).findFirst().ifPresent(x -> {
                        if (type == 1) {
                            currNum.set(x.getSalecount() != null ? x.getSalecount().intValue() : 0);
                        } else if (type == 2) {
                            currNum.set(x.getRnNum());
                        }
                    });
                }

                //与上月 销量/排名 的差值
                long diffNum = 0;
                if (lastNum.get() > 0 && currNum.get() > 0) {
                    diffNum = currNum.get() - lastNum.get();
                }
                lastNum.set(currNum.get());

                //最大的 销量/排名
                if (maxNum.get() < currNum.get()) {
                    maxNum.set(currNum.get());
                }

                String unit = "";
                String title = "";
                String reultValue = "";
                String reultColor = "";
                String resultTitle = "";
                if (StringUtils.isNotEmpty(lastmonth.get()) && StringUtils.isNotEmpty(month)) {
                    resultTitle = month + "月比" + lastmonth + "月";
                }
                lastmonth.set(month);
                if (type == 1) {
                    unit = "月销量";
                    title = currNum.get() > 0 ? String.valueOf(currNum.get()) : "暂无数据";
                    reultValue = diffNum == 0 ? "-" : diffNum > 0 ? String.format("+%d", diffNum) : String.format("-%d", Math.abs(diffNum));
                    reultColor = diffNum == 0 ? "#828CA0" : diffNum > 0 ? "#FF6600" : "#1CCD99";
                } else if (type == 2) {
                    unit = "月排名";
                    title = currNum.get() > 0 ? String.format("第%s名", currNum.get()) : "暂无数据";
                    reultValue = diffNum == 0 ? "-" : diffNum > 0 ? String.format("降%d名", diffNum) : String.format("升%d名", Math.abs(diffNum));
                    reultColor = diffNum == 0 ? "#828CA0" : diffNum > 0 ? "#1CCD99" : "#FF6600";
                }

                Cardlist.Data.Tablist.Timelist.Pklist.Chartlist.Builder chartlist = Cardlist.Data.Tablist.Timelist.Pklist.Chartlist.newBuilder();
                chartlist.setHeadertitle(month + unit);
                chartlist.setTitle(title);
                chartlist.setTitlevalue(currNum.get());
                chartlist.setResulttitle(resultTitle);
                chartlist.setReultvalue(reultValue);
                chartlist.setReultcolor(reultColor);

                pklist.addChartlist(chartlist);
            });

            timelist.addPklist(pklist);

            specIndex.getAndIncrement();
        });

        timelist.setTitle("月");
        if (type == 1) {
            timelist.addAllYlist(getSaleRange(maxNum.get()));
        } else if (type == 2) {
            timelist.addAllYlist(getRankRange(maxNum.get()));
        }
        return timelist;
    }

    private Cardlist.Data.Tablist.Timelist.Builder getTimelistWeek(int type, List<SeriesDetailDto> seriesDetails, List<DateRankResultDto> weekDatas) {
        Cardlist.Data.Tablist.Timelist.Builder timelist = Cardlist.Data.Tablist.Timelist.newBuilder();
        AtomicInteger atomicIndex = new AtomicInteger(0);
        AtomicInteger maxNum = new AtomicInteger(0);
        weekDatas.stream().forEach(weekData -> {
            String date = weekData.getDate();
            RankResultDto value = weekData.getRankResultDto();
            String[] dates = date.split("=");//格式2024-05-06=2024-05-12
            String beginDate = formatDate1(dates[0]);
            String endDate = formatDate1(dates[1]);
            Cardlist.Data.Tablist.Timelist.Xlist.Builder xlist = Cardlist.Data.Tablist.Timelist.Xlist.newBuilder();
            xlist.setTitle(String.format("%s\n-\n%s", beginDate, endDate));//输出格式：2/1-2/4\n-\n2/10
            xlist.setIsshow(atomicIndex.get() > 0 && atomicIndex.get() % 2 == 0 ? 1 : 0);
            timelist.addXlist(xlist);
            atomicIndex.getAndIncrement();
        });

        List<String> colors = Arrays.asList("#25C9FF", "#0088FF", "#FF6600");
        AtomicInteger specIndex = new AtomicInteger(0);
        seriesDetails.forEach(seriesDetail -> {
            Cardlist.Data.Tablist.Timelist.Pklist.Builder pklist = Cardlist.Data.Tablist.Timelist.Pklist.newBuilder();
            pklist.setSeriesname(seriesDetail.getName());
            pklist.setThemecolor(specIndex.get() < colors.size() ? colors.get(specIndex.get()) : "#FF6600");
            pklist.setComparename("对比车系");

            AtomicLong lastNum = new AtomicLong(0);
            weekDatas.stream().forEach(weekData -> {
                String date = weekData.getDate();
                RankResultDto value = weekData.getRankResultDto();
                String[] dates = date.split("=");//格式2024-05-06=2024-05-12
                String beginDate = formatDate1(dates[0]);
                String endDate = formatDate1(dates[1]);

                //本周销量
                AtomicInteger currNum = new AtomicInteger(0);
                if (value != null && value.getResult() != null && value.getResult().getList() != null) {
                    value.getResult().getList().stream().filter(x -> NumberUtils.toInt(x.getSeriesid()) == seriesDetail.getId()).findFirst().ifPresent(x -> {
                        if (type == 1) {
                            currNum.set(x.getSalecount() != null ? x.getSalecount().intValue() : 0);
                        } else if (type == 2) {
                            currNum.set(x.getRnNum());
                        }
                    });
                }

                //与上周销量的差值
                long diffNum = 0;
                if (lastNum.get() > 0 && currNum.get() > 0) {
                    diffNum = currNum.get() - lastNum.get();
                }
                lastNum.set(currNum.get());

                //最大的 销量/排名
                if (maxNum.get() < currNum.get()) {
                    maxNum.set(currNum.get());
                }

                String unit = "";
                String title = "";
                String reultValue = "";
                String reultColor = "";
                if (type == 1) {
                    unit = "\n销量";
                    title = currNum.get() > 0 ? String.valueOf(currNum.get()) : "暂无数据";
                    reultValue = diffNum == 0 ? "-" : diffNum > 0 ? String.format("+%d", diffNum) : String.format("-%d", Math.abs(diffNum));
                    reultColor = diffNum == 0 ? "#828CA0" : diffNum > 0 ? "#FF6600" : "#1CCD99";
                } else if (type == 2) {
                    unit = "\n排名";
                    title = currNum.get() > 0 ? String.format("第%s名", currNum.get()) : "暂无数据";
                    reultValue = diffNum == 0 ? "-" : diffNum > 0 ? String.format("降%d名", diffNum) : String.format("升%d名", Math.abs(diffNum));
                    reultColor = diffNum == 0 ? "#828CA0" : diffNum > 0 ? "#1CCD99" : "#FF6600";
                }

                Cardlist.Data.Tablist.Timelist.Pklist.Chartlist.Builder chartlist = Cardlist.Data.Tablist.Timelist.Pklist.Chartlist.newBuilder();
                chartlist.setHeadertitle(String.format("%s-%s", beginDate, endDate) + unit);
                chartlist.setTitle(title);
                chartlist.setTitlevalue(currNum.get());
                chartlist.setResulttitle("比上周");
                chartlist.setReultvalue(reultValue);
                chartlist.setReultcolor(reultColor);

                pklist.addChartlist(chartlist);
            });

            timelist.addPklist(pklist);

            specIndex.getAndIncrement();
        });

        timelist.setTitle("周");
        if (type == 1) {
            timelist.addAllYlist(getSaleRange(maxNum.get()));
        } else if (type == 2) {
            timelist.addAllYlist(getRankRange(maxNum.get()));
        }

        return timelist;
    }

    /**
     * 车系下车型购车优惠
     *
     * @param request
     * @return
     */
    public GetBuyCarDiscountInfoResponse getBuyCarDiscountInfo(GetBuyCarDiscountInfoRequest request) {
        try {
            int cityId = request.getCityid();
            int seriesId = request.getSeriesid();
            if (seriesId <= 0) {
                return GetBuyCarDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("参数错误").build();
            }

            List<SeriesSpecDto.Item> specItems = new ArrayList<>();
            seriesSpecComponent.getAsync(seriesId).thenAccept(x -> {
                if (Objects.nonNull(x)) {
                    List<SeriesSpecDto.Item> items = x.getItems().stream().filter(y -> y.getState() == 20 || y.getState() == 30).collect(Collectors.toList());
                    specItems.addAll(items);
                }
            }).join();
            if (CollectionUtils.isEmpty(specItems)) {
                return GetBuyCarDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("没有在售车型").build();
            }

            List<CompletableFuture> task = new ArrayList<>();
            List<Integer> specIds = specItems.stream().map(x -> x.getId()).collect(Collectors.toList());
            List<SpecDetailDto> specDetails = new ArrayList<>();
            List<SpecCityAskPriceDto> specAskPriceDtos = new ArrayList<>();
            List<SpecCitySubsidyDto> specSubsidyDtos = new ArrayList<>();
            task.add(specDetailComponent.getList(specIds).thenAccept(x -> {
                if (!CollectionUtils.isEmpty(x)) {
                    x.removeIf(Objects::isNull);
                    specDetails.addAll(x);
                }
            }));
            task.add(specCityAskPriceComponent.get(specIds, cityId).thenAccept(x -> {
                if (!CollectionUtils.isEmpty(x)) {
                    x.removeIf(Objects::isNull);
                    specAskPriceDtos.addAll(x);
                }
            }));
            task.add(specCitySubsidyComponent.getList(specIds, cityId).thenAccept(x -> {
                if (!CollectionUtils.isEmpty(x)) {
                    x.removeIf(Objects::isNull);
                    specSubsidyDtos.addAll(x);
                }
            }));
            CompletableFuture.allOf(task.toArray(new CompletableFuture[task.size()])).join();

            GetBuyCarDiscountInfoResponse.Result.Builder result = GetBuyCarDiscountInfoResponse.Result.newBuilder();
            result.setTitle("车型优惠信息");
            result.addAllTabletitle(Arrays.asList("报价", "车型信息", "指导价", "优惠金额", "置换补贴"));
            List<GetBuyCarDiscountInfoResponse.Result.Content> contentList = new ArrayList<>();
            specDetails.forEach(specDetail -> {
                GetBuyCarDiscountInfoResponse.Result.Content.Builder content = GetBuyCarDiscountInfoResponse.Result.Content.newBuilder();
                content.setSeriesid(specDetail.getSeriesId());
                content.setSpecid(specDetail.getSpecId());
                content.setName(specDetail.getSpecName());
                content.setGuideprice(priceDetailForamt(specDetail.getMinPrice()));
                content.setLinkurl(String.format("autohome://car/specmain?specid=%s&seriesid=%s", specDetail.getSpecId(), specDetail.getSeriesId()));
                content.setPrice("暂无报价");
                content.setDiscount("暂无");
                content.setSubsidy("暂无");
                specAskPriceDtos.stream().filter(x -> x.getSpecId() == specDetail.getSpecId()).findFirst().ifPresent(x -> {
                    if (x.getMinPrice() > 0) {
                        content.setPrice(priceDetailForamt(x.getMinPrice()));
                    }
                    int discount = specDetail.getMinPrice() - x.getMinPrice();
                    if (discount > 0) {
                        content.setDiscount(priceDetailForamt(discount));
                    }
                });
                specSubsidyDtos.stream().filter(subsidyDto -> subsidyDto.getSpec_id() == specDetail.getSpecId()).findFirst().ifPresent(subsidyDto -> {
                    if (subsidyDto.getBenefit_sum() != null) {
                        int subsidy = 0;
                        SpecCitySubsidyDto.BenefitSum benefit_sum = subsidyDto.getBenefit_sum();
                        if (benefit_sum.getReplace_this_brand_sum() != null) {
                            subsidy = Math.max(subsidy, benefit_sum.getReplace_this_brand_sum().getSum());
                        }
                        if (benefit_sum.getReplace_other_brand_sum() != null) {
                            subsidy = Math.max(subsidy, benefit_sum.getReplace_other_brand_sum().getSum());
                        }
                        if (benefit_sum.getReplace_all_brand_sum() != null) {
                            subsidy = Math.max(subsidy, benefit_sum.getReplace_all_brand_sum().getSum());
                        }
                        if(subsidy > 0){
                            content.setSubsidy(priceDetailForamt(subsidy));
                        }
                    }
                });
                contentList.add(content.build());
            });

            //排序
            contentList.sort(Comparator.comparing(GetBuyCarDiscountInfoResponse.Result.Content::getPrice).thenComparing(GetBuyCarDiscountInfoResponse.Result.Content::getName, Comparator.reverseOrder()));
            result.addAllContent(contentList);

            return GetBuyCarDiscountInfoResponse
                    .newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("成功")
                    .setResult(result)
                    .build();
        } catch (Exception e) {
            logger.error("getBuyCarDiscountInfo error:{}", ExceptionUtils.getStackTrace(e));
            return GetBuyCarDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("服务端异常").build();
        }
    }

    /**
     * 格式化日期，返回月日
     * @param date 格式2024-05-06
     * @return
     */
    private String formatDate1(String date){
        String result = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtil.parse(date, "yyyy-MM-dd"));
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            result = String.format("%s/%s", month + 1, day);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 格式化日期，返回月份
     * @param date 格式2024-05
     * @return
     */
    private String formatDate2(String date){
        String result = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtil.parse(date, "yyyy-MM"));
            int month = calendar.get(Calendar.MONTH);
            result = String.format("%s", month + 1);
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 取销量范围
     *
     * @param maxSale
     * @return
     */
    private List<Integer> getSaleRange(int maxSale) {
        List<Integer> rangeList = new ArrayList<>();
        int range = 0;
        int avg = maxSale / 7;
        if (avg < 100) {
            range = ((avg / 10) + 1) * 10;
        } else if (avg < 1000) {
            range = ((avg / 100) + 1) * 100;
        } else {
            range = ((avg / 1000) + 1) * 1000;
        }
        for (int i = 0; i < 8; i++) {
            rangeList.add(range * i);
        }
        return rangeList;
    }

    /**
     * 取排名范围
     *
     * @param maxRank
     * @return
     */
    private List<Integer> getRankRange(int maxRank) {
        List<Integer> rangeList = new ArrayList<>();
        int range = 0;
        int avg = maxRank / 7;
        if (avg < 10) {
            range = avg + 1;
        } else if (avg < 100) {
            range = ((avg / 10) + 1) * 10;
        } else {
            range = ((avg / 100) + 1) * 100;
        }
        //正常情况是添加7个数据，第8个数据是为客户端强制添加的，客户端针对最后一个最大数据做特殊处理（处理成了“无”）
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                rangeList.add(1);
            } else {
                if (range == 1) {
                    rangeList.add(range * i + 1);
                } else {
                    rangeList.add(range * i);
                }
            }
        }
        Collections.reverse(rangeList);
        return rangeList;
    }

    /**
     * 车系的购车优惠
     *
     * @param request
     * @return
     */
    public GetSeriesDiscountInfoResponse getSeriesDiscountInfo(GetSeriesDiscountInfoRequest request) {
        try {
            int cityId = request.getCityid();
            String seriesIdStr = request.getSeriesids();
            if (StringUtils.isEmpty(seriesIdStr)) {
                return GetSeriesDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("参数错误").build();
            }
            String[] ids = seriesIdStr.split(",");
            List<Integer> seriesIds = Arrays.asList(ids).stream().map(x -> NumberUtils.toInt(x)).filter(x -> Objects.nonNull(x) && x > 0).toList();
            if (CollectionUtils.isEmpty(seriesIds)) {
                return GetSeriesDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("参数错误").build();
            }

            List<SeriesDetailDto> seriesDetails = new ArrayList<>();
            List<SeriesSpecDto> seriesSpecDtos = new ArrayList<>();
            List<SeriesCityAskPriceDto> seriesAskPriceDtos = new ArrayList<>();
            List<SpecCityAskPriceDto> specAskPriceDtos = new ArrayList<>();
            List<SpecCitySubsidyDto> specSubsidyDtos = new ArrayList<>();

            List<CompletableFuture> tasks = new ArrayList<>();
            List<CompletableFuture> tasks2 = new ArrayList<>();
            //取车系数据
            tasks.add(seriesDetailComponent.getList(seriesIds).thenAccept(result -> {
                if (result != null && result.size() > 0) {
                    result.removeIf(Objects::isNull);
                    seriesDetails.addAll(result);
                }
            }).exceptionally(e -> {
                log.error("seriesDetailComponent.getList error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            //取经销商车系价格数据
            tasks.add(seriesCityAskPriceNewComponent.get(seriesIds, cityId).thenAccept(result -> {
                if (result != null) {
                    result.removeIf(Objects::isNull);
                    seriesAskPriceDtos.addAll(result);
                }
            }).exceptionally(e -> {
                log.error("seriesCityAskPriceNewComponent.get error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            //取车系的车型数据
            tasks.add(seriesSpecComponent.get(seriesIds).thenAccept(result -> {
                if (result != null && result.size() > 0) {
                    result.removeIf(Objects::isNull);
                    result.forEach(series -> {
                        series.getItems().removeIf(item -> item.getState() != 20 && item.getState() != 30);
                        List<Integer> specids = series.getItems().stream().map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(specids)) {
                            tasks2.add(specCityAskPriceComponent.get(specids, cityId).thenAccept(result2 -> {
                                if (result2 != null && result2.size() > 0) {
                                    result2.removeIf(Objects::isNull);
                                    specAskPriceDtos.addAll(result2);
                                }
                            }));
                            tasks2.add(specCitySubsidyComponent.getList(specids, cityId).thenAccept(result2 -> {
                                if (result2 != null && result2.size() > 0) {
                                    result2.removeIf(Objects::isNull);
                                    specSubsidyDtos.addAll(result2);
                                }
                            }));
                        }
                    });
                    seriesSpecDtos.addAll(result);
                    CompletableFuture.allOf(tasks2.toArray(new CompletableFuture[tasks2.size()])).join();
                }
            }).exceptionally(e -> {
                log.error("seriesSpecComponent.get error:", ExceptionUtils.getStackTrace(e));
                return null;
            }));

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

            if (CollectionUtils.isEmpty(seriesDetails)) {
                return GetSeriesDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("车系数据获取失败").build();
            }

            Cardlist.Data.Builder dataBuilder = getDataForDiscount(request.getFromtype(), seriesDetails, seriesAskPriceDtos, specAskPriceDtos, seriesSpecDtos, specSubsidyDtos);

            return GetSeriesDiscountInfoResponse
                    .newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("success")
                    .setResult(dataBuilder)
                    .build();
        } catch (Exception e) {
            logger.error("getSeriesDiscountInfo error:{}", ExceptionUtils.getStackTrace(e));
            return GetSeriesDiscountInfoResponse.newBuilder().setReturnCode(-1).setReturnMsg("服务端异常").build();
        }
    }

    private static String priceDetailForamt(int price) {
        String priceInfo = "暂无报价";
        try {
            if (price > 0) {
                String formatInfo = String.format("%.2f", Double.valueOf(price / 10000.0));
                priceInfo = formatInfo + "万";
            } else {
                priceInfo = "暂无报价";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return priceInfo;
    }

    private static String priceDetailForamt(int minPrice, int maxPrice) {
        String priceInfo = "暂无报价";
        if (minPrice > 0) {
            priceInfo = priceDetailForamt(minPrice);
        }
        if (maxPrice > 0 && maxPrice != minPrice) {
            priceInfo = priceInfo.replace("万", "") + "-" + priceDetailForamt(maxPrice);
        }
        return priceInfo;
    }

}
