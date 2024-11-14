package com.autohome.app.cars.service.services.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 2024/6/26
 * 车辆行情筛选项配置信息实体
 */
@Data
public class HangqingSearchOptionsGroupDto {
    /**
     * [
     *     {
     *         "key": "brand",
     *         "showname": "品牌",
     *         "isselectmore": 1,
     *         "list": [
     *             {
     *                 "name": "全部",
     *                 "value": "-1",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "比亚迪",
     *                 "value": "75",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "理想汽车",
     *                 "value": "345",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "奔驰",
     *                 "value": "36",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "奥迪",
     *                 "value": "33",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "特斯拉",
     *                 "value": "133",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "更多",
     *                 "value": "",
     *                 "key": "brand",
     *                 "parametername": "brand",
     *                 "childrenlist": []
     *             }
     *         ]
     *     },
     *     {
     *         "key": "price",
     *         "showname": "价格",
     *         "isselectmore": 0,
     *         "list": [
     *             {
     *                 "name": "全部",
     *                 "value": "-1",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "8万以下",
     *                 "value": "0|80000",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "8-15万",
     *                 "value": "80000|150000",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "15-20万",
     *                 "value": "150000|200000",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "20-30万",
     *                 "value": "200000|300000",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "30-50万",
     *                 "value": "300000|500000",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "50万以上",
     *                 "value": "500000|100000000",
     *                 "key": "price",
     *                 "parametername": "price",
     *                 "childrenlist": []
     *             }
     *         ]
     *     },
     *     {
     *         "key": "levelid",
     *         "showname": "级别",
     *         "isselectmore": 1,
     *         "list": [
     *             {
     *                 "name": "全部",
     *                 "value": "-1",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "SUV",
     *                 "value": "9",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": [
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "小型SUV",
     *                         "value": "16",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "紧凑型SUV",
     *                         "value": "17",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "中型SUV",
     *                         "value": "18",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "中大型SUV",
     *                         "value": "19",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "大型SUV",
     *                         "value": "20",
     *                         "parametername": "levelid"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "轿车",
     *                 "value": "101",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": [
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "微型车",
     *                         "value": "1",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "小型车",
     *                         "value": "2",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "紧凑型车",
     *                         "value": "3",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "中型车",
     *                         "value": "4",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "中大型车",
     *                         "value": "5",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "大型车",
     *                         "value": "6",
     *                         "parametername": "levelid"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "MPV",
     *                 "value": "8",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": [
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "紧凑型MPV",
     *                         "value": "21",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "中型MPV",
     *                         "value": "22",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "中大型MPV",
     *                         "value": "23",
     *                         "parametername": "levelid"
     *                     },
     *                     {
     *                         "key": "sublevelid",
     *                         "name": "大型MPV",
     *                         "value": "24",
     *                         "parametername": "levelid"
     *                     }
     *                 ]
     *             },
     *             {
     *                 "name": "跑车",
     *                 "value": "7",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "皮卡",
     *                 "value": "14",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "其他",
     *                 "value": "11,12,13",
     *                 "key": "levelid",
     *                 "parametername": "levelid",
     *                 "childrenlist": []
     *             }
     *         ]
     *     },
     *     {
     *         "key": "energytype",
     *         "showname": "能源",
     *         "isselectmore": 1,
     *         "list": [
     *             {
     *                 "name": "全部",
     *                 "value": "-1",
     *                 "key": "energytype",
     *                 "parametername": "energytype",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "燃油车",
     *                 "value": "1",
     *                 "key": "energytype",
     *                 "parametername": "energytype",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "新能源",
     *                 "value": "456",
     *                 "key": "energytype",
     *                 "parametername": "energytype",
     *                 "childrenlist": [
     *                     {
     *                         "key": "subenergytype",
     *                         "name": "全部新能源",
     *                         "value": "456",
     *                         "parametername": "energytype"
     *                     },
     *                     {
     *                         "key": "subenergytype",
     *                         "name": "插电混动",
     *                         "value": "5",
     *                         "parametername": "energytype"
     *                     },
     *                     {
     *                         "key": "subenergytype",
     *                         "name": "纯电动",
     *                         "value": "4",
     *                         "parametername": "energytype"
     *                     },
     *                     {
     *                         "key": "subenergytype",
     *                         "name": "增程式",
     *                         "value": "6",
     *                         "parametername": "energytype"
     *                     }
     *                 ]
     *             }
     *         ]
     *     },
     *     {
     *         "key": "orderid",
     *         "showname": "排序",
     *         "isselectmore": 0,
     *         "list": [
     *             {
     *                 "name": "综合",
     *                 "value": "1",
     *                 "key": "orderid",
     *                 "parametername": "orderid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "降幅大",
     *                 "value": "2",
     *                 "key": "orderid",
     *                 "parametername": "orderid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "近期降价",
     *                 "value": "5",
     *                 "key": "orderid",
     *                 "parametername": "orderid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "历史新低",
     *                 "value": "6",
     *                 "key": "orderid",
     *                 "parametername": "orderid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "销量多",
     *                 "value": "3",
     *                 "key": "orderid",
     *                 "parametername": "orderid",
     *                 "childrenlist": []
     *             },
     *             {
     *                 "name": "热度高",
     *                 "value": "4",
     *                 "key": "orderid",
     *                 "parametername": "orderid",
     *                 "childrenlist": []
     *             }
     *         ]
     *     }
     * ]
     */
    private String key;
    private String showname;
    private Integer isselectmore;

    private List<ListDTO> list = new ArrayList<>();

    @Data
    public static class ListDTO {
        private String name;
        private String value;
        private String key;
        private String parametername;
        private List<ChildrenlistDTO> childrenlist = new ArrayList<>();

        @Data
        public static class ChildrenlistDTO {
            private String key;
            private String name;
            private String value;
            private String parametername;

        }
    }
}
