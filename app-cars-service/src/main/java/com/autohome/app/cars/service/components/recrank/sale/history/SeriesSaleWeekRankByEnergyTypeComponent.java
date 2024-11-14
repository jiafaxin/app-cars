package com.autohome.app.cars.service.components.recrank.sale.history;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.RankSaleWeekMapper;
import com.autohome.app.cars.mapper.appcars.entities.SaleWeekEnergyTypeRankSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.recrank.dtos.SaleWeekEnergyTypeRankDto;
import com.autohome.app.cars.service.components.recrank.dtos.SaleWeekEnergyTypeRankListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Created by dx on 2024/7/9
 * 按能源类型汇总车系周销量排名数据组件
 */
@Slf4j
@Component
public class SeriesSaleWeekRankByEnergyTypeComponent extends BaseComponent<SaleWeekEnergyTypeRankDto> {
    private static String serieidparamName = "serieid";

    @Autowired
    private RankSaleWeekMapper rankSaleWeekMapper;


    TreeMap<String, Object> makeParam(int serieid) {
        return ParamBuilder.create(serieidparamName, serieid).build();
    }

    public CompletableFuture<SaleWeekEnergyTypeRankDto> get(int serieid) {
        return baseGetAsync(makeParam(serieid));
    }

    public CompletableFuture<List<SaleWeekEnergyTypeRankDto>> getList(List<Integer> serieIdList) {
        return baseGetListAsync(serieIdList.stream().map(this::makeParam).collect(Collectors.toList()));
    }


    public void refreshAll(Consumer<String> xxlLog) {
        try {
            List<SaleWeekEnergyTypeRankDto> list = getSaleWeekRankList();
            xxlLog.accept("总计执行：" + list.size());
            int count = 0;
            for (SaleWeekEnergyTypeRankDto item : list) {
                xxlLog.accept(item.getSeriesId() + "-执行：" + item.getList().size());
                update(makeParam(item.getSeriesId()), item);
                count++;
            }
            xxlLog.accept("执行结束：" + count);
        } catch (Exception ex) {
            xxlLog.accept("车系周销量能源类型排名异常:" + ExceptionUtil.getStackTrace(ex));
        }
    }

    /**
     * 数据更新刷新
     *
     * @return
     */
    public boolean modifyRefresh() {
        try {
            List<SaleWeekEnergyTypeRankDto> list = getSaleWeekRankList();
            for (SaleWeekEnergyTypeRankDto item : list) {
                update(makeParam(item.getSeriesId()), item);
            }
        } catch (Exception ex) {
            log.error("modifyRefresh异常-ex:{}", ex);
            return false;
        }
        return true;
    }


    /**
     * 根据时间、车系id、能源类型查询对应的排名值
     *
     * @param weekdayList 时间集合
     * @param serieId     车系id
     * @param energytype  能源类型
     * @return
     */
    public Map<String, Integer> getRankByWeekDayList(List<String> weekdayList, int serieId, int energytype) {
        Map<String, Integer> mapResult = new HashMap<>();
        try {
            SaleWeekEnergyTypeRankDto firstDto = get(serieId).join();
            if (firstDto != null) {
                List<SaleWeekEnergyTypeRankDto.WeekDayList> list = firstDto.getList();
                if (list != null && list.size() > 0) {
                    weekdayList.stream().forEach(weekday -> {
                        SaleWeekEnergyTypeRankDto.WeekDayList dto = list.stream().filter(p -> p != null && p.getWeek_day().equals(weekday)).findFirst().orElse(null);
//                        if (dto == null) {
//                            dto = getDtoByDB(weekday);
//                        }
                        int ranknum = -1;
                        if (dto != null) {
                            ranknum = getRankByWeekDay(dto, weekday, serieId, energytype);
                        }
                        mapResult.put(weekday, ranknum);
                    });
                }
            }
        } catch (Exception ex) {
            log.error("getRankByWeekDayList异常-ex:{}", ex);
        }
        return mapResult;
    }

