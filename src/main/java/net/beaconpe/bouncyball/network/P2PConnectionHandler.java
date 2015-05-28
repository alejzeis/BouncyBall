package net.beaconpe.bouncyball.network;

import net.beaconpe.bouncyball.BouncyBall;
import net.beaconpe.bouncyball.P2PManager;
import net.beaconpe.bouncyball.utility.BouncyThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * A Thread that handles P2P connections.
 */
public class P2PConnectionHandler extends BouncyThread {

    private P2PManager manager;
    private boolean isLocal;

    private Socket connection;
    private P2PSession session;

    private DataInputStream in;
    private DataOutputStream out;

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

        addShutdownTask(() -> {
            if(!connection.isClosed()){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run(){
        try {
            in = new DataInputStream(connection.getInputStream());
            out = new DataOutputStream(connection.getOutputStream());
            while(isRunning()){
                BouncyBall.PROXY_INSTANCE.getLogger().debug("Waiting for data...");
                int packetLen = in.readInt();
                byte[] packetBuffer = new byte[packetLen];
                in.read(packetBuffer);
                handlePacket(packetBuffer);
            }
        } catch(EOFException e){
            //Connection must of had closed.
            BouncyBall.PROXY_INSTANCE.getLogger().debug("Connection closed unexpectedly.");
            if(BouncyBall.PROXY_INSTANCE.getServerManager().isRegistered(session)){
                BouncyBall.PROXY_INSTANCE.getServerManager().removeServer(session);
            }
            try {
                shutdown();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public P2PManager getManager(){
        return manager;
    }

    public InetSocketAddress getRemoteAddress(){
        return new InetSocketAddress(connection.getInetAddress(), connection.getPort());
    }

    private void handlePacket(byte[] packetBuffer) throws IOException {
        if(session != null){
            session.handlePacket(packetBuffer);
        } else {
            session = P2PSession.openSession(this, packetBuffer);
        }
    }

    public void sendPacket(byte[] internalBuffer) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(internalBuffer.length + 4);
        buffer.putInt(internalBuffer.length);
        buffer.put(internalBuffer);

        out.write(buffer.array());
        out.flush();
    }
}
