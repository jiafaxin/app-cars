package com.autohome.app.cars.provider.services;

import autohome.rpc.car.app_cars.v1.carbase.DubboSeriesDetailServiceTriple;
import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoRequest;
import autohome.rpc.car.app_cars.v1.carbase.SeriesBaseInfoResponse;
import com.autohome.app.cars.common.utils.CommonHelper;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.SeriesTestDataComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SeriesTestDataDto;
import com.autohome.app.cars.service.components.che168.SeriesUsedCarComponent;
import com.autohome.app.cars.service.components.club.SeriesClubPostComponent;
import com.autohome.app.cars.service.components.cms.AutoShowConfigComponent;
import com.autohome.app.cars.service.components.cms.dtos.AutoShowConfigDto;
import com.autohome.app.cars.service.components.dealer.SeriesCityAskPriceNewComponent;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityAskPriceDto;
import com.autohome.app.cars.service.components.hqpic.HqPicDataComponent;
import com.autohome.app.cars.service.components.hqpic.dtos.HqPicDataDto;
import com.autohome.app.cars.service.components.misc.NewSeriesCityHotNewsAndTabComponent;
import com.autohome.app.cars.service.components.misc.SeriesCityTabComponent;
import com.autohome.app.cars.service.components.misc.dtos.NewSeriesCityHotNewsAndTabDto;
import com.autohome.app.cars.service.services.SeriesInfoService;
import com.autohome.app.cars.service.services.dtos.AutoShowConfig;
import com.autohome.app.cars.service.services.dtos.VideoVRConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@DubboService
@RestController
@Slf4j
public class SeriesDetailServiceGrpcImpl extends DubboSeriesDetailServiceTriple.SeriesDetailServiceImplBase {

    @Value("#{T(com.autohome.app.cars.service.services.dtos.AutoShowConfig).decodeAutoShowConfig('${autoshow_config:}')}")
    AutoShowConfig autoShowConfig;

    @Value("#{T(com.autohome.app.cars.service.services.dtos.VideoVRConfig).decodeVideoVRConfig('${videovrconfig:}')}")
    List<VideoVRConfig> videoVRConfigList;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SeriesUsedCarComponent seriesUsedCarComponent;

    @Autowired
    SeriesInfoService seriesInfoService;

    @Autowired
    AutoShowConfigComponent autoShowConfigComponent;

    @Autowired
    SeriesTestDataComponent seriesTestDataComponent;

    @Autowired
    SeriesCityAskPriceNewComponent seriesCityAskPriceComponent;

    @Autowired
    SeriesCityTabComponent seriesCityTabComponent;

    @Autowired
    private NewSeriesCityHotNewsAndTabComponent newSeriesCityHotNewsAndTabComponent;

    @Autowired
    private HqPicDataComponent hqPicDataComponent;
    @Autowired
    private SeriesClubPostComponent seriesClubPostComponent;

    /**
     * 从11.60.0版本 开始接入
     */

    @Override
    @GetMapping(value = "/carbase/seriessummary/seriesbasecardinfo", produces = "application/json;charset=utf-8")
    public SeriesBaseInfoResponse seriesBaseInfo(SeriesBaseInfoRequest request) {
        if (request.getSeriesid() <= 0 || !(request.getPm() == 1 || request.getPm() == 2|| request.getPm() == 3 || request.getPm() == 5)) {
            return SeriesBaseInfoResponse.newBuilder().setReturnCode(101).setReturnMsg("传入参数有误").build();
        }
        SeriesBaseInfoResponse.Result.Builder result = SeriesBaseInfoResponse.Result.newBuilder();

        //车系详情
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(request.getSeriesid());

        if(seriesDetailDto==null){
            return SeriesBaseInfoResponse.newBuilder()
                    .setReturnCode(-1)
                    .setReturnMsg("车系不存在")
                    .build();
        }
        //新版车系实验固化
        int funcabtest= CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.63.8")?1:request.getFuncabtest();
        //车展
        CompletableFuture<AutoShowConfigDto> autoShowFuture = autoShowConfigComponent.get(autoShowConfig.getAutoshowid());
        //实测冬测
        CompletableFuture<SeriesTestDataDto> seriesTestDataFuture = seriesTestDataComponent.get(seriesDetailDto.getId());
        //经销商价格
        CompletableFuture<SeriesCityAskPriceDto> seriesCityAskPriceFuture = seriesCityAskPriceComponent.get(seriesDetailDto.getId(), request.getCityid());
        //高质图库
        CompletableFuture<HqPicDataDto> hqPicDataDtoFuture = hqPicDataComponent.getAsync(seriesDetailDto.getId());

        List<CompletableFuture> tasks = new ArrayList<>();
        //区分实验版、普通版
        boolean isNewSeriesSummary = CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.66.0")
                && StringUtils.equalsAnyIgnoreCase(request.getNewsummaryab(), "1", "2");
        //车系主要信息
        tasks.add(seriesInfoService.getSeriesBaseInfoBuilder(seriesDetailDto, request, autoShowFuture, seriesTestDataFuture,seriesCityAskPriceFuture,funcabtest,isNewSeriesSummary).thenAccept(result::setSeriesbaseinfo));
        // 停售车系二手车tab 车系停售页改版  101915 https://doc.autohome.com.cn/docapi/page/share/share_z1K5BbhRVw
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.5") && request.getSeriesabtest().equals("B")) {
            tasks.add(seriesInfoService.getMainTabBuilder(request.getSeriesid(), request.getCityid(), seriesDetailDto).thenAccept(result::addAllMaintab));
        }
        CompletableFuture<Boolean> autoShowing = autoShowFuture.thenApply(info -> info != null && info.getCarAction(request.getSeriesid()) > -1);

