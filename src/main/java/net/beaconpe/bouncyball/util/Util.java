package net.beaconpe.bouncyball.util;

import java.nio.ByteBuffer;

/**
 * BouncyBall Utilites.
 * Portions from the BlockServerProject (https://github.com/BlockServerProject/BlockServer)
 */
public class Util {

    public static String toHex(byte... bytes){
        String returnString = "";
        for(byte b: bytes){
            returnString = returnString + String.format("%02x", b) + " ";
        }

        return returnString;
    }

    public static boolean inArray(byte needle, byte[] haystack){
        for(byte item : haystack){
            if(item == needle){
                return true;
            }
        }
        return false;
    }

    public static int readLTriad(ByteBuffer bb){
        byte[] triad = new byte[3];
        bb.get(triad);
        return readLTriad(triad);
    }

    public static int readLTriad(byte[] triad){
        return triad[0]
                + (triad[1] << 8)
                + (triad[2] << 16);
    }
}
