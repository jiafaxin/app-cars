package com.autohome.app.cars.service.components.owner;

import com.autohome.app.cars.common.carconfig.Spec;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.mapper.appcars.EnergyBeiliKoubeiMapper;
import com.autohome.app.cars.mapper.appcars.EnergyBeiliMapper;
import com.autohome.app.cars.mapper.appcars.EnergyKoubeiMapper;
import com.autohome.app.cars.mapper.appcars.EnergyZoneMappingMapper;
import com.autohome.app.cars.mapper.appcars.entities.EnergyBeiliEntity;
import com.autohome.app.cars.mapper.appcars.entities.EnergyBeiliKoubeiEntity;
import com.autohome.app.cars.mapper.appcars.entities.EnergyKoubeiEntity;
import com.autohome.app.cars.mapper.appcars.entities.ZoneMappingEntity;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecEntity;
import com.autohome.app.cars.mapper.popauto.entities.SpecOfficialRangeEntity;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.owner.dtos.BeiliKoubeiInfo;
import com.autohome.app.cars.service.components.owner.dtos.SeriesCityYangche;
import com.google.type.Decimal;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Component
@DBConfig(tableName = "series_wenqu_beili_koubei")
@Slf4j
public class BeiliKoubeiComponent extends BaseComponent<BeiliKoubeiInfo> {

    @Autowired
    EnergyZoneMappingMapper energyZoneMappingMapper;
    @Autowired
    EnergyBeiliMapper energyBeiliMapper;
    @Autowired
    EnergyKoubeiMapper energyKoubeiMapper;
    @Autowired
    SpecMapper specMapper;
    @Autowired
    SeriesMapper seriesMapper;
    @Autowired
    SeriesDetailComponent seriesDetailComponent;
    @Autowired
    EnergyBeiliKoubeiMapper energyBeiliKoubeiMapper;


    TreeMap<String, Object> makeParam(int seriesId, int zone_kb,String zone_bl) {
        return ParamBuilder.create("seriesId", seriesId).add("zone_kb", zone_kb).add("zone_bl",zone_bl).build();
    }

    public CompletableFuture<BeiliKoubeiInfo> get(int seriesId, int zone_kb,String zone_bl) {
        return baseGetAsync(makeParam(seriesId, zone_kb,zone_bl));
    }

    public CompletableFuture<BeiliKoubeiInfo> get(int seriesId,int cityId){
        CityUtil.ZoneInfo zone = CityUtil.getZone(cityId);
        if (zone == null) {
            return CompletableFuture.completedFuture(null);
        }
        return get(seriesId, zone.kb(),zone.bl());
    }

