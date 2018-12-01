package com.gy.alertCollector.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.common.AlertEnum;
import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.AlertAvlRuleMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.AlertPerfRuleMonitorEntity;
import com.gy.alertCollector.service.MonitorConfigService;
import com.gy.alertCollector.service.MonitorService;
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

    private String IP = "http://127.0.0.1";
    private String PORT = "8086";
    private String PREFIX = "monitorConfig";
    private String AVLMONITOR_PATH = "avlRuleMonitor";
    private String PERFMONITOR_PATH = "perfRuleMonitor";
    private String ONE = "one";
    private String TWO = "two";

    @Autowired
    ObjectMapper mapper;


    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    private String monitorConfigPrefix() {
        return IP + ":" + PORT + "/" + PREFIX + "/";
    }


    @Override
    public CompletionStage<Optional<Object>> getAlertRuleByAlertName(String name) {
        if (name.endsWith(AlertEnum.AlertType.RULENAME_AVL.name())) {
            return getAvlRuleByAlertName(name).thenApply(opt-> opt.map(alertEntity-> (Object) alertEntity));

        } else if (name.endsWith(AlertEnum.AlertType.RULENAME_PERF.name())) {
            return getPerfRuleByAlertName(name).thenApply(opt-> opt.map(alertEntity-> (Object) alertEntity));
        }
        return null;
    }

    private CompletionStage<Optional<AlertAvlRuleMonitorEntity>> getAvlRuleByAlertName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + AVLMONITOR_PATH + "?name=" + name, String.class);
            try {
                return Optional.ofNullable(mapper.readValue(response.getBody(), AlertAvlRuleMonitorEntity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

    private CompletionStage<Optional<AlertPerfRuleMonitorEntity>> getPerfRuleByAlertName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = rest().getForEntity(monitorConfigPrefix() + PERFMONITOR_PATH + "?name=" + name, String.class);

            try {
                return Optional.ofNullable(mapper.readValue(response.getBody(), AlertPerfRuleMonitorEntity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }
}
