package net.beaconpe.bouncyball.session;

import java.net.SocketAddress;

/**
 * Created by jython234 on 3/24/2015.
 */
public interface Session {
    void handlePacket(byte[] buffer);
    SocketAddress getAddress();
}
