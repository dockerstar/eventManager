package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    @Query("""
        select e from EventEntity e 
                left join fetch e.location l
                left join fetch e.user u
                        where u = :user
        """)
    List<EventEntity> findEventEntitiesByUser(@Param("user") UserEntity user);

    List<EventEntity> findAll(Specification<EventEntity> specification);

    @Modifying
    @Query(value = """
            update events
                    set event_status = :newStatus
                    where event_status = :curStatus
                                and date_start <= :dateNow
            """, nativeQuery = true)
    int updateEventStatusByDateStart(
            @Param("newStatus") String newStatus,
            @Param("curStatus") String curStatus,
            @Param("dateNow") Timestamp dateTime
    );

    @Modifying
    @Query(value = """
             update events e
                    set event_status = :newStatus
                    where event_status = :curStatus
                        and (date_start + make_interval(mins := duration)) <= :dateEnd
            """, nativeQuery = true)
    int updateEventStatusByDateEnd(
            @Param("newStatus") String newStatus,
            @Param("curStatus") String curStatus,
            @Param("dateEnd") Timestamp dateTime
    );
}
