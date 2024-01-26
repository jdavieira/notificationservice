package com.critical.notificationservice.messaging.kafka;

import com.critical.notificationservice.data.entity.NotificationRequest;
import com.critical.notificationservice.data.event.BookStockRequestEvent;
import com.critical.notificationservice.data.repository.NotificationRequestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CreateStockRequestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CreateStockRequestConsumer.class);

    private final NotificationRequestRepository repository;

    private final JobScheduler jobScheduler;

    public CreateStockRequestConsumer(NotificationRequestRepository repository, JobScheduler jobScheduler) {

        this.repository = repository;
        this.jobScheduler = jobScheduler;
    }

    @KafkaListener(id = "createStockRequestListener", topics = "${kafka.producer.topic.create-stock-request}")
    public void receive(String data) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var message = objectMapper.readValue(data,BookStockRequestEvent.class);
        logger.warn("Book Stock Request Event message consumed");
        try {
            this.createNotificationRequest(message);
        } catch (Exception ex) {
            jobScheduler.enqueue(() -> this.createNotificationRequest(message));
        }
    }

    private void createNotificationRequest(BookStockRequestEvent data) {

        try {
            var notificationRequest = new NotificationRequest(data.bookId, data.userEmail);
            this.repository.save(notificationRequest);
        } catch (Exception ex) {
            logger.error("An error occurred while saving the Book Stock Request Event", ex.getMessage());
            throw ex;
        }
    }
}