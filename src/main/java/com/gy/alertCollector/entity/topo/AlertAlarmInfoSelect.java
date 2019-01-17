package com.gy.alertCollector.entity.topo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/17.
 */
@Getter
@Setter
public class AlertAlarmInfoSelect {

    private String monitorUuid;

    private int severity;

    private int severityCount;
}
