package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.entity.UserRole;
import dev.sorokin.eventmanager.model.SignUpUserRequest;
import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public User toDomain(SignUpUserRequest signUpUserRequest) {
        return new User(
                null,
                signUpUserRequest.login(),
                signUpUserRequest.password(),
                signUpUserRequest.age(),
                UserRole.USER
        );
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.login(),
                user.age(),
                user.role()
        );
    }
}
