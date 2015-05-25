package net.beaconpe.bouncyball.utility.clock;

/**
 * An abstract Task used by the Clock that can be ran.
 */
public abstract class Task implements Runnable{
    private int delay;
    private int taskID;
    private long lastTickRan;

    public abstract void run();

    public long getLastTickRan() {
        return lastTickRan;
    }

    public void setLastTickRan(long lastTickRan) {
        this.lastTickRan = lastTickRan;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getTaskID() {
        return taskID;
    }

    protected void setTaskID(int taskID) {
        this.taskID = taskID;
    }
}
