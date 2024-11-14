package com.autohome.app.cars.apiclient.openApi.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SeriesAttentionResult {
    int returncode;

    Result result;

    @Data
    public static class Result{
        List<SpecItem> speclist;
    }
    @Data
    public static class SpecItem{
        int specid;
        int attention;
    }
}
