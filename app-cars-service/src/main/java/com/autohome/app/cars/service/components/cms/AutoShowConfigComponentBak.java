//package com.autohome.app.cars.service.components.cms;
//
//import com.autohome.app.cars.apiclient.cms.CmsApiClient;
//import com.autohome.app.cars.common.utils.ExceptionUtil;
//import com.autohome.app.cars.common.utils.JsonUtil;
//import com.autohome.app.cars.common.utils.ThreadUtil;
//import com.autohome.app.cars.mapper.popauto.SeriesMapper;
//import com.autohome.app.cars.service.common.BaseComponent;
//import com.autohome.app.cars.service.common.DBConfig;
//import com.autohome.app.cars.service.components.cms.dtos.AutoShowConfigDto;
//import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.TreeMap;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
///**
// * 车展相关 -- 沈巨明
// */
//@Component
//@DBConfig(tableName = "auto_show_config")
//public class AutoShowConfigComponent extends BaseComponent<AutoShowConfigDto> {
//
//    static String paramName = "autoShowId";
//
//    @Autowired
//    SeriesMapper seriesMapper;
//
//    @Autowired
//    CmsApiClient cmsApiClient;
//
//    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
//    AutoShowConfig autoShowConfig;
//
//    TreeMap<String, Object> makeParam(int autoShowId) {
//        return ParamBuilder.create(paramName, autoShowId).build();
//    }
//
//    public CompletableFuture<AutoShowConfigDto> get(int autoShowId) {
//        if (autoShowConfig.IsBetweenDate()) {
//            return baseGetAsync(makeParam(autoShowId));
//        }
//        return CompletableFuture.completedFuture(null);
//    }
//
//    public void refreshAll(Consumer<String> log) {
//        log.accept("车展配置：" + JsonUtil.toString(autoShowConfig));
//        if (!autoShowConfig.IsBetweenDate()) {
//            log.accept("不在车展时间内");
//            return;
//        }
//
//        AutoShowConfigDto configDto = new AutoShowConfigDto();
//        //51 新车上市、52 首发新车
//        Arrays.asList(51, 52).forEach(x -> {
//            cmsApiClient.getAutoShowConfig(autoShowConfig.getAutoshowid(), x).thenAccept(data -> {
//                if (data == null || data.getReturncode() != 0 || data.getResult() == null || data.getResult().size() == 0) {
//                    return;
//                }
//
//                String seriesIds = data.getResult().get(0).getField1() == null ? "" : data.getResult().get(0).getField1();
//                if (x == 51) {
//                    configDto.setNewCarSeriesList(Arrays.stream(seriesIds.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
//                } else {
//                    configDto.setFirstCarSeriesList(Arrays.stream(seriesIds.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
//                }
//            }).exceptionally(e -> {
//                log.accept(x + "失败:" + ExceptionUtil.getStackTrace(e));
//                return null;
//            }).join();
//            ThreadUtil.sleep(10);
//        });
//
//        update(makeParam(autoShowConfig.getAutoshowid()), configDto);
//
//        log.accept("刷新车展配置成功");
//    }
//}
