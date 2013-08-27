package com.skplanet.rakeflurry.collector;

public class PartInfo {

    private Integer totalServerCount;
    private Integer serverId;
    public PartInfo(Integer totalServerCount, Integer serverId) {
        this.totalServerCount = totalServerCount;
        this.serverId = serverId;
    }
    public Integer getTotalServerCount() {
        return totalServerCount;
    }
    public void setTotalServerCount(Integer totalServerCount) {
        this.totalServerCount = totalServerCount;
    }
    public Integer getServerId() {
        return serverId;
    }
    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }
    public String key() {
        return serverId + "/" + totalServerCount;
    }   
    public String toString() {
        return key();
    }
}
