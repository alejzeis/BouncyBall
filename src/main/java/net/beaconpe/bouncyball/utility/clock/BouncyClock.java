package net.beaconpe.bouncyball.utility.clock;

import net.beaconpe.bouncyball.MinecraftPEProxy;
import net.beaconpe.bouncyball.utility.BouncyThread;

import java.util.ArrayList;

/**
 * Timekeeping thread for BouncyBall.
 */
public class BouncyClock extends BouncyThread{
    private MinecraftPEProxy proxy;
    private ArrayList<Task> tasks = new ArrayList<>();
    private long currentTick = 0;
    private int TPS;

    private int nextTaskID = -1;

    public BouncyClock(MinecraftPEProxy proxy, int TPS){
        this.proxy = proxy;
        this.TPS = TPS;
    }

    @Override
    public void run() {
        setName("BouncyClock");
        proxy.getLogger().info("Clock started.");
        while(isRunning()) {
            currentTick++;
            synchronized (tasks) {
                for (Task task : tasks) {
                    if (currentTick - task.getLastTickRan() == task.getDelay()) {
                        task.run();
                        task.setLastTickRan(currentTick);
                    }
                }
            }
            int sleepTime = 1000 / TPS;
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int registerTask(Task task){
        synchronized (tasks) {
            task.setTaskID(nextTaskID++);
            task.setLastTickRan(currentTick);
            tasks.add(task);
        }
        return task.getTaskID();
    }

    public void cancelTask(Task task){
        synchronized (tasks) {
            tasks.remove(task);
        }
    }

    public void cancelTask(int taskID){
        synchronized (tasks) {
            for (Task task : tasks) {
                if (task.getTaskID() == taskID) {
                    tasks.remove(task);
                    break;
                }
            }
        }
    }

}
