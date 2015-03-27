package net.beaconpe.bouncyball.util;

/**
 * Global default exception used by the MCPE proxy.
 */
public class ProxyException extends RuntimeException{

    public ProxyException(Exception e){
        super(e);
    }

    public ProxyException(String e){
        super(e);
    }
}
