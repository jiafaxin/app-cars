package com.autohome.app.cars.service.services.dtos;

import com.autohome.app.cars.apiclient.che168.dtos.UsedCarSearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchengtao
 * @date 2024/10/28 16:43
 */
public class UsedCarInfoDto {
    /**
     * 总页数
     */
    private int totalPageCount = 0;

    private String priceInfo = "";
    private List<UsedCarSearchResult.CarDTO> carList = new ArrayList<>();
    private List<UsedCarSearchResult.YearDTO> yearList = new ArrayList<>();
    private Map<Integer, Integer> specYearMinGuidePriceMap = new HashMap<>();

    public UsedCarInfoDto() {
    }

    public UsedCarInfoDto(int totalPageCount, String priceInfo, List<UsedCarSearchResult.CarDTO> carList, List<UsedCarSearchResult.YearDTO> yearList, Map<Integer, Integer> specYearMinGuidePriceMap) {
        this.totalPageCount = totalPageCount;
        this.priceInfo = priceInfo;
        this.carList = carList;
        this.yearList = yearList;
        this.specYearMinGuidePriceMap = specYearMinGuidePriceMap;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public String getPriceInfo() {
        return priceInfo;
    }

    public void setPriceInfo(String priceInfo) {
        this.priceInfo = priceInfo;
    }

    public List<UsedCarSearchResult.CarDTO> getCarList() {
        return carList;
    }

    public void setCarList(List<UsedCarSearchResult.CarDTO> carList) {
        this.carList = carList;
    }

    public List<UsedCarSearchResult.YearDTO> getYearList() {
        return yearList;
    }

    public void setYearList(List<UsedCarSearchResult.YearDTO> yearList) {
        this.yearList = yearList;
    }

    public Map<Integer, Integer> getSpecYearMinGuidePriceMap() {
        return specYearMinGuidePriceMap;
    }

    public void setSpecYearMinGuidePriceMap(Map<Integer, Integer> specYearMinGuidePriceMap) {
        this.specYearMinGuidePriceMap = specYearMinGuidePriceMap;
    }
}
