package com.critical.notificationservice.data.repository;

import com.critical.notificationservice.data.entity.NotificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Integer> {

    @Query(value = "SELECT DISTINCT s FROM NotificationRequest s WHERE s.bookId = :bookId AND s.userEmail = :email")
    List<NotificationRequest> findNotificationRequestByBookIdAndUserEmail(@Param("bookId") int bookId, @Param("email") String email);


    @Query(value = "SELECT DISTINCT s FROM NotificationRequest s where s.notifiedOn IS NULL AND s.stockUpdatedOn IS NOT NULL")
    List<NotificationRequest> findNotificationRequestsByNotifiedOn();
}