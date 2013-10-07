package com.skplanet.rakeflurry.meta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.model.AppMetricsM;
import com.skplanet.rakeflurry.model.KeyMapM;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.rakeflurry.service.CollectApi;
import com.skplanet.rakeflurry.util.HiberUtil;

public class AppMetricsApi {  
    private Logger logger = LoggerFactory.getLogger(AppMetricsApi.class);
    private List<AppMetricsM> apiList = new ArrayList<AppMetricsM>();
    private static AppMetricsApi instance = new AppMetricsApi(); 
    public static AppMetricsApi getInstance() {
        return instance;
    }
    public synchronized List<AppMetricsM> getApiList() {
        return apiList;
    }
    public synchronized void init() {
        
        Logger logger = LoggerFactory.getLogger(AppMetricsApi.class);
        logger.debug("AppMetrics api init start", apiList.size());
        
        apiList.clear();
        logger.debug("api list.size() : {}", apiList.size());
        getDataFromDb();
        
        logger.info("app metrics api of flurry initialized. total {} api", apiList.size());
        for(int i = 0; i < apiList.size(); i++) {
            
            AppMetricsM metricsM = apiList.get(i);
            logger.info("api : {}, monthly : {}, weekly : {}", 
                    new Object[]{metricsM.getMetricName(), metricsM.getMonthly(), metricsM.getWeekly()});
        }
    }
    private void getDataFromDb() {
        Session session = HiberUtil.openSession();
        Transaction tx = session.beginTransaction();
        
        String hql  = "from AppMetricsM A where A.used = 1 order by A.metricName";
        Query query = session.createQuery(hql);
        
        List result = query.list();
        Iterator itRes = result.iterator();
        
        while(itRes.hasNext()) {
            AppMetricsM obj = (AppMetricsM)itRes.next();
            apiList.add(obj);
        }
        logger.info("read api list from db : {}", apiList.toString());
        
        tx.commit();
        session.close();
    }
}
