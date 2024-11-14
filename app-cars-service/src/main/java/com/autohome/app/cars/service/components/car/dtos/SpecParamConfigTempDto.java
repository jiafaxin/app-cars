package com.autohome.app.cars.service.components.car.dtos;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class SpecParamConfigTempDto {

    ParamTemp param;

    ConfigTemp config;


    //新能源专有项，油车不显示
    public static List<String> listNewEnergyParam = Arrays.asList("快充电量百分比", "纯电续航里程(km)", "NEDC纯电续航里程(km)", "WLTP纯电续航里程(km)", "工信部纯电续航里程(km)", "电池充电时间", "充电桩价格", "实测快充时间(小时)", "实测慢充时间(小时)", "快充时间(小时)", "慢充时间(小时)", "实测续航里程(km)");
    //都是油车情况下不显示的电车大类
    public static final List<String> electricTypeList = ImmutableList.of("电动机", "电池/续航", "充/放电");

    public static final List<String> electricSecondTypeList = Arrays.asList("电动机(Ps)","电动机总功率(kW)","电动机总扭矩(N·m)");

    /**
     * 纯电动车型不显示:
     * "充电桩价格", "系统综合功率(kW)", "系统综合扭矩(N·m)"
     */
    public static List<String> listNotDisPlayOfPEVCarParam = ImmutableList.of("油箱容积(L)", "NEDC综合油耗(L/100km)", "WLTC综合油耗(L/100km)", "四驱形式", "充电桩价格", "发动机", "变速箱", "系统综合功率(kW)", "系统综合扭矩(N·m)", "工信部综合油耗(L/100km)", "实测油耗(L/100km)", "环保标准");



    @Data
    public static class BaseTemp{
        int id;
        String name;
    }

    /**
     * 参数
     */
    @Data
    public static class ParamTemp{
        private List<ParamType> paramTypes = new ArrayList<>();
        private Map<String,String > itemMap =new HashMap<>();

        public List<ParamType> getParamTypes(int specCount, int evCount, int oilCount) {

            if(evCount==specCount){ //都是纯电不显示发动机
                return paramTypes.stream().filter(x->!x.equals("发动机")).collect(Collectors.toList());
            }
            if(oilCount == specCount){ //都是油车不显示电车的大类
                return paramTypes.stream().filter(x->!electricTypeList.contains(x.getName())).collect(Collectors.toList());
            }
            return paramTypes;
        }

        @Data
        public static class ParamType extends BaseTemp{
            private List<ParamItem> paramItems =new ArrayList<>();

            public List<ParamItem> getParamItems(int specCount, int newEnergyCount, int evCount, int oilCount) {
                if(getId()!=1){
                    return paramItems;
                }
                if(newEnergyCount==0){  //全部非新能源车
                    return paramItems.stream().filter(x->!listNewEnergyParam.contains(x.getName())).collect(Collectors.toList());
                }
                if(specCount==evCount){  //只有纯电车
                    return paramItems.stream().filter(x->!listNotDisPlayOfPEVCarParam.contains(x.getName())).collect(Collectors.toList());
                }
                if(specCount==oilCount){  //只有油车
                    return paramItems.stream().filter(x->!electricSecondTypeList.contains(x.getName())).collect(Collectors.toList());
                }
                return paramItems;
            }
        }

        @Data
        public static class ParamItem extends BaseTemp{
            int dataType;
            int displayType;
            int dynamicShow;
            int baikeId;
            String contentId;
            int playStartTime;

            public int getId(int firstLevelId,int fuelTypeDetail) {
                if(firstLevelId!=1){
                    return id;
                }
                if(id==52){
                    switch (fuelTypeDetail){
                        case 3:
                        case 5:
                            return 71;
                        case 4:
                        case 6:
                        case 7:
                        case 12:
                            return 65;
                    }
                }
                if(id==50){
                    switch (fuelTypeDetail){
                        case 3:
                        case 5:
                            return 70;
                        case 4:
                        case 6:
                        case 7:
                        case 12:
                            return 63;
                    }
                }
                return id;
            }
        }

    }

    @Data
    public static class ConfigTemp{
        private List<ConfigType> paramTypes = new ArrayList<>();
        private Map<String,String > itemMap =new HashMap<>();



        @Data
        public static class ConfigType extends BaseTemp{
            private String groupname;
            private List<ConfigItem> paramItems = new ArrayList<>();

        }

        @Data
        public static class ConfigItem extends BaseTemp{
            int dataType;
            int displayType;
            int dynamicShow;
            int baikeId;
            String contentId;
            int playStartTime;
            int cVIsShow;
            int isShow;
        }

    }
}
