package com.gy.alertCollector.entity.monitorConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/25.
 */
@Getter
@Setter
public class AlertCommonRule {
    private String uuid;

    private String metricUuid;

    private String metricDisplayUnit;

    private String templateUuid;

    private int severity;
}
