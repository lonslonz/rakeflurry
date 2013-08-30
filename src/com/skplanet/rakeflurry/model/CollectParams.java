package com.skplanet.rakeflurry.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;

import com.skplanet.rakeflurry.collector.PartInfo;


//duration : 30
//accessCodeStartIndex : 0 ~ n-1
//accessCodeWorkRange : { start : 0, end : n-1 } -- when start : -1 : all
//apiCodeStartIndex
public class CollectParams {
    
    
    private String endDay;
    private String startDay;
    
    private String id;
    private String password;
    
    private CollectOptions options;
    
    public void init() {
        
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        cal.add(Calendar.DATE, -1);
        endDay = dateFormat.format(cal.getTime());
        
        if(this.options.getDuration() == null) {
            this.options.setDuration(CollectOptions.DURATION_DEFAULT);
        }
        
        cal.add(Calendar.DATE, Math.abs(this.options.getDuration()) * -1);
        startDay = dateFormat.format(cal.getTime());
    }
    @JsonIgnore
    public boolean isMulti() {
        if(this.getOptions().getMulti() != null && this.getOptions().getMulti()) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CollectOptions getOptions() {
        return options;
    }

    public void setOptions(CollectOptions options) {
        this.options = options;
    }    
    
}
