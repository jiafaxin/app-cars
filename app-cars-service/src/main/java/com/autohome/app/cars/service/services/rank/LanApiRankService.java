package com.autohome.app.cars.service.services.rank;

import autohome.rpc.car.app_cars.v1.carext.RankLanApiRequest;
import autohome.rpc.car.app_cars.v1.carext.RankLanApiResponse;
import com.autohome.app.cars.common.enums.RankLanTypeEnum;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.StringUtils;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleCitySourceEntity;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.dtos.*;
import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseSaleRankDataDto;
import com.autohome.app.cars.service.components.recrank.hedge.HedgeComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankBrandMonthComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleCityOldComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthComponent;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangchengtao
 * @date 2024/9/14 14:52
 */
@Slf4j
@Service
public class LanApiRankService {

    @Resource
    private RankSaleMonthComponent monthComponent;

    @Resource
    private RankCommonService rankCommonService;

    @Resource
    private RankBrandMonthComponent brandMonthComponent;

    @Resource
    private RankSaleCityOldComponent cityComponent;

    @Resource
    private HedgeComponent hedgeComponent;
    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    /**
     * 提供给高德接口
     *
     * @param request     request
     * @param lanTypeEnum 榜单类型
     * @return RankLanApiResponse
     */
    public RankLanApiResponse getRankLanResult(RankLanApiRequest request, RankLanTypeEnum lanTypeEnum) {
        RankLanApiResponse.Builder responseBuilder = RankLanApiResponse.newBuilder();

        switch (lanTypeEnum) {
            case MONTH_RANK, NEW_ENERGY_MONTH_RANK -> {
                if (StringUtils.isEmpty(request.getDate())) {
                    responseBuilder.setReturnMsg("参数缺失");
                    responseBuilder.setReturnCode(101);
                    return responseBuilder.build();
                }
            }
            case MONTH_BRAND_RANK, NEW_ENERGY_MONTH_BRAND_RANK -> {
                if (StringUtils.isEmpty(request.getDate()) || request.getCityid() == 0) {
                    responseBuilder.setReturnMsg("参数缺失");
                    responseBuilder.setReturnCode(101);
                    return responseBuilder.build();
                }
            }
            case CITY_RANK -> {
                if (request.getCityid() == 0) {
                    responseBuilder.setReturnMsg("参数缺失");
                    responseBuilder.setReturnCode(101);
                    return responseBuilder.build();
                }
            }
        }

        RankLanApiResponse.RankLanApiResult result = switch (lanTypeEnum) {
            case MONTH_RANK -> getMonthRankLanResult(request, 0);
            case MONTH_BRAND_RANK -> getMonthBrandRankLanResult(request, 0);
            case CITY_RANK -> getCityRankLanResult(request);
            case NEW_ENERGY_MONTH_RANK -> getMonthRankLanResult(request, 1);
            case NEW_ENERGY_MONTH_BRAND_RANK -> getMonthBrandRankLanResult(request, 1);
            case KEEP_VALUE_RANK -> getKeepValueRankLanResult(request);
        };
        responseBuilder.setResult(result);
        return responseBuilder.build();
    }


