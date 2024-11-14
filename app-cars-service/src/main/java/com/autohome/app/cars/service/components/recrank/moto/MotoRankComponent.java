package com.autohome.app.cars.service.components.recrank.moto;

import com.autohome.app.cars.apiclient.rank.MotoBikeClient;
import com.autohome.app.cars.apiclient.rank.dtos.MotoRankResult;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.common.utils.StringUtils;
import com.autohome.app.cars.service.components.recrank.RankBaseComponent;
import com.autohome.app.cars.service.components.recrank.common.RankCommonComponent;
import com.autohome.app.cars.service.components.recrank.dtos.RankParam;
import com.autohome.app.cars.service.components.recrank.dtos.RankResultDto;
import com.autohome.app.cars.service.components.recrank.moto.dto.MotoRankDto;
import com.autohome.app.cars.service.services.dtos.PvItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/7/18
 */
@Slf4j
@Component
//@DBConfig(tableName = "rank_moto")
public class MotoRankComponent extends RankBaseComponent<List<MotoRankDto>> {


    @SuppressWarnings("all")
    @Autowired
    MotoBikeClient motoBikeClient;

    @Autowired
    private RankCommonComponent rankCommonComponent;
    final static String levelIdParam = "levelId";

    TreeMap<String, Object> makeParam(int levelId) {
        return ParamBuilder.create(levelIdParam, levelId).build();
    }

    public CompletableFuture<List<MotoRankDto>> getAsync(int levelId) {
        return baseGetAsync(makeParam(levelId));
    }

    public List<MotoRankDto> get(int levelId) {
        return baseGet(makeParam(levelId));
    }

