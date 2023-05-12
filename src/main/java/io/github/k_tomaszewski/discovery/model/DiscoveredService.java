package io.github.k_tomaszewski.discovery.model;

import java.net.InetAddress;
import java.util.Objects;

/**
 * A record with information about a discovered service.
 */
public class DiscoveredService extends ServiceReport {

    private final InetAddress address;

    public DiscoveredService(InetAddress address, ServiceReport report) {
        super(report);
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        DiscoveredService that = (DiscoveredService) o;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }

    @Override
    public String toString() {
        return String.format("address=%s, %s", address, super.toString());
    }
}
