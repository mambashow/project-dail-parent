package com.dail.starter.file.minio.service;

import com.dail.starter.file.config.FileProperties;
import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadPartResponse;
import io.minio.errors.*;
import io.minio.messages.Part;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * description
 *
 * @author Dail 2023/01/09 11:23
 */
public class MyMinioClient extends MinioClient {

    public MyMinioClient(FileProperties fileProperties) {
        super(MinioClient.builder().endpoint(fileProperties.getMinio().getUrl())
                .credentials(fileProperties.getMinio().getAccessKey(), fileProperties.getMinio().getSecretKey()).build());
    }

    public String initUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        CreateMultipartUploadResponse response = super.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);
        return response.result().uploadId();
    }

    public String uploadSlice(String bucketName, String region, String objectName, Object data, int length, String uploadId, int partNumber, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        UploadPartResponse response = super.uploadPart(bucketName, region, objectName, data, length, uploadId, partNumber, extraHeaders, extraQueryParams);
        return response.etag();
    }

    public ObjectWriteResponse combineSlice(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }
}
