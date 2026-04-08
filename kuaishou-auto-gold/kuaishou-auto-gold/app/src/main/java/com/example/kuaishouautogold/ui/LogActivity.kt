package com.example.kuaishouautogold.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kuaishouautogold.R

class LogActivity : AppCompatActivity() {

    private lateinit var tvLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        tvLog = findViewById(R.id.tv_log)

        val logContent = intent.getStringExtra("log_content") ?: ""
        tvLog.text = if (logContent.isNotEmpty()) logContent else "暂无日志"
    }
}