package com.gy.alertCollector.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertReceiveView {

    @JsonProperty("receiver")
    private String receiver;

    @JsonProperty("status")
    private String status;

    @JsonProperty("alerts")
    private List<WebhookAlertEntity> alerts;

    @JsonProperty("version")
    private String verion;

    @JsonProperty("groupKey")
    private String groupKey;
}
