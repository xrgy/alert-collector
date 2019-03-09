package com.gy.alertCollector.service;


import com.gy.alertCollector.entity.*;
import com.gy.alertCollector.entity.topo.AlertAlarmInfo;
import com.gy.alertCollector.entity.topo.TopoAlertView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertCollectorService {
    public TestEntity getJPAInfo();

    /**
     * 插入webhook推送过来的告警信息
     * @param webHookAlertList
     * @return
     * @throws Exception
     */
    public void insertOrUpdateAlert(List<WebhookAlertEntity> webHookAlertList) throws Exception;


    List<AlertEntity> getAlertDetail(int severity, int resolve, String uuid);
}
