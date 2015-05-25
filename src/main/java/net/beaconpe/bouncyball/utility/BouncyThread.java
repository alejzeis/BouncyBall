package net.beaconpe.bouncyball.utility;

import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;

/**
 * Abstract Thread wrapper.
 */
public abstract class BouncyThread extends Thread implements Runnable{
    private boolean running = false;
    private ArrayList<Runnable> startupTasks = new ArrayList<>();
    private ArrayList<Runnable> shutdownTasks = new ArrayList<>();

    public final void startup(){
        LogManager.getLogger("ThreadManager").debug("Starting thread: \""+getName()+"\"");
        if(!running){
            running = true;
            for(Runnable r : startupTasks){
                r.run();
            }
            start();
        } else {
            throw new RuntimeException("Can not start thread: already running.");
        }
    }

    public final void runInCurrentThread(){
        if(!running){
            running = true;
            run();
        } else {
            throw new RuntimeException("Can not start thread: already running.");
        }
    }

    public final void shutdown() throws InterruptedException {
        LogManager.getLogger("ThreadManager").debug("Shutting down thread: \""+getName()+"\"");
        if(running){
            running = false;
            for(Runnable r : shutdownTasks){
                r.run();
            }
            join(5000);
        } else {
            throw new RuntimeException("Can not stop thread: not running.");
        }
    }

    public void addStartupTask(Runnable r){
        startupTasks.add(r);
    }

    public void addShutdownTask(Runnable r){
        shutdownTasks.add(r);
    }

    public void clearStartupTasks(){
        startupTasks.clear();
    }

    public void clearShutdownTasks(){
        shutdownTasks.clear();
    }

    public final boolean isRunning(){
        return running;
    }

    public abstract void run();
}
