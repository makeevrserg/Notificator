package com.makeevrserg.notificator.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.makeevrserg.notificator.MainActivity
import com.makeevrserg.notificator.R
import kotlin.random.Random

/**
 * Обработчик уведомлений FireBase
 *
 * Если посылать уведомления через FCMNotification, то он не будет вызван.
 * Однако если приложение включено, то будет.
 *
 * Чтобы он вызывался всегда - необходимо посылать уведомления через messaging firebase_admin и при этом не указывать title и body.
 * В таком случае onMessageReceived будет всегда вызываться.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Функция, вызываемая при получении уведомления.
     *
     * Вызывается когда приложение открыто либо когда послено через mesasging без title и body
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        sendNotification(remoteMessage.data, remoteMessage.notification)

    }


    /**
     * Отправка кастомного уведомления
     */
    private fun sendNotification(
        data: Map<String, String>,
        notification: RemoteMessage.Notification?
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        //Если указан action, то указываем. Иначе указываем стандуртную активити
        val actionIntent = Intent(notification?.clickAction ?: "FromNotificationActivity")
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, actionIntent, 0)


        //Вероятно в data при уведомлении необходимо указывать тип уведомления, после чего менять channelID
        val channelId = getString(R.string.default_channel)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(
                data["title"] ?: notification?.title ?: getString(R.string.fcm_message)
            )
            .setContentText(data["body"] ?: notification?.body)
            .setColor(Color.parseColor(data["color"] ?: "#3897e0"))
            .setContentIntent(pendingIntent)
            .addAction(
                NotificationCompat.Action(
                    null,
                    data["action_name"],
                    pendingIntent
                )
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        //Если указана картинка для загрузки - загружаем её через Glide
        if (data.containsKey("large_icon")) {
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(data["large_icon"] ?: "")
                .submit()
            val bitmap = futureTarget.get()
            notificationBuilder.setLargeIcon(bitmap)
            Glide.with(this).clear(futureTarget)
        }


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        //В документации написано, что необходимо указывать этот id уникальным
        //Если они одинаковые, то старое уведомление будет перезаписывать новое
        notificationManager.notify(Random.nextInt(5000), notificationBuilder.build())
    }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
