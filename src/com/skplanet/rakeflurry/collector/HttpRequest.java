package com.skplanet.rakeflurry.collector;

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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import com.skplanet.cask.container.model.OutParams;


// TODO : change class name
// own exception class
// 
public class HttpRequest {
    private static int CONN_TIMEOUT = 2;
    private static int SO_TIMEOUT = 5;
    
    public static String sendHttpGet(String url, int connTimeout, int soTimeout) throws Exception {
        
        HttpClient httpClient = null;
        HttpResponse response = null;
        String result = null;
        
        
        httpClient = new DefaultHttpClient();
        HttpGet method = new HttpGet(url);
        
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connTimeout * 1000); 
        HttpConnectionParams.setSoTimeout(httpParams, soTimeout * 1000);
        
        response = httpClient.execute(method);
        if(response.getStatusLine().getStatusCode() != 200) {
            
            String errorResult = makeReponseStr(response);
            
            ObjectMapper mapper = new ObjectMapper();
            FlurryErrorResponse fresp = new FlurryErrorResponse();
            try {
                fresp = mapper.readValue(errorResult, FlurryErrorResponse.class);
            } catch(Exception e) {
                fresp.setCode("999");
                fresp.setMessage("return status code not 200 and result json is not valid.");
                throw new FlurryException(
                        "errorResult : " + errorResult, 
                        String.valueOf(response.getStatusLine().getStatusCode()),
                        fresp);
            }
            throw new FlurryException(
                    "Flurry doesn't return 200 status with valid json.",
                    String.valueOf(response.getStatusLine().getStatusCode()),
                    fresp);
        }
        
        if(response != null) {
            result = makeReponseStr(response);
        }
        if(httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
        
        Map<String, String> resultMap = null;
        try {
            // test if valid json
            ObjectMapper mapper = new ObjectMapper();
            resultMap = mapper.readValue(result, Map.class);
        } catch(Exception e) {
            FlurryErrorResponse fresp = new FlurryErrorResponse();
            fresp.setCode("999");
            fresp.setMessage("flurry doesn't return valid json. maybe html.");
            throw new FlurryException(
                                "result : " + result, 
                                String.valueOf(response.getStatusLine().getStatusCode()),
                                fresp);
        }
        
        return result;
    }
    private static String makeReponseStr(HttpResponse response) throws Exception {
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
 
        StringBuilder buf = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            buf.append(output);
        }
        is.close();
        return buf.toString();
    }
    public static String sendHttpGet(String url) throws Exception {
        return sendHttpGet(url, CONN_TIMEOUT, SO_TIMEOUT);
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
    public static OutParams sendModelHttpPut(String url, String content) throws Exception {
        
    	String result = sendHttpPut(url, content);
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.readValue(result, OutParams.class);
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
