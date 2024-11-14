package com.autohome.app.cars.service.services;

import autohome.rpc.car.app_cars.v1.subsidy.SubsidyPolicyReportResponse;
import autohome.rpc.car.app_cars.v1.subsidy.SubsidyPolicyReportResponse.Pvitem;
import com.alibaba.fastjson2.JSON;
import com.autohome.app.cars.common.enums.CarSellTypeEnum;
import com.autohome.app.cars.common.utils.CityUtil;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.common.utils.DateUtil;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SpecDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.subsidy.SpecCitySubsidyComponent;
import com.autohome.app.cars.service.components.subsidy.SubsidyPolicyCommonComponent;
import com.autohome.app.cars.service.components.subsidy.SubsidyPolicyEntryComponent;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecCitySubsidyDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SpecSubsidyCountDto;
import com.autohome.app.cars.service.components.subsidy.dtos.SubsidyPolicyEntryDto;
import com.autohome.app.cars.service.components.subsidy.enums.SubsidyGroupTypeEnum;
import com.autohome.app.cars.service.services.dtos.Card30104Data;
import com.google.protobuf.Any;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chengjincheng
 * @date 2024/5/28
 */
@Component
public class SubsidyPolicyService {

    @Autowired
    private SpecCitySubsidyComponent specCitySubsidyComponent;

    @Autowired
    private SpecDetailComponent specDetailComponent;

    @Autowired
    private SeriesDetailComponent seriesDetailComponent;

    @Autowired
    private SubsidyPolicyCommonComponent subsidyPolicyCommonComponent;

    @Autowired
    private SubsidyPolicyEntryComponent subsidyPolicyEntryComponent;

    @Autowired
    private SeriesCityAskPriceNewComponent seriesCityAskPriceNewComponent;

