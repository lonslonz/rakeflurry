package com.skplanet.rakeflurry.collector;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.rakeflurry.util.HttpUtil;

public class HttpWorker implements Callable<String> {
    private Logger logger = LoggerFactory.getLogger(HttpWorker.class);
    private String content;
    private String url;
    
    public HttpWorker(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String call() throws Exception {
        logger.info("http request : {}, {}", url, content);
        String result = HttpUtil.sendHttpPut(url,  content);
        return result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
