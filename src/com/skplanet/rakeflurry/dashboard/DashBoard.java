package com.skplanet.rakeflurry.dashboard;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.collector.CollectParams;
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.KeyMapDao;
import com.skplanet.rakeflurry.meta.KeyMapDef;
import com.skplanet.rakeflurry.model.KeyMapModel;

@Entity
@Table(name="tb_dashboard")
public class DashBoard {
    private Logger logger = LoggerFactory.getLogger(DashBoard.class);
    //private static DashBoard instance = new DashBoard();
    
    private List<AccessCodeSummary> accessCodeSummaries = new LinkedList<AccessCodeSummary>();
    
    private Integer dashboardId = null;
    private Integer totalCount = 0;
    private String startTime = null;
    private String finishTime = null;
    
    @JsonIgnore
    private String updateTime = null;
    
    private String callStartDay = null;
    private String callEndDay = null;
    private Integer recoverWhoId = null;
    private Integer recoverMeId = null;
    
    
    public void init(KeyMapDef keyMapDef) {
        initWithKeyMapList(keyMapDef.getKeyMapList());
    }
    public void initWithKeyMapList(List<KeyMapModel> kmmList) {
        accessCodeSummaries.clear();
        
        Iterator<KeyMapModel> it = kmmList.iterator();
        
        while(it.hasNext()) {
            KeyMapModel kmm = (KeyMapModel)it.next();
            AccessCodeSummary as = new AccessCodeSummary();
            accessCodeSummaries.add(as);
            as.init( kmm.getAccessCode(), kmm.getMbrNo(), kmm.getApiKeys(), this);
        }
        totalCount = accessCodeSummaries.size();
        
        logger.info("dashboard initialized. total {} access code.", totalCount);
    }
   
    public void saveAllIntoDb() throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            Integer id = (Integer)session.save(this);
            tx.commit();
            logger.info("save dashboard into db. id : {}", id);
        } catch (Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
            
        }
        
    }
    public void update() throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            session.update(this);
            tx.commit();
            logger.info("update dashboard to db.");
        } catch (Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
        
    }
    public static List<DashBoard> selectAll() throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String hql  = "from DashBoard D order by D.startTime desc";
            Query query = session.createQuery(hql);
            
            List result = query.list();
            Iterator itRes = result.iterator();
            
            List<DashBoard> dashboardList = (List<DashBoard>)result;
            
            tx.commit();
            return result;
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    public static DashBoard selectLastOne(boolean includeRecover) throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String hql;
            if(includeRecover) {
                hql  = "from DashBoard D order by dashboard_id desc ";    
            } else {
                hql  = "from DashBoard D where D.recoverWhoId is null order by dashboard_id desc ";
            }
            
            Query query = session.createQuery(hql);
            query.setMaxResults(1);
            
            DashBoard resultBoard = (DashBoard)query.uniqueResult();
            
            tx.commit();
            return resultBoard;
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    public static List<DashBoard> selectByDate(String date, boolean includeRecover) throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        String begin = date + " 00:00:00";
        String end = date + " 23:59:59";
        try {
            tx = session.beginTransaction();
            
            String hql;
            if(includeRecover) {
                hql = "from DashBoard D where D.startTime >= :begin and D.startTime < :end " + 
                        "order by D.startTime desc";
            } else {
                hql = "from DashBoard D where D.recoverWhoId is null and D.startTime >= :begin and D.startTime < :end " + 
                        "order by D.startTime desc";
            }
            
            Query query = session.createQuery(hql);
            query.setParameter("begin", begin);
            query.setParameter("end", end);
            
            List<DashBoard> boardList = (List<DashBoard>)query.list();
            
            tx.commit();
            return boardList;
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    public static DashBoard selectById(Integer id) throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            
            String hql  = "from DashBoard D where D.dashboardId = :id"; 
            Query query = session.createQuery(hql);
            query.setParameter("id", id);
            
            DashBoard dashboard = (DashBoard)query.uniqueResult();
            
            tx.commit();
            return dashboard;
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    @JsonIgnore
    @Transient
    public List<KeyMapModel> filterFailedKeyMap() {
        
        List<KeyMapModel> kmmList = new ArrayList<KeyMapModel>();
        for(int i = 0; i < accessCodeSummaries.size(); i++) {
            AccessCodeSummary currAcs = accessCodeSummaries.get(i);
            if(currAcs.getRunningStatus() == RunningStatus.ERROR) {
                KeyMapModel kmm = new KeyMapModel();
                kmmList.add(kmm);
                
                kmm.setMbrNo(currAcs.getMbrNo());
                kmm.setAccessCode(currAcs.getAccessCode());
                
                for(int j = 0; j < currAcs.getApiKeySummaries().size(); j++) {
                    ApiKeySummary currAks = currAcs.getApiKeySummaries().get(j);
                    
                    if(currAks.getRunningStatus() == RunningStatus.ERROR) {
                        kmm.getApiKeys().add(currAks.getApiKey());
                    }
                }
            }
        }
        return kmmList;
    }
    public void removeApi() {
        for(int i = 0; i < accessCodeSummaries.size(); ++i) { 
            AccessCodeSummary currAcs = accessCodeSummaries.get(i);
            for(int j = 0; j < currAcs.getApiKeySummaries().size(); j++) {
                ApiKeySummary currAks = currAcs.getApiKeySummaries().get(j);
                currAks.setApiSummaries(null);
            }
        }
    }
    public AccessCodeSummary getAccessCodeSummary(String accessCode) {
        
        for(int i = 0; i < accessCodeSummaries.size(); ++i) {
            if(accessCodeSummaries.get(i).getAccessCode().equals(accessCode)) {
                return accessCodeSummaries.get(i);
            }
        }
        return null;
    }
    public boolean hasError() {
        for(int i = 0; i < accessCodeSummaries.size(); ++i) {
            if(accessCodeSummaries.get(i).getRunningStatus() == RunningStatus.ERROR) {
                return true;
            }
        }
        return false;
    }
    
    @Id @GeneratedValue
    @Column(name="dashboard_id")
    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }
    
    
    @Column(name="total_count")
    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
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
    @JsonProperty("mbrNoSummaries")
    @OneToMany(cascade = CascadeType.ALL, mappedBy="parDashBoard", fetch=FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<AccessCodeSummary> getAccessCodeSummaries() {
        return accessCodeSummaries;
    }


    public void setAccessCodeSummaries(List<AccessCodeSummary> accessCodeSummaries) {
        this.accessCodeSummaries = accessCodeSummaries;
    }
    @Column(name="call_start_day")
    public String getCallStartDay() {
        return callStartDay;
    }
    public void setCallStartDay(String callStartDay) {
        this.callStartDay = callStartDay;
    }
    @Column(name="call_end_day")
    public String getCallEndDay() {
        return callEndDay;
    }
    public void setCallEndDay(String callEndDay) {
        this.callEndDay = callEndDay;
    }
    @Column(name="recover_who_id")
    public Integer getRecoverWhoId() {
        return recoverWhoId;
    }
    public void setRecoverWhoId(Integer recoverWhoId) {
        this.recoverWhoId = recoverWhoId;
    }
    @Column(name="recover_me_id")
    public Integer getRecoverMeId() {
        return recoverMeId;
    }
    public void setRecoverMeId(Integer recoverMeId) {
        this.recoverMeId = recoverMeId;
    }
    

    
}
