package com.autohome.app.cars.service.components.car;

import com.autohome.app.cars.apiclient.testdata.TestDataApiClient;
import com.autohome.app.cars.apiclient.testdata.dtos.TestStandardResult;
import com.autohome.app.cars.common.BaseModel;
import com.autohome.app.cars.common.utils.ExceptionUtil;
import com.autohome.app.cars.common.utils.ThreadUtil;
import com.autohome.app.cars.mapper.popauto.SpecMapper;
import com.autohome.app.cars.service.common.BaseComponent;
import com.autohome.app.cars.service.common.DBConfig;
import com.autohome.app.cars.service.common.RedisConfig;
import com.autohome.app.cars.service.components.car.dtos.SpecDetailDto;
import com.autohome.app.cars.service.components.car.dtos.SpecTestDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RedisConfig
@DBConfig(tableName = "spec_testdata")
public class SpecTestDataComponent extends BaseComponent<SpecTestDataDto> {
    final static String specIdParamName = "specId";

    @Autowired
    SpecMapper specMapper;
    @Autowired
    TestDataApiClient testDataApiClient;

    TreeMap<String, Object> makeParam(int specId) {
        return ParamBuilder.create(specIdParamName, specId).build();
    }

    public CompletableFuture<SpecTestDataDto> get(int specId) {
        return baseGetAsync(makeParam(specId));
    }

    public SpecTestDataDto getSync(int specId) {
        return baseGet(makeParam(specId));
    }

    public CompletableFuture<List<SpecTestDataDto>> get(List<Integer> specIds) {
        return baseGetListAsync(specIds.stream().map(x -> makeParam(x)).collect(Collectors.toList()));
    }

    public void refreshAll(Consumer<String> xxlLog) {
        List<Integer> specIds = specMapper.getAllSpecIds();
        List<Integer> cvSpecIds = specMapper.getAllCvSpecIds();
        specIds.addAll(cvSpecIds);
        specIds = specIds.stream().distinct().collect(Collectors.toList());
        specIds.forEach(specId -> {
            try {
                getData(specId);
                ThreadUtil.sleep(50);
                xxlLog.accept("specId = " + specId + " success");
            } catch (Exception e) {
                xxlLog.accept("specId = " + specId + " fail:" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    public void getData(int specId){
        testDataApiClient.getTestedDataIdBySpecIds(String.valueOf(specId)).thenAccept(testedDataId -> {
            if(Objects.nonNull(testedDataId) && testedDataId.getReturncode()==0 && Objects.nonNull(testedDataId.getResult())){
                if(!testedDataId.getResult().isEmpty()){
                    testedDataId.getResult().forEach(x -> {
                        BaseModel<TestStandardResult> baseModel = testDataApiClient.getTestStandardItemList(x.getDataId(), 0).join();
                        if(baseModel != null && baseModel.getReturncode() == 0){
                            if(baseModel.getResult() != null){
                                baseModel.getResult().setDataId(x.getDataId());
                                SpecTestDataDto dto = new SpecTestDataDto();
                                dto.setSpecId(specId);
                                dto.setTestStandardResult(baseModel.getResult());
                                update(makeParam(specId), dto);
                            }else{
                                delete(makeParam(specId));
                            }
                        }
                    });
                }else{
                    delete(makeParam(specId));
                }
            }

        }).join();
    }

}
