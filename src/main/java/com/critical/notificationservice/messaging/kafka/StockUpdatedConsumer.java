package com.critical.notificationservice.messaging.kafka;

import com.critical.notificationservice.data.event.BookStockRequestEvent;
import com.critical.notificationservice.data.event.StockUpdatedEvent;
import com.critical.notificationservice.data.repository.NotificationRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class StockUpdatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CreateStockRequestConsumer.class);

    private final NotificationRequestRepository repository;

    private final JobScheduler jobScheduler;

    public StockUpdatedConsumer(NotificationRequestRepository repository, JobScheduler jobScheduler) {

        this.repository = repository;
        this.jobScheduler = jobScheduler;
    }

    @KafkaListener(id = "stockUpdateListener", topics = "${kafka.producer.topic.stock-updated-request}")
    public void receive(String data) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var message = objectMapper.readValue(data, StockUpdatedEvent.class);
        logger.warn("Book Stock Request Event message consumed");
        try {
            this.updateNotificationRequest(message);
        } catch (Exception ex) {
            jobScheduler.enqueue(() -> this.updateNotificationRequest(message));
        }
    }

    @Transactional
    protected void updateNotificationRequest(StockUpdatedEvent data) {

        try {
            var notificationRequests = this.repository.findNotificationRequestByBookIdAndUserEmail(data.bookId, data.userEmail);
            if (null == notificationRequests || notificationRequests.isEmpty()) {
                var message = "Notification request not found with the book id: " + data.bookId;
                logger.warn(message);
                return;
            }
            for (var notificationRequest : notificationRequests) {
                notificationRequest.setStockUpdatedOn(Instant.now());
                this.repository.save(notificationRequest);
            }
        } catch (Exception ex) {
            logger.error("An error occurred while saving the Book Stock Request Event", ex.getMessage());
            throw ex;
        }
    }
}