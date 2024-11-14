package com.autohome.app.cars.service.components.club;

import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.ClubGroupResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.club.dtos.SeriesClubGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Component
@DBConfig(tableName = "series_club_group")
public class SeriesClubGroupComponent extends BaseComponent<SeriesClubGroupDto> {


    @SuppressWarnings("all")
    @Autowired
    ClubApiClient clubApiClient;

    final static String paramName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesClubGroupDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> clubApiClient.getClubGroups(seriesId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                return;
            }
            if (data.getResult() == null
                    || data.getResult().getTagList() == null
                    || data.getResult().getTagList().isEmpty()) {
                delete(makeParam(seriesId));
                return;
            }

            ClubGroupResult result = data.getResult();
            SeriesClubGroupDto dto = new SeriesClubGroupDto();
            dto.setBbs(result.getBbs());
            dto.setBbsId(result.getBbsId());
            dto.setBbsName(result.getBbsName());
            dto.setTagList(new ArrayList<>());
            result.getTagList().forEach(e -> {
                SeriesClubGroupDto.TagListBean tagListBean = new SeriesClubGroupDto.TagListBean();
                tagListBean.setTagId(e.getTagId());
                tagListBean.setTagName(e.getTagName());
                tagListBean.setAllReplyCount(e.getAllReplyCount());
                tagListBean.setAllTopicCount(e.getAllTopicCount());
                tagListBean.setHourTopicReplyCount(e.getHourTopicReplyCount());
                dto.getTagList().add(tagListBean);
            });
            update(makeParam(seriesId), dto);
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }).join(), xxlLog);
    }
}
