package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.widget.Toast
import java.util.*

/**
 * Created by 이은솔 on 2017-09-28.
 * 출처//http://bitsoul.tistory.com/147
 * https://stackoverflow.com/questions/44789492/convert-indefinitely-running-runnable-from-java-to-kotlin
 */
class TimeMeasureService: Service() {//서비스가 죽지 않게 만들기
    var time: Int = 0//어플시작 시간 측정
    var app: String = ""//어플 관리
    var dbHelper: DBHelper? = null

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if(msg.arg1 == 0){
            }
            if (msg.arg1 == 1) {
                Toast.makeText(applicationContext, "빼애애애애애애애애애애애애애액", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        dbHelper = DBHelper(this.baseContext, "Settings.db", null, 1)//DB다루기 위한 관리자
        Log.d("TimeMeasureService","onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service : ","onStartCommand")
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                Log.d("run : ","task running")
                val top = getForegroundPackageNameClassNameByUsageStats()
                Log.d("getLauncherTopApp : ",
                        getLauncherTopApp(this@TimeMeasureService, getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager))
                Log.d("",top[0])

                val temp = getLauncherTopApp(this@TimeMeasureService, getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager)
                if(app != temp){
                    if(temp == "failed"){//앱이 그대로 사용중임
                        //스레드에서 토스트메시지를 직접 띄울 수 없기 때문에 핸들러를 이용
                        // 출처 : https://stackoverflow.com/questions/7185942/error-while-dispaying-an-toast-message-cant-create-handler-inside-thread-that
                        if(OptionActivity.appLimitList.indexOf(app) != -1) {//제한된 앱을 사용중일 때
                            val msg = handler.obtainMessage()
                            msg.arg1 = OptionActivity.timeLimitSetting
                            handler.sendMessage(msg)
                            //인텐드 각종 플래그 태그들
                            //출처//http://theeye.pe.kr/archives/1298
                            val intent: Intent = Intent(applicationContext, LockActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                            startActivity(intent)
                        }
                    }else{//다른 앱으로 바뀜
                        app = temp
                    }
                }
            }
        }
        timer.schedule(task, 0, 5000)  // delay 초 후 run을 실행하고 period/1000초마다 실행
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
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

        return "failed"
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
                    Log.d(ContentValues.TAG, "packageNameByUsageStats is$packageNameByUsageStats, classByUsageStats is $classByUsageStats")
                }
            }
        }
        return arrayOf<String>(packageNameByUsageStats, classByUsageStats)
    }
}