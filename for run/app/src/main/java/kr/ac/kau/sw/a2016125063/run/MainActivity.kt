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
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import kr.ac.kau.sw.a2016125063.run.DialogItem
import java.util.*

class MainActivity : AppCompatActivity() {
    //custom adapter
    var listViewAdapter: ListViewAdapter? = null
    //시간 제한한 앱들의 누적 사용 시간
    var spentTime: Long = 0

    fun makeStringTime(sec: Int): String{
        val hour = if(sec/3600 >= 10) (sec/3600).toString() else "0"+(sec/3600).toString()
        val minute = if((sec/60)%60 >= 10) ((sec/60)%60).toString() else "0"+((sec/60)%60).toString()
        val second = if(sec%60 >= 10) (sec%60).toString() else "0"+(sec%60).toString()
        val stringTime = hour+":"+minute+":"+second
        return stringTime
    }

    fun tableAndItem(): ArrayList<ListViewItem>{
        //DB접근에 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //내 핸드폰 내의 앱 리스트 받아오기(앱 아이콘,패키지명,이름)
        //출처//http://blog.naver.com/pluulove84/100153350054
        var appDataList = ArrayList<ListViewItem>()//커스텀 리스트뷰에 사용

        var packageNameList = ArrayList<String>()//time 테이블 생성에 사용

        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS)
        /*
        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_META_DATA)
*/
        //모두 정돈된 데이터가 들어갈 리스트 (이 함수의 반환값)
        var sortedList = ArrayList<ListViewItem>()

        //time table이 없으면 만들고 초기화
        if(dbHelper.getTimeElementCount() == 0){
            for(app:ApplicationInfo in packs){
                packageNameList.add(app.packageName.toString())
            }
            dbHelper.initializeTime(packageNameList)
        }

        for(app:ApplicationInfo in packs){
            appDataList.add(ListViewItem(app.loadIcon(pm), app.loadLabel(pm).toString(), app.packageName.toString()))
        }

        /*
        * 시간 많은 순으로 정렬하기
        * DB 손봐서 누적시간 데이터 없이 앱 목록 패키지 명만 입력하기, DB 정돈
        * 여유가 된다면 그래프 그려넣기
        */

        // 기타 프로세스 목록 확인
        val usage = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
        if (stats != null) {
            Log.i(ContentValues.TAG, "===== CheckPhoneState isRooting stats is not NULL")
            Log.d("stats !!!!!",stats.toString())
            //누적시간 초기화
            spentTime = 0L
            //사용된 앱 목록만 집어넣기
            var usedApps: ArrayList<UsageStats> = ArrayList<UsageStats>()
            for (usageStats in stats) {
                spentTime += usageStats.totalTimeInForeground
                //Log.d("last time",usageStats.lastTimeUsed.toString())
                //마지막에 사용된 시간, 총 사용시간
                print("last time used --> ${usageStats.lastTimeUsed}  ")
                println("///  used time --> ${usageStats.totalTimeInForeground/1000L}")
                //사용 시간 0인 것들 골라내기

                if(usageStats.totalTimeInForeground != 0L){
                    usedApps.add(usageStats)
                }
                //사용시간 측정값 로그로 출력
                /*Log.i(TAG, "==== packageName = "
                        + usageStats.packageName
                        + "  # Time measured: " + usageStats.getTotalTimeInForeground() + "millisec")*/
            }
            Log.d("allAccumulatedTime",(spentTime/1000).toString()+" sec")
            //누적 사용시간으로 정렬
            usedApps.sortBy { it.packageName }
            Log.d("used App count ",usedApps.size.toString())
            //appDataList 정렬 뒤 비교하면서 패키지를 찾고 시간을 넣어줌.
            appDataList.sortBy { it.accumulatedTime } //일단은 acTime에 package이름이 들어있음

            //비교, 입력
            var i:Int = usedApps.size-1
            var k:Int = appDataList.size-1
            while(i>=0 && k>=0){
                //패키지명이 같을 때
                if(usedApps[i].packageName == appDataList[k].accumulatedTime){
                    val acTime = ((usedApps[i].totalTimeInForeground)/1000L).toInt()
                    appDataList[k].accumulatedTime = makeStringTime(acTime)
                    sortedList.add(appDataList[k])
                    i -= 1; k -= 1
                }else{//패키지명이 다를 때
                    k -= 1
                }
            }
            //어플 이름으로 정렬

            sortedList.sortBy { it.appName }
        } else {
            Log.i(ContentValues.TAG, "===== CheckPhoneState isRooting stats is NULL")
        }

        for(i in sortedList){
            Log.d("sortedList",i.accumulatedTime)
        }

        //출처//https://www.programiz.com/kotlin-programming/examples/sort-custom-objects-property
        /*
        var sortedList = appDataList.sortedWith(compareBy({it.accumulatedTime}))
        for(i:ListViewItem in sortedList){
            Log.d("sortedList",i.accumulatedTime)
        }
        Log.d("sortedList size (app)",sortedList.size.toString())
        */

        //appDataList.find { it.accumulatedTime != "00:00:00" }
        //appDataList.sortBy { it.appName }

        return sortedList
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
            //applicationContext.startService(i)
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
        listViewAdapter = ListViewAdapter(this, tableAndItem())
        //어뎁터 설정
        listView.adapter = listViewAdapter
        //아이템 추가
        //listViewAdapter!!.notifyDataSetChanged()

        /*임시방편으로 버그 해결
        *스크롤을 움직여야만 리스트뷰에 아이템이 차므로 처음 리스트뷰의 아이템이 보이는 위치를
        *내 화면보다 아래로 설정하고 그 다음 스크롤을 올려줘서 해결함
        *출처: https://stackoverflow.com/questions/3503133/listview-scroll-to-selected-item
        */
        //listView.setSelection(9)
        //listView.smoothScrollToPosition(0)

        //시간을 가리키는 글
        val signTime = findViewById<TextView>(R.id.left_or_acculumated)
        //남은 시간 textview
        val leftTimeTextview = findViewById<TextView>(R.id.calculated_left_time)
        //DB접근에 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)
        //option 값 1.시간제한 셋팅 2.셀카제한 3.앱제한 4.시간 5.분 6.초 7.초기화 시 8.초기화 분
        val optionValue = dbHelper.getSettings()
        if(optionValue[0] == 1){//시간 제한이 걸려 있을 때
            signTime.setText("남은 시간")
            val limitTime = optionValue[3]*3600 + optionValue[4]*60 + optionValue[5]
            val leftTime = limitTime - spentTime.toInt()
            if(leftTime > 0){//시간이 남아 있다면
                leftTimeTextview.setText(makeStringTime(leftTime))
            }else{//시간을 다 썼다면
                leftTimeTextview.setText("00:00:00")
            }
        }else{//시간제한이 풀려 있을 때 누적시간을 보여줌
            signTime.setText("총 누적 시간")
            leftTimeTextview.setText(makeStringTime((spentTime/1000).toInt()))
        }
    }
}