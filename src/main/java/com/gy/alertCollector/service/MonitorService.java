package com.gy.alertCollector.service;


import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.Metrics;
import org.springframework.data.geo.Metric;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
public interface MonitorService {

    public CompletionStage<Optional<OperationMonitorEntity>> getOperationMonitorEntity(String uuid);


    public String getMonitorRecord(String uuid,String lightType);


    public OperationMonitorEntity getCommonMonitorRecord(String uuid,String lightType) throws IOException;


}
