package com.autohome.app.cars.service.components.misc;

import com.autohome.app.cars.apiclient.koubei.KoubeiApiClient;
import com.autohome.app.cars.apiclient.openApi.DataOpenApiClient;
import com.autohome.app.cars.apiclient.opscard.OpsCardApiClient;
import com.autohome.app.cars.apiclient.opscard.dtos.SeriesReplyGuideTextResult;
import com.autohome.app.cars.apiclient.reply.ReplyApiClient;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dataopen.dtos.SeriesRecommendLikeDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesTabDto;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "series_tab")
public class SeriesTabComponent extends BaseComponent<SeriesTabDto> {

    static String paramName = "seriesId";

    @Autowired
    DataOpenApiClient dataOpenApiClient;

    @Autowired
    KoubeiApiClient koubeiApiClient;

    @Autowired
    ReplyApiClient replyApiClient;

    @Autowired
    OpsCardApiClient opsCardApiClient;

    @Value("${series_hot_reply_seriesids:}")
    private String seriesHotReplySeriesIds;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesTabDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public static void main(String[] args) {
        String ext = URLEncoder.encode("{\"userip\":\"\",\"series\":" + 45 + ",\"type\":1}");
        System.out.println(ext);
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
//        CompletableFuture<BaseModel<SeriesReplyGuideTextResult>> sereisReplyGuideInfo = opsCardApiClient.getSereisReplyGuideInfo();

        loopSeries(totalMinutes, seriesId -> {
            SeriesTabDto dto = new SeriesTabDto();
            dto.setSeriesId(seriesId);
            AtomicInteger apiError = new AtomicInteger(0);

            SeriesTabDto oldDto = baseGet(makeParam(seriesId));
            if (oldDto == null) {
                oldDto = new SeriesTabDto();
            }

            //参你喜欢
            String ext = URLEncoder.encode("{\"userip\":\"\",\"series\":" + seriesId + ",\"type\":1}");
            SeriesTabDto finalOldDto = oldDto;
            CompletableFuture<Void> recommendFuture = dataOpenApiClient.getRecommendLikeSeriesList(90100099, 5, ext).thenAccept(userlike -> {

                //无论是否异常，没有数据就认为没有tab
                if (userlike == null || userlike.getReturncode() != 0) {
                    dto.setHasRecommondLikeData(0);
                } else if (userlike != null && userlike.getResult() != null && userlike.getResult().getItemlist() != null
                        && userlike.getResult().getItemlist().size() > 0) {

                    boolean flag = userlike.getResult().getItemlist().stream().anyMatch(item -> item.getResourceobj() != null
                            && item.getResourceobj().getBiz_type() == 54
                            && item.getObjlist() != null && !item.getObjlist().isEmpty());

                    dto.setHasRecommondLikeData(flag ? 1 : 0);
                }
            }).exceptionally(e -> {
                dto.setHasRecommondLikeData(finalOldDto.getHasRecommondLikeData());
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            //口碑
            CompletableFuture<Void> koubeiFuture = koubeiApiClient.getSeriesHasKoubei(seriesId).thenAccept(koubei -> {
                if (koubei == null || koubei.getReturncode() != 0) {
                    dto.setHasKouBeiData(finalOldDto.getHasKouBeiData());
                    return;
                }

                if (koubei != null && koubei.getResult() != null && koubei.getResult().isIsshow()) {
                    dto.setHasKouBeiData(1);
                }
            }).exceptionally(e -> {
                dto.setHasKouBeiData(finalOldDto.getHasKouBeiData());
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            //车系热评
//            List<String> seriesIds = Arrays.stream(seriesHotReplySeriesIds.split(",")).toList();
            CompletableFuture<Void> replyFuture = replyApiClient.getSereisReplyCount(seriesId).thenAccept(reply -> {
                if (reply == null || reply.getReturncode() != 0) {
                    dto.setHasReplyData(finalOldDto.getHasReplyData());
                    return;
                }

                if (reply != null && reply.getResult() != null && reply.getResult() > 0) {
                    dto.setHasReplyData(reply.getResult());
                }
            }).exceptionally(e -> {
                dto.setHasReplyData(finalOldDto.getHasReplyData());
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            CompletableFuture.allOf(recommendFuture, koubeiFuture, replyFuture).join();

            update(makeParam(seriesId), dto);
        }, xxlLog);
    }
}
