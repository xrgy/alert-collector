package com.gy.alertCollector.entity.monitorConfig;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gy on 2018/6/8.
 */
@Getter
@Setter
public class Metrics {

    private String uuid;

    private String name;

    private String metricType;

    private String metricGroup;

    private String metricCollection;

    private String metricLightType;

    private String metricUnit;

    private String metricDisplayUnit;

    private String description;

}
