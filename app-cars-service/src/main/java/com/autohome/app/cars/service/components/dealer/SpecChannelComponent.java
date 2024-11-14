package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.dealer.ChannelApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecChannelReq;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecChannelResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SpecChannelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/4/26
 */
@Component
@DBConfig(tableName = "spec_channel")
@Slf4j
public class SpecChannelComponent extends BaseComponent<SpecChannelDto> {


    static String paramName = "specId";

    /**
     * 无效的渠道Id，需要排除掉
     */
    private static final List<Integer> invalidChannelIdList
            = List.of(144, 173, 177, 240, 268, 35, 136, 137, 170, 204, 209, 218, 236);

    @SuppressWarnings("all")
    @Autowired
    ChannelApiClient channelApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return BaseComponent.ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<SpecChannelDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        try {
            SpecChannelReq req = new SpecChannelReq();
            req.setPageIndex(1);
            req.setPageSize(20);
            SpecChannelResult result = channelApiClient.getChannelInfoList(req).join();
            if (Objects.isNull(result)
                    || Objects.isNull(result.getResult())
                    || result.getResult().getPageCount() == 0
                    || CollectionUtils.isEmpty(result.getResult().getRows())) {
                xxlLog.accept("查询不到数据");
                return;
            }
            List<SpecChannelResult> resultList = new ArrayList<>();
            for (int pageIndex = 1; pageIndex <= result.getResult().getPageCount(); pageIndex++) {
                req = new SpecChannelReq();
                req.setPageIndex(pageIndex);
                req.setPageSize(20);
                result = channelApiClient.getChannelInfoList(req).join();
                if (Objects.nonNull(result)
                        && Objects.nonNull(result.getResult())
                        && !CollectionUtils.isEmpty(result.getResult().getRows())) {
                    resultList.add(result);
                }
            }

            Map<TreeMap<String, Object>, SpecChannelDto> datas = new HashMap<>();
            // 本次新计算得到的key，用于删除历史数据
            Set<String> newKeys = new HashSet<>();

            resultList.stream()
                    .map(SpecChannelResult::getResult)
                    .flatMap(r -> r.getRows().stream())
                    .filter(r -> !invalidChannelIdList.contains(r.getChannelId()) // 排除无效的渠道Id
                            && r.getFortest() != 1 // 排除测试渠道
                            && r.getStatus() != 2) // 排除停用的渠道
                    .forEach(row -> row.getSpecList()
                            .forEach(spec -> {
                                SpecChannelDto specChannelDto = new SpecChannelDto();
                                specChannelDto.getChannelIdList().add(row.getChannelId());
                                datas.put(makeParam(spec.getSpecId()), specChannelDto);
                                if (datas.size() % 1000 == 0) {
                                    xxlLog.accept("now:" + datas.size());
                                }
                            }));

            updateBatch(datas);

            newKeys.addAll(datas.keySet().stream().map(this::getKey).collect(Collectors.toSet()));
            // 删除历史无效数据
            deleteHistory(new HashSet<>(newKeys), xxlLog);
        } catch (Exception e) {
            xxlLog.accept("车型-渠道数据更新失败:" + ExceptionUtil.getStackTrace(e));
        }
    }


}
