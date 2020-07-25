package com.emintolgahanpolat.api.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionsHandler {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAnyException(Exception ex, HttpServletRequest request) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getClass().getSimpleName(), ex.getMessage(), request.getRequestURI());
        HttpStatus httpStatus;
        if (ex instanceof BadCredentialsException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof DisabledException) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (ex instanceof CaptchaException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof ProcessException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof HttpMessageNotReadableException) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage.setMessage("JSON Format Error");
        } else if (ex instanceof MethodArgumentNotValidException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            List<FieldError> errorMessages = e.getBindingResult().getFieldErrors().stream()
                    .map(err -> new FieldError(err.getField(), messageSource.getMessage(err.getDefaultMessage(), null, LocaleContextHolder.getLocale())))
                    .distinct()
                    .collect(Collectors.toList());
            errorMessage.setErrors(errorMessages);
            errorMessage.setMessage(messageSource.getMessage("error.invalidParameter", null, LocaleContextHolder.getLocale()));
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(httpStatus).body(errorMessage.status(httpStatus.value()));

    }


}
