package com.skplanet.rakeflurry.collector;

public class FlurryErrorResponse {
    String code;
    String message;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String toString() {
        return "code : " + code + ", " + "message : " + message;
    }
    
}
