package com.pokeprint.api.infra.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Bucket authBucket = Bucket.builder()
            .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
            .build();
            
    private final Bucket publicBucket = Bucket.builder()
            .addLimit(Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1))))
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        Bucket bucketToUse = uri.startsWith("/api/v1/auth/") ? authBucket : publicBucket;
        
        if (!bucketToUse.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", "60");
            response.setContentType("application/problem+json");
            response.getWriter().write("{\"type\":\"about:blank\",\"title\":\"Too Many Requests\",\"status\":429,\"detail\":\"Rate limit exceeded. Please try again later.\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
