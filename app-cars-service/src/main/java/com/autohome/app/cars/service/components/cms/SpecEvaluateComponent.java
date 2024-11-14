package com.autohome.app.cars.service.components.cms;

import com.autohome.app.cars.apiclient.cms.CmsApiClient;
import com.autohome.app.cars.apiclient.cms.dtos.SpecEvaluateItemResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.cms.dtos.SpecEvaluateDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 源接口对接人：沈巨明
 */
@Component
@RedisConfig
@Slf4j
//@DBConfig(tableName = "spec_evaluate")
public class SpecEvaluateComponent extends BaseComponent<SpecEvaluateDto> {

    private static String specIdParamName = "specId";
    @Autowired
    private CmsApiClient cmsApiClient;

    private TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecEvaluateDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public CompletableFuture<List<SpecEvaluateDto>> get(List<Integer> specIdList) {
        return baseGetListAsync(specIdList.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    @Override
    protected SpecEvaluateDto sourceData(TreeMap<String, Object> params) {
        try {
            AtomicReference<SpecEvaluateDto> res = new AtomicReference<>();
            int specId = (int) params.get(specIdParamName);
            CompletableFuture<BaseModel<List<SpecEvaluateItemResult>>> future = cmsApiClient.getSpecEvaluateItems(String.valueOf(specId));
            future.thenAccept(result -> {
                if (result != null) {
                    SpecEvaluateDto dto = new SpecEvaluateDto();
                    result.getResult().forEach(item -> {
                        if (item != null) {
                            dto.setSpecId(item.getSpecid());
                            dto.setEvaluateItemResult(item);
                            res.set(dto);
                           
                        }
                    });
                    update(makeParam(specId), dto);
                }
            }).join();
            return res.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Map<TreeMap<String, Object>, SpecEvaluateDto> sourceDatas(List<TreeMap<String, Object>> params) {
        try {
            Map<TreeMap<String, Object>, SpecEvaluateDto> res = new LinkedHashMap<>();
            List<Integer> specids = params.stream().map(x -> (int) x.get(specIdParamName)).collect(Collectors.toList());
            List<List<Integer>> grouplist = new ArrayList<>();
            int groupSize = 5;
            int listSize = specids.size();
            for (int i = 0; i < listSize; i += groupSize) {
                grouplist.add(specids.subList(i, Math.min(listSize, i + groupSize)));
            }

            List<CompletableFuture> tasks = new ArrayList<>();
            for (List<Integer> group : grouplist) {
                tasks.add(cmsApiClient.getSpecEvaluateItems(StringUtils.join(group, ",")).thenAccept(result -> {
                    group.forEach(spec->{
                        SpecEvaluateDto dto = new SpecEvaluateDto();
                        if (result != null) {
                            Optional<SpecEvaluateItemResult> first = result.getResult().stream().filter(i -> i.getSpecid() == spec.intValue()).findFirst();
                            if(first.isPresent()){
                                SpecEvaluateItemResult item = first.get();
                                dto.setSpecId(item.getSpecid());
                                dto.setEvaluateItemResult(item);
                            }
                        }
                        res.put(makeParam(spec), dto);
                        update(makeParam(spec), dto);
                    });
                }));
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
            return res;
        } catch (Exception e) {
            log.error("参配ah100 异常",e);
        }
        return null;
    }

}
