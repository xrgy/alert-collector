package com.gy.alertCollector.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.dao.AlertDao;
import com.gy.alertCollector.entity.AlertSeverityView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gy.alertCollector.entity.topo.AlertAlarmInfoSelect;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class AlertDaoImpl implements AlertDao {

//    @Autowired
//    @Qualifier("database")
//    Executor executor;

    @Autowired
    @PersistenceContext
    EntityManager em;


    @Override
    public Map<Integer,Integer> getSeverityCountByMonitor(String monitorUuid) {
        Map<Integer,Integer> map = new HashMap<>();
        String sql = "select COUNT(*) count,severity from tbl_alert where resolved_status=0 and device_id="+"'"+monitorUuid+"'" +" group by severity";
        Query query = em.createNativeQuery(sql);
//        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Object[]> list =query.getResultList();
        for (Object[] objs : list) {
            map.put((Integer) objs[1],  Integer.valueOf(objs[0].toString()));
        }
        return map;
    }

    @Override
    public List<AlertAlarmInfoSelect> getAlertInfoBymonitorUuids(List<String> monitorUuids) {
        String sql = "select device_id As monitorUuid,severity, COUNT(severity) severityCount from tbl_alert where resolved_status=0 and device_id in('"+ StringUtils.join(monitorUuids,"','")+"')group by device_id,severity";
        Query query = em.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List list = query.getResultList();
        List<AlertAlarmInfoSelect> rsList=new ArrayList<>();
        for (Object object : list) {
            Map row=(Map)object;
            AlertAlarmInfoSelect message = new AlertAlarmInfoSelect();
            message.setMonitorUuid((String)row.get("monitorUuid"));
            message.setSeverity((Integer)row.get("severity"));
            message.setSeverityCount( Integer.valueOf(row.get("severityCount").toString()));
            rsList.add(message);
        }
        return rsList;
    }

    @Override
    public boolean deleteAlertResourceBymonitoruuid(String monitorUuid) {
        String sql = "DELETE FROM AlertEntity WHERE monitorUuid =:monitorUuid";
        int res = em.createQuery(sql)
                .setParameter("monitorUuid", monitorUuid)
                .executeUpdate();
        return res > 0;
    }
}
