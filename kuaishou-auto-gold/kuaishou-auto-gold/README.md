# 快手极速版自动领金币

一个Android应用，可以自动登录快手极速版并领取金币。

## 功能特点

- 完全自动登录（保存账号密码）
- 固定时间自动执行任务
- 使用无障碍服务模拟用户操作
- 任务执行日志记录
- 任务完成通知提醒

## 项目结构

```
kuaishou-auto-gold/
├── app/                      # 应用模块
│   ├── src/
│   │   └── main/
│   │       ├── java/         # Kotlin源代码
│   │       ├── res/          # 资源文件
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 构建说明

### 方法1：使用Android Studio构建（推荐）

1. 安装Android Studio
2. 打开Android Studio
3. 选择 "Open an existing project"
4. 导航到项目目录并选择
5. 等待Gradle同步完成
6. 点击 "Build" -> "Build Bundle(s) / APK(s)" -> "Build APK(s)"
7. 构建完成后，点击通知中的 "locate" 找到APK文件

### 方法2：使用命令行构建

确保已安装：
- JDK 8+
- Android SDK
- Android SDK Build-Tools 33.0.0

在项目根目录执行：
```bash
./gradlew assembleDebug
```

APK文件将生成在：`app/build/outputs/apk/debug/app-debug.apk`

## 使用说明

1. 安装APK到手机
2. 在系统设置中开启"快手自动领金币"无障碍服务
3. 在应用中输入快手极速版的账号和密码
4. 设置每天自动执行任务的时间
5. 点击"立即执行任务"按钮或等待定时执行

## 注意事项

- 应用需要无障碍服务权限和存储权限
- 账号密码会保存在本地，建议使用专用账号
- 应用会模拟真实用户操作，避免过度操作导致账号被封禁
- 如果快手极速版更新了界面，可能需要更新应用
- 建议将应用加入电池优化白名单，确保定时任务能够正常执行

## 许可证

本项目仅供学习交流使用。
