package com.dail.starter.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description
 *
 * @author Dail 2023/01/09 11:06
 */
@ConfigurationProperties(prefix = FileProperties.FILE_PREFIX)
public class FileProperties {
    public static final String FILE_PREFIX = "file";

    private Minio minio = new Minio();

    public Minio getMinio() {
        return minio;
    }

    public void setMinio(Minio minio) {
        this.minio = minio;
    }

    public static class Minio {
        private String url;
        private String accessKey;
        private String secretKey;
        private String accessControl = "read-write";
        private String bucketPrefix = "";
        private boolean createBucketFlag = true;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getAccessControl() {
            return accessControl;
        }

        public void setAccessControl(String accessControl) {
            this.accessControl = accessControl;
        }

        public String getBucketPrefix() {
            return bucketPrefix;
        }

        public void setBucketPrefix(String bucketPrefix) {
            this.bucketPrefix = bucketPrefix;
        }

        public boolean isCreateBucketFlag() {
            return createBucketFlag;
        }

        public void setCreateBucketFlag(boolean createBucketFlag) {
            this.createBucketFlag = createBucketFlag;
        }
    }
}
