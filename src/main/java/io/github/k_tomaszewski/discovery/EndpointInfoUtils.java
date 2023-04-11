package io.github.k_tomaszewski.discovery;

import org.apache.commons.lang3.Validate;

public class EndpointInfoUtils {

    public static String httpEndpoint(String method, String path) {
        return Validate.notBlank(method, "Blank method") + ' ' + Validate.notBlank(path, "Blank path");
    }
}
