package com.ylupol.im.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Location with such name exists already")
public class NonUniqueLocationNameException extends RuntimeException {

}
