package io.github.k_tomaszewski.discovery.client;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.LongSupplier;

@Configuration
public class TestConfig {

    @Bean
    public DiscoveryClientFactoryBean discoveryClientFactoryBean(Optional<DiscoveryClientParams> params,
            Optional<ExecutorService> executorService, Optional<LongSupplier> uniqueLongGenerator,
            Optional<JsonSerialization> packageFormat) {
        return new DiscoveryClientFactoryBean(params, executorService, uniqueLongGenerator, packageFormat);
    }
}
