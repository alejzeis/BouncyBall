package net.beaconpe.bouncyball.utility.io;

import org.blockserver.io.BinaryReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream with special methods.
 */
public class BouncyReader extends BinaryReader{
    public BouncyReader(InputStream is) {
        super(is);
    }

    public BouncyReader(InputStream is, boolean endianness) {
        super(is, endianness);
    }

    /**
     * Read a google VarInt.
     * <br> Used from: https://gist.github.com/thinkofdeath/e975ddee04e9c87faf22
     * @return The VarInt as an integer.
     * @throws IOException If there is an error while reading.
     */
    public int readVarInt() throws IOException{
        int i = 0;
        int j = 0;
        while (true) {
            int k = readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128) break;
        }

        return i;
    }
}
