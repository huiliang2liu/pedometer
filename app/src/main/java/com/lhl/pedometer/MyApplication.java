package com.lhl.pedometer;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.lhl.security.Security;
import com.lhl.security.SecurityListener;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Security.Builder(this).setCheckAdb(true)
                .setCheckSp(true).setCheckManifest(true)
                .setCheckRoot(true)
                .setCheckProxy(true)
                .setCheckEditTextEmpty(true)
                .setCheckNetworkChange(true)
                .setListener(new SecurityListener() {
                    @Override
                    public void onRoot(Activity activity) {
                        Log.e("======", "onRoot");
                    }

                    @Override
                    public void onAdb(Activity activity) {
                        Log.e("======", "onAdb");
                    }

                    @Override
                    public void onProxy() {
                        Log.e("======", "onProxy");
                    }

                    @Override
                    public void onNetworkChange() {
                        Log.e("======", "onNetworkChange");
                    }
                }).build();
    }
}
