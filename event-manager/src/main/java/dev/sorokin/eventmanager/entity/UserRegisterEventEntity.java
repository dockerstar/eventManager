package dev.sorokin.eventmanager.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_register_event",
        uniqueConstraints ={
                @UniqueConstraint(
                        name = "uk_user_event",
                        columnNames ={"user_id", "event_id"}
                )
        }
)
public class UserRegisterEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private EventEntity event;

    @Column(name = "count_tickets", nullable = false)
    private Integer countTicket;

    public UserRegisterEventEntity(Long id, UserEntity user, EventEntity event, Integer countTicket) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.countTicket = countTicket;
    }

    public UserRegisterEventEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public Integer getCountTicket() {
        return countTicket;
    }

    public void setCountTicket(Integer countTicket) {
        this.countTicket = countTicket;
    }
}
