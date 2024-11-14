package com.autohome.app.cars.service.components.recrank.sale;

import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.*;
import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseEnergyCountDataDto;
import com.autohome.app.cars.service.components.recrank.dtos.configdtos.BaseSaleRankDataDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RankBrandMonthComponent extends BaseComponent<BrandRankDataResultDto> {

    @Resource
    private RankCommonComponent commonComponent;
    @Resource
    private BrandDetailComponent brandComponent;
    @Resource
    private RankSaleMonthComponent monthComponent;
    @Resource
    private RankSaleCityComponent cityComponent;


    public RankResultDto getAllList(RankParam param) {
        RankResultDto resultDto = new RankResultDto();
        switch (param.getSalecounttype()) {
            // 零售量
            case "1" -> resultDto = getFromMonthRank(param);
            // 终端量
            case "2" -> resultDto = getFromCityRank(param);
        }
        commonComponent.filterByParam(resultDto, param);
        return resultDto;
    }


    /**
     * 从月榜获取数据
     *
     * @param param 查询条件
     * @return 结果
     */
    public RankResultDto getFromMonthRank(RankParam param) {
        List<MonthRankDataResultDto.RankDataDto> dataList = monthComponent.getDataList(param.getBeginMonth(), param.getEndMonth(), 1000);
        Map<Integer, List<MonthRankDataResultDto.RankDataDto>> monthDataGrpByBrandIdMap = dataList.stream().collect(Collectors.groupingBy(MonthRankDataResultDto.RankDataDto::getBrandId));
        RankResultDto result = getBrandInfoList(monthDataGrpByBrandIdMap, param);
        monthComponent.fillInfo(param, result);
        return result;
    }


    /**
     * 从周榜获取数据
     *
     * @param param 查询条件
     * @return 结果
     */
    public RankResultDto getFromCityRank(RankParam param) {
        List<CityRankDataResultDto.RankDataDto> dataList = cityComponent.getDataList(param.getBeginMonth(), param.getEndMonth(), param.getCityid());
        Map<Integer, List<CityRankDataResultDto.RankDataDto>> cityDataGrpByBrandIdMap = dataList.stream().collect(Collectors.groupingBy(CityRankDataResultDto.RankDataDto::getBrandId));
        RankResultDto result = getBrandInfoList(cityDataGrpByBrandIdMap, param);
        result.getResult().setScenetitle("全车型销量总榜");
        result.getResult().setSaleranktip("* 数据源于行业综合销量，每月10日左右更新");
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
        return result;
    }


    private <T extends BaseEnergyCountDataDto> RankResultDto getBrandInfoList(Map<Integer, List<T>> dataMap, RankParam param) {
        RankResultDto result = new RankResultDto();
        List<BrandRankDataResultDto.RankDataDto> dataDtoList = new ArrayList<>(dataMap.size());
        Map<Integer, BrandDetailDto> brandMap = getBrandMap(new ArrayList<>(dataMap.keySet()));
        dataMap.forEach((brandId, list) -> {
            BrandDetailDto brandDetailDto = brandMap.get(brandId);
            long saleCount = list.stream().mapToLong(BaseSaleRankDataDto::getSaleCount).sum();
            log.info("=brand-info{}=", brandDetailDto.toString());
            dataDtoList.add(BrandRankDataResultDto.RankDataDto.getInstance(brandId, brandDetailDto.getName(), brandDetailDto.getLogo(), list.get(0).getManuType(), saleCount));
        });
        if (!dataDtoList.isEmpty()) {
            result.getResult().setList(transToResult(dataDtoList, param));
        }
        commonComponent.filterByParam(result, param);
        if (Objects.nonNull(result.getResult()) && Objects.nonNull(result.getResult().getList()) && !result.getResult().getList().isEmpty()){
            // 处理排名
            commonComponent.processRankNum(result);
            commonComponent.processOtherInfo(result, param);
        }

        return result;
    }

    private List<RankResultDto.ListDTO> transToResult(List<BrandRankDataResultDto.RankDataDto> dataDtoList, RankParam param) {
        List<RankResultDto.ListDTO> resultList = new ArrayList<>(dataDtoList.size());
        dataDtoList.stream()
                .sorted(Comparator.comparingLong(BrandRankDataResultDto.RankDataDto::getSaleCount).reversed())
                .forEach(rankDataDto -> {
                    RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
                    dto.setBrandid(rankDataDto.getBrandId());
                    dto.setSeriesid(rankDataDto.getBrandId().toString());
                    dto.setSeriesname(rankDataDto.getBrandName());
                    dto.setSeriesimage(RankUtil.resizeBrandImage(rankDataDto.getBrandImage()));
                    dto.setSalecount(rankDataDto.getSaleCount());
                    dto.setManuType(rankDataDto.getManuType());
                    dto.setLinkurl(String.format("autohome://car/seriesbrand?brandid=%d&brandname=%s", rankDataDto.getBrandId(), URLEncoder.encode(rankDataDto.getBrandName(), StandardCharsets.UTF_8)));
                    dto.setCardtype(9);
                    Map<String, String> pvArgs = new HashMap<>();
                    pvArgs.put("subranktypeid", param.getSubranktypeid().toString());
                    pvArgs.put("rank", StrPool.EMPTY);
                    pvArgs.put("typeid", String.valueOf(param.getTypeid()));
                    pvArgs.put("brandid", rankDataDto.getBrandId().toString());
                    dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_brand_click", null, "car_rec_main_rank_brand_show", null));
                    dto.setRightinfo(genBrandRightInfo(pvArgs, dto, param));

                    resultList.add(dto);
                });

        return resultList;
    }

    private RankResultDto.RightinfoDTO genBrandRightInfo(Map<String, String> pvArgs, RankResultDto.ListDTO dto, RankParam param) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();
        rightInfo.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_sales_click", null, "", null));
        rightInfo.setRighttextone(dto.getSalecount().toString());
        rightInfo.setRighttexttwo("车系销量");
        Map<String, String> urlParamMap = new LinkedHashMap<>();
        urlParamMap.put("typeid", String.valueOf(param.getTypeid()));
        urlParamMap.put("subranktypeid", param.getSubranktypeid().toString());
        urlParamMap.put("brandid", dto.getBrandid().toString());
        if (StringUtils.hasLength(param.getDate())) {
            urlParamMap.put("date", param.getDate());
        }
        if (param.getCityid() > 0) {
            urlParamMap.put("cityid", String.valueOf(param.getCityid()));
        }
        if (-1 != param.getProvinceid()) {
            urlParamMap.put("provinceid", String.valueOf(param.getProvinceid()));
        }
        if (StringUtils.hasLength(param.getFcttypeid()) && !"0".equals(param.getFcttypeid())) {
            urlParamMap.put("fcttypeid", UrlUtil.encode(param.getFcttypeid()));
        }
        if (StringUtils.hasLength(param.getSalecounttype())) {
            urlParamMap.put("salecounttype", param.getSalecounttype());
        }
        String collect = urlParamMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        rightInfo.setRighttexttwolinkurl("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url="
                + UrlUtil.encode("rn://Car_SeriesSummary/SeriesSaleChild?panValid=0&" + collect));
        return rightInfo;
    }

    /**
     * 获取品牌Map
     *
     * @param brandIdList 品牌ID列表
     * @return 品牌Map
     */
    public Map<Integer, BrandDetailDto> getBrandMap(List<Integer> brandIdList) {
        List<BrandDetailDto> brandDetailList = brandComponent.mGet(brandIdList);
        if (Objects.nonNull(brandDetailList) && !brandDetailList.isEmpty()) {
            return brandDetailList.stream().collect(Collectors.toMap(BrandDetailDto::getId, Function.identity()));
        }
        return Collections.emptyMap();
    }


}
