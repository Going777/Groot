package com.groot.backend.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{
    private final HttpStatus status;
    private final String result;
    private final String msg;

}
