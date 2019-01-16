package com.gy.alertCollector.service.impl;

import com.gy.alertCollector.common.AlertEnum;
import com.gy.alertCollector.dao.AlertCollectorDao;
import com.gy.alertCollector.entity.*;
import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.AlertAvlRuleMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.AlertPerfRuleMonitorEntity;
import com.gy.alertCollector.service.AlertCollectorService;
import com.gy.alertCollector.service.MonitorConfigService;
import com.gy.alertCollector.service.MonitorService;
import com.gy.alertCollector.util.DateParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Created by gy on 2018/3/31.
 */
@Service
public class AlertCollectorServiceImpl implements AlertCollectorService {

    @Autowired
    AlertCollectorDao dao;

    @Autowired
    MonitorService monitorService;

    @Autowired
    MonitorConfigService monitorConfigService;

    @Autowired
    DateParseUtil dateParseUtil;

    private static final int RESOLVED = 1;
    private static final int UNRESOLVED = 0;
    private static final String UNREACH = "0";


    ApplicationContext acxt =
            new ClassPathXmlApplicationContext("/spring-mvc.xml");

    @Override
    public TestEntity getJPAInfo() {
        return dao.getJPAInfo();
    }

    @Override
    public void insertOrUpdateAlert(List<WebhookAlertEntity> webHookAlertList) throws Exception {
        Map<Boolean,String> returnMap = new HashMap<>();
       webHookAlertList.forEach(webhookAlertEntity -> {
           //todo 修改监控实体的数据结构之后 这里需要修改
            monitorService.getCommonMonitorRecord(webhookAlertEntity.getLabels().get("instance_id"))
                    .thenAccept(monitorOpt->{
                        if (monitorOpt.isPresent()){
                            //获取推送告警tag的哈希值
                            Integer webhookTagHashCode = getTagHashCode(webhookAlertEntity);
                            //获取推送告警的哈希值
                            Integer webhookAlertHashCode = getHashCode(webhookAlertEntity);
                            String status = webhookAlertEntity.getStatus();
                            CompletionStage<Optional<AlertEntity>> future = findByHashCode(status,webhookAlertHashCode,webhookTagHashCode);
                            String alertName = webhookAlertEntity.getLabels().get("alertname");
                             future.thenCompose(optional->monitorConfigService.getAlertRuleByAlertName(alertName)
                            .thenAccept(alertRuleOpt->{
                                //判断告警规则是否存在
                                if (!alertRuleOpt.isPresent()){
//                                    returnMap.put(false,"findByAlertName is null");
//                                    return CompletableFuture.supplyAsync(()->returnMap);
                                }
                                if (optional.isPresent()){
                                    //重复告警
                                    AlertEntity alertEntity = optional.get();
                                    //都是针对未恢复的告警
                                    if (alertEntity.getResolvedStatus() == UNRESOLVED){
                                        String startsAt = webhookAlertEntity.getStartsAt();
                                        Date startAt = dateParseUtil.parseStringAsDate(startsAt);
                                        Long startTime = startAt.getTime()/1000;
                                        Long createTime = alertEntity.getCreateTime().getTime()/1000;
                                        //若是未恢复告警产生时间和数据库里的首次告警不同或者来的是已恢复告警，再更新告警内容
                                        if (startTime != createTime || status.equals(AlertEnum.AlertResolvedType.RESOLVED.name())){
                                            //更新告警内容
                                            Map<String,String> annotationsMap = webhookAlertEntity.getAnnotations();

                                            String description =  acxt.getMessage(annotationsMap.get("description"),convert2Description(
                                                    webhookAlertEntity,monitorOpt.get()),Locale.CHINA);
                                            alertEntity.setDescription(description);
                                            if (status.equals(AlertEnum.AlertResolvedType.UNRESOLVED.name())){
                                                //新来的是未恢复告警，更新当前阈值和告警内容
                                                alertEntity.setCurrentValue(annotationsMap.get("current_value"));
                                                dao.insertIntoAlert(alertEntity);
//                                                        .thenCompose(val->{
//                                                    returnMap.put(val,"update alert");
//                                                    return CompletableFuture.supplyAsync(()->returnMap);

                                            }else if (status.equals(AlertEnum.AlertResolvedType.RESOLVED.name())){
                                                //新来的是恢复告警，更新告警状态，恢复时间，当前阈值，告警内容
                                                alertEntity.setCurrentValue(annotationsMap.get("current_value"));
                                                alertEntity.setResolvedStatus(RESOLVED);
                                                alertEntity.setResolvedTime(dateParseUtil.parseStringAsDate(webhookAlertEntity.getEndsAt()));
                                                dao.insertIntoAlert(alertEntity);
//                                                        .thenCompose(val->{
//                                                    returnMap.put(val,"update alert");
//                                                    return CompletableFuture.supplyAsync(()->returnMap);
//                                                });
                                            }
                                        }
                                    }
                                }else {
                                    //新告警插入数据库
                                    AlertEntity alertEntity = webhookAlertEntity2AlertEntity(webhookAlertEntity,monitorOpt.get(),alertRuleOpt.get());
                                    //设置哈希值
                                    alertEntity.setAlertHashCode(webhookAlertHashCode);
                                    //设置tag哈希值
                                    alertEntity.setTagHashCode(webhookTagHashCode);
                                    //设置告警规则uuid
                                    alertEntity.setAlertRuleUuid(getAlertRuleUuid(alertRuleOpt.get(),alertName));
                                    //针对插入时为resolved且数据库不存在的告警处理
                                    if (status.equals(AlertEnum.AlertResolvedType.RESOLVED.name())){
                                        //针对插入已存在的告警时通过异常来处理已存在的告警，数据库哈希值列加unique约束
                                        //已恢复告警设置已恢复时间及恢复状态，插入数据库
                                        alertEntity.setResolvedTime(dateParseUtil.parseStringAsDate(webhookAlertEntity.getEndsAt()));
                                        alertEntity.setResolvedStatus(RESOLVED);
                                        try{
                                             dao.insertIntoAlert(alertEntity);
//                                                     .thenCompose(insertval->{
//                                                returnMap.put(insertval,"insert resolved alert");
//                                                return CompletableFuture.supplyAsync(()->returnMap);
//                                            });
                                        }catch (Exception e){
//                                            returnMap.put(false,"insert resolved alert exception");
//                                            return CompletableFuture.supplyAsync(()->returnMap);
                                        }

                                    }else {
                                        //未恢复告警插入数据库
                                        alertEntity.setResolvedStatus(UNRESOLVED);
                                        try{
                                             dao.insertIntoAlert(alertEntity);
//                                                     .thenCompose(insertval->{
//                                                returnMap.put(insertval,"insert unresolved alert");
//                                                return CompletableFuture.supplyAsync(()->returnMap);
//                                            });
                                        }catch (Exception e){
//                                            returnMap.put(false,"insert unresolved alert exception");
//                                            return CompletableFuture.supplyAsync(()->returnMap);
                                        }
                                    }
                                }
                            }));
                        }else {
//                            returnMap.put(false,"monitor record is null");
//                            return CompletableFuture.supplyAsync(()->returnMap);
                        }
                    });
        });
    }

