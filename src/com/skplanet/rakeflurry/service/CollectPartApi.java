package com.skplanet.rakeflurry.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.collector.Collector;
import com.skplanet.rakeflurry.collector.Alerter;
import com.skplanet.rakeflurry.collector.PartInfo;
import com.skplanet.rakeflurry.collector.PartManager;
import com.skplanet.rakeflurry.collector.UserManager;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.file.FileManager;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.model.CollectOptions;
import com.skplanet.rakeflurry.model.CollectParams;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.rakeflurry.util.Right;

// collect a part of accesscodes of dashboard.
public class CollectPartApi implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(CollectApi.class);

    // options : { duration = 30, dashboard_id = 1, total_server = 4, server_id = 1} 
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        logger.info("start collect part api  service. {}", request.getParams());
        
        ObjectMapper mapper = new ObjectMapper();
        CollectParams params = mapper.convertValue(request.getParams(), CollectParams.class);
        
        if(!UserManager.validate(params.getId(), params.getPassword())) {
            throw new Exception("id or password not valid.");
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            validateCollectOptions(params.getOptions());
            
            if(!PartManager.getInstance().ownPart(params)) {
                logger.warn("part execution already exists. " + params.getOptions().getServerId() + 
                            " / " + params.getOptions().getTotalServerCount());
                throw new Exception("execution already exists");
            }
            
            FileManager.getInstance().init();
            AppMetricsApi.getInstance().init();
            
            DashBoard dashboard = DashBoard.selectById(params.getOptions().getDashboardId());
            if(dashboard == null) {
                throw new Exception("there is no dashboard. id : " + params.getOptions().getDashboardId());
            }
            
            dashboard.removeAcsButTarget(params.getOptions());
            
            Collector collector = new Collector(params, dashboard);
            collector.collectPart();
                
            resultMap.put("results",  dashboard);
            resultMap.put("process",  CollectOptions.getPartInfo(params.getOptions()));
            response.setParams(resultMap);
            
            logger.info("complete collect part api service : {}, {} ", 
                    CollectOptions.getPartInfo(params.getOptions()),
                    response.getParams());
        } catch(Exception e) {
            resultMap.put("returnCode",  -1);
            resultMap.put("returnDesc",  "fail");
            resultMap.put("process",  CollectOptions.getPartInfo(params.getOptions()));
            resultMap.put("message", e.getMessage()); 
            logger.error("part : " + CollectOptions.getPartInfo(params.getOptions()) + ", " + StringUtil.exception2Str(e));
            
            PartInfo partInfo = null;
            if(params != null) {
                partInfo = CollectOptions.getPartInfo(params.getOptions());
            }
            Alerter alerter = new Alerter();
            alerter.errorCollectPartApiService(e.getMessage() + ", part Info : " + partInfo) ;
            throw e;
        }
        finally {
            PartManager.getInstance().releasePart(params);
        }
    }
    private boolean validateCollectOptions(CollectOptions options) throws Exception{
        if(options.getDashboardId() == null) {
            throw new Exception("dashbord id is null");
        }
        if(options.getTotalServerCount() == null) {
            throw new Exception("total server count id is null");
        }
        if(options.getServerId() == null) {
            throw new Exception("server id is null");
        }
        return true;
    }
}
