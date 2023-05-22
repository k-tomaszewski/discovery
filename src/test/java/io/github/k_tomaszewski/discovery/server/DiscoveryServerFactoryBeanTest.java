package io.github.k_tomaszewski.discovery.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DiscoveryServerFactoryBeanTest {

    @Test
    public void shouldCreateDiscoveryServerBean() {
        // given
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);

        // when
        var discoveryServer = context.getBean(DiscoveryServerFactoryBean.class);

        // then
        Assertions.assertNotNull(discoveryServer);
    }
}
