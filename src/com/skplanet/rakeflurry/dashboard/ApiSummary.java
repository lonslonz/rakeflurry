package com.skplanet.rakeflurry.dashboard;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name="tb_api")
public class ApiSummary {
    
    private RunningStatus runningStatus = RunningStatus.STANDBY;
    private Integer apiId = null;
    @JsonIgnore
    private ApiKeySummary parApiKeySummary = null;
    private String api = null;
    private String startTime = null;
    private String finishTime = null;
    private String reqUrl = null;
    private String uri = null;
    private String updateTime = null;
    private Long elapsed = null;
    private Integer retryCount = null;
    
    public void init(String api, ApiKeySummary parent) {
        this.api = api;
        this.parApiKeySummary = parent;
    }
    
    @Id @GeneratedValue
    @Column(name="api_id")
    public Integer getApiId() {
        return apiId;
    }
    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }
    @ManyToOne
    @JoinColumn(name="apikey_id")
    public ApiKeySummary getParApiKeySummary() {
        return parApiKeySummary;
    }
    public void setParApiKeySummary(ApiKeySummary parApiKeySummary) {
        this.parApiKeySummary = parApiKeySummary;
    }
    @Column(name="api")
    public String getApi() {
        return api;
    }
    public void setApi(String api) {
        this.api = api;
    }

    @Column(name="running_status")
    @Enumerated(EnumType.STRING)
    public RunningStatus getRunningStatus() {
        return runningStatus;
    }
    public void setRunningStatus(RunningStatus runningStatus) {
        this.runningStatus = runningStatus;
    }
    @Column(name="req_url")
    public String getReqUrl() {
        return reqUrl;
    }
    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }
    @Column(name="uri")
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
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
    @Column(name="update_time")
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name="elapsed")
    public Long getElapsed() {
        return elapsed;
    }

    public void setElapsed(Long elapsed) {
        this.elapsed = elapsed;
    }
    @Column(name="retry_count")
    public Integer getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}
