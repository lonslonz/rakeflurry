package com.skplanet.rakeflurry.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.rakeflurry.collector.AppMetrics;
import com.skplanet.rakeflurry.collector.CollectParams;
import com.skplanet.rakeflurry.collector.Collector;
import com.skplanet.rakeflurry.collector.UserManager;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.meta.FileManager;
import com.skplanet.rakeflurry.meta.KeyMapDef;

//TODO :
// api caller
// save file
// copy hadoop
// 
// start from middle
// restart


// 
// duration : 30
// accessCodeStartIndex : 0 ~ n-1
// accessCodeWorkRange : { start : 0, end : n-1 } -- when start : -1 : all
// apiCodeStartIndex

public class RakeFlurry implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(RakeFlurry.class);
    private static Boolean owned = false;
    
    private synchronized static Boolean ownRight() {
        if(!owned) {
            owned = true;
            return true;
        }
        return false;
    }
    private synchronized static void releaseRight() {
        owned = false;
    }
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        logger.info("collect service start. {}", request.getParams());
        
        if(!UserManager.validate(request.get("id"), request.get("password"))) {
            throw new Exception("id or password not valid.");
        }
        
        if(!ownRight()) {
            logger.warn("request when execution already exists.");
            throw new Exception("execution already exists");
        }
                
        try {
            Map<String, Object> data = request.getParams();
            Iterator<String> it = data.keySet().iterator();
            
            ObjectMapper mapper = new ObjectMapper();
            CollectParams params = mapper.convertValue(request.get("options"), CollectParams.class);
            
            params.init();
            
            FileManager.getInstance().init();
            AppMetricsApi.getInstance().init();
            KeyMapDef.getInstance().init();
            DashBoard.getInstance().init(KeyMapDef.getInstance());
            DashBoard.getInstance().saveAllIntoDb();
            
            Collector collector = new Collector(params);
            collector.collect();
            
            Map<String, Object> resultMap = new HashMap<String, Object>();
            //resultMap.put("AccessCodeSummaries",  DashBoard.getInstance().getAccessCodeSummaries());
            resultMap.put("results",  DashBoard.getInstance());
            
            response.setParams(resultMap);
            logger.info("collect service response : {} ", response.getParams());
        } finally {
            releaseRight();
        }
    }
}
