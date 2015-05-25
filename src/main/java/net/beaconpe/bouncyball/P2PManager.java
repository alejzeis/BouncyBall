package net.beaconpe.bouncyball;

import net.beaconpe.bouncyball.network.P2PConnectionHandler;
import net.beaconpe.bouncyball.utility.BouncyThread;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Thread to handle all P2P communications.
 */
public class P2PManager extends BouncyThread {
    private ServerSocket socket;
    private MinecraftPEProxy proxy;

    private ArrayList<P2PConnectionHandler> connections = new ArrayList<>();

    private boolean createdPortMapping = true;
    private GatewayDevice d;

    public P2PManager(MinecraftPEProxy proxy){
        this.proxy = proxy;
        addShutdownTask(() -> {
            try {
                removePortMappings();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run(){
        setName("P2PManager");
        proxy.getLogger().info("Checking port mappings...");
        try {
            checkPortMappings();

            socket = new ServerSocket();
            socket.bind(new InetSocketAddress(19135));
            socket.setSoTimeout(2000);
            while(isRunning()){
                try {
                    Socket client = socket.accept();
                    P2PConnectionHandler connection = new P2PConnectionHandler(this, client, false);
                    connection.startup();
                    connections.add(connection);
                } catch(SocketTimeoutException e){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void removePortMappings() throws IOException, SAXException {
        if(createdPortMapping && d != null){
            proxy.getLogger().info("Removing port mappings...");
            d.deletePortMapping(19135, "TCP");
            proxy.getLogger().info("Port mappings removed.");
        }
    }

    private void checkPortMappings() throws IOException, SAXException, ParserConfigurationException {
        GatewayDiscover discover = new GatewayDiscover();
        proxy.getLogger().info("Searching for gateway devices...");
        discover.discover();

        d = discover.getValidGateway();
        if(d != null){
            proxy.getLogger().debug("Found a gateway device. "+d.getModelName()+" ("+d.getModelDescription()+")");
            InetAddress localAddr = d.getLocalAddress();
            PortMappingEntry entry = new PortMappingEntry();

            if(d.getSpecificPortMappingEntry(19135, "TCP", entry)){
                proxy.getLogger().info("Port mapping for 19135 exists. All port mappings complete :)");
            } else {
                proxy.getLogger().info("Did not find mapping for port 19135 (TCP). Sending map request...");
                if(!d.addPortMapping(19135, 19135, localAddr.getHostAddress(), "TCP", "BouncyBall P2P Port")){
                    proxy.getLogger().error("Failed to add a port mapping for port 19135!");
                    proxy.getLogger().error("Please port forward port 19135 TCP to the computer BouncyBall is running on.");
                } else {
                    createdPortMapping = true;
                    proxy.getLogger().info("Port mapping succeeded!");
                }
            }
        } else {
            proxy.getLogger().error("Could not find any valid gateway devices.");
            proxy.getLogger().error("Try turning UPNP on in your router settings, or port forward port 19135 (TCP) on your router");
        }
    }

    public MinecraftPEProxy getProxy(){
        return proxy;
    }
}
