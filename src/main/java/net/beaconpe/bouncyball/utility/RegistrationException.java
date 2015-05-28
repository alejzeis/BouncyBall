package net.beaconpe.bouncyball.utility;

/**
 * Class to be thrown when there is a problem registering a remote server.
 */
public class RegistrationException extends RuntimeException{

    public RegistrationException(String message){
        super(message);
    }
}
