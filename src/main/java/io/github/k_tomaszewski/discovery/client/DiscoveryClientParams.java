package io.github.k_tomaszewski.discovery.client;

import io.github.k_tomaszewski.discovery.DiscoveryParams;

import java.time.Duration;

public class DiscoveryClientParams extends DiscoveryParams {

     private Duration timeout;

     /**
      * Limit on response size.
      */
     private int maxPacketBytes = 4096;


     public Duration getTimeout() {
          return timeout;
     }

     public void setTimeout(Duration timeout) {
          this.timeout = timeout;
     }

     public int getMaxPacketBytes() {
          return maxPacketBytes;
     }

     public void setMaxPacketBytes(int maxPacketBytes) {
          this.maxPacketBytes = maxPacketBytes;
     }
}
