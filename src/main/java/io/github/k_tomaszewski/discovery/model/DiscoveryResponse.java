package io.github.k_tomaszewski.discovery.model;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Set;

public class DiscoveryResponse {

    /**
     * Correlation ID taken from the request.
     */
    private final long cid;

    /**
     * Application's name.
     */
    private final String app;

    private final Map<Integer, Set<String>> ports;

    /**
     * Host name as established by the application. On Unix-like systems this is what command `hostname` returns.
     */
    private final String hostname;

    @ConstructorProperties({"cid", "app", "ports", "host"})
    public DiscoveryResponse(long cid, String app, Map<Integer, Set<String>> ports, String hostname) {
        this.cid = cid;
        this.app = app;
        this.ports = ports;
        this.hostname = hostname;
    }

    public long getCid() {
        return cid;
    }

    public String getApp() {
        return app;
    }

    public Map<Integer, Set<String>> getPorts() {
        return ports;
    }

    public String getHostname() {
        return hostname;
    }
}
