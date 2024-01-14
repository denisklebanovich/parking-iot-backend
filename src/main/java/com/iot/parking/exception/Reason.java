package com.iot.parking.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Reason {

	PARKING_NOT_FOUND(HttpStatus.NOT_FOUND, "Parking not found"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found");

	private final HttpStatus status;
	private final String message;
}
