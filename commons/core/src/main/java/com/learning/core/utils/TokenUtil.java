package com.learning.core.utils;

public interface TokenUtil {

    /**
     * 解密token
     * @param token
     * @param salt
     * @return
     */
    static String decodeToken(String token, String salt) {

        return MD5Utils.decode(token).substring(salt.length());
    }

    /**
     * 解密token
     * @param uuid
     * @param salt
     * @return
     */
    static String encodeToken(String uuid, String salt) {

        return MD5Utils.encode(salt + uuid);
    }
}
