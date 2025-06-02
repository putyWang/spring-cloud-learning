package com.learning.interrogation.server.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/2 上午9:12
 */
public class AesUtil {
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDw1L1j+L6siGI14wcwSbOVbgB+gLIMxnjPlDBS1I/J52OlYmy2Ht/bpwqCFW3NnWgVLaNL9WquiFDrVqAM7CP3adI2AnqUUubBUYwTgKHlQLiVGFm7J95FakWhZaYxFixg6hj60bAY4hDOI35gEH0Wk+59HjrPPwnV+p3APTI2cQMnkQr79tM4axAXscF3yYrLQudm26bBPpSQIM2gQmDAwpaoPr3MxfxXjXblY3Y2t97sf5VYVrHSVogFjsYMgS1BYwT6sUPGB0++tYBIyFV+vzXHmEyes8NJnHegiIAVCNzIgUTjAHeZDnU3mtHhxQQF7LsYyUE8aqJHrtrQpLG5AgMBAAECggEAYsmxYofPlzBesOIOudURsFMuKrYZ0zm6ptOPuvpsfRLIgjfMhixGAXEU02V85CIUZtOQr4DdIPjT0KV5A7P90Oh8jSEZLSyQcW5E+l8CieqxjJ8vd6EUAP9lzp3GvU3uKFb+pixLgvmAkT5oAXniBdfqtTIoR4kBPOAPw6KWrVOElIJQxG8Xcl4ZUHSpqtUlR1kD/c6Q49TFBmTirVngx/vI5MsxDSYNlgpKTpAg20zya+hYF46Al0FCqVwgG4uDgmDlETcowg+xsKBJj1EqdZEkiR4Z2MZ5ysTQRLwFcq+5I/N/JdZ3MLrYADoozxNbz1IvC51+TbH6mHHHINYTXQKBgQD6LdTqFWHVVHAoMBEPSsKQxM5KDuFJ2bi0BvSfy3MMuO2yP0evo9mOxEoVxBA2bBfuC/4KTYmSS/dmjdi0L8KJBYN7euLUJ+cPl54XrLNRv6prCe8z+pUylnlmC2m7X1RYh3Vid/adIeXXlUgExrRGcJuoHga3Qw6cKsYw8zBEpwKBgQD2bzo+wcS2HPzSrs5C3BrPvVFqUo3h9kIE9XJyWs8FsqK/Ieo/qlYwr5g0aapUqUmZz9fsiXoJKIRO7s4BaP1i6xtfsX5x97sQc0EscuE4+Lnn6BQL631Dd8uEJTwT4nq+ZzBAOTga5253uS0vncXkkouu40pt6KKEm+twUDFCnwKBgGJkihtyQ2LybZXwqZT5EyJ6tKM9zO8NmO382lLKYjo7tQhoiNoUN+lFW9nJnZK52oN95fDJY/TMucV0vijHq9vV9ksnHnTQKLjiKYQoIPJizjSA2Ln5sJoPwjkW/gN6T6Lty4Xppm1QOqQaCxQNzjLx8NYi0QNcdS+IRyBTVMV7AoGBALghk5ugnKKWNjIVevhqDX57LoIK/QiBQAMEgmb/gv/24bN+W9EGLTBjg45mBWcMtnm+2s9aSVzecT2pITPchreKhAGUVa5CM/uuolvb2nz/G6kztwbRBd+Lev1Z9PHGn3/1ChBysIEpu1ipfCG/+UU4WjZqjgphcE01U02wHZ5xAoGAIlCxDKJ/wCAN9mBwncWV12whKHW72WXQAXOEydjv5LGGgOO4nhuHkQV3AoyEpm0jgUky+8MyBAsQ/l42NmgnndsdYyh4nE6FAH7tKLqFmnyMP39NByIuSbBjswJOX6cbwRA35s26Pkm6ufSRoUxu7MT0eHi82R/LadqswxHZ24s=";
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8NS9Y/i+rIhiNeMHMEmzlW4AfoCyDMZ4z5QwUtSPyedjpWJsth7f26cKghVtzZ1oFS2jS/VqrohQ61agDOwj92nSNgJ6lFLmwVGME4Ch5UC4lRhZuyfeRWpFoWWmMRYsYOoY+tGwGOIQziN+YBB9FpPufR46zz8J1fqdwD0yNnEDJ5EK+/bTOGsQF7HBd8mKy0LnZtumwT6UkCDNoEJgwMKWqD69zMX8V4125WN2Nrfe7H+VWFax0laIBY7GDIEtQWME+rFDxgdPvrWASMhVfr81x5hMnrPDSZx3oIiAFQjcyIFE4wB3mQ51N5rR4cUEBey7GMlBPGqiR67a0KSxuQIDAQAB";
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    /**
     * 生成 RSA 密钥对并以 String 形式返回
     * @return 包含公钥和私钥的字符串数组，索引 0 为公钥，索引 1 为私钥
     * @throws NoSuchAlgorithmException 若指定的算法不可用
     */
    public static String[] generateKeyPairAsString() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        return new String[]{publicKeyStr, privateKeyStr};
    }
    /**
     * 将 Base64 编码的公钥字符串转换为 PublicKey 对象
     * @return PublicKey 对象
     * @throws Exception 若转换过程出现异常
     */
    public static PublicKey getPublicKey() throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(PUBLIC_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * 将 Base64 编码的私钥字符串转换为 PrivateKey 对象
     * @return PrivateKey 对象
     * @throws Exception 若转换过程出现异常
     */
    public static PrivateKey getPrivateKey() throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(PRIVATE_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * 使用公钥加密数据
     * @param plainText 明文数据
     * @return 加密后的 Base64 编码字符串
     * @throws Exception 若加密过程出现异常
     */
    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 使用私钥解密数据
     * @param cipherText 加密后的 Base64 编码字符串
     * @return 解密后的明文数据
     * @throws Exception 若解密过程出现异常
     */
    public static String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
