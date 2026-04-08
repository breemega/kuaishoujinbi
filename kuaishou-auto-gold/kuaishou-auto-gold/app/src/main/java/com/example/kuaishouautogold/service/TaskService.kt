package com.example.kuaishouautogold.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.kuaishouautogold.R
import com.example.kuaishouautogold.login.AccountManager
import com.example.kuaishouautogold.utils.LogUtils
import com.example.kuaishouautogold.utils.NotificationUtils

class TaskService : Service() {

    private val accountManager by lazy { AccountManager(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // 启动前台服务
        startForeground(1, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 执行任务
        executeTask()
        return START_STICKY
    }

    private fun executeTask() {
        LogUtils.log("开始执行任务")
        
        try {
            // 获取账号信息
            val account = accountManager.getAccount()
            if (account.phone.isEmpty() || account.password.isEmpty()) {
                LogUtils.log("账号信息为空，任务执行失败")
                NotificationUtils.sendNotification(this, "任务执行失败", "账号信息为空")
                stopSelf()
                return
            }

            // 启动快手极速版
            val packageName = "com.kuaishou.nebula"
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
                LogUtils.log("已启动快手极速版")
            } else {
                LogUtils.log("未安装快手极速版，任务执行失败")
                NotificationUtils.sendNotification(this, "任务执行失败", "未安装快手极速版")
                stopSelf()
                return
            }

            // 任务执行完成后发送通知
            LogUtils.log("任务执行完成")
            NotificationUtils.sendNotification(this, "任务执行完成", "已完成金币领取任务")
        } catch (e: Exception) {
            LogUtils.log("任务执行异常: ${e.message}")
            NotificationUtils.sendNotification(this, "任务执行失败", "发生异常: ${e.message}")
        } finally {
            // 停止服务
            stopSelf()
        }
    }

    private fun createNotification(): Notification {
        val channelId = "task_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "任务服务",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        return Notification.Builder(this, channelId)
            .setContentTitle("快手自动领金币")
            .setContentText("正在执行任务")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}