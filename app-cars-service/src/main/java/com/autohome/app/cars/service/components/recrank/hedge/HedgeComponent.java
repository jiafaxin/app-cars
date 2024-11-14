package com.autohome.app.cars.service.components.recrank.hedge;

import com.autohome.app.cars.apiclient.rank.LeaderboardClient;
import com.autohome.app.cars.apiclient.rank.dtos.HedgeRankResult;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.HedgeRankDto;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HedgeComponent extends RankBaseComponent<HedgeRankDto> {

    @Autowired
    private LeaderboardClient leaderboardClient;

    @Resource
    private RankCommonComponent rankCommonComponent;

    private static String paramName = "newenergy";
    private final int pageIndex = 1;//页码
    private final int pageSize = 2000;//每页条数

    TreeMap<String, Object> makeParam(int newenergy) {
        return ParamBuilder.create(paramName, newenergy).build();
    }

    public List<HedgeRankDto.Item> getDtoList(int newenergy) {
        try {
            HedgeRankDto info = baseGetAsync(makeParam(newenergy)).get();
            if (info != null && info.getList() != null && info.getList().size() > 0) {
                return HedgeRankDto.toDtos(info.getList());
            }
        } catch (Exception ex) {
            log.error("get异常-ex:{}", ex);
        }
        return null;
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        refreshRank(0).exceptionally(e -> {
            xxlLog.accept("油车保值榜数据失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }).join();

        refreshRank(1).exceptionally(e -> {
            xxlLog.accept("新能源保值榜数据失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }).join();
    }

    @Override
    protected HedgeRankDto sourceData(TreeMap<String, Object> params) {
        return refreshRank((int) params.get(paramName)).join();
    }

    protected CompletableFuture<HedgeRankDto> refreshRank(int newenergy) {
        return leaderboardClient.getHedgeRankList(pageIndex, pageSize, newenergy == 1 ? "201908" : "")
                .thenApplyAsync(data -> {
                    if (data == null || data.getReturncode() != 0) {
                        return null;
                    }
                    if (data.getResult() != null && (data.getResult().getList() == null || data.getResult().getList().size() == 0)) {
                        return null;
                    }
                    if (data.getResult() != null && data.getResult().getList() != null && data.getResult().getList().size() > 0) {
                        HedgeRankDto dto = new HedgeRankDto();
                        List<HedgeRankDto.Item> resultList = new ArrayList<>();
                        List<Integer> seriesIdList = data.getResult().getList().stream().map(x -> x.getSeriesid()).collect(Collectors.toList());
                        // 获取车系信息Map
                        Map<String, SeriesDetailDto> seriesDetailMap = new HashMap<>();
                        Lists.partition(seriesIdList, 100).forEach(sublist -> {
                            seriesDetailMap.putAll(rankCommonComponent.getSeriesDetailMap(sublist));
                        });
                        for (HedgeRankResult.ListDTO item : data.getResult().getList()) {
                            HedgeRankDto.Item info = new HedgeRankDto.Item();
                            info.setSeriesid(item.getSeriesid());
                            SeriesDetailDto seriesDetailDto = seriesDetailMap.get(item.getSeriesid() + "");
                            if (Objects.nonNull(seriesDetailDto)) {
                                info.setLevelid(seriesDetailDto.getLevelId());
                                info.setPlace(seriesDetailDto.getPlace());
                                info.setFueltypes(seriesDetailDto.getFueltypes());
                                info.setIsnewenergy(seriesDetailDto.getEnergytype());
                                info.setMinprice(seriesDetailDto.getMinPrice());
                                info.setMaxprice(seriesDetailDto.getMaxPrice());
                            }
                            if (!"暂无".equals(item.getScorevalue())) {
                                info.setPraisescore(Float.valueOf(item.getScorevalue()));
                            }
                            info.setRatevalue(Float.valueOf(StringUtils.substringBefore(item.getRighttextone(), "%")));
                            resultList.add(info);
                        }
                        dto.setList(HedgeRankDto.toArray(resultList));
                        update(makeParam(newenergy), dto);
                        return dto;
                    }
                    return null;
                });

    }

    public RankResultDto getResultListByCondition(RankParam param) {
        RankResultDto result = new RankResultDto();
        try {
            int pageCount = 0;//总页数
            //按城市获取
            int isnewenergy = param.getEnergytype() > 1 ? 1 : 0;
            List<HedgeRankDto.Item> itemList = getDtoList(isnewenergy);
            if (CollectionUtils.isNotEmpty(itemList)) {
                //价格
                if (param.getMaxprice() > 0 && param.getMaxprice() >= param.getMinprice()) {
                    itemList = itemList.stream().filter(p -> p.getMinprice() <= param.getMaxprice() && p.getMaxprice() >= param.getMinprice()).collect(Collectors.toList());
                }
                //车系级别筛选
                if (!Strings.isNullOrEmpty(param.getLevelid()) && !param.getLevelid().equals("0")) {
                    List<String> levelList = Arrays.asList(param.getLevelid().split(","));
                    itemList = itemList.stream().filter(p -> levelList.contains(String.valueOf(p.getLevelid()))).collect(Collectors.toList());
                }
                //厂商属性
                if (!Strings.isNullOrEmpty(param.getFcttypeid()) && !param.getFcttypeid().equals("0")) {
                    itemList = itemList.stream().filter(p -> p.getPlace().equals(param.getFcttypeid())).collect(Collectors.toList());
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
                        HedgeRankDto.Item item = itemList.get(i);
                        item.setRanknum(i+1);
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
                    result.getResult().setScenetitle("");

                    List<RankResultDto.ListDTO> list = new ArrayList<>();
                    List<Integer> seriesIdList = itemList.stream().map(e -> e.getSeriesid()).collect(Collectors.toList());
                    // 获取车系信息Map
                    Map<String, SeriesDetailDto> seriesDetailMap = new HashMap<>();
                    Lists.partition(seriesIdList, 100).forEach(sublist -> {
                        //TODO
                        seriesDetailMap.putAll(rankCommonComponent.getSeriesDetailMap(sublist));
                    });
//                    String  eid = param.getPm() == 1 ? "3|1411002|572|25528|205415|304431" : "3|1412002|572|25528|205415|304430";
//                    String rnSchemePrifix = "autohome://rninsidebrowser?animationtype=1&bgtransparent=1&conttransparent=1&coverlaycolor=00000000&contmargintop=0.1&screenOrientation=0&url=%s";
//                    String askScherme = "rn://MallService/AskPrice?panValid=0&pvareaid=6849804&seriesid=%s&eid=%s";
                    for (HedgeRankDto.Item dataItem : itemList) {
                        SeriesDetailDto fullInfo = seriesDetailMap.get(String.valueOf(dataItem.getSeriesid()));
                        RankResultDto.ListDTO item = new RankResultDto.ListDTO();
                        item.setCardtype(1);
                        item.setIsshowscorevalue(1);
                        item.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%d&fromtype=107", dataItem.getSeriesid()));
                        item.setPriceinfo(CommonHelper.priceForamt(dataItem.getMinprice(), dataItem.getMaxprice()));
//                        String urlFormat = String.format(askScherme, e.getSeriesid(), UrlUtil.encode(eid));
//                        item.setPricelinkurl(String.format(rnSchemePrifix, UrlUtil.encode(urlFormat)));
                        item.setSeriesid(dataItem.getSeriesid() + "");
                        item.setSeriesname(fullInfo.getName());

                        item.setRank(StringUtils.leftPad(dataItem.getRanknum() + "", 2, "0"));
                        item.setSeriesimage(ImageUtils.convertImageUrl(CommonHelper.ChangeLogoSize(12, fullInfo.getPngLogo()), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                        item.setScorevalue(CommonHelper.getRandInfo(String.valueOf(dataItem.getPraisescore()), "0"));
                        item.setScoretip("分");


                        item.setShowenergyicon(dataItem.getIsnewenergy());//是否展示新能源按钮  该值原项目是最外层处理
                        //region 格式化 rightinfo
                        RankResultDto.RightinfoDTO rightinfoDTO = new RankResultDto.RightinfoDTO();

                        rightinfoDTO.setRighttextone(String.format("%.2f", dataItem.getRatevalue()) + "%");
                        rightinfoDTO.setRighttexttwo("三年保值率");

                        rightinfoDTO.setRighttexttwolinkurl(UrlUtil.getInsideBrowerSchemeWK(String.format("https://m.che168.com/keepvalue/result_%s.html?sourcename=mainapp&pvareaid=110674", item.getSeriesid())));

                        PvItem r_pvitem = new PvItem();
                        Map<String, String> r_argvs = new HashMap<>();
                        r_argvs.put("seriesid", dataItem.getSeriesid() + "");
                        r_argvs.put("typeid", "6");
                        r_argvs.put("rank", item.getRank());
                        r_argvs.put("subranktypeid", "1");
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
                        argvs.put("seriesid", dataItem.getSeriesid() + "");
                        argvs.put("typeid", "6");
                        argvs.put("rank", item.getRank());
                        argvs.put("subranktypeid", "1");
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
}
