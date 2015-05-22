package net.beaconpe.bouncyball;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Represents a frontend, MCPE proxy.
 */
public class MinecraftPEProxy extends Thread{
    private boolean running;
    private P2PManager p2PManager;

    private DatagramSocket socket;
    private Logger logger;

    public MinecraftPEProxy(){
        logger = LogManager.getLogger("BouncyBall");
        p2PManager = new P2PManager(this);
    }

    public final void startup(){
        if(!running){
            running = true;
            start();
        } else {
            throw new RuntimeException("Can not start proxy: already running.");
        }
    }

    public final void runInCurrentThread(){
        if(!running){
            running = true;
            run();
        } else {
            throw new RuntimeException("Can not start proxy: already running.");
        }
    }

    public final void shutdown() throws InterruptedException {
        if(running){
            running = false;
            p2PManager.shutdown();
            join();
        } else {
            throw new RuntimeException("Can not stop proxy: not running.");
        }
    }

    @Override
    public void run(){
        Thread.currentThread().setName("MCPE-Proxy");
        logger.info("Starting Minecraft PE Proxy...");
        p2PManager.startup();

        try {
            socket = new DatagramSocket(19132);
            while(running){
                socket.setSoTimeout(2000);

                byte[] buffer = new byte[2048];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(dp);
                    handlePacket(dp);
                } catch(SocketTimeoutException e){

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(DatagramPacket dp){

    }

    public Logger getLogger(){
        return logger;
    }
}
