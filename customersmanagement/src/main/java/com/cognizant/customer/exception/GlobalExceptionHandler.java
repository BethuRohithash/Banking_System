package com.cognizant.customer.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cognizant.customer.exception.ResourceNotFoundException;
import com.cognizant.customer.dto.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

	

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<?> handleCustomerNotFoundException(CustomerNotFoundException exception, WebRequest request){
		
		ErrorResponseDto error = new ErrorResponseDto(
				request.getDescription(false),
				HttpStatus.NOT_FOUND,
				exception.getMessage(),
				LocalDateTime.now()
			);
		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(CustomerAlreadyExistsException.class)
	public ResponseEntity<?> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException exception, WebRequest request){
		
		ErrorResponseDto error= new ErrorResponseDto(
				request.getDescription(false),
				HttpStatus.CONFLICT,
				exception.getMessage(),
				LocalDateTime.now()
				
				
			);
		return new ResponseEntity<>(error,HttpStatus.CONFLICT);
	}
	@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                                 WebRequest webRequest) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception,
                                                                            WebRequest webRequest) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
