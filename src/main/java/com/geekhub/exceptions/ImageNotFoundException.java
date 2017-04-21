package com.geekhub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Invalid path to images for upload")
public class ImageNotFoundException extends RuntimeException {
}
