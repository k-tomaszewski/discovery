package io.github.k_tomaszewski.discovery.client;

import io.github.k_tomaszewski.discovery.JsonSerialization;
import io.github.k_tomaszewski.discovery.model.DiscoveryRequest;
import io.github.k_tomaszewski.discovery.model.DiscoveredService;
import io.github.k_tomaszewski.discovery.model.DiscoveryResponse;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import java.util.function.UnaryOperator;

import static io.github.k_tomaszewski.discovery.IoUtils.closeSafely;

/**
 * Service discovery client bean.
 */
public class DiscoveryClientBean implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryClientBean.class);

    private final InetSocketAddress groupSocketAddress;
    private final DatagramChannel channel;
    private final Selector selector;
    private final AtomicReference<CompletableFuture<Set<DiscoveredService>>> currentCompletableAtomicRef = new AtomicReference<>();
    private final ExecutorService executorService;
    private final LongSupplier uniqueLongGenerator;
    private final JsonSerialization packageFormat;
    private final ByteBuffer buffer;

    public DiscoveryClientBean(DiscoveryClientParams params, ExecutorService executorService, LongSupplier uniqueLongGenerator,
            JsonSerialization packageFormat) {
        groupSocketAddress = createGroupSocketAddress(params);
        this.executorService = Objects.requireNonNull(executorService, "ExecutorService cannot be null");
        this.uniqueLongGenerator = Objects.requireNonNull(uniqueLongGenerator, "Unique long value generator cannot be null");
        this.packageFormat = Objects.requireNonNull(packageFormat, "JsonSerialization cannot be null");
        buffer = ByteBuffer.allocate(params.getMaxPacketBytes());
        var opened = openChannel(params);
        channel = opened.channel;
        selector = opened.selector;
    }

    public CompletableFuture<Set<DiscoveredService>> discoverServices(Duration waitDuration) {
        var updater = new CompletableUpdater();
        var completable = currentCompletableAtomicRef.updateAndGet(updater);
        if (updater.hasCreatedNewCompletable()) {
            executorService.submit(() -> runDiscovery(waitDuration, completable));
        }
        return completable;
    }

    @Override
    public void destroy() {
        if (selector != null) {
            try {
                selector.wakeup();
            } catch (Throwable t) {
                // nop
            }
        }
        closeSafely(selector, LOG);
        closeSafely(channel, LOG);
    }

    private void runDiscovery(Duration waitDuration, CompletableFuture<Set<DiscoveredService>> completable) {
        long correlationId = 0;
        try {
            correlationId = uniqueLongGenerator.getAsLong();
            LOG.debug("Started service discovery with cid = {}.", correlationId);

            ByteBuffer requestBytes = packageFormat.serialize(new DiscoveryRequest(correlationId));
            send(requestBytes);
            LOG.debug("Discovery request with cid = {} was sent.", correlationId);

            final long totalDurationMillis = waitDuration.toMillis();
            final long startTimeMillis = System.currentTimeMillis();
            final Set<DiscoveredService> discoveredServices = new HashSet<>();

            long leftDurationMillis = totalDurationMillis;
            while (leftDurationMillis > 0) {
                if (completable.isCancelled()) {
                    LOG.debug("Service discovery with cid = {} was cancelled.", correlationId);
                    return;
                }
                LOG.debug("Waiting for responses for service discovery with cid = {} for {} ms...", correlationId, leftDurationMillis);
                if (selector.select(leftDurationMillis) > 0) {
                    receiveResponse(correlationId, discoveredServices);
                    selector.selectedKeys().clear();
                }
                leftDurationMillis = totalDurationMillis - (System.currentTimeMillis() - startTimeMillis);
            }

            LOG.debug("Discovery request with cid = {} completed.", correlationId);
            completable.complete(discoveredServices);
        } catch (Throwable e) {
            completable.completeExceptionally(e);
            LOG.error("Failure of service discovery with cid = {}", correlationId, e);
        }
    }

    private void receiveResponse(long correlationId, Set<DiscoveredService> discoveredServices) {
        SocketAddress srcSocketAddress = null;
        try {
            buffer.clear();
            srcSocketAddress = channel.receive(buffer);
            LOG.debug("Service discovery with cid = {} received packet of {} bytes from {}.", correlationId, buffer.position(),
                    srcSocketAddress);

            Validate.isInstanceOf(InetSocketAddress.class, srcSocketAddress, "Source address is not IP: %s", srcSocketAddress);
            final InetSocketAddress srcInetSocketAddr = (InetSocketAddress) srcSocketAddress;

            buffer.flip();
            DiscoveryResponse response = packageFormat.deserialize(buffer, DiscoveryResponse.class);

            if (response.getCid() == correlationId) {
                var discoveredService = new DiscoveredService(srcInetSocketAddr.getAddress(), response);
                discoveredServices.add(discoveredService);
                LOG.debug("Service discovery with cid = {} got a result: {}", correlationId, discoveredService);
            }
        } catch (Exception e) {
            LOG.warn("Error during processing packet received from {}", srcSocketAddress, e);
        }
    }

    private void send(ByteBuffer requestBytes) {
        requestBytes.rewind();
        int sentBytesCount = 0;
        try {
            sentBytesCount = channel.send(requestBytes, groupSocketAddress);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send a datagram.", e);
        }
        if (sentBytesCount != requestBytes.limit()) {
            throw new RuntimeException(String.format("Failed to send a datagram. Expecting to send %d bytes, but sent %d bytes.",
                    requestBytes.limit(), sentBytesCount));
        }
    }

    private static ChannelAndSelector openChannel(DiscoveryClientParams params) {
        DatagramChannel channel = null;
        Selector selector = null;
        try {
            channel = DatagramChannel.open(params.getProtocolFamily()).bind(null);
            channel.configureBlocking(false);

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            return new ChannelAndSelector(channel, selector);
        } catch (Exception e) {
            closeSafely(selector, LOG);
            closeSafely(channel, LOG);
            throw new RuntimeException("Cannot create DiscoveryClientBean", e);
        }
    }

    private static InetSocketAddress createGroupSocketAddress(DiscoveryClientParams params) {
        try {
            InetAddress groupInetAddr = InetAddress.getByName(params.getGroup());
            Validate.isTrue(groupInetAddr.isMulticastAddress(), "Not a multicast address: %s", params.getGroup());
            return new InetSocketAddress(groupInetAddr, params.getPort());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create DiscoveryClientBean", e);
        }
    }

    private static class ChannelAndSelector {
        final DatagramChannel channel;
        final Selector selector;

        ChannelAndSelector(DatagramChannel channel, Selector selector) {
            this.channel = channel;
            this.selector = selector;
        }
    }

    private static class CompletableUpdater implements UnaryOperator<CompletableFuture<Set<DiscoveredService>>> {

        private volatile CompletableFuture<Set<DiscoveredService>> created = null;

        @Override
        public CompletableFuture<Set<DiscoveredService>> apply(CompletableFuture<Set<DiscoveredService>> currentValue) {
            if (currentValue == null) {
                created = new CompletableFuture<>();
                return created;
            }
            return currentValue;
        }

        boolean hasCreatedNewCompletable() {
            return created != null;
        }
    }
}
