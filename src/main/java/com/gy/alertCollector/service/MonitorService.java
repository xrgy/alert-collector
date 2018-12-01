package com.gy.alertCollector.service;


import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
public interface MonitorService {
    public CompletionStage<Optional<OperationMonitorEntity>> getOperationMonitorEntity(String uuid);

}