    private int getRankByWeekDay(SaleWeekEnergyTypeRankDto.WeekDayList dto, String week_day, int serieId, int energytype) {
        try {
            //获取当前周的数据
            if (dto != null) {
                List<SaleWeekEnergyTypeRankListDto> rankDtoList = dto.getList();
                if (rankDtoList != null && rankDtoList.size() > 0) {
                    if (energytype == 1) {
                        //燃油
                        rankDtoList = rankDtoList.stream().filter(p -> p.getFuelCount() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleWeekEnergyTypeRankListDto::getFuelCount).reversed().thenComparingInt(SaleWeekEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 456) {
                        //新能源
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergyCount() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleWeekEnergyTypeRankListDto::getEnergyCount).reversed().thenComparingInt(SaleWeekEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 4) {
                        //纯电
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergy4Count() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleWeekEnergyTypeRankListDto::getEnergy4Count).reversed().thenComparingInt(SaleWeekEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 5) {
                        //插混
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergy5Count() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleWeekEnergyTypeRankListDto::getEnergy5Count).reversed().thenComparingInt(SaleWeekEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 6) {
                        //增程
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergy6Count() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleWeekEnergyTypeRankListDto::getEnergy6Count).reversed().thenComparingInt(SaleWeekEnergyTypeRankListDto::getSerieId));
                    } else {
                        //默认
                        rankDtoList.sort(Comparator.comparingLong(SaleWeekEnergyTypeRankListDto::getAllCount).reversed().thenComparingInt(SaleWeekEnergyTypeRankListDto::getSerieId));
                    }
                    SaleWeekEnergyTypeRankListDto rankDto = rankDtoList.stream().filter(p -> p.getSerieId() == serieId).findFirst().orElse(null);
                    if (rankDto != null) {
                        int index = rankDtoList.indexOf(rankDto);
                        return index + 1;
                    }
                }
            }

        } catch (Exception ex) {
            log.error("getRankByWeekDay异常-week_day:{},dto:{},serieId:{},energytype:{},ex:{}",
                    week_day, JsonUtil.toString(dto), serieId, energytype, ex);
        }
        return -1;
    }


    /**
     * 获取最近8周的每个车系下的各个能源类型销量数据
     *
     * @return
     */
    public List<SaleWeekEnergyTypeRankDto> getSaleWeekRankList() {
        List<SaleWeekEnergyTypeRankDto> resultList = new ArrayList<>();
        try {
            List<Integer> seriesIdList = rankSaleWeekMapper.getAllSeriesId();
            if (seriesIdList != null && seriesIdList.size() > 0) {
                seriesIdList.stream().forEach(seriesId -> {
                    List<SaleWeekEnergyTypeRankDto.WeekDayList> list = new ArrayList<>();
                    List<String> weekDayList = rankSaleWeekMapper.getLastWeekDayList(seriesId);
                    if (weekDayList != null && weekDayList.size() > 0) {
                        weekDayList.stream().forEach(time -> {
                            SaleWeekEnergyTypeRankDto.WeekDayList weekDayDto = getDtoByDB(time);
                            list.add(weekDayDto);
                        });
                    }
                    SaleWeekEnergyTypeRankDto dto = new SaleWeekEnergyTypeRankDto();
                    dto.setSeriesId(seriesId);
                    dto.setList(list);
                    resultList.add(dto);
                });
            }
        } catch (Exception ex) {
            log.error("getSaleWeekRankList异常-ex:{}", ex);
        }
        return resultList;
    }

    /**
     * 根据时间查询db数据
     *
     * @param week_day 周时间
     * @return
     */
    private SaleWeekEnergyTypeRankDto.WeekDayList getDtoByDB(String week_day) {
        List<SaleWeekEnergyTypeRankListDto> rankDtoList = new ArrayList<>();
        try {
            List<SaleWeekEnergyTypeRankSourceEntity> listDo = rankSaleWeekMapper.getSeriesIdListByWeekDay(week_day);
            if (listDo != null && listDo.size() > 0) {
                //取出所有车系id
                List<Integer> seriesIdList = listDo.stream().map(SaleWeekEnergyTypeRankSourceEntity::getSeriesid).distinct().collect(Collectors.toList());
                seriesIdList.stream().forEach(serieId -> {
                    //总数
                    long allCount = listDo.stream().filter(p -> p.getSeriesid() == serieId).mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //燃油
                    long fuelCount = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 1).mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //新能源456
                    List<Integer> energyType = Arrays.asList(4, 5, 6);//energyType.contains(p.getEnergy_type())
                    long energyCount = listDo.stream().filter(p -> p.getSeriesid() == serieId && energyType.contains(p.getEnergy_type())).mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //4-电动
                    long energy4Count = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 4).mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //5-插电
                    long energy5Count = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 5).mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //6-增程
                    long energy6Count = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 6).mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
                    SaleWeekEnergyTypeRankListDto rankDto = new SaleWeekEnergyTypeRankListDto();
                    rankDto.setSerieId(serieId);
                    rankDto.setAllCount(allCount);
                    rankDto.setFuelCount(fuelCount);
                    rankDto.setEnergyCount(energyCount);
                    rankDto.setEnergy4Count(energy4Count);
                    rankDto.setEnergy5Count(energy5Count);
                    rankDto.setEnergy6Count(energy6Count);
                    rankDtoList.add(rankDto);
                });
            }
        } catch (Exception ex) {
            log.error("getDtoByDB异常-ex:{}", ex);
        }
        SaleWeekEnergyTypeRankDto.WeekDayList dto = new SaleWeekEnergyTypeRankDto.WeekDayList();
        dto.setWeek_day(week_day);
        dto.setList(rankDtoList);
        return dto;
    }

    /**
     * 根据当前时间获取最近8周时间-每周的周二(包含当前时间所在周)
     *
     * @param time 当前时间
     * @return
     */
    private static List<String> getLastTuesDayTimeList(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> tuesdays = new ArrayList<>();
        LocalDate today = LocalDate.parse(time);
        for (int i = 0; i < 8; i++) {
            LocalDate tuesday = today.minusDays(i * 7L).with(DayOfWeek.TUESDAY);
            tuesdays.add(tuesday.format(formatter));
        }
        return tuesdays;
    }
}
