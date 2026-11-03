package dev.sorokin.eventmanager.conf;

import io.lettuce.core.RedisConnectionException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

//@Component
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {
    private final FixedLimiterWindow fixedLimiterWindow;

    public RateLimiterFilter(FixedLimiterWindow fixedLimiterWindow) {
        this.fixedLimiterWindow = fixedLimiterWindow;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            String clientId = Optional.ofNullable(request.getHeader("X-API-KEY"))
//                    .filter(header -> !header.isBlank())
//                    .orElseGet(() -> Optional.ofNullable(request.getRemoteAddr()).orElse("unknown"));
//
//            boolean allowed = fixedLimiterWindow.limiterWindow(clientId, 10L, Duration.ofMinutes(1));
//
//            if (!allowed) {
//                response.setStatus(429);
//                response.getWriter().write("Rate limit exceeded");
//                return;
//            }
//        } catch (RedisConnectionFailureException ex) {
//            log.error("Redis not connection: " + ex.getMessage());
//        }
//        filterChain.doFilter(request, response);
//    }
    }
}
