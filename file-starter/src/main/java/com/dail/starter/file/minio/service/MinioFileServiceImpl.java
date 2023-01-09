package com.dail.starter.file.minio.service;

import com.dail.starter.file.config.FileProperties;
import com.dail.starter.file.entity.FileInfo;
import com.dail.starter.file.exception.CommonException;
import com.dail.starter.file.service.AbstractFileService;
import com.dail.starter.file.utils.AesUtils;
import com.dail.starter.file.utils.FileConstants;
import com.dail.starter.file.utils.FilenameUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.messages.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * description
 *
 * @author Dail 2023/01/09 11:25
 */
@EnableConfigurationProperties(FileProperties.class)
public class MinioFileServiceImpl extends AbstractFileService {
    public static final Logger LOGGER = LoggerFactory.getLogger(MinioFileServiceImpl.class);
    private String policyConfig;
    private MyMinioClient client;

    private MyMinioClient getClient() {
        if (this.client == null) {
            try {
                this.client = new MyMinioClient(this.config);
            } catch (Exception var2) {
                throw new CommonException(var2);
            }
        }

        return this.client;
    }

    @Override
    public AbstractFileService init(FileProperties config) {
        this.config = config;
        String accessControl = config.getMinio().getAccessControl();

        switch (accessControl) {
            case FileConstants.MinioAccessControl.READ_WRITE:
                this.policyConfig = "";
                break;
            case FileConstants.MinioAccessControl.READ_ONLY:
                this.policyConfig = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::${bucketName}\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::${bucketName}/*\"]}]}";
                break;
            case FileConstants.MinioAccessControl.WRITE_ONLY:
                this.policyConfig = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::${bucketName}\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::${bucketName}/*\"]}]}";
                break;
            case FileConstants.MinioAccessControl.NONE:
            default:
                this.policyConfig = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::${bucketName}\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::${bucketName}/*\"]}]}";
        }
        return this;
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        String realBucketName = this.getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();

        try {
            this.checkAndCreateBucket(realBucketName);
            this.getClient().uploadObject(((UploadObjectArgs.builder().bucket(realBucketName)).object(fileKey)).filename(filePath).contentType(file.getFileType()).build());
            return this.getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (CommonException var8) {
            throw var8;
        } catch (Exception var9) {
            try {
                this.getClient().removeObject(((RemoveObjectArgs.builder().bucket(realBucketName)).object(fileKey)).build());
            } catch (Exception var7) {
                LOGGER.error(var7.getMessage());
            }

            throw new CommonException("error.file_upload", var9);
        }
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = this.getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();

        try {
            this.checkAndCreateBucket(realBucketName);
            this.getClient().putObject(((PutObjectArgs.builder().bucket(realBucketName)).object(fileKey)).stream(inputStream, inputStream.available(), -1L).contentType(file.getFileType()).build());
            return this.getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (CommonException var8) {
            throw var8;
        } catch (Exception var9) {
            try {
                this.getClient().removeObject(((RemoveObjectArgs.builder().bucket(realBucketName)).object(fileKey)).build());
            } catch (Exception var7) {
                LOGGER.error(var7.getMessage());
            }

            throw new CommonException("error.file.upload", var9);
        }
    }

    @Override
    public String initUpload(FileInfo file) {
        String realBucketName = this.getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        try {
            this.checkAndCreateBucket(realBucketName);
            Multimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", file.getFileType());
            Multimap<String, String> extraQueryParams = HashMultimap.create();
            return this.getClient().initUpload(realBucketName, null, fileKey, headers, extraQueryParams);
        } catch (Exception var6) {
            throw new CommonException("error.file.init_upload", var6);
        }
    }

    @Override
    public String uploadSlice(FileInfo file, String uploadId, Integer partNumber, InputStream inputStream) {
        String realBucketName = this.getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();

        try {
            return this.getClient().uploadSlice(realBucketName, null, fileKey, new BufferedInputStream(inputStream), file.getFileSize().intValue(), uploadId, partNumber, null, null);
        } catch (Exception var8) {
            throw new CommonException("error.file.upload_slice", var8);
        }
    }

    @Override
    public String combine(FileInfo file, String uploadId, Map<String, String> map) {
        String realBucketName = this.getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        if (map.size() > 10000) {
            throw new CommonException("Too many parts!");
        } else {
            Part[] parts = new Part[map.size()];

            for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                int partNumber = Integer.parseInt(stringStringEntry.getKey());
                String etag = stringStringEntry.getValue();
                parts[partNumber - 1] = new Part(partNumber, etag);
            }

            try {
                this.getClient().combineSlice(realBucketName, null, fileKey, uploadId, parts, null, null);
            } catch (Exception var11) {
                throw new CommonException("error.file.upload_combine", var11);
            }

            return this.getObjectPrefixUrl(realBucketName) + fileKey;
        }
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String realBucketName = this.getRealBucketName(file.getBucketName());

        try {
            this.checkAndCreateBucket(realBucketName);
            CopySource source = ((CopySource.builder().bucket(this.getRealBucketName(oldBucketName))).object(oldFileKey)).build();
            this.getClient().copyObject(((CopyObjectArgs.builder().bucket(realBucketName)).object(file.getFileKey())).source(source).build());
            return this.getObjectPrefixUrl(realBucketName) + file.getFileKey();
        } catch (Exception var6) {
            throw new CommonException("error.file.upload_copy", var6);
        }
    }

    @Override
    public void deleteFile(String bucketName, String url, String fileKey) {
        String realBucketName = this.getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = this.getFileKey(realBucketName, url);
        }

        try {
            Assert.hasLength(fileKey, "error.not_null_delete_file");
            this.getClient().removeObject(((RemoveObjectArgs.builder().bucket(realBucketName)).object(fileKey)).build());
        } catch (Exception var6) {
            throw new CommonException("error.delete.file", var6);
        }
    }

    @Override
    public String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires) {
        return null;
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        String realBucketName = this.getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = this.getFileKey(realBucketName, url);
        }

        try {
            Assert.hasLength(fileKey, "error.not_null");
            InputStream is = this.getClient().getObject(((GetObjectArgs.builder().bucket(realBucketName)).object(fileKey)).build());
            this.buildResponse(response, is, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception var8) {
            throw new CommonException("error.download.file", var8);

        }
    }

    @Override
    public void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password) {
        String realBucketName = this.getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = this.getFileKey(realBucketName, url);
        }

        try {
            Assert.hasLength(fileKey, "error.not_null");
            InputStream is = this.getClient().getObject(((GetObjectArgs.builder().bucket(realBucketName)).object(fileKey)).build());
            byte[] data = IOUtils.toByteArray(is);
            if (StringUtils.isBlank(password)) {
                data = AesUtils.decrypt(data);
            } else {
                data = AesUtils.decrypt(data, password);
            }

            this.buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception var10) {
            throw new CommonException("error.download.file", var10);
        }
    }

    @Override
    public String getObjectPrefixUrl(String realBucketName) {
        return String.format("%s/%s/", this.config.getMinio().getUrl(), realBucketName);
    }

    private void checkAndCreateBucket(String realBucketName) throws Exception {
        if (Objects.equals(this.config.getMinio().isCreateBucketFlag(), Boolean.TRUE)) {
            boolean isExist = this.getClient().bucketExists((BucketExistsArgs.builder().bucket(realBucketName)).build());
            if (!isExist) {
                this.getClient().makeBucket(MakeBucketArgs.builder().bucket(realBucketName).build());
                if (StringUtils.isNotBlank(this.policyConfig)) {
                    this.getClient().setBucketPolicy(SetBucketPolicyArgs.builder().bucket(realBucketName).config(this.policyConfig.replace("${bucketName}", realBucketName)).build());
                }
            }

        }
    }
}
