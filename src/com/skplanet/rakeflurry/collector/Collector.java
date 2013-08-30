package com.skplanet.rakeflurry.collector;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.config.ConfigReader;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.dashboard.AccessCodeSummary;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.WorkerDao;
import com.skplanet.rakeflurry.model.CollectOptions;
import com.skplanet.rakeflurry.model.CollectParams;

public class Collector {
    private Logger logger = LoggerFactory.getLogger(Collector.class);
    
    private CollectParams params = null;
    private DashBoard dashboard = null;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static String CALL_PART_URL = "http://%s:8100/rakeflurry/collectpart";
    
    private Collector() {
        
    }
    public Collector(CollectParams params, DashBoard dashboard) {
        this.params = params;
        this.dashboard = dashboard;
    }
    
    public void collect() throws Exception {
        if(params.isMulti()) {
            collectMultiLow();
        } else {
            collectLow(false);
        }
        
    }
    private int calcWorkerSize(List<WorkerDao> workerList) {
        int totalServerCount = 0;
        for(int i = 0; i < workerList.size(); i++) {
            totalServerCount += workerList.get(i).getWorkerCount();
        }
        return totalServerCount;
    }
    private void spread() throws Exception {
        List<WorkerDao> workerList = selectWorker();
        int totalWorkerCount = calcWorkerSize(workerList);
        
        int workerIndex = 0;
        ObjectMapper mapper = new ObjectMapper();
        
        ExecutorService executor = Executors.newFixedThreadPool(totalWorkerCount);
        List<Future<String>> futureList = new ArrayList<Future<String>>();
        
        for(int i = 0; i < workerList.size(); i++) {
            WorkerDao worker = workerList.get(i);
            for(int j = 0; j < worker.getWorkerCount(); j++) {
                CollectParams params = new CollectParams();
                params.setId(ConfigReader.getInstance().getServerConfig().getPropValue("id"));
                params.setPassword(ConfigReader.getInstance().getServerConfig().getPropValue("password"));
                
                CollectOptions options = new CollectOptions();
                options.setServerId(workerIndex++);
                options.setTotalServerCount(totalWorkerCount);
                options.setDuration(this.params.getOptions().getDuration());  
                options.setDashboardId(dashboard.getDashboardId());
                params.setOptions(options);
                
                String url = String.format(CALL_PART_URL, worker.getServerAddr());
                String jsonData = mapper.writeValueAsString(params);
                
                Callable<String> workerThread = new HttpWorker(url, jsonData);
                
                Future<String> future = executor.submit(workerThread);
                futureList.add(future);
                
                logger.info("call part execution : {}, url : {}, json : {}", 
                            new Object[]{CollectOptions.getPartInfo(options), url, jsonData});
            }
        }
        
        for(int i = 0; i < futureList.size(); i++) {
            try {
                String result = futureList.get(i).get();
                logger.info("finish part : {}", result);
            } catch (InterruptedException ie) {
                logger.warn(StringUtil.exception2Str(ie));
            } catch (ExecutionException ee) {
                logger.warn(StringUtil.exception2Str(ee));
            }
        }
        executor.shutdown();
        logger.info("finish all part.");
    }
    private void collectMultiLow() throws Exception {
        dashboard.setStartTime(dateFormat.format(new Date()));
        dashboard.setCallStartDay(params.getStartDay());
        dashboard.setCallEndDay(params.getEndDay());
        HiberUtil.update(dashboard, "update starting dashboard.");
        
        spread();
        
        dashboard.setFinishTime(dateFormat.format(new Date()));
        dashboard.getAccessCodeSummaries().clear();
        HiberUtil.update(dashboard, "update finishing dashboard.");
        
    }
    public void collectPart() throws Exception {
        collectCore();
    }
    public void recover() throws Exception {
        collectLow(true);
    }
    private void collectLow(Boolean reusePrevDashboard) throws Exception {
        
        dashboard.setStartTime(dateFormat.format(new Date()));
        if(!reusePrevDashboard) {
            dashboard.setCallStartDay(params.getStartDay());
            dashboard.setCallEndDay(params.getEndDay());
        }
        HiberUtil.update(dashboard, "update starting dashboard.");
        
        collectCore();
        
        dashboard.setFinishTime(dateFormat.format(new Date()));
        HiberUtil.update(dashboard, "update finishing dashboard.");
    }
    private void collectCore() throws Exception {
        AppMetrics appMetrics = new AppMetrics(params, dashboard);
        List<AccessCodeSummary> acsList = dashboard.getAccessCodeSummaries();
        
        int i;
        for(i = 0; i < acsList.size(); ++i) {
        
            AccessCodeSummary acs = acsList.get(i);
            appMetrics.collectAccessCode(acs);
        }
        
        if(i == 0) {
            logger.info("Nothing to do. There is no access code summaries. {}", CollectOptions.getPartInfo(params.getOptions()));
        }
    }
    public List<WorkerDao> selectWorker()  throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String hql  = "from WorkerDao W where W.valid = 1 order by W.workerId desc";
            Query query = session.createQuery(hql);
            
            List result = query.list();            
            
            tx.commit();
            return (List<WorkerDao>)result;
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
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
