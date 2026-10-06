package com.loginseguro.exception.handler;

import com.loginseguro.exception.EmailAlreadyExistsException;
import com.loginseguro.exception.OperationNotAllowedException;
import com.loginseguro.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException exception, Model model,
                                              HttpServletResponse response) {
        return buildErrorPage(HttpStatus.NOT_FOUND, exception.getMessage(), model, response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExistsException(EmailAlreadyExistsException exception, Model model,
                                                   HttpServletResponse response) {
        return buildErrorPage(HttpStatus.CONFLICT, exception.getMessage(), model, response);
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public String handleOperationNotAllowedException(OperationNotAllowedException exception, Model model,
                                                     HttpServletResponse response) {
        return buildErrorPage(HttpStatus.FORBIDDEN, exception.getMessage(), model, response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException exception) {
        return "redirect:/login";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleInvalidArgument(MethodArgumentTypeMismatchException exception, Model model,
                                        HttpServletResponse response) {
        return buildErrorPage(HttpStatus.BAD_REQUEST, "O parâmetro informado é inválido", model, response);
    }

    private String buildErrorPage(HttpStatus status, String message, Model model,
                                  HttpServletResponse response) {
        response.setStatus(status.value());
        model.addAttribute("status", status.value());
        model.addAttribute("message", message);
        return "error/error";
    }
}
