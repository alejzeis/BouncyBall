package net.beaconpe.bouncyball.session;

import net.beaconpe.bouncyball.MinecraftPEServer;
import net.beaconpe.bouncyball.network.packet.CustomPacket;
import net.beaconpe.bouncyball.network.packet.DataPacket;
import net.beaconpe.bouncyball.network.packet.MessagePacket;
import net.beaconpe.bouncyball.util.LoginData;
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

    private LoginData loginData;

    private int currentMessageIndex = 0;
    private boolean reliabilityLock = false;
    private int currentSeqNum = 0;

    private long lastMessageSentAt = -1;

    private boolean hasSpawned = false;

    public RemoteClientSession(MinecraftPEServer server, SocketAddress address, RemoteServerSession remoteServer){
        this.server = server;
        this.address = address;
        this.remoteServer = remoteServer;
    }

    public synchronized void sendDataPacket(DataPacket dp) throws IOException {
        reliabilityLock = true;

        currentSeqNum = currentSeqNum + 1;
        currentMessageIndex = currentMessageIndex + 1;

        CustomPacket cp = new CustomPacket();
        cp.seqNumber = currentSeqNum;
        CustomPacket.EncapsulatedPacket ep = new CustomPacket.EncapsulatedPacket();
        ep.hasSplit = false;
        ep.reliability = 2;
        ep.messageIndex = currentMessageIndex;
        ep.buffer = dp.encode();

        cp.packets.add(ep);

        forwardNoCheck(cp.toBytes());

        CustomPacket cp2 = new CustomPacket();
        cp.seqNumber = currentSeqNum;
        remoteServer.forwardToServer(cp2.toBytes());

        reliabilityLock = false;
    }

    @Override
    public void handlePacket(byte[] buffer) {
        //server.getLogger().debug("Forwarded packet "+buffer[0]+" to ");
        try {
            server.getPacketIntercepter().interceptPacket(buffer, this, true);
            remoteServer.forwardToServer(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forwardToClient(byte[] buffer){
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address);
        try {
            server.getPacketIntercepter().interceptPacket(buffer, this, false);
            server.sendPacket(dp);
            //server.getLogger().debug("Forwarded packet to client.");
        } catch (IOException e) {
            throw new ProxyException(e);
        }
    }

    protected void forwardNoCheck(byte[] buffer){
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address);
        try {
            server.sendPacket(dp);
        } catch (IOException e) {
            throw new ProxyException(e);
        }
    }

    public void sendMessage(String message){
        MessagePacket mp = new MessagePacket();
        mp.message = message;
        try {
            sendDataPacket(mp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lastMessageSentAt = System.currentTimeMillis();
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
        if(loginData != null) {
            return loginData.username;
        } else {
            return "null";
        }
    }

    public void setSpawned(boolean spawned){
        hasSpawned = spawned;
    }

    public void setMessageIndex(int messageIndex){
        if(!reliabilityLock) {
            currentMessageIndex = messageIndex;
        }
    }

    public void setCurrentSeqNum(int seqNum){
        if(! reliabilityLock) {
            currentSeqNum = seqNum;
        }
    }

    public synchronized void setLoginData(LoginData ld){
        this.loginData = ld;
    }

    public LoginData getLoginData(){
        return loginData;
    }
}
