package com.warren.clob.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@NullMarked
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    private @Nullable String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken)) {
            bearerToken = request.getHeader("authorization");
        }
        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.regionMatches(true, 0, "Bearer ", 0, 7)) {
                return bearerToken.substring(7).trim();
            }
        }
        return null;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                if (jwtTokenProvider.validateToken(jwt)) {
                    String email = jwtTokenProvider.getEmailFromToken(jwt);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("JWT validation failed for token: " + jwt);
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred while setting authentication", e);
        }
        filterChain.doFilter(request, response);
    }
}
