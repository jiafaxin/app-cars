package com.autohome.app.cars.provider.controller;

import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.httpclient.AutoHttpClientEnv;
import com.autohome.app.cars.service.services.SubsidyTradeInDataService;
import com.autohome.app.cars.service.services.dtos.mofang.MoFangFeedResult;
import com.autohome.app.cars.service.services.dtos.mofang.MoFangSubsidyHeadCard;
import com.autohome.app.cars.service.services.dtos.mofang.MoFangSubsidyPartCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhangchengtao
 * @date 2024/10/15 20:45
 */
@RestController
public class SubsidyTradeInController {

    @Autowired
    private SubsidyTradeInDataService dataService;


    @GetMapping("/subsidy/tradein/mofang/getbaseinfobycityid")
    public BaseModel<List<MoFangSubsidyHeadCard>> getBaseInfoByCityId(@RequestParam(value = "_cityid", defaultValue = "0") int cityId,
                                                                      @RequestParam(value = "pvareaid", defaultValue = "", required = false) String pvareaid) {
        BaseModel<List<MoFangSubsidyHeadCard>> result = new BaseModel<>();
        result.setResult(dataService.getMoFangHeadInfoByCityId(cityId, pvareaid));
        result.setMessage("success");
        return result;
    }

    @GetMapping("/subsidy/tradein/mofang/getpartinfobycityid")
    public BaseModel<List<MoFangSubsidyPartCard>> getPartInfoByCityId(@RequestParam(value = "_cityid", defaultValue = "0") int cityId,
                                                                      @RequestParam(value = "tabid", defaultValue = "0") int tabId,
                                                                      @RequestParam(value = "pvareaid", defaultValue = "", required = false) String pvareaid) {
        BaseModel<List<MoFangSubsidyPartCard>> result = new BaseModel<>();
        result.setResult(dataService.getMoFangPartList(cityId, tabId, pvareaid));
        result.setMessage("success");
        return result;
    }

    @GetMapping("/subsidy/tradein/mofang/gettabinfolist")
    public BaseModel<MoFangFeedResult> getTabInfoList(@RequestParam(value = "_cityid", defaultValue = "0") int cityId,
                                                      @RequestParam(value = "pagetypeid", defaultValue = "0") int tabId,
                                                      @RequestParam(value = "pagenum", defaultValue = "1") int pageIndex,
                                                      @RequestParam(value = "pagesize", defaultValue = "30") int pageSize,
                                                      @RequestParam(value = "pvareaid", defaultValue = "", required = false) String pvareaid) {
        BaseModel<MoFangFeedResult> result = new BaseModel<>();
        result.setResult(dataService.getSeriesIdListByTabId(cityId, tabId, pageIndex, pageSize, pvareaid));
        result.setMessage("success");
        return result;
    }
}
