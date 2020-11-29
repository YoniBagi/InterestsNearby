package com.interestsnearby

import android.app.Application
import android.content.Context

class InterestsNearbyApplication: Application() {
    companion object{
        private lateinit var sAppContext: Context

        fun getAppContext() = sAppContext
    }

    override fun onCreate() {
        super.onCreate()
        sAppContext = applicationContext
    }

}