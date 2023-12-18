package com.kingdom.controledeestoque

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getSystemService


class NotificationManager(context: Context) {
    var ctxt = context

    val CHANNEL_NAME = "Notificação de Erro"
    val NOTIF_ID = 0
    val CHANNEL_ID = "channel_01"


    val builder = NotificationCompat.Builder(ctxt, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_notifications_24) // Set a small icon
        .setContentTitle("Notification Title") // Set the title
        .setContentText("Notification Text") // Set the body text
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the priority

    // Display the notification






    /*val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "Notificação de Erro"
    val NOTIF_ID = 0
    val channelID = "channel_01"

    fun NotificacaoErro(produto: String, mensagem: String, mensagemLong: String, date: Long ){
        var notif = NotificationCompat.Builder(ctxt, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_logout_24)
            .setTicker("Titulo")
            .setContentTitle(produto)
            .setContentText(mensagem)
            .setContentInfo(mensagemLong)
            .setColor(getColor(ctxt, R.color.colorPrimary))
            .setWhen(date)

        val notification = notif.build()
        val manager = NotificationManagerCompat.from(ctxt)

        manager.notify(1, notification)

    }
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "my_channel_id_01"
    }

    @SuppressLint("ServiceCast")
    private fun registerNotificationManagerWithSystem(channel: NotificationChannel) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            (ctxt.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }


    private fun createNotificationChannel(channel: NotificationChannel) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannelInstance = NotificationChannel(channelID, "Channel 1", IMPORTANCE_HIGH).let {
                it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                it.setShowBadge(true)

                registerNotificationManagerWithSystem(it)
            }
        }
    }*/

}

