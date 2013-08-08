package com.skplanet.rakeflurry.collector;

public class FlurryException extends Exception {
    private static final long serialVersionUID = 1L;
    
    String httpStatusCode = null;
    FlurryErrorResponse flurryResponse = null;
    
    public FlurryException(String msg) {
        super(msg);
    }
    public FlurryException(String msg, String httpStatusCode) {
        super(msg);
        setHttpStatusCode(httpStatusCode);
    }
    public FlurryException(String msg, String httpStatusCode, FlurryErrorResponse response) {
        super(msg);
        setHttpStatusCode(httpStatusCode);
        setFlurryResponse(response);
    }
    
    public FlurryErrorResponse getFlurryResponse() {
        return flurryResponse;
    }
    public void setFlurryResponse(FlurryErrorResponse flurryResponse) {
        this.flurryResponse = flurryResponse;
    }
    public String getHttpStatusCode() {
        return httpStatusCode;
    }
    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
