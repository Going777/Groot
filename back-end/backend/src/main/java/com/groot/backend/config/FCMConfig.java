package com.groot.backend.config;

import io.jsonwebtoken.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Configuration
public class FCMConfig {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException{
        ClassPathResource resource = new ClassPathResource("firebase/ .json");

        InputStream refreshToken = resource.getInputStream();

        FirebaseApp firebaseApp = null;
    }
}
