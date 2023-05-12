package io.github.k_tomaszewski.discovery.model;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ServiceReport {

    /**
     * Application's name.
     */
    private final String app;

    /**
     * For each port number there is a set of descriptions. They're up to the service. The general idea is that for a common REST API
     * there will be a TCP port a service listens on and requiring HTTP. Each description element can then denote a single REAT API endpoint
     * like `GET /health`. So by convention, a description item starting with HTTP method name means this is HTTP endpoint.
     */
    private final Map<Integer, Set<String>> ports;

    /**
     * Host name as established by the application. On Unix-like systems this is what command `hostname` returns.
     */
    private final String hostname;


    public ServiceReport(String app, Map<Integer, Set<String>> ports, String hostname) {
        this.app = app;
        this.ports = ports;
        this.hostname = hostname;
    }

    public ServiceReport(ServiceReport src) {
        app = src.app;
        ports = src.ports;
        hostname = src.hostname;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ServiceReport that = (ServiceReport) other;
        return Objects.equals(app, that.app) && Objects.equals(ports, that.ports) && Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, ports, hostname);
    }

    @Override
    public String toString() {
        return "app='" + app + '\'' + ", ports=" + ports + ", hostname='" + hostname + '\'';
    }
}
