package ru.mrlargha.stemobile;

import android.app.Application;
import android.util.Log;

import com.vk.api.sdk.VK;

import ru.mrlargha.stemobile.data.STERepository;

public class STEMobile extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("stemobile", "Application created");
        VK.initialize(this);
        Log.d("stemobile", "Vk lib initialized");
        STERepository.getRepository(this);
    }
}
