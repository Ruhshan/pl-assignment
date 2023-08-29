package xyz.ruhshan.pl.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name}") String title) {
        return new OpenAPI()
            .components(new Components())
            .info(new Info().title("pl-assignment-"+title)
                .description(
                    "ruhshan.ahmed@gmail.com")
                .version("0.0.1-SNAPSHOT"));
    }
}

