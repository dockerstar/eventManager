package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsUserEntityByLogin(String login);
}
