package com.dail.starter.file.utils;

/**
 * description
 *
 * @author Dail 2023/01/09 13:42
 */
public interface FileConstants {
    interface Flag{
        String YES="Y";
        String NO = "N";
    }
    interface MinioAccessControl {
        String READ_WRITE = "read-write";
        String READ_ONLY = "read-only";
        String WRITE_ONLY = "write-only";
        String NONE = "none";
    }
}
