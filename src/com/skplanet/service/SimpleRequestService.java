package com.skplanet.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;

public class SimpleRequestService implements SimpleService {
    
    private Logger logger = LoggerFactory.getLogger(SimpleRequestService.class);
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        Map<String, Object> data = request.getParams();
        Iterator<String> it = data.keySet().iterator();
        
        Map<String, Object> tempMap = new HashMap<String, Object>();
        while(it.hasNext()) {
            String me =  it.next();
            Object obj = data.get(me);
            logger.debug("key : {}, value : {}", me, obj);
            tempMap.put(me, obj);
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("returnCode",  1);
        resultMap.put("returnDesc",  "success");
        resultMap.put("requests",  tempMap);
        
        response.setParams(resultMap);
        logger.info("simple service response : {} ", response.getParams());
    }
}
