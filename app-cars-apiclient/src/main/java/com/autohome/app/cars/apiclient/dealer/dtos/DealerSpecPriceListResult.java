package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Data
public class DealerSpecPriceListResult {
    private  int dealerId;
    private  String dealerName;
    private  BigDecimal  latitude;
    private  BigDecimal longitude;
    private  int cityId;
    private  int saleScope;
    private List<SpecPrice> specList =new ArrayList<>();


    @Data
    public static class SpecPrice{
        private int specId;
        private  int price;

        public SpecPrice() {

        }

        public SpecPrice(int specId, int price) {
            this.specId = specId;
            this.price = price;
        }
    }
}
