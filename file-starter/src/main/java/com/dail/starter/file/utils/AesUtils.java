package com.dail.starter.file.utils;

import com.dail.starter.file.exception.CommonException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * description
 *
 * @author Dail 2023/01/09 14:57
 */
public class AesUtils {
    private static final String DEFAULT_PWD = "RGarqXE1wpAnW6V5hQs0Lg==";
    private static final String ALGORITHM = "AES";

    private AesUtils() {
    }

    private static SecretKeySpec getKey(String password) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
            kg.init(128, new SecureRandom(password.getBytes(StandardCharsets.UTF_8)));
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
        } catch (Exception var3) {
            throw new CommonException("error.error", var3);
        }
    }

    public static byte[] encrypt(byte[] data) {
        return encrypt(data, DEFAULT_PWD);
    }

    public static byte[] encrypt(byte[] data, String password) {
        try {
            SecretKeySpec key = getKey(password);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, key);
            return cipher.doFinal(data);
        } catch (Exception var4) {
            throw new CommonException("error.error", var4);
        }
    }

    public static byte[] decrypt(byte[] data) {
        return decrypt(data, DEFAULT_PWD);
    }

    public static byte[] decrypt(byte[] data, String password) {
        try {
            SecretKeySpec key = getKey(password);
            byte[] raw = key.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception var6) {
            throw new CommonException("error.error.decrypt", var6);
        }
    }
}
