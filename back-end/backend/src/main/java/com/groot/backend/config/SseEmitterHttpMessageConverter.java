package com.groot.backend.config;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public class SseEmitterHttpMessageConverter extends AbstractHttpMessageConverter<SseEmitter> {

    public SseEmitterHttpMessageConverter() {
        super(MediaType.TEXT_EVENT_STREAM);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return SseEmitter.class.isAssignableFrom(clazz);
    }

    @Override
    protected SseEmitter readInternal(Class<? extends SseEmitter> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Reading Server-Sent Events not supported");
    }

    @Override
    protected void writeInternal(SseEmitter sseEmitter, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // Write the SSE events to the output stream
        // Example code:
        // sseEmitter.send("event: ping\nid: 123\ndata: Hello, world!\n\n");
        // sseEmitter.complete();
    }
}
