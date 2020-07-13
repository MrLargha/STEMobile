package ru.mrlargha.stemobile

import android.app.Application
import android.util.Log
import com.vk.api.sdk.VK.initialize
import ru.mrlargha.stemobile.data.STERepository.Companion.getRepository

class STEMobile : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("stemobile", "Application created")
        initialize(this)
        Log.d("stemobile", "Vk lib initialized")
        getRepository(this)
    }
}