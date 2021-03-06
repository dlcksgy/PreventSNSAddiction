package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.Manifest
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
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    //custom adapter
    var listViewAdapter: ListViewAdapter? = null
    //시간 제한한 앱들의 누적 사용 시간
    var spentTime: Long = 0

    private val CAMERA_PERMISSION_CODE = 2
    private val READ_EXTERNAL_STORAGE = 3

    fun makeStringTime(sec: Int): String{
        val hour = if(sec/3600 != 0) (sec/3600).toString()+"시간 " else ""
        val minute = if((sec/60)%60 != 0) ((sec/60)%60).toString()+"분 " else ""
        val second = if(sec%60 != 0) (sec%60).toString()+"초 " else ""
        val stringTime = hour+minute+second
        return stringTime
    }

    fun tableAndItem(): ArrayList<ListViewItem>{
        //DB접근에 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //내 핸드폰 내의 앱 리스트 받아오기(앱 아이콘,패키지명,이름)
        //출처//http://blog.naver.com/pluulove84/100153350054
        var appDataList = ArrayList<ListViewItem>()//커스텀 리스트뷰에 사용

        var packageNameList = ArrayList<String>()//time 테이블 생성에 사용
/*
        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS)
  */
        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        //모두 정돈된 데이터가 들어갈 리스트 (이 함수의 반환값)
        var sortedList = ArrayList<ListViewItem>()
        //핸드폰에 설치된 앱의 아이콘과 앱이르, 패키지명 가져오기
        for(app:ApplicationInfo in packs){
            appDataList.add(ListViewItem(app.loadIcon(pm), app.loadLabel(pm).toString(), app.packageName.toString()))
        }

        /*
        * 시간 많은 순으로 정렬하기
        * 여유가 된다면 그래프 그려넣기
        */

        //http://eteris.tistory.com/1485
        // 기타 프로세스 목록 확인
        val usage = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000*1000
        val stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime)
        //이상한점1: INTERVAL_WEEKLY나 INTERVAL_DAILY나 시작 시간과 끝시간이 같으면 동일한 결과를 뽑아와야할 것 같은데 그렇지 않음.
        //이상한점2: 내가 알기로는 beginTime을 만들때 빼주는 시간은 밀리초로 대략 16분 이상 정도인데 실제 결과값은 12시간을 상회한다.
        //https://stackoverflow.com/questions/36238481/android-usagestatsmanager-not-returning-correct-daily-results
        //이곳의 답변에 의하면 API21 버전에서는 UsageStatsManager가 정상적으로 ㅈ가동하지 않을 수 있나보다.
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
                print(usageStats.packageName+" last time used before --> " +
                        "${if(usageStats.lastTimeUsed == 0L) usageStats.lastTimeUsed else (endTime-usageStats.lastTimeUsed)/1000}")
                println("///  used time --> ${usageStats.totalTimeInForeground/1000L}")
                //사용 시간 0인 것들 골라내기

                if(usageStats.totalTimeInForeground >= 1000L){
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

        return sortedList
    }

    fun tableAndItem2(): ArrayList<ListViewItem>{
        //DB접근에 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //내 핸드폰 내의 앱 리스트 받아오기(앱 아이콘,패키지명,이름)
        //출처//http://blog.naver.com/pluulove84/100153350054
        var appDataList = ArrayList<ListViewItem>()//커스텀 리스트뷰에 사용
        var packageNameList = ArrayList<String>()//time 테이블 생성에 사용
        val pm: PackageManager = getPackageManager()
        val packs = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        //모두 정돈된 데이터가 들어갈 리스트 (이 함수의 반환값)
        var sortedList = ArrayList<ListViewItem>()
        //핸드폰에 설치된 앱의 아이콘과 앱이름, 패키지명 가져오기
        for(app:ApplicationInfo in packs){
            appDataList.add(ListViewItem(app.loadIcon(pm), app.loadLabel(pm).toString(), app.packageName.toString()))
        }

        //time table이 없으면 만들고 초기화
        if(dbHelper.getTimeElementCount() == 0){
            for(app:ApplicationInfo in packs){
                packageNameList.add(app.packageName.toString())
            }
            dbHelper.initializeTime(packageNameList)
        }

        //누적시간 초기화
        spentTime = 0L
        //일단은 acTime에 package이름을 넣어 정렬
        appDataList.sortBy { it.accumulatedTime }
        for(app in appDataList){
            val acTime = dbHelper.getTime(app.accumulatedTime!!)
            if(acTime != 0){//저장된 시간이 0이 아닐 때
                app.accumulatedTime = makeStringTime(acTime)
                spentTime += acTime.toLong()
                sortedList.add(app)
            }
        }

        return sortedList
    }

    fun isServiceRunningCheck(): Boolean{
        //작동중인 서비스 확인
        //출처: http://darkcher.tistory.com/184
        val manager: ActivityManager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if("kr.ac.kau.sw.a2016125063.preventsnsaddiction.TimeMeasureService".equals(service.service.className.toString())){
                println("serviceName == "+service.service.className.toString())
                return true
            }
        }
        return false
    }

    //출처:
    fun usedTime(minusTime: Long): Long{
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - minusTime
        val usageStatsManager: UsageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usagesList = usageStatsManager.queryAndAggregateUsageStats(beginTime, endTime)
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, beginTime+60*60*1000L)
        var timeSum = 0L
        for (usage in usagesList.values) {
            if(usage.totalTimeInForeground >= 1000L) {
                print("packageName --> " + usage.packageName)
                println(",  usedTime -->" + usage.totalTimeInForeground/1000)
                //timeSum += usage.totalTimeInForeground
            }
        }
        for(usage in stats){
            timeSum += usage.totalTimeInForeground
        }
        println("all time --> "+timeSum/1000)
        println("endTime --> "+endTime)
        println("beginTime --> "+beginTime)
        //탑 액티비티가 바뀔때 바뀐내용이 뒤늦게 나온다.
        //만일 인터넷앱을 사용한뒤 유튜브로 바꾼다면 화면이 바뀐뒤 인터넷, 유튜브로 들어가도 유튜브가 로그에 나오지 않음
        return timeSum
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //타이틀바 없애기
        //출처: http://commin.tistory.com/63 [Commin의 일상코딩]
        setContentView(R.layout.activity_main)
