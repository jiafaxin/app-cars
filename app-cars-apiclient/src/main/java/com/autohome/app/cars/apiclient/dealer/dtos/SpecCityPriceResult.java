package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjincheng
 * @date 2024/2/28
 */
@Data
public class SpecCityPriceResult {

    private ArrayList<DealerSpecCanAskPrice_DealerItem> list;

    @Data
    public static class DealerSpecCanAskPrice_DealerItem {
        private int newsPrice;
        private double minOriginalPrice;
    }

}
