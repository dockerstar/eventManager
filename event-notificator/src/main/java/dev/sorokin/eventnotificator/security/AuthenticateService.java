package dev.sorokin.eventnotificator.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticateService {

    public Long getUserIdForAuthenticateUser() {
        var user = SecurityContextHolder.getContext().getAuthentication();
        if (user == null) throw new IllegalStateException("User not authenticated");
        return  (Long) user.getPrincipal();
    }
}
