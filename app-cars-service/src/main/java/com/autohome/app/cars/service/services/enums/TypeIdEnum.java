package com.autohome.app.cars.service.services.enums;


import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 *
 *
 */
public enum TypeIdEnum {


    //用车养车
    USECAR_YCCB("养车成本","YCCB",1010601),
    USECAR_YHDB("车主油耗","YHDB",1010602),
    USECAR_BZL("保值率","BZL",1010603),
    USECAR_YCJQ("用车技巧","YCJQ",1010604),
    USECAR_ACGZ("爱车改装","ACGZ",1010605),
    USECAR_BYSC("保养手册","BYSC",1010606),
    USECAR_CJGZ("常见故障","CJGZ",1010607),
    USECAR_BBCP("必备车品","BBCP",1010608),
    USECAR_QCMR("汽车美容","QCMR",1010609),
    USECAR_CXJG("车险价格","CXJG",1010610),
    USECAR_JGZS("交规知识","JGZS",1010611),
    USECAR_CXLP("车险理赔", "CXLP", 1010612),
    USECAR_YJSC("应急手册", "YJSC", 1010613),
    USECAR_SGAQ("事故安全", "SGAQ", 1010614),
    USECAR_ESCBJ("二手车报价", "ESCBJ", 1010615),
    USECAR_JTHJY("特惠加油", "THJY", 1010616),
    USECAR_CJZF("查驾照分", "CJZF", 1010617),
    USECAR_CWZ("违章查询", "CWZ", 1010618),
    USECAR_FDDJ("罚款代缴", "FDDJ", 1010619),
    USECAR_TJ4S("推荐4S店", "TJ4S", 1010620),
    USECAR_BYSCTJ4S("保养手册推荐4S店", "BYSCTJ4S", 1010621),
    USECAR_THXC("特惠洗车", "THXC", 1010622),
    USECAR_YCSMS("用车说明书", "YCSMS", 1010623),
    USECAR_SGCL("事故处理", "SGCL", 1010624),
    USECAR_CWBL("车务办理", "CWBL", 1010625),
    USECAR_GZT("故障通", "GZT", 1010626),
    USECAR_SYXD("用车心得", "SYXD", 1010627),
    USECAR_ACGZ_CARREIFT("爱车改装", "ACGZ_CARREIFT", 1010628),
    USECAR_BY4S("4S保养", "BY4S", 1010629),
    USECAR_CWZN("车务指南", "CWZN", 1010630),

    CANDY_NEWS("资讯","NEWS",1010561),
    CANDY_KB("口碑","KB",1010504),
    CANDY_CLUB("论坛","CLUB",1010503),
    CANDY_ASK("问答","ASK",1010517),
    CANDY_CHAT("热聊","CHAT",1010562),
    CANDY_ESC("二手车","ESC",1010515),
    CANDY_TCJ("提车价","TCJ",1010514),
    CANDY_YH("优惠","YH",1010516),
    CANDY_DELER("经销商","DELER",1010563),
    CANDY_YCCB("养车成本","YCCB",1010564),
    CANDY_PC("评测","PC",1010565),
    CANDY_VINST("视频说明书","VINST",1010567),
    CANDY_CYPZ("差异配置","CYPZ",1010568),
    CANDY_BZL("保值率", "BZL", 1010573),
    JMCONFIG_CSPZ("竞媒-参数配置","JMCSPZ",1010569),
    JMCONFIG_ZJSC("竞媒-之家实测","JMZJSC",1010570),
    JMCONFIG_CZPJ("竞媒-车主评价","JMCZPJ",1010571),
    JMCONFIG_CLUB("竞媒-论坛","JMCLUB",1010572),
    ;

    private String name;
    private String code;
    private Integer typeid;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Integer getTypeid() {
        return typeid;
    }

    TypeIdEnum(String name, String code, Integer typeid) {
        this.name = name;
        this.code = code;
        this.typeid = typeid;
    }

    public static TypeIdEnum getByName(String name){
        if (StringUtils.isNotEmpty(name)) {
            for (TypeIdEnum typeIdEnum : TypeIdEnum.values()) {
                if (StringUtils.equals(typeIdEnum.name,name)) {
                    return typeIdEnum;
                }
            }
        }
        return null;
    }

    public static TypeIdEnum getByCode(String code){
        if (StringUtils.isNotEmpty(code)) {
            for (TypeIdEnum typeIdEnum : TypeIdEnum.values()) {
                if (StringUtils.equals(typeIdEnum.code,code)) {
                    return typeIdEnum;
                }
            }
        }
        return null;
    }

    public static TypeIdEnum getByTypeid(Integer typeid){
        if (typeid!=null) {
            for (TypeIdEnum mainDataTypeEnum : TypeIdEnum.values()) {
                if (mainDataTypeEnum.typeid==typeid) {
                    return mainDataTypeEnum;
                }
            }
        }
        return null;
    }
}
