package com.dail.starter.file.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description
 *
 * @author Dail 2023/01/09 13:53
 */
@Data
@Accessors(chain = true)
public class FileInfo {
    private String attachmentUuid;
    private String directory;
    private String fileUrl;
    private String fileType;
    private String fileName;
    private Long fileSize;
    private String bucketName;
    private String fileKey;
    private String md5;
    private String sourceType;
    private String serverCode;
}
