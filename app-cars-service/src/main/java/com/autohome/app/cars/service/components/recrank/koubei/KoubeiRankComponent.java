package com.autohome.app.cars.service.components.recrank.koubei;

import com.autohome.app.cars.apiclient.rank.KoubeiRankClient;
import com.autohome.app.cars.apiclient.rank.dtos.KoubeiRankResult;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankUtil;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.SeriesKouBeiComponent;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKouBeiDto;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.enums.RankLevelIdEnum;
import com.autohome.app.cars.service.components.recrank.koubei.dto.KoubeiRankDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/7/12
 */
@Slf4j
@Component
@DBConfig(tableName = "rank_koubei")
public class KoubeiRankComponent extends RankBaseComponent<List<KoubeiRankDto>> {

    @SuppressWarnings("all")
    @Autowired
    KoubeiRankClient koubeiRankClient;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private RankCommonComponent rankCommonComponent;

    @Autowired
    private SeriesKouBeiComponent seriesKouBeiComponent;

    final static String koubeiTypeIdParam = "koubeiTypeId";

    TreeMap<String, Object> makeParam(int koubeiTypeId) {
        return ParamBuilder.create(koubeiTypeIdParam, koubeiTypeId).build();
    }

    public CompletableFuture<List<KoubeiRankDto>> getAsync(int koubeiTypeId) {
        return baseGetAsync(makeParam(koubeiTypeId));
    }

    public List<KoubeiRankDto> get(int koubeiTypeId) {
        return baseGet(makeParam(koubeiTypeId));
    }

    @Override
    public String get(TreeMap<String, Object> params) {
        return super.get(params);
    }

    public void refreshAll(Consumer<String> logInfo) {
        List<Integer> koubeiTypeIdList = List.of(0, 3, 4, 5, 6, 7, 8, 9, 15);
        koubeiTypeIdList.forEach(koubeiTypeId -> {
            try {
                KoubeiRankResult koubeiRankResult = koubeiRankClient.getKoubeiRankList(koubeiTypeId).join();
                if (Objects.nonNull(koubeiRankResult)
                        && !CollectionUtils.isEmpty(koubeiRankResult.getResult())) {
                    List<Integer> seriesIdList = koubeiRankResult.getResult().stream()
                            .map(KoubeiRankResult.KoubeiRankResultDto::getSeriesId).toList();
                    Map<Integer, SeriesDetailDto> seriesDetailMap = seriesDetailComponent.getList(seriesIdList).join()
                            .stream().filter(Objects::nonNull).collect(Collectors.toMap(SeriesDetailDto::getId, e -> e));

                    List<KoubeiRankDto> koubeiRankDtoList = new ArrayList<>();
                    List<KoubeiRankDto> finalKoubeiRankDtoList = koubeiRankDtoList;
                    koubeiRankResult.getResult().forEach(resultDto -> {
                        if (Objects.nonNull(resultDto)
                                && Objects.nonNull(seriesDetailMap.get(resultDto.getSeriesId()))) {
                            SeriesDetailDto seriesDetail = seriesDetailMap.get(resultDto.getSeriesId());
                            KoubeiRankDto koubeiRankDto = new KoubeiRankDto();
                            koubeiRankDto.setSeriesId(resultDto.getSeriesId());
                            koubeiRankDto.setNumber(resultDto.getNumber());
                            koubeiRankDto.setRank(resultDto.getRank());
                            koubeiRankDto.setScore(resultDto.getScore());
                            koubeiRankDto.setLevelId(seriesDetail.getLevelId());
                            koubeiRankDto.setIsNewEnergy(seriesDetail.getEnergytype());
                            koubeiRankDto.setFuelTypes(seriesDetail.getFueltypes());
                            koubeiRankDto.setMinPrice(seriesDetail.getMinPrice());
                            koubeiRankDto.setMaxPrice(seriesDetail.getMaxPrice());
                            koubeiRankDto.setFctTypeId(seriesDetail.getPlace());
                            finalKoubeiRankDtoList.add(koubeiRankDto);
                        }
                    });
                    // 在存储数据时就做好排序，为取数时节省时间
                    koubeiRankDtoList = koubeiRankDtoList.stream()
                            .sorted(Comparator.comparing(KoubeiRankDto::getRank))
                            .toList();
                    update(makeParam(koubeiTypeId), koubeiRankDtoList);
                    logInfo.accept(String.format("数据刷新成功, koubeiTypeId=%s", koubeiTypeId));
                }
            } catch (Exception e) {
                logInfo.accept(String.format("数据刷新失败, koubeiTypeId=%s, e=%s", koubeiTypeId, e));
            }
        });
    }

