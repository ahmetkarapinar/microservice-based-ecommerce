package com.ecommerce.product_service.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private final KeyStore keyStore;

    public JwtTokenFilter(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            try{
                PublicKey publicKey = keyStore.getCertificate("auth-server").getPublicKey();

                Claims claims = Jwts.parser().setSigningKey(publicKey)
                        .parseClaimsJws(token)
                        .getBody();

                List<String> roles = claims.get("role", List.class);
                List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                Long userId = claims.get("userId", Long.class);


                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request, response);
    }
}