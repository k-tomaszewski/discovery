package io.github.k_tomaszewski.discovery.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class DefaultServerThreadTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServerThreadTest.class);

    @Test
    public void shouldStartAndStop() throws InterruptedException {
        // given
        BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetConsumerMock = Mockito.mock(BiFunction.class);
        DefaultServerThread serverThread = new DefaultServerThread(new DiscoveryServerParams(), packetConsumerMock);

        // when
        serverThread.start();
        Thread.sleep(200);
        LOG.info("Interrupting the server thread...");
        serverThread.interrupt();

        // then
        serverThread.join();
        LOG.info("Thread interrupted");
    }

    @Test
    public void shouldProcessPacketReceivedFromMulticast() throws IOException, InterruptedException {
        // given
        final String someText = "test-" + System.currentTimeMillis();
        var params = new DiscoveryServerParams();

        BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetConsumerMock = Mockito.mock(BiFunction.class);
        Mockito.doAnswer(invocationOnMock -> {
            String receivedText = StandardCharsets.UTF_8.decode(invocationOnMock.getArgument(1, ByteBuffer.class)).toString();
            LOG.info("Mock consumer accepted '{}'", receivedText);
            Assertions.assertEquals(someText, receivedText);
            return null;
        }).when(packetConsumerMock).apply(Mockito.any(), Mockito.any());

        DefaultServerThread serverThread = new DefaultServerThread(params, packetConsumerMock);

        // when
        serverThread.start();

        DatagramSocket socket = new DatagramSocket();   // binding to any port
        byte[] packetData = someText.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getByName(params.getGroup()), params.getPort());
        socket.send(packet);
        LOG.info("Packet sent to the multicast group");

        // then
        Thread.sleep(200);
        LOG.info("Interrupting the server thread...");
        serverThread.interrupt();
        serverThread.join();
        LOG.info("Thread interrupted");

        Mockito.verify(packetConsumerMock).apply(Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldProcessPacketReceivedFromMulticastOnAllServers() throws IOException, InterruptedException {
        // given
        final String someText = "test-" + System.currentTimeMillis();
        var params = new DiscoveryServerParams();
        var received = new ConcurrentHashMap<String, String>();

        BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetConsumerMock1 = Mockito.mock(BiFunction.class);
        Mockito.doAnswer(invocationOnMock -> {
            LOG.info("Message received from {}", invocationOnMock.getArgument(0, SocketAddress.class));
            received.put("1", StandardCharsets.UTF_8.decode(invocationOnMock.getArgument(1, ByteBuffer.class)).toString());
            return null;
        }).when(packetConsumerMock1).apply(Mockito.any(), Mockito.any());
        DefaultServerThread serverThread1 = new DefaultServerThread(params, packetConsumerMock1);

        BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetConsumerMock2 = Mockito.mock(BiFunction.class);
        Mockito.doAnswer(invocationOnMock -> {
            LOG.info("Message received from {}", invocationOnMock.getArgument(0, SocketAddress.class));
            received.put("2", StandardCharsets.UTF_8.decode(invocationOnMock.getArgument(1, ByteBuffer.class)).toString());
            return null;
        }).when(packetConsumerMock2).apply(Mockito.any(), Mockito.any());
        DefaultServerThread serverThread2 = new DefaultServerThread(params, packetConsumerMock2);

        // when
        serverThread1.start();
        serverThread2.start();

        DatagramSocket socket = new DatagramSocket();   // binding to any port
        byte[] packetData = someText.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(packetData, packetData.length, InetAddress.getByName(params.getGroup()), params.getPort());
        socket.send(packet);
        LOG.info("Packet sent to the multicast group");

        // then
        Thread.sleep(200);
        LOG.info("Interrupting the server threads...");
        serverThread1.interrupt();
        serverThread2.interrupt();
        serverThread1.join();
        serverThread2.join();
        LOG.info("Threads interrupted.");

        Assertions.assertEquals(Map.of("1", someText, "2", someText), received);
    }
}
