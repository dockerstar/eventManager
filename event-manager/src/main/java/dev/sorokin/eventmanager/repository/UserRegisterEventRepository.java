package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserRegisterEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRegisterEventRepository extends JpaRepository<UserRegisterEventEntity, Long> {
    @Query("""
        select u from UserRegisterEventEntity u 
                where u.user = :user and u.event = :event    
        """)
    UserRegisterEventEntity searchUserRegister(
            @Param("user") UserEntity user, 
            @Param("event") EventEntity event);

    boolean existsByUserAndEvent(UserEntity user, EventEntity event);

    @Query("""
        select ure.event from UserRegisterEventEntity ure
                where ure.user = :user
        """)
    List<EventEntity> findUserRegisterEventEntitiesByUser(
            @Param("user") UserEntity user);
}