    public RankResultDto getResultListByCondition(RankParam rankParam) {
        RankResultDto result = new RankResultDto();
        // 原始口碑ID
        // 此段处理逻辑，油耗和续航是同一数据，只是油耗只针对燃油车，续航只针对新能源
        int oraginKoubeiTypeId = rankParam.getKoubeitypeid();
        if (rankParam.getKoubeitypeid() == -6) {
            rankParam.setKoubeitypeid(6);
        }
        if (oraginKoubeiTypeId == -6 && rankParam.getEnergytype() == 1) {
            return result;
        }
        if (oraginKoubeiTypeId == 6 && (Arrays.asList(4, 5, 6).contains(rankParam.getEnergytype()) || rankParam.getEnergytype() == 456)) {
            return result;
        }
        // 当未筛选能源类型, 但筛选续航/油耗时, 添加隐含条件 油耗只返回非新能源车, 续航只返回新能源车
        if (rankParam.getEnergytype() == 0 && Math.abs(oraginKoubeiTypeId) == 6) {
            if (oraginKoubeiTypeId == 6) {
                rankParam.setEnergytype(1);
            } else if (oraginKoubeiTypeId == -6) {
                rankParam.setEnergytype(456);
            }
        }

        // 设置Result公共信息
        result.getResult().setSaleranktip(StringUtils.EMPTY);
        RankLevelIdEnum rankLevelIdEnum = RankLevelIdEnum.getInstance(rankParam.getLevelid());
        if (Objects.nonNull(rankLevelIdEnum)) {
            result.getResult().setScenetitle(rankCommonComponent.getPriceDesc(rankParam)
                    + String.format(rankLevelIdEnum.getRankNameScheme(), "口碑"));
        }
        result.getResult().setMorescheme("autohome://car/recmainrank?from=8&typeid=1");

        try {
            List<KoubeiRankDto> dtoList = getByParams(rankParam.getKoubeitypeid(), rankParam.getLevelid(), rankParam.getFcttypeid(),
                    rankParam.getEnergytype(), rankParam.getMinprice(), rankParam.getMaxprice());
            // 分页处理
            dtoList = rankCommonComponent.pageHandle(result, rankParam, dtoList);
            if (!CollectionUtils.isEmpty(dtoList)) {
                // 获取相关信息
                AtomicReference<Map<Integer, SeriesDetailDto>> seriesDetailMap = new AtomicReference<>(new HashMap<>());
                AtomicReference<Map<Integer, SeriesKouBeiDto>> seriesKouBeiMap = new AtomicReference<>(new HashMap<>());
                // 获取车系id
                List<Integer> seriesIdList = dtoList.stream().map(KoubeiRankDto::getSeriesId).toList();
                seriesDetailComponent.getList(seriesIdList)
                        .thenCombineAsync(seriesKouBeiComponent.getList(seriesIdList), (seriesDetail, seriesKouBei) -> {
                            seriesDetailMap.set(seriesDetail.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toMap(SeriesDetailDto::getId, Function.identity())));
                            seriesKouBeiMap.set(seriesKouBei.stream()
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toMap(SeriesKouBeiDto::getSeriesId, Function.identity())));
                            return null;
                        }).join();

                for (int index = 0; index < dtoList.size(); index++) {
                    KoubeiRankDto item = dtoList.get(index);
                    RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
                    dto.setRcmtext(StringUtils.EMPTY);
                    dto.setRcmdesc(StringUtils.EMPTY);
                    dto.setCardtype(1);
                    dto.setRightpricetitle("查成交价");
                    dto.setSalecount(0L);
                    int seriesId = item.getSeriesId();
                    String seriesIdStr = String.valueOf(item.getSeriesId());
                    SeriesDetailDto seriesDetail = seriesDetailMap.get().get(seriesId);
                    // 设置价格和车系图片
                    dto.setSeriesimage(ImageUtils.convertImageUrl(seriesDetail.getPngLogo(), true,
                            false, false,
                            ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
                    dto.setSeriesname(seriesDetail.getName());
                    dto.setPriceinfo(CommonHelper.priceForamtV2(seriesDetail.getMinPrice(),
                            seriesDetail.getMaxPrice()));
                    dto.setShowenergyicon(seriesDetail.getEnergytype());
                    dto.setSpecname(StringUtils.EMPTY);

                    // 设置排名
                    String rankStr = rankCommonComponent.getRankStr(rankParam, index);
                    dto.setRank(rankStr);
                    dto.setRankchange(0);
                    dto.setRankNum(0);

                    // 关注分数
                    dto.setScorevalue(String.valueOf(item.getScore()));
                    dto.setScoretip("分");
                    dto.setIsshowscorevalue(1);

                    // 设置PV
                    Map<String, String> pvArgs = new HashMap<>();
                    pvArgs.put("subranktypeid", "1");
                    pvArgs.put("rank", rankStr);
                    pvArgs.put("typeid", String.valueOf(rankParam.getTypeid()));
                    pvArgs.put("seriesid", seriesIdStr);
                    dto.setSeriesid(seriesIdStr);
                    dto.setLinkurl(String.format("autohome://car/seriesmain?seriesid=%s&fromtype=107", seriesId));
                    dto.setPricelinkurl(RankUtil.genPriceLinkUrl(rankParam.getPm(), rankParam.getChannel(),
                            item.getSeriesId()));
                    dto.setPvitem(PvItem.getInstance(pvArgs, "car_rec_main_rank_series_click", null,
                            "car_rec_main_rank_series_show", null));
                    // 设置RightInfo
                    String linkUrl = "autohome://reputation/reputationlist?brandid=" + seriesDetail.getBrandId() +
                            "&seriesid=" + seriesIdStr + "&seriesname=" +
                            UrlUtil.encode(seriesDetail.getName()).replace("+", "%20") + "&koubeifromkey=35&categoryid="
                            + rankParam.getKoubeitypeid();
                    dto.setRightinfo(genKouBeiRightInfo(pvArgs, dto.getScorevalue() + dto.getScoretip(), linkUrl));
                    result.getResult().getList().add(dto);
                }
            }
        } catch (Exception e) {
            log.error("查询口碑榜出错:{}", rankParam, e);
        }

        // 返回结果统一业务逻辑处理
        rankCommonComponent.resultCommonDeal(result, rankParam);
        return result;
    }

