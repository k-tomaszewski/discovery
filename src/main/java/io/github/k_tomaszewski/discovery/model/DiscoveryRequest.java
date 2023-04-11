package io.github.k_tomaszewski.discovery.model;

import java.beans.ConstructorProperties;

/**
 * In the future this may contain query criteria.
 */
public class DiscoveryRequest {

    /**
     * Correlation ID. It should be a randomly generated number.
     */
    private final long cid;

    @ConstructorProperties({"cid"})
    public DiscoveryRequest(long cid) {
        this.cid = cid;
    }

    public long getCid() {
        return cid;
    }
}
