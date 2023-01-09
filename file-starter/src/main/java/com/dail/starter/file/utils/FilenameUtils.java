package com.dail.starter.file.utils;

import org.springframework.util.Base64Utils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * description
 *
 * @author Dail 2023/01/09 14:55
 */
public class FilenameUtils {

    private FilenameUtils() {
    }


    public static String encodeFileName(HttpServletRequest request, String filename) throws IOException {
        filename = filename.replace(" ", "");
        filename = filename.replace("\n", "");
        filename = filename.replace("\r", "");
        if (request == null) {
            return URLEncoder.encode(filename, "UTF-8");
        } else {
            String userAgent = request.getHeader("User-Agent").toLowerCase();
            String encodeFilename;
            if (!userAgent.contains("msie") && !userAgent.contains("like gecko")) {
                if (userAgent.toLowerCase().contains("firefox")) {
                    encodeFilename = "=?UTF-8?B?" + Base64Utils.encodeToString(filename.getBytes(StandardCharsets.UTF_8)) + "?=";
                } else {
                    encodeFilename = URLEncoder.encode(filename, "UTF-8");
                }
            } else {
                encodeFilename = URLEncoder.encode(filename, "UTF-8");
            }

            return encodeFilename;
        }
    }

    public static String encodeFileName(String filename) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return URLEncoder.encode(filename, "UTF-8");
        } else {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return encodeFileName(request, filename);
        }
    }

    public static String getFileName(String fileUrl) {
        return FileUtils.getFileName(fileUrl);
    }
}
