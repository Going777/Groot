package com.chocobi.groot.util


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat.Builder
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.google.firebase.messaging.FirebaseMessaging


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"
    private val CHANNEL_ID = "channel_id"
    private val CHANNEL_NAME = "channel_name"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //token을 서버로 전송
        Log.d("MyFirebaseMessagingService", "$token")

        // 토큰 값을 따로 저장
        val pref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", token).apply()
        editor.commit()
        Log.i(TAG, "성공적으로 토큰을 저장함")
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

        //받은 remoteMessage의 값 출력해보기. 데이터메세지 / 알림메세지
        Log.d(TAG, "Message data : ${remoteMessage.data}")
        Log.d(TAG, "Message noti : ${remoteMessage.notification}")

        if (remoteMessage.data.isNotEmpty()) {
            //알림생성
            sendNotification(remoteMessage)
//            Log.d(TAG, remoteMessage.data["title"].toString())
//            Log.d(TAG, remoteMessage.data["body"].toString())
        } else {
            Log.e(TAG, "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
        }

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // 일회용 PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
        val intent = Intent(this, MainActivity::class.java)
        //각 key, value 추가
        for (key in remoteMessage.data.keys) {
            intent.putExtra(key, remoteMessage.data.getValue(key))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Activity Stack 을 경로만 남김(A-B-C-D-B => A-B)
        val pendingIntent = PendingIntent.getActivity(
            this,
            uniId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보, 작업
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_app_logo_round) // 아이콘 설정
            .setContentTitle(remoteMessage.data["title"].toString()) // 제목
            .setContentText(remoteMessage.data["body"].toString()) // 메시지 내용
            .setAutoCancel(true) // 알람클릭시 삭제여부
            .setSound(soundUri)  // 알림 소리
            .setContentIntent(pendingIntent) // 알림 실행 시 Intent

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }

    fun getFirebaseToken() {
        //비동기 방식
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "token=${it}")
        }
    }
}

