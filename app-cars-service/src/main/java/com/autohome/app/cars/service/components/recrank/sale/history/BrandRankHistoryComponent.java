package com.autohome.app.cars.service.components.recrank.sale.history;

import com.autohome.app.cars.mapper.appcars.RankSaleMonthMapper;
import com.autohome.app.cars.mapper.appcars.entities.SaleMonthEnergyTypeRankSourceEntity;
import com.autohome.app.cars.mapper.popauto.BrandMapper;
import com.autohome.app.cars.mapper.popauto.entities.BrandEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.common.RankConstant;
import com.autohome.app.cars.service.components.recrank.dtos.BrandRankHistoryDto;
import com.autohome.app.cars.service.components.recrank.dtos.SaleMonthEnergyTypeRankDto;
import com.autohome.app.cars.service.components.recrank.dtos.SaleMonthEnergyTypeRankListDto;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : zzli
 * @description : 品牌月榜、周榜销量趋势
 * @date : 2024/10/30 16:37
 */
@Component
@Slf4j
public class BrandRankHistoryComponent extends BaseComponent<BrandRankHistoryDto> {
    final String brandIdParamName = "brandId";

    @Autowired
    BrandDetailComponent brandDetailComponent;

    @Autowired
    BrandMapper brandMapper;
    @Autowired
    private RankSaleMonthMapper rankSaleMonthMapper;

    @Autowired
    SeriesSaleMonthRankByEnergyTypeComponent seriesSaleMonthRankByEnergyTypeComponent;

    TreeMap<String, Object> makeParam(int brandId) {
        return ParamBuilder.create(brandIdParamName, brandId).build();
    }

    public BrandRankHistoryDto get(int brandId) {
        return baseGet(makeParam(brandId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        //获取所有月份
        List<String> monthList = rankSaleMonthMapper.getMonthList();
        List<SaleMonthEnergyTypeRankDto> rankDtos = new ArrayList<>();
        Lists.partition(monthList, 100).forEach(sublist -> {
            seriesSaleMonthRankByEnergyTypeComponent.getList(monthList).thenAccept(list -> {
                if (list != null && !list.isEmpty()) {
                    rankDtos.addAll(list.stream().filter(x -> Objects.nonNull(x)).collect(Collectors.toList()));
                }
            }).join();
        });


        for (BrandEntity brand : brandMapper.getAllBrands()) {

//            if (brand.getId()!=598) {
//                continue;
//            }
            List<SaleMonthEnergyTypeRankSourceEntity> brandHistory = rankSaleMonthMapper.getBrandHistory(brand.getId());

            if (brandHistory != null && !brandHistory.isEmpty()) {

                BrandRankHistoryDto brandRankHistoryDto = new BrandRankHistoryDto();
                brandRankHistoryDto.setBrandid(brand.getId());
                brandRankHistoryDto.setBrandname(brand.getName());

                for (int i = 0; i < brandHistory.size(); i++) {
                    SaleMonthEnergyTypeRankSourceEntity current = brandHistory.get(i);
                    LocalDate month = LocalDate.parse(current.getMonth(), RankConstant.LOCAL_MONTH_FORMATTER);
                    String date = String.format("%d年%02d月", month.getYear(), month.getMonthValue());
                    brandRankHistoryDto.getMonth().add(new BrandRankHistoryDto.MonthItem(month.getMonthValue() + "月", date));

                    BrandRankHistoryDto.SaleCountItem.Info info = new BrandRankHistoryDto.SaleCountItem.Info();
                    info.setTitle(date + "·零售销量");
                    info.setText("较上月");
                    info.setCount(current.getSalecnt());
                    //todo 计算排名
                    SaleMonthEnergyTypeRankDto saleMonthDto = rankDtos.stream().filter(x -> x.getMonth().equals(current.getMonth())).findFirst().orElse(null);
                    info.setRank(getRank(brand.getId(), saleMonthDto));

                    //第一个月，没有比较
                    if (i == 0) {
                        info.setComparenum(0L);
                        info.setComparetype(0);
                    } else {
                        SaleMonthEnergyTypeRankSourceEntity next = brandHistory.get(i - 1);
                        Long saleDiffCount = current.getSalecnt() - next.getSalecnt();
                        // 上升1，下降-1，持平0
                        if (saleDiffCount > 0) {
                            info.setComparetype(1);
                        } else if (saleDiffCount == 0) {
                            info.setComparetype(0);
                        } else {
                            info.setComparetype(-1);
                        }
                        info.setComparenum(Math.abs(saleDiffCount));
                    }
                    brandRankHistoryDto.getSalecount().add(new BrandRankHistoryDto.SaleCountItem(current.getSalecnt(), info));
                }
                update(makeParam(brand.getId()), brandRankHistoryDto);
                xxlLog.accept("品牌id:" + brand.getId() + "  --->  success");
            }
        }
    }

    String getRank(int brandId, SaleMonthEnergyTypeRankDto saleMonthDto) {
        if (Objects.isNull(saleMonthDto) || CollectionUtils.isEmpty(saleMonthDto.getList())) {
            return "-";
        }
        //把车系销量，转换成品牌分组的销量
        List<SaleMonthEnergyTypeRankListDto> brandList = new ArrayList<>();
        Map<Integer, List<SaleMonthEnergyTypeRankListDto>> collect = saleMonthDto.getList().stream().collect(Collectors.groupingBy(SaleMonthEnergyTypeRankListDto::getBrandid));

        if (!collect.containsKey(brandId)) {
            return "-";
        }
        collect.forEach((id, list) -> {
            long saleCount = list.stream().mapToLong(SaleMonthEnergyTypeRankListDto::getAllCount).sum();
            SaleMonthEnergyTypeRankListDto item = new SaleMonthEnergyTypeRankListDto();
            item.setBrandid(id);
            item.setAllCount(saleCount);
            brandList.add(item);
        });

        List<SaleMonthEnergyTypeRankListDto> collect1 = brandList.stream().sorted(Comparator.comparingLong(SaleMonthEnergyTypeRankListDto::getAllCount).reversed()).collect(Collectors.toList());

        Optional<SaleMonthEnergyTypeRankListDto> first = collect1.stream().filter(x -> x.getBrandid() == brandId).findFirst();
        if (first.isPresent()) {
            return (collect1.indexOf(first.get()) + 1) + "";
        }
        return "-";
    }

}
