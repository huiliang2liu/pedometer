package com.lhl.pedometer;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;

class StepPedometer implements Pedometer, PedometerListener {
    private HandlerThread thread = new HandlerThread("Pedometer-Thread");
    SharedPreferences preferences;
    Sensor sensor;
    SensorEventListener listener;
    PedometerListener pedometerListener;
    private SensorManager sensorManager;
    private boolean isRegister = false;


    public StepPedometer(SharedPreferences sp, Sensor sensor, Class<? extends SensorEventListener> clazz, SensorManager sensorManager, PedometerListener pedometerListener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        preferences = sp;
        this.sensor = sensor;
        thread.start();
        this.listener = clazz.getDeclaredConstructor(Looper.class, SharedPreferences.class, PedometerListener.class).newInstance(thread.getLooper(), sp, this);
        this.sensorManager = sensorManager;
        this.pedometerListener = pedometerListener;
        isRegister = sensorManager.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void destroy() {
        if (listener instanceof Pedometer)
            ((Pedometer) listener).destroy();
        if (isRegister)
            sensorManager.unregisterListener(listener);
        thread.quit();
    }

    @Override
    public void onStepChange(int step) {
        if (pedometerListener != null)
            pedometerListener.onStepChange(step);
    }

    @Override
    public void onDayChange(int step) {
        if (pedometerListener != null)
            pedometerListener.onDayChange(step);
    }

    @Override
    public int getSteps() {
        if (listener instanceof Pedometer)
            ((Pedometer) listener).getSteps();
        return 0;
    }
}
