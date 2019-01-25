package com.gy.alertCollector.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.common.AlertEnum;
import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.*;
import com.gy.alertCollector.service.MonitorConfigService;
import com.gy.alertCollector.service.MonitorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
@Service
public class MonitorConfigServiceImpl implements MonitorConfigService {

//    private String IP = "http://127.0.0.1";
private static final String CONFIG_PORT = "8081";
    private static final String MONITOR_PREFIX = "monitorConfig";
    private String AVLMONITOR_PATH = "getAvlRuleByRuleUuid";
    private String PERFMONITOR_PATH = "getPerfRuleByRuleUuid";
    private String PATH_GET_METRIC = "getMetricByUuid";
    private String ONE = "one";
    private String TWO = "two";
    private static final String HTTP="http://";

    @Autowired
    ObjectMapper mapper;


    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    private String monitorConfigPrefix() {
        //        try {
        String ip= "127.0.0.1";
//            String ip = EtcdUtil.getClusterIpByServiceName("monitorconfig-core-service");
        return HTTP+ip + ":" + CONFIG_PORT + "/" + MONITOR_PREFIX + "/";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
    }


    @Override
    public AlertCommonRule getAlertRuleByAlertName(String name, String ruleid) {
        AlertCommonRule commonRule = new AlertCommonRule();
        if (name.endsWith(AlertEnum.AlertType.RULENAME_AVL.value())) {

            AlertAvlRuleEntity avlRuleEntity= getAvlRuleByAlertRuleUuid(ruleid);
            BeanUtils.copyProperties(avlRuleEntity,commonRule);
            return commonRule;
        } else if (name.endsWith(AlertEnum.AlertType.RULENAME_PERF.value())) {
            AlertPerfRuleEntity perfRuleEntity = getPerfRuleByAlertRuleUUid(ruleid);
            BeanUtils.copyProperties(perfRuleEntity,commonRule);
            Metrics metric = getMetricByUuid(commonRule.getMetricUuid());
            if (metric!=null){
                commonRule.setMetricDisplayUnit(metric.getMetricDisplayUnit());
            }
            return commonRule;
        }
        return null;
    }

    @Override
    public Metrics getMetricByUuid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PATH_GET_METRIC + "?uuid={1}", String.class, uuid);
        try {
            return mapper.readValue(response.getBody(), Metrics.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AlertAvlRuleEntity getAvlRuleByAlertRuleUuid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + AVLMONITOR_PATH + "?uuid={1}", String.class, uuid);
        try {
            return mapper.readValue(response.getBody(), AlertAvlRuleEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AlertPerfRuleEntity getPerfRuleByAlertRuleUUid(String uuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PERFMONITOR_PATH + "?uuid={1}", String.class, uuid);
        try {
            return mapper.readValue(response.getBody(), AlertPerfRuleEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
