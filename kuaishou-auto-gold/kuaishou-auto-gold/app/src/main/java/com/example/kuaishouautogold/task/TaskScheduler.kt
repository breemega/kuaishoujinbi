package com.example.kuaishouautogold.task

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.kuaishouautogold.service.TaskService

class TaskScheduler(private val context: Context) {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun scheduleTask(hour: Int, minute: Int) {
        val intent = Intent(context, TaskReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            // 如果时间已过，设置为明天
            if (timeInMillis < System.currentTimeMillis()) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, 
                calendar.timeInMillis, 
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, 
                calendar.timeInMillis, 
                pendingIntent
            )
        }

        // 保存设置的时间
        context.getSharedPreferences("task", Context.MODE_PRIVATE).edit()
            .putInt("hour", hour)
            .putInt("minute", minute)
            .apply()
    }

    fun startTask() {
        val intent = Intent(context, TaskService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun getSavedTime(): Pair<Int, Int> {
        val sharedPreferences = context.getSharedPreferences("task", Context.MODE_PRIVATE)
        val hour = sharedPreferences.getInt("hour", 8)
        val minute = sharedPreferences.getInt("minute", 0)
        return Pair(hour, minute)
    }

    fun cancelTask() {
        val intent = Intent(context, TaskReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}