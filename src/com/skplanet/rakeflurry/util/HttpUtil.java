package com.skplanet.rakeflurry.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import com.skplanet.cask.container.model.OutParams;

public class HttpUtil {
    private static int CONN_TIME_OUT = 5000;
    public static String sendHttpGet(String url, Integer timeout) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        
        HttpGet method = new HttpGet(url);
        if(timeout != null) {
            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, timeout); 
            //HttpConnectionParams.setSoTimeout(httpParams, timeout);
        }
        
        HttpResponse response = httpClient.execute(method);
        if(response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("get status code error : " + response.getStatusLine().getStatusCode());
        }
        
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        
        BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
 
        StringBuilder buf = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            buf.append(output);
        }

        httpClient.getConnectionManager().shutdown();
        return buf.toString();
    }
    public static String sendHttpGet(String url) throws Exception {
        return sendHttpGet(url, CONN_TIME_OUT);
    }
    
    public static Map<String, Object> sendHttpGet2Map(String url) throws Exception {
        String result = sendHttpGet(url);
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = mapper.readValue(result, new TypeReference<Map<String, Object>>() { } );
        
        return resultMap;
    }
    public static OutParams sendModelHttpPut(String url, String content) throws Exception {
        
    	String result = sendHttpPut(url, content);
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.readValue(result, OutParams.class);
    }
    
    public static String sendHttpPut(String url, String content) throws Exception {
        return sendHttpPut(url, content, CONN_TIME_OUT, null);
    }
    
    public static String sendHttpPut(String url, String content, Integer connTimeout, Integer sockTimeout) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(url);
        
        HttpParams httpParams = httpClient.getParams();
        if(connTimeout != null) {
            HttpConnectionParams.setConnectionTimeout(httpParams, connTimeout);
        }
        if(sockTimeout != null) {
            HttpConnectionParams.setSoTimeout(httpParams, sockTimeout);
        }
 
        StringEntity input = new StringEntity(content);
        input.setContentType ("application/json");
        postRequest.setEntity(input);
        
        HttpResponse response = httpClient.execute(postRequest);
        if(response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("get status code error : " + response.getStatusLine().getStatusCode());
        }

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(is));

        StringBuilder buf = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            buf.append(output);
        }
        httpClient.getConnectionManager().shutdown();
        return buf.toString();
    }
}
