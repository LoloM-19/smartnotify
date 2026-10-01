package com.smartnotify.repository;

import com.smartnotify.model.Notification;
import com.smartnotify.model.NotificationStatus;
import com.smartnotify.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByRecipientAndType(String recipient, NotificationType type);

    long countByRecipientAndStatus(String recipient, NotificationStatus status);
}