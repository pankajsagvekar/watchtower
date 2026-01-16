package com.watchtower.filter;

import com.watchtower.model.ApiMetric;
import com.watchtower.repository.ApiMetricRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricFilter extends OncePerRequestFilter {

    private final ApiMetricRepository apiMetricRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String path = request.getRequestURI();

            // Skip gathering metrics for H2 console or static resources if any

            if (!path.startsWith("/api/metrics") && !path.startsWith("/h2-console")) {
                ApiMetric metric = ApiMetric.builder()
                        .endpoint(path)
                        .method(request.getMethod())
                        .status(response.getStatus())
                        .responseTimeMs(duration)
                        .timestamp(LocalDateTime.now())
                        .build();

                try {
                    apiMetricRepository.save(metric);
                } catch (Exception e) {
                    log.error("Failed to save metric", e);
                }
            }
        }
    }
}
