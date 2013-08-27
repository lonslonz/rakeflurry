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
import com.skplanet.rakeflurry.collector.UserManager;
import com.skplanet.rakeflurry.dashboard.DashBoard;
import com.skplanet.rakeflurry.file.FileManager;
import com.skplanet.rakeflurry.meta.AppMetricsApi;
import com.skplanet.rakeflurry.model.CollectParams;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.rakeflurry.util.Right;


public class RecoverApi implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(CollectApi.class);

    
    // options : { duration = 30, dashboard_id = 1} 
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        logger.info("start recover api  service. {}", request.getParams());
        
        ObjectMapper mapper = new ObjectMapper();
        CollectParams params = mapper.convertValue(request.getParams(), CollectParams.class);
        
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
            
            DashBoard prevDashboard = null;
            if(params.getOptions().getDashboardId() != null) {
                prevDashboard = DashBoard.selectById(params.getOptions().getDashboardId());
            } else {
                if(params.getOptions().getRecover() != null) {
                    prevDashboard = DashBoard.selectLastOne(params.getOptions().getRecover());    
                } else {
                    prevDashboard = DashBoard.selectLastOne(true);
                }
                
            }
            if(prevDashboard == null) {
                throw new Exception("there is no dashboard. id : " + params.getOptions().getDashboardId());
            }
            List<KeyMapModel> kmmList = prevDashboard.filterFailedKeyMap();
            
            DashBoard recoverDashboard = null;
            if(kmmList.size() == 0) {
                logger.info("There is no error case. dashboard id : {}", prevDashboard.getDashboardId());
                resultMap.put("results",  "no error");
                
            } else {
                recoverDashboard = new DashBoard();
                recoverDashboard.initWithKeyMapList(kmmList);
                
                recoverDashboard.setRecoverWhoId(prevDashboard.getDashboardId());
                recoverDashboard.setCallEndDay(prevDashboard.getCallEndDay());
                recoverDashboard.setCallStartDay(prevDashboard.getCallStartDay());
                recoverDashboard.saveAllIntoDb();
                
                prevDashboard.setRecoverMeId(recoverDashboard.getDashboardId());
                prevDashboard.update();
                
                Collector collector = new Collector(params, recoverDashboard);
                collector.recover();
                
                resultMap.put("results",  recoverDashboard);
            }
            response.setParams(resultMap);
            
            Alerter alerter = new Alerter();
            if(kmmList.size() != 0) {
                alerter.finishRecoverApiService(recoverDashboard);
            } else {
                alerter.finishNoRecoverApiService(prevDashboard);
            }
            
            logger.info("complete recover api  service : {} ", response.getParams());
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
