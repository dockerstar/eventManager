package dev.sorokin.eventmanager.security.jwt;

import dev.sorokin.eventmanager.model.AuthUserRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticateService {

    private final JwtManagerToken jwtManagerToken;
    private final AuthenticationManager authenticationManager;

    public AuthenticateService(JwtManagerToken jwtManagerToken, AuthenticationManager authenticationManager) {
        this.jwtManagerToken = jwtManagerToken;
        this.authenticationManager = authenticationManager;
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
}
