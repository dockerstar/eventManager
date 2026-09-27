package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    private final EventEntityMapper eventEntityMapper;

    public UserEntityMapper(EventEntityMapper eventEntityMapper) {
        this.eventEntityMapper = eventEntityMapper;
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.passwordHash(),
                user.age(),
                user.role(),
                user.eventsList().stream()
                        .map(eventEntityMapper::toEntity)
                        .toList()
        );
    }

    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPasswordHash(),
                userEntity.getAge(),
                userEntity.getRole(),
                userEntity.getEventsList().stream()
                        .map(eventEntityMapper::toDomain)
                        .toList()
        );
    }
}
