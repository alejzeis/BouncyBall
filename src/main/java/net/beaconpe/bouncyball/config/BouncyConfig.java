package net.beaconpe.bouncyball.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the BouncyBall configuration.
 */
public class BouncyConfig {

    private LinkedServer hubServer;
    private ArrayList<LinkedServer> servers = new ArrayList<>();

    public BouncyConfig(File file) throws FileNotFoundException {
        Yaml yml = new Yaml();
        Map<String, Object> config = null;
        try {
            config = (Map<String, Object>) yml.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            createNewConfig();
            config = (Map<String, Object>) yml.load(new FileReader(file));
        } finally {
            if(!parseHubServer((String) config.get("hubServer"))){
                System.err.println("Hub server is invalid.");
            }
            List<String> serversList = (List<String>) config.get("servers");
            for (Object o : serversList) {
                if (!parseAddress((String) o)) {
                    System.err.println("Failed to parse a server address in the config, maybe it's invalid?");
                }
            }
        }
    }

    private void createNewConfig(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("config.yml")));
        File file = new File("config.yml");
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            String line = "";
            while((line = reader.readLine()) != null){
                writer.write(line + "\n");
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean parseAddress(String s) {
        String[] addr = s.split(":");
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(addr[0]), Integer.parseInt(addr[1]));
            LinkedServer server = new LinkedServer();
            server.setAddress(address);
            servers.add(server);
            return true;
        } catch(NumberFormatException e){
            System.err.println("Invalid config server value: "+s);
            return false;
        } catch (UnknownHostException e) {
            System.err.println("Could not figure out host: "+addr[0]+" ("+e.getMessage()+")");
            return false;
        }
    }

    private boolean parseHubServer(String s){
        String[] addr = s.split(":");
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(addr[0]), Integer.parseInt(addr[1]));
            LinkedServer server = new LinkedServer();
            server.setAddress(address);
            hubServer = server;
            return true;
        } catch(NumberFormatException e){
            System.err.println("Invalid config server value: "+s);
            return false;
        } catch (UnknownHostException e) {
            System.err.println("Could not figure out host: "+addr[0]+" ("+e.getMessage()+")");
            return false;
        }
    }

    public ArrayList<LinkedServer> getServers() {
        return servers;
    }

    public LinkedServer getHubServer() {
        return hubServer;
    }
}
