package com.yanzord.votingappservice.config;

import com.yanzord.votingappservice.feign.CPFValidator;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.yanzord.votingappservice")
public class AppConfig {
    @Value("${cpf_validator.url}")
    private String cpfValidatorUrl;

    @Bean
    public CPFValidator cpfValidator() {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .decoder(new JacksonDecoder())
                .target(CPFValidator.class, cpfValidatorUrl);
    }
}