    public List<List<MotoRankDto>> getList(List<Integer> levelIdList) {
        return baseGetList(levelIdList.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    public void refreshAll(Consumer<String> logInfo) {
        List<Integer> levelIdList = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
        levelIdList.forEach(levelId -> {
            List<MotoRankResult.ResultBean.ListBean> resultList = new ArrayList<>();
            int pageIndex = 1;
            int pageSize = 100;

            try {
                MotoRankResult motoRankResult = motoBikeClient.getMotoRankList(levelId, pageIndex, pageSize).join();
                while (hasResult(motoRankResult)) {
                    resultList.addAll(new ArrayList<>(motoRankResult.getResult().getList()));
                    motoRankResult = motoBikeClient.getMotoRankList(levelId, pageIndex, pageSize).join();
                }
                saveMotoRankResult(levelId, resultList);
                logInfo.accept(String.format("摩托车接口更新数据成功，levelId=%s", levelId));
            } catch (Exception e) {
                logInfo.accept(String.format("摩托车接口获取数据失败，levelId=%s", levelId));
            }
        });
    }

    private boolean hasResult(MotoRankResult motoRankResult) {
        if (Objects.isNull(motoRankResult)
                || motoRankResult.getReturncode() != 0
                || Objects.isNull(motoRankResult.getResult())) {
            throw new RuntimeException("查询结果异常");
        }
        return !CollectionUtils.isEmpty(motoRankResult.getResult().getList());
    }

    private void saveMotoRankResult(int levelId,
                                    List<MotoRankResult.ResultBean.ListBean> resultList) {
        List<MotoRankDto> motoRankDtoList = resultList.stream().map(e -> {
            MotoRankDto motoRankDto = new MotoRankDto();
            motoRankDto.setSeriesId(e.getSeriesId());
            motoRankDto.setSeriesName(e.getSeriesName());
            motoRankDto.setMinPrice(e.getMinPrice());
            motoRankDto.setMaxPrice(e.getMaxPrice());
            motoRankDto.setSeriesLogo(e.getSeriesLogo());
            motoRankDto.setUv(e.getUv());
            motoRankDto.setPv(e.getPv());
            motoRankDto.setLevelId(e.getLevelId());
            motoRankDto.setLevelName(e.getLevelName());
            return motoRankDto;
        }).toList();
        update(makeParam(levelId), motoRankDtoList);
    }

    private RankResultDto getMotoRankList(RankParam recRankParam) {
        RankResultDto result = new RankResultDto();
        String price = recRankParam.getPrice();
        String level = recRankParam.getLevelid();
        int pageSize = recRankParam.getPagesize();
        int pageIndex = recRankParam.getPageindex();
        int rolling = recRankParam.getRolling();
        if (StringUtils.isNotEmpty(price)) {//价格转换源接口接收形式0_1
            price = price.replace("-", "_");
        }
        pageSize = pageSize > 200 ? 200 : pageSize;
        List<MotoRankDto> dtoList = getByParams(level, recRankParam.getMinprice(), recRankParam.getMaxprice());
        result.getResult().setScenetitle("摩托车榜");
        result.getResult().setSaleranktip("*日均热度是车系在全国的浏览数据，每日更新。");
        rankCommonComponent.pageHandle(result, recRankParam, dtoList);
        if (!CollectionUtils.isEmpty(dtoList)) {
            AtomicInteger rank = new AtomicInteger(pageSize * (pageIndex - 1) + 1);
            int total = dtoList.size();
            total = total > 200 ? 200 : total;//最多返回200个
            result.getResult().setPagecount((total - 1) / pageSize + 1);
            result.getResult().setPagesize(pageSize);
            for (int index = 0; index < dtoList.size(); index++) {
                MotoRankDto motoRank = dtoList.get(index);
                RankResultDto.ListDTO dto = new RankResultDto.ListDTO();
                dto.setCardtype(1);
                dto.setSeriesid(motoRank.getSeriesId() + "");
                String scheme = "autohome://car/mtseriesmain?seriesid=" + motoRank.getSeriesId() + "&fromtype=1";
                dto.setLinkurl(scheme);
                String seriesPic = "http:" + motoRank.getSeriesLogo();
                seriesPic = ImageUtils.convertImage_Size(seriesPic, ImageSizeEnum.ImgSize_4x3_400x300);
                dto.setSeriesimage(seriesPic);
                dto.setSeriesname(motoRank.getSeriesName());
                dto.setPriceinfo(CommonHelper.priceForamtV2(motoRank.getMinPrice(), motoRank.getMaxPrice()));
                RankResultDto.RightinfoDTO rightinfoDTO = new RankResultDto.RightinfoDTO();
                rightinfoDTO.setRighttextone(motoRank.getPv() + "");
                rightinfoDTO.setRighttexttwo("热度值");
                PvItem r_pvitem = new PvItem();
                Map<String, String> r_argvs = new HashMap<>();
                r_argvs.put("seriesid", motoRank.getSeriesId() + "");
                r_argvs.put("typeid", recRankParam.getTypeid() + "");
                r_argvs.put("rank", rank.intValue() + "");
                PvItem.PvObj r_click = new PvItem.PvObj();
                r_click.setEventid("car_rec_main_rank_history_click");
                r_pvitem.setArgvs(r_argvs);
                r_pvitem.setClick(r_click);

                rightinfoDTO.setPvitem(r_pvitem);
                dto.setRightinfo(rightinfoDTO);

                // 设置排名
                String rankStr = rankCommonComponent.getRankStr(recRankParam, index);
                dto.setRank(rankStr);

                PvItem pvitem = new PvItem();
                Map<String, String> argvs = new HashMap<>();
                argvs.put("seriesid", motoRank.getSeriesId() + "");
                argvs.put("typeid", String.valueOf(recRankParam.getTypeid()));
                argvs.put("rank", dto.getRank());
                PvItem.PvObj click = new PvItem.PvObj();
                click.setEventid("car_rec_main_rank_series_click");
                PvItem.PvObj show = new PvItem.PvObj();
                show.setEventid("car_rec_main_rank_series_show");

                pvitem.setArgvs(argvs);
                pvitem.setClick(click);
                pvitem.setShow(show);

                dto.setPvitem(pvitem);
                result.getResult().getList().add(dto);
                rank.getAndIncrement();
            }
        } else {
            if (result != null) {
                result.setCacheable(0);
            }
        }
        return result;
    }

    public List<MotoRankDto> getByParams(String levelIds,
                                         int minPrice,
                                         int maxPrice) {

        List<MotoRankDto> motoRankDtoList =
                getList(Arrays.stream(levelIds.split(",")).map(Integer::valueOf).toList())
                        .stream()
                        .flatMap(Collection::stream)
                        .toList();
        if (CollectionUtils.isEmpty(motoRankDtoList)) {
            return Collections.emptyList();
        }

        return motoRankDtoList.stream()
                .filter(e -> e.getMinPrice() <= maxPrice && e.getMaxPrice() >= minPrice)
                .toList();
    }

}
