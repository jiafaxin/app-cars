package com.autohome.app.cars.service.components.recrank.sale.history;

import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.RankSaleWeekMapper;
import com.autohome.app.cars.mapper.appcars.entities.SaleWeekEnergyTypeRankSourceEntity;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.recrank.dtos.BrandRankHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 品牌周榜趋势
 * @date : 2024/11/1 13:34
 */
@Component
@Slf4j
public class BrandWeekRankHistoryComponent extends BaseComponent<BrandRankHistoryDto> {
    final String brandIdParamName = "brandId";


    @Autowired
    BrandMapper brandMapper;
    @Autowired
    private RankSaleWeekMapper rankSaleWeekMapper;


    TreeMap<String, Object> makeParam(int brandId) {
        return ParamBuilder.create(brandIdParamName, brandId).build();
    }

    public BrandRankHistoryDto get(int brandId) {
        return baseGet(makeParam(brandId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Map<String, List<SaleWeekEnergyTypeRankSourceEntity>> saleWeekMap = new HashMap<>();

        for (BrandEntity brand : brandMapper.getAllBrands()) {
            List<SaleWeekEnergyTypeRankSourceEntity> brandHistory = rankSaleWeekMapper.getBrandHistory(brand.getId(), 12);
            Collections.reverse(brandHistory);
            if (brandHistory != null && !brandHistory.isEmpty()) {

                BrandRankHistoryDto brandRankHistoryDto = new BrandRankHistoryDto();
                brandRankHistoryDto.setBrandid(brand.getId());
                brandRankHistoryDto.setBrandname(brand.getName());

                for (int i = 0; i < brandHistory.size(); i++) {
                    SaleWeekEnergyTypeRankSourceEntity current = brandHistory.get(i);
                    LocalDate week = LocalDate.parse(current.getWeek_day(), RankConstant.LOCAL_WEEK_FORMATTER);
                    String weekBegin_month = week.minusDays(1).format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER);
                    String weekBegin_Year = week.minusDays(1).format(formatter1);
                    String weekEnd = week.plusDays(5).format(RankConstant.LOCAL_WEEK_RANGE_FORMATTER);
                    String date_year = weekBegin_Year + "-" + weekEnd;
                    String date_month = weekBegin_month + "-" + weekEnd;


                    brandRankHistoryDto.getMonth().add(new BrandRankHistoryDto.MonthItem(date_month, date_month));

                    BrandRankHistoryDto.SaleCountItem.Info info = new BrandRankHistoryDto.SaleCountItem.Info();
                    info.setTitle(date_year + "·终端销量");
                    info.setText("较上周");
                    info.setCount(current.getSalecnt());
                    //todo 计算排名
                    List<SaleWeekEnergyTypeRankSourceEntity> listDo;
                    if (saleWeekMap.containsKey(current.getWeek_day())) {
                        listDo = saleWeekMap.get(current.getWeek_day());
                    } else {
                        listDo = rankSaleWeekMapper.getSeriesIdListByWeekDay(current.getWeek_day());
                        saleWeekMap.put(current.getWeek_day(), listDo);
                    }
                    info.setRank(getRank(brand.getId(), listDo));

                    //第一个月，没有比较
                    if (i == 0) {
                        info.setComparenum(0L);
                        info.setComparetype(-1);
                    } else {
                        SaleWeekEnergyTypeRankSourceEntity next = brandHistory.get(i - 1);
                        Long saleDiffCount = current.getSalecnt() - next.getSalecnt();
                        // 上升1，下降-1，持平0
                        if (saleDiffCount > 0) {
                            info.setComparetype(1);
                        } else if (saleDiffCount == 0) {
                            info.setComparetype(-1);//与上月持平默认下降0
                        } else {
                            info.setComparetype(-1);
                        }
                        info.setComparenum(Math.abs(saleDiffCount));
                    }
                    brandRankHistoryDto.getSalecount().add(new BrandRankHistoryDto.SaleCountItem(current.getSalecnt(), info));
                }
                update(makeParam(brand.getId()), brandRankHistoryDto);
                xxlLog.accept("品牌id:" + brand.getId() + "  --->  success");
                xxlLog.accept(JsonUtil.toString(brandRankHistoryDto));
            }
        }
    }

    String getRank(int brandId, List<SaleWeekEnergyTypeRankSourceEntity> saleMonthDto) {
        if (Objects.isNull(saleMonthDto) || saleMonthDto.isEmpty()) {
            return "-";
        }
        //把车系销量，转换成品牌分组的销量
        List<SaleWeekEnergyTypeRankSourceEntity> brandList = new ArrayList<>();
        Map<Integer, List<SaleWeekEnergyTypeRankSourceEntity>> collect = saleMonthDto.stream().collect(Collectors.groupingBy(SaleWeekEnergyTypeRankSourceEntity::getBrandid));

        if (!collect.containsKey(brandId)) {
            return "-";
        }
        collect.forEach((id, list) -> {
            long saleCount = list.stream().mapToLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).sum();
            SaleWeekEnergyTypeRankSourceEntity item = new SaleWeekEnergyTypeRankSourceEntity();
            item.setBrandid(id);
            item.setSalecnt(saleCount);
            brandList.add(item);
        });

        List<SaleWeekEnergyTypeRankSourceEntity> collect1 = brandList.stream().sorted(Comparator.comparingLong(SaleWeekEnergyTypeRankSourceEntity::getSalecnt).reversed()).collect(Collectors.toList());

        Optional<SaleWeekEnergyTypeRankSourceEntity> first = collect1.stream().filter(x -> x.getBrandid() == brandId).findFirst();
        if (first.isPresent()) {
            return (collect1.indexOf(first.get()) + 1) + "";
        }
        return "-";
    }
}
