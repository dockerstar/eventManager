package dev.sorokin.eventmanager.security.jwt;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserEntityMapper;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtManagerToken jwtManagerToken;
    private final UserService userService;
    private final UserEntityMapper userEntityMapper;

    public JwtTokenFilter(JwtManagerToken jwtManagerToken, UserService userService, UserEntityMapper userEntityMapper) {
        this.jwtManagerToken = jwtManagerToken;
        this.userService = userService;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        String login;
        try {
            login = jwtManagerToken.getLoginFromToken(token);
        } catch (Exception e) {
            log.error("Jwt error reading");
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity userEntity = userEntityMapper.toEntity(userService.findByLogin(login));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userEntity.getLogin(),
                        null,
                        List.of(new SimpleGrantedAuthority(userEntity.getRole().toString()))
                );

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }
}
