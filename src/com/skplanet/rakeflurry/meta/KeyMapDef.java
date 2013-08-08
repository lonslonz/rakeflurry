package com.skplanet.rakeflurry.meta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.db.HiberUtil;
import com.skplanet.rakeflurry.db.KeyMapDao;
import com.skplanet.rakeflurry.model.KeyMapModel;
import com.skplanet.rakeflurry.service.OverwriteKeyMap;

public class KeyMapDef {
    private Logger logger = LoggerFactory.getLogger(KeyMapDef.class);
    private static KeyMapDef instance = new KeyMapDef();
    private List<KeyMapModel> keyMapList = new ArrayList<KeyMapModel>();
    
    public static KeyMapDef getInstance() {
        return instance;
    }
    // TODO : get from DB
    public void init() {
        
        keyMapList.clear();
        keyMapList = getDataFromDb();
        logger.info("read keymap from db");
    }
    public List<KeyMapModel> getDataFromDb() {
        Session session = HiberUtil.openSession();
        Transaction tx = session.beginTransaction();
        
        String hql  = "from KeyMapDao K where K.used = 1 order by K.accessCode, K.apiKey ";
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
        session.close();
        return kmmList;
    }
    public List<KeyMapModel> getKeyMapList() {
        return keyMapList;
    }
    public void setKeyMapList(List<KeyMapModel> keyMapList) {
        this.keyMapList = keyMapList;
    }
    

    
}