    public void refreshAll(Consumer<String> xxlLog) {
        String version = DateUtil.format(new Date(), "yyyyMMddHHmmss");

        var koubeiList = energyKoubeiMapper.getAll();
        var beiliList = energyBeiliMapper.getAll();
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        Map<Integer, SeriesEntity> allSeriesMap = allSeries.stream().collect(toMap(SeriesEntity::getId, Function.identity(), (key1, key2) -> key2));
        Map<Integer, SpecEntity> specs = new HashMap<>();
        //口碑数据赋值需要的属性
        koubeiList.forEach(item->{
            try {
                Integer specId = specMapper.getMaxSpecIdByXuhang(item.getSeries_id().intValue(), item.getOfficial_range().intValue()+"");
                if(specId==null) {
                    specId = specMapper.getMaxSpecIdByXuhang_CV(item.getSeries_id().intValue(), item.getOfficial_range().intValue()+"");
                }
                if(specId==null){
                    xxlLog.accept("口碑车型id不存在"+JsonUtil.toString(item));
                    return;
                }
                item.setSpecId(specId);
                SpecEntity specEntity = specs.containsKey(specId)?specs.get(specId) : specMapper.getSpec(specId);
                specs.put(specId,specEntity);
                if(specEntity==null){
                    xxlLog.accept("口碑车型不存在"+JsonUtil.toString(item));
                    return ;
                }

                item.setSpecState(specEntity.getState());
                item.setEnergyType(specEntity.getFuelType());
                item.setBrandId(specEntity.getBrandId());
                item.setLevelId(specEntity.getLevelId());
                item.setSpec_name(specEntity.getName());
            }catch (Exception e){
                xxlLog.accept("处理koubei xinxi 报错"+ JsonUtil.toString(item)+ExceptionUtil.getStackTrace(e));
                log.error("处理koubei xinxi 报错",e);
            }
        });
        xxlLog.accept("完成口碑数据补充");
        beiliList.forEach(item->{
            try {
                int specId = item.getSpec_id().intValue();
                SpecEntity specEntity = specs.containsKey(specId)?specs.get(specId) : specMapper.getSpec(specId);
                specs.put(specId,specEntity);
                if(specEntity==null){
                    xxlLog.accept("北理车型不存在"+JsonUtil.toString(item));
                    return ;
                }

                if(StringUtils.isBlank(item.getZone_id())){
                    item.setZone_id("999");
                }

                item.setSpecState(specEntity.getState());
                item.setEnergyType(specEntity.getFuelType());
                item.setBrandId(specEntity.getBrandId());
                item.setLevelId(specEntity.getLevelId());
                item.setSeriesId(specEntity.getSeriesId());
                item.setOfficial_range(getOfficialRange(item.getSpec_id().intValue()));
                item.setSpec_name(specEntity.getName());
            }catch (Exception e){
                xxlLog.accept("处理beili xinxi 报错"+ JsonUtil.toString(item));
                log.error("处理beili xinxi 报错",e);
            }
        });
        xxlLog.accept("完成北理数据补充");

        koubeiList.removeIf(x-> x.getBrandId() <= 0|| x.getOfficial_range().intValue() <= 0);
        beiliList.removeIf(x-> x.getSpec_id() == null || x.getBrandId()<=0 || x.getOfficial_range() <= 0);
        xxlLog.accept("移除找不到车型的数据结束");


        for (ZoneMappingEntity zone : energyZoneMappingMapper.getMapping()) {

            //兼容香港、澳门、台湾：北理没有温区，口碑=6
            if(StringUtils.isBlank(zone.getBl_zone_id()) && zone.getKb_zone_id()!=6){
                continue;
            }
            if(StringUtils.isBlank(zone.getBl_zone_id())){
                zone.setBl_zone_id("999");
            }


            Map<String,List<BeiliKoubeiInfo.SeasonDetail>> items = new HashMap<>();

            var koubei = koubeiList.stream().filter(x -> x.getZone_id() == zone.getKb_zone_id()).collect(toList());
            var beili = beiliList.stream().filter(x -> x.getZone_id().equals(zone.getBl_zone_id())).collect(toList());

            if(koubei.size()==0 && beili.size()==0) {
                continue;
            }


            //口碑车系的最大续航
            Map<Integer,Integer> kbormax = koubei.stream().collect(Collectors.toMap(x->x.getSeries_id().intValue(), x->x.getOfficial_range().intValue(),Integer::max));
            //北理按车系的最大续航
            Map<Integer,Integer> blormax = beili.stream().collect(Collectors.toMap(x->x.getSeriesId(), x->x.getOfficial_range(),Integer::max));

            //用于记录口碑都有哪些车系有数据了，如果有数据了，就不用北理的数据了。
//            List<String> seriesList = new ArrayList<>();
            AtomicInteger count = new AtomicInteger(0);
            koubei.forEach(item -> {

                if(item.getDrive_range()==null || item.getDrive_range().intValue() <= 0) {
                    return;
                }

                int isMaxOR = kbormax.get(item.getSeries_id().intValue()).equals(item.getOfficial_range().intValue())?1:0;
                int min_price = 0;
                int max_price = 0;
                if(allSeriesMap.containsKey(item.getSeries_id().intValue())){
                    min_price = allSeriesMap.get(item.getSeries_id().intValue()).getSeriesPriceMin();
                    max_price = allSeriesMap.get(item.getSeries_id().intValue()).getSeriesPriceMax();
                }
                boolean saveSuccess = save(item.getSeason(),
                        zone.getBl_zone_id(),
                        zone.getKb_zone_id(),
                        item.getDrive_range(),
                        item.getEnergy_cost(),
                        item.getSpecId(),
                        version,item.getSpecState(),item.getEnergyType(),item.getBrandId(),item.getSeries_id().intValue(),item.getLevelId(),1,
                        item.getOfficial_range().intValue(),isMaxOR,item.getSpec_name(),min_price,max_price
                );
                if (saveSuccess) {
                    count.incrementAndGet();
//                    seriesList.add(item.getSeries_id() + "_" + item.getSeason());
                    if(isMaxOR == 1){
                        //组件化数据
                        String key = String.format("%s_%s_%s",item.getSeries_id(),zone.getKb_zone_id(),zone.getBl_zone_id());
                        if(!items.containsKey(key)){
                            items.put(key,new ArrayList<>());
                        }
                        items.get(key).add(new BeiliKoubeiInfo.SeasonDetail(){{
                            setType(1);
                            setSeason(item.getSeason());
                            setDrive_range(item.getDrive_range());
                            setEnergy_cost(item.getEnergy_cost());
                        }});
                    }
                }else{
                    xxlLog.accept("口碑失败"+JsonUtil.toString(item));
                }
            });
            //没口碑的用北理
            beili.forEach(item -> {
                if(item.getDrive_range()==null || item.getDrive_range().intValue() <= 0) {
                    return;
                }

//                //用了口碑的数据，就不再用北理的数据
//                if (seriesList.contains(item.getSeriesId() + "_" + item.getSeason())) {
//                    return;
//                }
                int min_price = 0;
                int max_price = 0;
                if(allSeriesMap.containsKey(item.getSeriesId())){
                    min_price = allSeriesMap.get(item.getSeriesId()).getSeriesPriceMin();
                    max_price = allSeriesMap.get(item.getSeriesId()).getSeriesPriceMax();
                }
                int isMaxOR = blormax.get(item.getSeriesId()).equals(item.getOfficial_range())?1:0;
                boolean saveSuccess = save(item.getSeason(),
                        zone.getBl_zone_id(),
                        zone.getKb_zone_id(),
                        item.getDrive_range(),
                        item.getEnergy_cost(),
                        item.getSpec_id().intValue(),
                        version,item.getSpecState(),item.getEnergyType(),item.getBrandId(),item.getSeriesId(),item.getLevelId(),2,
                        item.getOfficial_range(),isMaxOR,item.getSpec_name(),min_price,max_price
                );
                if (saveSuccess) {
                    count.incrementAndGet();
                    if(isMaxOR == 1){
                        //组件化数据
                        String key = String.format("%s_%s_%s",item.getSeriesId(),zone.getKb_zone_id(),zone.getBl_zone_id());
                        if(!items.containsKey(key)){
                            items.put(key,new ArrayList<>());
                        }
                        items.get(key).add(new BeiliKoubeiInfo.SeasonDetail(){{
                            setType(2);
                            setSeason(item.getSeason());
                            setDrive_range(item.getDrive_range());
                            setEnergy_cost(item.getEnergy_cost());
                        }});
                    }
                }else{
                    xxlLog.accept("口碑失败"+JsonUtil.toString(item));
                }
            });

            items.forEach((k,v)->{
                String[] ks = k.split("_");
                BeiliKoubeiInfo info = new BeiliKoubeiInfo();
                info.setSeriesId(Integer.parseInt(ks[0]));
                info.setKb_zone(Integer.parseInt(ks[1]));
                info.setBl_zone(ks[2]);
                info.setSeasons(v);
                update(makeParam(info.getSeriesId(),info.getKb_zone(),info.getBl_zone()),info);
            });

            xxlLog.accept("北理温区：" + zone.getBl_zone_id() + " , 口碑温区：" + zone.getKb_zone_id() + " [success] 共同步:" + count.get());
        }
        xxlLog.accept("all  success");
        var olds = energyBeiliKoubeiMapper.getOldList(version);
        for (EnergyBeiliKoubeiEntity item : olds) {
            energyBeiliKoubeiMapper.deleteOld(item.getId());
            TreeMap<String, Object> params = makeParam(item.getSeries_id(),item.getKb_zone_id(),item.getBl_zone_id());
            BeiliKoubeiInfo old = baseGet(params);
            if(old!=null){
                old.remove(item.getSeason());
                if(old.getSeasons().size()>0){
                    update(params,old);
                }else {
                    delete(params);
                }
            }
        }
        xxlLog.accept("共移除数据：" + olds.size());
    }

