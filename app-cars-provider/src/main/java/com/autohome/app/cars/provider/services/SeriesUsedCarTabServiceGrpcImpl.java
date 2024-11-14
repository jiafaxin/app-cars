package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.*;
import com.autohome.app.cars.apiclient.che168.dtos.UsedCarSearchResult;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.StrPool;
import com.autohome.app.cars.service.services.SeriesUsedCarService;
import com.autohome.app.cars.service.services.dtos.UsedCarInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author zhangchengtao
 * @date 2024/10/9 15:04
 */
@DubboService
@RestController
@Slf4j
public class SeriesUsedCarTabServiceGrpcImpl extends DubboSeriesUsedCarTabServiceTriple.SeriesUsedCarTabServiceImplBase {

    @Autowired
    private SeriesUsedCarService seriesUsedCarService;


    /**
     * 车系页二手车卡片
     * https://doc.autohome.com.cn/docapi/page/share/share_z1K5BbhRVw
     * @param request req
     * @return SeriesUsedCarTabResponse
     */
    @Override
    @GetMapping(value = "/carstreaming/seriessummary/usedcarlist", produces = "application/json;charset=utf-8")
    public SeriesUsedCarTabResponse getUsedCarList(SeriesUsedCarTabRequest request) {
        SeriesUsedCarTabResponse.Builder responseBuilder = SeriesUsedCarTabResponse.newBuilder();
        SeriesUsedCarTabResponse.Result.Builder resultBuilder = SeriesUsedCarTabResponse.Result.newBuilder();
        resultBuilder.setRecommendtitle("为您推荐更多车源");
        resultBuilder.setPageindex(request.getPageindex());
        // 查询二手车列表
        UsedCarInfoDto usedCarListInfo = seriesUsedCarService.getUsedCarListInfo(request);
        if (Objects.isNull(usedCarListInfo)) {
            responseBuilder.setResult(resultBuilder);
            responseBuilder.setReturnMsg("暂无数据");
            responseBuilder.setReturnCode(-1);
            return responseBuilder.build();
        }
        List<UsedCarSearchResult.CarDTO> carList = usedCarListInfo.getCarList();
        List<UsedCarSearchResult.YearDTO> yearList = usedCarListInfo.getYearList();
        Map<Integer, Integer> specYearMinGuidePriceMap = usedCarListInfo.getSpecYearMinGuidePriceMap();
        String priceInfo = usedCarListInfo.getPriceInfo();
        // 设置总页码
        resultBuilder.setPagecount(usedCarListInfo.getTotalPageCount());
        // 填充数据
        seriesUsedCarService.buildUsedCarTab(carList, resultBuilder, 1);
        if (request.getPageindex() == 1) {
            // 年代款List
            if (!yearList.isEmpty()) {
                List<SeriesUsedCarTabResponse.Result.YearList> yearBuilderList = new ArrayList<>();
                Integer minPrice = specYearMinGuidePriceMap.get(-1);
                yearBuilderList.add(SeriesUsedCarTabResponse.Result.YearList.newBuilder()
                        .setYearname("全部")
                        .setYearvalue(-1)
                        .setGuidepricetip("指导价:")
                        .setUsedpricetip("二手价:")
                        .setGuideprice(CommonHelper.getPriceInfo(minPrice) + (minPrice > 0 ? "起" : StrPool.EMPTY))
                        .setUsedprice(priceInfo)
                        .build());
                yearList.stream().sorted(Comparator.comparingInt(UsedCarSearchResult.YearDTO::getYear).reversed()).forEach(yearDto -> {
                    int price = (int) (10000 * Double.parseDouble(StringUtils.hasLength(yearDto.getLowprice()) ? yearDto.getLowprice() : StringUtils.hasLength(yearDto.getAlllowprice()) ? yearDto.getAlllowprice() : "0.0"));
                    yearBuilderList.add(SeriesUsedCarTabResponse.Result.YearList.newBuilder()
                            .setYearname(yearDto.getSyname())
                            .setYearvalue(yearDto.getSyid())
                            .setGuidepricetip("指导价:")
                            .setUsedpricetip("二手价:")
                            .setGuideprice(CommonHelper.getPriceInfo(specYearMinGuidePriceMap.getOrDefault(yearDto.getSyid(), 0)) + (specYearMinGuidePriceMap.containsKey(yearDto.getSyid()) ? "起" : StrPool.EMPTY))
                            .setUsedprice(CommonHelper.getPriceInfo(price) + (price > 0 ? "起" : StrPool.EMPTY))
                            .build());
                });
                resultBuilder.addAllYearlist(yearBuilderList);
            }
            seriesUsedCarService.addUsedCarFilter(resultBuilder);
        }
        responseBuilder.setResult(resultBuilder);
        return responseBuilder.build();
    }


    @Override
    @GetMapping(value = "/carstreaming/seriessummary/hedgeratiacharts", produces = "application/json;charset=utf-8")
    public HedgeRatioChartResponse getHedgeCharts(SeriesUsedCarTabRequest request) {
        HedgeRatioChartResponse.Builder responseBuilder = HedgeRatioChartResponse.newBuilder();
        HedgeRatioChartResponse.Result.Builder resultBuilder = HedgeRatioChartResponse.Result.newBuilder();
        seriesUsedCarService.getHedgeCharts(request, resultBuilder);
        responseBuilder.setResult(resultBuilder);
        return responseBuilder.build();
    }

    /**
     * 保值率二手车列表
     * @param request req
     * @return  SeriesUsedCarTabResponse
     */
    @Override
    @GetMapping(value = "/carstreaming/seriessummary/hedgeratiocarlist", produces = "application/json;charset=utf-8")
    public SeriesHedgeRatioResponse getHedgeRatioCarList(SeriesUsedCarTabRequest request) {
        SeriesHedgeRatioResponse.Builder responseBuilder = SeriesHedgeRatioResponse.newBuilder();
        SeriesHedgeRatioResponse.Result.Builder resultBuilder = SeriesHedgeRatioResponse.Result.newBuilder();
        resultBuilder.setRecommendtitle("为您推荐更多车源");
        resultBuilder.setPageindex(request.getPageindex());
        // 查询二手车列表
        UsedCarInfoDto usedCarListInfo = seriesUsedCarService.getUsedCarListInfo(request);
        if (Objects.isNull(usedCarListInfo)) {
            responseBuilder.setResult(resultBuilder);
            responseBuilder.setReturnMsg("暂无数据");
            responseBuilder.setReturnCode(-1);
            return responseBuilder.build();
        }
        List<UsedCarSearchResult.CarDTO> carList = usedCarListInfo.getCarList();
        resultBuilder.setPagecount(usedCarListInfo.getTotalPageCount());
        if (!carList.isEmpty()) {
            // 填充数据
            seriesUsedCarService.buildHedgeUsedCarList(request.getCityid(), request.getPageindex() ,carList, resultBuilder);
        }
        responseBuilder.setResult(resultBuilder);
        return responseBuilder.build();
    }

    /**
     * <a href="https://doc.autohome.com.cn/docapi/page/share/share_zuDHNS6WTA">获取图库二手车列表</a>
     * @param request 请求
     * @return 响应
     */
    @Override
    @GetMapping(value = "/carstreaming/seriessummary/picusedcarlist", produces = "application/json;charset=utf-8")
    public PicUsedCarResponse getPicUsedCarList(PicUsedCarRequest request) {
        return seriesUsedCarService.getPicUsedCarList(request);
    }
}
