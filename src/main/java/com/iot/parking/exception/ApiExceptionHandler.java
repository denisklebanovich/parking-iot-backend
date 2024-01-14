package com.iot.parking.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@SuppressWarnings("NullableProblems")
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ApiExceptionDto> handleServiceException(ServiceException e) {
		return ResponseEntity.status(e.getReason().getStatus())
				.body(new ApiExceptionDto(e.getReason().getStatus(), e.getMessage(), e.getTimestamp()));
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		var body = new ApiExceptionDto(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiExceptionDto> handleException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), LocalDateTime.now()));
	}
}
