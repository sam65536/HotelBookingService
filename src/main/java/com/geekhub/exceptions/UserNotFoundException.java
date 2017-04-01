package com.geekhub.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such User")
public class UserNotFoundException extends RuntimeException {
}