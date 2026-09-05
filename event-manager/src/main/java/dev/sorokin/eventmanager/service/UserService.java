package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.dto.UserDtoMapper;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserEntityMapper;
import dev.sorokin.eventmanager.model.SignUpUserRequest;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final UserEntityMapper userEntityMapper;

    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper, UserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
        this.userEntityMapper = userEntityMapper;
    }

    public User save(SignUpUserRequest  signUpUserRequest) {
        if (userRepository.existsUserEntityByLogin(signUpUserRequest.login()))
            throw new IllegalArgumentException("Пользователь с данным логином %s уже есть в системе".formatted(signUpUserRequest.login()));
        User user = userDtoMapper.toDomain(signUpUserRequest);
        UserEntity userEntity = userRepository.save(userEntityMapper.toEntity(user));
        return userEntityMapper.toDomain(userEntity);
    }

    public User findById(Long id) {
        UserEntity userFound = userRepository.findById(id).orElseThrow(()->
                new NoSuchElementException("Пользователь с данным id=%s не найден".formatted(id)));
        return userEntityMapper.toDomain(userFound);
    }
}
