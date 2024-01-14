package com.iot.parking.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ServiceException extends RuntimeException {

	private final Reason reason;

	private final String message;

	private final LocalDateTime timestamp;

	public ServiceException(Reason reason, String message) {
		this.reason = reason;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
	public ServiceException(Reason reason) {
		this.reason = reason;
		this.message = reason.getMessage();
		this.timestamp = LocalDateTime.now();
	}
}
