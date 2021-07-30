package com.github.glorindor.priconneserverbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request contains invalid input")
public class InvalidRequestInputException extends RuntimeException{
}
