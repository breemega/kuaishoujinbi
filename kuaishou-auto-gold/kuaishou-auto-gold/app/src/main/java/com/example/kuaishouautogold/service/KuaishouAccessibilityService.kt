package com.example.kuaishouautogold.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.kuaishouautogold.login.AccountManager
import com.example.kuaishouautogold.utils.LogUtils

class KuaishouAccessibilityService : AccessibilityService() {

    private val accountManager by lazy { AccountManager(this) }
    private val handler = Handler(Looper.getMainLooper())
    private var isTaskRunning = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtils.log("无障碍服务已连接")
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_DEFAULT or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            packageNames = arrayOf("com.kuaishou.nebula")
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isTaskRunning) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event)
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                handleViewScrolled(event)
            }
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                handleViewTextChanged(event)
            }
        }
    }

    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        
        // 处理登录界面
        if (event.className == "com.kuaishou.nebula.ui.login.LoginActivity") {
            handleLogin(rootNode)
        }
        
        // 处理主页
        else if (event.className == "com.kuaishou.nebula.ui.main.MainActivity") {
            handleMainPage(rootNode)
        }
    }

    private fun handleLogin(rootNode: AccessibilityNodeInfo) {
        val account = accountManager.getAccount()
        if (account.phone.isEmpty() || account.password.isEmpty()) {
            LogUtils.log("账号信息为空，无法登录")
            return
        }

        // 查找手机号输入框
        val phoneNode = findNodeByText(rootNode, "请输入手机号")
        phoneNode?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            val intent = Intent(Intent.ACTION_INSERT).apply {
                putExtra(Intent.EXTRA_TEXT, account.phone)
            }
            performGlobalAction(GLOBAL_ACTION_BACK)
            handler.postDelayed({ 
                it.performAction(AccessibilityNodeInfo.ACTION_PASTE)
            }, 500)
        }

        // 查找密码输入框
        val passwordNode = findNodeByText(rootNode, "请输入密码")
        passwordNode?.let {
            handler.postDelayed({ 
                it.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    putExtra(Intent.EXTRA_TEXT, account.password)
                }
                performGlobalAction(GLOBAL_ACTION_BACK)
                handler.postDelayed({ 
                    it.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                }, 500)
            }, 1000)
        }

        // 查找登录按钮
        val loginNode = findNodeByText(rootNode, "登录")
        loginNode?.let {
            handler.postDelayed({ 
                it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                LogUtils.log("已点击登录按钮")
            }, 1500)
        }
    }

    private fun handleMainPage(rootNode: AccessibilityNodeInfo) {
        // 查找金币任务入口
        val goldNode = findNodeByText(rootNode, "金币")
        goldNode?.let {
            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            LogUtils.log("已点击金币入口")
            handler.postDelayed({ 
                handleGoldPage(rootNode)
            }, 1000)
        }
    }

    private fun handleGoldPage(rootNode: AccessibilityNodeInfo) {
        // 查找并点击所有可领取金币的任务
        val taskNodes = findNodesByText(rootNode, "领取")
        taskNodes.forEachIndexed { index, node ->
            handler.postDelayed({ 
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                LogUtils.log("已点击领取按钮")
            }, (index * 500).toLong())
        }

        // 处理完任务后返回主页
        handler.postDelayed({ 
            performGlobalAction(GLOBAL_ACTION_BACK)
            isTaskRunning = false
            LogUtils.log("任务执行完成")
        }, (taskNodes.size * 500 + 1000).toLong())
    }

    private fun handleViewScrolled(event: AccessibilityEvent) {
        // 处理滚动事件，用于查找更多任务
    }

    private fun handleViewTextChanged(event: AccessibilityEvent) {
        // 处理文本变化事件，用于监控登录状态
    }

    private fun findNodeByText(rootNode: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        return if (nodes.isNotEmpty()) nodes[0] else null
    }

    private fun findNodesByText(rootNode: AccessibilityNodeInfo, text: String): List<AccessibilityNodeInfo> {
        return rootNode.findAccessibilityNodeInfosByText(text)
    }

    fun startTask() {
        isTaskRunning = true
        LogUtils.log("开始执行无障碍任务")
    }

    fun stopTask() {
        isTaskRunning = false
        LogUtils.log("停止执行无障碍任务")
    }

    override fun onInterrupt() {
        LogUtils.log("无障碍服务被中断")
    }
}