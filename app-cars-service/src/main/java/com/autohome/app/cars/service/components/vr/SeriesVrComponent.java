package com.autohome.app.cars.service.components.vr;

import com.autohome.app.cars.apiclient.vr.PanoApiClient;
import com.autohome.app.cars.apiclient.vr.VrApiClient;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.apiclient.vr.dtos.VrSuperCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 车系头部vr -- 李广朋
 */
@Component
@DBConfig(tableName = "series_vr")
public class SeriesVrComponent extends BaseComponent<SeriesVr> {

    static String paramName = "seriesId";

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    PanoApiClient panoApiClient;

    @Autowired
    VrApiClient vrApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesVr> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public CompletableFuture<List<SeriesVr>> getList(List<Integer> seriesIds) {
        if(seriesIds==null||seriesIds.size()==0){
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        List<TreeMap<String,Object>> params = seriesIds.stream().map(x->makeParam(x)).collect(Collectors.toList());
        return baseGetListAsync(params);
    }

    public CompletableFuture<Map<Integer,SeriesVr>> getMap(List<Integer> seriesIds) {
        return getList(seriesIds).thenApply(x->{
            if(Objects.isNull(x)){
                return new HashMap<>();
            }
            x.removeIf(Objects::isNull);
           return x.stream().collect(Collectors.toMap(SeriesVr::getSeriesId,v->v,(v1,v2)->v2));
        });
    }

    public void refreshAll(Consumer<String> log) {
        List<Integer> seriesIds = seriesMapper.getAllSeriesIds();
        AtomicInteger successCount = new AtomicInteger(0);
        long st = System.currentTimeMillis();

//        seriesIds = new ArrayList<>() {{
//            add(15);
//        }};

        //超级vr
        CompletableFuture<BaseModel<List<VrSuperCarResult>>> superCarFuture = vrApiClient.getSuperCarList();

        for (Integer seriesId : seriesIds) {
            List<CompletableFuture> tasks = new ArrayList<>();
            SeriesVr seriesVr = new SeriesVr();
            seriesVr.setSeriesId(seriesId);

            SeriesVr oldVr = baseGet(makeParam(seriesId));

            //普通外观vr
            tasks.add(panoApiClient.getSeriesExterior(seriesId).thenAccept(data -> {
                //为null或者returncode!=0的时候，说明原接口异常，这时候不处理这条数据
                if (data == null) {
                    if (oldVr != null) {
                        seriesVr.setVrMaterial(oldVr.getVrMaterial());
                    }
                    return;
                }

                if (data.getResult() == null) {
                    seriesVr.setVrMaterial(null);
                } else {
                    if (data.getResult().getColor_list() != null && data.getResult().getColor_list().size() > 0) {
                        data.getResult().setShowtype(1);
                        data.getResult().setIscloud(0);
                        data.getResult().setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/series_vrbg1_1208.jpg");

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
//                        if (newColorList.size() > 0) {
//                            newColorList = newColorList.subList(0, 1);
//                        }
                        data.getResult().setColor_list(newColorList);
                    } else {
                        seriesVr.setVrMaterial(new SeriesVrExteriorResult());
                    }

                    seriesVr.setVrMaterial(data.getResult());
                }
            }).exceptionally(e -> {
                log.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }));

            //h5vr
            tasks.add(vrApiClient.getVrInfo(seriesId).thenAccept(data -> {
                if (data == null) {
                    if (oldVr != null) {
                        seriesVr.setH5Vr(oldVr.getH5Vr());
                    }
                    return;
                }
                if (Objects.isNull(data.getResult())
                        || (CollectionUtils.isEmpty(data.getResult().getIntInfo())
                        && CollectionUtils.isEmpty(data.getResult().getExtInfo()))) {
                    seriesVr.setH5Vr(null);
                    return;
                }

                SeriesVr.VrH5Info h5Info = new SeriesVr.VrH5Info();
                h5Info.setHasExterior(data.getResult().isHasExterior());
                h5Info.setHasInterior(data.getResult().isHasInterior());
                if (!CollectionUtils.isEmpty(data.getResult().getExtInfo())) {
                    SeriesVr.VrH5Info.ExtInfoDTO extInfo = new SeriesVr.VrH5Info.ExtInfoDTO();
                    extInfo.setNarration(data.getResult().getExtInfo().get(0).getNarration());
                    extInfo.setSpecId(data.getResult().getExtInfo().get(0).getSpecId());
                    extInfo.setCoverUrl(data.getResult().getExtInfo().get(0).getCoverUrl());
                    extInfo.setIs_show(data.getResult().getExtInfo().get(0).getIs_show());
                    extInfo.setShowUrl(data.getResult().getExtInfo().get(0).getShowUrl());
                    h5Info.setExtInfo(extInfo);
                }
                if (!CollectionUtils.isEmpty(data.getResult().getIntInfo())) {
                    SeriesVr.VrH5Info.IntInfoDTO intInfo = new SeriesVr.VrH5Info.IntInfoDTO();
                    intInfo.setNarration(data.getResult().getIntInfo().get(0).getNarration());
                    intInfo.setSpecId(data.getResult().getIntInfo().get(0).getSpecId());
                    intInfo.setCoverUrl(data.getResult().getIntInfo().get(0).getCoverUrl());
                    intInfo.setIs_show(data.getResult().getIntInfo().get(0).getIs_show());
                    intInfo.setShowUrl(data.getResult().getIntInfo().get(0).getShowUrl());
                    h5Info.setIntInfo(intInfo);
                }

                seriesVr.setH5Vr(h5Info);
            }).exceptionally(e -> {
                log.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }));

            //全景/实景vr
            tasks.add(panoApiClient.getSeriesRealScene(seriesId).thenAccept(data -> {
                //为null或者returncode!=0的时候，说明原接口异常，这时候不处理这条数据
                if (data == null || data.getReturncode() != 0) {
                    if (oldVr != null) {
                        seriesVr.setRealScene(oldVr.getRealScene());
                    }
                    return;
                }
                if (data.getResult() == null || data.getResult().size() == 0) {
                    seriesVr.setRealScene(null);
                } else {
                    SeriesVr.VrRealScene realScene = new SeriesVr.VrRealScene();
                    realScene.setSeries_id(data.getResult().get(0).getSeries_id());
                    realScene.setShow_url(data.getResult().get(0).getShow_url());
                    realScene.setCover_url(data.getResult().get(0).getCover_url());
                    realScene.setScene_url(data.getResult().get(0).getScene_url());
                    seriesVr.setRealScene(realScene);
                }
            }).exceptionally(e -> {
                log.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }));

            //超级vr
            tasks.add(superCarFuture.thenAccept(data -> {
                //为null或者returncode!=0的时候，说明原接口异常，这时候不处理这条数据
                if (data == null || data.getReturncode() != 0) {
                    if (oldVr != null) {
                        seriesVr.setSuperCarList(oldVr.getSuperCarList());
                    }
                    return;
                }

                if (data.getResult() == null) {
                    seriesVr.setSuperCarList(null);
                } else {
                    List<SeriesVr.VrSuperCar> carList = new ArrayList<>();
                    data.getResult().stream().filter(item -> item.getSpecidinfo().containsKey(seriesId)).forEach(item -> {
                        SeriesVr.VrSuperCar vrSuperCar = new SeriesVr.VrSuperCar();
                        vrSuperCar.setId(item.getId());
                        vrSuperCar.setExhibitionType(item.getExhibitionType());
                        vrSuperCar.setTitle(item.getTitle());
                        vrSuperCar.setUrl(item.getUrl());
                        vrSuperCar.setTerminal(item.getTerminal());
                        vrSuperCar.setPosition(item.getPosition());
                        vrSuperCar.setSpecidinfo(item.getSpecidinfo());

                        carList.add(vrSuperCar);
                    });

                    seriesVr.setSuperCarList(carList);
                }
            }).exceptionally(e -> {
                log.accept("失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }));

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

            if (seriesVr.getVrMaterial() != null || seriesVr.getRealScene() != null
                    || (seriesVr.getSuperCarList() != null && seriesVr.getSuperCarList().size() > 0)) {
                update(makeParam(seriesId), seriesVr);
            } else {
                delete(makeParam(seriesId));
            }
            if (successCount.incrementAndGet() % 100 == 0) {
                log.accept("当前：" + seriesId + "--" + successCount.get() + "，共用时：" + (System.currentTimeMillis() - st) / 1000);
            }

            ThreadUtil.sleep(10);
        }
        log.accept("共计成功：" + successCount.get());
    }
}