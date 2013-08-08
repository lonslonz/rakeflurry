package com.skplanet.rakeflurry.dashboard;

public class WorkStatus {
    private RunningStatus runningStatus = RunningStatus.STANDBY;
    
    public RunningStatus getRunningStatus() {
        return runningStatus; 
    }
    public void setRunningStatus(RunningStatus runningStatus) {
        this.runningStatus = runningStatus;
    }
    
//    private long downloadCount = 0;
//    private long completeCount  = 0;
//    private long errorCount = 0;
//    private long standbyCount = 0;
//    
//    
//    public long getDownloadCount() {
//        return downloadCount;
//    }
//    public void setDownloadCount(long downloadCount) {
//        this.downloadCount = downloadCount;
//    }
//    public long getCompleteCount() {
//        return completeCount;
//    }
//    public void setCompleteCount(long completeCount) {
//        this.completeCount = completeCount;
//    }
//    public long getErrorCount() {
//        return errorCount;
//    }
//    public void setErrorCount(long errorCount) {
//        this.errorCount = errorCount;
//    }
//    public long getStandbyCount() {
//        return standbyCount;
//    }
//    public void setStandbyCount(long standbyCount) {
//        this.standbyCount = standbyCount;
//    }
    public void moveStandbyToDownload() {
        this.runningStatus = RunningStatus.DOWNLOADING;
    }
    public void moveStandbyToError() {
        this.runningStatus = RunningStatus.ERROR;
    }
    public void moveDownloadToError() {
        this.runningStatus = RunningStatus.ERROR;
    }
    public void moveDownloadToComplete() {
        this.runningStatus = RunningStatus.COMPLETE;
    }
}
