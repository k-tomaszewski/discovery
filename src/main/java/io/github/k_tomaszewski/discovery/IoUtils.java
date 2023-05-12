package io.github.k_tomaszewski.discovery;

import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class IoUtils {

    public static void closeSafely(Closeable obj, Logger logger) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                if (logger != null) {
                    logger.warn("Error occurred when closing {}: {}", obj.getClass().getName(), e.getMessage());
                }
            }
        }
    }
}
