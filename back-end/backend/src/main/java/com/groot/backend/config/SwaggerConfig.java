package com.groot.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
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

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .servers(serverList)
                .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth))
                .security(Arrays.asList(securityRequirement))
                .info(new Info().title("GROOT")
                        .description("API")
                        .version("v0.1"));
    }
}
