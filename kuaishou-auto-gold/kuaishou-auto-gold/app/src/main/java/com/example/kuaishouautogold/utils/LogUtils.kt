package com.example.kuaishouautogold.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogUtils {

    private const val LOG_FILE_NAME = "kuaishou_auto_gold.log"
    private const val MAX_LOG_SIZE = 1024 * 1024 // 1MB

    fun log(message: String) {
        val logMessage = "${getCurrentTime()} - $message\n"
        println(logMessage)
        writeToFile(logMessage)
    }

    private fun getCurrentTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

    private fun writeToFile(message: String) {
        try {
            val logFile = File(getLogFileDir(), LOG_FILE_NAME)
            // 检查文件大小，如果超过限制则清空
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                logFile.delete()
            }
            val writer = FileWriter(logFile, true)
            writer.write(message)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getLogFileDir(): File {
        val dir = File(Environment.getExternalStorageDirectory(), "kuaishou_auto_gold")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getLogContent(): String {
        val logFile = File(getLogFileDir(), LOG_FILE_NAME)
        if (!logFile.exists()) {
            return ""
        }
        return try {
            logFile.readText()
        } catch (e: IOException) {
            ""
        }
    }
}