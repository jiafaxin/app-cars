package com.autohome.app.cars.service.components.cms;

import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.CmsTestEvalItemsResult;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.components.cms.dtos.AHEvaluateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
@DBConfig(tableName = "series_cms_ahevaluate")
public class SeriesCmsTestEvalComponent extends BaseComponent<AHEvaluateDto> {

    @Autowired
    CmsApiClient cmsApiClient;

    static String seriesIdParamName = "seriesId";

    TreeMap<String, Object> makeParam(int seriesId) {
        return ParamBuilder.create(seriesIdParamName, seriesId).build();
    }

    public CompletableFuture<AHEvaluateDto> get(int seriesId){
        return baseGetAsync(makeParam(seriesId));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        loopSeries(30,seriesId->{
            cmsApiClient.getAHEvaluateItemsWithPointView(seriesId).thenAccept(data -> {
                if (data == null || data.getReturncode() != 0) {
                    return;
                }

                if (data.getResult() == null ||data.getResult().getEvaluatedto()==null||data.getResult().getEvaluatedto().getEvaluateitems()==null||data.getResult().getEvaluatedto().getEvaluateitems().size()==0) {
                    delete(makeParam(seriesId));
                    return;
                }
                CmsTestEvalItemsResult.EvaluatedtoBean.EvaluateitemsBean miludata  = data.getResult().getEvaluatedto().getEvaluateitems().stream().filter(x->x.getCategoryname().equals("0-100km/h加速时间")).findFirst().orElse(null);
                CmsTestEvalItemsResult.EvaluatedtoBean.EvaluateitemsBean optional0100  = data.getResult().getEvaluatedto().getEvaluateitems().stream().filter(x->x.getCategoryname().equals("麋鹿测试成绩")).findFirst().orElse(null);
                if(miludata==null&&optional0100==null) {
                    delete(makeParam(seriesId));
                    return;
                }
                AHEvaluateDto dto = new AHEvaluateDto();
                if(miludata!=null) {
                    dto.setMiludata(new AHEvaluateDto.Item() {{
                        setName(miludata.getCategoryname());
                        setData(miludata.getData());
                    }});
                }
                if(optional0100!=null) {
                    dto.setMiludata(new AHEvaluateDto.Item() {{
                        setName(optional0100.getCategoryname());
                        setData(optional0100.getData());
                    }});
                }
                update(makeParam(seriesId), dto);
            }).exceptionally(e -> {
                xxlLog.accept(seriesId + "失败:" + ExceptionUtil.getStackTrace(e));
                return null;
            }).join();
        },xxlLog);

    }

}
