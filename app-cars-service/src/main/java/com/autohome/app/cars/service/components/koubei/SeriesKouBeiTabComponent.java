package com.autohome.app.cars.service.components.koubei;

import com.autohome.app.cars.apiclient.koubei.KoubeiApiClient;
import com.autohome.app.cars.apiclient.maindata.MainDataApiClient;
import com.autohome.app.cars.apiclient.maindata.dtos.HotDataResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.enums.KoubeiTabTypeEnum;
import com.autohome.app.cars.common.utils.*;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.SeriesDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.SeriesDetailDto;
import com.autohome.app.cars.service.components.koubei.dtos.SeriesKoubeiTabDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@DBConfig(tableName = "series_koubei_tab")
public class SeriesKouBeiTabComponent extends BaseComponent<SeriesKoubeiTabDto> {


    static String paramName = "seriesId";

    @Autowired
    KoubeiApiClient koubeiApiClient;

    @Autowired
    SeriesDetailComponent seriesDetailComponent;

    @Autowired
    SeriesKouBeiComponent seriesKouBeiComponent;

    @Autowired
    MainDataApiClient mainDataApiClient;

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(paramName, seriesId).build();
    }

    public CompletableFuture<SeriesKoubeiTabDto> get(int seriesId) {
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(int totalMinutes, Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> {
            refreshOne(xxlLog, seriesId);

        }, xxlLog);
    }

    public void refreshOne(Consumer<String> xxlLog, Integer seriesId) {
        SeriesDetailDto seriesDetailDto = seriesDetailComponent.get(seriesId);

        List<CompletableFuture> tasks = new ArrayList<>();
        SeriesKoubeiTabDto dto = getFromRedis(makeParam(seriesId));
        if (dto == null) {
            dto = new SeriesKoubeiTabDto();
        }

        //口碑评分
        SeriesKoubeiTabDto finalDto = dto;
        tasks.add(seriesKouBeiComponent.get(seriesId).thenAccept(data -> {
            if (data == null || data.getScoreInfo() == null) {
                return;
            }

            double average = data.getScoreInfo().getAverage();
            if (average > 0) {
                finalDto.setAverage(String.format("%.2f", average));
            }
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }));

        //口碑语义
        tasks.add(koubeiApiClient.LoadSeriesPRCType(seriesId, seriesDetailDto.getState() == 40 ? 2 : 1).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                return;
            }

            if (data.getResult() == null || data.getResult().getDimSeriesPRCTypes() == null || data.getResult().getDimSeriesPRCTypes().isEmpty()) {
                return;
            }

            finalDto.setSemanticSummaries(new ArrayList<>());
            data.getResult().getDimSeriesPRCTypes().forEach(prcType -> {
                prcType.getSummary().forEach(summary -> {
                    SeriesKoubeiTabDto.SemanticSummary semanticSummary = new SeriesKoubeiTabDto.SemanticSummary();
                    semanticSummary.setVolume(summary.getVolume());
                    semanticSummary.setTabid(prcType.getTypeKey() == 15 ? 10 : prcType.getTypeKey());
                    semanticSummary.setSentimentkey(summary.getSentimentKey()); //2：负向 3：正向
                    semanticSummary.setIselectronic(data.getResult().isIsElectric());
                    semanticSummary.setCombination(summary.getCombination());
                    semanticSummary.setSummarykey(summary.getSeriesSummaryKey());
                    finalDto.getSemanticSummaries().add(semanticSummary);
                });
            });

            //按照sentimentKey排序
            finalDto.getSemanticSummaries().sort((o1, o2) -> {
                Integer name1 = o1.getSentimentkey();
                Integer name2 = o2.getSentimentkey();
                return name2.compareTo(name1);
            });

        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        }));

        //口碑语义内容：并发调用10次
        CopyOnWriteArrayList<SeriesKoubeiTabDto.Evaluation> oldEvaluations = finalDto.getEvaluations();
        finalDto.setEvaluations(new CopyOnWriteArrayList<>());
