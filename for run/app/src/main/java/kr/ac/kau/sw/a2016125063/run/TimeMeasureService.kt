package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.util.*

/**
 * Created by 이은솔 on 2017-09-28.
 * 출처//http://bitsoul.tistory.com/147
 * https://stackoverflow.com/questions/44789492/convert-indefinitely-running-runnable-from-java-to-kotlin
 */
class TimeMeasureService: Service() {//서비스가 죽지 않게 만들기
var time: Int = 0//어플시작 시간 측정
    var app: String = ""//어플 관리
    var  dbHelper: DBHelper? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        dbHelper = DBHelper(this.baseContext, "Settings.db", null, 1)//DB다루기 위한 관리자
        Log.d("TimeMeasureService","onCreate")
    }

    var count: Int = 0//test용
    val timer = Timer()
    val monitor = object: TimerTask(){
        override fun run() {
            Log.d("timer",count.toString())
            count++

            val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
            val topActivity: ComponentName = info.get(0).topActivity
            val activityName = topActivity.packageName
            //Log.d("service",activityName)

            val usage = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 1000*1000
            val stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, beginTime, endTime)
            if (stats != null) {
                //사용된 앱 목록만 집어넣기
                //var usedApps: ArrayList<UsageStats> = ArrayList<UsageStats>()
                var usedApps = ArrayList<Pair<String,Long>>()
                for (usageStats in stats) {
                    //사용 시간 0인 것들 골라내기
                    if (usageStats.totalTimeInForeground >= 1000L) {
                        usedApps.add(Pair(usageStats.packageName, usageStats.lastTimeUsed))
                    }
                }
                //누적 사용시간으로 정렬
                usedApps.sortBy { it.second }
                Log.d("endTime --> ",endTime.toString())
                println("the latest used app --> "+usedApps[usedApps.size-1].first+"  //  time --> "+usedApps[usedApps.size-1].second)
                //Log.d("used App amount ", usedApps.size.toString())
            }


            if(app != activityName){
                if(app != "") {//탑액티비티의 이름이 달라질때 마다 실행
                    //val acTime = dbHelper!!.getTime(app)
                    //dbHelper!!.updateTime(Pair(app, acTime + ((System.currentTimeMillis() / 1000).toInt() - time)))
                }
                app = activityName
                time = (System.currentTimeMillis()/1000).toInt()

                val top = getForegroundPackageNameClassNameByUsageStats()
                Log.d("getLauncherTopApp : ",
                        getLauncherTopApp(this@TimeMeasureService, getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager))
                Log.d(ContentValues.TAG,top[0])
                getForegroundActivity()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimeMeasureService","onStartCommand")
        //if(!isServiceRunningCheck()) {
            //timer.schedule(monitor,0,1000)
        //}
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

    fun isServiceRunningCheck(): Boolean{
        //작동중인 서비스 확인
        //출처: http://darkcher.tistory.com/184
        val manager: ActivityManager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if("kr.ac.kau.sw.a2016125063.preventsnsaddiction.TimeMeasureService".equals(service.service.className.toString())){
                Log.d("service running check","service is running")
                return true
            }
        }
        Log.d("service running check","service is not running")
        return false
    }

    //출처: https://stackoverflow.com/questions/42942986/android-6-0-usageevents-method-usagestatsmanger-queryevents-is-giving-coun
    fun getForegroundActivity() {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 5000
        val usageStatsManager: UsageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        if (usageStatsManager != null) {
            val queryEvents: UsageEvents = usageStatsManager.queryEvents(beginTime, endTime)

            if (queryEvents != null) {
                var event: UsageEvents.Event
                while (queryEvents.hasNextEvent()) {
                    var eventAux: UsageEvents.Event = UsageEvents.Event()
                    queryEvents.getNextEvent(eventAux)
                    if (eventAux.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        event = eventAux;
                        Log.d("getForeground",event.packageName)
                        Log.d("getForeground",event.className)
                    }
                }
            }
        }
    }
}