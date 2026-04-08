package com.example.kuaishouautogold.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kuaishouautogold.R
import com.example.kuaishouautogold.login.AccountManager
import com.example.kuaishouautogold.task.TaskScheduler
import com.example.kuaishouautogold.utils.LogUtils

class MainActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etHour: EditText
    private lateinit var etMinute: EditText
    private lateinit var btnSaveAccount: Button
    private lateinit var btnSaveTime: Button
    private lateinit var btnAccessibility: Button
    private lateinit var btnStartTask: Button
    private lateinit var btnViewLog: Button
    private lateinit var tvAccessibilityStatus: TextView

    private val accountManager by lazy { AccountManager(this) }
    private val taskScheduler by lazy { TaskScheduler(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadSavedAccount()
        loadSavedTime()
        checkAccessibilityStatus()
        setupListeners()
    }

    private fun initViews() {
        etPhone = findViewById(R.id.et_phone)
        etPassword = findViewById(R.id.et_password)
        etHour = findViewById(R.id.et_hour)
        etMinute = findViewById(R.id.et_minute)
        btnSaveAccount = findViewById(R.id.btn_save_account)
        btnSaveTime = findViewById(R.id.btn_save_time)
        btnAccessibility = findViewById(R.id.btn_accessibility)
        btnStartTask = findViewById(R.id.btn_start_task)
        btnViewLog = findViewById(R.id.btn_view_log)
        tvAccessibilityStatus = findViewById(R.id.tv_accessibility_status)
    }

    private fun loadSavedAccount() {
        val account = accountManager.getAccount()
        etPhone.setText(account.phone)
        etPassword.setText(account.password)
    }

    private fun loadSavedTime() {
        val time = taskScheduler.getSavedTime()
        etHour.setText(time.first.toString())
        etMinute.setText(time.second.toString())
    }

    private fun checkAccessibilityStatus() {
        val isEnabled = isAccessibilityServiceEnabled()
        tvAccessibilityStatus.text = if (isEnabled) "无障碍服务已开启" else "无障碍服务未开启"
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver, 
            Settings.Secure.ACCESSIBILITY_ENABLED, 
            0
        )
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                contentResolver, 
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return services != null && services.contains(packageName + ".service.KuaishouAccessibilityService")
        }
        return false
    }

    private fun setupListeners() {
        btnSaveAccount.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入手机号和密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            accountManager.saveAccount(phone, password)
            Toast.makeText(this, "账号保存成功", Toast.LENGTH_SHORT).show()
        }

        btnSaveTime.setOnClickListener {
            val hourStr = etHour.text.toString().trim()
            val minuteStr = etMinute.text.toString().trim()
            if (hourStr.isEmpty() || minuteStr.isEmpty()) {
                Toast.makeText(this, "请输入执行时间", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val hour = hourStr.toIntOrNull() ?: 0
            val minute = minuteStr.toIntOrNull() ?: 0
            if (hour !in 0..23 || minute !in 0..59) {
                Toast.makeText(this, "请输入有效的时间", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            taskScheduler.scheduleTask(hour, minute)
            Toast.makeText(this, "定时设置成功", Toast.LENGTH_SHORT).show()
        }

        btnAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnStartTask.setOnClickListener {
            if (!isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "请先开启无障碍服务", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val account = accountManager.getAccount()
            if (account.phone.isEmpty() || account.password.isEmpty()) {
                Toast.makeText(this, "请先保存账号密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 启动任务服务
            taskScheduler.startTask()
            Toast.makeText(this, "任务开始执行", Toast.LENGTH_SHORT).show()
        }

        btnViewLog.setOnClickListener {
            val logContent = LogUtils.getLogContent()
            val intent = Intent(this, LogActivity::class.java)
            intent.putExtra("log_content", logContent)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        checkAccessibilityStatus()
    }
}