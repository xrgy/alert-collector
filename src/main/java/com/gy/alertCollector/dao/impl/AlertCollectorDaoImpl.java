package com.gy.alertCollector.dao.impl;

import com.gy.alertCollector.dao.AlertCollectorDao;
import com.gy.alertCollector.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
        List<TestEntity> result = em.createQuery("FROM TestEntity", TestEntity.class)
                .getResultList();
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }


    @Override
    public CompletionStage<Optional<AlertEntity>> getAlertRecordByHashCode(Integer hashcode) {
        String sql = "From AlertEntity WHERE alertHashCode = :hashcode";
        return CompletableFuture.supplyAsync(() -> {
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("hashcode", hashcode)
                    .getResultList();
        }).thenApply(list -> {
            if (list.size() > 0) {
                return Optional.ofNullable(list.get(0));
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletionStage<Optional<AlertEntity>> getAlertRecordByTagHashCode(Integer hashcode) {
        String sql = "From AlertEntity WHERE tagHashCode = :taghashcode AND resolvedStatus =:status";
        return CompletableFuture.supplyAsync(() -> {
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("taghashcode", hashcode)
                    .setParameter("status", 0)
                    .getResultList();
        }).thenApply(list -> {
            if (list.size() > 0) {
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
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public List<AlertEntity> getAlertDetail(AlertView view) {
        String sql = "From AlertEntity Where monitorUuid =:monitoruuid";
        if ((null == view.getResolve() || ("-1").equals(view.getResolve())) && (null == view.getSeverity() || ("-1").equals(view.getSeverity()))) {
            //两个都为空
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("monitoruuid", view.getUuid())
                    .getResultList();
        } else if (null == view.getResolve() || ("-1").equals(view.getResolve())) {
            //只有resolve为空
            sql += " AND severity =:severity";
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("monitoruuid", view.getUuid())
                    .setParameter("severity", Integer.parseInt(view.getSeverity()))
                    .getResultList();
        } else if (null == view.getSeverity() || ("-1").equals(view.getSeverity())) {
            //只有级别为空
            sql += " AND resolvedStatus=:resolve";
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("monitoruuid", view.getUuid())
                    .setParameter("resolve", Integer.parseInt(view.getResolve()))
                    .getResultList();
        } else {
            //都不为空
            sql += " AND severity =:severity AND resolvedStatus=:resolve";
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("monitoruuid", view.getUuid())
                    .setParameter("severity", Integer.parseInt(view.getSeverity()))
                    .setParameter("resolve", Integer.parseInt(view.getResolve()))
                    .getResultList();
        }

    }

    @Override
    public List<AlertEntity> getAlertDetailByStatus(AlertView view) {
        String sql = "From AlertEntity";
        if ((null == view.getResolve() || ("-1").equals(view.getResolve())) && (null == view.getSeverity() || ("-1").equals(view.getSeverity()))) {
            //两个都为空
            return em.createQuery(sql, AlertEntity.class)
                    .getResultList();
        } else if (null == view.getResolve() || ("-1").equals(view.getResolve())) {
            //只有resolve为空
            sql += " Where severity =:severity";
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("severity", Integer.parseInt(view.getSeverity()))
                    .getResultList();
        } else if (null == view.getSeverity() || ("-1").equals(view.getSeverity())) {
            //只有级别为空
            sql += " Where resolvedStatus=:resolve";
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("resolve", Integer.parseInt(view.getResolve()))
                    .getResultList();
        } else {
            //都不为空
            sql += " Where severity =:severity AND resolvedStatus=:resolve";
            return em.createQuery(sql, AlertEntity.class)
                    .setParameter("severity", Integer.parseInt(view.getSeverity()))
                    .setParameter("resolve", Integer.parseInt(view.getResolve()))
                    .getResultList();
        }
    }

    @Override
    public List<AlertEntity> getAlertDetailByStatusAndLightType(AlertView view, List<String> type) {
        String sql = "From AlertEntity ";
        if (type.size() > 0) {
            sql += " Where (";
            for (int i = 0; i < type.size(); i++) {
                sql += " light_type =? ";
                if (i < type.size() - 1) {
                    sql += " or ";
                }
            }
            sql += " ) ";
        }
        if ((null == view.getResolve() || ("-1").equals(view.getResolve())) && (null == view.getSeverity() || ("-1").equals(view.getSeverity()))) {
            //两个都为空
            Query query = em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            return query.getResultList();
        } else if (null == view.getResolve() || ("-1").equals(view.getResolve())) {
            //只有resolve为空
            sql += " AND severity =:severity";
            Query query = em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("severity", Integer.parseInt(view.getSeverity()));
            return query.getResultList();
        } else if (null == view.getSeverity() || ("-1").equals(view.getSeverity())) {
            //只有级别为空
            sql += " AND resolvedStatus=:resolve";
            Query query = em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("resolve", Integer.parseInt(view.getResolve()));
            return query.getResultList();
        } else {
            //都不为空
            sql += " AND severity =:severity AND resolvedStatus=:resolve";
            Query query =  em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("severity", Integer.parseInt(view.getSeverity()));
            query.setParameter("resolve", Integer.parseInt(view.getResolve()));
            return query.getResultList();
        }
    }

    @Override
    public List<AlertEntity> getAlertDetailByLightType(AlertView view, List<String> type) {
        String sql = "From AlertEntity ";
        if (type.size() > 0) {
            sql += " Where (";
            for (int i = 0; i < type.size(); i++) {
                sql += " light_type =? ";
                if (i < type.size() - 1) {
                    sql += " or ";
                }
            }
            sql += " ) ";
        }
        sql+=" AND monitorUuid =:monitoruuid ";
        if ((null == view.getResolve() || ("-1").equals(view.getResolve())) && (null == view.getSeverity() || ("-1").equals(view.getSeverity()))) {
            //两个都为空
            Query query =  em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("monitoruuid", view.getUuid());
            return query.getResultList();
        } else if (null == view.getResolve() || ("-1").equals(view.getResolve())) {
            //只有resolve为空
            sql += " AND severity =:severity";
            Query query =  em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("monitoruuid", view.getUuid());
            query.setParameter("severity", Integer.parseInt(view.getSeverity()));
           return query.getResultList();
        } else if (null == view.getSeverity() || ("-1").equals(view.getSeverity())) {
            //只有级别为空
            sql += " AND resolvedStatus=:resolve";
            Query query = em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("monitoruuid", view.getUuid());
            query.setParameter("resolve", Integer.parseInt(view.getResolve()));
            return query.getResultList();
        } else {
            //都不为空
            sql += " AND severity =:severity AND resolvedStatus=:resolve";
            Query query = em.createQuery(sql, AlertEntity.class);
            if (type.size() > 0) {
                for (int i = 0; i < type.size(); i++) {
                    query.setParameter(i + 1, type.get(i));
                }
            }
            query.setParameter("monitoruuid", view.getUuid());
            query.setParameter("severity", Integer.parseInt(view.getSeverity()));
            query.setParameter("resolve", Integer.parseInt(view.getResolve()));
            return query.getResultList();
        }
    }
}
