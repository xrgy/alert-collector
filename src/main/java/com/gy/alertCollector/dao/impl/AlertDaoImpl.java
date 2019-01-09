package com.gy.alertCollector.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.alertCollector.dao.AlertDao;
import com.gy.alertCollector.entity.AlertSeverityView;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
}
