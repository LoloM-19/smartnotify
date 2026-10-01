package com.smartnotify.service;

import com.smartnotify.dto.NotificationRequest;
import com.smartnotify.dto.NotificationResponse;
import com.smartnotify.model.Notification;
import com.smartnotify.model.NotificationStatus;
import com.smartnotify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RateLimiterService rateLimiterService;

    public NotificationResponse sendNotification(NotificationRequest request) {
        // Check rate limit
        if (!rateLimiterService.isAllowed(request.getRecipient())) {
            log.warn("Rate limit exceeded for recipient: {}", request.getRecipient());

            Notification rateLimited = Notification.builder()
                    .recipient(request.getRecipient())
                    .type(request.getType())
                    .subject(request.getSubject())
                    .message(request.getMessage())
                    .status(NotificationStatus.RATE_LIMITED)
                    .build();

            notificationRepository.save(rateLimited);
            return mapToResponse(rateLimited);
        }

        // Create notification
        Notification notification = Notification.builder()
                .recipient(request.getRecipient())
                .type(request.getType())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(NotificationStatus.PENDING)
                .build();

        notificationRepository.save(notification);
        processNotificationAsync(notification);
        return mapToResponse(notification);
    }

    @Async
    protected void processNotificationAsync(Notification notification) {
        try {
            log.info("Processing {} notification for: {}",
                    notification.getType(), notification.getRecipient());

            // Simulate processing time
            Thread.sleep(500);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);

            log.info("Notification {} sent successfully", notification.getId());

        } catch (Exception e) {
            log.error("Failed to process notification {}: {}", notification.getId(), e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notificationRepository.save(notification);
        }
    }

    public List<NotificationResponse> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return mapToResponse(notification);
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipient(notification.getRecipient())
                .type(notification.getType())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .errorMessage(notification.getErrorMessage())
                .build();
    }
}