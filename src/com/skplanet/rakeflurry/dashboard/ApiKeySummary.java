package com.skplanet.rakeflurry.dashboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.skplanet.rakeflurry.meta.AppMetricsApi;

@Entity
@Table(name="tb_apikey")
public class ApiKeySummary {
    private String apiKey = null;
    
    private List<ApiSummary> apiSummaries = new LinkedList<ApiSummary>();    
    private Integer apiKeyId = null;
    @JsonIgnore
    private AccessCodeSummary parAccessCodeSummary = null;
    private Integer totalCount = 0;
    private String name = null;
    private String startTime = null;
    private String finishTime = null;
    private RunningStatus runningStatus = RunningStatus.STANDBY;
    private String fileName = null;
    private String updateTime = null;
    private String errorMsg = null;
    
    public void init(String apiKey, List<String> apiList, AccessCodeSummary parent) {
        
        this.apiKey = apiKey;
        this.totalCount = AppMetricsApi.getInstance().getApiList().size();
        this.parAccessCodeSummary = parent;
    }
    
    @Id @GeneratedValue
    @Column(name="apikey_id")
    public Integer getApiKeyId() {
        return apiKeyId;
    }
    public void setApiKeyId(Integer apiKeyId) {
        this.apiKeyId = apiKeyId;
    }
    @ManyToOne
    @JoinColumn(name="accesscode_id")
    public AccessCodeSummary getParAccessCodeSummary() {
        return parAccessCodeSummary;
    }
    public void setParAccessCodeSummary(AccessCodeSummary parAccessCodeSummary) {
        this.parAccessCodeSummary = parAccessCodeSummary;
    }    
    @OneToMany( cascade = CascadeType.ALL, mappedBy="parApiKeySummary", fetch=FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<ApiSummary> getApiSummaries() {
        return apiSummaries;
    }
    public void setApiSummaries(List<ApiSummary> apiSummaries) {
        this.apiSummaries = apiSummaries;
    }
  
    
    @Column(name="apikey")
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    @Column(name="name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Column(name="start_time")
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    @Column(name="finish_time")
    public String getFinishTime() {
        return finishTime;
    }
    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }
    @Column(name="running_status")
    @Enumerated(EnumType.STRING)
    public RunningStatus getRunningStatus() {
        return runningStatus;
    }
    public void setRunningStatus(RunningStatus runningStatus) {
        this.runningStatus = runningStatus;
    }


    @Column(name="total_count")
    public Integer getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    @Column(name="filename")
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    @Column(name="update_time")
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name="error_msg")
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public void setErrorMsgLimit(String errorMsg) {
        if(errorMsg != null) {
            setErrorMsg(errorMsg.substring(0, 2045));
        } else {
            setErrorMsg(errorMsg);
        }
    }
}
