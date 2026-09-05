package com.bancoxyz.bank_batch_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-KEY";

    private final String webKey;
    private final String mobileKey;
    private final String cajeroKey;

    public ApiKeyAuthFilter(
            @Value("${bff.apikey.web}") String webKey,
            @Value("${bff.apikey.mobile}") String mobileKey,
            @Value("${bff.apikey.cajero}") String cajeroKey) {
        this.webKey = webKey;
        this.mobileKey = mobileKey;
        this.cajeroKey = cajeroKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(HEADER);
        String role = resolveRole(apiKey);

        if (role != null) {
            var auth = new UsernamePasswordAuthenticationToken(
                    "canal-" + role.toLowerCase(), null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    private String resolveRole(String apiKey) {
        if (apiKey == null) {
            return null;
        }
        if (apiKey.equals(webKey)) {
            return "WEB";
        }
        if (apiKey.equals(mobileKey)) {
            return "MOBILE";
        }
        if (apiKey.equals(cajeroKey)) {
            return "CAJERO";
        }
        return null;
    }
}