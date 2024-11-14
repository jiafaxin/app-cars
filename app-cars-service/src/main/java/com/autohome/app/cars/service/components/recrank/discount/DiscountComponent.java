package com.autohome.app.cars.service.components.recrank.discount;

import com.autohome.app.cars.apiclient.rank.LeaderboardClient;
import com.autohome.app.cars.apiclient.rank.dtos.DiscountRankResult;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.mapper.appcars.RankDiscountMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankDiscountEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.DiscountRankDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.autohome.app.cars.service.services.dtos.PvItem;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by dx on 2024/6/4
 */
@Slf4j
@Component
//@DBConfig(tableName = "rank_discount")
public class DiscountComponent extends RankBaseComponent<DiscountRankDto> {
    private static String paramName = "cityId";

    @Autowired
    private LeaderboardClient leaderboardClient;

    @Resource
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private RankDiscountMapper rankDiscountMapper;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    private final int pageIndex = 1;//页码
    private final int pageSize = 3000;//每页条数

    TreeMap<String, Object> makeParam(int cityId) {
        return ParamBuilder.create(paramName, cityId).build();
    }

    public CompletableFuture<DiscountRankDto> get(int cityId) {
        return baseGetAsync(makeParam(cityId));
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    public List<DiscountRankDto.Item> getDtoList(int cityId) {
        try {
            DiscountRankDto info = baseGetAsync(makeParam(cityId)).get();
            if (info != null && info.getList() != null && info.getList().size() > 0) {
                return DiscountRankDto.toDtos(info.getList());
            }
        } catch (Exception ex) {
            log.error("get异常-ex:{}", ex);
        }
        return null;
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopCity(totalMinutes, cityId -> leaderboardClient.getDiscountRankList(pageIndex, pageSize, cityId)
                .thenAccept(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return;
                    }
                    if (data.getResult() != null && (data.getResult().getList() == null || data.getResult().getList().size() == 0)) {
                        delete(makeParam(cityId));
                        return;
                    }
                    if (data.getResult() != null && data.getResult().getList() != null && data.getResult().getList().size() > 0) {
                        DiscountRankDto dto = new DiscountRankDto();
                        List<DiscountRankDto.Item> resultList = new ArrayList<>();
                        dto.setCityid(cityId);
                        List<Integer> seriesIdList = data.getResult().getList().stream().map(x -> x.getSeries_id()).collect(Collectors.toList());
                        // 获取车系信息Map
                        //Map<String, SeriesDetailDto> seriesDetailMap = rankCommonComponent.getSeriesDetailMap(seriesIdList);
                        Map<String, SeriesDetailDto> seriesDetailMap = new HashMap<>();
                        Lists.partition(seriesIdList, 100).forEach(sublist -> {
                            seriesDetailMap.putAll(rankCommonComponent.getSeriesDetailMap(sublist));
                        });
                        for (DiscountRankResult.Item item : data.getResult().getList()) {
                            DiscountRankDto.Item info = new DiscountRankDto.Item();
                            info.setSeriesid(item.getSeries_id());
                            info.setPraisescore(item.getPraise_score());
                            info.setSpecid(item.getSpec_id());
                            info.setSpecdealerprice(item.getSpec_price());
                            info.setMinprice(item.getMin_price());
                            info.setMaxprice(item.getMax_price());
                            info.setSpecminprice(item.getSpec_min_price());
                            info.setSpecmaxprice(item.getSpec_max_price());
                            if (seriesDetailMap.containsKey(String.valueOf(item.getSeries_id()))) {
                                SeriesDetailDto seriesDetail = seriesDetailMap.get(String.valueOf(item.getSeries_id()));
                                info.setLevelid(seriesDetail.getLevelId());
                                info.setPlace(seriesDetail.getPlace());
                                info.setIsnewenergy(seriesDetail.getEnergytype());
                                info.setFueltypes(seriesDetail.getFueltypes());
                                info.setBrandid(seriesDetail.getBrandId());
                            }
                            resultList.add(info);
                        }
                        dto.setList(DiscountRankDto.toArray(resultList));
                        update(makeParam(cityId), dto);
                        //xxlLog.accept("getDiscountRankList执行-cityId:" + cityId + ",data:" + JsonUtil.toString(data));
                    }
                }).exceptionally(e -> {
                    xxlLog.accept(cityId + "降价榜数据失败:" + ExceptionUtil.getStackTrace(e));
                    return null;
                }).join(), xxlLog);
    }

