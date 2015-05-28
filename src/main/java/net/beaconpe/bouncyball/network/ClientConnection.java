package net.beaconpe.bouncyball.network;

import net.beaconpe.bouncyball.MinecraftPEProxy;
import net.beaconpe.bouncyball.config.LinkedServer;
import net.beaconpe.bouncyball.utility.BouncyThread;
import net.beaconpe.bouncyball.utility.clock.CallableTask;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Represents a connection from client -> proxy -> server.
 * Also is a thread that handles packets from the server.
 */
public class ClientConnection extends BouncyThread{
    private MinecraftPEProxy proxy;
    private InetSocketAddress clientAddress;
    private LinkedServer server;

    private DatagramSocket socket;

    private boolean connected = true;

    private int packetCheckTaskId;

    private long client_lastPacketSentAt;
    private long server_lastPacketSentAt;

    public ClientConnection(MinecraftPEProxy proxy, InetSocketAddress clientAddress, LinkedServer server){
        this.proxy = proxy;
        this.clientAddress = clientAddress;
        this.server = server;

        try {
            packetCheckTaskId = proxy.getClock().registerTask(new CallableTask("checkPacketTimes", this, 40)); //Check packet times every 2 seconds
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        addStartupTask(() -> {
            client_lastPacketSentAt = System.currentTimeMillis();
            server_lastPacketSentAt = System.currentTimeMillis();
            try {
                socket = new DatagramSocket(clientAddress.getPort());
            } catch (SocketException e) {
                e.printStackTrace();
            }
        });

        addShutdownTask(() -> {
            proxy.getClock().cancelTask(packetCheckTaskId);
            socket.close();
        });
    }

    public boolean checkPacketTimes() throws InterruptedException {
        synchronized ((Long) server_lastPacketSentAt){
            if((System.currentTimeMillis() - server_lastPacketSentAt) >= 10000 && connected){ //10 seconds
                //Assume they have disconnected
                proxy.getLogger().debug("Session "+clientAddress.toString()+" to "+server.getAddress().toString()+" has disconnected: packet timeout.");
                connected = false;

                shutdown();
                return true;
            }
        }
        synchronized ((Long) client_lastPacketSentAt){
            if((System.currentTimeMillis() - client_lastPacketSentAt) >= 10000 && connected){ //10 seconds
                //Assume they have disconnected
                proxy.getLogger().debug("Session "+clientAddress.toString()+" to "+server.getAddress().toString()+" has disconnected: packet timeout.");
                connected = false;

                shutdown();
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        setName("ClientConnection-"+clientAddress.toString());

        try {
            socket.setSoTimeout(2000);
            while(isRunning()){
                byte[] buffer = new byte[2048];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

                try{
                    socket.receive(dp);
                    dp.setData(Arrays.copyOf(dp.getData(), dp.getLength()));
                    synchronized ((Long) server_lastPacketSentAt) {
                        server_lastPacketSentAt = System.currentTimeMillis();
                    }
                    forwardToClient(dp.getData());
                } catch(SocketTimeoutException e){

                } catch(SocketException e){
                    if(!e.getMessage().contains("socket closed")){
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void forwardToClient(byte[] buffer) throws IOException {
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, clientAddress);
        proxy.sendDatagramPacket(dp);
    }

    public void forwardToServer(byte[] buffer) throws IOException {
        synchronized ((Long) client_lastPacketSentAt) {
            client_lastPacketSentAt = System.currentTimeMillis();
        }
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, server.getAddress());
        socket.send(dp);
    }

    public InetSocketAddress getClientAddress(){
        return clientAddress;
    }

    public LinkedServer getServer(){
        return server;
    }
}
