package com.skplanet.rakeflurry.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_worker")
public class WorkerM {
    private Integer workerId;
    private String serverAddr;
    private Integer workerCount;
    private Date updateTime;
    private Integer valid; 
    
    @Id @GeneratedValue
    @Column(name = "worker_id")
    public Integer getWorkerId() {
        return workerId;
    }
    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }
    @Column(name = "server_addr")
    public String getServerAddr() {
        return serverAddr;
    }
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }
    @Column(name = "worker_count")
    public Integer getWorkerCount() {
        return workerCount;
    }
    public void setWorkerCount(Integer workerCount) {
        this.workerCount = workerCount;
    }
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name = "valid")
    public Integer getValid() {
        return valid;
    }
    public void setValid(Integer valid) {
        this.valid = valid;
    }
    
}
