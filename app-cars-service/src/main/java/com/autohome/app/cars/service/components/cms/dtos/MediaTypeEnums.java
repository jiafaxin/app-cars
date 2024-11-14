package com.autohome.app.cars.service.components.cms.dtos;

/**
 * 卡片业务类型枚举
 *
 * @author wangkaixuan
 */
public enum MediaTypeEnums {
    PuTongNew("文章", 1),
    /**
     * TODO 数据已下线
     **/
//	ShuoKe("说客", 2),
    Video("视频", 3),
    FocusImg("焦点图", 4),
    Topic("帖子", 5),
    TuKu("图库", 6),
    FastNews("图文直播", 7),
    /** TODO 数据已下线**/
//	Radio("电台", 8),
    /**
     * TODO 数据已下线
     **/
    UserCar("二手车", 9),
    TuWen("图说", 10),
    KouBei("口碑", 11),
    YCChangWen("长文", 12),
    YCDuanWen("轻文", 13),
    YCVideo("视频", 14),
    YCRadio("音频", 15),
    ThirdPlatform("第三方文章", 16),
    /**
     * TODO 数据已下线
     **/
//	FeedRadio("汽车之家电台", 17),
    Live("直播", 20),
    ReLive("重播", 21),
    /** TODO 数据已下线**/
//	WX_Shuoke("说客", 22),
    /** TODO 数据已下线**/
//	WX_YouChuang("优创", 23),
    /**
     * TODO 数据已下线
     **/
//	YCTopic("话题", 24),
    TravelNote("游记", 25),
    NewsTopic("资讯话题", 26),
    /** TODO 数据已下线**/
//	CarServer("服务", 27),
    /**
     * TODO 数据已下线
     **/
//	Server("服务", 28),
    DealerNews("促销", 29),
    DealerTeamBuy("团购", 30),
    SecondCarNews("二手车", 31),
    VR_KanChe("VR看车", 35),
    CarPrice("车主价格", 36),
    /**
     * TODO 数据已下线
     **/
//	DealerMarket("行情", 37),
    QuestionNaire("问卷", 38),
	KouBeiList("口碑", 43),
    SpecCompare("车型对比", 47),
    CheYouQuan("车友圈", 48),
    /**
     * TODO 数据已下线
     **/
//	JiKe("迹客", 50),
    PinZhan("品牌智能展馆", 51),

    JinRongFQ("金融分期购车", 55),
    CarServer_DianPing("维修店点评", 57),
    VR_Feature("VR外观", 59),
    ShouYou("广告", 60),
    PinZhan4S("4S智能展馆", 61),
    JinRonHuoDong("金融活动", 62),
    QingShaoNianTopic("青少年", 63),
    AH_100("AH-100", 64),
    ClubVideo("论坛视频", 66),
    VRVideo("VR视频", 67),
    SmallVideo("小视频", 68),
    /**
     * TODO 数据已下线
     **/
//	Pic_AutoShow("车展图库", 69),
    ClubQA("论坛问答", 74),
    HotChat("热聊", 76),
    CarCity("汽车城", 77),
    /** TODO 数据已下线**/
//	SmallGame("小游戏", 78),
    /**
     * TODO 数据已下线
     **/
//	TravelCard("旅行锦囊", 79),
    KouBeiStarLevel("口碑星级", 80),
    KoubeiWD("维度口碑", 81),
    YCThirdVideo("车家号第三方来源视频", 82),
    SevenBuyCar_CS("七步买车-初始", 83),
    SevenBuyCar_CX("七步买车-初选", 84),
    SevenBuyCar_JX("七步买车-精选", 85),
    ConcernTopic("关注-话题", 86),
    ConcernTopicList("关注-话题列表", 87),
    DealerAskPrice("经销商私有询价", 89),
    CONCERN_TOPIC("关注话题",90),
    RECOMMEND_CONCERN_TOPIC("关注话题",91),
    FIXED_CONCERN_TOPIC("关注话题",92),
    SeriesGuide("车系指南", 95),
    /**资讯视频直播**/
    Live_News("直播",97),
    /**资讯视频重播平台**/
    ReLive_News("重播",98),
    /**汽车之家原创视频**/
    AutoVideo("视频",99),
    XiaoMi("家家小秘", 200),
    YcTheme("车家号专题", 93),
    HotPeople("红人", 205),
    BusinessPromotion_DP("商业推广-汽车点评",204),
    BusinessPromotion_HC("商业推广-嗨Car",203),
    BiSeries("推荐车系", 56),
    CarPrice_AI("车主价格-AI",94),
    QA("问答-综合",202),
    CarServer_Case("车服务-优质案例",201),
    CarServer_HighDianPing("车服务-优质店",208),
    YC_CheDan("车家号-车单",206),
    PinPaiZhou("品牌周",210),
    Dealer_PinPaiZhou("商业—品牌周",211),
    TravelOGC("游记-OGC", 212),
    PriceHelper("砍价助手", 213),
    FinanceNews("金融文章",214),
    YC_FanTuan("车家号-饭团",215),
    BigDataCar("carso大数据说车",216),
    CommercialNewCar("商业新车",217),
    YCVideoManual("车家号-视频说明书",218),
    FanTuanFansGroup("饭团粉丝社群",219),
    CarsoShuoChe("carso说车",220),
    DealerSmallVideo("经销商小视频",221),
    BaiKe("百科",209),
    UserCar_Series("二手车-车系", 52),
    UserCar_CarSource("二手车-车源", 71),
    QingShaoNianVideo("青少年-视频", 73),

    BU("BU", 10000),
    MadaInChina("中国制造", 10001),
    /**
     * 数据已下线
     **/
//	BU_PK("BU-PK", 10002),
    SmallVideoGroup("小视频组数据", 10003),

    SmallVideoGroup_New("小视频组数据", 10021),

    ConcernBigVGroup("关注大V",10022),

    SmallVideoGroup_Topic("小视频组数据-话题组卡", 20003),

    NewCarSell("新车特卖",600027),

    BISubject("智能专题",600035),

    YCAggregationTopic("车家号自动聚合专题",600036),

    NewCarLease("新车直租",600037),

    IntelligentMarketing("智能营销",600038),

    NewCarShop("新车电商店铺",600039),

    YC_FanTuanTheme("车家号-饭团主题",600040),

    IntelligentMarketingAGC("智能营销AGC",600042),

    NewCarsSell_AGC("新车电商AGC",600043),

    DealerCPS("经销商CPS",600048),

    DealerActivity("经销商活动",600049),

    UsedCar818("818二手车",700007),

    FocusImg_Ops("运营焦点图",998),
    ORIGINAL_CHANNEL_SMALLVIDEO_GROUP("小视频组卡", 10021),
    INDUSTRY_SPECIALTOPIC("行业专题卡片",10019),
    ORIGINAL_CHANNEL_AH100_GROUP("AH100组卡",10054),

    UnKnown("H5", 999),

    FAST_NEWS("快讯",700112);


    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private MediaTypeEnums(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (MediaTypeEnums c : MediaTypeEnums.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static MediaTypeEnums getByValue(int index) {
        for (MediaTypeEnums iterable_element : MediaTypeEnums.values()) {
            if (iterable_element.getIndex() == index) {
                return iterable_element;
            }
        }
        return null;
    }
}
