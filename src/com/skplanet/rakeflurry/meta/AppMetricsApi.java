package com.skplanet.rakeflurry.meta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.db.AppMetricsDao;
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.KeyMapDao;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.rakeflurry.service.CollectApi;

public class AppMetricsApi {  
    private List<String> apiList = new ArrayList<String>();
    private static AppMetricsApi instance = new AppMetricsApi(); 
    public static synchronized AppMetricsApi getInstance() {
        return instance;
    }
    public List<String> getApiList() {
        return apiList;
    }
    public void init() {
        
        Logger logger = LoggerFactory.getLogger(AppMetricsApi.class);
        
        apiList.clear();
        getDataFromDb();
        
        logger.info("app metrics api of flurry initialized. total {} api", apiList.size());
    }
    public void getDataFromDb() {
        Session session = HiberUtil.openSession();
        Transaction tx = session.beginTransaction();
        
        String hql  = "from AppMetricsDao A where A.used = 1 order by A.id";
        Query query = session.createQuery(hql);
        
        List result = query.list();
        Iterator itRes = result.iterator();
        
        while(itRes.hasNext()) {
            AppMetricsDao obj = (AppMetricsDao)itRes.next();
            apiList.add(obj.getMetricName());
        }
        
        tx.commit();
        session.close();
    }
}
