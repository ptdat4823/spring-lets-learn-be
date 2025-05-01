package com.letslive.letslearnbackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letslive.letslearnbackend.entities.RefreshToken;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.utils.TimeUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String ACCESS_TOKEN_AUTHORIZATION_HEADER = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_AUTHORIZATION_HEADER = "REFRESH_TOKEN";
    private static final String AUTHORIZATION_PREFIX = "Bearer_";

    private static final int ACCESS_TOKEN_EXPIRE_TIME_IN_SECOND = 360;
    private static final int REFRESH_TOKEN_EXPIRE_TIME_IN_SECOND = 3600 * 24 * 7;

    private static final String USER_CLAIM = "user";
    private static final String ISSUER = "auth0";

    @Value("${jwt.secret}")
    private static String SECRET_KEY = "LMAO_IT_DOES_NOT_WORK_WITH_STATIC";

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    public static RefreshToken createRefreshToken(User user) {
        JwtTokenVo jwtTokenVo = new JwtTokenVo(user.getId(), user.getRole());
        String token = SecurityUtils.createToken(jwtTokenVo, false);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setValue(token);
        refreshToken.setExpiresAt(TimeUtils.getCurrentTimeGMT7().plusSeconds(REFRESH_TOKEN_EXPIRE_TIME_IN_SECOND));
        refreshToken.setCreatedAt(TimeUtils.getCurrentTimeGMT7());

        return refreshToken;
    }

    public static String createAccessToken(UUID userID, String userRole) {
        JwtTokenVo jwtTokenVo = new JwtTokenVo(userID, userRole);
        return createToken(jwtTokenVo, true);
    }

    public static void removeAllTokens(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCESS_TOKEN_AUTHORIZATION_HEADER) || cookie.getName().equals(REFRESH_TOKEN_AUTHORIZATION_HEADER)) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    attributes.getResponse().addCookie(cookie);
                }
            }
        }

    }

    public static JwtTokenVo GetJwtTokenVoFromPrinciple(Object obj) {
        if (obj instanceof JwtTokenVo) {
            return (JwtTokenVo) obj;
        } else throw new CustomException("Principle object is not an instance of JwtTokenVo", HttpStatus.FORBIDDEN);
    }

    @SneakyThrows
    private static String createToken(JwtTokenVo jwtTokenVo, boolean isAccessToken) {
        var builder = JWT.create();
        var tokenJson = OBJECT_MAPPER.writeValueAsString(jwtTokenVo);
        builder.withClaim(USER_CLAIM, tokenJson);
        return builder
                .withIssuedAt(new Date())
                .withIssuer(ISSUER)
                .withExpiresAt(new Date(System.currentTimeMillis() + (isAccessToken ? ACCESS_TOKEN_EXPIRE_TIME_IN_SECOND : REFRESH_TOKEN_EXPIRE_TIME_IN_SECOND) * 1000))
                .sign(ALGORITHM);
    }

    public static void setTokenToClient(String token, boolean isAccessToken) {
        var cookie = new Cookie(isAccessToken ? ACCESS_TOKEN_AUTHORIZATION_HEADER : REFRESH_TOKEN_AUTHORIZATION_HEADER, AUTHORIZATION_PREFIX + token);
        cookie.setMaxAge(isAccessToken ? ACCESS_TOKEN_EXPIRE_TIME_IN_SECOND : REFRESH_TOKEN_EXPIRE_TIME_IN_SECOND);
        cookie.setPath("/");
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        attributes.getResponse().addCookie(cookie);
    }

    @SneakyThrows
    public static DecodedJWT validate(String token) {
        var verifier = JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(token);
    }

    @SneakyThrows
    public static JwtTokenVo getValueObject(DecodedJWT decodedJWT) {
        var userClaim = decodedJWT.getClaims().get(USER_CLAIM).asString();
        return OBJECT_MAPPER.readValue(userClaim, JwtTokenVo.class);
    }

    public static String getToken(HttpServletRequest req, boolean isAccessToken) {
        String header = isAccessToken ? ACCESS_TOKEN_AUTHORIZATION_HEADER : REFRESH_TOKEN_AUTHORIZATION_HEADER;
        var cookies = req.getCookies();
        if (cookies == null) {
            throw new CustomException("No authorization cookies!", HttpStatus.UNAUTHORIZED);
        }

        var authCookie = Arrays.stream(cookies)
                .filter(e -> e.getName().equals(header))
                .findFirst()
                .orElseThrow(() -> new CustomException("No authorization found!", HttpStatus.UNAUTHORIZED));

        String authorizationHeader = authCookie.getValue();
        Assert.isTrue(authorizationHeader.startsWith(AUTHORIZATION_PREFIX), "Authorization header must start with '" + AUTHORIZATION_PREFIX + "'.");

        String jwtToken = authorizationHeader.substring(AUTHORIZATION_PREFIX.length());
        return jwtToken;
    }
}
