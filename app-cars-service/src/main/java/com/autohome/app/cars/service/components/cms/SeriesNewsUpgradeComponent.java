package com.autohome.app.cars.service.components.cms;

import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.bicard.entity.BICardItemModel;
import com.autohome.app.bicard.entity.cardinfo.Card_CardInfo_TagModel;
import com.autohome.app.bicard.enums.TagPositionEnums;
import com.autohome.app.cars.apiclient.chejiahao.ChejiahaoClient;
import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.SeriesAllTabResult;
import com.autohome.app.cars.apiclient.maindata.MainDataApiClient;
import com.autohome.app.cars.apiclient.maindata.dtos.MainDataSeriesSummaryFeedsResult;
import com.autohome.app.cars.apiclient.maindata.dtos.MultipleInfoFeed;
import com.autohome.app.cars.apiclient.openApi.DataOpenApiClient;
import com.autohome.app.cars.apiclient.openApi.dtos.SeriesHotEventResult;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.common.utils.ListUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowNewsDto;
import com.autohome.app.cars.service.components.cms.dtos.MainDataTypeEnum;
import com.autohome.app.cars.service.components.cms.dtos.MediaTypeEnums;
import com.autohome.app.cars.service.components.cms.dtos.SeriesSummaryNewTabEnum;
import com.autohome.app.cars.service.components.cms.dtos.biadapter.*;
import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@DBConfig(tableName = "series_upgrade_news")
@Slf4j
public class SeriesNewsUpgradeComponent extends BaseComponent<Map<String, List<BICardItemModel>>>{

    @Autowired
    MainDataApiClient mainDataClient;

    @Autowired
    DataOpenApiClient openApiClient;

    @Autowired
    ChejiahaoClient chejiahaoClient;

    @Autowired
    CmsApiClient cmsApiClient;

    @Autowired
    AutoShowNewsComponent autoShowNewsComponent;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;


    TreeMap<String, Object> makeParam(int seriesId) {
        return BaseComponent.ParamBuilder.create("seriesId", seriesId).build();
    }

