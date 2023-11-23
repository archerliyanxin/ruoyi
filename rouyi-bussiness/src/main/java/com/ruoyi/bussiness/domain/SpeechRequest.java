package com.ruoyi.bussiness.domain;
/**
 *  前端音频地址
 *
 * @author lyx
 * @date 2023-11-20
 */
public class SpeechRequest {
    private String speechUrl;

    public String getSpeechUrl() {
        return speechUrl;
    }

    public void setSpeechUrl(String speechUrl) {
        this.speechUrl = speechUrl;
    }

    @Override
    public String toString() {
        return "SpeechRequest{" +
                "speechUrl='" + speechUrl + '\'' +
                '}';
    }
}
