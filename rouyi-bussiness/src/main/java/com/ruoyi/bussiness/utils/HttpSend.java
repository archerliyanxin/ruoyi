package com.ruoyi.bussiness.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.InetAddress;
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
        String url = JSON.toJSONString(map);
        HttpEntity<String> requestEntity = new HttpEntity<String>(url,headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                targetUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String textResponse = responseEntity.getBody();
        HashMap<String, String> dataMap = JSON.parseObject(textResponse, new TypeReference<HashMap<String,String>>() {});
        String audioUrl = dataMap.get(responseType);
        return audioUrl;
    }
    public static String send(String speechUrl, String targetUrl, String requestType,String responseType,String wavUrl){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String,String> map = new HashMap<>();
        map.put(requestType, speechUrl);
        map.put("wav_file_path",wavUrl);
        String url = JSON.toJSONString(map);
        HttpEntity<String> requestEntity = new HttpEntity<String>(url,headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                targetUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String textResponse = responseEntity.getBody();
        HashMap<String, String> dataMap = JSON.parseObject(textResponse, new TypeReference<HashMap<String,String>>() {});
        String audioUrl = dataMap.get(responseType);
        return audioUrl;
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
