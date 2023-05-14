package com.learning.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public MD5Utils() {
    }

    /**
     * 转化为长度为32的Md5加密字符串
     * @param str
     * @return
     */
    public static String strToMd5_32(String str) {
        String md5Str = "";
        if (str != null && str.length() != 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes());
                StringBuffer buf = new StringBuffer("");
                byte[] mds = md.digest();

                for(byte value : mds) {
                    int i = value;
                    if (value < 0) {
                        i = value + 256;
                    }

                    if (i < 16) {
                        buf.append("0");
                    }

                    buf.append(Integer.toHexString(i));
                }

                md5Str = buf.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return md5Str;
    }

    /**
     * 转化为长度为16的Md5加密字符串
     * @param str
     * @return
     */
    public static String strToMd5_16(String str) {

        return strToMd5_32(str).substring(8, 24);
    }

    /**
     * 分字符进行MD5加密
     * @param dataStr
     * @return
     */
    public static String encrypt(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes(StandardCharsets.UTF_8));
            byte[] s = m.digest();
            StringBuilder result = new StringBuilder();

            for(int i = 0; i < s.length; ++i) {
                result.append(Integer.toHexString(255 & s[i] | -256).substring(6));
            }

            return result.toString();
        } catch (Exception var5) {
            var5.printStackTrace();
            return "";
        }
    }

    /**
     * 使用116对字符串进行编码
     * @param inStr
     * @return
     */
    public static String encode(String inStr) {
        if (StringUtils.isEmpty(inStr)) {
            return null;
        }

        char[] a = inStr.toCharArray();

        for(int i = 0; i < a.length; ++i) {
            a[i] = (char)(a[i] ^ 116);
        }

        return new String(a);
    }

    /**
     * 使用116对字符串进行解码
     * @param inStr
     * @return
     */
    public static String decode(String inStr) {
        if (StringUtils.isEmpty(inStr)) {
            return null;
        } else {
            char[] a = inStr.toCharArray();

            for(int i = 0; i < a.length; ++i) {
                a[i] = (char)(a[i] ^ 116);
            }

            return new String(a);
        }
    }
}
