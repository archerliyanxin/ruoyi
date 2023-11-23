package com.ruoyi.bussiness.service;

import com.ruoyi.bussiness.controller.TextToSpeechController;
import com.ruoyi.bussiness.domain.UploadFile;
import com.ruoyi.bussiness.service.impl.FileStorageServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.poi.util.SystemOutLogger;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileStorageServiceTest {
    @Spy
    @InjectMocks
    private FileStorageServiceImpl fileStorageService;
    private final Path path = Paths.get("fileStorage");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

//    @Test
//    public void load(){
//        String filename = "a.txt";
//        FileStorageServiceImpl fileStorageService = new FileStorageServiceImpl();
//        path.getFileName().toString();
//        String url = MvcUriComponentsBuilder
//                .fromMethodName(TextToSpeechController.class,
//                        "getFile",
//                        path.getFileName().toString() + "/a.txt"
//                ).build().toString();
//
//        List<UploadFile> files = fileStorageService.load()
//                .map(path -> {
//                    String fileName = path.getFileName().toString();
//                    String url1 = MvcUriComponentsBuilder
//                            .fromMethodName(TextToSpeechController.class,
//                                    "getFile",
//                                    path.getFileName().toString()
//                            ).build().toString();
//
//                    return new UploadFile(fileName,url1);
//                }).collect(Collectors.toList());
//
//    }
}
