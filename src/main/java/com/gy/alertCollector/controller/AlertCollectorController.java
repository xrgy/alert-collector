package com.gy.alertCollector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.entity.WebhookAlertEntity;
import com.gy.alertCollector.entity.TestEntity;
import com.gy.alertCollector.service.AlertCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletionStage;


/**
 * Created by gy on 2018/3/31.
 */
@RestController
@RequestMapping("monitor")
public class AlertCollectorController {

    @Autowired
    private AlertCollectorService service;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping("jpa")
    @ResponseBody
    public TestEntity testJPA(HttpServletRequest request){
//        TestEntity entity = new TestEntity();
//        entity.setId("sasada");
//        entity.setName("gygy");
//        return entity;
        return service.getJPAInfo();
    }


    @RequestMapping("saveAlerts")
    @ResponseBody
    public void saveAlerts(List<WebhookAlertEntity> webhookAlertList){



    }
}
