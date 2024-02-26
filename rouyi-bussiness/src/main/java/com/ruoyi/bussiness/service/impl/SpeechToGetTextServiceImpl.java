package com.ruoyi.bussiness.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.bussiness.exception.FileFormatException;
import com.ruoyi.bussiness.service.IFfmpegConver;
import com.ruoyi.bussiness.service.ISpeechToGetTextService;
import com.ruoyi.bussiness.utils.HttpSend;

import com.ruoyi.bussiness.utils.NlsClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 12290
 */
@Service
public class SpeechToGetTextServiceImpl implements ISpeechToGetTextService {

    @Autowired
    private IFfmpegConver ffmpegConver;
    private final String GEN_URL = "https://u22746-a810-129cef75.westc.gpuhub.com:8443/gen";

    @Override
    public String SendSpeechToAlg(String speechUrl) throws Exception {
        String outputFile = "";
        if(speechUrl.endsWith(".mp3")){
            outputFile = speechUrl.replace(".mp3", ".wav");
        }else if(speechUrl.endsWith(".m4a")){
            outputFile = speechUrl.replace(".m4a", ".wav");
        }else{
            throw new FileFormatException("文件格式不正确");
        }

        ffmpegConver.convertAudio(speechUrl, outputFile);
        NlsClientRequest nls = new NlsClientRequest();
        return nls.process(outputFile);
    }

    @Override
    public String SendTextToGetText(String speechUrl) {
        return HttpSend.send(speechUrl,"http://localhost:13001/baichuan2_call","prompt","response");
    }

    @Override
    public String getAudioUrlFromSpeech(String speechUrl) {
        return HttpSend.send(speechUrl,"http://localhost:12306/process_video","audio","video_url");
    }

    @Override
    public String convertTextToSpeech(String text, String wavUrl) {
        return HttpSend.send(text,"http://localhost:13003/text2sound","answered_text","result",wavUrl );
    }

    @Override
    public String postFileToAlg(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送文件上传请求
        ResponseEntity<String> response = new RestTemplate().exchange(
                "https://u22746-a810-129cef75.westc.gpuhub.com:8443/upload",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // 处理返回结果
        return response.getBody();
    }

    @Override
    public String getNerfVideoAlg() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = new RestTemplate().exchange(GEN_URL, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 解析返回结果中的URL
            String url = extractUrlFromResponse(response.getBody());

            long startTime = System.currentTimeMillis();

            // 轮询URL直到返回视频结果
            if(isVideoResult(url)){
                return "文件链接未给出";
            }
            while (true) {
                if(System.currentTimeMillis() - startTime >= 60000){
                    return "文件未生成成功";
                }
                try {
                    ResponseEntity<byte[]> videoResponse = new RestTemplate().exchange(url, HttpMethod.GET, null, byte[].class);
                    if (videoResponse.getStatusCode() == HttpStatus.OK) {
                        // 如果响应状态码为200，说明视频已经生成
                        return url;
                    }
                } catch (Exception  e) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

        } else {
            // 处理请求失败的情况
            return response.getBody();
        }
    }

    @Override
    public void exchangeBackbond(String url, String resultUrl) {
        HttpSend.send(url, "http://localhost:13003/text2sound", "video_url", "result", resultUrl);
    }

    private String extractUrlFromResponse(String responseBody) {
        // 使用 Jackson ObjectMapper 解析 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将 JSON 字符串转换为 JsonNode 对象
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            // 获取 "url" 字段的值
            return jsonNode.get("url").asText();
        } catch (Exception e) {
            // 解析出错时返回空字符串
            return "";
        }
    }

    private boolean isVideoResult(String url) {
        // 判断URL是否为视频结果
        // 这里假设视频结果URL以.mp4结尾，实际中根据实际返回数据进行判断
        return url.endsWith(".mp4");
    }

    // 自定义MultipartFile资源类
    private static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(MultipartFile multipartFile) throws IOException {
            super(multipartFile.getInputStream());
            this.filename = multipartFile.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
