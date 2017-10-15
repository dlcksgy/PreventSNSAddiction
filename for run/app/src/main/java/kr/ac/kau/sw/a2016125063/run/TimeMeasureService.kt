package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.ActivityManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*

/**
 * Created by 이은솔 on 2017-09-28.
 * 출처//http://bitsoul.tistory.com/147
 * https://stackoverflow.com/questions/44789492/convert-indefinitely-running-runnable-from-java-to-kotlin
 */
class TimeMeasureService: Service() {
    var count: Int = 0//test용
    val timer = Timer()
    val monitor = object: TimerTask(){
        override fun run() {
            Log.d("timer",count.toString())
            count++

            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
            val topActivity: ComponentName = info.get(0).topActivity
            val activityName = topActivity.packageName
            Log.d("service",activityName)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        Log.d("TimeMeasureService","onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimeMeasureService","onStartCommand")
        timer.schedule(monitor,0,1000)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}