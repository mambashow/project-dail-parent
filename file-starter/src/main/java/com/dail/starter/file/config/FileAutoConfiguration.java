package com.dail.starter.file.config;

import com.dail.starter.file.minio.service.MinioFileServiceImpl;
import com.dail.starter.file.minio.service.MyMinioClient;
import com.dail.starter.file.service.AbstractFileService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author Dail 2023/01/09 14:20
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileAutoConfiguration {

    @Bean
    public MyMinioClient myMinioClient(FileProperties fileProperties){
        return new MyMinioClient(fileProperties);
    }

    @Bean
    public AbstractFileService minioFileService(FileProperties fileProperties){
        AbstractFileService abstractFileService = new MinioFileServiceImpl();
        return abstractFileService.init(fileProperties);
    }
}
