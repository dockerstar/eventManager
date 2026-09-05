package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.passwordHash(),
                user.age(),
                user.role()
        );
    }

    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPasswordHash(),
                userEntity.getAge(),
                userEntity.getRole()
        );
    }
}
