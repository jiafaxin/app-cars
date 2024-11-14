package com.autohome.app.cars.service.components.subsidy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chengjincheng
 * @date 2024/5/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecSubsidyCountDto {

    int specId;

    int subsidyCount;

    boolean hasSubsidy;

    boolean hasFactoryBenefits;
}
