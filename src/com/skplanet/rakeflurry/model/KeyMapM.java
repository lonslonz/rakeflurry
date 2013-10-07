package com.skplanet.rakeflurry.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_keymap")
public class KeyMapM {
    private String id;
    private String mbrNo;
    private String apiKey;
    
    private String accessCode;
    private String apiKeyName;
    private Integer used = 1;
    private Date updateTime;
    
    @Id @GeneratedValue
    @Column(name = "id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "mbr_no")
    public String getMbrNo() {
        return mbrNo;
    }
    public void setMbrNo(String mbrNo) {
        this.mbrNo = mbrNo;
    }
    
    @Column(name = "access_code")
    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
    @Column(name = "api_key")
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    @Column(name = "used")
    public Integer getUsed() {
        return used;
    }
    public void setUsed(Integer used) {
        this.used = used;
    }
    @Column(name = "api_key_name")
    public String getApiKeyName() {
        return apiKeyName;
    }
    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    
    
}
