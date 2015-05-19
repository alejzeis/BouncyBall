package net.beaconpe.bouncyball.network.packet;

import net.beaconpe.bouncyball.BouncyBall;
import net.beaconpe.bouncyball.network.PacketIDs;
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
    public ArrayList<EncapsulatedPacket> packets = new ArrayList<>();

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

    public CustomPacket(){ }

    public byte[] toBytes(){
        ByteBuffer bb = ByteBuffer.allocate(getLength());
        bb.put(PacketIDs.RAKNET_CUSTOM_PACKET_DEFAULT);
        Util.writeLTriad(seqNumber, bb);
        for(EncapsulatedPacket ep : packets){
            bb.put(ep.toBytes());
        }
        return bb.array();
    }

    public int getLength(){
        int len = 4;
        for(EncapsulatedPacket e : packets){
            len = len + e.getLength();
        }
        return len;
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

        public EncapsulatedPacket() { }

        public EncapsulatedPacket(ByteBuffer bb) {
            bb.order(ByteOrder.BIG_ENDIAN);
            byte flag = bb.get();
            reliability = (byte) (flag >> 5);
            hasSplit = (flag & 0x10) == 0x10;

            final short _length = bb.getShort();
            /*
            final int length = (_length & 0x0000FFF8) >> 3;
            */
            final int length = (_length / 8);
            if (Util.inArray(reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)) {
                messageIndex = Util.readLTriad(bb);
            }
            if (Util.inArray(reliability, RAKNET_HAS_ORDER_RELIABILITIES)) {
                orderIndex = Util.readLTriad(bb);
                orderChannel = bb.get();
            }
            if (hasSplit) {
                splitCount = bb.getInt();
                splitId = bb.getShort();
                splitIndex = bb.getInt();
            }

            if (length > bb.capacity() || length < 1) {
                //TODO: Something is wrong
                if (BouncyBall.SERVER_INSTANCE.logPacketErrors()) {
                    System.err.println("[Length is greater than capacity] Decoding encapsulated packet, buffer len: " + length + ", original: " + _length);
                }
            } else {
                buffer = new byte[length];
                bb.get(buffer);
            }
        }

        public byte[] toBytes(){
            ByteBuffer bb = ByteBuffer.allocate(getLength());
            bb.put((byte) (reliability << 5));
            bb.putShort((short) (buffer.length * 8));
            if (Util.inArray(reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)) {
                Util.writeLTriad(messageIndex, bb);
            }
            if (Util.inArray(reliability, RAKNET_HAS_ORDER_RELIABILITIES)) {
                Util.writeLTriad(orderIndex, bb);
                bb.put(orderChannel);
            }
            if (hasSplit) {
                bb.putInt(splitCount);
                bb.putShort(splitId);
                bb.putInt(splitIndex);
            }
            bb.put(buffer);
            return bb.array();
        }

        public int getLength(){
            int len = 3 + buffer.length;
            if(Util.inArray(reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)){
                len = len + 3;
            }
            if(Util.inArray(reliability, RAKNET_HAS_ORDER_RELIABILITIES)){
                len = len + 4;
            }
            if(hasSplit){
                len = len + 10;
            }
            return len;
        }
    }
}
