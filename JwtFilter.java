package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService; 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        String role = null; // To store the role extracted from the token

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract the JWT token
            try {
                username = jwtUtil.extractUsername(jwt); // Extract username (email)
                role = jwtUtil.extractRole(jwt); // Extract role (e.g., "ADMIN", "USER") from token claims
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token is expired: " + e.getMessage());
         
            } catch (SignatureException e) {
                logger.warn("JWT token signature is invalid: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Error extracting JWT details: " + e.getMessage(), e);
            }
        }

        // If username is extracted and no authentication is currently set in SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Ensure the role was successfully extracted from the token
            if (role != null && jwtUtil.validateToken(jwt, userDetails)) {
                // the format Spring Security expects for authorization checks (e.g., "ROLE_ADMIN").
                List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()) // Add "ROLE_" prefix here
                );

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
               
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response); // Continue the filter chain
    }
}
