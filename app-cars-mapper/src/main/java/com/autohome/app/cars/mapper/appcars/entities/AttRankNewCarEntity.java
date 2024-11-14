package com.autohome.app.cars.mapper.appcars.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author chengjincheng
 * @date 2024/3/27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttRankNewCarEntity {

    private int id;

    private String dt;

    private int seriesId;

    private String seriesName;

    private int levelId;

    private Integer att;

    private String onTime;

    private Integer seriesTagId;

    private Integer articleId;

    private Integer rankNum;

    private int isDel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdStime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifiedStime;
}
