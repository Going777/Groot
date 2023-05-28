package com.groot.backend.controller.exception;

public class WrongArticleException extends RuntimeException{
    public WrongArticleException(String message) {
        super("Inadequate Article for transfer " + message);
    }
}
