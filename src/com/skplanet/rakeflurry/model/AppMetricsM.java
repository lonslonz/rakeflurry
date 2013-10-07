package com.skplanet.rakeflurry.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_appmetrics")
public class AppMetricsM {
    private Integer id;
    private String metricName;
    private Integer used;
    private Date updateTime;
    private Integer monthly;
    private Integer weekly;
    
    @Id @GeneratedValue
    @Column(name = "id")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    @Column(name = "metric_name")
    public String getMetricName() {
        return metricName;
    }
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
    @Column(name = "used")
    public Integer getUsed() {
        return used;
    }
    public void setUsed(Integer used) {
        this.used = used;
    }
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name = "monthly")
    public Integer getMonthly() {
        return monthly;
    }
    public void setMonthly(Integer monthly) {
        this.monthly = monthly;
    }
    @Column(name = "weekly")
    public Integer getWeekly() {
        return weekly;
    }
    public void setWeekly(Integer weekly) {
        this.weekly = weekly;
    }
}
