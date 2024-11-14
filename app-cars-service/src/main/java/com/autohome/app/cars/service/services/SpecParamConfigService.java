package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carcfg.*;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.autohome.app.cars.apiclient.baike.dtos.ConfigBaikeLinkDto;
import com.autohome.app.cars.apiclient.car.ConfigItemApiClient;
import com.autohome.app.cars.apiclient.car.dtos.ConfigItemResult;
import com.autohome.app.cars.apiclient.car.dtos.KouBeiInfoDto;
import com.autohome.app.cars.apiclient.car.dtos.SpecConfigResult;
import com.autohome.app.cars.apiclient.cms.dtos.SpecEvaluateItemResult;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.IMApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.SListAreaButtonResult;
import com.autohome.app.cars.apiclient.dealer.dtos.SpecCityCpsInfoResult;
import com.autohome.app.cars.apiclient.testdata.dtos.TestStandardResult;
import com.autohome.app.cars.apiclient.video.dtos.SpecShiCeSmallVideoResult;
import com.autohome.app.cars.apiclient.video.dtos.SpecSmallVideoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.baike.BaikelinkforconfigComponent;
import com.autohome.app.cars.service.components.baike.dtos.ConfigInfoDto;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.*;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardDataComponent;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardHotComponent;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardTagComponent;
import com.autohome.app.cars.service.components.clubcard.UserAndRzcComponent;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardData;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardDataDto;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardTagDto;
import com.autohome.app.cars.service.components.cms.SpecEvaluateComponent;
import com.autohome.app.cars.service.components.cms.dtos.SpecEvaluateDto;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiTabComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKoubeiTabDto;
import com.autohome.app.cars.service.components.video.SpecAiVideoComponent;
import com.autohome.app.cars.service.components.video.SpecConfigSmallVideoComponent;
import com.autohome.app.cars.service.components.video.SpecShiCeSmallVideoComponent;
import com.autohome.app.cars.service.components.video.dtos.SpecAiVideoDto;
import com.autohome.app.cars.service.components.video.dtos.SpecConfigSmallVideoDto;
import com.autohome.app.cars.service.components.vr.SeriesVrComponent;
import com.autohome.app.cars.service.components.vr.dtos.SeriesVr;
import com.autohome.app.cars.service.services.dtos.ParamConfigVideoInfo;
import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpecParamConfigService {

    private final static Logger logger = LoggerFactory.getLogger(SpecParamConfigService.class);
    private final static TreeMap<String, ParamConfigVideoInfo> parmconfigvideoinfo = new TreeMap<>();
    static {
        parmconfigvideoinfo.put("燃油标号", new ParamConfigVideoInfo("燃油标号", "34FEF20296E731A8", 6));
        parmconfigvideoinfo.put("供油方式", new ParamConfigVideoInfo("供油方式", "7819EA4FDCA83D8B", 6));
        parmconfigvideoinfo.put("驻车制动类型", new ParamConfigVideoInfo("驻车制动类型", "2210205ED65207ED", 6));
        parmconfigvideoinfo.put("进气形式", new ParamConfigVideoInfo("进气方式", "EF127A4540BA721C", 6));
        // parmconfigvideoinfo.put("四驱形式",new
        // ParmConfigVideoInfo("四驱形式","F10767BC0B2F7B5A",6));
        parmconfigvideoinfo.put("最大马力(Ps)", new ParamConfigVideoInfo("最大马力", "6BC427D9E070FFC2", 16));
        parmconfigvideoinfo.put("最大功率(kW)", new ParamConfigVideoInfo("最大功率", "6BC427D9E070FFC2", 16));
        parmconfigvideoinfo.put("最大扭矩(N·m)", new ParamConfigVideoInfo("最大扭矩", "6BC427D9E070FFC2", 47));
        parmconfigvideoinfo.put("驱动方式", new ParamConfigVideoInfo("驱动方式", "7D7E01E37C754293", 5));
        parmconfigvideoinfo.put("车体结构", new ParamConfigVideoInfo("车体结构", "BDF9D05B39C45BD9", 27));
        parmconfigvideoinfo.put("变速箱类型", new ParamConfigVideoInfo("变速箱类型", "DFD7C9AFFADC78D4", 11));
        parmconfigvideoinfo.put("燃料形式", new ParamConfigVideoInfo("燃料形式", "A020354BBF50A800", 12));
        parmconfigvideoinfo.put("排量(mL)", new ParamConfigVideoInfo("排量", "A020354BBF50A800", 34));
        parmconfigvideoinfo.put("气缸数(个)", new ParamConfigVideoInfo("气缸数", "A020354BBF50A800", 53));
        parmconfigvideoinfo.put("气缸排列形式", new ParamConfigVideoInfo("气缸排列形式", "A020354BBF50A800", 44));
        parmconfigvideoinfo.put("每缸气门数(个)", new ParamConfigVideoInfo("每缸气门数", "A020354BBF50A800", 65));
        parmconfigvideoinfo.put("压缩比", new ParamConfigVideoInfo("压缩比", "A020354BBF50A800", 75));
        parmconfigvideoinfo.put("前轮胎规格", new ParamConfigVideoInfo("前轮轮胎规格", "50E34F56BB808FB6", 32));
        parmconfigvideoinfo.put("后轮胎规格", new ParamConfigVideoInfo("后轮轮胎规格", "50E34F56BB808FB6", 32));
        parmconfigvideoinfo.put("备胎规格", new ParamConfigVideoInfo("备胎规格", "50E34F56BB808FB6", 20));
        parmconfigvideoinfo.put("前悬架类型", new ParamConfigVideoInfo("前悬架类型", "EB7DB50FBC223702", 6));
        parmconfigvideoinfo.put("后悬架类型", new ParamConfigVideoInfo("后悬架类型", "EB7DB50FBC223702", 6));
        parmconfigvideoinfo.put("助力类型", new ParamConfigVideoInfo("助力类型", "0B6ADAB9CC0161D1", 18));
        parmconfigvideoinfo.put("前制动器类型", new ParamConfigVideoInfo("前制动器类型", "57C703C787B9867B", 13));
        parmconfigvideoinfo.put("后制动器类型", new ParamConfigVideoInfo("后制动器类型", "57C703C787B9867B", 13));
        parmconfigvideoinfo.put("巡航系统", new ParamConfigVideoInfo("巡航系统", "32C74D2B0185D540", 11));
        // parmconfigvideoinfo.put("自适应巡航",new
        // ParmConfigVideoInfo("自适应巡航","32C74D2B0185D540",40));
        // parmconfigvideoinfo.put("全速自适应巡航",new
        // ParmConfigVideoInfo("全速自适应巡航","32C74D2B0185D540",93));
        parmconfigvideoinfo.put("车道保持辅助系统", new ParamConfigVideoInfo("车道保持辅助系统", "B3E3A84B8956DA76", 75));
        parmconfigvideoinfo.put("近光灯光源", new ParamConfigVideoInfo("近灯光光源", "B0BED8F0CCEDA863", 11));
        parmconfigvideoinfo.put("远光灯光源", new ParamConfigVideoInfo("远光灯光源", "B0BED8F0CCEDA863", 11));
        parmconfigvideoinfo.put("手机互联/映射", new ParamConfigVideoInfo("手机互联/映射", "DC41524164C92533", 19));
        parmconfigvideoinfo.put("限滑差速器/差速锁", new ParamConfigVideoInfo("限滑差速器/差速锁", "B2462C5897F9132E", 12));
        parmconfigvideoinfo.put("可变悬架功能", new ParamConfigVideoInfo("可变悬架功能", "38A22C57DB5007E2", 15));
        parmconfigvideoinfo.put("空气悬架", new ParamConfigVideoInfo("空气悬架", "38A22C57DB5007E2", 28));
        parmconfigvideoinfo.put("电磁感应悬架", new ParamConfigVideoInfo("电磁感应悬架", "38A22C57DB5007E2", 44));
        parmconfigvideoinfo.put("主动降噪", new ParamConfigVideoInfo("主动降噪", "5B7F3E721F99A606", 15));
        parmconfigvideoinfo.put("远程启动功能", new ParamConfigVideoInfo("远程启动", "AE2C70F660993824", 16));
        parmconfigvideoinfo.put("自适应远近光", new ParamConfigVideoInfo("自适应远近光", "D3E77300E2A0C8F5", 80));
        parmconfigvideoinfo.put("感应后备厢", new ParamConfigVideoInfo("感应后备厢", "81DC2B052F755FDA", 63));
        parmconfigvideoinfo.put("整体主动转向系统", new ParamConfigVideoInfo("整体主动转向系统", "6357314F12A13D8F", 18));
        parmconfigvideoinfo.put("主动刹车/主动安全系统", new ParamConfigVideoInfo("主动刹车/主动安全系统", "8A67E81D4B4F936D", 25));
        parmconfigvideoinfo.put("车道偏离预警系统", new ParamConfigVideoInfo("车道偏离预警", "B3E3A84B8956DA76", 37));
        parmconfigvideoinfo.put("并线辅助", new ParamConfigVideoInfo("并线辅助", "B3E3A84B8956DA76", 11));
        parmconfigvideoinfo.put("发动机启停技术", new ParamConfigVideoInfo("发动机启停技术", "EB87110D10A9DFBE", 15));
        parmconfigvideoinfo.put("自动泊车入位", new ParamConfigVideoInfo("自动泊车入位", "C27B996E6036F731", 11));
        parmconfigvideoinfo.put("转向头灯", new ParamConfigVideoInfo("转向头灯", "D3E77300E2A0C8F5", 45));
        parmconfigvideoinfo.put("转向辅助灯", new ParamConfigVideoInfo("转向辅助灯", "D3E77300E2A0C8F5", 17));
        parmconfigvideoinfo.put("HUD抬头数字显示", new ParamConfigVideoInfo("HUD抬头数字显示", "EA4291DD6E8032E1", 23));
        parmconfigvideoinfo.put("电动后备厢", new ParamConfigVideoInfo("电动后备厢", "81DC2B052F755FDA", 14));
        parmconfigvideoinfo.put("前排座椅功能", new ParamConfigVideoInfo("前排座椅功能", "38BBD9C55C666A2E", 6));
        parmconfigvideoinfo.put("陡坡缓降", new ParamConfigVideoInfo("陡坡缓降", "93AE11ADAE5241C4", 53));
        parmconfigvideoinfo.put("自动驻车", new ParamConfigVideoInfo("自动驻车", "2210205ED65207ED", 51));
        parmconfigvideoinfo.put("上坡辅助", new ParamConfigVideoInfo("上坡辅助", "93AE11ADAE5241C4", 15));
        parmconfigvideoinfo.put("无钥匙进入功能", new ParamConfigVideoInfo("无钥匙进入系统", "5BCC963988E3F107", 15));
        parmconfigvideoinfo.put("无钥匙启动系统", new ParamConfigVideoInfo("无钥匙启动系统", "5BCC963988E3F107", 44));
        parmconfigvideoinfo.put("ISOFIX儿童座椅接口", new ParamConfigVideoInfo("ISOFIX儿童座椅接口", "E7F0D1D72E39ACB9", 13));
        parmconfigvideoinfo.put("零胎压继续行驶", new ParamConfigVideoInfo("零胎压继续行驶", "7319A7BD46E02426", 77));
        parmconfigvideoinfo.put("胎压监测功能", new ParamConfigVideoInfo("胎压监测装置", "7319A7BD46E02426", 23));
        // parmconfigvideoinfo.put("流媒体后视镜",new
        // ParmConfigVideoInfo("流媒体后视镜","65C67DBE7BA427E2",6));
        parmconfigvideoinfo.put("内后视镜功能", new ParamConfigVideoInfo("内后视镜功能", "8BAD56D571FE1A6A", 6));
        parmconfigvideoinfo.put("主/副驾驶座安全气囊", new ParamConfigVideoInfo("主/副驾驶座安全气囊", "3143F1B75C62BEBE", 35));
        parmconfigvideoinfo.put("前/后排侧气囊", new ParamConfigVideoInfo("前/后排安全气囊", "3143F1B75C62BEBE", 48));
        parmconfigvideoinfo.put("前/后排头部气囊(气帘)", new ParamConfigVideoInfo("前/后前/后排头部气囊(气帘)", "3143F1B75C62BEBE", 55));
        parmconfigvideoinfo.put("膝部气囊", new ParamConfigVideoInfo("膝部气囊", "3143F1B75C62BEBE", 70));
        // parmconfigvideoinfo.put("后排中央安全气囊",new
        // ParmConfigVideoInfo("后排中央安全气囊","3143F1B75C62BEBE",109));
        parmconfigvideoinfo.put("ABS防抱死", new ParamConfigVideoInfo("ABS防抱死", "E252C132D63F3C1E", 22));
        parmconfigvideoinfo.put("制动力分配(EBD/CBC等)", new ParamConfigVideoInfo("制动力分配(EBD/CBC等)", "E252C132D63F3C1E", 41));
        parmconfigvideoinfo.put("刹车辅助(EBA/BAS/BA等)",
                new ParamConfigVideoInfo("刹车辅助(EBA/BAS/BA等)", "E252C132D63F3C1E", 67));
        parmconfigvideoinfo.put("牵引力控制(ASR/TCS/TRC等)",
                new ParamConfigVideoInfo("牵引力控制(ASR/TCS/TRC等)", "E252C132D63F3C1E", 84));
        parmconfigvideoinfo.put("车身稳定控制(ESC/ESP/DSC等)",
                new ParamConfigVideoInfo("车身稳定控制(ESC/ESP/DSC等)", "E252C132D63F3C1E", 112));
        parmconfigvideoinfo.put("涉水感应系统", new ParamConfigVideoInfo("涉水感应系统", "0243E0ED5D06B36B", 10));
        parmconfigvideoinfo.put("全液晶仪表盘", new ParamConfigVideoInfo("全液晶仪表盘", "2CF0B114770326F4", 13));
        parmconfigvideoinfo.put("道路交通标识识别", new ParamConfigVideoInfo("道路交通标示识别", "F0DFC82C3C1E1E40", 19));
        parmconfigvideoinfo.put("天窗类型", new ParamConfigVideoInfo("天窗类型", "72CEB7518B3F5039", 6));
        parmconfigvideoinfo.put("感应雨刷功能", new ParamConfigVideoInfo("感应雨刷功能", "D4B3382669148133", 6));
        parmconfigvideoinfo.put("主座椅调节方式", new ParamConfigVideoInfo("主座椅调节方式", "115BB005DB3BA48A", 6));
        parmconfigvideoinfo.put("副座椅调节方式", new ParamConfigVideoInfo("副座椅调节方式", "115BB005DB3BA48A", 6));
        parmconfigvideoinfo.put("电动吸合车门", new ParamConfigVideoInfo("电动吸合车门", "5D89483FF92603FF", 19));
        parmconfigvideoinfo.put("被动行人保护", new ParamConfigVideoInfo("行人保护", "8F72525A98F7A6D3", 11));
        parmconfigvideoinfo.put("自动驾驶技术", new ParamConfigVideoInfo("自动驾驶技术", "14CEC66C60037AC8", 6));
        parmconfigvideoinfo.put("多层隔音玻璃", new ParamConfigVideoInfo("多层隔音玻璃", "DC5606D5B35027CE", 12));
        parmconfigvideoinfo.put("侧滑门形式", new ParamConfigVideoInfo("侧滑门行驶", "906A8B0FE20F85C6", 6));
    }

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;
    @Autowired
    private SpecYearNewComponent specYearNewComponent;
    @Autowired
    private SpecDetailComponent specDetailComponent;
    @Autowired
    private SpecParamInfoComponent specParamInfoComponent;
    @Autowired
    private SpecConfigInfoComponent specConfigInfoComponent;
    @Autowired
    private BaikelinkforconfigComponent baikelinkforconfigComponent;
    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;
    @Autowired
    private SpecConfigSmallVideoComponent specConfigSmallVideoComponent;
    @Autowired
    private SpecAiVideoComponent specAiVideoComponent;
    @Autowired
    private SpecShiCeSmallVideoComponent specShiCeSmallVideoComponent;
    @Autowired
    private SpecParamConfigPicInfoComponent specParamConfigPicInfoComponent;
    @Autowired
    private SpecColorComponent specColorComponent;
    @Autowired
    private SpecOutInnerColorComponent specOutInnerColorComponent;
    @Autowired
    private SeriesVrComponent seriesVrComponent;
    @Autowired
    private SpecSpecialConfigComponent specSpecificConfigComponent;
    @Autowired
    private SeriesConfigDiffComponent seriesConfigDiffComponent;
    @Autowired
    private SpecConfigBagComponent specConfigBagComponent;
    @Autowired
    private SpecEvaluateComponent specEvaluateComponent;
    @Autowired
    private SpecTestDataComponent specTestDataComponent;
    @Autowired
    IMApiClient imApiClient;
    @Autowired
    private ConfigItemApiClient configItemApiClient;
    @Autowired
    private DealerApiClient dealerApiClient;
    @Value("${baike_info_config:}")
    private String baiKeInFoConfig;
    @Value("${koubei_limit_count:0}")
    private int kouBeiLimitCount;
    @Autowired
    UserAndRzcComponent userAndRzcComponent;


    public GetSpecParamConfigInfoResponse getSpecParamConfigInfo(GetSpecParamConfigInfoRequest request) {
        try {
            int site = request.getSite();
            int seriesId = request.getSeriesid();
            String specIds = request.getSpecids();

            long s = System.currentTimeMillis();
            Map<String,Long> ts = new LinkedHashMap<>();

            SpecDetailDto specDto =null;
            List<SpecGroupOfSeriesDto> specYearList = new ArrayList<>();
            //车型入口 取车系id
            if(site==2){
                specDto = specDetailComponent.get(Integer.valueOf(specIds)).join();
                if(Objects.isNull(specDto)){
                    return GetSpecParamConfigInfoResponse.newBuilder()
                            .setReturnCode(0)
                            .setReturnMsg("车型获取失败")
                            .build();
                }
                seriesId = specDto.getSeriesId();
            }
            ts.put("1",System.currentTimeMillis() -s);
            //车型、车系入口 筛选项数据使用
            if(site==1 || site==2){
                specYearList.addAll(specYearNewComponent.get(seriesId));
            }
            List<SpecDetailDto> specDetailList = getSpecDetailData(request,specYearList,specDto);
            ts.put("2",System.currentTimeMillis() -s);
            List<String> seriesIdList = specDetailList.stream().map(x -> String.valueOf(x.getSeriesId())).distinct().collect(Collectors.toList());
            List<Integer> specIdList = specDetailList.stream().map(SpecDetailDto::getSpecId).collect(Collectors.toList());

            ts.put("4",System.currentTimeMillis() -s);
            if (ListUtil.isEmpty(specIdList)) {
                return GetSpecParamConfigInfoResponse.newBuilder()
                        .setReturnCode(0)
                        .setReturnMsg("无参配外显车型")
                        .build();
            }

            GetSpecParamConfigInfoResponse.Result.Builder resultBuilder = GetSpecParamConfigInfoResponse.Result.newBuilder();
            resultBuilder.setSeriesids(String.join(",", seriesIdList));

            List<CompletableFuture> tasks = new ArrayList<>();

            tasks.add(buildDatalist(request, resultBuilder, specYearList, specDetailList)
                    .thenAccept(resultBuilder::addAllDatalist).thenAccept((x)->ts.put("5",System.currentTimeMillis() -s))
                    .exceptionally(e -> {
                        logger.error("车型参配接口异常-buildDatalist error: {}", ExceptionUtils.getStackTrace(e));
                        return null;
                    }));
            tasks.add(buildMustSeeList(request)
                    .thenAccept(resultBuilder::addAllMustseelist).thenAccept((x)->ts.put("6",System.currentTimeMillis() -s))
                    .exceptionally(e -> {
                        logger.error("车型参配接口异常-buildMustSeeList error: {}", ExceptionUtils.getStackTrace(e));
                        return null;
                    }));
            tasks.add(buildAttentionSpecInfo(request,seriesId)
                    .thenAccept(resultBuilder::setAttentionspecinfo).thenAccept((x)->ts.put("7",System.currentTimeMillis() -s))
                    .exceptionally(e -> {
                        logger.error("参配接口异常-buildAttentionSpecInfo error: {}", ExceptionUtils.getStackTrace(e));
                        return null;
                    }));
            //车系参配 设置工具箱
            if(request.getSite() == 1){
                tasks.add(buildToolboxEntry(request, specDetailList)
                        .thenAccept(resultBuilder::setToolboxentry).thenAccept((x)->ts.put("8",System.currentTimeMillis() -s))
                        .exceptionally(e -> {
                            logger.error("参配接口异常-buildToolboxEntry error: {}", ExceptionUtils.getStackTrace(e));
                            return null;
                        }));
            }
            if (request.getSite() == 2) {
                tasks.add(buildCpsinfo(request)
                        .thenAccept(resultBuilder::setCpsinfo).thenAccept((x)->ts.put("9",System.currentTimeMillis() -s))
                        .exceptionally(e -> {
                            logger.error("参配接口异常-buildSpecCpsinfo error: {}", ExceptionUtils.getStackTrace(e));
                            return null;
                        }));
            }
            if (request.getSite() == 2 || request.getSite() == 3) {
                tasks.add(buildDeleteSpecInfo(request, specDetailList, resultBuilder).thenAccept((x)->ts.put("10",System.currentTimeMillis() -s))
                        .exceptionally(e -> {
                            logger.error("参配接口异常-buildDeleteSpecInfo error: {}", ExceptionUtils.getStackTrace(e));
                            return null;
                        }));
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            ts.put("11",System.currentTimeMillis() -s);
            if(System.currentTimeMillis() -s > 250) {
                StringBuilder sb = new StringBuilder();
                ts.forEach((k, v) -> {
                    sb.append("，" + k + ":" + v);
                });
                log.warn("参数配置超时"+sb.toString());
            }
            resultBuilder.addAllTitlelist(buildTitleList(resultBuilder));
            return GetSpecParamConfigInfoResponse.newBuilder()
                    .setReturnCode(0)
                    .setReturnMsg("success")
                    .setResult(resultBuilder)
                    .build();
        } catch (Exception e) {
            logger.error("参配接口异常-getSpecParamConfigInfo error: {}", ExceptionUtils.getStackTrace(e));
            return GetSpecParamConfigInfoResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("服务端错误")
                    .build();
        }
    }

    public GetBaiKeInfoResponse getConfigBaikeInfo(GetBaiKeInfoRequest request) {
        GetBaiKeInfoResponse.Builder result_builder = GetBaiKeInfoResponse.newBuilder();
        GetBaiKeInfoResponse.ConfigWikiResult.Builder builder = GetBaiKeInfoResponse.ConfigWikiResult.newBuilder();
        CompletableFuture<List<ConfigBaikeLinkDto>> baiKeFuture = CompletableFuture.supplyAsync(() -> baikelinkforconfigComponent.getList(), ThreadPoolUtils.defaultThreadPoolExecutor);//获取参配对应的百科内容
        CompletableFuture<SpecConfigSmallVideoDto> configVideoFuture = specConfigSmallVideoComponent.get(request.getSpecid());//获取参配实拍视频
        CompletableFuture<SpecConfigInfoDto> configInfoFuture = specConfigInfoComponent.get(request.getSpecid());//获取参配差异配置图
        CompletableFuture<SpecParamConfigPicInfoDto> paramFuture = specParamConfigPicInfoComponent.get(request.getSpecid());//获取参配实拍图
        //获取口碑数据
        CompletableFuture<BaseModel<List<KouBeiInfoDto>>> kouBeiFuture = configItemApiClient.getKouBeiInfo(request.getSeriesid(), request.getSpecid(), request.getItemid(), request.getYear(), request.getSubitemid());
        //获取用户数据
        CompletableFuture.allOf(baiKeFuture, configVideoFuture, configInfoFuture,paramFuture,kouBeiFuture)
                .thenAccept(x -> {
                    List<ConfigBaikeLinkDto> baiKeDto = baiKeFuture.join();
                    SpecConfigSmallVideoDto videoDto = configVideoFuture.join();
                    SpecConfigInfoDto configInfoDto = configInfoFuture.join();
                    SpecParamConfigPicInfoDto paramDto = paramFuture.join();
                    BaseModel<List<KouBeiInfoDto>> kouBeiInfo = kouBeiFuture.join();
                    processResult(result_builder, builder, baiKeDto, videoDto, configInfoDto,paramDto,kouBeiInfo,request.getConfigid(), request.getVideoid(), request.getItemid(),request.getSpecid(),request);
                })
                .exceptionally(ex -> {
                    log.error("百科浮层接口getbaikeinfo异常：{}", ex);
                    return null;
                })
                .join();
        return result_builder.setResult(builder).build();
    }

    private void processResult(GetBaiKeInfoResponse.Builder resultBuilder, GetBaiKeInfoResponse.ConfigWikiResult.Builder builder, List<ConfigBaikeLinkDto> baiKeDto, SpecConfigSmallVideoDto videoDto,SpecConfigInfoDto configInfoDto, SpecParamConfigPicInfoDto paramDto,BaseModel<List<KouBeiInfoDto>> kouBeiInfo,
                               Integer configId,String videoId, Integer itemId,Integer specId,GetBaiKeInfoRequest request) {
        Optional<ConfigBaikeLinkDto> configOpt = Optional.of(new ConfigBaikeLinkDto());
        if (baiKeDto != null){
            configOpt = baiKeDto.stream()
                    .filter(dto -> Objects.equals(dto.getId(), configId))
                    .findFirst();
            //content：百科内容
            configOpt.ifPresent(opt ->
                    builder.setContent(opt.getFirstpartcnt())
            );
        }
        List<GetBaiKeInfoResponse.KouBeiInfo> kouBeiInfoList = new ArrayList<>();

        //口碑数据需大于3条
        if (kouBeiInfo != null && !CollectionUtils.isEmpty(kouBeiInfo.getResult()) && kouBeiInfo.getResult().size() >= 3 && kouBeiLimitCount > 0){
            List<String> userIdS = kouBeiInfo.getResult().stream().map(KouBeiInfoDto::getUserId).toList();
            List<Integer> specIds = kouBeiInfo.getResult().stream().map(KouBeiInfoDto::getSpecId).toList();
            String user_ids = String.join(",", userIdS);
            Map<Integer, GetBaiKeInfoResponse.UserInfo> userInfoMap = userAndRzcComponent.setUserConfigKouBeiInfo(user_ids);
            Map<Integer, String> specMap = new HashMap<>();//key:specId、value：specName
            specDetailComponent.getList(specIds)
                    .thenAccept(res -> {
                        if (!CollectionUtils.isEmpty(res)){
                            for (SpecDetailDto specDto : res) {
                                specMap.put(specDto.getSpecId(),specDto.getSpecName());
                            }
                        }
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        log.error("specDetailComponent异常:{}", e.getMessage());
                        return null;
                    })
                    .join();
            for (int i = 0; i < Math.min(kouBeiLimitCount,kouBeiInfo.getResult().size()); i++) {
                GetBaiKeInfoResponse.KouBeiInfo.Builder kouBei_builder = GetBaiKeInfoResponse.KouBeiInfo.newBuilder();
                KouBeiInfoDto info = kouBeiInfo.getResult().get(i);
                //填充口碑内容
                String specName = specMap.get(info.getSpecId());
                GetBaiKeInfoResponse.UserInfo userInfo = userInfoMap.get(Integer.parseInt(info.getUserId()));
                GetBaiKeInfoResponse.UserInfo newUserInfo = GetBaiKeInfoResponse.UserInfo.newBuilder()
                        .setAvatar(userInfo.getAvatar())
                        .setUsername(userInfo.getUsername())
//                        .setCarimage(userInfo.getCarimage())产品觉得体验连贯性差，不展示用户的认证车信息和认证车对应的品牌logo
//                        .setIdentity(userInfo.getIdentity())
                        .setSpecname(StringUtils.isNotEmpty(specName) ? specName : "")
                        .build();
                Pvitem kouBeiPvitem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("config_explain_koubei_click"))
                        .setShow(Pvitem.Show.newBuilder().setEventid("config_explain_koubei_show"))
                        .putArgvs("typeid",request.getSite()+"")//1车系、2车型、3对比
                        .putArgvs("seriesid", info.getSeriesId() + "")
                        .putArgvs("specid", info.getSpecId() + "")
                        .putArgvs("configid", info.getConfigId() + "")//参配id
                        .putArgvs("koubeiid",info.getKoubeiId()+"")//口碑id
                        .build();
                kouBei_builder.setContent(GetBaiKeInfoResponse.KouBeiContent
                                .newBuilder()
                                .setText(info.getLongContent())
                                .setHightext(info.getShortContent())
                                .setPubtime(convertDateFormat(info.getCreated()))
                                .setArticletitle("《" + info.getFeelingSummary() + "》")
                                .build())
                        .setUserinfo(newUserInfo)
                        .setScheme(info.getAppScheme())
                        .setPvitem(kouBeiPvitem);
                kouBeiInfoList.add(kouBei_builder.build());
            }
            String more_scheme = "autohome://reputation/reputationlist?seriesid=%d&koubeifromkey=51";//"查看更多"协议
            Pvitem more_pvItem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("config_explain_koubeimore_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("config_explain_koubeimore_show"))
                    .putArgvs("typeid",request.getSite()+"")//1车系、2车型、3对比
                    .putArgvs("seriesid", request.getSeriesid() + "")
                    .putArgvs("specid", request.getSpecid() + "")
                    .putArgvs("configid", request.getItemid() + "")
                    .build();
            builder.setListheader(GetBaiKeInfoResponse.ListHeader
                            .newBuilder()
                            .setTitle("车主口碑评价"))
                    .setListfooter(GetBaiKeInfoResponse.ListFooter
                            .newBuilder()
                            .setTitle("查看更多")
                            .setScheme(String.format(more_scheme, request.getSeriesid()))
                            .setPvitem(more_pvItem));
        }
        builder.addAllList(kouBeiInfoList);

        /**
         * 优先级：配置视频>百科视频>配置项图片
         */
        //配置视频
        if (videoDto != null && !CollectionUtils.isEmpty(videoDto.getVideoInfoMap())) {
            Optional<SpecSmallVideoResult.ResultBean.VideolistBean> opt = videoDto.getVideoInfoMap().values().stream()
                    .filter(info -> info.getMediaid().equals(videoId))
                    .findFirst();
            builder.setVideoid(videoId);
            builder.setVideopic(opt.isPresent() ? ImageUtils.convertImageUrl(opt.get().getVideoimg169(),true,false,false) : "");
            if (StringUtils.isNotEmpty(videoId) && StringUtils.isNotEmpty(builder.getVideopic())) {
                return;
            }
        }

        //百科视频
        if (configOpt.isPresent() && StringUtils.isNotEmpty(configOpt.get().getMid())){
            builder.setVideoid((configOpt.get().getMid()));
            builder.setVideopic(ImageUtils.convertImageUrl(configOpt.get().getVideocover(),true,false,false,ImageSizeEnum.ImgSize_16x9_640x360));
            if (StringUtils.isNotEmpty(builder.getVideoid()) && StringUtils.isNotEmpty(builder.getVideopic())){
                return;
            }
        }

        //实拍图
        if (paramDto != null && !CollectionUtils.isEmpty(paramDto.getList())){
            Optional<SpecParamConfigPicTipDto> opt = paramDto.getList().stream()
                    .filter(info -> info.getDatatype() == 2 && Objects.equals(info.getItemid(), itemId))
                    .findFirst();
            builder.setImageurl(opt.isPresent() ? ImageUtils.convertImageUrl(opt.get().getPicurl(),true,false,false,ImageSizeEnum.ImgSize_4x3_800x600_Without_Opt) : "");
            if (StringUtils.isNotEmpty(builder.getImageurl())){
                return;
            }
        }

        if (configInfoDto != null && !CollectionUtils.isEmpty(configInfoDto.getConfigtypeitems())){
            for (SpecConfigResult.Configtypeitems configtypeitem : configInfoDto.getConfigtypeitems()) {
                Optional<SpecConfigResult.Configitems> opt = configtypeitem.getConfigitems().stream()
                        .filter(item -> Objects.equals(item.getConfigid(), itemId))
                        .findFirst();
                if (opt.isPresent()) {
                    SpecConfigResult.Configitems configitems = opt.get();
                    String imageUrl = configitems.getLogo();
                    if (StringUtils.isNotEmpty(imageUrl)){
                        //二级配置项有图片则取二级配置项图片并返回
                        builder.setImageurl(ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(imageUrl),true,false,false,ImageSizeEnum.ImgSize_4x3_800x600_Without_Opt));
                        return;
                    }
                    //没有二级配置项图片，则取二级配置项的第一个子项的图片
                    Optional<SpecConfigResult.Valueitems> valueOpt = configitems.getValueitems().stream()
                            .filter(item -> Objects.equals(item.getSpecid(), specId))
                            .findFirst();
                    valueOpt.ifPresent(value -> {
                        if (!CollectionUtils.isEmpty(value.getSublist())){
                            SpecConfigResult.Sublist sublist = value.getSublist().get(0);
                            builder.setImageurl(ImageUtils.convertImageUrl(CarSettings.getInstance().GetFullImagePath(sublist.getLogo()),true,false,false,ImageSizeEnum.ImgSize_4x3_800x600_Without_Opt));
                        }
                    });
                }
            }
        }
    }

    private List<SpecDetailDto> getSpecDetailData(GetSpecParamConfigInfoRequest request, List<SpecGroupOfSeriesDto> specYearList, SpecDetailDto specDto) {
        List<SpecDetailDto> specDetailList = new ArrayList<>();
        List<SpecGroupOfSeriesDto.Spec> specList = new ArrayList<>();
        try {
            //车系来源 site=1
            if (request.getSite() == 1 && specYearList != null) {
                Optional<SpecGroupOfSeriesDto> onSaleOpt = specYearList.stream().filter(item -> "在售".equals(item.getYearname())).findFirst();
                Optional<SpecGroupOfSeriesDto> unSaleOpt = specYearList.stream().filter(item -> "即将销售".equals(item.getYearname())).findFirst();
                List<SpecGroupOfSeriesDto.Spec> list = new ArrayList<>();
                if (request.getYear() > 0) {
                    Optional<SpecGroupOfSeriesDto> yearOpt = specYearList.stream().filter(i -> request.getYear() == i.getYearvalue()).findFirst();
                    if (yearOpt.isPresent()) {
                        yearOpt.get().getYearspeclist().forEach(i -> list.addAll(i.getSpeclist().stream().filter(spec -> spec.getParamIsShow() == 1 && spec.getState()==40).collect(Collectors.toList())));
                    }
                } else {
                    if (onSaleOpt.isPresent()) {
                        onSaleOpt.get().getYearspeclist().forEach(i -> list.addAll(i.getSpeclist().stream().filter(spec -> spec.getParamIsShow() == 1).collect(Collectors.toList())));
                    }
                    if (unSaleOpt.isPresent()) {
                        unSaleOpt.get().getYearspeclist().forEach(i -> list.addAll(i.getSpeclist().stream().filter(spec -> spec.getParamIsShow() == 1).collect(Collectors.toList())));
                    }
                    if (list.isEmpty()) {
                        specYearList.stream().filter(year -> year.getYearstate() == 40).collect(Collectors.toList()).forEach(year -> {
                            year.getYearspeclist().forEach(i -> list.addAll(i.getSpeclist().stream().filter(spec -> spec.getParamIsShow() == 1).collect(Collectors.toList())));
                        });
                    }
                }
                specList.addAll(list);
            } else if (request.getSite() == 2) {
                //车型来源 site=2
                if (request.getYear() > 0) {
                    Optional<SpecGroupOfSeriesDto> yearOpt = specYearList.stream().filter(i -> request.getYear() == i.getYearvalue()).findFirst();
                    if (yearOpt.isPresent()) {
                        yearOpt.get().getYearspeclist().forEach(i -> specList.addAll(i.getSpeclist().stream().filter(spec -> spec.getParamIsShow() == 1).collect(Collectors.toList())));
                    }
                }else if (Objects.nonNull(specDto) && specDto.getParamIsShow() == 1) {
                    specDetailList.add(specDto);
                }
            } else if (request.getSite() == 3) {
                //pk来源 site=3
                List<Integer> ids = Arrays.asList(request.getSpecids().split(",")).stream().distinct().map(i -> Integer.valueOf(i)).collect(Collectors.toList());
                //pk车型数量限制9
                if (ids.size() > 9) {
                    ids = ids.subList(0, 9);
                }
                List<SpecDetailDto> specDetailDtos = specDetailComponent.mGet(ids);
                specDetailDtos = specDetailDtos.stream().filter(i -> i != null && i.getParamIsShow() == 1).collect(Collectors.toList());
                specDetailList.addAll(specDetailDtos);
            }
            if(!specList.isEmpty()){
                List<Integer> specIdList = specList.stream().map(item -> item.getSpecId()).collect(Collectors.toList());
                specDetailList.addAll(specDetailComponent.mGet(specIdList));
            }
        } catch (Exception e) {
            log.error("参配车型列表数据异常, request={},specYearList={},specDt{}", request, specYearList, specDto, e);
        }
        specDetailList.removeIf(Objects::isNull);
        return specDetailList;
    }

    /**
     * CPS信息
     *
     * @param request
     * @return
     */
    private CompletableFuture<Cpsinfo> buildCpsinfo(GetSpecParamConfigInfoRequest request) {
        int specId = NumberUtils.toInt(request.getSpecids());
        int cityId = request.getCityid();
        Cpsinfo.Builder cpsinfoBuilder = Cpsinfo.newBuilder();
        return dealerApiClient.getSpecCityCpsInfo(cityId, String.valueOf(specId)).thenApply(result -> {
            if (result != null && result.getResult() != null && result.getResult().size() > 0) {
                SpecCityCpsInfoResult source = result.getResult().get(0);
                // 1：库存版（家家特价） 2：厂商版（红包）
                String flbnb = "";
                // 产品确认 source.getProductType() == 1的逻辑已经没有了
//                if(source.getProductType() == 1){
//                    flbnb = "6837594";
//                    cpsinfoBuilder.setTypeid(6837594);
//                    cpsinfoBuilder.setTitle(source.getDealerBtnMainTitle());
//                }
                    if (source.getProductType() == 2) {
                    flbnb = "6837595";
                    cpsinfoBuilder.setTypeid(6837595);
                    cpsinfoBuilder.setTitle(source.getFacBtnMainTitle());
                }
                String linkurl = UrlUtil.getInsideBrowerSchemeWK(source.getOriginalJumpUrl() + "&flbnb=" + flbnb);
                cpsinfoBuilder.setLinkurl(linkurl);
                return cpsinfoBuilder.build();
            }
            return cpsinfoBuilder.build();
        });
    }

    /**
     * 删除的车型信息
     *
     * @param request
     * @param specDetailDtos
     * @param resultBuilder
     * @return
     */
    private CompletableFuture<Void> buildDeleteSpecInfo(GetSpecParamConfigInfoRequest request, List<SpecDetailDto> specDetailDtos, GetSpecParamConfigInfoResponse.Result.Builder resultBuilder) {
        return CompletableFuture.runAsync(() -> {
            if (request.getSite() == 2 || request.getSite() == 3) {
                List<Integer> specIdList = specDetailDtos.stream().map(SpecDetailDto::getSpecId).collect(Collectors.toList());
                String[] ids = request.getSpecids().split(",");
                List<Integer> idList = Arrays.asList(ids).stream().map(i -> Integer.valueOf(i)).collect(Collectors.toList());
                idList.removeIf(id -> specIdList.contains(id));
                if (ListUtil.isNotEmpty(idList)) {
                    List<SpecDetailDto> delSpecDetailDtos = specDetailComponent.mGet(idList);
                    delSpecDetailDtos.removeIf(s -> s == null);
                    if (ListUtil.isNotEmpty(delSpecDetailDtos)) {
                        List<String> deleteSpecIds = delSpecDetailDtos.stream().map(x -> String.valueOf(x.getSpecId())).collect(Collectors.toList());
                        List<String> deleteSpecNames = delSpecDetailDtos.stream().map(x -> x.getSeriesName() + " " + x.getSpecName()).collect(Collectors.toList());
                        resultBuilder.setDeletespecids(String.join(",", deleteSpecIds));
                        resultBuilder.setDeletetip(String.join(",", deleteSpecNames));
                    }
                }
            }
        });
    }

    /**
     * 构建必参配项列表
     *
     * @param request
     * @return
     */
    private CompletableFuture<List<Mustseelist>> buildMustSeeList(GetSpecParamConfigInfoRequest request) {
        if (request.getSite() == 1 && request.getSeriesid() > 0) {
            return CompletableFuture.supplyAsync(() -> seriesDetailComponent.get(request.getSeriesid())).thenApply(seriesInfo -> {
                List<Mustseelist> mustSeeList = new ArrayList<>();
                if (seriesInfo != null) {
                    List<MustSeeItemDto> mustSeeItemDtos = new ArrayList<>();
                    //级别判断
                    if (Arrays.asList(1, 2, 3, 4, 5, 6, 7, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24).contains(seriesInfo.getLevelId())) {
                        mustSeeItemDtos.add(new MustSeeItemDto("基本参数", "长*宽*高(mm)"));//长*宽*高(mm)-参数项
                        mustSeeItemDtos.add(new MustSeeItemDto("车身", "轴距(mm)"));//轴距(mm)-参数项
                        mustSeeItemDtos.add(new MustSeeItemDto("基本参数", "能源类型"));//能源类型-参数项，注意：产品强制将“能源类型”归类到“基本参数”下
                        mustSeeItemDtos.add(new MustSeeItemDto("基本参数", "官方0-100km/h加速(s)"));//官方0-100km/h加速(s)-参数项
                        mustSeeItemDtos.add(new MustSeeItemDto("车身", "车身结构"));//车身结构-参数项
                        mustSeeItemDtos.add(new MustSeeItemDto("基本参数", "整车质保"));//整车质保-参数项
                        mustSeeItemDtos.add(new MustSeeItemDto("被动安全", "主/副驾驶座安全气囊"));//主/副驾驶座安全气囊-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("被动安全", "前/后排侧气囊"));//前/后排侧气囊-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("被动安全", "前/后排头部气囊(气帘)"));//前/后排头部气囊(气帘)-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("被动安全", "膝部气囊"));//膝部气囊-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("被动安全", "车身稳定控制(ESC/ESP/DSC等)"));//车身稳定控制(ESC/ESP/DSC等)-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("主动安全", "车道偏离预警系统"));//车道偏离预警系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("主动安全", "主动刹车/主动安全系统"));//主动刹车/主动安全系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("主动安全", "疲劳驾驶提示"));//疲劳驾驶提示-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("主动安全", "前方碰撞预警"));//前方碰撞预警-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("主动安全", "后方碰撞预警"));//后方碰撞预警-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "驾驶模式切换"));//驾驶模式切换-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "自动驻车"));//自动驻车-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "空气悬架"));//空气悬架-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶硬件", "前/后驻车雷达"));//前/后驻车雷达-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶硬件", "驾驶辅助影像"));//驾驶辅助影像-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶功能", "巡航系统"));//巡航系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶功能", "辅助驾驶系统"));//辅助驾驶系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶功能", "辅助驾驶等级"));//辅助驾驶等级-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("外观/防盗", "电动后备厢"));//电动后备厢-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("外观/防盗", "钥匙类型"));//钥匙类型-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("外观/防盗", "无钥匙进入功能"));//无钥匙进入功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("车外灯光", "近光灯光源"));//近光灯光源-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("车外灯光", "远光灯光源"));//远光灯光源-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("车外灯光", "自动头灯"));//自动头灯-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("天窗/玻璃", "天窗类型"));//天窗类型-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("天窗/玻璃", "车内化妆镜"));//车内化妆镜-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("外后视镜", "外后视镜功能"));//外后视镜功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "中控屏幕尺寸"));//中控屏幕尺寸-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "副驾娱乐屏"));//副驾娱乐屏-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "手机互联/映射"));//手机互联/映射-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "语音识别控制系统"));//语音识别控制系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "车载智能系统"));//车载智能系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "车机智能芯片"));//车机智能芯片-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("屏幕/系统", "后排液晶屏幕尺寸"));//后排液晶屏幕尺寸-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("智能化配置", "4G/5G网络"));//4G/5G网络-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("智能化配置", "OTA升级"));//OTA升级-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("智能化配置", "手机APP远程功能"));//手机APP远程功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("智能化配置", "主动降噪"));//主动降噪-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("方向盘/内后视镜", "方向盘材质"));//方向盘材质-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("方向盘/内后视镜", "方向盘位置调节"));//方向盘位置调节-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("方向盘/内后视镜", "液晶仪表尺寸"));//液晶仪表尺寸-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("方向盘/内后视镜", "HUD抬头数字显示"));//HUD抬头数字显示-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("方向盘/内后视镜", "内后视镜功能"));//内后视镜功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("车内充电", "USB/Type-C接口数量"));//USB/Type-C接口数量-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("车内充电", "手机无线充电功能"));//手机无线充电功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "座椅材质"));//座椅材质-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "主座椅调节方式"));//主座椅调节方式-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "副座椅调节方式"));//副座椅调节方式-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "主/副驾驶座电动调节"));//主/副驾驶座电动调节-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "前排座椅功能"));//前排座椅功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "后排座椅放倒形式"));//后排座椅放倒形式-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("音响/车内灯光", "扬声器品牌名称"));//扬声器品牌名称-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("音响/车内灯光", "扬声器数量"));//扬声器数量-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("音响/车内灯光", "车内环境氛围灯"));//车内环境氛围灯-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("空调/冰箱", "空调温度控制方式"));//空调温度控制方式-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("空调/冰箱", "后排独立空调"));//后排独立空调-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("空调/冰箱", "车载空气净化器"));//车载空气净化器-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("空调/冰箱", "车载冰箱"));//车载冰箱-配置项

                        //能源类型判断
                        if (seriesInfo.getEnergytype() == 1) {
                            mustSeeItemDtos.add(new MustSeeItemDto("电动机", "CLTC纯电续航里程(km)"));//CLTC纯电续航里程(km)-参数项
                            mustSeeItemDtos.add(new MustSeeItemDto("电动机", "WLTC纯电续航里程(km)"));//WLTC纯电续航里程(km)-参数项
                            mustSeeItemDtos.add(new MustSeeItemDto("电动机", "电池快充时间(小时)"));//电池快充时间(小时)-参数项
                            mustSeeItemDtos.add(new MustSeeItemDto("基本参数", "电动机(Ps)"));//电动机(Ps)-参数项
                            mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "单踏板模式"));//单踏板模式-配置项
                            mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "能量回收系统"));//能量回收系统-配置项
                            mustSeeItemDtos.add(new MustSeeItemDto("驾驶硬件", "辅助驾驶芯片"));//辅助驾驶芯片-配置项
                            mustSeeItemDtos.add(new MustSeeItemDto("驾驶硬件", "芯片总算力"));//芯片总算力-配置项
                            mustSeeItemDtos.add(new MustSeeItemDto("外观/防盗", "电池预加热"));//电池预加热-配置项
                            mustSeeItemDtos.add(new MustSeeItemDto("外观/防盗", "对外放电"));//对外放电-配置项
                            mustSeeItemDtos.add(new MustSeeItemDto("空调/冰箱", "热泵空调"));//热泵空调-配置项
                        } else {
                            mustSeeItemDtos.add(new MustSeeItemDto("基本参数", "发动机"));//发动机-参数项
                            mustSeeItemDtos.add(new MustSeeItemDto("变速箱", "简称"));//简称-参数项
                            mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "发动机启停技术"));//发动机启停技术-配置项
                        }
                    }
                    //级别判断
                    if (Arrays.asList(16, 17, 18, 19, 20).contains(seriesInfo.getLevelId())) {
                        //SUV
                        mustSeeItemDtos.add(new MustSeeItemDto("驾驶操控", "陡坡缓降"));//陡坡缓降-配置项
                    }
                    if (Arrays.asList(16, 17, 18, 19, 20).contains(seriesInfo.getLevelId()) || Arrays.asList(14, 15).contains(seriesInfo.getLevelId())) {
                        //SUV/皮卡
                        mustSeeItemDtos.add(new MustSeeItemDto("四驱/越野", "中央差速器锁止功能"));//中央差速器锁止功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("四驱/越野", "限滑差速器/差速锁"));//限滑差速器/差速锁-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("四驱/越野", "涉水感应系统"));//涉水感应系统-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("四驱/越野", "低速四驱"));//低速四驱-配置项
                    }
                    if (Arrays.asList(21, 22, 23, 24).contains(seriesInfo.getLevelId())) {
                        //MPV
                        mustSeeItemDtos.add(new MustSeeItemDto("外观/防盗", "侧滑门形式"));//侧滑门形式-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第二排座椅调节"));//第二排座椅调节-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第二排座椅电动调节"));//第二排座椅电动调节-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第二排座椅功能"));//第二排座椅功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "福祉座椅"));//福祉座椅-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第二排独立座椅"));//第二排独立座椅-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第三排座椅调节"));//第三排座椅调节-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第三排座椅电动调节"));//第三排座椅电动调节-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "第三排座椅功能"));//第三排座椅功能-配置项
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "座椅布局"));//座椅布局-配置项
                    }
                    if (Arrays.asList(7).contains(seriesInfo.getLevelId())) {
                        //跑车
                        mustSeeItemDtos.add(new MustSeeItemDto("座椅配置", "头颈暖风系统"));//头颈暖风系统-配置项
                    }

                    for (MustSeeItemDto item : mustSeeItemDtos) {
                        Mustseelist.Builder builder = Mustseelist.newBuilder();
                        builder.setItemtype(item.getItemtype());
                        builder.setParamitemname(item.getParamitemname());
                        mustSeeList.add(builder.build());
                    }
                }
                return mustSeeList;
            });
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    /**
     * 构建关注度最高车型信息
     *
     * @param request
     * @param seriesId
     * @return
     */
    private CompletableFuture<Attentionspecinfo> buildAttentionSpecInfo(GetSpecParamConfigInfoRequest request, int seriesId) {
        Attentionspecinfo.Builder attentionSpecInfobuilder = Attentionspecinfo.newBuilder();
        if (seriesId > 0) {
            SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);
            if (Objects.nonNull(seriesDetailDto)) {
                int hotSpecId = seriesDetailDto.getHotSpecId();
                return specDetailComponent.get(hotSpecId).thenApply(specInfo -> {

                    if (Objects.nonNull(specInfo) && specInfo.getParamIsShow() == 1) {
                        attentionSpecInfobuilder.setParamisshow(specInfo.getParamIsShow());
                        attentionSpecInfobuilder.setParamisshow(specInfo.getParamIsShow());
                        String priceInfo = PriceUtil.getSpecPrice(specInfo.getMinPrice());
                        if (specInfo.isBooked() && specInfo.getState() == 10) {
                            priceInfo = getDynamicPrice(specInfo);
                        }
                        attentionSpecInfobuilder.setPriceinfo(priceInfo);
                        attentionSpecInfobuilder.setSeriesid(specInfo.getSeriesId());
                        attentionSpecInfobuilder.setSeriesname(specInfo.getSeriesName());
                        attentionSpecInfobuilder.setSpecid(specInfo.getSpecId());
                        attentionSpecInfobuilder.setSpecname(specInfo.getSpecName());
                    }
                    return attentionSpecInfobuilder.build();
                });
            }
        }
        return CompletableFuture.completedFuture(attentionSpecInfobuilder.build());
    }

    /**
     * 构建车型信息、参配数据列表
     * @param request
     * @param resultBuilder
     * @param specYearList
     * @param specDetailDtos
     * @return
     */
    private CompletableFuture<List<GetSpecParamConfigInfoResponse.Result.Datalist>> buildDatalist(GetSpecParamConfigInfoRequest request, GetSpecParamConfigInfoResponse.Result.Builder resultBuilder, List<SpecGroupOfSeriesDto> specYearList, List<SpecDetailDto> specDetailDtos) {
        List<Integer> seriesIdList = specDetailDtos.stream().map(SpecDetailDto::getSeriesId).distinct().collect(Collectors.toList());
        List<Integer> specIdList = specDetailDtos.stream().map(SpecDetailDto::getSpecId).collect(Collectors.toList());

        ConcurrentHashMap<Integer, SListAreaButtonResult> smartAreaButtonMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap = new ConcurrentHashMap<>();
        List<SListAreaButtonResult> areaButtonList = Collections.synchronizedList(new ArrayList<>());
        ConcurrentHashMap<String, SeriesVr> seriesVrMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, ConfigBaikeLinkDto> baikeMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, LinkedHashMap<String, SpecConfigResult.Configitems>> specConfigInfoMap =new ConcurrentHashMap<>();
        LinkedHashMap<String, Map<String, List<SpecConfigResult.Configitems>>> allConfigTypeItemsMap = new LinkedHashMap<>();
        ConcurrentHashMap<Integer, LinkedHashMap<String, SeriesParamTypeModel.ParamitemsBean>> specParamInfoMap =new ConcurrentHashMap<>();
        LinkedHashMap<String, Map<String, List<SeriesParamTypeModel.ParamitemsBean>>> allParamTypeItemsMap = new LinkedHashMap<>();
        ConcurrentHashMap<Integer, Map<Long, SpecShiCeSmallVideoResult.ResultBean>> shiceSmallVideoMap = new ConcurrentHashMap<>();//实测小视频
        ConcurrentHashMap<Integer, List<Integer>> configPicMap = new ConcurrentHashMap<>();//配置项图片
        ConcurrentHashMap<Integer, SpecTestDataDto> specTestDataMap =new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecConfigSmallVideoDto> specSmallVideMap =new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, SpecAiVideoDto> specAiVideoMap =new ConcurrentHashMap<>();
        AtomicBoolean isHaveColor = new AtomicBoolean(false);//是否有外观或内饰颜色
        ConcurrentHashMap<Integer, SpecOutInnerColorDto> specOuterColorDtoMap = new ConcurrentHashMap<>();//外观颜色
        ConcurrentHashMap<Integer, SpecOutInnerColorDto> specInnerColorDtoMap = new ConcurrentHashMap<>();//内饰颜色
        LinkedHashMap<String, Map<Integer,SpecSpecificConfigDto.ConfigItem>> allSpecificConfigMap = new LinkedHashMap<>();//特色配置
        LinkedHashMap<String, Map<Integer,SpecConfigBagDto.ConfigBagValue>> specConfigBagDtoMap = new LinkedHashMap<>();//选装包
        ConcurrentHashMap<String, ConfigItemResult> configItemDtoMap = new ConcurrentHashMap<>();//设置配置ID

//specIdList.forEach(i->{
//    specConfigInfoComponent.refresh(i);
//});
        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(baikelinkforconfigComponent.getMap()
                .thenAccept(result -> {
                    if(result != null) {
                        baikeMap.putAll(result);
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-baikelinkforconfigComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specParamInfoComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null){
                        result.removeIf(Objects::isNull);
                        Map<Integer, List<SpecParamInfoDto>> specParamMap = result.stream().collect(Collectors.groupingBy(SpecParamInfoDto::getSpecId));
                        for (Integer specKey : specParamMap.keySet()) {
                            List<SpecParamInfoDto> configInfoDtoList = specParamMap.get(specKey);
                            if (ListUtil.isNotEmpty(configInfoDtoList)) {
                                SpecParamInfoDto paramInfoDto = configInfoDtoList.get(0);
                                List<SeriesParamTypeModel> paramtypeitems = paramInfoDto.getRsptmList();
                                LinkedHashMap<String, SeriesParamTypeModel.ParamitemsBean> specParamItemMap = new LinkedHashMap<>();
                                for (SeriesParamTypeModel typeItem : paramtypeitems) {
                                    typeItem.getParamitems().forEach(subitem -> {
                                        //车型配置项map key = 一级分类_二级分类_配置id
                                        specParamItemMap.put(String.format("%s_%s_%s", typeItem.getGroupname(), typeItem.getName(), subitem.getId()), subitem);
                                    });
                                }
                                //车型配置map key = 车型id
                                specParamInfoMap.put(specKey, specParamItemMap);
                            }
                        }

                        List<SeriesParamTypeModel> allParamTypeItems = new ArrayList<>();
                        result.forEach(specParam -> {
                            if (specParam != null && ListUtil.isNotEmpty(specParam.getRsptmList())) {
                                allParamTypeItems.addAll(specParam.getRsptmList());
                            }
                        });
                        //分组（配置类别）
                        LinkedHashMap<String, ArrayList<SeriesParamTypeModel>> paramTypeMap = allParamTypeItems.stream().collect(Collectors.groupingBy(config ->
                                config.getGroupname() + config.getName(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                        for (Map.Entry<String, ArrayList<SeriesParamTypeModel>> paramTypeItemsMap : paramTypeMap.entrySet()) {
                            //配置项分组数据
                            SeriesParamTypeModel configTypeItem = paramTypeItemsMap.getValue().get(0);
                            String groupname = configTypeItem.getGroupname();
                            String itemtype = configTypeItem.getName();
                            List<SeriesParamTypeModel.ParamitemsBean> allConfigItems = new ArrayList<>();
                            paramTypeItemsMap.getValue().forEach(configItem -> {
                                allConfigItems.addAll(configItem.getParamitems());
                            });
                            //分组（配置项）
                            Map<String, List<SeriesParamTypeModel.ParamitemsBean>> allConfigItemsMap = allConfigItems.stream().collect(Collectors.groupingBy(config ->
                                    config.getId() + "_" + config.getName(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                            allParamTypeItemsMap.put(groupname + "_" + itemtype, allConfigItemsMap);
                        }
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specParamInfoComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specConfigInfoComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null){
                        result.removeIf(Objects::isNull);

                        if (ListUtil.isNotEmpty(result)) {

                            Map<Integer, List<SpecConfigInfoDto>> specConfigMap = result.stream().collect(Collectors.groupingBy(SpecConfigInfoDto::getSpecId));
                            for(Integer specKey:specConfigMap.keySet()){
                                List<SpecConfigInfoDto> configInfoDtoList = specConfigMap.get(specKey);
                                if(ListUtil.isNotEmpty(configInfoDtoList)){
                                    SpecConfigInfoDto configInfoDto  = configInfoDtoList.get(0);
                                    List<SpecConfigResult.Configtypeitems> configtypeitems = configInfoDto.getConfigtypeitems();
                                    LinkedHashMap<String, SpecConfigResult.Configitems> specCfgItemMap = new LinkedHashMap<>();
                                    for(SpecConfigResult.Configtypeitems typeItem:configtypeitems){
                                        typeItem.getConfigitems().forEach(subitem->{
                                            //车型配置项map key = 一级分类_二级分类_配置id
                                            specCfgItemMap.put(String.format("%s_%s_%s",typeItem.getGroupname(),typeItem.getName(),subitem.getConfigid()),subitem);
                                        });
                                    }
                                    //车型配置map key = 车型id
                                    specConfigInfoMap.put(specKey,specCfgItemMap);
                                }

                            }

                            List<SpecConfigResult.Configtypeitems> allConfigTypeItems = new ArrayList<>();
                            result.forEach(specConfig -> {
                                if(specConfig != null && ListUtil.isNotEmpty(specConfig.getConfigtypeitems())){
                                    allConfigTypeItems.addAll(specConfig.getConfigtypeitems());
                                }
                            });
                            //分组（配置类别）
                            LinkedHashMap<String, ArrayList<SpecConfigResult.Configtypeitems>> configTypeMap = allConfigTypeItems.stream().collect(Collectors.groupingBy(config ->
                                    config.getGroupname() + config.getName(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                            for (Map.Entry<String, ArrayList<SpecConfigResult.Configtypeitems>> configTypeItemsMap : configTypeMap.entrySet()) {
                                //配置项分组数据
                                SpecConfigResult.Configtypeitems configTypeItem = configTypeItemsMap.getValue().get(0);
                                String groupname = configTypeItem.getGroupname();
                                String itemtype = configTypeItem.getName();
                                List<SpecConfigResult.Configitems> allConfigItems = new ArrayList<>();
                                configTypeItemsMap.getValue().forEach(configItem -> {
                                    allConfigItems.addAll(configItem.getConfigitems());
                                });
                                //分组（配置项）
                                Map<String, List<SpecConfigResult.Configitems>> allConfigItemsMap = allConfigItems.stream().collect(Collectors.groupingBy(config ->
                                        config.getConfigid() +"_"+ config.getName(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
                                allConfigTypeItemsMap.put(groupname+"_"+itemtype,allConfigItemsMap);
                            }
                        }
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specConfigInfoComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specCityAskPriceComponent.get(specIdList, request.getCityid())
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        specCityAskPriceMap.putAll(result.stream().collect(Collectors.toMap(SpecCityAskPriceDto::getSpecId, Function.identity(), (k1, k2) -> k2)));
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specCityAskPriceComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specShiCeSmallVideoComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        result.forEach(action -> {
                            if(ListUtil.isNotEmpty(action.getVideoInfoList())){
                                action.getVideoInfoList().forEach(video -> {
                                    if (shiceSmallVideoMap.containsKey(video.getSpec_id())) {
                                        shiceSmallVideoMap.get(video.getSpec_id()).put(video.getTag_id(), video);
                                    } else {
                                        Map<Long, SpecShiCeSmallVideoResult.ResultBean> map = new HashMap();
                                        map.put(video.getTag_id(), video);
                                        shiceSmallVideoMap.put(video.getSpec_id(), map);
                                    }
                                });
                            }
                        });
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specShiCeSmallVideoComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specParamConfigPicInfoComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        result.stream().forEach(haspicdata -> {
                            if(ListUtil.isNotEmpty(haspicdata.getList())){
                                haspicdata.getList().forEach(picitemdata -> {
                                    if (picitemdata.getDatatype() == 2) {
                                        if (configPicMap.containsKey(haspicdata.getSpecId())) {
                                            configPicMap.get(haspicdata.getSpecId()).add(picitemdata.getItemid());
                                        } else {
                                            List<Integer> list = new ArrayList();
                                            list.add(picitemdata.getItemid());
                                            configPicMap.put(haspicdata.getSpecId(), list);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specParamConfigPicInfoComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specTestDataComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        specTestDataMap.putAll(result.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecTestDataDto::getSpecId, Function.identity(), (k1, k2) -> k2)));
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specTestDataComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specConfigSmallVideoComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        specSmallVideMap.putAll(result.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecConfigSmallVideoDto::getSpecId, Function.identity(), (k1, k2) -> k2)));
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specConfigSmallVideoComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specAiVideoComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        specAiVideoMap.putAll(result.stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecAiVideoDto::getSpecId, Function.identity(), (k1, k2) -> k2)));
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specAiVideoComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specOutInnerColorComponent.get(specIdList, false)
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        specOuterColorDtoMap.putAll(result.stream().collect(Collectors.toMap(SpecOutInnerColorDto::getSpecid, Function.identity(), (k1, k2) -> k2)));
                        long count = result.stream().filter(x -> x != null && ListUtil.isNotEmpty(x.getColoritems())).count();
                        if (count > 0) {
                            isHaveColor.set(true);
                        }
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specOutInnerColorComponent-outer error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specOutInnerColorComponent.get(specIdList, true)
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        specInnerColorDtoMap.putAll(result.stream().collect(Collectors.toMap(SpecOutInnerColorDto::getSpecid, Function.identity(), (k1, k2) -> k2)));
                        long count = result.stream().filter(x -> x != null && ListUtil.isNotEmpty(x.getColoritems())).count();
                        if (count > 0) {
                            isHaveColor.set(true);
                        }
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specOutInnerColorComponent-inner error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specSpecificConfigComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        List<SpecSpecificConfigDto.ConfigItem> allconfigitems = new ArrayList<>();
                        result.forEach(x -> {
                            if (x != null && ListUtil.isNotEmpty(x.getConfigitems())) {
                                allconfigitems.addAll(x.getConfigitems());
                            }
                        });
                        //分组
                        LinkedHashMap<String, Map<Integer, SpecSpecificConfigDto.ConfigItem>> allIficMap = allconfigitems.stream().collect(Collectors.groupingBy(config ->
                                config.getConfigid() + config.getName(), LinkedHashMap::new, Collectors.toMap(SpecSpecificConfigDto.ConfigItem::getSpecid, Function.identity(), (k1, k2) -> k2)));
                        allSpecificConfigMap.putAll(allIficMap);
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specSpecificConfigComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(specConfigBagComponent.get(specIdList)
                .thenAccept(result -> {
                    if(result != null) {
                        result.removeIf(Objects::isNull);
                        result.sort(Comparator.comparing((SpecConfigBagDto x) -> x.getConfigbags().size()).reversed());
                        List<Item> configitems = new ArrayList<>();
                        List<SpecConfigBagDto.ConfigBagValue> allConfigBags = new ArrayList<>();
                        result.forEach(x -> {
                            if (x != null && ListUtil.isNotEmpty(x.getConfigbags())) {
                                allConfigBags.addAll(x.getConfigbags());
                            }
                        });
                        //分组
                        LinkedHashMap<String, Map<Integer,SpecConfigBagDto.ConfigBagValue>> allConfigBagsMap = allConfigBags.stream().collect(Collectors.groupingBy(config ->
                                config.getBagid() + config.getName(), LinkedHashMap::new, Collectors.toMap(SpecConfigBagDto.ConfigBagValue::getSpecid, Function.identity(), (k1, k2) -> k2)));
                        specConfigBagDtoMap.putAll(allConfigBagsMap);
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-specConfigBagComponent error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        tasks.add(configItemApiClient.getConfigItemBaseInfo()
                .thenAccept(result -> {
                    if (result != null && result.getResult() != null) {
                        result.getResult().removeIf(Objects::isNull);
                        configItemDtoMap.putAll(result.getResult().stream().filter(Objects::nonNull).collect(Collectors.toMap(ConfigItemResult::getName, Function.identity(), (k1, k2) -> k2)));
                    }
                }).exceptionally(e -> {
                    logger.error("车型参配接口异常-configItemApiClient error: {}", ExceptionUtils.getStackTrace(e));
                    return null;
                }));
        //询价按钮程序化 三端 deviceType
        String deviceType;
        int pm = request.getPm();
        if (pm == 1) {
            deviceType = "ios";
        } else if (pm == 2) {
            deviceType = "android";
        } else if (pm == 3) {
            deviceType = "harmony";
        } else {
            deviceType = "ios";
        }
        //询价按钮程序化 车系、车型、pk areaid
        int areaid;//车系
        if (request.getSite() == 2) {
            areaid = 501;//车型
        } else if (request.getSite() == 3) {
            areaid = 502;//pk
        } else {
            areaid = 500;
        }
        Lists.partition(specIdList, 20).forEach(sublist -> {
            tasks.add(dealerApiClient.getListSmartAreaButton(request.getCityid(), deviceType, areaid,
                    org.apache.commons.lang3.StringUtils.join(sublist, ","),
                    request.getPluginversion(), "92f6e950_5616_4589_a7b2_0702fdb77432", "", 0,
                    UUID.randomUUID().toString()).thenAccept(result -> {
                if (result != null && result.getResult() != null) {
                    areaButtonList.addAll(result.getResult());
                }
            }).exceptionally(e -> {
                log.error("listSmartAreaButton", e);
                return null;
            }));
        });
        seriesIdList.forEach(seriesId -> tasks.add(seriesVrComponent.get(seriesId).thenAccept(seriesVrDto -> {
            if (seriesVrDto != null) {
                seriesVrMap.put(seriesId + "", seriesVrDto);
            }
        }).exceptionally(e -> {
            logger.error("车型参配接口异常-seriesVrComponent error: {}", ExceptionUtils.getStackTrace(e));
            return null;
        })));

        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(obj -> {
            List<GetSpecParamConfigInfoResponse.Result.Datalist.Builder> datalist = new ArrayList<>();
            Map<String,List<String>> specConListMap = new HashMap<>();
            //询价按钮信息转map
            areaButtonList.removeIf(Objects::isNull);
            smartAreaButtonMap.putAll(areaButtonList.stream().collect(Collectors.toMap(SListAreaButtonResult::getSpecId, Function.identity(), (k1, k2) -> k2)));
            //设置筛选项信息
            if (specYearList != null && !specYearList.isEmpty()) {
                if (request.getSite() == 1) {
                    //车系来源 返回车型筛选项列表 map
                    specConListMap.putAll(buildSeriesConditionlist(request, resultBuilder, specYearList,specDetailDtos));
                } else if (request.getSite() == 2) {
                    buildSpecConditionlist(request, resultBuilder, specYearList,specDetailDtos);
                }
            }

            //获取需要展示的参配百科数据
            List<ConfigInfoDto> configInfos = JSONObject.parseObject(baiKeInFoConfig, new TypeReference<List<ConfigInfoDto>>() {
            });
            Map<String, Integer> configMap = new HashMap<>();
            configInfos.forEach(infos -> configMap.put(infos.getConfigName(), infos.getConfigId()));
            if (ListUtil.isNotEmpty(specDetailDtos)) {
                specDetailDtos.forEach(specInfo -> {
                    if (Objects.nonNull(specInfo)) {
                        GetSpecParamConfigInfoResponse.Result.Datalist.Builder dataitem = GetSpecParamConfigInfoResponse.Result.Datalist.newBuilder();
                        SpecCityAskPriceDto specCityAskPriceDto = specCityAskPriceMap.get(specInfo.getSpecId());
                        dataitem.setSpecinfo(buildSpecitem(
                                request,
                                specInfo,
                                specCityAskPriceDto,
                                smartAreaButtonMap,
                                specConListMap.get(specInfo.getSpecId()+"")));
                        dataitem.addAllParamitems(buildParamitem(
                                request,
                                specInfo,
                                baikeMap,
                                shiceSmallVideoMap.get(specInfo.getSpecId()),
                                specParamInfoMap.get(specInfo.getSpecId()),
                                allParamTypeItemsMap,
                                specCityAskPriceDto,
                                specTestDataMap.get(specInfo.getSpecId())));
                        dataitem.addAllConfigitems(buildConfigitem(
                                request,
                                specInfo,
                                baikeMap,
                                seriesVrMap.get(specInfo.getSeriesId() + ""),
                                specConfigInfoMap.get(specInfo.getSpecId()),
                                allConfigTypeItemsMap,
                                specSmallVideMap.get(specInfo.getSpecId()),
                                specAiVideoMap.get(specInfo.getSpecId()),
                                shiceSmallVideoMap.get(specInfo.getSpecId()),
                                configPicMap.get(specInfo.getSpecId()),
                                isHaveColor.get(),
                                specOuterColorDtoMap.get(specInfo.getSpecId()),
                                specInnerColorDtoMap.get(specInfo.getSpecId()),
                                allSpecificConfigMap,
                                specConfigBagDtoMap,
                                configItemDtoMap,
                                specTestDataMap.get(specInfo.getSpecId()),
                                configMap));
                        datalist.add(dataitem);
                    }
                });

                buildTestStandardData(request, datalist, specTestDataMap);

                buildSpecOnsaleorder(request, datalist, specYearList);

                if (request.getSite() == 2) {
                    Footaskpriceinfo.Builder footaskpriceinfo = buildSpecFootaskpriceinfo(request, specDetailDtos, specCityAskPriceMap, smartAreaButtonMap);
                    if(footaskpriceinfo != null){
                        resultBuilder.setFootaskpriceinfo(footaskpriceinfo);
                    }
                }
            }

            return datalist.stream().map(GetSpecParamConfigInfoResponse.Result.Datalist.Builder::build).collect(Collectors.toList());
        });
    }

    /**
     * 构建车型配置信息
     *
     * @param specInfo
     * @param
     * @return
     */
    private List<Configitem> buildConfigitem(
            GetSpecParamConfigInfoRequest request,
            SpecDetailDto specInfo,
            ConcurrentHashMap<String, ConfigBaikeLinkDto> baikeMap,
            SeriesVr seriesVr,
            LinkedHashMap<String, SpecConfigResult.Configitems> specConfigInfoMap,
            LinkedHashMap<String, Map<String, List<SpecConfigResult.Configitems>>> allConfigTypeItemsMap,
            SpecConfigSmallVideoDto specConfigSmallVideoDto,
            SpecAiVideoDto specAiVideoDto,
            Map<Long, SpecShiCeSmallVideoResult.ResultBean> shiceSmallVideoDtoMap,
            List<Integer> configPicInfoDtoList,
            boolean isHaveColor,
            SpecOutInnerColorDto specOutColorDto,
            SpecOutInnerColorDto specInnerColorDto,
            LinkedHashMap<String, Map<Integer,SpecSpecificConfigDto.ConfigItem>> allSpecialConfingMap,
            LinkedHashMap<String, Map<Integer,SpecConfigBagDto.ConfigBagValue>> specConfigBagDtoMap,
            ConcurrentHashMap<String, ConfigItemResult> configItemDtoMap,
            SpecTestDataDto specTestDataDto,
            Map<String, Integer> configMap
    ) {

        List<Configitem> groupInfos = new ArrayList<>();
        if (Objects.nonNull(specConfigInfoMap) && !specConfigInfoMap.isEmpty()) {

            for (String groupKey : allConfigTypeItemsMap.keySet()) {
                //配置项分组数据
                String[] keys = groupKey.split("_");
                Configitem.Builder groupInfoBuilder = Configitem.newBuilder();
                String groupname;
                String itemtype;
                if(keys.length==2){
                     groupname = keys[0];
                     itemtype = keys[1];
                    groupInfoBuilder.setGroupname(groupname);
                    groupInfoBuilder.setItemtype(itemtype);
                } else {
                    itemtype = "";
                    groupname = "";
                }
                groupInfoBuilder.setShowtips(true);
                //分组（配置项）
                Map<String, List<SpecConfigResult.Configitems>> allConfigItemsMap = allConfigTypeItemsMap.get(groupKey);
                for (Map.Entry<String, List<SpecConfigResult.Configitems>> configItemsMap : allConfigItemsMap.entrySet()) {
                    //配置项数据
                    SpecConfigResult.Configitems configItem = configItemsMap.getValue().get(0);
                    int itemid = configItem.getConfigid();
                    String itemname = configItem.getName().replace("&nbsp;", " ").replace("&amp;", "&");
                    Item.Builder itemBuilder = Item.newBuilder();
                    itemBuilder.setId(getBaikeId(baikeMap, itemname));//设置百科数据
                    itemBuilder.setParamitemid(itemid);
                    itemBuilder.setName(itemname);
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specInfo.getSpecId());
                    modelexcessid.setValue("-");
                    modelexcessid.setPriceinfo("-1");
                    modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder());
                    String cfgKey =String.format("%s_%s_%s",groupname,itemtype,itemid+"");
                    if(specConfigInfoMap != null && specConfigInfoMap.containsKey(cfgKey)){
                        SpecConfigResult.Valueitems valueitems = null;
                        SpecConfigResult.Configitems config = specConfigInfoMap.get(cfgKey);
                        if(ListUtil.isNotEmpty(config.getValueitems())) {
                            valueitems = config.getValueitems().get(0);
                            if(valueitems != null){
                                String value = valueitems.getValue().replace("&nbsp;", " ").replace("&amp;", "&").replace("/", " / ");
                                modelexcessid.setValue(value);
                            }
                        }
                        if(config.getDisptype() == 1){
                            if (valueitems != null && ListUtil.isNotEmpty(valueitems.getSublist())) {
                                modelexcessid.setValue("");
                                valueitems.getSublist().forEach(item -> {
                                    Item.Modelexcessid.Sublist.Builder sublist = Item.Modelexcessid.Sublist.newBuilder();
                                    sublist.setName(item.getSubname().replace("&nbsp;", " ").replace("&amp;", "&"));
                                    sublist.setValue(item.getSubvalue() == 1 ? "●" : "○");
                                    sublist.setPriceinfo(PriceUtil.getPriceInfoNoDefult(item.getPrice()));
                                    modelexcessid.addSublist(sublist);
                                });
                            }
                        }else if(config.getDisptype() == 0){
                            if (valueitems!=null && ListUtil.isNotEmpty(valueitems.getPrice())) {
                                modelexcessid.setPriceinfo(org.apache.commons.lang3.StringUtils.join(valueitems.getPrice().stream().map(i -> PriceUtil.getPriceInfoNoDefult(Integer.parseInt(i.getPrice()))).collect(Collectors.toList()), " / "));
                            }
                        }
                    }

                    //设置配置ID
                    if (configItemDtoMap != null && !configItemDtoMap.isEmpty()) {
                        ConfigItemResult configItemResult = configItemDtoMap.get(itemname);
                        if (configItemResult != null) {
                            itemBuilder.setSubid(configItemResult.getId());
                        } else {
                            itemBuilder.setSubid(-1);
                        }
                    } else {
                        itemBuilder.setSubid(-1);
                    }

                    //参配视频内容
                    ParamConfigVideoInfo info = parmconfigvideoinfo.get(itemname);
                    if (info != null) {
                        itemBuilder.setContentid(info.getContentid());
                        itemBuilder.setPlaystarttime(info.getPlaystarttime());
                    }

                    //之家实测数据
                    if(Objects.nonNull(specTestDataDto)){
                        TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
                        if (testStandardResult != null) {
                            String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=" + (request.getSite() + 6);
                            if ("被动安全".equals(groupInfoBuilder.getItemtype()) && "车身稳定控制(ESC/ESP/DSC等)".equals(itemBuilder.getName())) {
                                TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "安全性", "麋鹿", "麋鹿成绩", "麋鹿成绩");
                                if (testDataItem != null) {
                                    modelexcessid.setCornertype(5);
                                    modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                    modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 4, testDataItem.getParentId()));
                                }
                            }
                        }
                    }

                    //有视频数据，跳转到“autohome://car/videomotor”页面播放

                    String videourl = "";
                    String videoid = "";
                    String small_videoId = "";
                    if(Objects.nonNull(specConfigSmallVideoDto)){
                        SpecSmallVideoResult.ResultBean.VideolistBean videoInfo = specConfigSmallVideoDto.getVideoInfoMap().get(itemid);
                        if(Objects.nonNull(videoInfo)){
                            videourl=videoInfo.getPlayurl();
                            small_videoId = videoInfo.getMediaid();
                        }
                    }
                    if(Objects.nonNull(specAiVideoDto)){
                        SpecAiVideoDto.SpecAiVideoResult videoInfo = specAiVideoDto.getVideoInfoMap().get(itemid);
                        if(Objects.nonNull(videoInfo)){
                            modelexcessid.setVideoid(videoInfo.getVideoid());
                            videoid=videoInfo.getVideoid();
                        }
                    }


                    if ((StringUtils.isNotEmpty(modelexcessid.getValue()) && !modelexcessid.getValue().equals("-")) || modelexcessid.getSublistCount() > 0) {
                        if (StringUtils.isNotEmpty(videoid) || StringUtils.isNotEmpty(videourl)) {
                            String url = StringUtils.format("autohome://car/videomotor?vid={0}&iconurl={1}&videourl={2}&type=1", videoid, "", UrlUtil.encode(videourl));
                            modelexcessid.setCornertype(1);
                            modelexcessid.setCornerscheme(url);
                            modelexcessid.setLinkurl(url);
                        }
                    }

                    // 处理小视频;产品说小视频和视频不会冲突;
                    if (shiceSmallVideoDtoMap != null) {
                        if ("加速性能(s)".equalsIgnoreCase(itemname) && shiceSmallVideoDtoMap.containsKey(30050011018L)) {
                            modelexcessid.setCornertype(2);
                            modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011018L).getApp_jump() + "&loadmodel=0&source=66");
                        }
                        if ("刹车性能(m)".equalsIgnoreCase(itemname) && shiceSmallVideoDtoMap.containsKey(30050011019L)) {
                            modelexcessid.setCornertype(2);
                            modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011019L).getApp_jump() + "&loadmodel=0&source=66");
                        }
                    }
                    // 处理图片
                    if (Objects.nonNull(configPicInfoDtoList) && configPicInfoDtoList.contains(modelexcessid.getId())) {
                        modelexcessid.setHaspic(1);
                        modelexcessid.setCornertype(3);
                        modelexcessid.setCornerscheme("");
                        itemBuilder.setDatatype(2);
                    }

                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0") && "B".equals(request.getBaikeabtest())) {
                        if (StringUtils.isNotEmpty(itemname) && configMap.get(itemname) != null && ((StringUtils.isNotEmpty(modelexcessid.getValue()) && !"-".equals(modelexcessid.getValue())) || !modelexcessid.getSublistList().isEmpty())) {
                            modelexcessid.setCornertype(6);
                            String scheme = "autohome://carcompare/configjiedu?customshowanimationtype=2&title=%s&configid=%d&seriesid=%d&specid=%d&typeid=%d&videoid=%s&itemid=%d&year=%d&subitemid=%d";
                            modelexcessid.setCornerscheme(String.format(scheme, UrlUtil.encode(itemname), configMap.get(itemname), specInfo.getSeriesId(), specInfo.getSpecId(),request.getSite(),small_videoId,itemBuilder.getParamitemid(),specInfo.getYearName()));
                        }else{
                            //命中实验但不展示百科的参配，隐藏图片、视频标签
                            modelexcessid.setCornertype(0);
                            modelexcessid.setHaspic(0);
                            modelexcessid.setCornerscheme("");
                            modelexcessid.setPlayurl("");
                            modelexcessid.setLinkurl("");
                            modelexcessid.setVideoid("");
                        }
                    }
                    itemBuilder.addModelexcessids(modelexcessid);
                    groupInfoBuilder.addItems(itemBuilder.build());
                }
                groupInfos.add(groupInfoBuilder.build());
            }
            //专业评测-汽车之家实测

            //个性化-特色配置
            if(!allSpecialConfingMap.isEmpty()){
                Configitem specialConfig = buildSpecialConfig(specInfo, allSpecialConfingMap);
                if (specialConfig != null) {
                    groupInfos.add(specialConfig);
                }
            }
            //个性化-颜色
            Configitem colorConfig = buildSpecColorConfig(specInfo, isHaveColor, specOutColorDto, specInnerColorDto, seriesVr);
            if (colorConfig != null) {
                groupInfos.add(colorConfig);
            }
            //个性化-选装包
            if(!specConfigBagDtoMap.isEmpty()){
                Configitem selectConfigBag = buildSelectConfigBag(specInfo, specConfigBagDtoMap);
                if (selectConfigBag != null) {
                    groupInfos.add(selectConfigBag);
                }
            }
        }
        return groupInfos;
    }

    /**
     * 构建车型参数信息
     *
     * @return
     */
    private List<Paramitem> buildParamitem(
            GetSpecParamConfigInfoRequest request,
            SpecDetailDto specInfo,
            ConcurrentHashMap<String, ConfigBaikeLinkDto> baikeMap,
            Map<Long, SpecShiCeSmallVideoResult.ResultBean> shiceSmallVideoDtoMap,
            LinkedHashMap<String, SeriesParamTypeModel.ParamitemsBean> specParamInfoMap,
            LinkedHashMap<String, Map<String, List<SeriesParamTypeModel.ParamitemsBean>>> allParamTypeItemsMap,
            SpecCityAskPriceDto specCityAskPriceDto,
            SpecTestDataDto specTestDataDto
    ) {
        List<Paramitem> groupInfos = new ArrayList<>();
        if (Objects.nonNull(specParamInfoMap) && !specParamInfoMap.isEmpty()) {
            for (String groupKey : allParamTypeItemsMap.keySet()) {
                //参数项分组数据
                String[] keys = groupKey.split("_");
                String groupname;
                String itemtype;
                if(keys.length==2){
                    groupname = keys[0];
                    itemtype = keys[1];
                } else {
                    itemtype = "";
                    groupname = "";
                }
                Paramitem.Builder groupInfoBuilder = Paramitem.newBuilder();
                groupInfoBuilder.setGroupname("参数信息");
                groupInfoBuilder.setItemtype(itemtype);
                groupInfoBuilder.setShowtips(false);
                //分组（配置项）
                Map<String, List<SeriesParamTypeModel.ParamitemsBean>> allConfigItemsMap = allParamTypeItemsMap.get(groupKey);
                for (Map.Entry<String, List<SeriesParamTypeModel.ParamitemsBean>> paramItemsMap : allConfigItemsMap.entrySet()) {
                    //配置项数据
                    SeriesParamTypeModel.ParamitemsBean paramItem = paramItemsMap.getValue().get(0);
                    int itemid = paramItem.getId();
                    String itemname = paramItem.getName();
                    Item.Builder itemBuilder = Item.newBuilder();
                    itemBuilder.setId(getBaikeId(baikeMap, itemname));//设置百科数据
                    itemBuilder.setParamitemid(itemid);
                    itemBuilder.setName(itemname);
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specInfo.getSpecId());
                    modelexcessid.setValue("-");
                    modelexcessid.setPriceinfo("-1");
                    modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder());
                    String cfgKey =String.format("%s_%s_%s",groupname,itemtype,itemid+"");
                    if(specParamInfoMap != null && specParamInfoMap.containsKey(cfgKey)){
                        SeriesParamTypeModel.ParamitemsBean config = specParamInfoMap.get(cfgKey);
                        if (ListUtil.isNotEmpty(config.getValueitems())) {
                            SeriesParamTypeModel.ParamitemsBean.ValueitemsBean valueitem = config.getValueitems().get(0);
                            if (valueitem != null) {
                                String value = valueitem.getValue().replace("&nbsp;", "");
                                if ("厂商指导价(元)".equals(itemname) && "0.00万".equals(value)) {
                                    value = "暂无报价";
                                }
                                modelexcessid.setValue(value);
                                if (ListUtil.isNotEmpty(valueitem.getSublist())) {
                                    valueitem.getSublist().forEach(item -> {
                                        StringBuilder sb = new StringBuilder();
                                        if (StringUtils.isNotEmpty(item.getSubname())) {
                                            sb.append(item.getSubname());
                                        }
                                        if (StringUtils.isNotEmpty(item.getSubvalue())) {
                                            if (sb.length()>0) {
                                                sb.append(":");
                                            }
                                            sb.append(item.getSubvalue());
                                        }
                                        Item.Modelexcessid.Sublist.Builder sublist = Item.Modelexcessid.Sublist.newBuilder();
                                        sublist.setName(sb.toString());
                                        if (item.getPrice() > 0) {
                                            sublist.setPriceinfo(PriceUtil.getPriceInfoNoDefult(item.getPrice()));
                                        }
                                        if (item.getOptiontype() == 1) {
                                            sublist.setValue("●");
                                        } else if (item.getOptiontype() == 2) {
                                            sublist.setValue("○");
                                        }
                                        modelexcessid.addSublist(sublist);
                                    });
                                }
                            }
                        }
                    }

                    //参配视频内容
                    ParamConfigVideoInfo info = parmconfigvideoinfo.get(itemname);
                    if (info != null) {
                        itemBuilder.setContentid(info.getContentid());
                        itemBuilder.setPlaystarttime(info.getPlaystarttime());
                    }

                    //之家实测数据
                    if(Objects.nonNull(specTestDataDto)){
                        TestStandardResult testStandardResult = specTestDataDto.getTestStandardResult();
                        if (testStandardResult != null) {
                            String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=" + (request.getSite() + 6);
                            if ("官方0-100km/h加速(s)".equals(itemBuilder.getName())) {
                                TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "性能", "加速", "百公里加速", "0-100km/h加速时间");
                                if (testDataItem != null) {
                                    modelexcessid.setCornertype(5);
                                    modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                    modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 1, testDataItem.getParentId()));
                                }
                            } else if ("满载最小离地间隙(mm)".equals(itemBuilder.getName()) || "空载最小离地间隙(mm)".equals(itemBuilder.getName())) {
                                TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "性能", "越野通过性", "离地间隙", "离地间隙");
                                if (testDataItem != null) {
                                    modelexcessid.setCornertype(5);
                                    modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                    modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 1, testDataItem.getParentId()));
                                }
                            } else if ("快充时间(小时)".equals(itemBuilder.getName())) {
                                TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "充电", "充电", "30%-80%充电时长");
                                if (testDataItem != null) {
                                    modelexcessid.setCornertype(5);
                                    modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                    modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getParentId()));
                                }
                            } else if ("快充功率(kW)".equals(itemBuilder.getName())) {
                                TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "充电", "充电", "峰值充电功率");
                                if (testDataItem != null) {
                                    modelexcessid.setCornertype(5);
                                    modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                    modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getParentId()));
                                }
                            } else if ("后备厢容积(L)".equals(itemBuilder.getName())) {
                                TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "空间", "装载空间");
                                if (testDataItem != null) {
                                    modelexcessid.setCornertype(5);
                                    modelexcessid.setSubvalue("装载空间实测");
                                    modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 2, testDataItem.getItemId()));
                                }
                            }
                        }
                    }

                    // 处理小视频;产品说小视频和视频不会冲突;
                    if (shiceSmallVideoDtoMap != null) {
                        if ("加速性能(s)".equalsIgnoreCase(itemname) && shiceSmallVideoDtoMap.containsKey(30050011018L)) {
                            modelexcessid.setCornertype(2);
                            modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011018L).getApp_jump() + "&loadmodel=0&source=66");
                        }
                        if ("刹车性能(m)".equalsIgnoreCase(itemname) && shiceSmallVideoDtoMap.containsKey(30050011019L)) {
                            modelexcessid.setCornertype(2);
                            modelexcessid.setCornerscheme(shiceSmallVideoDtoMap.get(30050011019L).getApp_jump() + "&loadmodel=0&source=66");
                        }
                    }

                    itemBuilder.addModelexcessids(modelexcessid);
                    groupInfoBuilder.addItems(itemBuilder.build());
                }

                //基本参数-参考价(元)
                if("基本参数".equals(groupInfoBuilder.getItemtype()) && groupInfoBuilder.getItemsCount() > 0){
                    Item.Builder item = Item.newBuilder();
                    item.setId(getBaikeId(baikeMap, "参考价(元)"));
                    item.setName("参考价(元)");
                    item.setSubid(100003);
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specInfo.getSpecId());
                    modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder());
                    if (Objects.nonNull(specCityAskPriceDto)) {
                        modelexcessid.setValue(specCityAskPriceDto.getMinPrice() > 0 ? CommonHelper.df02.format(specCityAskPriceDto.getMinPrice() / 10000.0) + "万起" : "暂无报价");
                    } else {
                        modelexcessid.setValue("暂无报价");
                    }
                    item.addModelexcessids(modelexcessid);
                    groupInfoBuilder.addItems(1, item.build());
                }

                //基本参数-优惠信息（废弃了固定这样返回，已有单独的接口返回）
                if("基本参数".equals(groupInfoBuilder.getItemtype()) && groupInfoBuilder.getItemsCount() > 0){
                    Item.Builder item = Item.newBuilder();
                    item.setName("优惠信息");
                    item.setSubid(100005);
                    item.setId(-1);
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specInfo.getSpecId());
                    modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder());
                    modelexcessid.setValue("暂无");
                    item.addModelexcessids(modelexcessid);
                    groupInfoBuilder.addItems(2, item.build());
                }

                groupInfos.add(groupInfoBuilder.build());
            }
        }
        return groupInfos;
    }

    private void buildSpecOnsaleorder(GetSpecParamConfigInfoRequest request, List<GetSpecParamConfigInfoResponse.Result.Datalist.Builder> datalist, List<SpecGroupOfSeriesDto> specYearList){
        if (specYearList != null && specYearList.size() > 0) {
            AtomicInteger onsaleorder = new AtomicInteger(1);
            specYearList.forEach(s1 -> {
                if ("在售".equals(s1.getYearname()) && s1.getYearspeclist() != null
                        && s1.getYearspeclist().size() > 0) {
                    s1.getYearspeclist().forEach(s2 -> {
                        if (s2.getSpeclist() != null && s2.getSpeclist().size() > 0) {
                            s2.getSpeclist().forEach(s3 -> {
                                datalist.forEach(dataitem -> {
                                    Specitem.Builder specinfo = dataitem.getSpecinfoBuilder();
                                    if(specinfo.getSpecid() == s3.getSpecId()){
                                        specinfo.setOnsaleOrder(onsaleorder.getAndIncrement());
                                    }
                                });
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 参配项中插入实测数据
     * @param request
     * @param dataList
     * @param specTestDataMap
     */
    private void buildTestStandardData(
            GetSpecParamConfigInfoRequest request,
            List<GetSpecParamConfigInfoResponse.Result.Datalist.Builder> dataList,
            ConcurrentHashMap<Integer, SpecTestDataDto> specTestDataMap) {
        try {
            if (ListUtil.isNotEmpty(dataList) && !specTestDataMap.isEmpty() && request.getPm() != 3) {
                List<Paramitem> paramitems = dataList.get(0).getParamitemsList();
                String url = "autohome://car/ahtest?seriesid=%s&specid=%s&dataid=%s&tabid=%s&secid=%s&sourceid=" + (request.getSite() + 6);
                if (ListUtil.isNotEmpty(paramitems)) {
                    //要处理的参数项的一级分类
                    List<String> configtype = new ArrayList<>(Arrays.asList("基本参数", "车轮制动", "车身", "底盘转向", "电动机"));
                    //同一表单项
                    List<String> configitem = new ArrayList<>(Arrays.asList("后备厢容积(L)", "官方0-100km/h加速(s)", "满载最小离地间隙(mm)", "空载最小离地间隙(mm)", "快充时间(小时)", "快充功率(kW)"));
                    //独立表单项,,对应参数项 下增加的项名称
                    Map<String, String> addconfigitem = new HashMap<>();
                    addconfigitem.put("高度(mm)", "实测乘坐空间");
                    addconfigitem.put("四驱形式", "实测越野能力");
                    addconfigitem.put("后制动器类型", "刹车距离");
                    addconfigitem.put("WLTC综合油耗(L/100km)", "综合油耗");
                    addconfigitem.put("NEDC综合油耗(L/100km)", "综合油耗");
                    addconfigitem.put("CLTC纯电续航里程(km)", "综合续航");
                    addconfigitem.put("NEDC纯电续航里程(km)", "综合续航");
                    addconfigitem.put("WLTC纯电续航里程(km)", "综合续航");

                    for (Paramitem paramitem : paramitems) {
                        if (configtype.contains(paramitem.getItemtype()) && paramitem.getItemsCount() > 0) {
                            List<Item> items = paramitem.getItemsList();
                            for (int i = 0; i < items.size(); i++) {
                                Item item = items.get(i);
                                if (addconfigitem.containsKey(item.getName())) {
                                    String name = addconfigitem.get(item.getName());
                                    if (items.stream().filter(x -> x.getName().equals(name)).findFirst().isPresent()) {
                                        continue;
                                    }
                                    AtomicBoolean isAdd = new AtomicBoolean(false);
                                    Map<String, Item.Builder> newItemsMap = new HashMap<>();
                                    for (GetSpecParamConfigInfoResponse.Result.Datalist.Builder data : dataList) {
                                        Specitem specinfo = data.getSpecinfo();

                                        Item.Builder newItem = Item.newBuilder();
                                        newItem.setName(name);
                                        newItem.setVideoid("");
                                        newItem.setId(-1);
                                        newItem.setContentid("");
                                        Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                                        modelexcessid.setId(specinfo.getSpecid());
                                        modelexcessid.setValue("-");
                                        modelexcessid.setPriceinfo("-1");
                                        modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder());

                                        if (specTestDataMap.containsKey(specinfo.getSpecid())) {
                                            TestStandardResult testStandardResult = specTestDataMap.get(specinfo.getSpecid()).getTestStandardResult();
                                            if(testStandardResult != null){
                                                if ("高度(mm)".equals(item.getName())) {
                                                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "空间", "乘坐空间");
                                                    if (testDataItem != null) {
                                                        modelexcessid.setSubvalue(name);
                                                        modelexcessid.setCornertype(5);
                                                        modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 2, testDataItem.getItemId()));
                                                        isAdd.set(true);
                                                    }
                                                } else if ("四驱形式".equals(item.getName())) {
                                                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "性能", "越野通过性");
                                                    if (testDataItem != null) {
                                                        modelexcessid.setSubvalue(name);
                                                        modelexcessid.setCornertype(5);
                                                        modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 1, testDataItem.getItemId()));
                                                        isAdd.set(true);
                                                    }
                                                } else if ("后制动器类型".equals(item.getName())) {
                                                    TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "安全性", "刹车", "刹车距离", "刹车距离");
                                                    if (testDataItem != null) {
                                                        modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                                        modelexcessid.setCornertype(5);
                                                        modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 4, testDataItem.getItemId()));
                                                        isAdd.set(true);
                                                    }
                                                } else if ("WLTC综合油耗(L/100km)".equals(item.getName()) || "NEDC综合油耗(L/100km)".equals(item.getName())) {
                                                    //仅对应1项，优先WLTC
                                                    boolean WLTC = paramitem.getItemsList().stream().filter(x -> x.getName().equals("WLTC综合油耗(L/100km)")).findFirst().isPresent();
                                                    if (WLTC && "NEDC综合油耗(L/100km)".equals(item.getName())) {

                                                    } else {
                                                        TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "油耗", "油耗", "百公里油耗");
                                                        if (testDataItem != null) {
                                                            modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                                            modelexcessid.setCornertype(5);
                                                            modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getItemId()));
                                                            isAdd.set(true);
                                                        }
                                                    }
                                                } else if ("基本参数".equals(paramitem.getItemtype()) && ("CLTC纯电续航里程(km)".equals(item.getName()) || "NEDC纯电续航里程(km)".equals(item.getName()) || "WLTC纯电续航里程(km)".equals(item.getName()))) {
                                                    //仅对应1项，优先CLTC>NEDC>WLTC
                                                    boolean CLTC = paramitem.getItemsList().stream().filter(x -> x.getName().equals("CLTC纯电续航里程(km)")).findFirst().isPresent();
                                                    boolean WLTC = paramitem.getItemsList().stream().filter(x -> x.getName().equals("WLTC纯电续航里程(km)")).findFirst().isPresent();

                                                    if (CLTC && !"CLTC纯电续航里程(km)".equals(item.getName())) {

                                                    } else if (CLTC == false && WLTC && !"WLTC纯电续航里程(km)".equals(item.getName())) {

                                                    } else {
                                                        TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, "能耗", "续航", "续航电耗", "综合续航里程");
                                                        if (testDataItem != null) {
                                                            modelexcessid.setSubvalue("实测" + testDataItem.getResultShowValue() + testDataItem.getContentTypeUnit());
                                                            modelexcessid.setCornertype(5);
                                                            modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), 3, testDataItem.getItemId()));
                                                            isAdd.set(true);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if ("-".equals(modelexcessid.getValue()) && StringUtils.isNotEmpty(modelexcessid.getSubvalue())) {
                                            modelexcessid.setValue("");
                                        }
                                        newItem.addModelexcessids(modelexcessid);
                                        newItemsMap.put(specinfo.getSpecid() + "", newItem);
                                    }

                                    if (isAdd.get()) {
                                        for (GetSpecParamConfigInfoResponse.Result.Datalist.Builder data : dataList) {
                                            String key = data.getSpecinfo().getSpecid() + "";
                                            int finalI = i;
                                            data.getParamitemsBuilderList().stream().filter(group -> group.getItemtype().equals(paramitem.getItemtype())).findFirst().ifPresent(group -> {
                                                group.addItems(finalI + 1, newItemsMap.get(key).build());
                                            });
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                List<Configitem> configitems = dataList.get(0).getConfigitemsList();
                if (ListUtil.isNotEmpty(configitems)) {
                    Map<String, String> configtype = new HashMap<>();
                    configtype.put("主动安全", "实测主动安全");
                    configtype.put("驾驶功能", "实测智能驾驶");
                    configtype.put("智能化配置", "实测智能座舱");
                    configtype.put("被动安全", "");

                    Map<String, String> testTab = new HashMap<>();
                    testTab.put("主动安全", "安全性");
                    testTab.put("驾驶功能", "智能驾驶");
                    testTab.put("智能化配置", "智能座舱");

                    Map<String, String> testTabId = new HashMap<>();
                    testTabId.put("主动安全", "4");
                    testTabId.put("驾驶功能", "7");
                    testTabId.put("智能化配置", "8");
                    for (Configitem configitem : configitems) {
                        if (configtype.containsKey(configitem.getItemtype()) && configitem.getItemsCount() > 0) {
                            if (configitem.getItemsList().stream().filter(item -> item.getName().equals(configtype.get(configitem.getItemtype()))).findFirst().isPresent()) {
                                continue;
                            }

                            AtomicBoolean isAdd = new AtomicBoolean(false);
                            Map<String, Item.Builder> newItemsMap = new HashMap<>();
                            for (GetSpecParamConfigInfoResponse.Result.Datalist.Builder data : dataList) {
                                Specitem specinfo = data.getSpecinfo();

                                Item.Builder newItem = Item.newBuilder();
                                newItem.setName(configtype.get(configitem.getItemtype()));
                                newItem.setVideoid("");
                                newItem.setId(-1);
                                newItem.setContentid("");
                                Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                                modelexcessid.setId(specinfo.getSpecid());
                                modelexcessid.setValue("-");
                                modelexcessid.setPriceinfo("-1");
                                modelexcessid.setColorinfo(Item.Modelexcessid.Colorinfo.newBuilder());
                                if(specTestDataMap.containsKey(specinfo.getSpecid())){
                                    TestStandardResult testStandardResult = specTestDataMap.get(specinfo.getSpecid()).getTestStandardResult();
                                    if (testStandardResult != null) {
                                        TestStandardResult.TestDataItemListDTO testDataItem = getDataItemByName(testStandardResult, testTab.get(configitem.getItemtype()), "主动安全".equals(configitem.getItemtype()) ? "AEB主动安全" : "");
                                        if (testDataItem != null) {
                                            modelexcessid.setSubvalue(configtype.get(configitem.getItemtype()));
                                            modelexcessid.setCornertype(5);
                                            modelexcessid.setCornerscheme(String.format(url, testStandardResult.getSeriesId(), testStandardResult.getSpecId(), testStandardResult.getDataId(), testTabId.get(configitem.getItemtype()), "主动安全".equals(configitem.getItemtype()) ? testDataItem.getItemId() : ""));
                                            isAdd.set(true);
                                        }
                                    }
                                }
                                if ("-".equals(modelexcessid.getValue()) && StringUtils.isNotEmpty(modelexcessid.getSubvalue())) {
                                    modelexcessid.setValue("");
                                }
                                newItem.addModelexcessids(modelexcessid);
                                newItemsMap.put(specinfo.getSpecid() + "", newItem);
                            }

                            if (isAdd.get()) {
                                for (GetSpecParamConfigInfoResponse.Result.Datalist.Builder data : dataList) {
                                    String key = data.getSpecinfo().getSpecid() + "";
                                    data.getConfigitemsBuilderList().stream().filter(group -> group.getItemtype().equals(configitem.getItemtype())).findFirst().ifPresent(group -> {
                                        group.addItems(newItemsMap.get(key).build());
                                    });
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildTestStandardData error: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 判断实测一级、二级是否有数据
     */
    private TestStandardResult.TestDataItemListDTO getDataItemByName(TestStandardResult parentDataItem, String level1Name, String level2Name) {
        //性能>加速>百公里加速>0-100km/h加速时间
        if (parentDataItem == null || parentDataItem.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level1Name))
            return null;
        //性能
        TestStandardResult.TestDataItemListDTO level1ListDTO = parentDataItem.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level1Name)).findFirst().orElse(null);
        if (level1ListDTO==null|| level1ListDTO.getTestDataItemList() == null ||level1ListDTO.getTestDataItemList().size()==0)
            return null;
        if (StringUtil.isNullOrEmpty(level2Name)) {
            return level1ListDTO;
        }
        TestStandardResult.TestDataItemListDTO level2ListDTO =  level1ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level2Name)).findFirst().orElse(null);
        if (level2ListDTO==null|| level2ListDTO.getTestDataItemList() == null || level2ListDTO.getTestDataItemList().size()==0)
            return null;
        return level2ListDTO;
    }

    private TestStandardResult.TestDataItemListDTO getDataItemByName(TestStandardResult parentDataItem, String level1Name, String level2Name, String level3Name, String level4Name) {
        //性能>加速>百公里加速>0-100km/h加速时间
        if (parentDataItem == null || parentDataItem.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level1Name))
            return null;
        //性能
        TestStandardResult.TestDataItemListDTO level1ListDTO = parentDataItem.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level1Name)).findFirst().orElse(null);
        if (level1ListDTO==null|| level1ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level2Name))
            return null;
        //加速
        TestStandardResult.TestDataItemListDTO level2ListDTO = level1ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level2Name)).findFirst().orElse(null);
        if (level2ListDTO==null|| level2ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level3Name))
            return null;
        //百公里加速
        TestStandardResult.TestDataItemListDTO level3ListDTO = level2ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level3Name)).findFirst().orElse(null);
        if (level3ListDTO==null|| level3ListDTO.getTestDataItemList() == null || StringUtil.isNullOrEmpty(level4Name))
            return null;
        //0-100km/h加速时间
        TestStandardResult.TestDataItemListDTO testDataItemListDTO = level3ListDTO.getTestDataItemList().stream().filter(a -> a.getName().trim().equals(level4Name)).findFirst().orElse(null);
        if (testDataItemListDTO!=null) {
            //为了拿到二级tab的id
            testDataItemListDTO.setParentId(level2ListDTO.getItemId());
        }
        return testDataItemListDTO;
    }

    private int getBaikeId(ConcurrentHashMap<String, ConfigBaikeLinkDto> baikeMap, String itemname) {
        int baikeid = -1;
        //设置百科数据
        if (baikeMap != null && baikeMap.containsKey(itemname)) {
            ConfigBaikeLinkDto baike = baikeMap.get(itemname);
            if (baike != null) {
                baikeid = baike.getId();
            }
        }
        return baikeid;
    }

    private Specitem.Builder buildSpecitem(
            GetSpecParamConfigInfoRequest request,
            SpecDetailDto specInfo,
            SpecCityAskPriceDto specCityAskPriceDto,
            ConcurrentHashMap<Integer, SListAreaButtonResult> smartAreaButtonMap,
            List<String> conList){
        String dynamicprice = "";
        if (specInfo.getState() == 10) {
            dynamicprice = (specInfo.isBooked() ? "订金:" : "预售价:") + PriceUtil.getStrPrice(specInfo.getMinPrice(), specInfo.getMaxPrice());
        } else {
            dynamicprice = "指导价:" + PriceUtil.getStrPrice(specInfo.getMinPrice(), specInfo.getMaxPrice());
        }

        Specitem.Builder specitem = Specitem.newBuilder();
        specitem.setYear(specInfo.getYearName());
        specitem.setBrandid(specInfo.getBrandId());
        specitem.setSpecid(specInfo.getSpecId());
        specitem.setCount(0);
        specitem.setParamisshow(specInfo.getParamIsShow());
        specitem.setSeriesid(specInfo.getSeriesId());
        specitem.setSeriesname(specInfo.getSeriesName());
        specitem.addAllPicitems(new ArrayList());
        specitem.setCanaskprice(0);  //询价逻辑由经销商接口来判断，默认不可询价
        specitem.setPresell(specInfo.getState() == 10 ? 1 : 0);
        specitem.setMinprice(StringUtils.subAfter(dynamicprice, ":", true));
        specitem.setSpecname(specInfo.getSpecName());
        specitem.setNoshowprice(specInfo.getMinPrice());
        specitem.setDownprice("");
        specitem.setSpecstatus(specInfo.getState());
        if (specInfo.getMinPrice() > 0) {
            specitem.setDealerprice(String.format("%.2f", specInfo.getMinPrice() / 10000.0).toString() + "万");
        } else {
            specitem.setDealerprice("--");
        }
        if (specInfo.getState() == 10 && specInfo.isBooked()) {
            specitem.setPresell(0);
            specitem.setMinprice(dynamicprice);
            specitem.setDealerprice(dynamicprice);
        }
        specitem.setDynamicprice(dynamicprice);
        if (StringUtils.isNotEmpty(dynamicprice) && dynamicprice.contains(":")) {
            specitem.setPricetitle(StringUtils.subBefore(dynamicprice, ":", true) + "：");
        }

        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.66.0")){
            Optional<SpecCityAskPriceDto> specAskPriceOpt = Optional.ofNullable(specCityAskPriceDto);//specCityAskPriceDto可能为null
            specAskPriceOpt.ifPresent(dto -> {//dto不为null-执行下面的逻辑
                int minPrice = dto.getMinPrice();//获取参考价
                specitem.setReferprice(minPrice <= 0 ? "暂无报价" : CommonHelper.df02.format(minPrice / 10000.0) + "万起");
                specitem.setReferpricetitle("参考价：");
            });
        }
        specitem.setSpecisbooked(specInfo.isBooked() ? 1 : 0);
        specitem.setDealerpricetip("厂商指导价：");

        if(Objects.nonNull(specCityAskPriceDto)){
            SpecCityAskPriceDto askPrice = specCityAskPriceDto;
            specitem.setDealerprice("暂无报价");
            specitem.setNoshowprice(askPrice.getMinPrice());
            if (askPrice.getMinPrice() > 0) {
                specitem.setCanaskprice(1);
                specitem.setDealerprice(CommonHelper.df02.format(askPrice.getMinPrice() / 10000.0) + "万起");
                specitem.setDealerpricetip("");
            } else {
                specitem.setDealerprice("--");
                specitem.setDealerpricetip("");
            }
            if (specitem.getNoshowprice() > askPrice.getMinPrice()) {
                specitem.setDownprice(CommonHelper.df02.format((specitem.getNoshowprice() - askPrice.getMinPrice()) / 10000.0) + "万");
            }
            //订金显示优化
            if (specitem.getSpecisbooked() == 1) {
                specitem.setDealerprice(specitem.getDynamicprice());
            }
        }

        Specitem.Moresendinfo.Builder moresendinfo= buildSpecMoresendinfo();
        if(moresendinfo != null){
            specitem.setMoresendinfo(moresendinfo);
        }

        Specitem.Askpriceinfo.Builder askpriceinfo = buildSpecInfoAskPriceInfo(request, specInfo, specCityAskPriceDto, smartAreaButtonMap);
        if(askpriceinfo != null){
            specitem.setAskpriceinfo(askpriceinfo);
        }

        Specitem.Iminfo.Builder iminfo = buildIminfo(request, specInfo, specCityAskPriceDto, smartAreaButtonMap);
        if (iminfo != null) {
            specitem.setIminfo(iminfo);
        }

        //设置筛选项
        if(conList!=null){
            specitem.addAllCondition(conList);
        }

        return specitem;
    }

    private Specitem.Iminfo.Builder buildIminfo(GetSpecParamConfigInfoRequest request, SpecDetailDto specInfo, SpecCityAskPriceDto specCityAskPriceDto, ConcurrentHashMap<Integer, SListAreaButtonResult> smartAreaButtonMap){
        try {
            Specitem.Iminfo.Builder iminfo = Specitem.Iminfo.newBuilder();
            if(specInfo != null && Objects.nonNull(specCityAskPriceDto) && smartAreaButtonMap != null && smartAreaButtonMap.containsKey(specInfo.getSpecId())){
                if(specCityAskPriceDto.getMinPrice() > 0){
                    SListAreaButtonResult dealerIMInfo = smartAreaButtonMap.get(specInfo.getSpecId());
                    int pm = request.getPm();
                    List<SListAreaButtonResult.ButtonListDTO> buttonList = dealerIMInfo.getButtonList();
                    if (pm == 3) {
                        //鸿蒙不处理im
                    } else {
                        // 非鸿蒙的处理逻辑
                        if (!CollectionUtils.isEmpty(buttonList)) {
                            Optional<SListAreaButtonResult.ButtonListDTO> btnType3 = buttonList.stream()
                                    .filter(x -> x.getSpecId() == specInfo.getSpecId() && x.getBtnType() == 3)
                                    .findFirst();
                            if (btnType3.isPresent()) {
                                // btnType == 3 im按钮处理
                                String eid = StringUtils.format("&eid={0}", UrlUtil.encode(request.getPm() == 1 ? "3|1411002|572|3285|205313|304229" : "3|1412002|572|3285|205313|304228"));
                                iminfo.setImtitle(btnType3.get().getMainText());
                                iminfo.setImlinkurl(btnType3.get().getImSchema() + eid);
                                iminfo.setImiconurl("");
                            }
                        }
                    }
                }
            }
            return iminfo;
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildIminfo error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private Specitem.Moresendinfo.Builder buildSpecMoresendinfo(){
        Specitem.Moresendinfo.Builder moresendinfo = Specitem.Moresendinfo.newBuilder();
        moresendinfo.setMoresendbtnname("");
        moresendinfo.setMoresendsubbtnname("");
        moresendinfo.setMoresendlinkurl("");
        moresendinfo.setItemId("");
        moresendinfo.setItemType("");
        moresendinfo.setPosition("");
        moresendinfo.setProductType("");
        moresendinfo.setYldfLocationid("");
        return moresendinfo;
    }

    private Footaskpriceinfo.Builder buildSpecFootaskpriceinfo(
            GetSpecParamConfigInfoRequest request,
            List<SpecDetailDto> specDetailDtos,
            ConcurrentHashMap<Integer, SpecCityAskPriceDto> specCityAskPriceMap,
            ConcurrentHashMap<Integer, SListAreaButtonResult> smartAreaButtonMap){
        try {
            Footaskpriceinfo.Builder footaskpriceinfo = Footaskpriceinfo.newBuilder();
            if (ListUtil.isNotEmpty(specDetailDtos)) {
                specDetailDtos.stream().forEach(specDetail -> {
                    if(specDetail != null && specCityAskPriceMap.containsKey(specDetail.getSpecId())){
                        SpecCityAskPriceDto askPriceDto = specCityAskPriceMap.get(specDetail.getSpecId());
                        boolean canaskprice = askPriceDto.getMinPrice() > 0;
                        if (canaskprice) {
                            Integer key = specDetail.getSpecId();
                            if (smartAreaButtonMap != null && smartAreaButtonMap.containsKey(key)) {
                                SListAreaButtonResult sListAreaButtonResult = smartAreaButtonMap.get(key);
                                Map<String, String> dataMap = getAskpriceSchema(request, specDetail, sListAreaButtonResult.getButtonList());
                                if (dataMap != null) {
                                    footaskpriceinfo.setCanaskprice(1);
                                    footaskpriceinfo.setAskpricescheme(dataMap.get("scheme"));
                                    footaskpriceinfo.setAskpricetitle(dataMap.get("mainText"));
                                    footaskpriceinfo.setAskpricesubtitle(dataMap.get("subtitle"));
                                }
                                int pm = request.getPm();
                                List<SListAreaButtonResult.ButtonListDTO> buttonList = sListAreaButtonResult.getButtonList();
                                if (pm == 3) {
                                    //鸿蒙不处理im
                                } else {
                                    // 非鸿蒙的处理逻辑
                                    if (!CollectionUtils.isEmpty(buttonList)) {
                                        Optional<SListAreaButtonResult.ButtonListDTO> btnType3 = buttonList.stream()
                                                .filter(x -> x.getSpecId() == specDetail.getSpecId() && x.getBtnType() == 3)
                                                .findFirst();
                                        if (btnType3.isPresent()) {
                                            // btnType == 3 im按钮处理
                                            String eid = StringUtils.format("&eid={0}", UrlUtil.encode(request.getPm() == 1 ? "3|1411002|572|3285|205313|304229" : "3|1412002|572|3285|205313|304228"));
                                            footaskpriceinfo.setImtitle(btnType3.get().getMainText());
                                            footaskpriceinfo.setImsubtitle(btnType3.get().getSubText());
                                            footaskpriceinfo.setImlinkurl(btnType3.get().getImSchema() + eid);
                                            footaskpriceinfo.setImiconurl("");
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }


            return footaskpriceinfo;
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildSpecFootaskpriceinfo error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private Specitem.Askpriceinfo.Builder buildSpecInfoAskPriceInfo(
            GetSpecParamConfigInfoRequest request,
            SpecDetailDto specDetail,
            SpecCityAskPriceDto specCityAskPriceDto,
            ConcurrentHashMap<Integer, SListAreaButtonResult> smartAreaButtonMap) {
        try {
            Specitem.Askpriceinfo.Builder askpriceinfo = Specitem.Askpriceinfo.newBuilder();
            askpriceinfo.setAskpricesubtitle("");
            askpriceinfo.setAskpricetitle("询底价");
            askpriceinfo.setAskpriceurl("");
            askpriceinfo.setCopa("");
            askpriceinfo.setType(0);
            if (Objects.nonNull(specCityAskPriceDto)) {
                askpriceinfo.setCanaskprice(specCityAskPriceDto.getMinPrice() > 0 ? 1 : 0);
                if (askpriceinfo.getCanaskprice() == 1) {
                    Integer key = specDetail.getSpecId();
                    if (smartAreaButtonMap != null && smartAreaButtonMap.containsKey(key)) {
                        Map<String, String> dataMap = getAskpriceSchema(request, specDetail, smartAreaButtonMap.get(key).getButtonList());
                        if (dataMap != null) {
                            askpriceinfo.setScheme(dataMap.get("scheme"));
                            askpriceinfo.setAskpricetitle(dataMap.get("mainText"));
                            askpriceinfo.setAskpricesubtitle(dataMap.get("subtitle"));
                            if(StringUtils.isNotEmpty(dataMap.get("ext"))){
                                askpriceinfo.setExt(dataMap.get("ext"));
                            }

                        }
                    }
                }
            }
            return askpriceinfo;
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildSpecInfoAskPriceInfo error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private Map<String, String> getAskpriceSchema(GetSpecParamConfigInfoRequest request, SpecDetailDto specDetail,List<SListAreaButtonResult.ButtonListDTO> buttonList){
        Map<String, String> dataMap = new HashMap<>();
        int site = request.getSite();
        int pm = request.getPm();
        int seriesId = specDetail.getSeriesId();
        HashMap<String,String> eidMap = new HashMap<>(); //key规则： site_pm 页面来源_客户端类型
        eidMap.put("1_1","3|1411002|572|3285|203989|302210" );
        eidMap.put("1_2","3|1412002|572|3285|203987|302209" );
        eidMap.put("1_3","3|1474001|48|35|204434|306043" );
        eidMap.put("2_1","3|1411002|572|3286|200072|300000" );
        eidMap.put("2_2","3|1412002|572|3286|200072|300000" );
        eidMap.put("2_3","3|1474001|108|219|204436|306043" );
        eidMap.put("3_1","3|1411112|1197|11411|203703|301601" );
        eidMap.put("3_2","3|1412112|1197|11411|203703|301602" );
        eidMap.put("3_3","3|1474002|20|0|206412|306043" );
        String eid = eidMap.get(site+"_"+pm);
        if (pm == 3) {
            if (buttonList != null && buttonList.size() > 0) {
                Optional<SListAreaButtonResult.ButtonListDTO> first = buttonList.stream()
                        .filter(x -> x.getSpecId() == specDetail.getSpecId() && x.getBtnType() == 2)
                        .findFirst();
                if (first.isPresent()) {
                    dataMap.put("scheme", first.get().getUrl());
                    dataMap.put("mainText", first.get().getMainText());
                    dataMap.put("subtitle", "");
                    dataMap.put("ext", first.get().getExt());
                }
            }else{
                return null;
            }
        } else {
            // 非鸿蒙的处理逻辑
            if (!CollectionUtils.isEmpty(buttonList)) {
                Optional<SListAreaButtonResult.ButtonListDTO> btnType2 = buttonList.stream()
                        .filter(x -> x.getSpecId() == specDetail.getSpecId() && x.getBtnType() == 2)
                        .findFirst();
                if (btnType2.isPresent()) {
                    // btnType == 2 询价按钮处理
                    SListAreaButtonResult.ButtonListDTO button = btnType2.get();
                    // 询价按钮
                    String scheme="";
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(button.getUrl())) {
                        scheme = button.getUrl();
                    } else {
                        String askPriceSchemaTemp = "autohome://car/asklowprice?customshowanimationtype=2&eid=%s&seriesid=%s&specid=%s&inquirytype=2&price_show=%s&title=%s&ext=%s";
                        if (button.getWindowType() == 14) {
                            scheme = String.format("autohome://dealerconsult/dealerprice?seriesid=%s&specid=%s&eid=%s",
                                    seriesId, specDetail.getSpecId(), eid);
                        } else {
                            scheme = String.format(askPriceSchemaTemp, eid, seriesId, specDetail.getSpecId(),
                                    DealerCommHelp.getPriceShowFromWindowType(button.getWindowType()),
                                    org.apache.commons.lang3.StringUtils.isNotBlank(button.getMainText())
                                            ? UrlUtil.encode(button.getMainText()) : "",
                                    UrlUtil.encode(button.getExt()));
                        }

                    }
                    dataMap.put("scheme", scheme);
                    dataMap.put("mainText", button.getMainText());
                    dataMap.put("subtitle", "");
                    dataMap.put("ext", button.getExt());
                }


            } else {
                // 没有就走打底逻辑
                String rnUrl = String.format("rn://DealerPriceRn/ReverseAuctionDialog?seriesid=%s&specid=%s&siteid=21&gps=1&eid=%s",
                        seriesId, specDetail.getSpecId(), UrlUtil.encode(eid));
                dataMap.put("scheme", String.format("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s",
                        UrlUtil.encode(rnUrl)));
                dataMap.put("mainText", "查报价单");
                dataMap.put("subtitle", "");
                dataMap.put("ext", "{\"price_show\":36}");
            }
        }

        return dataMap;
    }

    private int getPriceShowFromWindowType(int windowType) {
        if (windowType == 1) {
            return 0;
        } else if (windowType == 2) {
            return 2;
        } else {
            return windowType + 2;
        }
    }

    /**
     * 构建参配项标题列表
     *
     * @param resultBuilder
     * @return
     */
    private List<GetSpecParamConfigInfoResponse.Result.Titlelist> buildTitleList(GetSpecParamConfigInfoResponse.Result.Builder resultBuilder) {
        List<GetSpecParamConfigInfoResponse.Result.Titlelist.Builder> titleListBuilder = new ArrayList<>();
        try {
            List<GetSpecParamConfigInfoResponse.Result.Datalist> datalist = resultBuilder.getDatalistList();
            if (ListUtil.isNotEmpty(datalist)) {
                GetSpecParamConfigInfoResponse.Result.Datalist dataitem = datalist.get(0);
                if (ListUtil.isNotEmpty(dataitem.getParamitemsList())) {
                    dataitem.getParamitemsList().forEach(groupInfo -> {
                        GetSpecParamConfigInfoResponse.Result.Titlelist.Builder titleBuilder = GetSpecParamConfigInfoResponse.Result.Titlelist.newBuilder();
                        titleBuilder.setGroupname(groupInfo.getGroupname());
                        titleBuilder.setItemtype(groupInfo.getItemtype());
                        titleBuilder.setShowtips(groupInfo.getShowtips());
                        groupInfo.getItemsList().forEach(item -> {
                            GetSpecParamConfigInfoResponse.Result.Titlelist.Item.Builder itemBuilder = GetSpecParamConfigInfoResponse.Result.Titlelist.Item.newBuilder();
                            itemBuilder.setParamitemid(item.getParamitemid());
                            itemBuilder.setName(item.getName());
                            itemBuilder.setDatatype(item.getDatatype());
                            itemBuilder.setId(item.getId());
                            titleBuilder.addItems(itemBuilder);
                        });
                        titleListBuilder.add(titleBuilder);
                    });
                }
                if (ListUtil.isNotEmpty(dataitem.getConfigitemsList())) {
                    dataitem.getConfigitemsList().forEach(groupInfo -> {
                        GetSpecParamConfigInfoResponse.Result.Titlelist.Builder titleBuilder = GetSpecParamConfigInfoResponse.Result.Titlelist.newBuilder();
                        titleBuilder.setGroupname(groupInfo.getGroupname());
                        titleBuilder.setItemtype(groupInfo.getItemtype());
                        titleBuilder.setShowtips(groupInfo.getShowtips());
                        groupInfo.getItemsList().forEach(item -> {
                            GetSpecParamConfigInfoResponse.Result.Titlelist.Item.Builder itemBuilder = GetSpecParamConfigInfoResponse.Result.Titlelist.Item.newBuilder();
                            itemBuilder.setParamitemid(item.getParamitemid());
                            itemBuilder.setName(item.getName());
                            itemBuilder.setDatatype(item.getDatatype());
                            itemBuilder.setId(item.getId());
                            titleBuilder.addItems(itemBuilder);
                        });
                        titleListBuilder.add(titleBuilder);
                    });
                }
            }
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildTitleList error: {}", ExceptionUtils.getStackTrace(e));
        }
        return titleListBuilder.stream().map(GetSpecParamConfigInfoResponse.Result.Titlelist.Builder::build).collect(Collectors.toList());
    }

    /**
     * 构建参配筛选项列表
     *
     * @param request
     * @param resultBuilder
     * @param specYearList
     * @param specDetailDtos
     * @return
     */
    private Map<String,List<String>> buildSeriesConditionlist(GetSpecParamConfigInfoRequest request, GetSpecParamConfigInfoResponse.Result.Builder resultBuilder, List<SpecGroupOfSeriesDto> specYearList, List<SpecDetailDto> specDetailDtos) {
        //取车型参数信息
        Map<String,List<String>> specConMap =new HashMap<>();

        //年款
        Map<String, String> unsale_yearMap = new HashMap<>();
        Map<String, String> onsale_yearMap = new HashMap<>();
        Map<String, String> stopsale_yearMap = new HashMap<>();
        //排量
        Map<String, String> disMap = new HashMap<>();
        //变速箱
        Map<String, String> goxMap = new HashMap<>();
        //环保标准
        Map<String, String> hbbzMap = new LinkedHashMap<>();
        //车身结构
        Map<String, String> structMap = new HashMap<>();
        //驱动形式
        Map<String, String> driveTypeMap = new HashMap<>();
        //座位数
        Map<String, String> seatMap = new HashMap<>();
        if(Objects.nonNull(specYearList) && !specYearList.isEmpty()){
            List<SpecDetailDto> specList =specDetailDtos;
            // 分组处理在售 和停售
            for (SpecGroupOfSeriesDto configSpecGroupDto : specYearList) {
                String yearname = configSpecGroupDto.getYearname();
                int yearstate = configSpecGroupDto.getYearstate();
                String yearvalue = configSpecGroupDto.getYearvalue()+"";
                if(Arrays.asList(10,20,30).contains(yearstate)){
                    configSpecGroupDto.getYearspeclist().forEach(year->{

                        List<SpecGroupOfSeriesDto.Spec> speclist = year.getSpeclist();
                        boolean hasShow = speclist.stream().anyMatch(spec -> spec.getParamIsShow() == 1);
                        if (hasShow) {
                            if (yearstate == 10) {
                                unsale_yearMap.put(yearvalue, yearname);
                                boolean hasOnSale = speclist.stream().anyMatch(spec -> spec.getState() == 20);
                                if (hasOnSale) {
                                    onsale_yearMap.put(yearvalue, yearname);
                                }
                            } else {
                                onsale_yearMap.put(yearvalue, yearname);
                            }
                        }
                        if (speclist != null) {
                            boolean hasStop = speclist.stream().anyMatch(spec -> spec.getState() == 40);
                            if (hasStop) {
                                stopsale_yearMap.put(configSpecGroupDto.getYearvalue() + "", configSpecGroupDto.getYearname());
                            }
                        }
                    });
                }else  if(yearstate==40){
                    stopsale_yearMap.put(configSpecGroupDto.getYearvalue()+"",configSpecGroupDto.getYearname());
                }
            }

            specList.forEach(item -> {
                List<String> conList = new ArrayList<>();
                if (item.getYearName() > 0) {
                    conList.add(item.getYearName() + "");
                }
                int isEc = 0;
                if (item.getFuelType() == 4) {
                    if (Objects.nonNull(item.getDisplacement()) && item.getDisplacement().doubleValue() > 0d) {
                        String disStr = item.getDisplacement().doubleValue() + (item.getFlowModeId() == 1 ? "L" : "T");
                        disMap.put(disStr, disStr);
                        conList.add(disStr);
                    } else {
                        isEc = 1;
                        disMap.put("新能源", "新能源");
                        conList.add("新能源");
                    }
                } else {
                    if (Objects.nonNull(item.getDisplacement()) && item.getDisplacement().doubleValue() > 0) {
                        String disStr = item.getDisplacement().doubleValue() + (item.getFlowModeId() == 1 ? "L" : "T");
                        disMap.put(disStr, disStr);
                        conList.add(disStr);
                    }

                }

                if (StringUtils.isNotEmpty(item.getGearbox())) {
                    goxMap.put(item.getGearbox(), item.getGearbox());
                    conList.add(item.getGearbox());
                }
                if (StringUtils.isNotEmpty(item.getStructtype())) {
                    structMap.put(item.getStructtype(), item.getStructtype());
                    conList.add(item.getStructtype());
                }

                if(isEc == 1){
                    hbbzMap.put("纯电", "纯电");
                    conList.add("纯电");
                }else if(StringUtils.isNotEmpty(item.getEmissionStandards())){
                    hbbzMap.put(item.getEmissionStandards(), item.getEmissionStandards());
                    conList.add(item.getEmissionStandards());
                }

                String newModeName = convertDrivemodeName(item.getDrivingModeName());
                if (StringUtils.isNotEmpty(newModeName)) {
                    driveTypeMap.put(newModeName, newModeName);
                    conList.add(newModeName);
                }
                if (StringUtils.isNotEmpty(item.getSeats())) {
                    seatMap.put(item.getSeats(), item.getSeats() + "座");
                    conList.add(item.getSeats());
                }

                specConMap.put(item.getSpecId()+"",conList);
            });
            int index = 0;
            //设置年款筛选项
            Conditionlist.Builder yearCon =Conditionlist.newBuilder();
            yearCon.setIndex(index);
            yearCon.setIsselectmore(1);
            yearCon.setTypeid(index);
            yearCon.setName("年款");
            yearCon.setTypevalue("year");
            List<Conditionlist.List.Builder> itemList =new ArrayList<>();
            Map<String, String> sale_yearMap = new HashMap<>();
            sale_yearMap.putAll(unsale_yearMap);
            sale_yearMap.putAll(onsale_yearMap);
            for (String key : sale_yearMap.keySet()) {
                Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
                if (key == null || "0".equals(key) ) {
                    continue;
                }
                dtoItem.setId(key);
                dtoItem.setLazyload(0);
                dtoItem.setName(sale_yearMap.get(key));
                itemList.add(dtoItem);
            }

            int stopIndex = 0;
            for (String key : stopsale_yearMap.keySet()) {
                Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
                if (key == null || "0".equals(key) ) {
                    continue;
                }
                dtoItem.setId(key);
                //无在售年款时 lazyload=0
//                int lazyload=0;
//                if(sale_yearMap.isEmpty()){
//                    lazyload = stopIndex==0?0:1;
//                }else{
//                    lazyload = 1;
//                }
                dtoItem.setLazyload(sale_yearMap.isEmpty()?0:1);
                dtoItem.setName(stopsale_yearMap.get(key));
                itemList.add(dtoItem);
                stopIndex++;
            }

            itemList.sort(Comparator.comparing(Conditionlist.List.Builder::getLazyload).thenComparing(Conditionlist.List.Builder::getName,Comparator.reverseOrder()));
            if(onsale_yearMap.size()>1){
                Conditionlist.List.Builder onsaleCon =Conditionlist.List.newBuilder();
                onsaleCon.setId("onsale");
                onsaleCon.setLazyload(0);
                onsaleCon.setName("在售");
                itemList.add(0,onsaleCon);
            }
            itemList.forEach(i->{
                yearCon.addList(i);
            });
            resultBuilder.addConditionlist(yearCon.build());
            index++;
//            if(!disMap.isEmpty()){
                Map<String,String> finalDisMap =new LinkedHashMap<>();
                if(!disMap.containsKey("新能源")){
                    disMap.keySet().stream().filter(i-> org.apache.commons.lang3.StringUtils.contains(i,"L")).forEach(i->finalDisMap.put(i,disMap.get(i)));
                    disMap.keySet().stream().filter(i-> org.apache.commons.lang3.StringUtils.contains(i,"T")).forEach(i->finalDisMap.put(i,disMap.get(i)));
                }else{
                    finalDisMap.putAll(disMap);
                }
                resultBuilder.addConditionlist(getMapCondition(finalDisMap, "排量", 1, index++, "displacement",0));
//            }
//            if(!goxMap.isEmpty()){
                resultBuilder.addConditionlist(getMapCondition(goxMap, "变速箱", 1, index++, "gearbox",0));
//            }
            if (!hbbzMap.isEmpty()) {
                resultBuilder.addConditionlist(getMapCondition(hbbzMap, "环保标准", 1, index++, "standards",0));
            }
//            if(!structMap.isEmpty()){
                resultBuilder.addConditionlist(getMapCondition(structMap, "车身结构", 1, index++, "cartype",0));
//            }
//            if(!driveTypeMap.isEmpty()){
                resultBuilder.addConditionlist(getMapCondition(driveTypeMap, "驱动形式", 1, index++, "drivemode",0));
//            }
//            if(!seatMap.isEmpty()){
                resultBuilder.addConditionlist(getMapCondition(seatMap, "座位数", 1, index++, "seatcount",0));
//            }
        }

        return specConMap;
    }

    private void buildSpecConditionlist(GetSpecParamConfigInfoRequest request, GetSpecParamConfigInfoResponse.Result.Builder resultBuilder, List<SpecGroupOfSeriesDto> specYearList, List<SpecDetailDto> specDetailDtos) {
        //年款
        Map<String, String> yearMap = new HashMap<>();
        if(Objects.nonNull(specYearList) && !specYearList.isEmpty()){
            for (SpecGroupOfSeriesDto configSpecGroupDto : specYearList) {
                if (configSpecGroupDto.getYearvalue() > 1000) {
                    if (Objects.nonNull(configSpecGroupDto.getYearspeclist())) {
                        configSpecGroupDto.getYearspeclist().forEach(year -> {
                            boolean hasShow = year.getSpeclist().stream().anyMatch(spec -> spec.getParamIsShow() == 1);
                            if (hasShow) {
                                yearMap.put(configSpecGroupDto.getYearvalue() + "", configSpecGroupDto.getYearname());
                            }
                        });
                    }
                }
            }
            int index = 0;
            //设置年款筛选项
            Conditionlist.Builder yearCon =Conditionlist.newBuilder();
            yearCon.setIndex(index);
            yearCon.setIsselectmore(1);
            yearCon.setTypeid(index);
            yearCon.setName("年款");
            yearCon.setTypevalue("year");
            List<Conditionlist.List.Builder> itemList =new ArrayList<>();
            for (String key : yearMap.keySet()) {
                Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
                if (key == null || "0".equals(key) ) {
                    continue;
                }
                dtoItem.setId(key);
                dtoItem.setLazyload(0);
                dtoItem.setName(yearMap.get(key));
                itemList.add(dtoItem);
            }

            itemList.sort(Comparator.comparing(Conditionlist.List.Builder::getName).reversed());

            if(yearMap.size()>0){
                Conditionlist.List.Builder onsaleCon =Conditionlist.List.newBuilder();
                onsaleCon.setId("currentspec");
                onsaleCon.setLazyload(0);
                onsaleCon.setName("当前车款");
                itemList.add(0,onsaleCon);
            }
            itemList.forEach(i->{
                yearCon.addList(i);
            });
            resultBuilder.addConditionlist(yearCon.build());

        }
    }
    private String convertDrivemodeName(String drivemodeName) {
        if (StringUtils.isNotEmpty(drivemodeName)) {
            if ("双电机后驱/前置前驱/前置后驱/中置后驱/后置后驱".contains(drivemodeName)) {
                return "两驱";
            } else if ("三电机四驱/前置四驱/中置四驱/后置四驱/双电机四驱/四电机四驱/电子适时四驱".contains(drivemodeName)) {
                return "四驱";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private Conditionlist.Builder  getMapCondition(Map<String, String> map, String name, int isSelectMore, int index, String typekey,int lazyload) {
        Conditionlist.Builder builder =Conditionlist.newBuilder();
        builder.setIndex(index);
        builder.setIsselectmore(isSelectMore);
        builder.setTypeid(index);
        builder.setName(name);
        builder.setTypevalue(typekey);
        List<Conditionlist.List> itemList =new ArrayList<>();
        for (String key : map.keySet()) {
            Conditionlist.List.Builder dtoItem = Conditionlist.List.newBuilder();
            if (key == null || "0".equals(key) ) {
                continue;
            }
            dtoItem.setId(key);
            dtoItem.setLazyload(lazyload);
            dtoItem.setName(map.get(key));
            itemList.add(dtoItem.build());
        }
//        itemList.sort(Comparator.comparing(Conditionlist.List::getName).reversed());
        builder.addAllList(itemList);
        return builder;
    }

    private String getDynamicPrice(SpecDetailDto specDetail) {
        String dynamicprice = "";
        if (specDetail != null) {
            dynamicprice = specDetail.getState() == 10
                    ? (specDetail.isBooked() ? "订金:" + PriceUtil.getStrPrice(specDetail.getMinPrice(), specDetail.getMaxPrice()) : "预售价:" + PriceUtil.getStrPrice(specDetail.getMinPrice(), specDetail.getMaxPrice()))
                    : "指导价:" + PriceUtil.getStrPrice(specDetail.getMinPrice(), specDetail.getMaxPrice());
        }
        return dynamicprice;
    }

    /**
     * 专业评测-汽车之家实测
     *
     * @param specDetailDto
     * @param specEvaluateDto
     * @return
     */
    private Configitem buildSpecEvaluate(
            SpecDetailDto specDetailDto,
            SpecEvaluateDto specEvaluateDto
    ) {
        try {
            Item.Builder ah100Item = Item.newBuilder();
            ah100Item.setSubid(100004);
            ah100Item.setId(-1);
            ah100Item.setName("AH-100评分");
            Item.Modelexcessid.Builder ah100Modelexcessid = Item.Modelexcessid.newBuilder();
            ah100Modelexcessid.setId(specDetailDto.getSpecId());
            ah100Modelexcessid.setValue("-");

            Item.Builder jiasuItem = Item.newBuilder();
            jiasuItem.setId(-1);
            jiasuItem.setName("加速性能(s)");
            Item.Modelexcessid.Builder jiasuModelexcessid = Item.Modelexcessid.newBuilder();
            jiasuModelexcessid.setId(specDetailDto.getSpecId());
            jiasuModelexcessid.setValue("-");

            Item.Builder shacheItem = Item.newBuilder();
            shacheItem.setId(-1);
            shacheItem.setName("刹车性能(m)");
            Item.Modelexcessid.Builder shacheModelexcessid = Item.Modelexcessid.newBuilder();
            shacheModelexcessid.setId(specDetailDto.getSpecId());
            shacheModelexcessid.setValue("-");

            Item.Builder youhaoItem = Item.newBuilder();
            youhaoItem.setId(-1);
            youhaoItem.setName("实测油耗(L/100km)");
            Item.Modelexcessid.Builder youhaoModelexcessid = Item.Modelexcessid.newBuilder();
            youhaoModelexcessid.setId(specDetailDto.getSpecId());
            youhaoModelexcessid.setValue("-");

            Item.Builder zaoyinItem = Item.newBuilder();
            zaoyinItem.setId(-1);
            zaoyinItem.setName("实测噪音(分贝)");
            Item.Modelexcessid.Builder zaoyinModelexcessid = Item.Modelexcessid.newBuilder();
            zaoyinModelexcessid.setId(specDetailDto.getSpecId());
            zaoyinModelexcessid.setValue("-");
            if (Objects.nonNull(specEvaluateDto)) {
                SpecEvaluateItemResult item = specEvaluateDto.getEvaluateItemResult();
                if (item != null) {
                    if (item.getAutomodelscore() > 0) {
                        ah100Modelexcessid.setValue(item.getAutomodelscore() + "分");
                    }
                    if (item.getArticleid() > 0) {
                        ah100Modelexcessid.setAh100Url("autohome://article/articledetail?newsid=" + item.getArticleid() + "&newstype=64");
                    }
                    if (item.getEvaluateitems() != null && item.getEvaluateitems().size() > 0) {
                        item.getEvaluateitems().forEach(s1 -> {
                            Item.Modelexcessid.Sublist.Builder sublist = Item.Modelexcessid.Sublist.newBuilder();
                            switch (s1.getEvaluateitemid()) {
                                case 33:// 加速
                                case 35:
                                case 36:
                                    sublist.setName(s1.getEvaluateitemname() + ":" + s1.getData());
                                    sublist.setValue("");
                                    sublist.setPriceinfo("");
                                    jiasuModelexcessid.setValue("");
                                    jiasuModelexcessid.addSublist(sublist);
                                    break;
                                case 50:// 刹车
                                    sublist.setName(s1.getEvaluateitemname() + ":" + s1.getData());
                                    sublist.setValue("");
                                    sublist.setPriceinfo("");
                                    shacheModelexcessid.setValue("");
                                    shacheModelexcessid.addSublist(sublist);
                                    break;
                                case 45:// 油耗
                                    sublist.setName(s1.getEvaluateitemname() + ":" + s1.getData());
                                    sublist.setValue("");
                                    sublist.setPriceinfo("");
                                    youhaoModelexcessid.setValue("");
                                    youhaoModelexcessid.addSublist(sublist);
                                    break;
                                case 60:// 噪音
                                case 61:
                                case 62:
                                    sublist.setName(s1.getEvaluateitemname() + ":" + s1.getData());
                                    sublist.setValue("");
                                    sublist.setPriceinfo("");
                                    zaoyinModelexcessid.setValue("");
                                    zaoyinModelexcessid.addSublist(sublist);
                                    break;
                                default:
                                    break;
                            }
                        });
                    }
                }
            }

            ah100Item.addModelexcessids(ah100Modelexcessid);
            jiasuItem.addModelexcessids(jiasuModelexcessid);
            shacheItem.addModelexcessids(shacheModelexcessid);
            youhaoItem.addModelexcessids(youhaoModelexcessid);
            zaoyinItem.addModelexcessids(zaoyinModelexcessid);

            Configitem.Builder configitem = Configitem.newBuilder();
            configitem.setGroupname("专业评测");
            configitem.setItemtype("汽车之家实测");
            configitem.setShowtips(true);
            configitem.addItems(ah100Item);
            configitem.addItems(jiasuItem);
            configitem.addItems(shacheItem);
            configitem.addItems(youhaoItem);
            configitem.addItems(zaoyinItem);
            return configitem.build();

        } catch (Exception e) {
            logger.error("车型参配接口异常-buildSpecEvaluate error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 个性化-选装包
     *
     * @param specDetailDto
     * @param specConfigBagDtoMap
     * @return
     */
    private Configitem buildSelectConfigBag(
            SpecDetailDto specDetailDto,
            LinkedHashMap<String, Map<Integer,SpecConfigBagDto.ConfigBagValue>> specConfigBagDtoMap
    ) {
        try {
            if (!specConfigBagDtoMap.isEmpty()) {
                List<Item> configitems = new ArrayList<>();
                for (Map.Entry<String, Map<Integer,SpecConfigBagDto.ConfigBagValue>> configBagsMap : specConfigBagDtoMap.entrySet()) {
                    Map<Integer, SpecConfigBagDto.ConfigBagValue> specDataMap = configBagsMap.getValue();
                    List<SpecConfigBagDto.ConfigBagValue> itemlist = specDataMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
                    SpecConfigBagDto.ConfigBagValue  configBagValue =itemlist.get(0);

                    Item.Builder item = Item.newBuilder();
                    item.setName(configBagValue.getName());
                    item.setParamitemid(0);
                    item.setSubid(20231019);
                    item.setId(-1);
                    item.setVideoid("");
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specDetailDto.getSpecId());
                    modelexcessid.setPriceinfo("");
                    modelexcessid.setValue("-");
                    SpecConfigBagDto.ConfigBagValue configBagValue1 = specDataMap.get(specDetailDto.getSpecId());
                    if (configBagValue1 != null) {
                        //截取部分字符串，这里一个汉字的长度认为是2
                        if (StringUtils.isNotEmpty(configBagValue1.getDescription())) {
                            String str = subPreGbk(configBagValue1.getDescription().replace("&#8304;", "度").replace("&#8482;", "™").replace("&#174", "®"), 600, "...");
                            modelexcessid.setValue(str != null ? str : "");
                        }
                        Item.Modelexcessid.Sublist.Builder sublist = Item.Modelexcessid.Sublist.newBuilder();
                        sublist.setName("选配");
                        sublist.setPriceinfo(configBagValue1.getPricedesc());
                        sublist.setValue("○");
                        modelexcessid.addSublist(sublist);
                    }
                    item.addModelexcessids(modelexcessid);
                    configitems.add(item.build());
                }

                //组装
                if (ListUtil.isNotEmpty(configitems)) {
                    Configitem.Builder configitem = Configitem.newBuilder();
                    configitem.setGroupname("个性化");
                    configitem.setItemtype("选装包");
                    configitem.setShowtips(true);
                    configitem.addAllItems(configitems);
                    return configitem.build();
                }
            }
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildSelectConfigBag error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 个性化-特色配置
     *
     * @return
     */
    private Configitem buildSpecialConfig(
            SpecDetailDto specDetailDto,
            LinkedHashMap<String, Map<Integer,SpecSpecificConfigDto.ConfigItem>> allSpecialConfingMap
    ) {
        try {
            if (!allSpecialConfingMap.isEmpty()) {
                List<Item> configitems = new ArrayList<>();
                for (Map.Entry<String, Map<Integer,SpecSpecificConfigDto.ConfigItem>> specificConfigMap : allSpecialConfingMap.entrySet()) {
                    Map<Integer, SpecSpecificConfigDto.ConfigItem> specDataMap = specificConfigMap.getValue();
                    List<SpecSpecificConfigDto.ConfigItem> itemlist = specDataMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
                    SpecSpecificConfigDto.ConfigItem  specificConfigItem =itemlist.get(0);
                    SpecSpecificConfigDto.ConfigItem specSpecificConfigItem = specDataMap.get(specDetailDto.getSpecId());
                    Item.Builder item = Item.newBuilder();
                    item.setId(specificConfigItem.getBaikeid() > 0 ? specificConfigItem.getBaikeid() : -1);
                    item.setName(specificConfigItem.getName());
                    Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                    modelexcessid.setId(specDetailDto.getSpecId());
                    modelexcessid.setValue("-");
                    modelexcessid.setPriceinfo("-1");
                    if(specSpecificConfigItem != null){
                        modelexcessid.setValue(specSpecificConfigItem.getValue());
                        if(StringUtils.isNotEmpty(specSpecificConfigItem.getPrice())){
                            int price = (int) Double.parseDouble(specSpecificConfigItem.getPrice());
                            if (price > 0) {
                                modelexcessid.setPriceinfo(PriceUtil.getSpecPrice(price) + "元");
                            }
                        }
                    }
                    item.addModelexcessids(modelexcessid);
                    configitems.add(item.build());
                }
                //组装
                if (ListUtil.isNotEmpty(configitems)) {
                    Configitem.Builder configitem = Configitem.newBuilder();
                    configitem.setGroupname("个性化");
                    configitem.setItemtype("特色配置");
                    configitem.setShowtips(true);
                    configitem.addAllItems(configitems);
                    return configitem.build();
                }
            }
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildSpecialConfig error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 个性化-颜色（内饰颜色、外观颜色）
     *
     * @param specDetailDto
     * @return
     */
    private Configitem buildSpecColorConfig(
            SpecDetailDto specDetailDto,
            boolean isHaveColor,
            SpecOutInnerColorDto specOutColorDto,
            SpecOutInnerColorDto specInnerColorDto,
            SeriesVr seriesVr
    ) {
        try {
            List<Item> items = new ArrayList<>();
            //外观颜色
            if (isHaveColor) {
                Item.Builder outItem = Item.newBuilder();
                outItem.setName("外观颜色");
                outItem.setParamitemid(0);
                outItem.setSubid(20210610);
                outItem.setId(-1);
                Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                modelexcessid.setId(specDetailDto.getSpecId());
                modelexcessid.setValue("-");
                if (specOutColorDto != null) {
                    Item.Modelexcessid.Colorinfo colorinfo = getConfigColorInfo(1, specOutColorDto, seriesVr);
                    if (colorinfo != null) {
                        modelexcessid.setValue("");
                        modelexcessid.setColorinfo(colorinfo);
                    }
                }
                outItem.addModelexcessids(modelexcessid);
                items.add(outItem.build());
            }

            //内饰颜色
            if (isHaveColor) {
                Item.Builder innerItem = Item.newBuilder();
                innerItem.setName("内饰颜色");
                innerItem.setParamitemid(0);
                innerItem.setSubid(20210610);
                innerItem.setId(-1);
                Item.Modelexcessid.Builder modelexcessid = Item.Modelexcessid.newBuilder();
                modelexcessid.setId(specDetailDto.getSpecId());
                modelexcessid.setValue("-");
                if (specInnerColorDto != null) {
                    Item.Modelexcessid.Colorinfo colorinfo = getConfigColorInfo(2, specInnerColorDto, seriesVr);
                    if (colorinfo != null) {
                        modelexcessid.setValue("");
                        modelexcessid.setColorinfo(colorinfo);
                    }
                }
                innerItem.addModelexcessids(modelexcessid);
                items.add(innerItem.build());
            }

            //有配置项才返回
            if (ListUtil.isNotEmpty(items)) {
                Configitem.Builder configitem = Configitem.newBuilder();
                configitem.setGroupname("个性化");
                configitem.setItemtype("颜色");
                configitem.setShowtips(true);
                configitem.addAllItems(items);
                return configitem.build();
            }
        } catch (Exception e) {
            logger.error("车型参配接口异常-buildSpecColorConfig error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 内饰、外观颜色信息
     * @param type 1代表外观，2内饰，用于客户端区分
     * @param specColorDto 颜色信息
     * @param seriesVr 车系vr信息
     * @return
     */
    private Item.Modelexcessid.Colorinfo getConfigColorInfo(int type, SpecOutInnerColorDto specColorDto, SeriesVr seriesVr) {
        try {
            Item.Modelexcessid.Colorinfo.Builder colorInfo = Item.Modelexcessid.Colorinfo.newBuilder();
            colorInfo.setType(type);
            colorInfo.setTitle("共" + specColorDto.getColoritems().size() + "色");
            specColorDto.getColoritems().forEach(item -> {
                Item.Modelexcessid.Colorinfo.List.Builder listItemBuilder = Item.Modelexcessid.Colorinfo.List.newBuilder();
                listItemBuilder.setName(item.getName() != null ? item.getName() : "");
                listItemBuilder.setValue(item.getValue() != null ? item.getValue() : "");
                listItemBuilder.setIsaddprice(item.getPrice() > 0);
                listItemBuilder.setAddpricetext(item.getPrice() > 0 ? "+¥" + item.getPrice() : item.getPrice() < 0 ? "-¥" + item.getPrice() : "价格已包含");
                listItemBuilder.setRemark(item.getRemarks() != null ? item.getRemarks() : "");
                if (type == 1) {
                    listItemBuilder.setPicurl("http://app2.autoimg.cn/appdfs/g26/M07/04/3B/autohomecar__ChxkjmU6Wt6ALwxcAAAUavI2etQ450.png");
                    if (seriesVr != null && seriesVr.getVrMaterial() != null && ListUtil.isNotEmpty(seriesVr.getVrMaterial().getColor_list())) {
                        seriesVr.getVrMaterial().getColor_list().stream().filter(p -> p.getColorName().equals(item.getName())).findFirst().ifPresent(p -> {
                            if(Objects.nonNull(p.getHori())&& Objects.nonNull(p.getHori().getPreview()) && p.getHori().getPreview().size()>0){
                                listItemBuilder.setPicurl(ImageUtils.convertImageUrl(p.getHori().getPreview().get(0).getUrl(), false, false, false, ImageSizeEnum.ImgSize_WxH_400x0));
                            }
                        });
                    }
                }
                colorInfo.addList(listItemBuilder.build());
            });
            return colorInfo.build();
        } catch (Exception e) {
            logger.error("车型参配接口异常-getConfigColorInfo error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 构建车系参配浮窗工具箱
     *
     * @param request
     * @param specDetailList
     * @return
     */
    private CompletableFuture<Toolboxentry> buildToolboxEntry(GetSpecParamConfigInfoRequest request, List<SpecDetailDto> specDetailList) {
        int seriesId = request.getSeriesid();
//        seriesConfigDiffComponent.refresh(seriesId);
        CompletableFuture<SeriesConfigDiffDto> configDifFuture = seriesConfigDiffComponent.get(seriesId);
        return configDifFuture.thenApply(diff -> {
            Toolboxentry.Builder toolboxbuilder = Toolboxentry.newBuilder();
            toolboxbuilder.setEntrypvdata(Pvitem.newBuilder()
                    .putArgvs("seriesid", request.getSeriesid() + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_entry_click").build()));
            //图片差异
            if (diff != null && diff.getPicSpecIds() != null) {
                Optional<SpecDetailDto> first = specDetailList.stream().filter(spec -> diff.getPicSpecIds().contains(spec.getSpecId())).findFirst();
                if (first.isPresent()) {
                    Toolboxentry.List.Builder picItem = Toolboxentry.List.newBuilder();
                    picItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cfg_img_0323.png");
                    picItem.setTitle("图片差异");
                    picItem.setTypeid(1);
                    String link = String.format("autohome://rninsidebrowser?url=%s", UrlUtil.encode(String.format("rn://Car_SeriesSummary/PictureContrast?seriesid=%s&panValid=0&specid=%s&typeid=1&fromtype=2&isfirst=1&locationid=0", first.get().getSeriesId(), first.get().getSpecId())));
                    picItem.setLinkurl(link);
                    picItem.setPvdata(Pvitem.newBuilder()
                            .putArgvs("seriesid", request.getSeriesid() + "")
                            .putArgvs("typeid", "1")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_list_click").build()));
                    toolboxbuilder.addList(picItem);
                }
            }
            //配置差异
            if (diff != null && diff.getConfigDiff() != null) {
                Toolboxentry.List.Builder cfgItem = Toolboxentry.List.newBuilder();
                cfgItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cfg_cydb_0323.png");
                cfgItem.setTitle(String.format("配置差异"));
                cfgItem.setTypeid(2);
                List<Integer> specIds = diff.getConfigDiff().getSpecIds();
                String link = String.format("autohome://car/summaryconfigdif?seriesid=%s&specids=%s", seriesId, UrlUtil.encode(String.format("%s,%s", specIds.get(0), specIds.get(1))));
                cfgItem.setLinkurl(link);
                cfgItem.setPvdata(Pvitem.newBuilder()
                        .putArgvs("seriesid", request.getSeriesid() + "")
                        .putArgvs("typeid", "2")
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_list_click").build()));
                toolboxbuilder.addList(cfgItem);
            }
            //参配纠错
            Toolboxentry.List.Builder jcItem = Toolboxentry.List.newBuilder();
            jcItem.setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/cfg_wtfk_0323.png");
            jcItem.setTitle("参配纠错");
            jcItem.setTypeid(4);
            String link = "";
            if (request.getPm() == 1) {
                link = "autohome://carcompare/specconfigissuereport?seriesid=" + seriesId;
            } else {
                link = "autohome://car/specconfigissuereport?seriesid=" + seriesId;
            }
            if (specDetailList != null && specDetailList.size() > 0) {
                link = link + "&seriesname=" + UrlUtil.encode(specDetailList.get(0).getSeriesName()).replace("+", "%20");
            }
            jcItem.setLinkurl(link);
            jcItem.setPvdata(Pvitem.newBuilder()
                    .putArgvs("seriesid", request.getSeriesid() + "")
                    .putArgvs("typeid", "4")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_param_diff_tool_list_click").build()));

            toolboxbuilder.addList(jcItem);
            return toolboxbuilder.build();
        });
    }

    private String subPreGbk(String str, int len, String suffix) {
        try {
            if (StringUtils.isNotEmpty(str)) {
                int counterOfDoubleByte = 0;
                byte[] b = str.toString().getBytes(Charset.forName("GBK"));
                if (b.length <= len) {
                    return str.toString();
                } else {
                    for(int i = 0; i < len; ++i) {
                        if (b[i] < 0) {
                            ++counterOfDoubleByte;
                        }
                    }

                    if (counterOfDoubleByte % 2 != 0) {
                        ++len;
                    }
                    return new String(b, 0, len, Charset.forName("GBK")) + suffix;
                }
            }
        } catch (Exception e) {
            logger.error("车型参配接口异常-subPreGbk error: {}", ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    private String convertDateFormat(String oldDate) {
        // 定义输入的日期格式
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 定义输出的日期格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            // 解析输入的日期字符串
            Date date = inputFormat.parse(oldDate);
            // 格式化为目标日期字符串
            return outputFormat.format(date);
        } catch (ParseException ex) {
            logger.error("日期字符串解析异常", ex);
            return "";
        }
    }
}
