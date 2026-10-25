package dev.sorokin.eventnotificator.controller;

import dev.sorokin.eventnotificator.entity.NotificationsEntity;
import dev.sorokin.eventnotificator.model.Notifications;
import dev.sorokin.eventnotificator.security.AuthenticateService;
import dev.sorokin.eventnotificator.service.NotificationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {
    private final NotificationsService notificationsService;
    private final AuthenticateService authenticateService;

    public NotificationsController(NotificationsService notificationsService, AuthenticateService authenticateService) {
        this.notificationsService = notificationsService;
        this.authenticateService = authenticateService;
    }

    @GetMapping
    public ResponseEntity<List<Notifications>> findUnReadNotifications() {
        Long userIdInJwtToken = authenticateService.getUserIdForAuthenticateUser();
        return ResponseEntity.ok(notificationsService.findUnReadNotifications(userIdInJwtToken));
    }

    @PostMapping
    public ResponseEntity<List<Notifications>> readNotifications() {
        Long userIdInJwtToken = authenticateService.getUserIdForAuthenticateUser();
        return ResponseEntity.ok(notificationsService.readNotificationsForUser(userIdInJwtToken));
    }
}
