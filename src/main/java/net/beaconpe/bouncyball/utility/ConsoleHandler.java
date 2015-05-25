package net.beaconpe.bouncyball.utility;

import net.beaconpe.bouncyball.MinecraftPEProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simple Thread that recieves console commands.
 */
public class ConsoleHandler extends BouncyThread{
    private MinecraftPEProxy proxy;
    private BufferedReader reader;

    public ConsoleHandler(MinecraftPEProxy proxy){
        this.proxy = proxy;
        addShutdownTask(() -> {
            try {
                reader.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        setName("ConsoleHandler");
        reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(isRunning()) {
                String cmd = reader.readLine();
                if (cmd.startsWith("stop")) {
                    try {
                        proxy.shutdown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
