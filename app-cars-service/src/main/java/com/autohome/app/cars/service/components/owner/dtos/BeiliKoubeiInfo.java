package com.autohome.app.cars.service.components.owner.dtos;

import com.autohome.app.cars.mapper.appcars.entities.BeiliKoubeiEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class BeiliKoubeiInfo {

    int kb_zone;
    String bl_zone;
    int seriesId;

    List<SeasonDetail> seasons;

    @Data
    public static class SeasonDetail{
        //1口碑，2北理
        int type;
        int season;
        BigDecimal drive_range;
        BigDecimal energy_cost;

        public String driveRangeStr(){
            return drive_range.intValue() + "km";
        }

        public String cost() {
            if(energy_cost == null || energy_cost.compareTo(new BigDecimal(0))<=0){
                return "";
            }
            return energy_cost.multiply(BigDecimal.valueOf(0.58)).setScale(1, RoundingMode.HALF_DOWN) + "元";
        }
    }

    /**
     * 获取四季续航最长数据，车系综述页糖豆可以使用
     * @return
     */
    public SeasonDetail getDefault(){
        if(seasons==null||seasons.size() == 0)
            return null;
        return seasons.stream().sorted(Comparator.comparing(SeasonDetail::getDrive_range).reversed()).findFirst().orElse(null);
    }

    public void remove(int season){
        if(seasons==null||seasons.size()==0){
            return;
        }
        seasons.removeIf(x->x.getSeason()==season);
    }

}
