package com.gy.alertCollector.service;


import com.gy.alertCollector.entity.TestEntity;
import com.gy.alertCollector.entity.WebhookAlertEntity;
import com.gy.alertCollector.entity.topo.AlertAlarmInfo;
import com.gy.alertCollector.entity.topo.TopoAlertView;

import java.util.List;
import java.util.Map;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertService {
    Map<Integer,Integer> getSeverityCountByMonitor(String monitorUuid);
    /**
     *
     * @param monitorUuids
     * @return
     */
    List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids);

    /**
     * 删除监控记录级联告警删除该monitor的告警记录
     * @param monitorUuid
     * @return
     */
    boolean deleteAlertResourceBymonitoruuid(String monitorUuid);
}
