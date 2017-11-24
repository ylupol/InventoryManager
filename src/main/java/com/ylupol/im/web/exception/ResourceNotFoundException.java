package com.ylupol.im.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Requested resource is not found")
public class ResourceNotFoundException extends RuntimeException {

}
