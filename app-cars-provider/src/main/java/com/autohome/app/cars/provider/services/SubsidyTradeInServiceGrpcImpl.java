package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.subsidy.DubboSubsidyTradeInServiceTriple;
import autohome.rpc.car.app_cars.v1.subsidy.SpecSubsidyInfoRequest;
import autohome.rpc.car.app_cars.v1.subsidy.SpecSubsidyInfoResponse;
import autohome.rpc.car.app_cars.v1.subsidy.SubsidyTradeInRequest;
import autohome.rpc.car.app_cars.v1.subsidy.SubsidyTradeInResponse;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.service.components.subsidy.dtos.CityLocalSubsidyDto;
import com.autohome.app.cars.service.services.SubsidyTradeInDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <a href="https://doc.autohome.com.cn/docapi/page/share/share_zPCkxF6Daq">以旧换新 国家政策补贴</a>
 *
 * @author zhangchengtao
 * @date 2024/10/14 15:35
 */
@DubboService
@RestController
@Slf4j
public class SubsidyTradeInServiceGrpcImpl extends DubboSubsidyTradeInServiceTriple.SubsidyTradeInServiceImplBase {

    @Autowired
    private SubsidyTradeInDataService subsidyTradeInDataService;

    @Override
    @GetMapping(value = "/subsidy/tradein/getbaseinfobycityid", produces = "application/json;charset=utf-8")
    public SubsidyTradeInResponse getBaseInfoByCityId(SubsidyTradeInRequest request) {
        SubsidyTradeInResponse.Builder responseBuilder = SubsidyTradeInResponse.newBuilder();
        SubsidyTradeInResponse.Result.Builder resultBuilder = SubsidyTradeInResponse.Result.newBuilder();
//        List<SubsidyTradeInResponse.Result.BaseHeadInfo> headInfoList = subsidyTradeInDataService.getHeadInfoByCityId(request.getCityid());
////        Map<Integer, SubsidyTradeInResponse.Result.PartInfo> partInfoMap = subsidyTradeInDataService.getSubsidyTradeInConfig(request.getCityid());
//        resultBuilder.addAllBaseInfoList(headInfoList);
//        resultBuilder.setLowTotalPrice(partInfoMap.get(1));
//        resultBuilder.setDropHigh(partInfoMap.get(2));

        responseBuilder.setResult(resultBuilder);
        return responseBuilder
                .setReturnCode(0)
                .setReturnMsg("success")
                .build();
    }

    @Override
    @GetMapping(value = "/subsidy/tradein/getspecsubsidyinfo", produces = "application/json;charset=utf-8")
    public SpecSubsidyInfoResponse getSpecSubsidyInfo(SpecSubsidyInfoRequest request) {
        SpecSubsidyInfoResponse.Builder responseBuilder = SpecSubsidyInfoResponse.newBuilder();
        try {
            CityLocalSubsidyDto cityLocalSubsidyDto = subsidyTradeInDataService.getSubsidyTradeInConfigList(request.getCityid(), request.getSeriesid(), request.getSpecid());
            if (Objects.nonNull(cityLocalSubsidyDto)) {
                SpecSubsidyInfoResponse.Result.Builder result = SpecSubsidyInfoResponse.Result.newBuilder();
                result.setSubsidyTypeId(cityLocalSubsidyDto.getSubsidyTypeId());
                result.setTitle(cityLocalSubsidyDto.getTitle() != null ? cityLocalSubsidyDto.getTitle() : "");
                result.setStartTime(cityLocalSubsidyDto.getStartTime() != null ? DateUtil.format(cityLocalSubsidyDto.getStartTime(), "yyyy-MM-dd HH:mm:ss") : "");
                result.setEndTime(cityLocalSubsidyDto.getEndTime() != null ? DateUtil.format(cityLocalSubsidyDto.getEndTime(), "yyyy-MM-dd HH:mm:ss") : "");
                result.setSubsidyPolicy(cityLocalSubsidyDto.getSubsidyPolicy() != null ? cityLocalSubsidyDto.getSubsidyPolicy() : "");
                result.setIndex(cityLocalSubsidyDto.getIndex());
                if (ListUtil.isNotEmpty(cityLocalSubsidyDto.getReceivePath())) {
                    result.addAllReceivePath(cityLocalSubsidyDto.getReceivePath());
                }
                if (ListUtil.isNotEmpty(cityLocalSubsidyDto.getPriceList())) {
                    cityLocalSubsidyDto.getPriceList().forEach(price -> {
                        SpecSubsidyInfoResponse.Result.PriceList.Builder item = SpecSubsidyInfoResponse.Result.PriceList.newBuilder();
                        item.setAmount(price.getAmount() != null ? price.getAmount().intValue() : 0);
                        item.setMinPrice(price.getMinPrice() != null ? price.getMinPrice().intValue() : 0);
                        item.setMinPriceInclude(price.getMinPriceInclude() != null ? price.getMinPriceInclude().intValue() : 0);
                        item.setMaxPrice(price.getMaxPrice() != null ? price.getMaxPrice().intValue() : 0);
                        item.setMaxPriceInclude(price.getMaxPriceInclude() != null ? price.getMaxPriceInclude().intValue() : 0);
                        result.addPriceList(item);
                    });
                }
                responseBuilder.setResult(result);
            }
        } catch (Exception e) {
            System.out.println("getSpecSubsidyInfo error"+e.getMessage());
        }
        return responseBuilder
                .setReturnCode(0)
                .setReturnMsg("success")
                .build();
    }

}
