package io.github.k_tomaszewski.discovery.client;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import io.github.k_tomaszewski.discovery.RandomLongSupplier;
import io.github.k_tomaszewski.discovery.server.DefaultServerThreadFactory;
import io.github.k_tomaszewski.discovery.server.DiscoveryServerBean;
import io.github.k_tomaszewski.discovery.server.DiscoveryServerParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class DiscoveryClientBeanTest {

    private final JsonSerialization serialization = new JsonSerialization();

    @Test
    public void shouldReturnNonNullResult() throws ExecutionException, InterruptedException {
        // given
        var discoveryClient = new DiscoveryClientBean(new DiscoveryClientParams(), Executors.newCachedThreadPool(),
                new RandomLongSupplier(), serialization);

        // when
        var discoveredServices = discoveryClient.discoverServices(Duration.ofSeconds(1)).get();
        discoveryClient.destroy();

        // then
        Assertions.assertNotNull(discoveredServices);
    }

    @Test
    public void shouldReturnCommunicateWithServers() throws ExecutionException, InterruptedException {
        // given
        Supplier<String> testHostnameSupplier = () -> "test-host";

        var serverBean = new DiscoveryServerBean("test-app", new DiscoveryServerParams(), serialization, testHostnameSupplier,
                new DefaultServerThreadFactory());
        serverBean.start();

        var discoveryClient = new DiscoveryClientBean(new DiscoveryClientParams(), Executors.newCachedThreadPool(),
                new RandomLongSupplier(), serialization);

        // when
        serverBean.registerHttpEndpoint(1234, "GET", "/foo-bar");
        var discoveredServices = discoveryClient.discoverServices(Duration.ofSeconds(1)).get();
        discoveryClient.destroy();
        serverBean.destroy();

        // then
        Assertions.assertFalse(discoveredServices.isEmpty());
        var serviceOptional = discoveredServices.stream().filter(service -> "test-app".equals(service.getApp())).findAny();
        Assertions.assertTrue(serviceOptional.isPresent());
        var service = serviceOptional.get();
        Assertions.assertEquals("test-host", service.getHostname());
        Assertions.assertNotNull(service.getPorts());
        Assertions.assertEquals(Set.of(1234), service.getPorts().keySet());
    }
}
