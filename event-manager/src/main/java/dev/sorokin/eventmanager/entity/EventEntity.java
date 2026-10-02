package dev.sorokin.eventmanager.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date_start", nullable = false)
    private LocalDateTime dateStart;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "max_places", nullable = false)
    private Integer maxPlaces;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "occupied_places")
    private Integer occupiedPlaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private LocationEntity location;

    @Column(name = "event_status")
    @Enumerated(value = EnumType.STRING)
    private EventStatus eventStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserEntity user;


    public EventEntity() {
    }

    public EventEntity(Long id, String name, LocalDateTime dateStart, Integer duration, Integer maxPlaces, Integer cost, Integer occupiedPlaces, LocationEntity location, EventStatus eventStatus, UserEntity user) {
        this.id = id;
        this.name = name;
        this.dateStart = dateStart;
        this.duration = duration;
        this.maxPlaces = maxPlaces;
        this.cost = cost;
        this.occupiedPlaces = occupiedPlaces;
        this.location = location;
        this.eventStatus = eventStatus;
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(Integer occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
