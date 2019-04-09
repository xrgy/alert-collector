package com.gy.alertCollector.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by gy on 2018/5/31.
 */
@Data
@Entity
@Table(name = "tbl_alert")
public class AlertEntity {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "device_id")
    private String monitorUuid;

    @Column(name = "severity")
    private int severity;

    @Column(name = "alert_rule_uuid")
    private String alertRuleUuid;

    @Column(name = "alert_hashcode")
    private int alertHashCode;

    @Column(name = "tag_hashcode")
    private int tagHashCode;

    @Column(name = "resolved_status")
    private int resolvedStatus;

//    @Column(name = "summary")
//    private String summary;

    @Column(name = "description")
    private String description;

    @Column(name = "current_value")
    private double currentValue;

//    @Column(name = "threshold")
//    private String threshold;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "resolved_time")
    private Date resolvedTime;

    @Column(name = "light_type")
    private String lightType;
}
