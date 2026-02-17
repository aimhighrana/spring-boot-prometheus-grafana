package org.ranasoftcraft.com.spring_boot_prometheus_grafana.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to ensure consistent trace ID throughout a request
 */
//@Component
@Order(1)
public class TracingFilter extends OncePerRequestFilter {
    
    private static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_MDC = "trace.id";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Check if there is an existing trace ID in the request headers
        String traceId = request.getHeader("X-B3-TraceId");
        
        if (traceId == null || traceId.isEmpty()) {
            // If no trace ID exists, generate a new one
            traceId = generateTraceId();
        }
        
        try {
            // Set the trace ID in MDC
            MDC.put(TRACE_ID_MDC, traceId);
            
            // Add the trace ID to the response headers
            response.setHeader("X-B3-TraceId", traceId);
            
            // Continue with the request
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC
            MDC.remove(TRACE_ID_MDC);
        }
    }
    
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
