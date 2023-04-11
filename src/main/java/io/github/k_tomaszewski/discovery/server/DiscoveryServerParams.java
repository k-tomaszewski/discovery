package io.github.k_tomaszewski.discovery.server;

import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;

public class DiscoveryServerParams {

	private int port = 1981;
	private String group = "ff18::8080:8080";
	private ProtocolFamily protocolFamily = StandardProtocolFamily.INET6;

	/**
	 * Limit on request size.
	 */
	private int maxPacketBytes = 1024;
	
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public ProtocolFamily getProtocolFamily() {
		return protocolFamily;
	}

	public void setProtocolFamily(ProtocolFamily protocolFamily) {
		this.protocolFamily = protocolFamily;
	}

	public int getMaxPacketBytes() {
		return maxPacketBytes;
	}

	public void setMaxPacketBytes(int maxPacketBytes) {
		this.maxPacketBytes = maxPacketBytes;
	}
}
