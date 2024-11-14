package com.autohome.app.cars.apiclient.reply.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewSeriesAiViewPointResult{
    private Integer contentSeq;//排序值

    private String pointTitle;//AI观点标题

    private String requestNo;//唯一请求标识，一个车系下多条观点该值一样
}
