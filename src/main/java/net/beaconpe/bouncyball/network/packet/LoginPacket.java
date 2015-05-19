package net.beaconpe.bouncyball.network.packet;


import org.blockserver.io.BinaryReader;

import java.io.IOException;

/**
 * MCPE Login Packet (0x82).
 */
public class LoginPacket extends DataPacket{
    public String username;
    public int protocol;
    public int protocol2;
    public int clientID;
    public String skin;

    @Override
    protected void _decode(BinaryReader reader) throws IOException {
        reader.readByte(); //PID
        username = reader.readString();
        protocol = reader.readInt();
        protocol2 = reader.readInt();
        clientID = reader.readInt();
    }
}
