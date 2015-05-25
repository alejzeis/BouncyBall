package net.beaconpe.bouncyball.config;

import java.net.InetSocketAddress;

/**
 * Simple class to represent a section of the configuration for a server.
 */
public class LinkedServer {
    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
}
