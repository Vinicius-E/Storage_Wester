package com.wester.storage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API de controle de estoque.
 * Mantém boas práticas de centralização e clareza na documentação da aplicação.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Storage API")
                        .description("API para gerenciamento de estoque 3D com movimentação entre níveis, produtos e usuários. "
                                + "Inclui histórico de movimentações, organização hierárquica por área, fileira, grade e nível.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vinicius Eduardo da Silva")
                                .email("viniciuseduardo0702@hotmail.com")
                                .url("https://github.com/Vinicius-E"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("https://github.com/Vinicius-E")
                                .description("GitHub"),
                        new Server()
                                .url("https://www.linkedin.com/in/vinicius-esilva")
                                .description("LinkedIn")
                ));
    }
}