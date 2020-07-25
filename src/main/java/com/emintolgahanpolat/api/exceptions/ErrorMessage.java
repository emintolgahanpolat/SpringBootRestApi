package com.emintolgahanpolat.api.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorMessage {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm a z")
    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> errors = new ArrayList<>();

    public ErrorMessage(Date timestamp, String error, String message, String path) {
        this.timestamp = timestamp;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorMessage status(int httpStatus) {
        setStatus(httpStatus);
        return this;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }
}
