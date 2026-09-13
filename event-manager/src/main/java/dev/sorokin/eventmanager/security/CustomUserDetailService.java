package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByLogin(username).orElseThrow(()->
                new NoSuchElementException("User %s not found".formatted(username)));
        return User.withUsername(username)
                .authorities(userEntity.getRole().toString())
                .password(userEntity.getPasswordHash())
                .build();
    }
}
