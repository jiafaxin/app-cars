package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carext.SeriesMvpRequest;
import autohome.rpc.car.app_cars.v1.carext.SeriesMvpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.apiclient.abtest.AbApiClient;
import com.autohome.app.cars.apiclient.abtest.dtos.ABTestDto;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.IMApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.CpsEditionDealersResult;
import com.autohome.app.cars.apiclient.dealer.dtos.CpsProductInfoResult;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerIMResult;
import com.autohome.app.cars.apiclient.dealer.dtos.DealerSpecPriceListResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.common.AbTestUtil;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecConfigBagDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecGroupOfSeriesDto;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.dealer.DealerSpecPriceComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.services.dtos.SeriesMvpConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeriesMvpService {

    @Resource
    DealerApiClient dealerApiClient;

    @Resource
    IMApiClient imApiClient;
    @Resource
    private AbApiClient abApiClient;
    @Autowired
    SpecYearNewComponent specYearComponent;
    @Autowired
    SeriesDetailComponent seriesDetailComponent;
    @Autowired
    private SpecOutInnerColorComponent specOutInnerColorComponent;
    @Autowired
    private SpecConfigBagComponent specConfigBagComponent;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private DealerSpecPriceComponent dealerSpecPriceComponent;
    @Autowired
    SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SeriesMvpConfig seriesMvpConfig;

    public CompletableFuture<SeriesMvpResponse.Result.Builder> getSeriesMvpInfo(int from, Integer cityid, Integer seriesid, Integer specid, String deviceid, String abtestVersion, SeriesMvpRequest request) {
        int info_specid;
        int info_seriesid;
        String abtest;
        SeriesMvpResponse.Result.Builder result = SeriesMvpResponse.Result.newBuilder();
        AtomicReference<String> seriesName = new AtomicReference<>("");
        AtomicInteger brandid = new AtomicInteger(0);
        if (seriesid == null || seriesid == 0) {
            SpecDetailDto specDetail = specDetailComponent.getSync(specid);
            if (Objects.nonNull(specDetail)) {
                info_seriesid = specDetail.getSeriesId();
                seriesName.set(specDetail.getSeriesName());
                brandid.set(specDetail.getBrandId());
            } else {
                return CompletableFuture.completedFuture(null);
            }
        } else {
            info_seriesid = seriesid;
        }
        List<CompletableFuture> taskList = new ArrayList<>();
        CompletableFuture<BaseModel<CpsProductInfoResult>> cpsProductInfoFuture = dealerApiClient.getCpsProductInfo(cityid, 167, info_seriesid, StringUtils.isEmpty(deviceid) ? "" : deviceid);
        taskList.add(cpsProductInfoFuture);
        CompletableFuture<ABTestDto> abTestFuture = null;
        if (StringUtils.isEmpty(abtestVersion)) {
            abTestFuture = abApiClient.getABTest("101784", StringUtils.isEmpty(deviceid) ? "" : deviceid);
            taskList.add(abTestFuture);
        }
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[taskList.size()])).join();
        BaseModel<CpsProductInfoResult> cpsProductInfo = cpsProductInfoFuture.join();
        if (StringUtils.isEmpty(abtestVersion) && Objects.nonNull(abTestFuture)) {
            abtest = AbTestUtil.getAbtestVersion(abTestFuture.join(), "101784");
        } else {
            abtest = abtestVersion;
        }
        if ("A".equals(abtest) && (Objects.isNull(cpsProductInfo) || Objects.isNull(cpsProductInfo.getResult()))) {
            return CompletableFuture.completedFuture(null);
        }
        AtomicReference<SpecCityAskPriceDto> specAskPriceDtoRef = new AtomicReference<>();
        if (from == 1) {
            List<SpecGroupOfSeriesDto> specGroupOfSeriesDtos = specYearComponent.get(info_seriesid);
            if (Objects.isNull(specGroupOfSeriesDtos)) {
                return CompletableFuture.completedFuture(null);
            }
            Optional<SpecGroupOfSeriesDto> yearOpt = specGroupOfSeriesDtos.stream().filter(item -> "在售".equals(item.getYearname())).findFirst();
            List<SpecGroupOfSeriesDto.Spec> list = new ArrayList<>();
            if (yearOpt.isPresent()) {
                yearOpt.get().getYearspeclist().forEach(year -> list.addAll(year.getSpeclist().stream().filter(spec -> spec.getParamIsShow() == 1).collect(Collectors.toList())));
            }
            if (list.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            List<Integer> specIds = list.stream().map(i -> i.getSpecId()).collect(Collectors.toList());
            CompletableFuture<List<SpecDetailDto>> specDetailFuture = specDetailComponent.getList(specIds);
            CompletableFuture<List<SpecCityAskPriceDto>> askPriceFuture = specCityAskPriceComponent.get(specIds, cityid);
            CompletableFuture<SeriesDetailDto> seriesFuture = seriesDetailComponent.getAsync(info_seriesid);
            AtomicInteger temp_specid = new AtomicInteger();
            CompletableFuture.allOf(specDetailFuture, askPriceFuture, seriesFuture).thenAccept(x -> {
                List<SpecDetailDto> specDtoList = specDetailFuture.join();
                List<SpecCityAskPriceDto> askPriceList = askPriceFuture.join();
                SeriesDetailDto seriesDetail = seriesFuture.join();
                if (Objects.nonNull(seriesDetail)) {
                    seriesName.set(seriesDetail.getName());
                    brandid.set(seriesDetail.getBrandId());
                }
                specDtoList.removeIf(Objects::isNull);
                askPriceList.removeIf(Objects::isNull);
                Map<Integer, SpecDetailDto> specDtoMap = specDtoList.stream().collect(Collectors.toMap(SpecDetailDto::getSpecId, Function.identity(), (key1, key2) -> key2));
                Map<Integer, SpecCityAskPriceDto> askPriceDtoMap = askPriceList.stream().collect(Collectors.toMap(SpecCityAskPriceDto::getSpecId, Function.identity(), (key1, key2) -> key2));
                list.forEach(spec -> {
                    SpecDetailDto specDto = specDtoMap.get(spec.getSpecId());
                    SpecCityAskPriceDto specCityAskPriceDto = askPriceDtoMap.get(spec.getSpecId());
                    if (Objects.isNull(specDto) || Objects.isNull(specCityAskPriceDto) || specCityAskPriceDto.getMinPrice() == 0) {
                        return;
                    }
                    if (Objects.isNull(specAskPriceDtoRef.get())) {
                        specAskPriceDtoRef.set(specCityAskPriceDto);
                    }
                    SeriesMvpResponse.Result.SpecList.Builder specList = SeriesMvpResponse.Result.SpecList.newBuilder();
                    specList.setSpecid(spec.getSpecId());
                    specList.setSpecname(specDto.getSpecName());
                    specList.setPrice(PriceUtil.getPriceInfoNoDefult(specDto.getMinPrice()));
                    specList.setPricetext("指导价：");
                    SeriesMvpResponse.Result.Pvitem.Builder specPv = SeriesMvpResponse.Result.Pvitem.newBuilder()
                            .putArgvs("seriesid", info_seriesid + "")
                            .putArgvs("specid", spec.getSpecId() + "")
                            .putArgvs("cityid", cityid + "")
                            .setClick(SeriesMvpResponse.Result.Pvitem.Click.newBuilder().setEventid("car_series_optional_spec_click"))
                            .setShow(SeriesMvpResponse.Result.Pvitem.Show.newBuilder().setEventid("car_series_optional_spec_show"));
                    specList.setPvitem(specPv);
                    result.addSpeclist(specList);
                });

                temp_specid.set(list.get(0).getSpecId());
                result.setBigtitle("立即购买");
            }).join();
            info_specid = temp_specid.get();
        } else {
            info_specid = specid;
            result.setSpecid(info_specid);
            SpecDetailDto specDetail = specDetailComponent.getSync(specid);
            if (Objects.nonNull(specDetail)) {
                brandid.set(specDetail.getBrandId());
            }
            specAskPriceDtoRef.set(specCityAskPriceComponent.get(info_specid, cityid).join());
        }
        int skuid;
        if (Objects.nonNull(cpsProductInfo) && Objects.nonNull(cpsProductInfo.getResult())) {
            skuid = cpsProductInfo.getResult().getSkuId();
        } else {
            skuid = 0;
        }
        result.setFirstshowsubsidycount(10);
        SeriesMvpResponse.Result.TipsInfo.Builder tipsInfo = SeriesMvpResponse.Result.TipsInfo.newBuilder();
        tipsInfo.setTipstitle("之家补贴规则");
        tipsInfo.setTipstext("超级补贴是在汽车之家平台专享的购车补贴（包括但不限于车价直减补贴、优惠权益等），发放内容详情见订单。同一个用户同款车在有限期内仅可享受一次优惠。不可与其他优惠叠加使用，不找零，不折现。仅在本店享受，请在到店购车前出示。由于部分补贴名额限量，具体以实际发放结果为准。");
        result.setTipsinfo(tipsInfo);
        SeriesMvpResponse.Result.BottomInfo.Builder bottomInfo = SeriesMvpResponse.Result.BottomInfo.newBuilder();
        bottomInfo.setBottomopen("展开更多优惠");
        bottomInfo.setBottomclose("收起更多优惠");
        SeriesMvpResponse.Result.Pvitem.Builder bottomPv = SeriesMvpResponse.Result.Pvitem.newBuilder()
                .putArgvs("seriesid", info_seriesid + "")
                .putArgvs("specid", info_specid + "")
                .putArgvs("cityid", cityid + "")
                .setClick(SeriesMvpResponse.Result.Pvitem.Click.newBuilder().setEventid("car_series_optional_more_click"));
        bottomInfo.setPvitem(bottomPv);
        result.setBottominfo(bottomInfo);
        CompletableFuture<SpecOutInnerColorDto> outerColorFuture = specOutInnerColorComponent.get(info_specid, false);
        CompletableFuture<SpecOutInnerColorDto> innerColorFuture = specOutInnerColorComponent.get(info_specid, true);
        CompletableFuture<SpecConfigBagDto> configBagFuture = specConfigBagComponent.get(info_specid);
        CompletableFuture<BaseModel<List<CpsEditionDealersResult>>> cpsEditionFuture = dealerApiClient.getCpsEditionDealers(cityid, 0, skuid, info_seriesid, info_specid);
        String sendText = "";
        SeriesMvpConfig.TextlistDTO abText = seriesMvpConfig.get().getTextlist().stream().filter(text -> text.getAbversion().equals(abtest)).findFirst().orElse(null);

        if (Objects.nonNull(abText)) {
            sendText = abText.getTextF();
            SeriesMvpConfig.TextBListDTO textB = seriesMvpConfig.get().getTextBList().stream().filter(i -> i.getSeriesid().equals(seriesid)).findFirst().orElse(null);
            if (Objects.nonNull(textB)) {
                abText.setAmount(textB.getAmount());
                abText.setTextB(textB.getText());
            }
        }
        String appversion = "11.67.0";
        if (StringUtils.isNotEmpty(request.getPluginversion())) {
            appversion = request.getPluginversion();
        }
        CompletableFuture<BaseModel<DealerIMResult>> imFuture = imApiClient.getBusinessMvpImEntranceInfo(info_seriesid, info_specid, brandid.get(), cityid, 187, appversion, sendText);
        return CompletableFuture.allOf(outerColorFuture, innerColorFuture, configBagFuture, cpsEditionFuture, cpsProductInfoFuture, imFuture).thenApply(x -> {
            SpecOutInnerColorDto outerColor = outerColorFuture.join();
            SpecOutInnerColorDto innerColor = innerColorFuture.join();
            SpecConfigBagDto configBag = configBagFuture.join();
            BaseModel<List<CpsEditionDealersResult>> cpsList = cpsEditionFuture.join();
            BaseModel<DealerIMResult> imInfo = imFuture.join();
            if ("A".equals(abtest)) {
                if (Objects.nonNull(cpsList) && Objects.nonNull(cpsList.getResult()) && !cpsList.getResult().isEmpty()) {
                    List<CpsEditionDealersResult> dealerList = cpsList.getResult();
                    List<Integer> dealerIds = dealerList.stream().map(i -> i.getDealerId()).collect(Collectors.toList());
                    List<DealerSpecPriceListResult> dealSpecList = dealerSpecPriceComponent.mGet(dealerIds);
                    dealSpecList.removeIf(Objects::isNull);
                    Map<Integer, DealerSpecPriceListResult> dealerSpecMap = new HashMap<>();
                    dealSpecList.forEach(dealer -> {
                        if (dealer.getSpecList().stream().filter(i -> i.getSpecId() == info_specid).findFirst().isPresent()) {
                            dealerSpecMap.put(dealer.getDealerId(), dealer);
                        }
                    });
                    dealerList.forEach(dealer -> {
                        DealerSpecPriceListResult dealerSpecPrice = dealerSpecMap.get(dealer.getDealerId());
                        if (Objects.isNull(dealerSpecPrice)) {
                            return;
                        }
                        result.addSubsidylist(getCard11104(dealer, cpsProductInfo, dealerSpecPrice, info_seriesid, info_specid, cityid, skuid));
                    });
                } else {
                    return null;
                }
                if(result.getSubsidylistBuilderList().isEmpty()){
                    return null;
                }
            }

            if (Objects.nonNull(outerColor) && Objects.nonNull(outerColor.getColoritems()) && !outerColor.getColoritems().isEmpty()) {
                result.addMvplist(getCard11102(outerColor, "外观颜色", "outcolorid", info_seriesid, info_specid, cityid, 1));
            }
            if (Objects.nonNull(innerColor) && Objects.nonNull(innerColor.getColoritems()) && !innerColor.getColoritems().isEmpty()) {
                result.addMvplist(getCard11102(innerColor, "内饰颜色", "innercolorid", info_seriesid, info_specid, cityid, 2));
            }
            if (Objects.nonNull(configBag) && Objects.nonNull(configBag.getConfigbags()) && !configBag.getConfigbags().isEmpty()) {
                List<SpecConfigBagDto.ConfigBagValue> configbags = configBag.getConfigbags().stream().filter(i -> i.getPrice() > 1 && StringUtils.isNotEmpty(i.getImgurl())).collect(Collectors.toList());
                if (!configbags.isEmpty()) {
                    result.addMvplist(getCard11103(configbags, "定制选装", "configbagids", info_seriesid, info_specid, cityid, 2));
                }
            }

            if (!"A".equals(abtest) && Objects.nonNull(abText)) {
                result.setBigtitle(MessageFormat.format(abText.getTextA(), seriesName));
                result.addMvplist(getCard11107(specAskPriceDtoRef.get(), cpsProductInfo, imInfo, abText, info_seriesid, info_specid, cityid, 3));
            }
            return result;
        });

    }

    public SeriesMvpResponse.Result.MvpList.Builder getCard11102(SpecOutInnerColorDto colorDto, String title, String key, Integer seriesid, Integer specid, Integer cityid, int positiontype) {
        SeriesMvpResponse.Result.MvpList.Builder mvpList = SeriesMvpResponse.Result.MvpList.newBuilder();
        mvpList.setType(11102);
        mvpList.setId(11102);
        SeriesMvpResponse.Result.Card11102.Builder card11102 = SeriesMvpResponse.Result.Card11102.newBuilder();
        card11102.setTitle(title);
        card11102.setKey(key);
        card11102.setPositiontype(positiontype);
        card11102.setSubtitle(String.format("共%s款", colorDto.getColoritems().size()));
        colorDto.getColoritems().stream().forEach(color -> {
            SeriesMvpResponse.Result.Card11102.DataList.Builder dataList = SeriesMvpResponse.Result.Card11102.DataList.newBuilder();
            dataList.setDataid(color.getId());
            dataList.setName(color.getName());
            dataList.setColor(color.getValue());
            dataList.setPricevalue(color.getPrice());
            card11102.addDatalist(dataList);
        });
        SeriesMvpResponse.Result.Pvitem.Builder cardPvBuilder = SeriesMvpResponse.Result.Pvitem.newBuilder()
                .putArgvs("seriesid", seriesid + "")
                .putArgvs("specid", specid + "")
                .putArgvs("cityid", cityid + "")
                .putArgvs("type", key)
                .setClick(SeriesMvpResponse.Result.Pvitem.Click.newBuilder().setEventid("car_series_optional_filter_click"))
                .setShow(SeriesMvpResponse.Result.Pvitem.Show.newBuilder().setEventid("car_series_optional_filter_show"));
        card11102.setPvitem(cardPvBuilder);
        mvpList.setCard11102(card11102);
        return mvpList;
    }

    public SeriesMvpResponse.Result.MvpList.Builder getCard11103(List<SpecConfigBagDto.ConfigBagValue> configbags, String title, String key, Integer seriesid, Integer specid, Integer cityid, int positiontype) {
        SeriesMvpResponse.Result.MvpList.Builder mvpList = SeriesMvpResponse.Result.MvpList.newBuilder();
        mvpList.setType(11103);
        mvpList.setId(11103);
        SeriesMvpResponse.Result.Card11103.Builder card11103 = SeriesMvpResponse.Result.Card11103.newBuilder();
        card11103.setTitle(title);
        card11103.setKey(key);
        card11103.setPositiontype(positiontype);
        card11103.setSubtitle(String.format("共%s款", configbags.size()));
        configbags.stream().forEach(item -> {
            SeriesMvpResponse.Result.Card11103.DataList.Builder dataList = SeriesMvpResponse.Result.Card11103.DataList.newBuilder();
            dataList.setDataid(item.getBagid());
            dataList.setName(item.getName());
            dataList.setImageurl("");
            dataList.setPricevalue(item.getPrice());
            if (StringUtils.isNotBlank(item.getImgurl())) {
                String[] split = item.getImgurl().split(",");
                if (split.length > 0) {
                    dataList.setImageurl(ImageUtils.convertImage_Size(CarSettings.getInstance().GetFullImagePath(split[0]), ImageSizeEnum.ImgSize_WxH_300x0));
                }
            }
            card11103.addDatalist(dataList);
        });
        SeriesMvpResponse.Result.Pvitem.Builder cardPvBuilder = SeriesMvpResponse.Result.Pvitem.newBuilder()
                .putArgvs("seriesid", seriesid + "")
                .putArgvs("specid", specid + "")
                .putArgvs("cityid", cityid + "")
                .putArgvs("type", key)
                .setClick(SeriesMvpResponse.Result.Pvitem.Click.newBuilder().setEventid("car_series_optional_filter_click"))
                .setShow(SeriesMvpResponse.Result.Pvitem.Show.newBuilder().setEventid("car_series_optional_filter_show"));
        card11103.setPvitem(cardPvBuilder);
        mvpList.setCard11103(card11103);
        mvpList.setCard11103(card11103);
        return mvpList;
    }

    public SeriesMvpResponse.Result.SubsidyList.Builder getCard11104(CpsEditionDealersResult dealer, BaseModel<CpsProductInfoResult> cpsInfo, DealerSpecPriceListResult dealerSpecPrice, Integer seriesid, Integer specid, Integer cityid, int skuid) {
        SeriesMvpResponse.Result.SubsidyList.Builder subsidyList = SeriesMvpResponse.Result.SubsidyList.newBuilder();
        subsidyList.setType(11104);
        subsidyList.setId(11104);
        SeriesMvpResponse.Result.Card11104.Builder card11104 = SeriesMvpResponse.Result.Card11104.newBuilder();
        Optional<DealerSpecPriceListResult.SpecPrice> specPriceOpt = dealerSpecPrice.getSpecList().stream().filter(j -> j.getSpecId() == specid).findFirst();
        if (specPriceOpt.isPresent()) {
            card11104.setPricevalue(specPriceOpt.get().getPrice());
        } else {
            card11104.setPricevalue(0);
        }
        String btntitle = "锁定优惠";
        if (Objects.nonNull(seriesMvpConfig)) {
            btntitle = seriesMvpConfig.get().getBtnTitle();
        }
        card11104.setTitle("汽车之家超级补贴");
        card11104.setPricename("裸车价");
        card11104.setTagname("赠");
        card11104.setSubsidytitle("之家补贴");
        card11104.setConfigbagname("选装");
        card11104.setLat(dealerSpecPrice.getLatitude().doubleValue() + "");
        card11104.setLon(dealerSpecPrice.getLongitude().doubleValue() + "");
        card11104.setAddress(dealer.getDealerSimpleName());
        card11104.setBtnname(btntitle);
        card11104.setDealerid(dealer.getDealerId());
        String linkurl = String.format("autohome://car/asklowprice?customshowanimationtype=2&eid=%s&isfullscreen=0&seriesid=%s&specid=%s&dealerid=%s&inquirytype=2&ordertype=1&price_show=1002&title=%s",
                UrlUtil.encode("3|1411002|572|0|211801|306901"), seriesid, specid, dealer.getDealerId(), UrlUtil.encode(btntitle));
        card11104.setLinkurl(linkurl);
        SeriesMvpResponse.Result.Card11104.SubsidyInfo.Builder subsidyInfo = SeriesMvpResponse.Result.Card11104.SubsidyInfo.newBuilder();
        String cpsPrice = "0";
        if (Objects.nonNull(cpsInfo)) {
            List<String> adTxtList = cpsInfo.getResult().getAdTxtList();
            if (Objects.nonNull(adTxtList) && adTxtList.size() == 3) {
                subsidyInfo.setPrefix("");
                subsidyInfo.setTitle(adTxtList.get(2));
                subsidyInfo.setUnit("元");
                subsidyInfo.setSuffix(adTxtList.get(1));
                cpsPrice = adTxtList.get(2);
            }
        }

        card11104.setSubsidyinfo(subsidyInfo);
        SeriesMvpResponse.Result.Pvitem.Builder cardPvBuilder = SeriesMvpResponse.Result.Pvitem.newBuilder()
                .putArgvs("seriesid", seriesid + "")
                .putArgvs("specid", specid + "")
                .putArgvs("cityid", cityid + "")
                .putArgvs("dealerid", dealer.getDealerId() + "")
                .putArgvs("cpsprice", cpsPrice)
                .setShow(SeriesMvpResponse.Result.Pvitem.Show.newBuilder().setEventid("car_series_optional_dealer_show"));
        ;
        card11104.setPvitem(cardPvBuilder);
        subsidyList.setCard11104(card11104);
        return subsidyList;
    }

    public SeriesMvpResponse.Result.MvpList.Builder getCard11107(SpecCityAskPriceDto specCityAskPriceDto, BaseModel<CpsProductInfoResult> cpsInfo, BaseModel<DealerIMResult> imInfo, SeriesMvpConfig.TextlistDTO abText, Integer seriesid, Integer specid, Integer cityid, int positiontype) {
        SeriesMvpResponse.Result.MvpList.Builder mvpList = SeriesMvpResponse.Result.MvpList.newBuilder();
        mvpList.setType(11107);
        mvpList.setId(11107);
        String orangetitle = abText.getAmount() != null ? abText.getAmount() : "";
        String subtitle = abText.getTextB();
//        if(Objects.nonNull(cpsInfo) && Objects.nonNull(cpsInfo.getResult())){
//            List<String> adTxtList = cpsInfo.getResult().getAdTxtList();
//            if (Objects.nonNull(adTxtList) && adTxtList.size() == 3) {
//                orangetitle = adTxtList.get(2)+"元";
//                subtitle = adTxtList.get(1)+orangetitle;
//            }
//        }
        SeriesMvpResponse.Result.Card11107.Builder card11107 = SeriesMvpResponse.Result.Card11107.newBuilder();

//                "pricename": "裸车价",
//                "pricevalue": 345000,
//                "positiontype": 3,
//                "orangetitle": "11000元",
//                "subtitle": "限时权益最高减免11000元",
//                "linkurl": "",
//                "eid": "",
//                "ext": "",
//                "btntitle": "提交配置单",
//                "btnsubtitle": "查询最新优惠",

        card11107.setPricename("裸车价");
        card11107.setPricevalue(0);
        if (Objects.nonNull(specCityAskPriceDto)) {
            card11107.setPricevalue(specCityAskPriceDto.getMinPrice());
        }
        card11107.setPositiontype(positiontype);
        card11107.setOrangetitle(orangetitle);
        card11107.setSubtitle(subtitle);
        String eid = "3|1411002|1373|0|211801|306954";

        String linkurl = String.format("autohome://car/asklowprice?customshowanimationtype=2&animation_type=2&flutterPresentType=2&price_show=0&ordertype=1&ext=%s&seriesid=%s&askspecid=%s&eid=%s&title=%s&btntitle=%s",
                UrlUtil.encode("{\"bjdabtest\":1}"), seriesid, specid, UrlUtil.encode(eid), UrlUtil.encode(abText.getTextC()), UrlUtil.encode(abText.getTextE()));
        String imurl = "";
        if (Objects.nonNull(imInfo) && Objects.nonNull(imInfo.getResult())) {
            imurl = imInfo.getResult().getLinkurl();
        }
        card11107.setLinktype(abText.getLinktype());
        if (abText.getLinktype() == 1) {
            card11107.setLinkurl(linkurl);
        } else {
            card11107.setLinkurl(imurl);
            card11107.setEntryid("sourceid__187");
        }
        card11107.setEid(eid);
        JSONObject extObj = new JSONObject();
        extObj.put("clickpos", 1);
        extObj.put("price_show", 0);
        extObj.put("linkurl", card11107.getLinkurl());
        card11107.setExt(extObj.toJSONString());
        card11107.setBtntitle(abText.getTextC());
        card11107.setBtnsubtitle(abText.getTextD());

        SeriesMvpResponse.Result.Pvitem.Builder cardPvBuilder = SeriesMvpResponse.Result.Pvitem.newBuilder()
                .putArgvs("seriesid", seriesid + "")
                .putArgvs("specid", specid + "")
                .putArgvs("cityid", cityid + "")
                .putArgvs("value", subtitle)
                .setClick(SeriesMvpResponse.Result.Pvitem.Click.newBuilder().setEventid("car_series_optional_money_click"))
                .setShow(SeriesMvpResponse.Result.Pvitem.Show.newBuilder().setEventid("car_series_optional_money_show"));
        card11107.setPvitem(cardPvBuilder);
        mvpList.setCard11107(card11107);
        return mvpList;
    }


}
