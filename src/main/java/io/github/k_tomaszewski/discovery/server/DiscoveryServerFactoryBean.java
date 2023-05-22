package io.github.k_tomaszewski.discovery.server;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import org.springframework.beans.factory.FactoryBean;

import java.util.Optional;
import java.util.function.Supplier;

public class DiscoveryServerFactoryBean implements FactoryBean<DiscoveryServerBean> {

    private final String appName;
    private final Optional<DiscoveryServerParams> params;
    private final Optional<JsonSerialization> packageFormat;
    private final Optional<Supplier<String>> hostnameSupplier;

    public DiscoveryServerFactoryBean(String appName, Optional<DiscoveryServerParams> params, Optional<JsonSerialization> packageFormat,
            Optional<Supplier<String>> hostnameSupplier) {
        this.appName = appName;
        this.params = params;
        this.packageFormat = packageFormat;
        this.hostnameSupplier = hostnameSupplier;
    }

    public DiscoveryServerFactoryBean(String appName) {
        this(appName, Optional.empty(), Optional.empty(), Optional.empty());
    }

    @Override
    public DiscoveryServerBean getObject() {
        return new DiscoveryServerBean(appName,
                params.orElseGet(DiscoveryServerParams::new),
                packageFormat.orElseGet(JsonSerialization::new),
                hostnameSupplier.orElseGet(HostnameSupplier::new),
                new DefaultServerThreadFactory());
    }

    @Override
    public Class<?> getObjectType() {
        return DiscoveryServerBean.class;
    }
}
