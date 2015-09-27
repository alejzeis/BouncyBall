package io.github.jython234.BouncyBall.utility;

/**
 * A collection of utility methods
 *
 * @author jython234
 */
public abstract class Utils {

    public static String hexDump(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%02X", b) + " ");
        }
        return sb.toString();
    }
}
