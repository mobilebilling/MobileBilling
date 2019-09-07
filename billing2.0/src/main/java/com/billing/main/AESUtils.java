package com.billing.main;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    private static String AESkey = "01AE020-UYSLPWSX";

    // 加密
    public static String encrypt128(String sSrc) throws Exception {
        byte[] byteContent;
        byteContent = sSrc.getBytes("utf-8");
        SecretKeySpec key = new SecretKeySpec(AESkey.getBytes("utf-8"), "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // 初始化
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 加密
        byte[] result = cipher.doFinal(byteContent);
        return parseByte2HexStr(result);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
}
