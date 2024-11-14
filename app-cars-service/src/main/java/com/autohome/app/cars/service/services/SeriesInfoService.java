package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoResponse;
import com.autohome.app.cars.apiclient.abtest.AbApiClient;
import com.autohome.app.cars.apiclient.che168.ApiAutoAppShClient;
import com.autohome.app.cars.apiclient.che168.dtos.UsedCarDetailResult;
import com.autohome.app.cars.apiclient.video.XuanGouVideoApiClient;
import com.autohome.app.cars.apiclient.video.dtos.XuanGouVideoResult;
import com.autohome.app.cars.apiclient.vr.dtos.SeriesVrExteriorResult;
import com.autohome.app.cars.common.enums.CarSellTypeEnum;
import com.autohome.app.cars.common.enums.EnergyTypesEnum;
import com.autohome.app.cars.common.enums.EnergyTypesNewEnum;
import com.autohome.app.cars.common.enums.NewSeriesHotTabEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.che168.SeriesCityUsedCarComponent;
import com.autohome.app.cars.service.components.che168.SeriesKeepValueComponent;
import com.autohome.app.cars.service.components.che168.SeriesUsedCarComponent;
import com.autohome.app.cars.service.components.che168.SeriesYearCityPriceComponent;
import com.autohome.app.cars.service.components.che168.dtos.KeepValueSeriesInfo;
import com.autohome.app.cars.service.components.club.SeriesClubComponent;
import com.autohome.app.cars.service.components.che168.dtos.SeriesUsedCarInfo;
import com.autohome.app.cars.service.components.club.SeriesClubPostComponent;
import com.autohome.app.cars.service.components.cms.AutoShowNewsComponent;
import com.autohome.app.cars.service.components.cms.SeriesCmsTestEvalComponent;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowConfigDto;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowNewsDto;
import com.autohome.app.cars.service.components.dataopen.SeriesRecommendLikeComponent;
import com.autohome.app.cars.service.components.dealer.*;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesDriveDto;
import com.autohome.app.cars.service.components.file.MegaDataComponent;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicDataDto;
import com.autohome.app.cars.service.components.im.SeriesCityImComponent;
import com.autohome.app.cars.service.components.jiage.SeriesJiageComponent;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiComponent;
import com.autohome.app.cars.service.components.misc.SeriesCityHotNewsComponent;
import com.autohome.app.cars.service.components.misc.SeriesCityTabComponent;
import com.autohome.app.cars.service.components.misc.SeriesTabComponent;
import com.autohome.app.cars.service.components.misc.dtos.NewSeriesCityHotNewsAndTabDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesCityTabDto;
import com.autohome.app.cars.service.components.misc.dtos.SeriesTabDto;
import com.autohome.app.cars.service.components.newcar.NewCarCalendarComponent;
import com.autohome.app.cars.service.components.newretail.NewRetailCitySeriesComponent;
import com.autohome.app.cars.service.components.newretail.dtos.CitySeriesListDto;
import com.autohome.app.cars.service.components.owner.*;
import com.autohome.app.cars.service.components.owner.dtos.BeiliKoubeiInfo;
import com.autohome.app.cars.service.components.subsidy.SpecCitySubsidyComponent;
import com.autohome.app.cars.service.components.visit.SeriesSpecVisitComponent;
import com.autohome.app.cars.service.components.visit.dtos.SeriesSpecVisitDto;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.autohome.app.cars.service.services.dtos.*;
import com.autohome.app.cars.service.services.enums.ComputerRoom;
import com.autohome.app.cars.service.services.enums.OperateListEnum;
import com.autohome.app.cars.service.services.enums.TypeIdEnum;
import com.autohome.autolog4j.common.JacksonUtil;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 车系综述页相关
 */
@Service
@Slf4j
public class SeriesInfoService {
    @Autowired
    SeriesTestDataComponent seriesTestDataComponent;

    @Autowired
    SeriesKouBeiComponent seriesKouBeiComponent;

    @Autowired
    SeriesClubComponent seriesClubComponent;

    @Autowired
    SeriesJiageComponent seriesJiageComponent;

    @Autowired
    SeriesUsedCarComponent seriesUsedCarComponent;

    @Autowired
    SeriesCityTabComponent seriesCityTabComponent;

    @Autowired
    SeriesCityImComponent seriesCityImComponent;

    @Autowired
    SeriesPicCountComponent seriesPicCountComponent;

    @Autowired
    SeriesCityYangcheComponent seriesCityYangcheComponent;

    @Autowired
    SeriesOwnerComponent seriesOwnerComponent;

    @Autowired
    SeriesCityDealerComponent seriesCityDealerComponent;

    @Autowired
    MegaDataComponent fileComponent;

    @Autowired
    SeriesDriveComponent seriesDriveComponent;

    @Autowired
    SeriesCityZhaodijiaComponent seriesCityZhaodijiaComponent;

    @Autowired
    SeriesGaizhuangComponent seriesGaizhuangComponent;

    @Autowired
    SeriesVrComponent seriesVrComponent;

    @Autowired
    SeriesBrightpointComponent seriesBrightpointComponent;

    @Autowired
    SeriesCmsTestEvalComponent seriesCmsTestEvalComponent;

    @Autowired
    SeriesOtaOwnerComponent seriesOtaOwnerComponent;

    @Autowired
    NewRetailCitySeriesComponent newRetailCitySeriesComponent;

    @Autowired
    SeriesRecommendLikeComponent seriesRecommendLikeComponent;

    @Autowired
    SeriesTabComponent seriesTabComponent;

    @Autowired
    SeriesCityAskPriceComponent seriesCityAskPriceComponent;

    @Autowired
    SeriesSpecComponent seriesSpecComponent;

    @Autowired
    SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    @Autowired
    SeriesEnergyInfoComponent seriesEnergyInfoComponent;

    @Autowired
    BeiliKoubeiComponent beiliKoubeiComponent;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    AutoShowNewsComponent autoShowNewsComponent;

    @Autowired
    SpecCitySubsidyComponent specCitySubsidyComponent;

    @Autowired
    SeriesCityCpsComponent seriesCityCpsComponent;

    @Autowired
    HqPhotoComponent hqPhotoComponent;

    @Autowired
    AbApiClient abApiClient;
    @Autowired
    XuanGouVideoApiClient xuanGouVideoApiClient;
    @Autowired
    SeriesTimeAxisComponent seriesTimeAxisComponent;
    @Autowired
    SeriesCityUsedCarComponent seriesCityUsedCarComponent;
    @Autowired
    NewCarCalendarComponent newCarCalendarComponent;

    @Autowired
    private SeriesCityHotNewsComponent seriesCityHotNewsComponent;

    @Autowired
    SeriesClubPostComponent seriesClubPostComponent;

    @Autowired
    private ApiAutoAppShClient apiAutoAppShClient;

    @Autowired
    private SeriesKeepValueComponent seriesKeepValueComponent;

