package net.beaconpe.bouncyball.utility.clock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An implementation of a task that allows methods to be called through reflection.
 */
public class CallableTask extends Task{
    private Method method;
    public Object instance;

    public CallableTask(String methodName, Object instance, int delay) throws NoSuchMethodException {
        this.method = instance.getClass().getMethod(methodName);
        this.instance = instance;
        setDelay(delay);
    }

    @Override
    public void run() {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
