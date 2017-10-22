package kr.ac.kau.sw.a2016125063.myapplication

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.content.ContentValues.TAG
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context.USAGE_STATS_SERVICE
import android.support.v4.content.ContextCompat.startActivity
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.PermissionChecker.checkCallingOrSelfPermission
import android.content.Context.APP_OPS_SERVICE
import android.os.IBinder
import android.util.Log
import java.util.*


/**
 * Created by Arduino on 2017-10-20.
 */
class UsageService : Service(){

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // GET_USAGE_STATS 권한 확인
        var granted = false
        val appOps = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), this.getPackageName())

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) === PackageManager.PERMISSION_GRANTED
        } else {
            granted = mode == AppOpsManager.MODE_ALLOWED
        }

        Log.e(TAG, "===== CheckPhoneState isRooting granted = " + granted)

        if (granted == false) {
            // 권한이 없을 경우 권한 요구 페이지 이동
            val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
            this.startActivity(intent)
        }

        if (!false) {
            // 기타 프로세스 목록 확인
            val usage = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (stats != null) {
                val runningTask = TreeMap<Long, UsageStats>()
                for (usageStats in stats) {
                    runningTask.put(usageStats.lastTimeUsed, usageStats)

                    Log.i(TAG, "===== CheckPhoneState isRooting packageName = " + usageStats.packageName)
                }
            } else {
                Log.i(TAG, "===== CheckPhoneState isRooting stats is NULL")
            }
        }



        return super.onStartCommand(intent, flags, startId)

    }


}