package com.lhl.pedometer;

public interface PedometerListener {
    void onStepChange(int step);

    void onDayChange(int step);
}
