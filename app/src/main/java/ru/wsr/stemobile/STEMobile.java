package ru.wsr.stemobile;

import android.app.Application;
import android.util.Log;

import ru.wsr.stemobile.data.STERepository;

public class STEMobile extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("stemobile", "Application created");
        STERepository.getRepository(this);
    }
}
