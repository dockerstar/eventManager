package dev.sorokin.eventmanager.security.jwt;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.model.AuthUserRequest;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticateService {

    private final JwtManagerToken jwtManagerToken;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticateService(JwtManagerToken jwtManagerToken, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.jwtManagerToken = jwtManagerToken;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public String authenticate(AuthUserRequest authUserRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authUserRequest.login(),
                        authUserRequest.password()
                )
        );

        return jwtManagerToken.generateToken(authUserRequest.login());
    }

    public UserEntity getAuthenticateUser() {
        var user = SecurityContextHolder.getContext().getAuthentication();
        if (user == null) throw new IllegalStateException("User not authenticated");
        String login = (String) user.getPrincipal();
        UserEntity userEntity = userRepository.findUserEntityByLogin(login).get();
        return userEntity;
    }
}