    public List<KoubeiRankDto> getByParams(int koubeiTypeId,
                                           String levelIds, // 0全部
                                           String fctTypeId, // 0/自主/合资/进口
                                           int energyType, // 0不限,1燃油车,4纯电,5插电,6增程,456新能源
                                           int minPrice,
                                           int maxPrice) {
        List<KoubeiRankDto> koubeiTypeDtoList = get(koubeiTypeId);
        if (CollectionUtils.isEmpty(koubeiTypeDtoList)) {
            return Collections.emptyList();
        }

        boolean filterByLevel = !List.of("0", "").contains((levelIds)); // 为0表示全部，是否按照级别进行过滤
        boolean filterEnergyType = energyType != 0; // 为0表示不限
        boolean filterFctTypeId = !List.of("0", "").contains(fctTypeId); // 为0表示不限
        List<String> energyTypeList = Arrays.asList(String.valueOf(energyType).split(""));

        return koubeiTypeDtoList.stream()
                .filter(e -> {
                    if (filterByLevel) {
                        return Arrays.asList(levelIds.split(",")).contains(String.valueOf(e.getLevelId()));
                    } else {
                        return true;
                    }
                })
                .filter(e -> {
                    if (filterFctTypeId) {
                        return e.getFctTypeId().equals(fctTypeId);
                    } else {
                        return true;
                    }
                })
                .filter(e -> {
                    if (filterEnergyType) {
                        if (energyType == 1) {
                            return e.getIsNewEnergy() == 0;
                        } else {
                            return CollectionUtils.containsAny(energyTypeList,
                                    Arrays.asList(e.getFuelTypes().split(",")));
                        }
                    } else {
                        return true;
                    }
                })
                .filter(e -> e.getMinPrice() <= maxPrice && e.getMaxPrice() >= minPrice)
                .toList();
    }

    private RankResultDto.RightinfoDTO genKouBeiRightInfo(Map<String, String> argsMap,
                                                          String kouBeiSource,
                                                          String rightPriceUrl) {
        RankResultDto.RightinfoDTO rightInfo = new RankResultDto.RightinfoDTO();
        rightInfo.setPvitem(PvItem.getInstance(argsMap, "car_rec_main_rank_history_click", null,
                StringUtils.EMPTY, null));
        rightInfo.setRighttextone(kouBeiSource);
        rightInfo.setRighttexttwo("查看口碑");
        rightInfo.setPriceinfo(new RankResultDto.RightinfoDTO.PriceInfoDto());
        rightInfo.setExt(StringUtils.EMPTY);
        rightInfo.setRightpriceeid(StringUtils.EMPTY);
        rightInfo.setRightpricetitle(StringUtils.EMPTY);
        rightInfo.setRightpriceurl(StringUtils.EMPTY);
        rightInfo.setRighttexttwolinkurl(rightPriceUrl);
        return rightInfo;
    }


}
