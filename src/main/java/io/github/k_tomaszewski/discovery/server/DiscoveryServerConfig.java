package io.github.k_tomaszewski.discovery.server;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscoveryServerConfig {

    @Bean
    public DiscoveryServerBean discoveryServerBean(@Value("${spring.application.name}") String appName, DiscoveryServerParams params) {
        return new DiscoveryServerBean(appName, params, new JsonSerialization(), new HostnameSupplier(), new DefaultServerThreadFactory());
    }
}
