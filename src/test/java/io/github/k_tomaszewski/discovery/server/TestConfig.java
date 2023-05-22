package io.github.k_tomaszewski.discovery.server;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.function.Supplier;

@Configuration
public class TestConfig {

    @Bean
    public DiscoveryServerFactoryBean discoveryServerFactoryBean(Optional<DiscoveryServerParams> params,
            Optional<JsonSerialization> packageFormat, Optional<Supplier<String>> hostnameSupplier) {
        return new DiscoveryServerFactoryBean("test-app", params, packageFormat, hostnameSupplier);
    }
}
