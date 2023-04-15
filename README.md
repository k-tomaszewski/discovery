# The "discovery" project
A simple service discovery solution based on IP multicast with a focus on a local network, IPv6 
and cooperation with the Spring Framework.

Current status: work in progress.

## Motives
I wanted a simple solution for service discovery in a local network that would work in a spirit 
of zero-configuration, without a dedicated server. IP multicast was a natural choice for a communication
channel. Moreover, I wanted to explore the domain of implementing IPv6 multicast in Java 11+, 
especially using NIO.

## Alternatives
### mdnsjava
https://posicks.github.io/mdnsjava/

Described as multicast DNS (mDNS) [RFC 6762] & DNS-Based Service Discovery (DNS-SD) [RFC 6763] in Java.
I've started reading about this solution before inventing my own. However, I found it: 
- complicated and hard to integrate on a service side;
- having bugs, including IPv6 support;
- dead, as the last commit was made 6 years ago (it's 2023 now). See: https://github.com/posicks/mdnsjava 

### Hola
https://github.com/fflewddur/hola

Described as a minimalist Java implementation of Multicast DNS Service Discovery (mDNS-SD). 
The purpose of Hola is to give Java developers a dead-simple API for finding Zeroconf-enabled 
services on a local network. It follows RFCs 6762 and 6763 and is compatible with Apple's Bonjour 
mDNS-SD implementation.

All this sounds promising, but Hola is a work-in-progress and the last commit was 5 years ago (in 2023).

### CU-Zeroconf
https://github.com/faceless2/cu-zeroconf

Looks like a response to some problems of mdnsjava. On the other hand the author claims his implementation
is not correct as well and the last commit was 6 years ago (as of 2023).
