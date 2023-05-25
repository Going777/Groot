package com.chocobi.groot.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

class ThreadUtil {

    companion object {
        private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        private val handler = Handler(Looper.getMainLooper())

        fun startThread(runnable: Runnable) {
            executorService.submit(runnable)
        }

        fun startUIThread(delayMillis: Int, runnable: Runnable) {
            handler.postDelayed(runnable, delayMillis.toLong())
        }
    }

    protected fun finalize() {
        if(!executorService.isShutdown) {
            executorService.shutdown()
        }
    }
}