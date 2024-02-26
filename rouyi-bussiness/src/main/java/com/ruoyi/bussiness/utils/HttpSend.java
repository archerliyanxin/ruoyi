package com.ruoyi.bussiness.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.bussiness.domain.urlMsg;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
/**
 * @author 12290
 */
public class HttpSend {
    public static String send(String speechUrl, String targetUrl, String requestType,String responseType){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String,String> map = new HashMap<>();
        map.put(requestType, speechUrl);
        return getString(targetUrl, responseType, restTemplate, headers, map);
    }
    public static String send(String speechUrl, String targetUrl, String requestType,String responseType,String wavUrl){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String,String> map = new HashMap<>();
        map.put(requestType, speechUrl);
        map.put("wav_file_path",wavUrl);
        return getString(targetUrl, responseType, restTemplate, headers, map);
    }

    public static urlMsg sendGetRequest(String baseUrl, String pathParam) {
        // 构建请求的URL，包含参数
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("path", pathParam)
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<urlMsg> response = restTemplate.getForEntity(uri, urlMsg.class);

        return response.getBody();
    }

    private static String getString(String targetUrl, String responseType, RestTemplate restTemplate, HttpHeaders headers, HashMap<String, String> map) {
        String url = JSON.toJSONString(map);
        HttpEntity<String> requestEntity = new HttpEntity<>(url,headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                targetUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String textResponse = responseEntity.getBody();
        HashMap<String, String> dataMap = JSON.parseObject(textResponse, new TypeReference<HashMap<String,String>>() {});
        if (dataMap != null) {
            return dataMap.get(responseType);
        }else{
            throw new JSONException("文本转化失败");
        }
    }

    public static String generateFileUrl(String fileUri){
        String serverIp ;
        try {
            // 获取服务器的IP地址
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // 处理获取不到IP的异常情况
            serverIp = "unknown";
        }
        String serverHost = "your_server_host";  // 你的服务器主机地址

        // 假设文件保存在 /uploads 目录下，可以根据实际情况调整
        String filePath = "/uploads/your_uploaded_file.txt";

        // 构建包含IP和主机绝对地址的URL
        return "http://" + serverIp + "/" + serverHost + filePath;
    }
}
