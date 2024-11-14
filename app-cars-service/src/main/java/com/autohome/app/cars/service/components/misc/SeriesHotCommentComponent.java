package com.autohome.app.cars.service.components.misc;

import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.opscard.OpsCardApiClient;
import com.autohome.app.cars.apiclient.opscard.dtos.SeriesReplyGuideTextResult;
import com.autohome.app.cars.apiclient.reply.ReplyApiClient;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.misc.dtos.SeriesCityTabDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesHotCommentDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesTabDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@DBConfig(tableName = "series_hot_comment")
public class SeriesHotCommentComponent extends BaseComponent<SeriesHotCommentDto> {
    @Autowired
    OpsCardApiClient opsCardApiClient;

    @Autowired
    ReplyApiClient replyApiClient;

    @Autowired
    SeriesTabComponent seriesTabComponent;

    final static String seriesIdParamName = "seriesId";


    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesHotCommentDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        CompletableFuture<BaseModel<SeriesReplyGuideTextResult>> sereisReplyGuideFuture = opsCardApiClient.getSereisReplyGuideInfo();

        loopSeries(totalMinutes, seriesId -> {
            SeriesHotCommentDto dto = new SeriesHotCommentDto();
            AtomicInteger apiError = new AtomicInteger(0);
            SeriesHotCommentDto oldDto = baseGet(makeParam(seriesId));
            if (oldDto == null) {
                oldDto = new SeriesHotCommentDto();
            }

            SeriesHotCommentDto finalOldDto = oldDto;
            CompletableFuture<Void> replyFuture = replyApiClient.getSereisReplyCount(seriesId).thenAccept(reply -> {
                if (reply == null || reply.getReturncode() != 0) {
                    dto.setCount(finalOldDto.getCount());
                    return;
                }

                if (reply != null && reply.getResult() != null && reply.getResult() > 0) {
                    dto.setCount(reply.getResult());
                }
            }).exceptionally(e -> {
                dto.setCount(finalOldDto.getCount());
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            CompletableFuture<Void> seriesGuideFuture = sereisReplyGuideFuture.thenAccept(opsCard -> {
                if (opsCard == null || opsCard.getReturncode() != 0) {
                    dto.setGuideText(finalOldDto.getGuideText());
                    return;
                }

                if (opsCard.getResult() != null && opsCard.getResult().getCards() != null && !opsCard.getResult().getCards().isEmpty()) {
                    List<SeriesReplyGuideTextResult.CardsDTO.CellsDTO> cells = opsCard.getResult().getCards().get(0).getCells();
                    //取cells对象里的seriesIds包括seriesId的最后一个信息
                    cells.forEach(cell -> {
                        if (StringUtils.isNotEmpty(cell.getSeriesids())) {
                            List<String> seriesIdList = Arrays.stream(cell.getSeriesids().split(",")).collect(Collectors.toList());
                            if (seriesIdList.contains(String.valueOf(seriesId))) {
                                dto.setGuideText(cell.getGuidetext());
                            }
                        }
                    });
                }
            }).exceptionally(e -> {
                dto.setGuideText(finalOldDto.getGuideText());
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            });

            CompletableFuture.allOf(seriesGuideFuture, replyFuture).join();

            update(makeParam(seriesId), dto);
        }, xxlLog);
    }
}
