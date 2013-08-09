package com.skplanet.rakeflurry.model;

import java.util.ArrayList;
import java.util.List;

public class KeyMapModel {
    private String mbrNo;
    private String accessCode;
    private List<String> apiKeys = new ArrayList<String>();
    
    
    public String getMbrNo() {
        return mbrNo;
    }
    public void setMbrNo(String mbrNo) {
        this.mbrNo = mbrNo;
    }
    public String getAccessCode() {
        return accessCode;
    }
    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
    public List<String> getApiKeys() {
        return apiKeys;
    }
    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    
    

}
