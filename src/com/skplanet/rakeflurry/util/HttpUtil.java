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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class HttpUtil {
    public static String sendHttpGet(String url) throws Exception {
         
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet method = new HttpGet(url);
        
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
    
    public static Map<String, Object> sendHttpGet2Map(String url) throws Exception {
        String result = sendHttpGet(url);
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = mapper.readValue(result, new TypeReference<Map<String, Object>>() { } );
        
        return resultMap;
    }

    public static Map<String, Object> sendSimpleHttpPut(String url, String content) throws Exception {
        
        String result = sendHttpPut(url, content);
        ObjectMapper mapper = new ObjectMapper();
        
        Map<String, Object> resultMap = mapper.readValue(result, new TypeReference<Map<String, Object>>() { });
        return resultMap;
    }
    public static String sendHttpPut(String url, String content) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(url);
 
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