        if (isNewSeriesSummary) {
            //热点配置数据
            CompletableFuture<List<NewSeriesCityHotNewsAndTabDto>> newSeriesCityHotNewsAndTabComponentAsync = newSeriesCityHotNewsAndTabComponent.getAsync(request.getSeriesid(), request.getCityid());
            //糖豆
            tasks.add(
                    seriesInfoService.getBeansNewVersion(
                            seriesDetailDto,
                            request.getCityid(),
                            request.getNodefaultcityid(),
                            request.getPm(),
                            request.getNewcarsingledingyueab(),
                            request.getPluginversion(),
                            seriesCityAskPriceFuture,
                            newSeriesCityHotNewsAndTabComponentAsync,
                            request.getBzlabtest()
                    ).thenAccept(result::addAllItemlist)
            );
            // 添加tab标签和热点数据
            tasks.add(seriesInfoService.getNewSeriesTabAndHotNews(request, seriesDetailDto, newSeriesCityHotNewsAndTabComponentAsync,result));
        }else {
            tasks.add(
                    seriesInfoService.getBeans(
                            request,
                            seriesDetailDto,
                            request.getCityid(),
                            funcabtest,
                            request.getNodefaultcityid(),
                            request.getPm(),
                            request.getNewcarsingledingyueab(),
                            autoShowing,
                            request.getEnergytestab(),
                            request.getPluginversion(),
                            request.getSubscribeabtest(),
                            request.getSubscribetitleabtest(),
                            seriesCityAskPriceFuture,
                            request.getToparticlehotab()
                    ).thenAccept(result::addAllItemlist)
            );
            //tab标签
            tasks.add(seriesInfoService.getTabInfos(request, seriesDetailDto,funcabtest,false).thenAccept(result::addAllTabinfo));
        }


        //其他处理：互动视频预加载，理想L7
        if (videoVRConfigList != null && videoVRConfigList.size() > 0) {
            SeriesBaseInfoResponse.Result.Actionvideoinfo.Builder actionVideoInfo = SeriesBaseInfoResponse.Result.Actionvideoinfo.newBuilder();
            VideoVRConfig videoVRConfig = this.videoVRConfigList.stream().filter(x -> x.isEnable() && x.getSeriesid() == seriesDetailDto.getId()).findFirst().orElse(null);
            if (videoVRConfig != null) {
                actionVideoInfo.setVideobytesize(videoVRConfig.getActionvideoinfo().getVideobytesize());
                actionVideoInfo.setVideourl(videoVRConfig.getActionvideoinfo().getVideourl());
                actionVideoInfo.setVideoimage(videoVRConfig.getActionvideoinfo().getVideoimage());
                actionVideoInfo.setScheme("autohome://car/actionvideo?sourceid=0&id=" + videoVRConfig.getId());
            }
            result.setActionvideoinfo(actionVideoInfo);
        }

        //megaab实验，控制车系头图展示
        boolean isNewHead = false;
        if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.67.3")) {
            if (List.of("C1", "C2").contains(request.getMegaab())) {
                isNewHead = true;
            }
        } else if (CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")) {
            if (Arrays.asList("C1").contains(request.getMegaab())) {
                isNewHead = true;
            }
        }
        if (isNewHead) {
            if (Arrays.asList(6939, 7177).contains(request.getSeriesid())) {
                tasks.add(seriesInfoService.getHeadinfo(request).thenAccept(head -> result.setHeadinfo(head.build())));
            } else {
                HqPicDataDto hqPicDataDto = hqPicDataDtoFuture.join();
                if (Objects.nonNull(hqPicDataDto)
                        && CommonHelper.isTakeEffectVersion(request.getPluginversion(), "11.65.8")) {
                    tasks.add(seriesInfoService.getHqPicHeadInfo(seriesDetailDto, hqPicDataDto).thenAccept(head ->
                            result.setHeadinfo(head.build())));
                }
            }
        }

        //等待所有任务完成
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenApply(x ->
                SeriesBaseInfoResponse.newBuilder()
                        .setReturnCode(0)
                        .setReturnMsg("success")
                        .setResult(result)
                        .build()
        ).exceptionally(e -> {
            log.error("seriesBaseInfo error:" + request.getSeriesid(), e);
            return SeriesBaseInfoResponse.newBuilder()
                    .setReturnCode(101)
                    .setReturnMsg("fail")
                    .build();
        }).join();
    }
}