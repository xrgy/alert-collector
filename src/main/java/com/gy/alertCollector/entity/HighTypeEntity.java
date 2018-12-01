package com.gy.alertCollector.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gy on 2018/5/5.
 */
@Data
@Entity
@Table(name = "tbl_monitor_high_type")
public class HighTypeEntity {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;
}
