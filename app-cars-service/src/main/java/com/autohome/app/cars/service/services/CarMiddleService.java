package com.autohome.app.cars.service.services;


import autohome.rpc.car.app_cars.v1.carmiddle.*;
import com.autohome.app.cars.apiclient.recommend.RecommendApiClient;
import com.autohome.app.cars.apiclient.recommend.dtos.*;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.AppIdEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.components.car.*;
import com.autohome.app.cars.service.components.car.dtos.*;
import com.autohome.app.cars.service.components.dealer.SpecCityAskPriceComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SpecCityAskPriceDto;
import com.google.common.collect.Lists;
import com.google.protobuf.ProtocolStringList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarMiddleService {

    @Autowired
    private BrandDetailAllComponent brandDetailAllComponent;

    @Autowired
    private BrandDetailComponent brandDetailComponent;

    @Autowired
    private BrandSeriesComponent brandSeriesComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SeriesSpecComponent seriesSpecComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    private SpecCityAskPriceComponent specCityAskPriceComponent;

    @Autowired
    private RecommendApiClient recommendApiClient;

    /**
     * 获取品牌信息
     * @param request
     * @return
     */
    public BrandInfoAllResponse getBrandInfoAll(BrandInfoAllRequest request){
        BrandInfoAllResponse.Builder builder = BrandInfoAllResponse.newBuilder();
        BrandInfoAllResponse.Result.Builder result = BrandInfoAllResponse.Result.newBuilder();
        List<BrandInfoDto> brandInfoDtoList = brandDetailAllComponent.get();
        //0全部 1在售 2非在售
        int queryType = request.getQueryType();
        //0全部 1新能源 2非新能源
        int energyType = request.getEnergyType();
        if(!CollectionUtils.isEmpty(brandInfoDtoList)){
            //添加AITO品牌
            //addBrand(brandInfoDtoList);
            switch (queryType){
                case 1:
                    brandInfoDtoList = brandInfoDtoList.stream().filter(x -> x.getState() == 1).collect(Collectors.toList());
                    break;
                case 2:
                    brandInfoDtoList = brandInfoDtoList.stream().filter(x -> x.getState() == 0).collect(Collectors.toList());
                    break;
            }
            //新能源
            switch (energyType) {
                case 1:
                    brandInfoDtoList = brandInfoDtoList.stream().filter(x -> x.getIsNewEnergy() == 1).collect(Collectors.toList());
                    break;
                case 2:
                    brandInfoDtoList = brandInfoDtoList.stream().filter(x -> x.getIsNewEnergy() != 1).collect(Collectors.toList());
                    break;
            }
            //分组
            LinkedHashMap<String, ArrayList<BrandInfoDto>> brandInfoMap = brandInfoDtoList.stream().sorted(Comparator.comparing(BrandInfoDto::getLetter).thenComparing(BrandInfoDto::getSort).thenComparing(BrandInfoDto::getName))
                    .collect(Collectors.groupingBy(BrandInfoDto::getLetter, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

            for(Map.Entry<String, ArrayList<BrandInfoDto>> brandMap  : brandInfoMap.entrySet()){
                BrandInfoAllResponse.BrandList.Builder brandList = BrandInfoAllResponse.BrandList.newBuilder();
                String letter = brandMap.getKey();
                brandList.setLetter(letter);
                for(BrandInfoDto brandInfoDto : brandMap.getValue()){
                    BrandInfoAllResponse.BrandInfo.Builder brandInfo = BrandInfoAllResponse.BrandInfo.newBuilder();
                    brandInfo.setBrandId(brandInfoDto.getId());
                    brandInfo.setName(brandInfoDto.getName());
                    if(request.getAppId().equals(AppIdEnum.APP_ID_ENUM_APP.getAppId())){
                        brandInfo.setImgUrl(ImageUtils.convertImage_SizeWebp(CarSettings.getInstance().GetFullImagePath(brandInfoDto.getLogo()), ImageSizeEnum.ImgSize_100x100));
                    }else{
                        brandInfo.setImgUrl(CarSettings.getInstance().GetFullImagePath(brandInfoDto.getLogo()));
                    }
                    brandInfo.setIsNewEnergy(brandInfoDto.getIsNewEnergy());
                    brandList.addList(brandInfo);
                }
                result.addList(brandList);
            }
        }
        return builder.setReturnCode(0).setReturnMsg("成功")
                .setResult(result).build();
    }

    /**
     * 添加AITO 品牌
     *
     */
    private void addBrand(List<BrandInfoDto> brandInfoDtoList){
        BrandInfoDto brandInfo = new BrandInfoDto();
        brandInfo.setId(609);
        brandInfo.setName("AITO");
        brandInfo.setLogo("~/cardfs/series/g31/M0B/EE/71/autohomecar__ChxoHWX85h-AGwuEAABla0Ui0JE217.png");
        brandInfo.setLetter("A");
        brandInfo.setSort(2);
        brandInfo.setIsNewEnergy(1);
        brandInfo.setState(1);
        brandInfoDtoList.add(brandInfo);
    }

    /**
     * 获取车系信息
     * @param request
     * @return
     */
    public SeriesListByBrandIdResponse getSeriesListByBrandId(SeriesListByBrandIdRequest request){
        SeriesListByBrandIdResponse.Builder builder = SeriesListByBrandIdResponse.newBuilder();
        SeriesListByBrandIdResponse.Result.Builder result = SeriesListByBrandIdResponse.Result.newBuilder();
        if(request.getBrandId() <= 0){
            return builder.
                    setReturnCode(102).setReturnMsg("参数错误").build();
        }
        BrandDetailDto brandDetailDto = brandDetailComponent.getById(request.getBrandId());
        if(null == brandDetailDto){
            return builder.
                    setReturnCode(102).setReturnMsg("当前品牌不存在").build();
        }
        int brandId = request.getBrandId();
        boolean isUpdate = brandId == 609;
        if (isUpdate) {
            brandId = 509;
        }
        //品牌信息
        SeriesListByBrandIdResponse.BrandInfo.Builder brandInfo = SeriesListByBrandIdResponse.BrandInfo.newBuilder();
        brandInfo.setBrandId(request.getBrandId());
        brandInfo.setName(isUpdate ? "AITO" : brandDetailDto.getName());
        if(request.getAppId().equals(AppIdEnum.APP_ID_ENUM_APP.getAppId())){
            brandInfo.setImgUrl(ImageUtils.convertImage_SizeWebp(CarSettings.getInstance().GetFullImagePath(brandDetailDto.getLogo()), ImageSizeEnum.ImgSize_100x100));
        }else{
            brandInfo.setImgUrl(CarSettings.getInstance().GetFullImagePath(brandDetailDto.getLogo()));
        }
        result.setBrandInfo(brandInfo);

        BrandSeriesDto brandSeriesDto = brandSeriesComponent.getByBrandId(brandId);

        int queryType = request.getQueryType();
        //获取车系ids
        List<Integer> seriesIds = new ArrayList<>();
        List<BrandSeriesDto.SeriesItem> seriesItems = new ArrayList<>();
         if (brandSeriesDto != null && brandSeriesDto.getFctoryList() != null
                && brandSeriesDto.getFctoryList().size() > 0) {
            brandSeriesDto.getFctoryList().forEach(fct -> {
                if(!CollectionUtils.isEmpty(fct.getSeriesList())){
                    seriesItems.addAll(fct.getSeriesList());
                }
            });
            //0 - 全部, 1 - 在售, 2 - 即将上市, 3 - 停售, 4 - 未上市, 5 - 停产在售, 7 - 未售+在售, 8 - 在售+停售
            switch (queryType){
                case 0 :
                    seriesIds = seriesItems.stream().map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 1 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 2 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 10).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 3 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 40).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 4 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 0).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 5 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 30).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 7 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 20 || x.getState() == 30 || x.getState() == 0).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                case 8 :
                    seriesIds = seriesItems.stream().filter(x -> x.getState() == 20 || x.getState() == 30 || x.getState() == 40).map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
                default:
                    seriesIds = seriesItems.stream().map(BrandSeriesDto.SeriesItem::getId).collect(Collectors.toList());
                    break;
            }
        }

        if(!CollectionUtils.isEmpty(seriesIds)){
            List<SeriesDetailDto> seriesDetailDtoList = new ArrayList<>();
            //获取车系详情信息
            if(seriesIds.size() >= 100){
                List<CompletableFuture<List<SeriesDetailDto>>> seriesDetailTasks = new ArrayList<>();
                List<List<Integer>> seriesIdList = Lists.partition(seriesIds, 100);
                seriesIdList.forEach(dataSeriesIds -> {
                    seriesDetailTasks.add(seriesDetailComponent.getList(dataSeriesIds));
                });
                CompletableFuture.allOf(seriesDetailTasks.toArray(new CompletableFuture[seriesDetailTasks.size()])).join();
                for (CompletableFuture<List<SeriesDetailDto>> seriesDetailTask : seriesDetailTasks) {
                    List<SeriesDetailDto> seriesDetailDtos = seriesDetailTask.join();
                    if(!CollectionUtils.isEmpty(seriesDetailDtos)){
                        seriesDetailDtoList.addAll(seriesDetailDtos);
                    }
                }
            }else{
                seriesDetailDtoList = seriesDetailComponent.getListSync(seriesIds);
            }
            seriesDetailDtoList = seriesDetailDtoList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            int energyType = request.getEnergyType();
            //新能源
            switch (energyType) {
                case 1:
                    seriesDetailDtoList = seriesDetailDtoList.stream().filter(x -> x.getEnergytype() == 1).collect(Collectors.toList());
                    break;
                case 2:
                    seriesDetailDtoList = seriesDetailDtoList.stream().filter(x -> x.getEnergytype() != 1).collect(Collectors.toList());
                    break;
            }
            //品牌63日产 把郑州日产厂商下途达车系，添加一份到东风日产
            this.addFctInfo(brandId,seriesDetailDtoList);
            //在售和即将销售
            List<SeriesDetailDto> fctInfos = seriesDetailDtoList.stream().filter(x -> x.getState() >= 10 && x.getState() <= 30).collect(Collectors.toList());
            //停售
            List<SeriesDetailDto> otherInfos = seriesDetailDtoList.stream().filter(x -> x.getState() == 40).collect(Collectors.toList());

            //即将销售和在售
            if(!CollectionUtils.isEmpty(fctInfos)){
                List<SeriesInfoDetailIDto> seriesInfoDetailIDtos = seriesInfoListSort(fctInfos, brandSeriesDto);
                List<SeriesListByBrandIdResponse.FctInfo> fctInfoList = new ArrayList<>();
                for(SeriesInfoDetailIDto seriesInfoDetailIDto : seriesInfoDetailIDtos){
                    SeriesListByBrandIdResponse.FctInfo.Builder fctInfo = SeriesListByBrandIdResponse.FctInfo.newBuilder();
                    for(SeriesDetailDto seriesDetailDto : seriesInfoDetailIDto.getSeriesDetailDtoList()){
                        SeriesListByBrandIdResponse.SeriesInfo.Builder seriesInfo = SeriesListByBrandIdResponse.SeriesInfo.newBuilder();
                        this.getSeriesInfo(request.getAppId(),seriesInfo,seriesDetailDto);
                        fctInfo.addList(seriesInfo);
                    }
                    fctInfo.setName(seriesInfoDetailIDto.getFctName());
                    fctInfoList.add(fctInfo.build());
                }
                result.addAllList(fctInfoList);
            }
            //停售
            if(!CollectionUtils.isEmpty(otherInfos)){
                List<SeriesInfoDetailIDto> seriesInfoDetailIDtos = seriesInfoListSort(otherInfos, brandSeriesDto);
                List<SeriesListByBrandIdResponse.FctInfo> otherFctInfoList = new ArrayList<>();
                for(SeriesInfoDetailIDto seriesInfoDetailIDto : seriesInfoDetailIDtos){
                    SeriesListByBrandIdResponse.FctInfo.Builder otherFctInfo = SeriesListByBrandIdResponse.FctInfo.newBuilder();
                    for(SeriesDetailDto seriesDetailDto : seriesInfoDetailIDto.getSeriesDetailDtoList()){
                        SeriesListByBrandIdResponse.SeriesInfo.Builder seriesInfo = SeriesListByBrandIdResponse.SeriesInfo.newBuilder();
                        this.getSeriesInfo(request.getAppId(),seriesInfo,seriesDetailDto);
                        otherFctInfo.addList(seriesInfo);
                    }
                    otherFctInfo.setName(seriesInfoDetailIDto.getFctName() + "(停售)");
                    otherFctInfoList.add(otherFctInfo.build());
                }
                result.addAllOtherList(otherFctInfoList);
            }
        }
        return builder.setReturnCode(0).setReturnMsg("成功")
                .setResult(result).build();
    }

    private List<SeriesInfoDetailIDto> seriesInfoListSort(List<SeriesDetailDto> seriesDetailDtos,BrandSeriesDto brandSeriesDto){
        List<SeriesInfoDetailIDto> seriesInfos = new ArrayList<>();
        for (SeriesDetailDto seriesDetailDto : seriesDetailDtos){
            Optional<SeriesInfoDetailIDto> optional = seriesInfos.stream().filter(s -> s.getFctId() == seriesDetailDto.getFctId()).findAny();
            if(optional.isPresent()){
                optional.get().getSeriesDetailDtoList().add(seriesDetailDto);
            }else{
                SeriesInfoDetailIDto seriesInfoDetailIDto = new SeriesInfoDetailIDto();
                seriesInfoDetailIDto.setFctId(seriesDetailDto.getFctId());
                seriesInfoDetailIDto.setFctName(seriesDetailDto.getFctName());
                //排序用
                int seriesPlaceNum = 0;
                if ("国产".equals(seriesDetailDto.getPlace())) {
                    seriesPlaceNum = 1;
                } else if ("合资".equals(seriesDetailDto.getPlace())) {
                    seriesPlaceNum = 2;
                } else if ("进口".equals(seriesDetailDto.getPlace())) {
                    seriesPlaceNum = 4;
                } else if ("独资".equals(seriesDetailDto.getPlace())) {
                    seriesPlaceNum = 3;
                }
                //排序用
                seriesInfoDetailIDto.setPlaceNum(seriesPlaceNum);
                //厂商拼音排序用
                BrandSeriesDto.FactoryItem factoryItem = brandSeriesDto.getFctoryList().stream().filter(x -> x.getId() == seriesInfoDetailIDto.getFctId()).findFirst().orElse(null);
                seriesInfoDetailIDto.setFcPy(null != factoryItem ? factoryItem.getPy() : "");
                List<SeriesDetailDto> tempList = new ArrayList<>();
                tempList.add(seriesDetailDto);
                seriesInfoDetailIDto.setSeriesDetailDtoList(tempList);
                seriesInfos.add(seriesInfoDetailIDto);
            }
        }
        seriesInfos.sort(Comparator.comparing(SeriesInfoDetailIDto::getPlaceNum).thenComparing(SeriesInfoDetailIDto::getFcPy));
        return seriesInfos;
    }


    /**
     * 组装车系信息
     * @param seriesInfo
     * @param seriesDetailDto
     */
    private void getSeriesInfo(String appId,SeriesListByBrandIdResponse.SeriesInfo.Builder seriesInfo,SeriesDetailDto seriesDetailDto){
        seriesInfo.setSeriesId(seriesDetailDto.getId());
        seriesInfo.setName(seriesDetailDto.getName());
        if(appId.equals(AppIdEnum.APP_ID_ENUM_APP.getAppId())){
            seriesInfo.setImgUrl(ImageUtils.convertImageUrl(seriesDetailDto.getPngLogo(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
        }else{
            seriesInfo.setImgUrl(seriesDetailDto.getPngLogo());
        }
        String[] levelArr = CarLevelUtil.getLevelName(seriesDetailDto.getLevelId()).split(",");
        int levelId = Integer.parseInt(levelArr[0]);
        seriesInfo.setLevelId(levelId);
        seriesInfo.setLevelName(seriesDetailDto.getLevelName());
        seriesInfo.setParamIsShow(seriesDetailDto.getParamIsShow() + "");
        seriesInfo.setPrice(seriesDetailDto.getPrice());
    }

    /**
     * 郑州日产173车系4691，需要copy到东风日产92下
     * @param brandId
     * @param seriesDetailDtoList
     */
    private void addFctInfo(int brandId,List<SeriesDetailDto> seriesDetailDtoList){
        //郑州日产173车系4691，需要copy到东风日产92下
        if (brandId == 63) {
            Optional<SeriesDetailDto> first = seriesDetailDtoList.stream().filter(p -> p.getId() == 4691).findFirst();
            if (first.isPresent()) {
                SeriesDetailDto detailDto = new SeriesDetailDto();
                BeanUtils.copyProperties(first.get(), detailDto);
                detailDto.setFctId(92);
                detailDto.setFctName("东风日产");
                seriesDetailDtoList.add(detailDto);
            }
        }

    }

    /**
     * 获取车型信息
     * @param request
     * /// 0 - 全部, 1 - 在售, 2 - 即将上市, 3 - 停售, 4 - 未上市, 5 - 停产在售, 7 - 未售+在售, 8 - 在售+停售(默认)
     * @return
     */
    public SpecListBySeriesIdResponse getSpecListBySeriesId(SpecListBySeriesIdRequest request){
        SpecListBySeriesIdResponse.Builder builder = SpecListBySeriesIdResponse.newBuilder();
        SpecListBySeriesIdResponse.Result.Builder result = SpecListBySeriesIdResponse.Result.newBuilder();
        if(request.getSeriesId() <= 0){
            return builder.
                    setReturnCode(102).setReturnMsg("参数错误").build();
        }
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesId());
        if (seriesDetailDto == null) {
            return builder
                    .setReturnCode(102)
                    .setReturnMsg("当前车系不存在")
                    .build();
        }
        //品牌信息
        SpecListBySeriesIdResponse.BrandInfo.Builder brandInfo = SpecListBySeriesIdResponse.BrandInfo.newBuilder();
        brandInfo.setBrandId(seriesDetailDto.getBrandId());
        brandInfo.setName(seriesDetailDto.getBrandId() == 609 ? "AITO" : seriesDetailDto.getBrandName());
        if(request.getAppId().equals(AppIdEnum.APP_ID_ENUM_APP.getAppId())){
            brandInfo.setImgUrl(ImageUtils.convertImage_SizeWebp(CarSettings.getInstance().GetFullImagePath(seriesDetailDto.getBrandLogo()), ImageSizeEnum.ImgSize_100x100));
        }else{
            brandInfo.setImgUrl(CarSettings.getInstance().GetFullImagePath(seriesDetailDto.getBrandLogo()));
        }
        result.setBrandInfo(brandInfo);
        //车系信息
        SpecListBySeriesIdResponse.SeriesInfo.Builder seriesInfo = SpecListBySeriesIdResponse.SeriesInfo.newBuilder();
        seriesInfo.setSeriesId(seriesDetailDto.getId());
        seriesInfo.setName(seriesDetailDto.getName());
        if(request.getAppId().equals(AppIdEnum.APP_ID_ENUM_APP.getAppId())){
            seriesInfo.setImgUrl(ImageUtils.convertImageUrl(seriesDetailDto.getPngLogo(), true, false, false, ImageSizeEnum.ImgSize_4x3_400x300_Without_Opts));
        }else{
            seriesInfo.setImgUrl(seriesDetailDto.getPngLogo());
        }
        seriesInfo.setPrice(seriesDetailDto.getPrice());
        seriesInfo.setParamIsShow(seriesDetailDto.getParamIsShow() + "");
        seriesInfo.setLevelId(seriesDetailDto.getLevelId());
        seriesInfo.setLevelName(seriesDetailDto.getLevelName());
        result.setSeriesInfo(seriesInfo);
        // 0 - 全部, 1 - 在售, 2 - 即将上市, 3 - 停售, 4 - 未上市, 5 - 停产在售, 7 - 未售+在售, 8 - 在售+停售(默认)
        int queryType = request.getQueryType();
        //0全部 1新能源 2非新能源
        int energyType = request.getEnergyType();
        SeriesSpecDto seriesSpecDto = seriesSpecComponent.get(seriesDetailDto.getId());
        List<Integer> specIds = new ArrayList<>();
        List<Integer> onSaleSpecIds = new ArrayList<>();
        if (seriesSpecDto != null && seriesSpecDto.getItems() != null
                && seriesSpecDto.getItems().size() > 0) {
            //新能源
            List<SeriesSpecDto.Item> items = seriesSpecDto.getItems();
            switch (energyType) {
                case 1:
                    items = seriesSpecDto.getItems().stream().filter(x -> x.getFuelType() >= 4 && x.getFuelType() <= 7).collect(Collectors.toList());
                    break;
                case 2:
                    items = seriesSpecDto.getItems().stream().filter(x -> !(x.getFuelType() >= 4 && x.getFuelType() <= 7)).collect(Collectors.toList());
                    break;
            }
            switch (queryType){
                case 0:
                    specIds = items.stream().map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    onSaleSpecIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                case 1:
                    specIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    onSaleSpecIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                case 2:
                    specIds = items.stream().filter(x -> x.getState() == 10).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                case 3:
                    specIds = items.stream().filter(x -> x.getState() == 40).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                case 4:
                    specIds = items.stream().filter(x -> x.getState() == 0).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                case 5:
                    specIds = items.stream().filter(x -> x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    onSaleSpecIds = items.stream().filter(x -> x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                case 7:
                    specIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30 || x.getState() == 0).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    onSaleSpecIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());

                    break;
                case 8:
                    specIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30 || x.getState() == 40).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    onSaleSpecIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
                default:
                    specIds = items.stream().filter(x -> x.getState() == 40 || x.getState() == 30 || x.getState() == 20).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    onSaleSpecIds = items.stream().filter(x -> x.getState() == 20 || x.getState() == 30).map(SeriesSpecDto.Item::getId).collect(Collectors.toList());
                    break;
            }
        }
        //大于100 分批处理
        List<SpecDetailDto> specDetailDtos = new ArrayList<>();
        List<SpecCityAskPriceDto> specCityAskPriceDtos = new ArrayList<>();
        if(specIds.size() >= 100){
            List<CompletableFuture<List<SpecDetailDto>>> specDetailTasks = new ArrayList<>();
            List<List<Integer>> specIdList = Lists.partition(specIds, 100);
            specIdList.forEach(dataSpecIds -> {
                specDetailTasks.add(specDetailComponent.getList(dataSpecIds));
            });
            if(request.getCityId() > 0){
                CompletableFuture<List<SpecCityAskPriceDto>> specCityAskPriceTask = specCityAskPriceComponent.get(onSaleSpecIds, request.getCityId());
                specCityAskPriceDtos = specCityAskPriceTask.join().stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
            CompletableFuture.allOf(specDetailTasks.toArray(new CompletableFuture[specDetailTasks.size()])).join();
            for (CompletableFuture<List<SpecDetailDto>> specTask : specDetailTasks) {
                List<SpecDetailDto> specDetailDtoList = specTask.join();
                if(!CollectionUtils.isEmpty(specDetailDtoList)){
                    specDetailDtos.addAll(specDetailDtoList);
                }
            }
        }else{
            List<CompletableFuture> tasks = new ArrayList<>();
            AtomicReference<List<SpecDetailDto>> specDetailTask = new AtomicReference<>();
            AtomicReference<List<SpecCityAskPriceDto>> specCityAskPriceTask = new AtomicReference<>();
            tasks.add(specDetailComponent.getList(specIds).thenAccept(x -> {
                specDetailTask.set(x);
            }));
            if(request.getCityId() > 0){
                tasks.add(specCityAskPriceComponent.get(onSaleSpecIds,request.getCityId()).thenAccept(x -> {
                    specCityAskPriceTask.set(x);
                }));
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
            //车型详情信息
            specDetailDtos = specDetailTask.get();
            //车型经销商报价信息
            if(request.getCityId() > 0){
                specCityAskPriceDtos = specCityAskPriceTask.get().stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
        }

        List<SpecGroupDto> specGroupDtos = new ArrayList<>();
        for(SpecDetailDto specDetailDto : specDetailDtos){
            int isShow = specDetailDto.isImageSpec() ? 0 : specDetailDto.getParamIsShow();
            String groupName = getGroupName(specDetailDto.getState(), specDetailDto.getDisplacement(), specDetailDto.getFlowModeName(), specDetailDto.getEnginePower(),
                    specDetailDto.getYearName(), isShow, specDetailDto.getFuelType(),specDetailDto.getElectricKw());
            Optional<SpecGroupDto> optional = specGroupDtos.stream().filter(s -> s.getName().equals(groupName)).findAny();
            if(optional.isPresent()){
                optional.get().getSpecDetailDtos().add(specDetailDto);
            }else{
                List<SpecDetailDto> tempList = new ArrayList<>();
                tempList.add(specDetailDto);
                SpecGroupDto specGroupDto = new SpecGroupDto();
                //全称
                specGroupDto.setName(groupName);
                if (groupName.split("@").length > 2) {
                    specGroupDto.setJinQiFS(groupName.split("@")[1]);
                    specGroupDto.setPaiLiang(groupName.split("@")[0]);
                    specGroupDto.setMaLi(groupName.split("@")[2]);
                } else {
                    specGroupDto.setJinQiFS("0");
                    specGroupDto.setPaiLiang("0");
                    specGroupDto.setMaLi("0");
                }
                //全部
                if (queryType == 0) {
                    specGroupDto.setType(groupName.contains("售") ? (groupName.contains("停售") ? "0x0010" : "0x0002") : "0x000c");
                } else {
                    specGroupDto.setType(groupName.contains("停售") ? "0x0010" : "0x000c");
                }
                if(queryType == 2){
                    specGroupDto.setType("0x0002");
                }
                specGroupDto.setSpecDetailDtos(tempList);
                //去掉占位符的
                specGroupDto.setGroupName(groupName.replace("@", ""));
                specGroupDtos.add(specGroupDto);
            }
        }
        specGroupDtos.sort(Comparator
                .comparing(SpecGroupDto::getType)// 销售状态;965按照销售状态排序;
                .thenComparing(SpecGroupDto::getMaLi)// 马力;
                .thenComparing(SpecGroupDto::getJinQiFS) // 进气方式;
                .thenComparing(SpecGroupDto::getPaiLiang));// 排量;
        //在售的
        List<SpecGroupDto> specList = null;
        if(queryType == 0){
            specList = specGroupDtos.stream().filter(x -> !x.getGroupName().contains("售")).collect(Collectors.toList());
        }else{
            specList = specGroupDtos;
        }
        for(SpecGroupDto specGroupDto : specList){
            //pc 端要求同组内按照价格从低到高排序
            if(AppIdEnum.APP_ID_ENUM_PC.getAppId().equals(request.getAppId())){
                specGroupDto.getSpecDetailDtos().sort(Comparator.comparing(item -> item.getMinPrice() == 0 ? 999999999 : item.getMinPrice()));
            }
            SpecListBySeriesIdResponse.SpecInfo.Builder specInfo = SpecListBySeriesIdResponse.SpecInfo.newBuilder();
            for(SpecDetailDto specDetailDto : specGroupDto.getSpecDetailDtos()){
                SpecListBySeriesIdResponse.SpecDetail.Builder specDetail = SpecListBySeriesIdResponse.SpecDetail.newBuilder();
                this.getSpecDetail(request.getAppId(),specDetail,specDetailDto,specCityAskPriceDtos);
                specInfo.addList(specDetail);
            }
            specInfo.setName(specGroupDto.getGroupName());
            result.addList(specInfo);
        }
        //未售，停售，即将销售
        List<SpecGroupDto> otherSpecList = null;
        if(queryType == 0){
            otherSpecList = specGroupDtos.stream().filter(x -> x.getGroupName().contains("售")).sorted(Comparator.comparing(SpecGroupDto::getGroupName,Comparator.reverseOrder())).collect(Collectors.toList());
        }else {
            otherSpecList = new ArrayList<>();
        }
        for(SpecGroupDto specGroupDto : otherSpecList){
            //pc 端要求同组内按照价格从低到高排序
            if(AppIdEnum.APP_ID_ENUM_PC.getAppId().equals(request.getAppId())){
                specGroupDto.getSpecDetailDtos().sort(Comparator.comparing(item -> item.getMinPrice() == 0 ? 999999999 : item.getMinPrice()));
            }
            SpecListBySeriesIdResponse.SpecInfo.Builder specInfo = SpecListBySeriesIdResponse.SpecInfo.newBuilder();
            for(SpecDetailDto specDetailDto : specGroupDto.getSpecDetailDtos()){
                SpecListBySeriesIdResponse.SpecDetail.Builder specDetail = SpecListBySeriesIdResponse.SpecDetail.newBuilder();
                this.getSpecDetail(request.getAppId(),specDetail,specDetailDto,specCityAskPriceDtos);
                specInfo.addList(specDetail);
            }
            specInfo.setName(specGroupDto.getGroupName());
            result.addOtherList(specInfo);
        }

        return builder.setReturnCode(0).setReturnMsg("成功")
                .setResult(result).build();
    }


    /**
     * 组装车型信息
     * @param specDetail
     * @param specDetailDto
     * @param specCityAskPriceDtos
     */
    private void getSpecDetail(String appId,SpecListBySeriesIdResponse.SpecDetail.Builder specDetail,SpecDetailDto specDetailDto,List<SpecCityAskPriceDto> specCityAskPriceDtos){
        specDetail.setSpecId(specDetailDto.getSpecId());
        specDetail.setName(specDetailDto.getSpecName());
        specDetail.setPrice(PriceUtil.GetPriceStringDetail(specDetailDto.getMinPrice(), specDetailDto.getMaxPrice(), specDetailDto.getState()));
        if(appId.equals(AppIdEnum.APP_ID_ENUM_APP.getAppId())){
            if(specDetailDto.getState() == 0){
                specDetail.setPrice("暂无报价");
            }
            specDetail.setImgUrl(CommonHelper.ChangeLogoSize(8, specDetailDto.getLogo()));
        }else{
            specDetail.setImgUrl(specDetailDto.getLogo());
        }
        int isShow = specDetailDto.isImageSpec() ? 0 : specDetailDto.getParamIsShow();
        specDetail.setParamIsShow(isShow + "");
        if (specDetailDto.getState() == 10) {
            if (specDetailDto.isBooked()) {
                specDetail.setPriceTip("订金: ");
            } else {
                specDetail.setPriceTip("预售价: ");
            }
        } else {
            specDetail.setPriceTip("厂商指导价: ");
        }
        if (specDetailDto.getMaxPrice() == 0) {
            specDetail.setPriceTip("厂商指导价: ");
            specDetail.setPrice("暂无报价");
        }
        specDetail.setDescription((specDetailDto.getDrivingModeName() == null ? "" : specDetailDto.getDrivingModeName()) + " "
                + (specDetailDto.getTransmission() == null ? "" : specDetailDto.getTransmission()));
        if (specDetail.getDescription() == null || specDetail.getDescription().trim().isEmpty()) {
            specDetail.setDescription("-");
        }
        //经销商报价
        SpecCityAskPriceDto askPriceDto = specCityAskPriceDtos.stream().filter(x -> x.getSpecId() == specDetailDto.getSpecId()).findFirst().orElse(null);
        if(null != askPriceDto && 0 != askPriceDto.getMinPrice()){
            specDetail.setDealerPrice(PriceUtil.getPriceDetailInfo(askPriceDto.getMinPrice()));
        }
        //差价
        if(specDetailDto.getMinPrice() != 0 && specDetailDto.getMinPrice() != 0 && null != askPriceDto && askPriceDto.getMinPrice() != 0){
            int downMinPrice = specDetailDto.getMinPrice() - askPriceDto.getMinPrice();
            int downMaxPrice = specDetailDto.getMaxPrice() - askPriceDto.getMinPrice();
            specDetail.setDownPrice(PriceUtil.priceDetailForamt(downMinPrice,downMaxPrice));
        }
        //pc 端用
        specDetail.setYear(specDetailDto.getYearName());
        specDetail.setState(specDetailDto.getState());
        if(specDetailDto.getState() == 0){
            specDetail.setTagTitle("即将销售");
        }
    }

    /**
     * 获取分组名称
     * @param state
     * @param displacement
     * @param flowModeName
     * @param enginePower
     * @param year
     * @param paramIsShow
     * @param fuelType
     * @return
     */
    private String getGroupName(int state, BigDecimal displacement, String flowModeName,
                                int enginePower, int year, int paramIsShow, int fuelType,double electricKw){
        String key = "";
        switch (state) {
            case 0:// 未售;
                key = "未售";
                break;
            case 10:// 待售;
                key = "即将销售";
                break;
            case 20:// 在产在售;
            case 30:// 停产在售;
                if (paramIsShow == 0) {
                    key = "暂无参数配置";
                } else {
                    if(fuelType == 4 || fuelType == 7){
                        key = String.format("电动机 %s马力", Math.round(((int) electricKw) * 1.36));
                    }else{
                        key = displacement + "升 @" + flowModeName + " @" + enginePower + "马力";
                    }
                }
                break;
            case 40:// 停售;
                key = year + "款(停售)";
                break;
        }
        return key;
    }

    public RecommendationListResponse getBrandTopRecommendation(RecommendationListRequest request) {
        RecommendationListResponse.Builder builder = RecommendationListResponse.newBuilder();
        RecommendationListResponse.Result.Builder result = RecommendationListResponse.Result.newBuilder();
        RecommendationListResponse.Result.Item.Builder itemBuilder = RecommendationListResponse.Result.Item.newBuilder();
        RecommendationListResponse.Result.Data.Builder dataBuilder = RecommendationListResponse.Result.Data.newBuilder();
        BaseModel<PkResultInfoDto> model = this.getRecommendPk(request);
        if (null != model && 0 == model.getReturncode() && null != model.getResult()) {
            //少于3条不返回，多余3条只取3条的逻辑
            if (model.getResult().getData().size() >= 3) {
                List<SeriesSpecPair> seriesSpecPairList = model.getResult().getData().subList(0, 3);
                List<Integer> seriesIdList = seriesSpecPairList.stream().map(ss -> ss.getSeries_id()).collect(Collectors.toList());
                List<Integer> specidList = seriesSpecPairList.stream().map(ss -> ss.getSpec_id()).collect(Collectors.toList());

                List<SeriesDetailDto> seriesList = seriesDetailComponent.getList(seriesIdList).join();
                List<SpecDetailDto> specDetailDtoList = specDetailComponent.getList(specidList).join();

                Map<Integer, SeriesDetailDto> seriesMap = seriesList.stream().collect(Collectors.toMap(SeriesDetailDto::getId, s -> s, (k1, k2) -> k1));
                seriesSpecPairList.forEach(p -> {
                    if (!CollectionUtils.isEmpty(specDetailDtoList)) {
                        SpecDetailDto specDetailDto = specDetailDtoList.stream().filter(x -> x.getSpecId() == p.getSpec_id().intValue()).findFirst().orElse(null);
                        if (null != specDetailDto) {
                            RecommendationListResponse.Result.CarCompareInfo.Builder info = RecommendationListResponse.Result.CarCompareInfo.newBuilder();
                            SeriesDetailDto seriesCache = seriesMap.get(p.getSeries_id());
                            info.setSeriesId(seriesCache.getId());
                            info.setSeriesName(StringEscapeUtils.unescapeHtml4(seriesCache.getName()));
                            info.setImage(ImageUtils.convertImage_ToWebp(CommonHelper.ChangeLogoSize(12, seriesCache.getPngLogo())));
                            info.setFctText("指导价:");

                            info.setSpecId(specDetailDto.getSpecId());
                            info.setSpecName(specDetailDto.getSpecName());
                            info.setEnergyType((1 == seriesCache.getEnergytype() ? "新能源" : "燃油车") + "/" + specDetailDto.getLevelName());

                            info.setSpecPrice(CommonHelper.getPriceInfoForDiffConfig(specDetailDto.getMinPrice()));

                            info.setBtnTitle("加入对比");

                            info.setStra(model.getResult().getPvdata());
                            dataBuilder.addList(info);
                        }
                    }
                });

            }
        } else {
            log.warn("getRecommendPk error");
        }
        itemBuilder.setType(100);
        itemBuilder.setData(dataBuilder);
        result.addItem(itemBuilder);
        return builder.setReturnCode(0).setReturnMsg("成功")
                .setResult(result).build();

    }

    /**
     * 调用第三方PK选择品牌接口，获取数据
     *
     * @param request
     * @return
     */
    private BaseModel<PkResultInfoDto> getRecommendPk(RecommendationListRequest request) {
        List<SeriesSpecPair> seriesSpecPairs = new ArrayList<>();
        ProtocolStringList seriesIdList = request.getSeriesIdList();
        seriesIdList.forEach(id -> {
            List<String> pair = Arrays.asList(id.split("-"));
            seriesSpecPairs.add(new SeriesSpecPair(Integer.valueOf(pair.get(0)), Integer.valueOf(pair.get(1))));
        });
        //组装请求体参数
        RecommendPkParam param = new RecommendPkParam();
        param.setRid(request.getRequestPid());
        param.setDevice_id(request.getDeviceId());
        param.setSeries(seriesSpecPairs);
        param.setDevice_type(1 == request.getPm() ? "ios" : "android");
        if (request.getUid() > 0) {
            param.setUid(String.valueOf(request.getUid()));
        }
        param.setSource(1);
        param.setNet_state(request.getNetState());
        param.setCity_id(request.getCityId());
        //发送请求拿到结果
        CompletableFuture<BaseModel<PkResultInfoDto>> future = recommendApiClient.getRecommendPk(param);
        BaseModel<PkResultInfoDto> result = future.join();
        return result;

    }

}
