package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.*
import android.os.*
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by 이은솔 on 2017-09-28.
 * 출처//http://bitsoul.tistory.com/147
 * https://stackoverflow.com/questions/44789492/convert-indefinitely-running-runnable-from-java-to-kotlin
 */
class TimeMeasureService: Service() {//서비스가 죽지 않게 만들기
    var dbHelper: DBHelper? = null
    var pReceiver: BroadcastReceiver? = null

    var timeOver = false
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if(msg.arg1 == 0){
                Toast.makeText(applicationContext, "지나친 휴대폰사용은\n" +
                        " 정신건강에 해롭습니다.", Toast.LENGTH_LONG).show()
            }
            if (msg.arg1 == 1) {
                Toast.makeText(applicationContext, "지나친 휴대폰사용은\n 정신건강에 해롭습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    val timer = Timer()
    var task: TimerTask? = null

    companion object {
        var isServiceRunnning: Boolean = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        dbHelper = DBHelper(this.baseContext, "Settings.db", null, 1)//DB다루기 위한 관리자
        Log.d("TimeMeasureService","onCreate")

        //앱 설치, 삭제, 업데이트시 서비스 실행하기 - 동적 인텐트필터 등록
        //출처: http://ccdev.tistory.com/29?category=554483 [초보코딩왕의 Power Dev.]

        //pReceiver = broadcastReceiver()
        pReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, i: Intent?) {
                val action = i?.action
                if(action.equals(Intent.ACTION_TIME_TICK)){
                    //각 시의 정각에 지금까지 사옹한 시간이 저장 된다.
                    val time = hourMinute()
                    if(time.first == 0){//시각이 정각일 때
                        //val dbHelper = DBHelper(context!!, "Settings.db", null, 1)
                        //dbHelper.updateHourTime(Pair(time.second, dbHelper.getTimeSum()))
                        //println("updatedTime == "+dbHelper.getTimeSum())
                        val hourTimeIntent = Intent(context, HourTimeService::class.java)
                        hourTimeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        applicationContext.startService(hourTimeIntent)
                        applicationContext.stopService(hourTimeIntent)
                    }
                    //내가 정한 시간에 시간이 초기화 된다.
                    if(time.second == OptionActivity.Hour){//초기화 시가 같을 때
                        if(time.first == OptionActivity.Minute){//초기화 분이 같을 때
                            timeOver = false
                            val initailzeAcTimeIntent = Intent(context, initializeService::class.java)
                            initailzeAcTimeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            applicationContext.startService(initailzeAcTimeIntent)
                            applicationContext.stopService(initailzeAcTimeIntent)
                            Log.d("BroadcastReceiver","it's time to initialize")
                        }
                    }
                    println("hour = ${time.second}, minute = ${time.first}, OptionHour = ${OptionActivity.Hour}, OptionMinute = ${OptionActivity.Minute}")
                    Log.d("BroadcastReceiver","ACTION_TIME_TICK")
                }
            }
        }
        var pFilter = IntentFilter()
        //pFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        //pFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        //pFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        //action time tick은 동적으로 해줘야 하는 듯
        //출처: http://la-stranger.blogspot.kr/2013/09/blog-post_26.html
        pFilter.addAction(Intent.ACTION_TIME_TICK)
        //pFilter.addDataScheme("package")

        applicationContext.registerReceiver(pReceiver, pFilter);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service : ","onStartCommand")
        var time: Int = 0//어플시작 시간 측정
        var app: String = ""//어플 관리
        val manager: ActivityManager = this.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager//서비스 모니터용
        var sum = 0
        for(i in OptionActivity.appLimitList){
            sum += dbHelper!!.getTime(i)
        }
        println("sum == ${sum}")
        OptionActivity.usedSec = sum
        //인텐드 각종 플래그 태그들
        //출처//http://theeye.pe.kr/archives/1298
        val lockIntent: Intent = Intent(applicationContext, LockActivity::class.java)
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        //휴대폰 화면의 on / off 확인
        //출처: https://stackoverflow.com/questions/19350258/how-to-check-screen-on-off-status-in-onstop
        val powerManager: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        var preScreenOn: Boolean = false
        var nowScreenOn: Boolean
        //val timer = Timer()
        task = object : TimerTask() {
            override fun run() {
                Log.d("run : ","task running")
                val top = getForegroundPackageNameClassNameByUsageStats()
                Log.d("getLauncherTopApp : ",
                        getLauncherTopApp(this@TimeMeasureService, getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager))
                Log.d("",top[0])
                for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
                    //println("serviceName == "+service.service.className.toString())
                }
                val topActivity: String = getLauncherTopApp(this@TimeMeasureService, getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager)

                //화면이 꺼졌을 때는 시간이 누적되어서는 안됨
                //화면이 켜짐->꺼짐으로 바뀔 때 시간을 저장시켜주고 꺼져있으면 현재 시간만 계속 바꿔주면 됨
                nowScreenOn = powerManager.isInteractive
                println("nowScreenOn == ${nowScreenOn}, preScreenOn == ${preScreenOn}")
                if(!nowScreenOn){
                    if(preScreenOn){//이전:켜짐 지금:꺼짐
                        val acTime = dbHelper!!.getTime(app)
                        dbHelper!!.updateTime(Pair(app, acTime + ((System.currentTimeMillis() / 1000).toInt() - time)))
                        time = (System.currentTimeMillis() / 1000).toInt()
                        println("time stored")
                    }
                    //화면이 켜져있으면 시간이 바뀔일이 없기 때문에 문제가 안됨
                    time = (System.currentTimeMillis()/1000).toInt()
                }
                //if(!isScreenOn) time = (System.currentTimeMillis()/1000).toInt()
                if(app != topActivity){
                    if(topActivity == "failed"){//앱이 그대로 사용중임
                        //스레드에서 토스트메시지를 직접 띄울 수 없기 때문에 핸들러를 이용
                        // 출처 : https://stackoverflow.com/questions/7185942/error-while-dispaying-an-toast-message-cant-create-handler-inside-thread-that
                        if(OptionActivity.appLimitList.indexOf(app) != -1) {//제한된 앱을 사용중일 때
                            //시간 제한 설정에 따라 락이 걸리고 안걸리고
                            //시간이 넘어갔을 때만 꺼지게 하기
                            val msg = handler.obtainMessage()
                            msg.arg1 = OptionActivity.timeLimitSetting
                            //handler.sendMessage(msg)     빼애애액
                            OptionActivity.usedSec += 1
                            //인텐드 각종 플래그 태그들
                            //출처//http://theeye.pe.kr/archives/1298
                            println("usedSec = ${OptionActivity.usedSec}, timeSec = ${OptionActivity.timeSec}")

                            if(OptionActivity.usedSec%5 == 0){   //5분 마다 경고메시지
                                handler.sendMessage(msg)
                            }
                            if(OptionActivity.usedSec - OptionActivity.timeSec > 0) {
                                if(OptionActivity.camera == 1 && !timeOver){//셀카 설정이 되어있을 때
                                    //timeOver = true
                                    val intent = Intent(applicationContext,CameraActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                                else if(OptionActivity.timeLimitSetting == 1) {
                                    startActivity(lockIntent)
                                    //사용 가능 시간이 지났을 때 창을 띄워야 함.
                                }else if(OptionActivity.camera == 1){

                                    startActivity(lockIntent)
                                }
                            }
                        }
                    }else {//사용중인 앱이 바뀌었을 때
                        if(app != "") {//처음 상태가 아닐때
                            val acTime = dbHelper!!.getTime(app)
                            dbHelper!!.updateTime(Pair(app, acTime + ((System.currentTimeMillis() / 1000).toInt() - time)))
                            if(OptionActivity.appLimitList.indexOf(app) != -1){//제한된 앱을 사용하다가 바뀌면
                                OptionActivity.usedSec += ((System.currentTimeMillis() / 1000).toInt() - time)
                            }
                        }
                        app = topActivity
                        time = (System.currentTimeMillis() / 1000).toInt()
                    }
                }
                preScreenOn = nowScreenOn
            }
        }
        //static 변수로 선언하여 boolean true일내는 작동 안함 false일 때는 등록을 하고 true로 바꿈
        if(isServiceRunnning == false) {
            timer.schedule(task, 0, 1000)  // delay 초 후 run을 실행하고 period/1000초마다 실행
            isServiceRunnning = true
        }

        return super.onStartCommand(intent, flags, startId)

        //서비스 강제종료시 다시 실행시키지 않게하기
        //작동이 잘 안되는 듯 하다.
        //출처: http://blog.naver.com/PostView.nhn?blogId=chazlqhemsks&logNo=10184226234&parentCategoryNo=&categoryNo=83&viewDate=&isShowPopularPosts=false&from=postView
        //return Service.START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        timer.cancel()
        timer.purge()
        task!!.cancel()
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TimeMeasureService","onDestroy")
        if(pReceiver != null){
            unregisterReceiver(pReceiver)
        }
        isServiceRunnning = false
    }

    //참고 : https://github.com/lizixian18/AppLock/blob/master/app/src/main/java/com/lzx/lock/service/LockService.java
    fun getLauncherTopApp(context: Context, activityManager: ActivityManager): String {

        //5.0以后需要用这方法  번역 -> 5.0이상부터는 이렇게 해야 함.
        Log.d("inner","getLauncherTopApp")
        val sUsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        if (!android.text.TextUtils.isEmpty(result)) {
            return result
        }

        return "failed"
    }

    // 참고 : https://stackoverflow.com/questions/38971472/using-usagestatsmanager-to-get-the-foreground-app
    fun getForegroundPackageNameClassNameByUsageStats(): Array<String> {
        var packageNameByUsageStats = "null"
        var classByUsageStats = "null"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val INTERVAL: Long = 10000
            val end = System.currentTimeMillis()
            val begin = end - INTERVAL
            val usageEvents = mUsageStatsManager.queryEvents(begin, end)
            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    packageNameByUsageStats = event.packageName
                    classByUsageStats = event.className
                    Log.d(ContentValues.TAG, "packageNameByUsageStats is$packageNameByUsageStats, classByUsageStats is $classByUsageStats")
                }
            }
        }
        return arrayOf<String>(packageNameByUsageStats, classByUsageStats)
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