package kr.ac.kau.sw.a2016125063.working


import android.app.ActivityManager
import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import java.lang.*
import android.app.usage.UsageEvents
import android.content.Context.USAGE_STATS_SERVICE
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Message
import android.widget.Toast


/**
 * Created by Arduino on 2017-11-03.
 */
class AppLockService : Service(){

    private var activityManager: ActivityManager? = null

    val kakaoTalk = "com.kakao.talk"
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.arg1 == 1)
                Toast.makeText(applicationContext, "빼애애애애액", Toast.LENGTH_LONG).show()
        }
    }
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        activityManager = getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        Log.d("appLockService : ","onCreate")

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service : ","onStartCommand")

        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                Log.d("run : ","task running")
                val top = getForegroundPackageNameClassNameByUsageStats()
                Log.d("getLauncherTopApp : ",
                        getLauncherTopApp(this@AppLockService, getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager))
                Log.d(TAG,top[0])

                //스레드에서 토스트메시지를 직접 띄울 수 없기 때문에 핸들러를 이용
                // 출처 : https://stackoverflow.com/questions/7185942/error-while-dispaying-an-toast-message-cant-create-handler-inside-thread-that
                val msg = handler.obtainMessage()
                msg.arg1 = 1
                handler.sendMessage(msg)



            }
        }


        timer.schedule(task, 0, 4000)  // delay 초 후 run을 실행하고 period/1000초마다 실행
        return super.onStartCommand(intent, flags, startId)



    }


    //참고 : https://github.com/lizixian18/AppLock/blob/master/app/src/main/java/com/lzx/lock/service/LockService.java
    fun getLauncherTopApp(context: Context, activityManager: ActivityManager): String {

        //5.0以后需要用这方法  번역 -> 5.0이상부터는 이렇게 해야 함.
        Log.d("inner","getLauncherTopApp")
        val sUsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        if (!android.text.TextUtils.isEmpty(result)) {
            return result
        }

        return ""
    }


    // 참고 : https://stackoverflow.com/questions/38971472/using-usagestatsmanager-to-get-the-foreground-app
    fun getForegroundPackageNameClassNameByUsageStats(): Array<String> {
        var packageNameByUsageStats = "null"
        var classByUsageStats = "null"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val INTERVAL: Long = 10000
            val end = System.currentTimeMillis()
            val begin = end - INTERVAL
            val usageEvents = mUsageStatsManager.queryEvents(begin, end)
            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    packageNameByUsageStats = event.packageName
                    classByUsageStats = event.className
                    Log.d(TAG, "packageNameByUsageStats is$packageNameByUsageStats, classByUsageStats is $classByUsageStats")
                }
            }
        }
        return arrayOf<String>(packageNameByUsageStats, classByUsageStats)
    }







}