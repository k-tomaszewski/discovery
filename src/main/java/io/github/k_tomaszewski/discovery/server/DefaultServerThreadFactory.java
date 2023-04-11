package io.github.k_tomaszewski.discovery.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public class DefaultServerThreadFactory implements ServerThreadFactory {
    @Override
    public Thread create(DiscoveryServerParams params, BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetProcessor) {
        return new DefaultServerThread(params, packetProcessor);
    }
}
