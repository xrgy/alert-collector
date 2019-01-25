package com.gy.alertCollector.entity.monitorConfig;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gy on 2018/5/31.
 */
@Getter
@Setter
public class AlertAvlRuleEntity {

    private String uuid;

    private String metricUuid;

    private String templateUuid;

    private int severity;

    private String description;

}
