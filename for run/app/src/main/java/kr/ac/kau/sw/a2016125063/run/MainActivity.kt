package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import java.lang.reflect.Array
import java.util.*

class MainActivity : AppCompatActivity() {

    var listViewAdapter: ListViewAdapter = ListViewAdapter(ArrayList<ListViewItem>())

    fun tableAndItem(): ArrayList<ListViewItem>{
        //MainActiivty의 그래프를 그리기 위해 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //내 핸드폰 내의 앱 리스트 받아오기(앱 아이콘,패키지명,이름)
        //출처//http://blog.naver.com/pluulove84/100153350054
        var appDataList = ArrayList<ListViewItem>()//커스텀 리스트뷰에 사용
        var packageNameList = ArrayList<String>()//time 테이블 생성에 사용
        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS)

        //time table이 없으면 만들고 초기화
        if(dbHelper.getTimeElementCount() == 0){
            for(app:ApplicationInfo in packs){
                packageNameList.add(app.packageName.toString())
            }
            dbHelper.initializeTime(packageNameList)
        }
        for(app:ApplicationInfo in packs){
            val acTime = dbHelper.getTime(app.packageName.toString())
            val hour = if((acTime/3600)%10 > 0) (acTime/3600).toString() else "0"+(acTime/3600).toString()
            val minute = if(((acTime/60)%60)%10 > 0) ((acTime/60)%60).toString() else "0"+((acTime/60)%60).toString()
            val second = if((acTime%60)%10 > 0) (acTime%60).toString() else "0"+(acTime%60).toString()
            val time = hour+":"+minute+":"+second
            appDataList.add(ListViewItem(app.loadIcon(pm), app.loadLabel(pm).toString(), app.packageName.toString()))
        }


        /*
        * 시간 0초인것들 골라 없애기
        * usageStatsManager의 결과로 listView 채우기
        * 시간 많은 순으로 정렬하기
        * DB 손봐서 누적시간 데이터 없이 앱 목록 패키지 명만 입력하기
        * option DB에 접속해서 앱 시간 제한이 걸려있고 시간이 정해져 있으면 그 시간과 현재까지의 누적시간을 비교하여 남은시간 바꿔주기
        * 여유가 된다면 그래프 그려넣기
        */

        // 기타 프로세스 목록 확인
        val usage = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
        if (stats != null) {
            Log.i(ContentValues.TAG, "===== CheckPhoneState isRooting stats is not NULL")
            Log.d("stats !!!!!",stats.toString())
            val runningTask = TreeMap<Long, UsageStats>()
            var allAccumulatedTime: Long = 0
            for (usageStats in stats) {
                runningTask.put(usageStats.lastTimeUsed, usageStats)
                allAccumulatedTime += usageStats.totalTimeInForeground
                //사용시간 측정값 로그로 출력
                /*Log.i(TAG, "==== packageName = "
                        + usageStats.packageName
                        + "  # Time measured: " + usageStats.getTotalTimeInForeground() + "millisec")*/
            }
            Log.d("allAccumulatedTime",(allAccumulatedTime/1000).toString()+" sec")

            val sortedList = stats.toList().sortedBy{ it.packageName }
            Log.d("sortedList size (Usage)",sortedList.size.toString())
            /*
            for(i in sortedList){
                Log.i(ContentValues.TAG, "==== packageName = "
                        + i.packageName
                        + "  # Time measured: " + i.getTotalTimeInForeground() + "millisec")
            }
            */
        } else {
            Log.i(ContentValues.TAG, "===== CheckPhoneState isRooting stats is NULL")
        }


        //출처//https://www.programiz.com/kotlin-programming/examples/sort-custom-objects-property
        var sortedList = appDataList.sortedWith(compareBy({it.accumulatedTime}))
        for(i:ListViewItem in sortedList){
            Log.d("sortedList",i.accumulatedTime)
        }
        Log.d("sortedList size (app)",sortedList.size.toString())

        appDataList.find { it.accumulatedTime != "00:00:00" }

        appDataList.sortBy { it.appName }
        return appDataList
    }

    fun isServiceRunningCheck(): Boolean{
        //작동중인 서비스 확인
        //출처: http://darkcher.tistory.com/184
        val manager: ActivityManager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if("kr.ac.kau.sw.a2016125063.preventsnsaddiction.TimeMeasureService".equals(service.service.className.toString())){
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // GET_USAGE_STATS 권한 확인
        val appOps = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), this.getPackageName())
        var granted = if (mode == AppOpsManager.MODE_DEFAULT) {
            this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            mode == AppOpsManager.MODE_ALLOWED
        }
        Log.e(ContentValues.TAG, "===== CheckPhoneState isRooting granted = " + granted)
        if (granted == false) {
            // 권한이 없을 경우 권한 요구 페이지 이동
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val optionButton = findViewById<Button>(R.id.option_button)
        optionButton.setOnClickListener{
            val intent = Intent(this@MainActivity, OptionActivity::class.java)
            startActivity(intent)
        }

        //서비스가 작동중이면 서비스 추가x
        if(isServiceRunningCheck() == false) {
            val i = Intent(applicationContext, TimeMeasureService::class.java)
            applicationContext.startService(i)
            Log.d("API checking", Build.VERSION.RELEASE.toString())
        }
    }

    override fun onResume(){
        Log.d("onResume","update")
        super.onResume()

        //커스텀 리스트뷰 만들기
        //출처//http://recipes4dev.tistory.com/43
        //listView 설정
        var listView: ListView = findViewById<ListView>(R.id.app_list)
        //어뎁터 생성
        //var listViewAdapter: ListViewAdapter = ListViewAdapter(appDataList)
        listViewAdapter = ListViewAdapter(tableAndItem())
        //어뎁터 설정
        listView.adapter = listViewAdapter
        //아이템 추가
        listViewAdapter.notifyDataSetChanged()

        /*임시방편으로 버그 해결
        *스크롤을 움직여야만 리스트뷰에 아이템이 차므로 처음 리스트뷰의 아이템이 보이는 위치를
        *내 화면보다 아래로 설정하고 그 다음 스크롤을 올려줘서 해결함
        *출처: https://stackoverflow.com/questions/3503133/listview-scroll-to-selected-item
        */
        listView.setSelection(9)
        listView.smoothScrollToPosition(0)
    }
}