    boolean save(
            int season, String bl_zone_id, int kb_zone_id, BigDecimal drive_range, BigDecimal energy_cost, int specId,String verison,
            int specState,int energyType,int brandId,int seriesId,int levelId,int dataFrom,
            int official_range, int is_max_range, String spec_name, int min_price, int max_price
    ){

        EnergyBeiliKoubeiEntity entity = new EnergyBeiliKoubeiEntity();
        entity.setBl_zone_id(bl_zone_id);
        entity.setKb_zone_id(kb_zone_id);

        entity.setDrive_range(drive_range);
        entity.setEnergy_cost(energy_cost);
        entity.setBrand_id(brandId);
        entity.setLevel_id(levelId);
        entity.setSeries_id(seriesId);
        entity.setSpec_id(specId);
        entity.setSeason(season);
        entity.setState(specState);
        entity.setEnergyType(energyType);
        entity.setDataVersion(verison);
        entity.setDataFrom(dataFrom);
        entity.setOfficial_range(official_range);
        entity.setIs_max_range(is_max_range);
        entity.setMin_price(min_price);
        entity.setMax_price(max_price);
        entity.setSpec_name(spec_name);
        if(energyBeiliKoubeiMapper.update(entity) <= 0){
            energyBeiliKoubeiMapper.insert(entity);
        }
        return true;
    }

    public int getOfficialRange(int specId){
        List<SpecOfficialRangeEntity> vals;
        if(Spec.isCvSpec(specId)){
            vals = specMapper.getSpecOfficialRange_CV(specId);
        }else{
            vals = specMapper.getSpecOfficialRange(specId);
        }
        if(vals == null || vals.size() == 0)
            return 0;

        Map<Integer,String> map = vals.stream().collect(toMap(x->x.getParamId(),x->x.getParamValue()));
        if(map.containsKey(101)){
            try {
                return Integer.parseInt(map.get(101));
            }catch (Exception e){

            }
        }
        if(map.containsKey(135)){
            try {
                return Integer.parseInt(map.get(135));
            }catch (Exception e){

            }
        }
        if(map.containsKey(75)){
            try {
                return Integer.parseInt(map.get(75));
            }catch (Exception e){

            }
        }
        return 0;
    }

}
