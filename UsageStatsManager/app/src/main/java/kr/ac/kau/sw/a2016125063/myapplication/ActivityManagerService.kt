package kr.ac.kau.sw.a2016125063.myapplication

import android.app.ActivityManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.util.*
import android.content.ContentValues.TAG







/**
 * Created by Arduino on 2017-10-20.
 */
class ActivityManagerService : Service(){

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("ActivityManagerService","onCreate")
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                try{
                    val am = this@ActivityManagerService.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
                    val tasks = am!!.getAppTasks()

                    for (task in tasks) {
                        var packagename = task.taskInfo.baseIntent.component!!.packageName
                        var label = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA)).toString().toInt()
                        Log.v(TAG, packagename + ":" + label)
                    }
                    /*
                    val am = this@ActivityManagerService.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
                    val tasks = am!!.getAppTasks()
                    for (task in tasks) {
                        Log.d(TAG, "stackId: " + task.getTaskInfo().topActivity)
                    }*/

                }catch(e : Exception){

                }



            }
        }


        timer.schedule(task, 0, 1000)  // delay 초 후 run을 실행하고 period/1000초마다 실행





    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {







        return super.onStartCommand(intent, flags, startId)
    }


}