    private String getAlertRuleUuid(Object o,String name) {
        if (name.endsWith(AlertEnum.AlertType.RULENAME_AVL.name())) {
            AlertAvlRuleMonitorEntity entity = (AlertAvlRuleMonitorEntity) o;
            return entity.getUuid();

        } else if (name.endsWith(AlertEnum.AlertType.RULENAME_PERF.name())) {
            AlertPerfRuleMonitorEntity entity = (AlertPerfRuleMonitorEntity) o;
            return entity.getUuid();
        }
        return null;
    }

    private AlertEntity webhookAlertEntity2AlertEntity(WebhookAlertEntity webhookAlertEntity,OperationMonitorEntity operationMonitorEntity,
                                                       Object alertRuleOpt) {
        AlertEntity entity = new AlertEntity();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setMonitorUuid(webhookAlertEntity.getLabels().get("instance_id"));
        entity.setSeverity(convertSeverity2Num(webhookAlertEntity.getLabels().get("severity")));
        Map<String,String> annotationsMap = webhookAlertEntity.getAnnotations();
        String description =  acxt.getMessage(annotationsMap.get("description"),convert2Description(webhookAlertEntity,
                operationMonitorEntity),Locale.CHINA);
        entity.setDescription(description);
        entity.setCurrentValue(annotationsMap.get("current_value"));
        entity.setThreshold(annotationsMap.get("threshold"));
        entity.setCreateTime(dateParseUtil.parseStringAsDate(webhookAlertEntity.getStartsAt()));
        entity.setSummary(acxt.getMessage(annotationsMap.get("summary"),null,Locale.CHINA));
        return entity;

    }

