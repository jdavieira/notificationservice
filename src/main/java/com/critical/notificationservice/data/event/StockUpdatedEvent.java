package com.critical.notificationservice.data.event;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdatedEvent {

    public int bookId;

    public int stock;

    @Email
    public String userEmail;
}