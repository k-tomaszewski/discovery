package io.github.k_tomaszewski.discovery.server;

import io.github.k_tomaszewski.discovery.DiscoveryParams;

public class DiscoveryServerParams extends DiscoveryParams {

	/**
	 * Limit on request size.
	 */
	private int maxPacketBytes = 1024;


	public int getMaxPacketBytes() {
		return maxPacketBytes;
	}

	public void setMaxPacketBytes(int maxPacketBytes) {
		this.maxPacketBytes = maxPacketBytes;
	}
}