//        Map<Integer, List<SeriesKoubeiTabDto.Evaluation>> evaluationMap = new ConcurrentHashMap<>();
        for (int i = 0; i <= 10; i++) {
            int finalI = i;
            tasks.add(koubeiApiClient.getKouBeiInfoList2(seriesId, i, 3, 1).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    oldEvaluations.stream().filter(e -> e.getTabid() == finalI).forEach(e -> finalDto.getEvaluations().add(e));
                    return;
                }

                if (data.getResult() == null || data.getResult().getList() == null || data.getResult().getList().isEmpty()) {
                    return;
                }

                List<SeriesKoubeiTabDto.Evaluation> evaluationList = new LinkedList<>();
                data.getResult().getList().forEach(kb -> {
                    try {
                        SeriesKoubeiTabDto.Evaluation evaluation = new SeriesKoubeiTabDto.Evaluation();
                        evaluation.setTabid(finalI);
                        evaluation.setUserid(kb.getUserid());
                        evaluation.setId(kb.getId());
                        evaluation.setUsername(kb.getNickName());
                        if (StringUtils.isEmpty(kb.getHeadImage())) {
                            evaluation.setUserimage("http://x.autoimg.cn/space/images/head_120X120.png?format=webp");
                        } else {
                            String userImage = "https://i2.autoimg.cn/userscenter" + kb.getHeadImage();
                            userImage = ImageUtils.convertImageUrl(userImage, true, false, false, null, true, false, true);
                            evaluation.setUserimage(userImage);
                        }
                        evaluation.setCarownerlevels(kb.getCarOwnerLevels());
                        evaluation.setCarownername(kb.getSeriesName() + "车主");
                        evaluation.setIsauth(kb.getIsAuthenticated());
                        evaluation.setPosttime(kb.getCreated().substring(0, 11));
                        evaluation.setCommentcount(kb.getCommentCount());
                        evaluation.setViewcount(kb.getVisitCount());
                        evaluation.setHelpfulcount(kb.getHelpfulCount());
                        evaluation.setSpecid(kb.getSpecid());
                        evaluation.setSpecname(kb.getSpecName());
                        evaluation.setBigV(kb.getBigV());
                        evaluation.setUserBigVLevel(kb.getUserBigVLevel());
                        evaluation.setRecommend(kb.getRecommend());
                        evaluation.setIsbattery("");
                        evaluation.setActual_oil_consumption(kb.getActual_oil_consumption());
                        evaluation.setActual_battery_consumption(kb.getActual_battery_consumption());
                        evaluation.setPowertypefeelingid(kb.getPower());
                        evaluation.setPowertype(kb.getPowerType());
                        evaluation.setDistance(kb.getDriven_kilometers());
                        evaluation.setBuyprice("");
                        evaluation.setBuyplace(kb.getBoughtCityName());
                        evaluation.setFeeling_summary(kb.getFeeling_summary());
                        evaluation.setUserBigVLevel(kb.getUserBigVLevel());
                        evaluation.setKoubeitype(kb.getKoubeiType());

                        if (finalI == 0) {
                            SeriesKoubeiTabDto.Evaluation.ContentsDTO content = new SeriesKoubeiTabDto.Evaluation.ContentsDTO();
                            content.setStructuredid(1);
                            content.setStructuredname("满意");
                            String good = kb.getBest().replace("【最满意】", "");
                            good = good.replaceAll("\r\n|\r|\n", "");
                            content.setContent(good);
                            evaluation.getContents().add(content);

                            if (!StringUtils.isEmpty(kb.getWorst())) {
                                SeriesKoubeiTabDto.Evaluation.ContentsDTO content2 = new SeriesKoubeiTabDto.Evaluation.ContentsDTO();
                                content2.setStructuredid(2);
                                content2.setStructuredname("不满意");
                                String bad = kb.getWorst().replace("【最不满意】", "");
                                bad = bad.replaceAll("\r\n|\r|\n", "");
                                content2.setContent(bad);
                                evaluation.getContents().add(content2);
                            }
                        } else {
                            SeriesKoubeiTabDto.Evaluation.ContentsDTO content = new SeriesKoubeiTabDto.Evaluation.ContentsDTO();
                            content.setStructuredid(1);
                            String tabName = KoubeiTabTypeEnum.of(finalI).getTabName(seriesDetailDto.getEnergytype());
                            String sdn = tabName.replace("最", "");
                            if ("不满意".equals(sdn)) {
                                content.setStructuredid(2);
                            }
                            content.setStructuredname(sdn);
                            String good = kb.getFeeling().replace("【" + tabName + "】", "");
                            good = good.replaceAll("\r\n|\r|\n", "");
                            content.setContent(good);
                            evaluation.getContents().add(content);

                            if (!StringUtils.isEmpty(kb.getWorst())) {
                                SeriesKoubeiTabDto.Evaluation.ContentsDTO content2 = new SeriesKoubeiTabDto.Evaluation.ContentsDTO();
                                content2.setStructuredid(2);
                                content2.setStructuredname(tabName);
                                String bad = kb.getFeeling().replace("【" + tabName + "】", "");
                                bad = bad.replaceAll("\r\n|\r|\n", "");
                                content2.setContent(bad);
                                evaluation.getContents().add(content2);
                            }
                        }

                        //判断只获取前三个图片，否则全部
                        List<String> photoList;
                        if (kb.getPhotos().size() > 3) {
                            photoList = kb.getPhotos().subList(0, 3);
                        } else {
                            photoList = kb.getPhotos();
                        }

                        //判断集合是否为空
                        if (!photoList.isEmpty()) {
                            photoList.forEach(photo -> evaluation.getPiclist().add(ImageUtils.convertImageUrl(photo, true, false, true, null, true, false, true)));
                        }

                        evaluation.setLinkurl("autohome://reputation/reputationdetail?seriesid=" + seriesId + "&seriesname=" + UrlUtil.encode(seriesDetailDto.getName()) + "&koubeiid=" + kb.getId() + "&specid=" + kb.getSpecid() + "&specname=" + UrlUtil.encode(kb.getSpecName()) + "&from=1");
                        evaluation.setCommentlinkurl("autohome://reputation/reputationdetail?seriesid=" + seriesId + "&specid=" + kb.getSpecid() + "&koubeiid=" + kb.getId() + "&showcomment=1&from=36");
                        evaluationList.add(evaluation);
                    } catch (Exception ex) {
                        xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(ex));
                    }
                });

                finalDto.getEvaluations().addAll(evaluationList);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }));

            ThreadUtil.sleep(20);
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

//        //按照key顺序取值
//        for (int i = 0; i <= 10; i++) {
//            List<SeriesKoubeiTabDto.Evaluation> evaluationList = evaluationMap.get(i);
//            if (evaluationList != null) {
//                finalDto.getEvaluations().addAll(evaluationList);
//            } else {
//                int finalI = i;
//                oldEvaluations.stream().filter(e -> e.getTabid() == finalI).forEach(e -> finalDto.getEvaluations().add(e));
//            }
//        }

        if (finalDto.getEvaluations().isEmpty()) {
            delete(makeParam(seriesId));
        } else {
            update(makeParam(seriesId), finalDto);
        }
    }
}
