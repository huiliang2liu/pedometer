package com.lhl.pedometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface Pedometer {

    void destroy();

    int getSteps();

    class Builder {
        private PedometerListener listener;
        private Context context;
        private SharedPreferences sharedPreferences;
        private boolean isDebug = false;

        public Builder setListener(PedometerListener listener) {
            assert listener != null : "listener is null";
            this.listener = listener;
            return this;
        }

        public Builder setContext(Context context) {
            assert context != null : "context is null";
            this.context = context;
            return this;
        }

        public void setDebug(boolean debug) {
            isDebug = debug;
        }

        public Builder setSharedPreferences(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
            return this;
        }

        public Pedometer build() {
            Pedometer pedometer = new DefPedometer();
            if (context == null) return pedometer;
            Class<? extends SensorEventListener> clazz = null;
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager == null) return pedometer;
            Sensor countSenor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            Sensor detectorSenor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (sharedPreferences == null)
                sharedPreferences = context.getSharedPreferences("Pedometer", Context.MODE_PRIVATE);
            Sensor sensor;
            if (countSenor != null) {
                sensor = countSenor;
                clazz = CounterSensorEventListener.class;
            } else {
                sensor = detectorSenor;
                clazz = DetectorSensorEventListener.class;
            }
            try {
                return new StepPedometer(sharedPreferences, sensor, clazz, sensorManager, (PedometerListener) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PedometerListener.class}, new InvocationHandler() {
                    private PedometerListener pedometerListener = listener;
                    private boolean debug = isDebug;
                    private Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            if (pedometerListener == null) return;
                            int what = msg.what;
                            Method method = (Method) msg.obj;
                            try {
                                method.invoke(pedometerListener, what);
                            } catch (Exception e) {
                            }
                        }
                    };

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        int step = (int) args[0];
                        Message message = handler.obtainMessage(step, method);
                        if (debug)
                            Log.e("Pedometer", method.getName() + " step:" + step);
                        handler.sendMessage(message);
                        return null;
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pedometer;
        }
    }

    class DefPedometer implements Pedometer {
        @Override
        public void destroy() {
            Log.e("DefPedometer", "destroy");
        }

        @Override
        public int getSteps() {
            Log.e("DefPedometer", "getSteps");
            return 0;
        }
    }


}