    public CompletableFuture<Map<String, List<BICardItemModel>>> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        Map<Integer, BICardItemModel> hotData = getHotSeriesNews().join();
        //refreshOne(7198,hotData);
        loopSeries(totalMinutes, seriesid -> {
            refreshOne(seriesid, hotData);
        }, xxlLog);
    }

    public void refreshOne(int seriesid, Map<Integer, BICardItemModel> hotData) {
        Map<String, List<BICardItemModel>> tabs = new HashMap<>();
        List<CompletableFuture> tasks = new ArrayList<>();
        for (SeriesSummaryNewTabEnum tabitem : SeriesSummaryNewTabEnum.values()) {
            switch (tabitem){
                case AUTOSHOW ->
                    //处理车展数据
                        tasks.add(getAutoShowNewsList(seriesid).thenAccept(x -> {
                            if (x == null || x.isEmpty()) {
                                return;
                            }
                            tabs.put(tabitem.getInfoType(), x);
                        }));
                case ALL ->
                    //处理全部tab数据
                        tasks.add(getAllTabList(seriesid).thenAccept(x -> {
                            if (x == null || x.isEmpty()) {
                                return;
                            }
                            tabs.put(tabitem.getInfoType(), x);
                        }));
                default ->
                        //处理原创、视频、评测等tab数据
                        tasks.add(getOtherTabList(seriesid, tabitem).thenAccept(x -> {
                            if (x == null || x.isEmpty()) {
                                return;
                            }
                            tabs.put(tabitem.getInfoType(), x);
                        }));
            }
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenAccept(x -> {
            if (tabs.isEmpty()) {
                return;
            }
            update(makeParam(seriesid), tabs);
        }).join();
    }

    CompletableFuture<Map<Integer, BICardItemModel>> getHotSeriesNews() {
        Map<Integer, BICardItemModel> result = new HashMap<>();
        return openApiClient.getHotSeriesNews().thenCompose(apiResult -> {
            if (apiResult == null || apiResult.getReturncode() != 0) {
                return CompletableFuture.completedFuture(result);
            }
            List<Integer> bizTypes = Arrays.asList(1, 3, 12, 14);
            Map<Integer, List<String>> seriesMainDataIds = new HashMap<>();
            for (SeriesHotEventResult.ResultDTO.ItemlistDTO item : apiResult.getResult().getItemlist()) {
                if (StringUtils.isBlank(item.getSeriesIds())) {
                    continue;
                }
                List<Integer> seriesIds = Arrays.stream(item.getSeriesIds().split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
                String[] ids = item.getItemList().split("/");
                List<String> mainDataIds = new ArrayList<>();
                for (String itemId : ids) {
                    String[] split = itemId.split("-");
                    if (split.length < 2) {
                        continue;
                    }
                    int bizType = Integer.parseInt(split[0]);
                    if (bizTypes.contains(bizType)) {
                        String prefix = "chejiahao";
                        if (bizType == 1) {
                            prefix = "cms";
                        } else if (bizType == 3) {
                            prefix = "video";
                        }
                        mainDataIds.add(prefix + "-" + split[1]);
                    }
                }
                for (Integer seriesId : seriesIds) {
                    if (seriesMainDataIds.containsKey(seriesId)) {
                        continue;
                    }
                    seriesMainDataIds.put(seriesId, mainDataIds);
                }
            }

            List<String> mdids = new ArrayList<>();
            for (List<String> value : seriesMainDataIds.values()) {
                mdids.addAll(value);
            }

            mdids = mdids.stream().distinct().collect(Collectors.toList());

            return mainDataClient.getMultipleInfos(
                    "author_name,vv,pv,author_img,img_list,img_url_16x9,cms_refine,title,pool_biz_type,cms_kind,duration,publish_time,pc_url,small_title,app_url,main_data_type,img_url_4x3,label,reply_count,is_close_comment,m_url,biz_update_time,parent_biz_id,multi_images,img_url,cms_createsource,biz_id,author_id"
                    , String.join(",", mdids)).thenApply(datas -> {
                if (datas == null || datas.getReturncode() != 0 || datas.getResult() == null || datas.getResult().size() == 0) {
                    return result;
                }
                Map<String, MultipleInfoFeed> mds = datas.getResult().stream().collect(Collectors.toMap(x -> x.getMain_data_type() + "-" + x.getBiz_id(), x -> x));
                seriesMainDataIds.forEach((k, v) -> {
                    for (String s : v) {
                        if (mds.containsKey(s)) {
                            MultipleInfoFeed item = mds.get(s);
                            MediaTypeEnums mediaTypeEnums = getMediaTypeEnumsByMainDataType(item.getMain_data_type(), item.getCms_refine(), item.getParent_biz_id(), item.getCms_createsource());
                            MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed itemNew = new MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed();
                            BeanUtils.copyProperties(item, itemNew);
                            itemNew.setHot_event(1);
                            if(item.getMulti_images()!=null && item.getMulti_images().length > 0) {
                                itemNew.setMulti_images(String.join(",", item.getMulti_images()));
                            }
                            BICardItemModel hotFeed = BI_AdapterFactorys.getCardItemModel_SeriesSummary_News_Feed(mediaTypeEnums, itemNew);
                            if (hotFeed == null) {
                                continue;
                            }
                            result.put(k, hotFeed);
                            return;
                        }
                    }
                });
                return result;
            });
        });
    }
    CompletableFuture<List<BICardItemModel>> getAutoShowNewsList(int seriesId) {
        if (autoShowConfig.IsBetweenDate()) {
            return autoShowNewsComponent.get(autoShowConfig.getAutoshowid(), seriesId).thenApply(authShowDto -> {
                List<BICardItemModel> newsList = new ArrayList<>();
                if (authShowDto == null || authShowDto.getNewsItems() == null || authShowDto.getNewsItems().size() == 0) {
                    return newsList;
                }
                for (AutoShowNewsDto.NewsItem newsItem : authShowDto.getNewsItems()) {
                    MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed itemNew = new MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed();
                    BeanUtils.copyProperties(newsItem, itemNew);
                    MediaTypeEnums mediaTypeEnums = getMediaTypeEnumsByMainDataType(itemNew.getMain_data_type(), itemNew.getCms_refine(), itemNew.getParent_biz_id(), itemNew.getCms_createsource());
                    BICardItemModel model = BI_AdapterFactorys.getCardItemModel_SeriesSummary_News_Feed(mediaTypeEnums, itemNew);
                    ListUtil.addIfNotNull(newsList, model);
                }
                if (newsList == null || newsList.size() == 0) {
                    return newsList;
                }
                if (newsList.size() > 5) {
                    newsList = newsList.subList(0, 5);
                }
                return newsList;
            }).exceptionally(e -> {
                log.error("车展数据拉取失败", e);
                return null;
            });
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    private CompletableFuture<List<BICardItemModel>> getOtherTabList(int seriesid, SeriesSummaryNewTabEnum tabitem) {
        //原创、视频、评测三个tab取自一个数据源
        return mainDataClient.getNewMainDataSeriesSummaryFeedsV2(seriesid, tabitem.getInfoType(), "", 20).thenApply(result -> {
            List<BICardItemModel> newsList = new Vector<>();
            if (result == null || result.getReturncode() != 0 || result.getResult() == null || CollectionUtils.isEmpty(result.getResult().getItems())) {
                return newsList;
            }
            List<MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed> items = result.getResult().getItems();
            //车系资讯下原创tab、评测tab中的内容需过滤原创视频和车家号视频
            if (tabitem.equals(SeriesSummaryNewTabEnum.ORIGINAL) || tabitem.equals(SeriesSummaryNewTabEnum.EVALUATION)) {
                items.removeIf(p -> StringUtils.isEmpty(p.getMain_data_type())
                        || StringUtils.equals(p.getMain_data_type(), MainDataTypeEnum.VIDEO.getValue())
                        || (StringUtils.equals(p.getMain_data_type(), MainDataTypeEnum.CHEJIAHAO.getValue()) && p.getParent_biz_id() == 3)
                );
            }

            for (MainDataSeriesSummaryFeedsResult.MainDataSeriesSummaryFeed item : items) {
                MediaTypeEnums mediaTypeEnums = getMediaTypeEnumsByMainDataType(item.getMain_data_type(), item.getCms_refine(), item.getParent_biz_id(), item.getCms_createsource());
                BICardItemModel model = BI_AdapterFactorys.getCardItemModel_SeriesSummary_News_Feed(mediaTypeEnums, item);
                if (model == null || model.getCarddata() == null) {
                    continue;
                }
                //原创tab下，不显示原创标签
                if (tabitem.equals(SeriesSummaryNewTabEnum.ORIGINAL) && model.getCarddata().getCardinfo() != null && model.getCarddata().getCardinfo().getTaginfo() != null) {
                    model.getCarddata().getCardinfo().getTaginfo().removeIf(x -> x.getText().equalsIgnoreCase("之家原创"));
                }
                newsList.add(model);
            }

            if (newsList.size() > 5) {
                //截取前五条数据
                newsList = newsList.subList(0, 5);
            }
            return newsList;
        }).exceptionally(ex -> {
            log.error("获取"+ tabitem.getName() + "tab报错", ex);
            return null;
        });
    }

    private CompletableFuture<List<BICardItemModel>> getAllTabList(int seriesId) {
        return mainDataClient.getAllTabMainDataSeriesSummaryFeeds(seriesId).thenApply(datas -> {
            List<BICardItemModel> newsList = new ArrayList<>();
            if (datas == null || datas.getReturncode() != 0 || CollectionUtils.isEmpty(datas.getResult())){
                return newsList;
            }
            List<SeriesAllTabResult> news = datas.getResult();
            for (SeriesAllTabResult item : news) {
                MediaTypeEnums mediaTypeEnums = getMediaTypeEnumsByMainDataType(item.getBizType());
                BICardItemModel model = BI_AdapterFactorys.getCardItemModel_SeriesSummary_News_FeedV2(mediaTypeEnums, item);
                if (model == null || model.getCarddata() == null) {
                    continue;
                }
                newsList.add(model);
            }
            return newsList;
        }).exceptionally(ex -> {
            log.error("获取'全部'tab报错",ex);
            return null;
        });
    }


    private MediaTypeEnums getMediaTypeEnumsByMainDataType(int bizType) {
        /**
         * putongnew:1 tuwen:10 fastnews:700112
         * YCChangWen:12 YCVideo:14 YC_CheDan:206
         * NewsTopic:26 AH_100:64 Video:3
         */
        MediaTypeEnums mediaTypeEnums = null;
            switch (bizType) {
                case 1 :
                    mediaTypeEnums = MediaTypeEnums.PuTongNew;
                    break;
                case 3 :
                    mediaTypeEnums = MediaTypeEnums.Video;
                    break;
                case 10 :
                    mediaTypeEnums = MediaTypeEnums.TuWen;
                    break;
                case 12 :
                    mediaTypeEnums = MediaTypeEnums.YCChangWen;
                    break;
                case 14 :
                    mediaTypeEnums = MediaTypeEnums.YCVideo;
                    break;
                case 26 :
                    mediaTypeEnums = MediaTypeEnums.NewsTopic;
                    break;
                case 64 :
                    mediaTypeEnums = MediaTypeEnums.AH_100;
                    break;
                case 206 :
                    mediaTypeEnums = MediaTypeEnums.YC_CheDan;
                    break;
                case 700112 :
                    mediaTypeEnums = MediaTypeEnums.FAST_NEWS;
                    break;
                default:
                    break;
            }
        return mediaTypeEnums;
    }

    private MediaTypeEnums getMediaTypeEnumsByMainDataType(String mainDataType, Integer cms_Refine, Integer parent_Biz_Id, Integer cmsCreateSource) {
        MediaTypeEnums mediaTypeEnums = null;
        MainDataTypeEnum mainDataTypeEnum = MainDataTypeEnum.getByValue(mainDataType);
        if (mainDataTypeEnum != null) {
            switch (mainDataTypeEnum) {
                case CMS:
                    mediaTypeEnums = cms_Refine == 10 ? MediaTypeEnums.TuWen : MediaTypeEnums.PuTongNew;
                    if (cmsCreateSource != null && cmsCreateSource == 2) {
                        mediaTypeEnums = MediaTypeEnums.FAST_NEWS;
                    }
                    break;
                case CHEJIAHAO:
                    if (parent_Biz_Id == 1) {
                        mediaTypeEnums = MediaTypeEnums.YCChangWen;
                    } else if (parent_Biz_Id == 3) {
                        mediaTypeEnums = MediaTypeEnums.YCVideo;
                    } else if (parent_Biz_Id == 7) {
                        mediaTypeEnums = MediaTypeEnums.YC_CheDan;
                    }
                    break;
                case CMS_TOPIC:
                    mediaTypeEnums = MediaTypeEnums.NewsTopic;
                    break;
                case CMS_AH100:
                    mediaTypeEnums = MediaTypeEnums.AH_100;
                    break;
                case VIDEO:
                    mediaTypeEnums = MediaTypeEnums.Video;
                    break;
                default:
                    break;
            }
        }
        return mediaTypeEnums;
    }

    CompletableFuture<BICardItemModel> getTopNews(int seriesId) {
        return cmsApiClient.getTopSeriesNews(seriesId).thenApply(apiResult -> {
            if (apiResult == null || apiResult.getReturncode() != 0 || apiResult.getResult() == null) {
                return null;
            }
            BICardItemModel biCardItemModel = null;
            switch (apiResult.getResult().getType()) {
                case 1:
                    biCardItemModel = new BI_NewsAdapter(apiResult.getResult()).initCardItemModel_SeriesSummary_News_Feed_Top();
                    break;
                case 3:
                    biCardItemModel = new BI_VideoAdapter(apiResult.getResult()).initCardItemModel_SeriesSummary_News_Feed_Top();
                    break;
                case 10:
                    biCardItemModel = new BI_TuWenAdapter(apiResult.getResult()).initCardItemModel_SeriesSummary_News_Feed_Top();
                    break;
                default:
                    return null;
            }
            if (biCardItemModel == null) {
                return null;
            }
            Date parse = DateUtil.parseDateFromWithDateStr(apiResult.getResult().getPublishtime());
            if (parse != null) {
                biCardItemModel.getCarddata().getCardinfo().getTaginfo().add(new Card_CardInfo_TagModel(DateUtil.format(parse, "yyyy-MM-dd"), TagPositionEnums.Right));
            }
            if (biCardItemModel == null || biCardItemModel.getCarddata() == null || biCardItemModel.getExtension() == null) {
                return null;
            }
            return biCardItemModel;
        });
    }

    CompletableFuture<BICardItemModel> getSecondChejiahaoNews(int seriesId) {
        return chejiahaoClient.getSecondCheJiaHaoNews(seriesId).thenApply(apiResult -> {
            if (apiResult == null || apiResult.getReturncode() != 0 || apiResult.getResult() == null) {
                return null;
            }
            BICardItemModel biCardItemModel = null;
            switch (apiResult.getResult().getInfotype()) {
                case 1:
                    biCardItemModel = new BI_YCChangWenAdapter(apiResult.getResult()).initCardItemModel_SeriesSummary_News_Feed_Second();
                    break;
                case 2:
                    biCardItemModel = new BI_YCVideoAdapter(apiResult.getResult()).initCardItemModel_SeriesSummary_News_Feed_Second();
                    break;
            }
            Date parse = DateUtil.parse(apiResult.getResult().getPublishtime(), "yyyy-MM-dd'T'HH:mm:ss");
            if (parse != null) {
                biCardItemModel.getCarddata().getCardinfo().getTaginfo().add(new Card_CardInfo_TagModel(DateUtil.format(parse, "yyyy-MM-dd"), TagPositionEnums.Right));
            }
            if (biCardItemModel == null || biCardItemModel.getCarddata() == null || biCardItemModel.getExtension() == null) {
                return null;
            }
            return biCardItemModel;
        });
    }
}
