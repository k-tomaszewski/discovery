package io.github.k_tomaszewski.discovery.client;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import io.github.k_tomaszewski.discovery.RandomLongSupplier;
import org.springframework.beans.factory.FactoryBean;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.LongSupplier;

/**
 * Refer to TestConfig class to see how this bean can be created to use optional dependencies.
 */
public class DiscoveryClientFactoryBean implements FactoryBean<DiscoveryClientBean> {

    private final Optional<DiscoveryClientParams> params;
    private final Optional<ExecutorService> executorService;
    private final Optional<LongSupplier> uniqueLongGenerator;
    private final Optional<JsonSerialization> packageFormat;

    public DiscoveryClientFactoryBean(Optional<DiscoveryClientParams> params, Optional<ExecutorService> executorService,
            Optional<LongSupplier> uniqueLongGenerator, Optional<JsonSerialization> packageFormat) {
        this.params = params;
        this.executorService = executorService;
        this.uniqueLongGenerator = uniqueLongGenerator;
        this.packageFormat = packageFormat;
    }

    public DiscoveryClientFactoryBean() {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    @Override
    public DiscoveryClientBean getObject() {
        return new DiscoveryClientBean(params.orElseGet(DiscoveryClientParams::new),
                executorService.orElseGet(Executors::newCachedThreadPool),
                uniqueLongGenerator.orElseGet(RandomLongSupplier::new),
                packageFormat.orElseGet(JsonSerialization::new));
    }

    @Override
    public Class<?> getObjectType() {
        return DiscoveryClientBean.class;
    }
}
