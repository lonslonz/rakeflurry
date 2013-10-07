package com.skplanet.rakeflurry.service;

import java.net.URI;
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
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.collector.AppMetrics;
import com.skplanet.rakeflurry.collector.Collector;
import com.skplanet.rakeflurry.collector.Alerter;
import com.skplanet.rakeflurry.collector.UserManager;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.file.FileManager;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.meta.KeyMapDef;
import com.skplanet.rakeflurry.model.CollectParams;
import com.skplanet.rakeflurry.util.FileSystemHelper;
import com.skplanet.rakeflurry.util.Right;


public class CollectApi implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(CollectApi.class);

    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        logger.info("start collect service. {}", request.getParams());
        
        ObjectMapper mapper = new ObjectMapper();
        CollectParams params = mapper.convertValue(request.getParams(), CollectParams.class);
        params.init();
        
        if(!UserManager.validate(params.getId(), params.getPassword())) {
            throw new Exception("id or password not valid.");
        }
        
        if(!Right.ownRight()) {
            logger.warn("request when execution already exists.");
            throw new Exception("execution already exists");
        }
            
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            
            FileManager.getInstance().init();
            AppMetricsApi.getInstance().init();
            
            KeyMapDef keymapDef = new KeyMapDef();
            keymapDef.init();
            
            DashBoard dashboard = new DashBoard();
            dashboard.init(keymapDef);
            
            dashboard.saveAllIntoDb();
                        
            Collector collector = new Collector(params, dashboard);
            collector.collect();
            
            if(params.isMulti()) {
                dashboard = DashBoard.selectById(dashboard.getDashboardId()); 
            }
            
            boolean hasError = dashboard.hasError();
            resultMap.put("results",  dashboard);
            resultMap.put("returnCode", hasError ? 0 : 1);
            
            response.setParams(resultMap);
            
            Alerter alerter = new Alerter();
            alerter.finishCollectApiService(dashboard, hasError);
            
            logger.info("complete collect service : {} ", mapper.writeValueAsString(response.getParams()));
        } catch(Exception e) {
            resultMap.put("returnCode",  -1);
            resultMap.put("returnDesc",  "fail");
            resultMap.put("message", e.getMessage()); 
            logger.error(StringUtil.exception2Str(e));
            
            Alerter alerter = new Alerter();
            alerter.errorCollectApiService(e.getMessage());
            
            throw e;
        }
        finally {
            Right.releaseRight();
        }
    }
}
