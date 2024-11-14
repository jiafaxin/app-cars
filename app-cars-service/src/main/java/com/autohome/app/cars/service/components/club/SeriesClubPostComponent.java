package com.autohome.app.cars.service.components.club;
import com.autohome.app.cars.apiclient.club.ClubApiClient;
import com.autohome.app.cars.apiclient.club.dtos.SeriesClubPostResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.JsonUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.car.dtos.CarPhotoCountDto;
import com.autohome.app.cars.service.components.club.dtos.SeriesClubPostDto;
import com.autohome.app.cars.service.components.vr.dtos.SpecVrInfoDto;
import com.autohome.autolog4j.common.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
//@DBConfig(tableName = "series_club_post")
@Slf4j
public class SeriesClubPostComponent extends BaseComponent<SeriesClubPostDto>{
    @Autowired
    ClubApiClient clubApiClient;

    final static String paramSeriesId = "seriesId";
    final static String paramDatatype = "datatype";
    final static String paramProfile = "profile";
    final static String paramSort = "sort";

    // 定义日期时间格式
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final Map<Integer,String> paramMap = new HashMap<>();

    static {
        /**
         * value:datatype,profile，sort
         * datatype:1: 热门贴 2: 入池贴
         * profile:用户画像标签xingqu,chezhu,maiche
         * sort:排序 2 发布时间倒叙 5 热度
         */
        paramMap.put(1,"1,xingqu,2");
        paramMap.put(2,"1,xingqu,5");
        paramMap.put(3,"1,chezhu,2");
        paramMap.put(4,"1,chezhu,5");
        paramMap.put(5,"1,maiche,2");
        paramMap.put(6,"1,maiche,5");
        paramMap.put(7,"1,,2");
        paramMap.put(8,"1,,5");
        paramMap.put(9,"2,xingqu,2");
        paramMap.put(10,"2,xingqu,5");
        paramMap.put(11,"2,chezhu,2");
        paramMap.put(12,"2,chezhu,5");
        paramMap.put(13,"2,maiche,2");
        paramMap.put(14,"2,maiche,5");
        paramMap.put(15,"2,,2");
        paramMap.put(16,"2,,5");
    }

    TreeMap<String, Object> makeParam(int seriesId,int datatype,String profile,int sort) {
        return BaseComponent.ParamBuilder.create()
                .add(paramSeriesId,seriesId)
                .add(paramDatatype,datatype)
                .add(paramProfile,profile)
                .add(paramSort,sort)
                .build();
    }

    /**
     *
     * @param seriesId
     * @return
     */
    public CompletableFuture<SeriesClubPostDto> get(int seriesId,int datatype,String profile,int sort){
        return baseGetAsync(makeParam(seriesId,datatype,profile,sort));
    }

    public String getData(TreeMap<String, Object> params) {
        int seriesId = null == params.get("seriesId") ? 0 : (int) params.get("seriesId");
        int datatype = null == params.get("datatype") ? 0 : (int) params.get("datatype");
        String profile = null == params.get("profile") ? "" :  params.get("profile").toString();
        int sort = null == params.get("sort") ? 0 : (int) params.get("sort");

        SeriesClubPostDto dto = get(seriesId, datatype, profile, sort).join();
        return JsonUtil.toString(dto);
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {
        loopSeries(totalMinutes, seriesId -> {
            refreshOne(seriesId).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                log.error("同步失败" + seriesId, e);
                return null;
            });
        }, xxlLog);
    }

    CompletableFuture<Object> refreshOne(int seriesId) {
        for (int i = 1; i <= 16; i++) {
            SeriesClubPostDto dto = new SeriesClubPostDto();
            SeriesClubPostDto.SeriesClubPostBean post = new SeriesClubPostDto.SeriesClubPostBean();
            dto.setSeriesId(seriesId);
            dto.setPost(post);
            String params = paramMap.get(i);
            String[] split = params.split(",");
            BaseModel<SeriesClubPostResult> clubPost = null;
            try {
                clubPost = clubApiClient.getSeriesSugarPostList(seriesId, Long.parseLong(split[0]), split[1], Long.parseLong(split[2]), 20L).join();
            } catch (Exception ex) {
                log.warn("车系页-获取论坛帖子失败",ex);
            }
            if (clubPost == null || clubPost.getResult() == null || CollectionUtils.isEmpty(clubPost.getResult().getItems())) {
                continue;
            }
            List<SeriesClubPostResult.ItemBean> neededPosts = clubPost.getResult().getItems().stream()
                    .filter(x -> isBetweenDate(x.getPublishTime(), Integer.parseInt(split[2])))
                    .toList();
            if (!CollectionUtils.isEmpty(neededPosts)){
                int random = new Random().nextInt(neededPosts.size());
                SeriesClubPostResult.ItemBean item = neededPosts.get(random);//随机选取一个满足日期条件的帖子
                post.setTopicId(item.getTopicId());
                post.setTitle(item.getTitle());
                post.setScheme(item.getScheme());
                String[] pics = item.getPic().split(",");
                if (pics.length > 0){
                    post.setPic(pics[0]);
                }
                post.setPublishTime(item.getPublishTime());
                post.setListScheme(item.getListscheme());
            }
            update(makeParam(seriesId,Integer.parseInt(split[0]),split[1],Integer.parseInt(split[2])), dto);
        }
        return null;
    }

    /**
     * 判断帖子发布日期距离当前日期是否在要求范围
     * 精确到分秒
     * @param publishTime 帖子发布日期
     * @param sortType 选取规则
     * @return
     */
    private boolean isBetweenDate(String publishTime,int sortType) {
        try {
            //解析输入的日期时间字符串
            LocalDateTime inputDateTime = LocalDateTime.parse(publishTime, DATE_TIME_FORMATTER);

            //获取当前系统时间
            LocalDateTime currentDateTime = LocalDateTime.now();

            //计算两个时间点之间的差值
            Duration duration = Duration.between(inputDateTime, currentDateTime);

            //选取规则为热度(5)，判断是否在90天以内；选取规则为发布时间(2)，判断是否在7天以内
            if (sortType == 5){
                return Math.abs(duration.toDays()) <= 90;
            }else{
                return Math.abs(duration.toDays()) <= 7;
            }
        } catch (DateTimeParseException e) {
            // 如果日期格式不正确，抛出解析异常
            System.err.println("Invalid date format: " + publishTime);
            return false;
        }
    }
}
