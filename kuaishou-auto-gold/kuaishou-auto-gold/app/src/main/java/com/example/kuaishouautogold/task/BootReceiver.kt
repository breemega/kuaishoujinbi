package com.example.kuaishouautogold.task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 设备开机后重新设置定时任务
            val taskScheduler = TaskScheduler(context)
            val time = taskScheduler.getSavedTime()
            taskScheduler.scheduleTask(time.first, time.second)
        }
    }
}