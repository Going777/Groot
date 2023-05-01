package com.groot.backend.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SseEmitters {

    private static final AtomicLong counter = new AtomicLong();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter add(SseEmitter emitter){
        this.emitters.add(emitter);
        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            emitter.complete();
        });

        return emitter;
    }

    public void count(){
        long count = counter.incrementAndGet();
        emitters.forEach(emitter -> {
            try{
                emitter.send(SseEmitter.event()
                        .name("count")
                        .data(count));
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        });
    }
}
