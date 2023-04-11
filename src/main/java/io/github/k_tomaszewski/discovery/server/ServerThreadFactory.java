package io.github.k_tomaszewski.discovery.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public interface ServerThreadFactory {

    Thread create(DiscoveryServerParams params, BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetProcessor);
}
