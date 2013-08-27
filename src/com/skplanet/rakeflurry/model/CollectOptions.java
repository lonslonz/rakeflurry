package com.skplanet.rakeflurry.model;

import java.util.Map;

import com.skplanet.rakeflurry.collector.PartInfo;

public class CollectOptions {
    public static Integer DURATION_DEFAULT = 30;
    
    private Integer duration;
    private Integer dashboardId = null;
    private Boolean recover = null;
    private Integer totalServerCount = null;
    private Integer serverId = null;
    private Boolean multi = null;

    public static PartInfo getPartInfo(CollectOptions options) {
        return new PartInfo(options.getTotalServerCount(), options.getServerId());
    }
    
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Boolean getRecover() {
        return recover;
    }

    public void setRecover(Boolean recover) {
        this.recover = recover;
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

    public Boolean getMulti() {
        return multi;
    }

    public void setMulti(Boolean multi) {
        this.multi = multi;
    }
}
