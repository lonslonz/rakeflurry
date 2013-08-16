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
    public void init() throws Exception {
        
        keyMapList.clear();
        keyMapList = getDataFromDb();
        logger.info("read keymap from db");
    }
    public static List<KeyMapModel> getDataFromDb() throws Exception {
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String hql  = "from KeyMapDao K order by K.mbrNo, K.accessCode, K.apiKey";
            Query query = session.createQuery(hql);
            
            List result = query.list();
            Iterator itRes = result.iterator();
            
            String prev = null;
            String curr;
            
            List<KeyMapModel> kmmList = new ArrayList<KeyMapModel>();
            KeyMapModel kmm = new KeyMapModel();
            while(itRes.hasNext()) {
                KeyMapDao obj = (KeyMapDao)itRes.next();
                curr = obj.getMbrNo();
                
                if(prev == null || !curr.equals(prev)) {
                    kmm = new KeyMapModel();
                    kmm.setMbrNo(curr);
                    kmm.setAccessCode(obj.getAccessCode());
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
    public static void removeAllAccessCode(List<String> mbrNoList) throws Exception {
        Logger logger = LoggerFactory.getLogger(KeyMapDef.class);
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            //String hql  = "DELETE FROM KeyMapDao K where K.mbrNo = :mbrNo";
            String hql  = "DELETE FROM KeyMapDao K";
            Query query = session.createQuery(hql);
            int result = query.executeUpdate();
            logger.info("remove all keymap table, affected : {}", result);
            
//            for(int i = 0 ; i < mbrNoList.size(); i++) {
//                String curr = mbrNoList.get(i);
//                
//                //query.setParameter("mbrNo", curr);
//                int result = query.executeUpdate();
//                logger.info("remove mbr no : {}, affected : {}", curr, result);
//            }
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
    public static void insertAll(List<KeyMapDao> keyMapDaoList) throws Exception  {
        Logger logger = LoggerFactory.getLogger(KeyMapDef.class);
        Session session = HiberUtil.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
        
            for(int i = 0; i < keyMapDaoList.size(); i++) {
                KeyMapDao obj = keyMapDaoList.get(i);
                session.save(obj);
                logger.info("save : mbr no : {}, access code : {}, api key : {}", 
                            new Object[]{obj.getMbrNo(), obj.getAccessCode(), obj.getApiKey()});
            }
            tx.commit();
        } catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    public List<KeyMapModel> getKeyMapList() {
        return keyMapList;
    }
    public void setKeyMapList(List<KeyMapModel> keyMapList) {
        this.keyMapList = keyMapList;
    }
    

    
}
