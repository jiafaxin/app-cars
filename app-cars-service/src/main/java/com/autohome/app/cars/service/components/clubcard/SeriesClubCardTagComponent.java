package com.autohome.app.cars.service.components.clubcard;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.autohome.app.cars.apiclient.clubcard.ClubCardApiClient;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubCardTagResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardTagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wbs
 * @date 2024/4/24
 */
@Component
@DBConfig(tableName = "series_club_card_tag")
public class SeriesClubCardTagComponent extends BaseComponent<SeriesClubCardTagDto> {

    private static final Logger logger = LoggerFactory.getLogger(SeriesClubCardTagComponent.class);

    static String paramName = "seriesId";

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    ClubCardApiClient clubNewApiClient;


    AtomicInteger errorCount = new AtomicInteger(0);

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public SeriesClubCardTagDto get(int seriesId) {
        return baseGet(makeParam(seriesId));
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

    CompletableFuture<SeriesClubCardTagDto> refreshOne(int seriesId) {
        return clubNewApiClient.getClubKoubeiTagConfig(seriesId).thenApply(data -> {

            if (data == null || null == data.getResult() || 0 != data.getReturncode()) {
                return null;
            }
            List<String> showTags = Arrays.asList("选买对比", "价格讨论", "提车作业", "用车感受");
            data.getResult().getTagInfoList().removeIf(e -> !showTags.contains(e.getTagName()));

            SeriesClubCardTagDto seriesClubTagDto = new SeriesClubCardTagDto();
            SeriesClubCardTagDto.ClubTab hot = new SeriesClubCardTagDto.ClubTab();

            hot.setName("热门");
            hot.setTabid(0);
            String schema = "autohome://club/topiclist?bbstype=c&seriesid=%s&select=1&sort=3&from=10";
            hot.setLinkurl(String.format(schema, seriesId));

            seriesClubTagDto.getTablist().add(hot);

            for (String tagName : showTags) {
                SeriesClubCardTagDto.ClubTab tag = new SeriesClubCardTagDto.ClubTab();
                Optional<SeriesClubCardTagResult.TagInfo> option = data.getResult().getTagInfoList().stream().filter(e -> tagName.equals(e.getTagName())).findFirst();

                if (option.isPresent()) {
                    tag.setTabid(option.get().getTagId());
                    String tagScheme = "autohome://club/topiclist?bbstype=c&seriesid=" + seriesId + "&select=1&thirdtagid=" + tag.getTabid() + "&from=10";
                    tag.setLinkurl(tagScheme);
                    tag.setName(tagName);
                    seriesClubTagDto.getTablist().add(tag);
                }
            }

            update(makeParam(seriesId), seriesClubTagDto);
            return null;
        });
    }




}
