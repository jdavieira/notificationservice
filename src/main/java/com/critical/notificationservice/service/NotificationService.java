package com.critical.notificationservice.service;

import com.critical.notificationservice.data.entity.NotificationRequest;
import com.critical.notificationservice.data.repository.NotificationRequestRepository;
import com.critical.notificationservice.dtos.StockRequestFulfilled;
import com.critical.notificationservice.util.AuthServiceException;
import com.critical.notificationservice.util.SaveEntityException;
import jakarta.persistence.EntityNotFoundException;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.spring.annotations.Recurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
public class NotificationService {



    private  static final String stockServiceUrl ="http://localhost:8882/";
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRequestRepository repository;

    private final AuthService authService;
    private final WebClient webClient;

    private final JobScheduler jobScheduler;

    public NotificationService(NotificationRequestRepository repository,
                               AuthService authService,
                               WebClient.Builder webClientBuilder, JobScheduler jobScheduler) {

        this.repository = repository;
        this.authService = authService;
        this.webClient = webClientBuilder.baseUrl(stockServiceUrl).build();
        this.jobScheduler = jobScheduler;
    }

    @Transactional
    public void updateRequestFulfilled(StockRequestFulfilled request) {

        var notificationRequests = this.repository.findNotificationRequestByBookIdAndUserEmail(request.bookId, request.userEmail);
        if (null == notificationRequests || notificationRequests.isEmpty()) {
            var message = "Notification request not found with the book id: " + request.bookId;
            logger.warn(message);
            throw new EntityNotFoundException(message);
        }
        try {
            this.repository.saveAll(notificationRequests);
        } catch (Exception exception) {
            logger.error("Error occurred while updating the notification request information", exception);
            throw new SaveEntityException(exception.getMessage());
        }
    }

     @Recurring(id = "notifyUser",  cron = "*/5 * * * *")
     @Job(name = "userNotificationUser")
     @Transactional
     public void executeNotifyUser(){
        var notificationRequests = this.repository.findNotificationRequestsByNotifiedOn();
         if (null == notificationRequests || notificationRequests.isEmpty()) {
             var message = "Notification request not found with the stock update on null";
             logger.warn(message);
             return;
         }

         try{
            for (var notificationRequest : notificationRequests){
                notificationRequest.setNotifiedOn(Instant.now());
                this.repository.save(notificationRequest);
                jobScheduler.enqueue(() -> this.sendRequestFulfilled(notificationRequest));
            }
            logger.info("Notification requests sent.");
         }catch (Exception ex){
             logger.error("Error occurred while notifying the user", ex);
         }
     }

    public void sendRequestFulfilled(NotificationRequest notificationRequest) throws Exception {

        String accessToken = this.authService.getAccessToken();

        if (accessToken == null) {
            throw new AuthServiceException("It was not possible to get the access token from auth service.");
        }

        var requestFulfilled = new StockRequestFulfilled(notificationRequest.getUserEmail(),notificationRequest.getBookId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity response = webClient
                .post()
                .uri("v1/api/stockRequestFulfilled")
                .header("Authorization", "Bearer " + accessToken)
                .body(BodyInserters.fromValue(requestFulfilled))
                .retrieve()
                .toBodilessEntity()
                .block();

        if(response == null || response.getStatusCode() != HttpStatus.OK){
            throw new Exception("error occurred while executing request fulfilled");
        }
    }
}