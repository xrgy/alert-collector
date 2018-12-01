package com.gy.alertCollector.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;
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
public class MonitorServiceImpl implements MonitorService {

    private String IP = "http://127.0.0.1";
    private String PORT = "8084";
    private String PREFIX = "monitor";
    private String RECORD_PATH = "getMonitorRecord";
    private String SNMPEXPORTER_PORT = "9106";
    private String LLDP_PREFIX = "";
    private String MIDDLE_PATH = "getMiddleType";
    private String Light_PATH = "getLightType";

    @Autowired
    ObjectMapper mapper;


    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }

    private String monitorPrefix(){
        return IP+":"+PORT+"/"+PREFIX+"/";
    }


    @Override
    public CompletionStage<Optional<OperationMonitorEntity>> getOperationMonitorEntity(String uuid) {
        return CompletableFuture.supplyAsync(()-> {
            ResponseEntity<String> response = rest().getForEntity(monitorPrefix() + RECORD_PATH + "?uuid=" + uuid, String.class);
            try {
                return Optional.ofNullable(mapper.readValue(response.getBody(), OperationMonitorEntity.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

}
