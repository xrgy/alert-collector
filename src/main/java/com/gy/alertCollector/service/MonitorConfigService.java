package com.gy.alertCollector.service;


import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
public interface MonitorConfigService {

    public CompletionStage<Optional<Object>> getAlertRuleByAlertName(String name);

}
