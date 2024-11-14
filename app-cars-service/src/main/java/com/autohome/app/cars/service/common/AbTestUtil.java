package com.autohome.app.cars.service.common;

import com.autohome.app.cars.apiclient.abtest.dtos.ABTestDto;
import com.autohome.app.cars.common.utils.ListUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class AbTestUtil {
    public static boolean getAbtestHitStatus(ABTestDto abTestDto, String testId, String version){
        boolean hit = false;
        if(StringUtils.isAnyEmpty(testId,version) || Objects.isNull(abTestDto)){
            return hit;
        }
        if(Objects.nonNull(abTestDto) && Objects.nonNull(abTestDto.getResult()) && ListUtil.isNotEmpty(abTestDto.getResult().getList())){
            Optional<ABTestDto.ResultDTO.ListDTO> first = abTestDto.getResult().getList().stream().filter(i -> StringUtils.equals(i.getVariable(), testId)).findFirst();
            if (first.isPresent()) {
                hit = StringUtils.equals(version,first.get().getVersion());
            }
        }
        return hit;
    }

    public static String getAbtestVersion(ABTestDto abTestDto, String testId){
        String version = "";
        if(StringUtils.isEmpty(testId) || Objects.isNull(abTestDto)){
            return version;
        }
        if(Objects.nonNull(abTestDto) && Objects.nonNull(abTestDto.getResult()) && ListUtil.isNotEmpty(abTestDto.getResult().getList()) ){
            Optional<ABTestDto.ResultDTO.ListDTO> first = abTestDto.getResult().getList().stream().filter(i -> StringUtils.equals(i.getVariable(), testId)).findFirst();
            if (first.isPresent()) {
                version = first.get().getVersion();
            }
        }
        return version;
    }
}
