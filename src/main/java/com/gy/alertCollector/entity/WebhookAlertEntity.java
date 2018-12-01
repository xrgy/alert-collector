package com.gy.alertCollector.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class WebhookAlertEntity {

    @JsonProperty("status")
    private String status;

    @JsonProperty("labels")
    private Map<String,String> labels;

    @JsonProperty("annotations")
    private Map<String,String> annotations;

    @JsonProperty("startsAt")
    private String startsAt;

    @JsonProperty("endsAt")
    private String endsAt;

    @JsonProperty("generatorURL")
    private String generatorUrl;
}
