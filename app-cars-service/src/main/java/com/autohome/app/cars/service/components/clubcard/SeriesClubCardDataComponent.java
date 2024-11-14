package com.autohome.app.cars.service.components.clubcard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.autohome.app.cars.apiclient.clubcard.ClubCardApiClient;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubCardDataResult;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubCardTagResult;
import com.autohome.app.cars.apiclient.clubcard.dtos.SeriesClubTopicListResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
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
@DBConfig(tableName = "series_club_card_data")
public class SeriesClubCardDataComponent extends BaseComponent<List<SeriesClubCardDataDto>> {

    private static final Logger logger = LoggerFactory.getLogger(SeriesClubCardDataComponent.class);

    static String paramName = "series_id";

    static String paramType = "tag_id";

    @Autowired
    ClubCardApiClient clubCardApiClient;

    @Autowired
    UserAndRzcComponent userAndRzcComponent;


    AtomicInteger errorCount = new AtomicInteger(0);

    public TreeMap<String, Object> makeParam(int seriesId, int tagId) {
        return ParamBuilder.create(paramName, seriesId).add(paramType, tagId).build();
    }

    public CompletableFuture<List<SeriesClubCardDataDto>> get(int seriesId, int tagId) {
        return baseGetAsync(makeParam(seriesId, tagId));
    }

    public CompletableFuture<Map<Integer, List<SeriesClubCardDataDto>>> mGet(List<TreeMap<String, Object>> params) {

        return baseGetListAsync(params).thenApply(x -> {
            List<List<SeriesClubCardDataDto>> list = x;
            Map<Integer, List<SeriesClubCardDataDto>> map = new HashMap<>();
            for (int i = 0; i < params.size(); i++) {
                Integer tagId = Integer.parseInt(params.get(i).get(paramType).toString());
                List<SeriesClubCardDataDto> value = list.get(i);
                if (null != tagId && tagId > 0) {
                    map.put(tagId, value);
                }
            }
            return map;
        });

    }


    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> {
            BaseModel<SeriesClubCardTagResult> tags = clubCardApiClient.getClubKoubeiTagConfig(seriesId).join();
            List<String> showTags = Arrays.asList("选买对比", "价格讨论", "提车作业", "用车感受");

            if (null != tags || null != tags.getResult() || 0 == tags.getReturncode()) {

                tags.getResult().getTagInfoList().removeIf(e -> !showTags.contains(e.getTagName()));

                for (SeriesClubCardTagResult.TagInfo tag : tags.getResult().getTagInfoList()) {
                    refreshOne(seriesId, tag.getTagId()).exceptionally(e -> {
                        xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                        logger.error("series_club_tag同步失败" + seriesId, e);
                        errorCount.incrementAndGet();
                        return null;
                    });
                }
            }

        }, xxlLog);
    }

    CompletableFuture<List<SeriesClubCardDataDto>> refreshOne(int seriesId, int tagId) {
        return clubCardApiClient.getClubKoubeiData(seriesId, tagId).thenApply(data -> {

            if (data == null || null == data.getResult() || 0 != data.getReturncode()) {
                return null;
            }

            SeriesClubCardDataResult cardDataResult = data.getResult();

            List<SeriesClubCardDataDto> list = new ArrayList<>();
            for (SeriesClubCardDataResult.Items itemSrc : cardDataResult.getItems()) {

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

            update(makeParam(seriesId, tagId), list);
            return null;
        });
    }


}
