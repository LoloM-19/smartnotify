package com.smartnotify.controller;

import com.smartnotify.dto.ApiResponse;
import com.smartnotify.dto.NotificationRequest;
import com.smartnotify.dto.NotificationResponse;
import com.smartnotify.service.NotificationService;
import com.smartnotify.service.RateLimiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final RateLimiterService rateLimiterService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {

        log.info("Received notification request for: {}", request.getRecipient());
        NotificationResponse response = notificationService.sendNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification queued successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable Long id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(ApiResponse.success("Notification retrieved", response));
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByRecipient(
            @PathVariable String recipient) {
        List<NotificationResponse> notifications =
                notificationService.getNotificationsByRecipient(recipient);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
    }

    @GetMapping("/rate-limit/{recipient}")
    public ResponseEntity<ApiResponse<Long>> getRemainingRequests(
            @PathVariable String recipient) {
        long remaining = rateLimiterService.getRemainingRequests(recipient);
        return ResponseEntity.ok(ApiResponse.success("Remaining requests", remaining));
    }
}