    @Autowired
    private SeriesYearCityPriceComponent seriesYearCityPriceComponent;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.TestDriveConfig).createFromJson('${testdrive_config:}')}")
    private TestDriveConfig testDriveConfig;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.NewEnergyPlanConfig).decodeNewEnergyPlanConfig('${newenergyplan_config:}')}")
    NewEnergyPlanConfig newenergyplanConfig;

    @Autowired
    SuperTestConfig superTestConfig;

    @Autowired
    private CarPriceChangeComponent carPriceChangeComponent;
    @Autowired
    private SeriesAttentionComponent seriesAttentionComponent;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.WinterRankSeries).createFromJson('${rank_winter_seriesinfos:}')}")
    private List<WinterRankSeries> rank_winter_seriesinfos;


    @Value("#{T(com.autohome.app.cars.service.services.dtos.ChejiSeriesDataConfig).createFromJson('${cheji_series_data_list:}')}")
    private List<ChejiSeriesDataConfig> cheji_series_data_list;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.Ah100TestConfig).createFromJson('${car_ah100test_config:}')}")
    private Ah100TestConfig car_ah100test_config;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.JJC2024Config).createFromJson('${jjc2024_config:}')}")
    private JJC2024Config jjc2024_config;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.JJCSpecConfig).createFromJson('${jjc2023_config_new:}')}")
    private JJCSpecConfig jjConfig;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.WinterTest2023Config).createFromJson('${wintertest_2023_config:}')}")
    private WinterTest2023Config winterTest2023Config;
    @Value("${series_operateposition:[]}")
    String series_operateposition;
    @Value("${series_candy_beans:}")
    String series_candy_beans;
    @Value("${supertest_isopen_config:0}")
    private int supertest_isopen_config;

    @Value("${winter_test_open_control:0}")
    private int winter_test_open_control;

    @Value("${functionentrybeansone:}")
    String functionentrybeansone;

    @Value("${functionbeancfg:}")
    private String functionbeancfg;

    @Value("${functionentrybeanstwo:}")
    String functionentrybeanstwo;

    //TODO 配置好
    @Value("${functionentrybeanstwo_ssc:}")
    String functionentrybeanstwoSsc;

    @Value("${tabinfolistv2:}")
    String tabinfolistv2;
    @Value("${newtabinfo:}")
    String newtabinfo;

    @Value("#{T(com.autohome.app.cars.service.services.enums.ComputerRoom).convertByValue('${computer_room_status:0}')}")
    private ComputerRoom computerRoom;

    @Value("${newenergyconfigbeans:[]}")
    String newenergyconfigbeans;

    @Value("${necd_smart_config:[]}")
    String necd_smart_config;

    //新车订阅
    @Value("${new_subscribe_entry_pop:}")
    private String newSubscribeEntryPopJson;

    //TODO 此处配置去掉newiconurl，直接把newiconurl的配置到iconurl
    //@Value("${functionentrybeanstwo_byjg:}")
    //String functionentrybeanstwoByjg;

    @Value("${series_hot_reply_seriesids:}")
    private String seriesHotReplySeriesIds;

    @Value("${series_hot_reply_seriesids_testf:}")
    private String seriesHotReplySeriesIdsTestF;

    @Value("${series_hot_reply_seriesids_testi:}")
    private String seriesHotReplySeriesIdsTestI;

    @Value("${newcar_calendar_config:}")
    private String NewCarCalendarConfigJson;
    @Value("${series_hot_reply_textab:[]}")
    private String seriesHotReplyTextAb;
    @Value("${edgeHyperShiPaiText:}")
    private String edgeHyperShiPaiText;


    @Value("${aiviewpointab_seriesidlist:[]}")
    private String aiViewPoinTabSeriesidList;

    @Value("${shuangzhi_entry_config:}")
    private String shuangzhi_entry_config;

    /**
     * 降价有效时间 单位：天
     */
    @Value("${car_price_reduce:14}")
    private int carPriceReduceValidity;

    //默认的糖豆配置
    static final String newenergyconfigbeansDefault = "[{\"title\":\"真实续航\",\"code\":\"location1\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/xh_230607.png\",\"typeid\":10001},{\"title\":\"百公里电费\",\"code\":\"location2\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/bgldf_230607.png\",\"typeid\":10002},{\"title\":\"智能驾驶\",\"code\":\"location3\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/znjs_230607.png\",\"typeid\":10003}]";
    //static final String necdsmartconfigDefault = "[{\"seriesid\":6700,\"location2title\":\"智能座舱\",\"location2subtitle\":\"360°体验\",\"location2url\":\"autohome://insidebrowserwk?url=https%3A%2F%2Fpano.autohome.com.cn%2Fcar%2Fseat%2F56354%3Fnavigationbarstyle%3D2%26landscapefullscreen%3D1&disable_back=1\",\"location3title\":\"智能驾驶\",\"location3subtitle\":\"9项功能\",\"location3url\":\"autohome://car/aidriving?seriesid=6700\"}]";
    //static final String testdriveconfigDefault = "{\"is_open\":1,\"title\":\"超级试驾\",\"subtitle\":\"之家自营\",\"linurl\":\"https://fs.autohome.com.cn/afu_spa/h5-equity/testdrive?cityid={}&pvareaid=6860394&seriesId={}&apptype=2\",\"city_list\":[310100,460100],\"series_city_list\":[460100],\"isvr\":1}";
    static final String functionentrybeansoneDefault = "[{\"title\":\"之家实测\",\"code\":\"SC\",\"subtitle\":\"\",\"subtitlehighlight\":\"\",\"linkurl\":\"\",\"iconurl\":\"\",\"typeid\":1476519},{\"title\":\"口碑\",\"code\":\"KB\",\"subtitle\":\"\",\"subtitlehighlight\":\"\",\"linkurl\":\"\",\"iconurl\":\"\",\"typeid\":1476509},{\"title\":\"提车价\",\"code\":\"TCJ\",\"subtitle\":\"\",\"subtitlehighlight\":\"\",\"linkurl\":\"\",\"iconurl\":\"\",\"typeid\":1476513},{\"title\":\"二手车\",\"code\":\"2scJ\",\"subtitle\":\"\",\"subtitlehighlight\":\"\",\"linkurl\":\"\",\"iconurl\":\"\",\"typeid\":1476517}]";

    static final String functionentrybeanstwoDefault = "[{\"title\":\"差异配置\",\"code\":\"CYPZ\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_cxcy.png\",\"typeid\":0},{\"title\":\"论坛\",\"code\":\"CLUB\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_lt.png\",\"typeid\":1476520},{\"title\":\"热聊\",\"code\":\"CHAT\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_rl.png\",\"typeid\":1476521},{\"title\":\"养车成本\",\"code\":\"BYJG\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_yccb.png\",\"typeid\":1476512},{\"title\":\"常见问题\",\"code\":\"ASK\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_cjwt.png\",\"typeid\":1476514},{\"title\":\"找底价\",\"code\":\"ZDJ\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_zdj.png\",\"typeid\":0},{\"title\":\"优惠\",\"code\":\"YH\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_yh.png\",\"typeid\":0},{\"title\":\"改装\",\"code\":\"BY\",\"subtitle\":\"\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_gz.png\",\"typeid\":1476515},{\"title\":\"4S保养\",\"code\":\"4SBY\",\"subtitle\":\"维修保养\",\"linkurl\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_4Sby.png\",\"typeid\":1476516},{\"title\":\"以旧换新\",\"code\":\"YJHX2\",\"subtitle\":\"\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_yjhx.png\",\"linkurl\":\"autohome://rninsidebrowser?url=rn%3A%2F%2FUsedCar%2FChangeOldCarStepIndex%3FpanValid%3Dfalse%26tab%3D0%26leadssources%3D25%26sourcetwo%3D4%26sourcethree%3D1571%26pvareaid%3D112439%26sourcename%3Dmainapp\",\"typeid\":1476521}]";

    static final String functionbeancfgDefault = "[{\"title\":\"之家实测\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/zjsc_bgimg01_230518.png\",\"code\":\"SC\",\"subtitle\":\"零百加速\",\"linkurl\":\"\",\"cornerinfo\":\"\",\"typeid\":1010701},{\"title\":\"资讯\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/news.png\",\"code\":\"NEWS\",\"subtitle\":\"之家专业评测\",\"linkurl\":\"autohome://article/newseriesarticle?seriesid={seriesid}&seriesname={seriesname}&tabid=10000\",\"cornerinfo\":\"\",\"typeid\":1010561},{\"title\":\"论坛\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/club.png\",\"code\":\"CLUB\",\"subtitle\":\"汇聚万千车友\",\"linkurl\":\"autohome://club/topiclist?bbsid={seriesid}&bbstype=c&seriesid={seriesid}&bbsname={seriesname}\",\"cornerinfo\":\"\",\"typeid\":1010503},{\"title\":\"口碑\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/kb.png\",\"code\":\"KB\",\"subtitle\":\"车主真实口碑\",\"linkurl\":\"autohome://reputation/reputationlist?brandid={brandid}&seriesid={seriesid}&seriesname={seriesname}&koubeifromkey=1&shownewdata=1\",\"cornerinfo\":\"\",\"typeid\":1010504},{\"title\":\"热聊\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/chat.png\",\"code\":\"CHAT\",\"subtitle\":\"车友在线讨论\",\"linkurl\":\"autohome://carfriend/flashchatconversation/group?targetId={seriesid}&targetType=1\",\"cornerinfo\":\"\",\"typeid\":1010562},{\"title\":\"问答\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/ask.png\",\"code\":\"ASK\",\"subtitle\":\"车友帮解答\",\"linkurl\":\"autohome://ask/askmain?bbsid={seriesid}&bbsname={seriesname}&seriesid={seriesid}&tagid=0\",\"cornerinfo\":0,\"typeid\":1010517},{\"title\":\"优惠\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/yh.png\",\"code\":\"YH\",\"subtitle\":\"最新报价走势\",\"linkurl\":\"autohome://flutter?url=flutter://car/cardiscount?seriesid={seriesid}&fromtype=1\",\"cornerinfo\":\"\",\"typeid\":1010516},{\"title\":\"经销商\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/jxs.png\",\"code\":\"JXS\",\"subtitle\":\"4S店报价\",\"linkurl\":\"\",\"cornerinfo\":\"\",\"typeid\":1010563},{\"title\":\"保养\",\"iconurl\":\"https://nfiles3.autohome.com.cn/zrjcpk10/dfk/by.png\",\"code\":\"BY\",\"subtitle\":\"真实保养案例\",\"linkurl\":\"autohome://flutter?url=flutter://yongche/homepage?seriesid={seriesid}&seriesname={seriesname}&sourceid=3\",\"cornerinfo\":\"\",\"typeid\":1010601}]";

    private static final String APP_KEY = "3J4a2P1Q8W9K7L5T6M0N2V1U4B8Z6Y7X0";


    public CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Builder> getSeriesBaseInfoBuilder(
            SeriesDetailDto series,
            SeriesBaseInfoRequest request,
            CompletableFuture<AutoShowConfigDto> autoShowFuture,
            CompletableFuture<SeriesTestDataDto> seriesTestDataFuture,
            CompletableFuture<SeriesCityAskPriceDto> seriesCityAskPriceFuture,
            int funcabtest,
            boolean isNewSeriesSummary
    ) {
        /*
         车系停售页改版  101915 https://doc.autohome.com.cn/docapi/page/share/share_z1K5BbhRVw
         保留 https://doc.autohome.com.cn/docapi/page/share/share_xonpSFZvhQ 需求中 tab以上的变更
         */
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.5") && series.getState() == 40 && request.getSeriesabtest().equals("B")) {
            isNewSeriesSummary = true;
        }
        try {
            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Builder seriesBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.newBuilder();
            seriesBuilder.setSeriesid(series.getId());
            seriesBuilder.setSeriesname(series.getName());
            seriesBuilder.setBrandid(series.getBrandId());
            seriesBuilder.setBrandname(StringEscapeUtils.unescapeHtml4(series.getBrandName()));
            seriesBuilder.setBrandlogo(ImageUtils.convertImageUrl(series.getBrandLogo(), true, false, false, ImageSizeEnum.ImgSize_1x1_100x100_NO_OPT));
            seriesBuilder.setEnergetype(series.getEnergytype());
            seriesBuilder.setLevelid(series.getLevelId());

            String levelName = series.getLevelName();
            if (series.getLevelId() == 14 || series.getLevelId() == 15) {
                levelName = "皮卡";
            }
            seriesBuilder.setLevelname(levelName);
            if (series.getEnergytype() == 0) {
                seriesBuilder.setSeriestag(levelName + "," + StringUtils.join(series.getDisplacementItems(), "/"));
            } else {
                seriesBuilder.setSeriestag(levelName);
            }

            seriesBuilder.setPnglogo(ImageUtils.convertImage_SizeWebp(series.getPngLogo(), ImageSizeEnum.ImgSize_4x3_400x300));
            seriesBuilder.setFctprice(PriceUtil.GetPriceStringDetail(series.getMinPrice(), series.getMaxPrice(), series.getState()));
            seriesBuilder.setAttentionspecid(series.getHotSpecId());
            seriesBuilder.setParamisshow(series.getParamIsShow());
            seriesBuilder.setState(series.getState());
            seriesBuilder.setAttentionspecname(series.getHotSpecName() == null ? "" : series.getHotSpecName());
            seriesBuilder.setCarparmconfig(getCarParamConfig(series, request.getSimpleinfoabtest()));
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.61.0")) {
                seriesBuilder.setLivestatus(series.getLiveStatus());
            }

            seriesBuilder.setFctpricenamenew("指导价:");
            if (series.getState() == 10) {
                if (series.getContainBookedSpec() == 1) {
                    seriesBuilder.setFctpricenamenew("订金 ");
                } else {
                    seriesBuilder.setFctpricenamenew("预售价 ");
                }
            }
            if (seriesBuilder.getFctprice().equals("即将销售")) {
                seriesBuilder.setFctpricenamenew("");
            }

            //车系图片处理，兼容老图
            String limg = ImageUtils.convertImage_ToHttp(series.getLogo());
            String logo="";
            if (StringUtils.isNotEmpty(limg)) {
                int index = limg.lastIndexOf("/") + 1;
                logo=!"gp_default.gif".equals(limg.substring(index))?limg.substring(0, index) + "u_" + limg.substring(index):limg;
            }
            seriesBuilder.setLogo(ImageUtils.convertImage_ToWebp(logo));

            if (isNewSeriesSummary&&series.getState()==40) {
                seriesBuilder.setDefaulttypeid(18);
            }


            CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial> vrMaterialFuture = getVrMaterial(seriesBuilder.getSeriesid(), series.getEnergytype(), request.getPm(), request.getPluginversion());
            // 车辆价格页协议
            AtomicReference<String> downPriceCarPriceScheme = new AtomicReference<>(StrPool.EMPTY);
            List<CompletableFuture> tasks = new ArrayList<>();
            //vr头图和背景样式处理
            tasks.add(processVrAndDarkStyle(seriesBuilder, request, autoShowFuture, seriesTestDataFuture, vrMaterialFuture,series,funcabtest,isNewSeriesSummary));
            //车系头图底部按钮
            tasks.add(getEdgehyperlink(series, vrMaterialFuture,request.getPm(),request.getPluginversion()).thenAccept(seriesBuilder::setEdgehyperlink));
            tasks.add(getDealerpricerangeinfo(request.getSeriesid(), request.getCityid(), series.getBrandId(), series.getHotSpecId(), series.getName(), funcabtest,request.getPm(),request.getPluginversion(),seriesCityAskPriceFuture).thenAccept(seriesBuilder::setDealerpricerangeinfo));
            //经销商报价
            //二手车信息
            tasks.add(getSscPriceInfo(seriesBuilder, series.getId(), series.getState(), request.getCityid()));
            // 限时降需求-官降迭代 https://doc.autohome.com.cn/docapi/page/share/share_zQTE7MgKMi
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.68.0") && "B".equals(request.getDowntagabtest())) {
                tasks.add(getDownPriceInfo(request, seriesBuilder, series).thenAccept(downPriceCarPriceScheme::set));
            }
            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenApply(p -> {
                // 设置官降跳转车辆价格页的跳转协议
                if (StringUtils.isNotBlank(downPriceCarPriceScheme.get())) {
                    seriesBuilder.getDealerpricerangeinfoBuilder().setDealerpricelinkurl(downPriceCarPriceScheme.get());
                }
                return seriesBuilder;
            }).exceptionally(e -> {
                log.error("车系详情报错", e);
                return seriesBuilder;
            });
        } catch (Exception e) {
            log.error("createSeriesBaseInfoBuilder error", e);
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * 经销商价格区间
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Dealerpricerangeinfo> getDealerpricerangeinfo(
            int seriesId,int cityId,int brandId,int hotSpecId,String seriesName,int funcabtest,int pm,String pluginversion,
            CompletableFuture<SeriesCityAskPriceDto> seriesCityAskPriceFuture
    ){
        return seriesCityAskPriceFuture.thenApply(p -> {
            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Dealerpricerangeinfo.Builder dealerPriceBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Dealerpricerangeinfo.newBuilder();
            dealerPriceBuilder.setDealerprice("暂无");
            dealerPriceBuilder.setDealerpricelinkurl("");
            dealerPriceBuilder.setDealerpricename("经销商价");
            dealerPriceBuilder.setTypeid(1010506);
            if (p != null) {
                int minPrice = p.getMinPrice();
                int maxPrice = p.getMaxPrice();
                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5") && p.getMinPriceOnSale() > 0 && p.getMaxPriceOnSale() > 0) {
                    minPrice = p.getMinPriceOnSale();
                    maxPrice = p.getMaxPriceOnSale();
                }
                if (minPrice > 0) {
                    if (minPrice == maxPrice) {
                        dealerPriceBuilder.setDealerprice(CommonHelper.getMoney(minPrice, "起"));
                    } else {
                        dealerPriceBuilder.setDealerprice(CommonHelper.priceFormat(Double.parseDouble(minPrice + ""), Double.parseDouble(maxPrice + ""), CarSellTypeEnum.Selling, "-"));
                    }
                    String newCarscheme = "autohome://car/pricelibrary?brandid=" + brandId + "&seriesid=" + seriesId + "&specid=" + hotSpecId + "&seriesname=" + UrlUtil.encode(seriesName).replace("+", "%20") + "&tabindex=1&fromtype=1";
                    dealerPriceBuilder.setDealerpricelinkurl(newCarscheme);
                    //实验版本报价链接
                    if (funcabtest == 1) {
                        dealerPriceBuilder.setDealerpricelinkurl(newCarscheme + "&tabtype=1&sourceid=1&tabpricename=" + UrlUtil.encode("本地报价"));
                    }
                }
            }
            if (pm==3&&CommonHelper.isTakeEffectVersion(pluginversion, "11.66.5")) {
                dealerPriceBuilder.setPvitem(Pvitem.newBuilder()
                        .putArgvs("cityid", String.valueOf(cityId))
                        .putArgvs("seriesid", String.valueOf(seriesId))
                        .putArgvs("typeid", "1010506")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_price_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_price_show")));
            }
            return dealerPriceBuilder.build();
        });
    }

    /**
     * 二手车价格处理
     */
    CompletableFuture<Void> getSscPriceInfo(SeriesBaseInfoResponse.Result.Seriesbaseinfo.Builder seriesBuilder,
                                            int seriesId,
                                            int state,
                                            int cityId) {
        seriesBuilder.setSscpricnamenew("二手车");
        if (state == 40) {
            seriesBuilder.setStateinfo("已停售");
            seriesBuilder.setSscpricnamenew("二手车");
            seriesBuilder.setSscpriceinfo("暂无报价");
            List<CompletableFuture> tasks = new ArrayList<>();

            tasks.add(seriesUsedCarComponent.get(seriesId).thenAccept(usedCar -> {
                if (usedCar != null) {
                    seriesBuilder.setSscpriceinfo(usedCar.getSubTitle());
                }
            }));
            tasks.add(seriesCityUsedCarComponent.get(seriesId, cityId).thenAccept(cityUsedCar -> {
                if (cityUsedCar != null) {
                    seriesBuilder.setSsclinkurl(cityUsedCar.getUrl() == null ? "" : cityUsedCar.getUrl());
                }
            }));

            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).exceptionally(e -> {
                log.error("getSscPriceInfo error", e);
                return null;
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * 处理顶部VR信息，以及Vr背景色，车展样式信息
     */
    CompletableFuture processVrAndDarkStyle(SeriesBaseInfoResponse.Result.Seriesbaseinfo.Builder seriesBuilder,
                                            SeriesBaseInfoRequest request,
                                            CompletableFuture<AutoShowConfigDto> autoShowFuture,
                                            CompletableFuture<SeriesTestDataDto> seriesTestDataFuture,
                                            CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial> vrMaterialFuture,
                                            SeriesDetailDto series,int funcabtest,boolean isNewSeriesSummary) {
        // 优先级：冬测》车展》默认其他，字段返回不同标识
        // darkstylevr：1车展 2冬测 0默认其他 3新车车系
        // boothbg(vr台子图)
        // headbg(vr背景图)
        // minusbgcolor(负一屏背景颜色)
        return CompletableFuture.allOf(autoShowFuture, seriesTestDataFuture, vrMaterialFuture).thenRunAsync(() -> {
            AutoShowConfigDto showConfig = autoShowFuture.join();
            boolean isAutoShow = showConfig != null && (showConfig.getCarAction(seriesBuilder.getSeriesid()) > -1);
            int winterFlag = 0;
            if (seriesBuilder.getEnergetype() == 1
                    && winterTest2023Config!=null
                    && winterTest2023Config.getIsopen()==1
                    && !winterTest2023Config.getExcludelist().contains(seriesBuilder.getSeriesid())
                    && Arrays.asList("test_a", "test_b", "test_c", "test_d", "test_g", "test_h", "test_i").contains(request.getEnergytestab())) {
                SeriesTestDataDto seriesTestData = seriesTestDataFuture.join();
                if (seriesTestData != null
                        && CollectionUtils.isNotEmpty(seriesTestData.getTestWinterData())) {
                    winterFlag = 1;
                }
            }
            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial vrMaterial = vrMaterialFuture.join();
            boolean existVr=vrMaterial != null && vrMaterial.getColorListCount() > 0;
            seriesBuilder.setVrmaterial(vrMaterial);
            if (isAutoShow) {
                seriesBuilder.setDarkstylevr(1);
                seriesBuilder.setBoothbg(autoShowConfig.getBg().getBoothbg());
                seriesBuilder.setHeadbg(funcabtest == 1 ?
                        autoShowConfig.getBg().getBheadbg() :
                        autoShowConfig.getBg().getAheadbg());
            } else if (winterFlag == 1) {
                seriesBuilder.setWintertestflag(winterFlag);
                seriesBuilder.setDarkstylevr(2);
                seriesBuilder.setBoothbg("http://nfiles3.autohome.com.cn/zrjcpk10/energy_test_tz_20211224.webp");
                seriesBuilder.setHeadbg(funcabtest == 1 ?
                        "http://nfiles3.autohome.com.cn/zrjcpk10/energy_test_bgm_B_20211224.webp" :
                        "http://nfiles3.autohome.com.cn/zrjcpk10/energy_test_bgm_A_20211224.webp");
            } else { //非车展和冬测：车系背景图和vr头图处理
                if(isNewSeriesSummary){
                    seriesBuilder.setBoothbg(seriesBuilder.getEnergetype() == 1 ?
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_new_series_energy_booth_20240822.webp" :
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_series_head_tz_40_noraml.png");

                    if (existVr) {
                        seriesBuilder.setHeadbg(seriesBuilder.getEnergetype() == 1 ?
                                "http://nfiles3.autohome.com.cn/zrjcpk10/car_new_series_energy_head_20240822.webp" :
                                "http://nfiles3.autohome.com.cn/zrjcpk10/car_series_head_bg_40.png");
                    }
                }else if (funcabtest == 1) {
                    seriesBuilder.setBoothbg(seriesBuilder.getEnergetype() == 1 ?
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_series_head_tz_new_energy_114700@3x.png" :
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_series_head_tz_40_noraml.png");

                    if (existVr) {
                        seriesBuilder.setHeadbg(seriesBuilder.getEnergetype() == 1 ?
                                "http://nfiles3.autohome.com.cn/zrjcpk10/car_series_head_bg_new_energy_114701@3x.png.webp" :
                                "http://nfiles3.autohome.com.cn/zrjcpk10/car_series_head_bg_40.png");
                    }
                } else {
                    seriesBuilder.setBoothbg(seriesBuilder.getEnergetype() == 1 ?
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_top_vr_114300_tz_new_energy@3x.png" :
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_top_vr_114300_tz@3x.png");

                    if (existVr) {
                        seriesBuilder.setHeadbg(seriesBuilder.getEnergetype() == 1 ?
                                "http://nfiles3.autohome.com.cn/zrjcpk10/car_top_vr_114300_bg_energy@3x.jpg" :
                                "http://nfiles3.autohome.com.cn/zrjcpk10/car_top_vr_114300_1_bg@3x.jpg");
                    }
                }
            }

            //负一屏背景颜色服务端下发
            String minusbgcolor = "#FFFFFF";
            if (existVr) {
                if (seriesBuilder.getDarkstylevr() == 1) {
                    minusbgcolor = autoShowConfig.getMinusbgcolor();
                } else if (winterFlag == 1) {
                    minusbgcolor = "#B1EBFF";
                } else if (isNewSeriesSummary) {
                    minusbgcolor = seriesBuilder.getEnergetype() == 1 ? "#AAEFEF" : "#E3F5FF";
                } else if (funcabtest == 1) {
                    minusbgcolor = seriesBuilder.getEnergetype() == 1 ? "#A8F0FE" : "#E3F5FF";
                }
            }
            //新车车系，头图氛围优先级：车展>新车>冬测>普通的
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.61.0")&&funcabtest==1&&series.getIsNewCar()) {
                seriesBuilder.setIsnewcar(1);
                if (!isAutoShow&&existVr) {//非车展时，新车的优先级就最高了,有VR时才下发新车氛围图
                    seriesBuilder.setDarkstylevr(3);
                    seriesBuilder.setHeadbg("http://nfiles3.autohome.com.cn/zrjcpk10/newcar_headbg_2024032719.webp");
                    seriesBuilder.setBoothbg("http://nfiles3.autohome.com.cn/zrjcpk10/newcar_boothbg_2024032719.webp");
                    minusbgcolor="#072348";
                }
            }

            seriesBuilder.setMinusbgcolor(minusbgcolor);
        }, ThreadPoolUtils.defaultThreadPoolExecutor);
    }

    /**
     * 参数配置按钮
     */
    SeriesBaseInfoResponse.Result.Seriesbaseinfo.Carparmconfig.Builder getCarParamConfig(SeriesDetailDto series, String simpleinfoabtest) {
        SeriesBaseInfoResponse.Result.Seriesbaseinfo.Carparmconfig.Builder carParamconfig = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Carparmconfig.newBuilder();

        carParamconfig.setName("参数配置")
                .setIconurl("https://files3.autoimg.cn/zrjcpk10/car_peizhi_20190529@3x.png")
                .setLinkurl(getCarParamUrl(series, simpleinfoabtest));

        if (series.getLevelId() == 14 || series.getLevelId() == 15) {
            carParamconfig.setLabels("皮卡");
        } else {
            carParamconfig.setLabels(series.getLevelName());
        }

        if (series.getEnergytype() == 1) {
            if (StringUtils.isNotEmpty(series.getFueltypes())) {
                List<String> typelist = new ArrayList<>();
                Arrays.asList(series.getFueltypes().split(",")).forEach(type -> {
                    if (Integer.parseInt(type) != 0) {
                        String typename = EnergyTypesNewEnum.getTypeByValue(Integer.parseInt(type));
                        typelist.add(typename);
                    }
                });
                carParamconfig.setLabels(carParamconfig.getLabels() + "," + String.join("/", typelist));
            }
        } else {
            if (CollectionUtils.isNotEmpty(series.getDisplacementItems())) {
                int i = series.getDisplacementItems().size();
                if (i < 3) {
                    carParamconfig.setLabels(carParamconfig.getLabels() + "," + String.join("/", series.getDisplacementItems()));
                } else {
                    carParamconfig.setLabels(carParamconfig.getLabels() + ",最高" + series.getDisplacementItems().get(i - 1));
                }
            }
        }

        return carParamconfig;
    }

    /**
     * 拼接参数配置页协议
     */
    String getCarParamUrl(SeriesDetailDto series, String simpleinfoabtest) {
        //存在在售和未售的且非商用车
        int state = 0;
        if ((series.getSellSpecNum() + series.getWaitSpecNum()) > 0
                && series.getParamIsShow() == 1
                && Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 17, 18, 19, 20, 21, 22, 23, 24).contains(series.getLevelId())) {
            state = 1;
        }

        String linkUrl = String.format("autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s&hassummaryconfig=%s",
                series.getId(), URLEncoder.encode(series.getName()), state);
        //命中实验，锚点到参配页的“简述tab”（typeid=15）
        if (StringUtils.isNotEmpty(simpleinfoabtest) && StringUtils.equalsAny(simpleinfoabtest, "1", "2")) {
            linkUrl = linkUrl + "&typeid=15";
        }

        return linkUrl;
    }

    /**
     * 车系头图底部按钮
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink> getEdgehyperlink(SeriesDetailDto series, CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial> vrMaterialFuture,int pm,String pluginversion) {
        CompletableFuture<SeriesPicDto> picCountComponent = seriesPicCountComponent.get(series.getId());
        CompletableFuture<SeriesVr> vrComponent = seriesVrComponent.get(series.getId());

        //图片张数
        CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist> picCountBtn = picCountComponent.thenApply(p -> {
            if (p == null || p.getItems() == null || p.getItems().isEmpty()) {
                return null;
            }
            long picCount = p.getItems().stream().filter(item -> item.getId() != 13 && item.getId() != 51 && item.getId() != 15).mapToInt(SeriesPicDto.Item::getCount).sum();
            if (picCount == 0) {
                return null;
            }
            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.Builder bottomBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.newBuilder();
            bottomBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_img_220113.png.webp");
            bottomBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_img_dark_220314.png.webp");
            bottomBuilder.setIconwidth(12);
            bottomBuilder.setPosition(0);
            bottomBuilder.setTitle(picCount + "图");
            bottomBuilder.setType(1);
            String url = String.format("autohome://car/seriespicture?seriesid=%s&orgin=0&seriesname=%s", series.getId(), UrlUtil.encode(series.getName()));
            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.0")) {
                url += "&sourceid=1";
            }
            bottomBuilder.setUrl(url);

            bottomBuilder.setPvitem(Pvitem.newBuilder()
                    .putArgvs("typeid", "1")
                    .putArgvs("seriesid", series.getId() + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_click").build())
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_show").build()));
            return bottomBuilder.build();
        }).exceptionally(e->{
            log.error("图片按钮报错",e);
            return null;
        });

        CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist> showBtn = picCountComponent.thenApply(p -> {
            //车展》实拍
            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.Builder showOrPicBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.newBuilder();
            showOrPicBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/owner_bottom_220323.png.webp");
            showOrPicBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/owner_bottom_dark_220323.png.webp");
            showOrPicBuilder.setIconwidth(12);
            showOrPicBuilder.setPosition(3);

            //车展|车主实拍，车展图片大于0
            if (p != null && p.getAutoShowPicCount() > 0 && autoShowConfig.IsBetweenDate()&&pm!=3) {
                showOrPicBuilder.setTitle(autoShowConfig.getAutoshowedgebtn());
                showOrPicBuilder.setType(55);
                showOrPicBuilder.setTagid(autoShowConfig.getAutoshowid());
                showOrPicBuilder.setUrl("autohome://car/seriespicture?seriesid=" + series.getId() + "&orgin=0&categoryid=55");
            } else {
                showOrPicBuilder.setTitle("实拍");
                EdgeHyperShiPaiConfig shiPaiConfig = JsonUtil.toObject(edgeHyperShiPaiText, EdgeHyperShiPaiConfig.class);
                if (shiPaiConfig.getSeriesIds().contains(series.getId()) && StringUtils.isNotEmpty(shiPaiConfig.getTitle())) {
                    showOrPicBuilder.setTitle(shiPaiConfig.getTitle());
                }
                showOrPicBuilder.setType(7);
                showOrPicBuilder.setUrl("autohome://car/seriespicture?seriesid=" + series.getId() + "&orgin=0&categoryid=1");
            }
            showOrPicBuilder.setPvitem(
                    Pvitem.newBuilder()
                            .putArgvs("typeid", showOrPicBuilder.getType() + "")
                            .putArgvs("seriesid", series.getId() + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_show").build())
            );
            return showOrPicBuilder.build();
        }).exceptionally(e->{
            log.error("车展&实拍按钮报错",e);
            return null;
        });

        //内饰
        SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.Builder interiorBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.newBuilder();
        interiorBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_vrin_220113.png.webp");
        interiorBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_vrin_dark_220314.png.webp");
        interiorBuilder.setIconwidth(12);
        interiorBuilder.setPosition(1);
        interiorBuilder.setTitle("内饰");
        interiorBuilder.setType(2);
        interiorBuilder.setUrl("autohome://car/seriespicture?seriesid=" + series.getId() + "&orgin=0&categoryid=10");
        interiorBuilder.setPvitem(Pvitem.newBuilder()
                .putArgvs("typeid", "2")
                .putArgvs("seriesid", series.getId() + "")
                .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_click"))
                .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_show")));

        //视频
        SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.Builder videoBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.newBuilder();
        videoBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_video_220113.png.webp");
        videoBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_video_dark_220523.png.webp");
        videoBuilder.setIconwidth(12);
        videoBuilder.setPosition(2);
        videoBuilder.setTitle("视频");
        videoBuilder.setType(3);
        videoBuilder.setPvitem(Pvitem.newBuilder()
                .putArgvs("typeid", "3")
                .putArgvs("seriesid", series.getId() + "")
                .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_click"))
                .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_show")));
        videoBuilder.setUrl("autohome://article/immersivepagelist?fromtype=10&seriesid=" + series.getId());

        CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist> videoBtn = seriesOwnerComponent.get(series.getId()).thenApply(p -> {
            if (p != null && p.getVideoShowEntry() == 1) {
                videoBuilder.setUrl("autohome://car/seriespicture?seriesid=" + series.getId() + "&orgin=0&tabid=0&categoryid=1101");
            }
            return videoBuilder.build();
        }).exceptionally(e -> {
            log.error("图片下方视频按钮异常", e);
            return videoBuilder.build();
        });

        //3D看车/外观》VR外观》实景看车
        CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist> vrBtn = vrComponent.thenCombineAsync(vrMaterialFuture, (seriesVr, vrMaterial) -> {
            if (vrMaterial == null || vrMaterial.getSpecId() == 0 || !seriesVr.getVrMaterial().isIs_show()) {
                return null;
            }

            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("seriesid", series.getId() + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_show"));

            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.Builder vrBuilder = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist.newBuilder();
            String extShowUrl = "autohome://insidebrowserwk?cangoback=1&navigationbarstyle=2&url=" + URLEncoder.encode("https://pano.autohome.com.cn/car/ext/" + vrMaterial.getSpecId() + "?_ahrotate=2&pagesrc=series_index&ipadtile=1&landscapefullscreen=1&appversion="+pluginversion);
            if (pm==3) {
                vrBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_vrout_220113.png.webp");
                vrBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_vrout_dark_220314.png.webp");
                vrBuilder.setIconwidth(12);
                vrBuilder.setPosition(3);
                vrBuilder.setTitle("VR外观");
                vrBuilder.setType(6);
                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.66.0")) {
                    vrBuilder.setUrl(extShowUrl+UrlUtil.encode("&cp=a0d0f0g0h0i0o0s0j0&ep=k0l0m0o0p0t0w0y0&tp=m0"));
                }else {
                    vrBuilder.setUrl(extShowUrl+UrlUtil.encode("&cp=a0d0f0g0h0i0o0s0j0&ep=k0l0m0o0p0s0t0v0w0y0&tp=m0"));
                }
            }else if (vrMaterial.getShowtype() == 3) {
                vrBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_3d_220113.png");
                vrBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_3d_dark_220314.png");
                vrBuilder.setIconwidth(12);
                vrBuilder.setPosition(3);
                vrBuilder.setType(5);
                if (vrMaterial.getIs3Dpk() == 1) {
                    vrBuilder.setTitle("3D看车");
                    vrBuilder.setUrl(vrMaterial.getJumpUrl());
                } else {
                    vrBuilder.setTitle("3D外观");
                    vrBuilder.setUrl(extShowUrl);
                }
            } else if (seriesVr.getRealScene() != null && StringUtils.isNotEmpty(seriesVr.getRealScene().getShow_url())) {
                vrBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/720vr_230801.png");
                vrBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/720vr_230801.png");
                vrBuilder.setPosition(3);
                vrBuilder.setIconwidth(31);
                vrBuilder.setTitle("实景看车");
                vrBuilder.setType(8);
                String url = "autohome://insidebrowserwk?navigationbarstyle=2&url=" + URLEncoder.encode(seriesVr.getRealScene().getShow_url() + "?_ahrotate=0&landscapefullscreen=1&fullScreen=true") + "&disable_back=1";
                vrBuilder.setUrl(url);
                pvBuilder.setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_pano_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_pano_show"));
            } else {
                vrBuilder.setIcon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_vrout_220113.png.webp");
                vrBuilder.setDarkicon("http://nfiles3.autohome.com.cn/zrjcpk10/series_bt_vrout_dark_220314.png.webp");
                vrBuilder.setIconwidth(12);
                vrBuilder.setPosition(3);
                vrBuilder.setTitle("VR外观");
                vrBuilder.setType(6);
                vrBuilder.setUrl(vrMaterial.getIssuperspeclinkurl() ? vrMaterial.getSuperspeclinkurl() : extShowUrl);
            }

            pvBuilder.putArgvs("typeid", vrBuilder.getType() + "");
            vrBuilder.setPvitem(pvBuilder);
            return vrBuilder.build();
        }).exceptionally(e -> {
            log.error("VR 按钮报错", e);
            return null;
        });

        return CompletableFuture.allOf(picCountBtn, showBtn, videoBtn, vrBtn).thenApply(p -> {
            // 1:图片张数
            // 2:内饰
            // 55:车展|7:实拍
            // 3:视频
            // 5:3D看车/外观|6:VR外观|8:实景看车
            List<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.Bottomlist> bottomList = new ArrayList<>();
            ListUtil.addIfNotNull(bottomList, picCountBtn.join());
            bottomList.add(interiorBuilder.build());
            ListUtil.addIfNotNull(bottomList, showBtn.join());
            if (pm!=3) {
                ListUtil.addIfNotNull(bottomList, videoBtn.join());
            }
            ListUtil.addIfNotNull(bottomList, vrBtn.join());
            return SeriesBaseInfoResponse.Result.Seriesbaseinfo.Edgehyperlink.newBuilder().addAllBottomlist(bottomList).build();
        }).exceptionally(e->{
            log.error("图片下按钮列表报错",e);
            return null;
        });
    }

    /**
     * 车系VR头图
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial> getVrMaterial(int seriesId, int energyType, int pm, String pluginversion) {
        //先取VR外观，在判断是否有超级车型库、3dpk数据，存在更新vr外观的链接和显示标识
        //优先级：超级车型库》3kpk》vr外观
        return seriesVrComponent.get(seriesId).thenApply(seriesVr -> {
            SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.Builder vrMaterial = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.newBuilder();
            if (seriesVr == null) {
                return vrMaterial.build();
            }
            SeriesVrExteriorResult vrInfo = seriesVr.getVrMaterial();
            //vr外观有值，加工处理字段
            if (vrInfo != null && vrInfo.getColor_list() != null && vrInfo.getColor_list().size() > 0) {
                if (pm == 1 && StringUtils.isNotEmpty(vrInfo.getAdurl_ios())) {
                    vrInfo.setSuperspeclinkurl("autohome://insidebrowserwk?navigationbarstyle=2&url="
                            + URLEncoder.encode(vrInfo.getAdurl_ios() + "&ipadtile=1"));
                } else if (pm == 2 && StringUtils.isNotEmpty(vrInfo.getAdurl_android())) {
                    vrInfo.setSuperspeclinkurl("autohome://insidebrowserwk?navigationbarstyle=2&url="
                            + URLEncoder.encode(vrInfo.getAdurl_android() + "&ipadtile=1"));
                }
                vrInfo.setAdurl_ios(null);
                vrInfo.setAdurl_android(null);

                if (StringUtils.isNotEmpty(vrInfo.getJump_url())) {
                    vrInfo.setJump_url(vrInfo.getJump_url()
                            + "?clicktype=1&noext=1&btnoffsety=15&appversion=" + pluginversion);
                }

                //图片大小处理：区分端、http前缀
                try {
                    vrInfo.setColor_list(vrInfo.getColor_list().subList(0, 1)); //只取第一个颜色
                    vrInfo.getColor_list().forEach(i -> {
                        i.getHori().getNormal().forEach(j -> {
                            j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                            j.setUrl(StringUtils.replace(j.getUrl(), "/900x0_autohomecar", "/1500x0_autohomecar"));
                            //android
                            if (pm == 2||pm == 3) {
                                if (StringUtils.isNotEmpty(j.getUrl()) && j.getUrl().contains("panovr.autoimg.cn")) {
                                    j.setUrl(ImageUtils.convertImage_Size(j.getUrl(), ImageSizeEnum.ImgSize_900x600_k1));
                                }
                            }
                        });
                        i.getHori().getPreview().forEach(j -> {
                            j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                        });
                        i.getOver().getNormal().forEach(j -> {
                            j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                            //android
                            if (pm == 2||pm == 3) {
                                if (StringUtils.isNotEmpty(j.getUrl()) && j.getUrl().contains("panovr.autoimg.cn")) {
                                    j.setUrl(ImageUtils.convertImage_Size(j.getUrl(), ImageSizeEnum.ImgSizeVR_4x3_640x0));
                                }
                            }
                        });
                        i.getOver().getPreview().forEach(j -> {
                            j.setUrl(StringUtils.replace(j.getUrl(), "https://", "http://"));
                        });
                    });
                } catch (Exception e) {
                    log.error("vr 切图异常", e);
                }
            } else {
                vrInfo = new SeriesVrExteriorResult();
            }

            AtomicInteger ifSuperGarageCar = new AtomicInteger(1);
            AtomicReference<String> vr_orgiurl = new AtomicReference<>("");
            //超级车型库：ifSuperGarageCar 取值
            //  1：品牌展馆
            //  2：超级车型库1.0
            //  3：超级车型库2.0
            if (vrInfo.getColor_list() != null && vrInfo.getColor_list().size() > 0) {
                List<SeriesVr.VrSuperCar> seriesIndexVrList = seriesVr.getSuperCarList().stream().filter(p -> p.getPosition().equalsIgnoreCase("series_index") && p.getTerminal() == pm).toList();
                seriesIndexVrList.forEach(p -> {
                    vr_orgiurl.set(p.getUrl());
                    if (p.getExhibitionType() == 6) {
                        ifSuperGarageCar.set(2);
                    } else if (p.getExhibitionType() == 8) {
                        ifSuperGarageCar.set(3);
                    }
                });
            }

            //标记是否有效，超级车型库
            if (StringUtils.isNotEmpty(vr_orgiurl.get())) {
                vrInfo.setIssuperspeclinkurl(true);
                vrInfo.setSuperspeclinkurl(vr_orgiurl.get());
            }

            if (ifSuperGarageCar.get() == 2) {
                vrInfo.setShowtype(2);
                vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/series_vrbg2_1208.jpg");
                vrInfo.setIscloud(0);
            } else if (ifSuperGarageCar.get() == 3) {
                vrInfo.setIscloud(0);
                vrInfo.setShowtype(3);
                vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/series_vrbg3_1208.jpg");
            } else {
                //是否有3Dpk数据
                Optional<SeriesVr.VrSuperCar> series3dpk = seriesVr.getSuperCarList().stream().filter(p -> p.getTerminal() == pm && p.getPosition().equalsIgnoreCase("series_3dpk")).findFirst();
                if (series3dpk.isPresent()) {
                    vrInfo.setIs3dpk(1);
                    vrInfo.setShowtype(3);
                    vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/series_vrbg3_1208.jpg");
                    vrInfo.setJump_url(CommonHelper.getInsideBrowerSchemeWK_Goback(UrlUtil.getUrlParamValue(series3dpk.get().getUrl(), "url") + "&navigationbarstyle=2&disable_back=1&ipadtile=1"));
                }
            }

            if (energyType == 1) {
                vrInfo.setVrinfo_backgroudImg("http://nfiles3.autohome.com.cn/zrjcpk10/newenergy_bgm_20211224.png");
            }

            //组件对象转换外展对象
            vrMaterial.setSpecId(vrInfo.getSpecId())
                    .setShowtype(vrInfo.getShowtype())
                    .setIscloud(vrInfo.getIscloud())
                    .setIssuperspeclinkurl(vrInfo.isIssuperspeclinkurl())
                    .setIs3Dpk(vrInfo.getIs3dpk())
                    .setJumpUrl(vrInfo.getJump_url())
                    .setNarration(vrInfo.getNarration())
                    .setSpecState(vrInfo.getSpecState())
                    .setVrspecstate(vrInfo.getVrspecstate())
                    .setSuperspeclinkurl(StringUtils.isNotEmpty(vrInfo.getSuperspeclinkurl()) ? vrInfo.getSuperspeclinkurl() : "")
                    .setVrinfoBackgroudImg(vrInfo.getVrinfo_backgroudImg());
            if (vrInfo.getColor_list() != null) {
                vrInfo.getColor_list().forEach(color -> {
                    SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Builder colorItem = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.newBuilder();
                    colorItem.setId(color.getId())
                            .setColorId(color.getColorId())
                            .setBaseColorName(color.getBaseColorName())
                            .setColorName(color.getColorName())
                            .setColorNames(color.getColorNames())
                            .setColorValue(color.getColorValue())
                            .setColorValues(color.getColorValues())
                            .setRemoteColorId(color.getRemoteColorId());

                    SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Over.Builder overItem = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Over.newBuilder();
                    SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Hori.Builder horiItem = SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Hori.newBuilder();
                    color.getOver().getNormal().forEach(normal -> overItem.addNormal(SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Normal.newBuilder().setSeq(normal.getSeq()).setUrl(normal.getUrl())));
                    color.getOver().getPreview().forEach(preview -> overItem.addPreview(SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Preview.newBuilder().setSeq(preview.getSeq()).setUrl(preview.getUrl())));
                    color.getHori().getNormal().forEach(normal -> horiItem.addNormal(SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Normal.newBuilder().setSeq(normal.getSeq()).setUrl(normal.getUrl())));
                    color.getHori().getPreview().forEach(preview -> horiItem.addPreview(SeriesBaseInfoResponse.Result.Seriesbaseinfo.Vrmaterial.ColorList.Preview.newBuilder().setSeq(preview.getSeq()).setUrl(preview.getUrl())));

                    colorItem.setHori(horiItem).setOver(overItem);
                    vrMaterial.addColorList(colorItem);
                });
            }

            return vrMaterial.build();
        }).exceptionally(e -> {
            log.error("车系vr处理异常", e);
            return null;
        });
    }

    /**
     * 糖豆把配置转成builder
     */
    public List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> functionentrybeans(String json) {
       JSONArray array = new JSONArray(json);
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> result = new ArrayList<>();
        array.forEach(item -> {
            try {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                JsonFormat.parser().ignoringUnknownFields().merge(item.toString(), builder);
                result.add(builder);
            } catch (InvalidProtocolBufferException e) {
                log.error("配置转builder报错", e);
            }
        });
        return result;
    }

    /**
     * 真实续航糖豆
     */
    CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> getNewenergyOwnerRange(int seriesId,int cityId,int intelligentDrivingNum,String seriesName) {
        CompletableFuture<SeriesTestDataDto> testDataTask = seriesTestDataComponent.get(seriesId);
        return seriesEnergyInfoComponent.get(seriesId).thenCombineAsync(beiliKoubeiComponent.get(seriesId, cityId), (energyInfoDto, beili) -> {
            if (energyInfoDto==null||!energyInfoDto.isShowEVSugarBeans()) {
                return null;
            }
            String json = StringUtils.isBlank(newenergyconfigbeans)?newenergyconfigbeansDefault:newenergyconfigbeans;
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> functionBeans = functionentrybeans(json);
            List<FunctionBean.LocationBeansTest> locationBeansTests = JsonUtil.toObjectList(necd_smart_config, FunctionBean.LocationBeansTest.class);
            BeiliKoubeiInfo.SeasonDetail beilidata = beili == null ? null : beili.getDefault();
            for(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item : functionBeans){
                switch (item.getCode()){
                    case "location1": //真实续航
                        if (beilidata!=null) {
                            if (StringUtils.isEmpty(item.getSubtitle())) {
                                item.setSubtitle(beilidata.driveRangeStr().toUpperCase());
                            }
                            item.setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarNewRankRN%2FOwnerRealDataPage%3Fseriesid%3D"+ seriesId);
                            item.setTypeid(100001);
                        } else {
                            item.setTitle("官方续航");
                            if (energyInfoDto.getElectricAttributes()!=null&&StringUtils.isNotEmpty(energyInfoDto.getElectricAttributes().getEnduranceMileage())) {
                                item.setSubtitle(energyInfoDto.getElectricAttributes().getEnduranceMileage());
                                item.setLinkurl(String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode("rn://CarNewRankRN/OwnerRealDataPage?seriesid="+seriesId)));
                            } else {
                                item.setSubtitle("暂无");
                                item.setLinkurl("");
                            }
                            item.setTypeid(100007);
                        }
                        break;
                    case "location2"://百公里电费
                        String linkUrl = StringUtils.EMPTY;
                        if (testDriveConfig!=null&&testDriveConfig.getIsvr() == 1
                                && Objects.nonNull(energyInfoDto.getCockpitVrResult())
                                && StringUtils.isNotEmpty(energyInfoDto.getCockpitVrResult().getShow_url())) {
                            linkUrl = energyInfoDto.getCockpitVrResult().getShow_url();
                        }
                        if (StringUtils.isNotEmpty(linkUrl)) {
                            item.setTitle("智能座舱");
                            item.setSubtitle("360°体验");
                            linkUrl = "autohome://insidebrowserwk?url=" + UrlUtil.encode(linkUrl) + "&disable_back=1";
                            item.setLinkurl(linkUrl);
                            item.setTypeid(100002);
                            item.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znzc_zy_230712.png");
                        } else if (locationBeansTests!=null&&locationBeansTests.stream().anyMatch(x->x.getSeriesid()==seriesId&&StringUtils.isNotEmpty(x.getLocation2url()))) {
                            FunctionBean.LocationBeansTest first = locationBeansTests.stream().filter(x -> x.getSeriesid() == seriesId).findFirst().get();
                            item.setTitle(first.getLocation2title());
                            item.setSubtitle(first.getLocation2subtitle());
                            item.setLinkurl(first.getLocation2url());
                            item.setTypeid(100002);
                            item.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znzc_zy_230712.png");
                        } else if (beilidata!=null&&StringUtils.isNotEmpty(beilidata.cost())) {//百公里电费
                            if (StringUtils.isEmpty(item.getSubtitle())) {
                                item.setSubtitle(beilidata.cost());
                            }
                            item.setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarNewRankRN%2FOwnerRealDataPage%3Fseriesid%3D" + seriesId + "%26tabid%3D1");
                            item.setTypeid(100003);
                        } else {//快充时间 》 慢充时间
                            item.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znzc_kc_230712.png");
                            item.setTitle("快充时间");
                            item.setSubtitle("暂无");
                            item.setLinkurl("");
                            item.setTypeid(100010);
                            if (energyInfoDto.getElectricAttributes()!=null) {
                                if (!StringUtils.isEmpty(energyInfoDto.getElectricAttributes().getFastChargetime())) {
                                    item.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znzc_kc_230712.png");
                                    item.setTitle("快充时间");
                                    item.setSubtitle(energyInfoDto.getElectricAttributes().getFastChargetime());
                                    item.setLinkurl(String.format("autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s&hassummaryconfig=1&subtargetsectionname=%s", seriesId, UrlUtil.encode(seriesName), UrlUtil.encode("快充时间(小时)")));
                                    item.setTypeid(100008);
                                } else if (!StringUtils.isEmpty(energyInfoDto.getElectricAttributes().getSlowChargetime())) {
                                    item.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znzc_mc_230712.png");
                                    item.setTitle("慢充时间");
                                    item.setSubtitle(energyInfoDto.getElectricAttributes().getSlowChargetime());
                                    item.setLinkurl(String.format("autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s&hassummaryconfig=1&subtargetsectionname=%s", seriesId, UrlUtil.encode(seriesName), UrlUtil.encode("慢充时间(小时)")));
                                    item.setTypeid(100009);
                                }
                            }
                        }
                        break;
                    case "location3"://智能驾驶 or 电池容量
                        if (intelligentDrivingNum>0) {
                            item.setTitle("智能驾驶");
                            item.setSubtitle(intelligentDrivingNum + "项功能");
                            item.setLinkurl("autohome://car/aidriving?seriesid=" + seriesId);
                            item.setTypeid(beilidata!=null ? 100004 : 100011);
                        } else {
                            //智能驾驶
                            SeriesTestDataDto testDataDto = testDataTask.join();
                            if (testDataDto != null && testDataDto.getTestData() != null&&testDataDto.getTestData().size()>0) {
                                var testItem = testDataDto.getTestData().get(0).getTestItemlist().stream().filter(e -> "智能驾驶".equals(e.getName())).findFirst().orElse(null);
                                if (testItem != null) {
                                    item.setSubtitle(testItem.getShowValue().split("/")[0]);
                                    item.setTitle("智能驾驶");
                                    item.setLinkurl("autohome://car/ahtest?seriesid=" + seriesId + "&sourceid=3&specid=" + testDataDto.getTestData().get(0).getSpecId() + "&dataid=" + testDataDto.getTestData().get(0).getDataId() + "&tabid=7");
                                    item.setTypeid(beilidata != null ? 100005 : 100012);
                                }
                            }
                            //电池容量
                            if (StringUtils.isEmpty(item.getSubtitle())
                                    &&energyInfoDto.getElectricAttributes()!=null
                                    &&StringUtils.isNotEmpty(energyInfoDto.getElectricAttributes().getBatteryCapacity())) {
                                item.setTitle("电池能量");
                                item.setSubtitle(energyInfoDto.getElectricAttributes().getBatteryCapacity());
                                item.setLinkurl(String.format("autohome://carcompare/paramcontrast?seriesid=%s&seriesname=%s&hassummaryconfig=0&subtargetsectionname=%s", seriesId, UrlUtil.encode(seriesName), UrlUtil.encode("电池能量(kWh)")));
                                item.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/dc_230607.png");
                                item.setTypeid(beilidata!=null ? 100006 : 100013);
                            }
                            if (StringUtils.isEmpty(item.getSubtitle())) {
                                return null;
                            }
                        }
                        break;
                }
                item.setScale(1);
                item.setPvitem(getPvItem(seriesId,item.getTypeid(),cityId,"car_series_newenergy_func_click","car_series_newenergy_func_show"));
            }

            return functionBeans.stream().map(x->x.build()).collect(Collectors.toList());
        }).exceptionally(e -> {
            log.error(String.format("获取真实续航糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> getEnergyconfigbeans(int seriesId,int cityId) {
        return seriesEnergyInfoComponent.get(seriesId).thenCombineAsync(beiliKoubeiComponent.get(seriesId, cityId), (energyInfoDto, beili) -> {
            if (beili == null) {
                return null;
            }
            var season = beili.getDefault();
            if(season == null){
                return null;
            }
            String xh_title = StringUtils.isBlank(season.driveRangeStr()) ? "暂无" : season.driveRangeStr();
            String df_title = StringUtils.isBlank(season.cost()) ? "暂无" : season.cost();
            String kc_title = (energyInfoDto == null || energyInfoDto.getElectricAttributes()==null || StringUtils.isBlank(energyInfoDto.getElectricAttributes().getFastChargetime())) ? "暂无" : energyInfoDto.getElectricAttributes().getFastChargetime();
            if(xh_title.equals("暂无") && df_title.equals("暂无") && kc_title.equals("暂无")){
                return null;
            }
            SeriesBaseInfoResponse.Result.Itemlist.Data.Energyinfo.Builder result = SeriesBaseInfoResponse.Result.Itemlist.Data.Energyinfo.newBuilder()
                    .setTitle("车主真实数据")
                    .setSubtitle("大家都在看")
                    .addAllImglist(Arrays.asList(
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_summary_config_header_1@3x.webp",
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_summary_config_header_2@3x.webp",
                            "http://nfiles3.autohome.com.cn/zrjcpk10/car_summary_config_header_3@3x.webp"))
                    .setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarNewRankRN%2FOwnerRealDataPage%3Fseriesid%3D" + seriesId)
                    .addList(SeriesBaseInfoResponse.Result.Itemlist.Data.Energyinfo.List.newBuilder()
                            .setTitle(xh_title)
                            .setSubtitle("车主续航")
                            .setPointcolor("#25C9FF"))
                    .addList(SeriesBaseInfoResponse.Result.Itemlist.Data.Energyinfo.List.newBuilder()
                            .setTitle(kc_title)
                            .setSubtitle("快充时间")
                            .setPointcolor("#0096FF"))
                    .addList(SeriesBaseInfoResponse.Result.Itemlist.Data.Energyinfo.List.newBuilder()
                            .setTitle(df_title)
                            .setSubtitle("百公里电费")
                            .setPointcolor("#85F8FF"))
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("cityid", cityId + "")
                            .putArgvs("seriesid", seriesId + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_energy_new_config_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_energy_new_config_show").build()));

            SeriesBaseInfoResponse.Result.Itemlist.Builder item = SeriesBaseInfoResponse.Result.Itemlist.newBuilder();
            item.setType(11087);
            item.setId(11087);
            item.setData(
                    SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                            .setSeriesid(seriesId)
                            .setSortid(2)
                            .setEnergyinfo(result)
            );

            return item.build();
        }).exceptionally(e->{
            log.error(String.format("getEnergyconfigbeans, seriesId:%s ", seriesId), e);
            return null;
        });
    }

    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> convertNewenergyOwnerRangeToOperatePosition(List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list) {
        if (list == null || list.size() == 0)
            return new ArrayList<>();
        return list.stream().map(i -> {
            SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
            builder.setTypeid(i.getTypeid());
            builder.setLinkurl(i.getLinkurl());
            builder.setScale(1);
            if (StringUtils.equals(i.getTitle(), "真实续航")) {
                builder.setContent(String.format("车主真实续航%s", i.getSubtitle()));
                builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/zsxh_231226.png");
            } else if (StringUtils.equals(i.getTitle(), "智能座舱")) {
                builder.setContent("360°身临其境体验座舱");
                builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znzc_231226.png");
            } else if (StringUtils.equals(i.getTitle(), "智能驾驶")) {
                builder.setContent(i.getSubtitle());
                builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/znjs_231226.png");
            }
            if (StringUtils.isNotEmpty(builder.getContent())) {
                return builder.build();
            }
            return null;
        }).filter(x -> x != null).collect(Collectors.toList());
    }

    /**
     * 官方续航糖豆
     *
     * @return
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> getEnergyconfiglist(SeriesDetailDto seriesDetailDto,int seriesId, int sortId) {
        //非新能源  || 没有在售和停售车型的都不显示
        if (seriesDetailDto == null || seriesDetailDto.getEnergytype()!=1 || (seriesDetailDto.getSellSpecNum() <=0 && seriesDetailDto.getStopSpecNum() <= 0)) {
            return CompletableFuture.completedFuture(null);
        }
        return seriesEnergyInfoComponent.get(seriesId).thenApply(info -> {
            if(info==null || info.getElectricAttributes() == null)
                return null;

            String paramConfigUrl = seriesDetailDto.getParamIsShow() == 1 ? getCarParamUrl(seriesDetailDto, "") : "";

            String noData = "暂无";
            String logo = "http://nfiles3.autohome.com.cn/zrjcpk10/necd_rl_20230320.png.webp";

            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> builderList = new ArrayList<>();
            builderList.add(SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setSubtitle("容量").setLogob(logo).setLogo(logo).setLinkurl(paramConfigUrl)
                    .setTitle(info != null && info.getElectricAttributes() != null && StringUtils.isNotEmpty(info.getElectricAttributes().getBatteryCapacity()) ? info.getElectricAttributes().getBatteryCapacity() : noData).build());

            logo = "http://nfiles3.autohome.com.cn/zrjcpk10/necd_xh_20230320.png.webp";
            builderList.add(SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setSubtitle("官方续航").setLogob(logo).setLogo(logo).setLinkurl(paramConfigUrl)
                    .setTitle(info != null && info.getElectricAttributes() != null && StringUtils.isNotEmpty(info.getElectricAttributes().getEnduranceMileage()) ? info.getElectricAttributes().getEnduranceMileage() : noData).build());

            logo = "http://nfiles3.autohome.com.cn/zrjcpk10/necd_kc_20230320.png.webp";
            builderList.add(SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setSubtitle("快充").setLogob(logo).setLogo(logo).setLinkurl(paramConfigUrl)
                    .setTitle(info != null && info.getElectricAttributes() != null && StringUtils.isNotEmpty(info.getElectricAttributes().getFastChargetime()) ? info.getElectricAttributes().getFastChargetime() : noData).build());

            logo = "http://nfiles3.autohome.com.cn/zrjcpk10/necd_mc_20230320.png.webp";
            builderList.add(SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setSubtitle("慢充").setLogob(logo).setLogo(logo).setLinkurl(paramConfigUrl)
                    .setTitle(info != null && info.getElectricAttributes() != null && StringUtils.isNotEmpty(info.getElectricAttributes().getSlowChargetime()) ? info.getElectricAttributes().getSlowChargetime() : noData).build());

            SeriesBaseInfoResponse.Result.Itemlist.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                    .setType(11072)
                    .setId(11072)
                    .setData(
                            SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                    .addAllList(builderList)
                                    .setSeriesid(seriesId)
                                    .setSortid(sortId)
                    );
            return builder.build();
        }).exceptionally(e -> {
            log.error(String.format("获取官方续航糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    /**
     * 冬测：11092
     * seriesTestDataComponent.get(seriesId)
     *
     * @param seriesId
     * @param funcabtest
     * @param testComponent
     * @return
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> winnerTestItemList(int seriesId, int funcabtest,String energytestab, CompletableFuture<SeriesTestDataDto> testComponent) {

        if(winterTest2023Config==null
                || winterTest2023Config.getIsopen()==0
                || winterTest2023Config.getExcludelist().contains(seriesId)
                || !Arrays.asList("test_a", "test_b", "test_c", "test_d","test_g", "test_h", "test_i").contains(energytestab)){
            return CompletableFuture.completedFuture(null);
        }

        return testComponent.thenApply(info -> {
            if (info == null || info.getTestWinterData() == null || info.getTestWinterData().size() == 0)
                return null;
            SeriesBaseInfoResponse.Result.Itemlist.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.newBuilder();
            List<String> sortList = Arrays.asList("能耗-寒冷-续航-寒冷续航-寒冷EV续航", "能耗-湿冷-续航-湿冷续航-湿冷EV续航", "能耗-暖热-续航-暖热续航-暖热EV续航");
            if(seriesId == 7003){
                sortList = Arrays.asList("性能-极寒-极寒干地加速-极寒加速", "安全-极寒-极寒干地制动-极寒制动", "性能-极寒-极寒雪地极速-雪地极速");
            }
            SeriesTestDataDto.SeriesTestDataWinter120Dto data = info.getTestWinterData().get(0);
            int typeId = 10001;
            int nodataCount=0;
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> items = new ArrayList<>();
            for (String name : sortList) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                String[] split = name.split("-");
                SeriesTestDataDto.TestWinterItemSummary summary = data.getTestItemlist().stream().filter(j -> StringUtils.equals(j.getLevel1name(), split[0])&& StringUtils.contains(j.getLevel2name(), split[1]) && StringUtils.contains(j.getLevel3name(), split[2])).findFirst().orElse(null);
                if (summary==null) {
                    nodataCount++;
                    item.setTitle("-");
                }else {
                    item.setTitle(summary.getShowValue() + summary.getUnit());
                }
                if (StringUtils.startsWith(name, "能耗")) {
                    item.setSubtitle(data.getFueltypedetail() == 4 ? split[3] : split[4]);
                } else {
                    item.setSubtitle(split[3]);
                }
                item.setTypeid(typeId);
                typeId++;
                items.add(item.build());
            }
            if (items.size()==nodataCount) {
                return null;
            }
            SeriesBaseInfoResponse.Result.Itemlist.Data.Builder dataBuilder = SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder();
            dataBuilder.addAllList(items);  //此处最多3个
            dataBuilder.setPvitem(
                    Pvitem.newBuilder()
                            .putArgvs("brand", "1")
                            .putArgvs("seriesid", seriesId + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_winter_click"))
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_winter_show"))
            );
            int sourceid = funcabtest == 1 ? 3 : 4;
            dataBuilder.setLeftimg("http://nfiles3.autohome.com.cn/zrjcpk10/wintertest2023_series_cardleft_240304.webp");
            dataBuilder.setLinkurl(String.format("autohome://car/supertest?sourceid=%s&dataid=%s&seriesid=%s&specid=%s%s", sourceid, data.getDataId(), seriesId, data.getSpecId(),data.getSpecId()!=null&&data.getSpecId()==62650?"&zoneid=1":""));
            dataBuilder.setSeriesid(seriesId);
            dataBuilder.setSortid(funcabtest == 0 ? 2 : 1);
            dataBuilder.setEnergetype(1);
            builder.setId(11092);
            builder.setType(11092);
            builder.setData(dataBuilder);
            return builder.build();
        }).exceptionally(e->{
            log.error(String.format("车系冬测入口异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    /**
     * 实验版糖豆区
     */
    public CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist>> getBeansNewVersion(SeriesDetailDto seriesDetailDto,
                                                                                              int cityId,
                                                                                              int noDefaultCityId,
                                                                                              int pm,
                                                                                              String newcarsingledingyueab,
                                                                                              String pluginversion,
                                                                                              CompletableFuture<SeriesCityAskPriceDto> askPriceComponent,
                                                                                              CompletableFuture<List<NewSeriesCityHotNewsAndTabDto>> newSeriesCityHotNewsAndTabComponentAsync,
                                                                                              String bzlTest
    ) {
        //新车日历
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo> carCalendarFuture = getCarCalendar(seriesDetailDto, cityId, pluginversion, newcarsingledingyueab)
                .exceptionally(e -> {
                    log.error(String.format("getCarCalendar-error seriesId:%s ", seriesDetailDto.getId()), e);
                    return null;
                });
        //灰色糖豆
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> greySugarBeansFuture = getGreySugarBeans(pluginversion, seriesDetailDto, cityId)
                .exceptionally(e -> {
                    log.error(String.format("getGreySugarBeans-error seriesId:%s ", seriesDetailDto.getId()), e);
                    return null;
                });
        //糖豆
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> newBeans11085Future = getNewBeans11085(seriesDetailDto, cityId, noDefaultCityId, pm, pluginversion, askPriceComponent, bzlTest)
                .exceptionally(e -> {
                    log.error(String.format("getNewBeans11085-error seriesId:%s ", seriesDetailDto.getId()), e);
                    return null;
                });

        return CompletableFuture.allOf(carCalendarFuture, greySugarBeansFuture, newBeans11085Future, newSeriesCityHotNewsAndTabComponentAsync).thenApply(x -> {
            List<SeriesBaseInfoResponse.Result.Itemlist> list = new ArrayList<>();
            SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo newCarInfo = carCalendarFuture.join();
            if (newCarInfo != null) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.Builder data = SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                        .setNewcarinfo(newCarInfo)
                        .setEnergetype(seriesDetailDto.getEnergytype())
                        .setSortid(1)
                        .setSeriesid(seriesDetailDto.getId());
                list.add(SeriesBaseInfoResponse.Result.Itemlist.newBuilder().setType(11084).setId(11084).setData(data).build());
            }
            ListUtil.addIfNotNull(list, greySugarBeansFuture.join());
            ListUtil.addIfNotNull(list, newBeans11085Future.join());
            ListUtil.addIfNotNull(list, getSeriesHotItemList(seriesDetailDto, newSeriesCityHotNewsAndTabComponentAsync.join(), cityId));
            return list;
        }).exceptionally(e -> {
            log.error(String.format("糖豆itemlist异常 seriesId:%s ", seriesDetailDto.getId()), e);
            return null;
        });
    }

    /**
     * 加热点数据
     */
    SeriesBaseInfoResponse.Result.Itemlist getSeriesHotItemList(SeriesDetailDto seriesDetailDto,List<NewSeriesCityHotNewsAndTabDto> list,int cityId) {
        if (Objects.isNull(list)) {
            return null;
        }
        Optional<NewSeriesCityHotNewsAndTabDto> specHot = list.stream().filter(hotItem -> NewSeriesHotTabEnum.SPEC.getTypeName().equals(hotItem.getType())).findFirst();
        if (specHot.isPresent()) {
            SeriesBaseInfoResponse.Result.Itemlist.Data.HotSpotInfo.Builder specHostInfo = SeriesBaseInfoResponse.Result.Itemlist.Data.HotSpotInfo.newBuilder()
                    .setIconurl(specHot.get().getIcon())
                    .setScale(1.93f)
                    .setTitle(specHot.get().getTitle())
                    .setSubtitle(specHot.get().getSubtitle())
                    .setPvitem(
                            Pvitem.newBuilder()
                                    .putArgvs("cityid", String.valueOf(cityId))
                                    .putArgvs("title", specHot.get().getTitle())
                                    .putArgvs("seriesid", String.valueOf(seriesDetailDto.getId()))
                                    .putArgvs("hotid", specHot.get().getPageCardDataId().toString())
                                    .putArgvs("hotposition", NewSeriesHotTabEnum.SPEC.getTypeName())
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_operate_entry_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_operate_entry_show")))
                    .setLinkurl(specHot.get().getLinkUrl());
            SeriesBaseInfoResponse.Result.Itemlist.Data.Builder data = SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                    .setHotspotinfo(specHostInfo)
                    .setSortid(4)
                    .setSeriesid(seriesDetailDto.getId());
            return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                    .setType(11105).setId(11105)
                    .setData(data).build();
        }
        return null;
    }

    public CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> getNewBeans11085(SeriesDetailDto seriesDetailDto,
                                                                                      int cityId,
                                                                                      int noDefaultCityId,
                                                                                      int pm,
                                                                                      String pluginversion,
                                                                                      CompletableFuture<SeriesCityAskPriceDto> askPriceComponent,
                                                                                      String bzlTest) {
        List<CompletableFuture> tasks = new ArrayList<>();
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11085 = beans_11085(pluginversion, seriesDetailDto.getBrandId(), seriesDetailDto.getBrandName(), seriesDetailDto.getId(), seriesDetailDto.getName(), cityId, seriesDetailDto.getHotSpecId(), seriesDetailDto.getEnergytype(), seriesDetailDto.getState(), noDefaultCityId, pm, askPriceComponent, "", bzlTest);
        tasks.add(beans_11085);

        String json = StringUtils.isBlank(functionentrybeansone) ? functionentrybeansoneDefault : functionentrybeansone;
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> beans = functionentrybeans(json);
        for (SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder : beans) {
            switch (builder.getCode()) {
                case "SC":
                    //只有新版才改这个名字
                    if (StringUtils.isEmpty(builder.getSubtitle())) {
                        builder.setTitle("资讯");
                        builder.setSubtitle("之家专业评测");
                    }
                    if (StringUtils.isEmpty(builder.getLinkurl())) {
                        builder.setLinkurl(String.format("autohome://article/newseriesarticle?seriesid=%s&seriesname=%s&tabid=10000", seriesDetailDto.getId(), UrlUtil.encode(seriesDetailDto.getName())));
                    }
                    builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/zixun_20240822.webp");
                    builder.setSortid(2);
                    tasks.add(getTestStandardBean(builder, seriesDetailDto.getId(), seriesDetailDto.getName()));
                    break;
                case "KB":
                    builder.setSortid(3);
                    builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/koubei_20240822.webp");
                    tasks.add(getKoubeiBean(builder, seriesDetailDto.getId()));
                    break;
                case "TCJ":
                    builder.setSortid(1);
                    builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/tichejia_20240822.webp");
                    tasks.add(getTichejiaBean(builder, seriesDetailDto.getId(), seriesDetailDto.getHotSpecId()));
                    break;
            }
            //设置埋点信息
            Pvitem.Builder pvitem = Pvitem.newBuilder();
            pvitem.putArgvs("seriesid", seriesDetailDto.getId() + "");
            pvitem.putArgvs("typeid", builder.getTypeid() + "");
            pvitem.putArgvs("typename", builder.getTitle() + "");
            pvitem.putArgvs("cityid", cityId + "");
            pvitem.putArgvs("type","0");
            pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
            pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
            builder.setScale(1);
            builder.setPvitem(pvitem);
        }

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {
            SeriesBaseInfoResponse.Result.Itemlist bean11085 = beans_11085.join();

            if (bean11085 != null && bean11085.getData() != null && bean11085.getData().getListList() != null) {
                List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list = new ArrayList<>(bean11085.getData().getListList());
                //移除 原糖豆：亮点/车型差异，养车成本; 或者 不可点击的
                list.removeIf(e -> StringUtils.equalsAnyIgnoreCase(e.getCode(), "BYJG", "CYPZ", "CXLD") || StringUtils.isEmpty(e.getLinkurl()));

                //处理原11084糖豆
                if (!beans.isEmpty()) {
                    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> beanSort = beans.stream()
                            .filter(obj -> !"2scJ".equals(obj.getCode()))
                            .map(item -> item.build())
                            .sorted(Comparator.comparing(SeriesBaseInfoResponse.Result.Itemlist.Data.List::getSortid))
                            .collect(Collectors.toList());

                    //把论坛拿出来，和原11084糖豆混合排序
                    SeriesBaseInfoResponse.Result.Itemlist.Data.List club = list.stream().filter(s -> "CLUB".equals(s.getCode())).findFirst().orElse(null);
                    if (club != null && beanSort.size() > 2) {
                        list.remove(club);
                        beanSort.add(2, club);
                    }
                    //有本地报价时放到本地报价后面，未侧插入到第一位
                    int index = list.size() > 0 && "CLJG".equals(list.get(0).getCode()) ? 1 : 0;
                    beanSort.removeIf(e->StringUtils.isEmpty(e.getLinkurl()));
                    list.addAll(index, beanSort);
                }
                return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                        .setType(11085).setId(11085)
                        .setData(SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                .addAllList(list)
                                .setSortid(3)
                                .setSeriesid(seriesDetailDto.getId())
                                .setEnergetype(seriesDetailDto.getEnergytype())).build();

            }
            return null;
        }).exceptionally(e -> {
            log.error("getNewBeans11085-error", e);
            return null;
        });
    }


    /**
     *  实验版新能源糖豆-灰糖豆
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> getGreySugarBeans(String pluginversion,
                                                                                SeriesDetailDto seriesDetailDto,
                                                                                int cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> builders = new CopyOnWriteArrayList<>();

        //1、仅新能源 真实续航XX公里/官方续航XX公里，智能化功能XX项（原智能驾驶）
        if (seriesDetailDto.getEnergytype() == 1) {
            //智能驾驶
            if (seriesDetailDto.getIntelligentDrivingNum() > 0) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                item.setTitle(String.format("%s%s项", "智能化功能", seriesDetailDto.getIntelligentDrivingNum()))
                        .setLinkurl("autohome://car/aidriving?seriesid=" + seriesDetailDto.getId())
                        .setTypeid(OperateListEnum.INTELLIGENT.getTypeid())
                        .setSortid(OperateListEnum.INTELLIGENT.getOrder());
                ListUtil.addIfNotNull(builders, item);
            } else {
                tasks.add(seriesTestDataComponent.get(seriesDetailDto.getId()).thenAccept(testDataDto -> {
                    if (testDataDto != null && testDataDto.getTestData() != null && testDataDto.getTestData().size() > 0) {
                        var testItem = testDataDto.getTestData().get(0).getTestItemlist().stream().filter(e -> "智能驾驶".equals(e.getName())).findFirst().orElse(null);
                        if (testItem != null) {
                            SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                            item.setTitle("智能化功能" + testItem.getShowValue().split("/")[0])
                                    .setLinkurl("autohome://car/ahtest?seriesid=" + seriesDetailDto.getId() + "&sourceid=3&specid=" + testDataDto.getTestData().get(0).getSpecId() + "&dataid=" + testDataDto.getTestData().get(0).getDataId() + "&tabid=7")
                                    .setTypeid(OperateListEnum.INTELLIGENT.getTypeid())
                                    .setSortid(OperateListEnum.INTELLIGENT.getOrder());
                            ListUtil.addIfNotNull(builders, item);
                        }
                    }
                }).exceptionally(e -> {
                    log.error(String.format("getGreySugarBeans seriesId:%s ", seriesDetailDto.getId()), e);
                    return null;
                }));
            }
            //真实续航XX公里/官方续航XX公里
            tasks.add(seriesEnergyInfoComponent.get(seriesDetailDto.getId()).thenAcceptBothAsync(beiliKoubeiComponent.get(seriesDetailDto.getId(), cityId), (seriesEnergyInfoDto, beiliKoubeiInfo) -> {
                if (beiliKoubeiInfo != null && beiliKoubeiInfo.getDefault() != null) {
                    SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                    item.setTitle("真实续航" + beiliKoubeiInfo.getDefault().driveRangeStr().toUpperCase())
                            .setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarNewRankRN%2FOwnerRealDataPage%3Fseriesid%3D" + seriesDetailDto.getId())
                            .setTypeid(OperateListEnum.REALBATTERY.getTypeid())
                            .setSortid(OperateListEnum.REALBATTERY.getOrder());
                    ListUtil.addIfNotNull(builders, item);
                } else if (seriesEnergyInfoDto != null && seriesEnergyInfoDto.getElectricAttributes() != null && StringUtils.isNotEmpty(seriesEnergyInfoDto.getElectricAttributes().getEnduranceMileage())) {
                    SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                    item.setTitle("官方续航" + seriesEnergyInfoDto.getElectricAttributes().getEnduranceMileage())
                            .setLinkurl(String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode("rn://CarNewRankRN/OwnerRealDataPage?seriesid="+seriesDetailDto.getId())))
                            .setTypeid(OperateListEnum.OFFICIALBATTERY.getTypeid())
                            .setSortid(OperateListEnum.OFFICIALBATTERY.getOrder());
                    ListUtil.addIfNotNull(builders, item);
                }
            }).exceptionally(e -> {
                log.error(String.format("getGreySugarBeans seriesId:%s ", seriesDetailDto.getId()), e);
                return null;
            }));
        }

        //2、原糖豆：亮点/车型差异，养车成本
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item_CXLD = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
        tasks.add(getCYPZBean(item_CXLD, seriesDetailDto.getId()));
        tasks.add(seriesCityYangcheComponent.get(seriesDetailDto.getId(), cityId).thenAccept(info -> {
            if (info != null) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                item.setTitle(OperateListEnum.YANGCHE.getName())
                        .setLinkurl(info.getAppHref())
                        .setTypeid(OperateListEnum.YANGCHE.getTypeid())
                        .setSortid(OperateListEnum.YANGCHE.getOrder());
                ListUtil.addIfNotNull(builders, item);
            }
        }).exceptionally(e -> {
            log.error(String.format("getGreySugarBeans seriesId:%s,cityId:%s ", seriesDetailDto.getId(), cityId), e);
            return null;
        }));

        //3、原运营横栏（仅新能源）：优惠补贴（原最高补贴），购车权益，OTA升级，充电桩，车机真体验。
        tasks.add(getOperatePositionButie(seriesDetailDto.getId(),cityId).thenAccept(data->{
            if (data!=null) {
                ListUtil.addIfNotNull(builders,convertItem(data, OperateListEnum.BUTIE));
            }
        }));

        tasks.add(getOtaAndRights(seriesDetailDto.getId()).thenAccept(lists -> {
            if (lists!=null&&lists.size()>0) {
                lists.forEach(x-> ListUtil.addIfNotNull(builders,convertItem(x,OperateListEnum.getByTypeid(x.getTypeid()))));
            }
        }));

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {
            //车机
            ListUtil.addIfNotNull(builders,convertItem(addOperatePositionCheji(seriesDetailDto.getId()),OperateListEnum.REALCAREXPERIENCE));

            if (StringUtils.isNotEmpty(item_CXLD.getLinkurl())) {
                ListUtil.addIfNotNull(builders, SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                        .setTitle(item_CXLD.getTitle())
                        .setLinkurl(item_CXLD.getLinkurl())
                        .setTypeid(OperateListEnum.HIGHLIGHTS_MODELDIFFERENCES.getTypeid())
                        .setSortid(OperateListEnum.HIGHLIGHTS_MODELDIFFERENCES.getOrder()));
            }
            if (builders.isEmpty()) {
                return null;
            }
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list =builders.stream()
                    .sorted(Comparator.comparing(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder::getSortid))
                    .map(builder -> {
                        //设置埋点信息
                        Pvitem.Builder pvitem = Pvitem.newBuilder();
                        pvitem.putArgvs("seriesid", seriesDetailDto.getId() + "");
                        pvitem.putArgvs("typeid", builder.getTypeid() + "");
                        pvitem.putArgvs("typename", builder.getTitle() + "");
                        pvitem.putArgvs("cityid", cityId + "");
                        pvitem.putArgvs("type","1");
                        pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
                        pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
                        builder.setPvitem(pvitem);
                        return builder.build();
                    }).collect(Collectors.toList());
            return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                    .setType(11106).setId(11106)
                    .setData(
                            SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                    .addAllList(list)
                                    .setSortid(2)
                                    .setSeriesid(seriesDetailDto.getId())
                                    .setEnergetype(seriesDetailDto.getEnergytype())
                    ).build();
        }).exceptionally(e -> {
            log.error("getGreySugarBeans-error", e);
            return null;
        });
    }

    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> getGreySugarBeansNew(SeriesDetailDto seriesDetailDto, int cityId) {
        List<CompletableFuture> tasks = new ArrayList<>();
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> builders = new CopyOnWriteArrayList<>();

        //3、原运营横栏（仅新能源）：优惠补贴（原最高补贴），购车权益，OTA升级，充电桩，车机真体验。
        tasks.add(getOperatePositionButie(seriesDetailDto.getId(),cityId).thenAccept(data->{
            if (data!=null) {
                ListUtil.addIfNotNull(builders,convertItem(data, OperateListEnum.BUTIE));
            }
        }));

        tasks.add(getOtaAndRights(seriesDetailDto.getId()).thenAccept(lists -> {
            if (lists!=null&&lists.size()>0) {
                lists.forEach(x-> ListUtil.addIfNotNull(builders,convertItem(x,OperateListEnum.getByTypeid(x.getTypeid()))));
            }
        }));

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {
            //车机体验
            ListUtil.addIfNotNull(builders, convertItemCjty(addOperatePositionCheji(seriesDetailDto.getId()), OperateListEnum.REALCAREXPERIENCE));

            if (builders.isEmpty()) {
                return null;
            }
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list =builders.stream()
                    .sorted(Comparator.comparing(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder::getSortid))
                    .map(builder -> {
                        //设置埋点信息
                        Pvitem.Builder pvitem = Pvitem.newBuilder();
                        pvitem.putArgvs("seriesid", seriesDetailDto.getId() + "");
                        pvitem.putArgvs("typeid", builder.getTypeid() + "");
                        pvitem.putArgvs("typename", builder.getTitle() + "");
                        pvitem.putArgvs("cityid", cityId + "");
                        pvitem.putArgvs("type","1");
                        pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
                        pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
                        builder.setPvitem(pvitem);
                        return builder.build();
                    }).collect(Collectors.toList());
            return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                    .setType(11106).setId(11106)
                    .setData(
                            SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                    .addAllList(list)
                                    .setSortid(5)
                                    .setSeriesid(seriesDetailDto.getId())
                                    .setEnergetype(seriesDetailDto.getEnergytype())
                    ).build();
        }).exceptionally(e -> {
            log.error("getGreySugarBeansNew-error", e);
            return null;
        });
    }

    private SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder convertItem(SeriesBaseInfoResponse.Result.Itemlist.Data.List x, OperateListEnum operateListEnum) {
        if (x==null||operateListEnum==null) {
            return null;
        }
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                .setTitle(operateListEnum.getName())
                .setLinkurl(x.getLinkurl())
                .setTypeid(operateListEnum.getTypeid())
                .setSortid(operateListEnum.getOrder());
        return builder;
    }

    private SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder convertItemCjty(SeriesBaseInfoResponse.Result.Itemlist.Data.List x, OperateListEnum operateListEnum) {
        if (x==null||operateListEnum==null) {
            return null;
        }
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                .setTitle("车机体验")
                .setLinkurl(x.getLinkurl())
                .setTypeid(operateListEnum.getTypeid())
                .setSortid(operateListEnum.getOrder());
        return builder;
    }

    /**
     * 糖豆 itemlist
     */
    public CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist>> getBeans(
            SeriesBaseInfoRequest request,
            SeriesDetailDto seriesDetailDto,
            int cityId, int funcabtest,int noDefaultCityId,int pm,String newcarsingledingyueab,
            CompletableFuture<Boolean> autoShowing,
            String energytestab,String pluginversion,String subscribeabtest,String subscribetitleabtest,
            CompletableFuture<SeriesCityAskPriceDto> askPriceComponent,
            String topArticleHotAb
    ) {
        int brandId = seriesDetailDto.getBrandId();
        String brandName = seriesDetailDto.getBrandName();
        int seriesId = seriesDetailDto.getId();
        String seriesName = seriesDetailDto.getName();
        int hotSpecId = seriesDetailDto.getHotSpecId();
        int seriesState = seriesDetailDto.getState();
        int energetype = seriesDetailDto.getEnergytype();
        int intelligentDrivingNum = seriesDetailDto.getIntelligentDrivingNum();
        //冬测
        CompletableFuture<SeriesTestDataDto> testComponent = seriesTestDataComponent.get(seriesId);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> energyconfigbeans = getEnergyconfigbeans(seriesId,cityId);
        //新能源糖豆：2023冬测(11092) > 真实续航(新版本：11086，老版本：11087) > 官方续航(11072)
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> newEnergyBeans = beans_newEnergy(pluginversion, seriesDetailDto,energetype, seriesId, funcabtest,energytestab ,cityId,intelligentDrivingNum,seriesName,testComponent,energyconfigbeans);

        //灰色糖豆
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> greySugarBeansFuture = getGreySugarBeansNew(seriesDetailDto,cityId)
                .exceptionally(e->{
                    log.error(String.format("getGreySugarBeans-error seriesId:%s ", seriesDetailDto.getId()), e);
                    return null;
                });

        if (funcabtest == 0) {
            /** 老版全量的糖豆列表
             * ItemList[0].data.pricelist 第一排价格糖豆(11070)
             * ItemList[0].data.list 第一排下拉区域(11073)
             * ItemList[1].data.list 2023冬测（11092） > 车主续航（11087） > 官方续航（11072）  只有新能源有
             * ItemList[2].data.list 第二排糖豆（11073）
             */
            CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11070 = beans_11070(seriesId, cityId, brandId, hotSpecId, noDefaultCityId, seriesName,intelligentDrivingNum,pm, autoShowing, askPriceComponent, testComponent,energyconfigbeans, topArticleHotAb, request.getPluginversion());
            CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11073 = beans_11073(seriesId, hotSpecId, cityId, seriesName, brandId, brandName);

            return CompletableFuture.allOf(beans_11070, beans_11073, newEnergyBeans).thenApply(x -> {
                List<SeriesBaseInfoResponse.Result.Itemlist> list = new ArrayList<>();
                ListUtil.addIfNotNull(list, beans_11070.join());
                ListUtil.addIfNotNull(list, newEnergyBeans.join());
                ListUtil.addIfNotNull(list, beans_11073.join());
                return list;
            });
        } else if (funcabtest == 1) {
            /** 新版全量的糖豆列表
             * ItemList[0].data.list 2023冬测（11092） > 车主续航（11086） > 官方续航（11072） 只有新能源有
             * ItemList[1].data.operatelist 第一排下拉区域(11084)
             * ItemList[1].data.list 第一排价格糖豆(11084)
             * ItemList[2].data.list 第二排糖豆（11085）
             */
            CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11084 = beans_11084(seriesDetailDto,pluginversion,subscribeabtest,subscribetitleabtest,cityId, noDefaultCityId, pm, newcarsingledingyueab,autoShowing, testComponent, topArticleHotAb, request.getArticlexuangouab(), request.getSeriesnewlineab(), request.getSeriesnewlinerecab(), request.getSerieskbrec());
            CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11085 = beans_11085(pluginversion, brandId, brandName, seriesId, seriesName, cityId, hotSpecId, energetype, seriesState, noDefaultCityId, pm, askPriceComponent, request.getSeriesnewlineab(), request.getBzlabtest());
            CompletableFuture<Boolean> existCarCalendar = existCarCalendar(pluginversion, energetype, seriesId, seriesDetailDto.getIsNewCar());

            return CompletableFuture.allOf(newEnergyBeans, greySugarBeansFuture, beans_11084, beans_11085,existCarCalendar).thenApply(x -> {
                List<SeriesBaseInfoResponse.Result.Itemlist> list = new ArrayList<>();
                if (!existCarCalendar.join()) {
                    ListUtil.addIfNotNull(list, newEnergyBeans.join());
                }
                ListUtil.addIfNotNull(list, beans_11084.join());
                ListUtil.addIfNotNull(list, beans_11085.join());
                if (CommonHelper.isTakeEffectVersion(pluginversion, "11.67.8") && Arrays.asList("C","E").contains(request.getSeriesnewlineab()) && null != greySugarBeansFuture.join()) {
                    ListUtil.addIfNotNull(list, greySugarBeansFuture.join());
                }
                return list;
            }).exceptionally(e->{
                log.error(String.format("糖豆itemlist异常 seriesId:%s ", seriesId), e);
                return null;
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 老版第一排糖豆
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11070(
            int seriesId,
            int cityId,
            int brandId,
            int hotSpecId,
            int noDefaultCityId,
            String seriesName,
            int intelligentDrivingNum,
            int pm,
            CompletableFuture<Boolean> autoShowing,
            CompletableFuture<SeriesCityAskPriceDto> askPriceComponent,
            CompletableFuture<SeriesTestDataDto> testComponent,
            CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> energyconfigbeans,
            String topArticleHotAb,
            String pluginversion
    ) {
        //车主续航
//        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> ownerRange = getNewenergyOwnerRange(seriesId,cityId,intelligentDrivingNum,seriesName);
        //价格
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> priceListTask = getPriceList(seriesId, cityId, brandId, hotSpecId, noDefaultCityId, seriesName,pm, askPriceComponent);
        //下拉列表
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> operateList = getOperateList(brandId, seriesId, seriesName, cityId, autoShowing, topArticleHotAb, pluginversion, "", 0);
        return CompletableFuture.allOf(priceListTask, askPriceComponent, operateList, testComponent, energyconfigbeans).thenApply(x -> {

            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list = operateList.join();
            //如果有实测数据，则把续航数据放在list前面
            SeriesTestDataDto seriesTestData = testComponent.join();
            if (seriesTestData != null && seriesTestData.getTestWinterData() != null && seriesTestData.getTestWinterData().size() > 0) {
                SeriesBaseInfoResponse.Result.Itemlist ownerRangeList = energyconfigbeans.join();
                if (ownerRangeList != null && ownerRangeList.getData()!=null) {
                    SeriesBaseInfoResponse.Result.Itemlist.Data.Energyinfo energyinfo = ownerRangeList.getData().getEnergyinfo();
                    if(energyinfo!=null && energyinfo.getListList()!=null && energyinfo.getListList().size() > 0){

                        int index = 0;
                        if(list!=null && list.size()>0 && list.get(0).getTypeid()==113){
                            //有补贴的时候，补贴放第一位
                            index = 1;
                        }

                        var info = energyinfo.getListList().get(0);
                        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder newItem = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                        newItem.setTypeid(10001);
                        newItem.setContent(info.getSubtitle() + info.getTitle());
                        newItem.setLinkurl(energyinfo.getLinkurl());
                        newItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/zsxh_231226.png");
                        list.add(index, newItem.build());
                    }
                }
            }

            return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                    .setType(11070).setId(11070)
                    .setData(
                            SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                    .addAllList(list)
                                    .addAllPricelist(priceListTask.join())
                                    .setSortid(1)
                                    .setSeriesid(seriesId)
                    ).build();
        });
    }


    /**
     * 老版第二排糖豆
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11073(int seriesId, int specId, int cityId, String seriesName, int brandId, String brandName) {
        String json = StringUtils.isBlank(functionbeancfg) ? functionbeancfgDefault : functionbeancfg;
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> beans = functionentrybeans(json);

        Map<String, String> replaceValue = Maps.newHashMap();
        replaceValue.put("{seriesid}", "" + seriesId);
        replaceValue.put("{brandid}", "" + brandId);
        replaceValue.put("{specid}", "" + specId);
        replaceValue.put("{seriesname}", UrlUtil.encode(seriesName));
        replaceValue.put("{brand}", UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}", brandId, brandName, seriesId, seriesName)));

        List<CompletableFuture> tasks = new ArrayList<>();
        for (SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder : beans) {
            Pvitem.Builder pvitem = Pvitem.newBuilder();
            pvitem.putArgvs("seriesid", seriesId + "");
            pvitem.putArgvs("typeid", builder.getTypeid() + "");
            pvitem.putArgvs("typename", builder.getTitle() + "");
            pvitem.putArgvs("cityid", cityId + "");
            pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
            pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
            builder.setPvitem(pvitem);

            TypeIdEnum typeIdEnum = TypeIdEnum.getByCode(builder.getCode());
            if (typeIdEnum != null) {
                builder.setTypeid(typeIdEnum.getTypeid());
            }

            String url = builder.getLinkurl();
            for (String k : replaceValue.keySet()) {
                url = url.replace(k, replaceValue.get(k));
            }
            url = UrlUtil.encodeRnAndFlutterUrl(url);
            builder.setLinkurl(url);

            switch (builder.getCode()) {
                case "ASK":
                    builder.setLinkurl("autohome://club/topiclist?bbstype=c&seriesid=" + seriesId + "&select=10004&from=2");
                    break;
                case "JXS":
                    builder.setLinkurl("autohome://car/pricelibrary?brandid=" + brandId + "&seriesid=" + seriesId + "&specid=" + specId + "&seriesname=" + UrlUtil.encode(seriesName).replace("%20", "") + "&tabindex=1&fromtype=1");
                    break;
                case "BY":
                    builder.setLinkurl("autohome://rninsidebrowser?url=" + UrlUtil.encode("rn://CarMaintenanceCostRN/CarSeriesManual?seriesid=" + seriesId + "&source=17&showtopbar=true"));
                    break;
                case "YH":
                    builder.setLinkurl(builder.getLinkurl().replace("%3A%2F%2Fcar%2F", "%3A%2F%2Fassistant%2F"));
                    break;
                case "SC":
                    tasks.add(getTestStandardBean(builder, seriesId, seriesName));
                    break;
            }
        }
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(xx -> {
            //如果实测没url，移除调
            beans.removeIf(x -> "SC".equals(x.getCode()) && "".equals(x.getLinkurl()));
            if (beans.stream().anyMatch(x -> "SC".equals(x.getCode()))) {
                beans.removeIf(e -> "NEWS".equals(e.getCode()));
            }
            beans.removeIf(e -> "BY".equals(e.getCode()) && !"改装".equals(e.getTitle()));

            SeriesBaseInfoResponse.Result.Itemlist.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.newBuilder();
            builder.setData(
                    SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                            .setSeriesid(seriesId)
                            .setSortid(4)
                            .addAllList(beans.stream().map(x -> x.build()).collect(Collectors.toList()))
            ).setId(11073).setType(11073);
            return builder.build();
        }).exceptionally(e -> {
            log.error("老版本糖豆列表报错", e);
            return null;
        });
    }

    CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> getPriceList(
            int seriesId,
            int cityId,
            int brandId,
            int hotSpecId,
            int noDefaultCityId,
            String seriesName,
            int pm,
            CompletableFuture<SeriesCityAskPriceDto> askPriceComponent
    ) {

        //经销商价格
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> dealerPrice = dealerPrice(seriesId, brandId, hotSpecId, cityId, seriesName, askPriceComponent);

        //车主价
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ownerPrice = ownerPrice(seriesId, hotSpecId,cityId);

        //二手车价
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> sscPirce = sscPirce(seriesId,cityId);

        //新零售对比试驾 > 养车成本
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ycPrice = ycPrice(seriesId, cityId, noDefaultCityId,pm);

        return CompletableFuture.allOf(dealerPrice, ownerPrice).thenApply(x -> {
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list = new ArrayList<>();
            list.add(dealerPrice.join());
            list.add(ownerPrice.join());
            list.add(sscPirce.join());
            list.add(ycPrice.join());
            return list.stream().filter(item -> item != null).collect(Collectors.toList());
        });
    }

    /**
     * 新能源糖豆
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_newEnergy(
            String pluginversion,
            SeriesDetailDto seriesDetailDto,
            int energetype,
            int seriesId, int funcabtest,String energytestab, int cityId,int intelligentDrivingNum,String seriesName,
            CompletableFuture<SeriesTestDataDto> testComponent,
            CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> energyconfigbeans
    ) {
        if (energetype != 1) {
            return CompletableFuture.completedFuture(null);
        }

        //双智入口
        try {
            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.65.5") && com.autohome.app.cars.common.utils.StringUtils.isNotEmpty(shuangzhi_entry_config)) {
                ShuangZhiEntryConfig shuangZhiEntryConfig = JsonUtil.toObject(shuangzhi_entry_config, ShuangZhiEntryConfig.class);
                if (shuangZhiEntryConfig != null && shuangZhiEntryConfig.getIsopen() == 1 && shuangZhiEntryConfig.getSeriesid() == seriesId) {
                    SeriesBaseInfoResponse.Result.Itemlist.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.newBuilder();
                    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> items = new ArrayList<>();
                    shuangZhiEntryConfig.getWinnertestinfo().getDatainfo().forEach(x -> {
                        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                        item.setTitle(x.getTitle());
                        item.setSubtitle(x.getSubtitle());
                        items.add(item.build());
                    });

                    SeriesBaseInfoResponse.Result.Itemlist.Data.Builder dataBuilder = SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder();
                    dataBuilder.addAllList(items);  //此处最多3个
                    dataBuilder.setPvitem(
                            Pvitem.newBuilder()
                                    .putArgvs("brand", "1")
                                    .putArgvs("seriesid", seriesId + "")
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_winter_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_winter_show"))
                    );
                    dataBuilder.setLeftimg(shuangZhiEntryConfig.getWinnertestinfo().getLeftimg());
                    dataBuilder.setLinkurl(shuangZhiEntryConfig.getSeriesscheme());
                    dataBuilder.setSeriesid(seriesId);
                    dataBuilder.setEnergetype(1);
                    builder.setId(11092);
                    builder.setType(11092);
                    builder.setData(dataBuilder);
                    return CompletableFuture.completedFuture(builder.build());
                }
            }
        } catch (Exception e) {
            log.error("双智入口错误", e);
        }

        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> winnerTestItemList = winnerTestItemList(seriesId, funcabtest,energytestab, testComponent);
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> ownerRange = getNewenergyOwnerRange(seriesId,cityId, intelligentDrivingNum,seriesName);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> officialRange = getEnergyconfiglist(seriesDetailDto,seriesId, funcabtest == 1 ? 2 : 3);
        return CompletableFuture.allOf(winnerTestItemList, ownerRange, officialRange).thenApply(x -> {
            SeriesBaseInfoResponse.Result.Itemlist winnerTest = winnerTestItemList.join();
            if (winnerTest != null) {
                return winnerTest;
            } else {
                if (funcabtest == 0) {
                    SeriesBaseInfoResponse.Result.Itemlist item11087 = energyconfigbeans.join();
                    if (item11087 != null) {
                        return item11087;
                    }
                } else {
                    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ownerRangeList = ownerRange.join();
                    if (ownerRangeList != null && ownerRangeList.size() > 0
                            && !(seriesDetailDto.getState() == 40 && ownerRangeList.stream().anyMatch(xx -> xx.getTypeid() == 100007))) { //排除车系停售且有官方续航的情况-因为落地页没有数据
                        return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                                .setType(11086)
                                .setId(11086)
                                .setData(
                                        SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                                .addAllList(ownerRangeList)
                                                .setSeriesid(seriesId)
                                                .setSortid(1)
                                ).build();
                    }
                }
                return officialRange.join();  //11072
//
//
//                List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ownerRangeList = ownerRange.join();
//                if (ownerRangeList != null && ownerRangeList.size() > 0) {
//                    if (funcabtest == 1) {
//                        return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
//                                .setType(11086)
//                                .setId(11086)
//                                .setData(
//                                        SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
//                                                .addAllList(ownerRangeList)
//                                                .setSeriesid(seriesId)
//                                                .setSortid(1)
//                                ).build();
//                    } else {
//                        //老版本的 11087
//                        return energyconfigbeans.join();
//                    }
//                } else {
//                    //11072
//                    return officialRange.join();
//                }
            }
        }).exceptionally(e->{
            log.error("新能源糖豆异常",e);
            return null;
        });
    }


    /**
     * 新版第一排糖豆，卡片id:11084
     */
    public CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11084(
            SeriesDetailDto seriesDetailDto,String pluginversion,String subscribeabtest,String subscribetitleabtest,
            int cityId, int noDefaultCityId, int pm,String newcarsingledingyueab,
            CompletableFuture<Boolean> autoShowing,
            CompletableFuture<SeriesTestDataDto> testComponent,
            String topArticleHotAb,
            String articlexuangouab,
            String seriesnewlineab, String seriesnewlinerecab, String serieskbrec
    ) {
        int brandId = seriesDetailDto.getBrandId();
        int seriesId = seriesDetailDto.getId();
        String seriesName = seriesDetailDto.getName();
        int hotSpecId = seriesDetailDto.getHotSpecId();
        int energetype = seriesDetailDto.getEnergytype();
        int state = seriesDetailDto.getState();
        int intelligentDrivingNum = seriesDetailDto.getIntelligentDrivingNum();

        String json = StringUtils.isBlank(functionentrybeansone) ? functionentrybeansoneDefault : functionentrybeansone;
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> beans = functionentrybeans(json);
        List<CompletableFuture> tasks = new ArrayList<>();

        if (pm == 3){
            //鸿蒙-屏蔽二手车入口
            beans.removeIf(sugarBean -> StringUtils.equals(sugarBean.getTitle(),"二手车"));
        }

        //车主续航
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> ownerRange = getNewenergyOwnerRange(seriesId, cityId, intelligentDrivingNum, seriesName);
        //下拉列表
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> operateList = getOperateList(brandId, seriesId, seriesName, cityId, autoShowing, topArticleHotAb, pluginversion, articlexuangouab, state);
        //新车日历-二期
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo> carCalendar = getCarCalendar(seriesDetailDto, cityId, pluginversion, newcarsingledingyueab);
        tasks.add(ownerRange);
        tasks.add(operateList);
        tasks.add(carCalendar);
        // 新运营栏
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.NewLineInfo> newLineInfo = getNewLineInfo(seriesId, seriesnewlineab, seriesnewlinerecab, serieskbrec);
        tasks.add(newLineInfo);

        for (SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder : beans) {
            switch (builder.getCode()) {
                case "SC":
                    //只有新版才改这个名字
                    if (StringUtils.isEmpty(builder.getSubtitle())) {
                        builder.setTitle("资讯");
                        builder.setSubtitle("之家专业评测");
                    }
                    if (StringUtils.isEmpty(builder.getLinkurl())) {
                        builder.setLinkurl(String.format("autohome://article/newseriesarticle?seriesid=%s&seriesname=%s&tabid=10000", seriesId, UrlUtil.encode(seriesName)));
                    }
                    tasks.add(getTestStandardBean(builder, seriesId, seriesName));
                    break;
                case "KB":
                    if (CommonHelper.isTakeEffectVersion(pluginversion, "11.67.8") && Arrays.asList("B","C","D","E","F").contains(seriesnewlineab)) {
                        builder.setCode("CLUB").setTitle("论坛").setTypeid(1476520).setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_lt.png");
                        tasks.add(getClubBeanOnly(builder, seriesId));
                    } else {
                        tasks.add(getKoubeiBean(builder, seriesId));
                    }
                    break;
                case "TCJ":
                    tasks.add(getTichejiaBean(builder, seriesId, hotSpecId));
                    break;
                case "2scJ":
                    tasks.add(getUsedCarBean(builder, seriesId));
                    break;
            }

            //设置埋点信息
            Pvitem.Builder pvitem = Pvitem.newBuilder();
            pvitem.putArgvs("seriesid", seriesId + "");
            pvitem.putArgvs("typeid", builder.getTypeid() + "");
            pvitem.putArgvs("typename", builder.getTitle() + "");

            if (builder.getCode().equals("2scJ")){
                int seriesState = seriesDetailDto.getState();
                String eid = seriesState==40?"112877":"111397";
                pvitem.putArgvs("eid", eid);
            }
            pvitem.putArgvs("cityid", cityId + "");
            pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
            pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
            builder.setScale(1);
            builder.setPvitem(pvitem);
        }
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {
            //如果有冬测数据，则把官方续航放到这里
            SeriesTestDataDto seriesTestData = testComponent.join();
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> operateListResult = operateList.join();
            if (pm == 3){
                operateListResult.removeIf(listDto -> listDto.getTypeid() != 113 && listDto.getTypeid() != 10);
            }
            if (!CommonHelper.isTakeEffectVersion(pluginversion, "11.63.0")) {
                if (seriesTestData != null && seriesTestData.getTestWinterData() != null && seriesTestData.getTestWinterData().size() > 0) {
                    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ownerRangeList = ownerRange.join();
                    if (ownerRangeList != null && ownerRangeList.size() > 0) {
                        int index = operateListResult != null && operateListResult.size() > 0 && operateListResult.get(0).getTypeid() == 113 ? 1 : 0;
                        operateListResult.addAll(index, convertNewenergyOwnerRangeToOperatePosition(ownerRangeList));
                    }
                }
            }
            SeriesCityAskPriceDto canAskPriceDto = seriesCityAskPriceNewComponent.get(seriesId, cityId).join();
            boolean canAskPrice = null != canAskPriceDto ? true : false;
            SeriesBaseInfoResponse.Result.Itemlist.Data.Builder data = SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                    .addAllList(setNewCarSubscription(beans, seriesDetailDto, pluginversion, subscribeabtest, subscribetitleabtest, cityId, pm, canAskPrice)
                            .stream().map(item -> item.build()).collect(Collectors.toList()));
            // 实验分组（ID=101963）只有A和B才返回旧的运营栏
            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.67.8")) {
                if(Arrays.asList("A", "B", "X").contains(seriesnewlineab)) {
                    data.addAllOperatelist(operateListResult);
                }
            } else {
                data.addAllOperatelist(operateListResult);
            }
            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.67.8") && Arrays.asList("C", "D", "E", "F").contains(seriesnewlineab)) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.NewLineInfo lineInfo = newLineInfo.join();
                if (null != lineInfo) {
                    data.setNewlineinfo(lineInfo);
                }
            }
                    data.setEnergetype(energetype)
                    .setSortid(3)
                    .setSeriesid(seriesId);
            SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo newCarInfo = carCalendar.join();
            if (Objects.nonNull(newCarInfo)) {
                data.setNewcarinfo(newCarInfo);
            }
            return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                    .setType(11084).setId(11084)
                    .setData(data).build();
        }).exceptionally(e -> {
            log.error(String.format("新版第一排糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.NewLineInfo> getNewLineInfo(int seriesId, String seriesnewlineab, String seriesnewlinerecab, String serieskbrec) {

        Map<String, Integer> dataTypeMap = Map.of("A",1,"B",1,"C",2,"D",2,"E",1,"F",1,"G",2,"H",2);
        Map<String, String> profileMap = Map.of("A","","B","","C","","D","","E",serieskbrec,"F",serieskbrec,"G",serieskbrec,"H",serieskbrec);
        Map<String, Integer> sortMap = Map.of("A",5,"B",2,"C",5,"D",2,"E",5,"F",2,"G",5,"H",2);

        if (null == dataTypeMap.get(seriesnewlinerecab) || null == profileMap.get(seriesnewlinerecab) || null == sortMap.get(seriesnewlinerecab)) {
            return CompletableFuture.completedFuture(null);
        }

        if (Arrays.asList("E","F","G","H").contains(seriesnewlinerecab) && StringUtils.isEmpty(serieskbrec)) {
            return CompletableFuture.completedFuture(null);
        }

        return seriesClubPostComponent.get(seriesId, dataTypeMap.get(seriesnewlinerecab), profileMap.get(seriesnewlinerecab), sortMap.get(seriesnewlinerecab)).thenApply(clubDto -> {

            if (null == clubDto || null == clubDto.getPost() || StringUtils.isEmpty(clubDto.getPost().getTitle())) {
                return null;
            }

            String iconHot = "http://nfiles3.autohome.com.cn/zrjcpk10/series_op_hot_1022@3x.png";
            String iconNew = "http://nfiles3.autohome.com.cn/zrjcpk10/series_op_new_1022@3x.png";

            List<String> iconHotTest = Arrays.asList("A","C","E","G");
            List<String> iconNewTest = Arrays.asList("B","D","F","H");

            SeriesBaseInfoResponse.Result.Itemlist.Data.NewLineInfo.Builder newLineInfo = SeriesBaseInfoResponse.Result.Itemlist.Data.NewLineInfo.newBuilder();
            if (Arrays.asList("C", "D").contains(seriesnewlineab)) {
                if (StringUtils.isEmpty(clubDto.getPost().getPic())) {
                    if (iconHotTest.contains(seriesnewlinerecab)) {
                        newLineInfo.setIcon(iconHot).setMaxline(2).setIconw(16).setIconh(16);
                    }
                    if (iconNewTest.contains(seriesnewlinerecab)) {
                        newLineInfo.setIcon(iconNew).setMaxline(2).setIconw(34).setIconh(16);
                    }
                } else {
                    newLineInfo.setIcon(ImageUtils.convertImage_Size_ClubImgBean(clubDto.getPost().getPic(), ImageSizeEnum.ImgSize_4x3_200x150));
                    newLineInfo.setMaxline(2).setIconw(48).setIconh(36);
                }
            }
            if (Arrays.asList("E", "F").contains(seriesnewlineab)) {
                if (iconHotTest.contains(seriesnewlinerecab)) {
                    newLineInfo.setIcon(iconHot).setMaxline(1).setIconw(16).setIconh(16);
                }
                if (iconNewTest.contains(seriesnewlinerecab)) {
                    newLineInfo.setIcon(iconNew).setMaxline(1).setIconw(34).setIconh(16);
                }
            }

            newLineInfo.setTitle(clubDto.getPost().getTitle())
                    .setLinkurl(clubDto.getPost().getScheme())
                    .setAngleindex(1)
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("seriesid", String.valueOf(seriesId))
                            .putArgvs("interest", "xingqu".equals(serieskbrec) ? "1" : "0")
                            .putArgvs("buy", "maiche".equals(serieskbrec) ? "1" : "0")
                            .putArgvs("owner", "chezhu".equals(serieskbrec) ? "1" : "0")
                            .putArgvs("picture", iconNew.equals(newLineInfo.getIcon()) || iconHot.equals(newLineInfo.getIcon()) ? "0" : "1")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_newbar_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_newbar_show").build()));

            return newLineInfo.build();
        }).exceptionally(e -> {
            log.error(String.format("新运营栏数据错误 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    //油车全新车系，油车非全新车系，新能源非全新车系
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo> getCarCalendar(SeriesDetailDto seriesDetailDto,int cityId,String pluginversion,String newcarsingledingyueab) {
        if (!CommonHelper.isTakeEffectVersion(pluginversion, "11.65.0") || !StringUtils.equalsAnyIgnoreCase(newcarsingledingyueab, "B", "B1")) {
            return CompletableFuture.completedFuture(null);
        }
        //新车，资讯新车+品库新车（资讯标签<=1）
        AtomicBoolean newCar = new AtomicBoolean((seriesDetailDto.getIsNewCar()&&seriesDetailDto.getNewBrandTagId()<=1) || seriesDetailDto.getNewBrandTagId() == 1);
        NewCarCalendarConfig newCarCalendarConfig = JsonUtil.toObject(NewCarCalendarConfigJson, NewCarCalendarConfig.class);
        //车系黑名单
        if (newCarCalendarConfig.getBlacklist().contains(seriesDetailDto.getId())
                //新能源开关
                || (seriesDetailDto.getEnergytype() == 1 && newCarCalendarConfig.getNewenergy_isopen() == 0)
                //油车开关
                || (seriesDetailDto.getEnergytype() != 1 && newCarCalendarConfig.getFuel_isopen() == 0)
                //新能源非全新车系
                || (newCar.get() && seriesDetailDto.getEnergytype() == 1)
                //只要是新能源，标签一定大于1 ===> 新能源非全新车系
                || (seriesDetailDto.getEnergytype() == 1 && seriesDetailDto.getNewBrandTagId() <= 1)) {
            return CompletableFuture.completedFuture(null);
        }
        return newCarCalendarComponent.get(seriesDetailDto.getId()).thenApply(carCalendarDto -> {
            if (carCalendarDto == null) {
                return null;
            }
            //级别黑名单
            if (StringUtils.isNotEmpty(carCalendarDto.getEventLevel()) && newCarCalendarConfig.getBlackeventlevel().contains(carCalendarDto.getEventLevel())) {
                return null;
            }

            boolean open = (newCar.get() && seriesDetailDto.getEnergytype() != 1) || seriesDetailDto.getNewBrandTagId() > 1;
            Date date = new Date();
            //非新车系 ,无上市(产品库车型的)时间，或其90天后隐藏掉入口
            if (!open && carCalendarDto.getSpecpublishtime() != null) {
                open = DateUtil.getDistanceOfTwoDate(carCalendarDto.getSpecpublishtime(), date) <= newCarCalendarConfig.getShow_limit();
                newCar.set(true);
            }
            if (!open) {
                return null;
            }

            Pvitem.Builder pvItem = Pvitem.newBuilder();
            pvItem.putArgvs("seriesid", seriesDetailDto.getId() + "")
                    .putArgvs("cityid", cityId + "")
                    //1新能源全新车系、2新能源非全新车系、3油车全新车系、4油车非全新车系
                    .putArgvs("seriestype",seriesDetailDto.getNewBrandTagId() > 1?(seriesDetailDto.getEnergytype() == 1?"2":"4"):"3")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_newcar_rightbook_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_newcar_leftbook_show"));
            SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo.Builder newCarInfo = SeriesBaseInfoResponse.Result.Itemlist.Data.NewCarInfo.newBuilder();
            newCarInfo.setBtntext("查看")
                    .setLinkurl("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=" + UrlUtil.encode("rn://CarAskpriceRN/CarCalendarPage?panValid=0&seriesid=" + seriesDetailDto.getId() + "&sourceid=20"));
            // 上市时间是否公布
            String listTime = Objects.nonNull(seriesDetailDto.getOnLineTime())
                    ? DateUtil.format(seriesDetailDto.getOnLineTime(), "yy-MM-dd")
                    : "";
            if (newCar.get()) {
                // 销量发布时间
                String salesCount = Objects.nonNull(carCalendarDto.getCarsales())
                        && Objects.nonNull(carCalendarDto.getCarsales().getCarddata())
                        ? carCalendarDto.getCarsales().getCarddata().getCurrentmonth() + "销量" + carCalendarDto.getCarsales().getCarddata().getCurrentmonthsales() + "台"
                        : "";
                // 已公布上市时间
                if (StringUtils.isNotEmpty(listTime)) {
                    // 上市时间前
                    if (date.before(seriesDetailDto.getOnLineTime())) {
                        newCarInfo.setTitle("上市时间").setTime(listTime).setNobtntext("上市通知");
                        pvItem.putArgvs("type", "1");
                    } else {
                        // 上市时间后
                        pvItem.putArgvs("type", "2");
                        if (StringUtils.isNotEmpty(salesCount)) {
                            // 销量已发布
                            newCarInfo.setTitle(salesCount).setNobtntext("更新提醒");
                        } else {
                            newCarInfo.setTitle("销量公布时间").setTime("?-?-?").setNobtntext("更新提醒");
                        }
                    }
                } else {
                    newCarInfo.setTitle("上市时间").setTime("?-?-?").setNobtntext("上市通知");
                }
            } else {
                NewCarCalendarConfig.SeriesTag seriesTag = newCarCalendarConfig.getSeriestag().stream().filter(x -> x.getTagid() == seriesDetailDto.getNewBrandTagId()).findFirst().orElse(null);
                if (seriesTag==null) {
                    return null;
                }
                String tagName = seriesTag.getTagname();
                //上市前
                if (seriesDetailDto.getOnLineTime() != null && date.before(seriesDetailDto.getOnLineTime())) {
                    pvItem.putArgvs("type", "1");
                    // 发布时间是否公布
                    String publishDate = Objects.nonNull(carCalendarDto.getPublishdate())
                            && Objects.nonNull(carCalendarDto.getPublishdate().getDate())
                            ? DateUtil.format(carCalendarDto.getPublishdate().getDate(), "yy-MM-dd")
                            : "";
                    //有发布时间且今天<发布时间
                    if (StringUtils.isNotEmpty(publishDate) && date.before(carCalendarDto.getPublishdate().getDate())) {
                        newCarInfo.setTitle(tagName + "发布").setTime(publishDate).setNobtntext("上市通知");
                    } else {
                        newCarInfo.setTitle(tagName + "上市").setTime(listTime).setNobtntext("上市通知");
                    }
                } else {
                    //上市后
                    pvItem.putArgvs("type", "2");
                    String subtitle = Objects.nonNull(carCalendarDto.getEvaluating()) ? "车友提车分享" : "新车测评视频即将发布";
                    newCarInfo.setTitle(tagName+"已上市").setNobtntext("更新提醒").setSubtitle(subtitle);
                }
            }
            pvItem.putArgvs("entrance1", newCarInfo.getTitle())
                    .putArgvs("button",newCarInfo.getNobtntext())
                    .putArgvs("time", newCarInfo.getTime());
            newCarInfo.setPvitem(pvItem);
            return newCarInfo.build();
        }).exceptionally(e -> {
            log.error(String.format("新车日历入口 seriesId:%s ", seriesDetailDto.getId()), e);
            return null;
        });
    }
    /**
     * 新车的糖豆订阅
     * biztype:口碑：201，提车价：202，保险：203
     * sourceid:7车系页口碑糖豆 8车系页车主价格糖豆 9车系页保险糖豆
     */
    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> setNewCarSubscription(List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> itemlist,SeriesDetailDto seriesDetailDto,String pluginversion,String subscribeabtest,String subscribetitleabtest, int cityId,int pm, boolean canAskPrice) {
        if (!CommonHelper.isTakeEffectVersion(pluginversion, "11.60.5") || !seriesDetailDto.getIsNewCar() || !StringUtils.equals(subscribeabtest, "B") || itemlist == null) {
            return itemlist;
        }
        SubscribePopContent newSubscribeEntryPop = JsonUtil.toObject(newSubscribeEntryPopJson, SubscribePopContent.class);
        boolean isBlack = newSubscribeEntryPop.getSeriesblackList().contains(seriesDetailDto.getId());
        if (newSubscribeEntryPop == null || isBlack || !canAskPrice) {
            return itemlist;
        }
        subscribetitleabtest=StringUtils.equals(subscribetitleabtest, "X")?"A":subscribetitleabtest;
        for (SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder : itemlist) {
            switch (builder.getCode()) {
                case "KB":
                    if (StringUtils.isEmpty(builder.getLinkurl()) || "暂无".equals(builder.getSubtitle()) || StringUtils.isEmpty(builder.getSubtitlehighlight())) {
                        setSugarTitle(builder, newSubscribeEntryPop, 201, 7, seriesDetailDto, subscribetitleabtest, cityId, pm);
                    }
                    break;
                case "TCJ":
                    if (StringUtils.isEmpty(builder.getLinkurl()) || "暂无".equals(builder.getSubtitle())) {
                        setSugarTitle(builder, newSubscribeEntryPop, 202, 8, seriesDetailDto, subscribetitleabtest, cityId, pm);
                    }
                    break;
                case "2scJ":
                    setSugarTitle(builder, newSubscribeEntryPop, 203, 9, seriesDetailDto, subscribetitleabtest, cityId, pm);
                    break;
            }
        }
        return itemlist;
    }
    void setSugarTitle(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder item,SubscribePopContent subscribePopContent,int biztype,int sourceId,SeriesDetailDto seriesDetailDto,String subscribetitleabtest, int cityId,int pm) {
        try {
            if (subscribePopContent.getCloseBizId() != null && subscribePopContent.getCloseBizId().contains(biztype)) {
                return;
            }
            SubscribePopContent.EntryText entryText = null;
            SubscribePopContent.EntryContent experimentalGroup = subscribePopContent.getEntryContent().stream().filter(x -> StringUtils.equals(x.getId(), subscribetitleabtest)).findFirst().orElse(null);
            if (experimentalGroup != null && experimentalGroup.getContent() != null) {
                SubscribePopContent.Content content = experimentalGroup.getContent().stream().filter(x -> x.getBizId() == biztype).findFirst().orElse(null);
                if (content != null && content.getEntryText() != null) {
                    entryText = content.getEntryText().stream().filter(x -> "M".equals(x.getTextType())).findFirst().orElse(null);
                }
            }
            if (entryText != null) {
                entryText.setTextType(subscribetitleabtest);//哪个实验组
            } else {
                return;
            }
            Pvitem.Builder pvItem = Pvitem.newBuilder();
            pvItem.putArgvs("seriesid", seriesDetailDto.getId() + "");
            pvItem.putArgvs("typeid", item.getTypeid() + "");
            pvItem.putArgvs("typename", item.getTitle());
            pvItem.putArgvs("cityid", cityId + "");
            pvItem.putArgvs("entrystyle", entryText.getTextType());
            pvItem.putArgvs("state", "1");
            pvItem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
            pvItem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));

            //1 线索平台，0 车商汇 int orderType = 1;
            String eid = "";
            Optional<SubscribePopContent.EntryEid> eidOp = subscribePopContent.getEntryEid().stream().filter(e -> sourceId == e.getSourceId()).findFirst();
            if (eidOp.isPresent()) {
                eid = 1 == pm ? eidOp.get().getPm1() : eidOp.get().getPm2();
            }
            Map<String, String> extObj = Maps.newHashMap();
            extObj.put("sourceid", sourceId + "");
            extObj.put("entrystyle", entryText.getTextType());
            extObj.put("biztype", biztype + "");
            extObj.put("keyActivityId", "1782579");
            extObj.put("keyCarAge", "1782579");
            String schema = "autohome://car/subscriptionpopup?customshowanimationtype=2&eid=%s&ext=%s&seriesid=%s&ordertype=%s&successjump=%s&biztype=%s&sourceid=%s&entrystyle=%s&brandid=%s";

             item.setLinkurl(String.format(schema, UrlUtil.encode(eid), UrlUtil.encode(JsonUtil.toString(extObj)), seriesDetailDto.getId(), 1, 1, biztype, sourceId, entryText.getTextType(), seriesDetailDto.getBrandId()))
                    .setSubtitle(entryText.getSubTitle())
                    .setTitle(entryText.getTitle()).setPvitem(pvItem);
        } catch (Exception e) {
            log.error("新车糖豆订阅异常", e);
        }
    }
    /**
     * 新版第二排糖豆
     */
    public CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist> beans_11085(
            String pluginversion,
            int brandId,
            String brandName,
            int seriesId,
            String seriesName,
            int cityId,
            int hotSpecId,
            int entryType,
            int seriesState,
            int noDefaultCityId,
            int pm,
            CompletableFuture<SeriesCityAskPriceDto> askPriceComponent,
            String seriesnewlineab,
            String bzlTest) {
        String json = StringUtils.isBlank(functionentrybeanstwo) ? functionentrybeanstwoDefault : functionentrybeanstwo;
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> beans = functionentrybeans(json);
        List<CompletableFuture> tasks = new ArrayList<>();

        if (pm == 3){
            //鸿蒙-11085节点增加二手车、提车价糖豆
            String scJ_json = StringUtils.isBlank(functionentrybeansone) ? functionentrybeansoneDefault : functionentrybeansone;
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> scJ_beans = functionentrybeans(scJ_json);
            scJ_beans.removeIf(bean -> !Arrays.asList("二手车").contains(bean.getTitle()));
            for (SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder : scJ_beans){
                switch (builder.getTitle()){
                    case "二手车" :
                        builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/car_spec_ershouche_240328.png");
                        beans.add(builder);
                        tasks.add(getUsedCarBean(builder, seriesId));
                        break;
                }
            }
        }

        //替换的新糖豆
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> replaceBeans = StringUtils.isBlank(functionentrybeanstwoSsc) ? new ArrayList<>() : functionentrybeans(functionentrybeanstwoSsc);
        if (replaceBeans != null && replaceBeans.size() > 0 && entryType == 1) {
            replaceBeans.forEach(x -> x.setIconurl(x.getIconurl().replace("_230913", "_green_230913")));
        }
        Map<String, SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> replaceMap = replaceBeans.stream().collect(Collectors.toMap(x -> x.getCode(), x -> x));

        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> operateList = getOperateListNew(seriesId, cityId);
        tasks.add(operateList);


        //把车辆价格，插入到第一个糖豆
        AtomicReference<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> clPriceBean = new AtomicReference<>(SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder());
        tasks.add(
                getPriceFunctionBean(askPriceComponent, brandId, hotSpecId, seriesName, entryType, seriesId, cityId).thenAccept(bean -> {
                    if (bean == null)
                        return;
                    clPriceBean.set(bean);
                })
        );
        // 保值率糖豆 https://doc.autohome.com.cn/docapi/page/share/share_zhDyAGUkCG
        boolean hasBzlCandy = CommonHelper.isTakeEffectVersion(pluginversion, "11.68.0") && bzlTest.equals("B");
        AtomicReference<KeepValueSeriesInfo> keepValueSeriesInfoRef = new AtomicReference<>();
        if (hasBzlCandy) {
            tasks.add(seriesKeepValueComponent.get(seriesId).thenAccept(keepValueSeriesInfoRef::set).exceptionally(e -> {
                log.warn("查询保值率错误");
                return null;
            }));
        }
        Map<String, String> replaceValue = Maps.newHashMap();
        replaceValue.put("{seriesid}", "" + seriesId);
        replaceValue.put("{brandid}", "" + brandId);
        replaceValue.put("{specid}", "" + hotSpecId);
        replaceValue.put("{seriesname}", UrlUtil.encode(seriesName));
        replaceValue.put("{brand}", UrlUtil.encode(String.format("{\"brandid\":\"%s\",\"bname\":\"%s\",\"seriesid\":\"%s\",\"sname\":\"%s\"}", brandId, brandName, seriesId, seriesName)));

        for (SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder : beans) {
            for (String k : replaceValue.keySet()) {
                builder.setLinkurl(builder.getLinkurl().replace(k, replaceValue.get(k)));
            }

            switch (builder.getCode()) {
                case "CYPZ":  //差异配置 & 车型亮点
                    tasks.add(getCYPZBean(builder, seriesId));
                    break;
                case "CLUB":  //论坛
                case "ASK":   ////对比试驾 > 上门试驾 > 问大家
                    if (CommonHelper.isTakeEffectVersion(pluginversion, "11.67.8") && "CLUB".equals(builder.getCode()) && Arrays.asList("B","C","D","E","F").contains(seriesnewlineab)) {
                        tasks.add(getKoubeiBean(builder, seriesId));
                        builder.setCode("KB").setTitle("口碑").setTypeid(1476509).setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/bean2024_kb_1024.png");
                    } else {
                        tasks.add(getClubBean(builder, seriesId, cityId, noDefaultCityId, pm));
                    }
                    break;
                case "CHAT":  //热聊
                    tasks.add(getReliaoBean(builder, seriesId, cityId));
                    break;
                case "BYJG":  //提车价 & 高价卖车
                    tasks.add(getBYJGBean(builder, seriesId, cityId, seriesState, replaceMap));
                    break;
                case "ZDJ":  //找底价 ： 待售、停售用 旧车估值 替换
                    tasks.add(getZhaodijiaBean(builder, seriesId, cityId, seriesState, replaceMap));
                    break;
                case "YH":  //优惠
                    getYouhuiBean(builder, seriesId);
                    break;
                case "BY":  //保养
                    tasks.add(getGaizhuangBean(builder, seriesId));
                    break;
                case "4SBY": //4s 保养
                    tasks.add(get4sbyBean(builder, seriesId, cityId, entryType));
                    break;
                case "YJHX2":
                    if(CommonHelper.isStopOrUnsold(seriesState)){
                        builder.setCode("YJHX");
                    }
                    break;
            }

        }
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {
            //如果有价格，插入到第一个
            if (StringUtils.isNotEmpty(clPriceBean.get().getTitle())) {
                beans.add(0, clPriceBean.get());
            }
            if (pm == 3){
                List<String> expose_sugars = new ArrayList<>(List.of("本地报价", "二手车", "亮点", "论坛", "常见问题"));
                if (CommonHelper.isTakeEffectVersion(pluginversion,"11.68.0")){
                    expose_sugars.add("热聊");
                    expose_sugars.add("4S保养");
                }
                beans.removeIf(bean -> !expose_sugars.contains(bean.getTitle()));
                //糖豆按顺序展示
                beans.sort((o1,o2) -> Integer.compare(getOrder(o1),getOrder(o2)));
            }

            if (CommonHelper.isTakeEffectVersion(pluginversion, "11.67.8") && Arrays.asList("D", "F").contains(seriesnewlineab)) {
                List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> opbeans = operateList.join();
                if (CollectionUtils.isNotEmpty(opbeans)) {
                    opbeans.forEach(b -> beans.add(b.toBuilder()));
                }
            }
                    beans.forEach(
                            builder -> {
                                //设置埋点信息
                                Pvitem.Builder pvitem = Pvitem.newBuilder();
                                pvitem.putArgvs("seriesid", seriesId + "");
                                pvitem.putArgvs("typeid", builder.getTypeid() + "");
                                pvitem.putArgvs("typename", builder.getTitle() + "");
                                pvitem.putArgvs("cityid", cityId + "");
                                pvitem.putArgvs("type","0");
                                pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
                                pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
                                builder.setPvitem(pvitem);
                                builder.setScale(1);
                            }
                    );
                    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list = beans.stream().map(item -> item.build()).collect(Collectors.toList());

                    //在售车去掉“以旧换新”、“改装”
                    if (!CommonHelper.isStopOrUnsold(seriesState)) {
                        list.removeIf(e -> StringUtils.equals("YJHX2", e.getCode()));
                        list.removeIf(e -> "BY".equals(e.getCode()) && (!"改装".equals(e.getTitle()) || StringUtils.isEmpty(e.getLinkurl())));
                    }
                    //如果没链接去掉
                    list.removeIf(e -> "4SBY".equals(e.getCode()) && StringUtils.isEmpty(e.getLinkurl()));
                    list.removeIf(e -> "ZDJ".equals(e.getCode()) && StringUtils.isEmpty(e.getLinkurl()));


                    if (hasBzlCandy && Objects.nonNull(keepValueSeriesInfoRef.get())) {
                        KeepValueSeriesInfo keepValueSeriesInfo = keepValueSeriesInfoRef.get();
                        if (CollectionUtils.isNotEmpty(keepValueSeriesInfo.getSerieskeeprate())) {
                            Optional<KeepValueSeriesInfo.KeepRateInfo> threeYearsHedgeRatio = keepValueSeriesInfo.getSerieskeeprate().stream().filter(rateInfo -> rateInfo.getYear() == 3).findFirst();
                            // 判断是否存在3年保值率
                            if (threeYearsHedgeRatio.isPresent()) {
                                addBzlCandy(list, seriesId, cityId);
                            }
                        }
                    }
                    return SeriesBaseInfoResponse.Result.Itemlist.newBuilder()
                            .setType(11085).setId(11085)
                            .setData(
                                    SeriesBaseInfoResponse.Result.Itemlist.Data.newBuilder()
                                            .addAllList(list)
                                            .setSortid(4)
                                            .setSeriesid(seriesId)
                                            .setEnergetype(entryType)
                            ).build();
                }
        );
    }

    /**
     * 经销商价
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> dealerPrice(
            int seriesId,
            int brandId,
            int hotSpecId,
            int cityId,
            String seriesName,
            CompletableFuture<SeriesCityAskPriceDto> askPriceComponent
    ) {
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
        builder.setPvitem(getPvItem(seriesId,22,cityId,"car_series_price_click","car_series_price_show"));
        builder.setTitle("经销商报价");
        builder.setSubtitle("暂无");
        builder.setTypeid(22);
        return askPriceComponent.thenApply(info -> {
            if (info == null || info.getMinPrice() <= 0)
                return builder.build();
            builder.setSubtitle(CommonHelper.getMoney(info.getMinPrice(), "起"));
            builder.setLinkurl("autohome://car/pricelibrary?brandid=" + brandId + "&seriesid=" + seriesId + "&specid=" + hotSpecId + "&seriesname=" + UrlUtil.encode(seriesName).replace("+", "%20") + "&tabindex=1&fromtype=1");
            return builder.build();
        });
    }


    /**
     * 经销商价
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ownerPrice(int seriesId, int hotSpecId,int cityId) {
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
        builder.setPvitem(getPvItem(seriesId, 11, cityId,"car_series_price_click", "car_series_price_show"));
        builder.setTitle("车主提车价");
        builder.setSubtitle("暂无");
        builder.setTypeid(11);
        if(computerRoom.anyDown()){
            return CompletableFuture.completedFuture(builder.build());
        }
        return seriesJiageComponent.get(seriesId).thenApply(info -> {
            if (info == null)
                return builder.build();
            builder.setSubtitle(String.format("%s人晒价",IntUtil.convertToWan(info.getTotal(),1)));
            builder.setLinkurl("autohome://flutter?url=" + UrlUtil.encode(String.format("flutter://car/ownerseriesprice?seriesid=%s&specid=%s&fromtype=1", seriesId, hotSpecId)));
            return builder.build();
        });
    }

    /**
     * 二手车价
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> sscPirce(int seriesId,int cityId) {
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
        builder.setPvitem(getPvItem(seriesId, 2,  cityId,"car_series_price_click", "car_series_price_show"));
        builder.setTitle("二手车");
        builder.setSubtitle("暂无报价");
        builder.setTypeid(2);
        return seriesUsedCarComponent.get(seriesId).thenApply(info -> {
            if (info == null)
                return builder.build();
            builder.setSubtitle(info.getSubTitle());
            builder.setTitle(info.getTitle());
            builder.setLinkurl(info.getJumpurl());
            return builder.build();
        }).exceptionally(e -> {
            log.error("二手车价获取失败",e);
            return null;
        });
    }


    /**
     * 养车价
     * 新零售对比试驾 > 上门试驾 > 养车成本
     *
     * @return
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> ycPrice(int seriesId, int cityId, int noDefaultCityId,int pm) {
        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
        Pvitem.Builder pvitem = getPvItem(seriesId, 3, cityId,"car_series_price_click", "car_series_price_show");
        builder.setPvitem(pvitem);
        builder.setTitle("养车价格");
        builder.setSubtitle("暂无");
        builder.setLinkurl("");
        builder.setTypeid(3);

        var nrTask = newRetailCitySeriesComponent.get(seriesId, cityId, noDefaultCityId);
        var infoTask = seriesDriveComponent.get(seriesId);
        var yangcheTask = seriesCityYangcheComponent.get(seriesId, cityId);

        return nrTask.thenApply(nr->{
            if (nr != null) {
                pvitem.putArgvs("typeid", "101");
                builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_smsj.png");
                builder.setTypeid(101);
                builder.setPvitem(pvitem);
                builder.setTitle("预约试驾");
                builder.setSubtitle("立即预约");
                builder.setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarAskpriceRN%2FSuperTestDrivePage%3Ftabid%3D1%26eid%3D6860394%26pvareaidlist%3D6860394%26seriesid%3D" + seriesId);
                return builder.build();
            }
            var info = infoTask.join();
            if (info != null && info.getHomeTestDriveCitys() != null && info.getHomeTestDriveCitys().size() > 0 && info.getHomeTestDriveCitys().contains(cityId)) {
                String eid = pm == 1 ? "3|1411002|1373|0|205168|305615" : "3|1412002|1373|0|205168|305615";
                pvitem.putArgvs("typeid", "102");
                builder.setPvitem(pvitem);
                builder.setTypeid(102);
                builder.setTitle("上门试驾");
                builder.setSubtitle("立即预约");
                builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_smsj.png");
                builder.setLinkurl(String.format("autohome://rninsidebrowser?url=%s",
                        UrlUtil.encode(String.format("rn://CarAskpriceRN/ApplyTestDrivePage?seriesid=%s&specid=&eid=%s&cityid=%s&cityname=%s&sourceid=3",
                                seriesId, UrlUtil.encode(eid), cityId, UrlUtil.encode(CityUtil.getCityName(cityId))))));
                return builder.build();
            }
            var yangche = yangcheTask.join();
            if (yangche != null) {
                builder.setSubtitle(yangche.getData().replace("元", ""));
                builder.setLinkurl(yangche.getAppHref());
                return builder.build();
            }
            return builder.build();
        });
    }


    Pvitem.Builder getPvItem(int seriesId, int typeId,int cityId, String clickEventId, String showEventId) {
        Pvitem.Builder pvitem = Pvitem.newBuilder();
        pvitem.putArgvs("seriesid", seriesId + "");
        pvitem.putArgvs("typeid", typeId + "");
        pvitem.putArgvs("cityid", cityId + "");
        pvitem.setClick(Pvitem.Click.newBuilder().setEventid(clickEventId));
        pvitem.setShow(Pvitem.Show.newBuilder().setEventid(showEventId));
        return pvitem;
    }

    public CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> getOperateListNew(int seriesId, int cityId) {
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionYouhuiButie = getOperatePositionButie(seriesId, cityId);
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> otaAndRights = getOtaAndRights(seriesId);

        return CompletableFuture.allOf(getOperatePositionYouhuiButie, otaAndRights).thenApply(x -> {
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> builders = new ArrayList<>();

            //横栏配置
            builders.addAll(getOperatePosition(seriesId));

            //优惠补贴
            SeriesBaseInfoResponse.Result.Itemlist.Data.List yhbt = getOperatePositionYouhuiButie.join();
            if(null != yhbt) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                        .setTitle("优惠补贴")
                        .setLinkurl(yhbt.getLinkurl())
                        .setTypeid(yhbt.getTypeid())
                        .setContent(yhbt.getContent())
                        .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/bean2024_btyh_1022.png")
                        .setCode("BTYH")
                        .setSubtitle("优惠补贴");

                builders.add(builder.build());
            }

            //购车权益，OTA升级，充电桩
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> otaList = otaAndRights.join();
            if (otaList != null && otaList.size() > 0) {
                Map<Integer, String> iconMap = Map.of(
                        9, "http://nfiles3.autohome.com.cn/zrjcpk10/bean2024_ota_1022.png",
                        10, "http://nfiles3.autohome.com.cn/zrjcpk10/bean2024_czqy_1022.png",
                        11, "http://nfiles3.autohome.com.cn/zrjcpk10/bean2024_cdz_1022.png");
                Map<Integer, String> codeMap = Map.of(
                        9, "OTA",
                        10, "CZQY",
                        11, "CDZ");
                otaList.forEach(e -> {
                    OperateListEnum operateListEnum = OperateListEnum.getByTypeid(e.getTypeid());
                    SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                            .setTitle(operateListEnum.getName())
                            .setLinkurl(e.getLinkurl())
                            .setIconurl(iconMap.get(e.getTypeid()))
                            .setContent(e.getContent())
                            .setCode(codeMap.get(e.getTypeid()))
                            .setSubtitle(operateListEnum.getName())
                            .setTypeid(operateListEnum.getTypeid())
                            .setSortid(operateListEnum.getOrder());

                    builders.add(builder.build());
                });
            }
            //车机真体验
            SeriesBaseInfoResponse.Result.Itemlist.Data.List cjty = addOperatePositionCheji(seriesId);
            if (null != cjty) {
                ListUtil.addIfNotNull(builders, cjty.toBuilder()
                        .setTitle("车机体验")
                        .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/bean2024_cjty_1022.png")
                        .setCode("CJTY")
                        .setSubtitle("车机体验")
                        .build());
            }

            return builders;
        }).exceptionally(e -> {
            log.error("getOperateListNew-error", e);
            return new ArrayList<>();
        });
    }


    /**
     * 糖豆下拉的更多选项
     *
     * @return
     */
    public CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> getOperateList(int brandId, int seriesId, String seriesName, int cityId, CompletableFuture<Boolean> autoShowing, String topArticleHotAb, String pluginversion, String articlexuangouab, int seriesState) {
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionYouhuiButie = getOperatePositionYouhuiButie(seriesId, cityId);
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> otaAndRights = getOtaAndRights(seriesId);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionSupertest = getOperatePositionSupertest(seriesId);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionAh100test = getOperatePositionAh100test(seriesId);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionAutoShow = getOperatePositionAutoShow(brandId, seriesId, autoShowing, seriesName);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> butie = CommonHelper.isTakeEffectVersion(pluginversion, "11.66.0") && Arrays.asList(20,30).contains(seriesState) ? getOperatePositionChangeCar(seriesId, cityId) : getOperatePositionButie(seriesId, cityId);
        CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> hotNewsAll= getHotNews(seriesId, cityId);
        CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> xuanGou = getOperatePositionXuanGou(pluginversion, seriesId, articlexuangouab);

        return CompletableFuture.allOf(getOperatePositionYouhuiButie, otaAndRights, getOperatePositionSupertest,
                getOperatePositionAh100test, getOperatePositionAutoShow, butie, hotNewsAll, xuanGou).thenApply(x -> {
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> builders = new ArrayList<>();
            //横栏配置
            builders.addAll(getOperatePosition(seriesId));
            //补贴
            ListUtil.addIfNotNull(builders, butie.join());
            //选购视频
            ListUtil.addIfNotNull(builders, xuanGou.join());
            // 热点资讯
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> hotNewsList = hotNewsAll.join();
            if (CollectionUtils.isNotEmpty(hotNewsList) && StringUtils.equals("1", topArticleHotAb)) {
                hotNewsList.stream().filter(Objects::nonNull)
                        .forEach(hotNews -> ListUtil.addIfNotNull(builders, hotNews));
            }
            //车主权益或者叫购车权益
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> otaList = otaAndRights.join();
            if (otaList!=null&&otaList.size()>0) {
                builders.addAll(otaList);
            }
            //降价潮
            ListUtil.addIfNotNull(builders,getJjc(seriesId));
            ListUtil.addIfNotNull(builders,getJjc2023(seriesId,cityId));
            //车机
            ListUtil.addIfNotNull(builders,addOperatePositionCheji(seriesId));
            //车展
            ListUtil.addIfNotNull(builders, getOperatePositionAutoShow.join());
            //新能源计划
            addOperatePositionNewenergyplan(seriesId, builders);
            //厂商+地方优惠政策补贴
            ListUtil.addIfNotNull(builders, getOperatePositionYouhuiButie.join());
            //超级测试
            ListUtil.addIfNotNull(builders, getOperatePositionSupertest.join());
            //冬测
            addOperatePositionWinterTest(seriesId, builders);
            //之家评测：AH100
            ListUtil.addIfNotNull(builders, getOperatePositionAh100test.join());
//            //车机
//            addOperatePositionCheji(seriesId, builders);
            return builders;
        }).exceptionally(e->{
            log.error("getOperatePosition-error",e);
            return new ArrayList<>();
        });
    }

    //选购视频
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionXuanGou(String pluginversion, int seriesId, String articlexuangouab) {
        if (!CommonHelper.isTakeEffectVersion(pluginversion, "11.67.5")) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            if (StringUtils.isNotEmpty(articlexuangouab)) {
                String module_id = "";
                Map<String, String> seriesModuleIdMap = new HashMap<>();
                try {
                    //18_331*629_331*5769_331
                    String[] moduleids = articlexuangouab.split("\\*");
                    if (moduleids != null && moduleids.length > 0) {
                        for (int i = 0; i < moduleids.length; i++) {
                            String[] str = moduleids[i].split("_");
                            if (str != null && str.length == 2) {
                                seriesModuleIdMap.put(str[0], str[1]);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("getOperatePositionXuanGou-xuangouabjson-error", e);
                }
                if (seriesModuleIdMap != null && seriesModuleIdMap.containsKey(String.valueOf(seriesId))) {
                    module_id = seriesModuleIdMap.get(String.valueOf(seriesId));
                }
                if (StringUtils.isNotEmpty(module_id)) {
                    XuanGouVideoResult result = xuanGouVideoApiClient.getXuanGouVideoResult(module_id).join();
                    if (result != null && result.getResult() != null && ListUtil.isNotEmpty(result.getResult().getList())) {
                        return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                                .setLinkurl(String.format("autohome://insidebrowserwk?loadtype=1&progressTimeout=0&bounces=false&url=%s", UrlUtil.encode("https://mf.autohome.com.cn/v3/7052")))
                                .setTypeid(201)
                                .setContent("一站式购车攻略，买车不犯难")
                                .setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/xuangouzhinan_20241009.png")
                                .setBgurl("")
                                .build();
                    }
                }
            }
            return null;
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            return null;
        });
    }

    //车展
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionAutoShow(int brandId, int seriesId, CompletableFuture<Boolean> autoShowing, String seriesName) {
        //2024北京车展没有运营位配置，返回null
        if (true || !autoShowConfig.IsBetweenDate()) {
            return CompletableFuture.completedFuture(null);
        }
        return autoShowing.thenApply(showing->{
            if(!showing)
                return null;
            return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setLinkurl(CommonHelper.getInsideBrowerSchemeWK("https://zt.autohome.com.cn/topic/chezhan/" + autoShowConfig.getAutoshowid() + "/index.html/#/home/" + brandId + "-" + seriesId + "?source=series"))
                    .setTypeid(111)
                    .setContent("广州车展现场直击" + seriesName)
                    .setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/gzcz2023_op_1101.png")
                    .setBgurl("https://nfiles3.autohome.com.cn/zrjcpk10/gzcz2023_op_bg_1109.png")
                    .build();
        }).exceptionally(e->{
            log.error("车展糖豆异常",e);
            return null;
        });
    }


    //补贴
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionButie(int seriesId, int cityId) {
        return specCitySubsidyComponent.getSeriesCityData(seriesId,cityId).thenCombine(seriesCityCpsComponent.get(seriesId,cityId),(subsidy,cps)->{
            if(subsidy==null||cps==null){
                return null;
            }
            int price = subsidy.getPrice() + cps.getPrice().intValue();

            return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setLinkurl(CommonHelper.getInsideBrowerSchemeWK("https://dealer.m.autohome.com.cn/jiajiago/pinganbrandlist?ext_popup=1&seriesId="+seriesId+"&kv=JEGuyfWCFD&pvareaid=6862112"))
                    .setTypeid(113)
                    .setContent("领取全网最高补贴"+price+"元")
                    .setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/butie_s_20240430.png")
                    .setBgurl("https://nfiles3.autohome.com.cn/zrjcpk10/braner_2024043019.png")
                    .build();
        }).exceptionally(e->{
            log.error("补贴横栏异常",e);
            return null;
        });
    }

    //以旧换新替换 补贴
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionChangeCar(int seriesId, int cityId) {
        return seriesSpecComponent.get(List.of(seriesId)).thenApply(list -> {
            if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(list.get(0).getItems())) {
                return null;
            }
            Optional<SeriesSpecDto.Item> item = list.get(0).getItems().stream().filter(e -> Arrays.asList(20,30).contains(e.getState())).min(Comparator.comparing(SeriesSpecDto.Item::getMinPrice));
            if (item.isPresent()) {
                int specId = item.get().getId();
                String linkUrl = CommonHelper.getInsideBrowerSchemeWK("https://fs.autohome.com.cn/afu_spa/autoConsumptionSubsidy?seriesid=" + seriesId
                        + "&specid=" + specId + "&cityid=" + cityId + "&eid=3|1411002|572|27564|211919|306974&pvareaid=6864505");

                return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                        .setLinkurl(linkUrl)
                        .setTypeid(113)
                        .setContent("汽车消费补贴至高20000元")
                        .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/butie_s_20240430.png")
                        .setBgurl("http://nfiles3.autohome.com.cn/zrjcpk10/braner_2024043019.png")
                        .build();
            }
            return null;
        }).exceptionally(e -> {
            log.error("补贴横栏异常", e);
            return null;
        });
    }

    /**
     * 热点资讯
     * @param seriesId
     * @param cityId
     * @return
     */
    CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> getHotNews(int seriesId, int cityId) {
        return seriesCityHotNewsComponent.getAsync(seriesId, cityId).thenApplyAsync(hotNewsDtoList -> {
            if (CollectionUtils.isEmpty(hotNewsDtoList)) {
                return null;
            }
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> resultList = new ArrayList<>();
            hotNewsDtoList.forEach(hotNews ->
                    resultList.add(SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                            .setLinkurl(CommonHelper.getInsideBrowerSchemeWK(hotNews.getLinkUrl()))
                            .setTypeid(8182)
                            .setContent(hotNews.getTitle())
                            .setIconurl(hotNews.getIcon())
                            .setSortid(Integer.parseInt(hotNews.getSort()))
                            .setBgurl("http://nfiles3.autohome.com.cn/zrjcpk10/hotnews_bg_240722.png")
                            .build()));
            return resultList;
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("补贴横栏异常", e);
            return null;
        });
    }

    void addOperatePositionNewenergyplan(int seriesId, List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> builders) {
        //新能源计划
        try {
            if (newenergyplanConfig != null && newenergyplanConfig.IsBetweenDate() && newenergyplanConfig.getAllow_brandlist().contains(seriesId)) {
                builders.add(
                        SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                                .setLinkurl(CommonHelper.getInsideBrowerSchemeWK(newenergyplanConfig.getLinkurl()))
                                .setTypeid(112)
                                .setContent(newenergyplanConfig.getContent())
                                .setIconurl(newenergyplanConfig.getIconurl())
                                .setBgurl(newenergyplanConfig.getBgurl() == null ? "" : newenergyplanConfig.getBgurl())
                                .build()
                );
            }
        }catch (Exception e){
            log.error("新能源计划糖豆异常",e);
        }
    }

    SeriesBaseInfoResponse.Result.Itemlist.Data.List getJjc(int seriesId) {
        if (jjc2024_config == null || !jjc2024_config.isOpen(seriesId))
            return null;
        return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                .setContent(jjc2024_config.getTitle())
                .setIconurl(jjc2024_config.getIcon())
                .setLinkurl(jjc2024_config.getLinkurl())
                .setTypeid(12)
                .build();
    }
    SeriesBaseInfoResponse.Result.Itemlist.Data.List getJjc2023(int seriesId,int cityId) {
        try {
            if (jjConfig != null && jjConfig.getIsopen() == 1 && !jjConfig.getSpeclist().isEmpty()) {
                String provinceName = CityUtil.getProvinceName(cityId);
                List<JJCSpecConfig.SpecList> specList = new ArrayList<>();
                Map<String, List<JJCSpecConfig.SpecList>> map = jjConfig.getSpeclist().stream()
                        .filter(i -> i.getSeriesid() == seriesId)
                        .collect(Collectors.groupingBy(i -> {
                            if (i.getPname().contains(provinceName)) {
                                return "local";
                            } else if (i.getPname().contains("湖北")) {
                                return "hubei";
                            } else if (i.getPname().contains("全国")) {
                                return "all";
                            } else {
                                return "other";
                            }
                        }));
                if (map.containsKey("local")) {
                    specList.addAll(map.get("local"));
                } else if (map.containsKey("hubei")) {
                    specList.addAll(map.get("hubei"));
                } else if (map.containsKey("all")) {
                    specList.addAll(map.get("all"));
                }
                if (!specList.isEmpty()) {
                    Optional<JJCSpecConfig.SpecList> max = specList.stream().max(Comparator.comparingInt(JJCSpecConfig.SpecList::getButiePrice));
                    if (max.isPresent() && StringUtils.isNotEmpty(max.get().getEndtime())) {
                        if (DateTime.parse(max.get().getEndtime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).isAfter(DateTime.now())) {
                            return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                                    .setContent(String.format("%s 最高优惠%s元", max.get().getSeriesname(), max.get().getButiePrice()))
                                    .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/jjc_seriesbase_230311.png")
                                    .setLinkurl(String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode(String.format("rn://CarNewRankRN/Discount?seriesid=%s&cityid=%s", seriesId, cityId))))
                                    .setTypeid(3)
                                    .build();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("降价潮配置异常", e);
        }
        return null;
    }
    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePosition(int seriesId){
        List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> result = new ArrayList<>();
        try {
            List<OperatePositionConfig> operatePositionConfigs = JsonUtil.toObjectList(series_operateposition, OperatePositionConfig.class);
            if (operatePositionConfigs!=null&&operatePositionConfigs.size()>0) {
                OperatePositionConfig operatePositionConfig = operatePositionConfigs.stream().filter(x -> x.getSeriesid() == seriesId).findFirst().orElse(null);
                if (operatePositionConfig!=null&&operatePositionConfig.getList()!=null&&operatePositionConfig.getList().size()>0) {
                    operatePositionConfig.getList().forEach(x->{
                        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                        builder.setContent(x.getContent());
                        builder.setIconurl(x.getIconurl());
                        if(StringUtils.isNotEmpty(x.getBgurl())){
                            builder.setBgurl(x.getBgurl());
                        }
                        builder.setLinkurl(x.getLinkurl());
                        builder.setTypeid(x.getTypeid());
                        result.add(builder.build());
                    });
                }
            }
        } catch (Exception e) {
            log.error("车系横栏配置异常", e);
        }
        return result;
    }

    String getOperateCandyBeans(int seriesId){
       String linkurl="";
        try {
            SeriesCandyBeansConfig beansConfigs = JsonUtil.toObject(series_candy_beans, SeriesCandyBeansConfig.class);
            if (beansConfigs!=null&&beansConfigs.getIsopen()==1) {
                SeriesCandyBeansConfig.ListDTO listDTO = beansConfigs.getList().stream().filter(i -> i.getSeriesid() == seriesId).findFirst().orElse(null);
                if (listDTO!=null) {
                    linkurl = listDTO.getLinkurl();
                }
            }
        } catch (Exception e) {
            log.error("车系横栏配置异常", e);
        }
        return linkurl;
    }
    void addOperatePositionWinterTest(int seriesId, List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> builders) {
        //冬测
        try {
            if (winter_test_open_control == 1 && rank_winter_seriesinfos.size() > 0) {
                WinterRankSeries info = rank_winter_seriesinfos.stream().filter(x -> x.getSeriesid() == seriesId).findFirst().orElse(null);
                if (info != null) {
                    SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                    builder.setContent(String.format("续航%skm /电耗%skWh/100km", info.getXhvalue(), info.getKwhvalue()));
                    builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/oplist/wintertest.png.webp");
                    builder.setLinkurl("autohome://insidebrowserwk?loadtype=1&url=" + UrlUtil.encode(info.getXhurl() + "?pvareaid=6853828"));
                    builder.setTypeid(6);
                    builders.add(builder.build());
                }
            }
        }catch (Exception e){
            log.error("冬测 糖豆异常",e);
        }
    }

    /**
     * 车机
     */
    SeriesBaseInfoResponse.Result.Itemlist.Data.List addOperatePositionCheji(int seriesId) {
        try {
            if (cheji_series_data_list.size() > 0) {
                ChejiSeriesDataConfig config = cheji_series_data_list.stream().filter(x -> x.getSeriesid() == seriesId).findFirst().orElse(null);
                if (config != null) {
                    SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                    String scheme = "autohome://insidebrowserwk?loadtype=1&url=";
                    if (StringUtils.isNotEmpty(config.getBrowserparam())) {
                        scheme = String.format("autohome://insidebrowserwk?loadtype=1&%s&url=", config.getBrowserparam());
                    }
                    scheme = scheme + UrlUtil.encode(config.getLinkurl());
                    builder.setContent(config.getContent());
                    builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/chejizhentiyan_icon_20220718.png");
                    builder.setLinkurl(scheme);
                    builder.setTypeid(8);
                    return builder.build();
                }
            }
            return null;
        }catch (Exception e){
            log.error("车机糖豆异常",e);
        }
        return null;
    }

    /**
     * 厂商+地方优惠政策补贴
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionYouhuiButie(int seriesId, int cityId) {
        return seriesCityDealerComponent.get(seriesId, cityId).thenApply(info -> {
            if (info != null && info.getPricePolicy() != null) {
                String newPreferential = info.getPricePolicy().getNewCar() > 0 ? "新车至高优惠" + info.getPricePolicy().getNewCar() + "元" : "";
                String replacePreferential = info.getPricePolicy().getReplaceCar() > 0 ? "置换至高优惠" + info.getPricePolicy().getReplaceCar() + "元" : "";
                String content = newPreferential + (StringUtils.isNoneBlank(newPreferential, replacePreferential) ? "/" : "") + replacePreferential;
                return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                        .setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarAskpriceRN%2FDisplaceDiscount%3Fseriesid%3D" + seriesId)
                        .setTypeid(12)
                        .setContent(content)
                        .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/yhzc_230804.webp")
                        .build();
            }
            return null;
        }).exceptionally(e -> {
            log.error("厂商+地方优惠政策补贴", e);
            return null;
        });
    }

    /**
     * 之家评测
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionAh100test(int seriesId) {
        if (car_ah100test_config != null && car_ah100test_config.getIsioen() == 1 && car_ah100test_config.getSeriesids()!=null && car_ah100test_config.getSeriesids().contains(seriesId)) {
            return seriesCmsTestEvalComponent.get(seriesId).thenApply(info -> {
                if (info == null)
                    return null;
                String content = "";
                if (info.getJiasu0100() != null) {
                    content = "0-100km/h加速：" + info.getJiasu0100().getData() + "s ";
                }
                if (info.getMiludata() != null) {
                    content += "麋鹿：" + info.getMiludata().getData() + "km/h";
                }
                return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                        .setTypeid(7)
                        .setContent(content)
                        .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/oplist/autotest.png.webp")
                        .setLinkurl("autohome://rninsidebrowser?url=" + UrlUtil.encode("rn://CarSeriesTestRN/zhijiatest?seriesid=" + seriesId + "&tabid=1&subid=11&panValid=0"))
                        .build();
            }).exceptionally(e -> {
                log.error("之家评测", e);
                return null;
            });
        }
        return CompletableFuture.completedFuture(null);

    }

    /**
     * 超级测试
     */
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List> getOperatePositionSupertest(int seriesId) {
        //超级测试
        if (supertest_isopen_config != 1) {
            return CompletableFuture.completedFuture(null);
        }
        return seriesBrightpointComponent.get(seriesId).thenApply(info -> {
            SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
            builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/supertest_info_220830.png");
            if (info != null &&info.getBrightpoint()!=null&& StringUtils.isNoneBlank(info.getBrightpoint().getTitle())) {
                builder.setTypeid(3);
                builder.setLinkurl(String.format("autohome://insidebrowserwk?loadtype=1&disable_back=1&url={}", UrlUtil.encode(info.getBrightpoint().getUrl() + "?pvareaid=6859879")));
                builder.setContent(info.getBrightpoint().getTitle());
            } else {
                List<SuperTestConfig> superTestConfigs = superTestConfig.get();
                if (superTestConfigs.size() == 0) {
                    return null;
                }
                SuperTestConfig config = superTestConfigs.stream().filter(x -> x.getSeriesid() == seriesId).findFirst().orElse(null);
                if (config == null) {
                    return null;
                }
                builder.setLinkurl("autohome://insidebrowserwk?loadtype=1&url=" + UrlUtil.encode(config.getLinkurl()));
                builder.setTypeid(config.getTypeid());
                if (config.getTypeid() == 3) {
                    builder.setContent(config.getTitle());
                } else {
                    builder.setContent("0-100km/h");
                }

                builder.setTypeid(4);
                builder.setLinkurl("");
            }
            return SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                    .setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarAskpriceRN%2FDisplaceDiscount%3Fseriesid%3D" + seriesId)
                    .setTypeid(12)
                    .setContent("acctext")
                    .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/yhzc_230804.webp").build();
        }).exceptionally(e -> {
            log.error("超级测试", e);
            return null;
        });
    }

    /**
     * ota 升级和车主权益
     */
    CompletableFuture<List<SeriesBaseInfoResponse.Result.Itemlist.Data.List>> getOtaAndRights(int seriesId) {
        return seriesOtaOwnerComponent.get(seriesId).thenApply(info -> {
            List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> result = new ArrayList<>();
            if (info == null) {
                return result;
            }
            if (info.getOwnerFlag() == 1) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                int totalRightCount = info.getAllOwnerCount();
                String content = "置换权益/免费权益等" + totalRightCount + "项";
                String url = "http://fs.autohome.com.cn/afu_spa/h5-equity?seriesId=" + seriesId;
                builder.setContent(content);
                builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/ota_czqy_240428.png");
                builder.setLinkurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(url));
                builder.setTypeid(10);
                result.add(builder.build());
            }
            if (info.getOtaFlag() == 1) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                String content = "最近推送时间 " + DateUtil.format(info.getOtaPushTime(), "yyyy年MM月dd日");
                String url = "http://fs.autohome.com.cn/afu_spa/h5-ota?seriesId=" + seriesId;
                builder.setContent(content);
                builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/ota_sj_230705.png");
                builder.setLinkurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(url));
                builder.setTypeid(9);
                result.add(builder.build());
            }
            if (info.getChargeStationFlag() == 1) {
                SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
                String payTypename = info.getChargeStationPayName();
//                int pagetype = info.getChargeStationPayType();
//                if (pagetype == 0) {
//                    payTypename = "全系免费";
//                } else if (pagetype == 1) {
//                    payTypename = "全系自费";
//                }

                String content = payTypename + "· " + info.getChargeStationName();
                String url = "https://fs.autohome.com.cn/afu_spa/h5-equity/chargestate?seriesId=" + seriesId;
                builder.setContent(content);
                builder.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cdz_230801.png");
                builder.setLinkurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(url));
                builder.setTypeid(11);
                result.add(builder.build());
            }
            return result;
        }).exceptionally(e -> {
            log.error("ota 升级和车主权益", e);
            return null;
        });
    }


    /**
     * 4s 保养
     */
    CompletableFuture<Void> get4sbyBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId, int cityId, int isNewEnergy) {
        return seriesCityDealerComponent.get(seriesId, cityId).thenAccept(info -> {
            if (info != null && info.getBy4s() != null && info.getBy4s().isHavedealers() && StringUtils.isNotBlank(info.getBy4s().getJumpdealerlisturl())) {
                builder.setLinkurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(info.getBy4s().getJumpdealerlisturl() + "&pvareaid=6860515"));
            }
        }).exceptionally(e -> {
            log.error(String.format("获取4s保养糖豆异常 seriesId:%s cityId:%s", seriesId, cityId), e);
            return null;
        });
    }
    /**
     * 差异配置
     */
    CompletableFuture<Void> getCYPZBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId) {
        return seriesBrightpointComponent.get(seriesId).thenAccept(data -> {
            if (data == null) {
                if (StringUtils.isEmpty(builder.getLinkurl())) {
                    builder.setCode("CXLD");
                    builder.setTitle("亮点")
                            .setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_ld.png");
                }
                return;
            }
            if (data.getBrightpoint() != null && StringUtils.isNotEmpty(data.getBrightpoint().getUrl())) {
                builder.setCode("CXLD")
                        .setTitle("亮点")
                        .setSubtitle("")
                        .setTypeid(3)
                        .setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_ld.png")
                        .setLinkurl(String.format("autohome://insidebrowserwk?loadtype=1&disable_back=1&url=%s", UrlUtil.encode(data.getBrightpoint().getUrl() + "?pvareaid=6860365")));
            } else if (data.getCarSeriesHighlight() != null && CollectionUtils.isNotEmpty(data.getCarSeriesHighlight().getMinPriceSpecIds()) && data.getCarSeriesHighlight().getMinPriceSpecIds().size() >= 2) {
                builder.setTitle("车型差异")
                        .setLinkurl(String.format("autohome://car/summaryconfigdif?seriesid=%s&specids=%s", seriesId, UrlUtil.encode(data.getCarSeriesHighlight().getMinPriceSpecIds().get(0) + "," + data.getCarSeriesHighlight().getMinPriceSpecIds().get(1))));
            } else {
                builder.setCode("CXLD");
                builder.setTitle("亮点")
                        .setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_ld.png");
                if (data.getCarSeriesHighlight() != null && data.getCarSeriesHighlight().getHighlightCount() > 0) {
                    builder.setCode("CXLD")
                            .setSubtitle("")
                            .setTypeid(3)
                            .setLinkurl(String.format("%s%s", "autohome://rninsidebrowser?url=",
                                    UrlUtil.encode(String.format(
                                            "rn://CarNewRankRN/LightConfigPage?specid=%s&seriesid=%s",
                                            data.getCarSeriesHighlight().getSpecId(), seriesId))));
                }
            }
        }).exceptionally(e -> {
            log.error(String.format("获取差异配置糖豆异常 seriesId:%s", seriesId), e);
            return null;
        });
    }
    CompletableFuture<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> getPriceFunctionBean(CompletableFuture<SeriesCityAskPriceDto> askPriceComponent, int brandId, int hostSpecId, String seriesName, int isNewEnergy, int seriesId, int cityId) {
        return askPriceComponent.thenApply(info -> {
            if (info == null)
                return null;
            String linkUrl = "autohome://car/pricelibrary?brandid=" + brandId + "&seriesid=" + seriesId + "&specid=" + hostSpecId + "&seriesname=" + UrlUtil.encode(seriesName).replace("+", "%20") + "&tabindex=1&fromtype=1&tabtype=1&sourceid=2&tabpricename=" + UrlUtil.encode("本地报价");
            SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder bean = SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder();
            bean.setCode("CLJG");
            bean.setTitle("车辆价格");
            bean.setTypeid(1022001);
            bean.setSubtitle("");
            bean.setLinkurl(linkUrl);
            bean.setTitle("本地报价");
            bean.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_cljg.png");
            Pvitem.Builder pvitem = Pvitem.newBuilder();
            pvitem.putArgvs("seriesid", seriesId + "");
            pvitem.putArgvs("typeid", bean.getTypeid() + "");
            pvitem.putArgvs("typename", bean.getTitle());
            pvitem.putArgvs("cityid", cityId + "");
            pvitem.setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"));
            pvitem.setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"));
            bean.setPvitem(pvitem);
            return bean;
        }).exceptionally(e -> {
            log.error("车辆价格糖豆异常",e);
            return null;
        });
    }


    /**
     * 改装糖豆
     */
    CompletableFuture<Void> getGaizhuangBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId) {
        return seriesGaizhuangComponent.get(seriesId).thenAccept(info -> {
            if (info == null)
                return;
            builder.setTitle(info.getTitle());
            if (StringUtils.isBlank(builder.getSubtitle())) {
                builder.setSubtitle(info.getSubTitle());
            }
            if (StringUtils.isBlank(builder.getLinkurl())) {
                builder.setLinkurl(info.getAppHref());
            }
        }).exceptionally(e -> {
            log.error(String.format("获取改装糖豆异常 seriesId:%s", seriesId), e);
            return null;
        });
    }

    /**
     * 优惠糖豆
     */
    void getYouhuiBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId) {
        String newurl = "autohome://flutter?url=flutter://assistant/cardiscount?seriesid=" + seriesId + "&fromtype=1";
        builder.setLinkurl(UrlUtil.getFlutterUrl(newurl));
        builder.setTitle("优惠");
        builder.setSubtitle("最新报价走势");
    }


    /**
     * 找底价糖豆
     * 如果是待售或停售，则用 旧车估值 替换
     */
    CompletableFuture<Void> getZhaodijiaBean(
            SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder,
            int seriesId, int cityId, int seriesState,
            Map<String, SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> replaceMap) {
        if (CommonHelper.isStopOrUnsold(seriesState) && replaceMap.containsKey("JCGZ")) {
            builder.mergeFrom(replaceMap.get("JCGZ").build());
            return CompletableFuture.completedFuture(null);
        } else {
            return seriesCityZhaodijiaComponent.get(seriesId, cityId).thenAccept(info -> {
                if (info == null)
                    return;
                builder.setTitle(info.getTitle());
                if (StringUtils.isBlank(builder.getSubtitle())) {
                    builder.setSubtitle(info.getSubTitle());
                }
                if (StringUtils.isBlank(builder.getLinkurl())) {
                    builder.setLinkurl(info.getTargetUrl());
                }
            }).exceptionally(e->{
                log.error("找底价糖豆异常",e);
                return null;
            });
        }
    }

    /**
     * 论坛&问答糖豆
     */
    CompletableFuture<Void> getClubBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId, int cityId, int noDefaultCityId, int pm) {
        if (StringUtils.isNoneBlank(builder.getSubtitle(), builder.getLinkurl())) {
            return CompletableFuture.completedFuture(null);
        }
        return seriesClubComponent.get(seriesId).thenAccept(info -> {
            if (info != null) {
                switch (builder.getCode()) {
                    case "CLUB":
                        if (StringUtils.isBlank(builder.getSubtitle())) {
                            builder.setSubtitle(info.getSubTitle());
                        }
                        if (StringUtils.isBlank(builder.getLinkurl())) {
                            builder.setLinkurl(info.getJumpUrl());
                        }
                        break;
                    case "ASK":
                        if (StringUtils.isBlank(builder.getSubtitle())) {
                            builder.setSubtitle(info.getQaSubTitle());
                        }
                        if (StringUtils.isBlank(builder.getLinkurl())) {
                            builder.setLinkurl(info.getQaJumpUrl());
                        }
                        break;
                }
            }
        }).thenComposeAsync(x -> { //问大家按钮用试驾替换：对比试驾 > 上门试驾 > 问大家，TODO 这里可以提前启动任务
            switch (builder.getCode()) {
                case "ASK":
                    return duiBiShijiaBean(builder, seriesId, cityId, noDefaultCityId, pm);
            }
            return CompletableFuture.completedFuture(null);
        }).exceptionally(e -> {
            log.error(String.format("获取论坛糖豆异常 seriesId:%s", seriesId), e);
            return null;
        });
    }

    CompletableFuture<Void> getClubBeanOnly(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId) {
        if (StringUtils.isNoneBlank(builder.getSubtitle(), builder.getLinkurl())) {
            return CompletableFuture.completedFuture(null);
        }
        return seriesClubComponent.get(seriesId).thenAccept(info -> {
            if (info != null) {
                if (StringUtils.isBlank(builder.getSubtitle())) {
                    builder.setSubtitle(StringUtils.isBlank(info.getSubTitle()) ? "暂无" : info.getSubTitle());
                }
                if (StringUtils.isBlank(builder.getLinkurl())) {
                    builder.setLinkurl(info.getJumpUrl());
                    if (StringUtils.isBlank(info.getJumpUrl())) {
                        builder.setSubtitle("暂无");
                    }
                }
                if (StringUtils.isNotEmpty(info.getSubTitle())) {
                    builder.setSubtitlehighlight(getNumFromSubtitle(info.getSubTitle()));
                }
            } else {
                builder.setSubtitle("暂无");
            }

        }).exceptionally(e -> {
            log.error(String.format("获取论坛糖豆异常 seriesId:%s", seriesId), e);
            return null;
        });
    }

    private String getNumFromSubtitle(String subtitle) {
        try {
            if (subtitle.contains("人")) {
                String num = subtitle.replaceAll("人分享","").replaceAll("人讨论","").replaceAll("人关注","");
                return num;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 对比试驾
     */
    CompletableFuture duiBiShijiaBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId, int cityId, int noDefaultCityId, int pm) {
        //不是默认城市，并且开启了对比试驾的城市，显示对比试驾
        CompletableFuture<CitySeriesListDto.Series> nrFuture = newRetailCitySeriesComponent.get(seriesId, cityId, noDefaultCityId);
        CompletableFuture<SeriesDriveDto> infoFuture = seriesDriveComponent.get(seriesId);
        CompletableFuture<CitySeriesListDto.Series> bFuture = newRetailCitySeriesComponent.getB(seriesId, cityId, noDefaultCityId);
        return CompletableFuture.allOf(nrFuture, infoFuture, bFuture).thenApply(v -> {
            CitySeriesListDto.Series nr = nrFuture.join();
            CitySeriesListDto.Series nrb = bFuture.join();
            SeriesDriveDto info = infoFuture.join();
            if (nr != null) {
                builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_smsj.png");
                builder.setTypeid(101);
                builder.setTitle("预约试驾");
                builder.setSubtitle("立即预约");
                builder.setLinkurl("autohome://rninsidebrowser?url=rn%3A%2F%2FCarAskpriceRN%2FSuperTestDrivePage%3Ftabid%3D1%26eid%3D6860394%26pvareaidlist%3D6860394%26seriesid%3D" + seriesId);
            } else if (nrb == null && info != null && info.getHomeTestDriveCitys() != null && info.getHomeTestDriveCitys().size() > 0 && info.getHomeTestDriveCitys().contains(cityId)) {
                String eid = pm == 1 ? "3|1411002|1373|0|205168|305615" : "3|1412002|1373|0|205168|305615";
                builder.setTypeid(102);
                builder.setTitle("上门试驾");
                builder.setIconurl("https://nfiles3.autohome.com.cn/zrjcpk10/2024_01_23_smsj.png");
                builder.setLinkurl(String.format("autohome://rninsidebrowser?url=%s",
                        UrlUtil.encode(String.format("rn://CarAskpriceRN/ApplyTestDrivePage?seriesid=%s&specid=&eid=%s&cityid=%s&cityname=%s&sourceid=3",
                                seriesId, UrlUtil.encode(eid), cityId, UrlUtil.encode(CityUtil.getCityName(cityId))))));
            }
            return null;
        }).exceptionally(e -> {
            log.error("对比试驾糖豆异常", e);
            return null;
        });
    }

    /**
     * 热聊糖豆
     */
    CompletableFuture<Void> getReliaoBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId, int cityId) {
        return seriesCityImComponent.get(seriesId, cityId).thenAccept(info -> {
            if (info != null && info.getMemberCount() > 0) {
                if (StringUtils.isBlank(builder.getSubtitle())) {
                    String chatNum = "";
                    int nums = info.getMemberCount();
                    if (nums > 10000) {
                        chatNum = String.format("%.1f", nums / 10000.0) + "W";
                    } else {
                        chatNum = nums + "";
                    }
                    String subtile = chatNum + "人讨论";
                    builder.setSubtitle(subtile);
                }
                if (StringUtils.isBlank(builder.getLinkurl())) {
                    builder.setLinkurl(String.format("autohome://carfriend/flashchatconversation/group?targetId=%s&targetType=%s&ryroomid=%s", info.getTargetId(), info.getTargetType(), info.getRyRoomId()));
                }
            }
        }).exceptionally(e -> {
            log.error(String.format("获取热聊糖豆异常 seriesId:%s，cityId:%s ", seriesId, cityId), e);
            return null;
        });
    }

    /**
     * 获取口碑糖豆
     * TODO 机房逻辑
     */
    CompletableFuture<Void> getKoubeiBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId) {
        return seriesKouBeiComponent.get(seriesId).thenAccept(kouBeiInfo -> {
            builder.setSubtitle("车主真实口碑");
            if (kouBeiInfo != null) {
                if(kouBeiInfo.getScoreInfo()!=null && kouBeiInfo.getScoreInfo().getEvalCount()>0){
                    String koubNum = IntUtil.convertToWan(kouBeiInfo.getScoreInfo().getEvalCount());
                    builder.setSubtitle(koubNum + "人评价");
                    builder.setSubtitlehighlight(koubNum);
                }
                if(kouBeiInfo.getBean()!=null && StringUtils.isBlank(builder.getLinkurl())){
//                    builder.setSubtitle(kouBeiInfo.getBean().getSubTitle());
                    if (StringUtils.isBlank(builder.getLinkurl())) {
                        builder.setLinkurl(kouBeiInfo.getBean().getAppScheme());
                        if (StringUtils.isBlank(kouBeiInfo.getBean().getAppScheme())) {
                            builder.setSubtitle("暂无");
                            builder.setSubtitlehighlight("");
                        }
                    }
                }
            }
        }).exceptionally(e -> {
            log.error(String.format("获取口碑糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    /**
     * 获取提车价糖豆
     * TODO 机房逻辑
     */
    CompletableFuture<Void> getTichejiaBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId, int hotSpecId) {
        return seriesJiageComponent.get(seriesId).thenAccept(info -> {
            builder.setSubtitle("暂无");
            if (info != null) {
                if (StringUtils.isBlank(builder.getSubtitle())) {
                    String subTitle = StringUtils.isBlank(info.getSubTitle()) ? info.getOwnerPrice() : info.getSubTitle();
                    builder.setSubtitle(subTitle);
                    builder.setSubtitlehighlight(subTitle.replace("降价", "").replace("价格", ""));
                }
                String wanNum = IntUtil.convertToWan(info.getTotal());
                builder.setSubtitle(wanNum + "人晒价");
                builder.setSubtitlehighlight(wanNum);
                if (StringUtils.isBlank(builder.getLinkurl())) {
                    String newCarscheme = "autohome://flutter?url=" + UrlUtil.encode(String.format("flutter://car/ownerseriesprice?seriesid=%s&specid=%s&fromtype=1", seriesId, hotSpecId));
                    builder.setLinkurl(newCarscheme);
                }
            }
        }).exceptionally(e -> {
            log.error(String.format("获取提车价糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    /**
     * 二手车糖豆
     */
    CompletableFuture<Void> getUsedCarBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId) {
        if (StringUtils.isNoneBlank(builder.getSubtitle(), builder.getLinkurl())) {
            return CompletableFuture.completedFuture(null);
        }
        return seriesUsedCarComponent.get(seriesId).thenAccept(info -> {
            if (info != null) {
                if (StringUtils.isBlank(builder.getSubtitle()) && !info.getSubTitle().contains("暂无")) {
                    builder.setSubtitle(info.getSubTitle());
                    builder.setSubtitlehighlight(info.getSubTitle().replace("最低", ""));
                }
                if (StringUtils.isBlank(builder.getLinkurl())) {
                    SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
                    int seriesState = seriesDetailDto.getState();
                    String eid = seriesState==40?"112877":"111397";
                    builder.setLinkurl(info.getJumpurl().replace("pvareaid=111397", "pvareaid="+eid));
                }
            }
            if (StringUtils.isBlank(builder.getSubtitle())) {
                builder.setSubtitle("暂无");
            }
        }).exceptionally(e -> {
            log.error(String.format("获取二手车糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }

    /**
     * 车展 > 之家实测糖豆
     */
    CompletableFuture<Void> getTestStandardBean(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder, int seriesId, String seriesName) {
        return seriesTestDataComponent.get(seriesId).thenAcceptBoth(autoShowNewsComponent.get(autoShowConfig.getAutoshowid(), seriesId), (info, news) -> {
            String optLinkurl = getOperateCandyBeans(seriesId);
            if (StringUtils.isNotEmpty(optLinkurl)) {
                builder.setLinkurl(String.format(optLinkurl,seriesId,UrlUtil.encode(seriesName)));
            } else if (autoShowConfig.IsBetweenDate() && news != null && news.getNewsItems().size() >= 3) {
                builder.setSubtitle(autoShowConfig.getBeanSubTitle());
                builder.setTitle(autoShowConfig.getBeanTitle());
                builder.setLinkurl("autohome://article/newseriesarticle?seriesid=" + seriesId + "&tabid=10007&seriesname=" + UrlUtil.encode(seriesName));
                builder.setTypeid(1010901);
                Pvitem.Builder pvitem = builder.getPvitem().toBuilder();
                pvitem.putArgvs("typeid", "1010901")
                        .putArgvs("typename", autoShowConfig.getBeanTitle());
                builder.setPvitem(pvitem);
            } else {
                if (info != null && CollectionUtils.isNotEmpty(info.getTestData())) {
                    SeriesTestDataDto.SeriesTestData119Dto seriesTestData119Dto = info.getTestData().get(0);
                    builder.setSubtitle("真实权威");
                    builder.setTitle("之家实测");
                    builder.setLinkurl("autohome://car/ahtest?seriesid=" + seriesId + "&sourceid=3&specid=" + seriesTestData119Dto.getSpecId() + "&dataid=" + seriesTestData119Dto.getDataId());

                    //燃油车副标题：油耗xx.xL，小数点保留1位，四舍五入，若没有油耗数据，使用打底文案
                    //新能源副标题：固定文案“座舱360°测评”，若没有座舱测试项，使用打底文案。
                    if (StringUtils.isNotEmpty(EnergyTypesEnum.getTypeByValue(seriesTestData119Dto.getFueltypedetail()))) {
                        if (seriesTestData119Dto.getTestItemlist().stream().filter(e -> "智能座舱".equals(e.getName())).findFirst().isPresent()) {
                            builder.setSubtitle("座舱360°测评");
                        }
                    } else {
                        Optional<SeriesTestDataDto.TestItemSummary> first = seriesTestData119Dto.getTestItemlist().stream().filter(e -> "百公里油耗".equals(e.getName())).findFirst();
                        if (first.isPresent()) {
                            builder.setSubtitle("油耗" + String.format("%.1f", Double.valueOf(first.get().getShowValue())) + first.get().getUnit());
                        }
                    }
                }
            }
            builder.setSubtitlehighlight(builder.getSubtitle());
        }).exceptionally(e -> {
            log.error(String.format("获取之家实测糖豆异常 seriesId:%s ", seriesId), e);
            return null;
        });
    }


    /**
     * 养车成本糖豆
     * 如果是停售车系，则换成高价卖车
     *
     * @param seriesId    车系id
     * @param cityId      城市id
     * @param seriesState 车类状态
     * @param replaceMap  替换的糖豆
     */
    CompletableFuture<Void> getBYJGBean(
            SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder,
            int seriesId, int cityId, int seriesState,
            Map<String, SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> replaceMap
    ) {
        //停售车系，换成高价卖车
        if (CommonHelper.isStopOrUnsold(seriesState) && replaceMap.containsKey("GJMC")) {
            builder.mergeFrom(replaceMap.get("GJMC").build());
            return CompletableFuture.completedFuture(null);
        } else {
            //如果配置了subtitle和linkurl，就不再请求原接口了
            if (StringUtils.isNoneBlank(builder.getSubtitle(), builder.getLinkurl())) {
                return CompletableFuture.completedFuture(null);
            }
            return seriesCityYangcheComponent.get(seriesId, cityId).thenAccept(info -> {
                if (info != null) {
                    if (StringUtils.isBlank(builder.getSubtitle()) && StringUtils.isNotBlank(info.getData())) {
                        builder.setSubtitle(info.getData().replace("元", ""));
                    }
                    if (StringUtils.isBlank(builder.getLinkurl())) {
                        builder.setLinkurl(info.getAppHref());
                    }
                }
                //https://doc.autohome.com.cn/docapi/page/share/share_tYgda7q6zI
                //版本：11.60.0版本之后,去掉打底的加油、洗车业务
//                if ("B".equals(yctgabtest) || StringUtils.isEmpty(builder.getLinkurl())) {
//                    List<SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder> functionBeansByjgTestList = functionentrybeans(functionentrybeanstwoByjg);
//                    if (functionBeansByjgTestList != null && !functionBeansByjgTestList.isEmpty()) {
//                        String key = isNewEnergy == 1 ? "XCBT" : "CZBT";
//
//                        //无数据时 走 加油 / 洗车 打底
//                        //命中B时，直接替换成 加油、洗车
//                        SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder replaceItem = functionBeansByjgTestList.stream().filter(x -> x.getCode().equals(key)).findFirst().orElse(null);
//                        if (replaceItem != null) {
//                            builder.setTitle(replaceItem.getTitle());
//                            builder.setSubtitle(replaceItem.getSubtitle());
//                            builder.setIconurl(replaceItem.getIconurl());
//                            builder.setLinkurl(replaceItem.getLinkurl());
//                            builder.setTypeid(replaceItem.getTypeid());
//                            builder.setCode(replaceItem.getCode());
//                        }
//                    }
//                }

            }).exceptionally(e -> {
                log.error(String.format("获取养车价格异常 seriesId:%s,cityId:%s ", seriesId, cityId), e);
                return null;
            });
        }
    }

    /**
     * 新车系-tab标签和热点数据
     */
    public CompletableFuture<Void> getNewSeriesTabAndHotNews(SeriesBaseInfoRequest request,
                                                             SeriesDetailDto seriesDetailDto,
                                                             CompletableFuture<List<NewSeriesCityHotNewsAndTabDto>> newSeriesCityHotNewsAndTabComponentAsync,
                                                             SeriesBaseInfoResponse.Result.Builder result) {

        CompletableFuture<List<SeriesBaseInfoResponse.Result.Tabinfo>> tabInfos = getTabInfos(request, seriesDetailDto, 1,true);

        return CompletableFuture.allOf(tabInfos, newSeriesCityHotNewsAndTabComponentAsync).thenAccept((obj) -> {
            List<NewSeriesCityHotNewsAndTabDto> hotNewsList = newSeriesCityHotNewsAndTabComponentAsync.join();
            List<SeriesBaseInfoResponse.Result.Tabinfo> subTabList = tabInfos.join();

            //处理新版车系tab
            List<SeriesSubTabInfo> newTabConfig = JsonUtil.toObjectList(newtabinfo, SeriesSubTabInfo.class);
            List<SeriesBaseInfoResponse.Result.Tabinfo> newTabList = new ArrayList<>();
            //没有评论时
            boolean hasHotReply = subTabList.stream().filter(tab -> tab.getTypeid() == 21).findFirst().isPresent();
            //没有新车对比
            boolean hasCarCompare = subTabList.stream().filter(tab -> tab.getTypeid() == 40003 && tab.getModuletype() == 90005).findFirst().isPresent();
            newTabList.add(SeriesBaseInfoResponse.Result.Tabinfo.newBuilder().setTabtitle("车型").setModuletype(90006).setTypeid(90006).build());
            newTabConfig.forEach(x -> {
                SeriesBaseInfoResponse.Result.Tabinfo item = subTabList.stream().filter(tab -> x.getTypeid() == tab.getTypeid() && x.getModuletype().intValue() == tab.getModuletype()).findFirst().orElse(null);
                if (item != null) {
                    SeriesBaseInfoResponse.Result.Tabinfo.Builder build = item.toBuilder()
                            .setDisplaytitlebar(x.getDisplaytitlebar())
                            .setTabtitle(x.getTabtitle())
                            .setCardtitle(x.getCardtitle())
                            .setTabicon(x.getTabicon());

                    if (x.getTypeid() == 2 && !hasHotReply) {
                        build.setDisplaytitlebar(1);
                    }else if (x.getTypeid() == 12 && !hasCarCompare) {
                        build.setDisplaytitlebar(1);
                    }
                    //调置角标
                    if (build.getDisplaytitlebar() == 1 && hotNewsList != null) {
                        Optional<NewSeriesCityHotNewsAndTabDto> hotNewsDto = hotNewsList.stream().filter(hot -> x.getTabtitle().equals(hot.getType())).findFirst();
                        if (hotNewsDto.isPresent()) {
                            build.setTaginfo(hotNewsDto.get().getRedDot());
                        }
                    }
                    newTabList.add(build.build());
                }
            });
            result.addAllTabinfo(newTabList);
            // 处理热点数据
            if (hotNewsList != null) {
                result.addAllHotitemlist(processHotTab(newTabList, hotNewsList));
            }
        }).exceptionally(e -> {
            log.error("getNewSeriesTabAndHotNews error", e);
           return null;
        });
    }
    public CompletableFuture<List<SeriesBaseInfoResponse.Result.Tabinfo>> getTabInfos(SeriesBaseInfoRequest request,
                                                                                      SeriesDetailDto series,
                                                                                      int funcabtest,
                                                                                      boolean isNewSeriesSummary) {
        CompletableFuture<SeriesTabDto> seriesTabFuture = seriesTabComponent.get(series.getId());
        CompletableFuture<SeriesCityTabDto> seriesCityTabFuture = seriesCityTabComponent.get(series.getId(), request.getCityid());
        CompletableFuture<AutoShowNewsDto> autoShowNewsFuture = autoShowNewsComponent.get(autoShowConfig.getAutoshowid(), series.getId());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(seriesTabFuture, seriesCityTabFuture, autoShowNewsFuture);

        return allFutures.thenApply((obj) -> {
            // 所有Future都已完成，现在可以获取结果
            SeriesTabDto seriesTabDto = seriesTabFuture.join();
            SeriesCityTabDto seriesCityTabDto = seriesCityTabFuture.join();
            AutoShowNewsDto autoShowNewsDto = autoShowNewsFuture.join();
            String tabinfo = tabinfolistv2;
            List<SeriesSubTabInfo> subTabList = JsonUtil.toObjectList(tabinfo, SeriesSubTabInfo.class);
            subTabList = subTabList.stream().filter(s1 -> StringUtils.isEmpty(s1.getPluginversion()) || CommonHelper.isTakeEffectVersion(request.getPluginversion(), s1.getPluginversion())).collect(Collectors.toList());
            Map<String, String> replaceValue = Maps.newHashMap();
            replaceValue.put("seriesid", series.getId() + "");
            replaceValue.put("brandid", series.getBrandId() + "");
            replaceValue.put("state", series.getState() + "");
            replaceValue.put("levelid", series.getLevelId() + "");
            replaceValue.put("seriesname", UrlUtil.encode(series.getName()).replace("+", "%20"));
            StrSubstitutor strSubstitutor = new StrSubstitutor(replaceValue, "{", "}");

            for (SeriesSubTabInfo tabItem : subTabList) {
                replaceValue.put("tabid", tabItem.getTypeid() + "");
                replaceValue.put("cardtitle", UrlUtil.encode(tabItem.getCardtitle()));
                tabItem.setTaburl(strSubstitutor.replace(tabItem.getTaburl()));
            }
            if (request.getPm()!=3) {
                //资讯tab逻辑：车展》新车》默认
                subTabList.stream().filter(t -> t.getTypeid() == 2).findFirst().ifPresent(x -> {
                    if (autoShowNewsDto != null && autoShowNewsDto.getNewsItems().size() >= 3) {
                        x.setTabtitle(autoShowConfig.getBeanTitle());
                    }
                    //卡片标题新车显示：【新车资讯】
                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.61.0") && series.getIsNewCar()) {
                        if (!x.getTabtitle().equals(autoShowConfig.getBeanTitle())) {
                            x.setTabtitle("新车资讯");
                        }
                        x.setCardtitle("新车资讯");
                        x.setTaburl("autohome://car/articlechannel?seriesid=" + series.getId() + "&viewheight=300&tabid=2&cardtitle=" + UrlUtil.encode("新车资讯"));
                    }
                });
            }
            //停售车系：二手车提到第二位
            if (series.getState() == 40) {
                Optional<SeriesSubTabInfo> first = subTabList.stream().filter(t -> t.getTypeid() == 18).findFirst();
                if (first.isPresent()) {
                    subTabList.remove(first.get());
                    subTabList.add(1, first.get());
                }
            }
            //实验版：图述tab
            if (funcabtest == 1
                    && !Arrays.asList(7, 11, 12, 13, 14, 15).contains(series.getLevelId())
                    && Arrays.asList(20, 30).contains(series.getState())
                    && series.getEnergytype() == 1
                    && series.getParamIsShow() == 1) {
                SeriesSubTabInfo subTabInfo = new SeriesSubTabInfo();
                String title = "图述";
                subTabInfo.setTabtitle(title);
                subTabInfo.setCardtitle(title);
                subTabInfo.setTaburl(String.format("autohome://car/energyconfig?seriesid=%s&specid=&fromtype=3&hiddenspec=0&issecondtab=1&cardtitle=%s", series.getId(), UrlUtil.encode(title)));
                subTabInfo.setTypeid(20);
                subTabList.add(1, subTabInfo);
            }
            //车系热评
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.61.5")) {
                //实验1-【车系热评】车系页是否有此功能，101364，A=下掉所有车系的图述，车系id在白名单的车系页有热评；B=线上原版；C=下掉所有车系的图述，车系页无热评；D=同A组；E=同C组
                //0520 在实验101364中，新增实验组F，实验组设定为“车系页有热评，下掉所有车系的图述”（同A），但该实验组独立使用一份热评车系白名单，白名单支持阿波罗配置。
                //0520 在实验101364中，新增实验组G，实验组设定为“车系页无热评，下掉所有车系的图述，将论坛和资讯交换位置。
                if (Arrays.asList("A", "D", "F", "I").contains(request.getHotcommentabtest())) {
                    //移除图述，增加热评
                    subTabList.removeIf(tab -> tab.getTypeid() == 20);
                    List<String> seriesIds = "F".equals(request.getHotcommentabtest()) ? Arrays.stream(seriesHotReplySeriesIdsTestF.split(",")).toList() : Arrays.stream(seriesHotReplySeriesIds.split(",")).toList();
                    if ("I".equals(request.getHotcommentabtest())) {
                        seriesIds = Arrays.stream(seriesHotReplySeriesIdsTestI.split(",")).toList();
                    }

                    if (seriesIds.contains(String.valueOf(series.getId())) && seriesTabDto != null && seriesTabDto.getHasReplyData() > 0) {
                        SeriesSubTabInfo subTabInfo = new SeriesSubTabInfo();
                        String title = getSeriesHotReplyTitleByAb(request);//"热评";
                        subTabInfo.setTabtitle(title);
                        subTabInfo.setCardtitle(title);
                        subTabInfo.setPluginversion("11.61.5");
                        subTabInfo.setTaburl("autohome://article/seriescommentpage?seriesid=" + series.getId() + "&objtype=1000002");
                        subTabInfo.setTypeid(21);
                        subTabList.add(1, subTabInfo);
                    }
                } else if ("C".equals(request.getHotcommentabtest()) || "E".equals(request.getHotcommentabtest())) {
                    //移除图述
                    subTabList.removeIf(tab -> tab.getTypeid() == 20);
                } else if ("G".equals(request.getHotcommentabtest())) {
                    //移除图述
                    subTabList.removeIf(tab -> tab.getTypeid() == 20);
                    //将论坛和资讯交换位置
                    Optional<SeriesSubTabInfo> news = subTabList.stream().filter(tab -> tab.getTypeid() == 2).findFirst();
                    Optional<SeriesSubTabInfo> forum = subTabList.stream().filter(tab -> tab.getTypeid() == 4).findFirst();
                    if (forum.isPresent() && news.isPresent()) {
                        int forumIndex = subTabList.indexOf(forum.get());
                        int newsIndex = subTabList.indexOf(news.get());
                        subTabList.set(newsIndex, forum.get());
                        subTabList.set(forumIndex, news.get());
                    }
                }
                //101509实验判断逻辑暂时屏蔽，将车系名单融合到101364有车系热评的实验组里 后续产品可能还会开启  需求地址：https://doc.autohome.com.cn/docapi/page/share/share_xdGce3XLrk
                //在原有实验101364基础上 增加101509实验 命中A在车系热评区增加AI观点 如果原实验101364已经展示车系热评则不再处理101509
//                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.63.0")) {
//                    Optional<SeriesSubTabInfo> tabInfo_21 = subTabList.stream().filter(tab -> tab.getTypeid() == 21).findFirst();
//                    if (Arrays.asList("A", "C").contains(request.getAiviewpointab())&&!tabInfo_21.isPresent()){
//                        //101509-AI观点车系白名单
//                        List<Integer> aiSeriesIds =JsonUtil.toObject(aiViewPoinTabSeriesidList,new TypeReference<List<Integer>>() {
//                        });
//                        if (aiSeriesIds.contains(series.getId()) && seriesTabDto != null && seriesTabDto.getHasReplyData() > 0) {
//                            //移除图述
//                            subTabList.removeIf(tab -> tab.getTypeid() == 20);
//
//                            SeriesSubTabInfo subTabInfo = new SeriesSubTabInfo();
//                            String title = getSeriesHotReplyTitleByAb(request);//"热评";
//                            subTabInfo.setTabtitle(title);
//                            subTabInfo.setCardtitle(title);
//                            subTabInfo.setPluginversion("11.61.5");
//                            subTabInfo.setTaburl("autohome://article/seriescommentpage?seriesid=" + series.getId() + "&objtype=1000002"+(request.getAiviewpointab().equals("A")?"&isshowai=1":""));
//                            subTabInfo.setTypeid(21);
//                            subTabList.add(1, subTabInfo);
//                        }
//                    }
//                }
            }

            //tab是否存在判断
            //口碑移除判断
            if (!isNewSeriesSummary&&seriesTabDto != null && seriesTabDto.getHasKouBeiData() == 0) {
                subTabList.removeIf(tab -> tab.getTypeid() == 10);
            }
            //是否有同级车数据判断
            if (!isNewSeriesSummary&&seriesTabDto != null && seriesTabDto.getHasRecommondLikeData() == 0) {
                subTabList.removeIf(tab -> tab.getTypeid() == 12);
            }
            //二手车移除判断
            if (seriesCityTabDto == null || seriesCityTabDto.getHasErShouData() == 0) {
                subTabList.removeIf(tab -> tab.getTypeid() == 18);
            }
            //命中实验，玩车tab移出判断
            if (request.getPlaycartab().equals("B")) {
                if (seriesCityTabDto == null || seriesCityTabDto.getHasGaizhuangWithRefitData() == 0) {
                    subTabList.removeIf(tab -> tab.getTypeid() == 11);
                } else {
                    //将玩车tab放置最后
                    Optional<SeriesSubTabInfo> playCar = subTabList.stream().filter(tab -> tab.getTypeid() == 11).findFirst();
                    if (playCar.isPresent()) {
                        subTabList.remove(playCar.get());
                        playCar.get().setTabtitle("改装");
                        playCar.get().setCardtitle("改装");
                        subTabList.add(playCar.get());
                    }
                }
            }
//            //买车tab 数据不一致暂不处理
//            if (seriesCityAskPriceDto == null || seriesCityAskPriceDto.getSpecCount() == 0) {
//                subTabList.removeIf(tab -> tab.getTypeid() == 16);
//            }

            //机房宕机逻辑判断
            if (computerRoom.anyDown()) {
                subTabList.removeIf(e -> e.getTypeid() == 15 || e.getTypeid() == 11);//用车\玩车

                if (computerRoom.langfangDown()) {
                    subTabList.removeIf(e -> e.getTypeid() == 10 || e.getTypeid() == 18);//口碑\二手车
                }
                if (computerRoom.yizhuangDown()) {
                    subTabList.removeIf(e -> e.getTypeid() == 2 || e.getTypeid() == 4);//论坛\资讯:因为调用的是主数据的接口，所以出不来数据
                }
            }

            //添加一些展位给客户端处理
            //1,猜你喜欢,90002
            //2,新车对比,90005
            //3,广告模块,2730 --- 固定资讯后面
            //在售商家,90003  --- 在售商家停售车才返回,放到最后
            //广告模块,2729   --- 放最后
            subTabList.add(1, new SeriesSubTabInfo("", 90002));
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.62.2") && (series.getState() == 20 || series.getState() == 30)) {
                subTabList.add(2, new SeriesSubTabInfo("", 90005));
            }
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.64.5")
                    && series.getState() == 40
                    && ("A".equalsIgnoreCase(request.getHotcommentabtest()) || "C".equalsIgnoreCase(request.getNewcarpkabtest()))
                    && subTabList.stream().anyMatch(x -> x.getTypeid() == 18)) {
                subTabList.add(2, new SeriesSubTabInfo("", 2730));
            } else {                      //广告2730：有热评tab显示其后，否则显示在资讯Tab后面
                Optional<SeriesSubTabInfo> tabInfo_hotReply = subTabList.stream().filter(tab -> tab.getTypeid() == 21).findFirst();
                if (tabInfo_hotReply.isPresent()) {
                    int hotReplyIndex = subTabList.indexOf(tabInfo_hotReply.get());
                    subTabList.add(hotReplyIndex + 1, new SeriesSubTabInfo("", 2730));
                } else {
                    Optional<SeriesSubTabInfo> news = subTabList.stream().filter(tab -> tab.getTypeid() == 2).findFirst();
                    if (news.isPresent()) {
                        int newsIndex = subTabList.indexOf(news.get());
                        subTabList.add(newsIndex + 1, new SeriesSubTabInfo("", 2730));
                    } else {
                        subTabList.add(3, new SeriesSubTabInfo("", 2730));
                    }
                }
            }
            //停售车判断，停售时将二手车tab放到猜你喜欢后面，如果有热评放到热评后面
            if (series.getState() == 40) {
                Optional<SeriesSubTabInfo> tab18 = subTabList.stream().filter(x -> x.getTypeid() == 18).findFirst();
                if (tab18.isPresent()) {
                    Optional<SeriesSubTabInfo> tabAnchor = subTabList.stream().filter(x -> x.getTypeid() == 21).findFirst();
                    if (tabAnchor.isEmpty()) {
                        tabAnchor = subTabList.stream().filter(x -> x.getTypeid() == 90002).findFirst();
                    }

                    int idx = subTabList.indexOf(tabAnchor.get());
                    subTabList.remove(tab18.get());
                    subTabList.add(idx + 1, tab18.get());
                }
                subTabList.add(new SeriesSubTabInfo("", 90003));
            }
            subTabList.add(1, new SeriesSubTabInfo("", 90004));
            subTabList.add(new SeriesSubTabInfo("", 2729));
            //卡片化结构处理
            List<SeriesBaseInfoResponse.Result.Tabinfo> tabList = new ArrayList<>();
            subTabList.forEach(x -> {
                SeriesBaseInfoResponse.Result.Tabinfo.Builder item = SeriesBaseInfoResponse.Result.Tabinfo.newBuilder();
                item.setCardtitle(x.getCardtitle());
                item.setTabtitle(x.getTabtitle());
                item.setTaburl(x.getTaburl());
                item.setTypeid(x.getTypeid());
                item.setModuletype(x.getTypeid());
                item.setTabbgurl("");

                switch (x.getTypeid()) {
                    case 1:
                        item.setDisplaytitlebar(1);
                        break;
                    case 20:
                        item.setDisplaytitlebar(1);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_series_energy_config_bg@3x.png.webp");
                        break;
                    case 2:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(2);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_spec_zixun_20230209@3x.jpg.webp");
                        break;
                    case 2730:
                        item.setModuletype(90001);
                        item.setExtrainfo(SeriesBaseInfoResponse.Result.Tabinfo.Extrainfo.newBuilder().setAreaid("2730"));
                        break;
                    case 4:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(1);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_summary_club_channel_20230209@3x.jpg.webp");
                        break;
                    case 10:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(1);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_koubei_20230209@3x.jpg.webp");
                        break;
                    case 16:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(2);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_buycar_20230209@3x.jpg.webp");
                        break;
                    case 15:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(1);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_usecar_20230209@3x.png.webp");
                        break;
                    case 11:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(1);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_playcar_20230209@3x.png.webp");
                        break;
                    case 12:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(2);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_samecar_20230209@3x.png.webp");
                        break;
                    case 18:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(2);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_usedcar_20230228@3x.png.webp");
                        break;
                    case 21:
                        item.setDisplaytitlebar(1);
                        item.setApiservicetype(2);
                        item.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/116150/hot_comment_title_background.webp");
                        break;
                    case 2729:
                        item.setModuletype(90001);
                        item.setExtrainfo(SeriesBaseInfoResponse.Result.Tabinfo.Extrainfo.newBuilder().setAreaid("2729"));
                        break;
                    case 90005:
                        item.setTypeid(40003);
                        break;
                    default:
                        // 默认情况
                }
                tabList.add(item.build());
            });

            //IM填充入口占位，车型和口碑后
            OptionalInt location1 = IntStream.range(0, tabList.size()).filter(i -> "车型".equals(tabList.get(i).getTabtitle())).findFirst();
            if (location1.isPresent()) {
                SeriesBaseInfoResponse.Result.Tabinfo.Builder item = SeriesBaseInfoResponse.Result.Tabinfo.newBuilder();
                item.setTabtitle("");
                item.setTabbgurl("");
                item.setModuletype(90005);
                item.setTypeid(40001);
                tabList.add(location1.getAsInt() + 1, item.build());
            }
            OptionalInt location2 = IntStream.range(0, tabList.size()).filter(i -> "口碑".equals(tabList.get(i).getTabtitle())).findFirst();
            if (location2.isPresent()) {
                SeriesBaseInfoResponse.Result.Tabinfo.Builder item = SeriesBaseInfoResponse.Result.Tabinfo.newBuilder();
                item.setTabtitle("");
                item.setTabbgurl("");
                item.setModuletype(90005);
                item.setTypeid(40002);
                tabList.add(location2.getAsInt() + 1, item.build());
            }
            return tabList;
        }).exceptionally(e -> {
            log.error("createTabInfos error", e);
            return new ArrayList<>();
        });
    }

    public CompletableFuture<SeriesBaseInfoResponse.Result.Head.Builder> getHeadinfo(SeriesBaseInfoRequest request) {

        return CompletableFuture.supplyAsync(() -> {
            SeriesBaseInfoResponse.Result.Head.Builder head = SeriesBaseInfoResponse.Result.Head.newBuilder();

            MegaDataDto megaData = fileComponent.getMegaPicData(request.getSeriesid());

            if (null == megaData) {
                return head;
            }
            Map<Integer, String> seriesMap = new HashMap();
            seriesMap.put(6939, "理想MEGA");
            seriesMap.put(7177, "豹5");
            List<Integer> tabList = Arrays.asList(1, 10, 3, 4);
            String tpl = "autohome://car/seriespicture?seriesid=%s&seriesname=%s&orgin=0&isfromseriestop=1&sourceid=0&categoryid=%s";

            for (int tabid : tabList) {
                SeriesBaseInfoResponse.Result.Head.Headinfo.Builder headinfo = SeriesBaseInfoResponse.Result.Head.Headinfo.newBuilder();
                SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.Builder tabinfo = SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.newBuilder();

                tabinfo.setPvitem(Pvitem.newBuilder()
                        .putArgvs("objectid", String.valueOf(tabid))
                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_new_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_new_show")));

                if (1 == tabid) {
                    tabinfo.setName("外观")
                            .setImgurl(changeSize(megaData.getActionvideoinfo().getVideoimage(), "150x0"))
                            .setCount(0)
                            .setObjectid(tabid)
                            .setTopcolor("#929BA3");

                    SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageinfo = SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                    pageinfo.setName("互动视频")
                            .setType(2)
                            .setVideourl(toHttp(megaData.getActionvideoinfo().getVideourl()))
                            .setImgurl(changeSize(megaData.getActionvideoinfo().getVideoimage(), "1100x0"))
                            .setScheme(String.format(tpl, request.getSeriesid(), UrlUtil.encode(seriesMap.get(request.getSeriesid())), tabid))
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("objectid", String.valueOf(tabid))
                                    .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                    .putArgvs("type", "2")
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));

                    headinfo.setTabinfo(tabinfo);
                    headinfo.addPagelist(pageinfo);
                }

                if (10 == tabid) {

                    List<MegaDataDto.Pic> inner = megaData.getPiclist().stream().filter(e -> tabid == e.getTypeId()).limit(5).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(inner)) {
                        continue;
                    }

                    tabinfo.setName("内饰")
                            .setCount(5)
                            .setObjectid(tabid)
                            .setImgurl(changeSize(inner.get(0).getPicurl(), "150x0"))
                            .setTopcolor("#000000");

                    headinfo.setTabinfo(tabinfo);

                    for (MegaDataDto.Pic pic : inner) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageinfo = SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageinfo.setName(pic.getPointname())
                                .setVid(pic.getVid())
                                .setType(pic.getMediatype())
                                .setVideourl("")
                                .setImgurl(changeSize(pic.getPicurl(), "1100x0"))
                                .setScheme(String.format(tpl, request.getSeriesid(), UrlUtil.encode(seriesMap.get(request.getSeriesid())), tabid))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(tabid))
                                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                        .putArgvs("type", String.valueOf(pic.getMediatype()))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));

                        headinfo.addPagelist(pageinfo);
                    }

                }

                if (3 == tabid) {

                    List<MegaDataDto.Pic> space = megaData.getPiclist().stream().filter(e -> tabid == e.getTypeId()).limit(3).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(space)) {
                        continue;
                    }

                    tabinfo.setName("座椅")
                            .setCount(3)
                            .setObjectid(tabid)
                            .setImgurl(changeSize(space.get(0).getPicurl(), "150x0"))
                            .setTopcolor("#000000");

                    headinfo.setTabinfo(tabinfo);

                    for (MegaDataDto.Pic pic : space) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageinfo = SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageinfo.setName(pic.getPointname())
                                .setVid(pic.getVid())
                                .setType(pic.getMediatype())
                                .setVideourl("")
                                .setImgurl(changeSize(pic.getPicurl(), "1100x0"))
                                .setScheme(String.format(tpl, request.getSeriesid(), UrlUtil.encode(seriesMap.get(request.getSeriesid())), tabid))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(tabid))
                                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                        .putArgvs("type", String.valueOf(pic.getMediatype()))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));

                        headinfo.addPagelist(pageinfo);
                    }
                }

                if (4 == tabid) {
                    List<MegaDataDto.Album> videos = megaData.getVideoalbum().getAlbum().stream().filter(e -> tabid == e.getTabId()).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(videos)) {
                        continue;
                    }

                    MegaDataDto.Videos videoNight = videos.get(0).getVideos().get(0);
                    tabinfo.setName("夜景")
                            .setCount(5)
                            .setObjectid(tabid)
                            .setImgurl(changeSize(videoNight.getImgurl(), "150x0"))
                            .setTopcolor("#000000");

                    headinfo.setTabinfo(tabinfo);

                    SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageinfoNight = SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();

                    pageinfoNight.setName(videoNight.getNamenoindex())
                            .setVid(videoNight.getVid())
                            .setType(1)
                            .setVideourl("")
                            .setImgurl(changeSize(videoNight.getImgurl(), "1100x0"))
                            .setScheme(String.format(tpl, request.getSeriesid(), UrlUtil.encode(seriesMap.get(request.getSeriesid())), tabid))
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("objectid", String.valueOf(tabid))
                                    .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                    .putArgvs("type", "1")
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));

                    headinfo.addPagelist(pageinfoNight);

                    List<MegaDataDto.Pic> night = megaData.getPiclist().stream().filter(e -> tabid == e.getTypeId()).limit(4).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(night)) {
                        continue;
                    }

                    for (MegaDataDto.Pic pic : night) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageinfo = SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageinfo.setName(pic.getPointname())
                                .setVid(pic.getVid())
                                .setType(pic.getMediatype())
                                .setVideourl("")
                                .setImgurl(changeSize(pic.getPicurl(), "1100x0"))
                                .setScheme(String.format(tpl, request.getSeriesid(), UrlUtil.encode(seriesMap.get(request.getSeriesid())), tabid))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(tabid))
                                        .putArgvs("seriesid", String.valueOf(request.getSeriesid()))
                                        .putArgvs("type", String.valueOf(pic.getMediatype()))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));

                        headinfo.addPagelist(pageinfo);
                    }
                }

                head.setScheme(String.format(tpl, request.getSeriesid(), UrlUtil.encode(seriesMap.get(request.getSeriesid())), 1));
                head.addList(headinfo);
            }

            return head;
        }).exceptionally(e -> {
            e.printStackTrace();
            log.error("setHeadinfo error", e);
            return SeriesBaseInfoResponse.Result.Head.newBuilder();
        });
    }

    private String changeSize(String picUrl, String sizeStr) {
        if (StringUtils.isEmpty(picUrl)) {
            return picUrl;
        }
        if (picUrl.toLowerCase().startsWith("https://")) {
            picUrl = "http:/" + picUrl.substring(7);
        }
        if (picUrl.contains("300x0_autohomecar__")) {
            return picUrl.replace("300x0_autohomecar__", sizeStr + "_autohomecar__");
        }
        if (picUrl.contains("autohomecar__")) {
            return picUrl.replace("autohomecar__", sizeStr + "_autohomecar__");
        }

        int index = picUrl.lastIndexOf("/");
        return picUrl.substring(0, index + 1) + sizeStr + "_autohomecar__" + picUrl.substring(index + 1);
    }

    private String toHttp(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        if (url.toLowerCase().startsWith("https://")) {
            url = "http:/" + url.substring(7);
        }
        return url;
    }

    /**
     * 是显示新车日历
     */
    CompletableFuture<Boolean>existCarCalendar(String pluginversion,int energetype,int seriesId,boolean isNewCar) {
        if (CommonHelper.isTakeEffectVersion(pluginversion, "11.62.0")) {
            if (isNewCar) {
                return CompletableFuture.completedFuture(true);
            }
            return seriesTimeAxisComponent.get(seriesId).thenApply(info -> {
                NewCarCalendarConfig newCarCalendarConfig = JsonUtil.toObject(NewCarCalendarConfigJson, NewCarCalendarConfig.class);
                if (info == null || info.getItemList() == null || newCarCalendarConfig == null) {
                    return false;
                }
                if (newCarCalendarConfig.getBlacklist().contains(seriesId)) {
                    return false;
                }
                if (energetype == 1 && newCarCalendarConfig.getNewenergy_isopen() == 0) {
                    return false;
                }
                if (energetype != 1 && newCarCalendarConfig.getFuel_isopen() == 0) {
                    return false;
                }
                //车型发布时间90天内
                SeriesTimeAxisDto.Item item = info.getItemList().stream().filter(x -> x.getTypecode() == 6666).findFirst().orElse(null);
                if (item != null && StringUtils.isNotEmpty(item.getDate())) {
                    return DateUtil.getDistanceOfTwoDate(DateUtil.parse(item.getDate(), "yyyy-MM-dd"), new Date()) <= newCarCalendarConfig.getShow_limit();
                }
                return false;
            }).exceptionally(e -> {
                log.error("existCarCalendar-error", e);
                return false;
            });
        }
        return CompletableFuture.completedFuture(false);
    }

    /**
     * 根据实验获取对应的车系评论文案内容
     * 需求地址：https://doc.autohome.com.cn/docapi/page/share/share_uolHGwnQW0
     * @param request 请求参数对象
     * @return
     */
    private String getSeriesHotReplyTitleByAb(SeriesBaseInfoRequest request){
        try {
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.62.5")&&!Strings.isNullOrEmpty(request.getComtabtextab())){
                List<SeriesHotReplyTextAbConfig> seriesHotReplyTextAbList = JsonUtil.toObjectList(seriesHotReplyTextAb, SeriesHotReplyTextAbConfig.class);
                if (seriesHotReplyTextAbList!=null&&seriesHotReplyTextAbList.size()>0){
                    SeriesHotReplyTextAbConfig config=seriesHotReplyTextAbList.stream().filter(p->p.getAbversion().equals(request.getComtabtextab())).findFirst().orElse(null);
                    if (config!=null&&!Strings.isNullOrEmpty(config.getFirsttabname())){
                        return config.getFirsttabname();
                    }
                }
            }
        }catch (Exception ex){
            log.error("getSeriesHotReplyTextAb-err:{}",ex);
        }
        return "热评";
    }

    /**
     * 鸿蒙-11085糖豆排序
     * @param builder
     * @return
     */
    private int getOrder(SeriesBaseInfoResponse.Result.Itemlist.Data.List.Builder builder){
        return switch (builder.getCode()) {
            case "CLJG" -> 1;//本地报价
            case "CXLD" -> 2;//亮点
            case "CLUB" -> 3;//论坛
            case "CHAT" -> 4;//热聊
            case "ASK" -> 5;//常见问题
            case "TCJ" -> 6;//提车价
            case "4SBY" -> 7;//4S保养
            case "2scJ" -> 8;//二手车
            default -> Integer.MAX_VALUE;
        };
    }

    /**
     * 获取热点标签
     * @param resultList 车系热点标签List
     * @param hasComment 是否有热评
     * @param hasNewCarPk 是否有新车对比
     * @param hasSameLevelCar 是否有同级车
     * @return 热点标签List response
     */
    public List<SeriesBaseInfoResponse.Result.HotItem> getHotItemList(List<NewSeriesCityHotNewsAndTabDto> resultList, boolean hasComment, boolean hasNewCarPk, boolean hasSameLevelCar) {

        List<SeriesBaseInfoResponse.Result.HotItem> itemList = new ArrayList<>();
        for (NewSeriesCityHotNewsAndTabDto item : resultList) {
            NewSeriesHotTabEnum hotTabEnum = NewSeriesHotTabEnum.getByName(item.getType());
            if (Objects.isNull(hotTabEnum)) {
                continue;
            }
            SeriesBaseInfoResponse.Result.HotItem.Builder builder = SeriesBaseInfoResponse.Result.HotItem.newBuilder();
            Integer moduleId = hotTabEnum.getModuleId();
            Integer typeId = hotTabEnum.getTypeId();
            String typeName = item.getType();
            switch (hotTabEnum) {
                case SPEC -> {
                    continue;
                }
                case COMMENT -> {
                    // 评论不存在时 将热点tab标签改为资讯
                    if (!hasComment) {
                        moduleId = 2;
                        typeId = 2;
                        typeName = "资讯";
                    }
                }
                case COMPARE -> {
                    if (!hasNewCarPk) {
                        // 没有新车对比 有同级车 对比热点标签设置为同级车
                        if (hasSameLevelCar) {
                            moduleId = 12;
                            typeId = 12;
                            typeName = "同级车";
                        } else {
                            // 既没有新车对比 也没有同级车 不添加对比的标签
                            continue;
                        }
                    }
                }
            }
            builder.setModuletype(moduleId);
            builder.setTypeid(typeId);
            SeriesBaseInfoResponse.Result.HotItem.Data.Builder dataBuilder = SeriesBaseInfoResponse.Result.HotItem.Data.newBuilder();
            dataBuilder.setIconurl(item.getIcon());
            dataBuilder.setTitle(item.getTitle());
            dataBuilder.setSubtitle(item.getSubtitle());
            dataBuilder.setLinkurl(item.getLinkUrl());
            dataBuilder.setScale(1.93);
            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("cityid", item.getCityId().toString())
                    .putArgvs("title", item.getTitle())
                    .putArgvs("seriesid", item.getSeriesId().toString())
                    .putArgvs("hotid", item.getPageCardDataId().toString())
                    .putArgvs("hotposition", typeName)
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_operate_entry_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_operate_entry_show"));

            dataBuilder.setPvitem(pvBuilder);
            builder.setData(dataBuilder);
            itemList.add(builder.build());
        }
        return itemList;
    }

    /**
     * 处理热点标签
     */
    List<SeriesBaseInfoResponse.Result.HotItem> processHotTab(List<SeriesBaseInfoResponse.Result.Tabinfo> tabinfos, List<NewSeriesCityHotNewsAndTabDto> newsAndTabDtoList) {
        try {
            Map<String, SeriesBaseInfoResponse.Result.Tabinfo> tabinfoMap = tabinfos.stream().collect(Collectors.toMap(tabInfo -> tabInfo.getTypeid() + StrPool.UNDERLINE + tabInfo.getModuletype(), tabinfo -> tabinfo));
            // 是否有热评
            boolean hasComment = tabinfoMap.containsKey("21_21");
            // 是否有新车对比
            boolean hasNewCarPk = tabinfoMap.containsKey("40003_90005");
            // 是否存在同级车
            boolean hasSameLevelCar = tabinfoMap.containsKey("12_12");
            return getHotItemList(newsAndTabDtoList, hasComment, hasNewCarPk, hasSameLevelCar);
        } catch (Exception e) {
            log.error("processHotTab-error", e);
            return new ArrayList<>();
        }
    }


    /**
     * 获取高质图库所构成的头图信息
     *
     * @param seriesDetail
     * @param hqPicDataDto
     * @return
     */
    public CompletableFuture<SeriesBaseInfoResponse.Result.Head.Builder> getHqPicHeadInfo(SeriesDetailDto seriesDetail,
                                                                                          HqPicDataDto hqPicDataDto) {

        return CompletableFuture.supplyAsync(() -> {
            List<CompletableFuture> tasks = new ArrayList<>();
            String tpl = "autohome://car/seriespicture?seriesid=%s&seriesname=%s&orgin=0&isfromseriestop=1&sourceid=0&categoryid=%s";
            //外观
            AtomicReference<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> type1HeadInfoRef = new AtomicReference<>();
            //内饰
            AtomicReference<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> type2HeadInfoRef = new AtomicReference<>();
            //座椅
            AtomicReference<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> type3HeadInfoRef = new AtomicReference<>();
            //夜景
            AtomicReference<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> type4HeadInfoRef = new AtomicReference<>();
            tasks.add(buildType1HeadInfo(tpl, seriesDetail, hqPicDataDto).thenAcceptAsync(type1HeadInfoRef::set)
                    .exceptionally(e -> null));
            tasks.add(buildType2HeadInfo(tpl, seriesDetail, hqPicDataDto).thenAcceptAsync(type2HeadInfoRef::set)
                    .exceptionally(e -> null));
            tasks.add(buildType3HeadInfo(tpl, seriesDetail, hqPicDataDto).thenAcceptAsync(type3HeadInfoRef::set)
                    .exceptionally(e -> null));
            tasks.add(buildType4HeadInfo(tpl, seriesDetail).thenAcceptAsync(type4HeadInfoRef::set)
                    .exceptionally(e -> null));
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

            SeriesBaseInfoResponse.Result.Head.Builder head = SeriesBaseInfoResponse.Result.Head.newBuilder();
            if (Objects.nonNull(type1HeadInfoRef.get())) {
                head.addList(type1HeadInfoRef.get());
            }
            if (Objects.nonNull(type2HeadInfoRef.get())) {
                head.addList(type2HeadInfoRef.get());
            }
            if (Objects.nonNull(type3HeadInfoRef.get())) {
                head.addList(type3HeadInfoRef.get());
            }
            if (Objects.nonNull(type4HeadInfoRef.get())) {
                head.addList(type4HeadInfoRef.get());
            }
            head.setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 1));
            return head;
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.error("get HeadInfo error, seriesId={}, hqPicDataDto={}", seriesDetail.getId(), hqPicDataDto, e);
            return SeriesBaseInfoResponse.Result.Head.newBuilder();
        });
    }

    private CompletableFuture<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> buildType1HeadInfo(String tpl,
                                                                                                      SeriesDetailDto seriesDetail,
                                                                                                      HqPicDataDto hqPicDataDto) {
        // 取列表中的第一张图，用于获取对应车型id
        return hqPhotoComponent.get(seriesDetail.getId(), 0, 0, 0, 1, 0, 1, 1, 0, 0)
                .thenApplyAsync(hqPhotoPageDto -> {
                    int specId = Objects.isNull(hqPhotoPageDto) || CollectionUtils.isEmpty(hqPhotoPageDto.getItems())
                            ? 0
                            : hqPhotoPageDto.getItems().get(0).getSpecId();
                    // 互动视频列表
                    List<HqPicDataDto.RotateVideoAlbum> videoAlbumList = hqPicDataDto.getRotateVideoAlbumList();
                    if (CollectionUtils.isNotEmpty(videoAlbumList)) {
                        // 取互动视频列表
                        HqPicDataDto.RotateVideoAlbum rotateVideoAlbum;
                        if (specId > 0) {
                            rotateVideoAlbum = videoAlbumList.stream()
                                    .filter(e -> e.getSpecId() == specId)
                                    .findFirst()
                                    .orElse(null);
                            // 兜底处理
                            if (Objects.isNull(rotateVideoAlbum)) {
                                rotateVideoAlbum = videoAlbumList.get(0);
                            }
                        } else {
                            rotateVideoAlbum = videoAlbumList.get(0);
                        }

                        if (Objects.nonNull(rotateVideoAlbum)) {
                            // 取关门关灯的互动视频
                            HqPicDataDto.PointRotateVideo pointRotateVideo = rotateVideoAlbum.getPointRotateVideoList().stream()
                                    .filter(e -> e.getPointId() == 1)
                                    .findFirst()
                                    .orElse(null);
                            if (Objects.nonNull(pointRotateVideo)
                                    && Objects.nonNull(pointRotateVideo.getMiniVideo())) {
                                HqPicDataDto.RotateVideo rotateVideo = pointRotateVideo.getMiniVideo();
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Builder headInfo =
                                        SeriesBaseInfoResponse.Result.Head.Headinfo.newBuilder();
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.Builder tabInfo =
                                        SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.newBuilder();

                                tabInfo.setName("外观")
                                        .setImgurl(HttpUtils.ToHttps(changeSize(rotateVideo.getImgUrl(), "150x0")))
                                        .setCount(0)
                                        .setObjectid(1)
                                        .setTopcolor("#929BA3")
                                        .setPvitem(Pvitem.newBuilder()
                                                .putArgvs("objectid", String.valueOf(1))
                                                .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                                .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_new_click"))
                                                .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_new_show")));

                                SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageInfo =
                                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                                pageInfo.setName("互动视频")
                                        .setType(2)
                                        .setVideourl(toHttp(rotateVideo.getVideoUrl()))
                                        .setImgurl(HttpUtils.ToHttps(changeSize(rotateVideo.getImgUrl(), "1100x0")))
                                        .setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 1))
                                        .setPvitem(Pvitem.newBuilder()
                                                .putArgvs("objectid", String.valueOf(1))
                                                .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                                .putArgvs("type", "2")
                                                .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                                .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));
                                headInfo.setTabinfo(tabInfo);
                                headInfo.addPagelist(pageInfo);
                                return headInfo;
                            }
                        }
                    }
                    return null;
                });
    }

    private CompletableFuture<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> buildType2HeadInfo(String tpl,
                                                                                                      SeriesDetailDto seriesDetail,
                                                                                                      HqPicDataDto hqPicDataDto) {
        return hqPhotoComponent.get(seriesDetail.getId(), 0, 0, 0, 2, 0, 1, 2, 0, 0)
                .thenApplyAsync(hqPhotoPageDto -> {
                    if (Objects.isNull(hqPhotoPageDto) || CollectionUtils.isEmpty(hqPhotoPageDto.getItems())) {
                        return null;
                    }
                    int specId = hqPhotoPageDto.getItems().get(0).getSpecId();

                    // 取影音娱乐合集中的前两个视频
                    List<HqPicDataDto.Video> videoList = new ArrayList<>();
                    HqPicDataDto.VideoAlbum videoAlbum;
                    if (specId > 0) {
                        videoAlbum = hqPicDataDto.getVideoAlbumList().stream()
                                .filter(e -> e.getSpecId() == specId)
                                .findFirst()
                                .orElse(null);
                        // 兜底处理
                        if (Objects.isNull(videoAlbum)) {
                            videoAlbum = hqPicDataDto.getVideoAlbumList().get(0);
                        }
                    } else {
                        videoAlbum = hqPicDataDto.getVideoAlbumList().get(0);
                    }
                    if (Objects.nonNull(videoAlbum)) {
                        HqPicDataDto.TypeAlbum typeAlbum = videoAlbum.getTypeAlbumList().stream()
                                .filter(e -> e.getTypeId() == 2)
                                .findFirst()
                                .orElse(null);
                        if (Objects.nonNull(typeAlbum)) {
                            // 取影音娱乐的前两个视频
                            HqPicDataDto.SubTypeAlbum subTypeAlbum = typeAlbum.getSubTypeAlbumList().stream()
                                    .filter(e -> e.getSubTypeId() == 6)
                                    .findFirst()
                                    .orElse(null);
                            if (Objects.nonNull(subTypeAlbum)) {
                                videoList = subTypeAlbum.getVideoList().stream().limit(2).toList();
                            }
                        }
                    }

                    SeriesBaseInfoResponse.Result.Head.Headinfo.Builder headInfo =
                            SeriesBaseInfoResponse.Result.Head.Headinfo.newBuilder();
                    SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.Builder tabInfo =
                            SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.newBuilder();

                    tabInfo.setName("内饰")
                            .setCount(hqPhotoPageDto.getItems().size() + videoList.size())
                            .setObjectid(10)
                            .setImgurl(HttpUtils.ToHttps(changeSize(
                                    ImageUtils.getFullImagePathWithoutReplace(hqPhotoPageDto.getItems().get(0).getUrl()), "150x0")))
                            .setTopcolor("#000000")
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("objectid", String.valueOf(10))
                                    .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_new_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_new_show")));
                    headInfo.setTabinfo(tabInfo);

                    for (HqPhotoDto pic : hqPhotoPageDto.getItems()) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageInfo =
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageInfo.setName(Objects.nonNull(pic.getPointName()) ? pic.getPointName() : "")
                                .setVid(Objects.nonNull(pic.getMid()) ? pic.getMid() : "")
                                .setType(0)
                                .setVideourl("")
                                .setImgurl(HttpUtils.ToHttps(changeSize(ImageUtils.getFullImagePathWithoutReplace(pic.getUrl()), "1100x0")))
                                .setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 10))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(10))
                                        .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                        .putArgvs("type", String.valueOf(0))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));
                        headInfo.addPagelist(pageInfo);
                    }
                    for (HqPicDataDto.Video video : videoList) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageInfo =
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageInfo.setName(Objects.nonNull(video.getName()) ? video.getName() : "")
                                .setVid(Objects.nonNull(video.getVid()) ? video.getVid() : "")
                                .setType(1)
                                .setVideourl("")
                                .setImgurl(HttpUtils.ToHttps(changeSize(video.getImgUrl(), "1100x0")))
                                .setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 10))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(10))
                                        .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                        .putArgvs("type", String.valueOf(1))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));
                        headInfo.addPagelist(pageInfo);
                    }

                    return headInfo;
                });
    }

    private CompletableFuture<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> buildType3HeadInfo(String tpl,
                                                                                                      SeriesDetailDto seriesDetail,
                                                                                                      HqPicDataDto hqPicDataDto) {
        return hqPhotoComponent.get(seriesDetail.getId(), 0, 0, 0, 3, 0, 1, 3, 0, 0)
                .thenApplyAsync(hqPhotoPageDto -> {
                    if (Objects.isNull(hqPhotoPageDto) || CollectionUtils.isEmpty(hqPhotoPageDto.getItems())) {
                        return null;
                    }
                    int specId = hqPhotoPageDto.getItems().get(0).getSpecId();

                    List<HqPicDataDto.Video> videoList = new ArrayList<>();
                    HqPicDataDto.VideoAlbum videoAlbum;
                    if (specId > 0) {
                        videoAlbum = hqPicDataDto.getVideoAlbumList().stream()
                                .filter(e -> e.getSpecId() == specId)
                                .findFirst()
                                .orElse(null);
                        // 兜底处理
                        if (Objects.isNull(videoAlbum)) {
                            videoAlbum = hqPicDataDto.getVideoAlbumList().get(0);
                        }
                    } else {
                        videoAlbum = hqPicDataDto.getVideoAlbumList().get(0);
                    }
                    if (Objects.nonNull(videoAlbum)) {
                        HqPicDataDto.TypeAlbum typeAlbum = videoAlbum.getTypeAlbumList().stream()
                                .filter(e -> e.getTypeId() == 3)
                                .findFirst()
                                .orElse(null);
                        if (Objects.nonNull(typeAlbum)) {
                            // 储物体验
                            HqPicDataDto.SubTypeAlbum subTypeAlbum1 = typeAlbum.getSubTypeAlbumList().stream()
                                    .filter(e -> e.getSubTypeId() == 9)
                                    .findFirst()
                                    .orElse(null);
                            if (Objects.nonNull(subTypeAlbum1)) {
                                HqPicDataDto.Video video1 = subTypeAlbum1.getVideoList().stream()
                                        .limit(1)
                                        .findFirst()
                                        .orElse(null);
                                if (Objects.nonNull(video1)) {
                                    videoList.add(video1);
                                }
                            }
                            // 装载体验
                            HqPicDataDto.SubTypeAlbum subTypeAlbum2 = typeAlbum.getSubTypeAlbumList().stream()
                                    .filter(e -> e.getSubTypeId() == 10)
                                    .findFirst()
                                    .orElse(null);
                            if (Objects.nonNull(subTypeAlbum2)) {
                                HqPicDataDto.Video video2 = subTypeAlbum2.getVideoList().stream()
                                        .limit(1)
                                        .findFirst()
                                        .orElse(null);
                                if (Objects.nonNull(video2)) {
                                    videoList.add(video2);
                                }
                            }
                        }
                    }

                    SeriesBaseInfoResponse.Result.Head.Headinfo.Builder headInfo =
                            SeriesBaseInfoResponse.Result.Head.Headinfo.newBuilder();
                    SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.Builder tabInfo =
                            SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.newBuilder();

                    tabInfo.setName("座椅")
                            .setCount(hqPhotoPageDto.getItems().size() + videoList.size())
                            .setObjectid(3)
                            .setImgurl(HttpUtils.ToHttps(changeSize(ImageUtils.getFullImagePathWithoutReplace(hqPhotoPageDto.getItems().get(0).getUrl()), "150x0")))
                            .setTopcolor("#000000")
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("objectid", String.valueOf(3))
                                    .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_new_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_new_show")));
                    headInfo.setTabinfo(tabInfo);

                    for (HqPhotoDto pic : hqPhotoPageDto.getItems()) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageInfo =
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageInfo.setName(Objects.nonNull(pic.getPointName()) ? pic.getPointName() : "")
                                .setVid(Objects.nonNull(pic.getMid()) ? pic.getMid() : "")
                                .setType(0)
                                .setVideourl("")
                                .setImgurl(HttpUtils.ToHttps(changeSize(ImageUtils.getFullImagePathWithoutReplace(pic.getUrl()), "1100x0")))
                                .setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 3))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(3))
                                        .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                        .putArgvs("type", String.valueOf(0))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));
                        headInfo.addPagelist(pageInfo);
                    }
                    for (HqPicDataDto.Video video : videoList) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageInfo =
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageInfo.setName(Objects.nonNull(video.getName()) ? video.getName() : "")
                                .setVid(Objects.nonNull(video.getVid()) ? video.getVid() : "")
                                .setType(1)
                                .setVideourl("")
                                .setImgurl(HttpUtils.ToHttps(changeSize(video.getImgUrl(), "1100x0")))
                                .setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 3))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(3))
                                        .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                        .putArgvs("type", String.valueOf(1))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));
                        headInfo.addPagelist(pageInfo);
                    }

                    return headInfo;
                });
    }

    private CompletableFuture<SeriesBaseInfoResponse.Result.Head.Headinfo.Builder> buildType4HeadInfo(String tpl,
                                                                                                      SeriesDetailDto seriesDetail) {
        return hqPhotoComponent.get(seriesDetail.getId(), 0, 0, 0, 4, 0, 1, 3, 0, 0)
                .thenApplyAsync(hqPhotoPageDto -> {
                    if (Objects.isNull(hqPhotoPageDto) || CollectionUtils.isEmpty(hqPhotoPageDto.getItems())) {
                        return null;
                    }

                    SeriesBaseInfoResponse.Result.Head.Headinfo.Builder headinfo =
                            SeriesBaseInfoResponse.Result.Head.Headinfo.newBuilder();
                    SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.Builder tabinfo =
                            SeriesBaseInfoResponse.Result.Head.Headinfo.Tabinfo.newBuilder();

                    tabinfo.setName("夜景")
                            .setCount(hqPhotoPageDto.getItems().size())
                            .setObjectid(4)
                            .setImgurl(HttpUtils.ToHttps(changeSize(ImageUtils.getFullImagePathWithoutReplace(hqPhotoPageDto.getItems().get(0).getUrl()), "150x0")))
                            .setTopcolor("#000000")
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("objectid", String.valueOf(4))
                                    .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_cover_bottom_new_click"))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_cover_bottom_new_show")));
                    headinfo.setTabinfo(tabinfo);

                    for (HqPhotoDto pic : hqPhotoPageDto.getItems()) {
                        SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.Builder pageInfo =
                                SeriesBaseInfoResponse.Result.Head.Headinfo.Pageinfo.newBuilder();
                        pageInfo.setName(Objects.nonNull(pic.getPointName()) ? pic.getPointName() : "")
                                .setVid(Objects.nonNull(pic.getMid()) ? pic.getMid() : "")
                                .setType(0)
                                .setVideourl("")
                                .setImgurl(HttpUtils.ToHttps(changeSize(ImageUtils.getFullImagePathWithoutReplace(pic.getUrl()), "1100x0")))
                                .setScheme(String.format(tpl, seriesDetail.getId(), UrlUtil.encode(seriesDetail.getName()), 4))
                                .setPvitem(Pvitem.newBuilder()
                                        .putArgvs("objectid", String.valueOf(4))
                                        .putArgvs("seriesid", String.valueOf(seriesDetail.getId()))
                                        .putArgvs("type", String.valueOf(0))
                                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_pic_click"))
                                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_pic_show")));
                        headinfo.addPagelist(pageInfo);
                    }

                    return headinfo;
                });
    }

    /**
     * 二手车tab信息
     * @param seriesId 车系ID
     * @param seriesDetail 车系信息
     * @return List<SeriesBaseInfoResponse.Result.MainTab>
     */
    public CompletableFuture<List<SeriesBaseInfoResponse.Result.MainTab>> getMainTabBuilder(int seriesId, int cityId, SeriesDetailDto seriesDetail) {
        List<SeriesBaseInfoResponse.Result.MainTab> resultTabList = new ArrayList<>(2);
        boolean isStopSale = seriesDetail.getState() == 40;
        String priceInfo = CommonHelper.getPriceInfo(seriesDetail.getMinPrice());
        resultTabList.add(SeriesBaseInfoResponse.Result.MainTab.newBuilder()
                .setType(1)
                .setTabtitle("新车")
                .setTagtext(isStopSale ? "已停售" : StrPool.EMPTY)
                .setSubtitle("指导价:" + CommonHelper.priceForamtV2(seriesDetail.getMinPrice(), seriesDetail.getMaxPrice()))
                .setShortsubtitle("指导价:" + priceInfo + (seriesDetail.getMinPrice() > 0 ? "起" : StrPool.EMPTY))
                .build()
        );
        return seriesYearCityPriceComponent.getByCity(seriesId, cityId)
                .thenApply(result -> {
                    SeriesBaseInfoResponse.Result.MainTab.Builder tabBuilder = SeriesBaseInfoResponse.Result.MainTab.newBuilder()
                            .setType(2)
                            .setTabtitle("二手车")
                            .setShortsubtitle(StrPool.EMPTY);
                    tabBuilder.setTagtext("车源个数：— —");
                    tabBuilder.setSubtitle("二手车价：— —");
                    if (isStopSale && Objects.nonNull(result)) {
                        if (Objects.nonNull(result.getCityInfo()) || Objects.nonNull(result.getAll())) {
                            tabBuilder.setTagtext(String.format("全国共%d个车源", result.getAll().getCunt())).setShortsubtitle(StrPool.EMPTY);
                            if (Objects.nonNull(result.getCityInfo()) && Objects.nonNull(result.getCityInfo().getMinprice()) && result.getCityInfo().getMinprice() > 0) {
                                tabBuilder.setSubtitle(BigDecimal.valueOf(result.getCityInfo().getMinprice()).setScale(2, RoundingMode.HALF_UP) + "万起");
                            } else if (Objects.nonNull(result.getAll()) && Objects.nonNull(result.getAll().getMinprice()) && result.getAll().getMinprice() > 0) {
                                tabBuilder.setSubtitle(BigDecimal.valueOf(result.getAll().getMinprice()).setScale(2, RoundingMode.HALF_UP) + "万起");
                            }
                        }
                        resultTabList.add(tabBuilder.build());
                    }
                    return resultTabList;
                })
                .exceptionally(e -> {
                    log.warn("查询年款价格数据失败", e);
                    return null;
                });
    }



    private CompletableFuture<UsedCarDetailResult> getUsedCarDetail(String appId, int seriesId, int cityId, int provinceId, String sign) {
        return apiAutoAppShClient.seriesDetail(appId, seriesId, cityId, provinceId, sign).thenApply(detailResultModel -> {
            if (Objects.nonNull(detailResultModel) && Objects.nonNull(detailResultModel.getResult())) {
                return detailResultModel.getResult();
            }
            return null;
        }).exceptionally(e -> {
            log.warn("getMainTabBuilder error", e);
            return null;
        });
    }

    /**
     * 添加保值率糖豆
     * @param list 糖豆List
     * @param cityId 城市ID
     * @param seriesId 车系ID
     */
    private void addBzlCandy(List<SeriesBaseInfoResponse.Result.Itemlist.Data.List> list, int seriesId, int cityId) {

        // 常见问题
        Optional<SeriesBaseInfoResponse.Result.Itemlist.Data.List> askOptional = list.stream().filter(item -> item.getTypeid() == 1476514).findFirst();
        // 养车成本
        Optional<SeriesBaseInfoResponse.Result.Itemlist.Data.List> yccbOptional = list.stream().filter(item -> item.getTypeid() == 1476512).findFirst();
        int index = list.size() > 6 ? 5 : list.size() - 1;
        if (askOptional.isPresent() || yccbOptional.isPresent()) {
            index = askOptional.map(list::indexOf).orElseGet(() -> list.indexOf(yccbOptional.get()) + 1);
        }
        HashMap<String, String> argMap = new HashMap<>(5);
        argMap.put("seriesid", String.valueOf(seriesId));
        argMap.put("cityid", String.valueOf(cityId));
        argMap.put("typeid", "1010573");
        argMap.put("typename", "保值率");
        argMap.put("type", "0");
        list.add(index, SeriesBaseInfoResponse.Result.Itemlist.Data.List.newBuilder()
                .setCode("BZL")
                .setIconurl("http://nfiles3.in.autohome.com.cn/zrjcpk10/2024_10_30_bzl.png")
                .setTitle("保值率")
                .setTypeid(1010573)
                .setLinkurl(String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode(String.format("rn://CarCPKFTRRN/RetentionPage?seriesid=%d&source=2", seriesId))))
                .setScale(1)
                .setPvitem(Pvitem.newBuilder()
                        .putAllArgvs(argMap)
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_center_func_show"))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_series_center_func_click"))
                        .build())
                .build());
    }


    /**
     * 查询官降 https://doc.autohome.com.cn/docapi/page/share/share_zQTE7MgKMi
     *
     * @param request request
     * @param seriesBuilder 车系信息Response
     * @param series  车系详情
     * @return CompletableFuture<CarPriceChangeExtDto>
     */
    private CompletableFuture<String> getDownPriceInfo(SeriesBaseInfoRequest request, SeriesBaseInfoResponse.Result.Seriesbaseinfo.Builder seriesBuilder, SeriesDetailDto series) {

        return CompletableFuture.supplyAsync(() -> {
            String scheme = StrPool.EMPTY;
            // 查询车系下所有车型Future
            AtomicReference<SeriesSpecDto> seriesSpecDtoRef = new AtomicReference<>();
            List<CarPriceChangeDto.CutPriceListDTO> cutPriceList = new ArrayList<>();
            AtomicReference<SeriesAttentionDto> seriesAttentionRef = new AtomicReference<>();

            List<CompletableFuture<Void>> tasks = new ArrayList<>(3);

            tasks.add(seriesSpecComponent.getAsync(request.getSeriesid()).thenAccept(seriesSpecDtoRef::set));
            // 查询车系下降价所有车型Future
            tasks.add(CompletableFuture.runAsync(() -> {
                List<CarPriceChangeDto.CutPriceListDTO> carPriceChangeList = carPriceChangeComponent.getBySeriesId(request.getSeriesid());
                if (CollectionUtils.isNotEmpty(carPriceChangeList)) {
                    cutPriceList.addAll(carPriceChangeList);
                }
            }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
                log.warn("查询车系下降价活动错误:", e);
                return null;
            }));

            // 查询关注度
            tasks.add(seriesAttentionComponent.get(request.getSeriesid()).thenAccept(seriesAttentionRef::set));

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

            if (CollectionUtils.isNotEmpty(cutPriceList)) {
                // 所有车型的降价
                // 官降根据Apollo配置设置结束日期
                Calendar calendar = Calendar.getInstance();
                cutPriceList.forEach(cutPriceListDTO -> {
                    if (cutPriceListDTO.getChangeType() == 40) {
                        calendar.setTime(cutPriceListDTO.getStartTime());
                        calendar.add(Calendar.DAY_OF_MONTH, carPriceReduceValidity);
                        cutPriceListDTO.setEndTime(calendar.getTime());
                    }
                });
                Date now = new Date();
                // 过滤有效期内的降价活动 & 按开始时间倒序排序
                List<CarPriceChangeDto.CutPriceListDTO> finalCutPriceList = cutPriceList.stream().filter(x -> now.before(x.getEndTime()) && now.after(x.getStartTime()))
                        .sorted(Comparator.comparingInt(CarPriceChangeDto.CutPriceListDTO::getCutPrice).reversed())
                        .toList();
                // 40:官降, 50: 限时降
                Map<Integer, List<CarPriceChangeDto.CutPriceListDTO>> changeTypeListMap = finalCutPriceList.stream().collect(Collectors.groupingBy(CarPriceChangeDto.CutPriceListDTO::getChangeType));
                List<CarPriceChangeDto.CutPriceListDTO> finalTypeList;
                // 有显示降价的优先显示限时降，没有显示限时降的显示官降
                if (changeTypeListMap.containsKey(50)) {
                    finalTypeList = changeTypeListMap.get(50);
                } else {
                    finalTypeList = changeTypeListMap.getOrDefault(40, Collections.emptyList());
                }
                int typeId = -1;
                int finalTye;
                int finalSpecId = 0;
                String title = StrPool.EMPTY;
                // 有降价的才显示
                if (!finalTypeList.isEmpty()) {
                    // 按降价金额排序
                    finalTypeList.sort(Comparator.comparingInt(CarPriceChangeDto.CutPriceListDTO::getCutPrice).reversed());
                    // 最高降价
                    int maxCutPrice = finalTypeList.get(0).getCutPrice();
                    List<CarPriceChangeDto.CutPriceListDTO> maxPriceDtoList = new ArrayList<>();
                    for (CarPriceChangeDto.CutPriceListDTO cutPriceListDTO : finalTypeList) {
                        if (cutPriceListDTO.getCutPrice() == maxCutPrice) {
                            maxPriceDtoList.add(cutPriceListDTO);
                        }
                    }
                    CarPriceChangeDto.CutPriceListDTO finalTypeDto;
                    // 如果最高价只有一个车型, 则直接赋值
                    if (maxPriceDtoList.size() == 1) {
                        finalTypeDto = maxPriceDtoList.get(0);
                    } else {
                        // 若有多个最高
                        SeriesAttentionDto seriesAttentionDto = seriesAttentionRef.get();
                        if (Objects.nonNull(seriesAttentionDto) && !seriesAttentionDto.getSpecAttentions().isEmpty()) {
                            List<SeriesAttentionDto.SpecAttention> attentionList = seriesAttentionDto.getSpecAttentions();
                            // 按照关注度由高到底排序的车型ID List
                            List<Integer> maxPriceSpecIdAttentionList = attentionList.stream()
                                    .sorted(Comparator.comparingInt(SeriesAttentionDto.SpecAttention::getAttention).reversed())
                                    .map(SeriesAttentionDto.SpecAttention::getSpecid).toList();
                            int minIndex = Integer.MAX_VALUE;
                            finalTypeDto = maxPriceDtoList.get(0);
                            for (CarPriceChangeDto.CutPriceListDTO cutPriceListDTO : maxPriceDtoList) {
                                int curIndex = maxPriceSpecIdAttentionList.indexOf(cutPriceListDTO.getSpecId());
                                if (curIndex != -1 && curIndex < minIndex) {
                                    finalTypeDto = cutPriceListDTO;
                                    minIndex = curIndex;
                                }
                            }
                        } else {
                            return scheme;
                        }
                    }
                    finalTye = finalTypeDto.getChangeType();
                    finalSpecId = finalTypeDto.getSpecId();
                    // 根据类型 取 价格前文案&标签文案
                    switch (finalTye) {
                        case 40:
                            typeId = 0;
                            title = "最新指导价:";
                            break;
                        case 50:
                            typeId = 1;
                            title = "限时指导价:";
                            break;
                        default:
                            break;
                    }
                }
                int curMinPrice = Integer.MAX_VALUE;
                int curMaxPrice = Integer.MIN_VALUE;
                SeriesSpecDto seriesSpecDto = seriesSpecDtoRef.get();
                // 只过滤在售状态的车型
                List<SeriesSpecDto.Item> specList = seriesSpecDto.getItems().stream().filter(x -> Constants.ON_SALE_STATE_LIST.contains(x.getState())).toList();
                Map<Integer, CarPriceChangeDto.CutPriceListDTO> carPriceChangeMap = finalCutPriceList.stream().collect(Collectors.toMap(CarPriceChangeDto.CutPriceListDTO::getSpecId, x -> x));

                for (SeriesSpecDto.Item spec : specList) {
                    if (carPriceChangeMap.containsKey(spec.getId())) {
                        CarPriceChangeDto.CutPriceListDTO cutPriceListDTO = carPriceChangeMap.get(spec.getId());
                        if (cutPriceListDTO.getTargetPrice() > 0) {
                            // 灰色价格
                            curMinPrice = Math.min(curMinPrice, cutPriceListDTO.getTargetPrice());
                            curMaxPrice = Math.max(curMaxPrice, cutPriceListDTO.getTargetPrice());
                        }
                    } else {
                        if (spec.getMinPrice() > 0) {
                            curMinPrice = Math.min(curMinPrice, spec.getMinPrice());
                            curMaxPrice = Math.max(curMaxPrice, spec.getMinPrice());
                        }
                    }
                }
                if (typeId >= 0) {
                    scheme = "autohome://car/pricelibrary?brandid=" + series.getBrandId() + "&seriesid=" + series.getId() + "&specid=" + (finalSpecId != 0 ? finalSpecId : series.getHotSpecId()) + "&seriesname=" + UrlUtil.encode(series.getName()).replace("+", "%20") + "&tabindex=1&fromtype=1" + "&tabtype=1&sourceid=1&tabpricename=" + UrlUtil.encode("本地报价");
                    if (curMaxPrice > 0) {
                        seriesBuilder.setFctprice(PriceUtil.GetPriceStringDetail(curMinPrice, curMaxPrice, series.getState()));
                        seriesBuilder.setFctpricenamenew(title);
                    }
                }
            }
            return scheme;
        }, ThreadPoolUtils.defaultThreadPoolExecutor).exceptionally(e -> {
            log.warn("查询官降错误: ", e);
            return null;
        });
    }

}