    public CompletableFuture<SubsidyPolicyReportResponse.Result.Builder> getResult(int seriesId,
                                                                                   int specId,
                                                                                   int cityId,
                                                                                   String eid,
                                                                                   String businessid,
                                                                                   String ordertype) {
        // 获取车型列表
        List<SpecSubsidyCountDto> specSubsidyList = subsidyPolicyCommonComponent.getAllSpecBySeries(seriesId, cityId);
        if (specId == 0) {
            // 会不会有传入了specId，但在list里没有的情况？理论上不会,list里都没有的车型，不应该展示入口
            if (CollectionUtils.isEmpty(specSubsidyList)) {
                return CompletableFuture.completedFuture(null);
            } else {
                specId = specSubsidyList.get(0).getSpecId();
            }
        }

        // 异步获取相关信息
        List<CompletableFuture> preparationTasks = new ArrayList<>();
        AtomicReference<List<SpecDetailDto>> SpecDetailListRef = new AtomicReference<>();
        AtomicReference<SpecCitySubsidyDto> SpecCitySubsidyRef = new AtomicReference<>();
        AtomicReference<SeriesDetailDto> seriesDetailDtoRef = new AtomicReference<>();
        AtomicReference<SubsidyPolicyEntryDto> subsidyPolicyEntryDtoRef = new AtomicReference<>();
        AtomicReference<SeriesCityAskPriceDto> seriesCityAskPriceDtoRef = new AtomicReference<>();
        preparationTasks.add(specDetailComponent.getList(specSubsidyList.stream()
                        .filter(Objects::nonNull).map(SpecSubsidyCountDto::getSpecId).toList())
                .thenAccept(SpecDetailListRef::set).exceptionally(e -> null));
        preparationTasks.add(specCitySubsidyComponent.get(specId, cityId)
                .thenAccept(SpecCitySubsidyRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesDetailComponent.getAsync(seriesId)
                .thenAccept(seriesDetailDtoRef::set).exceptionally(e -> null));
        preparationTasks.add(subsidyPolicyEntryComponent.get(seriesId, specId, cityId)
                .thenAccept(subsidyPolicyEntryDtoRef::set).exceptionally(e -> null));
        preparationTasks.add(seriesCityAskPriceNewComponent.get(seriesId, cityId)
                .thenAccept(seriesCityAskPriceDtoRef::set).exceptionally(e -> null));
        CompletableFuture.allOf(preparationTasks.toArray(new CompletableFuture[0])).join();

        SubsidyPolicyReportResponse.Result.Builder resultBuilder = SubsidyPolicyReportResponse.Result.newBuilder();
        SeriesDetailDto seriesDetailDto = seriesDetailDtoRef.get();
        if (Objects.isNull(seriesDetailDto)) {
            return CompletableFuture.completedFuture(null);
        }
        // 填充基本信息
        resultBuilder.setBaseinfo(getBaseInfoBuilder(seriesDetailDto, SpecDetailListRef.get(), seriesId, specId, eid,
                businessid, ordertype));
        // 填充卡片信息
        resultBuilder.addAllCardlist(getAllCardList(seriesDetailDto, SpecDetailListRef.get(), SpecCitySubsidyRef.get(),
                subsidyPolicyEntryDtoRef.get(), specSubsidyList, seriesCityAskPriceDtoRef.get(), specId));
        return CompletableFuture.completedFuture(resultBuilder);
    }

    /**
     * 填充基本信息
     *
     * @param seriesDetailDto
     * @param specDetailDtoList
     * @param seriesId
     * @param specId
     * @param eid
     * @return
     */
    private SubsidyPolicyReportResponse.Result.BaseInfo.Builder getBaseInfoBuilder(SeriesDetailDto seriesDetailDto,
                                                                                   List<SpecDetailDto> specDetailDtoList,
                                                                                   int seriesId,
                                                                                   int specId,
                                                                                   String eid,
                                                                                   String businessid,
                                                                                   String ordertype) {
        SubsidyPolicyReportResponse.Result.BaseInfo.Builder baseInfoBuilder =
                SubsidyPolicyReportResponse.Result.BaseInfo.newBuilder();
        baseInfoBuilder.setPagetitle(seriesDetailDto.getName() + "全部优惠");
        baseInfoBuilder.setSeriesid(seriesId);
        baseInfoBuilder.setSeriesname(seriesDetailDto.getName());
        baseInfoBuilder.setSpecid(specId);
        SpecDetailDto specDetailDto = null;
        int finalSpecId = specId;
        if (!CollectionUtils.isEmpty(specDetailDtoList)) {
            specDetailDto = specDetailDtoList.stream().filter(Objects::nonNull)
                    .filter(e -> e.getSpecId() == finalSpecId).findFirst()
                    .orElse(null);
        }
        baseInfoBuilder.setSpecname(Objects.nonNull(specDetailDto) ? specDetailDto.getSpecName() : "");
        baseInfoBuilder.setEid(eid);
        baseInfoBuilder.setAskpricescheme(genAskPriceScheme(seriesId, specId, eid, businessid, ordertype));
        baseInfoBuilder.addAllExplain(List.of("1、活动内容包括品牌活动、政府购车优惠政策补贴。",
                "2、因线上的优惠信息可能无法实时更新，仅供参考，实际以品牌和地区的实际信息为准，详情请咨询当地经销商。"));
        return baseInfoBuilder;
    }


    /**
     * 获取所有卡片信息
     *
     * @param seriesDetailDto
     * @param specDetailDtoList
     * @param specCitySubsidyDto
     * @param subsidyPolicyEntryDto
     * @param specSubsidyList
     * @param selectSpecId
     * @return
     */
    private List<SubsidyPolicyReportResponse.Result.CardList> getAllCardList(SeriesDetailDto seriesDetailDto,
                                                                             List<SpecDetailDto> specDetailDtoList,
                                                                             SpecCitySubsidyDto specCitySubsidyDto,
                                                                             SubsidyPolicyEntryDto subsidyPolicyEntryDto,
                                                                             List<SpecSubsidyCountDto> specSubsidyList,
                                                                             SeriesCityAskPriceDto seriesCityAskPriceDto,
                                                                             int selectSpecId) {
        // 异步组装卡片
        List<CompletableFuture> cardTasks = new ArrayList<>();
        AtomicReference<SubsidyPolicyReportResponse.Result.Card30101.Builder> card30101Ref = new AtomicReference<>();
        AtomicReference<SubsidyPolicyReportResponse.Result.Card30102.Builder> card30102Ref = new AtomicReference<>();
        AtomicReference<SubsidyPolicyReportResponse.Result.Card30103.Builder> card30103Ref = new AtomicReference<>();
        AtomicReference<SubsidyPolicyReportResponse.Result.Card30105.Builder> card30105RefFinancial =
                new AtomicReference<>();
        AtomicReference<SubsidyPolicyReportResponse.Result.Card30105.Builder> card30105RefOwner =
                new AtomicReference<>();

        cardTasks.add(buildCard30101(seriesDetailDto, selectSpecId, specDetailDtoList, specSubsidyList,
                seriesCityAskPriceDto).exceptionally(e -> null)
                .thenAccept(card30101Ref::set).exceptionally(e -> null));
        cardTasks.add(buildCard30102(subsidyPolicyEntryDto).exceptionally(e -> null)
                .thenAccept(card30102Ref::set).exceptionally(e -> null));
        cardTasks.add(buildCard30103(specCitySubsidyDto).exceptionally(e -> null)
                .thenAccept(card30103Ref::set).exceptionally(e -> null));
        cardTasks.add(buildCard30105Financial(specCitySubsidyDto).exceptionally(e -> null)
                .thenAccept(card30105RefFinancial::set).exceptionally(e -> null));
        cardTasks.add(buildCard30105Owner(specCitySubsidyDto).exceptionally(e -> null)
                .thenAccept(card30105RefOwner::set).exceptionally(e -> null));

        CompletableFuture.allOf(cardTasks.toArray(new CompletableFuture[0])).join();

        List<SubsidyPolicyReportResponse.Result.CardList> cardListList = new ArrayList<>();
        if (Objects.nonNull(card30101Ref.get())) {
            SubsidyPolicyReportResponse.Result.CardList.Builder cardList30101 =
                    SubsidyPolicyReportResponse.Result.CardList.newBuilder();
            cardList30101.setType(30101);
            cardList30101.setData(Any.pack(card30101Ref.get().build()));
            cardListList.add(cardList30101.build());
        }
        if (Objects.nonNull(card30102Ref.get())) {
            SubsidyPolicyReportResponse.Result.CardList.Builder cardList30102 =
                    SubsidyPolicyReportResponse.Result.CardList.newBuilder();
            cardList30102.setType(30102);
            cardList30102.setData(Any.pack(card30102Ref.get().build()));
            cardListList.add(cardList30102.build());
        }
        if (Objects.nonNull(card30103Ref.get())) {
            SubsidyPolicyReportResponse.Result.CardList.Builder cardList30103 =
                    SubsidyPolicyReportResponse.Result.CardList.newBuilder();
            cardList30103.setType(30103);
            cardList30103.setData(Any.pack(card30103Ref.get().build()));
            cardListList.add(cardList30103.build());
        }
        if (Objects.nonNull(card30105RefFinancial.get())) {
            SubsidyPolicyReportResponse.Result.CardList.Builder cardList30105 =
                    SubsidyPolicyReportResponse.Result.CardList.newBuilder();
            cardList30105.setType(30105);
            cardList30105.setData(Any.pack(card30105RefFinancial.get().build()));
            cardListList.add(cardList30105.build());
        }
        if (Objects.nonNull(card30105RefOwner.get())) {
            SubsidyPolicyReportResponse.Result.CardList.Builder cardList30105 =
                    SubsidyPolicyReportResponse.Result.CardList.newBuilder();
            cardList30105.setType(30105);
            cardList30105.setData(Any.pack(card30105RefOwner.get().build()));
            cardListList.add(cardList30105.build());
        }

        return cardListList;
    }

    private CompletableFuture<SubsidyPolicyReportResponse.Result.Card30101.Builder>
    buildCard30101(SeriesDetailDto seriesDetailDto,
                   int selectSpecId,
                   List<SpecDetailDto> specDetailDtoList,
                   List<SpecSubsidyCountDto> specSubsidyList,
                   SeriesCityAskPriceDto seriesCityAskPriceDto) {
        SubsidyPolicyReportResponse.Result.Card30101.Builder card30101Builder =
                SubsidyPolicyReportResponse.Result.Card30101.newBuilder();

        SubsidyPolicyReportResponse.Result.Card30101.SeriesInfo.Builder seriesInfoBuilder =
                SubsidyPolicyReportResponse.Result.Card30101.SeriesInfo.newBuilder();
        seriesInfoBuilder.setSeriesname(seriesDetailDto.getName());
        seriesInfoBuilder.setCarpic(seriesDetailDto.getPngLogo());
        String dealerPrice = getSeriesDealerPrice(seriesCityAskPriceDto);
        if (dealerPrice.endsWith("万起")) {
            seriesInfoBuilder.setPrice(dealerPrice.replace("万起", ""));
            seriesInfoBuilder.setUnit("万起");
        } else if (dealerPrice.endsWith("万")) {
            seriesInfoBuilder.setPrice(dealerPrice.replace("万", ""));
            seriesInfoBuilder.setUnit("万");
        } else {
            seriesInfoBuilder.setFctprice("指导价:" + seriesDetailDto.getPrice());
        }
        seriesInfoBuilder.setSpecnum("共" + specSubsidyList.size() + "款");
        seriesInfoBuilder.setScheme(String.format("autohome://car/seriesmain?seriesid=%d&fromtype=130", seriesDetailDto.getId()));
        Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                .putArgvs("seriesid", seriesDetailDto.getId() + "")
                .putArgvs("specid", selectSpecId + "")
                .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_series_click"));
        seriesInfoBuilder.setPvitem(pvBuilder.build());

        Map<Integer, Boolean> hasFactoryBenefitsMap = specSubsidyList.stream().collect(
                Collectors.toMap(SpecSubsidyCountDto::getSpecId, SpecSubsidyCountDto::isHasFactoryBenefits));

        List<SubsidyPolicyReportResponse.Result.Card30101.SpecList.Builder> specListBuilderList = new ArrayList<>();
        specDetailDtoList.forEach(specDetailDto -> {
            SubsidyPolicyReportResponse.Result.Card30101.SpecList.Builder specListBuilder =
                    SubsidyPolicyReportResponse.Result.Card30101.SpecList.newBuilder();
            if (specDetailDto.getMinPrice() > 0) {
                specListBuilder.setFctprice("指导价：" + CommonHelper.getPriceDetailInfo(specDetailDto.getMinPrice()));
            } else if (specDetailDto.getMaxPrice() > 0) {
                specListBuilder.setFctprice("指导价：" + CommonHelper.getPriceDetailInfo(specDetailDto.getMaxPrice()));
            } else {
                specListBuilder.setFctprice("暂无报价");
            }
            specListBuilder.setSpecname(specDetailDto.getSpecName());
            specListBuilder.setSpecid(specDetailDto.getSpecId());
            if (hasFactoryBenefitsMap.get(specDetailDto.getSpecId())) {
                specListBuilder.setTag("厂商活动");
            }
            specListBuilder.setSelect(selectSpecId == specDetailDto.getSpecId() ? 1 : 0);
            Pvitem.Builder specPvBuilder = Pvitem.newBuilder()
                    .putArgvs("seriesid", seriesDetailDto.getId() + "")
                    .putArgvs("specid", specDetailDto.getSpecId() + "")
                    .putArgvs("isselect", selectSpecId == specDetailDto.getSpecId() ? 1 + "" : 0 + "")
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_card_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_card_show"));
            specListBuilder.setPvitem(specPvBuilder.build());
            specListBuilderList.add(specListBuilder);
        });

        card30101Builder.setSeriesinfo(seriesInfoBuilder);
        card30101Builder.addAllSpeclist(specListBuilderList.stream()
                .map(SubsidyPolicyReportResponse.Result.Card30101.SpecList.Builder::build).toList());
        return CompletableFuture.completedFuture(card30101Builder);
    }

    private String getSeriesDealerPrice(SeriesCityAskPriceDto seriesCityAskPriceDto) {
        if (Objects.isNull(seriesCityAskPriceDto)) {
            return "";
        }
        int minPrice = seriesCityAskPriceDto.getMinPrice();
        int maxPrice = seriesCityAskPriceDto.getMaxPrice();
        if (minPrice > 0) {
            if (minPrice == maxPrice) {
                return CommonHelper.getMoney(minPrice, "起");
            } else {
                return CommonHelper.priceFormat(Double.parseDouble(minPrice + ""), Double.parseDouble(maxPrice + ""),
                        CarSellTypeEnum.Selling, "-");
            }
        } else {
            return "";
        }
    }


    private CompletableFuture<SubsidyPolicyReportResponse.Result.Card30102.Builder>
    buildCard30102(SubsidyPolicyEntryDto subsidyPolicyEntryDto) {
        SubsidyPolicyReportResponse.Result.Card30102.Builder card30102Builder =
                SubsidyPolicyReportResponse.Result.Card30102.newBuilder();
        if (Objects.isNull(subsidyPolicyEntryDto)) {
            return CompletableFuture.completedFuture(null);
        }

        card30102Builder.setTitle(subsidyPolicyEntryDto.getSubsidyTitle());
        card30102Builder.setRighttext(String.valueOf(subsidyPolicyEntryDto.getSubsidyPriceInt()));
        card30102Builder.setRighttextvague(
                maskAllPosition(String.valueOf(subsidyPolicyEntryDto.getSubsidyPriceInt())));
        card30102Builder.setUnit("元");

        List<SubsidyPolicyReportResponse.Result.Card30102.List.Builder> listBuilderList = new ArrayList<>();
        subsidyPolicyEntryDto.getTaglist().forEach(tagItem -> {
            SubsidyPolicyReportResponse.Result.Card30102.List.Builder listBuilder =
                    SubsidyPolicyReportResponse.Result.Card30102.List.newBuilder();
            if ("其他补贴".equals(tagItem.getTitle())) {
                listBuilder.setTitle(tagItem.getTitle());
                listBuilder.setValue(tagItem.getPrice().replace("项", ""));
                listBuilder.setValuevague(tagItem.getPrice().replace("项", ""));
                listBuilder.setUnit("项");
                listBuilderList.add(listBuilder);
            } else {
                listBuilder.setTitle(tagItem.getTitle());
                listBuilder.setValue(String.valueOf(tagItem.getPriceInt()));
                listBuilder.setValuevague(tagItem.getPrice().replace("元", ""));
                listBuilder.setUnit("元");
                listBuilderList.add(listBuilder);
            }
        });
        // 第一项加*
        if (listBuilderList.size() > 1) {
            listBuilderList.get(0).setValuevague(maskAllPosition(listBuilderList.get(0).getValuevague()));
        }
        // 只有一项时去掉taglist
        if (listBuilderList.size() == 1) {
            listBuilderList.clear();
        }
        card30102Builder.addAllList(listBuilderList.stream().map(SubsidyPolicyReportResponse.Result.Card30102.List.Builder::build).toList());

        return CompletableFuture.completedFuture(card30102Builder);
    }


    private CompletableFuture<SubsidyPolicyReportResponse.Result.Card30103.Builder>
    buildCard30103(SpecCitySubsidyDto specCitySubsidyDto) {
        if (Objects.isNull(specCitySubsidyDto)) {
            return CompletableFuture.completedFuture(null);
        }
        List<SubsidyPolicyReportResponse.Result.Card30104.Builder> card30104BuilderList =
                new ArrayList<>();
        List<Card30104Data> card30104DataList = buildCard30104DataList(specCitySubsidyDto);
        if (CollectionUtils.isEmpty(card30104DataList)) {
            return CompletableFuture.completedFuture(null);
        }
        card30104DataList.forEach(card -> {
            SubsidyPolicyReportResponse.Result.Card30104.Builder card30104Builder =
                    SubsidyPolicyReportResponse.Result.Card30104.newBuilder();
            card30104Builder.setFilterid(card.getFilterid());
            card30104Builder.setActtitle(card.getActtitle());
            card30104Builder.setActtime(card.getActtime());
            if (Objects.nonNull(card.getActinfo())) {
                SubsidyPolicyReportResponse.Result.Card30104.ActInfo.Builder actInfoBuilder =
                        SubsidyPolicyReportResponse.Result.Card30104.ActInfo.newBuilder();
                actInfoBuilder.setTitle(card.getActinfo().getTitle());
                List<SubsidyPolicyReportResponse.Result.Card30104.ActInfoList.Builder> actInfoListBuilderList =
                        new ArrayList<>();
                card.getActinfo().getList().forEach(e -> {
                    SubsidyPolicyReportResponse.Result.Card30104.ActInfoList.Builder actInfoListBuilder =
                            SubsidyPolicyReportResponse.Result.Card30104.ActInfoList.newBuilder();
                    actInfoListBuilder.setTitle(e.getTitle());
                    actInfoListBuilder.addAllSubtitle(e.getSubtitle());
                    actInfoListBuilderList.add(actInfoListBuilder);
                });
                actInfoBuilder.addAllList(actInfoListBuilderList.stream()
                        .map(SubsidyPolicyReportResponse.Result.Card30104.ActInfoList.Builder::build).toList());
                Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                        .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                        .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                        // 目前只有本地置换/本地新购 这两种有详情提示
                        .putArgvs("type", String.valueOf(card.getGroupType().getType()))
                        .putArgvs("id", String.valueOf(StringUtils.join(card.getActinfo().getList().stream()
                                .map(Card30104Data.ListTip::getBenefit_id).toList(), ",")))
                        .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_detail_click"));
                actInfoBuilder.setPvitem(pvBuilder.build());
                card30104Builder.setActinfo(actInfoBuilder);
            }
            if (Objects.nonNull(card.getTaginfo())) {
                SubsidyPolicyReportResponse.Result.Card30104.TagInfo.Builder tagInfoBuilder =
                        SubsidyPolicyReportResponse.Result.Card30104.TagInfo.newBuilder();
                tagInfoBuilder.setText(card.getTaginfo().getText());
                tagInfoBuilder.setBackcolor(card.getTaginfo().getBackcolor());
                card30104Builder.setTaginfo(tagInfoBuilder);
            }
            card30104Builder.setRighttext(Objects.isNull(card.getRighttext()) ? "" : card.getRighttext());
            card30104Builder.setRighttextvague(Objects.isNull(card.getRighttextvague()) ? "" : card.getRighttextvague());
            if (Objects.nonNull(card.getList())) {
                List<SubsidyPolicyReportResponse.Result.Card30104.List.Builder> listBuilderList =
                        new ArrayList<>();
                card.getList().forEach(e -> {
                    SubsidyPolicyReportResponse.Result.Card30104.List.Builder listBuilder =
                            SubsidyPolicyReportResponse.Result.Card30104.List.newBuilder();
                    listBuilder.setTitle(e.getTitle());
                    listBuilder.setValue(e.getValue());
                    listBuilderList.add(listBuilder);
                });
                card30104Builder.addAllList(listBuilderList.stream()
                        .map(SubsidyPolicyReportResponse.Result.Card30104.List.Builder::build).toList());
            }
            List<String> ids = CollectionUtils.isEmpty(card.getList())
                    ? List.of(card.getLocal_benefit_id())
                    : card.getList().stream().map(Card30104Data.Item::getBenefit_id).toList();
            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                    .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                    .putArgvs("type", String.valueOf(card.getGroupType().getType()))
                    .putArgvs("price", String.valueOf(card.getPriceInt()))
                    .putArgvs("id", String.valueOf(StringUtils.join(ids, ",")))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_subsidy_show"));
            card30104Builder.setPvitem(pvBuilder.build());
            card30104BuilderList.add(card30104Builder);
        });


        SubsidyPolicyReportResponse.Result.Card30103.Builder card30103Builder =
                SubsidyPolicyReportResponse.Result.Card30103.newBuilder();

        card30103Builder.setTitle("购车优惠补贴");
        List<SubsidyPolicyReportResponse.Result.Card30103.TabList.Builder> tabListBuilderList = new ArrayList<>();

        // 全部
        SubsidyPolicyReportResponse.Result.Card30103.TabList.Builder tabListAllBuilder =
                SubsidyPolicyReportResponse.Result.Card30103.TabList.newBuilder();
        tabListAllBuilder.setName("全部");
        tabListAllBuilder.setTabid(0);
        tabListAllBuilder.addAllFilterids(card30104DataList.stream()
                .map(Card30104Data::getFilterid).toList());
        Pvitem.Builder pvBuilderAll = Pvitem.newBuilder()
                .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                .putArgvs("tabid", String.valueOf(0))
                .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_tab_click"))
                .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_tab_show"));
        tabListAllBuilder.setPvitem(pvBuilderAll.build());

        tabListBuilderList.add(tabListAllBuilder);

        // 新购
        List<Card30104Data> benefitsNew = card30104DataList.stream()
                .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_NEW
                        || e.getGroupType() == SubsidyGroupTypeEnum.LOCAL_BENEFITS_NEW).toList();
        if (!CollectionUtils.isEmpty(benefitsNew)) {
            SubsidyPolicyReportResponse.Result.Card30103.TabList.Builder tabListNewBuilder =
                    SubsidyPolicyReportResponse.Result.Card30103.TabList.newBuilder();
            tabListNewBuilder.setName("新购");
            tabListNewBuilder.setTabid(1);
            tabListNewBuilder.addAllFilterids(benefitsNew.stream()
                    .map(Card30104Data::getFilterid).toList());
            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                    .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                    .putArgvs("tabid", String.valueOf(1))
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_tab_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_tab_show"));
            tabListNewBuilder.setPvitem(pvBuilder.build());
            tabListBuilderList.add(tabListNewBuilder);
        }

        // 置换
        List<Card30104Data> benefitsReplace = card30104DataList.stream()
                .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_REPLACE
                        || e.getGroupType() == SubsidyGroupTypeEnum.LOCAL_BENEFITS_REPLACE).toList();
        if (!CollectionUtils.isEmpty(benefitsReplace)) {
            SubsidyPolicyReportResponse.Result.Card30103.TabList.Builder tabListReplaceBuilder =
                    SubsidyPolicyReportResponse.Result.Card30103.TabList.newBuilder();
            tabListReplaceBuilder.setName("旧车置换");
            tabListReplaceBuilder.setTabid(2);
            tabListReplaceBuilder.addAllFilterids(card30104DataList.stream()
                    .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_REPLACE
                            || e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_NEW
                            || e.getGroupType() == SubsidyGroupTypeEnum.LOCAL_BENEFITS_REPLACE)
                    .map(Card30104Data::getFilterid).toList());
            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                    .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                    .putArgvs("tabid", String.valueOf(2))
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_tab_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_tab_show"));
            tabListReplaceBuilder.setPvitem(pvBuilder.build());
            tabListBuilderList.add(tabListReplaceBuilder);
        }

        // 增购
        List<Card30104Data> benefitsAdd = card30104DataList.stream()
                .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_ADD).toList();
        if (!CollectionUtils.isEmpty(benefitsAdd)) {
            SubsidyPolicyReportResponse.Result.Card30103.TabList.Builder tabListAddBuilder =
                    SubsidyPolicyReportResponse.Result.Card30103.TabList.newBuilder();
            tabListAddBuilder.setName("增购");
            tabListAddBuilder.setTabid(3);
            tabListAddBuilder.addAllFilterids(card30104DataList.stream()
                    .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_ADD
                            || e.getGroupType() == SubsidyGroupTypeEnum.FACTORY_BENEFITS_NEW
                            || e.getGroupType() == SubsidyGroupTypeEnum.LOCAL_BENEFITS_NEW)
                    .map(Card30104Data::getFilterid).toList());
            Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                    .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                    .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                    .putArgvs("tabid", String.valueOf(3))
                    .setClick(Pvitem.Click.newBuilder().setEventid("car_buycarsubsidy_tab_click"))
                    .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_tab_show"));
            tabListAddBuilder.setPvitem(pvBuilder.build());
            tabListBuilderList.add(tabListAddBuilder);
        }
        card30103Builder.addAllTablist(tabListBuilderList.stream()
                .map(SubsidyPolicyReportResponse.Result.Card30103.TabList.Builder::build).toList());

        List<SubsidyPolicyReportResponse.Result.Card30103.SubCardList.Builder> subCardListBuilderList =
                new ArrayList<>();
        card30104BuilderList.forEach(e -> {
            SubsidyPolicyReportResponse.Result.Card30103.SubCardList.Builder subCardListBuilder =
                    SubsidyPolicyReportResponse.Result.Card30103.SubCardList.newBuilder();
            subCardListBuilder.setType(30104);
            subCardListBuilder.setData(e);
            subCardListBuilderList.add(subCardListBuilder);
        });
        card30103Builder.addAllSubcardlist(subCardListBuilderList.stream()
                .map(SubsidyPolicyReportResponse.Result.Card30103.SubCardList.Builder::build).toList());

        return CompletableFuture.completedFuture(card30103Builder);
    }


    /**
     * 组装104卡片数据
     *
     * @param specCitySubsidyDto
     * @return
     */
    private List<Card30104Data> buildCard30104DataList(SpecCitySubsidyDto specCitySubsidyDto) {
        BenefitsGroup benefitsGroup = subsidyGroup(specCitySubsidyDto);
        if (Objects.isNull(benefitsGroup)) {
            return Collections.emptyList();
        }

        // 30104卡片的数据
        List<Card30104Data> card30104DataList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(benefitsGroup.getFactoryBenefits())) {
            // 厂商置换补贴
            List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitsReplace = benefitsGroup.getFactoryBenefits().stream()
                    .filter(localBenefit -> "置换".equals(localBenefit.getSubsidy_type_name())).toList();
            if (!CollectionUtils.isEmpty(factoryBenefitsReplace)) {
                Card30104Data card30104Data = new Card30104Data();
                card30104Data.setGroupType(SubsidyGroupTypeEnum.FACTORY_BENEFITS_REPLACE);
                card30104Data.setFilterid("1");
                card30104Data.setActtitle("品牌置换补贴");
                card30104Data.setActtime(getActTime(factoryBenefitsReplace.get(0).getStart_time(),
                        factoryBenefitsReplace.get(0).getEnd_time())); // 活动时间，多个取第一个
                Card30104Data.TagInfo taginfo = new Card30104Data.TagInfo();
                taginfo.setText("厂商活动");
                taginfo.setBackcolor("#FFEFE5");
                card30104Data.setTaginfo(taginfo);
                card30104Data.setList(new ArrayList<>());

                // 厂商置换补贴中，最大补贴为所有的置换中最大值
                List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitsCash =
                        factoryBenefitsReplace.stream().filter(e -> e.getAmount() > 0).toList();
                int maxSubsidy = factoryBenefitsCash.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount)
                        .max().orElse(0);
                if (factoryBenefitsCash.size() > 1) {
                    card30104Data.setRighttext("最高补贴" + maxSubsidy + "元");
                } else {
                    if (maxSubsidy > 0) {
                        card30104Data.setRighttext(maxSubsidy + "元");
                    }
                }
                if (maxSubsidy > 0) {
                    card30104Data.setPriceInt(maxSubsidy);
                    card30104Data.setRighttextvague(maskAllPosition(card30104Data.getRighttext()));
                }

                factoryBenefitsReplace.forEach(e -> {
                    Card30104Data.Item item = new Card30104Data.Item();
                    item.setTitle(e.getSub_type_name().replace("（渠道）", ""));
                    item.setValue(StringUtils.isNotEmpty(e.getBenefit_short_content())
                            ? e.getBenefit_short_content()
                            : e.getBenefit_content());
                    item.setBenefit_id(e.getBenefit_id());
                    card30104Data.getList().add(item);
                });
                card30104DataList.add(card30104Data);
            }

            // 厂商增购补贴
            List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitsAdd = benefitsGroup.getFactoryBenefits().stream()
                    .filter(localBenefit -> "增购".equals(localBenefit.getSubsidy_type_name())).toList();
            if (!CollectionUtils.isEmpty(factoryBenefitsAdd)) {
                Card30104Data card30104Data = new Card30104Data();
                card30104Data.setGroupType(SubsidyGroupTypeEnum.FACTORY_BENEFITS_ADD);
                card30104Data.setFilterid("2");
                card30104Data.setActtitle("品牌增购补贴");
                card30104Data.setActtime(getActTime(factoryBenefitsAdd.get(0).getStart_time(),
                        factoryBenefitsAdd.get(0).getEnd_time())); // 活动时间，多个取第一个
                Card30104Data.TagInfo taginfo = new Card30104Data.TagInfo();
                taginfo.setText("厂商活动");
                taginfo.setBackcolor("#FFEFE5");
                card30104Data.setTaginfo(taginfo);
                card30104Data.setList(new ArrayList<>());

                // 厂商增购补贴中，最大补贴为所有的增购中最大值
                List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitsCash =
                        factoryBenefitsAdd.stream().filter(e -> e.getAmount() > 0).toList();
                int maxSubsidy = factoryBenefitsCash.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount)
                        .max().orElse(0);
                if (factoryBenefitsCash.size() > 1) {
                    card30104Data.setRighttext("最高补贴" + maxSubsidy + "元");
                } else {
                    if (maxSubsidy > 0) {
                        card30104Data.setRighttext(maxSubsidy + "元");
                    }
                }
                if (maxSubsidy > 0) {
                    card30104Data.setPriceInt(maxSubsidy);
                    card30104Data.setRighttextvague(maskAllPosition(card30104Data.getRighttext()));
                }

                factoryBenefitsAdd.forEach(e -> {
                    Card30104Data.Item item = new Card30104Data.Item();
                    item.setTitle(e.getSub_type_name().replace("（渠道）", ""));
                    item.setValue(StringUtils.isNotEmpty(e.getBenefit_short_content())
                            ? e.getBenefit_short_content()
                            : e.getBenefit_content());
                    item.setBenefit_id(e.getBenefit_id());
                    card30104Data.getList().add(item);
                });
                card30104DataList.add(card30104Data);
            }

            // 厂商新购补贴
            List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitsNew = benefitsGroup.getFactoryBenefits().stream()
                    .filter(localBenefit -> "新购".equals(localBenefit.getSubsidy_type_name())).toList();
            if (!CollectionUtils.isEmpty(factoryBenefitsNew)) {
                Card30104Data card30104Data = new Card30104Data();
                card30104Data.setGroupType(SubsidyGroupTypeEnum.FACTORY_BENEFITS_NEW);
                card30104Data.setFilterid("3");
                card30104Data.setActtitle("品牌新购补贴");
                card30104Data.setActtime(getActTime(factoryBenefitsNew.get(0).getStart_time(),
                        factoryBenefitsNew.get(0).getEnd_time())); // 活动时间，多个取第一个
                Card30104Data.TagInfo taginfo = new Card30104Data.TagInfo();
                taginfo.setText("厂商活动");
                taginfo.setBackcolor("#FFEFE5");
                card30104Data.setTaginfo(taginfo);
                card30104Data.setList(new ArrayList<>());

                // 厂商新购补贴中，最大补贴为所有的新购加总
                List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitsCash =
                        factoryBenefitsNew.stream().filter(e -> e.getAmount() > 0).toList();
                int maxSubsidy = factoryBenefitsCash.stream()
                        .mapToInt(SpecCitySubsidyDto.FactoryBenefit::getAmount)
                        .sum();
                if (factoryBenefitsCash.size() > 1) {
                    card30104Data.setRighttext("最高补贴" + maxSubsidy + "元");
                } else {
                    if (maxSubsidy > 0) {
                        card30104Data.setRighttext(maxSubsidy + "元");
                    }
                }
                if (maxSubsidy > 0) {
                    card30104Data.setPriceInt(maxSubsidy);
                    card30104Data.setRighttextvague(maskAllPosition(card30104Data.getRighttext()));
                }

                factoryBenefitsNew.forEach(e -> {
                    Card30104Data.Item item = new Card30104Data.Item();
                    item.setTitle(e.getBenefit_title());
                    item.setValue(StringUtils.isNotEmpty(e.getBenefit_short_content())
                            ? e.getBenefit_short_content()
                            : e.getBenefit_content());
                    item.setBenefit_id(e.getBenefit_id());
                    card30104Data.getList().add(item);
                });
                card30104DataList.add(card30104Data);
            }
        }

        if (!CollectionUtils.isEmpty(benefitsGroup.getLocalBenefits())) {
            List<Card30104Data.ListTip> listTipList = new ArrayList<>();

            // 地方政策补贴-置换
            List<SpecCitySubsidyDto.LocalBenefit> localBenefitsReplace = benefitsGroup.getLocalBenefits().stream()
                    .filter(localBenefit -> "置换".equals(localBenefit.getSubsidy_type_name())).toList();
            if (!CollectionUtils.isEmpty(localBenefitsReplace)) {
                Card30104Data card30104Data = new Card30104Data();
                card30104Data.setGroupType(SubsidyGroupTypeEnum.LOCAL_BENEFITS_REPLACE);
                card30104Data.setLocal_benefit_id(localBenefitsReplace.get(0).getBenefit_id());
                card30104Data.setFilterid("4");
                card30104Data.setActtitle(CityUtil.getCityName(benefitsGroup.getCityId()) + "置换补贴");
                card30104Data.setActtime(getActTime(localBenefitsReplace.get(0).getStart_time(),
                        localBenefitsReplace.get(0).getEnd_time())); // 活动时间，多个取第一个
                Card30104Data.TagInfo taginfo = new Card30104Data.TagInfo();
                taginfo.setText("本地政策");
                taginfo.setBackcolor("#E8FAF4");
                card30104Data.setTaginfo(taginfo);
                card30104Data.setList(new ArrayList<>());

                List<SpecCitySubsidyDto.LocalBenefit> localBenefitsCash =
                        localBenefitsReplace.stream().filter(e -> e.getAmount() > 0).toList();
                int maxSubsidy = localBenefitsCash.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount)
                        .max().orElse(0);
                if (maxSubsidy > 0) {
                    card30104Data.setRighttext(maxSubsidy + "元");
                    card30104Data.setPriceInt(maxSubsidy);
                    card30104Data.setRighttextvague(maskAllPosition(card30104Data.getRighttext()));
                }

                localBenefitsReplace.forEach(e -> {
                    Card30104Data.ListTip listTip = new Card30104Data.ListTip();
                    List<String> textList = new ArrayList<>(Arrays.stream(
                            e.getPolicy_text().split("\\n")).toList());
                    if (!CollectionUtils.isEmpty(textList)) {
                        listTip.setTitle(textList.get(0));
                        textList.remove(0);
                        listTip.setSubtitle(textList);
                        listTip.setBenefit_id(e.getBenefit_id());
                    }
                    listTipList.add(listTip);
                });

                card30104DataList.add(card30104Data);
            }

            // 地方政策补贴-新购
            List<SpecCitySubsidyDto.LocalBenefit> localBenefitsAdd = benefitsGroup.getLocalBenefits().stream()
                    .filter(localBenefit -> "新购".equals(localBenefit.getSubsidy_type_name())).toList();
            if (!CollectionUtils.isEmpty(localBenefitsAdd)) {
                Card30104Data card30104Data = new Card30104Data();
                card30104Data.setGroupType(SubsidyGroupTypeEnum.LOCAL_BENEFITS_NEW);
                card30104Data.setLocal_benefit_id(localBenefitsAdd.get(0).getBenefit_id());
                card30104Data.setFilterid("5");
                card30104Data.setActtitle(CityUtil.getCityName(benefitsGroup.getCityId()) + "新购补贴");
                card30104Data.setActtime(getActTime(localBenefitsAdd.get(0).getStart_time(),
                        localBenefitsAdd.get(0).getEnd_time())); // 活动时间，多个取第一个
                Card30104Data.TagInfo taginfo = new Card30104Data.TagInfo();
                taginfo.setText("本地政策");
                taginfo.setBackcolor("#E8FAF4");
                card30104Data.setTaginfo(taginfo);
                card30104Data.setList(new ArrayList<>());

                List<SpecCitySubsidyDto.LocalBenefit> localBenefitsCash =
                        localBenefitsAdd.stream().filter(e -> e.getAmount() > 0).toList();
                int maxSubsidy = localBenefitsCash.stream()
                        .mapToInt(SpecCitySubsidyDto.LocalBenefit::getAmount)
                        .max().orElse(0);
                if (maxSubsidy > 0) {
                    card30104Data.setRighttext(maxSubsidy + "元");
                    card30104Data.setPriceInt(maxSubsidy);
                    card30104Data.setRighttextvague(maskAllPosition(card30104Data.getRighttext()));
                }

                localBenefitsAdd.forEach(e -> {
                    Card30104Data.ListTip listTip = new Card30104Data.ListTip();
                    List<String> textList = new ArrayList<>(Arrays.stream(
                            e.getPolicy_text().split("\\n")).toList());
                    if (!CollectionUtils.isEmpty(textList)) {
                        listTip.setTitle(textList.get(0));
                        textList.remove(0);
                        listTip.setSubtitle(textList);
                        listTip.setBenefit_id(e.getBenefit_id());
                    }
                    listTipList.add(listTip);
                });

                card30104DataList.add(card30104Data);
            }

            if (!CollectionUtils.isEmpty(listTipList)) {
                Optional<Card30104Data> localBenefitsReplaceCard = card30104DataList.stream()
                        .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.LOCAL_BENEFITS_REPLACE)
                        .findFirst();
                if (localBenefitsReplaceCard.isPresent()) {
                    Card30104Data card = localBenefitsReplaceCard.get();
                    Card30104Data.ActInfo actInfo = new Card30104Data.ActInfo();
                    actInfo.setTitle(CityUtil.getCityName(benefitsGroup.getCityId()) + "购车政策详情");
                    actInfo.setList(listTipList);
                    card.setActinfo(actInfo);
                }
                Optional<Card30104Data> localBenefitsNewCard = card30104DataList.stream()
                        .filter(e -> e.getGroupType() == SubsidyGroupTypeEnum.LOCAL_BENEFITS_NEW)
                        .findFirst();
                if (localBenefitsNewCard.isPresent()) {
                    Card30104Data card = localBenefitsNewCard.get();
                    Card30104Data.ActInfo actInfo = new Card30104Data.ActInfo();
                    actInfo.setTitle(CityUtil.getCityName(benefitsGroup.getCityId()) + "购车政策详情");
                    actInfo.setList(listTipList);
                    card.setActinfo(actInfo);
                }
            }
        }

        return card30104DataList;
    }

    private BenefitsGroup subsidyGroup(SpecCitySubsidyDto specCitySubsidyDto) {
        // 获取所有不重复的BenefitId
        List<String> allDistinctBenefitIds = getAllDistinctBenefitIds(specCitySubsidyDto);
        // 根据最大的加和来处理补贴信息的构成项
        // 厂商补贴Map
        Map<String, SpecCitySubsidyDto.FactoryBenefit> factoryBenefitMap =
                CollectionUtils.isEmpty(specCitySubsidyDto.getFactory_benefits())
                        ? new HashMap<>()
                        : specCitySubsidyDto.getFactory_benefits().stream().collect(
                        Collectors.toMap(SpecCitySubsidyDto.FactoryBenefit::getBenefit_id, Function.identity(),
                                (k1, k2) -> k2));
        // 地方补贴Map
        Map<String, SpecCitySubsidyDto.LocalBenefit> localBenefitMap =
                CollectionUtils.isEmpty(specCitySubsidyDto.getLocal_benefits())
                        ? new HashMap<>()
                        : specCitySubsidyDto.getLocal_benefits().stream().collect(
                        Collectors.toMap(SpecCitySubsidyDto.LocalBenefit::getBenefit_id, Function.identity(),
                                (k1, k2) -> k2));
        List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefitList = new ArrayList<>();
        List<SpecCitySubsidyDto.LocalBenefit> localBenefitList = new ArrayList<>();
        // 厂商补贴or地方补贴（包含现金补贴和权益补贴）
        for (String benefitId : allDistinctBenefitIds) {
            if (Objects.nonNull(factoryBenefitMap.get(benefitId))) {
                factoryBenefitList.add(factoryBenefitMap.get(benefitId));
            } else if (Objects.nonNull(localBenefitMap.get(benefitId))) {
                localBenefitList.add(localBenefitMap.get(benefitId));
            }
        }

        if (factoryBenefitList.isEmpty() && localBenefitList.isEmpty()) {
            // 如果厂商补贴和地方补贴均为空，则表示没有补贴信息，返回null不展示入口
            return null;
        }

        BenefitsGroup benefitsGroup = new BenefitsGroup();
        benefitsGroup.setCityId(specCitySubsidyDto.getCity_id());
        benefitsGroup.getLocalBenefits().addAll(localBenefitList);
        benefitsGroup.getFactoryBenefits().addAll(factoryBenefitList);
        return benefitsGroup;

    }

    /**
     * 获取所有不重复的BenefitId（不含报废买新，和最大sum，与入口保持一致）
     *
     * @param specCitySubsidyDto SpecCitySubsidyDto
     * @return List<String>
     */
    private List<String> getAllDistinctBenefitIds(SpecCitySubsidyDto specCitySubsidyDto) {
        List<String> allBenefitIds = new ArrayList<>();
        SpecCitySubsidyDto.BenefitSum benefitSum = specCitySubsidyDto.getBenefit_sum();
        if (Objects.isNull(benefitSum)) {
            return Collections.emptyList();
        } else {
            if (Objects.nonNull(benefitSum.getNew_car_sum())) {
                allBenefitIds.addAll(benefitSum.getNew_car_sum().getBenefit_id_list());
            }
            if (Objects.nonNull(benefitSum.getReplace_this_brand_sum())) {
                allBenefitIds.addAll(benefitSum.getReplace_this_brand_sum().getBenefit_id_list());
            }
            if (Objects.nonNull(benefitSum.getReplace_other_brand_sum())) {
                allBenefitIds.addAll(benefitSum.getReplace_other_brand_sum().getBenefit_id_list());
            }
            if (Objects.nonNull(benefitSum.getReplace_all_brand_sum())) {
                allBenefitIds.addAll(benefitSum.getReplace_all_brand_sum().getBenefit_id_list());
            }
            if (Objects.nonNull(benefitSum.getAdd_this_brand_sum())) {
                allBenefitIds.addAll(benefitSum.getAdd_this_brand_sum().getBenefit_id_list());
            }
            if (Objects.nonNull(benefitSum.getAdd_other_brand_sum())) {
                allBenefitIds.addAll(benefitSum.getAdd_other_brand_sum().getBenefit_id_list());
            }
            if (Objects.nonNull(benefitSum.getAdd_all_brand_sum())) {
                allBenefitIds.addAll(benefitSum.getAdd_all_brand_sum().getBenefit_id_list());
            }
            return allBenefitIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        }
    }

    @Data
    private static class BenefitsGroup {

        private int cityId;

        private List<SpecCitySubsidyDto.LocalBenefit> localBenefits = new ArrayList<>();

        private List<SpecCitySubsidyDto.FactoryBenefit> factoryBenefits = new ArrayList<>();
    }

    // 金融
    private CompletableFuture<SubsidyPolicyReportResponse.Result.Card30105.Builder>
    buildCard30105Financial(SpecCitySubsidyDto specCitySubsidyDto) {
        SubsidyPolicyReportResponse.Result.Card30105.Builder card30105Builder =
                SubsidyPolicyReportResponse.Result.Card30105.newBuilder();
        if (Objects.isNull(specCitySubsidyDto) ||
                CollectionUtils.isEmpty(specCitySubsidyDto.getFactory_benefits())) {
            return CompletableFuture.completedFuture(null);
        }

        List<SpecCitySubsidyDto.FactoryBenefit> financials = specCitySubsidyDto.getFactory_benefits().stream()
                .filter(e -> e.getSubsidy_type_name().equals("金融"))
                .toList();
        List<SubsidyPolicyReportResponse.Result.Card30105.List.Builder> listBuilderList = new ArrayList<>();
        if (CollectionUtils.isEmpty(financials)) {
            return CompletableFuture.completedFuture(null);
        }
        card30105Builder.setTitle("金融优惠");
        financials.forEach(f -> {
            SubsidyPolicyReportResponse.Result.Card30105.List.Builder listBuilder =
                    SubsidyPolicyReportResponse.Result.Card30105.List.newBuilder();
            listBuilder.setActtitle(StringUtils.isEmpty(f.getBenefit_title()) ? "金融礼遇" : f.getBenefit_title());
            listBuilder.setActtime(getActTime(f.getStart_time(), f.getEnd_time()));
            listBuilder.addAllActlist(Arrays.stream(f.getBenefit_content().split("\\n")).toList());
            listBuilderList.add(listBuilder);
        });
        Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                .putArgvs("type", String.valueOf(10))
                .putArgvs("price", String.valueOf(0))
                .putArgvs("id", String.valueOf(StringUtils.join(financials.stream()
                        .map(SpecCitySubsidyDto.FactoryBenefit::getBenefit_id).toList(), ",")))
                .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_subsidy_show"));
        card30105Builder.setPvitem(pvBuilder.build());
        card30105Builder.addAllList(listBuilderList.stream()
                .map(SubsidyPolicyReportResponse.Result.Card30105.List.Builder::build).toList());
        return CompletableFuture.completedFuture(card30105Builder);
    }

    // 车主权益
    private CompletableFuture<SubsidyPolicyReportResponse.Result.Card30105.Builder>
    buildCard30105Owner(SpecCitySubsidyDto specCitySubsidyDto) {
        SubsidyPolicyReportResponse.Result.Card30105.Builder card30105Builder =
                SubsidyPolicyReportResponse.Result.Card30105.newBuilder();
        if (Objects.isNull(specCitySubsidyDto) ||
                CollectionUtils.isEmpty(specCitySubsidyDto.getFactory_benefits())) {
            return CompletableFuture.completedFuture(null);
        }

        List<SpecCitySubsidyDto.FactoryBenefit> owners = specCitySubsidyDto.getFactory_benefits().stream()
                .filter(e -> e.getSubsidy_type_name().equals("车主权益"))
                .toList();
        List<SubsidyPolicyReportResponse.Result.Card30105.List.Builder> listBuilderList = new ArrayList<>();
        if (CollectionUtils.isEmpty(owners)) {
            return CompletableFuture.completedFuture(null);
        }
        card30105Builder.setTitle("车主权益");
        owners.forEach(o -> {
            SubsidyPolicyReportResponse.Result.Card30105.List.Builder listBuilder =
                    SubsidyPolicyReportResponse.Result.Card30105.List.newBuilder();
            listBuilder.setActtitle(StringUtils.isEmpty(o.getBenefit_title()) ? "购车权益" : o.getBenefit_title());
            listBuilder.setActtime(getActTime(o.getStart_time(), o.getEnd_time()));
            listBuilder.addAllActlist(Arrays.stream(o.getBenefit_content().split("\\n")).toList());
            listBuilderList.add(listBuilder);
        });
        Pvitem.Builder pvBuilder = Pvitem.newBuilder()
                .putArgvs("seriesid", String.valueOf(specCitySubsidyDto.getSeries_id()))
                .putArgvs("specid", String.valueOf(specCitySubsidyDto.getSpec_id()))
                .putArgvs("type", String.valueOf(11))
                .putArgvs("price", String.valueOf(0))
                .putArgvs("id", String.valueOf(StringUtils.join(owners.stream()
                        .map(SpecCitySubsidyDto.FactoryBenefit::getBenefit_id).toList(), ",")))
                .setShow(Pvitem.Show.newBuilder().setEventid("car_buycarsubsidy_subsidy_show"));
        card30105Builder.setPvitem(pvBuilder.build());
        card30105Builder.addAllList(listBuilderList.stream()
                .map(SubsidyPolicyReportResponse.Result.Card30105.List.Builder::build).toList());
        return CompletableFuture.completedFuture(card30105Builder);
    }


    /**
     * 获取活动时间
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String getActTime(Date startTime, Date endTime) {
        return "活动时间:" + DateUtil.format(startTime, "yyyy.MM.dd")
                + "-" + DateUtil.format(endTime, "yyyy.MM.dd");
    }

    /**
     * 第一位加*
     *
     * @param value
     * @return
     */
    private String maskFirstPosition(String value) {
        if (value.matches("\\d.*")) {
            return "*".concat(value.substring(1));
        }
        return value;
    }

    /**
     * 全部替换为*
     *
     * @param value
     * @return
     */
    private String maskAllPosition(String value) {
        return value.replaceAll("\\d", "*");
    }


    /**
     * 生成询价协议
     *
     * @param seriesId 车系ID
     * @param specId   车型ID
     * @param eid      eid
     * @return 询价协议
     */
    private String genAskPriceScheme(int seriesId, int specId, String eid, String businessid, String ordertype) {
        Map<String, Object> extMap = new HashMap<>(3);
        extMap.put("abortbgtap", 1);
        extMap.put("inquirybiztype", 4);
        extMap.put("title", "解锁全网优惠信息");
        String otype = StringUtils.isNotBlank(ordertype) ? ordertype : "1";
        String template = "autohome://car/zixunpoppage?customshowanimationtype=2&animation_type=2&ordertype=%s&businesstype=1&successjump=0&seriesid=%d&specid=%d&eid=%s&ext=%s";
        if (StringUtils.isNotBlank(businessid)) {
            template = template + "&businessid=" + businessid;
        }
        return String.format(template, otype, seriesId, specId,
                URLEncoder.encode(eid, StandardCharsets.UTF_8), URLEncoder.encode(JSON.toJSONString(extMap), StandardCharsets.UTF_8));
    }
}
