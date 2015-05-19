package net.beaconpe.bouncyball.network;

import net.beaconpe.bouncyball.MinecraftPEServer;
import net.beaconpe.bouncyball.network.packet.CustomPacket;
import net.beaconpe.bouncyball.network.packet.LoginPacket;
import net.beaconpe.bouncyball.network.packet.MessagePacket;
import net.beaconpe.bouncyball.session.RemoteClientSession;
import net.beaconpe.bouncyball.util.LoginData;
import net.beaconpe.bouncyball.util.ProxyException;
import net.beaconpe.bouncyball.util.Util;

import static net.beaconpe.bouncyball.network.PacketIDs.*;

import org.blockserver.io.BinaryReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 */
public class PacketIntercepter {
    private MinecraftPEServer server;

    public PacketIntercepter(MinecraftPEServer server){
        this.server = server;
    }

    public void interceptPacket(byte[] buffer, RemoteClientSession session, boolean toServer){
        CustomPacket cp = null;
        try {
            ByteBuffer bb = ByteBuffer.wrap(buffer);
            byte pid = bb.get();

            //server.getLogger().debug("Intercepting packet "+ Util.toHex(pid)+" (ToServer: "+Boolean.toString(toServer)+")");

            if(pid <= RAKNET_CUSTOM_PACKET_MAX && pid >= RAKNET_CUSTOM_PACKET_MIN){ //Custom Packet
                cp = new CustomPacket(bb);
                session.setCurrentSeqNum(cp.seqNumber);
                for(CustomPacket.EncapsulatedPacket ep: cp.packets){
                    if(ep.messageIndex != -1){
                        if(!toServer) {
                            session.setMessageIndex(ep.messageIndex);
                        }
                    }
                    if(ep.buffer != null) {
                        handleCustomPacket(ep, session, toServer);
                    }
                }
            }
        } catch (IOException e) {
            server.getLogger().error(e.getMessage()+", while intercepting custom packet (Packets "+cp.packets.size());
            //throw new ProxyException(e);
        }
    }

    private void handleCustomPacket(CustomPacket.EncapsulatedPacket ep, RemoteClientSession session, boolean toServer) throws IOException {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(ep.buffer));
        byte pid = reader.readByte();
        //server.getLogger().debug("Intercepting packet "+ Util.toHex(pid)+" (ToServer: "+Boolean.toString(toServer)+")");

        switch(pid){
            case MC_LOGIN_PACKET:
                LoginPacket lp = new LoginPacket();
                lp.decode(ep.buffer);
                server.getLogger().info(lp.username+"["+session.getAddress().toString()+"] logged into the proxy. (Protocol "+lp.protocol+")");

                session.setLoginData(new LoginData(lp));
                break;

            case MC_MOVE_PLAYER_PACKET:
                if(!session.hasSpawned()){
                    session.setSpawned(true);
                    server.getLogger().debug("Spawned!");
                }
                break;

            case MC_DISCONNECT:

                if(reader.getInputStream().available() > 1){
                    String reason = reader.readString();
                    System.out.println("There is a reason: "+reason);
                }

                session.getRemoteServer().setRunning(false);

                server.serverSessions.remove(session.getRemoteServer().getAddress().toString());
                server.clientSessions.remove(session.getAddress().toString());

                if(toServer) {
                    server.getLogger().info(session.getUsername() + "[" + session.getAddress().toString() + "] disconnected: disconnected by client.");
                } else {
                    server.getLogger().info(session.getUsername() + "[" + session.getAddress().toString() + "] disconnected: disconnected by remote server.");
                }
                break;

            case MC_MESSAGE_PACKET:
                if(toServer) { //To prevent private messages from being displayed.
                    MessagePacket mp = new MessagePacket();
                    mp.decode(ep.buffer);
                    if(server.logChat() && !(mp.message.startsWith("/"))) {
                        server.getLogger().info("[Server: " + session.getRemoteServer().getAddress().toString() + "] " + mp.message);
                    } else if(mp.message.startsWith("/")){
                        String[] cmd = mp.message.replaceAll("/", "").split(" ");
                        String cmdName = cmd[0];
                        server.getLogger().debug("Got a command (len: "+cmd.length+") "+Arrays.toString(cmd));
                        if(cmdName.equalsIgnoreCase("server")){
                            if(cmd.length > 0){
                                //TODO
                                session.sendMessage("[BouncyBall]: "+Arrays.toString(cmd)+", length: "+cmd.length);
                            } else {
                                session.sendMessage("[BouncyBall]: Usage: /server [server]");
                            }
                        } else {
                            //TODO
                        }
                    }
                }
                break;
        }
    }
}
