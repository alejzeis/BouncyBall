package io.github.jython234.BouncyBall;


import io.github.jython234.BouncyBall.network.JRakLibInterface;
import io.github.jython234.BouncyBall.utility.YamlConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import net.beaconpe.jraklib.server.JRakLibServer;
import net.beaconpe.jraklib.server.ServerHandler;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An implementation of a Minecraft: PE proxy.
 *
 * @author jython234
 */
public class BouncyBallProxy {
    @Getter private ExecutorService pool;
    @Getter private final Logger logger;
    @Getter private final YamlConfiguration config;
    @Getter private boolean running = false;

    @Getter private String name;
    @Getter private final String bindInterface;
    @Getter private final int bindPort;

    @Getter private JRakLibInterface rakLibInterface;

    public BouncyBallProxy(Logger logger, YamlConfiguration config) {
        this.logger = logger;
        this.config = config;

        name = config.getString("serverName");
        bindInterface = config.getString("serverIP");
        bindPort = config.getInt("serverPort");
        pool = Executors.newFixedThreadPool(config.getInt("worker-threads"));
    }

    public void start() {
        if(running) throw new UnsupportedOperationException("Proxy already running!");
        running = true;
        run();
    }

    public void stop() {
        if(!running) throw new UnsupportedOperationException("Proxy not running!");
        running = false;
    }

    private void run() {
        logger.info("Starting BouncyBall " + BouncyBall.VERSION + "...");
        rakLibInterface = new JRakLibInterface(this);
        rakLibInterface.setName(name);
        logger.info("Using "+config.getInt("worker-threads")+" NIO threads.");
        logger.info("Started server on " + bindInterface + ":" + bindPort);
        while (running) {
            long start = System.currentTimeMillis();
            doTick();
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed < 50) {
                try {
                    Thread.currentThread().sleep(50 - elapsed);
                } catch (InterruptedException e) {
                    logger.fatal("Interrupted while sleeping! " + e.getMessage());
                }
            } else {
                logger.warn("Can't keep up! (" + elapsed + " > 50) Did the system time change or is the software overloaded?");
            }
        }
    }

    public void sendPacket(byte[] data, InetSocketAddress sendTo) {
        rakLibInterface.sendPacket(data, JRakLibInterface.socketAddressToIdentifier(sendTo));
    }

    private void doTick() {
        rakLibInterface.process();

    }
}
