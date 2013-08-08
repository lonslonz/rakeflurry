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
import com.skplanet.rakeflurry.collector.UserManager;
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.KeyMapDao;
import com.skplanet.rakeflurry.model.KeyMapModel;

public class OverwriteKeyMap implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(OverwriteKeyMap.class);
    
    /*
     * [
    {
        "accessCode": "JMCZPBCMJW6SXKPH8ZRG",
        "apiKeys": [
            "P2THW59FVVK9JGKR6BKQ",
            "Q9MQ6PRS5D6DC2SHJWFX"
        ]
    },
    {
        "accessCode": "JMCZPBCMJW6SXKPH8ZRG111",
        "apiKeys": [
            "P2THW59FVVK9JGKR6BKQ",
            "P2THW59FVVK9JGKR6BKQ"
        ]
    }
]
     * 
     */
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        logger.info("start overwrite keymap : {} ", request.getParams());
        if(!UserManager.validate(request.get("id"), request.get("password"))) {
            throw new Exception("id or password not valid.");
        }
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Map<String, Object> data = request.getParams();
            
            ObjectMapper mapper = new ObjectMapper();
            List keyMapList = mapper.convertValue(request.get("keymap"), List.class);
            
            Iterator it = keyMapList.iterator();
            List<KeyMapDao> keyMapSourceList = new ArrayList<KeyMapDao>();
            List<String> accessCodeList = new ArrayList<String>();
            while(it.hasNext()) {
                KeyMapModel kmm = mapper.convertValue(it.next(), KeyMapModel.class);
                
                List<String> apiKeyList = (List<String>)kmm.getApiKeys();           
                for(int i = 0; i < apiKeyList.size(); i++) {
                    KeyMapDao kms  = new KeyMapDao();
                    kms.setAccessCode(kmm.getAccessCode());
                    kms.setApiKey(apiKeyList.get(i));   
                    keyMapSourceList.add(kms);
                }
                accessCodeList.add(kmm.getAccessCode());
            }
            
            removeAllAccessCode(accessCodeList);
            insertAll(keyMapSourceList);
            
            resultMap.put("returnCode",  1);
            resultMap.put("returnDesc",  "success");
        } catch(Exception e) {
            resultMap.put("returnCode",  -1);
            resultMap.put("returnDesc",  "fail");
            resultMap.put("message", e.getMessage());
            logger.error(StringUtil.exception2Str(e));
        } finally {
            response.setParams(resultMap);
            logger.info("complete overwrite key map : {} ", response.getParams());
        }
    }
    
    public void removeAllAccessCode(List<String> accessCodeList) throws Exception {
        
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String hql  = "DELETE FROM KeyMapDao K where K.accessCode = :accessCode";
            Query query = session.createQuery(hql);
            
            for(int i = 0 ; i < accessCodeList.size(); i++) {
                String curr = accessCodeList.get(i);
                
                query.setParameter("accessCode", curr);
                int result = query.executeUpdate();
                logger.info("remove access code : {}, affected : {}", curr, result);
            }
            tx.commit();
        }catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    public void insertAll(List<KeyMapDao> keyMapSourceList) {
        Session session = HiberUtil.openSession();
        Transaction tx = session.beginTransaction();
        
        for(int i = 0; i < keyMapSourceList.size(); i++) {
            KeyMapDao obj = keyMapSourceList.get(i);
            session.save(obj);
            logger.info("save : access code : {}, api key : {}", obj.getAccessCode(), obj.getApiKey());
        }
        tx.commit();
        session.flush();
        session.close();
    }
}
