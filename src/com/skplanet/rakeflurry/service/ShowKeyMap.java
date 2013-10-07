package com.skplanet.rakeflurry.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.rakeflurry.meta.KeyMapDef;
import com.skplanet.rakeflurry.model.KeyMapM;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.rakeflurry.util.HiberUtil;

public class ShowKeyMap implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(ShowKeyMap.class);
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        logger.info("start show key map : {} ", request.getParams());
        Map<String, Object> resultMap = new HashMap<String, Object>();;
        try {
        
            List<KeyMapModel> listKeyMap = KeyMapDef.getDataFromDb();
            
            resultMap.put("results", listKeyMap );
        } catch (Exception e) {
            
            resultMap.put("returnCode",  -1);
            resultMap.put("returnDesc",  "fail");
            resultMap.put("message", e.getMessage());
            throw e;
        } finally {
            response.setParams(resultMap);   
        }
        
        logger.info("complete show key map : {} ", response.getParams());
    }
   
}
