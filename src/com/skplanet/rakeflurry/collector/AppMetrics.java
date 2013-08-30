package com.skplanet.rakeflurry.collector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.config.ConfigReader;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.dashboard.AccessCodeSummary;
import com.skplanet.rakeflurry.dashboard.ApiKeySummary;
import com.skplanet.rakeflurry.dashboard.ApiSummary;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.dashboard.RunningStatus;
import com.skplanet.rakeflurry.dashboard.WorkStatus;
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.file.FileManager;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.meta.KeyMapDef;
import com.skplanet.rakeflurry.model.CollectOptions;
import com.skplanet.rakeflurry.model.CollectParams;
import com.skplanet.rakeflurry.service.CollectApi;

// certain api key, access code
// middle of api key
// 
public class AppMetrics {
    private Logger logger = LoggerFactory.getLogger(AppMetrics.class);
    private static int SLEEP_TIME_MSEC = 1000;
    private static String URL_TEMPLATE = 
            "http://api.flurry.com/appMetrics/%s?apiAccessCode=%s&apiKey=%s&startDate=%s&endDate=%s";
            //"http://api.flurry.com/appMetrics/%s?apiAccessCode=%s&apiKey=%s&startDate=%s&endDate=%s&country=ALL";
    private long totalElapsed = 0;
    private long totalCount = 0;
    private long errorCount = 0;
    private long totalRetryCount = 0;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");   
    private static DateFormat timeFormat = new SimpleDateFormat("HHmm");
    
    private CollectParams params = null;
    private DashBoard dashboard = null;
    
    private AppMetrics() {
        
    }
    public AppMetrics(CollectParams params, DashBoard dashboard) {
        this.params = params;
        this.dashboard = dashboard;
    }
    

    public Boolean collectAccessCode(AccessCodeSummary acs) throws Exception {
        
        acs.setStartTime(dateFormat.format(new Date()));
        acs.setRunningStatus(RunningStatus.DOWNLOADING);
        PartInfo partInfo = CollectOptions.getPartInfo(params.getOptions());
        if(partInfo.getServerId() != null) {
            acs.setWorker(partInfo.toString());
        }
        
        HiberUtil.update(acs, "update starting access summary code.");
        
        List<ApiKeySummary> aksList = acs.getApiKeySummaries();
        
        Boolean error = false;
        Boolean errorDetected = false;
        for(int i = 0; i < aksList.size(); ++i) {
            
            ApiKeySummary aks = aksList.get(i);
             
            error = collectApiKey(acs, aks);
            if(error) {
                errorDetected = true;
                Alerter alerter = new Alerter();
                alerter.errorApiKey(acs, aks); 
            }
            error = FileManager.getInstance().copyToHdfs(acs, aks, dashboard);
            if(error) {
                errorDetected = true;
                Alerter alerter = new Alerter();
                alerter.errorHdfsCopy(acs, aks);
            }
        }
        
        acs.setFinishTime(dateFormat.format(new Date()));
        if(errorDetected) {
            acs.setRunningStatus(RunningStatus.ERROR);
        } else {
            acs.setRunningStatus(RunningStatus.COMPLETE);
        }
        logElapsed();
        HiberUtil.update(acs, "update finishing access summary code.");
        return errorDetected;
    }
   
    public Boolean collectApiKey(AccessCodeSummary acs, ApiKeySummary aks) throws Exception {
        
        List<String> apiList = AppMetricsApi.getInstance().getApiList();

        Boolean error = false;
        
        aks.setStartTime(dateFormat.format(new Date()));
        aks.setRunningStatus(RunningStatus.DOWNLOADING);
        
        String fileName = FileManager.getDataFileName(acs, aks, dashboard);
        String fullFileName = FileManager.getInstance().getLocalDataDir() + "/" + fileName;
        
        FileOutputStream fos = new FileOutputStream(fullFileName, false);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
        aks.setFileName(fileName);
        
        ApiSummary as = null;
        logger.debug("api list.size : {}", apiList.size());
        for(int i = 0; i < apiList.size(); i++) {
            try {
                logger.info("try api, access code : {}, api key : {}, api : {}",  
                            new Object[]{acs.getAccessCode(), aks.getApiKey(), apiList.get(i)});
                
                as = new ApiSummary();
                as.init(apiList.get(i), aks);
                aks.getApiSummaries().add(as);
                HiberUtil.update(aks, "update db about starting api key.");
                
                String result = requestApiWithRetry(acs, aks, as);
                bw.write(result);
                bw.write("\n");
            } catch (Exception e) {
                aks.setRunningStatus(RunningStatus.ERROR);
                acs.setRunningStatus(RunningStatus.ERROR);
                aks.setErrorMsgLimit(StringUtil.exception2Str(e));
                error = true;
                HiberUtil.update(acs, "update error api key.");
                continue;
            }
        }
        
        bw.close();
        
        aks.setFinishTime(dateFormat.format(new Date()));
        if(!error) {
            aks.setRunningStatus(RunningStatus.COMPLETE);
        } 
        HiberUtil.update(aks, "update finishing api key.");
        return error;
    }
    
