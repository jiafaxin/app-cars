package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.subsidy.SubsidyTradeInResponse;
import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.MarketCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.BasePageModel;
import com.autohome.app.cars.common.enums.SubsidyTypeEnum;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.Constants;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.components.car.BrandSeriesComponent;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.dtos.BrandSeriesDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.hangqing.CitySortHangqingComponent;
import com.autohome.app.cars.service.components.hangqing.dtos.CitySortHangqingDto;
import com.autohome.app.cars.service.components.recrank.attention.AreaSeriesAttentionComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.AreaSeriesAttentionDto;
import com.autohome.app.cars.service.components.recrank.dtos.MonthRankDataResultDto;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthComponent;
import com.autohome.app.cars.service.components.subsidy.CityLocalSubsidyComponent;
import com.autohome.app.cars.service.components.subsidy.dtos.CityLocalSubsidyDto;
import com.autohome.app.cars.service.services.dtos.SubInfoDto;
import com.autohome.app.cars.service.services.dtos.SubsidyTradeInConfig;
import com.autohome.app.cars.service.services.dtos.mofang.MoFangFeedResult;
import com.autohome.app.cars.service.services.dtos.mofang.MoFangSubsidyHeadCard;
import com.autohome.app.cars.service.services.dtos.mofang.MoFangSubsidyPartCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangchengtao
 * @date 2024/10/14 19:34
 */
@Slf4j
@Service
public class SubsidyTradeInDataService {

    @Autowired
    private CityLocalSubsidyComponent cityLocalSubsidyComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;


    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private RankSaleMonthComponent rankSaleMonthComponent;

    @Autowired
    private AreaSeriesAttentionComponent areaSeriesAttentionComponent;

    @Autowired
    private CitySortHangqingComponent citySortHangqingComponent;

    @Autowired
    private BrandSeriesComponent brandSeriesComponent;

    @Autowired
    private CmsApiClient cmsApiClient;


    @Value("#{T(com.autohome.app.cars.service.services.dtos.SubsidyTradeInConfig).decode('${subsidy_tradein_config:}')}")
    public SubsidyTradeInConfig subsidyTradeInConfig;

    private static final List<Integer> SUBSIDY_TYPE_ORDER_LIST = Arrays.asList(2, 1, -1);
    private static final String ITEM_URL_DEFAULT_PREFIX = "http://fs.autohome.com.cn/afu_spa/autoConsumptionSubsidy";
    /**
     * 补贴信息模块跳转参数
     */
    private static final String TOP_URL_TEMPLATE = "?cityid=%d&provinceid=%d&pluginversion=11.67.5&pvareaid=%s&eid=3|1411002|572|27564|211919|306974";
    private static final String ITEM_URL_TEMPLATE = "?seriesid=%d&specid=%d&cityid=%d&provinceid=%d&pluginversion=11.67.5&pvareaid=%s&eid=3|1411002|572|27564|211919|306974";

