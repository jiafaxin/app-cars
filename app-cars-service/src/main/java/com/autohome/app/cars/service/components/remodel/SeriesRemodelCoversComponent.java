package com.autohome.app.cars.service.components.remodel;

import com.autohome.app.cars.apiclient.remodel.RemodelApiClient;
import com.autohome.app.cars.apiclient.remodel.dtos.RemodelCoversResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.CarPhotoDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.remodel.dtos.SeriesRemodelCoversDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系改装封面图列表 -- 李李佳
 */
@Component
@DBConfig(tableName = "series_remodel_covers")
public class SeriesRemodelCoversComponent extends BaseComponent<SeriesRemodelCoversDto> {
    static String paramName = "seriesId";

    @Autowired
    RemodelApiClient remodelApiClient;

    @Autowired
    SpecDetailComponent specDetailComponent;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesRemodelCoversDto> get(int seriesId, int specId, int page, int size, int currentType, int currentId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(data -> {
            if (data == null) {
                return null;
            }
            SeriesRemodelCoversDto dto = new SeriesRemodelCoversDto();
            dto.setTotal(data.getTotal());

            int newPage = page;

            if (specId > 0) {
                List<SeriesRemodelCoversDto.ListDTO> specList = data.getList().stream().filter(x -> x.getSpec_id() == specId).toList();
                dto.setTotal(specList.size());
                if (currentId > 0 && currentType > 0) { //如果传了图片id，需要根据图片id重新计算翻页
                    int index = getIndex(specList, currentType, currentId);
                    if (index > 0) {
                        newPage = index / size + 1;
                    }
                }
                dto.setList(specList.stream().skip((newPage - 1) * size).limit(size).collect(Collectors.toList()));
            } else {
                if (currentId > 0 && currentType > 0) { //如果传了图片id，需要根据图片id重新计算翻页
                    int index = getIndex(data.getList(), currentType, currentId);
                    if (index > 0) {
                        newPage = index / size + 1;
                    }
                }
                dto.setList(data.getList().stream().skip((newPage - 1) * size).limit(size).collect(Collectors.toList()));
            }
            dto.setPageIndex(newPage);
            return dto;
        });
    }

    public int getIndex(List<SeriesRemodelCoversDto.ListDTO> datas, int type, int id) {
        int i = 0;
        for (SeriesRemodelCoversDto.ListDTO data : datas) {
            if (data.getBiz_id() == id) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void refreshAll(Consumer<String> xxlLog) {
        try {
            Set<RemodelCoversResult.ListDTO> list = new LinkedHashSet<>();
            int pageIndex = 1;
            int pageSize = 100;
            while (pageIndex < 200) {
                long s = System.currentTimeMillis();
                //自己写的全量接口拉去数据
                RemodelCoversResult result = remodelApiClient.getAppUsercarRefitCover((pageIndex - 1) * pageSize, pageSize).exceptionally(e -> {
                    xxlLog.accept("失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join();
                pageIndex++;
                if (result != null && result.getList() != null && !result.getList().isEmpty()) {
                    list.addAll(result.getList());
                    if (list.size() == result.getTotal()) {
                        break;
                    }
                }

                Thread.sleep(50);
            }

            if (list.isEmpty()) {
                return;
            }

            list.stream().collect(Collectors.groupingBy(RemodelCoversResult.ListDTO::getSeries_ids, LinkedHashMap::new, Collectors.toList())).forEach((seriesId, items) -> {
                SeriesRemodelCoversDto dto = new SeriesRemodelCoversDto();
                dto.setTotal(items.size());
                dto.setList(items.stream().map(p -> {
                    SeriesRemodelCoversDto.ListDTO item = new SeriesRemodelCoversDto.ListDTO();
                    item.setSpec_name(p.getSpec_names().get(0));
                    item.setApp_url(p.getApp_url());
                    item.setImg_url(p.getImg_url());
                    item.setBiz_id(p.getBiz_id() == null ? 0 : p.getBiz_id());
                    item.setSpec_id(p.getSpec_ids().get(0) == null ? 0 : p.getSpec_ids().get(0));
                    item.setCommunity_width(p.getCommunity_width() == null ? 0 : p.getCommunity_width());
                    item.setCommunity_height(p.getCommunity_height() == null ? 0 : p.getCommunity_height());
//                    item.setSpecState(specMap.get(p.getSpec_ids().get(0)));
                    return item;
                }).collect(Collectors.toList()));

                update(makeParam(seriesId.get(0)), dto);
            });
        } catch (Exception ex) {
            xxlLog.accept("失败:" + ExceptionUtil.getStackTrace(ex));
        }
    }
}
