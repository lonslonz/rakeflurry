package com.skplanet.rakeflurry.collector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;


//duration : 30
//accessCodeStartIndex : 0 ~ n-1
//accessCodeWorkRange : { start : 0, end : n-1 } -- when start : -1 : all
//apiCodeStartIndex
public class CollectParams {
    private static Integer DURATION_DEFAULT = 30;
    
    private Integer duration;
    private Integer accessCodeStartIndex;
    private Map<String, Integer> accessCodeWorkRange;
    private Integer apiCodeStartIndex;
    
    private String endDay;
    private String startDay;
       
    private String id;
    private String password;
    private Integer dashboardId = null;
    private Boolean recover = null;
    
    public void init() {
        
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        cal.add(Calendar.DATE, -1);
        endDay = dateFormat.format(cal.getTime());
        
        if(this.duration == null) {
            cal.add(Calendar.DATE, Math.abs(DURATION_DEFAULT) * -1);
        } else {            
            cal.add(Calendar.DATE, Math.abs(this.duration) * -1);
        }
        
        startDay = dateFormat.format(cal.getTime());
    }

    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public Integer getAccessCodeStartIndex() {
        return accessCodeStartIndex;
    }
    public void setAccessCodeStartIndex(Integer accessCodeStartIndex) {
        this.accessCodeStartIndex = accessCodeStartIndex;
    }
    public Map<String, Integer> getAccessCodeWorkRange() {
        return accessCodeWorkRange;
    }
    public void setAccessCodeWorkRange(Map<String, Integer> accessCodeWorkRange) {
        this.accessCodeWorkRange = accessCodeWorkRange;
    }
    public Integer getApiCodeStartIndex() {
        return apiCodeStartIndex;
    }
    public void setApiCodeStartIndex(Integer apiCodeStartIndex) {
        this.apiCodeStartIndex = apiCodeStartIndex;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Boolean getRecover() {
        return recover;
    }

    public void setRecover(Boolean recover) {
        this.recover = recover;
    }

   
    
    
    
}
