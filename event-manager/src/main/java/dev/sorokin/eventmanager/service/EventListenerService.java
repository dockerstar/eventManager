package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserRole;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ContextResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EventListenerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EventListenerService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    public void listenerStartContextForCreateDefaultUsers(ContextRefreshedEvent context) {
        UserEntity user = new UserEntity(
                null,
                "user",
                passwordEncoder.encode("user"),
                18,
                UserRole.USER
        );
        UserEntity admin = new UserEntity(
                null,
                "admin",
                passwordEncoder.encode("admin"),
                18,
                UserRole.ADMIN
        );

        if (!userRepository.existsUserEntityByLogin(user.getLogin())) {
            userRepository.save(user);
        }
        if (!userRepository.existsUserEntityByLogin(admin.getLogin())) {
            userRepository.save(admin);
        }

        System.out.printf("Во время запуска сгенерировались " +
                "2 дефолтный пользователя\nUSER - %s\nADMIN - %s%n",
                userRepository.findUserEntityByLogin(user.getLogin()),
                userRepository.findUserEntityByLogin(admin.getLogin()));
    }
}
