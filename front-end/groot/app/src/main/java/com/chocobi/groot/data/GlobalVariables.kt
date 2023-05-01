package com.chocobi.groot.data

import android.app.Application

class GlobalVariables : Application() {
    companion object {
        private var BASE_URL = "https://k8a303.p.ssafy.io"

        fun getBaseUrl(): String {
            return BASE_URL
        }

        fun setBaseUrl(url: String) {
            BASE_URL = url
        }
    }
}