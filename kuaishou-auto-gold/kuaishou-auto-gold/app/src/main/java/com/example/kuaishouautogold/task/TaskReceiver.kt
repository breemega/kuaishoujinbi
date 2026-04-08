package com.example.kuaishouautogold.task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kuaishouautogold.service.TaskService

class TaskReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 启动任务服务
        val serviceIntent = Intent(context, TaskService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        
        // 重新设置明天的任务
        val taskScheduler = TaskScheduler(context)
        val time = taskScheduler.getSavedTime()
        taskScheduler.scheduleTask(time.first, time.second)
    }
}