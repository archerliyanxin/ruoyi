package com.ruoyi.bussiness.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.bussiness.service.ISpeechToGetTextService;
import com.ruoyi.bussiness.utils.HttpSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author 12290
 */
@Service
public class SpeechToGetTextServiceImpl implements ISpeechToGetTextService {


    @Override
    public String SendSpeechToAlg(String speechUrl) {
        return HttpSend.send(speechUrl,"http://192.168.1.20:13002/sound2text","file_loc","result");
    }

    @Override
    public String SendTextToGetText(String speechUrl) {
        return HttpSend.send(speechUrl,"http://192.168.1.20:13001/baichuan2_call","prompt","response");
    }

    @Override
    public String getAudioUrlFromSpeech(String speechUrl) {
        return HttpSend.send(speechUrl,"http://192.168.1.20:12306/process_video","audio","video_url");
    }

    @Override
    public String convertTextToSpeech(String text, String wavUrl) {
        return HttpSend.send(text,"http://192.168.1.20:13003/text2sound","answered_text","result",wavUrl );
    }
}
