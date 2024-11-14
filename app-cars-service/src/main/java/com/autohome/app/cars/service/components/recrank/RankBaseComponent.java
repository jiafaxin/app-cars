package com.autohome.app.cars.service.components.recrank;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public abstract class RankBaseComponent<T> extends BaseComponent<T> {

    public String get(TreeMap<String, Object> params) {
        // 获取UrlEncode后的参数JSON
        String encodedSrt = params.get("param").toString();
        // 进行Decode
        String decode = URLDecoder.decode(encodedSrt, StandardCharsets.UTF_8);
        // 将JSON转为RankParam对象
        RankParam param = JSONObject.parseObject(decode, RankParam.class);
        RankParam.getInstance(param);
        // 调用组件的查询方法
        RankResultDto resultDto = getResultListByCondition(param);
        return JsonUtil.toString(resultDto);
    }

    public RankResultDto getResultListByCondition(RankParam param) {
        return new RankResultDto();
    }

    /**
     * @param params 通用的查询参数
     * @return com.autohome.app.cars.service.components.recrank.dtos.RankResultDto
     * @description 通用的查询参数 处理
     * @author zzli
     */

    public RankParam getParams(TreeMap<String, Object> params) {
        // 获取UrlEncode后的参数JSON
        String encodedSrt = params.get("params").toString();
        // 进行Decode
        String decode = URLDecoder.decode(encodedSrt, StandardCharsets.UTF_8);
        // 将JSON转为RankParam对象
        RankParam param = JSONObject.parseObject(decode, RankParam.class);
       return RankParam.getInstance(param);

    }
}
