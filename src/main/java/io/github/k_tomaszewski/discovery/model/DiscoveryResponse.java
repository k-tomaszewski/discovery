package io.github.k_tomaszewski.discovery.model;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.Set;

public class DiscoveryResponse extends ServiceReport {

    /**
     * Correlation ID taken from the request.
     */
    private final long cid;

    @ConstructorProperties({"cid", "app", "ports", "host"})
    public DiscoveryResponse(long cid, String app, Map<Integer, Set<String>> ports, String hostname) {
        super(app, ports, hostname);
        this.cid = cid;
    }

    public long getCid() {
        return cid;
    }
}
