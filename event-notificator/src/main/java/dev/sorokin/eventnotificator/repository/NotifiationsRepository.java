package dev.sorokin.eventnotificator.repository;

import dev.sorokin.eventnotificator.entity.NotificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotifiationsRepository extends JpaRepository<NotificationsEntity, Long> {
    List<NotificationsEntity> findNotificationsEntitiesByUserIdAndIsRead(Long userId, boolean isRead);

    @Modifying
    @Query(value = """
        delete from notifications n
                where (n.read_at + make_interval(days := 7)) <= :dateNow ;
        """, nativeQuery = true)
    void deleteNotificationsEntitiesByReadAtBefore(@Param("dateNow")Timestamp dateNow);
}