    public String requestApiWithRetry(AccessCodeSummary acs, ApiKeySummary aks, ApiSummary as) throws Exception {
        int retryCount = 0;
        String result = null;
        String url = null;
        int retryMax = Integer.parseInt(ConfigReader.getInstance().getServerConfig().getPropValue("retryMax"));
        
        as.setStartTime(dateFormat.format(new Date()));
        as.setRunningStatus(RunningStatus.DOWNLOADING);
        as.setRetryCount(0);
        HiberUtil.update(as, "update db about starting api.");
        
        long elapsed = 0;
        while(true) {
            try {
                long start = System.currentTimeMillis();
                // one month
                url = String.format(URL_TEMPLATE, 
                                    as.getApi(),
                                    acs.getAccessCode(), 
                                    aks.getApiKey(),
                                    dashboard.getCallStartDay(),
                                    dashboard.getCallEndDay());
                
                logger.info("url : {}",  url);
                result = HttpRequest.sendHttpGet(url);
                logger.info("result length : {}", result.length());
                logger.debug("result : {}", result);
             
                elapsed = System.currentTimeMillis() - start;
                  
                long apiCallTime = Integer.parseInt(
                                        ConfigReader.getInstance().getServerConfig().getPropValue("apiCallTimeInterval"));
                long sleepForNext =  apiCallTime - elapsed; 
                if(sleepForNext > 0) {
                    Thread.sleep(sleepForNext);
                }
                
                totalElapsed += elapsed;
                ++totalCount;
                logger.info("elapsed : {}, sleep for next : {}", (double)elapsed/1000, sleepForNext);
                
                as.setFinishTime(dateFormat.format(new Date()));
                as.setRunningStatus(RunningStatus.COMPLETE);
                as.setElapsed(elapsed);
                HiberUtil.update(as, "update finishing api.");
                
                return result;
                
                
            } catch(Exception ex) {
                
                if (ex instanceof FlurryException) {
                    FlurryErrorResponse resp = ((FlurryException)ex).getFlurryResponse();
                    logger.error("retry count : {}, httpStatusCode : {}, flurryResponse : {}, url : {}, {} ",
                            new Object[]{
                               retryCount + 1,
                               ((FlurryException)ex).getHttpStatusCode(),
                               resp.toString(),
                               url,
                               StringUtil.exception2Str(ex)});
                }
                else {
                    logger.error("retry count : {}, url : {}, {}", 
                            new Object[]{retryCount + 1, url, StringUtil.exception2Str(ex)});
                }
                
                ++retryCount;
                ++totalRetryCount;
                as.setRetryCount(retryCount);
                
                if(retryCount > retryMax) {
                    as.setRunningStatus(RunningStatus.ERROR);
                    as.setFinishTime(dateFormat.format(new Date()));
                    HiberUtil.update(as, "update error api.");
                    ++errorCount;
               
                    throw ex;
                } else {
                    logger.error("retry. sleep {} msec", SLEEP_TIME_MSEC);
                    Thread.sleep(SLEEP_TIME_MSEC);
                    continue;
                }
            } finally {

            }
        }
    }
    private void logElapsed() {
        logger.info("totalElapsed : {} sec, totalCount : {}, errorCount : {}, totalRetryCount : {}", 
                    new Object[]{(double)totalElapsed/1000, totalCount, errorCount, totalRetryCount});
    }
    
    
}
