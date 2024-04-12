package com.learning.core.utils;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpUtils {

    /**
     * 空 x509 证书验证类
     */
    private static final X509TrustManager emptyX509TrustManager = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    public static OkHttpClient getUnsafeOkHttpsClient() throws KeyManagementException {
        try {
            // 1.https 空信任验证处理对象
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{emptyX509TrustManager}, new SecureRandom());
            // 2.构造 https 客户端
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return new Builder()
                    .sslSocketFactory(sslSocketFactory, emptyX509TrustManager)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("minioClient创建失败", e);
        }
    }
}
