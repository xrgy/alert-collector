package com.gy.alertCollector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.entity.AlertReceiveView;
import com.gy.alertCollector.entity.TestEntity;
import com.gy.alertCollector.entity.topo.TopoAlertView;
import com.gy.alertCollector.service.AlertCollectorService;
import com.gy.alertCollector.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by gy on 2018/3/31.
 */
@RestController
@RequestMapping("alerts")
public class AlertCollectorController {

    @Autowired
    private AlertCollectorService service;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping("jpa")
    @ResponseBody
    public TestEntity testJPA(HttpServletRequest request) {
//        TestEntity entity = new TestEntity();
//        entity.setId("sasada");
//        entity.setName("gygy");
//        return entity;
        return service.getJPAInfo();
    }


    @RequestMapping("saveAlerts")
    @ResponseBody
//    public void saveAlerts(){

    public void saveAlerts(HttpServletRequest request, HttpServletResponse response) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println("start receive webhook ..."+df.format(new Date()));

        StringBuffer jb = new StringBuffer();
        String line = null;
        BufferedReader reader1 = request.getReader();
        while ((line = reader1.readLine()) != null)
            jb.append(line);
//        System.out.println(mapper.writeValueAsString(msg));

        System.out.println(jb);
        //{"receiver":"","status":"","alerts":null,"groupLabels":null,"commonLabels":null,"commonAnnotations":null,"externalURL":"","version":"4","groupKey":"11111"}
        AlertReceiveView view = mapper.readValue(jb.toString(), AlertReceiveView.class);
    }

    @RequestMapping("getAlertSeverityCountMap")
    @ResponseBody
    public String getAlertInfoByMonitorUuid(String monitorUuid) throws JsonProcessingException {
        //返回{"0":3,"1":2,"2":1} severity:count key都是string
        return mapper.writeValueAsString(alertService.getSeverityCountByMonitor(monitorUuid));
    }

    @RequestMapping("getAlertInfoByMonitorUuids")
    @ResponseBody
    public String getAlertInfoByMonitorUuids(@RequestBody String data) throws IOException {
        List<String> view = mapper.readValue(data, new TypeReference<List<String>>() {});
        return mapper.writeValueAsString(alertService.getAlertInfoByMonitorUuids(view));
    }


}
