package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.carbase.Pvitem;
import autohome.rpc.car.app_cars.v1.carext.SeriesTabRequest;
import autohome.rpc.car.app_cars.v1.carext.SeriesTabResponse;
import autohome.rpc.car.app_cars.v1.common.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.cars.apiclient.abtest.AbApiClient;
import com.autohome.app.cars.apiclient.abtest.dtos.ABTestDto;
import com.autohome.app.cars.apiclient.che168.Api2scautork2Client;
import com.autohome.app.cars.apiclient.che168.dtos.GetRecommendCarResult;
import com.autohome.app.cars.apiclient.clubcard.ClubCardApiDal;
import com.autohome.app.cars.apiclient.clubcard.dtos.SBI_RcmDataResult;
import com.autohome.app.cars.apiclient.dealer.YoucheApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.HomeDealerListResult;
import com.autohome.app.cars.apiclient.dealer.dtos.NewRepairFactoryResult;
import com.autohome.app.cars.apiclient.maindata.MainDataApiClient;
import com.autohome.app.cars.apiclient.maindata.dtos.HotDataResult;
import com.autohome.app.cars.apiclient.openApi.DataOpenApiClient;
import com.autohome.app.cars.apiclient.openApi.dtos.SameLevelRecommendSeriesResult;
import com.autohome.app.cars.apiclient.owner.OwnerApiClient;
import com.autohome.app.cars.apiclient.owner.dtos.PlayCarCardResult;
import com.autohome.app.cars.apiclient.owner.dtos.UseCarCardResult;
import com.autohome.app.cars.apiclient.reply.ReplyApiClient;
import com.autohome.app.cars.apiclient.reply.dtos.NewSeriesAiViewPointResult;
import com.autohome.app.cars.apiclient.reply.dtos.SeriesAiViewPointResult;
import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;
import com.autohome.app.cars.apiclient.video.XuanGouVideoApiClient;
import com.autohome.app.cars.apiclient.video.dtos.MainDataInfoResult;
import com.autohome.app.cars.apiclient.video.dtos.XuanGouVideoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.ClubCardTypeEnum;
import com.autohome.app.cars.common.enums.KoubeiTabTypeEnum;
import com.autohome.app.cars.common.redis.MainDataRedisTemplate;
import com.autohome.app.cars.common.redis.PeerDataRedisTemplate;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.common.MessageUtil;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.che168.SeriesUsedCarComponent;
import com.autohome.app.cars.service.components.che168.dtos.SeriesUsedCarInfo;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardDataComponent;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardHotComponent;
import com.autohome.app.cars.service.components.clubcard.SeriesClubCardTagComponent;
import com.autohome.app.cars.service.components.clubcard.dto.ReplyAndLikeDto;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardData;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardDataDto;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardTagDto;
import com.autohome.app.cars.service.components.cms.SeriesNewsComponent;
import com.autohome.app.cars.service.components.cms.SeriesNewsUpgradeComponent;
import com.autohome.app.cars.service.components.cms.dtos.SeriesSummaryNewTabEnum;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiTabComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKoubeiTabDto;
import com.autohome.app.cars.service.components.misc.SeriesHotCommentComponent;
import com.autohome.app.cars.service.components.misc.dtos.SeriesHotCommentDto;
import com.autohome.app.cars.service.components.owner.SeriesPlayCarComponent;
import com.autohome.app.cars.service.components.owner.SeriesUseCarComponent;
import com.autohome.app.cars.service.components.recrank.attention.AreaSeriesAttentionComponent;
import com.autohome.app.cars.service.components.recrank.attention.dtos.AreaSeriesAttentionDto;
import com.autohome.app.cars.service.components.remodel.SeriesRemodel3DComponent;
import com.autohome.app.cars.service.services.dtos.UsedCarFilterConfig;
import com.autohome.app.cars.service.services.dtos.RepairFctCityDto;
import com.autohome.app.cars.service.services.enums.TypeIdEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SeriesTabService {

    @Autowired
    private SeriesKouBeiTabComponent seriesKouBeiTabComponent;

    @Autowired
    SeriesNewsComponent seriesNewsComponent;

    @Autowired
    private MainDataApiClient mainDataApiClient;

    @Autowired
    SeriesClubCardDataComponent clubData;

    @Autowired
    SeriesClubCardHotComponent clubHot;

    @Autowired
    SeriesClubCardTagComponent clubTag;

    @Autowired
    SeriesPlayCarComponent seriesPlayCarComponent;

    @Autowired
    SeriesRemodel3DComponent seriesRemodel3DComponent;

    @Autowired
    SeriesUseCarComponent seriesUseCarComponent;

    @Autowired
    SeriesUsedCarComponent seriesUsedCarComponent;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    YoucheApiClient youcheApiClient;

    @Autowired
    Api2scautork2Client api2scautork2Client;

    @Autowired
    DataOpenApiClient dataOpenApiClient;

    @Autowired
    AreaSeriesAttentionComponent areaSeriesAttentionComponent;

    @Autowired
    OwnerApiClient ownerApiClient;

    @Autowired
    SeriesHotCommentComponent seriesHotCommentComponent;

    @Autowired
    ReplyApiClient replyApiClient;

    @Autowired
    XuanGouVideoApiClient xuanGouVideoApiClient;

    @Autowired
    AbApiClient abApiClient;

    @Autowired
    SeriesNewsUpgradeComponent seriesNewsUpgradeComponent;

    @Autowired
    ClubCardApiDal clubCardApiDal;

    @Value("${aiviewpointab_seriesidlist:[]}")
    private String aiViewPoinTabSeriesidList;

    @Autowired
    public MainDataRedisTemplate redisTemplate;

    @Autowired(required = false)
    PeerDataRedisTemplate peerDataRedisTemplate;

    @Autowired
    BI_AdapterFactorys bi_adapterFactorys;


    private static final String[] VALID_SCHEME = new String[]{"autohome://article/slidevideodetail", "autohome://article/videodetail", "autohome://videodetail", "autohome://article/newsflash", "autohome://articleplatform/detail/short", "autohome://article/detail/newshort", "autohome://article/articledetail", "autohome://article/articledetailcolumn", "autohome://articleplatform/detail/long"};

    private static String RECOMMEND_KEY = "app-cars-allTab:recommend:";
    /**
     * 二手车顶部筛选项
     */
    @Value("#{T(com.autohome.app.cars.service.services.dtos.UsedCarFilterConfig).format('${used_car_filter_config:[]}')}")
    private List<UsedCarFilterConfig> usedCarFilterConfigList;
    @Value("${exhibit_repairfct_city:}")
    private String exhibitRepairFctCity;

    /**
     * 资讯 tab
     *
     * @param series
     * @param request
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getNewsTab(SeriesDetailDto series, SeriesTabRequest request) {

        //每个tab下显示的最大内容条数
        int tabCount = 3;
        //资讯体验优化ab
        String dataFromAb = request.getSeriesarticledatafromab();
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();
        CompletableFuture<Map<String, List<BICardItemModel>>> datasFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<SBI_RcmDataResult> recommendDatas = CompletableFuture.completedFuture(null);
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.68.5") && StringUtils.isNotEmpty(dataFromAb) && StringUtils.equals("B",dataFromAb)){
            datasFuture = seriesNewsUpgradeComponent.get(request.getSeriesid());
            recommendDatas = clubCardApiDal.getRecommendNews(request);
        }else{
            datasFuture = seriesNewsComponent.get(request.getSeriesid());
        }
        CompletableFuture<Map<String, List<BICardItemModel>>> final_datasFuture = datasFuture;
        CompletableFuture<SBI_RcmDataResult> final_recommendDatas = recommendDatas;
        CompletableFuture<XuanGouVideoResult> xuanGouVideoFuture = getXuanGouVideoResult(request);

        return CompletableFuture.allOf(datasFuture, xuanGouVideoFuture,recommendDatas).thenCompose(x -> {
            Map<String, List<BICardItemModel>> datas = final_datasFuture.join();
            XuanGouVideoResult xuanGouVideo = xuanGouVideoFuture.join();
            SBI_RcmDataResult recommendData = final_recommendDatas.join();
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.68.5") && StringUtils.equals("B",dataFromAb) && datas != null){
                List<BICardItemModel> allTabModels = datas.get("recommend");//获取'全部tab'数据
                allTabModels = allTabModels == null ? new ArrayList<>() : allTabModels;//未从原接口拿到数据时，list为null
                if (recommendData != null && recommendData.getResult() != null && !CollectionUtils.isEmpty(recommendData.getResult().getItemlist())){
                    //推荐接口正常返回数据
                    getSeriesAllTabNewsFromRecommend(recommendData,allTabModels,request,redisTemplate,peerDataRedisTemplate,datas);
                }else{
                    //推荐接口超时，走缓存;
                    getSeriesAllTabNewsFromCache(request, allTabModels,datas);
                }
            }

            builder.setTabname(series.getIsNewCar() ? "新车资讯" : "精彩资讯");
            builder.setTabbgurl(getTabbgurl(request.getTabid()));

            String moreSchema = String.format("autohome://article/newseriesarticle?seriesid=%s&seriesname=%s", request.getSeriesid(), UrlUtil.encode(series.getName()));
            builder.setToprightbtn(
                    SeriesTabResponse.Result.Toprightbtn.newBuilder()
                            .setText("更多")
                            .setBtnurl(moreSchema)
            );

            SeriesTabResponse.Result.Bottombtn.Builder bottomBtn = SeriesTabResponse.Result.Bottombtn.newBuilder()
                    .setText("查看更多")
                    .setBtnurl(moreSchema);

            if (datas == null || datas.size() == 0) {
                return CompletableFuture.completedFuture(builder);
            }

            if (request.getFrom() == 2) {
                bottomBtn.setPvitem(
                        Pvitem.newBuilder()
                                .putArgvs("seriesid", "" + request.getSeriesid())
                                .putArgvs("specid", "" + request.getSpecid())
                                .putArgvs("pagetabid", "2")
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_secondtab_awc_morebtn_show"))
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_secondtab_awc_morebtn_click"))

                );
            }
            builder.setBottombtn(bottomBtn);


            List<String> mids = new ArrayList<>();
            for (SeriesSummaryNewTabEnum tabitem : SeriesSummaryNewTabEnum.values()) {
                if (!datas.containsKey(tabitem.getInfoType())) {
                    continue;
                }
                List<BICardItemModel> list = datas.get(tabitem.getInfoType());
                for (int position = 0; position < tabCount && position < list.size(); position++) {
                    BICardItemModel model = list.get(position);
                    if (model == null || model.getExtension() == null || model.getExtension().getObjinfo() == null) {
                        continue;
                    }
                    long id = model.getExtension().getObjinfo().getId();
                    switch (model.getCarddata().getMediatype()) {
                        case 3: //原创视频  video
                            mids.add("video-reply_count-" + id);
                            mids.add("video-pv-" + id);
                            break;
                        case 1: //原创文章
                        case 10:
                        case 700112:
                            mids.add("cms-reply_count-" + id);
                            mids.add("cms-pv-" + id);
                            break;

                        case 12:  //chejiahao
                        case 14:
                        case 206:
                            mids.add("chejiahao-reply_count-" + id);
                            mids.add("chejiahao-pv-" + id);
                            break;
                    }
                }
            }

            CompletableFuture<BaseModel<List<HotDataResult>>> hotDataResultFuture = mainDataApiClient.getHotData(String.join(",",mids));
            CompletableFuture<MainDataInfoResult> mainDataInfoResultFuture = getXuanGouMainDataInfoResult(request, xuanGouVideo);
            CompletableFuture<ABTestDto> abTestFuture = getXuanGouAbTestResult(request);

//            return mainDataApiClient.getHotData(String.join(",",mids)).thenApply(hds->{
              return CompletableFuture.allOf(hotDataResultFuture, mainDataInfoResultFuture, abTestFuture).thenApply(y->{
                BaseModel<List<HotDataResult>> hds = hotDataResultFuture.join();
                if(hds==null || hds.getResult()==null||hds.getResult().size()==0){
                    bindBulder(request.getSeriesid(),builder,datas,tabCount,request.getPm(),request);
                    return builder;
                }

                Map<String, Integer> hcs = hds.getResult().stream().collect(Collectors.toMap(i -> i.getMain_data_type() + "-" + i.getHot_data_type() + "-" + i.getBiz_id(), i -> i.getCount()));

                //绑定热点数据
                for (SeriesSummaryNewTabEnum tabitem : SeriesSummaryNewTabEnum.values()) {
                    if (!datas.containsKey(tabitem.getInfoType())) {
                        continue;
                    }
                    List<BICardItemModel> list = datas.get(tabitem.getInfoType());
                    for (int position = 0; position < tabCount && position < list.size(); position++) {
                        BICardItemModel model = list.get(position);
                        if (model == null || model.getCarddata() == null || model.getCarddata().getCardinfo() == null || model.getCarddata().getCardinfo().getTaginfo() == null || model.getCarddata().getCardinfo().getTaginfo().size() == 0) {
                            continue;
                        }
                        if (model.getExtension() == null || model.getExtension().getObjinfo() == null) {
                            continue;
                        }
                        String mt = "";
                        switch (model.getCarddata().getMediatype()) {
                            case 3: //原创视频  video
                                mt = "video";
                                break;
                            case 1: //原创文章
                            case 10:
                            case 700112:
                                mt = "cms";
                                break;

                            case 12:  //chejiahao
                            case 14:
                            case 206:
                                mt = "chejiahao";
                                break;
                            default:
                                continue;
                        }
                        long id = model.getExtension().getObjinfo().getId();
                        for (Card_CardInfo_TagModel tag : model.getCarddata().getCardinfo().getTaginfo()) {
                            if (tag.getText().endsWith("评论")) {
                                String key = mt + "-reply_count-" + id;
                                if (hcs.containsKey(key)) {
                                    tag.setText(hcs.get(key) + "评论");
                                }
                            } else if (tag.getText().endsWith("播放")) {
                                String key = mt + "-pv-" + id;
                                if (hcs.containsKey(key)) {
                                    tag.setText(SafeParamUtil.convertToWan(hcs.get(key)) + "播放");
                                }
                            }
                        }
                    }
                }


                bindBulder(request.getSeriesid(),builder,datas,tabCount,request.getPm(),request);
                if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.5")) {
                    bindXuanGouBulder(builder, request.getSeriesid(), tabCount, xuanGouVideo, mainDataInfoResultFuture.join());
                    rebindVideoBulder(builder, abTestFuture.join());
                }
                return builder;
            }).exceptionally(e -> {
                log.warn("获取热点数据失败", e);
                bindBulder(request.getSeriesid(), builder, datas, tabCount, request.getPm(),request);
                return builder;
            });


        });
    }

    /**
     * 推荐接口超时，读取缓存逻辑
     * @param request
     * @param allTabModels
     * @param datas
     */
    private void getSeriesAllTabNewsFromCache(SeriesTabRequest request, List<BICardItemModel> allTabModels,Map<String, List<BICardItemModel>> datas) {
        List<String> cache = redisTemplate.opsForList().range(RECOMMEND_KEY + request.getSeriesid(), 0, -1);
        List<String> peer_cache = new ArrayList<>();
        if (peerDataRedisTemplate != null){
            peer_cache = peerDataRedisTemplate.opsForList().range(RECOMMEND_KEY + request.getSeriesid(), 0, -1);
        }
        if (!CollectionUtils.isEmpty(cache) || !CollectionUtils.isEmpty(peer_cache)){
            int allTabSize = CollectionUtils.isEmpty(allTabModels) ? 0 : allTabModels.size();
            List<BICardItemModel> recommend_cache = cache.stream().map(str -> JSONObject.parseObject(str, BICardItemModel.class)).filter(Objects::nonNull).collect(Collectors.toList());
            if (cache != null && !cache.isEmpty()){
                //主库有数据拿主库
                recommend_cache = cache.stream().map(str -> JSONObject.parseObject(str, BICardItemModel.class)).filter(Objects::nonNull).collect(Collectors.toList());
            }else if (peer_cache != null && !peer_cache.isEmpty()){
                //主库没数据，拿从库
                recommend_cache = peer_cache.stream().map(str -> JSONObject.parseObject(str, BICardItemModel.class)).filter(Objects::nonNull).collect(Collectors.toList());
            }
            //最多只需三条数据
            int needSize = 3 - allTabSize;
            for (int i = 0; i < needSize && i < recommend_cache.size(); i++) {
                allTabModels.add(recommend_cache.get(i));
            }
            datas.put("recommend",allTabModels);
        }
    }

    /**
     * 过滤重复物料
     * 写缓存
     * @param recommend
     * @param allTabModels
     */
    private void getSeriesAllTabNewsFromRecommend(SBI_RcmDataResult recommend, List<BICardItemModel> allTabModels, SeriesTabRequest request, MainDataRedisTemplate redisTemplate, PeerDataRedisTemplate peerDataRedisTemplate, Map<String, List<BICardItemModel>> datas) {
        try {
            //去除重复物料
            List<Long> biz_ids = new ArrayList<>();
            if (!CollectionUtils.isEmpty(allTabModels)){
                allTabModels.forEach(x -> biz_ids.add(x.getExtension().getObjinfo().getId()));
            }
            for (SBI_RcmDataResult.SBI_RcmData_Item item : recommend.getResult().getItemlist()) {
                if (biz_ids.contains(item.getResourceobj().getBiz_id())){
                    recommend.getResult().getItemlist().remove(item);
                }
            }

            //缓存
            List<String> recommend_cache = redisTemplate.opsForList().range(RECOMMEND_KEY + request.getSeriesid(), 0, -1);

            if (!CollectionUtils.isEmpty(recommend.getResult().getItemlist())) {
                for (SBI_RcmDataResult.SBI_RcmData_Item rcmDataItem : recommend.getResult().getItemlist()) {
                    if (rcmDataItem != null && rcmDataItem.getResourceobj() != null && rcmDataItem.getResourceobj().getShow() != null) {
                        BICardItemModel biCardItemModel = null;
                        try {
                            biCardItemModel = bi_adapterFactorys.buildCardItemModelForSeriesSummaryRcmFeeds(rcmDataItem);
                            if (biCardItemModel != null){
                                JSONObject ext = JSONObject.parseObject(biCardItemModel.getCarddata().getPvdata().getPvclick());
                                ext.put("type","AIRec");
                                biCardItemModel.getCarddata().getPvdata().setPvclick(JSONObject.toJSONString(ext));
                                allTabModels.add(biCardItemModel);
                                if (CollectionUtils.isEmpty(recommend_cache)){
                                    //缓存为空才设置缓存
                                    ext.put("type","cache");
                                    biCardItemModel.getCarddata().getPvdata().setPvclick(JSONObject.toJSONString(ext));
                                    redisTemplate.opsForList().leftPush(RECOMMEND_KEY + request.getSeriesid(),JSONObject.toJSONString(biCardItemModel));
                                    //设置超时时间
                                    redisTemplate.expire(RECOMMEND_KEY + request.getSeriesid(),1, TimeUnit.HOURS);
                                    if (peerDataRedisTemplate != null){
                                        peerDataRedisTemplate.opsForList().leftPush(RECOMMEND_KEY + request.getSeriesid(),JSONObject.toJSONString(biCardItemModel));
                                        peerDataRedisTemplate.expire(RECOMMEND_KEY + request.getSeriesid(),1, TimeUnit.HOURS);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //超过三条就截取
            if (allTabModels.size() > 3){
                allTabModels = allTabModels.subList(0,3);
            }
            datas.put("recommend",allTabModels);
        } catch (Exception ex) {
            log.error("全部tab组装数据错误",ex);
        }
    }

    private CompletableFuture<ABTestDto> getXuanGouAbTestResult(SeriesTabRequest request) {
        CompletableFuture<ABTestDto> abTestFuture = CompletableFuture.completedFuture(null);
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.5")) {
            abTestFuture = abApiClient.getABTest("101918", request.getDeviceid());
        }
        return abTestFuture;
    }

    private CompletableFuture<XuanGouVideoResult> getXuanGouVideoResult(SeriesTabRequest request) {
        CompletableFuture<XuanGouVideoResult> xuanGouVideoFuture = CompletableFuture.completedFuture(null);
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.5")) {
            int seriesId = request.getSeriesid();
            String articlexuangouab = request.getArticlexuangouab();
            String module_id = "";
            if (StringUtils.isNotEmpty(articlexuangouab)) {
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
                    log.error("getNewsTab-xuangouabjson-error", e);
                }
                if (seriesModuleIdMap != null && seriesModuleIdMap.containsKey(String.valueOf(seriesId))) {
                    module_id = seriesModuleIdMap.get(String.valueOf(seriesId));
                }
            }
            if (StringUtils.isNotEmpty(module_id)) {
                xuanGouVideoFuture = xuanGouVideoApiClient.getXuanGouVideoResult(module_id);
            }
        }
        return xuanGouVideoFuture;
    }

    private CompletableFuture<MainDataInfoResult> getXuanGouMainDataInfoResult(SeriesTabRequest request, XuanGouVideoResult xuanGouVide) {
        CompletableFuture<MainDataInfoResult> xuanGouMainDataInfoFuture = CompletableFuture.completedFuture(null);
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.5")) {
            if (xuanGouVide != null && xuanGouVide.getResult() != null && ListUtil.isNotEmpty(xuanGouVide.getResult().getList())) {
                List<String> mainDataIds = new ArrayList<>();
                xuanGouVide.getResult().getList().forEach(item -> {
                    if (Objects.nonNull(item) && StringUtils.isNotEmpty(item.getMain_data_type()) && StringUtils.isNotEmpty(item.getObj_id())) {
                        mainDataIds.add(item.getMain_data_type() + "-" + item.getObj_id());
                    }
                });
                xuanGouMainDataInfoFuture = xuanGouVideoApiClient.getMainDataInfoResult(String.join(",", mainDataIds));
            }
        }
        return xuanGouMainDataInfoFuture;
    }

    //构建“选购”的tab和数据
    private void bindXuanGouBulder(SeriesTabResponse.Result.Builder builder, int seriesId, int tabCount, XuanGouVideoResult xuanGouVideo, MainDataInfoResult mainDataInfo) {
        try {
            if (tabCount > 0
                    && Objects.nonNull(xuanGouVideo)
                    && Objects.nonNull(xuanGouVideo.getResult())
                    && ListUtil.isNotEmpty(xuanGouVideo.getResult().getList())
                    && Objects.nonNull(mainDataInfo)
                    && ListUtil.isNotEmpty(mainDataInfo.getResult())) {
                int tabid = 10008;
                List<SeriesTabResponse.Result.CardData.Builder> xuanGouList = new ArrayList<>();
                List<SeriesTabResponse.Result.CardData.Builder> finalXuanGouList = xuanGouList;
                AtomicInteger index = new AtomicInteger(0);
                xuanGouVideo.getResult().getList().forEach(item -> {
                    index.getAndIncrement();
                    MainDataInfoResult.ResultBean mainData = mainDataInfo.getResult().stream().filter(x -> StringUtils.equals(String.valueOf(x.getBiz_id()), item.getObj_id()) && StringUtils.equals(x.getMain_data_type(), item.getMain_data_type())).findFirst().orElse(null);
                    List<CardCardInfoTagModel.Builder> tagInfos = new ArrayList<>();
                    List<CardCardInfoImageModel.Builder> imgs = new ArrayList<>();
                    //int cardType = index.get() == 1 ? 10400 : 10100;
                    int cardType = 10400;
                    int mediatype = 0;
                    int module_id = item.getModule_id();
                    if ("chejiahao".equals(item.getMain_data_type())) {
                        mediatype = 14;
                    } else if ("video".equals(item.getMain_data_type())) {
                        mediatype = 3;
                    }
                    //需要拼接多个数据时，参考这个结构（为啥不是json，客户端的兼容问题）：18_331*629_331*5769_331
                    String apiext = "moduleid_" + module_id;
                    String schemebase = "autohome://article/slidevideodetail?newsid=%s&from=0&mediatype=%s&seriesids=%s&vid=%s&scrolltocomment=0&iscontinue=1&frompage=0&fromsite=1106&apiext=%s";
                    String scheme = String.format(schemebase, item.getObj_id(), mediatype, seriesId, item.getVideo_source(), apiext);
                    int pv = 0;
                    String author_name = "";
                    if (Objects.nonNull(mainData)) {
                        pv = mainData.getPv();
                        author_name = mainData.getAuthor_name();
                    }
                    tagInfos.add(CardCardInfoTagModel.newBuilder().setText(author_name).setPosition(3000));
                    tagInfos.add(CardCardInfoTagModel.newBuilder().setText(IntUtil.convertToWan(pv, 1) + "播放").setPosition(3000));
                    tagInfos.add(CardCardInfoTagModel.newBuilder().setText(DateUtil.format(DateUtil.parse(item.getPublish_time(), "yyyy/MM/dd HH:mm:ss"), "yyyy-MM-dd")).setPosition(2000));
                    imgs.add(CardCardInfoImageModel.newBuilder().setUrl(ImageUtils.convertImageUrl(item.getImg_url(), false, false, false, ImageSizeEnum.ImgSize_16x9_640x360)));

                    NewsCard.Builder card = NewsCard.newBuilder();
                    card.setTabid(tabid);
                    card.setIstop(0);
                    card.setSource(0);
                    card.setCarddata(CardData.newBuilder()
                            .setCardtype(cardType)
                            .setMediatype(mediatype)
                            .setCardinfo(CardInfo.newBuilder()
                                    .setPlaytime(DateUtil.formatTime(item.getDuration()))
                                    .setTitle(item.getTitle())
                                    .addAllTaginfo(tagInfos.stream().map(x -> x.build()).collect(Collectors.toList()))
                                    .addAllImg(imgs.stream().map(x -> x.build()).collect(Collectors.toList())))
                            .setPvitem(Pvitem.newBuilder()
                                    .putArgvs("seriesid", String.valueOf(seriesId))
                                    .putArgvs("linkid", "1")
                                    .putArgvs("typeid", String.valueOf(tabid))
                                    .putArgvs("istop", "0")
                                    .putArgvs("ishot", "0")
                                    .putArgvs("position", String.valueOf(index.get()))
                                    .putArgvs("mediatype", String.valueOf(mediatype))
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_article_content_show"))
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_article_content_click"))
                            ));
                    card.setExtension(CardExtension.newBuilder()
                            .setComponetid(cardType + "," + item.getObj_id() + "," + mediatype)
                            .setScheme(scheme)
                            .setObjinfo(CardDataObjInfo.newBuilder()
                                    .setId(NumberUtils.toInt(item.getObj_id()))
                                    .setTitle(item.getTitle())
                                    .setPlaycount(String.valueOf(pv))
                                    .setSessionId(UUID.randomUUID().toString().replace("-", ""))
                                    .setAuthorname(author_name)));
                    SeriesTabResponse.Result.CardData.Builder cardData = SeriesTabResponse.Result.CardData.newBuilder()
                            .setType(10000)
                            .setCardNews(card);
                    finalXuanGouList.add(cardData);
                });
                if (xuanGouList.size() > tabCount) {
                    xuanGouList = xuanGouList.subList(0, tabCount);
                }
                if (ListUtil.isNotEmpty(xuanGouList)) {
                    SeriesTabResponse.Result.Subtablist.Builder xuanGouSubTab = SeriesTabResponse.Result.Subtablist.newBuilder();
                    xuanGouSubTab.setTabid(tabid);
                    xuanGouSubTab.setName("选购");
                    int position = -1;
                    SeriesTabResponse.Result.Subtablist subtab = builder.getSubtablistList().stream().filter(x -> x.getTabid() == SeriesSummaryNewTabEnum.ORIGINAL.getValue()).findFirst().orElse(null);
                    if (Objects.nonNull(subtab)) {
                        //“选购”放在“原创”后面
                        position = builder.getSubtablistList().indexOf(subtab);
                        builder.addSubtablist(position + 1, xuanGouSubTab.build());
                        builder.addAllList(xuanGouList.stream().map(x -> x.build()).collect(Collectors.toList()));
                    } else {
                        subtab = builder.getSubtablistList().stream().filter(x -> x.getTabid() == SeriesSummaryNewTabEnum.EVALUATION.getValue()).findFirst().orElse(null);
                        if (Objects.nonNull(subtab)) {
                            //“选购”放在“评测”前面
                            position = builder.getSubtablistList().indexOf(subtab);
                            builder.addSubtablist(position, xuanGouSubTab.build());
                            builder.addAllList(xuanGouList.stream().map(x -> x.build()).collect(Collectors.toList()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("bindXuanGouBulder", e);
        }
    }

    //需求文档：https://doc.autohome.com.cn/docapi/page/share/share_yro9CB9Q5w
    //3个都是视频的情况下，展示的卡片改为：第一个是当前视频大卡片形态，第二、三个改为图文布局（从而提升屏效）。
    private void rebindVideoBulder(SeriesTabResponse.Result.Builder builder, ABTestDto abTestDto) {
        try {
            if (Objects.nonNull(builder) && ListUtil.isNotEmpty(builder.getListBuilderList()) && Objects.nonNull(abTestDto) && Objects.nonNull(abTestDto.getResult()) && ListUtil.isNotEmpty(abTestDto.getResult().getList())) {
                ABTestDto.ResultDTO.ListDTO listDTO = abTestDto.getResult().getList().stream().filter(x -> x.getVariable().equals("101918")).findFirst().orElse(null);
                if (Objects.nonNull(listDTO) && StringUtils.equalsIgnoreCase(listDTO.getVersion(), "B")) {
                    //tab下数据数量
                    Map<String, Integer> tabDataCountMap = new HashMap<>();
                    //tab下视频数据数量
                    Map<String, Integer> tabVideoCountMap = new HashMap<>();
                    List<Integer> tabids = Arrays.asList(SeriesSummaryNewTabEnum.ALL.getValue(), SeriesSummaryNewTabEnum.VIDEO.getValue(), 10008);
                    List<Integer> videoMediatype = Arrays.asList(14, 3);
                    builder.getListList().forEach(item -> {
                        int tabid = item.getCardNews().getTabid();
                        int mediatype = item.getCardNews().getCarddata().getMediatype();
                        if (tabids.contains(tabid)) {
                            int dataCount = 0;
                            if (tabDataCountMap.containsKey(tabid + "")) {
                                dataCount = tabDataCountMap.get(tabid + "");
                            }
                            dataCount++;
                            tabDataCountMap.put(tabid + "", dataCount);

                            int videoCount = 0;
                            if (tabVideoCountMap.containsKey(tabid + "")) {
                                videoCount = tabVideoCountMap.get(tabid + "");
                            }
                            if (videoMediatype.contains(mediatype)) {
                                videoCount++;
                            }
                            tabVideoCountMap.put(tabid + "", videoCount);
                        }
                    });
                    tabids.forEach(tabid -> {
                        int dataCount = 0;
                        int videoCount = 0;
                        if (tabDataCountMap.containsKey(tabid + "")) {
                            dataCount = tabDataCountMap.get(tabid + "");
                        }
                        if (tabVideoCountMap.containsKey(tabid + "")) {
                            videoCount = tabVideoCountMap.get(tabid + "");
                        }
                        if (dataCount == videoCount && videoCount >= 3) {
                            List<SeriesTabResponse.Result.CardData.Builder> cardDatas = builder.getListBuilderList().stream().filter(x -> x.getCardNews().getTabid() == tabid).collect(Collectors.toList());
                            if (ListUtil.isNotEmpty(cardDatas)) {
                                AtomicInteger index = new AtomicInteger(0);
                                cardDatas.forEach(item -> {
                                    index.getAndIncrement();
                                    item.getCardNewsBuilder().getCarddataBuilder().setCardtype(index.get() == 1 ? 10400 : 10100);
                                });
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.warn("rebindVideoBulder", e);
        }
    }

    void bindBulder(int seriesId, SeriesTabResponse.Result.Builder builder,Map<String, List<BICardItemModel>> datas,int tabCount,int pm,SeriesTabRequest request){

        int subtabselectid = 0;
        for (SeriesSummaryNewTabEnum tabitem : SeriesSummaryNewTabEnum.values()) {
            if (!datas.containsKey(tabitem.getInfoType())) {
                continue;
            }
            builder.addSubtablist(
                    SeriesTabResponse.Result.Subtablist.newBuilder()
                            .setName(tabitem.getName())
                            .setTabid(tabitem.getValue())
            );
            List<BICardItemModel> list = datas.get(tabitem.getInfoType());
            if (pm == 3) {
                //鸿蒙特殊处理，有些资讯的落地页还未开发，需要过滤
                int location = 0;
                for (int position = 0; position < list.size(); position++) {
                    //有三条内容则不再填充
                    if (location == 3) {
                        break;
                    }
                    BICardItemModel model = list.get(position);
                    Pvitem.Builder pvitem = Pvitem.newBuilder()
                            .putArgvs("seriesid", String.valueOf(seriesId))
                            .putArgvs("linkid", "1")
                            .putArgvs("typeid", String.valueOf(tabitem.getValue()))
                            .putArgvs("istop", "0")
                            .putArgvs("ishot", "0")
                            .putArgvs("position", "" + (location + 1))
                            .putArgvs("mediatype", model.getCarddata().getMediatype() == null ? "" : model.getCarddata().getMediatype().toString())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_article_content_show"))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_article_content_click"));

                    if (model.getExtension() != null && model.getExtension().getObjinfo() != null) {
                        pvitem.putArgvs("objectid", model.getExtension().getObjinfo().getId() == null ? "" : model.getExtension().getObjinfo().getId().toString());
                    }
                    if (tabitem.getValue() == 10006) {
                        pvitem.putArgvs("cardtype", model.getCarddata().getCardtype() == null ? "" : model.getCarddata().getCardtype().toString());
                    }
                    boolean isShow = false;
                    for (String scheme : VALID_SCHEME) {
                        if (model.getExtension().getScheme().startsWith(scheme)) {
                            isShow = true;
                            break;
                        }
                    }
                    //isShow为true代表当前资讯内容的scheme支持跳转；false则代表不支持跳转，跳过此次循环
                    if (!isShow) {
                        continue;
                    }
                    NewsCard.Builder card = NewsCard.newBuilder();
                    ProtobufUtil.merge(model, card);
                    card.setIstop(0);
                    card.setSource(0);
                    card.setTabid(tabitem.getValue());
                    card.getCarddataBuilder().setPvitem(pvitem);
                    location++;
                    if (tabitem.getValue() == 10007) {
                        subtabselectid = 10007;
                    }
                    SeriesTabResponse.Result.CardData.Builder cardData = SeriesTabResponse.Result.CardData.newBuilder()
                            .setType(10000)
                            .setCardNews(card);
                    builder.addList(cardData);
                }
            } else {
                for (int position = 0; position < tabCount && position < list.size(); position++) {
                    BICardItemModel model = list.get(position);
                    Pvitem.Builder pvitem = Pvitem.newBuilder()
                            .putArgvs("seriesid", String.valueOf(seriesId))
                            .putArgvs("linkid", "1")
                            .putArgvs("typeid", String.valueOf(tabitem.getValue()))
                            .putArgvs("istop", "0")
                            .putArgvs("ishot", "0")
                            .putArgvs("position", "" + (position + 1))
                            .putArgvs("mediatype", model.getCarddata().getMediatype() == null ? "" : model.getCarddata().getMediatype().toString())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_article_content_show"))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_article_content_click"));

                    if (model.getExtension() != null && model.getExtension().getObjinfo() != null) {
                        pvitem.putArgvs("objectid", model.getExtension().getObjinfo().getId() == null ? "" : model.getExtension().getObjinfo().getId().toString());
                    }
                    if (tabitem.getValue() == 10006) {
                        pvitem.putArgvs("cardtype", model.getCarddata().getCardtype() == null ? "" : model.getCarddata().getCardtype().toString());
                    }

                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.68.5") && "B".equals(request.getSeriesarticledatafromab())){
                        String fromType = "";
                        if ("recommend".equals(tabitem.getInfoType())){
                            if (Arrays.asList("1","2","3").contains(model.getCarddata().getPvdata().getPvclick()))
                                switch (model.getCarddata().getPvdata().getPvclick()){
                                    case "1" :
                                        fromType = "houtaitop";
                                        break;
                                    case "2" :
                                        fromType = "hot";
                                        break;
                                    case "3":
                                        fromType = "goodOGC";
                                        break;
                                    default:
                                        break;
                                }else{
                                //来自智能推荐接口
                                JSONObject ext = JSONObject.parseObject(model.getCarddata().getPvdata().getPvclick());
                                fromType = ext.getString("type");
                            }
                        }else{
                            fromType = "other";
                        }

                        pvitem.putArgvs("fromtype",fromType);
                    }

                    NewsCard.Builder card = NewsCard.newBuilder();
                    ProtobufUtil.merge(model, card);
                    card.setIstop(0);
                    card.setSource(0);
                    card.setTabid(tabitem.getValue());
                    card.getCarddataBuilder().setPvitem(pvitem);

                    if (tabitem.getValue() == 10007) {
                        subtabselectid = 10007;
                    }

                    SeriesTabResponse.Result.CardData.Builder cardData = SeriesTabResponse.Result.CardData.newBuilder()
                            .setType(10000)
                            .setCardNews(card);
                    builder.addList(cardData);
                }
            }
        }
        builder.setSubtabselectid(subtabselectid);

        if (datas.containsKey("recommend")) {
            builder.getPvdataBuilder()
                    .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("recm_id").setArgvalue("").build())
                    .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("refreshtype").setArgvalue("").build())
                    .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("itemlist").setArgvalue("[]").build())
                    .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("itemcount").setArgvalue("" + Math.min(datas.get("recommend").size(), 3)).build())
                    .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("pv_event_id").setArgvalue("car_series_channel_overview_page_pv").build())
                    .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("user_id").setArgvalue("0").build());
        }
    }


    /**
     * 口碑tab
     *
     * @param series
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getKouBeiTab(SeriesDetailDto series, SeriesTabRequest request) {

        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();

        return seriesKouBeiTabComponent.get(series.getId()).thenApply(seriesKouBeiTabDto -> {
            if (seriesKouBeiTabDto != null) {
                builder.setTabname("口碑评价")
                        .setTabbgurl(getTabbgurl(request.getTabid()));

                String seriesName = UrlUtil.encode(series.getName());

                List<SeriesTabResponse.Result.Subtablist> subtablist = new ArrayList<>();
                SeriesTabResponse.Result.Subtablist.Builder subtablistBuilder = SeriesTabResponse.Result.Subtablist.newBuilder();
                for (KoubeiTabTypeEnum kbTypo : KoubeiTabTypeEnum.values()) {
                    subtablist.add(subtablistBuilder.setName(kbTypo.getTabName(series.getEnergytype()))
                            .setTabid(kbTypo.getTabId())
                            .setLinkurl("autohome://reputation/reputationlist?brandid=" + series.getBrandId() + "&seriesid=" + series.getId() + "&seriesname=" + seriesName + "&koubeifromkey=1" + "&scrollindex=3&categoryid=" + (kbTypo.getTabId() == 10 ? 15 : kbTypo.getTabId())).build());
                }

                builder.addAllSubtablist(subtablist);

                String moreUrl = "autohome://reputation/reputationlist?brandid=" + series.getBrandId() + "&seriesid=" + series.getId() + "&seriesname=" + seriesName + "&koubeifromkey=1&scrollindex=3";
                builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                        .setText("更多")
                        .setBtnurl(moreUrl).build());
                builder.setBottombtn(SeriesTabResponse.Result.Bottombtn.newBuilder()
                        .setText("查看更多口碑")
                        .setBtnurl(moreUrl).build());
                builder.setSubtitle(StringUtils.isEmpty(seriesKouBeiTabDto.getAverage()) || seriesKouBeiTabDto.getAverage().equals("0.0") ? "" : seriesKouBeiTabDto.getAverage() + "分");

                List<SeriesTabResponse.Result.Summary> summaryList = MessageUtil.toMessageList(seriesKouBeiTabDto.getSemanticSummaries(), SeriesTabResponse.Result.Summary.class);
                builder.addAllSummary(summaryList);

                //获取热点数据
                List<String> bizIdTypes = seriesKouBeiTabDto.getEvaluations().stream()
                        .flatMap(evaluation -> Stream.of(
                                "koubei" + "-pv-" + evaluation.getId(),
                                "koubei" + "-like_count-" + evaluation.getId()
                        )).distinct().toList();

                mainDataApiClient.getHotData(String.join(",", bizIdTypes)).thenAccept(hotDataResult -> {
                    if (hotDataResult != null && hotDataResult.getResult() != null) {
                        hotDataResult.getResult().forEach(hot -> {
                            seriesKouBeiTabDto.getEvaluations().stream().filter(e -> e.getId() == hot.getBiz_id()).forEach(e -> {
                                switch (hot.getHot_data_type()) {
                                    case "pv" -> e.setViewcount(hot.getCount());
                                    case "like_count" -> e.setHelpfulcount(hot.getCount());
                                }
                            });
                        });
                    }
                }).join();

                //测试数据排序
                seriesKouBeiTabDto.getEvaluations().sort(Comparator.comparing(SeriesKoubeiTabDto.Evaluation::getTabid));
                List<KouBeiCard> cardList = MessageUtil.toMessageList(seriesKouBeiTabDto.getEvaluations(), KouBeiCard.class);

                Pvitem.Builder pvItem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_kb_content_click").build())
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_series_kb_content_show").build())
                        .putArgvs("linkid", "0")
                        .putArgvs("seriesid", series.getId() + "");

                cardList.forEach(card -> {
                    KouBeiCard.Builder cardBuilder = card.toBuilder();
                    pvItem.putArgvs("typeid", card.getTabid() + "");
                    cardBuilder.setPvitem(pvItem);
                    SeriesTabResponse.Result.CardData data11010 = SeriesTabResponse.Result.CardData.newBuilder()
                            .setId(0)
                            .setType(11010)
                            .setDatafield("data11010")
                            .setCardKoubei(cardBuilder)
                            .build();
                    builder.addList(data11010);
                });
            }

            return builder;
        }).exceptionally(e -> {
            log.error("getKouBeiTab error", e);
            return builder;
        });
    }

    /**
     * 玩车tab
     *
     * @param series
     * @param request
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getPlayCarTab(SeriesDetailDto series, SeriesTabRequest request) {
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();
        return ownerApiClient.getPlayCarCard(series.getId(), series.getLevelId(), request.getCityid()).thenCombine(seriesRemodel3DComponent.get(series.getId()), (playCarCardResult, remodel3Data) -> {
            builder.setTabbgurl(getTabbgurl(request.getTabid()));
            String seriesName = UrlUtil.encode(series.getName());

            boolean newVersion = request.getPlaycartab().equals("B");
            if (newVersion) {
                builder.setTabname("改装");
            } else {
                builder.setTabname("玩车");
            }

            if (playCarCardResult == null || playCarCardResult.getResult() == null || playCarCardResult.getResult().getList().isEmpty()) {
                return builder;
            }

            if (newVersion) {
                if (playCarCardResult.getResult().getTabs().isEmpty()) {
                    return builder;
                }
                Optional<PlayCarCardResult.TabsDTO> refitTab = playCarCardResult.getResult().getTabs().stream().filter(x -> x.getTabName().equals("改装")).findFirst();
                if (refitTab.isEmpty()) {
                    return builder;
                }

                builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                        .setText("更多")
                        .setBtnurl(refitTab.get().getSourceUrl()).build());
                builder.setBottombtn(SeriesTabResponse.Result.Bottombtn.newBuilder()
                        .setText("查看更多")
                        .setBtnurl(refitTab.get().getSourceUrl()).build());

                //调整只返回改装list
                playCarCardResult.getResult().setList(playCarCardResult.getResult().getList().stream().filter(x -> x.getTabid().equals(refitTab.get().getTabId())).toList());
            } else {
                if (!playCarCardResult.getResult().getTabs().isEmpty()) {
                    playCarCardResult.getResult().getTabs().forEach(tab -> {
                        if (tab.getTabId() == 0) {
                            builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                                    .setText("更多")
                                    .setBtnurl(tab.getSourceUrl()).build());
                            builder.setBottombtn(SeriesTabResponse.Result.Bottombtn.newBuilder()
                                    .setText("查看更多")
                                    .setBtnurl(tab.getSourceUrl()).build());
                        }
                        SeriesTabResponse.Result.Subtablist.Builder subtablist = SeriesTabResponse.Result.Subtablist.newBuilder()
                                .setTabid(tab.getTabId())
                                .setName(tab.getTabName())
                                .setLinkurl(tab.getSourceUrl());
                        builder.addSubtablist(subtablist.build());
                    });
                } else {
                    String mainDataType = playCarCardResult.getResult().getList().get(0).getMainDataType();
                    String linkUrl = "autohome://refitcar/detail?seriesid=" + series.getId() + "&seriesname=" + seriesName;
                    if ("zijiayou".equals(mainDataType)) {
                        linkUrl = String.format("autohome://insidebrowserwk?url=%s", UrlUtil.encode(String.format("https://y.autohome.com.cn/zixun_travel_fe/list?isappchannel=1&order=0&carlevelid=%d&seriesid=%d", series.getLevelId(), series.getId())));
                    }

                    builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                            .setText("更多")
                            .setBtnurl(linkUrl).build());
                    builder.setBottombtn(SeriesTabResponse.Result.Bottombtn.newBuilder()
                            .setText("查看更多")
                            .setBtnurl(linkUrl).build());
                }
            }

            //数据加工处理
            playCarCardResult.getResult().getList().forEach(card -> {
                card.getCarddata().setMediatype(1);
                card.getCarddata().getCardinfo().getImg().forEach(img -> {
                    boolean toWebp = true;
                    if (img.getUrl().contains("userphotos")) {
                        toWebp = false;
                    }
                    img.setUrl(ImageUtils.convertImageUrl(img.getUrl(), toWebp, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts, true, true, true));

                });
                card.getCarddata().getCardinfo().getTaginfo().stream().filter(x -> x.getPosition() == 1000).forEach(tag -> {
                    tag.setBgcolor("#150088FF");
                    tag.setFontcolor("#FF0088FF");
                });
            });

            List<PlayCarCard> cardList = MessageUtil.toMessageList(playCarCardResult.getResult().getList(), PlayCarCard.class);

            Pvitem.Builder pvItem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_playcar_content_click").build())
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_playcar_content_show").build())
                    .putArgvs("linkid", "1")
                    .putArgvs("seriesid", series.getId() + "");

            cardList.forEach(card -> {
                PlayCarCard.Builder cardBuilder = card.toBuilder();
                pvItem.putArgvs("typeid", card.getTabid() + "");

                PlayCarCard.Carddata.Builder cardData = cardBuilder.getCarddata().toBuilder();
                cardData.setPvitem(pvItem);
                cardBuilder.setCarddata(cardData);

                SeriesTabResponse.Result.CardData data10000 = SeriesTabResponse.Result.CardData.newBuilder()
                        .setId(0)
                        .setType(10000)
                        .setDatafield("data10000")
                        .setPlayCar(cardBuilder)
                        .build();
                builder.addList(data10000);
            });

            //改装信息
            if (remodel3Data != null) {
                SeriesTabResponse.Result.RefitInfo.Builder refitInfo = SeriesTabResponse.Result.RefitInfo.newBuilder()
                        .setBgimg("http://nfiles3.autohome.com.cn/zrjcpk10/car_series_play_vrbg111105@3x.png.webp")
                        .setSeriesid(series.getId())
                        .setSeriesname(series.getName())
                        .setLinkurl(StringUtils.isNotEmpty(remodel3Data.getJumpUrl()) ? remodel3Data.getJumpUrl() : "")
                        .setId(999999);

                SeriesTabResponse.Result.RefitInfo.Hori.Builder hori = SeriesTabResponse.Result.RefitInfo.Hori.newBuilder();
                AtomicReference<Integer> normalIndex = new AtomicReference<>(0);
                AtomicReference<Integer> previewIndex = new AtomicReference<>(0);
                remodel3Data.getImages().forEach(img -> {
                    SeriesTabResponse.Result.RefitInfo.Normal.Builder normal = SeriesTabResponse.Result.RefitInfo.Normal.newBuilder();
                    normal.setSeq(normalIndex.getAndSet(normalIndex.get() + 1));
                    if (request.getPm() == 2) {
                        normal.setUrl(ImageUtils.convertImage_Size(img, ImageSizeEnum.ImgSizeVR_4x3_640x0));
                    } else {
                        normal.setUrl(img);
                    }
                    hori.addNormal(normal.build());
                });
                remodel3Data.getSmallImages().forEach(img -> {
                    SeriesTabResponse.Result.RefitInfo.Preview.Builder preview = SeriesTabResponse.Result.RefitInfo.Preview.newBuilder();
                    preview.setSeq(previewIndex.getAndSet(previewIndex.get() + 1));
                    preview.setUrl(img);
                    hori.addPreview(preview.build());
                });

                refitInfo.setHori(hori.build());

                builder.setRefitinfo(refitInfo.build());
            }

            return builder;
        }).exceptionally(e -> {
            log.error("getPlayCarTab error", e);
            return null;
        });
    }

    /**
     * 用车tab
     *
     * @param series
     * @param request
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getUseCarTab(SeriesDetailDto series, SeriesTabRequest request) {
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();

        int provinceId = request.getCityid() / 1000 * 1000;
        CompletableFuture<BaseModel<UseCarCardResult>> useCarCardFuture = ownerApiClient.getUseCarCard(series.getId(), provinceId, request.getCityid());
//        CompletableFuture<UseCarCardResult> useCarCardFuture = seriesUseCarComponent.get(series.getId(), request.getCityid());
        CompletableFuture<BaseModel<HomeDealerListResult>> homeDealerFuture = youcheApiClient.getHomeDealerList(series.getId(), request.getCityid(), request.getLongitude(), request.getLatitude());
        CompletableFuture<BaseModel<List<NewRepairFactoryResult>>> new_repairFactoryFuture = CompletableFuture.completedFuture(new BaseModel<>());
        List<RepairFctCityDto> repairFctCityDtos = JSONObject.parseObject(exhibitRepairFctCity, new TypeReference<List<RepairFctCityDto>>() {
        });
        Map<Integer, String> cityMap = repairFctCityDtos.stream().collect(Collectors.toMap(RepairFctCityDto::getCityId, RepairFctCityDto::getCityName));
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.5") && cityMap.containsKey(request.getCityid())) {
            String longitude = validateLongitude(request.getLongitude());
            String latitude = validateLatitude(request.getLatitude());
            if ("0.0".equals(longitude) && "0.0".equals(latitude)) {
                longitude = "";
                latitude = "";
            }
            new_repairFactoryFuture = youcheApiClient.newGetRepairFactoryList(request.getCityid(), series.getId(), longitude, latitude);
        }

        CompletableFuture<BaseModel<List<NewRepairFactoryResult>>> finalRepairFactoryFuture = new_repairFactoryFuture;
        return CompletableFuture.allOf(useCarCardFuture, homeDealerFuture, new_repairFactoryFuture).thenApply(x -> {
            BaseModel<UseCarCardResult> resultBaseModel = useCarCardFuture.join();
            BaseModel<HomeDealerListResult> homeDealerList = homeDealerFuture.join();
            BaseModel<List<NewRepairFactoryResult>> repairFactoryList = finalRepairFactoryFuture.join();
            UseCarCardResult useCarCardResult;
            if (resultBaseModel == null || resultBaseModel.getResult() == null) {
                return builder;
            } else {
                useCarCardResult = resultBaseModel.getResult();
            }

            String seriesName = UrlUtil.encode(series.getName());

            //用车数据 -- 11050 11051
            if (!useCarCardResult.getTopConfigItems().isEmpty() || !useCarCardResult.getCards().isEmpty()) {
                builder.setTabname("用车养车");
                builder.setTabbgurl(getTabbgurl(request.getTabid()));

                String urlParam = "flutter://yongche/homepage?seriesid=%s&seriesname=%s&sourceid=3";
                String moreUrl = "autohome://flutter?url=" + URLEncoder.encode(String.format(urlParam, series.getId(), seriesName, "utf-8"));
                builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                        .setText("更多")
                        .setBtnurl(moreUrl).build());
                builder.setBottombtn(SeriesTabResponse.Result.Bottombtn.newBuilder()
                        .setText("查看更多用车内容")
                        .setBtnurl(moreUrl).build());

                List<UseCarCard.ListData> list11050 = new ArrayList<>();
                List<UseCarCard.ListData> list11051 = new ArrayList<>();

                useCarCardResult.getCards().forEach(item -> {
                    UseCarCard.ListData.Builder listData = UseCarCard.ListData.newBuilder();
                    listData.setApphref(item.getAppHref());
                    listData.setCode(item.getCode());
                    listData.setImgurl(ImageUtils.convertImageUrl(item.getImgUrl(), true, false, false));
                    listData.setData(item.getData());
                    listData.setMhref(item.getMhref());
                    listData.setSubtitle(item.getSubtitle());
                    listData.setTitle(item.getTitle());
                    TypeIdEnum typeIdEnum = TypeIdEnum.getByCode(item.getCode());
                    if (typeIdEnum != null) {
                        listData.setTypeid(typeIdEnum.getTypeid());
                    }
                    list11050.add(listData.build());
                });

                useCarCardResult.getTopConfigItems().forEach(item -> {
                    UseCarCard.ListData.Builder listData = UseCarCard.ListData.newBuilder();
                    listData.setApphref(item.getAppHref());
                    listData.setCode(item.getCode());
                    listData.setImgurl(ImageUtils.convertImageUrl(item.getImgUrl(), true, false, false));
                    listData.setImgurlforrn(ImageUtils.convertImageUrl(item.getImgUrlForRN(), true, false, false));
                    listData.setMhref(item.getMhref());
                    listData.setSubtitle(item.getSubtitle());
                    listData.setTitle(item.getTitle());
                    TypeIdEnum typeIdEnum = TypeIdEnum.getByCode(item.getCode());
                    if (typeIdEnum != null) {
                        listData.setTypeid(typeIdEnum.getTypeid());
                    }
                    list11051.add(listData.build());
                });
                if (!list11050.isEmpty()) {
                    builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                            .setId(0)
                            .setType(11050)
                            .setUseCar(UseCarCard.newBuilder().addAllList(list11050).build())
                            .build());
                }
                if (!list11051.isEmpty()) {
                    builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                            .setId(0)
                            .setType(11051)
                            .setUseCar(UseCarCard.newBuilder().addAllList(list11051).build())
                            .build());
                }
            }
            //经销商数据 -- 11052 11053
            boolean isAdd4SBYData = false;
            if ((repairFactoryList != null && repairFactoryList.getResult() != null && !repairFactoryList.getResult().isEmpty())
                    || (homeDealerList != null && homeDealerList.getResult() != null && homeDealerList.getResult().getList() != null && !homeDealerList.getResult().getList().isEmpty())) {
                //11052
                String scheme = "";
                if (homeDealerList != null && homeDealerList.getResult() != null && StringUtils.isNotEmpty(homeDealerList.getResult().getJumpdealerlisturl())) {
                    scheme = "autohome://insidebrowserwk?url=" + UrlUtil.encode(homeDealerList.getResult().getJumpdealerlisturl() + "&pvareaid=6860515");
                }
                if (repairFactoryList != null && !CollectionUtils.isEmpty(repairFactoryList.getResult())) {
                    //目前综修厂不返回"查看全部"的跳转协议，也没有相应字段映射，这里直接置空
                    scheme = "";
                }
                Pvitem.Builder pvItem11052 = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_shorp_more_click").build())
                        .putArgvs("seriesid", series.getId() + "")
                        .putArgvs("specid", request.getSpecid() + "")
                        .putArgvs("cityid", request.getCityid() + "")
                        .putArgvs("pvid", "6860513");
                builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                        .setId(0)
                        .setType(11052)
                        .setUseCar(UseCarCard.newBuilder()
                                .setTitle(repairFactoryList != null && !CollectionUtils.isEmpty(repairFactoryList.getResult()) ? "精选好店" : "4S保养")
                                .setSubtitle("维修保养·车主权益")
                                .setRighttitle(StringUtils.isNotEmpty(scheme) ? "查看全部" : "")
                                .setScheme(scheme)
                                .setPvitem(pvItem11052)
                                .build())
                        .build());

                //11053
                HomeDealerListResult homeList = new HomeDealerListResult();
                List<NewRepairFactoryResult> repairList = new ArrayList<>();
                if (homeDealerList != null) {
                    homeList = homeDealerList.getResult();
                }
                if (repairFactoryList != null) {
                    repairList = repairFactoryList.getResult();
                }
                List<HomeDealerListResult.DealerInfoBean> subList = getUseCar11053Data(homeList, repairList, cityMap, request);
                for (int i = 0; i < subList.size(); i++) {
                    HomeDealerListResult.DealerInfoBean item = subList.get(i);
                    String showScheme = "";
                    if (item.isRepair()) {
                        //综修厂接口返回的url直接下发
                        showScheme = item.getDealerurl();
                    } else {
                        showScheme = "autohome://insidebrowserwk?url=" + UrlUtil.encode(item.getDealerurl().replaceAll("&{0,1}pvareaid=\\d{0,}", "") + "&pvareaid=6860520");
                    }

                    String abTest = "";

                    Pvitem.Builder telPvItem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_phone_click").build())
                            .putArgvs("seriesid", series.getId() + "")
                            .putArgvs("specid", request.getSpecid() + "")
                            .putArgvs("cityid", request.getCityid() + "")
                            .putArgvs("pvid", item.isRepair() ? "6863252" : "6860520")
                            .putArgvs("shop_card_id", item.getDealerid() + "")
                            .putArgvs("p", (i + 1) + "")
                            .putArgvs("type", item.isRepair() ? "0" : "1")
                            .putArgvs("ABtest", abTest);

                    Pvitem.Builder pvItem11053 = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_maintenance_card_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_maintenance_card_show").build())
                            .putArgvs("seriesid", series.getId() + "")
                            .putArgvs("specid", request.getSpecid() + "")
                            .putArgvs("cityid", request.getCityid() + "")
                            .putArgvs("pvid", item.isRepair() ? "6863252" : "6860520")
                            .putArgvs("shop_card_id", item.getDealerid() + "")
                            .putArgvs("p", (i + 1) + "")
                            .putArgvs("type", item.isRepair() ? "0" : "1")
                            .putArgvs("shop_level", item.isRepair() ? "" : item.getAftersalesscore().setScale(1, RoundingMode.HALF_DOWN).toString())
                            .putArgvs("ABtest", abTest);

                    Pvitem.Builder repairFctPvItem = Pvitem.newBuilder();
                    UseCarCard.CornerTagBean.Builder cornerTag_builder = UseCarCard.CornerTagBean.newBuilder();
                    List<UseCarCard.SmallTagsBean> smallTags = new ArrayList<>();
                    List<UseCarCard.Taglist> tagList = new ArrayList<>();
                    if (item.isRepair()) {
                        if (item.getCornertag() != null) {
                            cornerTag_builder.setImgurl(item.getCornertag().getImgurl());
                            cornerTag_builder.setHeight(item.getCornertag().getHeight());
                            cornerTag_builder.setWidth(item.getCornertag().getWidth());
                        }
                        if (!CollectionUtils.isEmpty(item.getSmalltags())) {
                            for (HomeDealerListResult.SmallTagsBean smallTag : item.getSmalltags()) {
                                smallTags.add(UseCarCard.SmallTagsBean.newBuilder()
                                        .setText(smallTag.getText())
                                        .setTextcolor(smallTag.getTextcolor())
                                        .setBgcolor(smallTag.getBgcolor())
                                        .build()
                                );
                            }
                        }
                        for (HomeDealerListResult.DealerInfoBean.TagItem tag : item.getTaglist()) {
                            tagList.add(UseCarCard.Taglist.newBuilder()
                                    .setTag(tag.getTag())
                                    .setCoupon(tag.getCoupon())
                                    .setLinkurl(tag.getLinkurl())
                                    .setSuffix(StringUtils.isNotEmpty(tag.getSuffix()) ? tag.getSuffix() : "")
                                    .setSuffixcolor(StringUtils.isNotEmpty(tag.getSuffixcolor()) ? tag.getSuffixcolor() : "")
                                    .setPvitem(Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_rightsandinterests_click").build())
                                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_rightsandinterests_card_show").build())
                                            .putArgvs("seriesid", series.getId() + "")
                                            .putArgvs("specid", request.getSpecid() + "")
                                            .putArgvs("cityid", request.getCityid() + "")
                                            .putArgvs("pvid", item.isRepair() ? "6863252" : "6860522")
                                            .putArgvs("shop_card_id", item.getDealerid() + "")
                                            .putArgvs("p", (i + 1) + "")
                                            .putArgvs("type", "0")
                                            .putArgvs("coupon", tag.getCoupon())
                                            .putArgvs("ABtest", abTest)
                                            .build())
                                    .build());
                        }
                        repairFctPvItem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().build())
                                .setShow(Pvitem.Show.newBuilder().setEventid("car_series_zxc_card_show").build())
                                .setClick(Pvitem.Click.newBuilder().setEventid("car_series_zxc_card_click").build())
                                .putArgvs("seriesid", series.getId() + "")
                                .putArgvs("specid", request.getSpecid() + "")
                                .putArgvs("cityid", request.getCityid() + "")
                                .putArgvs("sourceid", "221111406.RN004")
                                .putArgvs("shop_card_id", item.getDealerid() + "")
                                .putArgvs("shop_name", item.getDealername())
                                .putArgvs("type", "0");
                    } else {
                        String coupon = "";
                        String couponScheme = "";
                        if (item.getComboactivitylist() != null && !item.getComboactivitylist().isEmpty()) {
                            HomeDealerListResult.DealerInfoBean.ComboactivitylistBean comboactivitylistBean = item.getComboactivitylist().get(0);
                            if (comboactivitylistBean != null) {
                                coupon = comboactivitylistBean.getTitle();
                                if (StringUtils.isNotEmpty(comboactivitylistBean.getJumpurl())) {
                                    couponScheme = "autohome://insidebrowserwk?url=" + UrlUtil.encode(comboactivitylistBean.getJumpurl().replaceAll("&{0,1}pvareaid=\\d{0,}", "") + "&pvareaid=6860522");
                                }
                            }
                        }
                        if (StringUtils.isNotEmpty(coupon)) {
                            Pvitem.Builder couponPvItem = Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_rightsandinterests_click").build())
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_rightsandinterests_card_show").build())
                                    .putArgvs("seriesid", series.getId() + "")
                                    .putArgvs("specid", request.getSpecid() + "")
                                    .putArgvs("cityid", request.getCityid() + "")
                                    .putArgvs("pvid", "6860522")
                                    .putArgvs("shop_card_id", item.getDealerid() + "")
                                    .putArgvs("p", (i + 1) + "")
                                    .putArgvs("type", "1")
                                    .putArgvs("ABtest", abTest);

                            tagList.add(UseCarCard.Taglist.newBuilder()
                                    .setTag(StringUtils.isNotEmpty(coupon) ? "权益" : "")
                                    .setLinkurl(couponScheme)
                                    .setCoupon(coupon)
                                    .setPvitem(couponPvItem).build());
                        }
                    }

                    UseCarCard.ShopLevelInfo.Builder showLevelBuilder = UseCarCard.ShopLevelInfo.newBuilder();
                    if (!item.isRepair()) {
                        showLevelBuilder.setImg("http://nfiles3.autohome.com.cn/zrjcpk10/115900/car_sale_store_score_img.png")
                                .setScorecontent("售后服务")
                                .setScore(item.getAftersalesscore().setScale(1, RoundingMode.HALF_DOWN).toString() + "分");
                    }

                    builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                            .setId(0)
                            .setType(11053)
                            .setUseCar(UseCarCard.newBuilder()
                                    .setShopurl(ImageUtils.convertImageUrl(item.getDealerimage(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts))
                                    .setShopscheme(showScheme)
                                    .setTitle(item.getDealername())
                                    .setTel(item.getDealerlinktel())
                                    .setAddress(item.getDealeraddress())
                                    .setDistance(item.getDistance() > 0 ? item.getDistance() + "km" : "")
                                    .addAllTaglist(tagList)
                                    .setShoplevelinfo(showLevelBuilder)
                                    .setLast(i == 1)
                                    .setPvitem(pvItem11053)
                                    .setTelPVItem(telPvItem)
                                    .setCornertag(cornerTag_builder)
                                    .addAllSmalltags(smallTags)
                                    .setRepairfctpvitem(repairFctPvItem)
                                    .build())
                            .build());
                }

                //11054 -- 无内容占位
                builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                        .setUseCar(UseCarCard.newBuilder().build())
                        .setId(0)
                        .setType(11054)
                        .build());
                isAdd4SBYData = true;
            }
            //用车数据 -- 最后内容案例 -- 10000
            if (useCarCardResult.getNewContentItems() != null && !useCarCardResult.getNewContentItems().isEmpty()) {
                //如果有4S保养数据，就只保留最后一个文章数据。
                if (isAdd4SBYData) {
                    useCarCardResult.setNewContentItems(useCarCardResult.getNewContentItems().subList(useCarCardResult.getNewContentItems().size() - 1, useCarCardResult.getNewContentItems().size()));
                }
                int index = 0;
                for (UseCarCardResult.NewContentItemsDTO item : useCarCardResult.getNewContentItems()) {
                    UseCarCard.Carddata.Builder cardData = UseCarCard.Carddata.newBuilder();
                    cardData.setMediatype(5);

                    UseCarCard.Cardinfo.Builder cardInfo = UseCarCard.Cardinfo.newBuilder();
                    cardInfo.setTitle(item.getContent());
                    //标签1
                    if (StringUtils.isNotEmpty(item.getLabel())) {
                        if (StringUtils.isNotEmpty(item.getLabelUrl())) {
                            cardInfo.addTaginfo(UseCarCard.Taginfo.newBuilder()
                                    .setPosition(3000)
                                    .setStyletype(14)
                                    .setText(item.getLabel())
                                    .setScheme(item.getLabelUrl())
                                    .setIconurl("http://nfiles3.autohome.com.cn/zrjcpk10/news_blueicon_230223.png.webp")
                                    .build());
                        } else {
                            cardInfo.addTaginfo(UseCarCard.Taginfo.newBuilder()
                                    .setPosition(1000)
                                    .setStyletype(4)
                                    .setText(item.getLabel())
                                    .build());
                        }
                    }
                    //标签2
                    if (StringUtils.isNotEmpty(item.getDesc())) {
                        cardInfo.addTaginfo(UseCarCard.Taginfo.newBuilder()
                                .setPosition(1000)
                                .setStyletype(4)
                                .setText(item.getDesc())
                                .setBgcolor("#150088FF")
                                .setFontcolor("#FF0088FF")
                                .build());
                    }

                    if (item.getType() == 1) {//单图
                        cardData.setCardtype(10100);
                        cardInfo.addImg(UseCarCard.Img.newBuilder().setUrl(ImageUtils.convertImageUrl(item.getCoverImg().get(0), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts)).build());
                    } else if (item.getType() == 2) {
                        cardData.setCardtype(10200);
                    } else if (item.getType() == 3) {
                        cardData.setCardtype(10400);
                    }

                    item.getCoverImg().forEach(img -> {
                        cardInfo.addImg(UseCarCard.Img.newBuilder().setUrl(ImageUtils.convertImageUrl(img, true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts)).build());
                    });

                    UseCarCard.Builder cardBuilder = UseCarCard.newBuilder();
                    cardBuilder.setExtension(UseCarCard.Extension.newBuilder().setScheme(item.getJumpUrl()).build());
                    cardBuilder.setCarddata(cardData.setCardinfo(cardInfo)
                            .setPvitem(Pvitem.newBuilder().setClick(Pvitem.Click.newBuilder().setEventid("car_series_yc_article_click").build())
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_yc_article_show").build())
                                    .putArgvs("index", ++index + "")
                                    .putArgvs("contentid", "0")).build());

                    builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                            .setId(0)
                            .setType(10000)
                            .setUseCar(cardBuilder)
                            .build());
                }
            }

            return builder;
        }).exceptionally(e -> {
            log.error("getUseCarTab error", e);
            return null;
        });
    }

    /**
     * 判断经度是否合法
     *
     * @param longitude
     * @return
     */
    private String validateLongitude(double longitude) {
        if (longitude >= -180.0 && longitude <= 180.0) {
            return Double.toString(longitude);
        } else {
            return "";
        }
    }

    /**
     * 判断纬度是否合法
     *
     * @param latitude
     * @return
     */
    private String validateLatitude(double latitude) {
        if (latitude >= -90.0 && latitude <= 90.0) {
            return Double.toString(latitude);
        } else {
            return "";
        }
    }

    /**
     * 融合用车tab的4S店和综修厂数据
     *
     * @param homeList
     * @param repairList
     * @return
     */
    public List<HomeDealerListResult.DealerInfoBean> getUseCar11053Data(HomeDealerListResult homeList, List<NewRepairFactoryResult> repairList, Map<Integer, String> cityMap, SeriesTabRequest request) {
        List<HomeDealerListResult.DealerInfoBean> subList = new ArrayList<>();
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.5") && cityMap.containsKey(request.getCityid())) {
            //展示综修厂的城市，根据不同情况组装数据
            //4s店：homeList和综修厂：repairList均有数据，每个接口各取一条；第一个是综修厂，第二个是4S店
            if (homeList != null && homeList.getList() != null && !homeList.getList().isEmpty()
                    && repairList != null && !repairList.isEmpty()) {

                subList.add(convertRepairToDealerInfoBean(repairList.get(0)));
                subList.add(homeList.getList().get(0));
            }
            //4s店：homeList无数据，综修厂：repairList有数据，只取一条综修厂的数据
            if ((homeList == null || homeList.getList() == null || homeList.getList().isEmpty())
                    && repairList != null && !repairList.isEmpty()) {
                List<HomeDealerListResult.DealerInfoBean> finalSubList = subList;
                repairList.subList(0, 1).forEach(repair -> {
                    finalSubList.add(convertRepairToDealerInfoBean(repair));
                });
            }
            //4s店：homeList有数据，综修厂：repairList无数据，取4s店的前两条
            if (homeList != null && homeList.getList() != null && !homeList.getList().isEmpty()
                    && (repairList == null || repairList.isEmpty())) {
                subList = homeList.getList().subList(0, Math.min(2, homeList.getList().size()));
            }
        } else {
            //不展示综修厂的城市，正常展示两条4s店数据
            if (homeList != null && homeList.getList() != null && !homeList.getList().isEmpty()) {
                List<HomeDealerListResult.DealerInfoBean> dealerInfoBeans = homeList.getList().subList(0, Math.min(2, homeList.getList().size()));
                subList.addAll(dealerInfoBeans);
            }
        }
        return subList;
    }

    public HomeDealerListResult.DealerInfoBean convertRepairToDealerInfoBean(NewRepairFactoryResult repair) {
        HomeDealerListResult.DealerInfoBean dealerInfoBean = new HomeDealerListResult.DealerInfoBean();
        dealerInfoBean.setDealerid(repair.getCshDealerId());
        dealerInfoBean.setDealername(repair.getDealerShortName());
        dealerInfoBean.setDealeraddress(repair.getAddress());
        dealerInfoBean.setDealerimage(repair.getDealerImg());
        dealerInfoBean.setDealerlinktel(repair.getSellPhone());
        if ("H5".equals(repair.getSchemeType())) {
            //H5协议需要手动拼协议头及编码
            dealerInfoBean.setDealerurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(repair.getLandingPageUrl()));
        } else {
            //RN可直接下发
            dealerInfoBean.setDealerurl(repair.getLandingPageUrl());
        }
        if (repair.isRecommendDealer()) {
            //是推荐商家，需要展示安心养车图标
            HomeDealerListResult.CornerTagBean cornerTagBean = new HomeDealerListResult.CornerTagBean();
            cornerTagBean.setImgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_repair_tag_240909.png");
            cornerTagBean.setWidth(156);
            cornerTagBean.setHeight(48);
            dealerInfoBean.setCornertag(cornerTagBean);
        }
        if (!CollectionUtils.isEmpty(repair.getDealerTags())) {
            List<HomeDealerListResult.SmallTagsBean> smallTags = new ArrayList<>();
            AtomicBoolean is_highlights = new AtomicBoolean(true);
            for (int i = 0; i < repair.getDealerTags().subList(0, Math.min(2, repair.getDealerTags().size())).size(); i++) {
                NewRepairFactoryResult.DealerTags dealerTags = repair.getDealerTags().get(i);
                HomeDealerListResult.SmallTagsBean bean = new HomeDealerListResult.SmallTagsBean();
                if (is_highlights.get() && dealerTags.isSelected()) {
                    //isSelected字段标识标签是否需要高亮，且高亮的标签只能有一个
                    bean.setText(dealerTags.getTagName());
                    bean.setTextcolor("#111E36");
                    bean.setBgcolor("#FFEFE5");
                    smallTags.add(0, bean);
                    is_highlights.set(false);
                } else {
                    bean.setText(dealerTags.getTagName());
                    bean.setBgcolor("#F5F6FA");
                    bean.setTextcolor("#828CA0");
                    smallTags.add(bean);
                }
            }
            dealerInfoBean.setSmalltags(smallTags);
        }
        dealerInfoBean.setRepair(true);
        if (repair.getDistance() != null) {
            //综修厂返回的distance变量单位是米，需要转换成千米并保留一位小数(四舍五入)
            BigDecimal bigDecimal = new BigDecimal(repair.getDistance().doubleValue());
            double distance = bigDecimal.divide(new BigDecimal(1000), 1, RoundingMode.HALF_UP).doubleValue();
            dealerInfoBean.setDistance(distance);
        }

        if (repair.getActivities() != null) {
            repair.getActivities().forEach(activity -> {
                HomeDealerListResult.DealerInfoBean.TagItem tagItem = new HomeDealerListResult.DealerInfoBean.TagItem();
                tagItem.setTag((activity.getPreferPrice() != null && activity.getPreferPrice().doubleValue() > 0.0) ? "优惠" : "权益");
                tagItem.setCoupon(activity.getTitle());
                if ("H5".equals(activity.getSchemeType())) {
                    //H5协议需要手动拼协议头及编码
                    tagItem.setLinkurl("autohome://insidebrowserwk?url=" + UrlUtil.encode(activity.getLandingPageUrl()));
                } else {
                    //RN可直接下发
                    tagItem.setLinkurl(activity.getLandingPageUrl());
                }
                if (activity.getPreferPrice() != null && activity.getPreferPrice().doubleValue() > 0.0) {
                    tagItem.setCoupon(activity.getTitle() + " 仅需");
                    tagItem.setSuffix(activity.getPreferPrice().toString() + "元");
                    tagItem.setSuffixcolor("#FF6600");
                }
                dealerInfoBean.getTaglist().add(tagItem);
            });
        }
        return dealerInfoBean;
    }

    /**
     * 二手车tab
     *
     * @param series
     * @param request
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getUsedCarTab(SeriesDetailDto series, SeriesTabRequest request) {
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();

        boolean isNewSummaryVersion = CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.3");
        boolean isNewSummaryTest = StringUtils.equalsAny(request.getNewsummaryab(), "1", "2");
        boolean isNewSummaryTestB = request.getNewsummaryab().equals("1");

        CompletableFuture<BaseModel<GetRecommendCarResult>> recommendCarsFuture;
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesid());
        int seriesState = seriesDetailDto == null ? -1 : seriesDetailDto.getState();
        String pvareaid = seriesState == 40 ? "112875" : "104680";
        String eid = seriesState == 40 ? "112876" : "104679";
        String bpvareaid = seriesState == 40? "112891" : "112892";
        // https://doc.autohome.com.cn/docapi/page/share/share_y0gZRRQwvA
        if (isNewSummaryVersion && isNewSummaryTest) {
            // 实验C组固定取第一页前5个, 不允许翻页
            int pageIndex = isNewSummaryTestB ? (request.getPageindex() == 0 ? 1 : request.getPageindex()) : 1;
            int pageSize = isNewSummaryTestB ? (request.getPagesize() == 0 ? 20 : request.getPagesize()) : 5;
            if (isNewSummaryTestB && pageIndex == 1) {
                addUsedCarFilter(builder, request.getSeriesid());
            }
            recommendCarsFuture = api2scautork2Client.getRecommendCarsV2(series.getId(), request.getCityid(), pageIndex, pageSize, request.getSort(), request.getMileage(), request.getPrice(), request.getDeviceid(), request.getAge());
        } else {
            recommendCarsFuture = api2scautork2Client.getRecommendCars(series.getId(), request.getCityid(), request.getDeviceid());
//            recommendCarsFuture = api2scautork2Client.getRecommendCars2(series.getId(), request.getCityid(), request.getDeviceid(), pvareaid, eid,bpvareaid);
        }
        // 当页码大于1的时候, 只返回list, pageindex, totalcount
        if (isNewSummaryVersion && isNewSummaryTest && request.getPageindex() > 1) {
            return recommendCarsFuture.thenApply(model -> {
                builder.setPageindex(request.getPageindex());
                if (Objects.nonNull(model) && Objects.nonNull(model.getResult()) && Objects.nonNull(model.getResult().getCars())) {
                    // 当请求分页页码 < 实际分页 但 cars 为空时, 将pagecount 设置为请求的pageindex
                    if (isNewSummaryTestB) {
                        builder.setPagecount(
                                request.getPageindex() < model.getResult().getPagecount()
                                        && model.getResult().getCars().isEmpty() ?
                                        request.getPageindex() :
                                        model.getResult().getPagecount()
                        );
                    }
                    if (!model.getResult().getCars().isEmpty()) {
                        List<GetRecommendCarResult.SUsedCarItem> cars = model.getResult().getCars();
                        int carCount = model.getResult().getCarcount();
                        addUsedCarListItem(builder, cars, carCount, true, true, isNewSummaryTestB, request);
                    }
                }
                return builder;
            });
        }
        CompletableFuture<SeriesUsedCarInfo> usedCarFuture = seriesUsedCarComponent.get(series.getId());

        return CompletableFuture.allOf(usedCarFuture, recommendCarsFuture).thenApply(x -> {
            SeriesUsedCarInfo usedCarInfo = usedCarFuture.join();
            BaseModel<GetRecommendCarResult> recommendCars = recommendCarsFuture.join();

            builder.setTabname("买二手车");
            builder.setTabbgurl(getTabbgurl(request.getTabid()));

            UsedCarCard.Builder usedCarCard11030 = UsedCarCard.newBuilder();
            usedCarCard11030.setSeriesid(series.getId());
            usedCarCard11030.setSeriesname(series.getName());

            //保值率
            if (usedCarInfo != null && usedCarInfo.getRate() > 0) {
                usedCarCard11030.setHadgetext("3年保值率")
                        .setHadgevalue((int) (usedCarInfo.getRate() * 100) + "%");
            }

            // 二手车信息
            if (recommendCars != null && recommendCars.getResult() != null) {
                List<GetRecommendCarResult.SUsedCarItem> usedCarList;
                // 当请求分页页码 < 实际分页 但 cars 为空时, 将pagecount 设置为请求的pageindex
                if (isNewSummaryTestB) {
                    builder.setPagecount(
                            request.getPageindex() < recommendCars.getResult().getPagecount()
                                    && recommendCars.getResult().getCars().isEmpty() ?
                                    request.getPageindex() :
                                    recommendCars.getResult().getPagecount()
                    );
                }
                if (recommendCars.getResult().getCars() != null) {
                    int carCount = 0;
                    if (isNewSummaryVersion && isNewSummaryTest) {
                        usedCarList = recommendCars.getResult().getCars();
                        carCount = recommendCars.getResult().getCarcount();

                    } else {
                        usedCarList = recommendCars.getResult().getCars().subList(0, Math.min(5, recommendCars.getResult().getCars().size()));
                        carCount = usedCarList.size();
                    }

                    builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                            .setText("更多")
                            .setPvareaid(pvareaid)
                            .setBtnurl(recommendCars.getResult().getMoreurl().replace("pvareaid=104680","pvareaid="+pvareaid)).build());

                    if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.0") && StringUtils.equalsAnyIgnoreCase(request.getNewsummaryab(), "0", "2") && StringUtils.equalsIgnoreCase(request.getUsedcarrecab(), "B")) {
                        builder.setBottombtn(SeriesTabResponse.Result.Bottombtn.newBuilder()
                                .setText("查看更多二手车")
                                .setPvareaid(bpvareaid)
                                .setBtnurl(recommendCars.getResult().getMoreurl().replace("pvareaid=104680","pvareaid="+bpvareaid))
                                .build());
                    }

                    usedCarCard11030.setRighttoptext(String.format("共%d辆车", recommendCars.getResult().getCarcount()))
                            .setPricetext("二手车价")
                            .setPricevalue(recommendCars.getResult().getPricerange() + "万")
                            .setLinkurl(recommendCars.getResult().getMoreurl().replace("pvareaid=104680","pvareaid="+eid))
                            .setCrosscut(UsedCarCard.Crosscut.newBuilder()
                                    .setLabel("置换")
                                    .setTitle(recommendCars.getResult().getReplacetitle())
                                    .setLinkurl(UrlUtil.getInsideBrowerSchemeWK("https://activitym.che168.com/2023/changeonestop/index?&leadssources=25&sourcetwo=4&sourcethree=924&pvareaid="+eid))
                                    .setRightbtntext("去领取")
                                    .setPvitem(Pvitem.newBuilder()
                                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_newusedcar_content_click")
                                                    .putArgvs("seriesid", series.getId() + "")
                                                    .putArgvs("linkid", "3")
                                                    .putArgvs("typeid", "0")
                                                    .putArgvs("eid", eid)
                                                    .putArgvs("cityid", request.getCityid() + "")
                                                    .putArgvs("specid", request.getSpecid() + "")
                                                    .build())
                                    ).build())
                            .setPvitem(Pvitem.newBuilder()
                                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_newusedcar_content_click").build())
                                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_newusedcar_content_show").build())
                                    .putArgvs("seriesid", series.getId() + "")
                                    .putArgvs("linkid", "2")
                                    .putArgvs("typeid", "0")
                                    .putArgvs("eid", eid)
                                    .putArgvs("cityid", request.getCityid() + "")
                                    .putArgvs("specid", request.getSpecid() + "")
                                    .build());

                    //顶部内容 -- 11030
                    builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                            .setType(11030)
                            .setUsedCar(usedCarCard11030.build())
                            .build());

                    //二手车信息 -- 11031
                    addUsedCarListItem(builder, usedCarList, carCount, isNewSummaryVersion, isNewSummaryVersion, isNewSummaryTestB, request);
                }
            }

            return builder;
        });
    }

    /**
     * 添加二手车筛选项
     *
     * @param builder  response
     * @param seriesId 车系ID
     */
    private void addUsedCarFilter(SeriesTabResponse.Result.Builder builder, int seriesId) {

        usedCarFilterConfigList.forEach(filter -> {
            Map<String, String> argMap = new HashMap<>(2);
            argMap.put("typeid", String.valueOf(filter.getType()));
            argMap.put("seriesid", String.valueOf(seriesId));
            SeriesTabResponse.Result.UsedCarFilter.Builder filterBuilder = SeriesTabResponse.Result.UsedCarFilter.newBuilder()
                    .setParameter(filter.getParameter())
                    .setName(filter.getName())
                    .setPvitem(Pvitem.newBuilder()
                            .putAllArgvs(argMap)
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_usedcar_select_show"))
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_usedcar_select_click")))
                    .setSubmitpvitem(Pvitem.newBuilder()
                            .putAllArgvs(argMap)
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_usedcar_submit_click")));
            filter.getList().forEach(filterItem -> {
                SeriesTabResponse.Result.UsedCarFilterItem.Builder filterItemBuilder = SeriesTabResponse.Result.UsedCarFilterItem.newBuilder()
                        .setName(filterItem.getName())
                        .setTypeid(filterItem.getTypeid())
                        .setSelected(filterItem.getSelected())
                        .setValue(filterItem.getValue());
                filterBuilder.addList(filterItemBuilder.build());
            });
            builder.addFilter(filterBuilder);
        });
    }


    private void addUsedCarListItem(SeriesTabResponse.Result.Builder builder, List<GetRecommendCarResult.SUsedCarItem> usedCarList, int carCount, boolean isNewSummaryVersion, boolean isNewSummaryTest, boolean isNewSummaryTestB, SeriesTabRequest request) {
        if (isNewSummaryVersion && isNewSummaryTestB) {
            builder.setPageindex(request.getPageindex());
            builder.setTotalcount(carCount);
        }
        int index = 0;
        for (GetRecommendCarResult.SUsedCarItem item : usedCarList) {
            UsedCarCard.RdPvInfo.Builder rdPvInfo = UsedCarCard.RdPvInfo.newBuilder();
            if (StringUtils.isNotEmpty(item.getCstencryptinfo())) {
                rdPvInfo.setUrl("https://cshow.che168.com/shop/show/event")
                        .setJson(String.format("{\"data\":[{\"_appid\":\"%s\",\"encryptinfo\":\"%s\",\"deviceid\":\"%s\",\"userarea\":%s}]}", request.getPm() == 1 ? "main2sc.ios" : "main2sc.android", item.getCstencryptinfo(), request.getDeviceid(), request.getCityid()))
                        .build();
            }
            String savePrice = String.format(isNewSummaryVersion && isNewSummaryTest ? "%s万" : "比新车省%s万", item.getSaveprice());
            // 实验版本去除商家标签
            String mark = isNewSummaryVersion
                    ? String.format("%s/%s", item.getMileage() + "万公里", item.getRegisteyear() + "年")
                    : String.format("%s/%s/%s", item.getMileage() + "万公里", item.getRegisteyear() + "年", item.getDealerlevel());
            SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesid());
            int seriesState = seriesDetailDto == null ? -1 : seriesDetailDto.getState();
            String eid = seriesState == 40 ? "112876" : "104679";
            String eid2 = seriesState == 40 ? "pvareaid%3d112876" : "pvareaid%3d104679";
            UsedCarCard.Builder usedCardBuilder = UsedCarCard.newBuilder()
                    .setInfoid(item.getInfoid())
                    .setLinkurl(item.getDetailurl().replace("pvareaid%3d104679",eid2))
                    .setMark(mark)
                    .setPrice(item.getPrice() + "万")
                    .setSaveprice(savePrice)
                    .setSpecname(item.getCarname())
                    .setSpecimage(ImageUtils.convertImageUrl(item.getImageurl(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300c42))
                    .addAllSubmark(item.getTags())
                    .setRdpvinfo(rdPvInfo)
                    .setPvitem(Pvitem.newBuilder()
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_newusedcar_content_click").putArgvs("linkid", "4").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_newusedcar_content_show").putArgvs("linkid", "4").build())
                            .putArgvs("seriesid", request.getSeriesid() + "")
                            .putArgvs("infoid", item.getInfoid() + "")
                            .putArgvs("typeid", ++index + "")
                            .putArgvs("eid", eid)
                            .putArgvs("cityid", request.getCityid() + "")
                            .putArgvs("specid", request.getSpecid() + "")
                            .build());
            if (isNewSummaryTest) {
                usedCardBuilder.setSavetitle("比新车省");
            }
            builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                    .setType(11031)
                    .setUsedCar(usedCardBuilder)
                    .build());
        }
    }

    /**
     * 同级车
     *
     * @param series
     * @param request
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getSameLevelTab(SeriesDetailDto series, SeriesTabRequest request) {
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();
        CompletableFuture<List<AreaSeriesAttentionDto>> seriesAttentionFuture = areaSeriesAttentionComponent.getAsync(-1);
        CompletableFuture<SameLevelRecommendSeriesResult> guessSameLevelFuture = dataOpenApiClient.getGuessSameLevel(request.getCityid(), 90100217, request.getDeviceid(), request.getPm() == 1 ? "ios" : "android", request.getPluginversion(), series.getId());

        return CompletableFuture.allOf(seriesAttentionFuture, guessSameLevelFuture).thenApply(x -> {
            List<AreaSeriesAttentionDto> seriesAttentionList = seriesAttentionFuture.join();
            SameLevelRecommendSeriesResult guessSameLevel = guessSameLevelFuture.join();
            // 新版车系页修改 https://doc.autohome.com.cn/docapi/page/share/share_xonpSFZvhQ
            boolean isNewSeriesSummary = CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0") && StringUtils.equalsAnyIgnoreCase(request.getNewsummaryab(), "1", "2");
            builder.setTabname(isNewSeriesSummary ? "同级车对比" : "同级车");
            builder.setTabbgurl(getTabbgurl(request.getTabid()));

            if (guessSameLevel != null && guessSameLevel.getResult() != null && guessSameLevel.getResult().getData() != null && !guessSameLevel.getResult().getData().isEmpty()) {
                List<SameLevelRecommendSeriesResult.SameLevelRecommendSeriesInfo.RecommendSeriesSpecPair> items = guessSameLevel.getResult().getData();
                List<Integer> seriesIds = items.stream().map(SameLevelRecommendSeriesResult.SameLevelRecommendSeriesInfo.RecommendSeriesSpecPair::getSeries_id).toList();

                Map<Integer, AreaSeriesAttentionDto> seriesAttenttionMap;
                if (seriesAttentionList != null) {
                    seriesAttenttionMap = seriesAttentionList.stream().filter(xx -> {
                        if (series.getEnergytype() == 1) {
                            return xx.getIsNewEnergy() == 1;
                        } else if (Arrays.asList(21, 22, 23, 24).contains(series.getLevelId())) {
                            return xx.getLevelId() == 8;
                        } else {
                            return xx.getLevelId() == series.getLevelId();
                        }
                    }).collect(Collectors.toMap(AreaSeriesAttentionDto::getSeriesId, Function.identity(), (item, item2) -> item));
                } else {
                    seriesAttenttionMap = new HashMap<>();
                }


                return seriesDetailComponent.getList(seriesIds).thenApply(seriesList -> {
                    int index = 0;
                    int indexEid = 0;
                    List<SameLevelCard.Pvdata> pvList = new ArrayList<>();
                    for (SameLevelRecommendSeriesResult.SameLevelRecommendSeriesInfo.RecommendSeriesSpecPair item : items) {
                        SeriesDetailDto seriesDetail = seriesList.stream().filter(e -> e.getId() == item.getSeries_id()).findFirst().orElse(null);
                        if (seriesDetail == null) {
                            continue;
                        }

                        String attentionRankStr = "";
                        AreaSeriesAttentionDto seriesAttention = seriesAttenttionMap.get(item.getSeries_id());
                        if (seriesAttention != null && seriesAttention.getAtt() > 0) {
                            long rank = seriesAttenttionMap.values().stream().filter(xx -> xx.getAtt() > 0 && xx.getAtt() > seriesAttention.getAtt()).count() + 1;
                            if (series.getEnergytype() == 1) {
                                attentionRankStr = "新能源关注NO." + rank;
                            } else {
                                attentionRankStr = String.format("%s关注NO.%s", seriesDetail.getLevelId() >= 21 && seriesDetail.getLevelId() <= 24 ? "MPV" : seriesDetail.getLevelName(), rank);
                            }
                        }

                        String eid = "";
                        if (item.getIs_ad() == 1) {
                            if (indexEid == 0) {
                                eid = request.getPm() == 1 ? "3|1411002|1373|0|205191|304015" : "3|1412002|1373|0|205191|304016";
                            } else if (indexEid == 1) {
                                eid = request.getPm() == 1 ? "3|1411002|1373|0|205192|304017" : "3|1412002|1373|0|205192|304018";
                            }
                            indexEid++;
                        }

                        SameLevelCard.Pvdata.Builder pvData = SameLevelCard.Pvdata.newBuilder()
                                .setP(String.valueOf(++index))
                                .setPvid(guessSameLevel.getResult().getPvid())
                                .setStra(String.format("{\"t\":\"90100217\",\"pvid\":\"%s\",\"is_ad\":%d,\"pid\":\"90100217\",\"object_id\":%d}", guessSameLevel.getResult().getPvid(), item.getIs_ad(), seriesDetail.getId()));
                        pvList.add(pvData.build());

                        builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                                .setType(11020)
                                .setSameLevel(SameLevelCard.newBuilder()
                                        .setSeriesid(seriesDetail.getId())
                                        .setSeriesname(seriesDetail.getName())
                                        .setPriceinfo(CommonHelper.priceForamt(seriesDetail.getMinPrice(), seriesDetail.getMaxPrice()))
                                        .setImage(CommonHelper.ChangeLogoSize(12, seriesDetail.getPngLogo()))
                                        .setEnergetype(seriesDetail.getPlace().equals("自主") ? "国产" : seriesDetail.getPlace())
                                        .setRefreshtype("1")
                                        .setBtntitle(isNewSeriesSummary ? "对比" : "PK")
                                        .setBtnlinkurl("autohome://carcompare/comprehensivecontrast?specids=" + series.getHotSpecId() + "," + seriesDetail.getHotSpecId() + "&fromtype=13")
                                        .setSpecid(seriesDetail.getHotSpecId())
                                        .setAttentionrank(attentionRankStr)
                                        .setFromtype(1)
                                        .setRecmId("90100217")
                                        .setIsad(item.getIs_ad() == 1 ? 1 : 0)
                                        .setPvdata(pvData)
                                        .setEid(eid)
                                        .build())
                                .build());
                    }

                    builder.setPvdata(SeriesTabResponse.Result.PvData.newBuilder()
                            .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("itemlist").setArgvalue(JSON.toJSONString(pvList)).build())
                            .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("itemcount").setArgvalue(index + "").build())
                            .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("recm_id").setArgvalue("90100217").build())
                            .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("pv_event_id").setArgvalue("car_series_channel_overview_page_pv").build())
                            .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("user_id").setArgvalue("0").build())
                            .addRequestpvargs(SeriesTabResponse.Result.PvItemKV.newBuilder().setArgkey("refreshtype").setArgvalue("1").build())
                            .build());
                    return builder;
                }).join();
            }

            return builder;
        });
    }

    /**
     * 热评
     *
     * @param series
     * @param request
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getHotCommentTab(SeriesDetailDto series, SeriesTabRequest request) {
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();
        //AI观点车系ids
        List<Integer> aiSeriesIds = JsonUtil.toObjectList(aiViewPoinTabSeriesidList, Integer.class);

        CompletableFuture<SeriesHotCommentDto> seriesHotCommentFuture = seriesHotCommentComponent.get(series.getId());
        CompletableFuture<BaseModel<SeriesAiViewPointResult>> seriesAiViewPointFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<BaseModel<List<NewSeriesAiViewPointResult>>> newSeriesAiViewPointFuture = CompletableFuture.completedFuture(null);
        //AI观点
        if (request.getAiviewpointab().equals("A") && aiSeriesIds.contains(request.getSeriesid())) {
            if (CommonHelper.isTakeEffectVersion(request.getPluginversion(),"11.67.8")){
                newSeriesAiViewPointFuture = replyApiClient.getNewSeriesAiViewPoint(request.getSeriesid(),request.getDeviceid());
            }else{
                seriesAiViewPointFuture = replyApiClient.getSeriesAiViewPoint(request.getSeriesid(), request.getPm(), request.getPluginversion(), request.getDeviceid(), request.getAuthorization());
            }
        }
        List<String> headImgList = UserPhotoUtils.getSeriesReplyHeadImg(4);
        builder.addAllRighticonlist(headImgList);
        builder.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/116150/hot_comment_title_background.webp");
        builder.setTabname("车系评论");
        builder.setTabnameicon("http://nfiles3.autohome.com.cn/zrjcpk10/cpk/serieshotreply/cxpl_0514.webp");

        CompletableFuture<BaseModel<SeriesAiViewPointResult>> finalSeriesAiViewPointFuture = seriesAiViewPointFuture;
        CompletableFuture<BaseModel<List<NewSeriesAiViewPointResult>>> finalNewSeriesAiViewPointFuture = newSeriesAiViewPointFuture;
        return CompletableFuture.allOf(seriesHotCommentFuture, seriesAiViewPointFuture,newSeriesAiViewPointFuture).thenApply(x -> {
            SeriesHotCommentDto hotComment = seriesHotCommentFuture.join();
            BaseModel<SeriesAiViewPointResult> aiPoint = finalSeriesAiViewPointFuture.join();
            BaseModel<List<NewSeriesAiViewPointResult>> newAiPoint = finalNewSeriesAiViewPointFuture.join();

            String topRightText = "查看更多热评";
            if (hotComment != null && hotComment.getCount() > 0) {
                topRightText = "查看" + formatReplyCount(hotComment.getCount()) + "条评论";
            }

            boolean isHitAi = false;
            if (aiPoint != null && aiPoint.getReturncode() == 0 && aiPoint.getResult() != null) {
                isHitAi = true;
                HotCommentCard hotCommentCard = MessageUtil.toMessage(aiPoint.getResult(), HotCommentCard.class);
                builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                        .setHotComment(hotCommentCard)
                        .setType(11099)
                        .build());
            }
            if (newAiPoint != null && newAiPoint.getReturncode() == 0 && !CollectionUtils.isEmpty(newAiPoint.getResult())){
                isHitAi = true;
                List<AiViewPointCard.ViewPoint> viewPoints = new ArrayList<>();
                for (NewSeriesAiViewPointResult bean : newAiPoint.getResult()) {
                    AiViewPointCard.ViewPoint viewPoint = AiViewPointCard.ViewPoint.newBuilder()
                            .setTitle(bean.getPointTitle())
                            .setContentSeq(bean.getContentSeq())
                            .setRequestNo(bean.getRequestNo())
                            .setLinkurl("autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=" + UrlUtil.encode(String.format("rn://CarSeriesTestRN/AiPointPage?panValid=0&seriesid=%s&seq=%s&requestno=%s",request.getSeriesid(),bean.getContentSeq(),bean.getRequestNo())))
                            .build();
                    viewPoints.add(viewPoint);
                }
                builder.addList(SeriesTabResponse.Result.CardData.newBuilder()
                        .setAiViewpoint(AiViewPointCard.newBuilder()
                                .setButtonicon("https://z.autoimg.cn/ued/car_selection/button_animation_1012.gif")
                                .setSeriesId(request.getSeriesid())
                                .addAllList(viewPoints)
                                .build())
                        .setType(12211)
                        .build());
            }
            builder.setToprightbtn(SeriesTabResponse.Result.Toprightbtn.newBuilder()
                    .setBtnurl("autohome://article/seriescommentpage?seriesid=" + series.getId() + "&objtype=1000002" + (isHitAi ? "&isshowai=1" : ""))
                    .setText(topRightText).build());

            builder.addButtonbtnlist(SeriesTabResponse.Result.ButtonBtnItem.newBuilder()
                    .setText("写评论")
                    .setType(1)
                    .setScheme("")
                    .setGuidetext((hotComment != null && StringUtils.isNotEmpty(hotComment.getGuideText())) ? hotComment.getGuideText() : "")
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("copywriting", "写评论")
                            .putArgvs("linkid", "2")
                            .putArgvs("seriesid", series.getId() + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_comment_content_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_comment_content_show").build())
                            .build())
                    .build());
            builder.addButtonbtnlist(SeriesTabResponse.Result.ButtonBtnItem.newBuilder()
                    .setText(topRightText)
                    .setType(2)
                    .setScheme("autohome://article/seriescommentpage?seriesid=" + series.getId() + "&objtype=1000002" + (isHitAi ? "&isshowai=1" : ""))
                    .addAllAnimateiconlist(headImgList)
                    .setPvitem(Pvitem.newBuilder()
                            .putArgvs("linkid", "3")
                            .putArgvs("seriesid", series.getId() + "")
                            .setClick(Pvitem.Click.newBuilder().setEventid("car_series_comment_content_click").build())
                            .setShow(Pvitem.Show.newBuilder().setEventid("car_series_comment_content_show").build())
                            .build())
                    .build());
            return builder;
        });
    }

    private String getTabbgurl(int tabid) {
        switch (tabid) {
            case 2://资讯模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_spec_zixun_20230209@3x.jpg.webp";
            case 4://论坛模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_summary_club_channel_20230209@3x.jpg.webp";
            case 10://口碑模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_koubei_20230209@3x.jpg.webp";
            case 11://玩车模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_playcar_20230209@3x.png.webp";
            case 12://同级车模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_samecar_20230209@3x.png.webp";
            case 15://用车模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_usecar_20230209@3x.png.webp";
            case 16://买车模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_buycar_20230209@3x.jpg.webp";
            case 18://二手车模块
                return "http://nfiles3.autohome.com.cn/zrjcpk10/car_sum_usedcar_20230228@3x.png.webp";
        }
        return "";
    }

    private static String formatReplyCount(int replyCount) {
        if (replyCount < 10000) {
            return String.valueOf(replyCount);
        } else {
            double countInWan = (double) replyCount / 10000.0;
            double countInWanD = Double.parseDouble(String.format("%.1f", countInWan));
            if (countInWanD % 1 == 0) {
                return String.format("%.0f万", countInWan);
            } else {
                return String.format("%.1f万", countInWan);
            }
        }
    }

    /**
     * 论坛tab
     *
     * @param req
     * @return
     */
    public CompletableFuture<SeriesTabResponse.Result.Builder> getSeriesClubCard(SeriesTabRequest req) {
        SeriesTabResponse.Result.Builder builder = SeriesTabResponse.Result.newBuilder();
        try {

            builder.setTabname("论坛互动");
            builder.setTabbgurl("http://nfiles3.autohome.com.cn/zrjcpk10/car_summary_club_channel_20230209@3x.jpg.webp");

            SeriesTabResponse.Result.Bottombtn.Builder bottombtn = SeriesTabResponse.Result.Bottombtn.newBuilder();

            String moreSchema = "autohome://club/topiclist?bbstype=c&seriesid=" + req.getSeriesid() + "&select=1&sort=3&from=10";
            bottombtn.setBtnurl(moreSchema);
            bottombtn.setText("查看更多帖子");
            if (2 == req.getFrom()) {
                Pvitem.Builder pvitem = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(req.getSeriesid()))
                        .putArgvs("specid", String.valueOf(req.getSpecid()))
                        .putArgvs("pagetabid", "23")
                        .setShow(Pvitem.Show.newBuilder().setEventid("car_spec_secondtab_awc_morebtn_show"))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_spec_secondtab_awc_morebtn_click"));

                bottombtn.setPvitem(pvitem);
            }
            builder.setBottombtn(bottombtn.build());

            SeriesTabResponse.Result.Toprightbtn.Builder toprightbtn = SeriesTabResponse.Result.Toprightbtn.newBuilder();
            toprightbtn.setBtnurl(moreSchema);
            toprightbtn.setText("更多");
            builder.setToprightbtn(toprightbtn.build());

            SeriesClubCardTagDto tagDto = clubTag.get(req.getSeriesid());

            if (null != tagDto) {
                for (SeriesClubCardTagDto.ClubTab tagTab : tagDto.getTablist()) {
                    SeriesTabResponse.Result.Subtablist.Builder subTab = SeriesTabResponse.Result.Subtablist.newBuilder();
                    subTab.setLinkurl(tagTab.getLinkurl());
                    subTab.setTabid(tagTab.getTabid());
                    subTab.setName(tagTab.getName());
                    builder.addSubtablist(subTab.build());
                }
            }


            List<CompletableFuture> tasks = new ArrayList<>();

            SeriesClubCardData seriesClubCardData = new SeriesClubCardData();

            tasks.add(clubHot.get(req.getSeriesid())
                    .thenAccept(res -> seriesClubCardData.setClubHotList(res))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        log.error("clubHot---error:{}", e.getMessage());
                        return null;
                    }));


            List<Integer> tabIdList = new ArrayList<>();
            if (null != tagDto && null != tagDto.getTablist() && !CollectionUtils.isEmpty(tagDto.getTablist())) {
                List<TreeMap<String, Object>> params = new ArrayList<>();
                tabIdList = tagDto.getTablist().stream().filter(e -> e.getTabid() > 0).map(e -> e.getTabid()).collect(Collectors.toList());

                for (Integer tagId : tabIdList) {
                    params.add(clubData.makeParam(req.getSeriesid(), tagId));
                }

                tasks.add(clubData.mGet(params)
                        .thenAccept(res -> {
                            res.forEach((k, v) -> {
                                if (k != null && v != null) {
                                    seriesClubCardData.getSeriesClubCardDataMap().put(k, v);
                                }
                            });
                        })
                        .exceptionally(e -> {
                            e.printStackTrace();
                            log.error("clubData---error:{}", e.getMessage());
                            return null;
                        }));
            }


            List<Integer> finalTabIdList = tabIdList;
            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x -> {

                Map<Integer, ReplyAndLikeDto> replyAndLikeCountMap = getReplyAndLikeList(seriesClubCardData);

                List<SeriesClubCardDataDto> clubTopicList = seriesClubCardData.getClubHotList();

                int pageSize = 2 == req.getFrom() ? 5 : 3;

                if (null != clubTopicList) {
                    //热门
                    setCardList(builder, clubTopicList, replyAndLikeCountMap, 0, req.getSeriesid(), pageSize);
                }

                //其余四个
                for (Integer tagId : finalTabIdList) {
                    List<SeriesClubCardDataDto> cardDataResult = seriesClubCardData.getSeriesClubCardDataMap().get(tagId);
                    if (null != cardDataResult) {
                        setCardList(builder, cardDataResult, replyAndLikeCountMap, tagId, req.getSeriesid(), pageSize);
                    }
                }

                return builder;
            }).exceptionally(e -> {
                e.printStackTrace();
                log.error("获取车系页论坛卡片报错1,seriesid:{},e{}", req.getSeriesid(), e);
                return null;
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取车系页论坛卡片报错2,seriesid:{},{}", req.getSeriesid(), e);
            return CompletableFuture.completedFuture(null);
        }

    }

    private Map<Integer, ReplyAndLikeDto> getReplyAndLikeList(SeriesClubCardData seriesClubCardData) {

        Map<Integer, ReplyAndLikeDto> map = new HashMap<>();
        List<Integer> bizIdList = new ArrayList<>();

        List<SeriesClubCardDataDto> clubTopicList = seriesClubCardData.getClubHotList();
        if (null != clubTopicList && !CollectionUtils.isEmpty(clubTopicList)) {
            bizIdList.addAll(clubTopicList.stream().map(SeriesClubCardDataDto::getBiz_id).collect(Collectors.toList()));
        }
        seriesClubCardData.getSeriesClubCardDataMap().entrySet().forEach(e ->
                {
                    if (e != null && null != e.getValue() && !CollectionUtils.isEmpty(e.getValue())) {
                        bizIdList.addAll(e.getValue().stream().map(SeriesClubCardDataDto::getBiz_id).collect(Collectors.toList()));
                    }
                }

        );

        //获取热点数据
        List<String> bizIdTypes = bizIdList.stream()
                .flatMap(bizId -> Stream.of(
                        "club-reply_count-" + bizId,
                        "club-like_count-" + bizId
                )).distinct().toList();

        BaseModel<List<HotDataResult>> hotDataResult = mainDataApiClient.getHotData(String.join(",", bizIdTypes)).join();

        if (null != hotDataResult && null != hotDataResult.getResult() && 0 == hotDataResult.getReturncode()) {

            Map<Integer, List<HotDataResult>> hotDataResultMap = hotDataResult.getResult().stream().collect(Collectors.groupingBy(HotDataResult::getBiz_id));
            hotDataResultMap.entrySet().forEach(e -> {
                ReplyAndLikeDto dto = new ReplyAndLikeDto();
                dto.setBizId(e.getKey());
                e.getValue().forEach(v -> {
                    if ("reply_count".equals(v.getHot_data_type())) {
                        dto.setReplyCount(v.getCount());
                    }
                    if ("like_count".equals(v.getHot_data_type())) {
                        dto.setLikeCount(v.getCount());
                    }
                });
                map.put(dto.getBizId(), dto);

            });

            return map;
        }

        return null;
    }


    private void setCardList(SeriesTabResponse.Result.Builder builder, List<SeriesClubCardDataDto> dataList, Map<Integer, ReplyAndLikeDto> replyAndLikeCountMap, int tagId, int seriesId, int pageSize) {

        int count = 0;

        for (SeriesClubCardDataDto item : dataList) {
            if (count >= pageSize) {
                break;
            }
            SeriesTabResponse.Result.CardData.Builder card = SeriesTabResponse.Result.CardData.newBuilder();

            ClubCard.Builder data = ClubCard.newBuilder();

            // list >> data >> carddata
            ClubCard.CardData.Builder cardData = ClubCard.CardData.newBuilder();

            // list >> data >> carddata >> cardinfo
            ClubCard.CardData.CardInfo.Builder cardInfo = ClubCard.CardData.CardInfo.newBuilder();

            // list >> data >> carddata >> cardinfo >> userinfo
            ClubCard.CardData.CardInfo.UserInfo.Builder userInfo = ClubCard.CardData.CardInfo.UserInfo.newBuilder();
            userInfo.setUserid(item.getAuthor_id());

            userInfo.setUsername(StringUtils.isNotEmpty(item.getUsername()) ? item.getUsername() : ("之家车友" + item.getAuthor_id()));

            //用户头像和认证车
            String headImg = "http://x.autoimg.cn/account/Images/mr001.png?format=webp";
            userInfo.setHeadimg(StringUtils.isNotEmpty(item.getHeadimg()) ? item.getHeadimg() : headImg);
            userInfo.setAdddate("");
            userInfo.setCityname("");
            userInfo.setAuthseriesid(item.getAuthseriesid());
            userInfo.setAuthseriesname(null == item.getAuthseriesname() ? "" : (item.getAuthseriesname() + "车主"));
            userInfo.setAuthlevel(item.getAuthlevel());
            if (item.getRzcList().size() > 1) {
                Optional<UserAuthSeriesResult.AuthseriesResult> rzc = item.getRzcList().stream().filter(e -> e.getSeriesId() == seriesId).findFirst();
                if (rzc.isPresent()) {
                    userInfo.setAuthseriesname(rzc.get().getSeriesName() + "车主");
                }
            }

            cardInfo.setUserinfo(userInfo);

            String detailScheme = String.format("autohome://club/topicdetail?topicid=%d&bbsid=%d&from=10", item.getBiz_id(), item.getClub_bbs_id());

            // list >> data >> carddata >> cardinfo >> tagsv2
            ClubCard.CardData.CardInfo.Tags.Builder tagsV2 = ClubCard.CardData.CardInfo.Tags.newBuilder();

            //list >> data >> carddata >> cardinfo >> tagsv2 >> list
            ClubCard.CardData.CardInfo.Tags.TagInfo.Builder tag = ClubCard.CardData.CardInfo.Tags.TagInfo.newBuilder();

            tag.setPosition(1000);
            tag.setStyletype(0);
            String text = item.getClub_topic_lastPostDate() == null ? "" : DateUtil.pastTime(DateUtil.parse(item.getPublish_time(), "yyyy/MM/dd HH:mm:ss"));
            tag.setText(text);
            tagsV2.addList(tag.build());

            //list >> data >> carddata >> cardinfo >> tagsv2 >> replys
            ClubCard.CardData.CardInfo.Tags.ReplyInfo.Builder reply = ClubCard.CardData.CardInfo.Tags.ReplyInfo.newBuilder();

            if (null != replyAndLikeCountMap) {
                ReplyAndLikeDto replyAndLikeDto = replyAndLikeCountMap.get(item.getBiz_id());
                if (null != replyAndLikeDto) {
                    reply.setReplycount(replyAndLikeDto.getReplyCount());
                    reply.setPrizecount(replyAndLikeDto.getLikeCount());
                    reply.setShowcontrol("1");
                    reply.setReplyscheme(detailScheme + (item.getReply_count() > 0 ? "&isreply=1" : ""));
                    tagsV2.setReplys(reply);
                    cardInfo.setTagsv2(tagsV2);
                }
            }

            String imgs = StringUtils.isNotEmpty(item.getClub_jingxuan_imgs()) ? item.getClub_jingxuan_imgs() : item.getImgList();
            if (StringUtils.isNotEmpty(imgs)) {
                List<String> imgList = Arrays.asList(imgs.split(","));
                for (String imgurl : imgList) {
                    // list >> data >> carddata >> cardinfo >> img
                    ClubCard.CardData.CardInfo.Img.Builder img = ClubCard.CardData.CardInfo.Img.newBuilder();
                    img.setScheme(detailScheme);
                    img.setUrl(ImageUtils.convertImageUrl(imgurl, true, false, false));
                    if (cardInfo.getImgList().size() < 3) {
                        cardInfo.addImg(img.build());
                    }
                }

            }

            int cardType = 0;
            int mediaType = 1;
            if (cardInfo.getImgList().size() == 0) {
                cardType = ClubCardTypeEnum.TEXT.getId();
            } else if (cardInfo.getImgList().size() == 1) {
                cardType = ClubCardTypeEnum.BIGIMGCARD.getId();
            } else {// 最多3张
                cardType = ClubCardTypeEnum.THREEIMGCARD.getId();
            }

            if (item.getClub_is_poll() == 80) {
                cardType = ClubCardTypeEnum.VIDEO.getId();
                mediaType = 2;
                ClubCard.CardData.CardInfo.Video.Builder video = ClubCard.CardData.CardInfo.Video.newBuilder();

                video.setIconurl("");
                video.setPlaytime(DateUtil.formatTime(Integer.valueOf(item.getDuration())));
                video.setVideoid(item.getVideo_source());
                video.setVideourl(item.getVideo_source());
                cardInfo.setVideo(video.build());
                cardInfo.clearImg();

                ClubCard.CardData.CardInfo.Img.Builder img = ClubCard.CardData.CardInfo.Img.newBuilder();
                img.setScheme(detailScheme);
                img.setUrl(ImageUtils.convertImage_ToWebp(item.getClub_videoimgs()));
                cardInfo.addImg(img.build());
            }


            cardInfo.setBbs(item.getClub_bbs_type());
            cardInfo.setBbsid(item.getClub_bbs_id());
            cardInfo.setBizid(item.getBiz_id());
            cardInfo.setTitle(StringUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            if (20 == item.getClub_is_poll()) {
                if (StringUtils.isNotEmpty(item.getSubtitle())) {
                    cardInfo.setTitle(item.getSubtitle());
                } else if (StringUtils.isNotEmpty(item.getSummary())) {
                    cardInfo.setTitle(StringUtils.left(item.getSummary(), 100));
                } else if (StringUtils.isNotEmpty(item.getImgList())) {
                    cardInfo.setTitle("");
                    cardInfo.clearImg();

                    List<String> imgList = Arrays.asList(item.getImgList().split(","));
                    for (String imgurl : imgList) {
                        // list >> data >> carddata >> cardinfo >> img
                        ClubCard.CardData.CardInfo.Img.Builder img = ClubCard.CardData.CardInfo.Img.newBuilder();
                        img.setScheme(detailScheme);
                        img.setUrl(ImageUtils.convertImageUrl(imgurl, true, false, false));
                        if (cardInfo.getImgList().size() < 3) {
                            cardInfo.addImg(img.build());
                        }
                    }

                    if (cardInfo.getImgList().size() == 0) {
                        cardType = ClubCardTypeEnum.TEXT.getId();
                    } else if (cardInfo.getImgList().size() == 1) {
                        cardType = ClubCardTypeEnum.BIGIMGCARD.getId();
                    } else {// 最多3张
                        cardType = ClubCardTypeEnum.THREEIMGCARD.getId();
                    }

                }
            }

            cardData.setBizid(item.getBiz_id());
            cardData.setCardinfo(cardInfo);
            cardData.setCardtype(cardType);
            cardData.setMediatype(mediaType);

            cardData.setPvitem(Pvitem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(seriesId))
                    .putArgvs("bizid", String.valueOf(item.getBiz_id()))
                    .putArgvs("linkid", "0")
                    .putArgvs("typeid", String.valueOf(tagId))
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_series_club_content_click").build())
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_series_club_content_show").build())
                    .build());

            data.setCarddata(cardData);

            //list >> data >> extension
            ClubCard.Extension.Builder extension = ClubCard.Extension.newBuilder();
            extension.setScheme(detailScheme);

            //list >> data >> extension >> objinfo
            ClubCard.Extension.ObjInfo.Builder objInfo = ClubCard.Extension.ObjInfo.newBuilder();
            objInfo.setBizid(item.getBiz_id());
            objInfo.setJhtopic(item.getClub_refine() >= 3 ? 1 : 0);
            objInfo.setIspoll(item.getClub_is_poll());

            //list >> data >> extension >> objinfo >> share
            ClubCard.Extension.ObjInfo.ShareInfo.Builder share = ClubCard.Extension.ObjInfo.ShareInfo.newBuilder();
            String spic = "";
            if (null != item.getClub_topicimgs() && !CollectionUtils.isEmpty(item.getClub_topicimgs()) && item.getClub_topicimgs().size() > 0) {
                spic = item.getClub_topicimgs().get(0);
            }
            share.setPic(spic);
            share.setUrl(item.getPc_url());
            share.setTitle(item.getTitle());
            objInfo.setShare(share);

            extension.setObjinfo(objInfo);

            data.setExtension(extension);
            data.setTabid(tagId);

            card.setClubData(data).setType(10000);

            builder.addList(card);

            count++;
        }


    }
}
