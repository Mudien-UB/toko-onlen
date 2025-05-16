package com.example.toko_onlen.exception.common;

import com.example.toko_onlen.dto.common.CustomizeResponseEntity;
import com.example.toko_onlen.exception.BadRequestException;
import com.example.toko_onlen.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Custom Resource Not Found Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex){
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return CustomizeResponseEntity.buildResponse(
                ex.getStatus(),
                ex.getMessage(),
                null
        );
    }


    // ✅ Validation Exception (@Valid DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return CustomizeResponseEntity.buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
    }

    // ✅ Bad Request (e.g., JSON parse error)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request",
                null
        );
    }

    // ✅ Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP Method Not Allowed",
                null
        );
    }

//    // ✅ Access Denied (403)
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
//        return CustomizeResponseEntity.buildResponse(
//                HttpStatus.FORBIDDEN,
//                "Access Denied",
//                null
//        );
//    }

    // ✅ Authentication Failed (401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException ex) {
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                null
        );
    }

    // ✅ Global Generic Exception (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        // Bisa log.error disini kalau mau log error nya
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                null
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex) {
        return CustomizeResponseEntity.buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }



}
