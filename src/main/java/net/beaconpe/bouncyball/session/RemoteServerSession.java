package net.beaconpe.bouncyball.session;

import net.beaconpe.bouncyball.MinecraftPEServer;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by jython234 on 3/24/2015.
 */
public class RemoteServerSession extends Thread implements Session{
    private SocketAddress serverAddress;
    private MinecraftPEServer server;
    private SocketAddress clientAddr;

    private DatagramSocket socket;
    private boolean running = false;

    private int timeoutTimes = 0;

    public RemoteServerSession(MinecraftPEServer server, SocketAddress serverAddress, SocketAddress clientAddr){
        this.server = server;
        this.clientAddr = clientAddr;
        this.serverAddress = serverAddress;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void handlePacket(byte[] buffer) {
        //server.getLogger().info("Got a packet!");
        timeoutTimes = 0;
        if(server.clientSessions.containsKey(clientAddr.toString())){
            server.clientSessions.get(clientAddr.toString()).forwardToClient(buffer);
        }
    }

    @Override
    public SocketAddress getAddress() {
        return serverAddress;
    }

    @Override
    public void run(){
        setName("RemoteServer-"+clientAddr.toString());
        try {
            int port = Integer.parseInt(clientAddr.toString().split(":")[1]);
            socket = new DatagramSocket(port);
            server.getLogger().debug("Opened connection to "+serverAddress.toString());
            while (running && (!isInterrupted())) {
                if(timeoutTimes >= 5){
                    break;
                }
                socket.setSoTimeout(2000);

                byte[] recvBuf = new byte[1024 * 1024];
                DatagramPacket dp = new DatagramPacket(recvBuf, recvBuf.length);
                try {
                    socket.receive(dp);
                    dp.setData(Arrays.copyOf(recvBuf, dp.getLength()));
                    handlePacket(dp.getData());
                } catch (SocketTimeoutException e) {
                    timeoutTimes++;
                } catch (IOException e) {
                    server.getLogger().error("IOException: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            server.getLogger().debug("Closed connection to "+serverAddress.toString());
        } catch(SocketException e) {
            server.getLogger().error("SocketException: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void forwardToServer(byte[] buffer) throws IOException {
        //server.getLogger().debug("Sent to server.");
        if(socket != null) {
            socket.send(new DatagramPacket(buffer, buffer.length, serverAddress));
        }
    }
}
