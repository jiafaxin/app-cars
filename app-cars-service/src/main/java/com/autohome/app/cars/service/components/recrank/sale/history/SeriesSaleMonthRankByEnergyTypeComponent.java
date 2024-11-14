package com.autohome.app.cars.service.components.recrank.sale.history;

import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.RankSaleMonthMapper;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleMonthSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.RankSaleWeekSourceEntity;
import com.autohome.app.cars.mapper.appcars.entities.SaleMonthEnergyTypeRankSourceEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.recrank.dtos.SaleMonthEnergyTypeRankDto;
import com.autohome.app.cars.service.components.recrank.dtos.SaleMonthEnergyTypeRankListDto;

import com.autohome.app.cars.service.components.recrank.dtos.SaleWeekEnergyTypeRankDto;
import com.autohome.app.cars.service.components.recrank.dtos.SaleWeekEnergyTypeRankListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by dx on 2024/7/9
 */
@Slf4j
@Component
public class SeriesSaleMonthRankByEnergyTypeComponent extends BaseComponent<SaleMonthEnergyTypeRankDto> {
    private static String monthparamName = "month";

    @Autowired
    private RankSaleMonthMapper rankSaleMonthMapper;

    TreeMap<String, Object> makeParam(String month) {
        return ParamBuilder.create(monthparamName, month).build();
    }

    public CompletableFuture<SaleMonthEnergyTypeRankDto> get(String month) {
        return baseGetAsync(makeParam(month));
    }

    public CompletableFuture<List<SaleMonthEnergyTypeRankDto>> getList(List<String> monthList) {
        return baseGetListAsync(monthList.stream().map(this::makeParam).collect(Collectors.toList()));
    }

    /**
     * 批量获取
     *
     * @param monthList  月份集合
     * @param serieId    车系id
     * @param energytype 能源类型
     * @return
     */
    public Map<String, Integer> getRankByMonthList(List<String> monthList, int serieId, int energytype) {
        Map<String, Integer> mapResult = new HashMap<>();
        try {
            List<SaleMonthEnergyTypeRankDto> list = getList(monthList).join();
            if (list != null && list.size() > 0) {
                monthList.stream().forEach(month -> {
                    SaleMonthEnergyTypeRankDto dto = list.stream().filter(p -> p != null && p.getMonth().equals(month)).findFirst().orElse(null);
//                    if (dto == null) {
//                        dto = getDtoByDB(month);
//                    }
                    int ranknum = -1;
                    if (dto != null) {
                        ranknum = getRankByMonth(dto, month, serieId, energytype);
                    }
                    mapResult.put(month, ranknum);
                });
            }
        } catch (Exception ex) {
            log.error("getRankByMonthList异常-ex:{}", ex);
        }
        return mapResult;
    }

