package net.beaconpe.bouncyball.server;

import net.beaconpe.bouncyball.network.P2PSession;

/**
 * Represents a Registered Server.
 */
public class RegisteredServer {
    private String serverName;
    private P2PSession session;

    public RegisteredServer(String serverName, P2PSession session){
        this.serverName = serverName;
        this.session = session;
    }

    public P2PSession getSession() {
        return session;
    }

    public String getServerName() {
        return serverName;
    }
}
