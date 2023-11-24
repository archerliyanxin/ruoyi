package com.ruoyi.bussiness.config;

import com.ruoyi.bussiness.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * @author 12290
 */
@Service
public class FileUploadConfig implements CommandLineRunner {
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public void run(String... args) throws Exception {
        fileStorageService.clear();
        fileStorageService.init();
    }
}
