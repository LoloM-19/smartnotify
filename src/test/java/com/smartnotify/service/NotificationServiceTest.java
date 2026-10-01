package com.smartnotify.service;

import com.smartnotify.dto.NotificationRequest;
import com.smartnotify.dto.NotificationResponse;
import com.smartnotify.model.Notification;
import com.smartnotify.model.NotificationStatus;
import com.smartnotify.model.NotificationType;
import com.smartnotify.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private RateLimiterService rateLimiterService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequest request;
    private Notification notification;

    @BeforeEach
    void setUp() {
        request = new NotificationRequest();
        request.setRecipient("test@example.com");
        request.setType(NotificationType.EMAIL);
        request.setSubject("Test Subject");
        request.setMessage("Test Message");

        notification = Notification.builder()
                .id(1L)
                .recipient("test@example.com")
                .type(NotificationType.EMAIL)
                .subject("Test Subject")
                .message("Test Message")
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void sendNotification_WhenAllowed_ShouldReturnPendingStatus() {
        when(rateLimiterService.isAllowed(request.getRecipient())).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse response = notificationService.sendNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getRecipient()).isEqualTo("test@example.com");
        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }

    @Test
    void sendNotification_WhenRateLimited_ShouldReturnRateLimitedStatus() {
        when(rateLimiterService.isAllowed(request.getRecipient())).thenReturn(false);

        Notification rateLimitedNotification = Notification.builder()
                .id(2L)
                .recipient("test@example.com")
                .type(NotificationType.EMAIL)
                .subject("Test Subject")
                .message("Test Message")
                .status(NotificationStatus.RATE_LIMITED)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(rateLimitedNotification);

        NotificationResponse response = notificationService.sendNotification(request);

        assertThat(response.getStatus()).isEqualTo(NotificationStatus.RATE_LIMITED);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotificationById_WhenExists_ShouldReturnNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.getNotificationById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRecipient()).isEqualTo("test@example.com");
    }

    @Test
    void getNotificationById_WhenNotExists_ShouldThrowException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotificationById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notification not found with id: 99");
    }

    @Test
    void getNotificationsByRecipient_ShouldReturnList() {
        when(notificationRepository.findByRecipient("test@example.com"))
                .thenReturn(List.of(notification));

        List<NotificationResponse> responses =
                notificationService.getNotificationsByRecipient("test@example.com");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRecipient()).isEqualTo("test@example.com");
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getAllNotifications();

        assertThat(responses).hasSize(1);
    }
}