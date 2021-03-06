package kr.ac.kau.sw.a2016125063.working


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
import android.app.ActivityManager.RunningTaskInfo
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.ACTIVITY_SERVICE











/**
 * Created by Arduino on 2017-10-20.
 */
class ActivityManagerService : Service(){

    val mContext = this
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
                        Log.v(TAG, packagename + "탴 : " + label)
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

        val activityManager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info: List<RunningTaskInfo>
        info = activityManager.getRunningTasks(7)
        val iterator = info.iterator()


        while (iterator.hasNext()) {
            val runningTaskInfo = iterator.next() as RunningTaskInfo
            Log.e("runningTask", runningTaskInfo.topActivity.packageName)

        }

        //doraeul.tistory.com/61 [도래울]




        return super.onStartCommand(intent, flags, startId)
    }


}