package com.groot.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        List<Server> serverList = new ArrayList<>();

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080/api");
        Server ec2Server = new Server();
        ec2Server.setUrl("https://k8a303.p.ssafy.io/api");

        serverList.add(ec2Server);
        serverList.add(localServer);

        return new OpenAPI()
                .servers(serverList)
                .info(new Info().title("GROOT")
                        .description("API")
                        .version("v0.1"));
    }
}
