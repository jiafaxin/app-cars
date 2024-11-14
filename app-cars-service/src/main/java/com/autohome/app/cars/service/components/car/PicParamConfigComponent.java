package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.mapper.popauto.CarPhotoMapper;
import com.autohome.app.cars.mapper.popauto.ColorMapper;
import com.autohome.app.cars.mapper.popauto.entities.PicColorEntity;
import com.autohome.app.cars.mapper.popauto.entities.PicParamConfigEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.ColorDto;
import com.autohome.app.cars.service.components.car.dtos.ColorStatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class PicParamConfigComponent extends BaseComponent<List<PicParamConfigEntity>> {

    @Autowired
    CarPhotoMapper photoMapper;

    final static String paramName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<Map<Integer,List<PicParamConfigEntity>>> getMap(int seriesId) {
        return baseGetAsync(makeParam(seriesId)).thenApply(x -> {
            if (x == null || x.size() == 0) {
                return new HashMap<>();
            }
            x.sort(Comparator.comparing(PicParamConfigEntity::getDataType).thenComparing(PicParamConfigEntity::getSubItemOrderCls));
            return x.stream().collect(Collectors.groupingBy(y -> y.getPicId()));
        });
    }


    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(10, seriesId -> {
            List<PicParamConfigEntity> list = photoMapper.getPicParamConfig(seriesId);
            list.forEach(x->{
                if(x.getDataType()!=1){
                    return;
                }
                x.setName(getNameByParamId(x.getItemId()));
            });
            if (list == null || list.size() == 0) {
                delete(makeParam(seriesId));
            }else {
                update(makeParam(seriesId), list);
            }
        }, xxlLog);
    }


    public static  String getNameByParamId(int Id){
        String name = "";
        switch (Id){
            case 15:
                name="最大功率(kW)";
                break;
            case 17:
                name="最大扭矩(N·m)";
                break;
            case 28:
                name="长度(mm)";
                break;
            case  29:
                name="宽度(mm)";
                break;
            case 30:
                name="高度(mm)";
                break;
            case 31:
                name="轴距(mm)";
                break;
            case 39:
                name="行李厢容积(L)";
                break;
            case 43:
                name="前悬架类型";
                break;
            case 44:
                name="后悬架类型";
                break;
            case 49:
                name="前轮胎规格";
                break;
            case 50:
                name="后轮胎规格";
                break;
            case 53:
                name="备胎规格";
                break;
            case 61:
                name="工信部纯电续航里程(km)";
                break;
            case 90:
                name="能源类型";
                break;
            case 97:
                name="官方0-100km/h加速(s)";
                break;
            case 100:
                name="变速箱类型";
                break;
            case 106:
                name="整备质量(kg)";
                break;
        }
        return  name;
    }
}
