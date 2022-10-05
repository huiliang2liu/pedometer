package com.lhl.pedometer;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

class DetectorSensorEventListener implements SensorEventListener, Pedometer {
    private static final String SAVE_TIME = "sava_time";
    private static final String TODAY_STEP = "today_step";
    private boolean init = false;
    private SharedPreferences preferences;
    private Handler handler;
    private long initTime = 0;
    private int todaySteps = -1;
    private boolean fast = true;
    private PedometerListener listener;
    private long saveTime = 0;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what == 0) {
                init = true;
                todaySteps = preferences.getInt(TODAY_STEP, 0);
                long savaTime = preferences.getLong(SAVE_TIME, 0);
                long todayTime = Util.getToday();
                if (savaTime != todayTime) {
                    todaySteps = 0;
                }
                saveTime = SystemClock.uptimeMillis();
                stepChange(todaySteps);
                Log.e("Pedometer","todaySteps:"+todaySteps);
                return true;
            }
            preferences.edit()
                    .putLong(SAVE_TIME, initTime)
                    .putInt(TODAY_STEP, todaySteps)
                    .commit();
            return true;
        }
    };


    private DetectorSensorEventListener(Looper looper, SharedPreferences sp, PedometerListener listener) {
        preferences = sp;
        handler = new Handler(looper, callback);
        initTime = Util.getToday();
        handler.sendEmptyMessage(0);
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!init)
            return;
        long todayTime = Util.getToday();
        if (initTime != todayTime) {
            initTime = todayTime;
            int steps = todaySteps;
            todaySteps = 0;
            changeToday(steps);
            stepChange(todaySteps);
            return;
        }
        todaySteps++;
        stepChange(todaySteps);
    }

    private void changeToday(int todaySteps) {
        listener.onDayChange(todaySteps);
        handler.sendEmptyMessage(1);
    }


    private void stepChange(int steps) {
        listener.onStepChange(steps);
        long time = SystemClock.uptimeMillis();
        if (time - saveTime < 500)
            return;
        saveTime = time;
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void destroy() {
        preferences.edit()
                .putLong(SAVE_TIME, initTime)
                .putInt(TODAY_STEP, todaySteps)
                .commit();
    }

    @Override
    public int getSteps() {
        return todaySteps;
    }
}
