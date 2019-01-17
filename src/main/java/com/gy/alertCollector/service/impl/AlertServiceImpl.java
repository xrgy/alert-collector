package com.gy.alertCollector.service.impl;


import com.gy.alertCollector.dao.AlertDao;
import com.gy.alertCollector.entity.topo.AlertAlarmInfo;
import com.gy.alertCollector.entity.topo.AlertAlarmInfoSelect;
import com.gy.alertCollector.entity.topo.TopoAlertView;
import com.gy.alertCollector.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by gy on 2018/3/31.
 */
@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    AlertDao alertDao;

    @Override
    public Map<Integer, Integer> getSeverityCountByMonitor(String monitorUuid) {
        return alertDao.getSeverityCountByMonitor(monitorUuid);
    }

    @Override
    public List<AlertAlarmInfo> getAlertInfoByMonitorUuids(List<String> monitorUuids) {
        List<AlertAlarmInfoSelect> list = alertDao.getAlertInfoBymonitorUuids(monitorUuids);
        List<AlertAlarmInfo> alarmInfos = new ArrayList<>();
        List<String> uuids = new ArrayList<>();
        list.forEach(x->{
            uuids.add(x.getMonitorUuid());
        });
        List<String> disuuids = uuids.stream().distinct().collect(Collectors.toList());
        disuuids.forEach(x->{
            AlertAlarmInfo info = new AlertAlarmInfo();
            info.setMonitorUuid(x);
            Map<String,Integer> alarm = new HashMap<>();
            List<AlertAlarmInfoSelect> alarmselectList = list.stream().filter(z->z.getMonitorUuid().equals(x)).collect(Collectors.toList());
            alarmselectList.forEach(y->{
                alarm.put(y.getSeverity()+"",y.getSeverityCount());
            });
            info.setAlarm(alarm);
            alarmInfos.add(info);
        });
        alarmInfos.forEach(x->{
            Map<String,Integer> alarm = x.getAlarm();
            if (!alarm.containsKey("0")){
                alarm.put("0",0);
            }
            if (!alarm.containsKey("1")){
                alarm.put("1",0);
            }
            if (!alarm.containsKey("2")){
                alarm.put("2",0);
            }
            if (!alarm.containsKey("3")){
                alarm.put("3",0);
            }
            if (!alarm.containsKey("4")){
                alarm.put("4",0);
            }
        });

        return alarmInfos;
    }
}
