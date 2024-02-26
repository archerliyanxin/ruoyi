package com.ruoyi.bussiness.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ISpeechToGetTextService {

    public String SendSpeechToAlg(String speechUrl) throws Exception;
    public String SendTextToGetText(String speechUrl);

    public String getAudioUrlFromSpeech(String speechUrl);

    public  String convertTextToSpeech(String text,String wavUrl);

    public String postFileToAlg(MultipartFile file) throws IOException;

    public String getNerfVideoAlg() throws IOException;

    public void exchangeBackbond(String url, String resultUrl);
}
