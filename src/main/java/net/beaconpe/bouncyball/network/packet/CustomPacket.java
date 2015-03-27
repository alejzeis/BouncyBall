package net.beaconpe.bouncyball.network.packet;

import net.beaconpe.bouncyball.BouncyBall;
import net.beaconpe.bouncyball.util.Util;
import static net.beaconpe.bouncyball.network.PacketIDs.*;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Represents a RakNet CustomPacket (0x80-0x8F).
 * Portions from the BlockServerProject (https://github.com/BlockServerProject/BlockServer)
 */
public class CustomPacket {
    public int seqNumber;
    public ArrayList<EncapsulatedPacket> packets = new ArrayList<>(1);

    public CustomPacket(ByteBuffer bb) throws IOException {
        seqNumber = Util.readLTriad(bb);
        while(bb.remaining() >= 3){
            try {
                packets.add(new EncapsulatedPacket(bb));
            }catch(BufferUnderflowException e){
                if(BouncyBall.SERVER_INSTANCE.logPacketErrors()) {
                    System.err.println("BufferUnderflowException while decoding CustomPacket.");
                    System.err.println("Length: " + bb.capacity()+", Current Packets: "+packets.size()+", Remaining: "+bb.remaining());
                    e.printStackTrace();
                }
            }
        }
    }
    public static class EncapsulatedPacket{
        public byte reliability;
        public boolean hasSplit;
        public int messageIndex = -1;
        public int orderIndex = -1;
        public byte orderChannel = (byte) 0xFF;
        public int splitCount = -1;
        public short splitId = -1;
        public int splitIndex = -1;
        public byte[] buffer;

        public EncapsulatedPacket(ByteBuffer bb){
            bb.order(ByteOrder.BIG_ENDIAN);
            byte flag = bb.get();
            reliability = (byte) (flag >> 5);
            hasSplit = (flag & 0x10) == 0x10;

            final short _length = bb.getShort();
            /*
            final int length = (_length & 0x0000FFF8) >> 3;
            */
            final int length = (_length / 8);
            if(Util.inArray(reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)){
                messageIndex = Util.readLTriad(bb);
            }
            if(Util.inArray(reliability, RAKNET_HAS_ORDER_RELIABILITIES)){
                orderIndex = Util.readLTriad(bb);
                orderChannel = bb.get();
            }
            if(hasSplit){
                splitCount = bb.getInt();
                splitId = bb.getShort();
                splitIndex = bb.getInt();
            }
            try {
                buffer = new byte[length];
                try {
                    bb.get(buffer);
                } catch(BufferUnderflowException e){
                    if(BouncyBall.SERVER_INSTANCE.logPacketErrors()) {
                        System.err.println("Decoding encapsulated packet, buffer len: " + length + ", original: " + _length);
                    }
                    throw e;
                }
            } catch (NegativeArraySizeException e){
                if(BouncyBall.SERVER_INSTANCE.logPacketErrors()) {
                    System.err.println("Decoding encapsulated packet, buffer len: " + length + ", original: " + _length);
                    System.err.println("(Negative Array Size Exception)");
                }
                buffer = new byte[] {0x02}; //Unused packet
            }
        }
    }
}
