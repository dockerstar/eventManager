package dev.sorokin.eventmanager.conf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RateLimiterFilter extends OncePerRequestFilter {
    private final FixedLimiterWindow fixedLimiterWindow;

    public RateLimiterFilter(FixedLimiterWindow fixedLimiterWindow) {
        this.fixedLimiterWindow = fixedLimiterWindow;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientId = Optional.ofNullable(request.getHeader("X-API-KEY"))
                .filter(header->!header.isBlank())
                .orElseGet(()->Optional.ofNullable(request.getRemoteAddr()).orElse("unknown"));

        boolean allowed = fixedLimiterWindow.limiterWindow(clientId, 10L ,Duration.ofMinutes(1));

        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        filterChain.doFilter(request,response);
    }
}
