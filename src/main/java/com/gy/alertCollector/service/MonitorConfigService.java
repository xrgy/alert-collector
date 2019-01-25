package com.gy.alertCollector.service;


import com.gy.alertCollector.entity.monitorConfig.AlertCommonRule;
import com.gy.alertCollector.entity.monitorConfig.Metrics;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
public interface MonitorConfigService {

    public AlertCommonRule getAlertRuleByAlertName(String name, String ruleid);

    public Metrics getMetricByUuid(String uuid);

}
