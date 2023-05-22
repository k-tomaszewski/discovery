package io.github.k_tomaszewski.discovery.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DiscoveryClientFactoryBeanTest {

    @Test
    public void shouldCreateDiscoveryClientBean() {
        // given
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);

        // when
        var discoveryClient = context.getBean(DiscoveryClientBean.class);

        // then
        Assertions.assertNotNull(discoveryClient);
    }
}
