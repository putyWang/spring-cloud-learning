package com.learning.job.schedule.core.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class TokenUtil {
    private static final String TOKEN_SECRET = "yh-xxjob";
    private static final String TOKEN_USER = "user";

    public TokenUtil() {
    }

    public static String sign(XxlJobUser xxlJobUser) {
        try {
            Date date = new Date(System.currentTimeMillis() + 86400000L);
            Algorithm algorithm = Algorithm.HMAC256("yh-xxjob");
            Map<String, Object> header = new HashMap(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            return JWT.create().withHeader(header).withClaim("user", JsonUtil.toJson(xxlJobUser)).withExpiresAt(date).sign(algorithm);
        } catch (Exception var4) {
            Exception e = var4;
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("yh-xxjob");
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    public static XxlJobUser getClaim(HttpServletRequest request) {
        return (XxlJobUser)JsonUtil.parseObject(JWT.decode(request.getHeader("Authorization")).getClaim("user").asString(), XxlJobUser.class);
    }

    public static void main(String[] args) {
        String str = "eyJhbGciOiJIUzI1NiIsIlR5cGUiOiJKd3QiLCJ0eXAiOiJKV1QifQ.eyJleHAiOjE2MTcyNjYxNzYsInVzZXIiOiJ7XCJpZFwiOjAsXCJwYXNzd29yZFwiOlwiNTg3OTRjOTc1NDhiNzNiMDRmZTU0NGViYWY0YjVkZmNmMDNlNzAwYmQxYzYxMjZkYmYyYWI1ZmQyYzM3ZDBkNFwiLFwicGVybWlzc2lvblwiOlwiXCIsXCJyb2xlXCI6MSxcInVzZXJuYW1lXCI6XCJ3ZWJtYWluXCJ9In0.dugAnOuntnyYzwvwLuIJQgC4on8buOwZ9qGJdlCZlp8";
        System.out.println(JsonUtil.parseObject(JWT.decode(str).getClaim("user").asString(), XxlJobUser.class));
    }
}
