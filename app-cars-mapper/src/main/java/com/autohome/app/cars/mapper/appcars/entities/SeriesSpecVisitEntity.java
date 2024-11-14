package com.autohome.app.cars.mapper.appcars.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author chengjincheng
 * @date 2024/5/8
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeriesSpecVisitEntity {

    private int id;

    private String dt;

    private int series_id;

    private String series_name;

    private int all_uv;

    private String spec_uv;

    private int is_del;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created_stime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modified_stime;
}
