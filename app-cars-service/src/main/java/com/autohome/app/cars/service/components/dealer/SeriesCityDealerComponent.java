package com.autohome.app.cars.service.components.dealer;

import com.autohome.app.cars.apiclient.club.DriveApiClient;
import com.autohome.app.cars.apiclient.dealer.DealerApiClient;
import com.autohome.app.cars.apiclient.dealer.YoucheApiClient;
import com.autohome.app.cars.apiclient.dealer.dtos.FactOrGovSpecsResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.dealer.dtos.SeriesCityDealerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * http://api.youche.in.autohome.com.cn/dealer/getMoreDealerListCount
 * http://drive.api.corpautohome.com/v2/series/{seriesId}/cities/{cityId}/test-drive?_appId=app
 * http://dealer.api.lq.autohome.com.cn/statistics/policy/listFactOrGovSpecs?_appId=app&cityId=110100&seriesId=18
 * //TODO 如果数据没有了咋办？
 */
@Component
@DBConfig(tableName = "series_city_dealer")
public class SeriesCityDealerComponent extends BaseComponent<SeriesCityDealerInfo> {

    @Autowired
    YoucheApiClient youcheApiClient;

    @Autowired
    DriveApiClient driveApiClient;


    @Autowired
    DealerApiClient dealerApiClient;

    final static String seriesIdParamName = "seriesId";
    final static String cityParamName = "cityId";

    TreeMap<String, Object> makeParam(int seriesId, int cityId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).add(cityParamName, cityId).build();
    }

    public CompletableFuture<SeriesCityDealerInfo> get(int seriesId, int cityId) {
        return baseGetAsync(makeParam(seriesId, cityId));
    }

    public void refreshAll(int totalMinutes,Consumer<String> xxlLog) {
        loopSeriesCity(totalMinutes, (seriesId, cityId) -> {

            SeriesCityDealerInfo oldDto = baseGet(makeParam(seriesId, cityId));

            SeriesCityDealerInfo dto = new SeriesCityDealerInfo();
            dto.setSeriesId(seriesId);
            dto.setCityId(cityId);

            List<CompletableFuture> tasks = new ArrayList<>();

            //是否有接口异常了,任何一个接口报错了，都将此值设置
            AtomicBoolean anyError = new AtomicBoolean(false);

            //4s保养
            tasks.add(by4s(seriesId, cityId, dto, oldDto, anyError, xxlLog));

            tasks.add(listFactOrGovSpecs(seriesId, cityId, dto, oldDto, anyError, xxlLog));

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).thenAccept(x -> {
                if (!anyError.get() && dto.getBy4s() == null) {
                    delete(makeParam(seriesId, cityId));
                    return;
                }
                update(makeParam(seriesId, cityId), dto);
            });
        }, xxlLog);
    }

    CompletableFuture by4s(int seriesId,int cityId, SeriesCityDealerInfo dto,SeriesCityDealerInfo oldDto,AtomicBoolean anyError,Consumer<String> xxlLog){
        return youcheApiClient.getMoreDealerListCount(seriesId, cityId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {  //此时说明接口报错了，TODO 有待考虑returncode!=0是否算报错
                if(oldDto != null){
                    dto.setBy4s(oldDto.getBy4s());
                }
                anyError.set(true);
                return;
            }
            if(data.getResult()==null){
                return;
            }
            dto.setBy4s(new SeriesCityDealerInfo.By4s() {{
                setHavedealers(data.getResult().isHavedealers());
                setJumpdealerlisturl(data.getResult().getJumpdealerlisturl());
            }});
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "4s保养 失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        });
    }


    CompletableFuture listFactOrGovSpecs(int seriesId,int cityId, SeriesCityDealerInfo dto,SeriesCityDealerInfo oldDto,AtomicBoolean anyError,Consumer<String> xxlLog){
        return dealerApiClient.listFactOrGovSpecs(seriesId, cityId).thenAccept(data -> {
            if (data == null || data.getReturncode() != 0) {
                if(oldDto != null){
                    dto.setPricePolicy(oldDto.getPricePolicy());
                }
                anyError.set(true);
                return;
            }
            if(data.getResult()==null){
                return;
            }

            FactOrGovSpecsResult item = data.getResult().stream().max(Comparator.comparing(FactOrGovSpecsResult::getPolicyValueSum)).orElse(null);
            if(item!=null) {
                int maxNewPolicies = item.getPolicies().stream().filter(x -> x.getPolicyType().equals(4) || x.getPolicyType().equals(8)).mapToInt(x -> x.getPolicyValue()).max().orElse(-1);
                int maxReplacePolicies = item.getPolicies().stream().filter(x -> x.getPolicyType().equals(5) || x.getPolicyType().equals(10)).mapToInt(x -> x.getPolicyValue()).max().orElse(-1);

                dto.setPricePolicy(new SeriesCityDealerInfo.PricePolicy() {{
                    setNewCar(maxNewPolicies);
                    setReplaceCar(maxReplacePolicies);
                }});
            }
        }).exceptionally(e -> {
            xxlLog.accept(seriesId + "4s试驾 失败:" + ExceptionUtil.getStackTrace(e));
            return null;
        });
    }


}
