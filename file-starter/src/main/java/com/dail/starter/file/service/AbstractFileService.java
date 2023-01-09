package com.dail.starter.file.service;

import com.dail.starter.file.config.FileProperties;
import com.dail.starter.file.entity.FileInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Map;

/**
 * description
 *
 * @author Dail 2023/01/09 11:22
 */
public abstract class AbstractFileService {

    protected FileProperties config;

    public AbstractFileService init(FileProperties config) {
        this.config = config;
        return this;
    }

    public void shutdown() {
    }

    public abstract String upload(FileInfo file, String filePath);

    public abstract String upload(FileInfo file, InputStream inputStream);

    public abstract String initUpload(FileInfo file);

    public abstract String uploadSlice(FileInfo file, String uploadId, Integer partNumber, InputStream inputStream);

    public abstract String combine(FileInfo file, String uploadId, Map<String, String> map);

    public abstract String copyFile(FileInfo file, String oldFileKey, String oldBucketName);

    public abstract void deleteFile(String bucketName, String url, String fileKey);

    public abstract String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires);

    public abstract void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey);

    public abstract void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password);

    public abstract String getObjectPrefixUrl(String realBucketName);

    public void buildResponse(HttpServletResponse response, byte[] data, String fileName) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("multipart/form-data");
        response.addHeader("Content-Length", "" + data.length);
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", System.currentTimeMillis() + 1000L);
        IOUtils.write(data, response.getOutputStream());
    }

    public void buildResponse(HttpServletResponse response, InputStream inputStream, String fileName) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("multipart/form-data");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", System.currentTimeMillis() + 1000L);
        OutputStream outputStream = response.getOutputStream();
        Throwable var5 = null;

        try {
            byte[] buff = new byte[1024];

            int len;
            while((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
        } catch (Throwable var15) {
            var5 = var15;
            throw var15;
        } finally {
            if (outputStream != null) {
                if (var5 != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable var14) {
                        var5.addSuppressed(var14);
                    }
                } else {
                    outputStream.close();
                }
            }

        }

    }

    public String getRealBucketName(String bucketName) {
        return StringUtils.isNotBlank(this.config.getMinio().getBucketPrefix()) ? String.format("%s-%s", this.config.getMinio().getBucketPrefix(), bucketName) : bucketName;
    }

    public String getFileKey(String bucketName, String url) {
        String prefixUrl = this.getObjectPrefixUrl(bucketName);

        try {
            return URLDecoder.decode(url, "UTF-8").substring(prefixUrl.length());
        } catch (Exception var5) {
            return url.substring(prefixUrl.length());
        }
    }
}
