package com.wakeupdev.weatherforecast.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.wakeupdev.weatherforecast.Constants
import com.wakeupdev.weatherforecast.R

object NotificationUtils {
    fun createForegroundInfo(mContext: Context, syncContentText: String?): ForegroundInfo {
        val notificationId = 10

        var flag = 0

        flag = PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getActivity(mContext, 0, null, flag)

        //create notification builder
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(
            mContext,
            Constants.SYNC_NOTIFICATION_CHANNEL_ID
        ) //small icon should be your app icon
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(syncContentText) /*.setContentTitle(Const.SYNC_CONTENT_TITLE)*/ /*.setContentText(syncContentText)*/
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .setContentIntent(pendingIntent)


        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

            //notification channel
            val notificationChannel = NotificationChannel(
                Constants.SYNC_NOTIFICATION_CHANNEL_ID,
                Constants.SYNC_NOTIFICATION_CHANNEL_NAME,
                channelImportance
            )
            //set the description for the channel
            notificationChannel.description = Constants.SYNC_NOTIFICATION_CHANNEL_DESCRIPTION
            //register the channel with the system
            val notificationManager = mContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }

        //specify foreground service tyoe to meet Android 14 requirement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ForegroundInfo(
                notificationId,
                notification.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        }

        return ForegroundInfo(notificationId, notification.build())
    }

}