package net.beaconpe.bouncyball.util;

/**
 * Thread wrapper designed for tasks to not disrupt the main thread.
 */
public class Worker {
    private Thread thread;

    public Worker(Runnable runnable, String name){
        thread = new Thread(runnable);
        thread.setName("Worker-"+name);
    }

    public void start(){
        thread.start();
    }

    public void stop() throws InterruptedException {
        thread.join();
    }

    @Deprecated
    public void stopImmediately(){
        thread.stop();
    }
}
