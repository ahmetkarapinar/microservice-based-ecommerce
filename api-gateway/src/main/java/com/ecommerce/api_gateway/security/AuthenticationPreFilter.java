package com.ecommerce.api_gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class AuthenticationPreFilter extends AbstractGatewayFilterFactory<AuthenticationPreFilter.Config> {

    @Value("${keyStore.path}")
    private String keyStorePath;

    @Value("${keyStore.password}")
    private String keyStorePassword;

    @Value("${keyStore.alias}")
    private String keyStoreAlias;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private List<String> excludedUrls;


    public AuthenticationPreFilter() {
        super(Config.class);
    }
    public static class Config{
        private List<String> excludedPatterns;

        public List<String> getExcludedPatterns() {
            return excludedPatterns;
        }

        public void setExcludedPatterns(List<String> excludedPatterns) {
            this.excludedPatterns = excludedPatterns;
        }
    }

    private PublicKey getPublicKey() throws KeyStoreException, FileNotFoundException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(keyStorePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("KeyStore file not found: " + keyStorePath);
            }
            keyStore.load(inputStream, keyStorePassword.toCharArray());
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Certificate cert = keyStore.getCertificate(keyStoreAlias);
        return cert.getPublicKey();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            HttpHeaders httpHeaders = request.getHeaders();
            String token = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println("Path: "+path);
            if (isExcluded(path)){
                return chain.filter(exchange);
            }

            if (token == null || !token.startsWith("Bearer ")){
                return handleAuthError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }
            token = token.substring(7);

            try {
                PublicKey publicKey = getPublicKey();

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                String role = claims.get("role").toString();

                exchange.getRequest().mutate()
                        .header("username", username)
                        .header("role", role)
                        .build();
                return chain.filter(exchange);

            } catch (Exception e) {
                return handleAuthError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean isExcluded(String path){
        Predicate<String> isExcluded = pattern -> path.matches(pattern.replace("**", ".*"));
        return excludedUrls.stream().anyMatch(isExcluded);
    }

    private Mono<Void> handleAuthError(ServerWebExchange exchange, String message, HttpStatus status){
        ServerHttpResponse response =  exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        responseBody.put("message", message);
        responseBody.put("status", status.value());
        responseBody.put("errorCode", status.value());

        try{
            byte [] bytes = objectMapper.writeValueAsBytes(responseBody);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}