package com.ruoyi.bussiness.domain;

import java.io.Serializable;

/**
 *  文本请求
 *
 * @author lyx
 * @date 2023-11-20
 */
public class TextToSpeechRequest implements Serializable {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TextToSpeechRequest{" +
                "text='" + text + '\'' +
                '}';
    }
}
