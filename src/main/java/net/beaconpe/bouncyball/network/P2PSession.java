package net.beaconpe.bouncyball.network;

import net.beaconpe.bouncyball.MinecraftPEProxy;
import org.blockserver.io.BinaryReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Represents a P2P Session
 */
public class P2PSession {
    private P2PConnectionHandler handler;
    private MinecraftPEProxy proxy;

    private P2PSession(){}

    public static P2PSession openSession(P2PConnectionHandler handler, byte[] packet) throws IOException {
        P2PSession session = new P2PSession();
        session.handler = handler;
        session.proxy = handler.getManager().getProxy();

        session.handlePacket(packet);

        return session;
    }

    public void handlePacket(byte[] buffer) throws IOException {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(buffer));
        byte pid = reader.readByte();
        switch(pid){

            default:
                proxy.getLogger().debug("Got unknown PID: "+pid);
                break;

        }
    }
}