    private int getRankByMonth(SaleMonthEnergyTypeRankDto dto, String month, int serieId, int energytype) {
        try {
            //获取当前周的数据
            if (dto != null) {
                List<SaleMonthEnergyTypeRankListDto> rankDtoList = dto.getList();
                if (rankDtoList != null && rankDtoList.size() > 0) {
                    if (energytype == 1) {
                        //燃油
                        rankDtoList = rankDtoList.stream().filter(p -> p.getFuelCount() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getFuelCount).reversed().thenComparingInt(SaleMonthEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 456) {
                        //新能源
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergyCount() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getEnergyCount).reversed().thenComparingInt(SaleMonthEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 4) {
                        //纯电
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergy4Count() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getEnergy4Count).reversed().thenComparingInt(SaleMonthEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 5) {
                        //插混
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergy5Count() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getEnergy5Count).reversed().thenComparingInt(SaleMonthEnergyTypeRankListDto::getSerieId));
                    } else if (energytype == 6) {
                        //增程
                        rankDtoList = rankDtoList.stream().filter(p -> p.getEnergy6Count() > 0).collect(Collectors.toList());
                        rankDtoList.sort(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getEnergy6Count).reversed().thenComparingInt(SaleMonthEnergyTypeRankListDto::getSerieId));
                    } else {
                        //默认
                        rankDtoList.sort(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getAllCount).reversed().thenComparingInt(SaleMonthEnergyTypeRankListDto::getSerieId));
                    }
                    SaleMonthEnergyTypeRankListDto rankDto = rankDtoList.stream().filter(p -> p.getSerieId() == serieId).findFirst().orElse(null);
                    if (rankDto != null) {
                        int index = rankDtoList.indexOf(rankDto);
                        return index + 1;
                    }
                }
            }

        } catch (Exception ex) {
            log.error("getRankByMonth异常-month:{},dto:{},serieId:{},energytype:{},ex:{}",
                    month, JsonUtil.toString(dto), serieId, energytype, ex);
        }
        return -1;
    }


    public void refreshAll(Consumer<String> xxlLog) {
        try {
            List<SaleMonthEnergyTypeRankDto> list = getSaleMonthRankList();
            xxlLog.accept("总计执行：" + list.size());
            list.stream().forEach(item -> {
                xxlLog.accept(item.getMonth() + "-执行：" + item.getList().size());
                update(makeParam(item.getMonth()), item);
            });
        } catch (Exception ex) {
            xxlLog.accept("车系月销量能源类型排名异常:" + ExceptionUtil.getStackTrace(ex));
        }
    }

    /**
     * 刷新当前月份数据
     *
     * @return
     */
    public boolean modifyRefreshByMonth(String month) {
        try {
            SaleMonthEnergyTypeRankDto item = getDtoByDB(month);
            update(makeParam(item.getMonth()), item);
        } catch (Exception ex) {
            log.error("modifyRefresh异常-ex:{}", ex);
            return false;
        }
        return true;
    }


    /**
     * 获取全部月份数据
     *
     * @return
     */
    public List<SaleMonthEnergyTypeRankDto> getSaleMonthRankList() {
        List<SaleMonthEnergyTypeRankDto> resultList = new ArrayList<>();
        try {
            //获取最新周日期
            List<String> monthList = rankSaleMonthMapper.getMonthList();
            if (monthList != null && monthList.size() > 0) {
                monthList.stream().forEach(month -> {
                    SaleMonthEnergyTypeRankDto dto = getDtoByDB(month);
                    resultList.add(dto);
                });
            }
        } catch (Exception ex) {
            log.error("getSaleMonthRankList异常-ex:{}", ex);
        }
        return resultList;
    }

    /**
     * 回源查询
     *
     * @param month 月份
     * @return
     */
    private SaleMonthEnergyTypeRankDto getDtoByDB(String month) {
        List<SaleMonthEnergyTypeRankListDto> rankDtoList = new ArrayList<>();
        try {
            List<SaleMonthEnergyTypeRankSourceEntity> listDo = rankSaleMonthMapper.getSeriesIdListByMonth(month);
            if (listDo != null && listDo.size() > 0) {
                //取出所有车系id
                List<Integer> seriesIdList = listDo.stream().map(SaleMonthEnergyTypeRankSourceEntity::getSeriesid).distinct().collect(Collectors.toList());
                seriesIdList.stream().forEach(serieId -> {
                    //总数
                    List<SaleMonthEnergyTypeRankSourceEntity> seriesAll = listDo.stream().filter(p -> p.getSeriesid() == serieId).collect(Collectors.toList());
                    long allCount = seriesAll.stream().mapToLong(SaleMonthEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //燃油
                    long fuelCount = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 1).mapToLong(SaleMonthEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //新能源456
                    List<Integer> energyType = Arrays.asList(4, 5, 6);
                    long energyCount = listDo.stream().filter(p -> p.getSeriesid() == serieId && energyType.contains(p.getEnergy_type())).mapToLong(SaleMonthEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //4-电动
                    long energy4Count = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 4).mapToLong(SaleMonthEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //5-插电
                    long energy5Count = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 5).mapToLong(SaleMonthEnergyTypeRankSourceEntity::getSalecnt).sum();
                    //6-增程
                    long energy6Count = listDo.stream().filter(p -> p.getSeriesid() == serieId && p.getEnergy_type() == 6).mapToLong(SaleMonthEnergyTypeRankSourceEntity::getSalecnt).sum();
                    SaleMonthEnergyTypeRankListDto rankDto = new SaleMonthEnergyTypeRankListDto();
                    rankDto.setBrandid(seriesAll.get(0).getBrandid());
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
        SaleMonthEnergyTypeRankDto dto = new SaleMonthEnergyTypeRankDto();
        dto.setMonth(month);
        dto.setList(rankDtoList);
        return dto;
    }
}
