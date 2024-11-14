package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import autohome.rpc.car.app_cars.v1.carbase.SpecBaseInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.SpecBaseInfoResponse;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.CarPriceChangeComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesTestDataComponent;
import com.autohome.app.cars.service.components.car.SpecPicInfoComponent;
import com.autohome.app.cars.service.components.car.dtos.CarPriceChangeDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesTestDataDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecPicInfoDto;
import com.autohome.app.cars.service.components.che168.SpecCityUsedCarPriceComponent;
import com.autohome.app.cars.service.components.che168.SpecUsedCarComponent;
import com.autohome.app.cars.service.components.che168.dtos.SpecUsedCarInfo;
import com.autohome.app.cars.service.components.club.SeriesClubGroupComponent;
import com.autohome.app.cars.service.components.club.SeriesClubWendaComponent;
import com.autohome.app.cars.service.components.club.SpecClubTabComponent;
import com.autohome.app.cars.service.components.club.dtos.SeriesClubGroupDto;
import com.autohome.app.cars.service.components.club.dtos.SeriesClubWendaDto;
import com.autohome.app.cars.service.components.club.dtos.SpecClubTabDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.jiage.SpecCityOwnerPriceComponent;
import com.autohome.app.cars.service.components.jiage.dtos.SpecCityListOwnerPriceDto;
import com.autohome.app.cars.service.components.koubei.SpecKouBeiComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SpecKoubeiDto;
import com.autohome.app.cars.service.components.sou.CustomizedCarComponent;
import com.autohome.app.cars.service.services.dtos.SeriesConsultConfigDto;
import com.autohome.app.cars.service.services.dtos.SpecBaseTabinfoConfig;
import com.autohome.app.cars.service.services.dtos.SpecPriceListDto;
import com.autohome.app.cars.service.services.enums.ComputerRoom;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 车型综述页
 *
 * @author chengjincheng
 * @date 2024/3/6
 */
@Service
@Slf4j
public class SpecInfoService {

    @Autowired
    private CustomizedCarComponent customizedCarComponent;

    @Autowired
    private SpecPicInfoComponent specPicInfoComponent;

    @Autowired
    private SpecKouBeiComponent specKouBeiComponent;

    @Autowired
    private SpecCityOwnerPriceComponent ownerPriceComponent;

    @Autowired
    private SeriesTestDataComponent seriesTestDataComponent;

    @Autowired
    private SeriesClubGroupComponent seriesClubGroupComponent;

    @Autowired
    private SeriesClubWendaComponent seriesClubWendaComponent;


    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private SpecUsedCarComponent specUsedCarComponent;

    @Autowired
    private SpecCityUsedCarPriceComponent specUsedCarPriceComponent;

    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;


