package com.gy.alertCollector.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/9.
 */
@Getter
@Setter
public class AlertSeverityView {

    @JsonProperty("severity")
    private int severity;

    @JsonProperty("count")
    private int count;
}
