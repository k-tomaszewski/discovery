package io.github.k_tomaszewski.discovery.server;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * To stop the thread invoke interrupt() method.
 */
class DefaultServerThread extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultServerThread.class);

	private final DatagramChannel channel;
	private final ByteBuffer buffer;
	private final BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetProcessor;

	public DefaultServerThread(DiscoveryServerParams params, BiFunction<SocketAddress, ByteBuffer, ByteBuffer> packetProcessor) {
		channel = openChannel(params);
		buffer = ByteBuffer.allocate(params.getMaxPacketBytes());
		this.packetProcessor = packetProcessor;
	}

	@Override
	public void run() {
		LOG.info("Started.");
		try {
			do {
				SocketAddress sourceAddress = receivePacket();
				if (sourceAddress != null) {
					processPacketFrom(sourceAddress);
				}
			} while (channel.isOpen());
		} finally {
			closeSafely(channel);
			LOG.info("Ended.");
		}
	}

	private void processPacketFrom(SocketAddress sourceAddress) {
		buffer.flip();
		try {
			ByteBuffer responseBytes = packetProcessor.apply(sourceAddress, buffer);
			if (responseBytes != null) {
				int bytesSent = channel.send(responseBytes, sourceAddress);
				if (bytesSent != responseBytes.limit()) {
					LOG.warn("Sent bytes ({}) not equal to datagram size ({}).", bytesSent, responseBytes.limit());
				}
			}
		} catch (Throwable t) {
			LOG.error("Error occurred when processing UDP packet from {}", sourceAddress, t);
		}
	}

	private SocketAddress receivePacket() {
		try {
			buffer.clear();
			return channel.receive(buffer);
		} catch (ClosedChannelException e) {
			LOG.info("Channel is closed.");
		} catch (IOException e) {
			LOG.error("I/O error occurred when receiving UDP packet", e);
		}
		return null;
	}

	private static DatagramChannel openChannel(DiscoveryServerParams params) {
		DatagramChannel channel = null;
		try {
			final InetAddress groupInetAddr = InetAddress.getByName(params.getGroup());
			Validate.isTrue(groupInetAddr.isMulticastAddress(), "Not a multicast address: %s", params.getGroup());

			channel = DatagramChannel.open(params.getProtocolFamily());
			channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			channel.bind(new InetSocketAddress(params.getPort()));

			final DatagramChannel theChannel = channel;
			final var membershipKeys = new HashSet<MembershipKey>();
			NetworkInterface.networkInterfaces()
					.map(ni -> joinMulticastGroupOnInterface(theChannel, groupInetAddr, ni))
					.filter(Objects::nonNull)
					.forEach(membershipKeys::add);

			if (membershipKeys.isEmpty()) {
				LOG.warn("Multicast group {} not joined.", params.getGroup());
			} else {
				LOG.info("Multicast group {} joined. Membership keys: {}", params.getGroup(), membershipKeys.size());
			}

			return channel;
		} catch (Exception e) {
			closeSafely(channel);
			throw new RuntimeException("Cannot open datagram channel", e);
		}
	}

	private static MembershipKey joinMulticastGroupOnInterface(DatagramChannel channel, InetAddress group, NetworkInterface ni) {
		try {
			MembershipKey key = channel.join(group, ni);
			LOG.info("Joined multicast group on interface {}", ni.getName());
			return key;
		} catch (IOException e) {
			LOG.warn("Cannot join multicast group {} on interface {}", group, ni.getName(), e);
			return null;
		}
	}

	private static void closeSafely(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (IOException e) {
				LOG.warn("Error occurred when closing {}: {}", obj.getClass().getName(), e.getMessage());
			}
		}
	}
}
