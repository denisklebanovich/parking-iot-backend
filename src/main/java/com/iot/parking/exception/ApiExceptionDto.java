package com.iot.parking.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiExceptionDto{
	HttpStatus status;
	String message;
	LocalDateTime timestamp;

}
