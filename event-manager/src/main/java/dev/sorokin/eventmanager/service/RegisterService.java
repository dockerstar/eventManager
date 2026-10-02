package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.entity.*;
import dev.sorokin.eventmanager.exception.NoSuchFoundException;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.UserRegisterEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RegisterService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final EventEntityMapper eventEntityMapper;
    private final UserRegisterEventRepository userRegisterEventRepository;
    public RegisterService(EventRepository eventRepository, EventDtoMapper eventDtoMapper, EventEntityMapper eventEntityMapper, UserRegisterEventRepository userRegisterEventRepository) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.eventEntityMapper = eventEntityMapper;
        this.userRegisterEventRepository = userRegisterEventRepository;
    }

    @Transactional
    public void registerOnEvent(UserEntity user, Long eventId) {
        EventEntity eventFound = eventRepository.findById(eventId).orElseThrow(()->
                new NoSuchFoundException("События с id=%s не найдено".formatted(eventId)));
        if (!eventFound.getEventStatus().equals(EventStatus.WAIT_START))
            throw new IllegalArgumentException("Event имеет статус %s".formatted(eventFound.getEventStatus()));
        if (Objects.equals(eventFound.getOccupiedPlaces(), eventFound.getMaxPlaces()))
            throw new IllegalArgumentException("У Event все места заняты");
        eventFound.setOccupiedPlaces(eventFound.getOccupiedPlaces()+1);
        Integer ticketCount = userRegisterEventRepository.existsByUserAndEvent(user, eventFound) ?
                userRegisterEventRepository.searchUserRegister(user, eventFound).getCountTicket() + 1 :
                1;
        UserRegisterEventEntity userRegisterEventEntity = userRegisterEventRepository.existsByUserAndEvent(user, eventFound) ?
                userRegisterEventRepository.searchUserRegister(user, eventFound)
         :
        new UserRegisterEventEntity(
                null,
                user,
                eventFound,
                ticketCount);
        userRegisterEventEntity.setCountTicket(ticketCount);
        userRegisterEventRepository.save(userRegisterEventEntity);
        log.info("User id=%s register for event id=%s".formatted(user.getId(), eventId));
    }

    @Transactional
    public void cancelRegister(UserEntity user, Long eventId) {
        EventEntity eventFound = eventRepository.findById(eventId).orElseThrow(()->
                new NoSuchFoundException("События с id=%s не найдено".formatted(eventId)));
        if (!eventFound.getEventStatus().equals(EventStatus.WAIT_START))
            throw new IllegalArgumentException("Event имеет статус %s, отменить регистрацию не получится"
                    .formatted(eventFound.getEventStatus()));
        if (!userRegisterEventRepository.existsByUserAndEvent(user, eventFound))
            throw new NoSuchFoundException("Регистрация на данное событие отсутствует для текущего пользователя = " + user.getLogin());
        UserRegisterEventEntity userRegisterEventEntity = userRegisterEventRepository.searchUserRegister(user, eventFound);
        if (userRegisterEventEntity.getCountTicket()==0)
            throw new IllegalArgumentException("Пользователь не регистрировался на данное событие");

        userRegisterEventEntity.setCountTicket(userRegisterEventEntity.getCountTicket()-1);
        eventFound.setOccupiedPlaces(eventFound.getOccupiedPlaces()-1);

        log.info("User id=%s cancel register for event id=%s".formatted(user.getId(), eventId));
    }

    @Transactional(readOnly = true)
    public List<Event> findAllByUser(UserEntity user) {
        return userRegisterEventRepository.findUserRegisterEventEntitiesByUser(user).stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }
}
