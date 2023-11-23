package com.ruoyi.bussiness.service;

public interface ISpeechToGetTextService {

    public String SendSpeechToAlg(String speechUrl);
    public String SendTextToGetText(String speechUrl);

    public String getAudioUrlFromSpeech(String speechUrl);

    public  String convertTextToSpeech(String text,String wavUrl);
}
