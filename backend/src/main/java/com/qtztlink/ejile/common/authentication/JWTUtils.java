package com.qtztlink.ejile.common.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.qtztlink.ejile.system.model.user.Admin;
import com.qtztlink.ejile.system.model.user.Consumer;
import com.qtztlink.ejile.system.model.user.Shop;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JWTUtils {
    // 过期时间 1200 分钟 = 20 小时
    private static final long EXPIRE_TIME = 1200 * 60 * 1000;

    /**
     * 校验token是否正确
     * @param token 密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息
     * @return token中包含的id
     */
    public static Integer getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("id").asInt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * @param user 用户名
     * @param secret 用户的密码
     * @return 加密的token
     */
    public static String consumerSign(Consumer user, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);

            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", user.getUsername())
                    .withClaim("id", user.getId())
                    .withClaim("roles", "consumer")
                    .withClaim("permission", "ok")
                    .withClaim("expire_time", date.getTime())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String shopSign(Shop user, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
//            System.out.println(date.getTime());

            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", user.getUsername())
                    .withClaim("id", user.getId())
                    .withClaim("roles", "shop")
                    .withClaim("permission", "ok")
                    .withClaim("expire_time", date.getTime())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // admin
    public static String adminSign(Admin user, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
//            System.out.println(date.getTime());

            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", user.getUsername())
                    .withClaim("id", user.getId())
                    .withClaim("roles", "admin")
                    .withClaim("permission", "ok")
                    .withClaim("expire_time", date.getTime())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
