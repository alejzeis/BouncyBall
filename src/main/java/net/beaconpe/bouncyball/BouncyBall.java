package net.beaconpe.bouncyball;

/**
 * BouncyBall start file.
 */
public class BouncyBall implements Runnable{
    public static MinecraftPEProxy PROXY_INSTANCE;

    @Override
    public void run(){
        PROXY_INSTANCE = new MinecraftPEProxy();
        PROXY_INSTANCE.runInCurrentThread();
    }

    public static void main(String[] args){
        BouncyBall ball = new BouncyBall();
        ball.run();
    }
}
