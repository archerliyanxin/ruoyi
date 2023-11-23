package com.ruoyi.bussiness.domain;

import java.io.Serializable;

/**
 *  语音回复
 *
 * @author lyx
 * @date 2023-11-20
 */
public class TextToSpeechResponse implements Serializable {
    private String audioUrl;

    public TextToSpeechResponse(String audioUrl){
        this.audioUrl = audioUrl;
    }
    public TextToSpeechResponse(){
    }

    @Override
    public String toString() {
        return "TextToSpeechResponse{" +
                "audioUrl='" + audioUrl + '\'' +
                '}';
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
