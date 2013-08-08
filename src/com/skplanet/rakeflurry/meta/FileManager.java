package com.skplanet.rakeflurry.meta;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.config.ConfigReader;
import com.skplanet.rakeflurry.dashboard.AccessCodeSummary;
import com.skplanet.rakeflurry.dashboard.ApiKeySummary;
import com.skplanet.rakeflurry.dashboard.DashBoard;

public class FileManager {
    private String localDataDir = null;
    private static DateFormat dirFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static FileManager instance = new FileManager();
    private static String fileFormat = "%s_%s_%s.log";
    private static DateFormat timeFormat = new SimpleDateFormat("HHmm");
    
    private Logger logger = LoggerFactory.getLogger(FileManager.class);
    
    public static FileManager getInstance() {
        return instance;
    }
    
    public void init() throws Exception {
        
        localDataDir = 
                ConfigReader.getInstance().getServerConfig().getPropValue("dataDir") + "/" + 
                dirFormat.format(new Date());
        
        File dir = new File(localDataDir);
        if(dir.exists()) {
            logger.info("dir already exists: {}", localDataDir);
        } else {
            if(dir.mkdirs()) {
                logger.info("mkdirs created : {}", localDataDir); 
            } else {
                throw new Exception("mkdirs failed. " + localDataDir);
            }
        }
        logger.info("FileManager initialized.");
    }
    public static String getDataFileName(AccessCodeSummary acs, ApiKeySummary aks) {
        return String.format(fileFormat, 
                             acs.getAccessCode(), 
                             aks.getApiKey(), 
                             DashBoard.getInstance().getStartTimeFileCode());
    }
    public String getLocalDataDir() {
        return localDataDir;
    }

    public void setLocalDataDir(String localDataDir) {
        this.localDataDir = localDataDir;
    }

}
