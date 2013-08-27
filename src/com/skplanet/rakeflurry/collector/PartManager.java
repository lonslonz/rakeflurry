package com.skplanet.rakeflurry.collector;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.cask.container.config.ConfigReader;
import com.skplanet.rakeflurry.model.CollectOptions;
import com.skplanet.rakeflurry.model.CollectParams;

public class PartManager {
    
    private Map<String, PartInfo> partOwnMap = new LinkedHashMap<String, PartInfo>();
    
    private static PartManager instance = new PartManager();
    public static PartManager getInstance() {
        return instance;
    }
    
    public synchronized boolean ownPart(CollectParams params) {
        CollectOptions options = params.getOptions();
        PartInfo partInfo = CollectOptions.getPartInfo(options);
        
        if(partOwnMap.get(partInfo.key()) != null) {
            return false;
        }
        
        partOwnMap.put(partInfo.key(), partInfo);
        return true;
    }
    public synchronized void releasePart(CollectParams params) throws Exception {
        CollectOptions options = params.getOptions();
        PartInfo partInfo = CollectOptions.getPartInfo(options);
        
        if(partOwnMap.remove(partInfo.key()) == null) {
            throw new Exception("there is no owned part.");
        }
    }
}
