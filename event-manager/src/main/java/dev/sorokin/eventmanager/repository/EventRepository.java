package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findEventEntitiesByUser(UserEntity user);

    @Query("""
        select e from EventEntity e where (:name is null or e.name = :name  )
            and (:placesMin is null or e.maxPlaces >= :placesMin)
            and (:placesMax is null or e.maxPlaces <= :placesMax)
            and (:dateStartAfter is null or e.dateStart >= :dateStartAfter)
            and (:dateStartBefore is null or e.dateStart <= :dateStartBefore)
            and (:costMin is null or e.cost >= :costMin)
            and (:costMax is null or e.cost <= :costMax)
            and (:durationMin is null or e.duration >= :durationMin)
            and (:durationMax is null or e.duration <= :durationMax)
            and (:location is null or e.location = :location)
            and (:eventStatus is null or e.eventStatus = :eventStatus)
        """)
    List<EventEntity> search(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") OffsetDateTime dateStartAfter,
            @Param("dateStartBefore") OffsetDateTime dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("location") LocationEntity location,
            @Param("eventStatus") EventStatus eventStatus
    );
}
