package com.autohome.app.cars.service.components.car.dtos;

import com.autohome.app.cars.common.utils.PriceUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class SeriesDetailExtendDto {
    /**
     * 车系id
     */
    private int id;

    /**
     * 25图车型Id
     */
    private int delegate25SpecId;

    /**
     * 新能源关联车系id
     */
    private int newenergySeriesId;
}