    public List<SubsidyTradeInResponse.Result.BaseHeadInfo> getHeadInfoByCityId(int cityId) {
        List<SubsidyTradeInResponse.Result.BaseHeadInfo> headInfoList = new ArrayList<>(4);
        Map<Integer, CityLocalSubsidyDto> maxPriceDtoMap = new LinkedHashMap<>(4);
        Map<Integer, Integer> maxPriceMap = new LinkedHashMap<>(4);
        List<CityLocalSubsidyDto> cityLocalSubsidyDtoList = cityLocalSubsidyComponent.get(cityId);
        if (!CollectionUtils.isEmpty(cityLocalSubsidyDtoList)) {
            for (CityLocalSubsidyDto cityLocalSubsidyDto : cityLocalSubsidyDtoList) {
                int subsidyTypeId = cityLocalSubsidyDto.getSubsidyTypeId();
                if (subsidyTypeId == -1) {
                    if (!maxPriceMap.containsKey(-1)) {
                        maxPriceMap.put(subsidyTypeId, 20000);
                    }
                } else if (maxPriceDtoMap.containsKey(subsidyTypeId)) {
                    CityLocalSubsidyDto existDto = maxPriceDtoMap.get(subsidyTypeId);
                    // 计算最大补贴金额
                    int existMaxAmount = existDto.getPriceList().stream().mapToInt(CityLocalSubsidyDto.PriceSubsidyDto::getAmount).max().orElse(Integer.MAX_VALUE);
                    int newMaxAmount = cityLocalSubsidyDto.getPriceList().stream().mapToInt(CityLocalSubsidyDto.PriceSubsidyDto::getAmount).max().orElse(Integer.MIN_VALUE);
                    if (existMaxAmount < newMaxAmount) {
                        maxPriceDtoMap.put(subsidyTypeId, cityLocalSubsidyDto);
                        maxPriceMap.put(subsidyTypeId, newMaxAmount);
                    }
                } else {
                    maxPriceDtoMap.put(subsidyTypeId, cityLocalSubsidyDto);
                    int maxPrice = cityLocalSubsidyDto.getPriceList().stream().mapToInt(CityLocalSubsidyDto.PriceSubsidyDto::getAmount).max().orElse(0);
                    if (maxPrice > 0) {
                        maxPriceMap.put(subsidyTypeId, cityLocalSubsidyDto.getPriceList().stream().mapToInt(CityLocalSubsidyDto.PriceSubsidyDto::getAmount).max().orElse(0));
                    }
                }
            }
        }
        if (!maxPriceDtoMap.isEmpty()) {
            AtomicInteger index = new AtomicInteger();
            maxPriceDtoMap.forEach((subsidyTypeId, dto) -> {
                SubsidyTypeEnum subsidyTypeEnum = SubsidyTypeEnum.getSubsidyById(subsidyTypeId);
                if (Objects.nonNull(subsidyTypeEnum) && maxPriceMap.containsKey(subsidyTypeId)) {
                    headInfoList.add(SubsidyTradeInResponse.Result.BaseHeadInfo.newBuilder()
                            .setPosition(index.incrementAndGet())
                            .setDesc(dto.getTitle())
                            .setAmount(maxPriceMap.get(subsidyTypeId))
                            .build());
                }
            });
        }
        return headInfoList;
    }


    private Map<Integer, SubInfoDto> getSeriesSubsidyPrice(Map<Integer, SpecDetailDto> specDetailMap, List<CityLocalSubsidyDto> subsidyList, Map<Integer, SpecCityAskPriceDto> dealerPriceMap) {

        Map<Integer, SubInfoDto> subsidyMap = new HashMap<>(specDetailMap.size());
        for (Map.Entry<Integer, SpecDetailDto> entry : specDetailMap.entrySet()) {
            SpecDetailDto specDetailDto = entry.getValue();
            int specId = specDetailDto.getSpecId();
            if (CollectionUtils.isEmpty(subsidyList)) {
                continue;
            }
            SubInfoDto subsidyDto = getCityLocalSubsidyDto(specDetailDto, new ArrayList<>(subsidyList), dealerPriceMap.get(specId));
            if (subsidyDto != null) {
                subsidyMap.put(specDetailDto.getSeriesId(), subsidyDto);
            }
        }
        return subsidyMap;
    }

