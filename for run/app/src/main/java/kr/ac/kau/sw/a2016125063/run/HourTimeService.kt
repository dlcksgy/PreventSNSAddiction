package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by 이은솔 on 2017-12-24.
 * 시간이 저장이 되지 않기 때문에 서비스를 불러서 저장시키고 종료시킴.
 */
class HourTimeService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("HourTimeService","onCreate")
        val dbHelper = DBHelper(this.baseContext, "Settings.db", null, 1)
        val time = hourMinute()
        dbHelper.updateHourTime(Pair(time.second, dbHelper.getTimeSum()))
        println("updatedTime == "+dbHelper.getTimeSum())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HourTimeService","onDestory")
    }

    //현재 시간 출력하기
    //출처: https://medium.com/@peteryun/android-how-to-print-current-date-and-time-in-java-45b884917c6f
    fun hourMinute(): Pair<Int,Int>{
        var date = Date()
        val sdf_ampm = SimpleDateFormat("a").format(date).toString()
        val sdf_m = SimpleDateFormat("mm").format(date).toInt()
        var sdf_h = SimpleDateFormat("hh").format(date).toInt()
        if(sdf_ampm == "오후") sdf_h += 12
        if(sdf_h == 12 && sdf_ampm == "오전") sdf_h = 0
        println("시 = ${sdf_h}, 분 = ${sdf_m}, "+sdf_ampm)

        return Pair(sdf_m, sdf_h)
    }
}