package com.ruoyi.bussiness.service.impl;

import com.ruoyi.bussiness.service.FileStorageService;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Set;
import java.util.stream.Stream;



/**
 * @author 12290
 */
@Service("fileStorageService")
public class FileStorageServiceImpl implements FileStorageService {
    private Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private final Path path = Paths.get("fileStorage");
    // Warning: 当没有这个登录的linux账户 没有用户的时候，会报错
    @Value("${server.logOwner}")
    private String logOwner;
    @Value("${server.logGroup}")
    private String logGroup;
    @Override
    public void init() {
        try {
            Files.createDirectory(path);
            chmodPermit(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }
    @Override
    public void save(MultipartFile multipartFile) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            Path target = this.path.resolve(multipartFile.getOriginalFilename());

            if(Files.exists(target)){
                log.info(multipartFile.getOriginalFilename()+"file exist");
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }else{
                log.info(multipartFile.getOriginalFilename()+"begin send");
                Files.copy(inputStream, target);
            }
            chmodPermit(target);
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error:"+e.getMessage());
        }finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public Resource load(String filename) {
        Path file = path.resolve(filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("Could not read the file.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error:"+e.getMessage());
        }
    }

    @Override
    public Stream<Path> load() {
        try {
            return Files.walk(this.path,1)
                    .filter(path -> !path.equals(this.path))
                    .map(this.path::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files.");
        }
    }

    @Override
    public void clear() {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    public void chmodPermit(Path path) throws IOException {
        //设置文件所属用户
        String ownerName = logOwner;
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal owner = lookupService.lookupPrincipalByName(ownerName);
        Files.setOwner(path, owner);

        // 设置文件所属组
        String groupName = logGroup;
        GroupPrincipal group = lookupService.lookupPrincipalByGroupName(groupName);
        Files.getFileAttributeView(path, PosixFileAttributeView.class).setGroup(group);

        // 设置文件权限
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrw-rw-");
        Files.setPosixFilePermissions(path, permissions);
    }
}
