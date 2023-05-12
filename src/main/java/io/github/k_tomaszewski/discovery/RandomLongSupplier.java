package io.github.k_tomaszewski.discovery;

import java.util.Random;
import java.util.function.LongSupplier;

public class RandomLongSupplier implements LongSupplier {

    private final Random rndGenerator = new Random(System.currentTimeMillis());

    @Override
    public long getAsLong() {
        return rndGenerator.nextLong();
    }
}
