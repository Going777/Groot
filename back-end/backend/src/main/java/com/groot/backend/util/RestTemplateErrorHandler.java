package com.groot.backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.webjars.NotFoundException;

import java.io.IOException;

public class RestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
//        return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
//                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
        // handle by status code
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

    }
}
