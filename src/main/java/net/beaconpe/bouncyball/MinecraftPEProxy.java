package net.beaconpe.bouncyball;

import net.beaconpe.bouncyball.config.BouncyConfig;
import net.beaconpe.bouncyball.network.ClientConnection;
import net.beaconpe.bouncyball.server.ServerManager;
import net.beaconpe.bouncyball.utility.BouncyThread;
import net.beaconpe.bouncyball.utility.ConsoleHandler;
import net.beaconpe.bouncyball.utility.clock.BouncyClock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a frontend, MCPE proxy.
 */
public class MinecraftPEProxy extends BouncyThread{
    private P2PManager p2PManager;
    private ConsoleHandler consoleHandler;
    private BouncyClock clock;
    private ArrayList<ClientConnection> connections = new ArrayList<>();

    private BouncyConfig config;
    private ServerManager serverManager;

    private DatagramSocket socket;
    private Logger logger;

    public MinecraftPEProxy(){
        logger = LogManager.getLogger("BouncyBall");
        addShutdownTask(() -> {
            try {
                logger.info("Shutting down BouncyBall...");
                clock.shutdown();
                p2PManager.shutdown();
                consoleHandler.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        clock = new BouncyClock(this, 20);
        p2PManager = new P2PManager(this);
        consoleHandler = new ConsoleHandler(this);
        serverManager = new ServerManager(this);
    }

    @Override
    public void run(){
        Thread.currentThread().setName("MCPE-Proxy");
        logger.info("Starting Minecraft PE Proxy...");
        logger.info("Loading configuration...");

        try {
            config = new BouncyConfig(new File("config.yml"));

            clock.startup();
            consoleHandler.startup();
            p2PManager.startup();

            socket = new DatagramSocket(19132);
            logger.info("Started frontend proxy on port 19132.");
            while(isRunning()){
                socket.setSoTimeout(2000);

                byte[] buffer = new byte[2048];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(dp);
                    dp.setData(Arrays.copyOf(dp.getData(), dp.getLength()));
                    handlePacket(dp);
                } catch(SocketTimeoutException e){

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private boolean handlePacket(DatagramPacket dp) throws IOException {
        for(ClientConnection connection: connections){
            if(connection.getClientAddress().toString().equals(new InetSocketAddress(dp.getAddress(), dp.getPort()).toString())){
                connection.forwardToServer(dp.getData());
                return true;
            }
        }
        logger.debug("Accepting new connection from "+dp.getSocketAddress().toString());
        ClientConnection newConnection = new ClientConnection(this, new InetSocketAddress(dp.getAddress(), dp.getPort()), config.getHubServer());
        newConnection.startup();
        newConnection.forwardToServer(dp.getData());
        connections.add(newConnection);
        return true;
    }

    public void sendDatagramPacket(DatagramPacket dp) throws IOException {
        socket.send(dp);
    }

    public void removeConnection(ClientConnection connection){
        connections.remove(connection);
    }

    public Logger getLogger(){
        return logger;
    }

    public BouncyClock getClock() {
        return clock;
    }

    public long getProxyID(){
        return config.getProxyID();
    }

    public ServerManager getServerManager() {
        return serverManager;
    }
}
