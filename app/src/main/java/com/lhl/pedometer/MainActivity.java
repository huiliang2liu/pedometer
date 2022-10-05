package com.lhl.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements PedometerListener {
    TextView textView;
    Pedometer pedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.step);
        pedometer = new Pedometer.Builder()
                .setContext(this)
                .setListener(this)
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pedometer.destroy();
    }

    @Override
    public void onStepChange(int step) {
        textView.setText(String.valueOf(step));
    }

    @Override
    public void onDayChange(int step) {

    }
}