    @Value("#{T(com.autohome.app.cars.service.services.enums.ComputerRoom).convertByValue('${computer_room_status:0}')}")
    private ComputerRoom computerRoom;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.SpecBaseTabinfoConfig).createFromJson('${spec_base_tabinfo_config:}')}")
    private SpecBaseTabinfoConfig specBaseTabinfoConfig;

    @Value("${series_consult_config:}")
    private String seriesConsultConfig;

    @Value("${spec_club_tab_name_config:论坛}")
    private String specClubTabName;

    /**
     * 降价有效时间 单位：天
     */
    @Value("${car_price_reduce:14}")
    private int carPriceReduceValidity;

    public static DecimalFormat df02 = new DecimalFormat("0.00");
    @Autowired
    private SpecClubTabComponent specClubTagComponent;
    @Autowired
    private CarPriceChangeComponent carPriceChangeComponent;

    public CompletableFuture<SpecBaseInfoResponse.Result.Specbaseinfo.Builder> buildSpecBaseInfo
            (SpecDetailDto specDetailDto, CompletableFuture<SpecCityAskPriceDto> specCityAskPriceDtoFuture, SpecBaseInfoRequest request) {
        try {
            List<CompletableFuture> tasks = new ArrayList<>();
            SpecBaseInfoResponse.Result.Specbaseinfo.Builder specBuilder =
                    SpecBaseInfoResponse.Result.Specbaseinfo.newBuilder();
            specBuilder.setBrandid(specDetailDto.getBrandId());
            specBuilder.setBrandname(specDetailDto.getBrandName());
            specBuilder.setBrandlogo(ImageUtils.convertImageUrl(specDetailDto.getBrandLogo(), true, false,
                    false, ImageSizeEnum.ImgSize_1x1_100x100_NO_OPT));
            SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(specDetailDto.getSeriesId());
            if (Objects.nonNull(seriesDetailDto)) {
                specBuilder.setEnergetype(seriesDetailDto.getEnergytype());
            }
            specBuilder.setEnergetypename(specDetailDto.getEnergyTypeName());
            String[] fctPriceInfo = getFctPrice(specDetailDto, request.getSpecid());
            specBuilder.setFctpricename(fctPriceInfo[0]);
            specBuilder.setFctprice(fctPriceInfo[1]);
            specBuilder.setFctpricetipinfo(fctPriceInfo[2]);

            specBuilder.setLevelid(specDetailDto.getLevelId());
            specBuilder.setLevelname(specDetailDto.getLevelName());

            specBuilder.setLogo(ImageUtils.convertImageUrl(specDetailDto.getLogo(), true, false,
                    false, ImageSizeEnum.ImgSize_4x3_800x600_Without_Opt));
            specBuilder.setLogotypeid(101051);
            specBuilder.setParamisshow(specDetailDto.getParamIsShow());

            //需求文档：https://doc.autohome.com.cn/docapi/page/share/share_zQTE7MgKMi
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.68.0") && StringUtils.equalsIgnoreCase(request.getDowntagabtest(), "B")) {
                tasks.add(CompletableFuture.runAsync(() -> {
                    CarPriceChangeDto.CutPriceListDTO cutPriceListDTO = carPriceChangeComponent.getBySpecId(specDetailDto.getSpecId());
                    if (Objects.nonNull(cutPriceListDTO) && cutPriceListDTO.getCutPrice() > 0 && Arrays.asList(40, 50).contains(cutPriceListDTO.getChangeType())) {
                        if (cutPriceListDTO.getChangeType() == 40) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(cutPriceListDTO.getStartTime());
                            calendar.add(Calendar.DAY_OF_MONTH, carPriceReduceValidity);
                            cutPriceListDTO.setEndTime(calendar.getTime());
                        }
                        Date now = new Date();
                        if (now.after(cutPriceListDTO.getStartTime()) && now.before(cutPriceListDTO.getEndTime())) {
                            specBuilder.setFctpricename(cutPriceListDTO.getChangeType() == 40 ? "最新指导价:" : "限时指导价:");
                            specBuilder.setFctprice(CommonHelper.getPriceDetailInfo(cutPriceListDTO.getTargetPrice()));
                        }
                    }
                }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
                    log.warn("查询车系下降价活动错误:", e);
                    return null;
                }));
            }

            tasks.add(specPicInfoComponent.get(specDetailDto.getSpecId()).thenAccept(data -> {
                if (Objects.nonNull(data)) {
                    specBuilder.setPiccount(data.getSpecOrigPicCount());
                }
            }));
            specBuilder.setSalestate(specDetailDto.getSalestate());
            specBuilder.setSeriesid(specDetailDto.getSeriesId());
            specBuilder.setSeriesname(specDetailDto.getSeriesName());
            specBuilder.setSpecid(specDetailDto.getSpecId());
            specBuilder.setSpecname(specDetailDto.getSpecName());
            tasks.add(customizedCarComponent.get(specDetailDto.getSpecId()).thenAccept(data -> {
                if (Objects.isNull(data)) {
                    specBuilder.setSpecnamelink("");
                } else {
                    specBuilder.setSpecnamelink(CommonHelper.getInsideBrowerSchemeWK(
                            String.format("https://ca.mall.autohome.com.cn/detail?itemId=%s&adchannel=6857545&pvareaid=6857545&isIntroduce=1", data.getId())));
                }
            }));
            tasks.add(specCityAskPriceDtoFuture.thenCombineAsync(specUsedCarPriceComponent.get(specDetailDto.getSpecId(),request.getCityid()), (askPriceDto, usedCarDto) -> {
                int canaskprice = 0;
                if (!Objects.isNull(askPriceDto)) {
                    canaskprice = askPriceDto.getMinPrice() > 0 ? 1 : 0;
                }
                specBuilder.setTabdefaluttypeid(19);
                if (specDetailDto.getState() == 40) {
                    //旧版停售车型 经销商id=11
                    if ("0".equals(request.getFuncabtest()) || CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.5")) {
                        specBuilder.setTabdefaluttypeid(11);
                    }
                    if (!Objects.isNull(usedCarDto)) {
                        if (usedCarDto.getMinprice() > 0d) {
                            if (canaskprice == 0 && StringUtils.isNotEmpty(usedCarDto.getSpecurl())) {
                                specBuilder.setTabdefaluttypeid(4);
                            }
                            if (usedCarDto.getMinprice() < usedCarDto.getMaxprice()) {
                                specBuilder.setSscpriceinfo(String.format("%s-%s万", df02.format(usedCarDto.getMinprice()), df02.format(usedCarDto.getMaxprice())));
                            } else {
                                specBuilder.setSscpriceinfo(df02.format(usedCarDto.getMinprice()) + "万起");
                            }
                        } else {
                            specBuilder.setSscpriceinfo("暂无报价");
                        }
                        specBuilder.setSscpricname("二手车价 ");
                    } else {
                        specBuilder.setSscpricname("二手车价 ");
                        specBuilder.setSscpriceinfo("暂无报价");
                        specBuilder.setSsclinkurl("");
                    }
                }
                return null;
            }).exceptionally(e -> {
                log.error("二手车信息异常", e);
                return null;
            }));


            specBuilder.setState(specDetailDto.getState());
            specBuilder.setNewenergyprice("");
            specBuilder.setNewenergypricename("");
            specBuilder.setPvareaid("112850");
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
            return CompletableFuture.completedFuture(specBuilder);
        } catch (Exception e) {
            log.error("createSpecBaseInfoBuilder error", e);
            return CompletableFuture.completedFuture(null);
        }

    }


    private String[] getFctPrice(SpecDetailDto specDetailDto, int specid) {
        String[] fctpriceinfo = {"厂商指导价 ", "暂无报价", ""};
        try {
            int specminprice = specDetailDto.getMinPrice();
            int specmaxprice = specDetailDto.getMaxPrice();
            int specstate = specDetailDto.getState();

            if (StringUtils.isNotBlank(specDetailDto.getPriceDescription())) {
                fctpriceinfo[2] = specDetailDto.getPriceDescription();
            }

            if (specstate == 10 && specDetailDto.isBooked()) {
                fctpriceinfo[0] = "订金 ";
            } else if (specstate == 10) {
                fctpriceinfo[0] = "预售价 ";
            } else {
                fctpriceinfo[0] = "厂商指导价 ";
            }

            if (specminprice > 0) {
                fctpriceinfo[1] = CommonHelper.getPriceDetailInfo(specminprice);
            } else if (specmaxprice > 0) {
                fctpriceinfo[1] = CommonHelper.getPriceDetailInfo(specmaxprice);
            } else {
                fctpriceinfo[0] = "厂商指导价 ";
            }

            if ("厂商指导价 ".equals(fctpriceinfo[0])) {
                fctpriceinfo[0] = "指导价:";
            }
        } catch (Exception ex) {
            String exceptionMsg = String.format("ErrorMessage:%s;RequestParm:specid=%s;  exceptionStack:%s;",
                    "获取车型厂商指导价异常", specid, ExceptionUtils.getStackTrace(ex));
            log.error(exceptionMsg, ex);
        }

        return fctpriceinfo;
    }

    public CompletableFuture<SpecBaseInfoResponse.Result.Specpicinfo.Builder> buildSpecPicInfo
            (SpecDetailDto specDetailDto, SpecBaseInfoRequest request) {
        SpecBaseInfoResponse.Result.Specpicinfo.Builder picInfoBuilder =
                SpecBaseInfoResponse.Result.Specpicinfo.newBuilder();
        try {
            SpecPicInfoDto specPicInfoDto = specPicInfoComponent.get(specDetailDto.getSpecId()).join();
            if(Objects.nonNull(specPicInfoDto)){
                String linkurl = specPicInfoDto.getLinkUrl();
                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                    linkurl += "&sourceid=2";
                }
                picInfoBuilder.setLinkurl(linkurl);
                picInfoBuilder.setPiccount(specPicInfoDto.getPicCount());
                picInfoBuilder.setPicicon(specPicInfoDto.getPicIcon());
                picInfoBuilder.setTip(specPicInfoDto.getTip());
                Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                        .putArgvs("specid", request.getSpecid() + "")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_new_allpic_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_new_allpic_show"));
                picInfoBuilder.setPvitem(pvBuilder);

                specPicInfoDto.getPicList().forEach(pic -> {
                    SpecBaseInfoResponse.Result.Specpicinfo.Piclist.Builder picListBuilder =
                            SpecBaseInfoResponse.Result.Specpicinfo.Piclist.newBuilder();
                    picListBuilder.setIcon(pic.getIcon());
                    picListBuilder.setIsoutvr(pic.getIsOutVr());
                    String picLinkUrl = pic.getLinkUrl();
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                        picLinkUrl += "&sourceid=2";
                    }
                    picListBuilder.setLinkurl(picLinkUrl);
                    picListBuilder.setTag(pic.getTag());
                    picInfoBuilder.addPiclist(picListBuilder.build());
                });

                for (int i = 0; i < specPicInfoDto.getPicList().size(); i++) {
                    SpecBaseInfoResponse.Result.Specpicinfo.Piclist.Builder picListBuilder =
                            picInfoBuilder.getPiclistBuilderList().get(i);
                    Pvitem.Builder picPvBuilder = Pvitem.newBuilder()
                            .putArgvs("specid", request.getSpecid() + "")
                            .putArgvs("index", String.valueOf(i + 1))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_new_pic_click"))
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_new_pic_show"));
                    picListBuilder.setPvitem(picPvBuilder);
                }
            }
        } catch (Exception e) {
            log.error("buildSpecPicInfo error specDetailDto={}",specDetailDto, e);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(picInfoBuilder);
    }

    public CompletableFuture<List<SpecBaseInfoResponse.Result.Pricelist>> buildSpecPriceList
            (SpecDetailDto specDetailDto, SpecBaseInfoRequest request) {
        // 兜底配置
        String jsonStr = "[{\"subtitle\":\"暂无\",\"subtitlecolor\":\"#464E64\",\"typeid\":5,\"linkurl\":\"\",\"title\":\"计算器\"},{\"subtitle\":\"暂无\",\"subtitlecolor\":\"#464E64\",\"typeid\":6,\"linkurl\":\"\",\"title\":\"口碑\"},{\"subtitle\":\"暂无\",\"subtitlecolor\":\"#464E64\",\"typeid\":3,\"linkurl\":\"\",\"title\":\"车主提车价\"},{\"subtitle\":\"暂无\",\"subtitlecolor\":\"#464E64\",\"typeid\":4,\"linkurl\":\"\",\"title\":\"二手车价\"}]";
        List<SpecPriceListDto> specPriceListDtoList = JsonUtil.toObjectList(jsonStr, SpecPriceListDto.class);

        // 获取pricelist（计算器、口碑、车主提车价、二手车价）
        specPriceListDtoList = getSpecPriceListDtoList(specDetailDto, specPriceListDtoList, request.getSpecid(),
                specDetailDto.getSeriesId(), request.getCityid());

        // 构建返回值
        List<SpecBaseInfoResponse.Result.Pricelist> priceLists = new ArrayList<>();
        specPriceListDtoList.forEach(price -> {
            SpecBaseInfoResponse.Result.Pricelist.Builder priceListBuilder =
                    SpecBaseInfoResponse.Result.Pricelist.newBuilder();
            priceListBuilder.setLinkurl(price.getLinkurl());
            priceListBuilder.setSubtitle(price.getSubtitle());
            priceListBuilder.setSubtitlecolor(price.getSubtitlecolor());
            priceListBuilder.setTitle(price.getTitle());
            priceListBuilder.setTypeid(price.getTypeid());
            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("specid", request.getSpecid() + "")
                    .putArgvs("typeid", price.getTypeid() + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_price_function_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_price_function_show"));
            priceListBuilder.setPvitem(pvBuilder);
            priceLists.add(priceListBuilder.build());
        });
        return CompletableFuture.completedFuture(priceLists);
    }


    private List<SpecPriceListDto> getSpecPriceListDtoList(SpecDetailDto specDetailDto,
                                                           List<SpecPriceListDto> specPriceListDtoList,
                                                           int specId,
                                                           int seriesId,
                                                           int cityId) {
        try {
            // 计算器
            CompletableFuture<PageFunctionData> calculatorInfoFuture = CompletableFuture.supplyAsync(() ->
                    getCalculatorInfo(specDetailDto.getSeriesId(), specId, cityId, specDetailDto.getMaxPrice(),
                            specDetailDto.getSpecName(), specDetailDto), ThreadPoolUtils.defaultThreadPoolExecutor);
            // 口碑
            CompletableFuture<PageFunctionData> kouBeiInfoFuture = CompletableFuture.supplyAsync(() ->
                            getKouBeiInfo(specId, specDetailDto.getBrandId(), specDetailDto.getSeriesId()),
                    ThreadPoolUtils.defaultThreadPoolExecutor);
            // 车主成交价
            CompletableFuture<SpecCityListOwnerPriceDto> ownerSourceFuture = ownerPriceComponent.get(specId, cityId);
            // 二手车价
            CompletableFuture<SpecUsedCarInfo> specUsedCarInfoFuture = specUsedCarComponent.get(specId);
            CompletableFuture.allOf(calculatorInfoFuture, kouBeiInfoFuture, ownerSourceFuture, specUsedCarInfoFuture)
                    .join();

            // 计算器
            PageFunctionData calculatorInfo = calculatorInfoFuture.get();
            if (calculatorInfo != null) {
                int typeId = 5;
                SpecPriceListDto priceListDTO = specPriceListDtoList.get(0);
                priceListDTO.setTypeid(typeId);
                priceListDTO.setTitle(calculatorInfo.getTitle());
                priceListDTO.setSubtitle(calculatorInfo.getSubtitle());
                priceListDTO.setLinkurl(calculatorInfo.getLinkurl());
            }

            // 口碑
            PageFunctionData kouBeiInfo = kouBeiInfoFuture.get();
            if (kouBeiInfo != null) {
                int typeId = 6;
                SpecPriceListDto priceListDTO = specPriceListDtoList.get(1);
                priceListDTO.setTypeid(typeId);
                priceListDTO.setTitle(kouBeiInfo.getTitle());
                priceListDTO.setSubtitle(kouBeiInfo.getSubtitle());
                priceListDTO.setLinkurl(kouBeiInfo.getLinkurl());
                //处理机房逻辑
                if (computerRoom.anyDown()) {
                    priceListDTO.setSubtitle("暂无");
                    priceListDTO.setLinkurl("");
                }
            }

            // 车主成交价
            SpecCityListOwnerPriceDto ownerSource = ownerSourceFuture.get();
            if (ownerSource != null) {
                int total = ownerSource.getTotal();
                if (total > 0) {
                    SpecPriceListDto priceListDTO = specPriceListDtoList.get(2);
                    priceListDTO.setSubtitle(total + "人晒价");

                    String linkUrl = String.format("flutter://car/ownerspecprice?seriesid=%s&specid=%s",
                            specDetailDto.getSeriesId(), specId);
                    priceListDTO.setLinkurl("autohome://flutter?url=" + UrlUtil.encode(linkUrl));
                    //处理机房逻辑
                    if (computerRoom.anyDown()) {
                        priceListDTO.setSubtitle("暂无");
                        priceListDTO.setLinkurl("");
                    }
                }
            }

            // 二手车价
            SpecUsedCarInfo specUsedCarInfo = specUsedCarInfoFuture.get();
            if (specUsedCarInfo != null && StringUtils.isNotBlank(specUsedCarInfo.getJumpUrl())) {
                if (!specUsedCarInfo.getSubTitle().equals("暂无报价")) {
                    SpecPriceListDto priceListDTO = specPriceListDtoList.get(3);
                    priceListDTO.setSubtitle(specUsedCarInfo.getSubTitle());
                    priceListDTO.setLinkurl(specUsedCarInfo.getJumpUrl());
                    //处理机房逻辑
                    if (computerRoom.langfangDown()) {
                        priceListDTO.setSubtitle("暂无报价");
                        priceListDTO.setLinkurl("");
                    }
                }
            }

            return specPriceListDtoList;
        } catch (Exception e) {
            log.error("getSpecPriceListDtoList error", e);
        }
        return specPriceListDtoList;
    }

    @Data
    public class PageFunctionData {
        private String title;
        private int typeid;
        private String subtitle;
        private String linkurl;
    }

    public PageFunctionData getCalculatorInfo(int seriesId, int specId, int cityId, int maxPrice,
                                              String specName, SpecDetailDto specDetail) {
        PageFunctionData item = new PageFunctionData();
        item.setTitle("计算器");
        item.setSubtitle("购车费用计算");
        item.setLinkurl("");

        try {
            CompletableFuture<SpecCityAskPriceDto> specCityAskPriceFuture = specCityAskPriceComponent.get(specId, cityId);
            SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceFuture.join();
            int newPriceInt = Objects.nonNull(specCityAskPriceDto) ? specCityAskPriceDto.getMinPrice() : 0;
            int originalPrice = Objects.nonNull(specCityAskPriceDto) ? specDetail.getMinPrice() : 0;
            int dealerPrice = newPriceInt == 0 ? originalPrice : newPriceInt;
            if (dealerPrice <= 0) {
                dealerPrice = maxPrice;
            }

            if (dealerPrice != 0) {
                int allPrice = this.getCostCarPrice(dealerPrice, specDetail);
                String priceStr = new BigDecimal((double) allPrice / 10000 + "")
                        .setScale(2, RoundingMode.HALF_UP) + "万";
                int dotIndex = priceStr.indexOf('.');
                StringBuilder sb = new StringBuilder(priceStr);
                priceStr = sb.replace(dotIndex - 1, dotIndex + 2, "*.*").toString();
                item.setSubtitle("总价" + priceStr);

                String linkUrl = String.format("autohome://car/calculatedetail?specname=%s&specid=%s&seriesid=%s&inputprice=%s&from=9",
                        UrlUtil.encode(specName).replace("+", "%20"), specId, seriesId, dealerPrice);
                item.setLinkurl(linkUrl);
            }
        } catch (Exception e) {
            log.error("车型页决策信息再优化-计算器:{}", ExceptionUtils.getStackTrace(e));
        }
        return item;
    }

    public PageFunctionData getKouBeiInfo(int specId, int brandId, int seriesId) {
        PageFunctionData item = new PageFunctionData();
        item.setTitle("口碑");
        item.setSubtitle("暂无数据");
        item.setLinkurl("");
        try {
            SpecKoubeiDto specKoubeiDto = specKouBeiComponent.get(specId).get();
            if (specKoubeiDto != null && specKoubeiDto.getScoreUserNum() > 0) {
                item.setSubtitle(specKoubeiDto.getScoreUserNum() + "条真实口碑");
                String koubeiUrl = "autohome://reputation/reputationlist?brandid=" + brandId + "&seriesid=" + seriesId + "&specid=" + specId + "&koubeifromkey=2";
                item.setLinkurl(koubeiUrl);
            }
        } catch (Exception e) {
            log.error("车型页决策信息再优化-口碑:{}", ExceptionUtils.getStackTrace(e));
        }
        return item;
    }

    public int getCostCarPrice(int price, SpecDetailDto specDetailDto) {
        int biyaoFee = 0;
        int insuranceFee = 0;
        try {
            int chechaunshui = 0;
            int jiaoqianxian = 950;
            int gouzhishui = Integer.parseInt(Math.round((price / 1.13) * 0.1) + "");
            int xiaofeishui = 0;
            int seats = 5;
            if (StringUtils.isNotEmpty(specDetailDto.getSeats())) {
                try {
                    if (StringUtils.isNotBlank(specDetailDto.getSeats())
                            && specDetailDto.getSeats().length() < 3) {
                        seats = Integer.parseInt(specDetailDto.getSeats());
                    } else {
                        String separator ="-";
                        if(StringUtils.contains(specDetailDto.getSeats(),"/")){
                            separator ="/";
                        }
                        seats = Integer.parseInt(String.valueOf(specDetailDto.getSeats().split(separator)[0]));
                    }
                    if (seats > 5) {
                        jiaoqianxian = 1100;
                    }
                } catch (Exception e) {
                    log.error("车型页-pricelist-计算器费率计算异常, specDetailDto={}", specDetailDto, e);
                }
            }
            if (seats > 5) {
                jiaoqianxian = 1100;
            }
            if (!Arrays.asList(4, 5, 6, 7).contains(specDetailDto.getFuelType())) {
                chechaunshui = getEmissionRangeAndTravelTax(
                        Objects.isNull(specDetailDto.getDisplacement())
                                ? new BigDecimal(0)
                                : specDetailDto.getDisplacement());
            }
            if (specDetailDto.isTaxExemption()) {
                gouzhishui = 0;
            }
            insuranceFee = getShangBaoCost(price, seats >= 6);
            biyaoFee = chechaunshui + jiaoqianxian + gouzhishui + xiaofeishui + 500;
        } catch (Exception e) {
            log.error("获取价格异常：specDetailDto={}", JsonUtil.toString(specDetailDto), e);
        }
        return price + biyaoFee + insuranceFee;
    }

    private int getEmissionRangeAndTravelTax(BigDecimal emission) {
        // 现在的emissions字段分为“1.0L(含）以下”、“1.0-1.6L(含）”、“1.6-2.0L(含）”、“2.0-2.5L(含）”、“2.5-3.0L(含）”、“3.0-4.0L(含）”、“4.0L以上”
        int travelTaxValue = 0;

        if (c(emission, 1.0) == -1 || c(emission, 1.0) == 0) {
            travelTaxValue = 180;//1.0L(含)以下
        } else if (c(emission, 1.0) == 1 && (c(emission, 1.6) == -1 || c(emission, 1.6) == 0)) {
            travelTaxValue = 360;//1.0-1.6L(含)
        } else if (c(emission, 1.6) == 1 && (c(emission, 2.0) == -1 || c(emission, 2.0) == 0)) {
            travelTaxValue = 420;//1.6-2.0L(含)
        } else if (c(emission, 2.0) == 1 && (c(emission, 2.5) == -1 || c(emission, 2.5) == 0)) {
            travelTaxValue = 720;//2.0-2.5L(含)
        } else if (c(emission, 2.5) == 1 && (c(emission, 3.0) == -1 || c(emission, 3.0) == 0)) {
            travelTaxValue = 1800;//2.5-3.0L(含)
        } else if (c(emission, 3.0) == 1 && (c(emission, 4.0) == -1 || c(emission, 4.0) == 0)) {
            travelTaxValue = 3000;//3.0-4.0L(含)
        } else {
            travelTaxValue = 4500;//4.0L以上
        }
        return travelTaxValue;
    }

    private int c(BigDecimal bigDecimal, Double double1) {
        return bigDecimal.compareTo(BigDecimal.valueOf(double1));
    }

    /**
     * 购车花费明细-获取车型商业保险信息
     *
     * @param specPrice
     * @param isMoreSeat6
     */
    private int getShangBaoCost(int specPrice, Boolean isMoreSeat6) {
        // 第三者责任险
        int shangYeBX_ThirdZeRenMoney = 706;

        // 原代码与注释不匹配，以代码为准
        // 车辆损失险 - 基础保费：六座以下：459+裸车价x1.0880% ；六座及以上： 550+裸车价x1.0880%
        int shangYeBX_CheLiangSunShiMoney = 0;
        int baseSunMoney = isMoreSeat6 ? 711 : 583;
        shangYeBX_CheLiangSunShiMoney = baseSunMoney + Integer.parseInt(Math.round(specPrice * 0.0141) + "");

        // 商业保险总额
        int shangYeBX_ShangYeBaoXianMoney = 0;
        return shangYeBX_ThirdZeRenMoney + shangYeBX_CheLiangSunShiMoney;
    }

    public CompletableFuture<List<SpecBaseInfoResponse.Result.Dealermodules>> buildDealerModules
            (SpecBaseInfoRequest request) {
        List<SpecBaseInfoResponse.Result.Dealermodules> cards = new ArrayList<>();
        addCard(cards, "经销商", "经销商", "", 10001, 10001, "", null, null);
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.63.0")) {
            addCard(cards, "", "", "", 50001, 90005, "", null, null);
        }
        addCard(cards, "", "", "", 40001, 90005, "", null, null);
        addCard(cards, "猜你喜欢", "", "", 90002, 90002, "", null, null);
        addCard(cards, "热门二手车", "二手车", "", 10002, 10002, "", null, null);
        addCard(cards, "广告模块", "", "", 3139, 90001, "", "3139", null);
        addCard(cards, "广告模块", "", "", 2728, 90001, "", "2728", 1);
        return CompletableFuture.completedFuture(cards);
    }

    private static void addCard(List<SpecBaseInfoResponse.Result.Dealermodules> cards,
                                String cTitle,
                                String title,
                                String url,
                                int typeId,
                                int mType,
                                String bgUrl,
                                String areaId,
                                Integer isLast) {
        SpecBaseInfoResponse.Result.Dealermodules.Builder item = SpecBaseInfoResponse.Result.Dealermodules.newBuilder();
        item.setCardtitle(cTitle);
        item.setTabtitle(title);
        item.setTabbgurl(bgUrl);
        item.setTypeid(typeId);
        item.setModuletype(mType);
        item.setTaburl(url);
        if (StringUtils.isNotBlank(areaId)) {
            SpecBaseInfoResponse.Result.Dealermodules.Extrainfo.Builder extraInfo =
                    SpecBaseInfoResponse.Result.Dealermodules.Extrainfo.newBuilder();
            extraInfo.setAreaid(typeId + "");
            if (Objects.nonNull(isLast)) {
                extraInfo.setIslast(isLast);
            }
            item.setExtrainfo(extraInfo);
        }
        cards.add(item.build());
    }

    public CompletableFuture<SpecBaseInfoResponse.Result.Carparmconfig.Builder> buildCarParamConfig(
            SpecDetailDto specDetailDto, Integer pm) {
        int state = specDetailDto.getSalestate();
        int levelId = specDetailDto.getLevelId();
        state = state == 1 ? (specDetailDto.getParamIsShow() == 1 ? 1 : 0) : 0;
        if (state == 1) {
            state = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 17, 18, 19, 20, 21, 22, 23, 24)
                    .contains(levelId) ? 1 : 0;
        }
        SpecBaseInfoResponse.Result.Carparmconfig.Builder carParmConfigBuilder =
                SpecBaseInfoResponse.Result.Carparmconfig.newBuilder();
        carParmConfigBuilder.setName("参数配置");
        carParmConfigBuilder.setTypeid(101052);
        carParmConfigBuilder.setIconurl("http://x.autoimg.cn/app/image/appicon/car_configure181116@3x.png");
        carParmConfigBuilder.setIconurl("https://files3.autoimg.cn/zrjcpk10/car_peizhi_20190529@3x.png");
        try {
            carParmConfigBuilder.setLinkurl(String.format("autohome://car/specconfig?seriesid=%s&specid=%s&specname=%s&hassummaryconfig=%s",
                    specDetailDto.getSeriesId(), specDetailDto.getSpecId(),
                    UrlUtil.encode(specDetailDto.getSpecName()).replace("+", "%20"), state));
            if (pm == 1) {
                carParmConfigBuilder.setLinkurl(String.format("autohome://carcompare/specsummaryconfig?seriesid=%s&specid=%s&specname=%s&hassummaryconfig=%s",
                        specDetailDto.getSeriesId(), specDetailDto.getSpecId(),
                        UrlUtil.encode(specDetailDto.getSpecName()).replace("+", "%20"), state));
            }
        } catch (Exception ex) {
            String exceptionMsg = String.format("ErrorMessage:%s;  exceptionStack:%s;", "参数配置协议异常",
                    ExceptionUtils.getStackTrace(ex));
            log.error(exceptionMsg, ex);
        }
        return CompletableFuture.completedFuture(carParmConfigBuilder);
    }

    public CompletableFuture<SpecBaseInfoResponse.Result.Practicalinfo.Builder> buildPracticalInfo(
            SpecDetailDto specDetailDto) {
        try {
            SeriesTestDataDto seriesTestDataDto = seriesTestDataComponent.get(specDetailDto.getSeriesId()).get();
            SpecBaseInfoResponse.Result.Practicalinfo.Builder practicalInfoBuilder =
                    SpecBaseInfoResponse.Result.Practicalinfo.newBuilder();
            practicalInfoBuilder.setTitle("实测");
            practicalInfoBuilder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/zjsc_230516.png");

            if (null == seriesTestDataDto || seriesTestDataDto.getTestData() == null) {
                return CompletableFuture.completedFuture(SpecBaseInfoResponse.Result.Practicalinfo.newBuilder());
            }
            SeriesTestDataDto.SeriesTestData119Dto resultDTO = seriesTestDataDto.getTestData().stream()
                    .filter(e -> e.getSpecId() == specDetailDto.getSpecId())
                    .filter(e -> e.getStandardId() != null && e.getStandardId().equals(119))
                    .findFirst()
                    .orElse(null);
            if (null == resultDTO) {
                return CompletableFuture.completedFuture(SpecBaseInfoResponse.Result.Practicalinfo.newBuilder());
            }

            String schema = "autohome://car/ahtest?seriesid=" + specDetailDto.getSeriesId()
                    + "&sourceid=4&specid=" + specDetailDto.getSpecId() + "&dataid=" + resultDTO.getDataId();
            practicalInfoBuilder.setLinkurl(schema);

            //入口显示调整：https://doc.autohome.com.cn/docapi/page/share/share_nwjQm20hXs
            if (Arrays.asList(1, 2, 3, 8, 9).contains(resultDTO.getFueltypedetail())) {
                //燃油车：权重顺序：零百加速>百公里油耗>刹车距离>车内噪音>智能驾驶>智能座舱
                List<SpecBaseInfoResponse.Result.Practicalinfo.Infoitem> infoitemList = new ArrayList<>();
                resultDTO.getTestItemlist().forEach(e -> {
                    SpecBaseInfoResponse.Result.Practicalinfo.Infoitem.Builder item =
                            SpecBaseInfoResponse.Result.Practicalinfo.Infoitem.newBuilder();
                    if ("0-100km/h加速时间".equals(e.getName())) {
                        item.setTitle("零百加速");
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("百公里油耗".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("刹车距离".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("120km/h".equals(e.getName())) {
                        item.setTitle("车内噪音");
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("智能驾驶".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue());
                        infoitemList.add(item.build());
                    } else if ("智能座舱".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue());
                        infoitemList.add(item.build());
                    }
                });
                List<String> weightOrder = Arrays.asList("零百加速", "百公里油耗", "刹车距离", "车内噪音", "智能驾驶", "智能座舱");
                infoitemList.sort(Comparator.comparingInt(item -> weightOrder.indexOf(item.getTitle())));
                practicalInfoBuilder.addAllList(infoitemList);
            } else if (Arrays.asList(4, 5, 6).contains(resultDTO.getFueltypedetail())) {
                //新能源：权重顺序：纯电续航>智能驾驶>智能座舱>刹车距离>车内噪音>零百加速
                List<SpecBaseInfoResponse.Result.Practicalinfo.Infoitem> infoitemList = new ArrayList<>();
                resultDTO.getTestItemlist().forEach(e -> {
                    SpecBaseInfoResponse.Result.Practicalinfo.Infoitem.Builder item =
                            SpecBaseInfoResponse.Result.Practicalinfo.Infoitem.newBuilder();
                    if ("综合续航里程".equals(e.getName())) {
                        item.setTitle("综合续航");
                        item.setSubtitle(Math.round(Float.parseFloat(e.getShowValue())) + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("智能驾驶".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue());
                        infoitemList.add(item.build());
                    } else if ("智能座舱".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue());
                        infoitemList.add(item.build());
                    } else if ("刹车距离".equals(e.getName())) {
                        item.setTitle(e.getName());
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("120km/h".equals(e.getName())) {
                        item.setTitle("车内噪音");
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    } else if ("0-100km/h加速时间".equals(e.getName())) {
                        item.setTitle("零百加速");
                        item.setSubtitle(e.getShowValue() + e.getUnit());
                        infoitemList.add(item.build());
                    }
                });
                List<String> weightOrder = Arrays.asList("综合续航", "智能驾驶", "智能座舱", "刹车距离", "车内噪音", "零百加速");
                infoitemList.sort(Comparator.comparingInt(item -> weightOrder.indexOf(item.getTitle())));
                practicalInfoBuilder.addAllList(infoitemList);
            }

            //小于2个不显示，最多显示2个
            if (practicalInfoBuilder.getListList().size() < 2) {
                return CompletableFuture.completedFuture(SpecBaseInfoResponse.Result.Practicalinfo.newBuilder());
            } else {
                List<SpecBaseInfoResponse.Result.Practicalinfo.Infoitem> infoitemList =
                        practicalInfoBuilder.getListList().subList(0, 2);
                practicalInfoBuilder.clearList();
                practicalInfoBuilder.addAllList(infoitemList);
            }

            practicalInfoBuilder.setPvitem(Pvitem.newBuilder()
                    .putArgvs("scheme", schema)
                    .putArgvs("specid", String.valueOf(specDetailDto.getSpecId()))
                    .putArgvs("virtually", resultDTO.getIsGenerate() == 1 ? "1" : "0")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_practical_click").build())
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_practical_show").build()));
            return CompletableFuture.completedFuture(practicalInfoBuilder);
        } catch (Exception e) {
            log.error("构建车型实测信息异常，specDetailDto={}，", specDetailDto, e);
        }
        return CompletableFuture.completedFuture(SpecBaseInfoResponse.Result.Practicalinfo.newBuilder());
    }

    public CompletableFuture<SpecBaseInfoResponse.Result.Priceinfo.Builder> buildPriceInfo(
            SpecDetailDto specDetailDto, SpecBaseInfoRequest request) {
        try {
            CompletableFuture<SpecCityAskPriceDto> specCityAskPriceFuture =
                    specCityAskPriceComponent.get(specDetailDto.getSpecId(), request.getCityid());
            SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceFuture.get();
            int minPrice = 0;
            if (Objects.nonNull(specCityAskPriceDto)) {
                minPrice = specCityAskPriceDto.getMinPrice();
            }

            SpecBaseInfoResponse.Result.Priceinfo.Builder priceInfoBuilder =
                    SpecBaseInfoResponse.Result.Priceinfo.newBuilder();
            priceInfoBuilder.setDealerpricename("经销商价");
            priceInfoBuilder.setTypeid(1010506);
            priceInfoBuilder.setDealerprice("0.00");
            if (minPrice > 0 && specDetailDto.getState() != 40) {
                priceInfoBuilder.setDealerpricetip("万起");
                priceInfoBuilder.setDealerprice(new BigDecimal((double) minPrice / 10000 + "")
                        .setScale(2, RoundingMode.HALF_UP).toString());
                String linkUrl = String.format("autohome://car/pricelibrary?brandid=%s&seriesid=%s&specid=%s&seriesname=%s&tabindex=1&fromtype=1&tabtype=1&sourceid=3&tabpricename=%s",
                        specDetailDto.getBrandId(), specDetailDto.getSeriesId(), specDetailDto.getSpecId(),
                        UrlUtil.encode(specDetailDto.getSeriesName()).replace("+", "%20"),URLEncoder.encode("本地报价", "utf-8"));
                priceInfoBuilder.setDealerpricelinkurl(linkUrl);
                priceInfoBuilder.setPvitem(Pvitem.newBuilder()
                        .putArgvs("specid", String.valueOf(specDetailDto.getSpecId()))
                        .putArgvs("typeid",request.getPm() == 3 ? "1010506" : "1")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_price_function_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_price_function_show").build()));
            }

            return CompletableFuture.completedFuture(priceInfoBuilder);
        } catch (Exception e) {
            log.error("设置priceinfo信息异常，specDetailDto={}, request={}", specDetailDto, request, e);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<List<SpecBaseInfoResponse.Result.Tabinfo>> buildTabInfo(SpecDetailDto specDetailDto, SpecBaseInfoRequest request) {
        int specid = request.getSpecid();
        int cityid = request.getCityid();
        CompletableFuture<SpecCityListOwnerPriceDto> ownerSourceFuture = ownerPriceComponent.get(specid, cityid);
        CompletableFuture<SeriesClubGroupDto> clubGroupFuture = seriesClubGroupComponent.get(specDetailDto.getSeriesId());
        CompletableFuture<SeriesClubWendaDto> clubWendaFuture = seriesClubWendaComponent.get(specDetailDto.getSeriesId());
        return CompletableFuture.allOf(ownerSourceFuture, clubGroupFuture, clubWendaFuture).thenApply(obj -> {
            List<SpecBaseInfoResponse.Result.Tabinfo> tabList = new ArrayList<>();
            List<SpecBaseTabinfoConfig.TabItemDTO> tabItemList = new ArrayList<>();
            //替换tab协议占位参数
            Map<String, String> replaceValue = Maps.newHashMap();
            replaceValue.put("seriesid", specDetailDto.getSeriesId() + "");
            replaceValue.put("seriesname", UrlUtil.encode(specDetailDto.getSeriesName()));
            replaceValue.put("specid", specid + "");
            replaceValue.put("cityid", cityid + "");
            replaceValue.put("cid",Integer.toString(cityid));
            StrSubstitutor strSubstitutor = new StrSubstitutor(replaceValue, "{", "}");
            // funcabtest=1:新版车型页标识
            if ("1".equals(request.getFuncabtest())||request.getPm()==3) {
                //停售车型tab配置
                if (specDetailDto.getState() == 40) {
                    if (!CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0"))
                        tabItemList.addAll(specBaseTabinfoConfig.getStopsaleAb());
                    else
                        tabItemList.addAll(specBaseTabinfoConfig.getStopsaleAb_11650());//11.65.0版本更换停售车型二手车商家链接
                } else {
                    tabItemList.addAll(specBaseTabinfoConfig.getOnsaleAb());
                }

                SpecCityListOwnerPriceDto ownerPriceListDto = ownerSourceFuture.join();
                //车主价格
                int ownerTotal = 0;
                if (ownerPriceListDto != null) {
                    ownerTotal = ownerPriceListDto.getTotal();
                }
                if (ownerTotal == 0) {
                    tabItemList.removeIf(x -> x.getTypeid() == 21);
                }

                SeriesClubWendaDto seriesClubWendaDto = clubWendaFuture.join();
                //问答
                int wendaTotal = 0;
                if (seriesClubWendaDto != null) {
                    wendaTotal = seriesClubWendaDto.getRowCount();
                }
                if (wendaTotal == 0) {
                    tabItemList.removeIf(x -> x.getTypeid() == 22);
                }

                SeriesClubGroupDto seriesClubGroupDto = clubGroupFuture.join();
                //论坛
                int clubTotal = 0;
                if (seriesClubGroupDto != null && seriesClubGroupDto.getTagList() != null && seriesClubGroupDto.getTagList().size() > 0) {
                    final Optional<SeriesClubGroupDto.TagListBean> first = seriesClubGroupDto.getTagList().stream().filter(x -> x.getAllTopicCount() >= 3).findFirst();
                    if (first.isPresent()) {
                        clubTotal = 1;
                    }
                }
                if (clubTotal == 0) {
                    tabItemList.removeIf(x -> x.getTypeid() == 23);
                }

            } else {
                if (specDetailDto.getState() == 40) {
                    if (!CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.0"))
                        tabItemList.addAll(specBaseTabinfoConfig.getStopsale());
                    else
                        tabItemList.addAll(specBaseTabinfoConfig.getStopsale_11650());//11.65.0版本更换停售车型二手车商家链接
                } else {
                    tabItemList.addAll(specBaseTabinfoConfig.getOnsale());
                }
            }

            // 添加论坛标签
            if ( CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")) {
                SpecClubTabDto specClubTagDto = specClubTagComponent.get(specid);
                if (Objects.nonNull(specClubTagDto) && specClubTagDto.getHasTab() == 1 && specClubTagDto.getTopicCount() > 0) {
                    Optional<SpecBaseTabinfoConfig.TabItemDTO> first = tabItemList.stream().filter(x -> x.getTypeid() == 12).findFirst();
                    if (first.isPresent()) {
                        int kouBeiIndex = tabItemList.indexOf(first.get());
                        SpecBaseTabinfoConfig.TabItemDTO clubTab = new SpecBaseTabinfoConfig.TabItemDTO();
                        clubTab.setTabtitle(specClubTabName);
                        clubTab.setTypeid(24);
                        clubTab.setTaburl(String.format("autohome://club/clublistembedspecmodel?from=209&seriesid=%d&specid=%d&bbsid=%d&bbstype=%s&bbsname=%s", specDetailDto.getSeriesId(), specid, specClubTagDto.getBbsId(), specClubTagDto.getBbsType(), CommonHelper.encodeUrl(specClubTagDto.getBbsName())));
                        tabItemList.add(kouBeiIndex, clubTab);
                    }
                }
            }
            //新版车型页tab开关，可区分车型状态，因停售和非停售是分开配置的
            tabItemList.removeIf(x -> x.getHide() == 1);
            //机房宕机逻辑判断
            if (computerRoom.langfangDown()) {
                tabItemList.removeIf(x -> x.getTypeid() == 20 || x.getTypeid() == 12);//二手车：20、口碑：12
            }
            if (computerRoom.anyDown()) {
                tabItemList.removeIf(x -> x.getTypeid() == 13);//用车
            }
            for (SpecBaseTabinfoConfig.TabItemDTO tabItem : tabItemList) {
                SpecBaseInfoResponse.Result.Tabinfo.Builder tabinfo = SpecBaseInfoResponse.Result.Tabinfo.newBuilder();
                replaceValue.put("tabid", tabItem.getTypeid() + "");
                tabinfo.setTypeid(tabItem.getTypeid());
                tabinfo.setTabtitle(tabItem.getTabtitle());
                if (tabItem.getTypeid() == 13 || tabItem.getTypeid() == 20 || tabItem.getTypeid() == 4) {//用车：13、二手车：20、二手车源：4
                    tabinfo.setTaburl(UrlUtil.getFlutterUrl(strSubstitutor.replace(tabItem.getTaburl())));
                    //11.68.0客户端技术需求
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.68.0") && (tabItem.getTypeid() == 20 || tabItem.getTypeid() == 4)) {
                        String url = "rn://CarCPKFTRRN/usedcar?seriesid={seriesid}&specid={specid}";
                        url = strSubstitutor.replace(url);
                        tabinfo.setTaburl(String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode(url)));
                    }
                } else if (tabItem.getTypeid() == 18) {//二手车商家
                    tabinfo.setTaburl("autohome://car/custombrowser?url=" + UrlUtil.encode(strSubstitutor.replace(tabItem.getTaburl())));
                } else {
                    tabinfo.setTaburl(strSubstitutor.replace(tabItem.getTaburl()));
                }
                tabList.add(tabinfo.build());
            }
            //鸿蒙-修改二手车tab跳转scheme
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.68.0") && request.getPm() == 3){
                tabList.stream().filter(dto -> StringUtils.equals("二手车",dto.getTabtitle()) || StringUtils.equals("二手车源",dto.getTabtitle()))
                        .findFirst()
                        .ifPresent(tab -> {
                            String template = "rn://CarCPKFTRRN/usedcar?seriesid=%d&specid=%d";
                            SpecBaseInfoResponse.Result.Tabinfo newTab = tab.toBuilder().setTaburl(String.format(template, specDetailDto.getSeriesId(), specid))
                                    .setTabtitle(tab.getTabtitle())
                                    .setTypeid(tab.getTypeid())
                                    .build();
                            int position = tabList.indexOf(tab);
                            tabList.remove(tab);
                            tabList.add(position,newTab);
                        });
            }
            return tabList;
        }).exceptionally(e -> {
            log.error("车型tab信息异常：车型id[{}]", specDetailDto.getSpecId(), e);
            return new ArrayList<>();
        });
    }

    public CompletableFuture<SpecBaseInfoResponse.Result.ZixunInfo.Builder> buildZixunInfo(int seriesId,
                                                                                           int specId,
                                                                                           int cityId,
                                                                                           int pm,
                                                                                           String zixunabtest) {
        SpecBaseInfoResponse.Result.ZixunInfo.Builder zixunInfo = SpecBaseInfoResponse.Result.ZixunInfo.newBuilder();
        try {
            if (!StringUtils.equalsAnyIgnoreCase(zixunabtest, "B")) {
                // 只有B实验会对zixuninfo进行赋值
                return CompletableFuture.completedFuture(zixunInfo);
            }

            SeriesConsultConfigDto seriesConsultConfigDto =
                    JsonUtil.toObject(seriesConsultConfig, new TypeReference<>() {
                    });
            boolean isNotBlack = (!CollectionUtils.isEmpty(seriesConsultConfigDto.getSeriesblacklist()))
                    && !seriesConsultConfigDto.getSeriesblacklist().contains(seriesId);
            SeriesCityAskPriceDto seriesCityAskPriceDto = seriesCityAskPriceNewComponent.get(seriesId, cityId).join();
            boolean canAskPrice = seriesCityAskPriceDto != null;
            //车系是否在黑名单中，如果在，就不添加咨询入口
            if (seriesConsultConfigDto != null
                    && !CollectionUtils.isEmpty(seriesConsultConfigDto.getSeriesblacklist())
                    && !seriesConsultConfigDto.getSeriesblacklist().contains(seriesId)
                    && isNotBlack
                    && canAskPrice) {
                //非全新车系（与全新车系判断【state=10&&全部车型都是即将销售】相反的判断）
                boolean isNotAllNewSeries = false;
                HashMap map = new HashMap();
                map.put("seriesId", seriesId);
                SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
                if (seriesDetailDto.getState() != 10
                        || seriesDetailDto.getStopSpecNum() != 0
                        || seriesDetailDto.getSellSpecNum() != 0) {
                    isNotAllNewSeries = true;
                }
                if (isNotAllNewSeries) {
                    //阿波罗配置的横栏滚动文案
                    SeriesConsultConfigDto.BasedataBean baseDataBean = seriesConsultConfigDto.getSeriesdatalist()
                            .stream()
                            .filter(x -> x.getSeriesid() == seriesId)
                            .findFirst()
                            .orElse(null);
                    if (baseDataBean == null) {
                        baseDataBean = seriesConsultConfigDto.getBasedata();
                    }
                    if (baseDataBean != null && baseDataBean.getDatalist() != null) {
                        zixunInfo.setBgurl(baseDataBean.getBgurl());
                        zixunInfo.setInterval(baseDataBean.getInterval());

                        String zixuneid = pm == 1 ? "3|1411002|1374|0|206324|305897" : "3|1412002|1374|0|206324|305897";
                        String linkurl = "autohome://car/zixunpoppage?customshowanimationtype=2&seriesid=%s&specid=%s&eid=%s&ext=%s&ordertype=1&entrystyle=%s";
                        List<SpecBaseInfoResponse.Result.ZixunInfo.DatalistBean> beanList = new ArrayList<>();
                        for (SeriesConsultConfigDto.BasedataBean.DatalistBean item : baseDataBean.getDatalist()) {
                            JSONObject extObj = new JSONObject();
                            extObj.put("position", item.getPosition());

                            SpecBaseInfoResponse.Result.ZixunInfo.DatalistBean.Builder bean =
                                    SpecBaseInfoResponse.Result.ZixunInfo.DatalistBean.newBuilder();
                            bean.setIcon(item.getIcon());
                            bean.setTitle(item.getTitle());
                            bean.setBtntitle(item.getBtntitle());
                            bean.setPosition(item.getPosition());
                            bean.setLinkurl(String.format(linkurl, seriesId, specId, UrlUtil.encode(zixuneid),
                                    UrlUtil.encode(extObj.toString()), item.getPosition()));
                            bean.setEid(zixuneid);
                            bean.setExt(extObj.toString());
                            beanList.add(bean.build());
                        }
                        //打乱集合顺序
                        Collections.shuffle(beanList);
                        zixunInfo.addAllDatalist(beanList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getZixunInfo-error:{}", ExceptionUtils.getStackTrace(e));
        }
        return CompletableFuture.completedFuture(zixunInfo);
    }
}
