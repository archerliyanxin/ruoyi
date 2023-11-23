package com.ruoyi.bussiness.controller;

import com.ruoyi.bussiness.domain.TextToSpeechRequest;
import com.ruoyi.bussiness.domain.TextToSpeechResponse;
import com.ruoyi.bussiness.service.FileStorageService;
import com.ruoyi.bussiness.service.ISpeechToGetTextService;
import com.ruoyi.bussiness.utils.HttpSend;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *  音频请求
 *
 * @author lyx
 * @date 2023-11-20
 */
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Api("业务")
@RestController
@RequestMapping("/business")
public class TextToSpeechController extends BaseController {
    @Autowired
    private ISpeechToGetTextService iSpeechToGetTextService;

    @Autowired
    private FileStorageService fileStorageService;
    @Value("${server.ip}")
    private String serverIp; // 从配置文件中读取服务器IP

    private Logger log = LoggerFactory.getLogger(TextToSpeechController.class);

    private final Path path = Paths.get("fileStorage");

    @ApiOperation("语音转文本")
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<TextToSpeechResponse> convertTextToSpeechController(@RequestParam("file") MultipartFile file) throws Exception{
        try {
            fileStorageService.save(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new TextToSpeechResponse("Could not upload the file:" + file.getOriginalFilename()));
        }

        log.info("after save");
        String speechUrl = path.resolve(file.getOriginalFilename()).toAbsolutePath().toString();
//        String speechUrl = MvcUriComponentsBuilder
//                .fromMethodName(TextToSpeechController.class,
//                        "getFile",
//                        file.getOriginalFilename()
//                ).build().toString();

        log.info("speechUrl:" + speechUrl);
        String speechTotext = iSpeechToGetTextService.SendSpeechToAlg(speechUrl);
        log.info("speechTotext:" + speechTotext);
        String gcText = iSpeechToGetTextService.SendTextToGetText(speechTotext);
        log.info("gcText:" + gcText);
        String audioSpeechUrl = iSpeechToGetTextService.convertTextToSpeech(gcText,path.toAbsolutePath().toString());
        log.info("audioSpeechUrl:" + audioSpeechUrl);
        String audioLocation = iSpeechToGetTextService.getAudioUrlFromSpeech(audioSpeechUrl);
        log.info("audioLocation:" + audioLocation);
        String audioUrl = "http://" + serverIp +"/videos/"+ audioLocation.substring(audioLocation.lastIndexOf("/") + 1);
        TextToSpeechResponse response = new TextToSpeechResponse();
        response.setAudioUrl(audioUrl);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("文本转视频")
    @PostMapping("/text")
    @ResponseBody
    public ResponseEntity<TextToSpeechResponse> convertTextToVideo(@RequestBody TextToSpeechRequest request) throws Exception{
        log.info("after save");

        String audioSpeechUrl = iSpeechToGetTextService.convertTextToSpeech(request.getText(),path.toAbsolutePath().toString());
        log.info("audioSpeechUrl:" + audioSpeechUrl);
        String audioLocation = iSpeechToGetTextService.getAudioUrlFromSpeech(audioSpeechUrl);
        log.info("audioUrl:" + audioLocation);
        TextToSpeechResponse response = new TextToSpeechResponse();
        String audioUrl = "http://" + serverIp +"/videos/"+ audioLocation.substring(audioLocation.lastIndexOf("/") + 1);
        response.setAudioUrl(audioUrl);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("文件列表")
    @GetMapping("/files/{filename:.+}")
    @Anonymous
    public ResponseEntity<Resource> getFile(@PathVariable("filename")String filename) throws IOException {
        log.info(filename);
        Resource file = fileStorageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @ApiOperation("文件列表")
    @PostMapping("/test")
    public String test(){
        log.info(path.toAbsolutePath().toString());
        return HttpSend.send("hellop","http://localhost:13001/baichuan2_call/","prompt","response");
    }
}
