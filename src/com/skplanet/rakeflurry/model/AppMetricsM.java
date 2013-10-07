package com.skplanet.rakeflurry.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_appmetrics")
public class AppMetricsDao {
    private Integer id;
    private String metricName;
    private Integer used;
    private Date updateTime;
    
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
    @Column(name = "update_Time")
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    
}
