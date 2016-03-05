package io.github.jython234.BouncyBall.network;

import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Represents an external Server that a client is connected to.
 */
public class Server {
    @Getter private InetSocketAddress address;
}
