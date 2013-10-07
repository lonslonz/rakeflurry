package com.skplanet.rakeflurry.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rakecommon.tb_rcpt")
public class MailRcptM {

    private Integer rcptId;
    private String service;
    private String rcptAddr;
    private String updateTime;
    
    @Id
    @GeneratedValue
    @Column(name = "rcpt_id")
    public Integer getRcptId() {
        return rcptId;
    }
    public void setRcptId(Integer rcptId) {
        this.rcptId = rcptId;
    }
    @Column(name = "service")
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    @Column(name = "rcpt_addr")
    public String getRcptAddr() {
        return rcptAddr;
    }
    public void setRcptAddr(String rcptAddr) {
        this.rcptAddr = rcptAddr;
    }
    @Column(name = "update_Time")
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    
}
