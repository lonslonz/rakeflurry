package com.skplanet.rakeflurry.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.ServiceRuntimeInfo;
import com.skplanet.cask.container.model.SimpleParams;
import com.skplanet.cask.container.service.SimpleService;
import com.skplanet.cask.util.StringUtil;
import com.skplanet.rakeflurry.collector.Alerter;
import com.skplanet.rakeflurry.collector.UserManager;
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.KeyMapDao;
import com.skplanet.rakeflurry.meta.KeyMapDef;
import com.skplanet.rakeflurry.model.KeyMapModel;

public class OverwriteKeyMap implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(OverwriteKeyMap.class);
    
    

    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        Boolean deleteOnly = false;    
        logger.info("start overwrite keymap : {} ", request.getParams());
        if(!UserManager.validate(request.get("id"), request.get("password"))) {
            throw new Exception("id or password not valid.");
        }
        
        if(request.get("deleteOnly") != null) {
            deleteOnly = (Boolean)request.get("deleteOnly");
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List keyMapList = mapper.convertValue(request.get("keymap"), List.class);
            
            Iterator it = keyMapList.iterator();
            List<KeyMapDao> keyMapDaoList = new ArrayList<KeyMapDao>();
            List<String> mbrNoList = new ArrayList<String>();
            while(it.hasNext()) {
                KeyMapModel kmm = mapper.convertValue(it.next(), KeyMapModel.class);
                
                List<String> apiKeyList = (List<String>)kmm.getApiKeys();           
                for(int i = 0; i < apiKeyList.size(); i++) {
                    KeyMapDao kms  = new KeyMapDao();
                    kms.setMbrNo(kmm.getMbrNo());
                    kms.setAccessCode(kmm.getAccessCode());
                    kms.setApiKey(apiKeyList.get(i));   
                    keyMapDaoList.add(kms);
                }
                mbrNoList.add(kmm.getMbrNo());
            }
            
            KeyMapDef.removeAllAccessCode(mbrNoList);
            if(!deleteOnly) {
                KeyMapDef.insertAll(keyMapDaoList);
            }
            
            resultMap.put("returnCode",  1);
            resultMap.put("returnDesc",  "success");
        } catch(Exception e) {
            resultMap.put("returnCode",  -1);
            resultMap.put("returnDesc",  "fail");
            resultMap.put("message", e.getMessage());
            logger.error(StringUtil.exception2Str(e));
            
            Alerter alerter = new Alerter();
            alerter.errorOverwriteKeyMapService(e.getMessage());
            
            throw e;
        } finally {
            response.setParams(resultMap);
            logger.info("complete overwrite key map : {} ", response.getParams());
        }
    }
    
    
}
