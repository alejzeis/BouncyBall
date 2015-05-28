package net.beaconpe.bouncyball.network;

import net.beaconpe.bouncyball.MinecraftPEProxy;
import net.beaconpe.bouncyball.server.RegisteredServer;
import net.beaconpe.bouncyball.utility.io.BouncyReader;
import static net.beaconpe.bouncyball.network.protocol.BouncyP2P.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a P2P Session
 */
public class P2PSession {
    private P2PConnectionHandler handler;
    private MinecraftPEProxy proxy;
    private RegisteredServer server;

    private P2PSession(){}

    public static P2PSession openSession(P2PConnectionHandler handler, byte[] packet) throws IOException {
        P2PSession session = new P2PSession();
        session.handler = handler;
        session.proxy = handler.getManager().getProxy();

        session.handlePacket(packet);

        return session;
    }

    public void handlePacket(byte[] buffer) throws IOException {
        System.out.println(Arrays.toString(buffer));
        BouncyReader reader = new BouncyReader(new ByteArrayInputStream(buffer));
        reader.readByte(); //Random thing
        int pid = reader.readVarInt();

        switch(pid){
            case PacketID.SERVER_REGISTRATION_REQUEST_VALUE:
                proxy.getLogger().debug("Got a server registration request!");
                ServerRegistrationRequest request = ServerRegistrationRequest.parseFrom(buffer);
                processRequest(request);
                break;

            case PacketID.CLOSE_CONNECTION_VALUE:
                proxy.getLogger().debug("Client closed connection. (From: "+handler.getRemoteAddress().toString()+")");
                if(proxy.getServerManager().isRegistered(this)){
                    proxy.getServerManager().removeServer(this);
                }

                break;
            default:
                proxy.getLogger().debug("Got unknown PID: "+pid);
                break;

        }
    }

    public P2PConnectionHandler getHandler(){
        return handler;
    }

    private void processRequest(ServerRegistrationRequest request) throws IOException {
        if(request.getProxyID() == proxy.getProxyID()){
            server = new RegisteredServer(request.getServerName(), this);
            proxy.getServerManager().registerServer(this, request);

            ServerRegistrationAccepted.Builder builder = ServerRegistrationAccepted.newBuilder();
            builder.setPacketID(PacketID.SERVER_REGISTRATION_ACCEPTED);

            ServerRegistrationAccepted accepted = builder.build();
            handler.sendPacket(accepted.toByteArray());
        } else {
            ServerRegistrationDenied.Builder builder = ServerRegistrationDenied.newBuilder();
            builder.setPacketID(PacketID.SERVER_REGISTRATION_DENIED);
            builder.setReason(ServerRegistrationDenied.Reason.PROXY_ID_INVALID);

            ServerRegistrationDenied denied = builder.build();
            handler.sendPacket(denied.toByteArray());
        }
    }
}
