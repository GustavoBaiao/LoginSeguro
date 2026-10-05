package com.loginseguro.exception.handler;

import com.loginseguro.dto.response.error.ErrorResponseDTO;
import com.loginseguro.exception.EmailAlreadyExistsException;
import com.loginseguro.exception.OperationNotAllowedException;
import com.loginseguro.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(
            UserNotFoundException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<ErrorResponseDTO> handleOperationNotAllowedException(
            OperationNotAllowedException exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
            HttpStatus status, String message, HttpServletRequest request) {
        var response = new ErrorResponseDTO(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI());
        return ResponseEntity.status(status).body(response);
    }
}
