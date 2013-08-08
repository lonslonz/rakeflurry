package com.skplanet.rakeflurry.dashboard;

public enum RunningStatus {
    DOWNLOADING("DOWNLOADING"),
    COMPLETE("COMPLETE"),
    STANDBY("STANDBY"),
    ERROR("ERROR");
    
    private String status;
    
    RunningStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return this.status;
    }
    public String toString() {
        return this.status;
    }
}
