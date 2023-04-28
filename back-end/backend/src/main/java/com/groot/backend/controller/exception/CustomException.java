package com.groot.backend.controller.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{
    private final LocalDateTime date = LocalDateTime.now();

    private final HttpStatus status;

    private final String Message;
}
