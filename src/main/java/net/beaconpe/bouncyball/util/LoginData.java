package net.beaconpe.bouncyball.util;

import net.beaconpe.bouncyball.network.packet.LoginPacket;

/**
 * Represents LoginData.
 */
public class LoginData {
    public final int protocol;
    public final String username;
    public final String skin;

    public LoginData(LoginPacket lp){
        protocol = lp.protocol;
        username = lp.username;
        if(lp.protocol > 20){
            skin = lp.skin;
        } else {
            skin = "";
        }
    }
}
