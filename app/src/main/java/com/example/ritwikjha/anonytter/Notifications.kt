package com.example.ritwikjha.anonytter

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat

@Suppress("DEPRECATION")
/**
 * Created by RITWIK JHA on 04-01-2018.
 */
class Notifications(){

    val NOTIFY_TAG="New Request"

    fun Notify(context: Context,message:String){
        val notification=Intent(context,MainActivity::class.java)

        val builder=NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("New Post")
                .setSmallIcon(R.drawable.assassin_s_creed_anonymous)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(context,0,notification,PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)

        val nm=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ECLAIR){
            nm.notify(NOTIFY_TAG,0,builder.build())
        }else{
            nm.notify(NOTIFY_TAG.hashCode(),builder.build())
        }
    }
}