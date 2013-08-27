package com.skplanet.rakeflurry.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.config.ConfigReader;

public class UserManager {
    
    public static Boolean validate(Object id, Object password) {
        Logger logger = LoggerFactory.getLogger(UserManager.class);
        if(id == null || password == null) {
            logger.error("id, password invalid. id : {}, password : {}", id, password);
            return false;
        }
        
        String strId = (String)id;
        String strPassword = (String)password;
        
        if(strId.equals(ConfigReader.getInstance().getServerConfig().getPropValue("id")) &&
                strPassword.equals(ConfigReader.getInstance().getServerConfig().getPropValue("password"))) {
            return true;
        }
        
        logger.error("id, password invalid. id : {}, password : {}", id, password);
        return false;
    }
    
}
