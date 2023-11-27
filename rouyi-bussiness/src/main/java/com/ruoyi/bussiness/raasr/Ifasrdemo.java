package com.ruoyi.bussiness.raasr;

import cn.hutool.json.JSONUtil;

import com.google.gson.Gson;
import com.ruoyi.bussiness.raasr.sign.LfasrSignature;
import com.ruoyi.bussiness.raasr.utils.HttpUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.SignatureException;
import java.util.HashMap;

/**
 * @author 12290
 */
@Service
public class Ifasrdemo {
    private final String HOST = "https://raasr.xfyun.cn";

    private Logger log = LoggerFactory.getLogger(Ifasrdemo.class);

    @Value("${voice.appid}")
    private String appid ;
    @Value("${voice.keySecret}")
    private String keySecret ;
    private final Gson gson = new Gson();

    public void SendTextToGetText(String location) throws Exception {
        String result = upload(location);
        String jsonStr = StringEscapeUtils.unescapeJavaScript(result);
        String orderId = String.valueOf(JSONUtil.getByPath(JSONUtil.parse(jsonStr), "content.orderId"));
        getResult(orderId);
    }

    private String upload(String location) throws SignatureException, FileNotFoundException {
        HashMap<String, Object> map = new HashMap<>(16);
        File audio = new File(location);
        String fileName = audio.getName();
        long fileSize = audio.length();
        map.put("appId", appid);
        map.put("fileSize", fileSize);
        map.put("fileName", fileName);
        map.put("duration", "200");
        LfasrSignature lfasrSignature = new LfasrSignature(appid, keySecret);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());

        String paramString = HttpUtil.parseMapToPathParam(map);
        log.info("upload paramString:" + paramString);

        String url = HOST + "/v2/api/upload" + "?" + paramString;
        log.info("upload_url:" + url);
        String response = HttpUtil.iflyrecUpload(url, new FileInputStream(audio));

        log.info("upload response:" + response);
        return response;
    }

    private String getResult(String orderId) throws SignatureException, InterruptedException, IOException {
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("orderId", orderId);
        LfasrSignature lfasrSignature = new LfasrSignature(appid, keySecret);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());
        map.put("appId", appid);
        map.put("resultType", "transfer,predict");
        String paramString = HttpUtil.parseMapToPathParam(map);
        String url = HOST + "/v2/api/getResult" + "?" + paramString;
        log.info("\nget_result_url:" + url);
        while (true) {
            String response = HttpUtil.iflyrecGet(url);
            JsonParse jsonParse = gson.fromJson(response, JsonParse.class);
            if (jsonParse.content.orderInfo.status == 4 || jsonParse.content.orderInfo.status == -1) {
                log.info("订单完成:" + response);
                write(response);
                return response;
            } else {
                log.info("进行中...，状态为:" + jsonParse.content.orderInfo.status);
                //建议使用回调的方式查询结果，查询接口有请求频率限制
                Thread.sleep(7000);
            }
        }
    }

    public void write(String resp) throws IOException {
        //将写入转化为流的形式
        BufferedWriter bw = new BufferedWriter(new FileWriter("src\\main\\resources\\output\\test.txt"));
        String ss = resp;
        bw.write(ss);
        //关闭流
        bw.close();
        log.info("写入txt成功");
    }

    class JsonParse {
        Content content;
    }

    class Content {
        OrderInfo orderInfo;
    }

    class OrderInfo {
        Integer status;
    }
}
