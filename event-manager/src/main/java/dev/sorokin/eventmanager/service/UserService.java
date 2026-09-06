package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.dto.UserDtoMapper;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserEntityMapper;
import dev.sorokin.eventmanager.model.SignUpUserRequest;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper, UserEntityMapper userEntityMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
        this.userEntityMapper = userEntityMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(SignUpUserRequest  signUpUserRequest) {
        if (userRepository.existsUserEntityByLogin(signUpUserRequest.login()))
            throw new IllegalArgumentException("Пользователь с данным логином %s уже есть в системе".formatted(signUpUserRequest.login()));
        User user = userDtoMapper.toDomain(signUpUserRequest);
        UserEntity userEntity = userEntityMapper.toEntity(user);
        userEntity.setPasswordHash(passwordEncoder.encode(userEntity.getPasswordHash()));
        UserEntity userEntityCreated = userRepository.save(userEntity);
        return userEntityMapper.toDomain(userEntityCreated);
    }

    public User findById(Long id) {
        UserEntity userFound = userRepository.findById(id).orElseThrow(()->
                new NoSuchElementException("Пользователь с данным id=%s не найден".formatted(id)));
        return userEntityMapper.toDomain(userFound);
    }

    public User findByLogin(String login) {
        UserEntity userEntity = userRepository.findUserEntityByLogin(login).orElseThrow(
                ()-> new NoSuchElementException("Пользователь %s не найден".formatted(login))
        );

        return userEntityMapper.toDomain(userEntity);
    }
}
