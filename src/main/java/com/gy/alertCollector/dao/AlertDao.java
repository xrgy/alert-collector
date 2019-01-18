package com.gy.alertCollector.dao;


import com.gy.alertCollector.entity.AlertEntity;
import com.gy.alertCollector.entity.AlertSeverityView;
import com.gy.alertCollector.entity.TestEntity;
import com.gy.alertCollector.entity.topo.AlertAlarmInfoSelect;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertDao {

    Map<Integer,Integer> getSeverityCountByMonitor(String monitorUuid);

    List<AlertAlarmInfoSelect> getAlertInfoBymonitorUuids(List<String> monitorUuids);

    boolean deleteAlertResourceBymonitoruuid(String monitorUuid);
}
