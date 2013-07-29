package com.skplanet.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.cask.util.StringUtil;

public class SimpleBatchService implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(SimpleBatchService.class);
    
    private static final int SLEEP_DEFAULT_MSEC = 1000;
    private boolean stop = false;
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
    
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Integer sleepMSec = (Integer)request.get("sleepMSec");
            if(sleepMSec == null) {
                sleepMSec = SLEEP_DEFAULT_MSEC;
            }
            
            while(true) {
                try {
                    logger.info("execute batch logic");
                }
                catch(Exception e) {
                    logger.error(StringUtil.exception2Str(e));
                }
                
                logger.info("batch service sleep {} msec", sleepMSec);
                Thread.sleep(sleepMSec);
                
                if(stop) {
                    break;
                }
            }
            
            resultMap.put("returnCode",  true);
            resultMap.put("returnDesc",  "success");
            
        } catch(Exception e) {
            resultMap.put("returnCode",  false);
            resultMap.put("returnDesc",  StringUtil.exception2Str(e));
        } finally {
            response.setParams(resultMap);
            logger.info("simple service response : {} ", response.getParams());
        }
    }
}