    private Object[] convert2Description(WebhookAlertEntity webhookAlertEntity, OperationMonitorEntity operationMonitorEntity){
        Object[] object = new Object[6];
        object[0]=operationMonitorEntity.getName();
        object[1]=webhookAlertEntity.getLabels().get("instance");
        object[2]=webhookAlertEntity.getAnnotations().get("summary");
        String alertName = webhookAlertEntity.getLabels().get("alertname");
        if (alertName.endsWith(AlertEnum.AlertType.RULENAME_AVL.name())) {
            if (webhookAlertEntity.getAnnotations().get("current_value").equals(UNREACH)){
                object[3]=acxt.getMessage(AlertEnum.AlertI18n.AVL_NOTREACH.name(),null,Locale.CHINA);
            }
        } else if (alertName.endsWith(AlertEnum.AlertType.RULENAME_PERF.name())) {
            String currentValueStr = webhookAlertEntity.getAnnotations().get("current_value");
            String thresholdStr = webhookAlertEntity.getAnnotations().get("threshold");
            double currentvalue = Double.valueOf(currentValueStr.substring(0,currentValueStr.length()-1));
            double threshold = Double.valueOf(thresholdStr.substring(0,thresholdStr.length()-1));
            if (currentvalue>=threshold){
                object[3]=AlertEnum.AlertI18n.PERF_VALUE_OVERTHRESHOLD.name();
            }else {
                object[4]=AlertEnum.AlertI18n.PERF_VALUE_BELOWTHRESHOLD.name();
            }
            object[4]=currentValueStr;
            object[5]=thresholdStr;
        }

        return object;

    }

    private int convertSeverity2Num(String severity){
        int num = -1;
        switch (severity){
            case "critical":
                num = 0;
                break;
            case "major":
                num = 1;
                break;
            case "minor":
                num = 2;
                break;
            case "warning":
                num = 3;
                break;
            case "notice":
                num=4;
                break;
        }
        return num;
    }

    private Integer getHashCode(WebhookAlertEntity webhookAlertEntity){
        //告警标签label
        Map<String,String> labelsMap = null;
        if (Optional.ofNullable(webhookAlertEntity).isPresent()){
            labelsMap = webhookAlertEntity.getLabels();
            //告警开始时间
            String startsAt = webhookAlertEntity.getStartsAt();
            labelsMap.put("startsAt",startsAt);
        }
        //将告警标签label&startsAt联合计算哈希值，用于唯一告警判断
        Integer hashCode = labelsMap.toString().hashCode();
        return hashCode;
    }

    private Integer getTagHashCode(WebhookAlertEntity webhookAlertEntity){
        //告警标签label
        Map<String,String> labelsMap = null;
        if (Optional.ofNullable(webhookAlertEntity).isPresent()){
            labelsMap = webhookAlertEntity.getLabels();
        }
        //将告警标签label计算哈希值，用于合并未恢复告警用
        Integer hashCode = labelsMap.toString().hashCode();
        return hashCode;
    }

    private CompletionStage<Optional<AlertEntity>> findByHashCode(String status,Integer hashCode,Integer tagHashCode){
        if (status.equals(AlertEnum.AlertResolvedType.UNRESOLVED.name())){
           return dao.getAlertRecordByTagHashCode(tagHashCode);
        }else if (status.equals(AlertEnum.AlertResolvedType.RESOLVED.name())){
            return dao.getAlertRecordByHashCode(hashCode);
        }
        return null;
    }
}
