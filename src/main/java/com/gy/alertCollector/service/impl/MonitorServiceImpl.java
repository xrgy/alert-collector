package com.gy.alertCollector.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.common.MonitorEnum;
import com.gy.alertCollector.entity.monitor.*;
import com.gy.alertCollector.entity.monitorConfig.Metrics;
import com.gy.alertCollector.service.MonitorService;
import com.gy.alertCollector.util.EtcdUtil;
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
public class MonitorServiceImpl implements MonitorService {

    private static final String MONITOR_PORT = "8084";
    private static final String MONITOR_PREFIX = "monitor";
    private String RECORD_PATH = "getMonitorRecord";
    private String SNMPEXPORTER_PORT = "9106";
    private String LLDP_PREFIX = "";
    private static final String HTTP = "http://";


    @Autowired
    ObjectMapper mapper;


    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    private String monitorPrefix() {
//        try {
        String ip = "127.0.0.1";
//           String ip = EtcdUtil.getClusterIpByServiceName("monitor-core-service");
        return HTTP + ip + ":" + MONITOR_PORT + "/" + MONITOR_PREFIX + "/";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
    }


    @Override
    public CompletionStage<Optional<OperationMonitorEntity>> getOperationMonitorEntity(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<String> response = rest().getForEntity(monitorPrefix() + RECORD_PATH + "?uuid=" + uuid, String.class);
            try {
                return Optional.ofNullable(mapper.readValue(response.getBody(), OperationMonitorEntity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

    @Override
    public String getMonitorRecord(String uuid, String lightType) {
        ResponseEntity<String> response =  rest().getForEntity(monitorPrefix()+RECORD_PATH+"?uuid={1}&lightType={2}", String.class,uuid,lightType);
        return response.getBody();
    }

    @Override
    public OperationMonitorEntity getCommonMonitorRecord(String uuid, String lightType) throws IOException {
        OperationMonitorEntity operationMonitorEntity = new OperationMonitorEntity();
        if (lightType.equals(MonitorEnum.LightTypeEnum.SWITCH.value()) || lightType.equals(MonitorEnum.LightTypeEnum.ROUTER.value())
                || lightType.equals(MonitorEnum.LightTypeEnum.LB.value()) || lightType.equals(MonitorEnum.LightTypeEnum.FIREWALL.value())) {
            NetworkMonitorEntity net =  mapper.readValue(getMonitorRecord(uuid,lightType),NetworkMonitorEntity.class);
            BeanUtils.copyProperties(net,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.MYSQL.value())) {
            DBMonitorEntity db =  mapper.readValue(getMonitorRecord(uuid,lightType),DBMonitorEntity.class);
            BeanUtils.copyProperties(db,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.TOMCAT.value())) {
            TomcatMonitorEntity tomcat =  mapper.readValue(getMonitorRecord(uuid,lightType),TomcatMonitorEntity.class);
            BeanUtils.copyProperties(tomcat,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.CAS.value())) {
            CasMonitorEntity cas =  mapper.readValue(getMonitorRecord(uuid,lightType),CasMonitorEntity.class);
            BeanUtils.copyProperties(cas,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.CVK.value())) {
            HostMonitorEntity host =  mapper.readValue(getMonitorRecord(uuid,lightType),HostMonitorEntity.class);
            BeanUtils.copyProperties(host,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.VIRTUALMACHINE.value())) {
            VmMonitorEntity vm =  mapper.readValue(getMonitorRecord(uuid,lightType),VmMonitorEntity.class);
            BeanUtils.copyProperties(vm,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8S.value())) {
            K8sMonitorEntity k8s =  mapper.readValue(getMonitorRecord(uuid,lightType),K8sMonitorEntity.class);
            BeanUtils.copyProperties(k8s,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8SNODE.value())) {
            K8snodeMonitorEntity k8sn =  mapper.readValue(getMonitorRecord(uuid,lightType),K8snodeMonitorEntity.class);
            BeanUtils.copyProperties(k8sn,operationMonitorEntity);
        } else if (lightType.equals(MonitorEnum.LightTypeEnum.K8SCONTAINER.value())) {
            K8scontainerMonitorEntity k8sc =  mapper.readValue(getMonitorRecord(uuid,lightType),K8scontainerMonitorEntity.class);
            BeanUtils.copyProperties(k8sc,operationMonitorEntity);
        }
        return operationMonitorEntity;
    }


}
