package com.autohome.app.cars.service.components.clubcard;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.autohome.app.cars.apiclient.clubcard.ClubCardApiClient;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubTopicListResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardDataDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wbs
 * @date 2024/4/24
 */
@Component
@DBConfig(tableName = "series_club_card_hot")
public class SeriesClubCardHotComponent extends BaseComponent<List<SeriesClubCardDataDto>> {

    private static final Logger logger = LoggerFactory.getLogger(SeriesClubCardHotComponent.class);

    static String paramName = "seriesId";

    @Autowired
    ClubCardApiClient clubCardApiClient;

    @Autowired
    UserAndRzcComponent userAndRzcComponent;


    AtomicInteger errorCount = new AtomicInteger(0);

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<List<SeriesClubCardDataDto>> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }


    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {

        loopSeries(totalMinutes, seriesId -> {
            refreshOne(seriesId).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                logger.error("series_club_tag同步失败" + seriesId, e);
                errorCount.incrementAndGet();
                return null;
            });
        }, xxlLog);
    }

    CompletableFuture<List<SeriesClubCardDataDto>> refreshOne(int seriesId) {

        return clubCardApiClient.getClubTopicsList(seriesId).thenApply(data -> {

            if (data == null || null == data.getResult() || 0 != data.getReturncode()) {
                return null;
            }

            SeriesClubTopicListResult seriesClubTopicList = data.getResult();

            List<SeriesClubCardDataDto> list = new ArrayList<>();
            for (SeriesClubTopicListResult.Items itemSrc : seriesClubTopicList.getItems()) {

                if (list.size() >= 5) {
                    break;
                }
                SeriesClubCardDataDto target = new SeriesClubCardDataDto();

                BeanUtils.copyProperties(itemSrc, target);

                if (20 == target.getClub_is_poll() && StringUtils.isEmpty(target.getSubtitle()) && StringUtils.isEmpty(target.getSummary()) && StringUtils.isEmpty(target.getImgList())) {
                    continue;
                }

                //补充用户和认证信息
                int userId = itemSrc.getAuthor_id();

                userAndRzcComponent.setUserAndRzcInfo(target, userId);

                list.add(target);

            }

            update(makeParam(seriesId), list);

            return null;
        });
    }


}
