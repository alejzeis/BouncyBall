package net.beaconpe.bouncyball.network;

import net.beaconpe.bouncyball.P2PManager;
import org.blockserver.io.BinaryReader;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A Thread that handles P2P connections.
 */
public class P2PConnectionHandler extends Thread{

    private P2PManager manager;
    private boolean isLocal;

    private Socket connection;
    private P2PSession session;

    private DataInputStream in;
    private DataOutputStream out;

    private boolean running;

    /**
     * Construct a new connection handler.
     * @param manager The P2P Manager
     * @param socket The connection to the peer.
     * @param isLocal If this connection was opened to a peer.
     */
    public P2PConnectionHandler(P2PManager manager, Socket socket, boolean isLocal){
        this.manager = manager;
        this.isLocal = isLocal;
        connection = socket;
    }

    public final void startup(){
        if(!running){
            running = true;
            start();
        } else {
            throw new RuntimeException("Can not start p2p manager: already running.");
        }
    }

    public final void shutdown() throws InterruptedException {
        if(running){
            running = false;
            join();
        } else {
            throw new RuntimeException("Can not stop p2p manager: not running.");
        }
    }

    @Override
    public void run(){
        try {
            in = new DataInputStream(connection.getInputStream());
            out = new DataOutputStream(connection.getOutputStream());
            while(running){
                int packetLen = in.readInt();
                byte[] packetBuffer = new byte[packetLen];
                in.read(packetBuffer);
                handlePacket(packetBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public P2PManager getManager(){
        return manager;
    }

    private void handlePacket(byte[] packetBuffer) throws IOException {
        if(session != null){
            session.handlePacket(packetBuffer);
        } else {
            session = P2PSession.openSession(this, packetBuffer);
        }
    }
}
