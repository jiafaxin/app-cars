package com.autohome.app.cars.common.utils;

import com.autohome.app.cars.common.enums.CarLogoSizeEnum;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chengjincheng
 * @date 2024/3/21
 */
public class Constants {

    @SuppressWarnings("serial")
    public static Map<Integer, String> CarLogoSizePrefix = new HashMap<Integer, String>() {
        {
            put(0, "");
            put(1, "u_");
            put(2, "ys_");
            put(3, "cw_");
            put(4, "w_");
            put(5, "k_");
            put(6, "cp_");
            put(7, "tp_");
            put(8, "t_");
            put(9, "m_");
            put(10, "s_");
            put(11, "l_");
        }
    };


    public static String changeLogoSize(String Logo, CarLogoSizeEnum DescSizeEnum) {
        if (StringUtils.isNotEmpty(Logo)) {
            int preFixIndex = -1;
            String preFixStr = "";
            for (int val : CarLogoSizePrefix.keySet()) {
                if (val == 0) {

                } else if (Logo.contains("/" + CarLogoSizePrefix.get(val))) {
                    preFixIndex = val;
                    preFixStr = "/" + CarLogoSizePrefix.get(val);
                    break;
                } else {

                }
            }
            if (preFixIndex > 0) {
                Logo = Logo.replace(preFixStr, "/" + CarLogoSizePrefix.get(DescSizeEnum.getValue()));
            } else {
                Logo = Logo.substring(0, Logo.lastIndexOf("/") + 1) + CarLogoSizePrefix.get(DescSizeEnum.getValue())
                        + Logo.substring(Logo.lastIndexOf("/") + 1);
            }
        }
        return Logo;
    }


    public final static DateTimeFormatter BASIC_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 商用车级别List
     */
    public final static List<Integer> COMMERCIAL_LEVEL_ID = Arrays.asList(11, 12, 13, 14, 25);

    /**
     * 新能源车燃油类型
     */
    public static final List<Integer> NEW_ENERGY_TYPE_LIST = Arrays.asList(4, 5, 6);

    /**
     * 在售车系状态
     */
    public static final List<Integer> ON_SALE_STATE_LIST = Arrays.asList(20, 30);
}
