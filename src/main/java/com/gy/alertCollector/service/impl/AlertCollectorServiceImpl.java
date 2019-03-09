package com.gy.alertCollector.service.impl;

import com.gy.alertCollector.common.AlertEnum;
import com.gy.alertCollector.dao.AlertCollectorDao;
import com.gy.alertCollector.entity.*;
import com.gy.alertCollector.entity.monitor.OperationMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.AlertAvlRuleMonitorEntity;
import com.gy.alertCollector.entity.monitorConfig.AlertCommonRule;
import com.gy.alertCollector.entity.monitorConfig.AlertPerfRuleMonitorEntity;
import com.gy.alertCollector.entity.topo.AlertAlarmInfo;
import com.gy.alertCollector.entity.topo.TopoAlertView;
import com.gy.alertCollector.service.AlertCollectorService;
import com.gy.alertCollector.service.MonitorConfigService;
import com.gy.alertCollector.service.MonitorService;
import com.gy.alertCollector.util.DateParseUtil;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
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
        Map<Boolean, String> returnMap = new HashMap<>();
        webHookAlertList.forEach(webhookAlertEntity -> {
            OperationMonitorEntity monitor = null;
            try {
                monitor = monitorService.getCommonMonitorRecord(webhookAlertEntity.getLabels().get("job"),
                        webhookAlertEntity.getLabels().get("resource_type"));
            } catch (IOException e) {
                e.printStackTrace();
            }
//                    .thenAccept(monitorOpt->{
            if (monitor != null) {
                //获取推送告警tag的哈希值
                Integer webhookTagHashCode = getTagHashCode(webhookAlertEntity);
                //获取推送告警的哈希值
                Integer webhookAlertHashCode = getHashCode(webhookAlertEntity);
                String status = webhookAlertEntity.getStatus();
                CompletionStage<Optional<AlertEntity>> future = findByHashCode(status, webhookAlertHashCode, webhookTagHashCode);
                String alertName = webhookAlertEntity.getLabels().get("alertname");
                OperationMonitorEntity finalMonitor1 = monitor;
                future.thenAccept(optional -> {
                    OperationMonitorEntity finalMonitor = finalMonitor1;
                    String ruleuuid = webhookAlertEntity.getLabels().get("rule_id");
//                    AlertCommonRule isPresentRule = monitorConfigService.getAlertRuleByAlertName(alertName, ruleuuid);
                    AlertCommonRule isPresentRule = new AlertCommonRule();
                    if (optional.isPresent()) {
                        //重复告警
                        AlertEntity alertEntity = optional.get();
                        //都是针对未恢复的告警
                        if (alertEntity.getResolvedStatus() == UNRESOLVED) {
                            String startsAt = webhookAlertEntity.getStartsAt();
                            Date startAt = dateParseUtil.parseStringAsDate(startsAt);
                            Long startTime = startAt.getTime() / 1000;
                            Long createTime = alertEntity.getCreateTime().getTime() / 1000;
                            //若是未恢复告警产生时间和数据库里的首次告警不同或者来的是已恢复告警，再更新告警内容
                            //现在看来未恢复告警产生时间相同，也可能value不同，需要更新告警值
//                                    if (startTime != createTime || status.equals(AlertEnum.AlertResolvedType.RESOLVED.name())) {
                            //更新告警内容
                            Map<String, String> annotationsMap = webhookAlertEntity.getAnnotations();

                            String description = acxt.getMessage("alert.rule.description." + annotationsMap.get("description"), convert2Description(
                                    webhookAlertEntity, finalMonitor, isPresentRule), Locale.CHINA);
                            alertEntity.setDescription(description);
                            if (status.equals(AlertEnum.AlertResolvedType.UNRESOLVED.value())) {
                                //新来的是未恢复告警，更新当前阈值和告警内容
//                                int unitLength = isPresentRule.getMetricDisplayUnit().length();
                                String currentvalue = annotationsMap.get("current_value");
//                                String currentvalue = currentValueStr.substring(0, currentValueStr.length() - unitLength);
                                alertEntity.setCurrentValue(double2float2(currentvalue));
                                dao.insertIntoAlert(alertEntity);
//                                                        .thenCompose(val->{
//                                                    returnMap.put(val,"update alert");
//                                                    return CompletableFuture.supplyAsync(()->returnMap);

                            } else if (status.equals(AlertEnum.AlertResolvedType.RESOLVED.value())) {
                                //新来的是恢复告警，更新告警状态，恢复时间，当前阈值，告警内容
//                                int unitLength = isPresentRule.getMetricDisplayUnit().length();
                                String currentvalue = annotationsMap.get("current_value");
//                                String currentvalue = currentValueStr.substring(0, currentValueStr.length() - unitLength);
                                alertEntity.setCurrentValue(double2float2(currentvalue));
                                alertEntity.setResolvedStatus(RESOLVED);
                                alertEntity.setResolvedTime(dateParseUtil.parseStringAsDate(webhookAlertEntity.getEndsAt()));
                                dao.insertIntoAlert(alertEntity);
//                                                        .thenCompose(val->{
//                                                    returnMap.put(val,"update alert");
//                                                    return CompletableFuture.supplyAsync(()->returnMap);
//                                                });
                            }
//                                    }
                        }
                    } else {
                        //判断告警规则是否存在
                        //todo mysql总是重启 把这个先去掉 不然插不进去
//                        if (isPresentRule == null) {
//                                    returnMap.put(false,"findByAlertName is null");
//                                    return CompletableFuture.supplyAsync(()->returnMap);
                            //结束
//                            return;
//                        }
                        //新告警插入数据库
                        AlertEntity alertEntity = webhookAlertEntity2AlertEntity(webhookAlertEntity, finalMonitor, isPresentRule);
                        //设置哈希值
                        alertEntity.setAlertHashCode(webhookAlertHashCode);
                        //设置tag哈希值
                        alertEntity.setTagHashCode(webhookTagHashCode);
                        //设置告警规则uuid
                        alertEntity.setAlertRuleUuid(ruleuuid);
                        //针对插入时为resolved且数据库不存在的告警处理
                        if (status.equals(AlertEnum.AlertResolvedType.RESOLVED.value())) {
                            //针对插入已存在的告警时通过异常来处理已存在的告警，数据库哈希值列加unique约束
                            //已恢复告警设置已恢复时间及恢复状态，插入数据库
                            alertEntity.setResolvedTime(dateParseUtil.parseStringAsDate(webhookAlertEntity.getEndsAt()));
                            alertEntity.setResolvedStatus(RESOLVED);
                            try {
                                dao.insertIntoAlert(alertEntity);
//                                                     .thenCompose(insertval->{
//                                                returnMap.put(insertval,"insert resolved alert");
//                                                return CompletableFuture.supplyAsync(()->returnMap);
//                                            });
                            } catch (Exception e) {
//                                            returnMap.put(false,"insert resolved alert exception");
//                                            return CompletableFuture.supplyAsync(()->returnMap);
                            }

                        } else {
                            //未恢复告警插入数据库
                            alertEntity.setResolvedStatus(UNRESOLVED);
                            try {
                                dao.insertIntoAlert(alertEntity);
//                                                     .thenCompose(insertval->{
//                                                returnMap.put(insertval,"insert unresolved alert");
//                                                return CompletableFuture.supplyAsync(()->returnMap);
//                                            });
                            } catch (Exception e) {
//                                            returnMap.put(false,"insert unresolved alert exception");
//                                            return CompletableFuture.supplyAsync(()->returnMap);
                            }
                        }
                    }

                });
            } else {
//                            returnMap.put(false,"monitor record is null");
//                            return CompletableFuture.supplyAsync(()->returnMap);
            }
//                    });
        });
    }

    @Override
    public List<AlertEntity> getAlertDetail(int severity, int resolve, String uuid) {
        return dao.getAlertDetail(severity,resolve,uuid);
    }


    private String getAlertRuleUuid(Object o, String name) {
        if (name.endsWith(AlertEnum.AlertType.RULENAME_AVL.value())) {
            AlertAvlRuleMonitorEntity entity = (AlertAvlRuleMonitorEntity) o;
            return entity.getUuid();

        } else if (name.endsWith(AlertEnum.AlertType.RULENAME_PERF.value())) {
            AlertPerfRuleMonitorEntity entity = (AlertPerfRuleMonitorEntity) o;
            return entity.getUuid();
        }
        return null;
    }

    private AlertEntity webhookAlertEntity2AlertEntity(WebhookAlertEntity webhookAlertEntity, OperationMonitorEntity operationMonitorEntity, AlertCommonRule commonRule) {
        AlertEntity entity = new AlertEntity();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setMonitorUuid(webhookAlertEntity.getLabels().get("job"));
        entity.setSeverity(convertSeverity2Num(webhookAlertEntity.getLabels().get("severity")));
        Map<String, String> annotationsMap = webhookAlertEntity.getAnnotations();
        String description = acxt.getMessage("alert.rule.description." + annotationsMap.get("description"), convert2Description(webhookAlertEntity,
                operationMonitorEntity, commonRule), Locale.CHINA);
        entity.setDescription(description);
//        int unitLength = commonRule.getMetricDisplayUnit().length();
        String currentvalue = annotationsMap.get("current_value");
//        String currentvalue = currentValueStr.substring(0, currentValueStr.length() - unitLength);
        entity.setCurrentValue(double2float2(currentvalue));
//        entity.setThreshold(annotationsMap.get("threshold"));
        entity.setCreateTime(dateParseUtil.parseStringAsDate(webhookAlertEntity.getStartsAt()));
//        entity.setSummary(acxt.getMessage(annotationsMap.get("summary"), null, Locale.CHINA));
        return entity;

    }

    private Object[] convert2Description(WebhookAlertEntity webhookAlertEntity, OperationMonitorEntity operationMonitorEntity, AlertCommonRule commonRule) {
        Object[] object = new Object[6];
        object[0] = operationMonitorEntity.getName();
        object[1] = operationMonitorEntity.getIp();
        object[2] = acxt.getMessage("metric.description." + webhookAlertEntity.getAnnotations().get("description"), null, Locale.CHINA);
        String alertName = webhookAlertEntity.getLabels().get("alertname");
        if (alertName.endsWith(AlertEnum.AlertType.RULENAME_AVL.value())) {
            if (webhookAlertEntity.getAnnotations().get("current_value").equals(UNREACH)) {
                object[3] = acxt.getMessage(AlertEnum.AlertI18n.AVL_NOTREACH.value(), null, Locale.CHINA);
            }
        } else if (alertName.endsWith(AlertEnum.AlertType.RULENAME_PERF.value())) {
            String currentValueStr = webhookAlertEntity.getAnnotations().get("current_value");
            String thresholdStr = webhookAlertEntity.getAnnotations().get("threshold");
//            int unitLength = commonRule.getMetricDisplayUnit().length();
            double currentvalue = Double.valueOf(currentValueStr);
            double threshold = Double.valueOf(thresholdStr);
            if (currentvalue >= threshold) {
                object[3] = acxt.getMessage(AlertEnum.AlertI18n.PERF_VALUE_OVERTHRESHOLD.value(), null, Locale.CHINA);
            } else {
                object[3] = acxt.getMessage(AlertEnum.AlertI18n.PERF_VALUE_BELOWTHRESHOLD.value(), null, Locale.CHINA);
            }
            object[4] = double2float22(currentvalue) ;//+ commonRule.getMetricDisplayUnit();//todo
            object[5] = double2float22(threshold) ;//+ commonRule.getMetricDisplayUnit();//todo
        }

        return object;

    }

    private double double2float22(double d) {
        BigDecimal b = new BigDecimal(d);
//        float df = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return df;
    }

    private double double2float2(String d) {
        BigDecimal b = new BigDecimal(d);
//        float df = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return df;
    }

    private int convertSeverity2Num(String severity) {
        int num = -1;
        switch (severity) {
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
                num = 4;
                break;
        }
        return num;
    }

    private Integer getHashCode(WebhookAlertEntity webhookAlertEntity) {
        //告警标签label
        Map<String, String> labelsMap = null;
        if (Optional.ofNullable(webhookAlertEntity).isPresent()) {
            labelsMap = webhookAlertEntity.getLabels();
            //告警开始时间
            String startsAt = webhookAlertEntity.getStartsAt();
            labelsMap.put("startsAt", startsAt);
        }
        //将告警标签label&startsAt联合计算哈希值，用于唯一告警判断
        Integer hashCode = labelsMap.toString().hashCode();
        return hashCode;
    }

    private Integer getTagHashCode(WebhookAlertEntity webhookAlertEntity) {
        //告警标签label
        Map<String, String> labelsMap = null;
        if (Optional.ofNullable(webhookAlertEntity).isPresent()) {
            labelsMap = webhookAlertEntity.getLabels();
        }
        //将告警标签label计算哈希值，用于合并未恢复告警用
        Integer hashCode = labelsMap.toString().hashCode();
        return hashCode;
    }

    private CompletionStage<Optional<AlertEntity>> findByHashCode(String status, Integer hashCode, Integer tagHashCode) {
        if (status.equals(AlertEnum.AlertResolvedType.UNRESOLVED.value())) {
            return dao.getAlertRecordByTagHashCode(tagHashCode);
        } else if (status.equals(AlertEnum.AlertResolvedType.RESOLVED.value())) {
            return dao.getAlertRecordByHashCode(hashCode);
        }
        return null;
    }


}