//나중에 삭제해야함
        if(isServiceRunningCheck() == false) {
            val i = Intent(applicationContext, TimeMeasureService::class.java)
            //테스트 단계니깐 적용
            //작동중인 서비스를 잘 못잡는것 같음
            applicationContext.startService(i)
            Log.d("API checking", Build.VERSION.RELEASE.toString())
        }

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
            val intent = Intent(applicationContext, UsagePermissionActivity::class.java)
            this.startActivity(intent)
        }
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

        val optionButton = findViewById<ImageButton>(R.id.option_image_button)
        optionButton.setOnClickListener{
            val intent = Intent(this@MainActivity, OptionActivity::class.java)
            startActivity(intent)
        }

        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)
        //hour time table이 없으면 만들고 초기화
        if(dbHelper.getHourTimeElementCount() == 0){
            dbHelper.initializeHourTime()
        }
        val hourData = dbHelper.getAllHourTime()

        //시간마다 저장된 배열을 사용한다.
        val graph = findViewById<GraphView>(R.id.graph)
        val data = Array<DataPoint>(24, {i -> DataPoint(i.toDouble(),(usedTime((24-i)*60*60*1000L)/1000/60L).toDouble()) })
        for(i:Int in 0..23){
            data[i] = DataPoint(i.toDouble(),hourData[i].toDouble())
        }
        //val data = Array<DataPoint>(24, {i -> DataPoint(i.toDouble(),(hourData[i]).toDouble()) })
        var series: LineGraphSeries<DataPoint> = LineGraphSeries(data)
        series.isDrawDataPoints = true
        series.dataPointsRadius = 8F
        graph.addSeries(series)
//그래프 모양 수정 필요
        graph.gridLabelRenderer.labelFormatter = object: DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return super.formatLabel(value, isValueX)
            }
        }
    }

    override fun onResume(){
        Log.d("onResume","update")
        super.onResume()
        //DB접근에 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //커스텀 리스트뷰 만들기
        //출처//http://recipes4dev.tistory.com/43
        //listView 설정
        var listView: ListView = findViewById<ListView>(R.id.app_list)
        //어뎁터 생성
        //var listViewAdapter: ListViewAdapter = ListViewAdapter(appDataList)
        listViewAdapter = ListViewAdapter(this, tableAndItem2())
        //어뎁터 설정
        listView.adapter = listViewAdapter
        //아이템 추가
        //listViewAdapter!!.notifyDataSetChanged()

        println("spentTime ---> ${spentTime}")
        //시간을 가리키는 글
        val signTime = findViewById<TextView>(R.id.left_or_acculumated)
        //남은 시간 textview
        val leftTimeTextview = findViewById<TextView>(R.id.calculated_left_time)
        //option 값 1.시간제한 셋팅 2.셀카제한 3.앱제한 4.시간 5.분 6.초 7.초기화 시 8.초기화 분
        val optionValue = dbHelper.getSettings()
        if(optionValue[0] == 1){//시간 제한이 걸려 있을 때
            signTime.text = "남은 시간"
            val limitTime = optionValue[3]*3600 + optionValue[4]*60 + optionValue[5]
            val leftTime = limitTime - spentTime.toInt()
            if(leftTime > 0){//시간이 남아 있다면
                leftTimeTextview.text = makeStringTime(leftTime)
            }else{//시간을 다 썼다면
                leftTimeTextview.text = "00:00:00"
            }
        }else{//시간제한이 풀려 있을 때 누적시간을 보여줌
            signTime.text = "총 사용 시간"
            leftTimeTextview.text = makeStringTime(spentTime.toInt())
        }

        if(isServiceRunningCheck()){
            val intent = Intent(applicationContext, TimeMeasureService::class.java)
            //applicationContext.stopService(intent)
            Log.d("onResume"," stopService")
        }
    }

    override fun onPause() {
        super.onPause()
        //서비스가 작동중이면 서비스 추가x
        Log.d("onPause()","!!!!")
        if(isServiceRunningCheck() == false) {
            val i = Intent(applicationContext, TimeMeasureService::class.java)
            //테스트 단계니깐 적용
            //작동중인 서비스를 잘 못잡는것 같음
            //applicationContext.startService(i)
            Log.d("API checking", Build.VERSION.RELEASE.toString())
        }
    }
}