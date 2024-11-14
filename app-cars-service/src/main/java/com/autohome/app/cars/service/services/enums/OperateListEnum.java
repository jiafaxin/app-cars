package com.autohome.app.cars.service.services.enums;


/**
 * @author : zzli
 * @description : 实验车系横栏配置
 * @date : 2024/8/21 19:49
 */
public enum OperateListEnum {

    REALBATTERY("真实续航", 10, 100001),
    OFFICIALBATTERY("官方续航", 10, 100002),
    INTELLIGENT("智能化功能", 20, 100003),
    HIGHLIGHTS_MODELDIFFERENCES("亮点/车型差异", 30, 100004),
    YANGCHE("养车成本", 40, 100005),
    BUTIE("优惠补贴", 50, 100006),//优惠补贴（原最高补贴）
    OWNERFLAG("购车权益", 60, 10),
    OTAFLAG("OTA升级", 70, 9),
    CHARGESTATIONFLAG("充电桩", 80, 11),
    REALCAREXPERIENCE("车机真体验", 90, 100010);

    private String name;
    private Integer order;
    private Integer typeid;

    public String getName() {
        return name;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getTypeid() {
        return typeid;
    }

    OperateListEnum(String name, Integer order, Integer typeid) {
        this.name = name;
        this.order = order;
        this.typeid = typeid;
    }

    public static OperateListEnum getByTypeid(Integer typeid) {
        if (typeid != null) {
            for (OperateListEnum item : OperateListEnum.values()) {
                if (item.typeid == typeid) {
                    return item;
                }
            }
        }
        return null;
    }
}
