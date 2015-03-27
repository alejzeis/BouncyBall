package net.beaconpe.bouncyball.network.packet;

import static net.beaconpe.bouncyball.network.PacketIDs.MC_MESSAGE_PACKET;

import org.blockserver.io.BinaryReader;
import org.blockserver.io.BinaryWriter;

import java.io.IOException;

/**
 * Message Packet (0x85).
 */
public class MessagePacket extends DataPacket{
    /**
     * Message to be sent.
     */
    public String message;

    @Override
    protected void _encode(BinaryWriter writer) throws IOException {
        writer.writeByte(MC_MESSAGE_PACKET);
        writer.writeString("");
        writer.writeString(message);
    }

    @Override
    protected void _decode(BinaryReader reader) throws IOException {
        reader.readByte(); //PID
        reader.readString();
        message = reader.readString();
    }
}
