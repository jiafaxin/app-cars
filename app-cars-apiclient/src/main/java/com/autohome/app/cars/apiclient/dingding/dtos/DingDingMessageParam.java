package com.autohome.app.cars.apiclient.dingding.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by dx on 2024/9/5
 * 钉钉消息请求实体
 */
@Data
public class DingDingMessageParam {
    @JsonProperty("SendWho")
    private String SendWho;
    @JsonProperty("Type")
    private String Type;
    @JsonProperty("Title")
    private String Title;
    @JsonProperty("Message")
    private String Message;
}
