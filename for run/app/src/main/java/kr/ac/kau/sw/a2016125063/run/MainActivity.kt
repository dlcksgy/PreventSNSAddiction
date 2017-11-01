package kr.ac.kau.sw.a2016125063.preventsnsaddiction

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import java.lang.reflect.Array

class MainActivity : AppCompatActivity() {

    var listViewAdapter: ListViewAdapter = ListViewAdapter(ArrayList<ListViewItem>())

    fun tableAndItem(): ArrayList<ListViewItem>{
        //MainActiivty의 그래프를 그리기 위해 필요한 db 관리자
        val dbHelper: DBHelper = DBHelper(applicationContext, "Settings.db", null, 1)

        //내 핸드폰 내의 앱 리스트 받아오기(앱 아이콘,패키지명,이름)
        //출처//http://blog.naver.com/pluulove84/100153350054
        var appDataList = ArrayList<ListViewItem>()//커스텀 리스트뷰에 사용
        var packageNameList = ArrayList<String>()//time 테이블 생성에 사용
        var pm: PackageManager = getPackageManager()
        var packs = pm.getInstalledApplications(PackageManager.GET_DISABLED_COMPONENTS)

        //time table이 없으면 만들고 초기화
        if(dbHelper.getTimeElementCount() == 0){
            for(app:ApplicationInfo in packs){
                packageNameList.add(app.packageName.toString())
            }
            dbHelper.initializeTime(packageNameList)
        }
        for(app:ApplicationInfo in packs){
            val acTime = dbHelper.getTime(app.packageName.toString())
            val hour = if(acTime/3600 != 0) (acTime/3600).toString() else "00"
            val minute = if((acTime/60)%60 != 0) ((acTime/60)%60) else "00"
            val second = if(acTime%60 != 0) (acTime%60).toString() else "00"
            val time = hour+":"+minute+":"+second
            appDataList.add(ListViewItem(app.loadIcon(pm), app.loadLabel(pm).toString(), time))
        }
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

        val optionButton = findViewById<Button>(R.id.option_button)
        optionButton.setOnClickListener{
            val intent = Intent(this@MainActivity, OptionActivity::class.java)
            startActivity(intent)
        }

        if(isServiceRunningCheck() == false) {
            val i = Intent(applicationContext, TimeMeasureService::class.java)
            applicationContext.startService(i)
            Log.d("check", Build.VERSION.RELEASE.toString())
        }

        //val i = Intent("flag")
        //i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        //sendBroadcast(i)
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