package com.gy.alertCollector.dao.impl;

import com.gy.alertCollector.dao.AlertCollectorDao;
import com.gy.alertCollector.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class AlertCollectorDaoImpl implements AlertCollectorDao {

//    @Autowired
//    @Qualifier("database")
//    Executor executor;

    @Autowired
    @PersistenceContext
    EntityManager em;

    @Override
    public TestEntity getJPAInfo() {
        List<TestEntity> result = em.createQuery("FROM TestEntity",TestEntity.class)
                .getResultList();
        if (result.size() == 0){
            return null;
        }
        return result.get(0);
    }


    @Override
    public CompletionStage<Optional<AlertEntity>> getAlertRecordByHashCode(Integer hashcode) {
        String sql = "From AlertEntity WHERE alertHashCode = :hashcode";
        return CompletableFuture.supplyAsync(()->{
           return em.createQuery(sql,AlertEntity.class)
                    .setParameter("hashcode",hashcode)
                    .getResultList();
        }).thenApply(list->{
            if (list.size()>0){
                return Optional.ofNullable(list.get(0));
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletionStage<Optional<AlertEntity>> getAlertRecordByTagHashCode(Integer hashcode) {
        String sql = "From AlertEntity WHERE tagHashCode = :taghashcode AND resolvedStatus =:status";
        return CompletableFuture.supplyAsync(()->{
            return em.createQuery(sql,AlertEntity.class)
                    .setParameter("taghashcode",hashcode)
                    .setParameter("status",0)
                    .getResultList();
        }).thenApply(list->{
            if (list.size()>0){
                return Optional.ofNullable(list.get(0));
            }
            return Optional.empty();
        });
    }

    @Override
    @Transactional
    public boolean insertIntoAlert(AlertEntity alertEntity) {
            try {
                em.merge(alertEntity);
                return true;
            }catch (Exception e){
                return false;
            }

    }

    @Override
    public List<AlertEntity> getAlertDetail(int severity, int resolve, String uuid) {
        String sql = "From AlertEntity Where monitorUuid =:monitoruuid AND severity =:severity AND resolvedStatus=:resolve";
        return em.createQuery(sql, AlertEntity.class)
                .setParameter("monitoruuid",uuid)
                .setParameter("severity",severity)
                .setParameter("resolve",resolve)
                .getResultList();
    }
}