//    @Override
//    protected DiscountRankDto sourceData(TreeMap<String, Object> params) {
//        try {
//            Integer cityId = (Integer) params.get(paramName);
//            RankDiscountEntity rankDiscountEntity = rankDiscountMapper.getInfoByCityId(cityId);
//            if (rankDiscountEntity != null && !Strings.isNullOrEmpty(rankDiscountEntity.getData())) {
//                DiscountRankDto dto = JsonUtil.toObject(rankDiscountEntity.getData(), DiscountRankDto.class);
//                return dto;
//            }
//        } catch (Exception ex) {
//            log.error("sourceData异常-ex:{}", ex);
//        }
//        return null;
//    }

    /**
     * 降价榜
     *
     * @param param
     * @return
     */
    public RankResultDto getResultListByCondition(RankParam param) {
        RankResultDto result = new RankResultDto();
        try {
            int pageCount = 0;//总页数
            //按城市获取
            List<DiscountRankDto.Item> itemList = getDtoList(param.getCityid());
            if (CollectionUtils.isNotEmpty(itemList)) {
                //车系级别筛选
                if (!org.elasticsearch.common.Strings.isNullOrEmpty(param.getLevelid()) && !param.getLevelid().equals("0")) {
                    List<String> levelList = Arrays.asList(param.getLevelid().split(","));
                    itemList = itemList.stream().filter(p -> levelList.contains(String.valueOf(p.getLevelid()))).collect(Collectors.toList());
                }
                //厂商属性
                if (!org.elasticsearch.common.Strings.isNullOrEmpty(param.getFcttypeid()) && !param.getFcttypeid().equals("0")) {
                    itemList = itemList.stream().filter(p -> p.getPlace().equals(param.getFcttypeid())).collect(Collectors.toList());
                }
                //品牌
                if (!org.elasticsearch.common.Strings.isNullOrEmpty(param.getBrandid())) {
                    itemList = itemList.stream().filter(p -> String.valueOf(p.getBrandid()).equals(param.getBrandid())).collect(Collectors.toList());
                }
                //能源类型
                if (param.getEnergytype() > 0) {
                    if (param.getEnergytype() == 1) {//燃油
                        itemList = itemList.stream().filter(p -> p.getIsnewenergy() == 0).collect(Collectors.toList());
                    } else if (param.getEnergytype() == 456) {//新能源 4纯电, 5插电, 6增程
                        itemList = itemList.stream().filter(p -> p.getIsnewenergy() == 1).collect(Collectors.toList());
                    } else {//具体新能源类型 4纯电, 5插电, 6增程  线上有错误数据 fueltypes=“2,4,7”但是isnewenergy=0 车系id:2608
                        itemList = itemList.stream().filter(p -> p.getIsnewenergy() == 1 && Arrays.asList(p.getFueltypes().split(",")).contains(String.valueOf(param.getEnergytype()))).collect(Collectors.toList());
                    }
                }
                //处理查询结果
                if (CollectionUtils.isNotEmpty(itemList)) {
                    //重新设置排名编号
                    for (int i = 0; i < itemList.size(); i++) {
                        DiscountRankDto.Item item = itemList.get(i);
                        item.setRanknum(i + 1);
                    }
                    //分页处理
                    pageCount = (itemList.size() - 1) / param.getPagesize() + 1;
                    itemList = itemList.stream().skip((param.getPageindex() - 1) * param.getPagesize()).limit(param.getPagesize()).collect(Collectors.toList());
                    if (itemList.size() == 0) {
                        return result;
                    }

                    //分享按钮信息
                    RankResultDto.ResultDTO.ShareinfoDTO shareInfo = new RankResultDto.ResultDTO.ShareinfoDTO();
                    shareInfo.setLogo("");
                    shareInfo.setSubtitle("");
                    shareInfo.setTitle("");
                    shareInfo.setUrl("https://athm.cn/x/9Cvfb5c");//11.36.5版本后默认分享地址都是https://athm.cn/x/9Cvfb5c
                    result.getResult().setShareinfo(shareInfo);

                    result.getResult().setScenesubtitle("");
                    result.getResult().setPagecount(pageCount);
                    result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");
                    result.getResult().setPagesize(param.getPagesize());
                    result.getResult().setPageindex(param.getPageindex());
                    result.getResult().setSaleranktip("");
                    //scenetitle 枚举目前缺少车型数据
                    RankLevelIdEnum rankLevelIdEnum = RankLevelIdEnum.getInstance(param.getLevelid());
                    if (Objects.nonNull(rankLevelIdEnum)) {
                        String sceneTitle=rankLevelIdEnum.getRankNameScheme();
                        if(RankLevelIdEnum.ALL_CAR_EMPTY.equals(rankLevelIdEnum))
                            sceneTitle="全"+sceneTitle;
                        result.getResult().setScenetitle(String.format(sceneTitle, "降价"));
                    }

                    List<RankResultDto.ListDTO> list = new ArrayList<>();
                    List<Integer> seriesIdList = itemList.stream().map(e -> e.getSeriesid()).collect(Collectors.toList());
                    // 获取车系信息Map
                    Map<String, SeriesDetailDto> seriesDetailMap = new HashMap<>();
                    Lists.partition(seriesIdList, 100).forEach(sublist -> {
                        seriesDetailMap.putAll(rankCommonComponent.getSeriesDetailMap(sublist));
                    });
                    for (DiscountRankDto.Item e : itemList) {
                        SeriesDetailDto fullInfo = seriesDetailMap.get(String.valueOf(e.getSeriesid()));
                        SpecDetailDto specDetailDto = specDetailComponent.get(e.getSpecid()).join();
                        RankResultDto.ListDTO item = new RankResultDto.ListDTO();
                        item.setCardtype(3);
                        item.setPriceinfo(CommonHelper.priceForamt(e.getMinprice(), e.getMaxprice()));
                        item.setSeriesid(e.getSeriesid() + "");
                        item.setSeriesname(fullInfo.getName());
                        item.setRank((e.getRanknum() < 10 ? "0" + String.valueOf(e.getRanknum()) : String.valueOf(e.getRanknum())));
                        item.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%d&fromtype=107", e.getSeriesid()));
                        item.setSeriesimage(ImageUtils.convertImageUrl(CommonHelper.ChangeLogoSize(12, fullInfo.getPngLogo()), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                        item.setScorevalue(CommonHelper.getRandInfo(String.valueOf(e.getPraisescore()), "0"));
                        item.setRcmtext("最大降幅车型：");
                        item.setRcmlinkurl("autohome://car/specmain?specid=" + e.getSpecid());
                        item.setRcmdesc(specDetailDto != null ? specDetailDto.getSpecName() : "");
                        item.setScoretip("分");
                        //https://zhishi.autohome.com.cn/home/teamplace/file?targetId=qAHyRlZ0ng 新用户榜单增加论坛入口 23-11-20 张璐  已经找产品确认改功能已下线
                        //addClubFollowerInfo(item, fullInfo);

                        item.setIsshowscorevalue(1);
                        item.setShowenergyicon(e.getIsnewenergy());//是否展示新能源按钮  该值原项目是最外层处理
                        //region 格式化 rightinfo
                        RankResultDto.RightinfoDTO rightinfoDTO = new RankResultDto.RightinfoDTO();

                        rightinfoDTO.setRighttextone(getSeriesPrice(e.getSpecmaxprice() - e.getSpecdealerprice()));
                        rightinfoDTO.setRighttexttwo("最大降幅" + CommonHelper.getPercentInfo(String.valueOf(e.getSpecdealerprice()), String.valueOf(e.getSpecmaxprice())));
                        rightinfoDTO.setRighttexttwolinkurl("");

                        PvItem r_pvitem = new PvItem();
                        Map<String, String> r_argvs = new HashMap<>();
                        r_argvs.put("seriesid", e.getSeriesid() + "");
                        r_argvs.put("typeid", "3");
                        r_argvs.put("rank", item.getRank());
                        PvItem.PvObj r_click = new PvItem.PvObj();
                        r_click.setEventid("car_rec_main_rank_history_click");
                        r_pvitem.setArgvs(r_argvs);
                        r_pvitem.setClick(r_click);

                        rightinfoDTO.setPvitem(r_pvitem);
                        item.setRightinfo(rightinfoDTO);
                        //endregion
                        //region 格式化 pvitem
                        PvItem pvitem = new PvItem();
                        Map<String, String> argvs = new HashMap<>();
                        argvs.put("seriesid", e.getSeriesid() + "");
                        argvs.put("typeid", "3");
                        argvs.put("rank", item.getRank());
                        PvItem.PvObj click = new PvItem.PvObj();
                        click.setEventid("car_rec_main_rank_series_click");
                        PvItem.PvObj show = new PvItem.PvObj();
                        show.setEventid("car_rec_main_rank_series_show");

                        pvitem.setArgvs(argvs);
                        pvitem.setClick(click);
                        pvitem.setShow(show);

                        item.setPvitem(pvitem);
                        //endregion
                        list.add(item);
                    }
                    result.getResult().setList(list);
                    //公共结果处理
                    rankCommonComponent.resultCommonDeal(result, param);
                }
            }
        } catch (Exception ex) {
            log.error("getDiscountRank异常-param:{},ex:{}", JsonUtil.toString(param), ex);
        }
        return result;
    }

    private String getSeriesPrice(double price) {
        if (price != 0) {
            return BigDecimal.valueOf(price / 10000).setScale(2, RoundingMode.HALF_UP) + "万";
        }
        return "0";
    }
}
