package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.car.CarApiClient;
import com.autohome.app.cars.apiclient.car.dtos.WeekRankHistoryDto;
import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.CmsNewCarResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.SeriesTimeAxisEnum;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.common.utils.UrlUtil;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SeriesTimeAxisMapper;
import com.autohome.app.cars.mapper.popauto.entities.CarTimeAboutPicEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.SeriesTimeAxisDto;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 新车日历，各节点的发布时间：图片首发、配置首发、正式上市、车图更新、配置更新、新车销量
 * @date : 2024/4/26 10:31
 */
@Component
@DBConfig(tableName = "series_time_axis")
public class SeriesTimeAxisComponent extends BaseComponent<SeriesTimeAxisDto> {
    final static String seriesIdParamName = "seriesId";

    @Autowired
    SeriesTimeAxisMapper seriesTimeAxisMapper;
    @Autowired
    SeriesMapper seriesMapper;
    @Autowired
    CarApiClient carApiClient;

    @Autowired
    CmsApiClient cmsApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<SeriesTimeAxisDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        List<SpecEntity> specAll = seriesTimeAxisMapper.getSpecAll();
        Map<Integer, List<SpecEntity>> specMap = specAll.stream().collect(Collectors.groupingBy(SpecEntity::getSeriesId));
        allSeries.forEach(series -> {
            if (series.getState() != 40 && series.getState() != 0) {
                SeriesTimeAxisDto dto = new SeriesTimeAxisDto();
                List<SpecEntity> specEntityList = new ArrayList<>();
                if (specMap.containsKey(series.getId())) {
                    specEntityList = specMap.get(series.getId());
                }
                //最早发布的车型
                SpecEntity specEntity1 = specEntityList.stream().filter(x -> x.getTimeMarket() != null)
                        .sorted(Comparator.comparing(specEntity -> DateUtil.parse(specEntity.getTimeMarket(), "yyyy-MM-dd HH:mm:ss")))
                        .findFirst().orElse(null);

                if (specEntity1 != null) {
                    SeriesTimeAxisDto.Item item = new SeriesTimeAxisDto.Item();
                    item.setTypecode(SeriesTimeAxisEnum.SPEC_PUBLISH_TIME.getValue());
                    item.setDate(specEntity1.getTimeMarket());
                    dto.getItemList().add(item);
                }

                //图片首发时间
                CarTimeAboutPicEntity picFirstAddTime = seriesTimeAxisMapper.getPicFirstAddTime(series.getId());
                if (picFirstAddTime != null) {
                    SeriesTimeAxisDto.Item item = new SeriesTimeAxisDto.Item();
                    item.setTypecode(SeriesTimeAxisEnum.PIC_FIRST_PUBLISH_TIME.getValue());
                    item.setDate(DateUtil.format(picFirstAddTime.getDtime(), "yyyy-MM-dd"));
                    item.setUrl("autohome://car/seriespicture?seriesid=" + series.getId() + "&orgin=0&seriesname=" + UrlUtil.encode(series.getName()) + "&categoryid=" + picFirstAddTime.getPicClass());
                    dto.getItemList().add(item);

                    //首发当天更新不算更新
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(picFirstAddTime.getDtime());
                    calendar.add(Calendar.DATE, 1);
                    CarTimeAboutPicEntity picUpdateTime = seriesTimeAxisMapper.getPicUpdateTime(series.getId(), DateUtil.format(calendar.getTime(), "yyyy-MM-dd"));
                    if (picUpdateTime != null) {
                        item = new SeriesTimeAxisDto.Item();
                        item.setTypecode(SeriesTimeAxisEnum.PIC_UPDATE_TIME.getValue());
                        item.setDate(DateUtil.format(picUpdateTime.getDtime(), "yyyy-MM-dd"));
                        item.setUrl("autohome://car/seriespicture?seriesid=" + series.getId() + "&orgin=0&seriesname=" + UrlUtil.encode(series.getName()) + "&categoryid=" + picUpdateTime.getPicClass());
                        dto.getItemList().add(item);
                    }
                }

                //配置首发
                if (specEntityList.stream().anyMatch(x -> x.getParamIsShow() == 1)) {
                    Date paramFirstTime = seriesTimeAxisMapper.getParamFirstTime(series.getId());
                    if (paramFirstTime != null) {
                        SeriesTimeAxisDto.Item item = new SeriesTimeAxisDto.Item();
                        item.setTypecode(SeriesTimeAxisEnum.PARAM_FIRST_PUBLISH_TIME.getValue());
                        item.setDate(DateUtil.format(paramFirstTime, "yyyy-MM-dd"));
                        item.setUrl("autohome://carcompare/paramcontrast?seriesid=" + series.getId() + "&seriesname=" + UrlUtil.encode(series.getName()) + "&hassummaryconfig=1");
                        dto.getItemList().add(item);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(paramFirstTime);
                        calendar.add(Calendar.DATE, 1);
                        Date paramUpdateTime = seriesTimeAxisMapper.getParamUpdateTime(series.getId(), DateUtil.format(calendar.getTime(), "yyyy-MM-dd"));
                        if (paramUpdateTime != null) {
                            item = new SeriesTimeAxisDto.Item();
                            item.setTypecode(SeriesTimeAxisEnum.PARAM_UPDATE_TIME.getValue());
                            item.setDate(DateUtil.format(paramUpdateTime, "yyyy-MM-dd"));
                            item.setUrl("autohome://carcompare/paramcontrast?seriesid=" + series.getId() + "&seriesname=" + UrlUtil.encode(series.getName()) + "&hassummaryconfig=1");
                            dto.getItemList().add(item);
                        }
                    }
                }

                //周销量最新发布
                BaseModel<WeekRankHistoryDto> weekRankModel = carApiClient.getSeriesWeekRankHistory(series.getId()).join();
                if (weekRankModel != null && weekRankModel.getResult() != null && weekRankModel.getResult().getData() != null && weekRankModel.getResult().getData().size() > 0) {
                    String weekDate = weekRankModel.getResult().getData().get(0).getWeek_day();
                    SeriesTimeAxisDto.Item item = new SeriesTimeAxisDto.Item();
                    item.setTypecode(SeriesTimeAxisEnum.new_car_sales_time.getValue());
                    item.setDate(weekDate);

                    BaseModel<List<WeekRankHistoryDto.DataDTO>> rank = carApiClient.getSeriesWeekRank(weekDate).join();
                    int rn = 0;
                    if (rank != null && rank.getResult() != null) {
                        Optional<WeekRankHistoryDto.DataDTO> first = rank.getResult().stream().filter(x -> x.getSeriesid().equals(String.valueOf(series.getId()))).findFirst();
                        if (first.isPresent()) {
                            rn = NumberUtils.toInt(first.get().getRnnum() + "", 0);
                            item.setSalecnt(NumberUtils.toInt(first.get().getSalecnt() + "", 0));
                        }
                    }
                    if (rn > 0 && rn <= 100) {
                        item.setUrl("autohome://car/singlerank?from=0&typeid=1&subranktypeid=2&week=" + weekDate + "&rank=" + rn);
                    }
                    dto.getItemList().add(item);
                }

                //新车上市
                CmsNewCarResult seriesOnTime = getSeriesOnTime(series.getId());
                if (seriesOnTime != null) {
                    SeriesTimeAxisDto.Item item = new SeriesTimeAxisDto.Item();
                    item.setTypecode(SeriesTimeAxisEnum.SERIES_ONLINE.getValue());
                    String format = DateUtil.format(seriesOnTime.getOnDate(), "yyyy-MM-dd");
                    item.setDate(format);
                    //item.setUrl("autohome://carcompare/paramcontrast?seriesid=" + series.getId() + "&seriesname=" + UrlUtil.encode(series.getName()) + "&hassummaryconfig=1");
                    dto.getItemList().add(item);
                    //判断配置首发是否晚于上市时间，如果晚于，则改把配置首发时间改成上市时间
                    SeriesTimeAxisDto.Item paramFirstTime = dto.getItemList().stream().filter(x -> x.getTypecode() == SeriesTimeAxisEnum.PARAM_FIRST_PUBLISH_TIME.getValue()).findFirst().orElse(null);
                    if (paramFirstTime != null && paramFirstTime.getDate() != null) {
                        Date paramFirst = DateUtil.parse(paramFirstTime.getDate(), "yyyy-MM-dd");
                        if (!paramFirst.before(seriesOnTime.getOnDate())) {
                            paramFirstTime.setDate(format);
                            xxlLog.accept("配置首发是否晚于上市时间的车系："+series.getId());
                        }
                    }
                }
                update(makeParam(series.getId()), dto);
                xxlLog.accept(series.getId() + " " + series.getName() + " success");
            }
            ThreadUtil.sleep(50);
        });
    }

    CmsNewCarResult getSeriesOnTime(int seriesId) {
        return cmsApiClient.getCmsNewCar(seriesId).thenApply(x -> {
            if (x != null && x.getResult() != null && x.getResult().size() > 0) {
                return x.getResult().get(0);
            }
            return null;
        }).exceptionally(e -> {
            return null;
        }).join();
    }
}
