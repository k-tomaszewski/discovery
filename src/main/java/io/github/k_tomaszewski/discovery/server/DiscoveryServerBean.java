package io.github.k_tomaszewski.discovery.server;

import io.github.k_tomaszewski.discovery.EndpointInfoUtils;
import io.github.k_tomaszewski.discovery.JsonSerialization;
import io.github.k_tomaszewski.discovery.model.DiscoveryRequest;
import io.github.k_tomaszewski.discovery.model.DiscoveryResponse;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * The class should be instantiated as a singleton. Thread safe.
 */
public class DiscoveryServerBean implements InitializingBean, DisposableBean {
	
	private final AtomicReference<Thread> threadAtomicRef = new AtomicReference<>();
	private final String appName;
	private final DiscoveryServerParams params;
	private final ConcurrentMap<Integer, Set<String>> ports = new ConcurrentHashMap<>();
	private final String hostname;
	private final JsonSerialization packageFormat;
	private final ServerThreadFactory threadFactory;

	public DiscoveryServerBean(String appName, DiscoveryServerParams params, JsonSerialization packageFormat,
			Supplier<String> hostnameSupplier, ServerThreadFactory threadFactory) {
		this.appName = appName;
		this.params = params;
		hostname = (hostnameSupplier != null) ? hostnameSupplier.get() : null;
		this.packageFormat = packageFormat;
		this.threadFactory = threadFactory;
	}

	public void registerHttpEndpoint(int port, String method, String path) {
		register(port, EndpointInfoUtils.httpEndpoint(method, path));
	}

	public boolean start() {
		if (threadAtomicRef.get() == null) {
			if (threadAtomicRef.compareAndSet(null, threadFactory.create(params, this::processRequestPacket))) {
				threadAtomicRef.get().start();
				return true;
			}
		}
		return false;
	}
	
	public boolean stop() {
		var thread = threadAtomicRef.get();
		if (thread != null) {
			if (threadAtomicRef.compareAndSet(thread, null)) {
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void afterPropertiesSet() {
		start();
	}

	@Override
	public void destroy() {
		stop();
	}

	private void register(int port, String endpointInfo) {
		ports.compute(port, (unused, endpoints) -> {
			if (endpoints == null) {
				endpoints = ConcurrentHashMap.newKeySet();
			}
			endpoints.add(endpointInfo);
			return endpoints;
		});
	}

	private ByteBuffer processRequestPacket(SocketAddress srcSocketAddr, ByteBuffer requestPacket) {
		var request = packageFormat.deserialize(requestPacket, DiscoveryRequest.class);
		var response = new DiscoveryResponse(request.getCid(), appName, ports, hostname);
		return packageFormat.serialize(response);
	}
}
