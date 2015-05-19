package net.beaconpe.bouncyball;

import net.beaconpe.bouncyball.network.PacketIntercepter;
import net.beaconpe.bouncyball.session.RemoteClientSession;
import net.beaconpe.bouncyball.session.RemoteServerSession;
import net.beaconpe.bouncyball.util.Worker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A Minecraft PE Server.
 */
public class MinecraftPEServer implements Runnable{
    public final HashMap<String, RemoteClientSession> clientSessions = new HashMap();
    public final HashMap<String, RemoteServerSession> serverSessions = new HashMap();
    private int port;
    private Logger logger;
    private DatagramSocket serverSocket;
    private boolean running = false;

    private SocketAddress mainServer;
    private boolean logPacketErrors;
    private boolean logChat;

    private PacketIntercepter packetIntercepter;

    private Yaml yml;
    private Map<String, Object> config;

    public MinecraftPEServer(int bindPort){
        this.port = bindPort;
        packetIntercepter = new PacketIntercepter(this);
        logger = LogManager.getLogger("BouncyBall");
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("MCPE-Proxy");

        try {
            checkConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("BouncyBall version "+BouncyBall.SOFTWARE_VERSION+", implementing MCPE "+BouncyBall.MCPE_VERSION+" protocol "+BouncyBall.MCPE_PROTOCOL);
        try {
            yml = new Yaml();
            config = (Map<String, Object>) yml.load(new FileReader("config.yml"));
            logPacketErrors = (boolean) config.get("logDecodeErrors");
            logChat = (boolean) config.get("logChat");
            mainServer = new InetSocketAddress((String) (config.get("remoteServer")), (Integer)(config.get("remoteServerPort")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("Logging Packet Decode errors to console: "+logPacketErrors);
        try{
            serverSocket = new DatagramSocket(port);
            logger.info("Server started on port "+port);
            while(running){
                serverSocket.setSoTimeout(2000);

                byte[] recvBuf = new byte[1024 * 1024];
                DatagramPacket dp = new DatagramPacket(recvBuf, recvBuf.length);
                try {
                    serverSocket.receive(dp);
                    dp.setData(Arrays.copyOf(recvBuf, dp.getLength()));
                    handlePacket(dp);
                } catch(SocketTimeoutException e){

                } catch (IOException e) {
                    logger.error("IOException: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch(SocketException e){
            logger.error("SocketException: "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkConfig() throws IOException {
        File configFile = new File("config.yml");
        if(!configFile.exists()){
            InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

            String line = "";
            while((line = reader.readLine()) != null){
                writer.write(line+"\n");
            }
            reader.close();
            writer.close();
        }
    }

    private void handlePacket(DatagramPacket dp){
        boolean isFound = false;
        for(String addr: clientSessions.keySet()){
            if(addr.equals(dp.getSocketAddress().toString())){
                isFound = true;
                new Worker(() -> {
                    clientSessions.get(addr).handlePacket(dp.getData());
                }, "HandlePacket").start();
                break;
            }
        }

        if(!isFound && (dp.getData()[0] == 0x05 || dp.getData()[0] == 0x01)){ //Make sure it is a MCPE client
            logger.debug("Accepting new client session from "+dp.getSocketAddress().toString());
            RemoteServerSession serverSession;

            serverSession = new RemoteServerSession(this, mainServer, dp.getSocketAddress());
            serverSession.setRunning(true);
            serverSession.start();

            RemoteClientSession client = new RemoteClientSession(this, dp.getSocketAddress(), serverSession);
            clientSessions.put(dp.getSocketAddress().toString(), client);
            serverSessions.put(mainServer.toString(), serverSession);

            new Worker(() -> {
                client.handlePacket(dp.getData());
            }, "HandlePacket").start();
        }
    }

    public void sendPacket(DatagramPacket dp) throws IOException {
        serverSocket.send(dp);
    }

    public Logger getLogger(){
        return logger;
    }

    public PacketIntercepter getPacketIntercepter(){
        return packetIntercepter;
    }

    public boolean logPacketErrors(){
        return logPacketErrors;
    }

    public boolean logChat(){
        return logChat;
    }
}
