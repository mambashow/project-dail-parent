package com.dail.starter.file.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * description
 *
 * @author Dail 2023/01/09 14:55
 */
public class FileUtils {
    private FileUtils() {
    }

    public static String getFileName(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return null;
        } else {
            int index = fileUrl.lastIndexOf("/");
            if (index < 0) {
                return fileUrl;
            } else {
                String uuidFilename = fileUrl.substring(index + 1);
                if (uuidFilename.contains("@") && uuidFilename.length() >= 33) {
                    return uuidFilename.charAt(32) == '@' ? uuidFilename.substring(33) : uuidFilename;
                } else {
                    return uuidFilename;
                }
            }
        }
    }
}
