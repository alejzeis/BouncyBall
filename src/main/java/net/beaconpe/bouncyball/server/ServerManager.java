package net.beaconpe.bouncyball.server;

import net.beaconpe.bouncyball.MinecraftPEProxy;
import net.beaconpe.bouncyball.network.P2PSession;
import net.beaconpe.bouncyball.network.protocol.BouncyP2P;
import net.beaconpe.bouncyball.utility.RegistrationException;

import java.util.ArrayList;

/**
 * Class that manages all servers connected to this proxy.
 */
public class ServerManager {
    private ArrayList<P2PSession> serverSessions = new ArrayList<>();
    private MinecraftPEProxy proxy;

    public ServerManager(MinecraftPEProxy proxy){
        this.proxy = proxy;
    }

    public void registerServer(P2PSession serverSession, BouncyP2P.ServerRegistrationRequest request){
        if(!isRegistered(serverSession)){
            serverSessions.add(serverSession);
        } else {
            throw new RegistrationException("Could not register server: already registered.");
        }
    }

    /**
     * Internal Method. DO NOT USE!
     * @param session session
     */
    public void removeServer(P2PSession session){
        serverSessions.remove(session);
    }

    public boolean isRegistered(P2PSession session){
        for(P2PSession sessionCheck: serverSessions){
            if(sessionCheck.getHandler().getRemoteAddress().toString().equals(session.getHandler().getRemoteAddress().toString())){
                return true;
            }
        }
        return false;
    }
}
