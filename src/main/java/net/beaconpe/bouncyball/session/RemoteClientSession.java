package net.beaconpe.bouncyball.session;

import net.beaconpe.bouncyball.MinecraftPEServer;
import net.beaconpe.bouncyball.util.ProxyException;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.DatagramPacket;
import java.net.SocketAddress;

/**
 * Created by jython234 on 3/24/2015.
 */
public class RemoteClientSession implements Session{
    private MinecraftPEServer server;
    private SocketAddress address;
    private RemoteServerSession remoteServer;

    private String username;

    private boolean hasSpawned = false;

    public RemoteClientSession(MinecraftPEServer server, SocketAddress address, RemoteServerSession remoteServer){
        this.server = server;
        this.address = address;
        this.remoteServer = remoteServer;
    }

    @Override
    public void handlePacket(byte[] buffer) {
        //server.getLogger().debug("Forwarded packet "+buffer[0]+" to ");
        try {
            remoteServer.forwardToServer(buffer);
            server.getPacketIntercepter().interceptPacket(buffer, this, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forwardToClient(byte[] buffer){
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address);
        try {
            server.sendPacket(dp);
            server.getPacketIntercepter().interceptPacket(buffer, this, false);
            //server.getLogger().debug("Forwarded packet to client.");
        } catch (IOException e) {
            throw new ProxyException(e);
        }
    }

    @Override
    public SocketAddress getAddress() {
        return address;
    }

    public boolean hasSpawned(){
        return hasSpawned;
    }

    public RemoteServerSession getRemoteServer(){
        return remoteServer;
    }

    public String getUsername(){
        return username;
    }

    public void setSpawned(boolean spawned){
        hasSpawned = spawned;
    }

    public void setUsername(String username){
        this.username = username;
    }
}
