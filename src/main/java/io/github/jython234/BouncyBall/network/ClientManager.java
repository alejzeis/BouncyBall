package io.github.jython234.BouncyBall.network;

import io.github.jython234.BouncyBall.BouncyBallProxy;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles clients connected to the external servers.
 */
public class ClientManager {
    @Getter private final BouncyBallProxy proxy;
    private Map<Integer, Client> clients = new HashMap<>();

    public ClientManager(BouncyBallProxy proxy) {
        this.proxy = proxy;
    }

    public void update() {

    }
}
