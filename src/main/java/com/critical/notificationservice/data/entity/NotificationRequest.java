package com.critical.notificationservice.data.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notification_request")
@Getter
@Setter
@NoArgsConstructor
public class NotificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_request_id")
    private int id;

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "stock_updated_on")
    private Instant stockUpdatedOn;

    @Column(name = "notified_on")
    private Instant notifiedOn;

    @Column(name = "created_on", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.PROTECTED)
    private Instant createdOn;

    @Column(name = "updated_on")
    @UpdateTimestamp
    @Setter(AccessLevel.PROTECTED)
    private Instant updatedOn;

    public NotificationRequest(int bookId, String userEmail) {
        this.bookId = bookId;
        this.userEmail = userEmail;
    }
}