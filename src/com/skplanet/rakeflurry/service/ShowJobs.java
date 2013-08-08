package com.skplanet.rakeflurry.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.rakeflurry.collector.CollectParams;
import com.skplanet.rakeflurry.collector.Collector;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.meta.FileManager;
import com.skplanet.rakeflurry.meta.KeyMapDef;

public class ShowJobs implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(ShowJobs.class);
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        

        try {
            Map<String, Object> data = request.getParams();
            Iterator<String> it = data.keySet().iterator();
            
            ObjectMapper mapper = new ObjectMapper();
            CollectParams params = mapper.convertValue(request.getParams(), CollectParams.class);
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
            logger.info("simple service response : {} ", response.getParams());
        } finally {
           
        }
    }
}
