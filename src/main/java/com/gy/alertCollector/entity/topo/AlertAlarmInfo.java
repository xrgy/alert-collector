package com.gy.alertCollector.entity.topo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by gy on 2019/1/17.
 */
@Getter
@Setter
public class AlertAlarmInfo {

    private String monitorUuid;

    private Map alarm;
}
