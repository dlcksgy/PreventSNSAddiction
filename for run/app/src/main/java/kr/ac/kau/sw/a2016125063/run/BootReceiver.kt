package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by 이은솔 on 2017-09-28.
 * 출처// http://ccdev.tistory.com/27
 */
class BootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("BroadcastReceiver","onReceive")
        val action = intent?.getAction()
        Log.d("BroadcastReceiverAction",action)
        //작동 안되는 이유를 찾음.
        //출처: https://blog.naver.com/chazlqhemsks/10183683280
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            val i = Intent(context, TimeMeasureService::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startService(i)
            Log.d("BootReceiver","BOOT_COMPLETED")
        }
        if(action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            val i = Intent(context, TimeMeasureService::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startService(i)
            Log.d("BootReceiver", "PACKAGE_ADDED")
        }
        if(action.equals(Intent.ACTION_PACKAGE_REPLACED)){
            val i = Intent(context, TimeMeasureService::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startService(i)
            Log.d("BootReceiver","PACKAGE_REPLACED")
        }
        if(action.equals("flag")){
            Log.d("broadcast","flag catched")
        }
    }
}