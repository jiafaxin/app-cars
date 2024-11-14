package com.autohome.app.cars.service.components.vr;

//import cn.hutool.core.bean.BeanUtil;

import com.autohome.app.cars.apiclient.vr.VrApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.apiclient.vr.dtos.SpecVrInfoResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.club.dtos.SeriesClub;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author chengjincheng
 * @date 2024/3/1
 */
@Component
@DBConfig(tableName = "spec_vr")
public class SpecVrComponent extends BaseComponent<SpecVrInfoDto> {

    static String paramName = "specId";

    @SuppressWarnings("all")
    @Autowired
    VrApiClient vrApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(paramName, specId).build();
    }

    public CompletableFuture<SpecVrInfoDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public SpecVrInfoDto getSync(int specId) {
        return baseGet(makeParam(specId));
    }

   /* public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpec(totalMinutes, specId -> vrApiClient.getSpecVrInfo(specId)
                .thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return;
                    }
                    if (Objects.isNull(data.getResult())
                            || (CollectionUtils.isEmpty(data.getResult().getIntInfo())
                            && CollectionUtils.isEmpty(data.getResult().getExtInfo()))) {
                        delete(makeParam(specId));
                        return;
                    }

                    SpecVrInfoResult result = data.getResult();
                    SpecVrInfoDto dto = new SpecVrInfoDto();
                    dto.setHasExterior(result.isHasExterior());
                    dto.setIntInfo(new ArrayList<>());
                    dto.setExtInfo(new ArrayList<>());
                    if (!CollectionUtils.isEmpty(result.getIntInfo())) {
                        result.getIntInfo().forEach(e -> {
                            SpecVrInfoDto.SNewsSeriesVrInfo_IntInfo intInfo =
                                    new SpecVrInfoDto.SNewsSeriesVrInfo_IntInfo();
                            intInfo.setCoverUrl(e.getCoverUrl());
                            dto.getIntInfo().add(intInfo);
                        });
                    }
                    if (!CollectionUtils.isEmpty(result.getExtInfo())) {
                        result.getExtInfo().forEach(e -> {
                            SpecVrInfoDto.SNewsSeriesVrInfo_ExtInfo extInfo =
                                    new SpecVrInfoDto.SNewsSeriesVrInfo_ExtInfo();
                            extInfo.setCoverUrl(e.getCoverUrl());
                            extInfo.setIs_show(e.getIs_show());
                            dto.getExtInfo().add(extInfo);
                        });
                    }
                    update(makeParam(specId), dto);
                }).exceptionally(e -> {
                    xxlLog.accept(specId + "失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join(), xxlLog);
    }*/

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSpec(totalMinutes, specId -> refreshOne(specId, xxlLog), xxlLog);
    }

    void refreshOne(int specId, Consumer<String> xxlLog) {
        List<CompletableFuture> tasks = new ArrayList<>();
        SpecVrInfoDto dto = new SpecVrInfoDto();

        SpecVrInfoDto oldVr = baseGet(makeParam(specId));

        tasks.add(vrApiClient.getSpecVrInfo(specId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                if (oldVr != null) {
                    dto.setHasExterior(oldVr.isHasExterior());
                    dto.setIntInfo(oldVr.getIntInfo());
                    dto.setExtInfo(oldVr.getExtInfo());
                }
                return;
            }
            if (Objects.isNull(data.getResult())
                    || (CollectionUtils.isEmpty(data.getResult().getIntInfo())
                    && CollectionUtils.isEmpty(data.getResult().getExtInfo()))) {
                dto.setHasExterior(false);
                dto.setIntInfo(new ArrayList<>());
                dto.setExtInfo(new ArrayList<>());
                return;
            }

            SpecVrInfoResult result = data.getResult();
            dto.setHasExterior(result.isHasExterior());
            dto.setIntInfo(new ArrayList<>());
            dto.setExtInfo(new ArrayList<>());
            if (!CollectionUtils.isEmpty(result.getIntInfo())) {
                result.getIntInfo().forEach(e -> {
                    SpecVrInfoDto.SNewsSeriesVrInfo_IntInfo intInfo =
                            new SpecVrInfoDto.SNewsSeriesVrInfo_IntInfo();
                    intInfo.setCoverUrl(e.getCoverUrl());
                    intInfo.setShowUrl(e.getShowUrl());
                    intInfo.setIs_show(e.getIs_show());
                    intInfo.setNarration(e.getNarration());
                    dto.getIntInfo().add(intInfo);
                });
            }
            if (!CollectionUtils.isEmpty(result.getExtInfo())) {
                result.getExtInfo().forEach(e -> {
                    SpecVrInfoDto.SNewsSeriesVrInfo_ExtInfo extInfo =
                            new SpecVrInfoDto.SNewsSeriesVrInfo_ExtInfo();
                    extInfo.setCoverUrl(e.getCoverUrl());
                    extInfo.setIs_show(e.getIs_show());
                    extInfo.setNarration(e.getNarration());
                    dto.getExtInfo().add(extInfo);
                });
            }
        }).exceptionally(e -> {
            xxlLog.accept(specId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }));

        tasks.add(vrApiClient.getSpecExterior(specId).thenAccept(data -> {
            if (data == null) {
                if (oldVr != null) {
                    dto.setVrMaterial(oldVr.getVrMaterial());
                }
                return;
            }

            if (data.getResult() == null) {
                dto.setVrMaterial(null);
            } else {
                if (data.getResult().getColor_list() != null && data.getResult().getColor_list().size() > 0) {
                    data.getResult().setShowtype(1);
                    data.getResult().setIscloud(0);
                    data.getResult().setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/vrbgimg_default_0324.png.webp");

                    List<SeriesVrExteriorResult.Color_List> newColorList = new ArrayList<>();
                    for (SeriesVrExteriorResult.Color_List item : data.getResult().getColor_list()) {
                        if (item.getColorValues() != null && !"".equals(item.getColorValues())
                                && item.getColorNames() != null && !"".contentEquals(item.getColorNames())) {
                            item.setColorName(item.getColorNames());
                            item.setColorValue(item.getColorValues());
                            newColorList.add(item);
                        } else {
                            newColorList.add(item);
                        }
                    }
                    data.getResult().setColor_list(newColorList);
                    dto.setVrMaterial(data.getResult());
                } else {
                    dto.setVrMaterial(null);
                }
            }
        }).exceptionally(e -> {
            xxlLog.accept(specId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        if (!dto.getIntInfo().isEmpty() || !dto.getExtInfo().isEmpty() || dto.getVrMaterial() != null) {
            update(makeParam(specId), dto);
        } else {
            delete(makeParam(specId));
        }
    }
}
