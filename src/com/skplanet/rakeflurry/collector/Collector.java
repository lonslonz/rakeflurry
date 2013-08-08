package com.skplanet.rakeflurry.collector;

// TODO : 
// dashboard into db
// install hadoop
// file copy to hdfs
// read information from db
public class Collector {
    
    private CollectParams params = null;
    
    private Collector() {
        
    }
    public Collector(CollectParams params) {
        this.params = params;
    }
    
    public void collect() throws Exception {
        AppMetrics appMetrics = new AppMetrics(params);
        appMetrics.collect();
    }
    
    public CollectParams getParams() {
        return params;
    }
    public void setParams(CollectParams params) {
        this.params = params;
    }
    
}
