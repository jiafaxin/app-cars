package com.autohome.app.cars.provider.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.BasePageModel;
import com.autohome.app.cars.mapper.popauto.SeriesMapper;
import com.autohome.app.cars.mapper.popauto.entities.PicColorEntity;
import com.autohome.app.cars.mapper.popauto.entities.SeriesEntity;
import com.autohome.app.cars.service.common.PageOf;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.car.dtos.paramconfig.SpecOutInnerColorDto;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.ColorStatisticsDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecGroupOfSeriesDto;
import com.autohome.app.cars.service.components.newcar.SubscribeNewsHistoryData;
import com.autohome.app.cars.service.components.hqpic.HqPicDataComponent;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicDataDto;
import com.autohome.app.cars.service.components.recrank.attention.AreaSeriesAttentionComponent;
import com.autohome.app.cars.service.components.recrank.sale.RankSaleMonthComponent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    ColorComponent colorComponent;
    @Autowired
    SpecYearNewComponent specYearNewComponent;
    @Autowired
    SpecDetailComponent specDetailComponent;
    @Autowired
    SpecOutInnerColorComponent specColorComponent;

    @Autowired
    CarPhotoComponent carPhotoComponent;

    @Autowired
    private SubscribeNewsHistoryData subscribeNewsHistoryData;

    @Autowired
    private HqPicDataComponent hqPicDataComponent;

    @Autowired
    RankSaleMonthComponent rankSaleMonthComponent;


    @Autowired
    private SeriesMapper seriesMapper;
    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @GetMapping("/gethq")
    public HqPicDataDto gethq(int seriesId) {
        return hqPicDataComponent.get(seriesId);
    }

    @GetMapping("/getcolors")
    public List<ColorStatisticsDto> getColors(int type, int seriesId, int specId, String classIds) {
        List<Integer> cids = StringUtils.isBlank(classIds) ? new ArrayList<>() : Arrays.stream(classIds.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        return colorComponent.getColors(type, seriesId, specId, cids).join();
    }

    @GetMapping("/getpic")
    public PageOf<CarPhotoDto> getColors(int seriesId, int specId, int colorId, int innerColorId, String classIds, int page, int size) {
        List<Integer> cids = StringUtils.isBlank(classIds) ? new ArrayList<>() : Arrays.stream(classIds.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        return carPhotoComponent.get(seriesId, specId, colorId, innerColorId, cids, page, size, 0, 0).join();
    }

    @GetMapping("/getpicother")
    public PageOf<CarPhotoDto> getColorsOther(int seriesId, int specId, int specState, int colorId, int innerColorId, int isinner, String otherColors, String classIds, int page, int size) {
        List<Integer> cids = StringUtils.isBlank(classIds) ? new ArrayList<>() : Arrays.stream(classIds.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        List<Integer> otherids = StringUtils.isBlank(otherColors) ? new ArrayList<>() : Arrays.stream(otherColors.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        return carPhotoComponent.getWithOtherSpec(seriesId, specId, specState, colorId, innerColorId, isinner, otherids, cids, page, size, 0, 0).join();
    }

    @GetMapping("/getspeccolors")
    public SpecOutInnerColorDto getSpecColors(int specId, int isinner) {
        return specColorComponent.get(specId, isinner == 1).join();
    }

    @GetMapping(value = "/getSpecYearNewComponent", produces = "application/json;charset=UTF-8")
    public BaseModel getspeclist(int seriesId,
                                 @RequestParam(value = "refresh", required = false, defaultValue = "0") Integer refresh) {
        if (refresh > 0) {
            specYearNewComponent.refresh(seriesId);
        }
        List<SpecGroupOfSeriesDto> specGroupOfSeriesDtos = specYearNewComponent.get(seriesId);
        specGroupOfSeriesDtos.forEach(x -> {
            x.getYearspeclist().forEach(p -> {
                List<Integer> specIds = p.getSpeclist().stream().map(SpecGroupOfSeriesDto.Spec::getSpecId).collect(Collectors.toList());
                Map<Integer, SpecDetailDto> collect = specDetailComponent.mGet(specIds).stream().filter(Objects::nonNull).collect(Collectors.toMap(SpecDetailDto::getSpecId, Function.identity(), (k1, k2) -> k2));
                p.getSpeclist().forEach(spec -> {
                    spec.setDetails(collect.get(spec.getSpecId()));
                });
            });
        });
        return new BaseModel(specGroupOfSeriesDtos);
    }

    @GetMapping("/pricedownjob")
    public BaseModel<Integer> priceDownJob() {
        subscribeNewsHistoryData.refreshPriceDown(System.out::println);
        return new BaseModel<>(1);
    }

    //rankSaleMonthComponent.getLatestMonthDataList
    @GetMapping("/ranksalemonthcomponent/getlatestmonthdatalist")
    public BaseModel getLatestMonthDataList(int pageIndex, int pageSize, String ids) {
        List<Integer> cids = StringUtils.isBlank(ids) ? new ArrayList<>() : Arrays.stream(ids.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        BasePageModel latestMonthDataList = rankSaleMonthComponent.getLatestMonthDataList(pageIndex, pageSize, cids,1);
        return new BaseModel<>(latestMonthDataList);
    }

    @Autowired
    AreaSeriesAttentionComponent areaSeriesAttentionComponent;

    @GetMapping("/AreaSeriesAttentionComponent/getSeriesDataList")
    public BaseModel getSeriesDataList(int pageIndex,
                                       int pageSize,
                                       int provinceId,
                                       String ids,
                                       int energyType) {
        List<Integer> cids = StringUtils.isBlank(ids) ? new ArrayList<>() : Arrays.stream(ids.split(",")).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        BasePageModel latestMonthDataList = areaSeriesAttentionComponent.getSeriesDataList(pageIndex, pageSize, provinceId, cids, energyType,1);
        return new BaseModel<>(latestMonthDataList);
    }

    @GetMapping("/getExistSeriesAndSpec")
    public String getExistSeriesAndSpec() {
        List<String> strings = new ArrayList<>();
        List<SeriesEntity> allSeries = seriesMapper.getAllSeries();
        for (SeriesEntity series : allSeries) {
            SeriesDetailDto detailDto = seriesDetailComponent.get(series.getId());
            if (Objects.nonNull(detailDto)) {
                SpecDetailDto spec = specDetailComponent.getSync(detailDto.getMinPriceSpecId());
                if (Objects.nonNull(spec)) {
                    System.out.println("seriesId:" + series.getId() + " specId:" + spec.getSpecId());
                    strings.add("seriesId:" + series.getId() + " ,specId:" + spec.getSpecId() + ", fuleType:" + spec.getFuelType() + ", levelId:"+ spec.getLevelId());
                }
            }
        }
        return JSONArray.toJSONString(strings);
    }
}
