package com.gy.alertCollector.service.impl;


import com.gy.alertCollector.dao.AlertDao;
import com.gy.alertCollector.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;


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
}