    /**
     * 魔方: 获取头部数据
     *
     * @param cityId   城市ID
     * @param pvareaid pvareaid
     * @return 补贴头部数据
     */
    public List<MoFangSubsidyHeadCard> getMoFangHeadInfoByCityId(int cityId, String pvareaid) {
        String urlPrefix = StringUtils.hasLength(subsidyTradeInConfig.getH5Url()) ? subsidyTradeInConfig.getH5Url() : ITEM_URL_DEFAULT_PREFIX;
        List<MoFangSubsidyHeadCard> headInfoList = new ArrayList<>(8);
        List<CityLocalSubsidyDto> maxPriceDtoList = new ArrayList<>(8);
        List<CityLocalSubsidyDto> cityLocalSubsidyDtoList = cityLocalSubsidyComponent.get(cityId);

        if (!CollectionUtils.isEmpty(cityLocalSubsidyDtoList)) {
            for (CityLocalSubsidyDto cityLocalSubsidyDto : cityLocalSubsidyDtoList) {
                int subsidyTypeId = cityLocalSubsidyDto.getSubsidyTypeId();
                if (subsidyTypeId == -1) {
                    cityLocalSubsidyDto.setPriceList(Collections.singletonList(new CityLocalSubsidyDto.PriceSubsidyDto(20000)));
                    maxPriceDtoList.add(cityLocalSubsidyDto);
                } else {
                    maxPriceDtoList.add(cityLocalSubsidyDto);
                }
            }
        }
        if (!maxPriceDtoList.isEmpty()) {
            String url = urlPrefix + String.format(TOP_URL_TEMPLATE, cityId, cityId / 10000 * 10000, pvareaid);
            maxPriceDtoList.forEach(dto -> {
                SubsidyTypeEnum subsidyTypeEnum = SubsidyTypeEnum.getSubsidyById(dto.getSubsidyTypeId());
                if (Objects.nonNull(subsidyTypeEnum)) {
                    MoFangSubsidyHeadCard moFangSubsidyHeadCard = new MoFangSubsidyHeadCard();
                    moFangSubsidyHeadCard.setTitleTxt(dto.getTitle());
                    moFangSubsidyHeadCard.setAppUrl(url);
                    moFangSubsidyHeadCard.setMUrl(url);
                    moFangSubsidyHeadCard.setPcUrl(url);
                    int sortOffset = 0;
                    if (dto.getTitle().contains("纯电")) {
                        sortOffset = 1;
                    } else if (dto.getTitle().contains("新能源")) {
                        sortOffset = 2;
                    } else {
                        sortOffset = 3;
                    }
                    int maxPrice = !CollectionUtils.isEmpty(dto.getPriceList()) ? dto.getPriceList().stream().mapToInt(CityLocalSubsidyDto.PriceSubsidyDto::getAmount).max().orElse(0) : 0;
                    moFangSubsidyHeadCard.setSort(subsidyTypeEnum.getSort() * 10 + sortOffset);
                    moFangSubsidyHeadCard.setPriceTxt(String.format("%d元", maxPrice));
                    moFangSubsidyHeadCard.setBgImg(Collections.singletonList(new MoFangSubsidyHeadCard.BgImgDTO("https://nfiles3.autohome.com.cn/zrjcpk10/head_bgimg_20241018.png")));
                    headInfoList.add(moFangSubsidyHeadCard);
                }
            });
            headInfoList.sort(Comparator.comparingInt(MoFangSubsidyHeadCard::getSort));
        }
        return headInfoList;
    }


