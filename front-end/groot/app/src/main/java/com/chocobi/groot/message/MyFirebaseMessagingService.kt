package com.chocobi.groot.message


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat.Builder
import com.chocobi.groot.R
import com.google.firebase.messaging.FirebaseMessaging

private const val CHANNEL_ID = "channel_id"
private const val CHANNEL_NAME = "channel_name"


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //token을 서버로 전송
        Log.d("MyFirebaseMessagingService", "$token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //수신한 메시지를 처리
        Log.d("MyFirebaseMessagingService", "${remoteMessage.notification?.body}")

//        토큰 확인
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                token ?: ""
                Log.d("MyFirebaseMessagingService", "$token")
            }


        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )

        var builder: NotificationCompat.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            builder = Builder(applicationContext, CHANNEL_ID)
        } else {
            builder = Builder(applicationContext)
        }

        val title = remoteMessage.notification!!.title
        val body = remoteMessage.notification!!.body

        builder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_app_logo_round)

        val notification: Notification = builder.build()
        if (notificationManager.areNotificationsEnabled()) {
//            에러 일단 무시 (빌드 됨)
            notificationManager.notify(1, notification)
        }
    }
}

