package com.autohome.app.cars.service.services;

import com.autohome.app.cars.apiclient.abtest.AbApiClient;
import com.autohome.app.cars.apiclient.abtest.dtos.ABTestDto;
import com.autohome.app.cars.apiclient.bfai.BfaiClient;
import com.autohome.app.cars.apiclient.bfai.dtos.SSeriesSortListResult;
import com.autohome.app.cars.apiclient.bfai.dtos.SeriesSortParam;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class SeriesListService {

    @Autowired
    BfaiClient bfaiClient;

    @Autowired
    AbApiClient abApiClient;

    public CompletableFuture<SSeriesSortListResult> getSeriesSortList(String queryid, Integer cityid, Integer pm, String deviceid, Integer brandId, String brandname, String source, int recommendswitch, Map<Integer, Integer> seriesIds) {
        if (recommendswitch == 0) {
            return CompletableFuture.completedFuture(null);
        }
        SeriesSortParam sortParam = new SeriesSortParam();
        sortParam.setRid(queryid);
        sortParam.setCity_id(cityid);
        sortParam.setDevice_type(pm == 1 ? "ios" : "android");
        sortParam.setDevice_id(deviceid);
        sortParam.setUid("");
        sortParam.setIs_debug(false);
        sortParam.setNet_state("");
        sortParam.setSource("1");
        sortParam.setBrand_id(brandId);
        if (brandname != null) {
            sortParam.setBrand_name(StringEscapeUtils.unescapeHtml4(brandname));
        } else {
            sortParam.setBrand_name("");
        }
        sortParam.setNeed_img_url(1);
        sortParam.setFrom(source);
        List<SeriesSortParam.SeriesDTO> series = sortParam.getSeries();
        if (seriesIds != null && seriesIds.size() > 0) {
            seriesIds.forEach((id, state) -> {
                SeriesSortParam.SeriesDTO seriesDTO = new SeriesSortParam.SeriesDTO();
                seriesDTO.setSeries_id(id);
                seriesDTO.setStatus(state);
                series.add(seriesDTO);
            });
        }
        return bfaiClient.getSeriesSortList(sortParam);
    }

    public CompletableFuture<ABTestDto> getAbTest(String testids, String deviceid) {
        return abApiClient.getABTest(testids, deviceid);
    }
}
