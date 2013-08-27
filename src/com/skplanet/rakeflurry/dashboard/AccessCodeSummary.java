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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.file.FileManager;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.meta.KeyMapDef;

@Entity
@Table(name="tb_accesscode")
public class AccessCodeSummary {
    
    private Logger logger = LoggerFactory.getLogger(AccessCodeSummary.class);
    
    
    private List<ApiKeySummary> apiKeySummaries = new LinkedList<ApiKeySummary>();
    
    private Integer accessCodeId = null;
    @JsonIgnore
    private DashBoard parDashBoard = null;
    private Integer totalCount = 0;
    private String accessCode = null;
    private String mbrNo = null;
    private String startTime = null;
    private String finishTime = null;
    private RunningStatus runningStatus = RunningStatus.STANDBY;
    private String sourceUri = null;
    private String updateTime = null;   
    private String worker = null;
    
    public void init(String accessCode, String mbrNo, List<String> apiKeyList, DashBoard parent) {
        this.accessCode = accessCode;
        this.mbrNo = mbrNo;
        
        int count = apiKeyList.size();
        
        for(int i = 0; i < count; ++i) {
            String apiKey = apiKeyList.get(i);
            ApiKeySummary apiKeySummary = new ApiKeySummary();
            
            apiKeySummary.init(apiKey, AppMetricsApi.getInstance().getApiList(), this);
            apiKeySummaries.add(apiKeySummary);
        }
        totalCount = apiKeySummaries.size();
        parDashBoard = parent;
        sourceUri = FileManager.getInstance().getStrFullDestUri();
        logger.info("accessCodeSummary initialized. total {} api key.", totalCount);
    }
    
    public void update() throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(this);
            tx.commit();
            logger.info("update access code summary to db.");
        } catch (Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    
    @Id @GeneratedValue
    @Column(name="accesscode_id")
    public Integer getAccessCodeId() {
        return accessCodeId;
    }
    public void setAccessCodeId(Integer accessCodeId) {
        this.accessCodeId = accessCodeId;
    }
    @ManyToOne
    @JoinColumn(name="dashboard_id")
    public DashBoard getParDashBoard() {
        return parDashBoard;
    }
    public void setParDashBoard(DashBoard parDashBoard) {
        this.parDashBoard = parDashBoard;
    }
    @OneToMany( cascade = CascadeType.ALL, mappedBy="parAccessCodeSummary", fetch=FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<ApiKeySummary> getApiKeySummaries() {
        return apiKeySummaries;
    }
    public void setApiKeySummaries(List<ApiKeySummary> apiKeySummaries) {
        this.apiKeySummaries = apiKeySummaries;
    }
    @Column(name="total_count")
    public Integer getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    @Column(name="access_code")
    public String getAccessCode() {
        return accessCode;
    }
    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
    
    @Column(name="mbr_no")
    public String getMbrNo() {
        return mbrNo;
    }

    public void setMbrNo(String mbrNo) {
        this.mbrNo = mbrNo;
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
    @Column(name="source_uri")
    public String getSourceUri() {
        return sourceUri;
    }
    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }
    @Column(name="update_time")
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name="worker")
    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }
    
}
