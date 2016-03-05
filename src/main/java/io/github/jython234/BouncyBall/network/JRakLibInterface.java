package io.github.jython234.BouncyBall.network;

import io.github.jython234.BouncyBall.BouncyBallProxy;
import io.github.jython234.BouncyBall.utility.Utils;
import net.beaconpe.jraklib.JRakLib;
import net.beaconpe.jraklib.protocol.EncapsulatedPacket;
import net.beaconpe.jraklib.server.JRakLibServer;
import net.beaconpe.jraklib.server.ServerHandler;
import net.beaconpe.jraklib.server.ServerInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * A class that communicates with JRakLib.
 *
 * @author jython234
 */
public class JRakLibInterface implements ServerInstance {
    private final BouncyBallProxy proxy;
    private final JRakLibServer server;
    private final ServerHandler handler;

    public JRakLibInterface(BouncyBallProxy proxy) {
        this.proxy = proxy;

        server = new JRakLibServer(new JRakLibLogger(LogManager.getLogger("JRakLibServer")), 19132, "");
        handler = new ServerHandler(server, this);
    }

    public void setName(String name) {
        handler.sendOption("name", "MCPE;"+name+";34;0.12.1;0;0"); //TODO: Improve
    }

    public void process() {
        for(int i = 0; i < 500; i++) { //TODO: Get PPS from config
            if(!handler.handlePacket()) break;
        }

        if(server.getState() == Thread.State.TERMINATED) {
            proxy.getLogger().fatal("The JRakLibServer has crashed!");
            proxy.stop();
        }
    }

    public void sendPacket(byte[] data, String identifier) {
        EncapsulatedPacket pkt = new EncapsulatedPacket();
        pkt.messageIndex = 0;
        pkt.reliability = 2;
        pkt.buffer = data;
        handler.sendEncapsulated(identifier, pkt, (byte) (0 | JRakLib.PRIORITY_NORMAL));
    }

    @Override
    public void openSession(String identifier, String address, int port, long clientID) {
        proxy.getLogger().debug("(" + identifier + ") New session with clientID: " + clientID);
    }

    @Override
    public void closeSession(String identifier, String reason) {
        proxy.getLogger().debug("(" + identifier + ") Session closed: " + reason);
    }

    @Override
    public void handleEncapsulated(String identifier, EncapsulatedPacket packet, int flags) {
        proxy.getLogger().debug("(" + identifier + ") IN: " + Utils.hexDump(packet.buffer));
    }

    @Override
    public void handleRaw(String address, int port, byte[] payload) {
        proxy.getLogger().debug("(" + address + ":" + port + ") IN (RAW): " + Utils.hexDump(payload));
    }

    @Override
    public void notifyACK(String identifier, int identifierACK) {

    }

    @Override
    public void handleOption(String option, String value) {

    }

    public static String socketAddressToIdentifier(SocketAddress address) {
        if(address instanceof InetSocketAddress) {
            return ((InetSocketAddress) address).getAddress() + ":" + ((InetSocketAddress) address).getPort();
        }
        return null;
    }

    public static class JRakLibLogger implements net.beaconpe.jraklib.Logger {
        private final Logger logger;

        public JRakLibLogger(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void notice(String s) {
            logger.info(s);
        }

        @Override
        public void critical(String s) {
            logger.error(s);
        }

        @Override
        public void emergency(String s) {
            logger.fatal(s);
        }
    }
}
