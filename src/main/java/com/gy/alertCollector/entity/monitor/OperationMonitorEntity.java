package com.gy.alertCollector.entity.monitor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by gy on 2018/5/5.
 */
@Data
@Entity
@Table(name = "tbl_monitor_record")
public class OperationMonitorEntity {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "ip")
    private String ip;

    @Column(name = "high_resource_type_id")
    private String highTypeId;

    @Column(name = "middle_resource_type_id")
    private String middleTypeId;

    @Column(name = "light_resource_type_id")
    private String lightTypeId;

    @Column(name = "monitor_type")
    private String monitorType;

    @Column(name = "monitor_info")
    private String monitorInfo;

    @Column(name = "monitor_template_id")
    private String templateId;

    @Column(name = "scrape_interval")
    private String scrapeInterval;

    @Column(name = "scrape_timeout")
    private String scrapeTimeout;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "extra")
    private String extra;

    @Column(name = "deleted")
    private int deleted;

}
