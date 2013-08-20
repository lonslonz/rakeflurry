package com.skplanet.rakeflurry.collector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.skplanet.rakeflurry.dashboard.AccessCodeSummary;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.db.HiberUtil;



// TODO : 
// retry count 
// log4j
// access code result : error->complete
// test, real config 
// target name change
public class Collector {
    
    private CollectParams params = null;
    private DashBoard dashboard = null;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private Collector() {
        
    }
    public Collector(CollectParams params, DashBoard dashboard) {
        this.params = params;
        this.dashboard = dashboard;
    }
    
    public void collect() throws Exception {
        collectLow();
        
    }

    public void collectLow() throws Exception {
        
        AppMetrics appMetrics = new AppMetrics(params, dashboard);
        
        dashboard.setStartTime(dateFormat.format(new Date()));
        dashboard.setCallStartDay(params.getStartDay());
        dashboard.setCallEndDay(params.getEndDay());
        
        HiberUtil.update(dashboard, "update starting dashboard.");
        
        // TODO : multi thread and move this function to outside
        List<AccessCodeSummary> acsList = dashboard.getAccessCodeSummaries();
        for(int i = 0; i < acsList.size(); ++i) {
        
            AccessCodeSummary acs = acsList.get(i);
            Boolean error = appMetrics.collectAccessCode(acs);
        }
        
        dashboard.setFinishTime(dateFormat.format(new Date()));
        HiberUtil.update(dashboard, "update finishing dashboard.");
    }
    public void recover() throws Exception {
        
        AppMetrics appMetrics = new AppMetrics(params, dashboard);
        
        dashboard.setDashboardId(dashboard.getDashboardId() + 1);
        dashboard.setStartTime(dateFormat.format(new Date()));
        dashboard.setCallStartDay(params.getStartDay());
        dashboard.setCallEndDay(params.getEndDay());
        HiberUtil.update(dashboard, "update starting dashboard.");
        
        // TODO : multi thread and move this function to outside
        List<AccessCodeSummary> acsList = dashboard.getAccessCodeSummaries();
        for(int i = 0; i < acsList.size(); ++i) {
        
            AccessCodeSummary acs = acsList.get(i);
            Boolean error = appMetrics.collectAccessCode(acs);
        }
        
        dashboard.setFinishTime(dateFormat.format(new Date()));
        HiberUtil.update(dashboard, "update finishing dashboard.");
        
    }
    public CollectParams getParams() {
        return params;
    }
    public void setParams(CollectParams params) {
        this.params = params;
    }
    public DashBoard getDashboard() {
        return dashboard;
    }
    public void setDashboard(DashBoard dashboard) {
        this.dashboard = dashboard;
    }
    
}
