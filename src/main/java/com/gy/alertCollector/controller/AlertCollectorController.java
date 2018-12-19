package com.gy.alertCollector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.entity.AlertReceiveView;
import com.gy.alertCollector.entity.TestEntity;
import com.gy.alertCollector.service.AlertCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;


/**
 * Created by gy on 2018/3/31.
 */
@RestController
@RequestMapping("alerts")
public class AlertCollectorController {

    @Autowired
    private AlertCollectorService service;

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
        System.out.println("start receive webhook ...");

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
}
