package com.letslive.letslearnbackend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        var context = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(new LazyJwtSecurityContextProvider(req, res, context));
        filterChain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        String path = request.getRequestURI();
        ArrayList<String> skipURLs = new ArrayList<>();
        skipURLs.add("/auth/signup");
        skipURLs.add("/auth/refresh");
        skipURLs.add("/auth/login");
        skipURLs.add("/auth/logout");
        skipURLs.add("/ws");

        return skipURLs.stream().anyMatch(skipURL -> path.startsWith(skipURL));
    }

    @RequiredArgsConstructor
    static class LazyJwtSecurityContextProvider implements SecurityContext {
        private final HttpServletRequest req;
        private final HttpServletResponse res;
        private final SecurityContext securityCtx;

        @Override
        public Authentication getAuthentication() {
            if (securityCtx.getAuthentication() == null || securityCtx.getAuthentication() instanceof AnonymousAuthenticationToken) {
                try {
                    String accessToken = SecurityUtils.getToken(req, true);
                    DecodedJWT jwt = SecurityUtils.validate(accessToken);

                    if (jwt.getExpiresAt().before(new Date())) {
                        throw new AuthenticationServiceException("Token expired.");
                    }

                    JwtTokenVo tokenVo = SecurityUtils.getValueObject(jwt);
                    var authToken = new PreAuthenticatedAuthenticationToken(tokenVo, null, tokenVo.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                    securityCtx.setAuthentication(authToken);
                } catch (Exception e) {
                    throw new AuthenticationServiceException("Failed to get authentication context: " + e.getMessage());
                }
            }

            return securityCtx.getAuthentication();
        }

        @Override
        public void setAuthentication(Authentication authentication) {
            securityCtx.setAuthentication(authentication);
        }
    }
}