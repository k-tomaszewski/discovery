# The "discovery" project
Simple service discovery solution based on IP multicast with a focus on a local network, IPv6 
and cooperation with Spring Framework.

## Motives
I wanted a simple solution for service discovery in a local network that would work in spirit 
of zero-configuration, without a dedicated server. IP multicast was a natural choice for a communication
channel. Moreover, I wanted to explore the domain of implementing IPv6 multicast in Java 11+, 
especially using NIO.

## Alternatives
### mdnsjava
https://posicks.github.io/mdnsjava/

This is multicast DNS (mDNS) [RFC 6762] & DNS-Based Service Discovery (DNS-SD) [RFC 6763] in Java.
I've started reading about this solution before inventing my own. However, I found it: 
- complicated and hard to integrate on a service side;
- having bugs, including IPv6 support;
- dead, as the last commit was made 6 years ago (it's 2023 now). See: https://github.com/posicks/mdnsjava 