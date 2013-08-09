package com.skplanet.rakeflurry.collector;

import com.skplanet.rakeflurry.dashboard.DashBoard;

// TODO : 
// dashboard into db
// install hadoop
// file copy to hdfs
// read information from db
public class Collector {
    
    private CollectParams params = null;
    private DashBoard dashboard = null;
    
    private Collector() {
        
    }
    public Collector(CollectParams params, DashBoard dashboard) {
        this.params = params;
        this.dashboard = dashboard;
    }
    
    public void collect() throws Exception {
        AppMetrics appMetrics = new AppMetrics(params, dashboard);
        appMetrics.collect();
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
