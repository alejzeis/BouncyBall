package net.beaconpe.bouncyball.network.packet;

import net.beaconpe.bouncyball.util.ProxyException;
import org.blockserver.io.BinaryReader;
import org.blockserver.io.BinaryWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Represents a DataPacket
 */
public abstract class DataPacket {

    protected void _encode(BinaryWriter writer) throws IOException{
        throw new UnsupportedOperationException("Encoding not implemented.");
    }
    protected void _decode(BinaryReader reader) throws IOException{
        throw new UnsupportedOperationException("Decoding not implemented.");
    }

    public byte[] encode(){
        BinaryWriter writer = new BinaryWriter(new ByteArrayOutputStream());
        try {
            _encode(writer);
            ByteArrayOutputStream os = (ByteArrayOutputStream) writer.getOutputStream();
            return os.toByteArray();
        } catch (IOException e) {
            throw new ProxyException("Failed to encode packet: "+e.getMessage());
        }
    }

    public void decode(byte[] data){
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(data));
        try{
            _decode(reader);
        } catch (IOException e){
            throw new ProxyException("Failed to decode packet: "+e.getMessage());
        }
    }
}
