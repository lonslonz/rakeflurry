package com.skplanet.rakeflurry.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.collector.CollectParams;
import com.skplanet.rakeflurry.collector.Collector;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.file.FileManager;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.meta.KeyMapDef;
import com.skplanet.rakeflurry.model.KeyMapModel;

public class ShowJobs implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(ShowJobs.class);
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        
        logger.info("start show jobs : {} ", request.getParams());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            
            String date = request.getString("date");
            String id = request.getString("id");
            
            if(date != null) {
                List<DashBoard> dashboardList = DashBoard.selectByDate(date);
                resultMap.put("results", dashboardList);
            } else if(id != null) {
                DashBoard dashboard = DashBoard.selectById(Integer.parseInt(id));
                resultMap.put("results", dashboard);
            }
            else {
                DashBoard dashboard = DashBoard.selectLastOne();
                resultMap.put("results", dashboard );
            }
            
            
            
        } catch (Exception e) {
            resultMap.put("returnCode",  -1);
            resultMap.put("returnDesc",  "fail");
            resultMap.put("message", e.getMessage());
            throw e;
        } finally {
            response.setParams(resultMap);
            logger.info("complete show jobs : {} ", response.getParams());
        }
    }
}
