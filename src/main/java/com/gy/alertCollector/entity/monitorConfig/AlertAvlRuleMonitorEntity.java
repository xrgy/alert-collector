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
public class AlertAvlRuleMonitorEntity {

    private String uuid;

    private String monitorUuid;

    private String avlRuleUuid;

    private String alertRuleName;

}
