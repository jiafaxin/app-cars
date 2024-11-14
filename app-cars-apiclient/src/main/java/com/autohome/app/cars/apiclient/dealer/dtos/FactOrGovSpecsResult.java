package com.autohome.app.cars.apiclient.dealer.dtos;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class FactOrGovSpecsResult {
    private Integer specId;
    private String name;
    private Integer tax;
    private Integer price;
    private Integer subsidyTax;
    private Integer sumMoney;
    private List<PoliciesDTO> policies;
    private Object seriesName;
    private Object seriesPng;


    static List<Integer> effectivePoliceType = Arrays.asList(4, 5, 8, 10);
    public int getPolicyValueSum(){
        return policies.stream().filter(x->effectivePoliceType.contains(x.getPolicyType())).mapToInt(x->x.policyValue).sum();
    }

    @Data
    public static class PoliciesDTO {
        private Integer policyType;
        private String policyName;
        private Integer policyValue;
        private String prefix;
        private String suffix;
        private Integer smallType;
        private String smallName;
        private List<String> policyEntry;
    }
}
