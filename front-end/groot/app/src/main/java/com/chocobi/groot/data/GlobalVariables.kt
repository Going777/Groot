package com.chocobi.groot.data

import android.app.Application

class GlobalVariables : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil

        private var BASE_URL = "https://k8a303.p.ssafy.io"

        fun getBaseUrl(): String {
            return BASE_URL
        }

        fun setBaseUrl(url: String) {
            BASE_URL = url
        }
    }


    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(applicationContext)
    }
}