    public List<MoFangSubsidyPartCard> getMoFangPartList(int cityId, int tabId, String pvareaid) {
        String urlPrefix = StringUtils.hasLength(subsidyTradeInConfig.getH5Url()) ? subsidyTradeInConfig.getH5Url() : ITEM_URL_DEFAULT_PREFIX;
        List<Integer> seriesIdList = subsidyTradeInConfig.getPartConfigList().stream()
                .map(SubsidyTradeInConfig.PartConfig::getSeriesIdList)
                .flatMap(Collection::stream).distinct().toList();
        List<SeriesDetailDto> detailDtoList = seriesDetailComponent.getListSync(seriesIdList);
        Map<Integer, Integer> seriesSpecIdMap = detailDtoList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, SeriesDetailDto::getMinPriceSpecId));
        List<CityLocalSubsidyDto> cityLocalSubsidyDtoList = cityLocalSubsidyComponent.get(cityId);
        if (Objects.isNull(cityLocalSubsidyDtoList)) {
            cityLocalSubsidyDtoList = Collections.emptyList();
        }
        // 车系最低经销商价Map
        List<Integer> specIdList = new ArrayList<>(seriesSpecIdMap.values());
        CompletableFuture<List<SpecDetailDto>> specDetailListFuture = specDetailComponent.getList(specIdList);
        List<SpecDetailDto> specDetailDtoList = specDetailListFuture.join();
        Map<Integer, SpecDetailDto> specDetailMap = specDetailDtoList.stream().collect(Collectors.toMap(SpecDetailDto::getSeriesId, x -> x));
        List<SpecCityAskPriceDto> specCityAskPriceList = Collections.synchronizedList(new ArrayList<>(specIdList.size()));
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        specIdList.forEach(specId ->
                // 使用单个 有降级到省份逻辑
                tasks.add(specCityAskPriceComponent.get(specId, cityId).thenAccept(specCityAskPriceDto -> {
                    if (Objects.nonNull(specCityAskPriceDto)) {
                        specCityAskPriceList.add(specCityAskPriceDto);
                    }
                })));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        Map<Integer, SpecCityAskPriceDto> dealerPriceMap = specCityAskPriceList.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecCityAskPriceDto::getSpecId, x -> x));
        Map<Integer, SubInfoDto> seriesSubsidyInfoMap = getSeriesSubsidyPrice(specDetailMap, cityLocalSubsidyDtoList, dealerPriceMap);
        Map<Integer, SeriesDetailDto> seriesDetailMap = detailDtoList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));

        Optional<SubsidyTradeInConfig.PartConfig> firstOptional = subsidyTradeInConfig.getPartConfigList().stream().filter(x -> x.getPartId() == tabId).findFirst();
        if (firstOptional.isPresent()) {
            SubsidyTradeInConfig.PartConfig subsidyTradeInPart = firstOptional.get();
            MoFangSubsidyPartCard partCard = new MoFangSubsidyPartCard();
            partCard.setName(subsidyTradeInPart.getPartName());
            List<MoFangSubsidyPartCard.ListDTO> itemList = new ArrayList<>();
            subsidyTradeInPart.getSeriesIdList().forEach(seriesId -> {
                SeriesDetailDto detailDto = seriesDetailMap.get(seriesId);
                // 最低指导价
                int minGuidePrice = detailDto.getMinPrice();
                // 最低经销商价 没有最低经销商价格时使用指导价
                int minDealerPrice = dealerPriceMap.containsKey(seriesSpecIdMap.get(seriesId)) ? dealerPriceMap.get(seriesSpecIdMap.get(seriesId)).getMinPrice() : 0;
                // 经销商降价
                int dealerDownPrice = minGuidePrice - minDealerPrice;
                // 补贴价格
                int subsidyPrice = 0;
                if (seriesSubsidyInfoMap.containsKey(seriesId)) {
                    SubInfoDto subInfoDto = seriesSubsidyInfoMap.get(seriesId);
                    CityLocalSubsidyDto subsidyDto = subInfoDto.getSubsidyDto();
                    subsidyPrice = (seriesSubsidyInfoMap.containsKey(seriesId) && subInfoDto.getIndex() >= 0) ? subsidyDto.getPriceList().get(subInfoDto.getIndex()).getAmount() : 0;
                }
                String downPriceInfo = StrPool.EMPTY;
                int totalSubsidyPrice = subsidyPrice + (minDealerPrice == 0 ? 0 : dealerDownPrice);
                if (totalSubsidyPrice > 0) {
                    downPriceInfo = String.format("综合降价%d元", totalSubsidyPrice);
                }
                int finalPrice = (minDealerPrice == 0 ? minGuidePrice : minDealerPrice) - subsidyPrice;
                MoFangSubsidyPartCard.ListDTO item = new MoFangSubsidyPartCard.ListDTO();
                item.setTitle(detailDto.getName());
                item.setItemImg(detailDto.getPngLogo());
                item.setBtnName((finalPrice > 0 ? "到手" : StrPool.EMPTY) + CommonHelper.getPriceInfo(finalPrice));
                item.setSubTitle(downPriceInfo);
                item.setKey("tab" + tabId + "sub" + seriesId);
                itemList.add(item);
                MoFangSubsidyPartCard.ListDTO.BtnUrlDTO btnUrlDTO = new MoFangSubsidyPartCard.ListDTO.BtnUrlDTO();
                String url = String.format(urlPrefix + ITEM_URL_TEMPLATE, seriesId, detailDto.getMinPriceSpecId(), cityId, cityId / 10000 * 10000, pvareaid);
                btnUrlDTO.setPcUrl(url);
                btnUrlDTO.setAppUrl(url);
                btnUrlDTO.setMUrl(url);
                item.setBtnUrl(btnUrlDTO);
                item.setAppUrl(url);
                item.setMUrl(url);
                item.setPcUrl(url);
                item.setInfo1("https://nfiles3.autohome.com.cn/zrjcpk10/part_bg_20241022.png");
            });
            partCard.setList(itemList);
            partCard.setKey("tab" + tabId);
            return Collections.singletonList(partCard);
        }
        return null;
    }

    /**
     * 获取车型补贴
     *
     * @param cityId   城市ID
     * @param seriesId 车系ID
     * @param specId   车型ID
     * @return 当前车型适用的补贴
     */
    public CityLocalSubsidyDto getSubsidyTradeInConfigList(int cityId, int seriesId, int specId) {
        if (specId == 0) {
            SeriesDetailDto detailDto = seriesDetailComponent.get(seriesId);
            specId = detailDto.getMinPriceSpecId();
        }
        //补贴不适用商用车
        if (specId >= 1000000) {
            return null;
        }
        CompletableFuture<SpecCityAskPriceDto> specCityPriceFuture = specCityAskPriceComponent.get(specId, cityId);
        SpecCityAskPriceDto specCityAskPriceDto = specCityPriceFuture.join();
        List<CityLocalSubsidyDto> subsidyList = cityLocalSubsidyComponent.get(cityId);
        CompletableFuture<SpecDetailDto> specDetailFuture = specDetailComponent.get(specId);
        SpecDetailDto specDetail = specDetailFuture.join();
        int levelId = specDetail.getLevelId();
        int fuelType = specDetail.getFuelType();
        Map<Integer, List<CityLocalSubsidyDto>> subsidyListMap = subsidyList.stream().collect(Collectors.groupingBy(CityLocalSubsidyDto::getSubsidyTypeId));
        for (Integer subsidyTypeId : SUBSIDY_TYPE_ORDER_LIST) {
            if (subsidyListMap.containsKey(subsidyTypeId)) {
                List<CityLocalSubsidyDto> dtoList = subsidyListMap.get(subsidyTypeId);
                for (CityLocalSubsidyDto subsidyDto : dtoList) {
                    if (subsidyDto.getSubsidyTypeId() == -1) {
                        List<CityLocalSubsidyDto.PriceSubsidyDto> priceList = new ArrayList<>(2);
                        priceList.add(new CityLocalSubsidyDto.PriceSubsidyDto(15000));
                        priceList.add(new CityLocalSubsidyDto.PriceSubsidyDto(20000));
                        subsidyDto.setPriceList(priceList);
                        if (Constants.NEW_ENERGY_TYPE_LIST.contains(specDetail.getFuelType())) {
                            // 新能源补贴
                            subsidyDto.setIndex(1);
                        } else if (specDetail.getDisplacement().compareTo(BigDecimal.valueOf(2.0)) <= 0) {
                            subsidyDto.setIndex(0);
                        }
                        return subsidyDto;
                    }
                    // 符合燃油类型
                    boolean isFuelMatch = !CollectionUtils.isEmpty(subsidyDto.getFuelTypeSet()) && subsidyDto.getFuelTypeSet().contains(fuelType);
                    // 符合车系级别
                    boolean isLevelMatch = !CollectionUtils.isEmpty(subsidyDto.getCarLevelIdSet()) && subsidyDto.getCarLevelIdSet().contains(levelId);
                    boolean hasPrice = !CollectionUtils.isEmpty(subsidyDto.getPriceList());
                    if (isFuelMatch && isLevelMatch && hasPrice && specCityAskPriceDto != null) {
                        int dealerPrice = specCityAskPriceDto.getMinPrice();
                        for (CityLocalSubsidyDto.PriceSubsidyDto priceSubsidyDto : subsidyDto.getPriceList()) {
                            boolean priceMatch = false;
                            if (priceSubsidyDto.getMinPrice() == 0 || priceSubsidyDto.getMaxPrice() == 0) {
                                if (priceSubsidyDto.getMinPrice() == 0 && priceSubsidyDto.getMaxPrice() == 0) {
                                    priceMatch = true;
                                } else if (priceSubsidyDto.getMinPrice() == 0) {
                                    priceMatch = dealerPrice < priceSubsidyDto.getMaxPrice() || (priceSubsidyDto.getMaxPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMaxPrice());
                                } else {
                                    priceMatch = dealerPrice > priceSubsidyDto.getMinPrice() || (priceSubsidyDto.getMinPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMinPrice());
                                }
                            }
                            // 价格符合
                            if (priceMatch || ((dealerPrice > priceSubsidyDto.getMinPrice() || (priceSubsidyDto.getMinPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMinPrice()))
                                    && (dealerPrice < priceSubsidyDto.getMaxPrice() || (priceSubsidyDto.getMaxPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMaxPrice())))) {
                                subsidyDto.setIndex(subsidyDto.getPriceList().indexOf(priceSubsidyDto));
                                return subsidyDto;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return null;
    }


    public SubInfoDto getCityLocalSubsidyDto(SpecDetailDto specDetail, List<CityLocalSubsidyDto> subsidyList, SpecCityAskPriceDto specCityAskPriceDto) {
        int levelId = specDetail.getLevelId();
        int fuelType = specDetail.getFuelType();
        Map<Integer, List<CityLocalSubsidyDto>> subsidyListMap = subsidyList.stream().collect(Collectors.groupingBy(CityLocalSubsidyDto::getSubsidyTypeId));
        for (Integer subsidyTypeId : SUBSIDY_TYPE_ORDER_LIST) {
            if (subsidyListMap.containsKey(subsidyTypeId)) {
                List<CityLocalSubsidyDto> dtoList = subsidyListMap.get(subsidyTypeId);
                for (CityLocalSubsidyDto subsidyDto : dtoList) {
                    int index = -1;
                    if (subsidyDto.getSubsidyTypeId() == -1) {
                        List<CityLocalSubsidyDto.PriceSubsidyDto> priceList = new ArrayList<>(2);
                        priceList.add(new CityLocalSubsidyDto.PriceSubsidyDto(15000));
                        priceList.add(new CityLocalSubsidyDto.PriceSubsidyDto(20000));
                        subsidyDto.setPriceList(priceList);
                        if (Constants.NEW_ENERGY_TYPE_LIST.contains(specDetail.getFuelType())) {
                            // 新能源补贴
                            index = 1;
                        } else if (specDetail.getDisplacement().compareTo(BigDecimal.valueOf(2.0)) <= 0) {
                            index = 0;
                        }
                        return new SubInfoDto(index, subsidyDto);

                    }
                    // 符合燃油类型
                    boolean isFuelMatch = !CollectionUtils.isEmpty(subsidyDto.getFuelTypeSet()) && subsidyDto.getFuelTypeSet().contains(fuelType);
                    // 符合车系级别
                    boolean isLevelMatch = !CollectionUtils.isEmpty(subsidyDto.getCarLevelIdSet()) && subsidyDto.getCarLevelIdSet().contains(levelId);
                    boolean hasPrice = !CollectionUtils.isEmpty(subsidyDto.getPriceList());
                    if (isFuelMatch && isLevelMatch && hasPrice && specCityAskPriceDto != null) {
                        int dealerPrice = specCityAskPriceDto.getMinPrice();
                        for (CityLocalSubsidyDto.PriceSubsidyDto priceSubsidyDto : subsidyDto.getPriceList()) {
                            boolean priceMatch = false;
                            if (priceSubsidyDto.getMinPrice() == 0 || priceSubsidyDto.getMaxPrice() == 0) {
                                if (priceSubsidyDto.getMinPrice() == 0 && priceSubsidyDto.getMaxPrice() == 0) {
                                    priceMatch = true;
                                } else if (priceSubsidyDto.getMinPrice() == 0) {
                                    priceMatch = dealerPrice < priceSubsidyDto.getMaxPrice() || (priceSubsidyDto.getMaxPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMaxPrice());
                                } else {
                                    priceMatch = dealerPrice > priceSubsidyDto.getMinPrice() || (priceSubsidyDto.getMinPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMinPrice());
                                }
                            }
                            // 价格符合
                            if (priceMatch || ((dealerPrice > priceSubsidyDto.getMinPrice() || (priceSubsidyDto.getMinPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMinPrice()))
                                    && (dealerPrice < priceSubsidyDto.getMaxPrice() || (priceSubsidyDto.getMaxPriceInclude() == 1 && dealerPrice == priceSubsidyDto.getMaxPrice())))) {
                                return new SubInfoDto(subsidyDto.getPriceList().indexOf(priceSubsidyDto), subsidyDto);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return null;
    }


    public MoFangFeedResult getSeriesIdListByTabId(int cityId, int tabId, int pageIndex, int pageSize, String pvareaid) {
        MoFangFeedResult result = new MoFangFeedResult();
        try {

            List<Integer> seriseIdList = new ArrayList<>();
            int provinceId = cityId / 10000 * 10000;
            CompletableFuture<BrandSeriesDto> brandSeriesDtoCompletableFuture = brandSeriesComponent.get(75);
            BrandSeriesDto brandSeriesDto = brandSeriesDtoCompletableFuture.join();
            List<Integer> filterSeriesIdList = brandSeriesDto.getFctoryList().stream().flatMap(x -> x.getSeriesList().stream().map(BrandSeriesDto.SeriesItem::getId)).toList();
            int totalCount = 0;
            long skipCount = (long) (pageIndex - 1) * pageSize;
            switch (tabId) {
                case 1:
                    // 热销车
                    BasePageModel<MonthRankDataResultDto.RankDataDto> latestMonthDataList = rankSaleMonthComponent.getLatestMonthDataList(pageIndex, pageSize, filterSeriesIdList, 1);
                    totalCount = 100;
                    if (skipCount < totalCount && !CollectionUtils.isEmpty(latestMonthDataList.getDatalist())) {
                        seriseIdList = latestMonthDataList.getDatalist().stream().map(MonthRankDataResultDto.RankDataDto::getSeriesId).toList();
                    }
                    break;
                case 2:
                    // 新能源专区
                    BasePageModel<AreaSeriesAttentionDto> seriesDataResult = areaSeriesAttentionComponent.getSeriesDataList(pageIndex, pageSize, provinceId, filterSeriesIdList, 456, 1);
                    totalCount = seriesDataResult.getRowcount();
                    if (!CollectionUtils.isEmpty(seriesDataResult.getDatalist())) {
                        seriseIdList = seriesDataResult.getDatalist().stream().map(AreaSeriesAttentionDto::getSeriesId).toList();
                    }
                    break;
                case 3:
                    // 近期降价
                    BasePageModel<CitySortHangqingDto.HangqingDto> dataResult = citySortHangqingComponent.getSeriesDataList(cityId, pageIndex, pageSize, filterSeriesIdList, 0, 5, 1);
                    totalCount = dataResult.getRowcount();
                    if (!CollectionUtils.isEmpty(dataResult.getDatalist())) {
                        seriseIdList = dataResult.getDatalist().stream().map(CitySortHangqingDto.HangqingDto::getSeriesId).toList();
                    }
                    break;
                case 4:
                    // https://cms.api.autohome.com.cn/CmsJava/Wcf/CarSerivce.svc/GetAllNewBrandsByTimeNofilter?_appid=app&startTime=2024-5&endTime=2025-2
                    LocalDate localDate = LocalDate.now();
                    String startDate = localDate.minusMonths(3).withDayOfMonth(1).format(RankConstant.LOCAL_WEEK_FORMATTER);
                    String endDate = localDate.format(RankConstant.LOCAL_WEEK_FORMATTER);
                    CompletableFuture<BaseModel<MarketCarResult>> marketCarListFuture = cmsApiClient.getMarketCarList(1, startDate, endDate);
                    BaseModel<MarketCarResult> newCarPublishInfoBaseModel = marketCarListFuture.join();
                    MarketCarResult marketCarResult = newCarPublishInfoBaseModel.getResult();
                    List<MarketCarResult.ItemsDTO> dtoList = marketCarResult.getItems().stream()
                            .filter(x -> !Constants.COMMERCIAL_LEVEL_ID.contains(x.getSeriesLevelId())
                                    && (x.getEventLevel().equals(1) || x.getEventLevel().equals(2))).toList();
                    seriseIdList = dtoList.stream().map(MarketCarResult.ItemsDTO::getSeriesId).distinct().toList();
                    totalCount = seriseIdList.size();
                    if (skipCount < totalCount) {
                        seriseIdList = seriseIdList.stream().skip(skipCount).limit(pageSize).toList();
                    } else {
                        seriseIdList = Collections.emptyList();
                    }
                    break;
            }
            CompletableFuture<List<SeriesDetailDto>> detailListFuture = seriesDetailComponent.getList(seriseIdList);
            List<SeriesDetailDto> seriesDetailList = detailListFuture.join();
            Map<Integer, Integer> seriesIdSpecIdMap = seriesDetailList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, SeriesDetailDto::getMinPriceSpecId));
            List<Integer> specIdList = new ArrayList<>(seriesIdSpecIdMap.values());
            List<SpecDetailDto> specDetailDtoList = specDetailComponent.getList(specIdList).join();
            Map<Integer, SpecDetailDto> specDetailMap = specDetailDtoList.stream().collect(Collectors.toMap(SpecDetailDto::getSpecId, x -> x));
            List<CityLocalSubsidyDto> subsidyList = cityLocalSubsidyComponent.get(cityId);

            List<SpecCityAskPriceDto> specCityAskPriceList = Collections.synchronizedList(new ArrayList<>(specIdList.size()));
            List<CompletableFuture<Void>> tasks = new ArrayList<>();
            specIdList.forEach(specId ->
                    // 使用单个 有降级到省份逻辑
                    tasks.add(specCityAskPriceComponent.get(specId, cityId).thenAccept(specCityAskPriceDto -> {
                        if (Objects.nonNull(specCityAskPriceDto)) {
                            specCityAskPriceList.add(specCityAskPriceDto);
                        }
                    })));
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            Map<Integer, SpecCityAskPriceDto> specDealerPriceMap = specCityAskPriceList.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecCityAskPriceDto::getSpecId, x -> x));
            Map<Integer, SubInfoDto> seriesSubsidyMap = getSeriesSubsidyPrice(specDetailMap, subsidyList, specDealerPriceMap);
            List<MoFangFeedResult.ListDTO> list = new ArrayList<>(seriesDetailList.size());
            Map<Integer, SeriesDetailDto> detailDtoMap = seriesDetailList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));
            seriseIdList.forEach(seriesId -> {
                SeriesDetailDto detailDto = detailDtoMap.get(seriesId);
                SpecDetailDto specDetailDto = specDetailMap.get(detailDto.getMinPriceSpecId());
                boolean specDetailIsNull = Objects.isNull(specDetailDto);
                // 最低指导价
                int minGuidePrice = Objects.nonNull(specDetailDto) ? specDetailDto.getMinPrice() : detailDto.getMinPrice();
                // 最低经销商价 没有最低经销商价格时使用指导价

                int minDealerPrice = specDealerPriceMap.containsKey(detailDto.getMinPriceSpecId()) ? specDealerPriceMap.get(detailDto.getMinPriceSpecId()).getMinPrice() : minGuidePrice;
                // 补贴价格
                int subsidyPrice = 0;
                String title = StrPool.EMPTY;
                if (seriesSubsidyMap.containsKey(detailDto.getId())) {
                    SubInfoDto subInfoDto = seriesSubsidyMap.get(detailDto.getId());
                    CityLocalSubsidyDto subsidyDto = subInfoDto.getSubsidyDto();
                    if (subInfoDto.getIndex() >= 0) {
                        subsidyPrice = seriesSubsidyMap.containsKey(detailDto.getId()) ? subsidyDto.getPriceList().get(subInfoDto.getIndex()).getAmount() : 0;
                    }
                    title = subsidyDto.getTitle();
                }

                MoFangFeedResult.ListDTO dto = new MoFangFeedResult.ListDTO();
                dto.setItemImg(detailDto.getPngLogo());
//                dto.setSubTitle(subsidyPrice > 0 ? subsidyDto.getTitle() : StrPool.EMPTY);
                dto.setTitle(detailDto.getName());
                dto.setType("skuCard");
//            dto.setSubTitle(subsidyPrice > 0 ? "补:" + subsidyDto.getTitle() : StrPool.EMPTY);
                dto.setOriginalPrice(CommonHelper.getPriceInfo(specDetailIsNull ? detailDto.getMinPrice() : specDetailDto.getMinPrice()));
                dto.setCurrentPrice(CommonHelper.getPriceInfo(minDealerPrice - subsidyPrice) + (minDealerPrice != subsidyPrice ? "起" : StrPool.EMPTY));
                dto.setTags(subsidyPrice > 0 ? String.format("%d元%s", subsidyPrice, title) : StrPool.EMPTY);
                dto.setBtnTxt("立即领取");
                String urlPrefix = StringUtils.hasLength(subsidyTradeInConfig.getH5Url()) ? subsidyTradeInConfig.getH5Url() : ITEM_URL_DEFAULT_PREFIX;
                String url = String.format(urlPrefix + ITEM_URL_TEMPLATE, detailDto.getId(), detailDto.getMinPriceSpecId(), cityId, cityId / 10000 * 10000, pvareaid);
                dto.setPcUrl(url);
                dto.setAppUrl(url);
                dto.setMUrl(url);
                dto.setInfo1("https://nfiles3.autohome.com.cn/zrjcpk10/item_bg_20241018.png");
                list.add(dto);

            });
            result.setHaveMore(totalCount > pageSize * pageIndex);
            result.setTotalCount(totalCount);
            result.setList(list);
        } catch (Exception e) {
            log.warn("feed流错误:", e);
        }
        return result;
    }
}
