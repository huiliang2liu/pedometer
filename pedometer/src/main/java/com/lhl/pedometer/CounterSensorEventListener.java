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

class CounterSensorEventListener implements SensorEventListener, Pedometer {
    private static final String SAVE_TIME = "sava_time";
    private static final String INIT_STEP = "init_step";
    private static final String TODAY_STEP = "today_step";
    private static final int INIT = 1;
    private boolean init = false;
    private SharedPreferences preferences;
    private Handler handler;
    private long initTime = 0;
    private int steps = -1;
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
                steps = preferences.getInt(INIT_STEP, -1);
                todaySteps = preferences.getInt(TODAY_STEP, 0);
                long savaTime = preferences.getLong(SAVE_TIME, 0);
                long todayTime = Util.getToday();
                if (savaTime != todayTime) {
                    todaySteps = 0;
                    steps = -1;
                }
                saveTime = SystemClock.uptimeMillis();
                stepChange(todaySteps);
                Log.e("Pedometer", "todaySteps:" + todaySteps + " steps:" + steps);
                return true;
            }
            preferences.edit()
                    .putLong(SAVE_TIME, initTime)
                    .putInt(TODAY_STEP, todaySteps)
                    .putInt(INIT_STEP, steps)
                    .commit();
            return true;
        }
    };


    private CounterSensorEventListener(Looper looper, SharedPreferences sp, PedometerListener listener) {
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
        int step = (int) event.values[0];
        if (initTime != todayTime) {
            initTime = todayTime;
            steps = step;
            int steps = todaySteps;
            todaySteps = 0;
            changeToday(steps);
            stepChange(todaySteps);
            return;
        }
        if (steps == step)
            return;
        if (steps == -1) {//没有启动过
            steps = step;
            todaySteps = 0;
            init(steps);
            return;
        }
        if (steps > step) {//重启手机了
            steps = step;
            init(steps);
            return;
        }
        if (fast) {//就算离线经验
            fast = false;
            todaySteps += step - steps;
            steps = step;
            stepChange(todaySteps);
            return;
        }
        steps = step;
        todaySteps++;
        stepChange(todaySteps);
    }

    private void changeToday(int todaySteps) {
        listener.onDayChange(todaySteps);
        handler.sendEmptyMessage(1);
    }

    private void init(int initStep) {
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
                .putInt(INIT_STEP, steps)
                .commit();
    }


    @Override
    public int getSteps() {
        return todaySteps;
    }
}
