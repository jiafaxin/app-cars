package com.autohome.app.cars.service.components.recrank.newenergy;

import com.autohome.app.cars.common.enums.ZiXunNewCarTagEnum;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.AttRankNewCarMapper;
import com.autohome.app.cars.mapper.appcars.EnergyBeiliKoubeiMapper;
import com.autohome.app.cars.mapper.appcars.entities.AttRankNewCarEntity;
import com.autohome.app.cars.mapper.appcars.entities.EnergyBeiliKoubeiEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.components.recrank.dtos.AttentionNewCarListDto;
import com.autohome.app.cars.service.components.recrank.dtos.DriveRangeCostListDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangchengwei
 * @date 2024/3/28
 */
@Slf4j
@Component
public class NewEnergyComponent extends BaseComponent<DriveRangeCostListDto> {

    final static String priceParamName = "price";

    final static String typeidParamName = "typeid";
    final static String energytypeParamName = "energytype";
    final static String brandidParamName = "brandid";
    final static String seasonParamName = "season";
    final static String cityidParamName = "cityid";
    final static String pageindexParamName = "pageindex";
    final static String pagesizeParamName = "pagesize";

    final static String levelidsParamName = "levelids";
    final static String issaleParamName = "issale";

    final static String dataUpdateFlag = ":updateFlag:";

    @Autowired
    private EnergyBeiliKoubeiMapper energyBeiliKoubeiMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    TreeMap<String, Object> makeParam(String price, int typeid, String energytype, int brandid, int season, int cityid, int pageindex, int pagesize, String levelids, int issale) {
        return ParamBuilder.create(priceParamName, price)
                .add(typeidParamName, typeid)
                .add(energytypeParamName, energytype)
                .add(brandidParamName, brandid)
                .add(seasonParamName, season)
                .add(cityidParamName, cityid)
                .add(pageindexParamName, pageindex)
                .add(pagesizeParamName, pagesize)
                .add(levelidsParamName, levelids)
                .add(issaleParamName, issale)
                .build();
    }

    public String getList(TreeMap<String, Object> params) {
        DriveRangeCostListDto dto = getList(
                Objects.isNull(params.get("price")) ? null : (String) params.get("price"),
                Objects.isNull(params.get("typeid")) ? 0 : (int) params.get("typeid"),
                Objects.isNull(params.get("energytype")) ? null : (params.get("energytype") + ""),
                Objects.isNull(params.get("brandid")) ? 0 : (int) params.get("brandid"),
                Objects.isNull(params.get("season")) ? 0 : (int) params.get("season"),
                Objects.isNull(params.get("cityid")) ? 0 : (int) params.get("cityid"),
                Objects.isNull(params.get("pageindex")) ? 0 : (int) params.get("pageindex"),
                Objects.isNull(params.get("pagesize")) ? 0 : (int) params.get("pagesize"),
                Objects.isNull(params.get("levelids")) ? null : (params.get("levelids") + ""),
                Objects.isNull(params.get("issale")) ? 0 : (int) params.get("issale"))
                .join();
        return JsonUtil.toString(dto);
    }


    public CompletableFuture<DriveRangeCostListDto> getList(String price, int typeid, String energytype, int brandid, int season, int cityid, int pageindex, int pagesize, String levelids, int issale) {
        DriveRangeCostListDto listDto = baseGetAsync(makeParam(price, typeid, energytype, brandid, season, cityid, pageindex, pagesize, levelids, issale)).join();
        if (Objects.isNull(listDto)) {
            listDto = getFromDb(price, typeid, energytype, brandid, season, cityid, pageindex, pagesize, levelids, issale);
            if (Objects.nonNull(listDto)) {
                update(makeParam(price, typeid, energytype, brandid, season, cityid, pageindex, pagesize, levelids, issale), listDto);
            }
        }
        return CompletableFuture.completedFuture(listDto);
    }

    public DriveRangeCostListDto getFromDb(String price, int typeid, String energytype, int brandid, int season, int cityid, int pageindex, int pagesize, String levelids, int issale) {
        CityUtil.ZoneInfo zone = CityUtil.getZone(cityid);
        int min_price = 0;
        int max_price = 0;
        if (StringUtils.contains(price, "-")) {
            min_price = Integer.valueOf(price.split("-")[0]) * 10000;
            max_price = Integer.valueOf(price.split("-")[1]) * 10000;
        }
        int count = energyBeiliKoubeiMapper.countByByCondition(typeid, min_price, max_price, energytype, brandid, zone.bl(), zone.kb() + "", season, levelids, issale);
        int pagecount = (count + pagesize - 1) / pagesize;
        List<EnergyBeiliKoubeiEntity> energyBeiliKoubeiEntities = energyBeiliKoubeiMapper.pageGetByCondition(typeid, min_price, max_price, energytype, brandid, zone.bl(), zone.kb() + "", season, levelids, issale, (pageindex - 1) * pagesize, pagesize);
        DriveRangeCostListDto listDto = new DriveRangeCostListDto();
        listDto.setCount(count);
        listDto.setPageSize(pagesize);
        listDto.setPageIndex(pageindex);
        listDto.setPages(pagecount);
        energyBeiliKoubeiEntities.forEach(item -> {
            DriveRangeCostListDto.ResultDto resultDto = new DriveRangeCostListDto.ResultDto();
            resultDto.setSeriesId(item.getSeries_id());
            resultDto.setEnergyCost(item.getEnergy_cost().doubleValue());
            resultDto.setDriveRange(item.getDrive_range().doubleValue());
            resultDto.setLevelId(item.getLevel_id());
            resultDto.setBrandId(item.getBrand_id());
            resultDto.setSeason(item.getSeason());
            resultDto.setMinPrice(item.getMin_price());
            resultDto.setMaxPrice(item.getMax_price());
            resultDto.setMaxPrice(item.getMax_price());
            resultDto.setState(item.getState());
            resultDto.setSpecId(item.getSpec_id());
            resultDto.setSpecName(item.getSpec_name());
            resultDto.setEnergyType(item.getEnergyType() + "");
            listDto.getSeriesList().add(resultDto);
        });
        return listDto;
    }

}
