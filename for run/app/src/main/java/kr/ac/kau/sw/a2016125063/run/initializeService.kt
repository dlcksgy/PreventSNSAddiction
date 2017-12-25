package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Service
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import java.util.ArrayList

/**
 * Created by 이은솔 on 2017-12-25.
 */
class initializeService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //내부의 테이블을 드랍, 새로 생성
        //드랍하면 시간을 누적하는데 문제가 생길 가능 성이 있기 대문에 시간이 좀 지나더라도 1개씩 초기화 해준다.
        val dbHelper = DBHelper(this.baseContext, "Settings.db", null, 1)

        var packageNameList = ArrayList<String>()//초기화 목록
        val pm: PackageManager = packageManager
        val packs = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for(app: ApplicationInfo in packs){
            packageNameList.add(app.packageName.toString())
            println(app.packageName.toString())
        }
        dbHelper.initializeAcTime(packageNameList)
        Log.d("initializeService","time reset")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("initializeService","onDestroy")
    }
}