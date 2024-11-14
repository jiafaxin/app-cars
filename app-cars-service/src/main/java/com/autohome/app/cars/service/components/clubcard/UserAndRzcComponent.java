package com.autohome.app.cars.service.components.clubcard;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import autohome.rpc.car.app_cars.v1.carcfg.GetBaiKeInfoResponse;
import com.autohome.app.cars.apiclient.user.UserApiClient;
import com.autohome.app.cars.apiclient.user.dtos.UserAuthSeriesResult;
import com.autohome.app.cars.apiclient.user.dtos.UserDefaultCarResult;
import com.autohome.app.cars.apiclient.user.dtos.UserInfoResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ImageSizeEnum;
import com.autohome.app.cars.common.utils.ImageUtils;
import com.autohome.app.cars.service.ThreadPoolUtils;
import com.autohome.app.cars.service.components.car.BrandDetailComponent;
import com.autohome.app.cars.service.components.car.dtos.BrandDetailDto;
import com.autohome.app.cars.service.components.clubcard.dto.SeriesClubCardDataDto;
import com.autohome.app.cars.service.components.clubcard.dto.UserAndRzcDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author wbs
 * @date 2024/6/7
 */
@Slf4j
@Component
public class UserAndRzcComponent {

    @Autowired
    UserApiClient userApiClient;

    @Autowired
    private BrandDetailComponent brandDetailComponent;

    public void setUserAndRzcInfo(SeriesClubCardDataDto target, int userId) {

        UserAndRzcDataDto dto = new UserAndRzcDataDto();

        List<CompletableFuture> tasks = new ArrayList<>();

        tasks.add(CompletableFuture.supplyAsync(() -> userApiClient.getUserInfoList(userId).join(), ThreadPoolUtils.defaultThreadPoolExecutor)
                .thenAccept(res -> dto.setUserInfo(res))
                .exceptionally(e -> {
                    e.printStackTrace();
                    log.error("getUserInfoList---error:{}", e.getMessage());
                    return null;
                }));

        tasks.add(CompletableFuture.supplyAsync(() ->userApiClient.getUserAuthseries(userId).join(), ThreadPoolUtils.defaultThreadPoolExecutor)
                .thenAccept(res -> dto.setUserRzc(res))
                .exceptionally(e -> {
                    e.printStackTrace();
                    log.error("getUserAuthseries---error:{}", e.getMessage());
                    return null;
                }));

        tasks.add(CompletableFuture.supplyAsync(() ->userApiClient.getUserDefaultCar(userId).join(), ThreadPoolUtils.defaultThreadPoolExecutor)
                .thenAccept(res -> dto.setUserDefault(res))
                .exceptionally(e -> {
                    e.printStackTrace();
                    log.error("getUserDefaultCar---error:{}", e.getMessage());
                    return null;
                }));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        BaseModel<List<UserInfoResult>> userInfo = dto.getUserInfo();

        if (null != userInfo && null != userInfo.getResult() && 0 == userInfo.getReturncode() && !CollectionUtils.isEmpty(userInfo.getResult())) {
            UserInfoResult user = userInfo.getResult().get(0);
            target.setUsername(user.getNewnickname());
            String headImage = user.getHeadimage();
            if ("http://x.autoimg.cn/account/Images/mr001.png".equals(headImage)) {
                headImage = "http://x.autoimg.cn/account/Images/mr001.png?format=webp";
            } else {
                headImage = ImageUtils.convertImageUrl(headImage, true, false, false, ImageSizeEnum.ImgSize_1x1_120x120, true, false, true);
            }
            target.setHeadimg(headImage);
        }

        BaseModel<List<UserAuthSeriesResult>> authCar = dto.getUserRzc();

        BaseModel<List<UserDefaultCarResult>> userDefault = dto.getUserDefault();

        boolean hasAuthCar = null != authCar && null != authCar.getResult() && 0 == authCar.getReturncode() && !CollectionUtils.isEmpty(authCar.getResult());

        if (hasAuthCar) {
            UserAuthSeriesResult.AuthseriesResult rzc = authCar.getResult().get(0).getList().stream().max(Comparator.comparing(UserAuthSeriesResult.AuthseriesResult::getId)).get();
            target.setAuthseriesid(rzc.getSeriesId());
            target.setAuthseriesname(rzc.getSeriesName());
            target.setAuthlevel(rzc.getLevels());
            if (authCar.getResult().get(0).getList().size() > 1) {
                target.setRzcList(authCar.getResult().get(0).getList());
            }
        }

        boolean hasDefaultCar = null != userDefault && null != userDefault.getResult() && 0 == userDefault.getReturncode() && !CollectionUtils.isEmpty(userDefault.getResult());

        if (hasDefaultCar && hasAuthCar) {
            Optional<UserDefaultCarResult> optional = userDefault.getResult().stream().filter(e -> (1 == e.getIsdefault()) && userId == e.getUserid()).findAny();
            if (optional.isPresent()) {
                UserDefaultCarResult defaultCar = optional.get();
                Optional<UserAuthSeriesResult.AuthseriesResult> rzcOpt = authCar.getResult().get(0).getList().stream().filter(e -> defaultCar.getSeriesid() == e.getSeriesId()).findAny();
                if (rzcOpt.isPresent()) {
                    UserAuthSeriesResult.AuthseriesResult defaultRzc = rzcOpt.get();
                    target.setAuthseriesid(defaultRzc.getSeriesId());
                    target.setAuthseriesname(defaultRzc.getSeriesName());
                    target.setAuthlevel(defaultRzc.getLevels());
                }
            }
        }


    }

    /**
     * 批量获取用户数据
     * @param
     * @param userId
     */
    public Map<Integer,GetBaiKeInfoResponse.UserInfo> setUserConfigKouBeiInfo(String userId) {

        UserAndRzcDataDto dto = new UserAndRzcDataDto();

        List<CompletableFuture> tasks = new ArrayList<>();

        Map<Integer,GetBaiKeInfoResponse.UserInfo> resultMap = new HashMap<>();//key：userId value：user信息

        List<String> userIds = Arrays.asList(userId.split(","));

        tasks.add(CompletableFuture.supplyAsync(() -> userApiClient.batchGetUserInfoList(userId).join(), ThreadPoolUtils.defaultThreadPoolExecutor)
                .thenAccept(res -> dto.setUserInfo(res))
                .exceptionally(e -> {
                    e.printStackTrace();
                    log.error("getUserInfoList---error:{}", e.getMessage());
                    return null;
                }));

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        try {
            BaseModel<List<UserInfoResult>> userInfo = dto.getUserInfo();
            if (null != userInfo && null != userInfo.getResult() && 0 == userInfo.getReturncode() && !CollectionUtils.isEmpty(userInfo.getResult())) {
                for (UserInfoResult info : userInfo.getResult()) {
                    if (userIds.contains(info.getUserid()+"")) {
                        String headImage = info.getHeadimage();
                        if ("http://x.autoimg.cn/account/Images/mr001.png".equals(headImage)) {
                            headImage = "http://x.autoimg.cn/account/Images/mr001.png?format=webp";
                        } else {
                            headImage = ImageUtils.convertImageUrl(headImage, true, false, false, ImageSizeEnum.ImgSize_1x1_120x120, true, false, true);
                        }
                        GetBaiKeInfoResponse.UserInfo userDetail = GetBaiKeInfoResponse.UserInfo.newBuilder()
                                .setUsername(info.getNewnickname())
                                .setAvatar(headImage)
                                .build();
                        resultMap.put(info.getUserid(),userDetail);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getbaikeinfo-设置用户信息异常",ex);
        }
        return resultMap;
    }
}
