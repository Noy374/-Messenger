package com.example.messenger.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Messenger",
               version = "1.0.0",
                contact = @Contact(
                        name = "Svarian Karapet",
                        email = "ksvarian@mail.ru"
                )
        )
)
public class SwaggerConfig {
}
