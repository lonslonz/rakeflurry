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
import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.KeyMapDao;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.service.SimpleRequestService;

public class ShowKeyMap implements SimpleService {
    private Logger logger = LoggerFactory.getLogger(ShowKeyMap.class);
    
    @Override
    public void handle(SimpleParams request, SimpleParams response, ServiceRuntimeInfo runtimeInfo) throws Exception {
        
        Map<String, Object> tempMap = new HashMap<String, Object>();
        
        List<KeyMapModel> listKeyMap = select();
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("results", listKeyMap );
        
        response.setParams(resultMap);
        logger.info("simple service response : {} ", response.getParams());
    }
    private  List<KeyMapModel> select() throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String hql  = "from KeyMapDao K order by K.accessCode, K.apiKey";
            Query query = session.createQuery(hql);
            
            List result = query.list();
            Iterator itRes = result.iterator();
            
            String prev = null;
            String curr;
            
            List<KeyMapModel> kmmList = new ArrayList<KeyMapModel>();
            KeyMapModel kmm = new KeyMapModel();
            while(itRes.hasNext()) {
                KeyMapDao obj = (KeyMapDao)itRes.next();
                curr = obj.getAccessCode();
                
                if(prev == null || !curr.equals(prev)) {
                    kmm = new KeyMapModel(); 
                    kmm.setAccessCode(curr);
                    kmmList.add(kmm);
                } 
                kmm.getApiKeys().add(obj.getApiKey());
                prev = curr;
            }
            tx.commit();
            return kmmList;
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            
            session.close();
        }
    }
}
