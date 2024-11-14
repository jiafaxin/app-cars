package com.autohome.app.cars.job.controller;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.service.services.RecRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author chengjincheng
 * @date 2024/4/15
 */
@RestController
@Slf4j
public class RecRankUpdateController {

    @Autowired
    private RecRankService recRankService;

    @GetMapping(value = "/carext/recrank/attention/newcar/update", produces = "application/json;charset=utf-8")
    public String getAttentionNewCarRankTrend() {
        recRankService.updateAttentionNewCarData(log::info, true);
        return "success";
    }

    /**
     * 数仓更新月榜/周榜回调接口
     * @param type 1:月榜 2:周榜
     * @param dateStr 月榜格式日期格式为: yyyy-MM, 周榜格式日期格式为: yyyy-MM-dd
     * @return 返回结果
     */
    @GetMapping(value = "/saleRank/update", produces = "application/json;charset=utf-8")
    public JSONObject updateSaleRank(@RequestParam(value = "type") Integer type, @RequestParam(value = "date") String dateStr) {
        JSONObject result = new JSONObject();
        List<Integer> acceptTypeList = Arrays.asList(1, 2);
        int returnCode = 0;
        String message = "SUCCESS";
        if (!StringUtils.hasLength(dateStr) || Objects.isNull(type) || !acceptTypeList.contains(type)) {
            returnCode = -1;
            message = "参数不能为空";
        }
        String[] split = dateStr.split("-");
        if ((type.equals(1) && split.length != 2)) {
            returnCode = -1;
            message = "日期格式错误, 月榜格式日期格式为: yyyy-MM";
        }
        if ((type.equals(2) && split.length != 3)) {
            returnCode = -1;
            message = "日期格式错误, 周榜格式日期格式为: yyyy-MM-dd";
        }
        result.put("returncode", returnCode);
        result.put("message", message);
        if (returnCode == -1) {
            return result;
        }
        // 更新月榜/周榜数据
        recRankService.updateSaleRank(type, dateStr);
        return result;
    }
}
