package com.livestock.models.entities;

import lombok.Builder;

import java.beans.Transient;
import java.time.LocalDateTime;

/**
 * This class represent an error entity.
 * It permit to send a formatted error to the client.
 *
 * @param timeStamp   Time of the error
 * @param message     Message of the error
 * @param errorAuthor Author of the error
 * @param httpStatus  HTTP status code of the error
 */
@Builder
public record ErrorEntity(
        LocalDateTime timeStamp,
        String message,
        @Transient
        String errorAuthor,
        int httpStatus
) {
}
