package kr.ac.kau.sw.a2016125063.myapplication

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import java.lang.*

/**
 * Created by Arduino on 2017-11-03.
 */
class AppLockService : Service(){
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("Service : ","onCreate")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service : ","onStartCommand")

        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                var am = ActivityManager.RunningTaskInfo()
                Log.d("taskNum : ", Integer.toString(am.numRunning))
                Log.d("top activity : ", am.topActivity.toString())



            }
        }


        timer.schedule(task, 0, 1000)  // delay 초 후 run을 실행하고 period/1000초마다 실행
        return super.onStartCommand(intent, flags, startId)



    }

}