    /**
     * 月榜&新能源月榜
     * @param request 请求
     * @param isNewEnergy 是否新能源
     * @return 月榜List
     */
    public RankLanApiResponse.RankLanApiResult getMonthRankLanResult(RankLanApiRequest request, int isNewEnergy) {

        RankLanApiResponse.RankLanApiResult.Builder resultBuilder = RankLanApiResponse.RankLanApiResult.newBuilder();
        RankParam rankParam = new RankParam();
        rankParam.setIsnewenergy(isNewEnergy);
        if (isNewEnergy == 1) {
            rankParam.setEnergytype(456);
        }
        if (request.getLevelid() != 0) {
            rankParam.setLevelid(String.valueOf(request.getLevelid()));
        }
        String curMonth = request.getDate();
        String preMonth = rankCommonService.getDateOffset(curMonth, RankConstant.LOCAL_MONTH_FORMATTER, ChronoUnit.MONTHS, 1);
        List<MonthRankDataResultDto.RankDataDto> curDataList = monthComponent.getDataList(curMonth, curMonth, 1000);
        List<MonthRankDataResultDto.RankDataDto> preDataList = monthComponent.getDataList(preMonth, preMonth, 1000);
        curDataList = rankCommonService.filterByParam(curDataList, rankParam);
        preDataList = rankCommonService.filterByParam(preDataList, rankParam);
        curDataList = curDataList.subList(0, Math.min(20, curDataList.size()));
        setRankNum(curDataList);
        setRankNum(preDataList);
        Map<Integer, Integer> preRankMap = preDataList.stream().collect(Collectors.toMap(MonthRankDataResultDto.RankDataDto::getSeriesId, MonthRankDataResultDto.RankDataDto::getRn));
        ArrayList<Integer> seriesIdList = new ArrayList<>(preRankMap.keySet());
        List<SeriesDetailDto> seriesDetailList = seriesDetailComponent.getListSync(seriesIdList);
        Map<Integer, SeriesDetailDto> seriesDetailDtoMap = seriesDetailList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));
        List<RankLanApiResponse.RankLanApiResult.RankLanApiItem> itemList = new ArrayList<>(curDataList.size());
        curDataList.forEach(data -> {
            if (preRankMap.containsKey(data.getSeriesId())) {
                Integer perRankNum = preRankMap.get(data.getSeriesId());
                data.setPreRankNum(perRankNum);
                data.setRankChange(perRankNum - data.getRn());
            }
            SeriesDetailDto detail = seriesDetailDtoMap.get(data.getSeriesId());
            RankLanApiResponse.RankLanApiResult.RankLanApiItem.Builder itemBuilder = RankLanApiResponse.RankLanApiResult.RankLanApiItem.newBuilder();
            itemBuilder.setSeriesid(data.getSeriesId());
            itemBuilder.setBrandid(detail.getBrandId());
            itemBuilder.setMonth(data.getMonth());
            itemBuilder.setLevelid(detail.getLevelId());
            itemBuilder.setBrandname(detail.getBrandName());
            itemBuilder.setRankchange(String.valueOf(data.getRankChange()));
            itemBuilder.setRanknum(String.valueOf(data.getRnNum()));
            itemBuilder.setPrice(CommonHelper.priceForamtV2(data.getMinPrice(), data.getMaxPrice()));
            itemBuilder.setMinprice(data.getMinPrice());
            itemBuilder.setMaxprice(data.getMaxPrice());
            itemBuilder.setSeriesimage(RankUtil.resizeSeriesImage(detail.getPngLogo()));
            itemBuilder.setSeriesname(data.getSeriesName());
            itemBuilder.setSalecount(data.getSaleCount());
            RankLanApiResponse.EnergySaleCount.Builder energySaleCountBuilder = RankLanApiResponse.EnergySaleCount.newBuilder();
            if (data.getOfv() != 0) {
                energySaleCountBuilder.setOfv(data.getOfv());
                itemBuilder.addEnergyflag("ofv");
            }
            if (data.getEv() != 0) {
                energySaleCountBuilder.setEv(data.getEv());
                itemBuilder.addEnergyflag("ev");
            }
            if (data.getPhev() != 0) {
                energySaleCountBuilder.setPhev(data.getPhev());
                itemBuilder.addEnergyflag("phev");
            }
            if (data.getReev() != 0) {
                energySaleCountBuilder.setReev(data.getReev());
                itemBuilder.addEnergyflag("reev");
            }
            itemBuilder.setEnergysalecount(energySaleCountBuilder);
            itemList.add(itemBuilder.build());
        });

        resultBuilder.addAllList(itemList);
        return resultBuilder.build();
    }

    public RankLanApiResponse.RankLanApiResult getMonthBrandRankLanResult(RankLanApiRequest request, int isNewEnergy) {
        RankLanApiResponse.RankLanApiResult.Builder resultBuilder = RankLanApiResponse.RankLanApiResult.newBuilder();

        RankParam rankParam = new RankParam();
        if (isNewEnergy == 1) {
            rankParam.setEnergytype(456);
            rankParam.setIsnewenergy(1);
        }
        rankParam.setCityid(request.getCityid());
        rankParam.setBeginMonth(request.getDate());
        rankParam.setEndMonth(request.getDate());
        rankParam.setDate(request.getDate());
        List<RankSaleCitySourceEntity> allCityByMonth = cityComponent.getAllCityByMonth(request.getDate(), isNewEnergy);
        Map<String, Long> cityRankDataMap = allCityByMonth.stream().collect(Collectors.toMap(RankSaleCitySourceEntity::getBrandid, RankSaleCitySourceEntity::getSalecnt));
        List<CityRankDataResultDto.RankDataDto> dataList = cityComponent.getDataList(rankParam.getBeginMonth(), rankParam.getEndMonth(), rankParam.getCityid());
        dataList = rankCommonService.filterByParam(dataList, rankParam);
        Map<Integer, List<CityRankDataResultDto.RankDataDto>> brandListMap = dataList.stream().collect(Collectors.groupingBy(CityRankDataResultDto.RankDataDto::getBrandId));
        List<BrandRankDataResultDto.RankDataDto> brandDataList = new ArrayList<>();
        Map<Integer, BrandDetailDto> brandMap = rankCommonService.getBrandMap(brandListMap.keySet().stream().toList());
        brandListMap.forEach((brandId, list) -> {
            BrandDetailDto brandDetail = brandMap.get(brandId);
            brandDataList.add(BrandRankDataResultDto.RankDataDto.getInstance(brandId, brandDetail.getName(), RankUtil.resizeBrandImage(brandDetail.getLogo()), "", list.stream().mapToLong(BaseSaleRankDataDto::getSaleCount).sum()));
        });

        List<RankLanApiResponse.RankLanApiResult.RankLanApiItem> itemList = new ArrayList<>(brandDataList.size());
        brandDataList.sort(Comparator.comparingLong(BrandRankDataResultDto.RankDataDto::getSaleCount).reversed());
        AtomicInteger rankNum = new AtomicInteger(1);
        String cityName = CityUtil.getCityName(request.getCityid());
        brandDataList.stream().limit(20).forEach(data -> {
            RankLanApiResponse.RankLanApiResult.RankLanApiItem.Builder itemBuilder = RankLanApiResponse.RankLanApiResult.RankLanApiItem.newBuilder();
            itemBuilder.setRanknum(String.valueOf(rankNum.getAndIncrement()));
            itemBuilder.setCitysalecount(data.getSaleCount());
            itemBuilder.setSalecount(cityRankDataMap.get(data.getBrandId().toString()));
            itemBuilder.setBrandid(data.getBrandId());
            itemBuilder.setMonth(request.getDate());
            itemBuilder.setBrandname(data.getBrandName());
            itemBuilder.setBrandimage(data.getBrandImage());
            itemBuilder.setCityid(request.getCityid());
            itemBuilder.setCityname(cityName);
            itemList.add(itemBuilder.build());
        });
        resultBuilder.addAllList(itemList);
        return resultBuilder.build();
    }

    public RankLanApiResponse.RankLanApiResult getCityRankLanResult(RankLanApiRequest request) {
        RankLanApiResponse.RankLanApiResult.Builder resultBuilder = RankLanApiResponse.RankLanApiResult.newBuilder();
        if (request.getCityid() == 0) {
            return resultBuilder.build();
        }

        RankParam rankParam = new RankParam();
        rankParam.setCityid(request.getCityid());
        rankParam.setBeginMonth(request.getDate());
        rankParam.setEndMonth(request.getDate());
        if (request.getLevelid() != 0) {
            rankParam.setLevelid(String.valueOf(request.getLevelid()));
        }
        rankParam.setDate(request.getDate());


        String preMonth = rankCommonService.getDateOffset(rankParam.getBeginMonth(), RankConstant.LOCAL_MONTH_FORMATTER, ChronoUnit.MONTHS, 1);

        CompletableFuture<List<CityRankDataResultDto.RankDataDto>> allCityDataFuture = CompletableFuture
                .supplyAsync(() -> cityComponent.getDataList(rankParam.getBeginMonth(), rankParam.getEndMonth(), 0))
                .exceptionally(e -> {
                    log.warn("cityComponent.getDataList error", e);
                    return null;
                });
        CompletableFuture<List<CityRankDataResultDto.RankDataDto>> cityAllDataListFuture = CompletableFuture
                .supplyAsync(() -> cityComponent.getDataList(rankParam.getBeginMonth(), rankParam.getEndMonth(), request.getCityid()))
                .exceptionally(e -> {
                    log.warn("cityComponent.getDataList error", e);
                    return null;
                });
        CompletableFuture<List<CityRankDataResultDto.RankDataDto>> prevDataListFuture = CompletableFuture
                .supplyAsync(() -> cityComponent.getDataList(preMonth, preMonth, request.getCityid()))
                .exceptionally(e -> {
                    log.warn("cityComponent.getDataList error", e);
                    return null;
                });
        CompletableFuture.allOf(allCityDataFuture, cityAllDataListFuture, prevDataListFuture).join();
        List<CityRankDataResultDto.RankDataDto> dataList = allCityDataFuture.join();
        List<CityRankDataResultDto.RankDataDto> cityAllDataList = cityAllDataListFuture.join();
        List<CityRankDataResultDto.RankDataDto> prevMonthDataList = prevDataListFuture.join();

        AtomicInteger rn = new AtomicInteger(1);
        prevMonthDataList = rankCommonService.filterByParam(prevMonthDataList, rankParam);
        prevMonthDataList.forEach(x-> x.setRnNum(rn.getAndIncrement()));
        Map<Integer, CityRankDataResultDto.RankDataDto> prevDataMap = prevMonthDataList.stream().collect(Collectors.toMap(CityRankDataResultDto.RankDataDto::getSeriesId, x -> x));
        dataList = rankCommonService.filterByParam(dataList, rankParam);
        cityAllDataList = rankCommonService.filterByParam(cityAllDataList, rankParam);
        Map<Integer, List<CityRankDataResultDto.RankDataDto>> allCityBrandMap = dataList.stream().collect(Collectors.groupingBy(CityRankDataResultDto.RankDataDto::getSeriesId));
        AtomicInteger rankNum = new AtomicInteger(1);
        cityAllDataList = cityAllDataList.subList(0, Math.min(20, cityAllDataList.size()));
        List<Integer> seriesIdList = cityAllDataList.stream().map(CityRankDataResultDto.RankDataDto::getSeriesId).toList();
        List<SeriesDetailDto> seriesDetailList = seriesDetailComponent.getListSync(seriesIdList);
        Map<Integer, SeriesDetailDto> detailMap = seriesDetailList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));
        List<RankLanApiResponse.RankLanApiResult.RankLanApiItem> itemList = new ArrayList<>(20);
        String cityName = CityUtil.getCityName(request.getCityid());
        cityAllDataList.forEach(data -> {
            CityRankDataResultDto.RankDataDto prevData = prevDataMap.get(data.getSeriesId());
            int curRankNum = rankNum.getAndIncrement();
            List<CityRankDataResultDto.RankDataDto> list = allCityBrandMap.get(data.getSeriesId());
            long totalCount = list.stream().mapToLong(BaseSaleRankDataDto::getSaleCount).sum();
            RankLanApiResponse.RankLanApiResult.RankLanApiItem.Builder itemBuilder = RankLanApiResponse.RankLanApiResult.RankLanApiItem.newBuilder();
            itemBuilder.setRanknum(String.valueOf(curRankNum));
            if (Objects.nonNull(prevData)) {
                itemBuilder.setRankchange(String.valueOf(prevData.getRnNum() - curRankNum));
            }
            SeriesDetailDto detailDto = detailMap.get(data.getSeriesId());
            itemBuilder.setCitysalecount(data.getSaleCount());
            itemBuilder.setSalecount(totalCount);
            itemBuilder.setPrice(CommonHelper.priceForamtV2(detailDto.getMinPrice(), detailDto.getMaxPrice()));
            itemBuilder.setMinprice(detailDto.getMiniprice());
            itemBuilder.setMaxprice(detailDto.getMaxPrice());
            itemBuilder.setBrandname(detailDto.getBrandName());
            itemBuilder.setMonth(request.getDate());
            itemBuilder.setSeriesid(data.getSeriesId());
            itemBuilder.setSeriesimage(RankUtil.resizeSeriesImage(data.getSeriesImage()));
            itemBuilder.setBrandid(data.getBrandId());
            itemBuilder.setSeriesname(data.getSeriesName());
            itemBuilder.setLevelid(Integer.parseInt(data.getLevelId()));
            itemBuilder.setCityid(request.getCityid());
            itemBuilder.setCityname(cityName);
            itemList.add(itemBuilder.build());
        });

        resultBuilder.addAllList(itemList);
        return resultBuilder.build();
    }

    public RankLanApiResponse.RankLanApiResult getKeepValueRankLanResult(RankLanApiRequest request) {
        RankLanApiResponse.RankLanApiResult.Builder resultBuilder = RankLanApiResponse.RankLanApiResult.newBuilder();
        RankParam rankParam = new RankParam();
        rankParam.setLevelid(String.valueOf(request.getLevelid()));
        List<HedgeRankDto.Item> dtoList = hedgeComponent.getDtoList(0);
        if (request.getLevelid() != 0) {
            dtoList = dtoList.stream().filter(p -> p.getLevelid() == request.getLevelid()).toList();
        }
        dtoList = dtoList.subList(0, Math.min(20, dtoList.size()));
        List<Integer> seriesIdList = dtoList.stream().map(HedgeRankDto.Item::getSeriesid).toList();
        List<SeriesDetailDto> detailList = seriesDetailComponent.getListSync(seriesIdList);
        Map<Integer, SeriesDetailDto> detailDtoMap = detailList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, x -> x));
        List<RankLanApiResponse.RankLanApiResult.RankLanApiItem> itemList = new ArrayList<>(20);
        AtomicInteger rankNum = new AtomicInteger(1);
        dtoList.forEach(dto -> {
            String rateValue = String.format("%.2f", dto.getRatevalue()) + "%";
            SeriesDetailDto detail = detailDtoMap.get(dto.getSeriesid());
            RankLanApiResponse.RankLanApiResult.RankLanApiItem.Builder itemBuilder = RankLanApiResponse.RankLanApiResult.RankLanApiItem.newBuilder();
            itemBuilder.setRanknum(String.valueOf(rankNum.getAndIncrement()));
            itemBuilder.setBrandid(detail.getBrandId());
            itemBuilder.setSeriesname(detail.getName());
            itemBuilder.setSeriesimage(RankUtil.resizeSeriesImage(detail.getPngLogo()));
            itemBuilder.setSeriesid(dto.getSeriesid());
            itemBuilder.setRatevalue(rateValue);
            itemBuilder.setLevelid(detail.getLevelId());
            itemBuilder.setPrice(CommonHelper.priceForamtV2(dto.getMinprice(), dto.getMaxprice()));
            itemBuilder.setMinprice(detail.getMiniprice());
            itemBuilder.setMaxprice(detail.getMaxPrice());
            itemList.add(itemBuilder.build());
        });

        resultBuilder.addAllList(itemList);
        return resultBuilder.build();
    }


    private void setRankNum(List<MonthRankDataResultDto.RankDataDto> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            MonthRankDataResultDto.RankDataDto dataDto = dataList.get(i);
            dataDto.setRn(i + 1);
            dataDto.setRnNum(i + 1);
        }
    }

}
