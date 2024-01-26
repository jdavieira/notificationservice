package com.critical.notificationservice.controller;

import com.critical.notificationservice.dtos.StockRequestFulfilled;
import com.critical.notificationservice.dtos.error.ErrorResponse;
import com.critical.notificationservice.service.NotificationService;
import com.critical.notificationservice.util.EmailValidation;
import com.critical.notificationservice.util.EntityNullException;
import com.critical.notificationservice.util.SaveEntityDataIntegrityViolationException;
import com.critical.notificationservice.util.SaveEntityException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "Notification management API")
@RestController
@RequestMapping("/v1/api/notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService service;

    public NotificationController(NotificationService service) {

        this.service = service;
    }

    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Stock Request fulfilled request")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping("/requestFulfilled")
    public ResponseEntity updateRequestFulfilled(@Valid @RequestBody StockRequestFulfilled request) {
        if(EmailValidation.isEmailInvalid(request.userEmail)){
            return  ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Email format is invalid"));
        }

        try {
            this.service.updateRequestFulfilled(request);
            return ResponseEntity.ok().build();
        } catch (SaveEntityException | EntityNullException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
        } catch (SaveEntityDataIntegrityViolationException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
}