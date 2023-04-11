package io.github.k_tomaszewski.discovery.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Supplier;

public class HostnameSupplier implements Supplier<String> {

    private static final Logger LOG = LoggerFactory.getLogger(HostnameSupplier.class);

    @Override
    public String get() {
        try {
            var inputFromProcessStdOut = Runtime.getRuntime().exec("hostname").getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputFromProcessStdOut))) {
                return reader.readLine().trim();
            }
        } catch (Exception e) {
            LOG.warn("Cannot establish hostname", e);
            return null;
        }
    }
}
